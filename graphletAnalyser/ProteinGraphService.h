/* 
 * File:   ProteinGraphService.h
 * Author: ben
 *
 * Created on May 5, 2015, 4:09 PM
 */

#ifndef PROTEINGRAPHSERVICE_H
#define	PROTEINGRAPHSERVICE_H

#include "Graph.h"
#include "GraphletCounts.h"
using namespace std;

/* This class only exists so the old function getGraphTypeInt is stored in a good
 * place */
class ProteinGraphService: public GraphService {
    
    private:
        
        //attributes
        Graph g;
        GraphletCounts gc;
        std::vector<std::string> sse_graphlets_2p; // all possible labels for edges
        std::vector<std::string> sse_graphlets_3p; // all possible labels for 2-paths
        std::vector<std::string> sse_graphlets_tri; // all possible labels for triangles
        
        std::vector<int> abs_ptgl_counts;
        std::vector<float> norm_ptgl_counts;
    
    public:
        //methods
        ProteinGraphService();
        ProteinGraphService(const Graph graph);
        int getGraphTypeInt(string graphType);
        string getPdbid();
        string getChainID();
        string getGraphTypeString();
        std::vector<int> get_abs_ptgl_counts();
        std::vector<float> get_norm_ptgl_counts();
    
};

#endif	/* PROTEINGRAPHSERVICE_H */

