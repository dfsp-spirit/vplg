import fileinput
import sys
import os
import time

_start_time = time.time()

#reading COMPND.txt file which is the header of a  pdb file
#f = open("COMPND.txt", "r")
#header = f.read()
#header = header+'\n'

#amino acids (residues)in column 18-20 
aaCode = [ 'ALA', 'ARG', 'ASN', 'ASP', 'CYS', 'GLN', 'GLU', 'GLY', 'HIS', 'ILE', 'LEU', 'LYS', 'MET', 'PHE', 'PRO', 'SER', 'THR', 'TRP', 'TYR', 'VAL']
#list of ns for filename
fileNO = [1, 667, 1334, 2000]


for allfile in os.listdir('.'):
    if allfile.endswith(".pdb"):
        cnt = 0
        hetatms = ''
        currChainID = 1 # saves current atom ID for identifying chain brakes
        prevChainID = 1
        prevAtomID = 0
        prevLine = ''
        addNo = 0
        for line in fileinput.input(allfile, inplace=True):
            #header
            if 'ATOM' == line[0:4]:
                if line[75:76] != ' ':
                    chainID = line[75:76]
                else:
                    chainID = line[21]
                line = line.replace(line, line[:21]+chainID +line[22::])
                if line[17:20] not in aaCode:
                    line = line.replace(line, 'HETATM'+line[6:])
                    #hetatms=hetatms+str(line)
                if line[13:16] == 'OT1':
                    line = line.replace(line, line[:13]+'OXT'+line[16::])
                elif line[13:16] == 'OT2':
                    line = line.replace(line, line[:13]+'O  '+line[16::])
                currChainID = line[21]
                prevTER = False
                if currChainID != prevChainID and cnt != 1:
                    numberofSpaces = len(line[6:11])- len(str(prevAtomID+1))
                    addNo = addNo +1 #ATOM ID hochzaehlen
                    print('TER   '+str(numberofSpaces*' ')+str(prevAtomID+1)+'      '+prevLine[17:26])
                    line = line[0:6]+str(numberofSpaces*' ')+str(prevAtomID+2)+line[11::]
                    addNo = addNo +1 #ATOM ID hochzaehlen
                    prevAtomID = prevAtomID +2
                else:
                    numberofSpaces = len(line[6:11])- len(str(prevAtomID+1))
                    line = line[0:6]+str(numberofSpaces*' ')+str(prevAtomID+1)+line[11::]
                    prevAtomID = prevAtomID + 1
                sys.stdout.write(line)
                prevLine = line
                prevChainID = line[21]
            cnt += 1        
  
print('finish pdb file overwritting')
print("-- %s seconds ---"% (time.time()- _start_time))

'''

filepath = 'noQ_r1_frame1ns.pdb'
with open(filepath) as fp:
   line = fp.readline()
   cnt = 1
   while line:#print(line[21], line[72:76])
       if line[17:20] not in aaCode:
           print(line[17:20])
           print(line[0:4])
           
       line = fp.readline()
       cnt += 1
'''
