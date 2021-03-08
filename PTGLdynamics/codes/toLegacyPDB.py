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
import re


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
            


def sorted_nicely( l ):
    """ Sorts the given iterable in the way that is expected.
    creates a list for each file consisting of the different int and string parts of the name
    afterwards the file list is sorted considering those changed names only
 
    Required arguments:
    l -- The iterable to be sorted.
    
 
    """
    convert = lambda text: int(text) if text.isdigit() else text
    alphanum_key = lambda key: [convert(c) for c in re.split('([0-9]+)', key)]
    return sorted(l, key = alphanum_key)


def check_dir_args(argument):
    """Checks directory arguments and returns its value if the directory exists or exits the program otherwise."""
    
    if( argument != ""):
        if(os.path.isdir(argument)):
            return argument
        else:
            logging.error("Specified directory '%s' does not exist. Exiting now.", argument)
            sys.exit(1)
    else:
        return os.getcwd()
    
    

########### configure logger ###########

logging.basicConfig(format = "[%(levelname)s] %(message)s")
            
            
########### not built-in imports ###########
            
########### command line parser ###########
"""Arguments with hyphen like '--input-dir' are called with an underscore within the programm: args.input_dir
However, in the command line call the hyphen is used: --input-dir <path> """

## create the parser
cl_parser = argparse.ArgumentParser(description="Overwrites a differing pdb file to legacy pdb-format. To be more specific: C-terminal oxygens are changed from OT1 to O and from OT2 to OXT. The chain identifier in column 22 was added. TER-lines were added to identify the ending of protein chains.",
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

cl_parser.add_argument('-i',
                       '--input-dir',
                       metavar = 'input-directory',
                       default = '',
                       help = 'specify a path to your input directory. Otherwise the current folder is used.')

cl_parser.add_argument('-p',
                       '--output-dir',
                       metavar = 'output-directory',
                       default = '',
                       help = 'specify a path to your output directory. Otherwise the current folder is used.')

args = cl_parser.parse_args()


########### check arguments ###########

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

# input directory
input_dir = check_dir_args(args.input_dir)
    

# output directory
output_dir = check_dir_args(args.output_dir)


########### vamos ###########

log("Version " + version, "i")

_start_time = time.time()
output_dir = os.path.abspath(output_dir) + '/'
os.chdir(input_dir)
input_dir = os.getcwd() + '/'


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

#get number of files in os.listdir(input_dir) to avoid going through the modified ones several times
number_files = len(os.listdir(input_dir))

list_input_dir = os.listdir(input_dir)
list_input_dir = sorted_nicely(list_input_dir)

for allfile in list_input_dir:
    if allfile.endswith(".pdb"):
        data = []
        with open(allfile) as f:
            for line in f:
                data.append(line)
        
        os.chdir(output_dir)
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
                output.write(line)
            
            if 'ATOM' == line[0:4]:
                if line[75:76] != ' ':
                    chainID = line[75:76]
                else:
                    chainID = line[21]

                line = line[:21]+chainID +line[22::]
                if line[17:20] not in aaCode:
                    line = 'HETATM'+line[6:]
                if line[13:16] == 'OT1':
                    line = line[:13]+'OXT'+line[16::]
                elif line[13:16] == 'OT2':
                    line = line[:13]+'O  '+line[16::]
                currChainID = line[21]
                prevTER = False
                if (currChainID != prevChainID) and (cnt != 1):
                    numberofSpaces = len(line[6:11])- len(str(prevAtomID+1))
                    addNo += 1 #ATOM ID hochzaehlen
                    ter_line = 'TER   '+str(numberofSpaces*' ')+str(prevAtomID+1)+'      '+prevLine[17:26]
                    output.write(ter_line + '\n')
                    line = line[0:6]+str(numberofSpaces*' ')+str(prevAtomID+2)+line[11::]
                    addNo += 1 #ATOM ID hochzaehlen
                    prevAtomID += 2
                else:
                    numberofSpaces = len(line[6:11])- len(str(prevAtomID+1))
                    line = line[0:6]+str(numberofSpaces*' ')+str(prevAtomID+1)+line[11::]
                    prevAtomID += 1

                output.write(line)
                prevLine = line
                prevChainID = line[21]
            cnt += 1 
        output.close()
        os.chdir(input_dir)

log('finish pdb file overwritting', 'i')
log("-- %s seconds ---"% (time.time()- _start_time), 'i')


# tidy up
if (args.outputfile != ""):
    output_file.close()

 
