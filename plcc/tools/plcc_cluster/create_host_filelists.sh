#!/bin/sh
## create_host_filelists.sh -- create the file lists for all nodes. You should have the hostnames (DNS names, e.g., "worker1.cluster.mydomain.com" or "worker1") in a file named 'settings_node_hostnames.cfg' (one name per line) before running this script.



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

if [ "$1" = "all" ]; then
    DATE_FILTER=""
    echo "$APPTAG Updating all proteins in the database, even if they did not change more recently than the last update."
else
    if [ ! -r $TIMESTAMP_FILE_LAST_UPDATE_START ]; then
        echo "$APPTAG ##### ERROR: Requested partly update but no timestamp file found at '$TIMESTAMP_FILE_LAST_UPDATE_START'. #####"
        echo "$APPTAG This most likely means that this is the 1st time this is run. Use '$0 all' to update all files."
        exit 1
    fi

    LAST_UPDATE_DATE=$(stat -c %y $TIMESTAMP_FILE_LAST_UPDATE_START)
    echo "$APPTAG Last update was started at $LAST_UPDATE_DATE."
    DATE_FILTER="-cnewer $TIMESTAMP_FILE_LAST_UPDATE_START"

fi

## Write the list of files we need to handle
echo "$APPTAG Searching for PDB files that need to be handled..."
find $LOCAL_MIRRORDIR/ $DATE_FILTER -type f -name *$REMOTE_PDB_FILE_EXTENSION > $DBINSERT_FILE_LIST

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


