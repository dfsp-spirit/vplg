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
    
    Graph_p* getProductGraph();

private:
    const Graph& fstGraph;
    const Graph& secGraph;
    Graph_p *prodGraph;
    void computePrdGraph();
};



#endif	/* PRODUCTGRAPH_H */

