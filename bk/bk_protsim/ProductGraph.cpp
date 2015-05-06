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
    this->prodGraph = new Graph_p(num_vertices(fstGraph)*num_vertices(secGraph));
    computePrdGraph();
}

ProductGraph::ProductGraph(const ProductGraph&) : fstGraph(0), secGraph(0), prodGraph(0) {
    std::cerr << "not implemented";
}

ProductGraph::~ProductGraph() {
    delete prodGraph;
}

Graph_p* ProductGraph::getProductGraph() {
    return prodGraph;
}

void ProductGraph::computePrdGraph() { 
    //the vertices of the product graph get computed by paring every edge of the first graph with every edge 
    // of the second. If the edges are compatible a new vertex is added to the product graph
    
    int count =0; //counter to assign an id to created vertices 
    
    //iterating through all edge pairs
    EdgeIterator eiFst, eiEndFst ;
    EdgeIterator eiSec, eiEndSec ;
    for (tie(eiFst, eiEndFst) = edges(fstGraph); eiFst != eiEndFst; ++eiFst) {
        for (tie(eiSec, eiEndSec) = edges(secGraph); eiSec != eiEndSec; ++eiSec) {
            
            //getting the vertices of the current edges
            VertexDescriptor v1Fst, v2Fst, v1Sec, v2Sec;
            v1Fst = source(*eiFst, *prodGraph);
            v2Fst = target (*eiFst, *prodGraph);
            v1Sec = source(*eiSec,*prodGraph);
            v2Sec = target (*eiSec,*prodGraph);
            
            //check for compatibility of the edges (identity of edge labels and target/source vertex labels)
            bool labelCompatible = fstGraph[*eiFst].label == secGraph[*eiSec].label;
            bool verticesCompatible = ((fstGraph[v1Fst].label == secGraph[v1Sec].label)  &&
                                                        (fstGraph[v2Fst].label == secGraph[v2Sec].label)) ||
                                                       ((fstGraph[v1Fst].label == secGraph[v2Sec].label)  &&
                                                        (fstGraph[v2Fst].label == secGraph[v1Sec].label)) ;
            
            //add a new vertex to the product graph and save the original edges as properties
            if (labelCompatible && verticesCompatible)     {  
                
                
                vertex_info_p* temp = new vertex_info_p;
                temp->id = count++;
                temp->label = "";
                temp->comment = "";
                temp->sources["fs"]  =fstGraph[*eiFst].source;
                temp->sources["ft"]  =fstGraph[*eiFst].target;
                temp->sources["ss"]  =secGraph[*eiSec].source;
                temp->sources["st"]  =secGraph[*eiSec].target;
                add_vertex(*temp, *prodGraph); 


            } //if compatible
           
        } // for second edge
    } // for first edge      
    
    //iterate through all vertex pairs in the new product graph
    VertexIterator_p vi1Prod, vi2Prod, viEndProd ;
    for (tie(vi1Prod, viEndProd) = vertices(*prodGraph); vi1Prod != viEndProd; ++vi1Prod) {
        for (vi2Prod = vi1Prod; vi2Prod != viEndProd; ++vi2Prod) {
             
            /*
            bool fIdentity = false, sIdentity = false, fNeighbours = false, sNeighbours  = false;
            unsigned short fIdents = 0, sIdents = 0;
            std::string fLabel, sLabel;
            
            
            VertexDescriptor f1Source, f1Target, s1Source, s1Target, f2Source, f2Target, s2Source, s2Target;
            
            if ((*prodGraph)[*vi1Prod].sources["fs"] == (*prodGraph)[*vi2Prod].sources["fs"]) { 
                ++fIdents; 
            }
            if (*prodGraph[*vi1Prod]->sources["fs"] == *prodGraph[*vi2Prod]->sources["ft"]) { ++fIdents; }
            if (*prodGraph[*vi1Prod]->sources["ft"] == *prodGraph[*vi2Prod]->sources["fs"]) { ++fIdents; }
            if (*prodGraph[*vi1Prod]->sources["ft"] == *prodGraph[*vi2Prod]->sources["ft"]) { ++fIdents; }
            
            if (*prodGraph[*vi1Prod]->sources["ss"] == *prodGraph[*vi2Prod]->sources["ss"]) { ++sIdents; }
            if (*prodGraph[*vi1Prod]->sources["ss"] == *prodGraph[*vi2Prod]->sources["st"]) { ++sIdents; }
            if (*prodGraph[*vi1Prod]->sources["st"] == *prodGraph[*vi2Prod]->sources["ss"]) { ++sIdents; }
            if (*prodGraph[*vi1Prod]->sources["st"] == *prodGraph[*vi2Prod]->sources["st"]) { ++sIdents; }
            
            if ( (fIdents == sIdents) && (fIdents != 2) )
            */

           
            
            
        } // for second vertex
    } // for first vertex
}
