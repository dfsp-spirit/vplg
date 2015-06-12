/* 
 * File:   newmain.cpp
 * Author: julian
 *
 * Created on May 29, 2015, 12:29 PM
 */

#include <iostream>
#include "Graph.h"
#include "GMLptglProteinParser.h"
#include "ProductGraph.h"
#include "BronKerbosch.h"
#include "BK_Output.h"
#include "PG_Output.h"


using namespace std;

/*
 * 
 */
int main(int argc, char** argv) {
   /* 
    //testdata/2pol.gml
    //testdata/1bib.gml
    GMLptglProteinParser pa1 = GMLptglProteinParser("testdata/1bib.gml");
    GMLptglProteinParser pa2 = GMLptglProteinParser("testdata/2pol.gml");
    Graph fst = pa1.graph;
    Graph sec = pa2.graph;
    */
    
    /*
    Graph fst(3);
    fst[0].label = "e";fst[1].label = "e";fst[2].label = "e";
    fst[addEdge(0,1,fst).first].label = "p";
    fst[addEdge(1,2,fst).first].label = "m";
    Graph sec(5);
    sec[0].label = "e";sec[1].label = "e";sec[2].label = "e";sec[3].label = "e";sec[4].label = "e";
    sec[addEdge(0,1,sec).first].label = "m";
    sec[addEdge(1,2,sec).first].label = "p";
    sec[addEdge(0,3,sec).first].label = "m";
    sec[addEdge(3,4,sec).first].label = "m";
     */

    Graph fst(3);
    fst[0].label = "e";fst[1].label = "e";fst[2].label = "e";
    fst[addEdge(0,1,fst).first].label = "m";
    fst[addEdge(1,2,fst).first].label = "m";
    fst[addEdge(2,0,fst).first].label = "m";
    Graph sec(3);
    sec[0].label = "e";sec[1].label = "e";sec[2].label = "e";
    sec[addEdge(0,1,sec).first].label = "m";
    sec[addEdge(1,2,sec).first].label = "m";
    sec[addEdge(2,0,sec).first].label = "m";
     
   
   
    ProductGraph prd = ProductGraph(fst,sec);
    Graph_p p =prd.getProductGraph();
    cout <<"[PG]   Computed product graph.\n" 
            <<"[PG]    vertices : " << boost::num_vertices(p) << "\n"
            <<"[PG]    edges  :  " << boost::num_edges(p)   << "\n";
    
    /*
    EdgeIterator_p ei,ee;
    for (boost::tie(ei,ee) = boost::edges(p);  ei != ee; ++ei)  {
        cout << "[PG]    " 
             << source(*ei,p) <<":"<< p[boost::source(*ei,p)].edgeFst<<" , "<<p[boost::source(*ei,p)].edgeSec
             << " - "
             << target(*ei,p) <<":"<< p[boost::target(*ei,p)].edgeFst<<" , "<<p[boost::target(*ei,p)].edgeSec
             << " : " << p[*ei].label
             << "\n";
    }
    */
    
 
    
    BronKerbosch bk(p);
    bk.run();
    
    
    

    
    std::vector<int> r_pattern = BK_Output::get_formated_pattern(bk);
    int x =0;
    for (int count : r_pattern) {
        cout << "[BK]    Cliques of size: " << x << " : " << count << "\n";
        ++x;
    }
    
    
    cout << "\n\n";
    cout << BK_Output::get_JSON_largest(bk);
    
    cout << "\n\n";
    for(list<unsigned long>& x : bk.get_result_list()) {
        for (unsigned long l : x) {
            cout << l << " , ";
        }
        cout << "\n";
    }
    
    
    
    
    return 0;
}

/*
[GA]   Parsed input graph.
[GA]     The input gml file "testdata/2pol.gml" was parsed and protein graph was constructed.
[GA]     This graph has 33 vertices and 35 edges.
[GA]     The parser log file is "/home/julian/develop/vplg-full-sf/trunk/bk/bk_protsim/testdata/parse_testdata/2pol.gml.log".
[GA]   Parsed input graph.
[GA]     The input gml file "testdata/1bib.gml" was parsed and protein graph was constructed.
[GA]     This graph has 22 vertices and 17 edges.
[GA]     The parser log file is "/home/julian/develop/vplg-full-sf/trunk/bk/bk_protsim/testdata/parse_testdata/1bib.gml.log".

[PG]   Computed product graph.
[PG]    vertices : 252
[PG]    edges  :  22200

[BK]    Cliques of size: 0 : 0
[BK]    Cliques of size: 1 : 21
[BK]    Cliques of size: 2 : 29
[BK]    Cliques of size: 3 : 38
[BK]    Cliques of size: 4 : 31
[BK]    Cliques of size: 5 : 40
[BK]    Cliques of size: 6 : 38
[BK]    Cliques of size: 7 : 31
[BK]    Cliques of size: 8 : 23
[BK]    Cliques of size: 9 : 14
[BK]    Cliques of size: 10 : 6

RUN FINISHED; exit value 0; real time: 2s; user: 20ms; system: 2s
 */

/*
[GA]   Parsed input graph.
[GA]     The input gml file "testdata/1bib.gml" was parsed and protein graph was constructed.
[GA]     This graph has 22 vertices and 17 edges.
[GA]     The parser log file is "/home/julian/develop/vplg-full-sf/trunk/bk/bk_protsim/testdata/parse_testdata/1bib.gml.log".
[GA]   Parsed input graph.
[GA]     The input gml file "testdata/1bib.gml" was parsed and protein graph was constructed.
[GA]     This graph has 22 vertices and 17 edges.
[GA]     The parser log file is "/home/julian/develop/vplg-full-sf/trunk/bk/bk_protsim/testdata/parse_testdata/1bib.gml.log".

[PG]   Computed product graph.
[PG]    vertices : 125
[PG]    edges  :  5174

[BK]    Cliques of size: 0 : 0
[BK]    Cliques of size: 1 : 24
[BK]    Cliques of size: 2 : 14
[BK]    Cliques of size: 3 : 21
[BK]    Cliques of size: 4 : 4
[BK]    Cliques of size: 5 : 2
[BK]    Cliques of size: 6 : 6
[BK]    Cliques of size: 7 : 9
[BK]    Cliques of size: 8 : 2
[BK]    Cliques of size: 9 : 1
[BK]    Cliques of size: 10 : 2
[BK]    Cliques of size: 11 : 0
[BK]    Cliques of size: 12 : 1

RUN FINISHED; exit value 0; real time: 330ms; user: 0ms; system: 320ms
 */


//After changing data structure

/*
[GA]   Parsed input graph.
[GA]     The input gml file "testdata/1bib.gml" was parsed and protein graph was constructed.
[GA]     This graph has 22 vertices and 17 edges.
[GA]     The parser log file is "/home/julian/develop/vplg-full-sf/trunk/bk/bk_protsim/testdata/parse_testdata/1bib.gml.log".
[GA]   Parsed input graph.
[GA]     The input gml file "testdata/1bib.gml" was parsed and protein graph was constructed.
[GA]     This graph has 22 vertices and 17 edges.
[GA]     The parser log file is "/home/julian/develop/vplg-full-sf/trunk/bk/bk_protsim/testdata/parse_testdata/1bib.gml.log".

[PG]   Computed product graph.
[PG]    vertices : 125
[PG]    edges  :  5174

[BK]    Cliques of size: 0 : 0
[BK]    Cliques of size: 1 : 24
[BK]    Cliques of size: 2 : 14
[BK]    Cliques of size: 3 : 21
[BK]    Cliques of size: 4 : 4
[BK]    Cliques of size: 5 : 2
[BK]    Cliques of size: 6 : 6
[BK]    Cliques of size: 7 : 9
[BK]    Cliques of size: 8 : 2
[BK]    Cliques of size: 9 : 1
[BK]    Cliques of size: 10 : 2
[BK]    Cliques of size: 11 : 0
[BK]    Cliques of size: 12 : 1

RUN FINISHED; exit value 0; real time: 270ms; user: 0ms; system: 230ms
*/

/*
[GA]   Parsed input graph.
[GA]     The input gml file "testdata/1bib.gml" was parsed and protein graph was constructed.
[GA]     This graph has 22 vertices and 17 edges.
[GA]     The parser log file is "/home/julian/develop/vplg-full-sf/trunk/bk/bk_protsim/testdata/parse_testdata/1bib.gml.log".
[GA]   Parsed input graph.
[GA]     The input gml file "testdata/2pol.gml" was parsed and protein graph was constructed.
[GA]     This graph has 33 vertices and 35 edges.
[GA]     The parser log file is "/home/julian/develop/vplg-full-sf/trunk/bk/bk_protsim/testdata/parse_testdata/2pol.gml.log".

[PG]   Computed product graph.
[PG]    vertices : 252
[PG]    edges  :  22200

[BK]    Cliques of size: 0 : 0
[BK]    Cliques of size: 1 : 21
[BK]    Cliques of size: 2 : 29
[BK]    Cliques of size: 3 : 38
[BK]    Cliques of size: 4 : 31
[BK]    Cliques of size: 5 : 40
[BK]    Cliques of size: 6 : 38
[BK]    Cliques of size: 7 : 31
[BK]    Cliques of size: 8 : 23
[BK]    Cliques of size: 9 : 14
[BK]    Cliques of size: 10 : 6

RUN FINISHED; exit value 0; real time: 1s; user: 20ms; system: 1s
 */