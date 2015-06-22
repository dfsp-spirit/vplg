/* 
 * File:   main.cpp
 * Author: julian
 *
 * Created on June 16, 2015, 2:56 PM
 */

#include <iostream>
#include "GMLptglProteinParser.h"
#include "ProductGraph.h"
#include "BronKerbosch.h"
#include "BK_Output.h"
#include "PG_Output.h"

/*
 * 
 */
int main(int argc, char** argv) {
    
    std::string apptag = "[BK] ";
    
    std::cout << apptag << "Bron Kerbosch-based graph similarity\n";
    std::cout << apptag << "=====================================\n";
    std::cout << apptag << "written by Julian\n";
    
    if (argc < 3) {
        std::cout << apptag << "insufficient parameters \n";
        std::cout << apptag << "use Filename1.gml Filename2.gml output_parameter \n";
        std::cout << apptag << "The output parameter can be \"-a\" ,\"-l\", \"-s number\" (not case sensitive)\n";
        std::cout << apptag << "\t-a: Output all Cliques\n";
        std::cout << apptag << "\t-l: Output only the Cliques of maximum size\n";
        std::cout << apptag << "\t-s: Output all Cliques larger than a given size. Has to be followed by a number (e.g. ...-s 5)\n";
        std::cout << apptag << "If no output paramter is given -a will be used.\n";
        std::cout << apptag << "exampel call: " << argv[0] << " example1.gml example2.gml -s 8\n";
        std::cout << apptag << "This will output all cliques larger than 8 Vertices\n";
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
            if (argc >= 5) {size = atoi(argv[4]);}
            result_list = BK_Output::get_result_larger_than(bk, size);
        }//end -s
        else if ((strcmp(argv[3],"-a") == 0) || (strcmp(argv[3],"-A") == 0) ) {
            result_list = BK_Output::get_result_all(bk);
        }//end -a
        else {
            std::cout << "unkonw output parameter, using default (all cliques).\n";
            result_list = BK_Output::get_result_all(bk);
        }//end default
    } //end output param exists
    else {
        std::cout << "no output parameter, using default (all cliques).\n";
        result_list = BK_Output::get_result_all(bk);
    } //end no output param
    
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
}//end main
