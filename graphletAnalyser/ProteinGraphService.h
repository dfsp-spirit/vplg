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
#include "GraphService.h"


using namespace std;
using namespace boost;


class ProteinGraphService: public GraphService {
    
    protected:
        
        //attributes
        
        std::vector<std::string> sse_graphlets_2p; // all possible labels for edges
        std::vector<std::string> sse_graphlets_3p; // all possible labels for 2-paths
        std::vector<std::string> sse_graphlets_tri; // all possible labels for triangles
        
        std::vector<std::vector<int>> abs_ptgl_counts;
        std::vector<std::vector<float>> norm_ptgl_counts;
    
    public:
        //methods
        ProteinGraphService();
        ProteinGraphService(const Graph& graph);
        int getGraphTypeInt(string graphType);
        string getPdbid();
        string getChainID();
        string getGraphTypeString();
        std::vector<std::vector<int>> get_abs_ptgl_counts();
        std::vector<std::vector<float>> get_norm_ptgl_counts();
        std::vector<float> get_norm_ptgl_counts_1dim();
        ProteinGraphService & operator=(const ProteinGraphService & serv);
        std::vector<std::string> get_2_sse_labels();
        std::vector<std::vector<std::string>> get_3_sse_labels();
    
};

#endif	/* PROTEINGRAPHSERVICE_H */

