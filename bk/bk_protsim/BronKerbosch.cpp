/* 
 * File:   BronKerbosch.cpp
 * Author: julian
 *
 * Created on May 8, 2015, 2:12 PM
 */


#include "BronKerbosch.h"



/*
 * Constructor
 */
BronKerbosch::BronKerbosch(const Graph_p& graph): g(graph), result(), T(), vertex_array(0), set_array(0) {
    this->MAX_SETS = boost::num_vertices(this->g)*8;
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
 * The edge_info struct has to contain a std::string attribute "label", containing either "z" or "u" for each edge to mark z-edges and u-edges. 
 * The used algorithm uses an unspecified Pivot element and discriminates between z and u edges when building the cliques.
 * Returns a list of sets containing the VertexDescriptors of the vertices of the cliques.
 * Each set is a unique Clique in the graph.
 * If the graph g is modified by any other sources (invalidating the VertexDescriptors), while this function is 
 * running it will lead to wrong results and in the worst case segfaults!!
 * The graph will only ever be read and never written to.
 */
void BronKerbosch::run() {
    /* 
     * this function works as an initializer for the recursive findCliques function.
     * It will start the findClique function n times for n vertices in the graph.
     * Each time it will pass a different, single vertex as the set C and its neighbours in the sets D, P or S.
     */
    
    this-> vertex_array = new object[boost::num_vertices(this->g)]();
    this-> set_array = new object[MAX_SETS](); // 8 migth be to mutch must investigate further
    
    int C = new_set();
    VertexIterator_p vi, ve;
    for (boost::tie(vi,ve) = boost::vertices(this->g); vi != ve; ++vi) { //iterate over all vertices in g
        std::cout << "[IN]    vertex " << *vi+1 <<" of " << *ve << "\n"; 
        VertexDescriptor_p v = boost::vertex(*vi, this->g);
        insert_vertex(v,C);
        int P = new_set();
        int D = new_set();
        int S = new_set();
        
        AdjacencyIterator_p ai, ae; //construct the neighbour set
        for (boost::tie(ai,ae)=boost::adjacent_vertices(v, this->g); ai!=ae;++ai) { //iterate over all neighbours of v
            VertexDescriptor_p a = boost::vertex(*ai, this->g);
            //if this neighbour is connected by a u-edge it is added to D, otherwise to S or P, 
            //if it was already used to initialize the algorithm once (if it is already in T) it will be added to S otherwise to P.
            //by adding already used vertices to S every Clique will be found exactly one and never multiple times.
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
        findCliques(C, P, D, S); 
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
}// end run

/*
 * Clears the stored results from the last time run was called. 
 * This function is only useful if you want to reuse the same BronKerbosch object multiple times and want to 
 * minimize the used memory between the uses.
 */
void BronKerbosch::clear_results() {
    this->result.clear();
    this->T.clear();
}

/*
 * Returns the complete list of all calculated Cliques.
 * If run(...) was not called before this function the result will be an empty list.
 */
std::forward_list<std::list<VertexDescriptor_p>>  BronKerbosch::get_result_list() {
        return this->result;
    }

/*
 * Returns a vector with the number of occurrences of Cliques of each size.  A value x at position i indicates x cliques of size i were found.
 * If run(...) was not called before this function the result will be an empty vector.
 */
 std::vector<int>  BronKerbosch::get_result_pattern() {
        std::vector<int> pattern;
        for (std::list<VertexDescriptor_p> c : this->result) {
            while (pattern.size() <= c.size() ) {
                pattern.push_back(0);
            }
            ++pattern[c.size()];
        }
        return pattern;
    }
 
/*
 * Returns a list of all Cliques of maximum size
 * If run(...) was not called before this function the result will be an empty list.
 */
 std::forward_list<std::list<VertexDescriptor_p>> BronKerbosch::get_result_largest(){
     std::forward_list<std::list<VertexDescriptor_p>> list;
     int largest = 0;
     for (std::list<VertexDescriptor_p> c : this->result) {
         if (c.size() > largest) {
             largest = c.size();
             list.clear();
         } else {
             if (c.size() == largest) {
                 list.push_front(c);
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
void BronKerbosch::findCliques(int C, int P, int D, int S) {
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
            //check for edge between piv and ui. every vertex connected to piv can be skiped, as every Clique 
            //containing this vertex and the pivot have already been found
            if ((os && !e.second) || (os && zPath(piv, ui, D)) || !os) {
                //create new sets for the next recursion
                
                insert_vertex(ui, C);
                int P2 = new_set();
                int D2 = new_set();
                int S2 = new_set();
                

                AdjacencyIterator_p ai, ae;
                for (boost::tie(ai,ae)=boost::adjacent_vertices(ui, this->g); ai!=ae;++ai) {
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
                findCliques(C, P2, D2, S2);
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
 * returns true if the two vertices v and w are connected by a z marked edge in the graph g.
 */
bool BronKerbosch::zCon(VertexDescriptor_p v, VertexDescriptor_p w) {
    EdgeDescriptor_p e; bool flag;
    boost::tie(e, flag)= boost::edge(v,w, this->g);
    return flag && (this->g[e].label == "z");
}//end zCon()

/*
 * returns true if vertex start is neighbour of one vertex in set S, which is not connected to v
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
 * removes a vertex v from set s
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
 * creates a new object and inserts it into the pointer chains starting at vertex_array[v] and set_array[s]
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
 * returns true if vertex v is found in the set s
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
 *  writes all vertices from a set to a std::list
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