#include "ProteinGraphService.h"

ProteinGraphService::ProteinGraphService() {
   Graph g_tmp;
   g = g_tmp;  
};

ProteinGraphService::ProteinGraphService(const Graph graph) {
    g = graph;
};

/* 
 * Return an integer corresponding to a protein-chain-type */
int ProteinGraphService::getGraphTypeInt(string graphType) {
  if(graphType == "alpha") {
        return 1;
    }
    else if(graphType == "beta") {
        return 2;
    }
    else if(graphType == "albe") {
        return 3;
    }
    else if(graphType == "alphalig") {
        return 4;
    }
    else if(graphType == "betalig") {
        return 5;
    }
    else if(graphType == "albelig") {
        return 6;
    }
    else if(graphType == "aa_graph") {
        return 7;
    }
    cerr << "WARNING: Invalid graph type string, cannot translate it to graph type int code.\n";
    return -1;  
};

