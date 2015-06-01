/* 
 * File:   main.cpp
 * Author: ts
 *
 * Created on April 13, 2015, 12:42 PM
 */

#include <cstdlib>
#include "Graph.h"
#include "ProductGraph.h"
#include "BronKerbosch.h"

using namespace std;

/*
 * 
 */
int main(int argc, char** argv) {
    
    Graph fst(5);
    boost::add_edge(1,3,fst);
    boost::add_edge(1,4,fst);
    boost::add_edge(4,5,fst);
    Graph sec(4);
    boost::add_edge(1,3,sec);
    boost::add_edge(1,4,sec);
    boost::add_edge(2,3,sec);
    boost::add_edge(3,4,sec);
    ProductGraph prd = ProductGraph(fst,sec);
    BronKerbosch bk;
    bk.run(prd.getProductGraph());
    

    return 0;
}

