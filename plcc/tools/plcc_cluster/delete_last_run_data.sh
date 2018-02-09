#!/bin/sh
## delete_last_run_data.sh -- restart all jobs using the currently configured settings and job/host file listsi
#
# written by ts
# changed by jnw 2017: add ability to delete logs, status and OPBS jobs
# TODO use of setting file

## internal stuff, dont mess with this!
APPTAG="[DELETE_LAST_RUN_DATA] "
CONFIRMED="NO"

## settings
EMPTY_THE_DATABASE="YES"
STOP_OPENPBS_JOBS="YES"
DELETE_DATA_ON_DISK="YES"
PLCC_RUN_DIR="./plcc_run"
DATA_DIR_TO_DELETE="/shares/modshare/vplg_all_nodes_output_jnw_2017"
EMPTY_LOG_DIR="YES"
EMPTY_STATUS_DIR="NO"
DELETE_PBS_JOBFILES="YES"

## end of settings, dont mess with stuff below

CWD=`pwd`

if [ $1 = "confirm" ]; then
	CONFIRMED="YES"
else
	echo "$APPTAG -- remove all data from the last PTGL update run"
	echo "$APPTAG WARNING!!! This script will drop the database and delete result files from hard disk!"
	echo "$APPTAG WARNING!!! Therefore, you need to confirm that you want to do this. See usage below."
	echo "$APPTAG Usage: ./delete_last_run_data confirm"
	exit 1
fi

if [ $2 = "empty_db" ]; then
	EMPTY_THE_DATABASE="YES"
fi

## stop all currently running OpenPBS jobs for my user
if [ "$STOP_OPENPBS_JOBS" = "YES" ]; then
	echo "$APPTAG Stopping all your running OpenPBS jobs..."
	for JOBID in $(qstat | grep '^[0-9]' | awk -F . '{print $1}' | tr "\n" " "); do qdel $JOBID; done
else
	echo "$APPTAG NOT stopping OpenPBS jobs of user (as configured in script settings)."
fi


if [ "$EMPTY_THE_DATABASE" = "YES" ]; then
	echo "$APPTAG Emptying the database..."
	cd $PLCC_RUN_DIR && java -jar plcc.jar NONE -r
	cd $CWD
else
	echo "$APPTAG NOT emptying the database (as configured in script settings)."
fi


if [ "$DELETE_DATA_ON_DISK" = "YES" ]; then
	echo "$APPTAG Deleting updata data (graph file etc) on disk..."
	rm -rf $DATA_DIR_TO_DELETE && mkdir -p $DATA_DIR_TO_DELETE && chmod ugo+rwx $DATA_DIR_TO_DELETE
else
	echo "$APPTAG NOT deleting data on disk (as configured in script settings)."
fi

# /jnw ->
if [ "$EMPTY_LOG_DIR" = "YES" ]; then
	echo "$APPTAG Deleting all files in log directory."
	rm -f ./logs/*
else
	echo "$APPTAG NOT deleting files in logs directory (as configured in script settings)."
fi

if [ "EMPTY_STATUS_DIR" = "YES" ]; then
	echo "$APPTAG Deleting all files in status directory."
	rm -f ./status/*
else
	echo "$APPTAG NOT deleting files in status directory (as configured in script settings)."
fi

if [ "DELETE_PBS_JOBFILES" = "YES" ]; then
	echo "$APPTAG Deleting all OpenPBS files."
	rm -f ./OpenPBS_NoMPI_version/vplgsinglejob*
else
	echo "$APPTAG NOT deleting OpenPBS job files (as configured in script settings)."
fi
# <- /jnw

echo "$APPTAG All done, exiting."
exit 0

## 
