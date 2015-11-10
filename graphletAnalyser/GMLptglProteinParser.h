/* 
 * File:   GMLptglProteinParser.h
 * Author: ben
 *
 * Created on May 8, 2015, 2:18 PM
 */

#ifndef GMLPTGLPROTEINPARSER_H
#define	GMLPTGLPROTEINPARSER_H

#include "Graph.h"


/*
 * This Headerfile provides the interface for the GML parser written by tatiana
 * It was in another class at first but was moved to GMLptglProteinParser.cpp */
class GMLptglProteinParser {

    
    private:
        
        //methods
        bool isElementClosingLine(const std::string&); // looks for closing brackets in GML file
        std::string parse_value_string(const std::string&);
        Graph graph;// parses labels and such
    
    public:
        
        //constructors
        GMLptglProteinParser();
        GMLptglProteinParser(const std::string&, const std::string);
        
        Graph getGraph();
        
        
            
};

#endif	/* GMLPTGLPROTEINPARSER_H */

