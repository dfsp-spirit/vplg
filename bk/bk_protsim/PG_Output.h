/* 
 * File:   PG_Output.h
 * Author: julian
 *
 * Created on June 10, 2015, 2:19 PM
 */

#ifndef PG_OUTPUT_H
#define	PG_OUTPUT_H

#include "ProductGraph.h"


class PG_Output {
public:
    static std::list<unsigned long> get_common_first(const ProductGraph& pg, std::list<unsigned long> clique);
    static std::list<unsigned long> get_common_second(const ProductGraph& pg, std::list<unsigned long> clique);
    
    static std::list<int> get_vertex_ids_first(const ProductGraph& pg, std::list<unsigned long> clique);
    static std::list<int> get_vertex_ids_second(const ProductGraph& pg, std::list<unsigned long> clique);
    
    static std::string get_JSON_vertex_ids_first(const ProductGraph& pg, std::list<unsigned long> clique);
    static std::string get_JSON_vertex_ids_second(const ProductGraph& pg, std::list<unsigned long> clique);
private:
    static std::string int_list_to_JSON(std::list<int> clique);
};

#endif	/* PG_OUTPUT_H */

