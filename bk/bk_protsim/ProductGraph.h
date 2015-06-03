/* 
 * File:   ProductGraph.h
 * Author: julian
 *
 * Created on April 29, 2015, 1:22 PM
 */

#ifndef PRODUCTGRAPH_H
#define	PRODUCTGRAPH_H


#include "Graph.h"
#include <forward_list>


class ProductGraph {
public:
    ProductGraph();
    ProductGraph(const Graph& fstGraph, const Graph& secGraph);
    ProductGraph(const ProductGraph&);
    ~ProductGraph();
    
    std::forward_list<std::pair<EdgeDescriptor, EdgeDescriptor>> resultmapping(std::set<VertexDescriptor_p>& S);
    Graph_p& getProductGraph();
    const Graph& getFirstGraph();
    const Graph& getSecondGraph();

private:
    std::pair<bool,bool> verticesCompatible(VertexIterator_p vi1, VertexIterator_p vi2);
    const Graph& fstGraph;
    const Graph& secGraph;
    Graph_p prodGraph;
    void computePrdGraph();
};



#endif	/* PRODUCTGRAPH_H */

