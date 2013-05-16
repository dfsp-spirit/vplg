#!/usr/bin/env python
## This is the mpi4py script to start vplgWeb and other mass VPLG operations via openMPI.
## This script is run by the pbs job script, do NOT run it manually.
## Use 'qsub pbs_start_vplg_via_mpi4py.sh' instead.
## This script takes as argument a file which holds the full path to the PDB files (one file per line).

print "[MPI4PY] Starting.\n"

import sys
import os
print "[MPI4PY] PATH is '" + str(sys.path) + "'.\n"
curpath=os.getcwd()
sys.path.append(str(curpath + '/../../mpi4py/src/MPI/'))
sys.path.append(str(curpath + '/../../mpi4py/build/lib.linux-x86_64-2.6/mpi4py/include/'))
print "[MPI4PY] Appended copied MPI dirs to PATH. PATH is now '" + str(sys.path) + "'.\n"
from mpi4py import MPI
from mpi4py_tools import *
import shlex, subprocess
import os



print "[MPI4PY] Gathering global MPI information.\n"
comm_world=MPI.COMM_WORLD
rank=comm_world.Get_rank()
pro_count=comm_world.Get_size()

jobs=[]
result=[]

print "[MPI4PY] Preparing to split input data for nodes.\n"
if(rank==0):
   print "[MPI4PY] [ROOT] I am responsible for splitting the input data, dividing jobs.\n"
   # input is a list of paths to PDB files
   input=open(sys.argv[1]).readlines()
   print "[MPI4PY] [ROOT] There are " + str(len(input)) + " jobs total.\n"
   jobs=data_divide(input,pro_count)

print "[MPI4PY] Getting my part of the job list.\n"
jobs=comm_world.scatter(jobs,root=0)
print "[MPI4PY] [" + str(rank) + "]: " + str(len(jobs)) + " to do.\n"


# this script is run in the plcc_cluster/MPI_version directory, so go up to plcc_cluster/
os.chdir("../")

print "[MPI4PY] Running script 'process_single_pdb_file.sh' for all " + str(len(jobs)) + " jobs (PDB files) in my list.\n"

for cur_job in jobs:
      ### <paralell operation> ###
#      if ((cur_job%7)==0):
#         result.append(cur_job)
      pdbfile=cur_job      
      # Note: the PLCC command line options are set in the settings_statistics.cfg file
      command_line="./process_single_pdb_file.sh " + pdbfile + ""
      args = shlex.split(command_line)
      p = subprocess.Popen(args)
      #exitCode = p.wait()
      ### </paralell operation> ###
### collect all results in rootprocess - save/handle results:

#all_result=comm_world.gather(result,root=0)
#if(rank==0):
#    all_result=data_merge(all_result)
#    ### <handle output> ###
#    out=open("output.file","w")
#    for r in all_result:
#       out.write(str(r)+"\n")
#    ### </handle output>
MPI.Finalize()
