/* 
 * File:   BronKerbosch.cpp
 * Author: julian
 *
 * Created on May 8, 2015, 2:12 PM
 */


#include "BronKerbosch.h"
#include <set>
#include <bits/stl_set.h>

BronKerbosch::BronKerbosch() {
    
}

BronKerbosch::~BronKerbosch() {
    
}

std::forward_list<std::set<VertexDescriptor_p>>  BronKerbosch::run(const Graph_p& g) {
    this -> result.clear();
    std::set<VertexDescriptor_p> C;
    std::set<VertexDescriptor_p> D;
    std::set<VertexDescriptor_p> P;
    std::set<VertexDescriptor_p> S;
    findCliques(C, graphToSets(P, g), D, S, g); 
    return result;
}

std::set<VertexDescriptor_p>& BronKerbosch::graphToSets(std::set<VertexDescriptor_p>& result, const Graph_p& g) {
    VertexIterator_p vi, ve;
    boost::tie(vi,ve) = vertices(g);
    result.insert(vi, ve);
    return result;;
}

void BronKerbosch::findCliques(std::set<VertexDescriptor_p>& C, std::set<VertexDescriptor_p>& P, std::set<VertexDescriptor_p>& D,
                                                     std::set<VertexDescriptor_p>& S, const Graph_p& g) {
    if (P.empty() && S.empty()) {
        this->result.push_front(C);
    } else {
        std::set<VertexDescriptor_p> P2 = P; 
        std::set<VertexDescriptor_p> D2 = D;
        std::set<VertexDescriptor_p> S2 = S;
        std::set<VertexDescriptor_p> N;
        
        VertexDescriptor_p piv;
        piv = P.empty()  ? *S.begin() : *P.begin();
        for (VertexDescriptor_p ui : P) {
            std::pair<EdgeDescriptor_p, bool> e = edge(piv, ui, g); //check for edge between piv and ui
            if (!e.second || zPath(piv, ui, D, g)) {
                //create new sets for the next recursion
                P2 = P; 
                D2 = D;
                S2 = S;
                AdjacencyIterator_p ai, ae; //construct the neighbour set
                for (boost::tie(ai,ae)=adjacent_vertices(ui,g); ai!=ae;++ai) {
                    N.insert(vertex(*ai,g));
                }// for neighbour of ui
                for(VertexDescriptor_p v : D2) {
                    if (P.find(v) == P.end()) {
                        P2.insert(v);
                    } else {
                        if (D.find(v) == D.end()) {
                            if (zCon(v,ui,g)) {
                                if (true) {//v E T???
                                    S2.insert(v);
                                } else { 
                                    P2.insert(v);
                                }//if v (not) in T
                                D2.erase(D2.find(v));
                            } 
                        } else {
                            if (S.find(v) == S.end()) {
                                S2.insert(v);
                            }//if v in S
                        } //if v (not) in D
                    }//if v (not) in P
                }// for vertices in D2
            }//if ui adjacent to piv || z-path exists
            
            std::set<VertexDescriptor_p> P3;
            set_intersection(P2.begin(), P2.end(), N.begin(), N.end(), std::inserter(P3, P3.begin()));
            std::set<VertexDescriptor_p> D3 = D;
            set_intersection(D2.begin(), D2.end(), N.begin(), N.end(), std::inserter(D3, D3.begin()));
            std::set<VertexDescriptor_p> S3 = S;
            set_intersection(S2.begin(), S2.end(), N.begin(), N.end(), std::inserter(S3, S3.begin()));
            std::set<VertexDescriptor_p> C3 = C; C3.insert(ui);
            
            findCliques(C3, P3, D3, S3, g);
            S.insert(ui);
        }//for vertices in P
    }//if P and S not empty
}// findCliques()

bool BronKerbosch::zCon(VertexDescriptor_p v, VertexDescriptor_p w, const Graph_p& g) {
    EdgeDescriptor_p e; bool flag;
    boost::tie(e, flag)= edge(v,w,g);
    return flag && (g[e].label == "z");
}//zCon()

bool BronKerbosch::zPath(VertexDescriptor_p v, VertexDescriptor_p w, const std::set<VertexDescriptor_p>& s, const Graph_p& g) {
    
    //true if: w is connected to any vertex in s that is not adjacent to v
    
    //todo: implement stuff
    std::cerr << "not implemented";
    return true;
}//zPath()