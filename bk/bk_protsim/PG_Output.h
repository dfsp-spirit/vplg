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
    static std::list<VertexDescriptor_p> get_common_first(const ProductGraph& pg, std::list<VertexDescriptor_p> clique);
    static std::list<VertexDescriptor_p> get_common_second(const ProductGraph& pg, std::list<VertexDescriptor_p> clique);
    
    static std::list<int> get_vertex_ids_first(const ProductGraph& pg, std::list<VertexDescriptor_p> clique);
    static std::list<int> get_vertex_ids_second(const ProductGraph& pg, std::list<VertexDescriptor_p> clique);
    
    static std::string get_JSON_vertex_ids_first(const ProductGraph& pg, std::list<VertexDescriptor_p> clique);
    static std::string get_JSON_vertex_ids_second(const ProductGraph& pg, std::list<VertexDescriptor_p> clique);
    static std::string int_list_to_JSON(std::list<int> clique);
    static std::string int_list_to_plcc_vertex_mapping_string(std::list<int> clique, std::string prefix);
    static std::list<std::list<VertexDescriptor_p>> filter_iso(const ProductGraph& pg, std::list<std::list<VertexDescriptor_p>> cliques);
private:
    static bool comp(const std::pair<std::list<VertexDescriptor>,std::list<VertexDescriptor>> a, const std::pair<std::list<VertexDescriptor>,std::list<VertexDescriptor>> b);
  
};

#endif	/* PG_OUTPUT_H */

