#################################################
#################### HOW TO #####################
############### USE THIS TEMPLATE ###############
"""
- argparse
    - describe what the script does (see TODO)
    - add command line arguments with it
    - argument file can be specified with @filepath
- logging
    - use the function 'log' for each and every print!
        -> d(ebug) for debug messages
        -> i(nfo) for status and meta information
        -> w(arning), e(rror), c(ritical) for more severe messages
        -> without a level to print results and if required obligatory non-result prints
            -> command line option 'silent' should suppress those obligatory prints so that only results are printed
- pipelining
    - write the results with -o to an output file to use as next input or
    - use 'silent' mode (if obligatory prints exist) and pipe the output of stdout directly to next script
        -> all debug, info, warning etc. are printed to stderr
- imports from not built-in modules in the respective section with error handling and useful prints where to find the module and if possible how to install it
- change version to your script's version (see TODO)
        
(Remove this HOW TO and have fun programming!)
"""


########### settings ###########

# This script's version as MAJOR.MINOR.PATCH
#   major: big (re-)implementation, new user interface, new overall architecture
#   minor: new functions, git merge
#   patch: fixes, small changes
#   no version change: fix typos, changes to comments, debug prints, small changes to non-result output, changes within git branch
# -> only increment with commit / push / merge not while programming
version = "1.0.0"  # TODO version of this template, change this to 1.0.0 for a new script or 2.0.0 if you upgrade another script to this template's architecture


########### built-in imports ###########

import sys
import os
import argparse
import logging
import traceback
import pathlib


########### functions ###########


def check_file_writable(fp):
    """Checks if the given filepath is writable"""
    if os.path.exists(fp):
        if os.path.isfile(fp):
            return os.access(fp, os.W_OK)
        else:
            return False
    # target does not exist, check perms on parent dir
    parent_dir = os.path.dirname(fp)
    if not parent_dir: parent_dir = '.'
    return os.access(parent_dir, os.W_OK)


def log(message, level=""):
    """Prints the message according to level of severity and output settings"""
    if (level == "c"):
        logging.critical(message)
    if (level == "e"):
        logging.error(message)
    elif (level == "w"):
        logging.warning(message)
    elif (level == "i"):
        logging.info(message)
    elif (level == "d"):
        logging.debug(message)
    else:
        if (args.outputfile == ""):
            print(message)
        else:
            global output_file
            output_file.write(message + "\n")


########### configure logger ###########

logging.basicConfig(format = "[%(levelname)s] %(message)s")
            
            
########### not built-in imports ###########

# EXAMPLE - can be removed
"""
    from pygmlparser.Parser import Parser
except ModuleNotFoundError as exception:
    log(exception,"e")
    traceback.print_exc()
    log("  Module from https://github.com/hasii2011/PyGMLParser seems to be missing.", "e")
    log("  Try installing it with 'pip3 install PyGMLParser'. Exiting now.", "e")
    sys.exit(1)
"""

            
########### command line parser ###########

## create the parser
cl_parser = argparse.ArgumentParser(description="TODO describe what script does",
                                    fromfile_prefix_chars="@")

## add arguments

# add mutually exclusive group for silent / verbose
loudness = cl_parser.add_mutually_exclusive_group()
loudness.add_argument('-s',
                       '--silent',
                       action='store_true',
                       help='only print results to stdout, e.g., no status messages and meta information')

loudness.add_argument('-v',
                       '--verbose',
                       action='store_true',
                       help='print more to stdout, e.g., status messages and meta information')

loudness.add_argument('-d',
                      '--debug',
                      action='store_true',
                      help='print everything including debug information')

# add command line arguments
cl_parser.add_argument('--version',
                       action='version',
                       version='%(prog)s ' + version)

cl_parser.add_argument('-i1',
                       '--inputfile1',
                       metavar = 'path',
                       help='the first gml file')

cl_parser.add_argument('-i2',
                       '--inputfile2',
                       metavar = 'path',
                       help='the second gml file')


cl_parser.add_argument('-o',
                       '--outputfile',
                       metavar = 'path',
                       default = '',
                       help = 'save results to file')

cl_parser.add_argument('-od',
                       '--outputdirectory',
                       metavar = 'path',
                       default = '',
                       help = 'specify a path to your output files. Otherwise the current folder is used.')

args = cl_parser.parse_args()


########### check arguments ###########

# assign log level
log_level = logging.WARNING
if (args.debug):
    log_level = logging.DEBUG
elif (args.verbose):
    log_level = logging.INFO
logging.getLogger().setLevel(log_level)


# check number of arguments
if len(sys.argv) < 3:
    print("[Error] Expected file path to first and second GML file. Exiting now.")
    exit()

# input file 1
if (args.inputfile1 != ""):
    if(os.access(args.inputfile1, os.R_OK)):
        fp1 = args.inputfile1
    else:
        logging.error("Specified input file 1 '%s' is not readable. Exiting now.", args.inputfile1)
        sys.exit(1)
        
# input file 2
if (args.inputfile2 != ""):
    if(os.access(args.inputfile2, os.R_OK)):
        fp2 = args.inputfile2
    else:
        logging.error("Specified input file 2 '%s' is not readable. Exiting now.", args.inputfile2)
        sys.exit(1)


# output file
if (args.outputfile != ""):
    if(check_file_writable(args.outputfile)):
        output_file = open(args.outputfile, "w")
    else:
        logging.error("Specified output file '%s' is not writable. Exiting now.", args.outputfile)
        sys.exit(1)
        
# output directory
if (args.outputdirectory != ""):
    if(os.access(args.outputdirectory, os.W_OK)):
        o_dir = args.outputdirectory
    else:
        logging.error("Specified output directory '%s' is not writable. Exiting now.", args.outputdirectory)
        sys.exit(1)
else:
    o_dir = '.'


########### vamos ###########

log("Version " + version, "i")

# TODO add your code here


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
fp1_stem = pathlib.Path(fp1).stem
fp2_stem = pathlib.Path(fp2).stem
with open(o_dir + "compared" + fp1_stem + fp2_stem + ".csv", "w") as f3:
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




# tidy up
if (args.outputfile != ""):
    output_file.close()

