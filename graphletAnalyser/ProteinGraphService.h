/* 
 * File:   ProteinGraphService.h
 * Author: ben
 *
 * Created on May 5, 2015, 4:09 PM
 */

#ifndef PROTEINGRAPHSERVICE_H
#define	PROTEINGRAPHSERVICE_H

#include "Graph.h"
using namespace std;

/* This class only exists so the old function getGraphTypeInt is stored in a good
 * place */
class ProteinGraphService: public GraphService {
    
    private:
        
        //attributes
        Graph g;
    
    public:
        //methods
        ProteinGraphService();
        ProteinGraphService(const Graph graph);
        int getGraphTypeInt(string graphType);
        string getPdbid();
        string getChainID();
        string getGraphTypeString();
    
};

#endif	/* PROTEINGRAPHSERVICE_H */

