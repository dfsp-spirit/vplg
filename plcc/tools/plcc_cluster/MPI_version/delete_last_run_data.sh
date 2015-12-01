#!/bin/sh
## delete_last_run_data.sh -- restart all jobs using the currently configured settings and job/host file listsi

## internal stuff, dont mess with this!
APPTAG="[DELETE_LAST_RUN_DATA] "
CONFIRMED="NO"

## settings
EMPTY_THE_DATABASE="YES"
STOP_OPENPBS_JOBS="YES"
DELETE_DATA_ON_DISK="YES"

## end of settings, dont mess with stuff below

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
	cd ../plcc_run/ && java -jar plcc.jar NONE -r
else
	echo "$APPTAG NOT emptying the database (as configured in script settings)."
fi


if [ "$DELETE_DATA_ON_DISK" = "YES" ]; then
	echo "$APPTAG Deleting updata data (graph file etc) on disk..."
	cd ../plcc_run/ && java -jar plcc.jar NONE -r
else
	echo "$APPTAG NOT deleting data on disk (as configured in script settings)."
fi

echo "$APPTAG All done, exiting."
exit 0

## 
