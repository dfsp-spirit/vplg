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
import time
import fileinput


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
cl_parser = argparse.ArgumentParser(description="Overwrites a crocket pdb file to the official pdb-format.",
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

cl_parser.add_argument('-o',
                       '--outputfile',
                       metavar = 'path',
                       default = '',
                       help = 'save results to file')

cl_parser.add_argument('-c',
                       '--compndfile',
                       default = '',
                       help='The COMPND.txt file is used as a header for the pdb files.')

cl_parser.add_argument('-id',
                       '--inputdirectory',
                       default = '',
                       help = 'specify a path to your input directory. Otherwise the current folder is used.')

cl_parser.add_argument('-od',
                       '--outputdirectory',
                       default = '',
                       help = 'specify a path to your output files. Otherwise the current folder is used.')

args = cl_parser.parse_args()


########### check arguments ###########
'.'
# assign log level
log_level = logging.WARNING
if (args.debug):
    log_level = logging.DEBUG
elif (args.verbose):
    log_level = logging.INFO
logging.getLogger().setLevel(log_level)

# output file
if (args.outputfile != ""):
    if(check_file_writable(args.outputfile)):
        output_file = open(args.outputfile, "w")
    else:
        logging.error("Specified output file '%s' is not writable. Exiting now.", args.outputfile)
        sys.exit(1)

# COMPND.txt
if (args.compndfile != ""):
    if(os.access(args.compndfile, os.R_OK)):
        compnd = args.compndfile
    else:
        logging.error("Specified compound file '%s' is not readable. Continuing without compound file.", args.compndfile)
        compnd = ''
else:
    compnd = ''

# input folder
if (args.inputdirectory != ""):
    if(os.path.isdir(args.inputdirectory)):
        i_dir = args.inputdirectory
    else:
        logging.error("Specified input directory '%s' is does not exist. Exiting now.", args.inputdirectory)
        sys.exit(1)
else:
    i_dir = os.getcwd()

# output directory
if (args.outputdirectory != ""):
    if(os.path.isdir(args.outputdirectory)):
        o_dir = args.outputdirectory
    else:
        logging.error("Specified output directory '%s' does not exist. Exiting now.", args.outputdirectory)
        sys.exit(1)
else:
    o_dir = os.getcwd()


########### vamos ###########

log("Version " + version, "i")

# TODO add your code here

_start_time = time.time()
o_dir = os.path.abspath(o_dir) + '/'
os.chdir(i_dir)
i_dir = os.getcwd() + '/'


#reading COMPND.txt file which is the header of a  pdb file
if compnd != '':
    f = open(compnd, "r")
    header = f.read()
    header = header+'\n'

#amino acids (residues)in column 18-20 
aaCode = [ 'ALA', 'ARG', 'ASN', 'ASP', 'CYS', 'GLN', 'GLU', 'GLY', 'HIS', 'ILE', 'LEU', 'LYS', 'MET', 'PHE', 'PRO', 'SER', 'THR', 'TRP', 'TYR', 'VAL']
#list of ns for filename
fileNO = [1, 667, 1334, 2000]


log(__file__, 'i')

#get number of files in os.listdir(i_dir) to avoid going through the modified ones several times
number_files = len(os.listdir(i_dir))

for allfile in os.listdir(i_dir):
    if allfile.endswith(".pdb"):
        data = []
        with open(allfile) as f:
            for line in f:
                data.append(line)
        f.close()
        
        os.chdir(o_dir)
        output = open(allfile, "w")
        
        cnt = 0
        hetatms = ''
        currChainID = 1 # saves current atom ID for identifying chain brakes
        prevChainID = 1
        prevAtomID = 0
        prevLine = ''
        addNo = 0
        for line in data:
            #header
            if (cnt == 0) and (compnd != ''):
                line = line + str(header)
                #sys.stdout.write(line)
                output.write(line)
            
            if 'ATOM' == line[0:4]:
                if line[75:76] != ' ':
                    chainID = line[75:76]
                else:
                    chainID = line[21]
                #line = line.replace(line, line[:21]+chainID +line[22::])
                line = line[:21]+chainID +line[22::]
                if line[17:20] not in aaCode:
                    #line = line.replace(line, 'HETATM'+line[6:])
                    line = 'HETATM'+line[6:]
                    #hetatms=hetatms+str(line)
                if line[13:16] == 'OT1':
                    #line = line.replace(line, line[:13]+'OXT'+line[16::])
                    line = line[:13]+'OXT'+line[16::]
                elif line[13:16] == 'OT2':
                    #line = line.replace(line, line[:13]+'O  '+line[16::])
                    line = line[:13]+'O  '+line[16::]
                currChainID = line[21]
                prevTER = False
                if (currChainID != prevChainID) and (cnt != 1):
                    numberofSpaces = len(line[6:11])- len(str(prevAtomID+1))
                    addNo = addNo +1 #ATOM ID hochzaehlen
                    ter_line = 'TER   '+str(numberofSpaces*' ')+str(prevAtomID+1)+'      '+prevLine[17:26]
                    output.write(ter_line + '\n')
                    line = line[0:6]+str(numberofSpaces*' ')+str(prevAtomID+2)+line[11::]
                    addNo = addNo +1 #ATOM ID hochzaehlen
                    prevAtomID = prevAtomID +2
                else:
                    numberofSpaces = len(line[6:11])- len(str(prevAtomID+1))
                    line = line[0:6]+str(numberofSpaces*' ')+str(prevAtomID+1)+line[11::]
                    prevAtomID = prevAtomID + 1
                #sys.stdout.write(line)
                output.write(line)
                prevLine = line
                prevChainID = line[21]
            cnt += 1 
        output.close()
        os.chdir(i_dir)

log('finish pdb file overwritting', 'i')
log("-- %s seconds ---"% (time.time()- _start_time), 'i')


# tidy up
if (args.outputfile != ""):
    output_file.close()

 
