/* 
 * File:   GraphPTGLPrinter.h
 * Author: ben
 *
 * Created on May 13, 2015, 11:46 AM
 */

#ifndef GRAPHPTGLPRINTER_H
#define	GRAPHPTGLPRINTER_H

#include "GraphPrinter.h"
#include "ProteinGraphService.h"

class GraphPTGLPrinter : public GraphPrinter {
    
    private:
        ProteinGraphService service;

    
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
        int saveCountsToDatabasePGXX();
        long getGraphDatabaseID(std::string, std::string, int);
        int databaseContainsGraphletsForGraph(unsigned long int);
        void saveCountsInNovaFormat(std::vector<std::vector<float>>,bool);
        void deleteGraphletCountEntryForGraph(unsigned long int);
        int testDatabasePGXX();
};


#endif	/* GRAPHPTGLPRINTER_H */

