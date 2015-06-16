/* 
 * File:   PG_Output.cpp
 * Author: julian
 * 
 * Created on June 10, 2015, 2:19 PM
 */

#include "PG_Output.h"

/*
 * Returns a list of all vertices form the first graph that are represented by the product graph vertices in the passed list.
 */
std::list<unsigned long> PG_Output::get_common_first(const ProductGraph& pg, std::list<unsigned long> clique) {
    std::set<unsigned long> result_set; //set to ensure unique vertices. otherwise each vertex would be added n-1 times for a n-clique
    for (unsigned long& p_vertex : clique) {
        EdgeDescriptor e = pg.getProductGraph()[p_vertex].edgeFst;
        result_set.insert(boost::source(e,pg.getFirstGraph()));
        result_set.insert(boost::target(e,pg.getFirstGraph()));
    }
    return std::list<unsigned long> (result_set.begin(), result_set.end());
}

/*
 * Returns a list of all vertices form the second graph that are represented by the product graph vertices in the passed list.
 */
std::list<unsigned long> PG_Output::get_common_second(const ProductGraph& pg, std::list<unsigned long> clique) {
    std::set<unsigned long> result_set; //set to ensure unique vertices. otherwise each vertex would be added n-1 times for a n-clique
    for (unsigned long& p_vertex : clique) {
        EdgeDescriptor e = pg.getProductGraph()[p_vertex].edgeSec;
        result_set.insert(boost::source(e,pg.getSecondGraph()));
        result_set.insert(boost::target(e,pg.getSecondGraph()));
    }
    return std::list<unsigned long> (result_set.begin(), result_set.end());
}

/*
 * Returns a list of the IDs of the vertices stored in the Vertex_Properties. Basically this returns the vertex IDs
 * from the parsed input file. The list contains the vertices in the clique in the first Graph.
 */
std::list<int> PG_Output::get_vertex_ids_first(const ProductGraph& pg, std::list<unsigned long> clique) {
    std::set<int> result_set; //set to ensure unique vertices. otherwise each vertex would be added n-1 times for a n-clique
    for (unsigned long& p_vertex : clique) {
        EdgeDescriptor e = pg.getProductGraph()[p_vertex].edgeFst;
        result_set.insert(pg.getFirstGraph()[boost::source(e,pg.getFirstGraph())].id);
        result_set.insert(pg.getFirstGraph()[boost::target(e,pg.getFirstGraph())].id);
    }
    return std::list<int> (result_set.begin(), result_set.end());
}

/*
 * Returns a list of the IDs of the vertices stored in the Vertex_Properties. Basically this returns the vertex IDs
 * from the parsed input file. The list contains the vertices in the clique in the second Graph.
 */
std::list<int> PG_Output::get_vertex_ids_second(const ProductGraph& pg, std::list<unsigned long> clique){
    std::set<int> result_set; //set to ensure unique vertices. otherwise each vertex would be added n-1 times for a n-clique
    for (unsigned long& p_vertex : clique) {
        EdgeDescriptor e = pg.getProductGraph()[p_vertex].edgeSec;
        result_set.insert(pg.getSecondGraph()[boost::source(e,pg.getSecondGraph())].id);
        result_set.insert(pg.getSecondGraph()[boost::target(e,pg.getSecondGraph())].id);
    }
    return std::list<int> (result_set.begin(), result_set.end());
}

/*
 * Returns a JSON list of the original (gml parsed) vertex IDs, of all vertices in the specified clique in the first graph.
 */
std::string PG_Output::get_JSON_vertex_ids_first(const ProductGraph& pg, std::list<unsigned long> clique) {
    return PG_Output::int_list_to_JSON(PG_Output::get_vertex_ids_first(pg,  clique));
}

/*
 * Returns a JSON list of the original (gml parsed) vertex IDs, of all vertices in the specified clique in the second graph.
 */
std::string PG_Output::get_JSON_vertex_ids_second(const ProductGraph& pg, std::list<unsigned long> clique) {
    return PG_Output::int_list_to_JSON(PG_Output::get_vertex_ids_second(pg,  clique));
}


/*
 * Returns a JSON representation of a list of int
 */
std::string PG_Output::int_list_to_JSON(std::list<int> clique){
    std::stringstream sstream;
    sstream << "[\n";
    for (std::list<int>::iterator i = clique.begin(), ie = clique.end(); i != ie; ++i) {
        sstream << "\t" << *i;
        if (std::next(i) != ie) {
            sstream <<",\n";
        }
    }
    sstream << "\n]";
    return sstream.str();
}