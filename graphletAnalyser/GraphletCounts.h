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
    
    std::vector<float> graphlet2CountsNormalized;  // counts for 2-graphlets - normalized
    std::vector<float> graphlet3CountsNormalized;  // counts for 3-graphlets - normalized
    std::vector<float> graphlet4CountsNormalized;  // counts for 4-graphlets - normailzed
    std::vector<float> graphlet5CountsNormalized;  // counts for 5-graphlets - normalized
    
    
    std::vector<float> cl;  // counts for labeled graphlets
   
    std::vector< std::vector<float> > graphletDegreeDistribution;
    
    bool print;
    bool printGraphletDetails;
    std::ofstream logFile;
    
    bool countsComputed; // check whether compute_all_counts has already been called

public:
    // constructors
    GraphletCounts();
    GraphletCounts(Graph&);
    
    // methods
    void compute_all_counts(bool); 
    vector<float> normalize_counts(vector<int>,bool);
    void saveCountsSummary(bool);
    void saveCountsAsMatlabVariable(bool);
    void saveCountsInNovaFormat(bool);
    int saveCountsToDatabasePGXX(bool);
    int databaseContainsGraphletsForGraph(unsigned long int);
    void deleteGraphletCountEntryForGraph(unsigned long int);
    std::string print_counts(std::vector<int>&, bool);
    long getGraphDatabaseID(std::string, std::string, int);
    int testDatabasePGXX();

    /******* Graphlet Counting Algorithms by N. Shervashidze ********/
    std::vector<int> count_connected_2_graphlets(Graph&, bool);
    std::vector<int> count_connected_3_graphlets(Graph&, bool);
    std::vector<int> count_connected_4_graphlets(Graph&, bool);
    std::vector<int> count_connected_5_graphlets(Graph&, bool);
};

#endif	/* GRAPHLETCOUNTS_H */

