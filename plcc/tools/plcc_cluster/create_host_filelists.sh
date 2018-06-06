#!/bin/sh
## create_host_filelists.sh -- create the file lists for all nodes. You should have the hostnames (DNS names, e.g., "worker1.cluster.mydomain.com" or "worker1") in a file named 'settings_node_hostnames.cfg' (one name per line) before running this script.
# written by ts
# changed by jnw 2017: include a mode without timestamp file, include use of debug mode (see cluster settings)
# 		 2018: include a mode with provided filelist, fix debug mode
#
# USAGE
# parameter:
# '' -> incremental update with timestamp file
# 'YYYY-MM-DD' -> incremental update without timestamp file. date of last update start
# 'all' -> update everything
# 'filelist' -> expects ./status/dbinsert_file_list.lst with path to pdb files each per line and creates filelists for each node (as usual)

########## Some function(s) ##########

# from: https://wiki.magenbrot.net/programmierung/bash/datum-zeit_berechnung
# calculates the difference between two dates
# -* gives difference in seconds (s), minutes (m), hours (h), days (d) [default]
# next two parameters are dates in format "YYYY-MM-DD"
# function needed in workaround for missing timestamp /jnw
dateDiff (){
    case $1 in
        -s)   sec=1;      shift;;
        -m)   sec=60;     shift;;
        -h)   sec=3600;   shift;;
        -d)   sec=86400;  shift;;
        *)    sec=86400;;
    esac
    dte1=$(date --utc --date "$1" +%s)
    dte2=$(date --utc --date "$2" +%s)
    diffSec=$((dte2-dte1))
    if ((diffSec < 0)); then abs=-1; else abs=1; fi
    echo $((diffSec/sec*abs))
}


########## Settings ##########

NODE_FILE="settings_node_hostnames.cfg"

## Number of machines that run PLCC. This defines in how many parts the input file list should be split.
#NUM_NODES=6
NUM_NODES=$(cat $NODE_FILE | wc -l)

## A temp dir, must be writable
#TMPDIR="/tmp"

## the basename of the created filelists. A numeric suffix of length 3 will be added to this string, e.g. "filelistnum" will results in files named "filelistnum000", "filelistnum001", and so on.
FILELIST_BASENAME="filelistnum"

CFG_FILE="./settings_statistics.cfg"

APPTAG="[CHFL]"

if [ ! -f "$CFG_FILE" ]; then
	echo "$APPTAG Could not read config file '$CFG_FILE'."
else
	echo "$APPTAG Config file seems fine."
fi

## get settings

source $CFG_FILE


############################################   check various stuff  ############################################
## Checking all of this definitely pays off because we can stop now instead of getting the same error message for
##  tens of thousands of PDB files in the loop below...


echo "$APPTAG ================ Creating PDB filelists for all nodes ==================="
echo "$APPTAG Using settings from file '$CFG_FILE'."



## make sure the settings have been read
if [ -z "$SETTINGSREAD" ]; then
    echo "$APPTAG ##### ERROR: Could not load settings from file '$CFG_FILE'. #####"
    exit 1
fi


## check for status dir
if [ ! -d $STATUSDIR ]; then
    echo "$APPTAG ##### ERROR: Status dir '$STATUSDIR' does not exist but is required. Create it. Exiting."
    exit 1
fi


## check for log dir
if [ ! -d $LOGDIR ]; then
    echo "$APPTAG ##### ERROR: Log dir '$LOGDIR' does not exist but is required. Create it. Exiting."
    exit 1
fi


## make sure no rsync update is currently running
if [ -f "$LOCKFILE_RSYNC" ]; then
    echo "$APPTAG ##### ERROR: Rsync update currently in progress it seems, exiting. #####"
    echo "$APPTAG Delete '$LOCKFILE_RSYNC' and try again if you are sure this is not the case."
    exit 1
fi


## make sure no database update is currently running
if [ -f "$LOCKFILE_DBINSERT" ]; then
    echo "$APPTAG ##### ERROR: Database update already in progress it seems, exiting. #####"
    echo "$APPTAG Delete '$LOCKFILE_DBINSERT' and try again if you are sure this is not the case."
    exit 1
fi


## check plcc_run dir
if [ ! -d $PLCC_RUN_DIR ]; then
    echo "$APPTAG ##### ERROR: The plcc_run directory '$PLCC_RUN_DIR' does not exist. Check settings. #####"
    exit 1
fi


## check update dir
if [ ! -d $UPDATEDIR ]; then
    echo "$APPTAG ##### ERROR: The update directory '$UPDATEDIR' does not exist. Check settings. #####"
    exit 1
fi


## make sure this was executed from the path it is in (./update_db_from_new_pdb_files.sh instead of something like '/some/path/to/update_db_from_new_pdb_files.sh')
if [ ! -f $UPDATEDIR/$CFG_FILE ]; then
    echo "$APPTAG ##### ERROR: You have to run this script from the directory it is in. #####"
    exit 1
fi


############################################   vamos  ###################################################

echo "$APPTAG Using PDB files in sub directories of '$LOCAL_MIRRORDIR'. Assuming file extension '$REMOTE_PDB_FILE_EXTENSION'."

# included workaround for missing timestamp-file /jnw
ASUME_FILELIST="false"
if [ -z "$1" ]; then # => incremental update using timestamp file
    echo "$APPTAG No given parameter: Trying incremental update using timestamp file."
    
    if [ ! -r $TIMESTAMP_FILE_LAST_UPDATE_START ]; then
        echo "$APPTAG ##### ERROR: Requested partly update but no timestamp file found at '$TIMESTAMP_FILE_LAST_UPDATE_START'. #####"
        echo "$APPTAG This most likely means that this is the 1st time this is run. Use '$0 all' to update all files."
        exit 1
    fi

    LAST_UPDATE_DATE=$(stat -c %y $TIMESTAMP_FILE_LAST_UPDATE_START)
    echo "$APPTAG Last update was started at $LAST_UPDATE_DATE."
    DATE_FILTER="-cnewer $TIMESTAMP_FILE_LAST_UPDATE_START"
else
    case "$1" in
        "all") # => updating everything
            echo "$APPTAG Given parameter 'all': Updating all proteins in the database, even if they did not change more recently than the last update."
            DATE_FILTER=""
            ;;
        "filelist") # expecting a existing list with paths
            ASUME_FILELIST="true"
            ;;
        *) # => incremental update using given date as last update date
            echo "$APPTAG Given parameter '$1': Trying incremental update using $1 as last update date."
        
            # check length of parameter
            if [ $(expr length "$1") != 10 ]; then
                echo "$APPTAG Error: It seems like you tried to use incremental update without timestamp file."
                echo "$APPTAG Error: Make sure your parameter is of format YYYY-MM-DD (length did not match) and rerun script. Exiting now."
                exit 1
            fi
            
            # TODO check for correctness of given parameter in YYYY-MM-DD, for example check wether YYYY is year of date $1, MM is month of date $1 ...
            
            echo "$APPTAG Make absolutely sure that the parameter follows the convention 'YYYY-MM-DD'! It is not checked!"
            echo "$APPTAG Otherwise the resulting filelists may be wrong. Check for plausibility! Atleast the lists should not be empty."
            
            LAST_UPDATE_DATE=$1
            
            DATE_DIFF=$(dateDiff $LAST_UPDATE_DATE $(date +%Y-%m-%d))
            DATE_FILTER="-ctime -$DATE_DIFF"
            ;;
    esac
fi

echo "$APPTAG Using Datefilter: '$DATE_FILTER'"

# end of workaround /jnw

## Write the list of files we need to handle
echo "$APPTAG Searching for PDB files that need to be handled..."

# find PDB files (check for DEBUG mode and existence of filelist)
if [ $ASUME_FILELIST = "true" ]; then
    if [ ! -r $DBINSERT_FILE_LIST ]; then
        echo "$APPTAG Requested creation of node filelists using a existing filelist at $DBINSERT_FILE_LIST but no list found."
        echo "$APPTAG Exiting now."
        exit 1  
    fi
else
    if [ "$DEBUG" = true ]; then
        if [ -z $NUM_DEBUG ]; then
            echo "$APPTAG Error: Debug mode set true but no DEBUG_Num defined"
        else
            find $LOCAL_MIRRORDIR/ $DATE_FILTER -type f -name *$REMOTE_PDB_FILE_EXTENSION | head -$NUM_DEBUG > $DBINSERT_FILE_LIST
        fi
    else # act normal
        find $LOCAL_MIRRORDIR/ $DATE_FILTER -type f -name *$REMOTE_PDB_FILE_EXTENSION > $DBINSERT_FILE_LIST
    fi
fi

NUM_FILES=$(cat $DBINSERT_FILE_LIST | wc -w)

echo "$APPTAG Found $NUM_FILES PDB files that need to be handled. Wrote unprocessed list to '$DBINSERT_FILE_LIST'."

echo "$APPTAG Total number of nodes (and thus bins) is ${NUM_NODES}."


NUM_FILES_PER_BIN=$(expr $NUM_FILES / $NUM_NODES)
REST=$(($NUM_FILES % $NUM_NODES))

if [ $REST -ne 0 ]; then
	let NUM_FILES_PER_BIN+=1
fi

echo "$APPTAG Found $NUM_FILES input files, assigning $NUM_FILES_PER_BIN files to each of the $NUM_NODES machines."

#for BIN_NUM in {1..$NUM_NODES}
#do
#   HOSTNAME=$(cat $NODE_FILE | awk "NR==$BIN_NUM {print}")
#
#    echo "$APPTAG Preparing file list for node # ${BIN_NUM} with hostname '${HOSTNAME}'..."
#
#    ## determine borders
#    LOWER_BORDER=$(echo "(($BIN_NUM-1) * $NUM_FILES_PER_BIN)+1" | bc)
#    UPPER_BORDER=$(echo "$BIN_NUM * $NUM_FILES_PER_BIN" | bc)    
#
#    if [ $UPPER_BORDER -gt $NUM_FILES ]; then
#	    UPPER_BORDER=$NUM_FILES
#    fi
#
#    echo "$APPTAG  [${BIN_NUM}] Set lower border to ${LOWER_BORDER} and upper border to ${UPPER_BORDER}."
#
#
#    ## split list        
#done




#cd status/ && split -d -a 3 -l $NUM_FILES_PER_BIN $DBINSERT_FILE_LIST filelistnum
cd status/ && split --numeric-suffixes --suffix-length=3 --lines=$NUM_FILES_PER_BIN $DBINSERT_FILE_LIST_FNAME $FILELIST_BASENAME

echo "$APPTAG Wrote the filelists to status/ directory with basename '$FILELIST_BASENAME'."

echo "$APPTAG Preparing to write files which assign the lists to the hosts..."

## Now write the files which assign the lists to the hosts
#for BIN_NUM in {1..$NUM_NODES}
for BIN_NUM in $(seq $NUM_NODES)
do
   HOSTNAME=$(cat ../$NODE_FILE | awk "NR==$BIN_NUM {print}")
   echo "$APPTAG Preparing file list for node # ${BIN_NUM} with hostname '${HOSTNAME}'..."

   ## file lists start with suffix '000' (not '001'), so decrement
   LISTNUMBER_UNFORMATTED=$BIN_NUM
   (( LISTNUMBER_UNFORMATTED-- ))
   LISTNUMBER=$(printf "%.*d" 3 "$LISTNUMBER_UNFORMATTED")
   LINKFILE="filelist.${HOSTNAME}"
   echo "$APPTAG List number for host '$HOSTNAME' is '$LISTNUMBER', writing link file '$LINKFILE'."
   
   ## write the file
   echo "./status/filelistnum${LISTNUMBER}" > $LINKFILE


   #echo "$APPTAG Preparing file list for node # ${BIN_NUM} with hostname '${HOSTNAME}'..."
done



echo "$APPTAG Done, exiting."


