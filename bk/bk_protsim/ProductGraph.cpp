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
 * File:   ProductGraph.cpp
 * Author: julian
 *
 * Created on April 29, 2015, 1:32 PM
 */

#include "ProductGraph.h"
#include "common.h"
#include <tuple>
#include <forward_list>

/*
 * default constructor. Assigns 0-Values.
 */
ProductGraph::ProductGraph() : fstGraph(0), secGraph(0) {
    prodGraph = Graph_p(0);
}

/*
 * Constructor for a ProductGraph object. No calculation will be done here. To get the Compatibility-Graph, invoke the
 * run() function on the created object and access the graph via getProductGraph().
 */
ProductGraph::ProductGraph(const Graph& fstGraph, const Graph& secGraph) : fstGraph(fstGraph), secGraph(secGraph) {
    prodGraph = Graph_p(0);
}

/*
 * Copy constructor. Not implemented yet.
 */
ProductGraph::ProductGraph(const ProductGraph&) : fstGraph(0), secGraph(0), prodGraph(0) {
    std::cerr << "ERROR: ProductGraph: Copy constructor not implemented.\n";
    exit(1);
}

ProductGraph::~ProductGraph() {}

Graph_p& ProductGraph::getProductGraph() { return prodGraph;}
const Graph_p& ProductGraph::getProductGraph() const { return prodGraph;}
const Graph& ProductGraph::getFirstGraph() const { return fstGraph;}
const Graph& ProductGraph::getSecondGraph() const { return secGraph;}

/*
 * Main Algorithm to compute the compatibility graph.
 * the vertices of the product graph get computed by paring every edge of the first graph with every edge
 * of the second. If the edges are compatible a new vertex is added to the product graph.
 * They are compatible if they, and their source/target vertices have the same labels.
 * an edge between two vertices is added if the edge pairings they represent are compatible.
 * they are compatible if either the edge pairs in either of the graphs share no common vertex, 
 * or both of them share a vertex with the same label in both graphs.
 */
void ProductGraph::run() {
    //All vertices get first added to a temporary list and then copied over into the graph to avoid the
    // time complexity of continually adding elements to a vector
    std::list<vertex_info_p> vertexList;
    int count = 0;

    //iterating through all edge pairs
    EdgeIterator eiFst, eiEndFst ;
    EdgeIterator eiSec, eiEndSec;
    for ( boost::tie(eiFst, eiEndFst) = boost::edges(fstGraph); eiFst != eiEndFst; ++eiFst) {
        VertexDescriptor v1Fst, v2Fst;
        v1Fst = boost::source(*eiFst, fstGraph);
        v2Fst = boost::target (*eiFst, fstGraph);
        for ( boost::tie(eiSec, eiEndSec) = boost::edges(secGraph); eiSec != eiEndSec; ++eiSec) {

            //getting the vertices of the current edges
            VertexDescriptor v1Sec, v2Sec;
            v1Sec = boost::source(*eiSec,secGraph);
            v2Sec = boost::target (*eiSec,secGraph);

            //check for compatibility of the edges (identity of edge labels and target/source vertex labels)
            bool labelCompatible = fstGraph[*eiFst].label == secGraph[*eiSec].label;
            bool verticesCompatible = ((fstGraph[v1Fst].label == secGraph[v1Sec].label)  &&
                                                        (fstGraph[v2Fst].label == secGraph[v2Sec].label)) ||
                                                       ((fstGraph[v1Fst].label == secGraph[v2Sec].label)  &&
                                                        (fstGraph[v2Fst].label == secGraph[v1Sec].label)) ;

            //edit the properties of the new vertex and adds it to the list
            if (labelCompatible && verticesCompatible)     {
                vertex_info_p temp;
                temp.label = "";
                temp.comment = "";
                temp.edgeFst =*eiFst;
                temp.edgeSec =*eiSec;
                vertexList.push_front(temp);
                ++count;
            } 
        }//end for all edges in the second graph
    }//end for all edges in the first graph

    //create the graph, now that the number of vertices in known
    prodGraph = Graph_p(count);
    prodGraph[boost::graph_bundle].id = 0;
    prodGraph[boost::graph_bundle].directed = 0; //i hope 0 is undirected
    //save the id of the original graphs as the label of the product graph
    prodGraph[boost::graph_bundle].label = std::to_string(fstGraph[boost::graph_bundle].id)  + "$" +
                                                                               std::to_string(secGraph[boost::graph_bundle].id) ;
    prodGraph[boost::graph_bundle].comment = "Compatibility graph of "+ std::to_string(fstGraph[boost::graph_bundle].id)  + " and " +
                                                                               std::to_string(secGraph[boost::graph_bundle].id);
    //copy the values from the list to the graph
    VertexIterator_p vi, viEnd;
    boost::tie(vi, viEnd) = boost::vertices(prodGraph);
    for (vertex_info_p& elem : vertexList) {
        if (vi != viEnd) {
            prodGraph[*vi] = elem;
            prodGraph[*vi].id = *vi;
            ++vi;
        } else {
            std::cerr << "ERROR: ProductGraph::run List -> Graph size disparity.\n";
        }
    }

    
    //Add the edges to the graph
    //iterate through all vertex pairs in the new product graph
    VertexIterator_p vi1P, vi2P, viEndP;
    for (boost::tie(vi1P, viEndP) = boost::vertices(prodGraph); vi1P != viEndP; ++vi1P) {
        for (vi2P = vi1P,++vi2P; vi2P != viEndP; ++vi2P) {  //avoid computing every edge twice( (v1, v2) and (v2, v1) )
        //for (boost::tie(vi2P, vi2EndP) = boost::vertices(prodGraph); vi2P != vi2EndP; ++vi2P) {
            bool comp, z;
            std::tie(comp, z) = verticesCompatible(vi1P, vi2P);
            if (comp) {
                EdgeDescriptor_p new_edge; bool flag;
                std::tie(new_edge, flag) = addEdge(*vi1P, *vi2P, prodGraph);
                if (flag) { //check if the edge already exists 
                    prodGraph[new_edge].label = z ? "z":"u";
                    prodGraph[new_edge].source = prodGraph[*vi1P].id;
                    prodGraph[new_edge].target = prodGraph[*vi2P].id;
                    prodGraph[new_edge].comment = "";
                } 
            }
        } // for second vertex
    } // for first vertex
} // void ProductGraph::computePrdGraph()

/*
 * Checks if two vertices in the product graph can be connected by an edge.
 * Returns a pair of 2 bools. The first is true if there can be an edge at all, the second is true if that edge is a z-edge
 */
std::pair<bool, bool> ProductGraph::verticesCompatible(VertexIterator_p p1, VertexIterator_p p2) {
    bool neighbouredFst = false, neighbouredSec = false; // is only necessary to avoid a false positive with empty vertex labels
    std::string labelFst = "", labelSec = ""; //save the labels the common vertex of the edges (if it exists)
    VertexDescriptor_p s1,t1,s2,t2; //hold the source/target vertices of edges to be compared

    // if the edges in either of the graphs are identical return false.
    if (prodGraph[*p1].edgeFst == prodGraph[*p2].edgeFst || prodGraph[*p1].edgeSec == prodGraph[*p2].edgeSec) {
        return std::make_pair(0,0);
    }
    
    // check for a common vertex between the edges of the first graph
    // if it exists aquire its label for later comparison with the second graph
    s1= source(prodGraph[*p1].edgeFst, fstGraph);
    t1 = target(prodGraph[*p1].edgeFst, fstGraph);
    s2 = source(prodGraph[*p2].edgeFst, fstGraph);
    t2 = target(prodGraph[*p2].edgeFst, fstGraph);
    
    if (s1 == s2 || s1 == t2) {
        labelFst = fstGraph[s1].label;
        neighbouredFst = true;
    } else {
        if (t1 == t2 || t1 == s2) {
            labelFst = fstGraph[t1].label;
            neighbouredFst = true;
        }
    }
    
    // repeat for the second graph
    s1= source(prodGraph[*p1].edgeSec, secGraph);
    t1 = target(prodGraph[*p1].edgeSec, secGraph);
    s2 = source(prodGraph[*p2].edgeSec, secGraph);
    t2 = target(prodGraph[*p2].edgeSec, secGraph);
    
    if (s1 == s2 || s1 == t2) {
        labelSec = secGraph[s1].label;
        neighbouredSec = true;
    } else {
        if (t1 == t2 || t1 == s2) {
            labelSec = secGraph[t1].label;
            neighbouredSec = true;
        }
    }
    
    // debug
    //if(labelFst == labelSec) {
    //    std::cout << apptag << "DEBUG: ProductGraph::verticesCompatible: Vertex labels identical: '" << labelFst << "' and '" << labelSec << "'.\n";
    //}
    
    bool z = (neighbouredFst && neighbouredSec) && (labelFst == labelSec);
    return std::make_pair( z || !(neighbouredFst || neighbouredSec),    z );
} //end bool ProductGraph::verticesCompatible(VertexIterator_p p1, VertexIterator_p p2)





/*
 * only for testing purposes, should not be distributed.
 */
void ProductGraph::setProductGraph(Graph_p& pg) {
    this->prodGraph = pg;
}