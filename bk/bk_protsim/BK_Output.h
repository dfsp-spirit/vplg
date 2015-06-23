/* 
 * File:   BK_Output.h
 * Author: julian
 *
 * Created on June 9, 2015, 2:24 PM
 */

#ifndef BK_OUTPUT_H
#define	BK_OUTPUT_H

#include "BronKerbosch.h"

/*
 * Class to handle and format the results of a BronKerbosch object.
 * All member functions are static so there is no reason to create an instance of BK_Output.
 * 
 */
class BK_Output {
public:
    static std::string get_JSON_all( const BronKerbosch& bk) ;
    static std::string get_JSON_largest( const BronKerbosch& bk);
    static std::string get_JSON_larger_than( const BronKerbosch& bk, int size);
    
    static std::vector<int> get_formated_pattern( const BronKerbosch& bk);
    
    static std::list< std::list<unsigned long>> get_result_all( const BronKerbosch& bk);
    static std::list< std::list<unsigned long>> get_result_largest( const BronKerbosch& bk);
    static std::list< std::list<unsigned long>> get_result_larger_than( const BronKerbosch& bk, int size);
    static std::string int_list_to_JSON( const std::list<unsigned long>& clique);
    
private:
    
    static std::string cliques_to_JSON( const std::list<std::list<unsigned long>>& cliques);
};

#endif	/* BK_OUTPUT_H */