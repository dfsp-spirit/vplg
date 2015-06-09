/* 
 * File:   GraphPTGLPrinter.cpp
 * Author: ben
 *
 * Created on May 13, 2015, 11:46 AM
 */

#include "GraphPTGLPrinter.h"

/* default constructor */
GraphPTGLPrinter::GraphPTGLPrinter() {
    Graph g_tmp;
    GraphService service_tmp;
    g = g_tmp;
    service = service_tmp;
};


GraphPTGLPrinter::GraphPTGLPrinter(const Graph& graph) {
  g = graph;
  service = GraphService(g);
};

/*
 * prints vertices and their sse types to a string
 * CAUTION: only applicable to ptgl protein graphs */
std::string GraphPTGLPrinter::printVertices() {
    VertexIterator vi, vi_end, next; // create vertex iterator objects
    tie(vi, vi_end) = vertices(g); // that belong to the graph
    stringstream sstream; // to be saved in a string
    
    sstream << "Iterate over the vertices and print their properties:\n";
    for (next = vi; vi != vi_end; vi = next) { // iterate over the vertices
        ++next;
        // and print them with their sse type
        sstream << "  vertex " << setw(2) << *vi << " has  sse_type = " << g[*vi].properties["sse_type"] << endl;
    }
    sstream << endl;
    
    return sstream.str();
};

/*
 * print edges with their spatial (protein related) relationships to a string
 * CAUTION: only applicable to ptgl protein graphs */
std::string GraphPTGLPrinter::printEdges() {
    bool formatted = true;
    EdgeIterator ei, ei_end;
    stringstream sstream;
    
    
    sstream << "Iterate over the edges and print their properties:\n";
    if (formatted) {
        for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) { // iterate over edges
            sstream << "  edge ("
                    << setw(2) << g[*ei].source
                    << ","
                    << setw(2) << g[*ei].target
                    << ") is "
                    << g[*ei].properties["spatial"] << endl; // add their spatial relationships
        }       
    } else {
        for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) {
            sstream << "  edge number " << *ei
                    << " has: source = " << g[*ei].source
                    << ", target = " << g[*ei].target
                    << ", spatial = " << g[*ei].properties["spatial"] << endl;
        }
    }
    sstream << endl;
    
    return sstream.str();    
};

/* Print all vertices, then all edges, and then then all adjacent vertices for
 * each vertex
 * CAUTION: only applicable to ptgl protein graphs */
void GraphPTGLPrinter::printGraphInfo() {
        stringstream sstream;

    sstream << "[GRAPH INFO]\n"
            << printVertices()
            << printEdges() 
            << printAdjacentAll();
    
    cout << sstream.str();

};

/* Get the vertices with their sse types, aa residues, and the number of residues,
 * and the edges with their protein-related spatial relationships
 */
string GraphPTGLPrinter::printGraphString() {
    // define iterators
    VertexIterator vi, vi_end, next;
    EdgeIterator ei, ei_end;
    tie(vi, vi_end) = vertices(g);
    
    // set up the string stream
    stringstream sstream;
    
    // add vertices with their info
    sstream << "Vertices:\n";
    for (next = vi; vi != vi_end; vi = next) {
        ++next;
        sstream << "  Vertex " << setw(2) << *vi << ": id=" << g[*vi].id << ", label=" << g[*vi].label << ", sse_type=" << g[*vi].properties["sse_type"] << ", num_residues=" << g[*vi].properties["num_residues"] << ", aa_sequence=" << g[*vi].properties["aa_sequence"] << "." << endl;
    }
    sstream << endl;
    
    // add edges with their info
    sstream << "Edges:\n";
    for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) {
        sstream << "  edge ("
                << setw(2) << g[*ei].source
                << ","
                << setw(2) << g[*ei].target
                << ") is "
                << g[*ei].properties["spatial"] << endl;
    }
    
    return sstream.str();
};