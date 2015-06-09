///* 
// * File:   ProteinGraph.cpp
// * Author: tatiana
// * 
// * Created on May 22, 2013, 2:42 PM
// */
//
//#include "Graph.h"
//
//using namespace std;
//using namespace boost;


///** Returns the PDB id string parsed from the GML, e.g., "7tim". */
//string Graph::getPdbid() {
//    string s = g[graph_bundle].pdb_id;
//    return s;
//}
//
///**
//  *  Returns the graph type code which is computed from the graph type string (which was parsed from GML). 
//  *  The codes are:
//  * 1 = 'alpha'
//  * 2 = 'beta'
//  * 3 = 'albe'
//  * 4 = 'alphalig'
//  * 5 = 'betalig'
//  * 6 = 'albelig'
//  * 7 = 'aa_graph' 
//  */

//
///** Returns the PDB chain id string parsed from the GML, e.g., "A". */
//string Graph::getChainid() {
//    string s = g[graph_bundle].chain_id;
//    return s;
//}
//
///** Returns the graph type string parsed from the GML, e.g., "albe". */
//string Graph::getGraphTypeString() {
//    string s = g[graph_bundle].graph_type;
//    return s;
//}
//


///* 
// *  access vertices via vertex iterator
// */
////string Graph::printVertices() {
////    c
////}
//
///* 
// *  iterate over all edges and print their properties
// */
//
//
///* 
// *  for all vertices iterate over adjacent vertices

//
//
///*
// *  compute node degree distribution
// */
//vector<int> Graph::computeDegreeDist(){
//    proteinGraph g = getGraph();
//    int degree;
//    vector<int> degDist (num_vertices(getGraph()));
//    
//    VertexIterator vi, vi_end, next;
//    tie(vi, vi_end) = vertices(g);
//    
//    for (next = vi; vi != vi_end; vi = next) {
//        ++next;
//        degree = out_degree(*vi, g);
//        degDist[degree]++;
//    }
//    return degDist;
//}
//