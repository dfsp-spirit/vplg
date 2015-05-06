/* 
 * File:   ProductGraph.cpp
 * Author: julian
 *
 * Created on April 29, 2015, 1:32 PM
 */

#include "ProductGraph.h"
#include <tuple>

ProductGraph::ProductGraph() : fstGraph(0), secGraph(0), prodGraph(0) { }

ProductGraph::ProductGraph(const Graph& fstGraph, const Graph& secGraph) : fstGraph(fstGraph), secGraph(secGraph) {
    //upper bound for maximum number of vertices in the product graph is |E1| * |E2| 
    this->prodGraph = Graph_p(num_vertices(fstGraph)*num_vertices(secGraph)); 
    //save the id of the original graphs as the label of the product graph
    this->prodGraph[boost::graph_bundle].label = std::to_string(fstGraph[boost::graph_bundle].id)  + "$" +
                                                                               std::to_string(secGraph[boost::graph_bundle].id) 
    computePrdGraph();
    //todo fill other properties
}

ProductGraph::ProductGraph(const ProductGraph&) : fstGraph(0), secGraph(0), prodGraph(0) {
    std::cerr << "not implemented";
}

ProductGraph::~ProductGraph() {}

Graph_p ProductGraph::getProductGraph() {
    return prodGraph;
}

void ProductGraph::computePrdGraph() { 
    //the vertices of the product graph get computed by paring every edge of the first graph with every edge 
    // of the second. If the edges are compatible a new vertex is added to the product graph
    
    VertexIterator_p current, end;
    tie (current, end) = vertices(prodGraph); // dummy vertices in product graph
    
    //iterating through all edge pairs
    EdgeIterator eiFst, eiEndFst ;
    EdgeIterator eiSec, eiEndSec;
    for ( tie(eiFst, eiEndFst) = edges(fstGraph); eiFst != eiEndFst; ++eiFst) {
        VertexDescriptor v1Fst, v2Fst;
        v1Fst = source(*eiFst, fstGraph);
        v2Fst = target (*eiFst, fstGraph);
        for ( tie(eiSec, eiEndSec) = edges(secGraph); eiSec != eiEndSec; ++eiSec) {
            
            //getting the vertices of the current edges
            VertexDescriptor v1Sec, v2Sec;
            v1Sec = source(*eiSec,secGraph);
            v2Sec = target (*eiSec,secGraph);
            
            //check for compatibility of the edges (identity of edge labels and target/source vertex labels)
            bool labelCompatible = fstGraph[*eiFst].label == secGraph[*eiSec].label;
            bool verticesCompatible = ((fstGraph[v1Fst].label == secGraph[v1Sec].label)  &&
                                                        (fstGraph[v2Fst].label == secGraph[v2Sec].label)) ||
                                                       ((fstGraph[v1Fst].label == secGraph[v2Sec].label)  &&
                                                        (fstGraph[v2Fst].label == secGraph[v1Sec].label)) ;
            
            //edit the properties of the new vertex
            if (labelCompatible && verticesCompatible)     {  
                prodGraph[*current].id = *current;
                prodGraph[*current].label = ;
                prodGraph[*current].edgeFst = *eiFst;
                prodGraph[*current].edgeSec = *eiSec;
                ++current; //increment for the next loop.
            } //if compatible
        } // for second edge
    } // for first edge      
    
    //todo: get rid of the remaining vertices between current and end... not sure how 
    //"current" points to the first unused vertex. the range "current -> end" is all unused
    
    //iterate through all vertex pairs in the new product graph
    VertexIterator_p vi1P, vi2P, viEndP ;
    for (tie(vi1P, viEndP) = vertices(prodGraph); vi1P != viEndP; ++vi1P) {
        for (vi2P = vi1P; vi2P != viEndP; ++vi2P) {
            
            //todo: add edges between compatible vertices

        } // for second vertex
    } // for first vertex
}
