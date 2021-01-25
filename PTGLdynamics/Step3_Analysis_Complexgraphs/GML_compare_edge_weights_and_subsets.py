########### imports ###########

import sys
import os

########### check argument(s) ###########

if len(sys.argv) < 3:
    print("[Error] Expected file path to first and second GML file. Exiting now.")
    exit()
    
fp1 = sys.argv[1]
fp2 = sys.argv[2]

########### vamos ###########

# read file in
print("Reading file in")
with open(fp1, "r") as f:
    gml_lines = f.read().split("\n")


# create dict
print("Creating dictionary")
alledges = {}
allnodes = []
node_section = False
edge_section = False
weight = ''
for line in gml_lines:
    # handle in which section we are
    if "node [" in line:
        node_section = True
    if node_section == True and "label" in line:
        linelist = line.split('"')
        allnodes.append(linelist[1])
        node_section = False
    if "edge [" in line:
        edge_section = True
    if edge_section:
        if "source" in line:
            linelist = line.split(" ")
            source = int(linelist[1])
        elif "target" in line:
            linelist = line.split(" ")
            target = int(linelist[1])
        elif "label" in line:
            linelist = line.split('"')
            weight = linelist[1]
        elif weight != '':
            alledges[(allnodes[source], allnodes[target])] = weight
            weight = ''
            edge_section = False

#print(alledges)
print(allnodes)


with open(fp2, "r") as f2:
    gml_lines2 = f2.read().split("\n")


# create dict
print("Creating dictionary")
alledges2 = {}
allnodes2 = []
node_section = False
edge_section = False
weight = ''
for line in gml_lines2:
    # handle in which section we are
    if "node [" in line:
        node_section = True
    if node_section == True and "label" in line:
        linelist = line.split('"')
        allnodes2.append(linelist[1])
        node_section = False
    if "edge [" in line:
        edge_section = True
    if edge_section:
        if "source" in line:
            linelist = line.split(" ")
            source = int(linelist[1])
        elif "target" in line:
            linelist = line.split(" ")
            target = int(linelist[1])
        elif "label" in line:
            linelist = line.split('"')
            weight = linelist[1]
        elif weight != '':
            alledges2[(allnodes2[source], allnodes2[target])] = weight
            weight = ''
            edge_section = False

#print(alledges2)
print(allnodes2)

#node degree
def countDegree( nodeName, edges):
    nodeDegree = []
    for i in nodeName:
        counter = 0
        for key in edges:
            if i in key:
                counter = counter + 1
        nodeDegree.append((i, counter))
    return nodeDegree

print(countDegree( allnodes, alledges))
print(countDegree( allnodes2, alledges2))

keys = []
graph1Val = [] # all edge weights of graph 1
graph2Val = [] # all edge weights of graph 1
diff = [] # all differences of edge weights
deviation =[]# not common edges
with open("compared" + fp1 + fp2+".csv", "w") as f3:
    f3.write("edge, value of graph1, value of graph2, difference \n")
    for key in alledges2:
        if key in alledges:
            #keys.append(key)
            graph1Val.append(int(alledges[key]))
            graph2Val.append(int(alledges2[key]))
            diff.append(abs( int(alledges2[key]) - int(alledges[key])))
            f3.write(str('{'+key[0]+' '+key[1])+'},'+alledges[key]+','+ alledges2[key]+','+\
                     str(abs( int(alledges2[key]) - int(alledges[key])))+"\n")
        elif key[::-1] in alledges:
            #keys.append(key[::-1])
            graph1Val.append(int(alledges[key[::-1]]))
            graph2Val.append(int(alledges2[key]))
            diff.append(abs( int(alledges2[key]) - int(alledges[key[::-1]])))
            f3.write(str('{'+key[1]+' '+key[0])+'},'+alledges[key[::-1]]+','+ alledges2[key]\
                     +','+str(abs( int(alledges2[key]) - int(alledges[key[::-1]])))+"\n")
        else:
            deviation.append(key)
    f3.write("\n \n edges of graph2 not graph1: " + str(deviation))
    deviation = []
    for key in alledges:
        if key in alledges2 or key[::-1] in alledges2:
            pass
        else:
            deviation.append(key)
    f3.write("\n \n edges of graph1 not in graph2: "+ str(deviation))

'''
# create result file
print("Creating result file")
with open("result_Ge2i_" + fp + ".gml", "w") as f:
    for line in new_gml_lines:
        f.write(line + "\n")
'''    
