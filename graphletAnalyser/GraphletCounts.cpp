/* 
 * File:   GraphletCounts.cpp
 * Author: tatiana
 * 
 * Modified by Tim Schaefer, May 2014: added documentation, clarified some error messages, work on database support, minor other stuff
 * 
 * Modified by Ben Haladik, May 2015:   Refactoring for new Graph Interface
 *                        
 * 
 * Created on May 21, 2013, 4:08 PM
 */

#include "GraphletCounts.h"
#include "Database.h"
#include "db.h"
// see http://www.postgresql.org/docs/8.3/static/libpq-build.html for build details

using namespace std;
using namespace boost;
using namespace pqxx;





GraphletCounts::GraphletCounts() {
    Graph g_tmp;
    memberGraph = g_tmp;
    graphName = "";
    pdbid = "";
    chain = "";
    graphtypestr = "";
    graphtype = -1;
    
    vector<int> tmp21 (1);
    graphlet2CountsABS = tmp21;
    vector<int> tmp31 (2);
    graphlet3CountsABS = tmp31;
    vector<int> tmp41 (6);
    graphlet4CountsABS = tmp41;
    vector<int> tmp51 (21);
    graphlet5CountsABS = tmp51;
    
    vector<float> tmp2 (1);
    graphlet2CountsNormalized = tmp2;
    vector<float> tmp3 (2);
    graphlet3CountsNormalized = tmp3;
    vector<float> tmp4 (6);
    graphlet4CountsNormalized = tmp4;
    vector<float> tmp5 (21);
    graphlet5CountsNormalized = tmp5;
    vector<float> tmpl (26);
    cl = tmpl;
    vector<int> tmpl1 (26);
    labeled_abs_counts = tmpl1;
    vector<float> tmpl2 (26);
    labeled_norm_counts = tmpl2;
    
    print = verbose;
    printGraphletDetails = saveGraphletDetails;
    all_counts_computed = false;
    unlabeled_abs_counts_computed = false;
    norm_counts_computed = false;
    labeled_norm_counts_computed = false;
    
    two_counts = false;
    three_counts = false;
    four_counts = false;
    five_counts = false;
    
    num2 = 0;
    num3 = 0;
    num4 = 0;
    num5 = 0;
}

GraphletCounts::GraphletCounts(const Graph& graph) { 
    memberGraph = graph;
    
    // Testing for existence of ptgl graph properties
    
    if (memberGraph[graph_bundle].properties.count("label") == 1) {
        graphName = memberGraph[graph_bundle].properties["label"];
    } else {
        graphName = "unnamed";
    }
    
    if (memberGraph[graph_bundle].properties.count("pdb_id") == 1) {
        pdbid = memberGraph[graph_bundle].properties["pdb_id"];
    } else {
        pdbid = "no_pdb_id";
    }
    
    if (memberGraph[graph_bundle].properties.count("chain_id") == 1) {
        chain = memberGraph[graph_bundle].properties["chain_id"];
    } else {
        chain = "no_chain_id";
    }
    
    if (memberGraph[graph_bundle].properties.count("graph_type") == 1) {
        std::string graphType = memberGraph[graph_bundle].properties["graph_type"];
        if(graphType == "alpha") {
        graphtype = 1;
        }
        else if(graphType == "beta") {
            graphtype = 2;
        }
        else if(graphType == "albe") {
            graphtype = 3;
        }
        else if(graphType == "alphalig") {
            graphtype = 4;
        }
        else if(graphType == "betalig") {
            graphtype = 5;
        }
        else if(graphType == "albelig") {
            graphtype = 6;
        }
        else if(graphType == "aa_graph") {
            graphtype = 7;
        }
        cerr << "WARNING: Invalid graph type string, cannot translate it to graph type int code in GraphletCounts.\n";
        graphtype = -1;  
    } else {
        graphtypestr = "no_graph_type";
        graphtype = -1;
        
    }

    
    

    // the default graphlet count vectors
    vector<int> tmp21 (1);
    graphlet2CountsABS = tmp21;
    vector<int> tmp31 (2);
    graphlet3CountsABS = tmp31;
    vector<int> tmp41 (6);
    graphlet4CountsABS = tmp41;
    vector<int> tmp51 (21);
    graphlet5CountsABS = tmp51;
    
    vector<float> tmp2 (1);
    graphlet2CountsNormalized = tmp2;
    vector<float> tmp3 (2);
    graphlet3CountsNormalized = tmp3;
    vector<float> tmp4 (6);
    graphlet4CountsNormalized = tmp4;
    vector<float> tmp5 (21);
    graphlet5CountsNormalized = tmp5;

    
    // vectors for counting labeled graphlets
    vector<float> tmpl (26);
    cl = tmpl;
    

       
    
    print = verbose;
    printGraphletDetails = saveGraphletDetails;
    all_counts_computed = false;
    unlabeled_abs_counts_computed = false;
    norm_counts_computed = false;
    labeled_norm_counts_computed = false;
    
    
    
    two_counts = false;
    three_counts = false;
    four_counts = false;
    five_counts = false;
}

void GraphletCounts::compute_unlabeled_abs_counts() {
    graphlet2CountsABS = count_connected_2_graphlets(memberGraph, "", size_2_labels);
    graphlet3CountsABS = count_connected_3_graphlets(memberGraph,"", size_3_labels);
    graphlet4CountsABS = count_connected_4_graphlets(memberGraph, "", size_4_labels);
    graphlet5CountsABS = count_connected_5_graphlets(memberGraph);
    
    unlabeled_abs_counts_computed = true;
    
}

void GraphletCounts::compute_unlabeled_norm_counts() {
    
    if (!unlabeled_abs_counts_computed) {compute_unlabeled_abs_counts();}
    float total = float (get_total_counts());
    
    graphlet2CountsNormalized = normalize_counts(graphlet2CountsABS,total);
    graphlet3CountsNormalized = normalize_counts(graphlet3CountsABS,total);
    graphlet4CountsNormalized = normalize_counts(graphlet4CountsABS,total);
    graphlet5CountsNormalized = normalize_counts(graphlet5CountsABS,total);
    
    norm_counts_computed = true;
}



void GraphletCounts::compute_all_counts() {
    const string logFileName = logs_path + "count_graphlets_" + graphName + ".log";
    
    if( ! silent) {
        cout << apptag << "  Counting graphlets.\n";
    }
    
    if (print) {
        logFile.open(logFileName.c_str());
        if (!logFile.is_open()) {
            cout << apptag << "ERROR: could not open log file.\n";
        }
    }
    
    compute_unlabeled_abs_counts();
    compute_unlabeled_norm_counts();


 
   
    
    if (print) { logFile.close(); }
    
    if( ! silent) {
        cout << apptag << "    Graphlet counts for the protein graph " << graphName << " computed.\n";   
    }
    long unsigned int numGraphletsTypesCounted;

    numGraphletsTypesCounted = graphlet3CountsABS.size() + graphlet4CountsABS.size() + graphlet5CountsABS.size() + cl.size();
    if( ! silent) {
        cout << apptag << "    Counted " << graphlet3CountsABS.size() << " different 3-graphlets, " << graphlet4CountsABS.size() << " different 4-graphlets, " << graphlet5CountsABS.size() << " different 5-graphlets and " << cl.size() << " different labeled graphlets (" << numGraphletsTypesCounted << " total).\n";           
    }

    else {
        numGraphletsTypesCounted = graphlet3CountsABS.size() + graphlet4CountsABS.size() + graphlet5CountsABS.size();
        if( ! silent) {
            cout << apptag << "    Counted " << graphlet3CountsABS.size() << " different 3-graphlets, " << graphlet4CountsABS.size() << " different 4-graphlets and " << graphlet5CountsABS.size() << " different 5-graphlets (" << numGraphletsTypesCounted << " total).\n";   
        }
    }
    
    all_counts_computed = true;
    labeled_norm_counts_computed = true;
}

/**
 * When 'withLabeled' is TRUE, the function normalizes all
 * graphlet counts by the absolute amount of counted graphlets.
 * Otherwise it normalizes the the unlabeled graphlets only by the 
 * counts of the unlabeled graphlets.
 * @param <vector<int>> the vector to be normalized
 * @param <float> the number to which the vector should be normalized, normally the total number of counts
 */
std::vector<float> GraphletCounts::normalize_counts(std::vector<int> absCounts, float max) {
    
    // calculate the normalization for the given unlabeled vector
    std::vector<float> normalizedCounts = std::vector<float>();
    if (max == 0.0) {std::cerr << "WARNING: division by zero!!";}
    
    for (int i = 0; i<absCounts.size(); i++) {
        
        normalizedCounts.push_back((float (absCounts[i]))/max);
        
    }
    
    return normalizedCounts;

}


/**
 * Creates the graphlet counts given in the input vector c, properly formated.
 * @param c the input vector
 * @param asVector if true, they are printed in vector notation
 * @return a string which contains the formatted graphlet counts in c
 */
string GraphletCounts::print_counts(vector<int>& c, bool asVector) {
    stringstream sstream;

    if (asVector) {
        sstream << "(" << setw(3) << c[0];
        for (int i = 1; i < c.size(); i++) {
            sstream << "," << setw(3) << c[i];
        }
        sstream << " )\n";
        return sstream.str();
        
    } else {
        for (int i = 0; i < c.size(); i++) {
            sstream << " g" << i + 1 << " = " << c[i] << endl;
        }
        sstream << endl;
        return sstream.str();
    }
}


/**
 * Graphlet counting algorithm for connected 3-graphlets by N. Shervashidze, implementation by Tatiana.
 * Extensions for labeled graphlet and bio-graphlets by Tatiana.
 * Revised for support of more labeled graphlets by ben.
 * @param <Graph> g - the input graph
 * @param <string> label - the property under which the labels are stored
 * @param <vector<string>> label_vector - vector containing the labels to look for
 * @return a vector of graphlet counts (how often each graphlet was found)
 */
 vector<int> GraphletCounts::count_connected_3_graphlets(Graph& g, std::string label, std::vector<std::vector<string>> labelVector) { 
    vector<float> c3;
    c3 = vector<float>(2);
    float count[] = { 0.0, 0.0 };
    float w[]     = { 1/6.0, 1/2.0 };
    
    memberGraph = g;
    unlabeled_abs_counts_computed = false;
    norm_counts_computed = false;
    labeled_norm_counts_computed = false;
    all_counts_computed = false;
    size_3_labels = labelVector;
    three_counts = false;
    
    print = false; // printing might disturb tests

    
    
    
    
    
    int numelem = 12;
    float lcount[numelem];
    memset(lcount, 0.0, sizeof lcount);
    
    // symmetrical patterns will be counted two times
    float lw[] = { 1/6.0,  1/2.0,  1/2.0,  1/6.0,                  // labeled g1
                   1/2.0,  1.0,    1/2.0,  1/2.0,  1.0,  1/2.0,    // labeled g2
                   1.0,    1.0 };                                // labeled bio
  
    EdgeDescriptor e;
    string pattern;
    
    std::vector<std::vector<float>> labeled_3_counts_float = vector<vector<float>>();
    
    // set space for vector with labeled counts
    for (auto i : labelVector) {
        std::vector<float> fcount = std::vector<float>(i.size());
        for (int k = 0; k< i.size(); k++) {
            fcount[k] = 0.0;
        }
        labeled_3_counts_float.push_back(fcount);
        
        
    }
    
    std::vector<std::string> triangle_labels;
    std::vector<std::string> path_labels;
    
    
    if (!labelVector.empty()) {
        triangle_labels = labelVector[0];
        path_labels = labelVector[1];
    }
    
	
	int printDetails = 0; // might disturb tests, therefore set to zero
                                // was 1 before
	
    if (print) logFile << "Iterations for 3-graphlet counting:\n";
    VertexIterator    i, i_last;
    AdjacencyIterator j, j_last, 
                      k, k_last; 
    
    for (tie(i, i_last) = vertices(g); i != i_last; ++i) { 
        if (print) logFile << "i = " << *i << endl;
        
        // counting graphlets that have path of length 3
        for (tie(j, j_last) = adjacent_vertices(*i, g); j != j_last; ++j) {
            if (print) logFile << "----- j = " << *j << endl;
            
            for (tie(k, k_last) = adjacent_vertices(*j, g); k != k_last; ++k) {

                if (*k != *i) {
                    if (print) logFile << "----------- k = " << *k << endl;

                    if (edge(*i, *k, g).second) {
                        count[0]++;
						if(printDetails) { cout << apptag << "Found G0: " << g[*i].properties["label"] << "-" << g[*j].properties["label"] << "-" << g[*k].properties["label"] << ".\n"; }
                        if (print) { logFile << "-------------------> g1\n"; }
                        
                        if (!labelVector.empty()) {                           
                            pattern = "";
                            pattern = pattern + g[*i].properties[label] + g[*j].properties[label] + g[*k].properties[label];
                            
                            
                            
                            for (int i = 0; i < triangle_labels.size(); i++) {
                                
                                std::set<std::string> cats = compute_CAT(triangle_labels[i]);
                                
                                for (auto k : cats) {
                                    
                                    
                                    if (k.size() != 3) {std::cerr << "WARNING: pattern of wrong length detected. Length should be 3, but is " << k.size(); }
                                    if (pattern.compare(k) == 0) {
                                        
                                        labeled_3_counts_float[0][i] += w[0];
                                    }
                                }
                            }
                            
                            
                            
                            
                            // NOTE: optional part, can be for further bio-graphlets
                            pattern = "";
                            e = edge(*i, *j, g).first;
                            pattern = pattern + g[*i].properties[label] + g[e].properties[label];
                            e = edge(*j, *k, g).first;
                            pattern = pattern + g[*j].properties[label] + g[e].properties["spatial"] + g[*k].properties[label];
                            
                            //cout             << pattern << " \n";                           
                            if (print) { logFile << pattern << " \n"; }
                         }                       
                    } else {
                        count[1]++;
						
                        
                        if (!labelVector.empty()) {                            
                            pattern = "";
                            pattern = pattern + g[*i].properties[label] + g[*j].properties[label] + g[*k].properties[label];
                            
                            ;
                            
                            for (int i = 0; i < path_labels.size(); i++) {
                                
                                std::set<std::string> words = reverse_string(path_labels[i]);
                                
                                for (auto k : words) {
                                    
                                    if (pattern.compare(k) == 0) {
                                        
                                        labeled_3_counts_float[1][i] += w[1];
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    c3[0] = count[0] * w[0];
    c3[1] = count[1] * w[1];
    
    vector<int> vec0 = vector<int>();
    vector<int> vec1 = vector<int>();
    if (!labelVector.empty()) {
//        for (int i = 0; i < 10; i++) {
//            cl[i] = lcount[i] * lw[i];
//            labeled_abs_counts[i] = int (floor (cl[i]));
//        }
//        cl[20] = lcount[10] * lw[10];
//        cl[21] = lcount[11] * lw[11];
//        
//        labeled_abs_counts[20] = int (floor (cl[20]));
//        labeled_abs_counts[21] = int (floor (cl[21]));
//        
        
        // pushing the computed values into the vector
        for (int i = 0; i<labeled_3_counts_float[0].size(); i++) {
            vec0.push_back(int (floor (labeled_3_counts_float[0][i])));
            
        }
        
        labeled_3_countsABS.push_back(vec0);
        
        for (int i = 0; i<labeled_3_counts_float[1].size(); i++) {
            
            vec1.push_back(int (floor (labeled_3_counts_float[1][i])));
        }
        
        labeled_3_countsABS.push_back(vec1);

    }
    
    vector<int> graphlet3Counts;
    graphlet3Counts = vector<int>();
    
    
    for (auto i = c3.begin(); i != c3.end(); i++) {
        int count = int (floor(*i));
        graphlet3Counts.push_back(count);
        num3 = num3 + count;
        
    }
    
    
    

    

    
    if (print) logFile << "\n"
                       << "Number of 3-graphlets:\n"
                       << print_counts(graphlet3Counts, false);
    
    three_counts = true;
    return graphlet3Counts;    
}


/**
 * Graphlet counting algorithm for the connected 2-graphlet, implementation by Tim.
 * Extensions for labeled graphlet and bio-graphlets by Tim.
 * Revised for support of more labeled graphlets by ben.
 * @param <Graph>g the input graph
 * @param <string> label - the property in which the labels of the label_vector are stored
 * @param <vector<string>> label_vector - vector, containing the labels to look for
 * @return a vector of graphlet counts (how often each graphlet was found)
 */

vector<int> GraphletCounts::count_connected_2_graphlets(Graph& g,std::string label, vector<string> label_vector) { 
    vector<float> c2;
    c2 = vector<float>(1);   // The only 2-graphlet is a path of length 1 (one edge).
    float count[] = { 0.0 };
    float w[]     = { 1/2.0 };  // each edge will be found twice (from both vertices it connects))
    
    
    
    memberGraph = g;
    unlabeled_abs_counts_computed = false;
    norm_counts_computed = false;
    all_counts_computed = false;
    size_2_labels = label_vector;
    labeled_2_countsABS = vector<int>(label_vector.size());
    two_counts = false;
    
    
    
    
    
    int numelem = label_vector.size();
    float lcount[numelem];
    memset(lcount, 0.0, sizeof lcount);
    
    // symmetrical patterns will be counted two times
    float lw[] = { 1/2.0,  1/2.0,  1/2.0, 1/2.0 }; // "HH", "EH", , "HE", "EE"
  
    EdgeDescriptor e;
    string pattern;
    
    // all possible labeling of edge (2-vertex path of length 1) graphlet (triangle) concerning the symmetry
    string g0_vertex_patterns[] = { "HH", "EH", "HE", "EE" };

    if (print) logFile << "Iterations for 2-graphlet counting:\n";
    VertexIterator    i, i_last;
    AdjacencyIterator j, j_last; 
    
    for (tie(i, i_last) = vertices(g); i != i_last; ++i) { 
        if (print) {
            logFile << "i = " << *i << endl;
        }
        
        //counting graphlets that have path of length 3
        for (tie(j, j_last) = adjacent_vertices(*i, g); j != j_last; ++j) {
            
            if (print) {
                logFile << "----- j = " << *j << endl;                                    
            }
            
            if (*j != *i) {

                if (edge(*i, *j, g).second) {
                    count[0]++;
                    if (print) logFile << "-------------------> g0\n";

                    if (!label_vector.empty()) {                           
                        pattern = "";
                        pattern = pattern + g[*i].properties[label] + g[*j].properties[label];

  
                        for (int i = 0; i < label_vector.size(); i++) {
                            
                            std::set<string> cats = compute_CAT(label_vector[i]);
                            
                            for (auto k : cats) {
                                
                                if (k.size() != 2) {std::cerr << "WARNING: pattern of wrong length detected. Length should be 2, but is " << k.size(); }
                                if (pattern.compare(k) == 0) {
                                    labeled_2_countsABS[i] += 1;
                                    
                                }
                                
                            }
                        }
                        if (print) logFile << pattern << " \n";
                     }                       
                } 
            }
        }
    }

    c2[0] = count[0] * w[0];
    
    

    if (!label_vector.empty()) {
        for (int i = 0; i < numelem; i++) {
            cl[i] = lcount[i] * lw[i];
            //labeled_abs_counts[i] = int (floor (cl[i]));
            labeled_2_countsABS[i] = labeled_2_countsABS[i]/2;
        }
    }
    
    
    vector<int> graphlet2Counts;
    graphlet2Counts = vector<int>();
    
    
    graphlet2Counts.push_back(int (floor(c2[0])));
    num2 = graphlet2Counts[0];
    
    if (print) logFile << "\n"
                       << "Number of 2-graphlets:\n"
                       << print_counts(graphlet2Counts, false);
    
    two_counts = true;
    
    return graphlet2Counts; 

}

/**
 * Graphlet counting algorithm for connected 4-graphlets by N. Shervashidze, implementation by Tatiana.
 * Extensions for labeled graphlet and bio-graphlets by Tatiana.
 * Revised by ben.
 * @param g the input graph
 * @param <string> label - the property in which the labels of the label_vector are stored
 * @param <vector<string>> label_vector - vector, containing the labels to look for
 * @return a vector of graphlet counts (how often each graphlet was found)
 */
vector<int> GraphletCounts::count_connected_4_graphlets(Graph& g, std::string label, std::vector<std::vector<std::string>> label_vector) {    
    
    

    // resetting class attributes
    memberGraph = g;
    unlabeled_abs_counts_computed = false;
    norm_counts_computed = false;
    labeled_norm_counts_computed = false;
    all_counts_computed = false;
    size_4_labels = label_vector;
    four_counts = false;
    
    print = false; // printing might disturb tests
    
    vector<float> c4;
    c4 = vector<float>(6);
    float count[] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
    float w[]     = { 1/24.0, 1/12.0, 1/4.0, 1, 1/8.0, 1/2.0 };
    int aux = 0;
    
    int numelem = 12;
    float lcount[numelem];
    memset(lcount, 0.0, sizeof lcount);
    
    // symmetrical patterns will be counted two times
    float lw[] = { 1/2.0,  1.0,  1/2.0,                    // labeled g6
                   1.0,    1.0,  1.0,    1.0,
                   1/2.0,  1.0,  1/2.0,    
                   1.0,    1/2.0 };                                // labeled bio
  
    EdgeDescriptor e;
    string pattern;
    
    // all possible labeling of g6 graphlet (3-path) concerning the symmetry
    string g6_vertex_patterns[] = { "HHHH","HHHE",       "EHHE",
                                    "HHEH","HHEE","EHEH","EHEE",
                                    "HEEH","HEEE",       "EEEE"};
    
    // bio-motivated labeling of graphlets
    // currently implemented is the greek key and 4-beta-barrel motifs
    // NOTE: also check if its composing vertices are adjacent for greek key
    //       no adjacency check for 4-beta-barrel motif since there are possible SSEs in between 
    string g6_bio_patterns[]    = { "EaEaEaE",  // greek key
                                    "EpEpEpE"}; // 4-beta-barrel, non-adjacent
    
    /*
     * NOTE the correspondence 
     *   lcount[0..9] := g6_vertex_patterns[0..5]
     *   lcount[10]   := g6_bio_patterns[0]
     */
    
    
    std::vector<std::string> pathlabels;
    std::vector<std::set<std::string>>  mirrored_labels;
    std::set<std::string> mir;
    
    if (!label_vector.empty()) {
        labeled_4_countsABS = std::vector<std::vector<int>>(6);
        pathlabels = label_vector[5];
        mirrored_labels = std::vector<std::set<std::string>>();
        mir = std::set<std::string>();
        std::vector<int> vec = std::vector<int>(pathlabels.size());
        labeled_4_countsABS[5] = vec;
        for (int i = 0; i < pathlabels.size(); i++) {
        
            mir = reverse_string(pathlabels[i]);
            mirrored_labels.push_back(mir);
            
        }
    }
    
    
    
    
    
    
    if (print) logFile << "Iterations for 4-graphlet counting:\n";
    VertexIterator    i, i_last;
    AdjacencyIterator j, j_last, 
                      k, k_last, 
                      l, l_last;

    for (tie(i, i_last) = vertices(g); i != i_last; ++i) { 
        if (print) logFile << "i = " << *i << endl;
        
        // counting graphlets that have path of length 3
        for (tie(j, j_last) = adjacent_vertices(*i, g); j != j_last; ++j) {
            if (print) logFile << "----- j = " << *j << endl;
            
            for (tie(k, k_last) = adjacent_vertices(*j, g); k != k_last; ++k) {

                if (*k != *i) {
                    if (print) logFile << "----------- k = " << *k << endl;

                    for (tie(l, l_last) = adjacent_vertices(*k, g); l != l_last; ++l) {

                        if ((*l != *i) && (*l != *j)) {
                            if (print) logFile << "------------------ l = " << *l << endl;
                            aux = edge(*i, *k, g).second + edge(*i, *l, g).second + edge(*j, *l, g).second;
                            switch (aux) {
                                case 3:
                                    count[0]++;
                                    if (print) logFile << "--------------------------> g1\n";
                                    break;
                                case 2:
                                    count[1]++;
                                    if (print) logFile << "--------------------------> g2\n";
                                    break;
                                case 1:
                                    if (edge(*i, *l, g).second) {
                                        count[4]++;
                                        if (print) logFile << "--------------------------> g5\n";                                     
                                    } else {
                                        count[2]++;
                                        if (print) logFile << "--------------------------> g3\n";
                                    }
                                    break;
                                case 0:
                                    count[5]++;
                                    if (print) logFile << "--------------------------> g6\n";
                                        
                                    if (!label_vector.empty()) {
                                        pattern = "";
                                        pattern = pattern + g[*i].properties[label] + g[*j].properties[label] + g[*k].properties[label] + g[*l].properties[label];
                                        
                                        std::set<std::string> mirl; 
                                        
                                        for (int i = 0; i < mirrored_labels.size(); i++) {
                                            
                                            mirl = mirrored_labels[i];
                                            
                                            for (auto k : mirl) {
                                                
                                                if (k.size() != 4) {std::cerr << "WARNING: pattern of wrong length detected. Length should be 4, but is " << k.size(); }
                                                if (pattern.compare(k) == 0) {
                                                    labeled_4_countsABS[5][i] +=1;
                                                }
                                            }
                                            
                                        }


                                        pattern = "";
                                        e = edge(*i, *j, g).first;
                                        pattern = pattern + g[*i].properties[label] + g[e].properties["spatial"];
                                        e = edge(*j, *k, g).first;
                                        pattern = pattern + g[*j].properties[label] + g[e].properties["spatial"];
                                        e = edge(*k, *l, g).first;
                                        pattern = pattern + g[*k].properties[label] + g[e].properties["spatial"] + g[*l].properties[label];

                                        if (((*i - *l) == 1) && ((*l - *k) == 1) && ((*k - *j) == 1) && (pattern == g6_bio_patterns[0])) {
                                            lcount[10]++;
                                            if (print) logFile << "greek key motif: ";
                                        }
                                        
                                        if (pattern == g6_bio_patterns[1]) {
                                            lcount[11]++;
                                            if (print) logFile << "4-beta-barrel motif: ";
                                        }

                                        //cout             << pattern << " \n";                           
                                        if (print) logFile << pattern << " \n";
                                    }
                                    
                                    break;
                                default: 
                                    logFile <<"ERROR while computing aux variable!\n";
                            }
                        }
                    }
                }
            }
        }
        
        // counting "star"-graphlets
        // NOTE: loop boundaries were modified
        for (tie(j, j_last) = adjacent_vertices(*i, g); j != j_last; ++j) {

            for (tie(k, k_last) = adjacent_vertices(*i, g); k != k_last; ++k) {

                if (*k > *j) {

                    for (tie(l, l_last) = adjacent_vertices(*i, g); l != l_last; ++l) {

                        if ((*l > *j) && (*l > *k)) {
                            aux = edge(*j, *k, g).second + edge(*k, *l, g).second + edge(*l, *j, g).second;
                            
                            if (aux == 0) {
                                count[3]++;
                                if (print) logFile << "i = " << *i << " j = " << *j << " k = " << *k << " l = " << *l << endl;
                                if (print) logFile << "*-------------------------> g4" << endl;
                            }
                        }
                    }
                }
            }
        }
    }
    
    for (int i = 0; i < c4.size(); i++) {
        c4[i] = count[i] * w[i];;
    }

    if (!label_vector.empty()) {
        
        for (int i = 0; i < labeled_4_countsABS[5].size(); i++) {
            labeled_4_countsABS[5][i] = labeled_4_countsABS[5][i]/2;
        }
        
    }

    vector<int> graphlet4Counts;
    graphlet4Counts = vector<int>();
    
    
    for (auto i = c4.begin(); i != c4.end(); i++) {
        int count = int (floor(*i));
        graphlet4Counts.push_back(count);
        num4 = num4 + count;
    }
    
    if (print) logFile << "\n"
                       << "Number of 4-graphlets:\n"
                       << print_counts(graphlet4Counts, false);
    
    four_counts = true;
    return graphlet4Counts; 
}


/**
 * Graphlet counting algorithm for connected 5-graphlets by N. Shervashidze, implementation by Tatiana.
 * Extensions for labeled graphlet and bio-graphlets by Tatiana.
 * @param g the input graph
 * @param withLabeled whether to count labeled graphlets as well
 * @return a vector of graphlet counts (how often each graphlet was found)
 */

vector<int> GraphletCounts::count_connected_5_graphlets(Graph& g) {    
    
    // resetting class attributes
    memberGraph = g;
    unlabeled_abs_counts_computed = false;
    norm_counts_computed = false;
    labeled_norm_counts_computed = false;
    all_counts_computed = false;
    five_counts = false;
    
    print = false; // printing might disturb tests
    
    vector<float> c5;
    c5 = vector<float>(21);
    float count[21];
    memset(count, 0.0, sizeof count);
    float w[]     = { 1/120.0, 1/72.0, 1/48.0, 1/36.0, 1/28.0, 1/20.0, 1/14.0, 1/10.0, 1/12.0, 1/8.0,
                      1/8.0,   1/4.0,  1/2.0,  1/12.0, 1/12.0, 1/4.0,  1/4.0,  1/2.0,  1,      1/2.0,   1};
    int aux = 0;
    int deg_unsort[] = { 0, 0, 0, 0, 0 };
    int deg[]        = { 0, 0, 0, 0, 0 };
    
    //  deg_g1
    //  deg_g2
    int deg_g3[]  = { 3, 3, 3, 3, 4 };
    int deg_g4[]  = { 2, 3, 3, 4, 4 };
    int deg_g5[]  = { 2, 3, 3, 3, 3 };
    int deg_g6[]  = { 2, 2, 3, 3, 4 };
    int deg_g7[]  = { 2, 2, 2, 3, 3 }; // deg_g7 == deg_g15
    int deg_g8[]  = { 2, 2, 2, 2, 2 };
    int deg_g9[]  = { 1, 3, 3, 3, 4 };
    int deg_g10[] = { 1, 2, 3, 3, 3 };
    
    int deg_g11[] = { 2, 2, 2, 2, 4 };
    int deg_g12[] = { 1, 2, 2, 2, 3 }; // deg_g12 == deg_g17
    //  deg_g13
    int deg_g14[] = { 2, 2, 2, 4, 4 };
    int deg_g15[] = { 2, 2, 2, 3, 3 }; // deg_g7 == deg_g15
    int deg_g16[] = { 1, 2, 2, 3, 4 };
    int deg_g17[] = { 1, 2, 2, 2, 3 }; // deg_g12 == deg_g17
    int deg_g18[] = { 1, 1, 2, 3, 3 };
    //  deg_g19
    //  deg_g20
    //  deg_g21
    
    int index1, index2;
    
    VertexIterator    i, i_last;
    AdjacencyIterator j, j_last, 
                      k, k_last, 
                      l, l_last,
                      m, m_last;
    
    if (print) logFile << "Iterations for 5-graphlet counting:\n";

    for (tie(i, i_last) = vertices(g); i != i_last; ++i) { 
        if (print) logFile << "i = " << *i << endl;
        
        // counting graphlets that have path of length 4
        for (tie(j, j_last) = adjacent_vertices(*i, g); j != j_last; ++j) {
            if (print) logFile << "----- j = " << *j << endl;
            
            for (tie(k, k_last) = adjacent_vertices(*j, g); k != k_last; ++k) {

                if (*k != *i) {
                    if (print) logFile << "----------- k = " << *k << endl;

                    for (tie(l, l_last) = adjacent_vertices(*k, g); l != l_last; ++l) {

                        if ((*l != *i) && (*l != *j)) {
                            if (print) logFile << "------------------ l = " << *l << endl;

                            for (tie(m, m_last) = adjacent_vertices(*l, g); m != m_last; ++m) {

                                if ((*m != *i) && (*m != *j) && (*m != *k)) {
                                    if (print) logFile << "------------------------- m = " << *m << endl;

                                    aux = edge(*i, *k, g).second + edge(*i, *l, g).second + edge(*i, *m, g).second +
                                          edge(*j, *l, g).second + edge(*j, *m, g).second + edge(*k, *m, g).second;
                                    
                                    // NOTE: nodes degree computation and comparison were slightly modified        
                                    deg_unsort[0] = edge(*i, *k, g).second + edge(*i, *l, g).second + edge(*i, *m, g).second + 1;
                                    deg_unsort[1] = edge(*j, *l, g).second + edge(*j, *m, g).second + 2;
                                    deg_unsort[2] = edge(*k, *i, g).second + edge(*k, *m, g).second + 2;
                                    deg_unsort[3] = edge(*l, *i, g).second + edge(*l, *j, g).second + 2;
                                    deg_unsort[4] = edge(*m, *i, g).second + edge(*m, *j, g).second + edge(*m, *k, g).second + 1;
                                    
                                    memcpy(deg, deg_unsort, sizeof (deg_unsort));
                                    sort(deg, deg + 5);
                                    if (print) {
                                        logFile << "--------------------------------- deg_unsort = ";
                                        for (int z = 0; z < 5; z++) logFile << deg_unsort[z] << " ";
                                        logFile << endl;
                                        logFile << "--------------------------------- deg        = ";
                                        for (int z = 0; z < 5; z++) logFile << deg[z] << " ";
                                        logFile << "-----> ";
                                    }
                                    
                                    // optional TODO: improve index1 and index2 computation
                                    switch (aux) {
                                        
                                        case 6: // g1
                                            count[0]++;
                                            if (print) logFile << "g1\n";
                                            break;
                                            
                                        case 5: // g2
                                            count[1]++;
                                            if (print) logFile << "g2\n";
                                            break;   
                                            
                                        case 4: // g3, g4
                                            if (print) logFile << "CASE 4: "; 
                                            if (memcmp(deg, deg_g3, 5 * sizeof(int)) == 0) {
                                                count[2]++;
                                                if (print) logFile << "g3\n";
                                            } else if (memcmp (deg, deg_g4, 5 * sizeof(int)) == 0) {
                                                count[3]++;
                                                if (print) logFile << "g4\n";
                                            } else {
                                                logFile << "ERROR: in the g3 and g4 counting!\n";
                                            }
                                            break;  
                                            
                                        case 3: // g5, g6, g9, g14
                                            if (print) logFile << "CASE 3: ";
                                            if (memcmp(deg, deg_g5, 5 * sizeof (int)) == 0) {
                                                count[4]++;
                                                if (print) logFile << "g5\n";
                                            } else if (memcmp(deg, deg_g6, 5 * sizeof (int)) == 0) {
                                                count[5]++;
                                                if (print) logFile << "g6\n";
                                            } else if (memcmp(deg, deg_g9, 5 * sizeof (int)) == 0) {
                                                count[8]++;
                                                if (print) logFile << "g9\n";
                                            } else if (memcmp(deg, deg_g14, 5 * sizeof (int)) == 0) {
                                                count[13]++;
                                                if (print) logFile << "g14\n";
                                            } else {
                                                logFile << "ERROR: in the g5, g6, g9 and g14 counting!\n";
                                            }
                                            break;
                                            
                                        case 2: // g7, g10, g11, g15, g16
                                            if (print) logFile << "CASE 2: ";
                                            if (memcmp(deg, deg_g10, 5 * sizeof (int)) == 0) {
                                                count[9]++;
                                                if (print) logFile << "g10\n";
                                            } else if (memcmp(deg, deg_g11, 5 * sizeof (int)) == 0) {
                                                count[10]++;
                                                if (print) logFile << "g11\n";
                                            } else if (memcmp(deg, deg_g16, 5 * sizeof (int)) == 0) {
                                                count[15]++;
                                                if (print) logFile << "g16\n";
                                            } else if (memcmp(deg, deg_g7, 5 * sizeof (int)) == 0) { // g7 or g15
                                                index1 = -1;
                                                index2 = -1;
                                                for (int z = 0; z < 5; z++) {
                                                    if (deg_unsort[z] == 3) {
                                                        if (index1 == -1) index1 = z;
                                                        else             index2 = z;
                                                    }
                                                }
                                                switch (index1) {
                                                    case 0:  index1 = *i; break;
                                                    case 1:  index1 = *j; break;
                                                    case 2:  index1 = *k; break;
                                                    case 3:  index1 = *l; break;
                                                    case 4:  index1 = *m; break;
                                                    default: logFile << "ERROR while index1 computation in g7, g15\n";
                                                }
                                                switch (index2) {
                                                    case 0:  index2 = *i; break;
                                                    case 1:  index2 = *j; break;
                                                    case 2:  index2 = *k; break;
                                                    case 3:  index2 = *l; break;
                                                    case 4:  index2 = *m; break;                                                    
                                                    default: logFile << "ERROR while index2 computation in g7, g15\n";
                                                }
                                                if (print) logFile << "edge " << edge(index1, index2, g).first << "? " 
                                                                << edge(index1, index2, g).second;
                                               if (edge(index1, index2, g).second) { // --> g7
                                                    count[6]++;
                                                    if (print) logFile << " --> g7\n";
                                                } else { // --> g15
                                                    count[14]++;
                                                    if (print) logFile << " --> g15\n";
                                                }
                                            } else {
                                                logFile << "ERROR: in the g7, g10, g11, g15 and g16 counting!\n";
                                            } 
                                            break;
                                        case 1: // g8, g12, g17, g18
                                            if (print) logFile << "CASE 1: ";
                                             if (memcmp(deg, deg_g8, 5 * sizeof (int)) == 0) {
                                                count[7]++;
                                                if (print) logFile << "g8\n";
                                            } else if (memcmp(deg, deg_g18, 5 * sizeof (int)) == 0) {
                                                count[17]++;
                                                if (print) logFile << "g18\n";
                                            } else if (memcmp(deg, deg_g12, 5 * sizeof (int)) == 0) { // g12 or g17
                                                for (int z = 0; z < 5; z++) {
                                                    if (deg_unsort[z] == 1)      index1 = z;
                                                    else if (deg_unsort[z] == 3) index2 = z;
                                                }
                                                switch (index1) {
                                                    case 0:  index1 = *i; break;
                                                    case 1:  index1 = *j; break;
                                                    case 2:  index1 = *k; break;
                                                    case 3:  index1 = *l; break;
                                                    case 4:  index1 = *m; break;                                                    
                                                    default: logFile << "ERROR while index1 computation in g12, g17\n";
                                                }
                                                switch (index2) {
                                                    case 0:  index2 = *i; break;
                                                    case 1:  index2 = *j; break;
                                                    case 2:  index2 = *k; break;
                                                    case 3:  index2 = *l; break;
                                                    case 4:  index1 = *m; break;                                                    
                                                    default: logFile << "ERROR while index2 computation in g12, g17\n";
                                                }
                                                if (print) logFile << "edge " << edge(index1, index2, g).first << "? " 
                                                                << edge(index1, index2, g).second;
                                                if (edge(index1, index2, g).second) { // --> g17
                                                    count[16]++;
                                                    if (print) logFile << " --> g17\n";
                                                } else { // --> g12
                                                    count[11]++;
                                                    if (print) logFile << " --> g12\n";
                                                }
                                            } else {
                                                logFile << "ERROR: in the g8, g12, g17 and g18 counting!\n";
                                            }
                                            break;
                                        case 0: // g13
                                            count[12]++;
                                            if (print) logFile << "g13\n";
                                            break;                                        
                                        default:
                                            logFile << "ERROR while computing aux variable!\n";
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // counting g20
        for (tie(j, j_last) = adjacent_vertices(*i, g); j != j_last; ++j) {
            if (print) logFile << "----- j = " << *j << endl;
            
            for (tie(k, k_last) = adjacent_vertices(*j, g); k != k_last; ++k) {

                if ((*k != *i) && (!edge(*i, *k, g).second)) {
                    if (print) logFile << "----------- k = " << *k << endl;

                    for (tie(l, l_last) = adjacent_vertices(*k, g); l != l_last; ++l) {

                        if ((*l != *i) && (*l != *j) && (!edge(*i, *l, g).second) && (!edge(*j, *l, g).second)) {
                            if (print) logFile << "------------------ l = " << *l << endl;

                            for (tie(m, m_last) = adjacent_vertices(*k, g); m != m_last; ++m) {

                                if ((*m != *i) && (*m != *j) && (*m != *l)
                                               && (!edge(*i, *m, g).second) && (!edge(*j, *m, g).second) && (!edge(*l, *m, g).second)) {
                                    if (print) logFile << "------------------------- m = " << *m << endl;
                                    if (print) logFile << "--------------------------------------> g20\n";
                                    count[19]++;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // counting g19 and g21
        // NOTE: loop borders were modified
        for (tie(j, j_last) = adjacent_vertices(*i, g); j != j_last; ++j) {

            for (tie(k, k_last) = adjacent_vertices(*i, g); k != k_last; ++k) {

                if (*k > *j) {

                    for (tie(l, l_last) = adjacent_vertices(*i, g); l != l_last; ++l) {

                        if ((*l > *j) && (*l > *k)) {
                            
                            for (tie(m, m_last) = adjacent_vertices(*i, g); m != m_last; ++m) {

                                if ((*m > *j) && (*m > *k) && (*m > *l)) {

                                    aux = edge(*j, *k, g).second + edge(*j, *l, g).second + edge(*j, *m, g).second +
                                          edge(*k, *l, g).second + edge(*k, *m, g).second + edge(*l, *m, g).second;
                                    
                                    if (aux == 1) { // --> g19
                                        count[18]++;
                                        if (print) logFile <<  "i = " << *i << " j = " << *j << " k = " << *k 
                                                        << " l = " << *l << " m = " << *m << endl;
                                        if (print) logFile << " -------------------------> g19\n";
                                    } else if (aux == 0){ // --> g21
                                        count[20]++;
                                        if (print) logFile <<  "i = " << *i << " j = " << *j << " k = " << *k 
                                                        << " l = " << *l << " m = " << *m << endl;
                                        if (print) logFile << " -------------------------> g21\n";    
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }    
    
    for (int i = 0; i < c5.size(); i++) {
        c5[i] = count[i] * w[i];
    }
    
    
    
    vector<int> graphlet5Counts;
    graphlet5Counts = vector<int>();
    
    
    
    for (auto i = c5.begin(); i != c5.end(); i++) {
        int count = int (floor(*i));
        graphlet5Counts.push_back(count);
        num5 = num5 + count;
    }
    
    if (print) logFile << "\n"
                       << "Number of 5-graphlets:\n"
                       << print_counts(graphlet5Counts, false);
    
    five_counts = true;
    return graphlet5Counts; 
}


std::vector<std::vector<int>> GraphletCounts::get_unlabeled_abs_counts() {
    
    std::vector<std::vector<int>> abs_counts_vector = std::vector<std::vector<int>>();
    
    if (!unlabeled_abs_counts_computed) {
        compute_unlabeled_abs_counts();
        
    }
    
    abs_counts_vector.push_back(graphlet2CountsABS);
    abs_counts_vector.push_back(graphlet3CountsABS);
    abs_counts_vector.push_back(graphlet4CountsABS);
    abs_counts_vector.push_back(graphlet5CountsABS);
    
    return abs_counts_vector;
    
}

vector<vector<float>> GraphletCounts::get_normalized_counts() {
    
    vector<vector<float>> norm_counts_vector = vector<vector<float>>();
    
    if (!norm_counts_computed) {
        compute_unlabeled_norm_counts();
    }
    
    norm_counts_vector.push_back(graphlet2CountsNormalized);
    norm_counts_vector.push_back(graphlet3CountsNormalized);
    norm_counts_vector.push_back(graphlet4CountsNormalized);
    norm_counts_vector.push_back(graphlet5CountsNormalized);
    
    return norm_counts_vector;
}


vector<int> GraphletCounts::get_labeled_2_countsABS(std::string label, std::vector<std::string> label_vector) {
    
    
    if (size_2_labels == label_vector) {
        
        if (labeled_2_countsABS.empty()) {
            count_connected_2_graphlets(memberGraph, label, label_vector);
        }
            
    } else {
            count_connected_2_graphlets(memberGraph,label, label_vector);
    }
    
    return labeled_2_countsABS;
    
}

/* Finds all cyclic permutations for a given word */
set<string> GraphletCounts::compute_CAT(string word) {
    
    std::set<std::string> words = std::set<std::string>();
    string word2 = word + word;
    string cat_word = "";
    
    for (int i = 0; i < word.size(); i++) {
        cat_word = word2.substr (i, word.size());
                
        words.insert(cat_word);
    }
    
    return words;
}

vector<vector<int>> GraphletCounts::get_labeled_3_countsABS(std::string label, std::vector<std::vector<std::string>> label_vector) {
    
    if (size_3_labels == label_vector) {
        
        if (labeled_3_countsABS.empty()) {
            count_connected_3_graphlets(memberGraph, label, label_vector);
        }
    } else {
        count_connected_3_graphlets(memberGraph, label, label_vector);
    }

    return labeled_3_countsABS;
}

vector<vector<int>> GraphletCounts::get_labeled_4_countsABS(std::string label, std::vector<std::vector<std::string>> label_vector) {
    
    if (size_4_labels == label_vector) {
        
        if (labeled_4_countsABS.empty()) {
            count_connected_4_graphlets(memberGraph, label, label_vector);
        }
    } else {
        count_connected_4_graphlets(memberGraph, label, label_vector);
    }

    return labeled_4_countsABS;
}



std::set<std::string> GraphletCounts::reverse_string(std::string word) {
    
    std::set<std::string> words = std::set<std::string>();
    words.insert(word);
    std::string word2 = "";
    
   
    
    for (auto i = word.rbegin(); i != word.rend(); ++i) {
        word2 = word2 + *i;
    }
    words.insert(word2);
    return words;
    
}


GraphletCounts & GraphletCounts::operator=(const GraphletCounts & counter) {
    
    if (this == &counter) {
        return *this;
    }
    
    copy_graph(counter.get_graph(), memberGraph);
    
    //memberGraph = counter.get_graph();
    
    if (memberGraph[graph_bundle].properties.count("label") == 1) {
        graphName = memberGraph[graph_bundle].properties["label"];
    } else {
        graphName = "unnamed";
    }
    
    if (memberGraph[graph_bundle].properties.count("pdb_id") == 1) {
        pdbid = memberGraph[graph_bundle].properties["pdb_id"];
    } else {
        pdbid = "no_pdb_id";
    }
    
    if (memberGraph[graph_bundle].properties.count("chain_id") == 1) {
        chain = memberGraph[graph_bundle].properties["chain_id"];
    } else {
        chain = "no_chain_id";
    }
    
    if (memberGraph[graph_bundle].properties.count("graph_type") == 1) {
        std::string graphType = memberGraph[graph_bundle].properties["graph_type"];
        if(graphType == "alpha") {
        graphtype = 1;
        }
        else if(graphType == "beta") {
            graphtype = 2;
        }
        else if(graphType == "albe") {
            graphtype = 3;
        }
        else if(graphType == "alphalig") {
            graphtype = 4;
        }
        else if(graphType == "betalig") {
            graphtype = 5;
        }
        else if(graphType == "albelig") {
            graphtype = 6;
        }
        else if(graphType == "aa_graph") {
            graphtype = 7;
        }
        cerr << "WARNING: Invalid graph type string, cannot translate it to graph type int code.\n";
        graphtype = -1;  
    } else {
        graphtypestr = "no_graph_type";
        graphtype = -1;
        
    }

    
    

    // the default graphlet count vectors
    vector<int> tmp21 (1);
    graphlet2CountsABS = tmp21;
    vector<int> tmp31 (2);
    graphlet3CountsABS = tmp31;
    vector<int> tmp41 (6);
    graphlet4CountsABS = tmp41;
    vector<int> tmp51 (21);
    graphlet5CountsABS = tmp51;
    
    vector<float> tmp2 (1);
    graphlet2CountsNormalized = tmp2;
    vector<float> tmp3 (2);
    graphlet3CountsNormalized = tmp3;
    vector<float> tmp4 (6);
    graphlet4CountsNormalized = tmp4;
    vector<float> tmp5 (21);
    graphlet5CountsNormalized = tmp5;

    
    // vectors for counting labeled graphlets
    vector<float> tmpl (26);
    cl = tmpl;
    

       
    
    print = verbose;
    printGraphletDetails = saveGraphletDetails;
    all_counts_computed = false;
    
    return *this;
    
}

Graph GraphletCounts::get_graph() const {
    
    return memberGraph;
}

int GraphletCounts::get_total_counts() {
    std::vector<std::string> vec0 = std::vector<std::string>();
    std::vector<std::vector<std::string>> vecvec = std::vector<std::vector<std::string>>();
    
    
    if (!two_counts) count_connected_2_graphlets(memberGraph, "", vec0);
    if (!three_counts) count_connected_3_graphlets(memberGraph,"", vecvec);
    if (!four_counts) count_connected_4_graphlets(memberGraph,"",vecvec);
    if (!five_counts) count_connected_5_graphlets(memberGraph);
    
    int counts = num2 + num3 + num4 + num5;
    return counts;
}