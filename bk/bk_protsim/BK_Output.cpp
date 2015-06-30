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
 * File:   BK_Output.cpp
 * Author: julian
 * 
 * Created on June 9, 2015, 2:24 PM
 */

#include "BK_Output.h"

/*
 * Returns a JSON representation of an array containing all cliques.
 * Each inner array is one Clique, each integer is one vertex in that clique.
 * The indices are indices to vertices in the boost::adjacency_list graph that was passed to the BronKerbosch object.
 */
std::string BK_Output::get_JSON_all( const BronKerbosch& bk){
    return cliques_to_JSON(get_result_all(bk));
}

/*
 * Returns a JSON representation of an array containing all cliques of maximum size.
 * Each inner array is one Clique, each integer is one vertex in that clique.
 * The indices are indices to vertices in the boost::adjacency_list graph that was passed to the BronKerbosch object.
 */
std::string BK_Output::get_JSON_largest( const BronKerbosch& bk){
    return cliques_to_JSON(get_result_largest(bk));
}

/*
 * Returns a JSON representation of an array containing all cliques larger than the given size.
 * Each inner array is one Clique, each integer is one vertex in that clique.
 * The indices are indices to vertices in the boost::adjacency_list graph that was passed to the BronKerbosch object.
 */
std::string BK_Output::get_JSON_larger_than( const BronKerbosch& bk, int size){
    return cliques_to_JSON(get_result_larger_than(bk, size));
}

/*
 * Returns a vector with the number of Cliques of each size.  A value x at position i indicates x cliques of size i were found.
 */
std::vector<int> BK_Output::get_formated_pattern( const BronKerbosch& bk){
    std::vector<int> pattern;
    std::list<std::list<unsigned long>> result = bk.get_result_list();
        for (std::list<unsigned long>& c : result) {
            while (pattern.size() <= c.size() ) {
                pattern.push_back(0);
            }
            ++pattern[c.size()];
        }
        return pattern;
}

/*
 * Returns a list of all found cliques. Each Clique is a inner list of vertex indices.
 * The indices are indices to vertices in the boost::adjacency_list graph that was passed to the BronKerbosch object.
 */
std::list<std::list<unsigned long>> BK_Output::get_result_all( const BronKerbosch& bk){
    return bk.get_result_list();
}

/*
 * Returns a list of all cliques of maximum size. Each Clique is a inner list of vertex indices.
 * The indices are indices to vertices in the boost::adjacency_list graph that was passed to the BronKerbosch object.
 */
std::list< std::list<unsigned long>> BK_Output::get_result_largest( const BronKerbosch& bk){
    std::list<std::list<unsigned long>> list;
     int largest = 0;
     std::list<std::list<unsigned long>> bk_result = bk.get_result_list();
     for (std::list<unsigned long>& c : bk_result) {
         if (c.size() > largest) {
             largest = c.size();
             list.clear();
             list.push_back(c);
         } else {
             if (c.size() == largest) {
                 list.push_back(c);
             }
         }
     }
     return list;
}



/*
 * Returns a list of all Cliques larger than the given integer. Each Clique is a inner list of vertex indices.
 * The indices are indices to vertices in the boost::adjacency_list graph that was passed to the BronKerbosch object.
 */
std::list< std::list<unsigned long>> BK_Output::get_result_larger_than( const BronKerbosch& bk, int size){
    std::list<std::list<unsigned long>> list;
     for (std::list<unsigned long> c : bk.get_result_list()) {
         if (c.size() > size) {
             list.push_back(c);
         }
     }
     return list;
}

/*
 * Returns a JSON representation of a list of integers to be used as an element in a JSON list of lists.
 */
std::string BK_Output::int_list_to_JSON( const std::list<unsigned long>& list){
    std::stringstream sstream;
    sstream << "\t[\n";
    for (std::list<unsigned long>::const_iterator i = list.begin(), ie = list.end(); i != ie; ++i) {
        sstream << "\t\t" << *i;
        if (std::next(i) != ie) {
            sstream <<",\n";
        }
    }
    sstream << "\n\t]";
    return sstream.str();
}

/*
 * Returns a JSON representation of a list of lists of integers.
 */
std::string BK_Output::cliques_to_JSON( const std::list<std::list<unsigned long>>& clique_list) {
    std::stringstream sstream;
    sstream << "[\n";
    for (std::list<std::list<unsigned long>>::const_iterator i = clique_list.begin(), ie = clique_list.end(); i != ie; ++i) {
        sstream << int_list_to_JSON(*i);
        if (std::next(i) != ie) {
            sstream << ",\n";
        }
    }
    sstream << "\n]\n";
    return sstream.str();
}