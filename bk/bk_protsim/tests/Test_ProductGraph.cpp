/*
 * File:   Test_ProductGraph.cpp
 * Author: julian
 *
 * Created on Jun 12, 2015, 3:02:52 PM
 */

#include "Test_ProductGraph.h"


CPPUNIT_TEST_SUITE_REGISTRATION(Test_ProductGraph);

Test_ProductGraph::Test_ProductGraph() {
}

Test_ProductGraph::~Test_ProductGraph() {
}

void Test_ProductGraph::setUp() {
    Graph fst(4);
    fst[0].label = "h";fst[1].label = "e";fst[2].label = "e";fst[3].label = "e";
    fst[addEdge(0,1,fst).first].label = "p";
    fst[addEdge(1,2,fst).first].label = "m";
    fst[addEdge(2,3,fst).first].label = "m";
    fst[addEdge(3,0,fst).first].label = "m";
    this->f = fst;
    
    Graph sec(5);
    sec[0].label = "h";sec[1].label = "e";sec[2].label = "e";sec[3].label = "e";sec[4].label = "e";
    sec[addEdge(0,1,sec).first].label = "p";
    sec[addEdge(1,2,sec).first].label = "m";
    sec[addEdge(2,3,sec).first].label = "m";
    sec[addEdge(3,0,sec).first].label = "m";
    sec[addEdge(4,2,sec).first].label = "m";
    this->s = sec;
}

void Test_ProductGraph::tearDown() {
}

void Test_ProductGraph::testRun() {
    /*
     * Test with WAY TO MUTCH calculations.
     * almost needs a test to verify this test works...
     * however graph isomorphism is (most likely) NP-Hard so this test cant be simple
     * it currently only checks if the result graph has the same number of vertices and edges as the check graph
     * and also if for each vertex in the result graph there exists a vertex in the check graph that represents the same
     * mapping of edges between the original graphs (see setUp()).
     */
    ProductGraph pg(this->f, this->s);
    pg.run();
    Graph_p result = pg.getProductGraph();
    
    Graph_p check(8);
    check[0].edgeFst = boost::edge(3,0,f).first; check[0].edgeSec = boost::edge(3,0,s).first;
    check[1].edgeFst = boost::edge(2,3,f).first; check[1].edgeSec = boost::edge(4,2,s).first;
    check[2].edgeFst = boost::edge(2,3,f).first; check[2].edgeSec = boost::edge(2,3,s).first;
    check[3].edgeFst = boost::edge(2,3,f).first; check[3].edgeSec = boost::edge(1,2,s).first;
    check[4].edgeFst = boost::edge(1,2,f).first; check[4].edgeSec = boost::edge(4,2,s).first;
    check[5].edgeFst = boost::edge(1,2,f).first; check[5].edgeSec = boost::edge(2,3,s).first;
    check[6].edgeFst = boost::edge(1,2,f).first; check[6].edgeSec = boost::edge(1,2,s).first;
    check[7].edgeFst = boost::edge(0,1,f).first; check[7].edgeSec = boost::edge(0,1,s).first;
    check[addEdge(0,2,check).first].label = "z";
    check[addEdge(0,4,check).first].label = "u";
    check[addEdge(0,6,check).first].label = "u";
    check[addEdge(0,7,check).first].label = "z";
    check[addEdge(1,5,check).first].label = "z";
    check[addEdge(1,6,check).first].label = "z";
    check[addEdge(1,7,check).first].label = "u";
    check[addEdge(2,4,check).first].label = "z";
    check[addEdge(2,6,check).first].label = "z";
    check[addEdge(2,7,check).first].label = "u";
    check[addEdge(3,4,check).first].label = "z";
    check[addEdge(3,5,check).first].label = "z";
    check[addEdge(6,7,check).first].label = "z";
    
    CPPUNIT_ASSERT(boost::num_vertices(result)==boost::num_vertices(check));
    CPPUNIT_ASSERT(boost::num_edges(result)   ==boost::num_edges(check));
    
    VertexIterator_p r,re,c,ce;
    
    for (boost::tie(r,re) = boost::vertices(result);r!=re;++r) {
        for(boost::tie(c,ce) = boost::vertices(check); c!=ce; ++c) {
            if ((result[*r].edgeFst == check[*c].edgeFst) && (result[*r].edgeSec == check[*c].edgeSec))
                break;
        }
        CPPUNIT_ASSERT(c!=ce);
    }
}

