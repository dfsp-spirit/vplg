e = open("dsspCode_TableNew.csv", "w")
# open file to read
allSSEs = []
files = ['noQ_r1_frame1ns.dssp','noQ_r1_frame667ns.dssp', 'noQ_r1_frame1334ns.dssp', 'noQ_r1_frame2000ns.dssp']
for file in files:
    f = open(file, 'r')
    mode = False
    sse_code = ''
    for line in f:
        if mode == True:
            if line[16] != ' ':
                sse_code = sse_code+line[16]
            else:
                sse_code = sse_code+'-'
            #print(line[13], line[16])
        if '#  RESIDUE' in line:
            mode = True
            pass
    allSSEs.append(sse_code)

e.write('Res, 1ns, 667ns, 1334ns, 2000ns \n')
for i in range(len(sse_code)):
    e.write(','+str(allSSEs[0][i])+','+str(allSSEs[1][i])+','+ str(allSSEs[2][i])+','+str(allSSEs[3][i])+'\n')
print('finished')
