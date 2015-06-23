/* 
 * File:   PG_Output.h
 * Author: julian
 *
 * Created on June 10, 2015, 2:19 PM
 */

#ifndef PG_OUTPUT_H
#define	PG_OUTPUT_H

#include "ProductGraph.h"

/*
 * Class to handle and format results of ProductGraph object.
 * All member functions are static so there is no reason to create an instance of PG_Output.
 */
class PG_Output {
public:
    static std::list<unsigned long> get_common_first(const ProductGraph& pg, std::list<unsigned long> clique);
    static std::list<unsigned long> get_common_second(const ProductGraph& pg, std::list<unsigned long> clique);
    
    static std::list<int> get_vertex_ids_first(const ProductGraph& pg, std::list<unsigned long> clique);
    static std::list<int> get_vertex_ids_second(const ProductGraph& pg, std::list<unsigned long> clique);
    
    static std::string get_JSON_vertex_ids_first(const ProductGraph& pg, std::list<unsigned long> clique);
    static std::string get_JSON_vertex_ids_second(const ProductGraph& pg, std::list<unsigned long> clique);
    static std::string int_list_to_JSON(std::list<int> clique);
    static std::string int_list_to_plcc_vertex_mapping_string(std::list<int> clique);
private:
    
};

#endif	/* PG_OUTPUT_H */

