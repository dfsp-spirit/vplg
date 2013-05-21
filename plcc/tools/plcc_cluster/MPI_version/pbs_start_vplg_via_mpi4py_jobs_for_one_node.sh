#!/bin/bash
# pbs_start_vplg_via_mpi4py_jpbs_for_one_node.sh -- pbs job script to run VPLG on a part of the whole PDB database via PBS and MPI on a cluster.
#
# This script allows you to use all CPUs of one node. The other update scripts only use one CPU per networked computer.
#
# Do NOT start this script directly. It should be submitted to the openpbs queue via the 'qsub' command.
#
# Parameters: none, but the input file is read from the ENV variable $PDBFILELIST which has to be set (see 'qsub -v' documentation on how to do this in openPBS)

#
# This script is submitted to the queue by the 'pbs_start_all_jobs_on_all_nodes_whole_PDB.sh' script n times, each with 1 nth of the PDB files.
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
#PBS -N myjob
# standard queue is called batch
#PBS -q batch
#
# do not rerun this job if it fails
#PBS -r n
# resource limits: max. wall clock time during 
# which job can be running
#PBS -l walltime=1500:00:00
#PBS -l nodes=1:ppn=8 
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

APPTAG="[PBS_VPLG_VIA_MPI4PY]"
MYHOME="/home/ts"
NUM_PROCESSORS_PER_NODE=8

ERRORLOG="/dev/stderr"

#TODO: get the right input file for this node here! The file below is the full list, which makes no sense.
#INPUT_FILE="$TMPDIR/plcc_cluster/status/dbinsert_file_list.lst"	# this file contains a list of all PDB files that should be handled. You can create the list with the scripts in the plcc_cluster/ directory

## This is now read from an environment variable which is passed to this script via the '-v' option of the 'qsub' pbs command
INPUT_FILE=$PDBFILELIST

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
echo $APPTAG The java binary is at `which java`
echo $APPTAG pbs job id is $PBS_JOBID
echo "$APPTAG Using $NUM_PROCESSORS_PER_NODE processors"
echo "$APPTAG Using '$INPUT_FILE' as input file."

if [ ! -f "$INPUT_FILE" ]; then
    echo "$APPTAG ERROR: The input file '$INPUT_FILE' does not exist (wd=`pwd`)."
    echo "$APPTAG ERROR: The input file '$INPUT_FILE' does not exist (wd=`pwd`)." >> $ERRORLOG
    exit 1
fi

echo "$APPTAG The input file lists `wc -l $INPUT_FILE` PDB files to handle during this pbs job."
echo "$APPTAG -----"
 


echo "$APPTAG Loading bash modules..." 
## load openmpi environment module
. /usr/share/Modules/3.2.10/init/bash
#. /usr/share/modules/init/bash
module load gnu-openmpi
#module load openmpi

## set path to openmpi binarie
export PATH="/usr/lib64/mpi/gcc/openmpi/bin:$PATH"
#export LD_LIBRARY_PATH="/usr/lib64/mpi/gcc/openmpi/lib64:$LD_LIBRARY_PATH"


## copy my python MPI job scripts to temporary directory 
#echo "$APPTAG Copying files to temporary directory '$TMPDIR'..."
scp -r $MYHOME/software/plcc_cluster/ $TMPDIR/
PLCC_CLUSTER_DIR="$TMPDIR/plcc_cluster"

# we now use plcc_cluster from the global dir
#PLCC_CLUSTER_DIR="$MYHOME/software/plcc_cluster"


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

PLCC_JAR="$PLCC_CLUSTER_RUN_DIR/plcc.jar"
if [ ! -f "$PLCC_JAR" ]; then
    echo "$APPTAG ERROR: The plcc JAR file '$PLCC_JAR' does not exist. You have to build PLCC and the libs and copy them over."
    echo "$APPTAG ERROR: The plcc JAR file '$PLCC_JAR' does not exist. You have to build PLCC and the libs and copy them over." >> $ERRORLOG
    exit 1
fi

## copy DSSP binary
DSSP_SOURCE_BINARY="$MYHOME/software/dssp/dsspcmbi"
scp $DSSP_SOURCE_BINARY $PLCC_CLUSTER_RUN_DIR/

DSSP_BINARY="$PLCC_CLUSTER_RUN_DIR/dsspcmbi"
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
  echo "$APPTAG Creating output directory '$PLCC_OUTPUT_DIR' on this node."
  mkdir -p $PLCC_OUTPUT_DIR
fi
chmod ugo+rwx $PLCC_OUTPUT_DIR || echo "$APPTAG ERROR: Could not change fs permissions for PLCC output directory '$PLCC_OUTPUT_DIR'."

## copy mpi4py software
# not done anymore, we start it from the mounted /develop/ directory now
#scp -r $MYHOME/software/openmpi/mpi4py/ $TMPDIR/
#MPI4PY_DIR="$TMPDIR/mpi4py"

MPI4PY_DIR="/develop/openmpi_mpi4py/mpi4py"
#MPI4PY_SRC_DIR="$MPI4PY_DIR/src/MPI"

if [ ! -d "$MPI4PY_DIR" ]; then
    echo "$APPTAG ERROR: The mpi4py directory '$MPI4PY_DIR' does not exist."
    echo "$APPTAG ERROR: The mpi4py directory '$MPI4PY_DIR' does not exist." >> $ERRORLOG
    exit 1
fi

#if [ ! -d "$MPI4PY_SRC_DIR" ]; then
#    echo "$APPTAG ERROR: The mpi4py source directory '$MPI4PY_SRC_DIR' does not exist. Copying seems to have failed."
#    exit 1
#fi

#export PYTHONPATH="$MPI4PY_SRC_DIR:$PYTHONPATH"
#export PYTHONPATH="$MPI4PY_SRC_DIR/include:$PYTHONPATH"
#export PYTHONPATH="/develop/python2.7_site_packages/:$PYTHONPATH"
export PYTHONPATH="$MPI4PY_DIR/build/lib.linux-x86_64-2.7:$PYTHONPATH"

if [ ! -r "$INPUT_FILE" ]; then
    echo "$APPTAG ERROR: Cannot read input file '$INPUT_FILE'. This should be a file holding paths to all PDB files, one per line. Exiting."
    echo "$APPTAG ERROR: Cannot read input file '$INPUT_FILE'. This should be a file holding paths to all PDB files, one per line. Exiting." >> $ERRORLOG
    exit 1
fi




MPI4PY_SCRIPT="mpi4py_vplg.py"

echo "$APPTAG Running mpi4py python scripts via mpirun..."

#PYTHON="python"
PYTHON="$MPI4PY_DIR/build/exe.linux-x86_64-2.7/python2.7-mpi"
export LD_LIBRARY_PATH="$MPI4PY_DIR/build/lib.linux-x86_64-2.7/mpi4py/:$LD_LIBRARY_PATH"

## We need libmpi.so.0, but only have libmpi.so.1 at /usr/lib64/mpi/gcc/openmpi/lib64/libmpi.so.1.
#  So we create our own lib dir and symlink it from there.
#OPENMPI_LIBS="$TMPDIR/libs/"
#mkdir -p $OPENMPI_LIBS
#ln -s /usr/lib64/mpi/gcc/openmpi/lib64/libmpi.so.1 $MYLIBS/libmpi.so.0
#ln -s /usr/lib64/mpi/gcc/openmpi/lib64/libopen-pal.so $MYLIBS/libopen-pal.so.0
#ln -s /usr/lib64/mpi/gcc/openmpi/lib64/libopen-rte.so $MYLIBS/libopen-rte.so.0
#ln -s /develop/openmpi_build/lib/libmpi.so.1 $OPENMPI_LIBS/libmpi.so.0
#ln -s /develop/openmpi_build/lib/libopen-pal.so $OPENMPI_LIBS/libopen-pal.so.0
#ln -s /develop/openmpi_build/lib/libopen-rte.so $OPENMPI_LIBS/libopen-rte.so.0

OPENMPI_LIBS="/develop/openmpi_mpi4py/openmpi/lib/"

if [ ! -d "$OPENMPI_LIBS" ]; then
    echo "$APPTAG ERROR: Directory '$OPENMPI_LIBS' does not exist. This should contain the openMPI libs like libmpi.so, libopen-pal.so and libopen-rte.so. Exiting."
    echo "$APPTAG ERROR: Directory '$OPENMPI_LIBS' does not exist. This should contain the openMPI libs like libmpi.so, libopen-pal.so and libopen-rte.so. Exiting." >> $ERRORLOG
    exit 1
fi

export LD_LIBRARY_PATH="$OPENMPI_LIBS:$LD_LIBRARY_PATH"

echo "$APPTAG LD_LIBRARY_PATH is '$LD_LIBRARY_PATH'"
echo "$APPTAG PYTHONPATH is '$PYTHONPATH'"

#OPENMPI_DEFAULT_HOSTFILE="/etc/openmpi-default-hostfile"
OPENMPI_DEFAULT_HOSTFILE="none"



cd $PLCC_CLUSTER_DIR/MPI_version/
MPIRUN_CMD="mpirun --default-hostfile $OPENMPI_DEFAULT_HOSTFILE -np $NUM_PROCESSORS_PER_NODE $PYTHON $MPI4PY_SCRIPT $INPUT_FILE"

echo "$APPTAG Now in directory '$(pwd)'."
echo "$APPTAG The mpirun command to be executed is '$MPIRUN_CMD'."

# run script
echo -n "$APPTAG Started script '$MPI4PY_SCRIPT' at: "
date


## run it!
$MPIRUN_CMD


echo -n "$APPTAG The script '$MPI4PY_SCRIPT' terminated at: "
date

# copy-back everything needed. $TMPDIR gets cleaning in the new cluster!
# no need for this because we write to a permament directory
#scp -r output.file $PBS_O_HOST:$PBS_O_WORKDIR/

echo "$APPTAG All done, EOF."

