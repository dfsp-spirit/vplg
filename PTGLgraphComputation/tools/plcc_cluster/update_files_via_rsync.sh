#!/bin/sh
## update_pdb_files_via_rsync.sh -- downloads all PDB files from the RCSB server via rsync
## written by ts_2011


APPTAG="[UPD_PDB_FILES]"
CFG_FILE="./settings_statistics.cfg"




echo "$APPTAG ================ Updating PDB files from RCSB server ==================="
echo "$APPTAG Using settings from file '$CFG_FILE'."

## get settings
source $CFG_FILE

## make sure the settings have been read
if [ -z "$SETTINGSREAD" ]; then
    echo "$APPTAG ##### ERROR: Could not load settings from file '$CFG_FILE'. #####"
    exit 1
fi

if [ "$DEBUG" = "true" ]; then
    echo "$APPTAG WARNING: Debug mode active. Only handling the first few PDB files."
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
    echo "$APPTAG ##### ERROR: Rsync update already in progress it seems, exiting. #####"
    echo "$APPTAG Delete '$LOCKFILE_RSYNC' and try again if you are sure this is not the case."
    exit 1
fi


## make sure no database update is currently running
if [ -f "$LOCKFILE_DBINSERT" ]; then
    echo "$APPTAG ##### ERROR: Database update currently in progress it seems, exiting. #####"
    echo "$APPTAG Delete '$LOCKFILE_DBINSERT' and try again if you are sure this is not the case."
    exit 1
fi


## ccheck rsync binary
if [ ! -x $RSYNC ]; then
    echo "$APPTAG ##### ERROR: Could not execute rsync binary at '$RSYNC'. #####"
    exit 1
fi


###################################### let's go ######################################


LOCAL_DATA_SIZE_BEFORE=$(du -shD $LOCAL_MIRRORDIR | awk '{print $1}')

echo "$APPTAG Using server '$SERVER' on port '$PORT', remote dir set to '$REMOTEDIR'."
echo "$APPTAG Writing data to local dir '$LOCAL_MIRRORDIR' and logs to '$LOGDIR'."
echo "$APPTAG The local mirror dir currently contains $LOCAL_DATA_SIZE_BEFORE of data."
echo "$APPTAG Updating timestamp file '$TIMESTAMP_FILE_LAST_UPDATE_START'."

if [ -f $TIMESTAMP_FILE_LAST_UPDATE_START ]; then    
    if rm $TIMESTAMP_FILE_LAST_UPDATE_START ; then
        echo "$APPTAG Removed old timestamp file at '$TIMESTAMP_FILE_LAST_UPDATE_START'."
    else
	echo "$APPTAG ##### ERROR: Could not remove old timestamp file '$TIMESTAMP_FILE_LAST_UPDATE_START'. #####"
	exit 1
    fi
else
    echo "$APPTAG No timestamp file found at '$TIMESTAMP_FILE_LAST_UPDATE_START', this seems to be the first update."
fi

touch $TIMESTAMP_FILE_LAST_UPDATE_START #&& echo "$APPTAG Updated timestamp file." || { echo "$APPTAG ##### ERROR: Could not update timestamp file. #####" && exit 1 }

if touch $LOCKFILE_RSYNC ; then
    echo "$APPTAG Lockfile created at '$LOCKFILE_RSYNC'."
else
    echo "$APPTAG ##### ERROR: Could not create lockfile at '$LOCKFILE_RSYNC'. Check permissions. #####"
    exit 1
fi

echo "$APPTAG Running rsync. This may take a while..."

if $RSYNC -rlpt -v -z --delete --port=$PORT $SERVER::$REMOTEDIR $LOCAL_MIRRORDIR ; then
    echo "$APPTAG Rsync run finished, data should be in '$LOCAL_MIRRORDIR'"
else
    echo "$APPTAG ##### ERROR: Rsync run failed. #####"
fi

LOCAL_DATA_SIZE_AFTER=$(du -shD $LOCAL_MIRRORDIR | awk '{print $1}')

echo "$APPTAG Removing lock file '$LOCKFILE_RSYNC'."
rm $LOCKFILE_RSYNC || echo "$APPTAG ##### WARNING: Could not remove lock file '$LOCKFILE_RSYNC'. Delete it manually or next update will fail. #####"
echo "$APPTAG Local data dir size was $LOCAL_DATA_SIZE_BEFORE before rsync run, now at $LOCAL_DATA_SIZE_AFTER."
echo "$APPTAG Check the log files in '$LOGDIR' for errors."
echo "$APPTAG You can now run the script to process the new data and insert it into the PTGL database."
echo "$APPTAG All done, exiting."

exit 0


