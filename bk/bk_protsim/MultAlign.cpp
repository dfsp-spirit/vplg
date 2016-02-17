/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "MultAlign.h"



typedef std::list<EdgeDescriptor> c_edge;

typedef std::list<c_edge> c_clique;
typedef std::list<VertexDescriptor_p> s_clique_p;
typedef std::list<VertexDescriptor> s_clique;


MultAlign::MultAlign() : graphs(), products(), alignments(), I(0) {count = 0;}

MultAlign::MultAlign(const std::vector<Graph*> g, const std::vector<ProductGraph*> p, const std::vector<BronKerbosch*> a, int i, std::string out) 
                    : graphs(g), products(p), alignments(a), I(i){
    oManager = Mult_Output(out);
    count = 0;
} 

MultAlign::~MultAlign() {}

/*
 * Expands a complex clique by one simple clique
 * returns the new complex clique. all inputs are unchanged
 */
c_clique MultAlign::combine(c_clique& complex, s_clique_p& simple, const ProductGraph& pg){
    c_clique result;                                  //new complex clique
    for(c_edge& c_e : complex) {                               //for each complex edge
        result.push_back(c_edge());
        for(VertexDescriptor_p p_v : simple){                                     //for each pvertex
            if (c_e.front() == pg.getProductGraph()[p_v].edgeFst){               //if the pvertex matches the complex edge
                result.back() = c_e;                                        //copy complex edge to result list
                result.back().push_back( pg.getProductGraph()[p_v].edgeSec); }    //insert into the complex edge 
    }
        if (result.back().empty()) { result.pop_back(); }
    }
    return result;
}

void MultAlign::run() { 
    for(s_clique_p& clique : BK_Output::get_result_all(*alignments[1])) {            //for each simple clique in the 0-1 alignment
        c_clique complex = c_clique();                                        //create new complex clique list in results
        for(VertexDescriptor_p& p_vertex : clique) {                                                          //for each p_vertex in the simple clique
            complex.push_back(c_edge());                                        //create new list (complex edge)
            complex.back().push_back(products[1]->getProductGraph()[p_vertex].edgeFst);          //add the g1 edge as the first element  
            complex.back().push_back(products[1]->getProductGraph()[p_vertex].edgeSec);          //add the g2 edge as the second element 
        }
        intersect(complex, 2);
    }
}

void MultAlign::intersect(c_clique complex, int i){
    if (i<this->I) {
        for(s_clique_p& clique : BK_Output::get_result_all(*alignments[i])) {
            c_clique new_complex = this->combine(complex, clique, *products[i]);
            if( ! new_complex.empty()) {
                intersect(new_complex, i+1);
            } 
        } 
    } else {
        this->oManager.out(complex, this->graphs);
        ++count;
    }  
}

unsigned long MultAlign::num_cliques(){
    return this->count;
}

