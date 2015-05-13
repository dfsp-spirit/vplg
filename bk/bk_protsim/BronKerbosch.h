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

class BronKerbosch {
public:
    BronKerbosch();
    ~BronKerbosch();
    
    void run(const Graph_p& g);
    
private:
    void graphToSets();
    void findCliques(std::set<VertexIterator_p> C, std::set<VertexIterator_p> P, std::set<VertexIterator_p> D,
                                 std::set<VertexIterator_p> S, const Graph_p& g);
    bool is_neighbour_in_set(VertexIterator_p v, VertexIterator_p w, const std::set<VertexIterator_p>& s);
};
        



#endif	/* BRONKERBOSH_H */

