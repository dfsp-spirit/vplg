/*
 * File:   newtestclass.cpp
 * Author: ben
 *
 * Created on Apr 28, 2015, 2:59:20 PM
 */

#include "newtestclass.h"
using namespace boost;


// needs several mock objects: Graph, GML-File (broken and working)

//Tests should start with the more basic classes and objects
//provisional order:    GraphService (DONE), GMLParser, GraphPrinter, ProteinGraphService,
//                      GMLptglParser, GraphPTGLPrinter, GraphletCounts, main,
//                      Database, oldFunctions


CPPUNIT_TEST_SUITE_REGISTRATION(newtestclass);

newtestclass::newtestclass() {
}

newtestclass::~newtestclass() {
}

void newtestclass::setUp() {
    
    // first graph mock object, a graph with just one node
    vi.id = 0;
    vi.label = "TestVertex";
    vi.properties["sse_type"] = "E";
    v = add_vertex(vi, oneNodeGraph);
    oneNodeGraph[graph_bundle].id = 0;
    oneNodeGraph[graph_bundle].label = "TestGraph";
    oneNodeGraph[graph_bundle].properties["testProperty"] = "TESTTEST";
    
    // second graph mock object, a graph with two nodes, which are connected
    // via 1 edge
    v = add_vertex(vi, twoNodesGraph1);

    // node for second mock object
    ui.id = 1;
    ui.label = "TestVertex2";
    ui.properties["sse_type"] = "H";
    
    // constructing the two node graph with the edge
    add_vertex(ui, twoNodesGraph1);
    ei.source = 0;
    ei.target = 1;
    add_edge(v, u, ei, twoNodesGraph1);
    
    twoNodesGraph1[graph_bundle].id = 1;
    twoNodesGraph1[graph_bundle].label = "TestGraph2";
    
    
    
    // constructing the third mock graph
    // two nodes, two edges
    fi.source = 1;
    fi.target = 0;
    
    
    v = add_vertex(vi, twoNodesGraph2);
    u = add_vertex(ui, twoNodesGraph2);
    add_edge(v, u, ei, twoNodesGraph2);
    add_edge(u, v, fi, twoNodesGraph2);
    
    
    // a third graph mock object - two vertices, two edges
    twoNodesGraph2[graph_bundle].id = 2;
    twoNodesGraph2[graph_bundle].label = "TestGraph3";
    twoNodesGraph2[graph_bundle].properties["testProperty"] = "HAR_HAR";
    
    
    // corresponding GraphService mock objects
    service1 = GraphService(oneNodeGraph);
    service2 = GraphService(twoNodesGraph1);
    service3 = GraphService(twoNodesGraph2);
    
    // vertex vector for testing
    
    testVertexVector = std::vector<int>();
    testVectorDegDist = testVertexVector;
}

void newtestclass::tearDown() {

}


/* test whether the right string is returned by getName */
void newtestclass::test_getName() {
    
    CPPUNIT_ASSERT(oneNodeGraph[graph_bundle].label.compare("TestGraph") == 0);
    
    CPPUNIT_ASSERT(oneNodeGraph[graph_bundle].label.compare(twoNodesGraph1[graph_bundle].label) < 0);
}


/* DOESN'T TEST. Needs to be made new */
void newtestclass::test_getPropertyValue() {
    
    CPPUNIT_ASSERT(oneNodeGraph[graph_bundle].properties["testProperty"].compare("TESTTEST") == 0);
    CPPUNIT_ASSERT(!(oneNodeGraph[graph_bundle].properties["testProperty"].compare("HAR_HAR") == 0));
    
}

/* test whether the correct number of vertices is returned */
void newtestclass::test_getNumVertices() {
    
    CPPUNIT_ASSERT(service1.getNumVertices() == 1);
    
    CPPUNIT_ASSERT(service2.getNumVertices() == 2);
    
    CPPUNIT_ASSERT(!(service2.getNumVertices() == service1.getNumVertices()));
    
    CPPUNIT_ASSERT(service2.getNumVertices() == service3.getNumVertices());
}

/* test whether the correct edges are returned */
void newtestclass::test_getEdges() {
    
    // test, if getEdges returns the right number of edges for the graph with just one vertex
    testEdgeVector = std::vector<std::pair<int,int>>();
    
    CPPUNIT_ASSERT(service1.getEdges() == testEdgeVector);
    
    CPPUNIT_ASSERT(!(service2.getEdges() == testEdgeVector));
    
    CPPUNIT_ASSERT(!(service2.getEdges() == service3.getEdges()));
    
    
}


/* test whether the correct property keys are returned */
void newtestclass::test_getGraphProperties() {
    
    CPPUNIT_ASSERT(!(service1.getGraphProperties() == service2.getGraphProperties()));
    CPPUNIT_ASSERT(service1.getGraphProperties() == service3.getGraphProperties());
}


/* test whether the correct numbers of edges are returned */
void newtestclass::test_getNumEdges() {
    
    CPPUNIT_ASSERT(service1.getNumEdges() == 0);
    CPPUNIT_ASSERT(!(service1.getNumEdges() == service2.getNumEdges()));
    CPPUNIT_ASSERT(service2.getNumEdges() == 1);
}

/* test whether the right vector of vertex indices is returned */
void newtestclass::test_getVertices() {
    
    testVertexVector.push_back(0);
    
    CPPUNIT_ASSERT(service1.getVertices() == testVertexVector);
    
    CPPUNIT_ASSERT(!(service2.getVertices() == testVertexVector));
    
}

/* test whether the right degree distribution is returned */
void newtestclass::test_computeDegreeDist() {
    
    testVectorDegDist.push_back(1);
    testVectorDegDist.push_back(0);
    
    CPPUNIT_ASSERT(service2.computeDegreeDist() == testVectorDegDist);
    CPPUNIT_ASSERT(!(service3.computeDegreeDist() == testVectorDegDist));
    
    
}