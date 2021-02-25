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
import shutil
import re
import time


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
            
def new_directory(name):
    """Creates a new directory 'name' if it does not exist yet. Returns the absolute path of that directory"""
    if not os.path.isdir(name):
        os.makedirs(name)
    return os.path.abspath(name)

def get_working_dir(new_dir):
    """Changes the working directory to the given path and returns the new working directory"""
    os.chdir(new_dir)
    return os.getcwd() + '/'

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
"""Arguments with hyphen like '--input-dir' are called with an underscore within the programm: args.input_dir
However, in the command line call the hyphen is used: --input-dir <path> """

## create the parser
cl_parser = argparse.ArgumentParser(description="This is a pipeline that reads several snapshots in pdb-format. " +
                                    "The pipeline runs PTGLgraphComputation on each of them and compares the differences in between their Complex Graph outputs. "+
                                    "If not specified otherwise the results are written in the current folder in csv-format. " +
                                    "Use: 'python3 <path>/ptglDynamics.py <arguments>' to run the programm.",
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

cl_parser.add_argument('-id',
                       '--input-dir',
                       metavar = 'input-directory',
                       default = '',
                       help = 'specify a path to your input files. Otherwise the current folder is used.')

cl_parser.add_argument('-od',
                       '--output-dir',
                       metavar = 'output-directory',
                       default = '',
                       help = 'specify a path to your output files. Otherwise the current folder is used.')

cl_parser.add_argument('-c',
                       '--compoundfile',
                       metavar = 'compoundfile',
                       default = '',
                       help = 'to integrate a header in pdb files specify the path of your compound file.')

cl_parser.add_argument('-p',
                       '--programms',
                       metavar = 'programms',
                       nargs = "*",
                       type = str,
                       default = ['toLegacyPDB.py', 'dsspcmbi', 'postProcessDssp.py', 'PTGLgraphComputation', 'gmlCompareEdgeWeightsAndSubsets.py'],
                       help = 'to execute only the specified scripts.')

cl_parser.add_argument('-u',
                       '--sub-dir-structure',
                       action='store_true',
                       help='display the results in sub directories in the output directory.')

cl_parser.add_argument('-a',
                       '--PTGLgraphComputation-args',
                       metavar = 'PTGLgraphComputation-args',
                       type = str,
                       default = '',
                       help = 'a string with the PTGLgraphComputation arguments you want to use and its values to execute PTGLgraphComputation in different ways using PTGLgraphComputations command line arguments. Insert arguments like this: -a="<arguments and their inputs>" ')

cl_parser.add_argument('-b',
                       '--toLegacyPDB-args',
                       metavar = 'toLegacyPDB-arguments',
                       type = str,
                       default = '',
                       help = 'a string with the arguments for toLegacyPDB you want to use and its values to execute the script in different ways using your command line arguments. Insert arguments like this: -b="<arguments and their inputs>" ')

cl_parser.add_argument('-e',
                       '--dsspcmbi-args',
                       metavar = 'dsspcmbi-arguments',
                       type = str,
                       default = '',
                       help = 'a string with the arguments for dsspcmbi you want to use and its values to execute the script in different ways using your command line arguments. Insert arguments like this: -e="<arguments and their inputs>" ')

cl_parser.add_argument('-f',
                       '--postProcessDssp-args',
                       metavar = 'postProcessDssp-arguments',
                       type = str,
                       default = '',
                       help = 'a string with the arguments for postProcessDssp you want to use and its values to execute the script in different ways using your command line arguments. Insert arguments like this: -f="<arguments and their inputs>" ')

cl_parser.add_argument('-g',
                       '--gmlCompareEdgeWeightsAndSubsets-args',
                       metavar = 'gmlCompareEdgeWeightsAndSubsets-args',
                       type = str,
                       default = '',
                       help = 'a string with the arguments for gmlCompareEdgeWeightsAndSubsets you want to use and its values to execute the script in different ways using your command line arguments. Insert arguments like this: -g="<arguments and their inputs>" ')

cl_parser.add_argument('-j',
                       '--different-dssp-folders',
                       action = 'store_true',
                       help = 'saves the dssp from dsspcmbi and the post processed dssp in different folders if the sub directory structure is activated.')

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
if (args.input_dir != ""):
    if(os.path.isdir(args.input_dir)):
        i_dir = args.input_dir
    else:
        logging.error("Specified input directory '%s' is not readable. Exiting now.", args.inputdirectory)
        sys.exit(1)
else:
    i_dir = os.getcwd()
    

# output directory
if (args.output_dir != ""):
    if(os.path.isdir(args.output_dir)):
        o_dir = args.output_dir
    else:
        logging.error("Specified output directory '%s' is not writable. Exiting now.", args.outputdirectory)
        sys.exit(1)
else:
    o_dir = os.getcwd()
    
    
# output directory
if (args.compoundfile != ""):
    if(os.access(args.compoundfile, os.R_OK)):
        compound = os.path.abspath(args.compoundfile)
        cmd_compound = ' -c ' + compound
    else:
        logging.error("Specified compound file '%s' is not readable. Continuing without compound file.", args.compoundfile)
        compound = ''
        cmd_compound = ''
else:
    compound = ''
    cmd_compound = ''

# list of programms
programm_list = []
if (args.programms != []):
    for programm in args.programms:
        if programm in ['toLegacyPDB.py', 'dsspcmbi', 'postProcessDssp.py', 'PTGLgraphComputation', 'gmlCompareEdgeWeightsAndSubsets.py']:
            programm_list.append(programm)
        else:
            logging.error("Specified programm '%s' is not part of the ptglDynamics pipeline. Continuing without it.", programm)
else:
    programm_list = ['toLegacyPDB.py', 'dsspcmbi', 'postProcessDssp.py', 'PTGLgraphComputation', 'gmlCompareEdgeWeightsAndSubsets.py']
    
# PTGLgraphComputation arguments
if (args.PTGLgraphComputation_args != ''):
    add_PTGLgraphComputation_args = args.PTGLgraphComputation_args
else:
    add_PTGLgraphComputation_args = ''

# toLegacyPDB arguments
if (args.toLegacyPDB_args != ''):
    add_toLegacyPDB_args = args.toLegacyPDB_args
else:
    add_toLegacyPDB_args = ''

# postProcessDssp arguments
if (args.postProcessDssp_args != ''):
    add_postProcessDssp_args = args.postProcessDssp_args
else:
    add_postProcessDssp_args = ''
    
# gmlCompareEdgeWeightsAndSubsets arguments
if (args.gmlCompareEdgeWeightsAndSubsets_args != ''):
    add_gml_comparison_args = args.gmlCompareEdgeWeightsAndSubsets_args
else:
    add_gml_comparison_args = ''

# gmlCompareEdgeWeightsAndSubsets arguments
if (args.dsspcmbi_args != ''):
    add_dsspcmbi_args = args.dsspcmbi_args
else:
    add_dsspcmbi_args = ''
    
# different dssp folders
if (args.different_dssp_folders):
    dir_names = {'toLegacyPDB.py':'legacyPDB', 'dsspcmbi':'oldDssp', 'postProcessDssp.py':'newDssp', 'PTGLgraphComputation':'PTGLgraphComputation', 'gmlCompareEdgeWeightsAndSubsets.py': 'gml'}
else:
    dir_names = {'toLegacyPDB.py':'legacyPDB', 'dsspcmbi':'dssp', 'postProcessDssp.py':'dssp', 'PTGLgraphComputation':'PTGLgraphComputation', 'gmlCompareEdgeWeightsAndSubsets.py': 'gml'}

########### vamos ###########

log("Version " + version, "i")

# TODO add your code here



############### Declaration of variables ###############
_start_time = time.time()

ptglDynamics_path = os.path.dirname(__file__)
PTGLgraphComputation_path = os.path.dirname(ptglDynamics_path) + '/plcc/dist/plcc.jar'

cmd_start = 'python3 ' + ptglDynamics_path + '/codes/'

i_dir = os.path.abspath(i_dir) + '/'
o_dir = os.path.abspath(o_dir) + '/'

pdb_dir = i_dir
dssp_dir = i_dir
PTGLgraphComputation_dir = i_dir
gml_dir = i_dir

work_dir = get_working_dir(i_dir)
list_work_dir = []

log("work_dir: ", 'd')
log(work_dir, 'd')
log("programm list: ", 'd')
log(programm_list, 'd')


################## Go through the given Programms and execute them #####################
for elem in programm_list:
    log("elem: " + elem, 'd')
    
    # Get the output directory
    if (args.sub_dir_structure) and (dir_names[elem] != ''):
        os.chdir(o_dir)
        out_dir = new_directory(dir_names[elem]) + '/'
        os.chdir(work_dir)
    else:
        out_dir = o_dir
        
    #execute different scripts:
    if (elem == 'toLegacyPDB.py'):

        work_dir = get_working_dir(pdb_dir)
        exec_string = cmd_start + elem + ' ' + add_toLegacyPDB_args + ' -id ' + work_dir + ' -od ' + out_dir + cmd_compound
        log('exec_string ' + exec_string, 'd')
        os.chdir(out_dir)
        os.system(exec_string)
        pdb_dir = os.path.abspath(out_dir) + '/'
        
        log('toLegacyPDB computations are done.', 'i')
        
      
    elif (elem == 'dsspcmbi'):

        work_dir = get_working_dir(pdb_dir)
        
        list_work_dir = os.listdir(work_dir)
        list_work_dir = sorted_nicely(list_work_dir)
        
        for pdb in list_work_dir:
            if pdb.endswith(".pdb"):
                dssp = out_dir + pathlib.Path(pdb).stem + '.dssp'
                exec_string = ptglDynamics_path + '/codes/' + elem + ' ' + work_dir + pdb + ' ' + dssp  + ' ' + add_dsspcmbi_args
                log('exec_string ' + exec_string, 'd')
                os.chdir(out_dir)
                os.system(exec_string)
                os.chdir(work_dir)
        dssp_dir = os.path.abspath(out_dir) + '/'
        
        log('dsspcmbi computations are done.', 'i')

    
    elif (elem == 'postProcessDssp.py'):

        work_dir = get_working_dir(dssp_dir)
        exec_string = cmd_start + elem + ' ' + add_postProcessDssp_args + ' -id ' + work_dir + ' -od ' + out_dir
        log('exec_string ' + exec_string, 'd')
        os.chdir(out_dir)
        os.system(exec_string)
        dssp_dir = os.path.abspath(out_dir) + '/'
        
        log('postProcessDssp computations are done.', 'i')

    
    elif (elem == 'PTGLgraphComputation'):

        work_dir = get_working_dir(pdb_dir)
        
        list_work_dir = os.listdir(work_dir)
        list_work_dir = sorted_nicely(list_work_dir)
        len_work_dir = len(list_work_dir)
        
        counter = 0;
        
        for pdb in list_work_dir:
            if (pdb.endswith(".pdb")):
                
                pdb_id = pathlib.Path(pdb).stem
                pdb_id_folder = ''
                if(args.sub_dir_structure):
                    os.chdir(out_dir)
                    pdb_id_folder = new_directory(pdb_id)
                    os.chdir(work_dir)
                else:
                    pdb_id_folder = out_dir

                dssp = dssp_dir + pathlib.Path(pdb).stem + '.dssp'

                PTGLgraphComputation = 'java -jar ' + PTGLgraphComputation_path + ' ' + pdb_id + ' ' + add_PTGLgraphComputation_args + ' -p ' + work_dir + pdb + ' -d ' + dssp + ' -o ' + pdb_id_folder

                log(PTGLgraphComputation,'d') 
                os.chdir(out_dir)
                os.system(PTGLgraphComputation)
                os.chdir(work_dir)
                
                counter = counter + 1
                if(counter % 5 == 0):
                    log(str(counter) + ' / ' + str(len_work_dir) + ' file computations with PTGLgraphComputation are done.', 'i')
        
        PTGLgraphComputation_dir = os.path.abspath(out_dir) + '/'
        
        
    elif (elem == 'gmlCompareEdgeWeightsAndSubsets.py'):

        work_dir = get_working_dir(PTGLgraphComputation_dir)
        log('PTGLgraphComputation_dir: ' + PTGLgraphComputation_dir,'d')
        prevGml = ''
        list_work_dir = os.listdir(work_dir)
        list_work_dir = sorted_nicely(list_work_dir)
        for entry in list_work_dir:
            log(entry, 'd')
            
            if (os.path.isdir(entry)):

                temp_work_dir = get_working_dir(entry)
                list_temp_work_dir = os.listdir(temp_work_dir)
                log(list_temp_work_dir,'d')
                
                list_temp_work_dir = sorted_nicely(list_temp_work_dir);
                for gml in list_temp_work_dir:
                    log('in dir: ' + gml, 'd')
                    if gml.endswith("complex_chains_albelig_CG.gml"):
                        log(out_dir,'d')
                        try:
                            shutil.copy(gml, out_dir + gml)
                        except shutil.SameFileError:
                            log("Source and destination represents the same file.", 'i')

                        if prevGml != '':
                            os.chdir(out_dir)
                            gmlComparison = cmd_start + elem +' ' + add_gml_comparison_args + ' -i1 ' + prevGml + ' -i2 ' + gml + ' -od ' + o_dir
                            log(gmlComparison,'d')
                            os.system(gmlComparison)
                            os.chdir(temp_work_dir)

                        prevGml = gml
                os.chdir('../')
            else:
                log('not in dir: ' + entry, 'd')
                if entry.endswith("complex_chains_albelig_CG.gml"):
                    try:
                        shutil.copy(entry, out_dir + entry)
                    except shutil.SameFileError:
                        log("Source and destination represents the same file.", 'i')


                    if prevGml != '':
                        os.chdir(out_dir)
                        gml_comparison = cmd_start + elem +' ' + add_gml_comparison_args + ' -i1 ' + prevGml + ' -i2 ' + entry + ' -od ' + o_dir
                        log(gmlComparison,'d')
                        os.system(gml_comparison)
                        os.chdir(work_dir)
                    prevGml = entry
           
        gml_dir = os.path.abspath(out_dir) + '/'
        
        log('gmlCompareEdgeWeightsAndSubsets computations are done.', 'i')


log("-- %s seconds ---"% (time.time()- _start_time), 'i')
log("All done, exiting ptglDynamics.", 'i')

# tidy up
if (args.outputfile != ""):
    output_file.close()

