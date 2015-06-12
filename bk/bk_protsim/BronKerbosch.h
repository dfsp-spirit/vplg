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

/*
 * struct to handle the membership of vertices to sets. 
 */
struct object {
    object(): vertex(0), set(0), next_set(0),prev_set(0), next_vertex(0), prev_vertex(0){}
    unsigned long vertex; //to be compatible with VertexDescptor (typename for unsigned long)
    int set;
    object* next_set;
    object* prev_set;
    object* next_vertex;
    object* prev_vertex;
};

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
    BronKerbosch(const Graph_p& graph);
    ~BronKerbosch();
    
    void  run();
    void clear_results();
    std::list<std::list<VertexDescriptor_p>>  get_result_list() const;
    const Graph_p& get_Product_Graph() const;
    void set_result(std::list<std::list<VertexDescriptor_p>> value);  //only for test purposes , should be removed before final distribution
    
private:
    //members needed to find the cliques in a recursive function (work like global variables).
    const Graph_p& g;
    int num_sets;
    int MAX_SETS;
    object* set_array;
    object* vertex_array;
    std::set<VertexDescriptor_p> T;
   
    //stores the results 
    std::list<std::list<VertexDescriptor_p>> result;
    
    //convertes the results from the internal format into a std container
    std::list<VertexDescriptor_p> output_clique(int s);
    
    //main function for calculation
    void findCliques(int C, int P, int D, int S);
    bool zPath(VertexDescriptor_p v, VertexDescriptor_p w, int s);
    bool zCon(VertexDescriptor_p v, VertexDescriptor_p w);
    
    //functions to handle the data
    void remove_object(object* o);
    void remove_set(int s);
    void remove_vertex(int v, int s);
    void copy_set(int s1, int s2);
    void insert_vertex(int v, int s);
    bool is_vertex_in_set(int v, int s);
    int new_set();
};
        



#endif	/* BRONKERBOSH_H */

