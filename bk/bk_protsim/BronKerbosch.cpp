/*
 *   This file is part of:
 * 
 *   bk_protsim Copyright (C) 2015  Molecular Bioinformatics group, Goethe-University Frankfurt
 * 
 *   Written by Julian Gruber-Roet, maintained by Tim Schaefer.
 *   This program comes with ABSOLUTELY NO WARRANTY.
 *    This is free software, and you are welcome to redistribute it
 *   under certain conditions, see the LICENSE file for details.
 */

/* 
 * File:   BronKerbosch.cpp
 * Author: julian
 *
 * Created on May 8, 2015, 2:12 PM
 */


#include "BronKerbosch.h"
#include <iostream>



BronKerbosch::BronKerbosch():g(0), result(), T(),vertex_array(0), set_array(0){}

/*
 * Constructor
 */
BronKerbosch::BronKerbosch(const Graph_p& graph): g(graph), result(), T(), vertex_array(0), set_array(0) {
    this->MAX_VERTICES = boost::num_vertices(this->g);
    this->MAX_SETS = MAX_VERTICES*8;
    this-> num_sets =0;
}

/*
 * Destructor
 */
BronKerbosch::~BronKerbosch() {
    if (vertex_array) delete[] vertex_array;
    if(set_array)       delete[] set_array;
}

/*
 * runs a modified Bron-Kerbosch algorithm on a boost::adjacency_list type graph
 * The edge_info struct has to contain a std::string attribute "label", containing  "z" (case sensitive) to mark z-Edges.
 * All other labels will be interpreted as u-Edges.
 * The used algorithm uses an unspecified Pivot element and discriminates between z and u edges when building the cliques.
 * To access the result it is advised use the static BK_Output class functions.
 * If the graph g is modified by any other sources (invalidating the VertexDescriptors), while this function is 
 * running it will lead to wrong results and in the worst case segfaults!!
 * The graph will only ever be read and never written to.
 */
void BronKerbosch::run_c() {
    /* 
     * this function works as an initializer for the recursive findCliques function.
     * It will start the findClique function n times for n vertices in the graph.
     * Each time it will pass a different, single vertex as the set C and its neighbours in the sets D, P or S.
     */
    
    this-> vertex_array = new object[MAX_VERTICES]();
    this-> set_array = new object[MAX_SETS]();
    
    int C = new_set();
    VertexIterator_p vi, ve;
    for (boost::tie(vi,ve) = boost::vertices(this->g); vi != ve; ++vi) { //iterate over all vertices in g
        //std::cout << "[IN]    vertex " << *vi+1 <<" of " << *ve << "\n"; //if not commented the unitTests will do strange things
        VertexDescriptor_p v = boost::vertex(*vi, this->g);
        insert_vertex(v,C);
        int P = new_set();
        int D = new_set();
        int S = new_set();
        
        AdjacencyIterator_p ai, ae;
        for (boost::tie(ai,ae)=boost::adjacent_vertices(v, this->g); ai!=ae;++ai) { //iterate over all neighbours of v
            VertexDescriptor_p a = boost::vertex(*ai, this->g);
            //if this neighbour is connected by a u-edge it is added to D, otherwise to S or P, 
            //if it was already used to initialize the algorithm once (if it is already in T) it will be added to S otherwise to P.
            //by adding already used vertices to S every Clique will be found exactly once and never multiple times.
            if (zCon(v,a)) {
                if (T.find(a) != T.end()) {
                    insert_vertex(a,S);
                } else { 
                    insert_vertex(a,P);
                }
            } else {
                insert_vertex(a,D);
            } //end else v and a z-connected
        }//end for all neighbours of v
        findCliques_c(C, P, D, S); 
        remove_set(S);
        remove_set(D);
        remove_set(P);
        remove_vertex(v,C);
        
        this->T.insert(v);
    }//end for all vertices
    
    //clean up
    remove_set(C);
    this-> T.clear();
    delete[] this->vertex_array; this->vertex_array = nullptr;
    delete[] this->set_array;      this->set_array = nullptr;
}// end run()

/*
 * runs a modified Bron-Kerbosch algorithm on a boost::adjacency_list type graph
 * The used algorithm uses an unspecified Pivot element.
 * To access the result it is advised use the static BK_Output class functions.
 * If the graph g is modified by any other sources (invalidating the VertexDescriptors), while this function is 
 * running it will lead to wrong results and in the worst case segfaults!!
 * The graph will only ever be read and never written to.
 */
void BronKerbosch::run() {
    this-> vertex_array = new object[MAX_VERTICES]();
    this-> set_array = new object[MAX_SETS]();
    int C = new_set();
    int P = new_set();
    int S = new_set();
    
    VertexIterator_p vi, ve;
    for (boost::tie(vi,ve) = boost::vertices(this->g); vi != ve; ++vi) { //iterate over all vertices in g
        insert_vertex(boost::vertex(*vi, this->g),P);
    }
    
    findCliques(C,P,S);
    remove_set(S);
    remove_set(P);
    remove_set(C);
}// end run_c()

/*
 * Returns a const reference to the graph on which the Bron-Kerbosch algorithm was used on. 
 */
const Graph_p& BronKerbosch::get_Product_Graph() const{
    return this->g;
}

/*
 * Returns the complete list of all calculated Cliques.
 * If run() was not called before this function the result will be an empty list.
 */
std::list<std::list<VertexDescriptor_p>>  BronKerbosch::get_result_list() const{
        return this->result;
    }

/*
 * Private function to do the actual work of the connected variant.
 * It is initialized by the run function, giving it a single vertex in the set C.
 * The function will then recursively find all Cliques containing this vertex.
 * All found cliques are added to the private member field result.
 */
void BronKerbosch::findCliques_c(int C, int P, int D, int S) {
    if (!set_array[P].next_vertex && !set_array[S].next_vertex) {
        this->result.push_front(output_clique(C));//add the found clique to the result list

    } else {
        int P1 = new_set(); copy_set(P,P1);
        int S1 = new_set(); copy_set(S,S1);
        //chose a pivot element. Will choose the pivot from P, if possible, else from S.
        //no heuristic used, it is simply the first vertex in the set
        VertexDescriptor_p piv;
        object* os=set_array[S].next_vertex;
        if( os ) piv=os->vertex; 
        object* op=set_array[P].next_vertex;
        if( op ) piv=op->vertex; 
        
        
        //start of main algorithm
        //iterates over all vertices connected to every vertex in C by a z-edge
        while (op) {
            VertexDescriptor_p ui = op->vertex;
            std::pair<EdgeDescriptor_p, bool> e = boost::edge(piv, ui, this->g); 
            //check for edge between piv and ui. every vertex connected to piv can be skipped, as every Clique 
            //containing this vertex and the pivot have already been found
            if ((os && !e.second) || (os && zPath(piv, ui, D)) || !os) {
                //create new sets for the next recursion
                
                insert_vertex(ui, C);
                int P2 = new_set();
                int D2 = new_set();
                int S2 = new_set();
                

                AdjacencyIterator_p ai, ae;
                for (boost::tie(ai,ae)=boost::adjacent_vertices(ui, this->g); ai!=ae;++ai) { //iterate over all neighbours of ui
                    VertexDescriptor_p n = boost::vertex(*ai, this->g);
                    if (is_vertex_in_set(n,P1)) { //if n is in P
                        insert_vertex(n, P2);
                    } else {
                        if (is_vertex_in_set(n,D)) { //if n is in D
                            if (zCon(n,ui)) {//if c-edge between n and ui
                                if (this->T.find(n) == this->T.end())   {   
                                    insert_vertex(n, P2);
                                }  else {
                                    insert_vertex(n, S2);
                                } //end if else //if n is not in T
                            } else {
                                insert_vertex(n, D2); 
                            } // end if else n and ui z-connected 
                        } else {
                            if (is_vertex_in_set(n,S1))   {
                                insert_vertex(n, S2);
                            } //end if n in S1
                        } //end if else n  in D
                    }//end if else n in P
                }// end for neigbours of ui
                findCliques_c(C, P2, D2, S2);
                remove_set(S2);
                remove_set(D2);
                remove_set(P2);
                remove_vertex(ui,C);
                remove_vertex(ui,P1);
                insert_vertex(ui,S1);
            }//end if ui adjacent to piv || z-path exists
            op = op->next_vertex;
        }//end for vertices in P
        remove_set(S1);
        remove_set(P1);
    }//end if P and S not empty
}// end findCliques()

/*
 * Private function to do the actual work.
 * It is initialized by the run_c function, giving it a single vertex in the set C.
 * The function will then recursively find all Cliques containing this vertex.
 * All found cliques are added to the private member field result.
 */
void BronKerbosch::findCliques(int C, int P, int S) {
    if (!set_array[P].next_vertex && !set_array[S].next_vertex) {
        this->result.push_front(output_clique(C));//add the found clique to the result list

    } else {
        int P1 = new_set(); copy_set(P,P1);
        int S1 = new_set(); copy_set(S,S1);
        //chose a pivot element. Will choose the pivot from P, if possible, else from S.
        //no heuristic used, it is simply the first vertex in the set
        VertexDescriptor_p piv;
        object* op=set_array[P].next_vertex;
        if( op ) piv=op->vertex; 
        
        
        //start of main algorithm
        //iterates over all vertices connected to every vertex in C by a z-edge
        while (op) {
            VertexDescriptor_p ui = op->vertex;
            if( ! boost::edge(piv,ui,this->g).second) {
                insert_vertex(ui, C);
                int P2 = new_set();
                int S2 = new_set();
                AdjacencyIterator_p ai, ae;
                for (boost::tie(ai,ae)=boost::adjacent_vertices(ui, this->g); ai!=ae;++ai) { //iterate over all neighbours of ui
                    VertexDescriptor_p n = boost::vertex(*ai, this->g);
                    if (is_vertex_in_set(n,P1)) { //if n is in P
                        insert_vertex(n, P2);
                    }
                    else if (is_vertex_in_set(n,S1)) { //if n is in S
                        insert_vertex(n, S2);
                    } 
                }
                findCliques(C,P2,S2);
                remove_set(S2);
                remove_set(P2);
                remove_vertex(ui,C);
                remove_vertex(ui,P1);
                insert_vertex(ui,S1);
            }
            op = op->next_vertex;
        }//end for vertices in P
        remove_set(S1);
        remove_set(P1);
    }//end if P and S not empty        
}// end findCliques()

/*
 * Returns true if the two vertices v and w are connected by a z marked edge.
 */
bool BronKerbosch::zCon(VertexDescriptor_p v, VertexDescriptor_p w) {
    EdgeDescriptor_p e; bool flag;
    boost::tie(e, flag)= boost::edge(v,w, this->g);
    return flag && (this->g[e].label == "z");
}//end zCon()

/*
 * Returns true if vertex start is a neighbour of one vertex in set S, which is not connected to v
 */
bool BronKerbosch::zPath(VertexDescriptor_p v, VertexDescriptor_p start, int s) {
    object* o = set_array[s].next_vertex;
    while (o) {
        VertexDescriptor n = o->vertex;
        if (boost::edge(start,n, this->g).second && n!=v) { return true;}
        o = o->next_vertex;
    }
    return false;
}//end zPath()

/*
 * Deletes an object and frees the memory
 */
void BronKerbosch::remove_object(object* o) {
    o->prev_set->next_set = o->next_set;
    o->prev_vertex->next_vertex = o->next_vertex;
    if( o->next_set )  { o->next_set->prev_set = o->prev_set; }
    if( o->next_vertex ) { o->next_vertex->prev_vertex = o->prev_vertex; }
    delete o;
}

/*
 * Deletes all objects associated to one set and invalidates the pointer to that chain
 */
void BronKerbosch::remove_set(int s){
    if (s != this->num_sets) {
        std::cerr << "not last set\n";
        exit(1); 
    }
    while (set_array[s].next_vertex) {
        remove_object(set_array[s].next_vertex);
    }
    set_array[s].next_vertex = nullptr;
    --(this->num_sets);
}
    
/*
 * Removes a vertex v from set s
 */
void BronKerbosch::remove_vertex(int v, int s){
    object* o= vertex_array[v].next_set;
    if( !o || o->set != s ) {
        std::cerr << " not in last set\n";
        exit(1);
    }
    remove_object(o);
}
    
/*
 * Copies the contents from set s1 to s2. s1 remains unmodified.
 */
void BronKerbosch::copy_set(int s1, int s2){
    object* o = set_array[s1].next_vertex;
    while( o ) {
        insert_vertex(o->vertex,s2);
        o = o->next_vertex;
    }
}
    
/*
 * Creates a new object and inserts it into the pointer chains starting at vertex_array[v] and set_array[s]
 */
void BronKerbosch::insert_vertex(int v, int s){
    object *o = new object();
    //set the values of the new object
    o->vertex = v;
    o->set = s;
    //set the pointers to the the dummy objects in the arrays
    o->prev_vertex = &set_array[s];
    o->prev_set = &vertex_array[v];
    //set the pointers to the next elements in the object chains
    o->next_vertex = set_array[s].next_vertex;
    o->next_set = vertex_array[v].next_set;
    //set the pointers of the dummy objects in the arrays to point to this object
    vertex_array[v].next_set = o;
    set_array[s].next_vertex = o;
    //if there are objects in the chains (the new object is not the only one)
    // set the previous pointers of those objects to this object
    if( o->next_vertex ) {
        o->next_vertex->prev_vertex = o;}
    if( o->next_set ) {
        o->next_set->prev_set = o;}
}
    
/*
 * Returns true if vertex v is found in the set s
 */
bool BronKerbosch::is_vertex_in_set(int v, int s){
    object* o = vertex_array[v].next_set;
    for (int i = 0; (i <= 8) && o; ++i) {
        if (o->set == s) {
            return true;
        }
        o = o->next_set;
    }
    return false;
}
    
/*
 * Returns the index of a new, empty set.
 */
int BronKerbosch::new_set(){
    if (this->num_sets > this->MAX_SETS) {
        std::cerr << "Reached maximum sets"; exit(1);
    }
    return ++(this->num_sets);
}
    
/*
 *  Writes all vertices from a set to a std::list
 */
std::list<VertexDescriptor_p> BronKerbosch::output_clique(int s) {
    std::list<VertexDescriptor_p> res;
    object* o = set_array[s].next_vertex;
    while (o) {
        res.push_front(o->vertex);
        o = o->next_vertex;
    }
    return res;
}

/*
 * Sets the internal result storage to the new value. only for testing purposes, should not be in the distributed version.
 */
void BronKerbosch::set_result(std::list<std::list<VertexDescriptor_p>> value) {
    this->result = value;    
}