/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   MultAlign.h
 * Author: Julian
 *
 * Created on 13. Januar 2016, 18:36
 */

#ifndef MULTALIGN_H
#define MULTALIGN_H

#include "Graph.h"
#include "ProductGraph.h"
#include "BronKerbosch.h"
#include "BK_Output.h"
#include "PG_Output.h"
#include "Mult_Output.h"



class MultAlign {
public:
    MultAlign();
    MultAlign(const std::vector<Graph*>, const std::vector<ProductGraph*>, const std::vector<BronKerbosch*>, int, std::string);
    ~MultAlign();
    
    unsigned long num_cliques();
    void run();
    void filter();
private:
    std::list<std::list<EdgeDescriptor>> combine(std::list<std::list<EdgeDescriptor>>&, std::list<VertexDescriptor_p>&, const ProductGraph&);
    bool edgecomp (const Graph* a, const Graph* b) {return boost::num_edges(*a) < boost::num_edges(*b);}
    void intersect(std::list<std::list<EdgeDescriptor>>&, int);
    
    
    const std::vector<Graph*> graphs;
    const std::vector<ProductGraph*> products;
    const std::vector<BronKerbosch*> alignments;
    
    Mult_Output oManager;
    const int I;
    unsigned long count;
};


#endif /* MULTALIGN_H */

