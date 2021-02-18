###############################
########### HOW TO ############
###### USE THIS TEMPLATE ######
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
        -> without a level to print results and if required obligatory prints
- command line option 'silent' should suppress those obligatory prints so that only results are printed
- pipelining
    - write the results with -o to an output file to use as next input or
    - use 'silent' mode (if obligatory prints exist) and pipe the output to stdout directly to next script
        -> all debug, info, warning etc. are printed to stderr
- imports from not built-in modules in the respective section with error handling and useful prints where to find the module and if possible how to install it
        
(Remove this HOW TO and have fun programming!)
"""


########### built-in imports ###########

import sys
import os
import argparse
import logging
import traceback
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
cl_parser = argparse.ArgumentParser(description="Post processing of old dssp files to adjust them to plcc. As an input a folder that contains .dssp files is given. The modified .dssp files are written to an output folder.",
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

cl_parser.add_argument('-id',
                       '--inputdirectory',
                       default = '',
                       help = 'specify a path to your input files. Otherwise the current folder is used.')

cl_parser.add_argument('-od',
                       '--outputdirectory',
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
if (args.inputdirectory != ""):
    if(os.path.isdir(args.inputdirectory)):
        i_dir = args.inputdirectory
    else:
        logging.error("Specified input directory '%s' is not readable. Exiting now.", args.inputdirectory)
        sys.exit(1)
else:
    #i_dir = os.getcwd() + '/'
    i_dir = os.getcwd()

# output directory
if (args.outputdirectory != ""):
    if(os.path.isdir(args.inputdirectory)):
        o_dir = args.outputdirectory
    else:
        logging.error("Specified output directory '%s' is not writable. Exiting now.", args.outputdirectory)
        sys.exit(1)
else:
    #o_dir = os.getcwd() + '/'
    o_dir = os.getcwd()


########### vamos ###########

# TODO add your code here
o_dir = os.path.abspath(o_dir) + '/'
os.chdir(i_dir)
i_dir = os.getcwd() + '/'

for allfile in os.listdir(i_dir):

    if allfile.endswith(".dssp"):
        cnt = 0
        data = []
        with open(i_dir + allfile) as f:
            for line in f:
                data.append(line)
        f.close()
        
        os.chdir(o_dir)
        output = open(allfile, "w")
        
        #for line in fileinput.input(i_dir + allfile, inplace=True):
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
                    #line = line.replace(line, new_line + chain_id + '\n')
                    line = new_line + chain_id + '\n'
            else:
                line = line
            output.write(line)
            #sys.stdout.write(line)
        output.close()
        os.chdir(i_dir)
            
log('finish dssp file overwritting','i')


# tidy up
if (args.outputfile != ""):
    output_file.close()

