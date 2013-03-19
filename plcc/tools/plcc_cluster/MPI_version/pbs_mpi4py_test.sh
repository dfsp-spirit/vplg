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
 
 
echo Working directory is $PBS_O_WORKDIR
echo tmp directory is $TMPDIR
echo env is $ENVIRONMENT
echo Running on host `hostname`
echo Time is `date`
echo Directory is `pwd`
echo This job runs on the following processors:
echo `cat $PBS_NODEFILE`
echo pbs job id is $PBS_JOBID
 
# This tells PBS what directory to go to
cd $PBS_O_WORKDIR
 
hostname # not needed
#date  # not needed

# load openmpi environment module
. /usr/share/modules/init/bash
module load openmpi.module

# copy files in temporary directory 
scp /home/hs/src/*.py $TMPDIR
scp /home/hs/src/py_scripts/mpi4py_test.py $TMPDIR

cd $TMPDIR

#chmod u+x check_2x_pdbs.py

# create links (needed because data files are not copied into tmp directory)
# NOT USED IN TESTFILE
ln -s /develop/hendrik/textfiles $TMPDIR/textfiles
ln -s /develop/hendrik/PDB $TMPDIR/PDB
ln -s /home/hs/src/mpi4py-1.2.2/ $TMPDIR/mpi4py
export LD_LIBRARY_PATH=/home/hs/libs/:${LD_LIBRARY_PATH}

# run script
echo -n "started at: "
date

#mpirun -np 8 /home/hs/src/mpi4py-1.2.2/build/exe.linux-x86_64-2.7/python2.7-mpi /home/hs/src/py_scripts/mpi4py_test.py
#mpirun -np 8 /home/hs/src/mpi4py-1.2.2/build/exe.linux-x86_64-2.7/python2.7-mpi mpi4py_test.py
mpirun -np 8 python mpi4py_test.py


echo -n "terminated at: "
date

# copy-back everything needed. $TMPDIR gets cleaning in the new cluster!
scp -r output.file $PBS_O_HOST:$PBS_O_WORKDIR/

 
