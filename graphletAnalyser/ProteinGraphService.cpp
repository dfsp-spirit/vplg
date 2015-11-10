#include "ProteinGraphService.h"

ProteinGraphService::ProteinGraphService() {
   Graph g_tmp(1);
   g = g_tmp;
   GraphletCounts gc(g);
}

ProteinGraphService::ProteinGraphService(const Graph& graph) {
    g = graph;
    gc = GraphletCounts(g);
    sse_graphlets_2p = std::vector<std::string>();
    sse_graphlets_3p = std::vector<std::string>();
    sse_graphlets_tri = std::vector<std::string>();
    
    sse_graphlets_2p.push_back("HH");
    sse_graphlets_2p.push_back("HL");
    sse_graphlets_2p.push_back("EE");
    sse_graphlets_2p.push_back("HE");
    sse_graphlets_2p.push_back("EL");
    sse_graphlets_2p.push_back("LL");
    
    sse_graphlets_3p.push_back("HHH"); //0
    sse_graphlets_3p.push_back("HHE"); //1
    sse_graphlets_3p.push_back("HHL"); //2
    sse_graphlets_3p.push_back("HEH"); 
    sse_graphlets_3p.push_back("HLH");
    sse_graphlets_3p.push_back("EEE"); //5
    sse_graphlets_3p.push_back("EEH"); //6
    sse_graphlets_3p.push_back("EEL"); //7
    sse_graphlets_3p.push_back("EHE"); 
    sse_graphlets_3p.push_back("ELE");
    sse_graphlets_3p.push_back("EHE");
    sse_graphlets_3p.push_back("LLL"); //11
    sse_graphlets_3p.push_back("LLH"); //12
    sse_graphlets_3p.push_back("LLE"); //13
    sse_graphlets_3p.push_back("LHL"); 
    sse_graphlets_3p.push_back("LEL");
    
    sse_graphlets_tri.push_back(sse_graphlets_3p[0]);
    sse_graphlets_tri.push_back(sse_graphlets_3p[1]);
    sse_graphlets_tri.push_back(sse_graphlets_3p[2]);
    sse_graphlets_tri.push_back(sse_graphlets_3p[5]);
    sse_graphlets_tri.push_back(sse_graphlets_3p[6]);
    sse_graphlets_tri.push_back(sse_graphlets_3p[7]);
    sse_graphlets_tri.push_back(sse_graphlets_3p[11]);
    sse_graphlets_tri.push_back(sse_graphlets_3p[12]);
    sse_graphlets_tri.push_back(sse_graphlets_3p[13]);
    
    
    
    
    abs_ptgl_counts = std::vector<std::vector<int>>();
    norm_ptgl_counts = std::vector<std::vector<float>>();
    
    /* NOTE:
     * length of labeled 2 path vector:    6
     * length of labeled 3 path vector:   16
     * length of labeled triangle vector:  9
     * combined length:                   31 */
    
    
}

/* 
 * Return an integer corresponding to a protein-chain-type */
int ProteinGraphService::getGraphTypeInt(string graphType) {
    
  if(graphType.compare("alpha") == 0) {
        return 1;
    }
  else if(graphType.compare("beta") == 0) {
        return 2;
    }
  else if(graphType.compare("albe") == 0) {
        return 3;
    }
  else if(graphType.compare("alphalig") == 0) {
        return 4;
    }
  else if(graphType.compare("betalig") == 0) {
        return 5;
    }
  else if(graphType.compare("albelig") == 0) {
        return 6;
    }
  else if(graphType.compare("aa_graph") == 0) {
        return 7;
    }
  else { 
        cerr << "WARNING: Invalid graph type string, cannot translate it to graph type int code.\n";
    }
    return -1;  
}

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


std::vector<float> ProteinGraphService::get_norm_ptgl_counts_1dim() {
    
    std::vector<std::vector<float>> fvec = get_norm_ptgl_counts();
    std::vector<float> vec1dim = std::vector<float>();
    
    for (int i = 0; i< fvec.size(); i++) {
        
        std::vector<float> vec = fvec[i];
        
        for (int k = 0; k<vec.size();k++) {
            
            vec1dim.push_back(vec[k]);
            
        }
    }
    
    return vec1dim;
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

ProteinGraphService & ProteinGraphService::operator =(const ProteinGraphService & serv) {
    
    if (this == &serv) {
        return *this;
    }
    g = serv.getGraph();
    gc = GraphletCounts(g);
//    graphlet_identifier = "";
//    graphlet_patterns = std::vector<std::string>();
    
    
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
    return *this;
}

std::vector<std::string> ProteinGraphService::get_2_sse_labels() {
    
    return sse_graphlets_2p;
}

std::vector<std::vector<std::string>> ProteinGraphService::get_3_sse_labels() {
    
    std::vector<std::vector<std::string>> vec = std::vector<std::vector<std::string>>();
    vec.push_back(sse_graphlets_3p);
    vec.push_back(sse_graphlets_tri);
    
    return vec;
}