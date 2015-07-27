/* 
 * File:   GraphletCounts.h
 * Author: tatiana
 * Revision: ben
 *
 * Created on May 21, 2013, 4:08 PM
 */

#ifndef GRAPHLETCOUNTS_H
#define	GRAPHLETCOUNTS_H

#include "global.h"
#include "Graph.h"
#include "db.h"

class GraphletCounts {

    
private:
    // attributes
    Graph memberGraph;
    std::string graphName;
    std::string pdbid;
    std::string chain;
    std::string graphtypestr;
    int graphtype;
    
    std::vector<int> graphlet2CountsABS; // counts for 2-graphlets absolute
    std::vector<int> graphlet3CountsABS; // counts for 3-graphlets absolute
    std::vector<int> graphlet4CountsABS; // counts for 4-graphlets absolute
    std::vector<int> graphlet5CountsABS; // counts for 5-graphlets absolute
    
    std::vector<std::string> size_2_labels;
    std::vector<std::vector<std::string>> size_3_labels;
    std::vector<std::vector<std::string>> size_4_labels;
    
    
    std::vector<int> labeled_2_countsABS; // counts for labeled 2-graphlets absolute
    std::vector<std::vector<int>> labeled_3_countsABS; // counts for labeled 3-graphlets absolute
    std::vector<std::vector<int>> labeled_4_countsABS; // counts for labeled 4-graphlets absolute
    std::vector<int> labeled_graphlet5CountsABS; // counts for labeled 5-graphlets absolute NOT SUPPORTED YET
    
    
    std::vector<float> graphlet2CountsNormalized;  // counts for 2-graphlets - normalized
    std::vector<float> graphlet3CountsNormalized;  // counts for 3-graphlets - normalized
    std::vector<float> graphlet4CountsNormalized;  // counts for 4-graphlets - normailzed
    std::vector<float> graphlet5CountsNormalized;  // counts for 5-graphlets - normalized
    
    std::vector<float> labeled_graphlet2CountsNormalized;  // counts for 2-graphlets - normalized
    std::vector<float> labeled_graphlet3CountsNormalized;  // counts for 3-graphlets - normalized
    std::vector<float> labeled_graphlet4CountsNormalized;  // counts for 4-graphlets - normailzed
    
    std::vector<float> cl;  // counts for labeled graphlets
    std::vector<int> labeled_abs_counts; // absolute counts for labeled graphlets
    std::vector<float> labeled_norm_counts; // normalized counts for labeled graphlets
   
    
    bool print;
    bool printGraphletDetails;
    std::ofstream logFile;
    
    bool all_counts_computed; // check whether compute_all_counts has already been called
    bool unlabeled_abs_counts_computed;
    bool norm_counts_computed;
    
    bool labeled_norm_counts_computed;
    
    bool two_counts;
    bool three_counts;
    bool four_counts;
    bool five_counts;
    
    
    // number of found connected 2,3,4,5 graphlets
    int num2;
    int num3;
    int num4;
    int num5;
    
    void compute_all_counts();
    void compute_unlabeled_abs_counts();
    void compute_unlabeled_norm_counts();
    std::set<std::string> compute_CAT(std::string);
    std::set<std::string> reverse_string(std::string);
    
    

public:
    // constructors
    GraphletCounts();
    GraphletCounts(const Graph&);
    
    // methods
    int get_total_counts();
    std::vector<std::vector<int>> get_unlabeled_abs_counts();
    std::vector<std::vector<float>> get_normalized_counts();
    std::vector<int> get_labeled_abs_counts();
    std::vector<float> normalize_counts(std::vector<int>,float);
    Graph get_graph() const;
    
    
    
    std::string print_counts(std::vector<int>&, bool);
    
    
    std::vector<int> get_labeled_2_countsABS(std::string, std::vector<std::string>);
    std::vector<std::vector<int>> get_labeled_3_countsABS(std::string, std::vector<std::vector<std::string>>);
    std::vector<std::vector<int>> get_labeled_4_countsABS(std::string, std::vector<std::vector<std::string>>);

    /******* Graphlet Counting Algorithms by N. Shervashidze ********/
    std::vector<int> count_connected_2_graphlets(std::string, std::vector<std::string>);
    std::vector<int> count_connected_3_graphlets(std::string, std::vector<std::vector<std::string>>);
    std::vector<int> count_connected_4_graphlets(std::string, std::vector<std::vector<std::string>>);
    std::vector<int> count_connected_5_graphlets();
    GraphletCounts & operator=(const GraphletCounts &);
};

#endif	/* GRAPHLETCOUNTS_H */

