e = open("dsspCode_Table_PTGL.csv", "w")


# iterate over the lines in the file
allSSEs = []
files = ['output1ns.txt','output667ns.txt', 'output1334ns.txt', 'output2000ns.txt']
for file in files:
    f = open(file, 'r')
    mode = False
    FirstResidueMode= True
    sse_code = ''
    prevDSSPend = 0
    for line in f:
        if mode == True:
            if line[0] == '[':
                part1 = line.split("-")[0]
                dsspStart = int(part1.split(":")[2])
                part2 = line.split("-")[1]
                dsspEnd = int(part2.split(",")[0])
                #saves all dssp codes
                if FirstResidueMode:# for the first residue add "-" at the beginning
                    for i in range(dsspStart-1):
                        sse_code = sse_code +'-'
                        #print("-")
                    FirstResidueMode = False
                else:
                    for i in range(prevDSSPend,dsspStart-1):
                        sse_code = sse_code +'-'
                        #print("-")
                for j in range(dsspEnd-dsspStart+1 ):
                    sse_code = sse_code + line[1]
                prevDSSPend = dsspEnd
            else:
                mode =False
        if 'PTGL-SSEs' in line:
            mode = True
        if "Calculating complex graph (CG) of type albelig." in line:
            break
    allSSEs.append(sse_code)
indices = []
for j in allSSEs:
    indices.append(len(j))
ind = min(indices)
e.write('Res, 1ns, 667ns, 1334ns, 2000ns \n')
for i in range(ind):
    e.write(','+str(allSSEs[0][i])+','+str(allSSEs[1][i])+','+ str(allSSEs[2][i])+','+str(allSSEs[3][i])+'\n')
print('finished')
