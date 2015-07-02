/* 
 * File:   GraphPrinter.cpp
 * Author: ben
 *
 * Created on May 11, 2015, 4:27 PM
 */

#include "GraphPrinter.h"
#include "GraphService.h"

/*
 * Default constructor */
GraphPrinter::GraphPrinter() {
    Graph g_tmp(0);
    service = GraphService(g_tmp);
}



GraphPrinter::GraphPrinter(Graph g) {
    service = GraphService(g);
}


/* Print vertices adjacent to a given vertex i to a string
 * @param int i -- the vertex id */
std::string GraphPrinter::printAdjacent(int i) {
    std::stringstream sstream;
    
    
    std::vector<int> vertex_vector = service.get_adjacent(i);
    vertex_vector.erase(vertex_vector.begin());
    
    // iterate over adjacent vertices and print their ids to a string
    sstream << "  " << std::setw(2) << i << ": "; 
    for (auto it = vertex_vector.begin(); it != vertex_vector.end(); it++) {
        sstream << std::setw(3) << *it << " ";
    }
    sstream << std::endl;
    
    return sstream.str();
};

/* Iterate over the vertices and print their adjacent vertices */
std::string GraphPrinter::printAdjacentAll() {
    std::stringstream sstream;
    
    int n = service.getNumVertices();
    
    sstream << "Iterate over the vertices and print their adjacent vertices:\n"; 
    for (int i = 0; i < n; i++) {
        sstream << printAdjacent(i);
    }
    sstream << std::endl;
    
    return sstream.str();
};




/* Save the graph statistics to a csv file */
void GraphPrinter::saveGraphStatistics() {
    std::ofstream summaryFile;
    const std::string summaryFileName = output_path + "graphsStatistics.csv"; // make the file
    

    // get the graph statistics, i.e. Node-Degreee-Distribution, number of vertices, number of edges
    std::vector<int> degDist = service.computeDegreeDist();
    int n = num_vertices(service.getGraph());
    int m = num_edges(service.getGraph());
    /* NOTE:
     * p is the ratio of (number of edges in graph) to (maximal possible number of edges given n vertices) */
    float p = 2.0 * m / (n * (n - 1.0));  
    
    // open the file
    summaryFile.open(summaryFileName.c_str(), std::ios_base::app);
    if (!summaryFile.is_open()) { // ERROR: summary file not open
        std::cerr << apptag << "ERROR: could not open summary file for statistics.\n";
    } else { 
        // write statistics into the file
        summaryFile << std::setw(5)  << n << ","
                    << std::setw(5)  << m << ","
                    << std::setw(10) << std::fixed << std::setprecision(4) << p << ","
                    << std::setw(5)  << degDist[0];
        for (int i = 1; i < degDist.size(); i++) {
            summaryFile << "," << std::setw(5) << degDist[i];
        }
        summaryFile << "\n";
        
        // close the file
        summaryFile.close();
        if( ! silent) {
            std::cout << apptag << "    The statistics were saved to the summary in \"" << summaryFileName << "\".\n";
        }
    }
};

/* Stores all graphs in a matlab file */
void GraphPrinter::saveAsMatlabVariable(int& number) {    
    AdjacencyIterator first, last;
    std::ofstream matlabFile;
    const std::string matlabFileName = output_path + "graphsMatlabFormat.m"; // make output file
    
    Graph g = service.getGraph();
    
    // open the file
    matlabFile.open(matlabFileName.c_str(), std::ios_base::app);
    if (!matlabFile.is_open()) { // show error message, if file is not open
        std::cerr << apptag << "ERROR: could not open " << matlabFileName << " file.\n";
    } else {
        int pos = matlabFile.tellp(); // the current position
        if (pos == 0) { // if the filestream is at the beginning, add the info below
            matlabFile << "% load protein graphs in matlab\n"
                      << "% by defining a structure that stores\n"
                      << "% name, adjacency matrix and adjacency list\n"
                      << "% of each protein graph\n\n";
        }
        // write the name of the graph
        matlabFile << "graphs(" << number + 1 << ").name = \'" << service.get_label() << "\';\n";
        
        matlabFile << "graphs(" << number + 1 << ").am = [ ";
        for (int j = 0; j < num_vertices(g); j++) {
                matlabFile << (edge(0, j, g).second) << " ";
        }
        for (int i = 1; i < num_vertices(g); i++) {
            matlabFile << "; ";
            for (int j = 0; j < num_vertices(g); j++) {
                matlabFile << (edge(i, j, g).second) << " ";
            }
        }
        matlabFile << "];\n";

        matlabFile << "graphs(" << number + 1 << ").al = {[ ";  
        for (tie(first, last) = adjacent_vertices(0, g); first != last; ++first) {
            matlabFile << g[*first].id + 1 << " ";
        }
        for (int i = 1; i < num_vertices(g); i++) {
            matlabFile << "]; [";
            for (tie(first, last) = adjacent_vertices(i, g); first != last; ++first) {
                matlabFile << " " << g[*first].id + 1;
            }
        }
        matlabFile << "]};\n\n";
        matlabFile.close();
                
        if( ! silent) {
            std::cout << apptag << "    The adjacency matrix and list were saved to \"" << matlabFileName << "\".\n";
        }
    }
};

/* Save the graph statistics in a matlab file, i.e. node-degree-distribution, number of vertices and edges */
void GraphPrinter::saveGraphStatisticsAsMatlabVariable() {
    // create the file    
    std::ofstream summaryMatlabFile;
    const std::string summaryMatlabFileName = output_path + "graphsStatisticsMatlabFormat.m";
    int pos;
    
    // compute graph statistics
    std::vector<int> degDist = service.computeDegreeDist();
    int n = num_vertices(service.getGraph());
    int m = num_edges(service.getGraph());
    /* NOTE:
     * p is the ratio of (number of edges in graph) to (maximal possible number of edges given n vertices) */
    float p = 2.0 * m / (n * (n - 1.0)); 
    
    // write to the file
    summaryMatlabFile.open(summaryMatlabFileName.c_str(), std::ios_base::app);    
    if (!summaryMatlabFile.is_open()) {
        std::cerr << apptag << "ERROR: could not open matlab statistics file.\n";
    } else {
        pos = summaryMatlabFile.tellp();
        if (pos == 0) {
            summaryMatlabFile << "statistics = ([\n";
        } else {
            /* optional TODO: overwrite last 3 characters
             *                to substitute  "]);" by "],\n"
             *                otherwise, you have to do this manually 
             */
        }
        
        summaryMatlabFile << "[" << std::setw(5)  << n << ","
                    << std::setw(5)  << m << ","
                    << std::setw(10) << std::fixed << std::setprecision(4) << p << ","
                    << std::setw(5)  << degDist[0];
        //for (int i = 1; i < degDist.size(); i++) {
        for (int i = 1; i < 12; i++) {
            if (i < degDist.size()) summaryMatlabFile << "," << std::setw(5) << degDist[i];
            else                    summaryMatlabFile << "," << std::setw(5) << 0;
        }
        summaryMatlabFile << "],\n";
        summaryMatlabFile.close();
        
        if( ! silent) {
            std::cout << apptag << "    The statistics were saved to \"" << summaryMatlabFileName << "\".\n";
        }
    }
};

/* Save the graph in simple format.
 * The name of the file will be of the form "simple_format_graphname.graph".
 * In simple format, the graph will be represented by its edges. */
void GraphPrinter::saveInSimpleFormat() {
    
    Graph g = service.getGraph();
    
    EdgeIterator ei, ei_end;
    const std::string outFileName = output_path + "simple_format_" + service.get_label() + ".graph";
    std::ofstream outFile;

    outFile.open(outFileName.c_str());
    if (!outFile.is_open()) {
        std::cerr << apptag << "ERROR: could not open " << outFileName << " file.\n";
    } else {
        for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) {
            outFile << g[*ei].source << " " << g[*ei].target << std::endl;
        }
    }
};

/* Saves absolute graphlet counts to a file.
 * @param <vector<vector<int>>> absolute counts
 * @param <vector<float>>> labeled counts
 * @param <bool> include labeled graphlets */
void GraphPrinter::saveABSGraphletCountsSummary(std::vector<std::vector<int>> abs_counts, std::vector<float> labeled_counts) {
    std::ofstream summaryFile;
    const std::string summaryFileName = output_path + "counts.plain";
    

    
    std::string graphName = service.get_label();

    summaryFile.open(summaryFileName.c_str(), std::ios_base::app);
    if (!summaryFile.is_open()) {
        std::cout << apptag << "ERROR: could not open summary file at '" << summaryFileName << "'.\n";
    } else {
        summaryFile << graphName;
        


        summaryFile << std::setw(6) << "[g3] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[1][0];
        for (int i = 1; i < abs_counts[1].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[1][i];
        
        summaryFile << std::setw(6) << "[g4] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[2][0];
        for (int i = 1; i < abs_counts[2].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[2][i];
        
        summaryFile << std::setw(6) << "[g5] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[3][0];
        for (int i = 1; i < abs_counts[3].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << abs_counts[3][i];
        
        if (!labeled_counts.empty()) {
            summaryFile << std::setw(10) << " [labeled] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << labeled_counts[0];
            for (int i = 1; i < labeled_counts.size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << labeled_counts[i];
        }

        summaryFile << "\n\n";
        summaryFile.close();
        
        if( ! silent) {
            std::cout << apptag << "    The summary over all computed counts is in \"" << summaryFileName << "\".\n";
        }
    }
    
    
}

/* Saves normalized graphlet counts to a file.
 * @param <vector<vector<float>>> normalized counts
 * @param <vector<float>>> labeled counts
 *  */
void GraphPrinter::saveNormalizedGraphletCountsSummary(std::vector<std::vector<float>> norm_counts, std::vector<float> labeled_counts) {
    std::ofstream summaryFile;
    const std::string summaryFileName = output_path + "counts.plain";
    

    
    std::string graphName = service.get_label();

    summaryFile.open(summaryFileName.c_str(), std::ios_base::app);
    if (!summaryFile.is_open()) {
        std::cout << apptag << "ERROR: could not open summary file at '" << summaryFileName << "'.\n";
    } else {
        summaryFile << graphName;
        


        summaryFile << std::setw(6) << "[g3] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[1][0];
        for (int i = 1; i < norm_counts[1].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[1][i];
        
        summaryFile << std::setw(6) << "[g4] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[2][0];
        for (int i = 1; i < norm_counts[2].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[2][i];
        
        summaryFile << std::setw(6) << "[g5] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[3][0];
        for (int i = 1; i < norm_counts[3].size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << norm_counts[3][i];
        
        if (!labeled_counts.empty()) {
            summaryFile << std::setw(10) << " [labeled] " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << labeled_counts[0];
            for (int i = 1; i < labeled_counts.size(); i++) summaryFile << ", " << std::setiosflags(std::ios::fixed) << std::setprecision(4) << labeled_counts[i];
        }

        summaryFile << "\n\n";
        summaryFile.close();
        
        if( ! silent) {
            std::cout << apptag << "    The summary over all computed counts is in \"" << summaryFileName << "\".\n";
        }
    }
    
    
}

/**
 * Saves the normalized graphlet counts in MATLAB format to the MATLAB output file. If the file does already exist,
 * the data for this graph gets appended to it. Ignores 2-graphlets.
 * @param <vector<vector<float>>> unlabeled counts
 * @param <veector<float>> labeled counts
 */
void GraphPrinter::save_normalized_counts_as_matlab_variable(std::vector<std::vector<float>> unlabeled_counts, std::vector<float> labeled_counts) {    
    std::ofstream countsMatlabFile;
    const std::string countsMatlabFileName = output_path + "countsMatlabFormat.m";
    int pos;


    countsMatlabFile.open(countsMatlabFileName.c_str(), std::ios_base::app);    
    if (!countsMatlabFile.is_open()) {
        std::cout << apptag << "ERROR: could not open matlab counts file.\n";
    } else {
        pos = countsMatlabFile.tellp();
        if (pos == 0) {
            countsMatlabFile << "myCounts = ([\n";
        } else {
            /* optional TODO: overwrite last 3 characters
             *                to substitute  "]);" by "],\n"
             *                otherwise, you have to do this manually 
             */
        }
            
        countsMatlabFile << "[" << unlabeled_counts[1][0];
        for (int i = 1; i < unlabeled_counts[1].size(); i++) countsMatlabFile << ", " << unlabeled_counts[1][i];
        for (int i = 0; i < unlabeled_counts[2].size(); i++) countsMatlabFile << ", " << unlabeled_counts[2][i];
        for (int i = 0; i < unlabeled_counts[3].size(); i++) countsMatlabFile << ", " << unlabeled_counts[3][i];
        if (!labeled_counts.empty()) {
            for (int i = 0; i < labeled_counts.size(); i++) countsMatlabFile << ", " << labeled_counts[i];
        }
        
        countsMatlabFile << "],\n";
        countsMatlabFile.close();
        
        if( ! silent) {
            std::cout << apptag << "    The counts were added to the \"" << countsMatlabFileName << "\".\n";
        }
    }
}

/**
 * Saves the absolute graphlet counts in MATLAB format to the MATLAB output file. If the file does already exist,
 * the data for this graph gets appended to it. Ignores 2-graphlets.
 * @param <vector<vector<float>>> unlabeled counts
 * @param <veector<float>> labeled counts
 */
void GraphPrinter::save_absolute_counts_as_matlab_variable(std::vector<std::vector<int>> unlabeled_counts, std::vector<int> labeled_counts) {    
    std::ofstream countsMatlabFile;
    const std::string countsMatlabFileName = output_path + "countsMatlabFormat.m";
    int pos;


    countsMatlabFile.open(countsMatlabFileName.c_str(), std::ios_base::app);    
    if (!countsMatlabFile.is_open()) {
        std::cout << apptag << "ERROR: could not open matlab counts file.\n";
    } else {
        pos = countsMatlabFile.tellp();
        if (pos == 0) {
            countsMatlabFile << "myCounts = ([\n";
        } else {
            /* optional TODO: overwrite last 3 characters
             *                to substitute  "]);" by "],\n"
             *                otherwise, you have to do this manually 
             */
        }
            
        countsMatlabFile << "[" << unlabeled_counts[1][0];
        for (int i = 1; i < unlabeled_counts[1].size(); i++) countsMatlabFile << ", " << unlabeled_counts[1][i];
        for (int i = 0; i < unlabeled_counts[2].size(); i++) countsMatlabFile << ", " << unlabeled_counts[2][i];
        for (int i = 0; i < unlabeled_counts[3].size(); i++) countsMatlabFile << ", " << unlabeled_counts[3][i];
        if (!labeled_counts.empty()) {
            for (int i = 0; i < labeled_counts.size(); i++) countsMatlabFile << ", " << labeled_counts[i];
        }
        
        countsMatlabFile << "],\n";
        countsMatlabFile.close();
        
        if( ! silent) {
            std::cout << apptag << "    The counts were added to the \"" << countsMatlabFileName << "\".\n";
        }
    }
}


/* Saves the counts in NOVA format. The last vector of counts is reserved for 
 * labeled counts. The counts are saved to a file with the suffix
 * countsNovaFormat.csv. I don't really get why they should be saved this way...
 * 
 * @param counts - vector containing the counts
 * @param withLabeld - b whether labeled counts are part of the vector */
void GraphPrinter::save_counts_in_nova_format(std::string graphName, std::vector<std::vector<int>> counts, bool withLabeled) {
    std::ofstream countsNovaFormatFile;
    const std::string countsNovaFormatFileName = output_path + "countsNovaFormat.csv";
    int pos;
    int numberOfGraphlets;


    countsNovaFormatFile.open(countsNovaFormatFileName.c_str(), std::ios_base::app);    
    if (!countsNovaFormatFile.is_open()) {
        std::cout << apptag << "ERROR: could not open counts file in NOVA format.\n";
    } else {
        pos = countsNovaFormatFile.tellp();
        if (pos == 0) {
            countsNovaFormatFile << "ID,Group";
            
            
            // in an earlier version, numberOfGraphlets only counted the
            // size of the vectors
            
            numberOfGraphlets = 0;
            
            for (auto i : counts) {
                
                std::vector<int> g_counts = i;
                
                numberOfGraphlets = g_counts.size();

            }

            for (int i = 1; i <= numberOfGraphlets; i++) {
                countsNovaFormatFile << ",Graphlet" << i;
            }        
            countsNovaFormatFile << "\n";
        }
        countsNovaFormatFile << graphName << ",A";             
       
        
        
        for (auto i : counts) {
            
            std::vector<int> g_counts = i;
            
            for (auto k : g_counts) {
                
                countsNovaFormatFile << "," << k;
                
            }
        }
        
        
        countsNovaFormatFile << "\n";
        countsNovaFormatFile.close();
    
        if( ! silent) {
            std::cout << apptag << "    The counts were added to the \"" << countsNovaFormatFileName << "\".\n"; 
        }
    }    
    
    
}





