/* 
 * File:   ProductGraph.cpp
 * Author: julian
 *
 * Created on April 29, 2015, 1:32 PM
 */

#include "ProductGraph.h"
#include <tuple>

ProductGraph::ProductGraph() : fstGraph(0), secGraph(0) {
    prodGraph = Graph_p(0);
}

ProductGraph::ProductGraph(const Graph& fstGraph, const Graph& secGraph) : fstGraph(fstGraph), secGraph(secGraph) {
    //upper bound for maximum number of vertices in the product graph is |E1| * |E2| 
    prodGraph = Graph_p(num_vertices(fstGraph)*num_vertices(secGraph)); 
    prodGraph[boost::graph_bundle].id = 0; // maybe include parameter for the constructor or has to be changed manually later
    prodGraph[boost::graph_bundle].directed = 0; //i hope 0 is undirected
    //save the id of the original graphs as the label of the product graph
    prodGraph[boost::graph_bundle].label = std::to_string(fstGraph[boost::graph_bundle].id)  + "$" +
                                                                               std::to_string(secGraph[boost::graph_bundle].id) ;
    prodGraph[boost::graph_bundle].comment = "Compatibility graph of "+ std::to_string(fstGraph[boost::graph_bundle].id)  + " and " +
                                                                               std::to_string(secGraph[boost::graph_bundle].id);

    computePrdGraph();
}

ProductGraph::ProductGraph(const ProductGraph&) : fstGraph(0), secGraph(0), prodGraph(0) {
    std::cerr << "not implemented";
}

ProductGraph::~ProductGraph() {}

Graph_p& ProductGraph::getProductGraph() {
    return prodGraph;
}

void ProductGraph::computePrdGraph() { 
    /*the vertices of the product graph get computed by paring every edge of the first graph with every edge 
     * if the second. If the edges are compatible a new vertex is added to the product graph
     * they are compatible if they, and their source/target vertices have the same labels.
     * As all possible vertices were already added by the constructor, this method does not actually add vertices,
     * but rather fill the already existing ones and add the edges between them. All unused vertices get removes afterwards.
     */
    
    // current holds the next vertex to be edited
    VertexIterator_p current, end;
    boost::tie (current, end) = vertices(prodGraph); 
    
    //iterating through all edge pairs
    EdgeIterator eiFst, eiEndFst ;
    EdgeIterator eiSec, eiEndSec;
    for ( boost::tie(eiFst, eiEndFst) = edges(fstGraph); eiFst != eiEndFst; ++eiFst) {
        VertexDescriptor v1Fst, v2Fst;
        v1Fst = source(*eiFst, fstGraph);
        v2Fst = target (*eiFst, fstGraph);
        for ( boost::tie(eiSec, eiEndSec) = edges(secGraph); eiSec != eiEndSec; ++eiSec) {
            
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
                prodGraph[*current].label = "";
                prodGraph[*current].edgeFst = *eiFst;
                prodGraph[*current].edgeSec = *eiSec;
                ++current; //increment for the next loop.
            } //if compatible
        } // for second edge
    } // for first edge      
    
    //todo: get rid of the remaining vertices between current and end... not sure how 
    //"current" points to the first unused vertex. the range "current -> end" is all unused
    
    //Add the edges to the graph
    
    //iterate through all vertex pairs in the new product graph
    VertexIterator_p vi1P, vi2P, viEndP ;
    for (boost::tie(vi1P, viEndP) = vertices(prodGraph); vi1P != viEndP; ++vi1P) {
        for (vi2P = vi1P; vi2P != viEndP; ++vi2P) {
            if (vi2P == vi1P) {continue;} // there has to be a better way to do that
            bool comp, z;
            std::tie(comp, z) = verticesCompatible(vi1P, vi2P);
            if (comp) {
                EdgeDescriptor_p tmp; bool flag;
                boost::tie(tmp, flag) = add_edge(*vi1P, *vi2P, prodGraph);
                if (flag) {
                    prodGraph[tmp].label = z ? "z":"u";
                    prodGraph[tmp].source = prodGraph[*vi1P].id;
                    prodGraph[tmp].target = prodGraph[*vi2P].id;
                    prodGraph[tmp].comment = "";
                } else { std::cerr << "wait what? that was not supposed to be possible!";} //for-loop nesting should ensure this never happens
            }
        } // for second vertex
    } // for first vertex
} // void ProductGraph::computePrdGraph()

std::pair<bool, bool> ProductGraph::verticesCompatible(VertexIterator_p vi1, VertexIterator_p vi2) {
    bool edgeIdentityFst = 0, edgeIdentitySec = 0;
    bool neighbouredFst = 0, neighbouredSec = 0; // is only necessary to avoid a false positive with empty vertex labels
    std::string labelFst = "", labelSec = "";

    //first graph
    //identity check+ check for a common vertex in both edges and if it exists aquire its label 
    if (source(prodGraph[*vi1].edgeFst, fstGraph) == source(prodGraph[*vi2].edgeFst, fstGraph)) {
        if (target(prodGraph[*vi1].edgeFst, fstGraph) == target(prodGraph[*vi2].edgeFst, fstGraph)) {
            edgeIdentityFst = true;
        } else {
            neighbouredFst =  true;
            labelFst = fstGraph[source(prodGraph[*vi1].edgeFst, fstGraph)].label;
        }  
    } 

    if (source(prodGraph[*vi1].edgeFst, fstGraph) == target(prodGraph[*vi2].edgeFst, fstGraph)) {
        if (target(prodGraph[*vi1].edgeFst, fstGraph) == source(prodGraph[*vi2].edgeFst, fstGraph)) {
            edgeIdentityFst = true;
        } else {
            neighbouredFst =  true;
            labelFst = fstGraph[source(prodGraph[*vi1].edgeFst, fstGraph)].label;
        }  
    } 

    //second graph
    if (source(prodGraph[*vi1].edgeSec, secGraph) == source(prodGraph[*vi2].edgeSec, secGraph)) {
        if (target(prodGraph[*vi1].edgeSec, secGraph) == target(prodGraph[*vi2].edgeSec, secGraph)) {
            edgeIdentitySec = true;
        } else {
            neighbouredSec =  true;
            labelSec = secGraph[source(prodGraph[*vi1].edgeSec, secGraph)].label;
        }  
    } 

    if (source(prodGraph[*vi1].edgeSec, secGraph) == target(prodGraph[*vi2].edgeSec, secGraph)) {
        if (target(prodGraph[*vi1].edgeSec, secGraph) == source(prodGraph[*vi2].edgeSec, secGraph)) {
            edgeIdentitySec = true;
        } else {
            neighbouredSec =  true;
            labelSec = secGraph[source(prodGraph[*vi1].edgeSec, secGraph)].label;
        }  
    }
    bool z = (neighbouredFst && neighbouredSec) && (labelFst == labelSec);
    
    return std::make_pair(!(edgeIdentityFst || edgeIdentitySec) && (neighbouredFst == neighbouredSec) &&
                                (labelFst == labelSec),    z ); 
} //bool ProductGraph::verticesCompatible(VertexIterator_p vi1, VertexIterator_p vi2)