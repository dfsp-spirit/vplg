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
    
    test_prop_vector = std::vector<std::string>();
    test_prop_vector.push_back("testProperty");

    
    
    // corresponding GraphService mock objects
    service1 = GraphService(oneNodeGraph);

    // vertex vector for testing
    
    testVertexVector = std::vector<int>();
    testVectorDegDist = testVertexVector;
    
    
    ui3.id = 0;
    vi3.id = 1;
    wi3.id = 2;
    
    ei3.source = 0;
    ei3.target = 1;
    
    fi3.source = 1;
    fi3.target = 2;
    
    gi3.source = 2;
    gi3.target = 0;
    
    u3 = add_vertex(ui3, threeNodesGraph);
    v3 = add_vertex(vi3, threeNodesGraph);
    w3 = add_vertex(wi3, threeNodesGraph);
    
    add_edge(u3,v3,ei3,threeNodesGraph);
    add_edge(v3,w3,fi3,threeNodesGraph);
    add_edge(w3,u3,gi3,threeNodesGraph);
    
    test_adjacent_vector = std::vector<int>();
    test_adjacent_vector.push_back(0);
    test_adjacent_vector.push_back(1);
    test_adjacent_vector.push_back(2);
    
    service2 = GraphService(threeNodesGraph);
    
    test_adjacent_vector1 = std::vector<int>();
    test_adjacent_vector1.push_back(1);
    test_adjacent_vector1.push_back(0);
    test_adjacent_vector1.push_back(2);
    
    test_adjacent_vector2 = std::vector<int>();
    test_adjacent_vector2.push_back(2);
    test_adjacent_vector2.push_back(1);
    test_adjacent_vector2.push_back(0);
    
    test_adj_all_vector = std::vector<std::vector<int>>();
    test_adj_all_vector.push_back(test_adjacent_vector);
    test_adj_all_vector.push_back(test_adjacent_vector1);
    test_adj_all_vector.push_back(test_adjacent_vector2);
    
}

void newtestclass::tearDown() {

}


/* test whether the right string is returned by getName */
void newtestclass::test_get_label() {
    
    
    CPPUNIT_ASSERT(service1.get_label().compare("TestGraph") == 0);
    
}


/* DOESN'T TEST. Needs to be made new */
void newtestclass::test_getPropertyValue() {
    
    CPPUNIT_ASSERT(service1.getPropertyValue("testProperty").compare("TESTTEST") == 0);
    
}

/* test whether the correct number of vertices is returned */
void newtestclass::test_getNumVertices() {
    
    CPPUNIT_ASSERT(service1.getNumVertices() == 1);
    
   
}

/* test whether the correct edges are returned */
void newtestclass::test_getEdges() {
    
    // test, if getEdges returns the right number of edges for the graph with just one vertex
    testEdgeVector = std::vector<std::pair<int,int>>();
    
    CPPUNIT_ASSERT(service1.getEdges() == testEdgeVector);
    

    
    
}


/* test whether the correct property keys are returned */
void newtestclass::test_getGraphProperties() {
    
    
    
    
    CPPUNIT_ASSERT(service1.getGraphProperties() == test_prop_vector);

}


/* test whether the correct numbers of edges are returned */
void newtestclass::test_getNumEdges() {
    
    CPPUNIT_ASSERT(service1.getNumEdges() == 0);
}

/* test whether the right vector of vertex indices is returned */
void newtestclass::test_getVertices() {
    
    testVertexVector.push_back(0);
    
    CPPUNIT_ASSERT(service1.getVertices() == testVertexVector);
    
    
}

/* test whether the right degree distribution is returned */
void newtestclass::test_computeDegreeDist() {
    

    std::vector<int> test_vector = std::vector<int>();
    test_vector.push_back(0);
    
    CPPUNIT_ASSERT(service1.computeDegreeDist() == test_vector);
    
}

void newtestclass::test_get_adjacent() {
    
    CPPUNIT_ASSERT(service2.get_adjacent(0) == test_adjacent_vector);
    
}

void newtestclass::test_get_adjacent_all() {
    
    CPPUNIT_ASSERT(service2.get_adjacent_all() == test_adj_all_vector);
}