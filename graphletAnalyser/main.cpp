/* 
 * File:   main.cpp
 * Author: tatiana
 * Edits:  ben
 * Created on April 18, 2013, 10:42 AM
 * Modified by Tim from July 2014
 * Modified by Ben from May 2015
 */

#include "global.h"
#include "Graph.h"
#include "GraphletCounts.h"
#include "time.h"
#include "ProteinGraphService.h"
#include "GMLptglProteinParser.h"
#include "GraphPTGLPrinter.h"
#include <time.h>


using namespace std;
using namespace boost;


/*----------------------------------------------------
 *            initialize global variables
 *---------------------------------------------------*/
int verbose = 0;
int silent = 0;
int timerun = 0;
int inputFilesAsList = 0;
int useDatabase = 0;
int saveGraphletDetails = 0;
int skipLabeledGraphlets = 0;
int aa_graph = 0;
int sse_graph = 0;
int complex_graph = 0;
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
        options["compute_labeled_graphlet_counts"] = "yes"; // adding a field for ptgl_counts and removing this field might be a good idea
	options["output_graph_matlab"] = "no";
	options["output_graph_stats"] = "yes";
	options["output_graph_stats_matlab"] = "no";
	options["output_counts_summary"] = "yes";
	options["output_counts_matlab"] = "no";
	options["output_counts_nova"] = "yes";
	options["output_counts_database"] = "no";
        options["graph_vertex_type_field"] = "sse_type";
        options["graph_vertex_type_alphabet"] = "HEL";
        options["output_counts_JSON"] = "yes";
        options["output_labeled_counts_JSON"] = "yes";
        
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
            {"timerun",          no_argument, &timerun,          1},
            {"verbose",          no_argument, &verbose,          1},
            {"brief",            no_argument, &verbose,          0},
            {"inputFilesAsList", no_argument, &inputFilesAsList, 1},
	    {"useDatabase",      no_argument, &useDatabase,      1},
            {"printGraphletDetails",      no_argument, &saveGraphletDetails,      1},
            {"skipLabeledGraphlets",      no_argument, &skipLabeledGraphlets,      1},
            {"aa_graph",         no_argument, &aa_graph,         1},
            {"complex_graph",    no_argument, &complex_graph,    1},
            {"sse_graph",        no_argument, &sse_graph,        1},
            
         
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
    
    if((! silent) || timerun) {
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
                        
                        if (input_path.substr(0,3).compare(".//") == 0) {
                            
                            input_path = input_path.substr(2, input_path.size());
                            
                            files.back().insert(0, input_path);
                            
                        } else {
                            files.back().insert(0, input_path);    
                        }
                        
                        
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
                    if (input_path.substr(0,3).compare(".//") == 0) {
                        
                            input_path = input_path.substr(2, input_path.size());
                        
                            files.back().insert(0, input_path);
                            
                        } else {
                            files.back().insert(0, input_path);    
                        }
                } else {
                    files.back().insert(0, "./");
                }
                optind++;
            }
            if(verbose) { cout << "\n"; }
        }        
    }
    
    
    
    //TODO:
    //
    //modify get_norm_counts in way that enables them to be returned in a readable and
    //understandable way
    
    /*----------------------------------------------------
     *           process the input files
     *---------------------------------------------------*/ 
    
    //look for malformed paths
    
    std::vector<std::string> files2 = std::vector<std::string>();
    
    // fix for absolute paths
    for (int i = 0; i < files.size(); i++) {
        
        if (files[i].substr(0,3).compare(".//") == 0) {
            
            files2.push_back(files[i].substr(2,files[i].size()));
        } else {
            files2.push_back(files[i]);
        }
        
    }    
    files = files2;
    
    
    
    if( ! silent) {
        cout << apptag << "Handling all " << files.size() << " input files.\n";
    }
    for (int i = 0; i < files.size(); i++) {

        if((! silent) || timerun) {
            cout << apptag << "  Starting to process file #" << (i + 1) << " of " << files.size() << " at " << currentDateTime() << ": '" << files[i] << "'.\n";
        }
        
        time_t start_file_time = time(NULL);
        
        if(filesize(files[i].c_str()) <= 0) {
            cerr << apptag << "    Skipping empty input file '" << files[i] << "', invalid GML file.\n";
            continue;
        }
        
        
        // set graph type according to command line arguments
        if (sse_graph) {
            options["graph_vertex_type_field"] = "sse_type";
            options["graph_vertex_type_alphabet"] = "HEL";
            options["graphtype"] = "sse_graph";
            
                    
        } else if (aa_graph) {
            options["graph_vertex_type_field"] = "chem_prop3";
            options["graph_vertex_type_alphabet"] = "hpa?";
            options["graphtype"] = "aa_graph";
            
        } else if (complex_graph) {
            options["graph_vertex_type_field"] = "sse_type";
            options["graph_vertex_type_alphabet"] = "HEL";
            options["graphtype"] = "complex_graph";
        }
        
        
        
        
        //choose label to look for from cfg file or default settings
        std::string vertex_label = options["graph_vertex_type_field"];
        
        
        // TODO: finish computation of length 3 labels
        // TODO: enable fetching of alphabet from cfg file
        // TODO: change main to reflect above changes
        
        // construct protein graph by parsing gml file
        GMLptglProteinParser Parser(files[i], vertex_label);
        Graph graph;
        graph = Parser.getGraph();
        ProteinGraphService service;
        service = ProteinGraphService(graph);
       
        
        
        
        GraphPTGLPrinter printer;
        printer = GraphPTGLPrinter();
        
        
        
        
        
        if(service.getNumVertices() <= 0) {
            if( ! silent) {
                cout << apptag << "    Skipping graph #" << i << ", it has no vertices.\n";
            }
            continue;
        }
        
        if(verbose) {
            cout << apptag << "Printing graph: " << printer.printGraphString(graph) << "\n"; 
        }
        
        int n = num_vertices(graph);
        int m = num_edges(graph);
        // save graph in different formats
		if(options["output_graph_matlab"] == "yes") {
			printer.saveAsMatlabVariable(graph);
		}
        
        // save graph statistics
		if(options["output_graph_stats"] == "yes") {
                    std::vector<int> degDist = service.computeDegreeDist();
	            printer.saveGraphStatistics(degDist, n,m);
		}
		if(options["output_graph_stats_matlab"] == "yes") {
                    std::vector<int> degDist = service.computeDegreeDist();
	            printer.saveGraphStatisticsAsMatlabVariable(degDist, n,m);
		}
        
        // optionally print graph info
        if (verbose) { printer.printGraphInfo(graph); }
        
        // count graphlets and save these counts
        GraphletCounts gc(graph);
        
        bool withLabeled = true;
        if(options["compute_labeled_graphlet_counts"] != "yes") {
            withLabeled = false;
        }
        
        // setting up vectors for storing data
        
        std::vector<float> norm_labeled_counts = std::vector<float>();
        vector<vector<float>> norm_counts = vector<vector<float>>();
        std::vector<std::vector<int>> abs_counts = std::vector<std::vector<int>>();
        
        std::vector<int> counts2;
        std::vector<int> counts3;
        std::vector<int> counts4;
        std::vector<int> counts5;
        
        
        // set up variables for measuring time
        time_t timer;
        time_t other_timer;
        
        
        timer = time(NULL);
        
        int seconds;
        
        
        if (!withLabeled) {
            
            std::vector<std::string> evec1 = std::vector<std::string>();
            std::vector<std::vector<std::string>> evec = std::vector<std::vector<std::string>>();
        
        
            counts2 = gc.count_connected_2_graphlets("", evec1);
            counts3 = gc.count_connected_3_graphlets("",evec);
            counts4 = gc.count_connected_4_graphlets("",evec);
            counts5 = gc.count_connected_5_graphlets();

        

            
        } else {
            
           
                
                std::string alphabet = options["graph_vertex_type_alphabet"];
                std::vector<std::string> lab2vec = service.get_length_2_patterns(alphabet);
                std::vector<std::vector<std::string>> lab3vec = service.get_length_3_patterns(alphabet);
                std::vector<std::vector<std::string>> evec = std::vector<std::vector<std::string>>();
                
                counts2 = gc.count_connected_2_graphlets(vertex_label, lab2vec);
                counts3 = gc.count_connected_3_graphlets(vertex_label,lab3vec);
                counts4 = gc.count_connected_4_graphlets("",evec);
                counts5 = gc.count_connected_5_graphlets();
                
                norm_labeled_counts = gc.get_normalized_labeled_counts();
                

            
            
        }
        
        other_timer = time(NULL);
        
        seconds = difftime(other_timer, timer);
        
        
        
        abs_counts.push_back(counts2);
        abs_counts.push_back(counts3);
        abs_counts.push_back(counts4);
        abs_counts.push_back(counts5);
        
        int total = gc.get_total_counts();
        std::vector<float> ncounts2 = gc.normalize_counts(counts2,total);
        std::vector<float> ncounts3 = gc.normalize_counts(counts3,total);
        std::vector<float> ncounts4 = gc.normalize_counts(counts4,total);
        std::vector<float> ncounts5 = gc.normalize_counts(counts5,total);
        
        norm_counts.push_back(ncounts2);
        norm_counts.push_back(ncounts3);
        norm_counts.push_back(ncounts4);
        norm_counts.push_back(ncounts5);
        

	     
        if( ! silent) {
            cout << apptag << "  Saving results.\n";
        }
		
        if(options["output_counts_summary"] == "yes") {
            
            
                
            printer.saveNormalizedGraphletCountsSummary(graph[graph_bundle].label, norm_counts, norm_labeled_counts);
            
        }

        if(options["output_counts_database"] == "yes") {
            
            if(options["graphtype"] == "sse_graph") {
                
                std::cout << apptag << "Computing graphlets for " << service.getPdbid() << " sse graph of chain " << service.getChainID() << " from file '" << files[i] << "'." << std::endl;
                
                std::vector<std::string> id_vec = std::vector<std::string>();
                id_vec.push_back(service.getPdbid());
                id_vec.push_back(service.getChainID());
                id_vec.push_back(service.get_label());
                std::string graphtype = service.getGraphTypeString();
                id_vec.push_back(graphtype);
                int graphtype_int = service.getGraphTypeInt(graphtype);
            
            
                int db_res = printer.savePGCountsToDatabasePGXX(graphtype_int, id_vec, norm_counts, norm_labeled_counts, seconds);
            }
            else if(options["graphtype"] == "aa_graph") {
                
                std::cout << apptag << "Computing graphlets for " << service.getPdbid() << " AA graph from file '" << files[i] << "'." << std::endl;
                
                std::string pdb_id = service.getPdbid();
                std::string label = "";
                if (withLabeled) {
                    label = "chem_prop3";
                }
                
                int db_res = printer.saveAACountsToDatabasePGXX(pdb_id,label,norm_counts,norm_labeled_counts, seconds);
                
            }
            else if(options["graphtype"] == "complex_graph" || options["graphtype"] == "complex_albe" || options["graphtype"] == "complex_albelig") {
                
                std::cout << apptag << "Computing graphlets for " << service.getPdbid() << " complex graph from file '" << files[i] << "'." << std::endl;
                
                std::string pdb_id = service.getPdbid();
                std::string label = "";
                
                int db_res = printer.saveCGCountsToDatabasePGXX(pdb_id,label,norm_counts,norm_labeled_counts,seconds);
            }
            else {
                std:cerr << apptag << "Cannot save graphlet counts to DB for file '" << files[i] << "', graph type unknown." << std::endl;
            }
            
        }

        if(options["output_counts_matlab"] == "yes") {
            printer.save_normalized_counts_as_matlab_variable(norm_counts, norm_labeled_counts);
        }

        if(options["output_counts_nova"] == "yes") {
                printer.saveCountsInNovaFormat(graph[graph_bundle].label, norm_counts, norm_labeled_counts);
        }
        
        if (options["output_counts_JSON"] == "yes") {
            printer.save_counts_as_json(graph[graph_bundle].properties["pdb_id"] + "_counts", n, m, abs_counts, norm_counts);
        }
        
        if (options["output_labeled_counts_JSON"] == "yes") {
            std::vector<std::string> lab_vec = service.get_2_sse_labels();
            std::vector<std::vector<std::string>> lab_3_vec = service.get_3_sse_labels();
            lab_vec.insert(lab_vec.end(), lab_3_vec[0].begin(), lab_3_vec[0].end());
            
            
            std::unordered_map<std::string, std::vector<int>> map = service.get_labeled_abs_counts("sse_type", lab_vec);
            printer.save_labeled_counts_as_json(graph[graph_bundle].label, n, m, map);
            
        }
        
        time_t end_file_time = time(NULL);
        double runtime_seconds = difftime(end_file_time, start_file_time);
        
        
        if(( ! silent) || timerun) {
            cout << apptag << "  Done with file #" << (i + 1) << " of " << files.size() << " at " << currentDateTime() << ": '" << files[i] << "' (|V|=" << n << ", |E|=" << m << "). Took " << runtime_seconds << " secs.\n";
        }
    }
    
    if(verbose) {
        cout.fill('+');
        cout << setw(43) << "  finished processing  " << setw(25) << " \n";
    }
    
    if((! silent) || timerun) {
        cout << apptag << "Done with all " << files.size() << " input files at " << currentDateTime() << ", exiting.\n";
    }
    return 0;
}


