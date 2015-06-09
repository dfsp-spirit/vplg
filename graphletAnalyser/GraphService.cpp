/* File: GraphService.cpp
 * Author: ben
 * 
 * Created on April 29, 2015 3:49 PM
 */


#include "GraphService.h"
#include <unordered_map>
#include <utility>

using namespace std;
using namespace boost;

/* This Service Class is meant for graphs which were parsed from a GML file.
 * GML files are of the following form.
 * 
 * x    is any arbitrary integer
 * s    is a string
 * att  represents an arbitrary number of other attributes which are written in
 *      the same form as id, label, comment and directed
 * 
 * graph [
 * comment s
 * id x
 * label s
 * directed x (x=0 => undirected; x=1 => directed)
 * att
 * ]
 * 
 * node [
 * comment s
 * id x
 * label s
 * att
 * ]
 * 
 * For edges, the values for source and target refer to the id of the
 * corresponding nodes
 * 
 * edge [
 * source x
 * target x
 * comment s
 * label s
 * att
 * ]
 * 
 */

GraphService::GraphService() {
    Graph g_tmp;
    g = g_tmp;
};

GraphService::GraphService(const Graph graph) {
    g = graph;
};


Graph GraphService::getGraph() {
  return g;  
};

/* Return the keys i.e. the property names of a given graph
 * @param graph g
 * @return vector<string> properties */
vector<string> GraphService::getGraphProperties() {
    std::unordered_map<string, string> propertyMap = g[graph_bundle].properties;
    
    vector<string> keys;
    keys.reserve(propertyMap.size());

    for(auto kv : propertyMap) {
        keys.push_back(kv.first);
    } 
    return keys;   
};


    
    
/* Return the value of a given property as a string.
 * @param1 graph g - the graph
 * @param2 string prop - the property
 * @return string property */
string GraphService::getPropertyValue(string prop) {
    string propValue = g[graph_bundle].properties[prop];
    return propValue;
}; 
    

    
/* Return the number of vertices
 * @param Graph g
 * @return int number of edges */    
int GraphService::getNumVertices() {
    return num_vertices(g);
};

/* Return the number of edges
 * @param Graph g
 * @return int number of edges */
int GraphService::getNumEdges() {
    return num_edges(g);
};



/* Return a vector, containing the vertex indices for a given graph
 * @return vector<int> vertices */
vector<int> GraphService::getVertices() {


    vector<int> vertexVector;
    vertexVector.reserve(num_vertices(g));   
    typedef property_map<Graph,vertex_index_t>::type IndexMap;
    IndexMap index = get(vertex_index, g);
    pair<VertexIterator, VertexIterator> vertexPair;
    for (vertexPair = vertices(g); vertexPair.first != vertexPair.second; ++vertexPair.first)
        vertexVector.push_back(index[*vertexPair.first]);
    return vertexVector;
};


/* Return the a vector, containing the edges as integer pairs.
 * @return vector<pair<int, int>> edgeVector -- a vector containing the edges */
vector<pair<int, int>> GraphService::getEdges() {
    
    // create the vector and the pair
    vector<pair<int, int>> edgeVector;
    edgeVector.reserve(num_edges(g));
    pair<int, int> edgePair;
    
    // create a map, so the graph properties can be read
    typedef property_map<Graph, vertex_index_t>::type IndexMap;
    IndexMap index = get(vertex_index, g);
    
    // iterate over the edges in the map and save them to the vector
    EdgeIterator ei, eiEnd;
    for (tie(ei, eiEnd) = edges(g); ei != eiEnd; ++ ei) {
        edgePair.first = index[source(*ei, g)];
        edgePair.second = index[target(*ei, g)];
        edgeVector.push_back(edgePair);
    };
    return edgeVector;
    
};







/* Compute the node degree distribution
 * @return vector<int> degDist -- the node degree distribution */
vector<int> GraphService::computeDegreeDist() {
    
    // create the vector and a variable for the degree
    int degree;
    vector<int> degDist (num_vertices(getGraph()));
    
    // iterate over the vertices
    VertexIterator vi, vi_end, next;
    tie(vi, vi_end) = vertices(g);
    
    for (next = vi; vi != vi_end; vi = next) {
        ++next;
        degree = out_degree(*vi, g); // store their degree
        degDist[degree]++; // and put it into the vector
    }
    return degDist;
  
};

/* Returns the graph's label
 * @return string name -- the graph's name */
string GraphService::getName() {
    string name = g[graph_bundle].label;
    return name;
    
};