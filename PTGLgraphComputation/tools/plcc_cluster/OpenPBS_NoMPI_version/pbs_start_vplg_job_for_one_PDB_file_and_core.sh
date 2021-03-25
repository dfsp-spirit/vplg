#!/bin/bash
# pbs_start_vplg_for_one_node.sh -- pbs job script to run VPLG on a part of the whole PDB database via PBS and MPI on a cluster.
#
# This script starts one job and reserves one CPU (not one node!) for it.
#
# Do NOT start this script directly. It should be submitted to the openpbs queue via the 'qsub' command.
#
# Parameters: none, but the input file is read from the ENV variable $PDBFILE which has to be set (see 'qsub -v' documentation on how to do this in openPBS)
#
# This script is submitted to the queue by the 'pbs_start_all_jobs_on_all_nodes_whole_PDB.sh' script n times for n total PDB files.
#
#
# Written by ts.
#
# remarks: a line beginning with # is a comment;
# a line beginning with #PBS is a pbs command;
# assume (upper/lower) case to be sensitive;
#
# use: submit job with
# qsub myjobscript
#
# job name (default is name of pbs script file)
#PBS -N vplgsinglejob
# standard queue is called batch
#PBS -q batch
#
# do not rerun this job if it fails
#PBS -r n
# resource limits: max. wall clock time during 
# which job can be running
#PBS -l walltime=01:00:00
#PBS -l nodes=1:ppn=1 
# send me mail when job (b)egins, (e)nds or (a)borts
##PBS -m abe  # not used so far
##PBS -M nodi@odysseus.modlab  # not used so far
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

APPTAG="[PBS_VPLG_ONEPDBFILE_JOB]"
MYHOME="/home/ts"
NUM_PROCESSORS_PER_NODE=1

ERRORLOG="/dev/stderr"
SILENT="YES"

## This is now read from an environment variable which is passed to this script via the '-v' option of the 'qsub' pbs command
INPUT_FILE=$PDBFILE

if [ "$SILENT" = "NO" ]; then
    # report some stuff for log file
    echo $APPTAG ----- Submitting job to pbs queue -----
    echo $APPTAG Settings:
    echo "$APPTAG -----"
    echo $APPTAG MYHOME is $MYHOME
    echo $APPTAG Working directory is $PBS_O_WORKDIR
    echo $APPTAG The tmp directory is $TMPDIR
    echo $APPTAG Environment is $ENVIRONMENT
    echo $APPTAG Running on host `hostname`
    echo $APPTAG Time is `date`
    echo $APPTAG Directory is `pwd`
    echo -n "$APPTAG This job runs on the following processors: "
    echo `cat $PBS_NODEFILE`
    echo $APPTAG A java binary is at `which java`
    echo $APPTAG Note though that the plcc shell script in plcc_run may use another java
    echo $APPTAG pbs job id is $PBS_JOBID
    echo "$APPTAG Using $NUM_PROCESSORS_PER_NODE processors"
    echo "$APPTAG Using '$INPUT_FILE' as input file."
else
    echo $APPTAG Running on host `hostname` 
fi

if [ ! -f "$INPUT_FILE" ]; then
    echo "$APPTAG ERROR: The PDB input file '$INPUT_FILE' does not exist (wd=`pwd`)."
    echo "$APPTAG ERROR: The PDB input file '$INPUT_FILE' does not exist (wd=`pwd`)." >> $ERRORLOG
    exit 1
fi

if [ "$SILENT" = "NO" ]; then
    echo "$APPTAG The input PDB file is $INPUT_FILE."
    echo "$APPTAG -----"
fi
 



## copy my python MPI job scripts to temporary directory 
#echo "$APPTAG Copying files to temporary directory '$TMPDIR'..."
## This will also coyp the graphletanalyer, which is in plcc_run/
#scp -r $MYHOME/software/plcc_cluster/ $TMPDIR/
#PLCC_CLUSTER_DIR="$TMPDIR/plcc_cluster"
#chmod -R ugo+rwx $TMPDIR/


# we now use plcc_cluster from the global dir
PLCC_CLUSTER_DIR="$MYHOME/software/plcc_cluster"


## ensure important scripts are executable
#chmod u+x $TMPDIR/plcc_cluster/MPI_version/*.py
#chmod u+x $TMPDIR/plcc_cluster/process_single_pdb_file.sh
#chmod u+x $TMPDIR/plcc_cluster/plcc_run/get_pdb_file.sh
#chmod u+x $TMPDIR/plcc_cluster/plcc_run/create_dssp_file.sh
#chmod u+x $TMPDIR/plcc_cluster/plcc_run/plcc
#chmod u+x $TMPDIR/plcc_cluster/plcc_run/splitpdb

# test one of them to be sure
PLCC_CLUSTER_RUN_DIR="$PLCC_CLUSTER_DIR/plcc_run"

SOME_PLCC_SCRIPT="$PLCC_CLUSTER_RUN_DIR/get_pdb_file.sh"
if [ ! -f "$SOME_PLCC_SCRIPT" ]; then
    echo "$APPTAG ERROR: The script '$SOME_PLCC_SCRIPT' does not exist. The plcc_cluster directory seems to be missing."
    echo "$APPTAG ERROR: The script '$SOME_PLCC_SCRIPT' does not exist. The plcc_cluster directory seems to be missing." >> $ERRORLOG
    exit 1
fi

PLCC_JAR="$PLCC_CLUSTER_RUN_DIR/PTGLgraphComputation.jar"
if [ ! -f "$PLCC_JAR" ]; then
    echo "$APPTAG ERROR: The plcc JAR file '$PLCC_JAR' does not exist. You have to build PLCC and the libs and copy them over."
    echo "$APPTAG ERROR: The plcc JAR file '$PLCC_JAR' does not exist. You have to build PLCC and the libs and copy them over." >> $ERRORLOG
    exit 1
fi

GRAPHLETANALYSER="$PLCC_CLUSTER_RUN_DIR/graphletanalyser"
if [ ! -f "$GRAPHLETANALYSER" ]; then
    echo "$APPTAG WARNING: The graphletanaylser binary '$GRAPHLETANALYSER' does not exist. You have to build it and copy it over to plcc_run."
    echo "$APPTAG WARNING: The graphletanaylser binary '$GRAPHLETANALYSER' does not exist. You have to build it and copy it over to plcc_run." >> $ERRORLOG
fi

## copy DSSP binary
#DSSP_SOURCE_BINARY="$MYHOME/software/dssp/dsspcmbi"
#scp $DSSP_SOURCE_BINARY $PLCC_CLUSTER_RUN_DIR/

#DSSP_BINARY="$PLCC_CLUSTER_RUN_DIR/dsspcmbi"
DSSP_BINARY="$MYHOME/software/dssp/dsspcmbi"
chmod +x $DSSP_BINARY
if [ ! -x "$DSSP_BINARY" ]; then
    echo "$APPTAG ERROR: The DSSP binary '$DSSP_BINARY' does not exist or is not executable."
    echo "$APPTAG ERROR: The DSSP binary '$DSSP_BINARY' does not exist or is not executable." >> $ERRORLOG
    exit 1
fi


## Create the PLCC_OUTPUT_DIR which is defined in the settings.
# We do NOT copy results from this dir back to the cluster head atm, we keep them in /tmp/ only. This is a hack due to
# limited disk space.
source $PLCC_CLUSTER_DIR/settings_statistics.cfg
if [ ! -d $PLCC_OUTPUT_DIR ]; then
  if [ "$SILENT" = "NO" ]; then
      echo "$APPTAG Creating output directory '$PLCC_OUTPUT_DIR'."
  fi
  mkdir -p $PLCC_OUTPUT_DIR
fi
chmod ugo+rwx $PLCC_OUTPUT_DIR || echo "$APPTAG ERROR: Could not change fs permissions for PLCC output directory '$PLCC_OUTPUT_DIR'."


if [ ! -r "$INPUT_FILE" ]; then
    echo "$APPTAG ERROR: Cannot read input file '$INPUT_FILE'. This should be a PDB file. Exiting."
    echo "$APPTAG ERROR: Cannot read input file '$INPUT_FILE'. This should be a PDB file. Exiting." >> $ERRORLOG
    exit 1
fi



cd "$PLCC_CLUSTER_DIR"


RUN_CMD="/bin/bash ./process_single_pdb_file.sh $INPUT_FILE"

if [ "$SILENT" = "NO" ]; then
  echo "$APPTAG Now in directory '$(pwd)'."
  echo "$APPTAG The run command to be executed is '$RUN_CMD'."
fi



if [ "$SILENT" = "NO" ]; then
  echo "$APPTAG Purging old PDB files and OpenPBS job logs (output and error)."
fi

## purge PDB files which have not been accessed for 60 minutes
find $PLCC_CLUSTER_RUN_DIR -type f -amin +60 -name "????.pdb"  -delete
find $PLCC_CLUSTER_RUN_DIR -type f -amin +60 -name "????.dssp"  -delete

## running this job script for all > 100.000 PDB files will result in absurd amount of openPBS log files, which may brake everything.
## so we need to delete those damn files as well. The name depends on the openPBS job name, which ise set in this bash scripts (see the line starting with '#PBS -N' at the top)
## Update: delete only log files with size < 2 byte. if all scripts are in silent mode, only logs with relevant content will be kept this way.
find $PLCC_CLUSTER_DIR/OpenPBS_NoMPI_version/ -type f -amin +60 -name "vplgsinglejob.o*" -size -2b -delete
find $PLCC_CLUSTER_DIR/OpenPBS_NoMPI_version/ -type f -amin +60 -name "vplgsinglejob.e*" -size -2b -delete


# run script
if [ "$SILENT" = "NO" ]; then
  echo -n "$APPTAG Started command at: "
  date
fi


## run it!
$RUN_CMD

if [ "$SILENT" = "NO" ]; then
  echo -n "$APPTAG The command $RUN_CMD terminated at: "
  date
fi

# copy-back everything needed. $TMPDIR gets cleaning in the new cluster!
# no need for this because we write to a permament directory directly, it is mounted via NFS
#scp -r output.file $PBS_O_HOST:$PBS_O_WORKDIR/
#cp -r $PLCC_OUTPUT_DIR $PLCC_OUTPUT_COPY_DIR_ALL_NODES

if [ "$SILENT" = "NO" ]; then
  echo "$APPTAG Job done, EOF."
fi

