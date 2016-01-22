/* 
 * File:   includes.h
 * Author: tatiana
 * Edits: Ben
 * Created on April 30, 2013, 2:55 PM
 */

#ifndef GLOBAL_H
#define	GLOBAL_H

/*
 * using the standard c++ libraries
 */
#include <cstdlib>
#include <iostream>
#include <exception>
#include <fstream>
#include <string>
#include <string.h>
#include <stdio.h>
#include <vector>
#include <sstream>
#include <iterator>
#include <map>
#include <vector>
#include <iomanip>
#include <time.h>
#include <getopt.h>
#include <math.h>

#include <stdexcept>
#include <map>


/********************** using the Boost Library, version 1.53.0 *************************************
 *                                                                                                  *     
 * NOTE: in order to use the boost_graph and boost_regex libraries in a project, do the following:  * 
 *       - install boost library in:                                                                *
 *              "/usr/local/"                                                                       *            
 *       - add required library files from "/usr/local/lib" to the project by editing:              *
 *              "filesFile -> Project Properties -> Build -> Linker -> Libraries -> Add Library.."  *
 ****************************************************************************************************/ 
#include <boost/config.hpp> 
#include <boost/version.hpp> 
#include <boost/static_assert.hpp>
#include <boost/regex.hpp>
#include <boost/graph/graph_utility.hpp>
#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/graph_traits.hpp>
#include <boost/graph/properties.hpp>
#include <boost/property_map/property_map.hpp>
#include <boost/graph/copy.hpp>


 /**********************************************************************************
  * NOTE: for running this program in the NetBeans environme  t:                    *
  *     - add required command line options to the project properties by editing:  *
  *              "filesFile -> Project Properties -> Run -> Run Command.."         *
  **********************************************************************************/ 

/*
 *  global variables
 */
extern int silent;
extern int verbose;
extern int timerun;
extern int saveGraphletDetails;
extern int withLabeledGraphlets;
extern std::string input_path;
extern std::string output_path;
extern std::string logs_path;
extern std::string apptag;
extern std::map<std::string, std::string> options;

/*
 *  constants
 */
const int dozen = 12;


/*
 *  messages
 */
const std::string greeting_message_long =
        "*------------------------------------------------------------------------------*\n"
        "*                            graphletAnalyzer                                  *\n"
        "*                                                                              *\n"
        "*   This tool is an extendable graphlet features extraction framework          *\n"
        "*   which currently allows:                                                    *\n"
        "*                                                                              *\n"
        "*   - to compute the relative and absolute frequencies of                      *\n"
        "*               - unlabeled graphlets and                                      *\n"
        "*               - some types of labeled graphlets                              *\n"
        "*     for input graphs in GML format;                                          *\n"
        "*                                                                              *\n"
        "*   - to store these counts in different formats (plain, Matlab, NOVA, JSON).  *\n"
        "*                                                                              *\n"
        "*------------------------------------------------------------------------------*\n";

const std::string greeting_message =
        "[GA] === graphletAnalyser -- compute graphlet counts in networks ===\n"
        "[GA] ===                  (c) MolBI group, 2014-2015.            ===\n"
        "[GA] === written by Tatiana Bakirova, Ben Haladik and Tim Schaefer ===\n";


const std::string usage_message = "\n"
        "Usage: ./graphletanalyser [OPTIONS] SOURCE \n\n"

        "SOURCE:\n"
        " - one or several input files with a protein graph in GML format  or\n"
        " - a file with input files names stored, in case when inputFilesAsList flag is set. \n\n"

        "OPTIONS:\n"
        " -h, --help            : Prints this usage message.\n"
        " -i, --input-path <p>  : Sets path to the directory with the input GML files. Default: the current directory.\n"
        " -o, --output-path <p> : Sets path to the output directory for counts. Default: the current directory.\n"
        " -l, --logs-path <p>   : Sets path to the log files directory. Default: the current directory.\n"
        " --brief               : Generates only one run log. Default: off.\n"
        " --verbose             : Generates logs also for gml file parsing and graphlet counting. Default: off.\n"
        " --timerun             : Show system time at start and end of execution, even with silent. Default: off.\n"
        " --inputFilesAsList    : Treat the input file as a list of files to handle, one file per line. Default: off.\n"
        " --printGraphletDetails: Print details on certain graphlets to stdout. Default: off.\n"
        " --useDatabase         : Write graphlet counts to a PostgreSQL database (set DB credentials in config file 'graphletanalyser.cfg'). Default: off.\n"
        " --skipLabeledGraphlets: Do not compute the counts of labeled graphlets. Default: off.\n"
        " --aa_graph            : Compute graphlets for an amino acid graph and use the corresponding tables.\n"
        " --sse_graph            : Compute graphlets for a ptgl graph and use the corresponding tables.\n"
        " --complex_graph            : Compute graphlets for a ptgl complexgraph and use the corresponding tables.\n";
				       

#endif	/* GLOBAL_H */

