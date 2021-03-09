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
cl_parser = argparse.ArgumentParser(description="Post processing of old dssp files to adjust them to PTGLgraphComputation. As an input a folder that contains .dssp files is given. The modified .dssp files are written to an output folder.",
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

cl_parser.add_argument('-i',
                       '--input-dir',
                       metavar = 'input-directory',
                       default = '',
                       help = 'specify a path to your input files. Otherwise the current folder is used.')

cl_parser.add_argument('-p',
                       '--output-dir',
                       metavar = 'input-directory',
                       default = '',
                       help = 'specify a path to your output files. Otherwise the current folder is used.')

cl_parser.add_argument('-o',
                       '--outputfile',
                       metavar = 'path',
                       default = '',
                       help = 'save output to this file')
                       

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

# input directory
input_dir = check_dir_args(args.input_dir)
    

# output directory
output_dir = check_dir_args(args.output_dir)


########### vamos ###########

output_dir = os.path.abspath(output_dir) + '/'
os.chdir(input_dir)
input_dir = os.getcwd() + '/'

list_input_dir = os.listdir(input_dir)
list_input_dir = sorted_nicely(list_input_dir)

for allfile in list_input_dir:

    if allfile.endswith(".dssp"):
        cnt = 0
        data = []
        with open(input_dir + allfile) as f:
            for line in f:
                data.append(line)
        
        os.chdir(output_dir)
        output = open(allfile, "w")
        
        for line in data:
            
            if ("  #  RESIDUE AA" == line[0:15]) and ("CHAIN AUTHCHAIN" not in line):
                chain = "           CHAIN AUTHCHAIN "
                new_line = line.rstrip('\n')
                #if (line != new_line + chain + '\n'):
                    #line = line.replace(line, new_line + chain + '\n')
                line = new_line + chain + '\n'
                #else:
                 #   line = line
                cnt +=1
            elif (line[11] != ' ') and (cnt > 0):
                chain_id = "                " + line[11] + "         " + line[11]
                new_line = line.rstrip('\n')
                if (line != new_line + chain_id + '\n'):
                    line = new_line + chain_id + '\n'
            else:
                line = line
            output.write(line)
        output.close()
        os.chdir(input_dir)
            
log('finish dssp file overwritting','i')


# tidy up
if (args.outputfile != ""):
    output_file.close()

