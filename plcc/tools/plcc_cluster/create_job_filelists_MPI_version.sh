#!/bin/sh
## create_job_filelists_MPI_version.sh -- create the file lists for all MPI jobs. In the MPI version, the job lists are NOT assigned to a certain host. You should have at least as many jobs as you have cluster nodes. Having more jobs wont hurt (but having n+1 jobs with n nodes may be pretty stupid, rather choose 2n, 3n or whatever).



########## Settings ##########

NODE_FILE="settings_node_hostnames.cfg"

## Number of machines that run PLCC. This defines in how many parts the input file list should be split.
#NUM_NODES=6
NUM_NODES=$(cat $NODE_FILE | wc -l)

## The number of openpbs jobs you want, i.e., in how many parts the list of PDB files should be split. You should have at least as many jobs as you have cluster nodes. Having more jobs wont hurt (but having n+1 jobs with n nodes may be pretty stupid, rather choose 2n, 3n or whatever).
NUM_JOBS=$NUM_NODES

## A temp dir, must be writable
#TMPDIR="/tmp"

## the basename of the created filelists. A numeric suffix of length 3 will be added to this string, e.g. "filelistnum" will results in files named "filelistnum000", "filelistnum001", and so on.
FILELIST_BASENAME_MPI="MPIfilelistnum"

CFG_FILE="./settings_statistics.cfg"

APPTAG="[CHFL_MPI]"

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


echo "$APPTAG ================ Creating PDB filelists for all MPI jobs ==================="
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

echo "$APPTAG Total number of cluster nodes is ${NUM_NODES}."
echo "$APPTAG Total number of requested jobs is ${NUM_JOBS}."


NUM_FILES_PER_BIN=$(expr $NUM_FILES / $NUM_JOBS)
REST=$(($NUM_FILES % $NUM_JOBS))

if [ $REST -ne 0 ]; then
	let NUM_FILES_PER_BIN+=1
fi

echo "$APPTAG Found $NUM_FILES input files, assigning $NUM_FILES_PER_BIN files to each of the $NUM_JOBS jobs."



#cd status/ && split -d -a 3 -l $NUM_FILES_PER_BIN $DBINSERT_FILE_LIST filelistnum
cd status/ && split --numeric-suffixes --suffix-length=3 --lines=$NUM_FILES_PER_BIN $DBINSERT_FILE_LIST_FNAME $FILELIST_BASENAME_MPI

echo "$APPTAG Wrote the MPI job file lists to status/ directory with basename '$FILELIST_BASENAME_MPI'."

echo "$APPTAG Done, exiting."


