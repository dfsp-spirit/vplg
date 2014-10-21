#!/bin/sh
#
# remarks: a line beginning with # is a comment;
# a line beginning with #PBS is a pbs command;
# assume (upper/lower) case to be sensitive;
#
# use: submit job with
# qsub myjobscript
#
# job name (default is name of pbs script file)
#PBS -N graphletsimilarity
# standard queue is called batch
#PBS -q batch
#
# do not rerun this job if it fails
#PBS -r n
# resource limits: max. wall clock time during 
# which job can be running
#PBS -l walltime=48:00
#PBS -l nodes=1 
# send me mail when job (b)egins, (e)nds or (a)borts
##PBS -m abe  # not used so far
##PBS -M ts@odysseus.modlab  # not used so far
#
## END of PBS commands
## INFO BLOCK - INFO BLOCK - INFO BLOCK
#
# Using PBS - Environment Variables
#
# When a batch job starts execution, a number of
# environment variables are predefined, which include:
#
#      Variables defined on the execution host.
#      Variables exported from the submission host with
#                -v (selected variables) and -V (all variables).
#      Variables defined by PBS.
#
# The following reflect the environment where the user ran qsub:
# PBS_O_HOST    The host where you ran the qsub command.
# PBS_O_LOGNAME Your user ID where you ran qsub.
# PBS_O_HOME    Your home directory where you ran qsub.
# PBS_O_WORKDIR The working directory where you ran qsub.
#
# These reflect the environment where the job is executing:
# PBS_ENVIRONMENT      
# Set to PBS_BATCH to indicate the job is a batch job, or to
# PBS_INTERACTIVE to indicate the job is a PBS interactive job.
# PBS_O_QUEUE   The original queue you submitted to.
# PBS_QUEUE     The queue the job is executing from.
# PBS_JOBID     The job's PBS identifier.
# PBS_JOBNAME   The job's name.
#
# some echos for output, you may ignore them 
# but mind a new variable $TMPDIR
# this is scratch storage on the node you get onto, and it
# is way much quicker to copy things there, run there and back-copy in the end
 
 
APPTAG="[PBS_GRAPHLETSIMILARITY]"
MYHOME="/home/ts"

 
echo $APPTAG Working directory is $PBS_O_WORKDIR
echo $APPTAG tmp directory is $TMPDIR
echo $APPTAG env is $ENVIRONMENT
echo $APPTAG Running on host `hostname`
echo $APPTAG Time is `date`
echo $APPTAG Directory is `pwd`
echo $APPTAG This job runs on the following processors:
echo $APPTAG `cat $PBS_NODEFILE`
echo $APPTAG pbs job id is $PBS_JOBID


# This tells PBS what directory to go to
scp -r $MYHOME/software/plcc_cluster/ $TMPDIR/
PLCC_CLUSTER_DIR="$TMPDIR/plcc_cluster"
chmod -R ugo +rwx $TMPDIR/


# test one of them to be sure
PLCC_CLUSTER_RUN_DIR="$PLCC_CLUSTER_DIR/plcc_run"

SOME_PLCC_SCRIPT="$PLCC_CLUSTER_RUN_DIR/get_pdb_file.sh"
if [ ! -f "$SOME_PLCC_SCRIPT" ]; then
    echo "$APPTAG ERROR: The script '$SOME_PLCC_SCRIPT' does not exist. The plcc_cluster directory seems to be missing."
    echo "$APPTAG ERROR: The script '$SOME_PLCC_SCRIPT' does not exist. The plcc_cluster directory seems to be missing." >> $ERRORLOG
    exit 1
fi

PLCC_JAR="$PLCC_CLUSTER_RUN_DIR/plcc.jar"
if [ ! -f "$PLCC_JAR" ]; then
    echo "$APPTAG ERROR: The plcc JAR file '$PLCC_JAR' does not exist. You have to build PLCC and the libs and copy them over."
    echo "$APPTAG ERROR: The plcc JAR file '$PLCC_JAR' does not exist. You have to build PLCC and the libs and copy them over." >> $ERRORLOG
    exit 1
fi
 

echo -n "$APPTAG Starting to compute Graphletsimilarity for whole DB at: "
date  # not needed 
echo ""

cd $PLCC_CLUSTER_RUN_DIR && ./plcc NONE --compute-whole-db-graphlet-similarities

 
# nothing to copy back, results were written to the database
echo -n "$APPTAG Graphletsimilarity done at: "
date  # not needed 
echo ""

echo "$APPTAG All done, EOF."