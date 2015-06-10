/* 
 * File:   main.cpp
 * Author: tatiana
 *
 * Created on April 18, 2013, 10:42 AM
 * Modified by Tim from July 2014
 * Modified by Ben from May 2015
 */

#include "global.h"
#include "Graph.h"
#include "GraphletCounts.h"
#include "time.h"
#include "GraphService.h"
#include "GMLptglProteinParser.h"
#include "GraphPTGLPrinter.h"


using namespace std;
using namespace boost;


/*----------------------------------------------------
 *            initialize global variables
 *---------------------------------------------------*/
int verbose = 0;
int silent = 0;
int inputFilesAsList = 0;
int useDatabase = 0;
int saveGraphletDetails = 0;
int skipLabeledGraphlets = 0;
string input_path = "./";
string input_files_list = "";
string output_path = "./";
string logs_path = "./";
string apptag = "[GA] ";
std::map<std::string, std::string> options;


/**
 * Determine file size in bytes.
 * @param filename the file
 * @return the size in bytes
 */
std::ifstream::pos_type filesize(const char* filename) {
    std::ifstream in(filename, std::ifstream::ate | std::ifstream::binary);
    return in.tellg(); 
}

/**
  * Parses a config file in 'key = value' line format. All lines starting with '#' are considered comments and skipped.
  * The resulting settings are stored as strings in the global map<string, string> options.
  */
void parse(std::ifstream & cfgfile)
{
    std::string id, eq, val;
    
    int numComments = 0;
    int numLinesParsedOk = 0;    

    while(cfgfile >> id >> eq >> val)
    {
      if (id[0] == '#') {          
          numComments++;
          continue;  // skip comments
      }
      
      if (eq != "=") { 
          //throw std::runtime_error("Parse error");
          cerr << apptag << "ERROR: Could not parse config file line, skipping line.\n";
          continue;
      }

      options[id] = val;
      //cout << "    Parsed config line: '" << id << "' => '" << val << "'\n";
      numLinesParsedOk++;
    }
    //cout << "  Parsed " << numLinesParsedOk << " settings from config file.\n";
}

const std::string currentDateTime() {
    time_t     now = time(0);
    struct tm  tstruct;
    char       buf[80];
    tstruct = *localtime(&now);
    // Visit http://en.cppreference.com/w/cpp/chrono/c/strftime
    // for more information about date/time format
    strftime(buf, sizeof(buf), "%Y-%m-%d %X", &tstruct);

    return buf;
}

/**
  * Writes default values to the global settings stored in the options map.
  * These defaults can later be overwritten by config file contents or command line arguments.
  */
void fill_settings_default() {
	options["dbhost"] = "127.0.0.1";
	options["dbusername"] = "vplg";
	options["dbname"] = "vplg";
	options["dbpassword"] = "vplg";
	options["dbtimeout"] = "10";
	options["dbport"] = "5432";
        options["compute_labeled_graphlet_counts"] = "yes";
	options["output_graph_matlab"] = "no";
	options["output_graph_stats"] = "yes";
	options["output_graph_stats_matlab"] = "no";
	options["output_counts_summary"] = "yes";
	options["output_counts_matlab"] = "no";
	options["output_counts_nova"] = "yes";
	options["output_counts_database"] = "no";
}

/*********************************************************
 ***                     MAIN                          ***
 *********************************************************/
int main(int argc, char** argv) {
    
    // Save the messages we are about to print to a string. Only print it after we read settings and know that silent mode is not active.
    std::string startOutput = "";

    
    if( ! silent) {
        startOutput.append(greeting_message);
    }
               
    /*----------------------------------------------------
     *          parse command line arguments
     *---------------------------------------------------*/
    int opt;
    int option_index;
    vector<string> files;
    ifstream inputFilesList;
    string line;

    // if there are neither options nor arguments
    if (argc < 2) {
        cout << usage_message;
        return 1;
    }

    /* --------------------- read config ------------------ */
    // load the internal default settings
    fill_settings_default();
        
    // assume the config file is in the current directory by default
    std::string config_file_name = "graphletanalyser.cfg";
    
    // test whether cfg file is in HOME, use it from there if it exists. otherwise, the code above applies and it is searched in the local dir
    bool cfg_parsed_from_home = false;
    std::string home_path = getenv("HOME");
    //char* cfgHomePath;
    //cfgHomePath = getenv("HOME");
    //if (cfgHomePath != NULL) {
    //    printf ("The user home is: '%s'.\n", cfgHomePath);
    //}
    if ( ! home_path.empty()) {
        //printf ("The user home is: '%s'.\n", home_path.c_str());
        std::string config_file_name_home = home_path.append("/.graphletanalyser.cfg");
        ifstream configFileHome(config_file_name_home.c_str());
        if (configFileHome.is_open()) {
            startOutput.append(apptag).append("  Parsing config file from user home at '").append(config_file_name_home.c_str()).append("'.\n");
            parse(configFileHome);
            cfg_parsed_from_home = true;
        } else {
            cout << apptag << "  No config file found in user home at '" << config_file_name_home.c_str() <<  "', checking current dir.\n";
        }
    }
    else {
        cout << apptag << "  Could not determine user home directory to search for config file, $HOME is not set in the environment.\n";
    }
    
    // now the default settings may be overwritten by stuff in the config file
    if( ! cfg_parsed_from_home) {
        ifstream configFile(config_file_name.c_str());
        if (configFile.is_open()) {
            cout << apptag << "  Parsing config file from '" << config_file_name.c_str() <<  "'.\n";
            parse(configFile);
        } else {		
                cout << apptag << "WARNING: Could not read config file '" << config_file_name << "' in current dir or '." << config_file_name << "' in user home. Using internal default settings.\n";
        }
    }
    // now the settings from the config file may be overwritten on the command line
	
	
    // parse options and print their values
    while (1) {
        static struct option long_options[] = {
            /* These options set a flag. */
            {"silent",          no_argument, &silent,          1},
            {"verbose",          no_argument, &verbose,          1},
            {"brief",            no_argument, &verbose,          0},
            {"inputFilesAsList", no_argument, &inputFilesAsList, 1},
			{"useDatabase",      no_argument, &useDatabase,      1},
            {"printGraphletDetails",      no_argument, &saveGraphletDetails,      1},
            {"skipLabeledGraphlets",      no_argument, &skipLabeledGraphlets,      1},
         
            /* These options don't set a flag 
             * and will be distinguished by their indices. */
            {"help",             no_argument,        0, 'h'},
            {"input-path",       optional_argument,  0, 'i'},
            {"output-path",      optional_argument,  0, 'o'},
            {"logs-path",        optional_argument,  0, 'l'},
            {0, 0, 0, 0}
        };
        
        opt = getopt_long(argc, argv, "hi:o:l:", long_options, &option_index);
        if (opt == -1)
            break;
        
        switch (opt) {
            case 0:
                /* If this option sets a flag, do nothing else now. */
                if (long_options[option_index].flag != 0)
                    break;
                //cout << "option" << long_options[option_index].name;
                if (optarg)
                    //cout << " with arg " << optarg;
                //cout << endl;
                break;         
            case 'h':
                cout << usage_message;
                return 0;
            case 'i':
                //cout << "option -i with the argument " << optarg << endl;
                input_path = optarg;
                input_path.append("/");
                break;
            case 'o':
                //cout << "option -o with the argument " << optarg << endl;
                output_path = optarg;
                output_path.append("/");
                break;
            case 'l':
                //cout << "option -l with the argument " << optarg << endl;
                logs_path = optarg;
                logs_path.append("/");
                break;
            default:
                cerr << apptag << "ERROR: Some command line options are invalid.\n" << usage_message;
                return 1;                
        }
    }
    
    // copy some of the old flags to the new settings stored in the 'options' map
    if (useDatabase) {
        options["output_counts_database"] = "yes";
    }

    if(skipLabeledGraphlets) {
        options["compute_labeled_graphlet_counts"] = "no";
    }
    
    if (silent) { 
        verbose = 0; 
        //cout << apptag << "  GraphletAnalyser silent mode active, will print only errors from now on. Start time is " << currentDateTime() << ". Bye.\n";
    }
    else {
        cout << startOutput;
    }
    
    if(! silent) {
        std::cout << apptag << "  Starting at time " << currentDateTime() << std::endl;
    }
    
    if (verbose) { cout << apptag << "verbose flag is set\n"; }
    
    if(verbose) {
        if (inputFilesAsList) { cout << apptag << "inputFilesAsList flag is set\n"; }
        if (useDatabase) {
			cout << apptag << "useDatabase flag is set\n"; 
		}
    }

    // parse arguments and print their values
    if (optind >= argc) {
        cout << apptag << "ERROR: expected arguments after options.\n" << usage_message;
        return 1;       
    } else {
        
        // read input files from a list saved in a file
        if (inputFilesAsList) {
            if ((argc - optind) > 1) {
                cout << apptag << "ERROR: input files should be stored in a single file.\n" << usage_message;
                return 1;
            }           
            input_files_list = argv[optind];
            cout << apptag << "Input files will be read form: " << input_files_list << "\n";

            inputFilesList.open(input_files_list.c_str());
            if (!inputFilesList.is_open()) {
                cout << apptag << "ERROR: Unable to open input files list!\n";
                return 1;
            } else {
                while (inputFilesList.good()) {
                    getline(inputFilesList, line);
                    if ((line == "") || (isspace(line.c_str()[0]))) continue;
                    files.push_back(line);
                    if (input_path != "") {
                        files.back().insert(0, input_path);
                    } else {
                        files.back().insert(0, "./");
                    }
                }
            }
            inputFilesList.close();
            
            if(verbose) {
                cout << apptag << "Number of files to process: " << files.size() << "\n";
            }
            
        // read input files explicitly from arguments
        } else {
            if(verbose) { cout << apptag << "Arguments: "; }
            while (optind < argc) {
                if(verbose) { cout << argv[optind] << " "; }
                files.push_back(argv[optind]);
                if (input_path != "") {
                    files.back().insert(0, input_path);
                } else {
                    files.back().insert(0, "./");
                }
                optind++;
            }
            if(verbose) { cout << "\n"; }
        }        
    }
    
    
    
    /*----------------------------------------------------
     *           process the input files
     *---------------------------------------------------*/    
    if( ! silent) {
        cout << apptag << "Handling all " << files.size() << " input files.\n";
    }
    for (int i = 0; i < files.size(); i++) {

        if( ! silent) {
            cout << apptag << "  Starting to process file #" << (i + 1) << " of " << files.size() << ": '" << files[i] << "'.\n";
        }
        
        if(filesize(files[i].c_str()) <= 0) {
            cerr << apptag << "    Skipping empty input file '" << files[i] << "', invalid GML file.\n";
            continue;
        }
        
        // construct protein graph by parsing gml file
        GMLptglProteinParser Parser(files[i]);
        Graph graph = Parser.getGraph();
        GraphService service(graph);
        GraphPTGLPrinter printer(service);
        
        if(service.getNumVertices() <= 0) {
            if( ! silent) {
                cout << apptag << "    Skipping graph #" << i << ", it has no vertices.\n";
            }
            continue;
        }
        
        if(verbose) {
            cout << apptag << "Printing graph: " << printer.printGraphString() << "\n"; 
        }
        
        // save graph in different formats
		if(options["output_graph_matlab"] == "yes") {
			printer.saveAsMatlabVariable(i);
		}
        
        // save graph statistics
		if(options["output_graph_stats"] == "yes") {
			printer.saveGraphStatistics();
		}
		if(options["output_graph_stats_matlab"] == "yes") {
			printer.saveGraphStatisticsAsMatlabVariable();
		}
        
        // optionally print graph info
        if (verbose) { printer.printGraphInfo(); }
        
        // count graphlets and save these counts
        GraphletCounts gc(graph);
        
        bool withLabeled = true;
        if(options["compute_labeled_graphlet_counts"] != "yes") {
            withLabeled = false;
        }
        
        vector<vector<float>> norm_counts = gc.get_normalized_counts();
        vector<float> norm_labeled_counts = vector<float>();
        
        if (withLabeled) {norm_labeled_counts = gc.get_labeled_norm_counts();}
	     
        if( ! silent) {
            cout << apptag << "  Saving results.\n";
        }
		
        if(options["output_counts_summary"] == "yes") {
            
            if (withLabeled) {
                vector<float> labeled_counts = gc.get_labeled_norm_counts();
                printer.saveNormalizedGraphletCountsSummary(norm_counts, labeled_counts);
            } else {
                vector<float> labeled_counts = vector<float>();
                printer.saveNormalizedGraphletCountsSummary(norm_counts, labeled_counts);
            }
        }

        if(options["output_counts_database"] == "yes") {
            int db_res = gc.saveCountsToDatabasePGXX(withLabeled);
        }

        if(options["output_counts_matlab"] == "yes") {
            printer.save_normalized_counts_as_matlab_variable(norm_counts, norm_labeled_counts);
        }

        if(options["output_counts_nova"] == "yes") {
                gc.saveCountsInNovaFormat(withLabeled);
        }

        if( ! silent) {
            cout << apptag << "  Done with file #" << (i + 1) << " of " << files.size() << ": '" << files[i] << "'.\n";
        }
    }
    
    if(verbose) {
        cout.fill('+');
        cout << setw(43) << "  finished processing  " << setw(25) << " \n";
    }
    
    if( ! silent) {
        cout << apptag << "Done with all " << files.size() << " input files, exiting.\n";
    }
    return 0;
}


