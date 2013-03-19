#!/bin/bash
## update_db_from_filelist.sh -- uses a list of PDB files (a text file with 1 PDB file per line, created by running update_files_via_rsync) to fill the DB.
##
## Written by ts_2011

APPTAG="[UPD_DB_FL]"
CFG_FILE="settings_statistics.cfg"



echo "$APPTAG ================ Updating database from PDB file list ==================="
echo "$APPTAG Using settings from file '$CFG_FILE'."



## get settings
source $CFG_FILE

################################################## functions ##################################################

function del_output()
{
    ## remove various output files
    L_PDBID="$1"
    list_size=0
    num_del=0
    for f in $L_PDBID.pdb $L_PDBID.dssp $L_PDBID.geolig $L_PDBID.ligands $L_PDBID.contactstats $L_PDBID.models $L_PDBID.chains $L_PDBID.dssplig $L_PDBID.geolig $L_PDBID.pymol $L_PDBID.geo
    do
    	let list_size++
    	if [ -w $f ]; then
    		rm $f
    		#echo "$APPTAG   Deleted file $f."
    		let num_del++
    	fi
    done
    #echo "$APPTAG Checked for $list_size files, deleted $num_del."
}

################################################## end of functions ##################################################



############################################   check various stuff  ############################################
## Checking all of this definitely pays off because we can stop now instead of getting the same error message for
##  tens of thousands of PDB files in the loop below...

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
    echo "$APPTAG ##### ERROR: Rsync update currently in progress it seems, exiting. #####"
    echo "$APPTAG Delete '$LOCKFILE_RSYNC' and try again if you are sure this is not the case."
    exit 1
fi


## make sure no database update is currently running
#if [ -f "$LOCKFILE_DBINSERT" ]; then
#    echo "$APPTAG ##### ERROR: Database update already in progress it seems, exiting. #####"
#    echo "$APPTAG Delete '$LOCKFILE_DBINSERT' and try again if you are sure this is not the case."
#    exit 1
#fi


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

## remove old protocol files. this is required because we only append to them later.
if [ -f $DBINSERT_LOG_SUCCESS ]; then
    if rm $DBINSERT_LOG_SUCCESS ; then
        echo "$APPTAG Deleted old db insert log '$DBINSERT_LOG_SUCCESS'."
    else
        echo "$APPTAG ERROR: Could not delete old db insert log '$DBINSERT_LOG_SUCCESS'. Check permissions."
        exit 1
    fi
fi

touch $DBINSERT_LOG_SUCCESS || exit 1

if [ -f $DBINSERT_LOG_FAIL ]; then
    if rm $DBINSERT_LOG_FAIL ; then
        echo "$APPTAG Deleted old db insert fail log '$DBINSERT_LOG_FAIL'."
    else
        echo "$APPTAG ERROR: Could not delete old db insert fail log '$DBINSERT_LOG_FAIL'. Check permissions."
        exit 1
    fi
fi

touch $DBINSERT_LOG_FAIL || exit 1

if [ -f $DBINSERT_LOG_DSSP_FAIL ]; then
    if rm $DBINSERT_LOG_DSSP_FAIL ; then
        echo "$APPTAG Deleted old db insert dssp fail log '$DBINSERT_LOG_DSSP_FAIL'."
    else
        echo "$APPTAG ERROR: Could not delete old db insert dssp fail log '$DBINSERT_LOG_DSSP_FAIL'. Check permissions."
        exit 1
    fi
fi

touch $DBINSERT_LOG_DSSP_FAIL || exit 1



TIME_START=$(date)

############################################   vamos  ###################################################

if [ -z "$1" ]; then
    echo "$APPTAG ERROR: Usage: $0 <PDB_FILE_LIST> [all]"
    echo "$APPTAG If 'all' is omitted, only files that changed since last update are used."
    exit 1
fi

DBINSERT_FILE_LIST="$1"

if [ ! -r "$DBINSERT_FILE_LIST" ]; then
    echo "$APPTAG ERROR: Could not open file list at '$DBINSERT_FILE_LIST'."
    exit 1
fi

## The files are split by newlines (\n) but we want them to be split by spaces (' '). Fix this.
tr '\n' ' ' < $DBINSERT_FILE_LIST > $DBINSERT_FILE_LIST_PROC


echo "$APPTAG Using PDB files in list '$DBINSERT_FILE_LIST_PROC'. Assuming file extension '$REMOTE_PDB_FILE_EXTENSION'."

if [ "$2" = "all" ]; then
    DATE_FILTER=""
    echo "$APPTAG Updating all proteins in list, even if they did not change more recently than the last update."
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

echo "$APPTAG Logging successfully inserted proteins to '$DBINSERT_LOG_SUCCESS'."
echo "$APPTAG Logging failed inserts to '$DBINSERT_LOG_FAIL'."

NUM_FILES=$(cat $DBINSERT_FILE_LIST_PROC | wc -w)

echo "$APPTAG Found $NUM_FILES PDB files that need to be handled. Starting update..."
#touch $LOCKFILE_DBINSERT && echo "$APPTAG Created lock file '$LOCKFILE_DBINSERT'."

## the actual loop that handles all the files
echo "$APPTAG -------------------------------------------------------------------"

NUM_HANDLED=0
NUM_SUCCESS=0
NUM_TOTAL_FAIL=0
NUM_PDB_FAIL=0
NUM_DSSP_FAIL=0
NUM_PLCC_FAIL=0

## Remember that this is a list of file names like this: '/srv/www/PDB/data/structures/divided/pdb/ic/pdb8icd.ent.gz'. So we need to extract the
##  PDB ID from that name, then run the insert scripts for that PDB ID.

for FLN in $(cat $DBINSERT_FILE_LIST_PROC)
do
	PERC=0
	if [ $NUM_HANDLED -gt 0 ]; then
	    PERC=$(echo "scale=2; $NUM_SUCCESS*100/$NUM_HANDLED" | bc)
	    echo "$APPTAG At file '$FILE'. (Handled $NUM_HANDLED of $NUM_FILES files: $NUM_SUCCESS ok, $NUM_TOTAL_FAIL failed [Reason: $NUM_PDB_FAIL PDB, $NUM_DSSP_FAIL DSSP, $NUM_PLCC_FAIL PLCC], $PERC% ok)."
        fi

        let NUM_HANDLED++

	if [ -r $FLN ]; then

                ## extract only the file name from the 'path + filename' string (result looks like this: 'pdb8icd.ent.gz')
                FILE=$(basename $FLN)

                ## now get the PDB ID from the file name (it starts at index 3 and is 4 characters long)
	        PDBID=${FILE:3:4}


	        cd $PLCC_RUN_DIR
                if [ $? -ne 0 ]; then
                    echo "$APPTAG FATAL ERROR: Cannot cd to plcc_run directory '$PLCC_RUN_DIR'."
                    exit 1
                fi
	        
	        ## Get the PDB file
	        ./get_pdb_file.sh $PDBID >/dev/null

	        if [ $? -ne 0 ]; then
	            echo "$APPTAG   Could not get PDB file for protein '$PDBID', skipping protein '$PDBID'."
	            let NUM_TOTAL_FAIL++
	            let NUM_PDB_FAIL++
	            del_output $PDBID
	            continue
                fi


	        ## Now create the DSSP file
	        ./create_dssp_file.sh $PDBID.pdb >/dev/null

	        if [ $? -ne 0 ]; then
	            echo "$APPTAG   Could not create DSSP file from PDB file '$PDBID.pdb', skipping protein '$PDBID'."
	            let NUM_TOTAL_FAIL++
	            let NUM_DSSP_FAIL++
	            del_output $PDBID
	            continue
                fi


	        ## Ok, now call plcc to do the real work.
                ./plcc $PDBID $PLCC_OPTIONS >/dev/null 2>&1

	        if [ $? -ne 0 ]; then
	            echo "$APPTAG   Running plcc failed for PDB ID '$PDBID', skipping protein '$PDBID'."
	            let NUM_TOTAL_FAIL++
	            let NUM_PLCC_FAIL++
	            del_output $PDBID
	            continue
                fi
                
                ## everything worked it seems
                let NUM_SUCCESS++

                ## delete output via bash function
                del_output $PDBID

	        cd $UPDATEDIR
                if [ $? -ne 0 ]; then
                    echo "$APPTAG FATAL ERROR: Cannot cd to update directory '$UPDATEDIR'. (Did you execute this from another path?)"
                    exit 1
                fi

	else
	    echo "$APPTAG Could not read file '$FLN', skipping."
	    let NUM_TOTAL_FAIL++
	    let NUM_PDB_FAIL++
	fi
	
	## DEBUG only
	if [ "$DEBUG" = "true" ]; then
	    if [ $NUM_HANDLED -gt $NUM_DEBUG ]; then
	        echo "$APPTAG DEBUG MODE: Skipping the rest because this is just a test run."
	        break
	    fi
        fi
done


echo "$APPTAG -------------------------------------------------------------------"

TIME_END=$(date)

echo "$APPTAG Done, handled $NUM_HANDLED of the $NUM_FILES files ($NUM_SUCCESS ok, $NUM_TOTAL_FAIL failed)."
echo "$APPTAG Started at '$TIME_START', finished at '$TIME_END'."

for TF in $LOCKFILE_DBINSERT $DBINSERT_FILE_LIST $DBINSERT_FILE_LIST_PROC
do
    if [ -r $TF ]; then	
	if rm $TF ; then
	    echo "$APPTAG Deleted status file '$TF'."
	fi
    fi
done

echo "$APPTAG All done, exiting."
exit 0


