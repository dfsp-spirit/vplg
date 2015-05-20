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

class BronKerbosch {
public:
    BronKerbosch();
    ~BronKerbosch();
    
    std::forward_list<std::set<VertexDescriptor_p>>  run(const Graph_p& g);
    
private:
    std::forward_list<std::set<VertexDescriptor_p>> result;
    std::set<VertexDescriptor_p>& graphToSets(std::set<VertexDescriptor_p>& r, const Graph_p& g);
    void findCliques(std::set<VertexDescriptor_p>& C, std::set<VertexDescriptor_p>& P, std::set<VertexDescriptor_p>& D,
                                 std::set<VertexDescriptor_p>& S, const Graph_p& g);
    bool zPath(VertexDescriptor_p v, VertexDescriptor_p w, const std::set<VertexDescriptor_p>& s, const Graph_p& g);
    bool zCon(VertexDescriptor_p v, VertexDescriptor_p w, const Graph_p& g);
};
        



#endif	/* BRONKERBOSH_H */

