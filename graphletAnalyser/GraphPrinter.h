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
#include "JSON_printer.h"

using namespace boost;

/*
 * Interface for the GraphPrinter class which provides Graph-related output to
 * the console and to files
 *                           */
class GraphPrinter {
    
protected:
        
    JSON_printer j_print;
    
public:
    
    //constructors
    GraphPrinter();
    
    //methods, doing what their name says
    
    std::string printAdjacent(std::vector<int> vertex_vector) const; 
    std::string printAdjacentAll(std::vector<std::vector<int>> vertex_vector) const; 
    void saveGraphStatistics(std::vector<int> degDist, int n, int m); // saves the number of vertices and edges and the node-degree-distribution
    void saveAsMatlabVariable(const Graph& g); // saves the graphs as a matlab file
    void saveGraphStatisticsAsMatlabVariable(std::vector<int> degDist, int n, int m); // saves the output of printGraphInfo to a matlab file
    void saveInSimpleFormat(Graph& g); // save the edges of the graph to a file
    void saveABSGraphletCountsSummary(std::string graphName, std::vector<std::vector<int>> abs_counts, std::vector<float> labeled_counts);
    void saveNormalizedGraphletCountsSummary(std::string graphName, std::vector<std::vector<float>> norm_counts, std::vector<float> labeled_counts);
    void save_normalized_counts_as_matlab_variable(std::vector<std::vector<float>>,std::vector<float>);
    void save_absolute_counts_as_matlab_variable(std::vector<std::vector<int>>,std::vector<int>);
    void save_counts_in_nova_format(std::string,std::vector<std::vector<int>>);
    void save_abs_counts_as_matlab_variable();
    void save_norm_counts_as_matlab_variable();
    void save_statistics_as_json(std::string graphname, int num_vertices, int num_edges,std::vector<std::vector<int>> abs_counts, std::vector<std::vector<float>> rel_counts);
    //GraphPrinter & operator=(const GraphPrinter & printer);
    
};

#endif	/* GRAPHPRINTER_H */

