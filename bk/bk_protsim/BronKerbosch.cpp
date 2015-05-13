/* 
 * File:   BronKerbosch.cpp
 * Author: julian
 *
 * Created on May 8, 2015, 2:12 PM
 */


#include "BronKerbosch.h"
#include <set>

BronKerbosch::BronKerbosch() {
    
}

BronKerbosch::~BronKerbosch() {
    
}

void BronKerbosch::run(const Graph_p& g) {
    graphToSets();
    //findCliques();
    //todo: change return type and add some kind of return 
}

void BronKerbosch::graphToSets() {
    
}

void BronKerbosch::findCliques(std::set<VertexIterator_p> C, std::set<VertexIterator_p> P, std::set<VertexIterator_p> D,
                                 std::set<VertexIterator_p> S, const Graph_p& g) {
    if (P.empty() && S.empty()) {
            // gebe clique aus  (return C)
    } else {
        VertexIterator_p ut = *P.begin();
        for (VertexIterator_p elem : P) {
            std::pair<EdgeDescriptor_p, bool> e = edge(vertex(*ut,g), vertex(*elem,g), g); //check for edge between ut and elem
            if (e.second || is_neighbour_in_set(ut, elem, D)) {
                std::set<VertexIterator_p> P2 = P; 
                std::set<VertexIterator_p> D2 = D;
                std::set<VertexIterator_p> S2 = S;
                
                
            }
            
        }
    }
}

bool BronKerbosch::is_neighbour_in_set(VertexIterator_p v, VertexIterator_p w, const std::set<VertexIterator_p>& s) {
    //todo: implement stuff
    std::cerr << "not implemented";
    return true;
}