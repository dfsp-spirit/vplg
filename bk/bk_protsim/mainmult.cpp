/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "Graph.h"
#include "GMLptglProteinParser.h"
#include "ProductGraph.h"
#include "BronKerbosch.h"
#include "BK_Output.h"
#include "PG_Output.h"
#include "MultAlign.h"
#include <iostream>
#include <ctime>
#include <algorithm>




// should be <

bool edgecomp (const Graph* a, const Graph* b) {
    return boost::num_edges(*a) > boost::num_edges(*b);
}

int main (int argc, char** argv) {
    
    //check for sufficient parameters   
    if (argc <= 3) { return 1; }
    
    
    std::vector<Graph*> graphs             = std::vector<Graph*>       (argc-1);
    std::vector<ProductGraph*> products    = std::vector<ProductGraph*>(argc-1); products[0]   = 0;
    std::vector<BronKerbosch*> alignments  = std::vector<BronKerbosch*>(argc-1); alignments[0] = 0;
    std::clock_t start;
    
    
    
    try{
        
        
        
        //parse and save the graphs in a vector
        for (int i = 1; i < argc; ++i){
            Graph* g = new Graph();
            *g = GMLptglProteinParser(argv[i]).graph;
            graphs[i-1] = g;
        } //end for all input files  
        std::sort(graphs.begin(), graphs.end(),edgecomp);
        for (auto p : graphs) {
            if (p) { std::cout << (*p)[boost::graph_bundle].properties["pdb_id"] << (*p)[boost::graph_bundle].properties["chain_id"] << std::endl;}}
        std::cout << std::endl;


        
        //create product graphs and save in a vector 
        start = std::clock();
        for (int i = 1; i < argc-1; ++i) {
            ProductGraph* p = new ProductGraph(*graphs[0],*graphs[i]);
            products[i] = p;
            p->run();
            std::cout <<"PG "<< i << " with " << boost::num_edges(p->getProductGraph()) << " edges and "<< boost::num_vertices(p->getProductGraph())  <<  " vertices." << std::endl;
        } //end for all graphs
        std::cout << "Prod Time: " << (std::clock() - start) / (double)(CLOCKS_PER_SEC / 1000) << " ms" << std::endl;
        std::cout << std::endl;


        
        //compute pairwise alignments and save in a vector
        start = std::clock();
        for (int i = 1; i < argc-1; ++i) {
            BronKerbosch* bk = new BronKerbosch(products[i]->getProductGraph());
            alignments[i] = bk;
            bk->run_c();
            bk->set_result(PG_Output::filter_iso(*products[i],bk->get_result_list()));
            std::cout << "Alignment"  << i << " has " << BK_Output::get_result_all(*bk).size() <<" cliques." << std::endl;
        } //end for all product graphs
        std::cout << "Bron Time: " << (std::clock() - start) / (double)(CLOCKS_PER_SEC / 1000) << " ms" << std::endl;
        std::cout << std::endl;

        
        
        //compute multiple alignment
        start = std::clock();
        MultAlign multi(graphs, products, alignments, argc-1, "output.txt");
        multi.run();
        std::cout << "Mult Time: " << (std::clock() - start) / (double)(CLOCKS_PER_SEC / 1000) << " ms" << std::endl;
        std::cout << multi.num_cliques();
    
    
    } //end try
    catch(const std::exception& e){
        std::cout << e.what();
        exit(1);
    } //end catch
 
    //clean up
    for (auto p : alignments){
        if (p) { delete p; }}
    for (auto p : products){
        if (p) { delete p; }}
    for (auto p : graphs){
        if (p) { delete p; }}
}