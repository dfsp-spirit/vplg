/* 
 * File:   PG_Output.cpp
 * Author: julian
 * 
 * Created on June 10, 2015, 2:19 PM
 */

#include "PG_Output.h"

/*
 * returns a list of all vertices form the first graph that are represented by the product graph edges in the clique. 
 */
std::list<unsigned long> PG_Output::get_common_first(const ProductGraph& pg, std::list<unsigned long> clique) {
    std::set<unsigned long> result_set; //set to ensure unique vertices. otherwise each vertex would be added n-1 times for a n-clique
    for (unsigned long& p_vertex : clique) {
        EdgeDescriptor e = pg.getProductGraph()[p_vertex].edgeFst;
        result_set.insert(boost::source(e,pg.getFirstGraph()));
        result_set.insert(boost::target(e,pg.getFirstGraph()));
    }
    return std::list<unsigned long> (result_set.begin(), result_set.end());
}

/*
 * returns a list of all vertices form the second graph that are represented by the product graph edges in the clique.
 */
std::list<unsigned long> PG_Output::get_common_second(const ProductGraph& pg, std::list<unsigned long> clique) {
    std::set<unsigned long> result_set; //set to ensure unique vertices. otherwise each vertex would be added n-1 times for a n-clique
    for (unsigned long& p_vertex : clique) {
        EdgeDescriptor e = pg.getProductGraph()[p_vertex].edgeSec;
        result_set.insert(boost::source(e,pg.getSecondGraph()));
        result_set.insert(boost::target(e,pg.getSecondGraph()));
    }
    return std::list<unsigned long> (result_set.begin(), result_set.end());
}
