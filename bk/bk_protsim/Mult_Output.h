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


class Mult_Output {
public:
    Mult_Output();
    Mult_Output(std::string);
    ~Mult_Output();
    
    void out(const std::list<std::list<EdgeDescriptor>>&, const std::vector<Graph*>&);
    std::vector<unsigned int> distribution();

    
    
private:
    std::string DIR;
    std::vector<std::ofstream*> streams;
    std::vector<unsigned int> counts;
    
};


#endif /* MULT_OUTPUT_H */

