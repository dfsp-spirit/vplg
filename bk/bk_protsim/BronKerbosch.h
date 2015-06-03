/* 
 * File:   BronKerbosch.h
 * Author: julian
 *
 * Created on May 8, 2015, 2:03 PM
 */

#ifndef BRONKERBOSCH_H
#define	BRONKERBOSCH_H

#include "Graph.h"
#include <set>
#include <forward_list>

/*
 * This class finds all Cliques in a Graph using a modified version of the Bron-Kerbosch algorithm.
 * To do so simply pass a boost::adjacency_list type Graph to the run(...) method.
 * The result of the search will be stored in the class object and can be accessed by the get_result_...() functions.
 * As the cliques are stored as Vertex_Descriptors, any change to the graph (invalidating Descriptors and Iterators)
 *  will also invalidate the results, so avoid altering the graph between calculating the cliques and using the results.
 */
class BronKerbosch {
public:
    BronKerbosch();
    ~BronKerbosch();
    
    void  run(const Graph_p& g);
    void clear_results();
    std::forward_list<std::set<VertexDescriptor_p>>  get_result_list();
    std::vector<int>  get_result_pattern();
    std::forward_list<std::set<VertexDescriptor_p>> get_result_largest();
    
    
private:
    std::forward_list<std::set<VertexDescriptor_p>> result;
    std::set<VertexDescriptor_p> T;
    
    void findCliques(std::set<VertexDescriptor_p>& C, std::set<VertexDescriptor_p>& P, std::set<VertexDescriptor_p>& D,
                                 std::set<VertexDescriptor_p>& S, const Graph_p& g);
    bool zPath(VertexDescriptor_p v, VertexDescriptor_p w, const std::set<VertexDescriptor_p>& s, const Graph_p& g);
    bool zCon(VertexDescriptor_p v, VertexDescriptor_p w, const Graph_p& g);
};
        



#endif	/* BRONKERBOSH_H */

