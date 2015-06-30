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
 * 
 * Author: ben
 * 
 * Created on May 8, 2015, 2:18 PM
 * 
 * 
 */

#include <unordered_map>
#include <utility>
#include "GMLptglProteinParser.h"
#include <iostream>
#include <fstream>
#include "common.h"

using namespace std;
using namespace boost;

int verbose = 0;
int silent = 1;
int inputFilesAsList = 0;
//int useDatabase = 0;
//int saveGraphletDetails = 0;
//int skipLabeledGraphlets = 0;
string input_path = "./";
string input_files_list = "";
string output_path = "/home/julian/develop/vplg-full-sf/trunk/bk/bk_protsim/testdata/";
string logs_path = "/home/julian/develop/vplg-full-sf/trunk/bk/bk_protsim/testdata/";
//string apptag = "[BK] ";
//std::map<std::string, std::string> options;

/*
 * Default constructor */
GMLptglProteinParser::GMLptglProteinParser() {
    
};

/*
 * Constructor for the Parser, creates graph Objects from GML files in the
 * format of the ptgl
 * @param string gmlFile -- a GML file in the ptgl format */
GMLptglProteinParser::GMLptglProteinParser(const string& gmlFile) {

    bool print = verbose;
    
    string name = gmlFile;        
    const string logFileName = logs_path + "parse_" + name + ".log";
    
   ofstream logFile;
    if (print) {
        logFile.open(logFileName.c_str());
        if (!logFile.is_open()) {
            cerr << apptag << "ERROR: ProteinGraph::parse_gml -- Could not open log file.\n";
        }
    }
    
    const int PART_GRAPH = 1;
    const int PART_VERTEX = 2;
    const int PART_EDGE = 3;
    
    string line;
    ifstream inputFile;
    string buffer;
    string tmp;
    vector<string> tokens;

    string key;
    string value;	    
    
    
    // declare graph with elements
    vertex_info vi;
    edge_info ei;
    VertexDescriptor u, v;
    int part = 0;

    bool graph_open = false;            // whether the graph section in the GML is currently open
    bool graph_sub_elem_open = false;   // whether an element section (vertex or edge) in the GML is currently open
    int lineNum = 0;
    
    if (print) { 
        logFile << "Parsing input GML file '" << gmlFile  << "' and constructing a protein graph.\n"; 
    }
    
    inputFile.open(gmlFile.c_str());
    if (! inputFile.is_open()) {
        logFile << "ERROR: ProteinGraph::parse_gml -- Unable to open GML graph input file!\n";
    } 
    else {
        while (inputFile.good()) {    
            lineNum++;
            getline(inputFile, line);
            stringstream sstream(line);
            while (sstream >> buffer) {
                tokens.push_back(buffer);
            }
            key = tokens[0];    // not really. Depending on the line, there are more than 2 parts, so take care.
            value = tokens[1];  // not really. Depending on the line, there are more than 2 parts, so take care.
            if (!key.empty() && !value.empty()) {
                if (key == "graph") {
                    graph_open = true;
                    graph_sub_elem_open = false;
                    part = PART_GRAPH;
                } else if (key == "node")  {
                    graph_sub_elem_open = true;
                    part = PART_VERTEX;                                                            
                } else if (key == "edge")  {
                    graph_sub_elem_open = true;
                    part = PART_EDGE;
                }                 
                    
                
                             
                //	cout << "key='" << key << "', value ='" << value << "'.\n";
                switch (part) {
                    case PART_GRAPH:						// --------------- graph props --------------
                        if (key == "label") {
                            graph[graph_bundle].properties["label"] = parse_value_string(line);                            
                            if (print) {
                                logFile << "  Graph property label " << graph[graph_bundle].label << " was added." << endl;
                            }                         
                        }
                        else if(key == "pdb_id") {
                            graph[graph_bundle].properties["pdb_id"] = parse_value_string(line);
                        }
                        else if(key == "chain_id") {
                            graph[graph_bundle].properties["chain_id"] = parse_value_string(line);
                        }
                        else if(key == "graph_type") {
                            graph[graph_bundle].properties["graph_type"] = parse_value_string(line);
                        }
                        else if(key == "is_protein_graph") {
                            graph[graph_bundle].properties["is_protein_graph"] = atoi(value.c_str());
                        }
                        else {
                            if(isElementClosingLine(line)) {    
                                if( ! graph_open) {
                                    cerr << "WARNING: Graph already closed but closing bracket found in line " << lineNum << ".\n"; 
                                }
                                if(print) {
                                    cout << "Closing graph in line #" << lineNum << ".\n";
                                }
                            }
                        }
                        break;
                    case PART_VERTEX:
			// --------------- vertex props --------------
                        if (key == "id") {
                            vi.id = atoi(value.c_str());
                        }
                        else if (key == "sse_type") {
                            vi.properties["sse_type"] = parse_value_string(line);
                            vi.label = vi.properties["sse_type"];
                        }
                        else if (key == "label") {
                            vi.properties["label"] = parse_value_string(line);
                        }
			else if (key == "num_in_chain") {
                            vi.properties["num_in_chain"] = atoi(value.c_str());
                        }
			else if (key == "num_residues") {
                            vi.properties["num_residues"] = atoi(value.c_str());
                        }
			else if (key == "dssp_res_start") {
                            vi.properties["dssp_res_start"] = atoi(value.c_str());
                        }
			else if (key == "dssp_res_end") {
                            vi.properties["dssp_res_end"] = atoi(value.c_str());
                        }
                        else if (key == "pdb_res_start") {
                            vi.properties["pdb_res_start"] = parse_value_string(line);
                        }
			else if (key == "pdb_res_end") {
                            vi.properties["pdb_res_end"] = parse_value_string(line);
                        }
			else if (key == "pdb_residues_full") {
                            vi.properties["pdb_residues_full"] = parse_value_string(line);
                        }
			else if (key == "aa_sequence") {
                            vi.properties["aa_sequence"] = parse_value_string(line);
                        }
                        else {                            
                            if(isElementClosingLine(line)) {
                                if(graph_sub_elem_open) {
                                    v = add_vertex(vi, graph);
                                    if (print) {
                                        logFile << "  " << vi.properties["sse_type"] << " node with id " << vi.properties["id"] << " was added" << endl;
                                    }
                                    graph_sub_elem_open = false;
                                    if(print) {
                                        cout << apptag << "Closing vertex " << vi.properties["label"] << " in line #" << lineNum << ".\n";
                                    }
                                }
                                else {
                                    if( ! graph_open) {
                                        cerr << apptag << "WARNING: Graph already closed but closing bracket found in line " << lineNum << ".\n"; 
                                    }
                                    graph_open = false;
                                    if(print) {
                                        cout << apptag << "Closing graph in line #" << lineNum << ".\n";
                                    }
                                }                                
                            }
                        }
						
                        break;
                    case PART_EDGE:
						// --------------- edge props --------------
                        if (key == "source") {
                            ei.source = atoi(value.c_str());
                        }
			else if (key == "label") {
                        }						
			else if (key == "target") {
                            ei.target = atoi(value.c_str());
                        } 
			else if (key == "spatial") {
                            ei.properties["spatial"] = parse_value_string(line);
                        }
                        else {                            
                            if(isElementClosingLine(line)) {
                                if(graph_sub_elem_open) {
                                    graph_sub_elem_open = false;
                                    v = vertex(ei.source, graph);
                                    u = vertex(ei.target, graph);
                                    add_edge(v, u, ei, graph);
                                    if (print) { 
                                        logFile << "  " << ei.properties["spatial"] << " edge (" << ei.source << ", " << ei.target << ") was added" << endl;
                                    }
                                    if(print) {
                                        cout << apptag << "Closing edge (" << ei.source << ", " << ei.target << ") in line #" << lineNum << ".\n";
                                    }
                                }
                                else {
                                    if( ! graph_open) {
                                        cerr << apptag << "WARNING: Graph already closed but closing bracket found in line " << lineNum << ".\n"; 
                                    }
                                    graph_open = false;
                                    if(print) {
                                        cout << apptag << "Closing graph in line #" << lineNum << ".\n";
                                    }
                                }                                
                            }
                        }
                        
                        break;
                    default:
                        cerr << apptag << "ERROR in the GML file at line #" << lineNum << ": key='" << key << "'." << endl;
                }
            } else {
                cout << apptag << "WARNING: GML parser: Line ignored, no key value pair: '" << line << "'.\n";
            }
            buffer = "";
            tokens.clear();
        }
        inputFile.close();
    }
    
    if( ! silent) {
        cout << apptag << "  Parsed input graph.\n"
             << apptag << "    The input gml file \""  << gmlFile  <<"\" was parsed and protein graph was constructed.\n"
             << apptag << "    This graph has " << num_vertices(graph) << " vertices and " << num_edges(graph) << " edges.\n"
             << apptag << "    The parser log file is \"" << logFileName << "\".\n";
    }

};

/*
 * Checks whether there is a closing bracket in the read line i.e. the end of a node
 * defintiont */
bool GMLptglProteinParser::isElementClosingLine(const string& line) {
    std::string tmp; // create a string
    tmp.assign(line); // copy string to temp
    tmp.erase(std::remove_if(tmp.begin(), tmp.end(), ::isspace), tmp.end()); // delete whitespace
    //cout << "Trimmed line is '" << tmp << "'.\n";
    if(tmp == "]") { // return true, if there is a closing bracket
        return true;
    }
    return false;
};


/*
 * parses the identifiers of attributes like a label or similar ones from the GML file  */
string GMLptglProteinParser::parse_value_string(const string& line) {
    string label = "";
	try {
		int pos = line.find_first_of("\""); // look for "\" symbol
		if(pos == string::npos) { // if it is not there, report broken file
			cout << apptag << "WARNING: GML broken: No \" found in line '" << line << "'.\n"; 
		}
		else { // if it it is found
			string value = line.substr(pos); // save the label or pdbID or other which should follow and end with the "\" symbol
			if(value.length() >= 3) { // if it contains something
				label = value.substr(1, value.length() - 2); // save it
			} else {
				if(value.length() == 2) { // if it doesn't contain something
					label = ""; // there is no label, so save that
				}
				else { // if there is no second "\" the file is broken
					cout << apptag << "WARNING: GML broken: Missing second \" in line '" << line << "'.\n"; 
				}
			}
		}
	} catch (std::exception& e) {							  
		cout << apptag << "Exception: " << e.what() << '\n';
	}
	return label;
}