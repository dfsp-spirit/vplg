/* 
 * File:   BK_Output.cpp
 * Author: julian
 * 
 * Created on June 9, 2015, 2:24 PM
 */

#include "BK_Output.h"

/*
 * returns a JSON string of an array of arrays of integers representing the vertices in all cliques found.
 * each inner array is one Clique, each integer is one vertex in that clique.
 */
std::string BK_Output::get_JSON_all( const BronKerbosch& bk){
    return cliques_to_JSON(get_result_all(bk));
}

/*
 * returns a JSON string of an array of arrays of integers representing the vertices in all cliques of maximal size found.
 * each inner array is one Clique, each integer is one vertex in that clique.
 */
std::string BK_Output::get_JSON_largest( const BronKerbosch& bk){
    return cliques_to_JSON(get_result_largest(bk));
}

/*
 * returns a JSON string of an array of arrays of integers representing the vertices in all cliques larger than the given size found.
 * each inner array is one Clique, each integer is one vertex in that clique.
 */
std::string BK_Output::get_JSON_larger_than( const BronKerbosch& bk, int size){
    return cliques_to_JSON(get_result_larger_than(bk, size));
}

/*
 * Returns a vector with the number of occurrences of Cliques of each size.  A value x at position i indicates x cliques of size i were found.
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
 * return a list of all found cliques. Each clique is a list of vertex indices of the product graph.
 */
std::list<std::list<unsigned long>> BK_Output::get_result_all( const BronKerbosch& bk){
    return bk.get_result_list();
}

/*
 * return a list of all cliques of maximum size. Each clique is a list of vertex indices of the product graph.
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
 * Returns a list of all Cliques larger than the given integer. Each clique is a list of vertex indices of the product graph.
 */
std::list< std::list<unsigned long>> BK_Output::get_result_larger_than( const BronKerbosch& bk, int size){
    std::list<std::list<unsigned long>> list;
     for (std::list<unsigned long>& c : bk.get_result_list()) {
         if (c.size() > size) {
             list.push_front(c);
         }
     }
     return list;
}

/*
 * Returns a hopefully correct representation of a list in JSON
 */
std::string BK_Output::int_list_to_JSON( std::list<unsigned long> list){
    std::stringstream sstream;
    sstream << "\t[\n";
    for (std::list<unsigned long>::iterator i = list.begin(), ie = list.end(); i != ie; ++i) {
        sstream << "\t\t" << *i;
        if (std::next(i) != ie) {
            sstream <<",\n";
        }
    }
    sstream << "\n\t]";
    return sstream.str();
}

/*
 * returns a list of lists in JSON
 */
std::string BK_Output::cliques_to_JSON( std::list<std::list<unsigned long>> clique_list) {
    std::stringstream sstream;
    sstream << "[\n";
    for (std::list<std::list<unsigned long>>::iterator i = clique_list.begin(), ie = clique_list.end(); i != ie; ++i) {
        sstream << int_list_to_JSON(*i);
        if (std::next(i) != ie) {
            sstream << ",\n";
        }
    }
    sstream << "\n]\n";
    return sstream.str();

}