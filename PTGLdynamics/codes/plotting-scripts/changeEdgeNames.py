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

cl_parser.add_argument('inputfile',
                       metavar = 'inputfile',
                       default = '',
                       help = 'get data as csv file input')

cl_parser.add_argument('key',
                       metavar = 'key',
                       default = '',
                       help = "Insert either 't' if you inserted a csv containing edges from the dataset with ubiquinone or 'f' otherwise")


# add mutually exclusive group for  configuration of header
cl_parser_group_input_format = cl_parser.add_mutually_exclusive_group()
cl_parser_group_input_format.add_argument('-e',
                                          '--eval-edge-weights-input',
                                          action='store_true',
                                          help = "Set when the input files are in the original eval_edge_weights.py output format.")

cl_parser_group_input_format.add_argument('-a',
                                          '--all-edges-input',
                                          action='store_true',
                                          help = "Set when the input files are in the 'all_edges' output format from eval_edge_weights.py")



cl_parser.add_argument('-o',
                       '--outputfile',
                       metavar = 'path',
                       default = '',
                       help = 'save results to file')


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

# input file
if(os.access(args.inputfile, os.R_OK) == True):
    csv = args.inputfile
else:
    logging.error("Specified file '%s' is not readable.", args.inputfile)
    sys.exit(1)

# key
if(args.key in ["t","f"]):
    key = args.key
else:
    logging.error("This key input '%s' is invalid. Exiting now.", args.key)
    sys.exit(1)


    
########### vamos ###########

log("Version " + version, "i")

no_U_dict = {"0":"Nqo1","1":"Nqo2","2":"Nqo3","3":"Nqo4","4":"Nqo5","5":"Nqo6","6":"Nqo9","7":"Nqo7","8":"Nqo10","9":"Nqo11","10":"Nqo12","11":"Nqo13","12":"Nqo14","13":"Nqo8","14":"Nqo15","15":"TTHA1528","16":"F"}
with_U_dict = {"0":"Nqo7","1":"Nqo6","2":"Nqo1","3":"Nqo2","4":"Nqo3","5":"Nqo4","6":"Nqo5","7":"Nqo9","8":"Nqo10","9":"Nqo11","10":"Nqo12","11":"Nqo13","12":"Nqo14","13":"Nqo8","14":"Nqo15","15":"TTHA1528","16":"F","17":"U"}

order = ["Nqo1","Nqo2","Nqo3","Nqo4","Nqo5","Nqo6","Nqo7","Nqo8","Nqo9","Nqo10","Nqo11","Nqo12","Nqo13","Nqo14","Nqo15","TTHA1528","F","U"]


log(len(csv),'i')

with open(csv, "r") as f:
    csv_lines = f.read().split("\n")


if(args.all_edges_input):
    edge_names = csv_lines[0] # sth like: {0 | 1},{0 | 14},{0 | 16},{0 | 2}
    
    edge_names = edge_names.replace('{','')
    edge_names = edge_names.replace('}','')
    edge_names = edge_names.replace('|','')
    
    edge_names = edge_names.split(',')
    new_line = ""
    for j in range(len(edge_names)):
        #new_line += "{"
        edge_names[j] = edge_names[j].split('  ')
        
        for k in range(len(edge_names[j])):
            if(key == "t"):
                edge_names[j][k] = with_U_dict[edge_names[j][k]]
            else:
                edge_names[j][k] = no_U_dict[edge_names[j][k]]
            #new_line += edge_names[j][k]
            
        if(order.index(edge_names[j][0]) > order.index(edge_names[j][1])):
            new_edge_name = "{"+edge_names[j][1] + " | " + edge_names[j][0] + "}"
        else:
            new_edge_name = "{"+edge_names[j][0] + " | " + edge_names[j][1] + "}"
        
        new_line += new_edge_name
        
        if(j < len(edge_names)-1):
            new_line += ","
        else:
            csv_lines[0] = new_line
            
    # write result to output file
    #csv_name = csv.replace(".csv", "")
    #with open(csv_name+'_new.csv', "w") as f:
    for elem in csv_lines:
        log(elem)

            
elif(args.eval_edge_weights_input):
    for j in range(1, len(csv_lines)):
        if(csv_lines[j] == ''):
            continue
        
        line = csv_lines[j].split(',')

        source = line[0]
        target = line[1]

        log(source , 'i')
        log(target , 'i')

        if(key == "t"):
            source = with_U_dict[source]
            target = with_U_dict[target]
        else:
            source = no_U_dict[source]
            target = no_U_dict[target]

        if(order.index(source) > order.index(target)):
            new_source = target
            target = source
            source = new_source

        # write result back to csv_lines
        new_line = source + ',' + target
        for k in range(2, len(line)):
            new_line += ',' + line[k]
        csv_lines[j] = new_line
    
    # write result to output file
    #csv_name = csv.replace(".csv", "")
    #with open(csv_name+'_new.csv', "w") as f:
    for elem in csv_lines:
        log(elem)
        
                       

# tidy up
if (args.outputfile != ""):
    output_file.close()

