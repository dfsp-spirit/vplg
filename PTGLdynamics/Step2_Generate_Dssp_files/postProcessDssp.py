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

import fileinput
            
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

cl_parser.add_argument('-p',
                       '--programmfolder',
                       metavar = 'path',
                       default = '',
                       help = "specify a path to this programm's files. Otherwise the current folder is used.")

cl_parser.add_argument('-i',
                       '--inputfolder',
                       metavar = 'path',
                       default = '',
                       help = 'specify a path to your input files. Otherwise the current folder is used.')

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

if (args.inputfolder != ""):
    if(os.access(args.inputfolder, os.R_OK)):
        i_folder = args.inputfolder
    else:
        logging.error("Specified input folder '%s' is not readable. Exiting now.", args.inputfolder)
        sys.exit(1)
else:
    i_folder = '.'

if (args.programmfolder != ""):
    if(os.access(args.programmfolder, os.R_OK)):
        p_folder = args.programmfolder
    else:
        logging.error("Specified programm folder '%s' is not readable. Exiting now.", args.programmfolder)
        sys.exit(1)
else:
    p_folder = '.'

# check argument(s)
"""
if len(sys.argv) < 3:
    log("Missing argument(s).", "e")
    sys.exit()
"""

########### vamos ###########

# TODO add your code here

tableColumns = "  #  RESIDUE AA STRUCTURE BP1 BP2  ACC     N-H-->O    O-->H-N    N-H-->O    O-->H-N    TCO  KAPPA ALPHA  PHI   PSI    X-CA   Y-CA   Z-CA "
tableCnt = 0

for allfile in os.listdir(i_folder):
    cnt = 0
    
    if allfile.endswith(".dssp"):
        for line in fileinput.input(allfile, inplace = True):
            log('counter: '+ str(cnt), 'd')            
            if ("  #  RESIDUE AA" == line[0:15]):
                chain = "           CHAIN AUTHCHAIN "
                #line = line + chain
                line = line.rstrip('\n')
                line = line.replace(line, line + chain + '\n')
                #tableCnt = cnt
                cnt +=1
            #elif (line[11] != ' ') and (tableCnt > 0) and (tableCnt != cnt):
            elif (line[11] != ' ') and (cnt > 0):
                log(line[11], 'd')
                chain_id = "                " + line[11] + "         " + line[11]
                #line = line + chain_id
                line = line.rstrip('\n')
                line = line.replace(line, line + chain_id + '\n')
                #cnt +=1
            else:
                line = line
                #cnt +=1
            sys.stdout.write(line)
            
log('finish dssp file overwritting','i')


# tidy up
if (args.outputfile != ""):
    output_file.close()

