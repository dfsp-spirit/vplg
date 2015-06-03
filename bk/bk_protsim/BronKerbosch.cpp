/* 
 * File:   BronKerbosch.cpp
 * Author: julian
 *
 * Created on May 8, 2015, 2:12 PM
 */


#include "BronKerbosch.h"
int rec;
/*
 * default constructor. does nothing.
 */
BronKerbosch::BronKerbosch() {
    
}

/*
 * default destructor. does nothing.
 */
BronKerbosch::~BronKerbosch() {
    
}

/*
 * runs a modified Bron-Kerbosch algorithm on a boost::adjacency_list type graph
 * The edge_info struct has to contain a std::str attribute "label", containing either "z" or "u" for each edge to mark z-edges and u-edges. 
 * The used algorithm uses an unspecified Pivot element and discriminates between z and u edges when building the cliques.
 * Returns a list of sets containing the VertexDescriptors of the vertices of the cliques.
 * Each set is a unique Clique in the graph.
 * If the graph g is modified by any other sources (invalidating the VertexDescriptors), while this function is 
 * running it will lead to wrong results and in the worst case segfaults!!
 * The graph will only ever be read and never written to.
 */
void BronKerbosch::run(const Graph_p& g) {
    /* 
     * this function works as an initializer for the recursive findCliques function.
     * It will start the findClique function n times for n vertices in the graph.
     * Each time it will pass a different, single vertex as the set C and its neighbours in the sets D, P or S.
     */
    rec = 0;
    this -> result.clear();
    this -> T.clear();
    
    std::set<VertexDescriptor_p> C;
    
    VertexIterator_p vi, ve;
    for (boost::tie(vi,ve) = vertices(g); vi != ve; ++vi) { //iterate over all vertices in g
        std::cout << "[IN]    vertex " << *vi+1 <<" of " << *ve << "\n"; 
        VertexDescriptor_p v = vertex(*vi,g);
        C.insert(v);
        std::set<VertexDescriptor_p> D;
        std::set<VertexDescriptor_p> P;
        std::set<VertexDescriptor_p> S;
        
        AdjacencyIterator_p ai, ae; //construct the neighbour set
        for (boost::tie(ai,ae)=adjacent_vertices(v,g); ai!=ae;++ai) { //iterate over all neighbours of v
            VertexDescriptor_p a = vertex(*ai,g);
            //if this neighbour is connected by a u-edge it is added to D, otherwise to S or P, 
            //if it was already used to initialize the algorithm once (if it is already in T) it will be added to S otherwise to P.
            //by adding already used vertices to S every Clique will be found exactly one and never multiple times.
            if (zCon(v,a,g)) {
                if (T.find(a) != T.end()) {
                    S.insert(a);
                } else { 
                    P.insert(a);
                }
            } else {
                D.insert(a);
            } //end else v and a z-connected
        }//end for all neighbours of v
        findCliques(C, P, D, S, g); 
        C.clear();
        this->T.insert(v);
    }//end for all vertices
}// end run

/*
 * Clears the stored results from the last time run was called. 
 * This function is only useful if you want to reuse the same BronKerbosch object multiple times and want to 
 * minimize the used memory between the uses.
 */
void BronKerbosch::clear_results() {
    this->result.clear();
}

/*
 * Returns the complete list of all calculated Cliques.
 * If run(...) was not called before this function the result will be an empty list.
 */
std::forward_list<std::set<VertexDescriptor_p>>  BronKerbosch::get_result_list() {
        return this->result;
    }

/*
 * Returns a vector with the number of occurrences of Cliques of each size.  A value x at position i indicates x cliques of size i were found.
 * If run(...) was not called before this function the result will be an empty vector.
 */
 std::vector<int>  BronKerbosch::get_result_pattern() {
        std::vector<int> pattern;
        for (std::set<VertexDescriptor_p> s : this->result) {
            while (pattern.size() <= s.size() ) {
                pattern.push_back(0);
            }
            ++pattern[s.size()];
        }
        return pattern;
    }
 
/*
 * Returns a list of all Cliques of maximum size
 * If run(...) was not called before this function the result will be an empty list.
 */
 std::forward_list<std::set<VertexDescriptor_p>> BronKerbosch::get_result_largest(){
     std::forward_list<std::set<VertexDescriptor_p>> list;
     int largest = 0;
     for (std::set<VertexDescriptor_p> s : this->result) {
         if (s.size() > largest) {
             largest = s.size();
             list.clear();
         } else {
             if (s.size() == largest) {
                 list.push_front(s);
             }
         }
     }
     return list;
 }
 
/*
 * Private function to do the actual work.
 * It is initialized by the run function, giving it a single vertex in the set C.
 * The function will then recursively find all Cliques containing this vertex.
 * All found cliques are added to the private member field result.
 */
void BronKerbosch::findCliques(std::set<VertexDescriptor_p>& C, std::set<VertexDescriptor_p>& P, std::set<VertexDescriptor_p>& D,
                                                     std::set<VertexDescriptor_p>& S, const Graph_p& g) {
    if (P.empty() && S.empty()) {
        this->result.push_front(C);//add the found clique to the result list
        //std::cout << "[BK]    found Clique of size : " << C.size() <<" #-#-#\n";

    } else {
        std::set<VertexDescriptor_p> P1 = P;
        std::set<VertexDescriptor_p> S1 = S;
        //chose a pivot element. Will choose the pivot from P, if possible, else from S.
        //no heuristic used, it is simply the first vertex in the set
        VertexDescriptor_p piv;
        piv = P.empty()  ? *S.begin() : *P.begin();
        //start of main algorithm
        //iterates over all vertices connected to every vertex in C by a z-edge
        
        int count = 0;
        int siz = P.size();
        for (VertexDescriptor_p ui : P) {
            std::cout << "[BK]        ";
            for (int stuff = 0; stuff < rec; ++stuff) {std::cout << "    ";}
            std::cout << "vertex " << ++count <<" of " << siz << "\n";
            std::pair<EdgeDescriptor_p, bool> e = edge(piv, ui, g); 
            //check for edge between piv and ui. every vertex connected to piv can be skiped, as every Clique 
            //containing this vertex and the pivot have already been found
            if ((!S.empty() &&!e.second) || (!S.empty() && zPath(piv, ui, D, g)) || S.empty()) {
                //create new sets for the next recursion
                
                std::set<VertexDescriptor_p> P2;
                std::set<VertexDescriptor_p> D2;
                std::set<VertexDescriptor_p> S2;
                

                AdjacencyIterator_p ai, ae;
                for (boost::tie(ai,ae)=adjacent_vertices(ui,g); ai!=ae;++ai) {
                    VertexDescriptor_p v = vertex(*ai,g);
                    //std::cout << "[BK]    current n : " << v << "\n"; 
                    if (P1.find(v) != P1.end()) { //if v is in P
                        P2.insert(v);
                        //std::cout << "[BK]    v in p1 -> p2.insert\n"; 
                    } else {
                        if (D.find(v) != D.end()) { //if v is in D
                            if (zCon(v,ui,g)) {//if c-edge between v and ui
                                if (this->T.find(v) != this->T.end())   { S2.insert(v); /*std::cout << "[BK]    v in D, zcon, T -> S2.insert\n"; */} 
                                else   { P2.insert(v); /*std::cout << "[BK]    v in D,  zcon -> P2.insert\n";*/}
                            } else { D2.insert(v); /*std::cout << "[BK]    v in D, not zcon -> D2.insert\n";*/} // end if v and ui z-connected 
                        } else {
                            if (S1.find(v) != S1.end())   { S2.insert(v); /*std::cout << "[BK]    v in not D, S1-> S2.insert\n";*/} 
                        } //end if v  in D
                    }//end if v in P
                }// end for neigbours of ui
                C.insert(ui);
                //std::cout << "[BK]    before recursion\n";
                //std::cout << "[BK]    recursion " << ++rec << "\n";
                ++rec;
                findCliques(C, P2, D2, S2, g);
                --rec;
                //std::cout << "[BK]    recursion " << --rec << "-----------------------\n";
                //std::cout << "[BK]    after recursion\n";
                C.erase(C.find(ui));
                P1.erase(P1.find(ui));
                S1.insert(ui);
            }//end if ui adjacent to piv || z-path exists
        }//end for vertices in P
    }//end if P and S not empty
}// end findCliques()

/*
 * returns true if the two vertices v and w are connected by a z marked edge in the graph g.
 */
bool BronKerbosch::zCon(VertexDescriptor_p v, VertexDescriptor_p w, const Graph_p& g) {
    EdgeDescriptor_p e; bool flag;
    boost::tie(e, flag)= edge(v,w,g);
    return flag && (g[e].label == "z");
}//end zCon()

/*
 * returns true if vertex start is neighbour of one vertex in set S, which is not connected to v
 */
bool BronKerbosch::zPath(VertexDescriptor_p v, VertexDescriptor_p start, const std::set<VertexDescriptor_p>& S, const Graph_p& g) {
    for (VertexDescriptor_p n : S) {
        if (edge(start,n,g).second && n!=v) { return true;}
    }
    return false;
}//end zPath()