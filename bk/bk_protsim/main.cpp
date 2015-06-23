/* 
 * File:   main.cpp
 * Author: julian
 *
 * Created on June 16, 2015, 2:56 PM
 */

#include <iostream>
#include <fstream>
#include "GMLptglProteinParser.h"
#include "ProductGraph.h"
#include "BronKerbosch.h"
#include "BK_Output.h"
#include "PG_Output.h"


int stringToTextFile(std::string fname, std::string contents) {
    std::ofstream file;
    
    // write to the file
    file.open(fname.c_str(), std::ios_base::trunc);    
    if (!file.is_open()) {
        std::cerr << "ERROR: could not open file '" << fname << "'.\n";
        return 0;
    } else {        
        file << contents;
        file.close();        
        return 1;
    }
}

/*
 * 
 */
int main(int argc, char** argv) {
    
    std::string apptag = "[BK] ";
    
    std::cout << apptag << "=== Bron Kerbosch-based graph similarity ===\n";
    std::cout << apptag << "= Searches maximum common substructures in a pair (G1, G2) of graphs.\n";
    std::cout << apptag << "= Constructs a compatibility graph GC from G1 and G2 and runs a variant of the Bron-Kerbosch algorithm on it.\n";
    std::cout << apptag << "= The cliques in GC correspond to common substructures (compatible vertex mappings) between G1 and G2.\n";
    std::cout << apptag << "= Written by Julian Gruber-Roet at MolBI group, 2015.\n";
    std::cout << apptag << "\n";
    
    if (argc < 3) {
        std::cout << apptag << "Usage:\n";
        std::cout << apptag << argv[0] << " <graphFile1.gml> <graphFile2.gml> <output_parameters> \n";
        std::cout << apptag << "Output parameters:\n";
        std::cout << apptag << "\t-a     : Output all cliques (default)\n";
        std::cout << apptag << "\t-l     : Output only largest cliques\n";
        std::cout << apptag << "\t-f     : Filter permutations for STDOUT, i.e., print unique cliques only.\n";
        std::cout << apptag << "\t-s <n> : Output only cliques with minimum size <n> vertices.\n";        
        std::cout << apptag << "Example call: " << argv[0] << " example1.gml example2.gml -s 8\n";
        std::cout << apptag << "  This will output all cliques larger than 8 vertices.\n";
        return 1;
    }
    
    //main algorithms. All calculations are done here
    //parse input files
    Graph f = GMLptglProteinParser(argv[1]).graph;
    Graph s = GMLptglProteinParser(argv[2]).graph;
    //compute product graph
    ProductGraph pg(f,s);
    pg.run();
    //find cliques in the product graph
    BronKerbosch bk(pg.getProductGraph());
    bk.run();

    //parse output parameter, get list of found cliques
    std::list<std::list<unsigned long>> result_list;
    if (argc >= 4) {
        if ((strcmp(argv[3],"-l") == 0) || (strcmp(argv[3],"-L") == 0) ) {
            result_list = BK_Output::get_result_largest(bk);
        } //end -l
        else if ((strcmp(argv[3],"-s") == 0) || (strcmp(argv[3],"-S") == 0) ) {
            int size = 0;
            if (argc >= 5) {size = atoi(argv[4]);} else { std::cout << apptag << "No size given for parameter '-s', assuming 0.\n"; }
            result_list = BK_Output::get_result_larger_than(bk, size);
        }//end -s
        else if ((strcmp(argv[3],"-a") == 0) || (strcmp(argv[3],"-A") == 0) ) {
            result_list = BK_Output::get_result_all(bk);
        }//end -a
        else {
            std::cout << apptag << "Unknown output parameter, using default (all cliques).\n";
            result_list = BK_Output::get_result_all(bk);
        }//end default
    } //end output param exists
    else {
        std::cout << apptag << "No output parameter given, using default (all cliques).\n";
        result_list = BK_Output::get_result_all(bk);
    } //end no output param
    
    
    // we may need to filter permutations here
    
    
    int filter_permutations = 0; // TODO: get from settings. requires a settings objects and better command line parsing in the first place.
    if (argc >= 4) {
        if ((strcmp(argv[3],"-f") == 0) || (strcmp(argv[3],"-F") == 0) ) {
            filter_permutations = 1;
        }
    }
    if (argc >= 5) {
        if ((strcmp(argv[4],"-f") == 0) || (strcmp(argv[4],"-F") == 0) ) {
            filter_permutations = 1;
        }
    }
    if (argc >= 6) {
        if ((strcmp(argv[5],"-f") == 0) || (strcmp(argv[5],"-F") == 0) ) {
            filter_permutations = 1;
        }
    }
    if(filter_permutations) {
        
        std::stringstream fresult;
        std::list<std::pair<std::list<int>, std::list<int>>> res;
        for (std::list<unsigned long>& clique : result_list) {
            std::list<int> ids_first = PG_Output::get_vertex_ids_first(pg, clique);
            std::list<int> ids_second = PG_Output::get_vertex_ids_second(pg, clique);
            std::pair<std::list<int>, std::list<int>> pair(ids_first, ids_second);
            res.push_back(pair);            
        }
        int num_before_filter = res.size();
        res.sort();
        res.unique();
        std::cout << apptag << "Found " << num_before_filter << " possible vertex mappings. Filtered permutations, " << res.size() << " elements remaining.\n";
        
        int idx = 0;
        for (std::pair<std::list<int>, std::list<int>> pair : res) {
            fresult << "{ ";
            fresult << " \"first\": " << PG_Output::int_list_to_JSON(pair.first) << ", ";
            fresult << " \"second\": " << PG_Output::int_list_to_JSON(pair.second) << " ";
            fresult << "} \n";
            
            std::stringstream ss1;            
            ss1 << "results_" << idx << "_first.txt";
            std::string firstMappingsFileName = ss1.str();
            
            std::stringstream ss2;            
            ss2 << "results_" << idx << "_second.txt";
            std::string secondMappingsFileName = ss2.str();

            
            stringToTextFile(firstMappingsFileName, PG_Output::int_list_to_plcc_vertex_mapping_string(pair.first));
            stringToTextFile(secondMappingsFileName, PG_Output::int_list_to_plcc_vertex_mapping_string(pair.second));
            
            idx++;
        }
        
        std::cout << fresult.str()<< "\n";
        
    }
    else {
        //format the output
        std::stringstream result;
        for (std::list<unsigned long>& clique : result_list) {
            /*
            result << "{\n";
            result << "\t\"first\":\n" << PG_Output::get_JSON_vertex_ids_first(pg, clique) << ",\n";
            result << "\t\"second\":\n" << PG_Output::get_JSON_vertex_ids_second(pg, clique) << "\n";
            result << "}\n";
             */
            result << "{ ";
            result << " \"first\": " << PG_Output::get_JSON_vertex_ids_first(pg, clique) << ", ";
            result << " \"second\": " << PG_Output::get_JSON_vertex_ids_second(pg, clique) << " ";
            result << "} \n";
        }//end format loop

        //output
        std::cout << result.str()<< "\n";
    }
}//end main
