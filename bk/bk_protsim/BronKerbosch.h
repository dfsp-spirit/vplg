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
 * All calculation is done by the run() function, so instantiation and calculation can be separated.
 * The result of the search will be stored in the class object and should in most cases be accessed using the static
 * member functions of the BK_Output class.
 * As the cliques are stored as Vertex_Descriptors, any change to the graph that invalidates Descriptors and Iterators
 * will also invalidate the results, so avoid altering the graph between calculating the cliques and using the results.
 * The graph will only ever be read and never written to.
 */
class BronKerbosch {
public:
    BronKerbosch();
    BronKerbosch(const Graph_p& graph);
    ~BronKerbosch();
    
    void  run();
    void  run_c();
    std::list<std::list<VertexDescriptor_p>>  get_result_list() const;
    const Graph_p& get_Product_Graph() const;
    void set_result(std::list<std::list<VertexDescriptor_p>> value);  //only for test purposes , should be removed before final distribution
    
private:
    //members needed to find the cliques in a recursive function (work like global variables).
    const Graph_p& g;
    int num_sets;
    int MAX_VERTICES;
    int MAX_SETS;
    object* set_array;
    object* vertex_array;
    std::set<VertexDescriptor_p> T;
   
    //stores the results 
    std::list<std::list<VertexDescriptor_p>> result;
      
    //convertes the results from the internal format into a std container
    std::list<VertexDescriptor_p> output_clique(int s);
    
    //main function for calculation
    void findCliques(int C, int P, int S);
    void findCliques_c(int C, int P, int D, int S);
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

