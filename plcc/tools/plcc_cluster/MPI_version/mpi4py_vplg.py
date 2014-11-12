#!/usr/bin/env python
## This is the mpi4py script to start vplgWeb and other mass VPLG operations via openMPI.
## This script is run by the pbs job script, do NOT run it manually.
## Use 'qsub pbs_start_vplg_via_mpi4py.sh' instead.
## This script takes as argument a file which holds the full path to the PDB files (one file per line).


apptag = "[MPI4PY] "

print apptag + "Starting.\n"

silent = True

import sys
import os

if not silent:
  print apptag + "PATH is '" + str(sys.path) + "'.\n"
  
curpath=os.getcwd()
#sys.path.append(str(curpath + '/../../mpi4py/src/MPI/'))
#sys.path.append(str(curpath + '/../../mpi4py/build/lib.linux-x86_64-2.6/mpi4py/include/'))
#print apptag + "Appended copied MPI dirs to PATH. PATH is now '" + str(sys.path) + "'.\n"
from mpi4py import MPI
from mpi4py_tools import *
import shlex, subprocess
import os


if not silent:
   print apptag + "Gathering global MPI information.\n"
  
comm_world=MPI.COMM_WORLD
rank=comm_world.Get_rank()
apptag = "[MPI4PY] [" + str(rank) + "] "
pro_count=comm_world.Get_size()

jobs=[]
result=[]

if not silent:
   print apptag + "Preparing to split input data for nodes.\n"
  
if(rank==0):
   if not silent:
      print apptag + "[ROOT] I am responsible for splitting the input data, dividing jobs.\n"
   # input is a list of paths to PDB files
   input=open(sys.argv[1]).readlines()
   if not silent:
      print apptag + "[ROOT] There are " + str(len(input)) + " jobs total.\n"
      
   jobs=data_divide(input,pro_count)

if not silent:
   print apptag + "Getting my part of the job list.\n"
   
jobs=comm_world.scatter(jobs,root=0)

if not silent:
   print apptag + "There are " + str(len(jobs)) + " to do for me.\n"


# this script is run in the plcc_cluster/MPI_version directory, so go up one dir to plcc_cluster
rundir = "../"
if not os.path.exists(rundir):
   print apptag + "ERROR: The plcc_cluster directory does not exist at '" + rundir + "'.\n"
   sys.exit(1)

os.chdir(rundir)

if not silent:
   print apptag + "Changed directory to '" + os.getcwd() + "', this should be the plcc_cluster dir.\n"

rscript = "./process_single_pdb_file.sh"
if os.path.isfile(rscript):
   if not silent:
      print apptag + "Script '" + rscript + "' found in current dir, OK.\n"
   if os.access(rscript, os.X_OK):
      if not silent:
         print apptag + "Script '" + rscript + "' seems to be executable, OK.\n"
   else:
      print apptag + "ERROR: The script '" + rscript + "' does not seem to be executable.\n"
else:
   print apptag + "ERROR: The script '" + rscript + "' does not exist here.\n"

if not silent:
   print apptag + "Running script 'process_single_pdb_file.sh' for all " + str(len(jobs)) + " jobs (PDB files) in my list.\n"

for cur_job in jobs:
      ### <paralell operation> ###
#      if ((cur_job%7)==0):
#         result.append(cur_job)
      pdbfile=cur_job      
      # Note: the PLCC command line options are set in the settings_statistics.cfg file
      if not os.path.isfile(pdbfile):
         print apptag + "WARNING: PDB file '" + pdbfile + "' does not seem to exist.\n"
      command_line="/bin/bash ./process_single_pdb_file.sh " + pdbfile + ""
      if not silent:
         print apptag + "  Running command '" + command_line + "'.\n"
      args = shlex.split(command_line)
      p = subprocess.Popen(args)
      retVal = p.wait()
      if(retVal==0):
         if not silent:
            print apptag + "  Done, OK. Command '" + command_line + "' returned exit code " + str(retVal) + ".\n"
      else:
         print apptag + "ERROR: Command '" + command_line + "' returned exit code " + str(retVal) + ".\n"
      ### </paralell operation> ###
### collect all results in rootprocess - save/handle results:

if not silent:
   print apptag + "All my jobs are done."

#all_result=comm_world.gather(result,root=0)
#if(rank==0):
#    all_result=data_merge(all_result)
#    ### <handle output> ###
#    out=open("output.file","w")
#    for r in all_result:
#       out.write(str(r)+"\n")
#    ### </handle output>
#MPI.Finalize()

