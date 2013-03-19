#!/usr/bin/env python

from mpi4py import MPI
from MpiTools import *
comm_world=MPI.COMM_WORLD
rank=comm_world.Get_rank()
pro_count=comm_world.Get_size()

jobs=[]
result=[]

print "START"

if(rank==0):
   input=open(sys.argv[1]).readlines()
   jobs=data_divide(input,pro_count)

jobs=comm_world.scatter(jobs,root=0)
print "["+str(rank)+"]: "+str(len(jobs))+" to do\n"

### 
for cur_job in jobs:
      ### <paralell operation> ###
      if ((cur_job%7)==0):
         result.append(cur_job)
      ### </paralell operation> ###
### collect all results in rootprocess - save/handle results:
all_result=comm_world.gather(result,root=0)
if(rank==0):
    all_result=data_merge(all_result)
    ### <handle output> ###
    out=open("output.file","w")
    for r in all_result:
       out.write(str(r)+"\n")
    ### </handle output>
MPI.Finalize()