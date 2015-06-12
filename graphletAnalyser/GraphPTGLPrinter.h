/* 
 * File:   GraphPTGLPrinter.h
 * Author: ben
 *
 * Created on May 13, 2015, 11:46 AM
 */

#ifndef GRAPHPTGLPRINTER_H
#define	GRAPHPTGLPRINTER_H

#include "GraphPrinter.h"

class GraphPTGLPrinter : public GraphPrinter {
    

    
    public:
        // constructors
        GraphPTGLPrinter();
        GraphPTGLPrinter(Graph g);
        
        // methods, only applicable to graphs in ptgl format
        void printGraphInfo();
        std::string printVertices();
        std::string printEdges();
        std::string printGraphString();
        std::string printChainID();
        std::string printGraphTypeString();
    
};


#endif	/* GRAPHPTGLPRINTER_H */

