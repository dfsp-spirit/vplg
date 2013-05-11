#!/bin/bash
# start_all_jobs_on_all_nodes_for_whole_PDB.sh -- submit the whole PDB to the pbs queue
# 
# Run this script from the shell after preparing the MPI-version of the PDB file lists.
#
# This script does not require any parameters, so just do: './pbs_start_all_jobs_on_all_nodes_whole_PDB.sh'
#
# written by ts, 2013
#

APPTAG="[START_ALL_MPI]"

## settings
SINGLE_JOB_SCRIPT="pbs_start_vplg_via_mpi4py_jpbs_for_one_node.sh"

echo "$APPTAG Preparing to submit all jobs to the openbpbs queue..."

## submit a job for each file list
for FLIST in MPIfilelistnum*;
do
  ENV_VARIABLES="PDBFILELIST=$FLIST"
  CMD="qsub $SINGLE_JOB_SCRIPT -v $ENV_VARIABLES"
  echo "$APPTAG Submitting job: '$CMD'"
  $CMD
done

echo "$APPTAG Submitted all jobs. Exiting."

exit
