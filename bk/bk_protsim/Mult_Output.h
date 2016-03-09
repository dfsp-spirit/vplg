/* 
 * File:   Mult_Output.h
 * Author: Julian
 *
 * Created on 16. Februar 2016, 19:56
 */

#ifndef MULT_OUTPUT_H
#define MULT_OUTPUT_H

#include <vector>
#include <fstream>
#include "Graph.h"

/*
 * Class to handle the results of the Mult_align class.
 * All clique passed to an instance of this class will be written to files in the specified directory.
 * For each clique size a new file will be added to allow for easier comparison.
 * Additionally the number of cliques of each size is counted and can be accessed at any time.
 */
class Mult_Output {
public:
    Mult_Output();
    Mult_Output(std::string);
    ~Mult_Output();
    
    void out(std::list<std::list<EdgeDescriptor>>&, const std::vector<Graph*>&);
    std::vector<unsigned int> distribution();
    
    void filter_iso();
    
    
private: 
    int filter_copy(std::string&, std::string&);
    std::string DIR;
    std::vector<std::ofstream*> streams;
    std::vector<unsigned int> counts;
    
};


#endif /* MULT_OUTPUT_H */

