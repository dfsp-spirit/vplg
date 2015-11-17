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
    

    
    public:
        // constructors
        GraphPTGLPrinter();
        
        
        // methods, only applicable to graphs in ptgl format
        void printGraphInfo(const Graph&) const;
        std::string printVertices(const Graph& g) const;
        std::string printEdges(const Graph& g) const;
        std::string printGraphString(const Graph& g) const;
        std::string printChainID() const;
        std::string printGraphTypeString() const;
        int savePGCountsToDatabasePGXX(int,std::vector<std::string>,std::vector<std::vector<float>>, std::vector<float>);
        int saveAACountsToDatabasePGXX(std::string,std::string,std::vector<std::vector<float>>,std::vector<float>);
        int saveCGCountsToDatabasePGXX(std::string,std::string,std::vector<std::vector<float>>,std::vector<float>);
        long getPGGraphDatabaseID(std::string, std::string, int) const;
        long getAAGraphDatabaseID(std::string) const;
        long getCGGraphDatabaseID(std::string) const;
        int databaseContainsGraphletsForPGGraph(unsigned long int) const;
        int databaseContainsGraphletsForAAGraph(unsigned long int) const;
        int databaseContainsGraphletsForCGGraph(unsigned long int) const;
        void saveCountsInNovaFormat(std::string,std::vector<std::vector<float>>,std::vector<float>) const;
        void deletePGGraphletCountEntryForGraph(unsigned long int);
        void deleteAAGraphletCountEntryForGraph(unsigned long int);
        void deleteCGGraphletCountEntryForGraph(unsigned long int);
        int testDatabasePGXX();
};


#endif	/* GRAPHPTGLPRINTER_H */

