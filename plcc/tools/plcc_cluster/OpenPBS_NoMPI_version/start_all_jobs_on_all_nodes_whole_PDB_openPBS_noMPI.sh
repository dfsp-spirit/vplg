#!/bin/bash
# start_all_jobs_on_all_nodes_for_whole_PDB_openPBS_noMPI.sh -- submit the whole PDB to the pbs queue
# 
# Run this script from the shell after preparing the non-MPI-version of the PDB file lists.
#
# This script does not require any parameters, so just do: './pbs_start_all_jobs_on_all_nodes_whole_PDB_openPBS_noMPI.sh'
#
# This version does use openPBS but not openMPI. It submits each PDB file as a single job.
#
# written by ts, 2014
#

APPTAG="[START_ALL_NOMPI]"
MYHOME="/home/ts"
PLCC_CLUSTER_DIR="$MYHOME/software/plcc_cluster"

DEBUG_START_ALL_MPI="false"

## settings
SINGLE_JOB_SCRIPT="pbs_start_vplg_job_for_one_PDB_file_and_core.sh"

echo "$APPTAG ----- Starting all jobs via openpbs (no MPI, 1 core and 1 PDB file per job) -----"
echo "$APPTAG Assuming plcc_cluster is installed at '$PLCC_CLUSTER_DIR'."
echo "$APPTAG Preparing to submit all jobs to the openpbs queue..."

FILE_LIST="$PLCC_CLUSTER_DIR/status/dbinsert_file_list.lst"

if [ ! -f "$FILE_LIST" ]; then
    echo "$APPTAG ERROR: Could not find the PDB file list at '$FILE_LIST'. Did you run the script to generate the list?"
    exit 1
fi


	## submit a job for each file in the list
	for PDBFILE in $(cat $FILE_LIST);
	do
	  ENV_VARIABLES="PDBFILE=$PDBFILE"
	  CMD="qsub $SINGLE_JOB_SCRIPT -v $ENV_VARIABLES"
	  echo "$APPTAG Submitting job: '$CMD'"
	  $CMD
	done

	echo "$APPTAG Submitted all protein graph computation jobs."
#fi


echo "$APPTAG Submitted all jobs. Exiting."

exit
