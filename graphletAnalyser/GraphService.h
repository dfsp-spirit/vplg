/* 
 * File:   GraphService.h
 * Author: ben
 *
 * Created on April 28, 2015, 2:45 PM
 */

#ifndef GRAPHSERVICE_H
#define	GRAPHSERVICE_H


/*
 * TODO:
 * ask for Matlab inclusion
 * ask for method: parse_value_string
 * Separation of Parser and Graph Constructor

 * 
 * 
*/


#include "Graph.h"

/*This Headerfile is meant to define functions for supporting several graph services,
 * including:   Creation of GML files from graphs,
 *              Creation of graphs from strings in GML format,
 *              Reading of several properties from the graph
 * 
 *  */


// below is a short example/summary of the GML-Format
/* This interface is meant for graphs which were parsed from a GML file.
 * GML files are of the following form.
 * 
 * x     is any arbitrary integer
 *       NOTE: In GML vertex ids are in ascending order
 * s     is a string
 * prop  represents an arbitrary number of other properties which are written in
 *       the same form as id, label, comment and directed
 * 
 * graph [
 * comment s
 * id x
 * label s
 * directed x (x=0 => undirected; x=1 => directed)
 * prop
 * ]
 * 
 * node [
 * id x
 * label s
 * prop
 * ]
 * 
 * For edges, the values for source and target refer to the id of the
 * corresponding nodes
 * 
 * edge [
 * source x
 * target x
 * comment s
 * label s
 * prop
 * ]
 * 
 */



class GraphService {
    
private:    
    //attributes
    Graph g;
    

    
    
public:
    
    //methods
    
    GraphService(); // default constructor
    GraphService(Graph graph); // constructor
    Graph getGraph(); // returns the graph
    std::string get_label(); // returns the graph's label
    std::vector<std::string> getGraphProperties(); //Returns property keys
    std::string getPropertyValue(std::string prop); // returns value for a property
    int getNumVertices(); // number of vertices
    int getNumEdges(); //number of edges
    std::vector<int> getVertices(); 
    std::vector<std::pair<int, int>> getEdges();
    std::vector<int> computeDegreeDist(); // compute node degree distribution
    std::vector<int> get_adjacent(int i); // get all vertices adjacent to i as a vector
    std::vector<std::vector<int>> get_adjacent_all(); // call get_adjacent on all vertices

};


#endif	/* GRAPHSERVICE_H */

/*
 * Methods from the original ProteinGraph.cpp and ProteinGraph.h which are not
 * found here:
 * 
 * getGraphTypeString
 * getGraphTypeInt
 * getChainID
 * getPdbid
 *  specific to the Graphs retrieved from the PTGL and therefore ignored
 * 
 * getName
 *  renamed to getLabel
 * 
 * getNameWithSize
 *  redundant: use getNumVertices to get the size and getLabel to get the name
 * 
 * isElementClosingLine
 * parse_gml
 * parse_value_string
 * saveAsMatlabVariable
 * saveGraphStatisticsAsMatlabVariable
 * saveInSimpleFormat
 *  belong into an IO class/header
 *  The same counts for all methods starting with print. They are replaced by
 *  the get methods of this class. See GraphPrinter
 * 
 * parseValueString
 *  see TODO
 * 
 *  */