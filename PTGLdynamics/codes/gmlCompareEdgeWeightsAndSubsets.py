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
            
            

def check_input_files(inputfile):
    """Returns the argument if the file is readable. Otherwise raises an error and exits."""
    if(inputfile != ""):
        if(os.access(inputfile, os.R_OK)):
            return inputfile
        else:
            logging.error("Specified input file '%s' is not readable. Exiting now.", inputfile)
            sys.exit(1)
    else:
        logging.error("This program needs two input files to work. Exiting now.")



def create_gml_dict(f):
    """ Reads gml file f in and returns a dictionary of edges and list of nodes."""
    
    # read file in
    log("Reading file in", 'i')
    with open(f, "r") as f:
        #gml_lines = f.read().split("\n")
        gml_lines = []
        for line in f:
            gml_lines.append(line)


    # create dict
    log("Creating dictionary",'i')
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


    log(allnodes,'i')
    
    return alledges, allnodes


########### configure logger ###########

logging.basicConfig(format = "[%(levelname)s] %(message)s")
            
            
########### not built-in imports ###########
           
########### command line parser ###########

## create the parser
cl_parser = argparse.ArgumentParser(description="Compare two gml files and return a file in csv format giving the edge weights and their differences as well as additional edges.",
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

cl_parser.add_argument('-a',
                       '--inputfile1',
                       metavar = 'path',
                       help='the first gml file')

cl_parser.add_argument('-b',
                       '--inputfile2',
                       metavar = 'path',
                       help='the second gml file')


cl_parser.add_argument('-o',
                       '--outputfile',
                       metavar = 'path',
                       default = '',
                       help = 'save results to file')

cl_parser.add_argument('-p',
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

# input file 1
fp1 = check_input_files(args.inputfile1)
        
# input file 2
fp2 = check_input_files(args.inputfile2)


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
        output_dir = args.outputdirectory
    else:
        logging.error("Specified output directory '%s' is not writable. Exiting now.", args.outputdirectory)
        sys.exit(1)
else:
    output_dir = '.'


########### vamos ###########

log("Version " + version, "i")


alledges, allnodes = create_gml_dict(fp1)

alledges2, allnodes2 = create_gml_dict(fp2)

#node degree
def countDegree( nodeName, edges):
    nodeDegree = []
    for i in nodeName:
        counter = 0
        for key in edges:
            if i in key:
                counter += 1
        nodeDegree.append((i, counter))
    return nodeDegree

log(countDegree( allnodes, alledges),'i')
log(countDegree( allnodes2, alledges2),'i')

keys = []
graph1Val = [] # all edge weights of graph 1
graph2Val = [] # all edge weights of graph 1
diff = [] # all differences of edge weights
deviation =[]# not common edges
fp1_stem = pathlib.Path(fp1).stem
fp2_stem = pathlib.Path(fp2).stem
with open(output_dir + "compared" + fp1_stem + fp2_stem + ".csv", "w") as f3:
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


log("Finished gml comparison.", 'i')

# tidy up
if (args.outputfile != ""):
    output_file.close()

