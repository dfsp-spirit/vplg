/* 
 * File:   GraphPrinter.h
 * Author: ben
 *
 * Created on May 11, 2015, 4:27 PM
 */

#ifndef GRAPHPRINTER_H
#define	GRAPHPRINTER_H

#include "Graph.h"
#include "global.h"
#include "GraphService.h"
#include "GraphletCounts.h"

using namespace boost;

/*
 * Interface for the GraphPrinter class which provides Graph-related output to
 * the console and to files
 *                           */
class GraphPrinter {
    
protected:
        //attributes
        GraphService service; // a service object to get data from the graph
public:
    
    //constructors
    GraphPrinter();
    GraphPrinter(GraphService serv);
    
    //methods, doing what their name says
    
    std::string printAdjacent(int i); 
    std::string printAdjacentAll(); 
    void saveGraphStatistics(); // saves the number of vertices and edges and the node-degree-distribution
    void saveAsMatlabVariable(int& number); // saves the graphs as a matlab file
    void saveGraphStatisticsAsMatlabVariable(); // saves the output of printGraphInfo to a matlab file
    void saveInSimpleFormat(); // save the edges of the graph to a file
    void saveABSGraphletCountsSummary(vector<vector<int>> abs_counts, vector<float> labeled_counts);
    void saveNormalizedGraphletCountsSummary(vector<vector<float>> norm_counts, vector<float> labeled_counts);
    void save_normalized_counts_as_matlab_variable(vector<vector<float>>,vector<float>);
    void save_absolute_counts_as_matlab_variable(vector<vector<int>>,vector<int>);
};

#endif	/* GRAPHPRINTER_H */

