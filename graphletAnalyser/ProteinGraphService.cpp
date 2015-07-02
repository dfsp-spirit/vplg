#include "ProteinGraphService.h"

ProteinGraphService::ProteinGraphService() {
   Graph g_tmp;
   g = g_tmp;
   gc = GraphletCounts(g);
};

ProteinGraphService::ProteinGraphService(const Graph graph) {
    g = graph;
    sse_graphlets_2p = std::vector<std::string>();
    sse_graphlets_3p = std::vector<std::string>();
    sse_graphlets_tri = std::vector<std::string>();
    
    sse_graphlets_2p.push_back("HH");
    sse_graphlets_2p.push_back("HE");
    sse_graphlets_2p.push_back("HL");
    sse_graphlets_2p.push_back("EE");
    sse_graphlets_2p.push_back("EL");
    sse_graphlets_2p.push_back("LL");
    
    sse_graphlets_3p.push_back("HHH");
    sse_graphlets_3p.push_back("HHE");
    sse_graphlets_3p.push_back("HHL");
    sse_graphlets_3p.push_back("HEH");
    sse_graphlets_3p.push_back("HLH");
    sse_graphlets_3p.push_back("EEE");
    sse_graphlets_3p.push_back("EEH");
    sse_graphlets_3p.push_back("EEL");
    sse_graphlets_3p.push_back("EHE");
    sse_graphlets_3p.push_back("ELE");
    sse_graphlets_3p.push_back("EHE");
    sse_graphlets_3p.push_back("LLL");
    sse_graphlets_3p.push_back("LLH");
    sse_graphlets_3p.push_back("LLE");
    sse_graphlets_3p.push_back("LHL");
    sse_graphlets_3p.push_back("LEL");
    
    sse_graphlets_tri = sse_graphlets_3p;
    
    abs_ptgl_counts = std::vector<std::vector<int>>();
    norm_ptgl_counts = std::vector<std::vector<float>>();
    
    /* NOTE:
     * length of labeled 2 path vector:    6
     * length of labeled 3 path vector:   16
     * length of labeled triangle vector: 16
     * combined length:                   38 */
    
    
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

std::vector<std::vector<int>> ProteinGraphService::get_abs_ptgl_counts() {
    
    std::vector<std::vector<string>> patterns = std::vector<std::vector<std::string>>();
    std::vector<int> counts2p = gc.get_labeled_2_countsABS("sse_type", sse_graphlets_2p);
    
    
    patterns.push_back(sse_graphlets_3p);
    patterns.push_back(sse_graphlets_tri);
    
    std::vector<std::vector<int>> abs = gc.get_labeled_3_countsABS("sse_type",patterns);
    
    abs_ptgl_counts.push_back(counts2p);
    abs_ptgl_counts.push_back(abs[0]);
    abs_ptgl_counts.push_back(abs[1]);
    
    
    
    
    return abs_ptgl_counts;
    
    
}

std::vector<std::vector<float>> ProteinGraphService::get_norm_ptgl_counts() {
    
    abs_ptgl_counts = get_abs_ptgl_counts();
    float total = float (gc.get_total_counts());
    
    std::vector<float> norm2 = gc.normalize_counts(abs_ptgl_counts[0], total);
    std::vector<float> norm3p = gc.normalize_counts(abs_ptgl_counts[1],total);
    std::vector<float> norm3t = gc.normalize_counts(abs_ptgl_counts[2],total);
    
    norm_ptgl_counts.push_back(norm2);
    norm_ptgl_counts.push_back(norm3p);
    norm_ptgl_counts.push_back(norm3t);
    
    return norm_ptgl_counts;
}

std::string ProteinGraphService::getPdbid() {
    return g[graph_bundle].properties["pdb_id"];
}

std::string ProteinGraphService::getChainID() {
    
    return g[graph_bundle].properties["chain_id"];
}

std::string ProteinGraphService::getGraphTypeString() {
    
    return g[graph_bundle].properties["graph_type"];
}