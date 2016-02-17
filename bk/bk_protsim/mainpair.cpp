/*
 *   This file is part of:
 * 
 *   bk_protsim Copyright (C) 2015  Molecular Bioinformatics group, Goethe-University Frankfurt
 * 
 *   Written by Julian Gruber-Roet, maintained by Tim Schaefer.
 *   This program comes with ABSOLUTELY NO WARRANTY.
 *    This is free software, and you are welcome to redistribute it
 *   under certain conditions, see the LICENSE file for details.
 */

/* 
 * File:   main.cpp
 * Author: julian
 *         Tim Schaefer
 * Created on June 16, 2015, 2:56 PM
 */


#include <iostream>
#include <fstream>
#include "GMLptglProteinParser.h"
#include "ProductGraph.h"
#include "BronKerbosch.h"
#include "BK_Output.h"
#include "PG_Output.h"
#include "common.h"
#include <getopt.h>


int stringToTextFile(std::string fname, std::string contents) {
    std::ofstream file;
    
    // write to the file
    file.open(fname.c_str(), std::ios_base::trunc);    
    if (!file.is_open()) {
        std::cerr << "ERROR: could not open file '" << fname << "'.\n";
        return 0;
    } else {        
        file << contents;
        file.close();        
        return 1;
    }
}

bool hasEnding (std::string const &fullString, std::string const &ending) {
    if (fullString.length() >= ending.length()) {
        return (0 == fullString.compare (fullString.length() - ending.length(), ending.length(), ending));
    } else {
        return false;
    }
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
          std::cerr << apptag << "ERROR: Could not parse config file line, skipping line.\n";
          continue;
      }

      options[id] = val;
      //std::cout << "    Parsed config line: '" << id << "' => '" << val << "'\n";
      numLinesParsedOk++;
    }
    //std::cout << "  Parsed " << numLinesParsedOk << " settings from config file.\n";
}

/**
  * Writes default values to the global settings stored in the options map.
  * These defaults can later be overwritten by config file contents or command line arguments.
  */
void fill_settings_default() {
    options["output_path"] = "";
    options["silent"] = "no";
    options["verbose"] = "no";
    options["filter_permutations"] = "no";
    options["min_clique_output_size"] = "3";
    options["result_type"] = "all";
    options["write_result_text_files"] = "yes";
    options["outfile_prefix"] = "";
    options["check_home_for_config_file"] = "no";
}

void usage() {
    std::cout << apptag << "Usage:\n";
    std::cout << apptag << "bk_protsim" << " -f <graphFile1.gml> -s <graphFile2.gml> [<output_parameters>] \n";
    std::cout << apptag << "Output parameters:\n";    
    std::cout << apptag << " -r <result_type>      : The result type to output, valid values are 'largest', 'minsize' and 'all' (default).\n";
    std::cout << apptag << " -p <prefix>           : Set the prefix for output file names to <prefix>. Default is empty string.\n";
    std::cout << apptag << " --min-clique-size <s> : Set the minimum clique output size to <s>. Only used if --result-type is 'minsize'. Default is 3.\n";
    std::cout << apptag << " --filter-permutations : Filter permutations of vertex lists in output. Also writes filtered results to text files.\n";
    std::cout << apptag << " -o <path>             : Set the output path for files to <path>. Has to exist. Defaults to empty string, i.e., the current directory.\n";
    std::cout << apptag << " --verbose             : Additional output on stdout.\n";
    std::cout << apptag << "Example call: " << "bk_protsim" << " -f example1.gml -s example2.gml\n";
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


// global vars
std::string apptag = "[BK] ";
std::map<std::string, std::string> options;


/*
 * 
 */
int main2(int argc, char** argv) {
       
    //std::string apptag = "[BK] ";
    std::string startOutput = "";
    
    std::cout << apptag << "=== Bron Kerbosch-based graph similarity ===\n";
    std::cout << apptag << "= Searches maximum common substructures in a pair (G1, G2) of graphs.\n";
    std::cout << apptag << "= Constructs a compatibility graph GC from G1 and G2 and runs a variant of the Bron-Kerbosch algorithm on it.\n";
    std::cout << apptag << "= The cliques in GC correspond to common substructures (compatible vertex mappings) between G1 and G2.\n";
    std::cout << apptag << "= This is free software, and it comes without any warranty. See the LICENSE file for details.\n";
    std::cout << apptag << "= Written by Julian Gruber-Roet at MolBI group, 2015.\n";
    std::cout << apptag << "\n";
    
    
    fill_settings_default();
    
    // assume the config file is in the current directory by default
    std::string config_file_name = "bk_protsim.cfg";
    // test whether cfg file is in HOME, use it from there if it exists. otherwise, the code above applies and it is searched in the local dir
    bool cfg_parsed_from_home = false;
    
    if(options["check_home_for_config_file"] == "yes") {
    
        std::string home_path = getenv("HOME");
        if ( ! home_path.empty()) {
            //printf ("The user home is: '%s'.\n", home_path.c_str());
            std::string config_file_name_home = home_path.append("/.bk_protsim.cfg");
            std::ifstream configFileHome(config_file_name_home.c_str());
            if (configFileHome.is_open()) {
                startOutput.append(apptag).append("  Parsing config file from user home at '").append(config_file_name_home.c_str()).append("'.\n");
                parse(configFileHome);
                cfg_parsed_from_home = true;
            } else {
                std::cout << apptag << "  No config file found in user home at '" << config_file_name_home.c_str() <<  "', checking current dir.\n";
            }
        }
        else {
            std::cout << apptag << "  Could not determine user home directory to search for config file, $HOME is not set in the environment.\n";
        }
    }
    // now the default settings may be overwritten by stuff in the config file
    if( ! cfg_parsed_from_home) {
        std::ifstream configFile(config_file_name.c_str());
        if (configFile.is_open()) {
            std::cout << apptag << "  Parsing config file from '" << config_file_name.c_str() <<  "'.\n";
            parse(configFile);
        } else {		
                std::cout << apptag << "  Could not read config file '" << config_file_name << "' in current dir. Using internal default settings.\n";
        }
    }
    
    
    int opt;
    // These are set by getopt later
    int silent_flag;
    int verbose_flag;
    int filter_permutations_flag;
    int min_clique_output_size = atoi(options["min_clique_output_size"].c_str());
    std::string output_path;
    std::string first_graph_file;    
    std::string second_graph_file;
    std::string result_type;
    std::string outfile_prefix;

    while (1) {
        static struct option long_options[] = {
            /* These options set a flag. */
            {"silent",          no_argument, &silent_flag,          1},
            {"verbose",          no_argument, &verbose_flag,          1},
            {"filter-permutations",          no_argument, &filter_permutations_flag,          1},

         
            /* These options don't set a flag 
             * and will be distinguished by their indices. */
            {"help",             no_argument,        0, 'h'},
            {"first-graph",       required_argument,  0, 'f'},
            {"second-graph",       required_argument,  0, 's'},
            {"min-clique-size",       optional_argument,  0, 'm'},
            {"output-path",      optional_argument,  0, 'o'},
            {"result-type",      optional_argument,  0, 'r'},
            {"outfile-prefix",      optional_argument,  0, 'p'},
            {0, 0, 0, 0}
        };
        
        int option_index = 0;
        
        opt = getopt_long(argc, argv, "hf:s:m:o:r:p:", long_options, &option_index);  // the colon ':' behind a parameter tells getopt to look for an argument to it
        if (opt == -1) {
            break;  // end of options hit
        }
        
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
                usage();
                return 0;        
            case 'o':
                //cout << "option -o with the argument " << optarg << endl;
                output_path = optarg;
				if( ! hasEnding(output_path, "/")) {
					output_path.append("/");
				}
                options["output_path"] = output_path;
                break;   
            case 'f':
                //cout << "option -o with the argument " << optarg << endl;
                first_graph_file = optarg;
                options["first_graph_file"] = first_graph_file;
                break;   
            case 's':
                //cout << "option -o with the argument " << optarg << endl;
                second_graph_file = optarg;
                options["second_graph_file"] = second_graph_file;
                break;   
            case 'm':
                //cout << "option -o with the argument " << optarg << endl;                
                options["min_clique_output_size"] = optarg;
                //std::cout << apptag <<  "Setting clique size...\n";
                min_clique_output_size = atoi(optarg);
                break;   
            case 'r':
                //std::cout << "option -r \n";   
                result_type = optarg;
                //std::cout << apptag << "Setting result_type to " << result_type << ".\n";
                options["result_type"] = result_type;                
                break;   
            case 'p':
                //std::cout << "option -r \n";   
                outfile_prefix = optarg;
                //std::cout << apptag << "Setting result_type to " << result_type << ".\n";
                options["outfile_prefix"] = outfile_prefix;
                break;   
                
            default:
                std::cerr << apptag << "ERROR: Some command line options are invalid.\n";
                usage();
                return 1;                
        }
    }
    
    // check for invalid outfile prefixes
    std::size_t foundDots = options["outfile_prefix"].find("..");
    if (foundDots != std::string::npos) {
        std::cerr << apptag << "ERROR: Invalid outfile prefix, must not contain '..'. Set the output dir instead.\n";
        exit(1);
    }
    std::size_t foundSlash = options["outfile_prefix"].find("/");
    if (foundSlash != std::string::npos) {
        std::cerr << apptag << "ERROR: Invalid outfile prefix, must not contain '/'. Set the output dir instead.\n";
        exit(1);
    }
  
    if (verbose_flag) {
        options["verbose"] = "yes";
    }
    
    if (silent_flag) {
        options["silent"] = "yes";
        options["verbose"] = "no";
    }
    else {
        std::cout << startOutput;
    }
    
    if(! silent_flag) {
        std::cout << apptag << "  Starting at time " << currentDateTime() << std::endl;
    }
    
    if(filter_permutations_flag) {
        options["filter_permutations"] = "yes";
    }
    
    if (verbose_flag) {
        std::cout << apptag << "Settings: first_graph_file is '" << first_graph_file << "'.\n";
        std::cout << apptag << "Settings: second_graph_file is '" << second_graph_file << "'.\n";
        std::cout << apptag << "Settings: min_clique_output_size is '" << min_clique_output_size << "'.\n";
        std::cout << apptag << "Settings: filter_permutations_flag is '" << filter_permutations_flag << "'.\n";
        std::cout << apptag << "Settings: output_path is '" << options["output_path"] << "'.\n";
        std::cout << apptag << "Settings: result_type is '" << options["result_type"] << "'.\n";
    }
    
    
    if (argc < 5) {
        usage();
        return 1;
    }
    
    //main algorithms. All calculations are done here
    //parse input files
    Graph f = GMLptglProteinParser(first_graph_file).graph;
    Graph s = GMLptglProteinParser(second_graph_file).graph;
    
    std::cout << apptag << "Graph from file " << first_graph_file << " has " << f.vertex_set().size() << " vertices.\n";
    std::cout << apptag << "Graph from file " << second_graph_file << " has " << s.vertex_set().size() << " vertices.\n";
   
    
    //compute product graph
    ProductGraph pg(f,s);
    pg.run();
    //find cliques in the product graph
    BronKerbosch bk(pg.getProductGraph());
    bk.run_c();

    //parse output parameter, get list of found cliques
    std::list<std::list<unsigned long>> result_list;
    
    if(options["result_type"] == "largest") {
        result_list = BK_Output::get_result_largest(bk);
    }    
    else if(options["result_type"] == "minsize") {        
        result_list = BK_Output::get_result_larger_than(bk, atoi(options["min_clique_output_size"].c_str()));
    }
    else if(options["result_type"] == "all") {        
        result_list = BK_Output::get_result_all(bk);
    }
    else {
        std::cout << apptag << "Result type option invalid, assuming 'all'.\n";    
        result_list = BK_Output::get_result_all(bk);
    }
    
   
    // we may need to filter permutations here
    
    
    int filter_permutations = ( options["filter_permutations"] == "yes" ? 1 : 0 );
    int write_result_text_files = ( options["write_result_text_files"] == "yes" ? 1 : 0 );
       
    if(filter_permutations) {
        
        std::stringstream fresult;
        std::list<std::pair<std::list<int>, std::list<int>>> res;
        for (std::list<unsigned long>& clique : result_list) {
            std::list<int> ids_first = PG_Output::get_vertex_ids_first(pg, clique);
            std::list<int> ids_second = PG_Output::get_vertex_ids_second(pg, clique);
            std::pair<std::list<int>, std::list<int>> pair(ids_first, ids_second);
            res.push_back(pair);            
        }
        int num_before_filter = res.size();
        res.sort();
        res.unique();
        std::cout << apptag << "Found " << num_before_filter << " possible vertex mappings. Filtered permutations, " << res.size() << " elements remaining.\n";
        
        int idx = 0;
        for (std::pair<std::list<int>, std::list<int>> pair : res) {
            fresult << apptag << "{ ";
            fresult << " \"first\": " << PG_Output::int_list_to_JSON(pair.first) << ", ";
            fresult << " \"second\": " << PG_Output::int_list_to_JSON(pair.second) << " ";
            fresult << "} \n";
            
            if(write_result_text_files) {
                std::stringstream ss1;            
                ss1 << options["output_path"] << options["outfile_prefix"] << "results_" << idx << "_first.txt";
                std::string firstMappingsFileName = ss1.str();

                std::stringstream ss2;            
                ss2 << options["output_path"] << options["outfile_prefix"] << "results_" << idx << "_second.txt";
                std::string secondMappingsFileName = ss2.str();

                stringToTextFile(firstMappingsFileName, PG_Output::int_list_to_plcc_vertex_mapping_string(pair.first, "A"));
                stringToTextFile(secondMappingsFileName, PG_Output::int_list_to_plcc_vertex_mapping_string(pair.second, "B"));
                
                std::cout << apptag << "Wrote result mapping pair # " << idx << " to files '" << firstMappingsFileName << "' and '" << secondMappingsFileName <<  "'.\n";
            }
            
            idx++;
        }
        
        std::cout << fresult.str();
        
    }
    else {
        //format the output
        std::stringstream result;
        for (std::list<unsigned long>& clique : result_list) {
            /*
            result << "{\n";
            result << "\t\"first\":\n" << PG_Output::get_JSON_vertex_ids_first(pg, clique) << ",\n";
            result << "\t\"second\":\n" << PG_Output::get_JSON_vertex_ids_second(pg, clique) << "\n";
            result << "}\n";
             */
            result << "{ ";
            result << " \"first\": " << PG_Output::get_JSON_vertex_ids_first(pg, clique) << ", ";
            result << " \"second\": " << PG_Output::get_JSON_vertex_ids_second(pg, clique) << " ";
            result << "} \n";
        }//end format loop

        //output
        std::cout << result.str()<< "\n";
    }
}//end main
