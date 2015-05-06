/* 
 * File:   Graph.h
 * Author: tatiana
 * Revision: ben
 * Created on May 22, 2013, 2:42 PM
 */

#ifndef GRAPH_H
#define    GRAPH_H

#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/graph_traits.hpp>
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



struct vertex_info_p {
    int id;
    std::string label;
    std::string comment;
    EdgeDescriptor first;
    EdgeDescriptor second;
}; 

typedef boost::adjacency_list<boost::listS, boost::vecS, boost::undirectedS,
                              vertex_info_p, edge_info, graph_info, boost::listS> Graph_p;
typedef boost::graph_traits<Graph_p>::vertex_descriptor VertexDescriptor_p;
typedef boost::graph_traits<Graph_p>::edge_descriptor EdgeDescriptor_p;
typedef boost::graph_traits<Graph_p>::vertex_iterator VertexIterator_p;
typedef boost::graph_traits<Graph_p>::edge_iterator EdgeIterator_p;
typedef boost::graph_traits<Graph_p>::adjacency_iterator AdjacencyIterator_p;



#endif    /* GRAPH_H */


