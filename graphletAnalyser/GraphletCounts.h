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
    
    std::vector<string> size_2_labels;
    std::vector<std::vector<std::string>> size_3_labels;
    std::vector<std::vector<std::string>> size_4_labels;
    
    
    std::vector<int> labeled_2_countsABS; // counts for 2-graphlets absolute
    std::vector<std::vector<int>> labeled_3_countsABS; // counts for 3-graphlets absolute
    std::vector<std::vector<int>> labeled_4_countsABS; // counts for 4-graphlets absolute
    std::vector<int> labeled_graphlet5CountsABS; // counts for 5-graphlets absolute
    
    
    std::vector<float> graphlet2CountsNormalized;  // counts for 2-graphlets - normalized
    std::vector<float> graphlet3CountsNormalized;  // counts for 3-graphlets - normalized
    std::vector<float> graphlet4CountsNormalized;  // counts for 4-graphlets - normailzed
    std::vector<float> graphlet5CountsNormalized;  // counts for 5-graphlets - normalized
    
    std::vector<float> labeled_graphlet2CountsNormalized;  // counts for 2-graphlets - normalized
    std::vector<float> labeled_graphlet3CountsNormalized;  // counts for 3-graphlets - normalized
    std::vector<float> labeled_graphlet4CountsNormalized;  // counts for 4-graphlets - normailzed
    std::vector<float> labeled_graphlet5CountsNormalized;
    
    std::vector<float> cl;  // counts for labeled graphlets
    std::vector<int> labeled_abs_counts; // absolute counts for labeled graphlets
    std::vector<float> labeled_norm_counts; // normalized counts for labeled graphlets
   
    std::vector< std::vector<float> > graphletDegreeDistribution;
    
    bool print;
    bool printGraphletDetails;
    std::ofstream logFile;
    
    bool all_counts_computed; // check whether compute_all_counts has already been called
    bool abs_counts_computed;
    bool norm_counts_computed;
    
    bool labeled_norm_counts_computed;
    bool labeled_abs_counts_computed;
    
    
    void compute_all_counts();
    void compute_abs_counts(bool);
    void compute_norm_counts(bool);
    void compute_labeled_abs_counts();
    void compute_labeled_norm_counts();
    std::set<std::string> compute_CAT(std::string);
    std::set<std::string> reverse_string(std::string);
    

public:
    // constructors
    GraphletCounts();
    GraphletCounts(Graph&);
    
    // methods
    
    std::vector<std::vector<int>> get_abs_counts();
    vector<vector<float>> get_normalized_counts();
    std::vector<float> get_labeled_norm_counts();
    std::vector<int> get_labeled_abs_counts();
    vector<float> normalize_counts(vector<int>,bool);
    void saveCountsInNovaFormat(bool);
    int saveCountsToDatabasePGXX(bool);
    int databaseContainsGraphletsForGraph(unsigned long int);
    void deleteGraphletCountEntryForGraph(unsigned long int);
    std::string print_counts(std::vector<int>&, bool);
    long getGraphDatabaseID(std::string, std::string, int);
    int testDatabasePGXX();
    
    std::vector<int> get_labeled_2_countsABS(std::string, std::vector<std::string>);
    std::vector<vector<int>> get_labeled_3_countsABS(std::string, std::vector<std::vector<std::string>>);
    std::vector<vector<int>> get_labeled_4_countsABS(std::string, std::vector<std::vector<std::string>>);

    /******* Graphlet Counting Algorithms by N. Shervashidze ********/
    std::vector<int> count_connected_2_graphlets(Graph&, std::string, std::vector<std::string>);
    std::vector<int> count_connected_3_graphlets(Graph&, std::string, std::vector<std::vector<std::string>>);
    std::vector<int> count_connected_4_graphlets(Graph&, std::string, std::vector<std::vector<std::string>>);
    std::vector<int> count_connected_5_graphlets(Graph&, bool);
};

#endif	/* GRAPHLETCOUNTS_H */

