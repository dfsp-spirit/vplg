/* 
 * File:   ProductGraph.h
 * Author: julian
 *
 * Created on April 29, 2015, 1:22 PM
 */

#ifndef PRODUCTGRAPH_H
#define	PRODUCTGRAPH_H


#include "Graph.h"


class ProductGraph {
public:
    ProductGraph();
    ProductGraph(const Graph& fstGraph, const Graph& secGraph);
    ProductGraph(const ProductGraph&);
    ~ProductGraph();
    
    void run();
    
    Graph_p& getProductGraph();
    const Graph_p& getProductGraph() const;
    const Graph& getFirstGraph() const;
    const Graph& getSecondGraph() const;
    

    void setProductGraph(Graph_p& pg); // only for test purposes, should never be distributed.

private:
    std::pair<bool,bool> verticesCompatible(VertexIterator_p vi1, VertexIterator_p vi2);
    const Graph& fstGraph;
    const Graph& secGraph;
    Graph_p prodGraph;
};



#endif	/* PRODUCTGRAPH_H */

