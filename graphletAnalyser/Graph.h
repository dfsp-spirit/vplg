/* 
 * File:   Graph.h
 * Author: tatiana
 * Revision: ben
 * Created on May 22, 2013, 2:42 PM
 */

#ifndef GRAPH_H
#define	GRAPH_H

#include "global.h"
#include <unordered_map>


// The following structs store data on the graph itself and its elements i.e.
// vertices and edges

struct graph_info {     
    int id;
    int directed;
    std::string label;
    std::string commment;   
    std::unordered_map<std::string, std::string> properties; // used to store an arbitrary number of properties
};

struct vertex_info {
    int id;
    std::string label;
    std::string comment;
    std::unordered_map<std::string, std::string> properties; 
};

struct edge_info {
    int source;   
    int target;
    std::string label;
    std::string comment;
    std::unordered_map<std::string, std::string> properties;
};

// Renaming of BGL contents for better readability

typedef boost::adjacency_list<boost::vecS, boost::vecS, boost::undirectedS,
                              vertex_info, edge_info, graph_info, boost::listS> Graph;
typedef boost::graph_traits<Graph>::vertex_descriptor VertexDescriptor;
typedef boost::graph_traits<Graph>::edge_descriptor EdgeDescriptor;
typedef boost::graph_traits<Graph>::vertex_iterator VertexIterator;
typedef boost::graph_traits<Graph>::edge_iterator EdgeIterator;
typedef boost::graph_traits<Graph>::adjacency_iterator AdjacencyIterator;




#endif	/* GRAPH_H */


/*

#ifndef PROTEINGRAPH_H
#define	PROTEINGRAPH_H

#include "global.h"


 *  NOTE: commented fields in structures represent
 *        some example extentions for the ProteinGraph datatype 
 *        which would be reasonable for the GML files of the PTGL graphs

EdgeIterator
struct graph_info {       // EXAMPLE values from 1pnt-A-albelig.gml:
    //int id;             // id       1
    std::string label;    // label   "VPLG Protein Graph 1pnt-A-albelig[12,12]"
    //string comment;     // comment "some comments"
    //bool directed;      // directed 0
    //bool isplanar;      // isplanar 1   
    std::string pdb_id;              // pdb_id        "1pnt"
    std::string chain_id;              // chain_id      "A"
    std::string graph_type;          // graph_type    "albelig"
    int is_protein_graph;          // is_protein_graph 1
};

struct vertex_info {
    int id;                       // id             0
    std::string label;               // label         "0-E"
    int num_in_chain;           // num_in_chain   1
    std::string sse_type;                // sse_type      "E"
    int num_residues;           // num_residues   7
    std::string pdb_res_start;       // pdb_res_start "A-6- "
    std::string pdb_res_end;         // pdb_res_end   "A-12- "
    int dssp_res_start;         // dssp_res_start 6
    int dssp_res_end;           // dssp_res_end   12
    std::string pdb_residues_full;         // pdb_residues_full "A-5- ,A-6- ,A-7- ,A-8- ,A-9- ,A-10- "
    std::string aa_sequence;         // aa_sequence   "KSVLFVC"    
};

struct edge_info {
    int source;         // source   0
    int target;         // target   3
    std::string label;       // label   "p"
    std::string spatial;       // spatial "p"
};

typedef boost::adjacency_list<boost::vecS, boost::vecS, boost::undirectedS,
                              vertex_info, edge_info, graph_info, boost::listS> proteinGraph;
typedef boost::graph_traits<proteinGraph>::vertex_descriptor VertexDescriptor;
typedef boost::graph_traits<proteinGraph>::edge_descriptor EdgeDescriptor;
typedef boost::graph_traits<proteinGraph>::vertex_iterator VertexIterator;
typedef boost::graph_traits<proteinGraph>::edge_iterator EdgeIterator;
typedef boost::graph_traits<proteinGraph>::adjacency_iterator AdjacencyIterator;


class ProteinGraph {
    
private:
    // attributes
    proteinGraph g;
    
    // private methods
    proteinGraph parse_gml(const std::string&);

public:
    // constructors
    ProteinGraph();    
    ProteinGraph(const proteinGraph&);
    ProteinGraph(const std::string&);
    
    // public methods
    proteinGraph getGraph();
    std::string getName();
    std::string getPdbid();
    std::string getChainid();
    std::string getGraphTypeString();
    int getGraphTypeInt();
    std::string getNameWithSize();
    std::string printVertices();
    std::string printEdges();
    std::string printAdjacent(const int&);
    std::string printAdjacentAll();
    std::string getGraphString();
    std::string parse_value_string(const std::string&);
    bool isElementClosingLine(const std::string& line);
    int getNumVertices();
    int getNumEdges();
    void printGraphInfo();
    void saveGraphStatistics();
    void saveGraphStatisticsAsMatlabVariable();
    std::vector<int> computeDegreeDist();
    void saveInSimpleFormat();
    void saveAsMatlabVariable(int&);
};

#endif	/* PROTEINGRAPH_H */


