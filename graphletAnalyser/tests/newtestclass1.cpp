/*
 * File:   newtestclass1.cpp
 * Author: ben
 *
 * Created on May 20, 2015, 2:52:19 PM
 */

#include "newtestclass1.h"
using namespace boost;


CPPUNIT_TEST_SUITE_REGISTRATION(newtestclass1);

newtestclass1::newtestclass1() {
}

newtestclass1::~newtestclass1() {
}

void newtestclass1::setUp() {
    
    //defining vertices:
    vi.id = 0;
    ui.id = 1;
    wi.id = 2;
    
    //defining edges
    ei.source = 0;
    ei.target = 1;
    
    fi.source = 1;
    fi.target = 2;
    
    gi.source = 2;
    gi.target = 0;
    
    //defining graph
    
    threeNodesGraph[graph_bundle].id = 42;
    
    v = add_vertex(vi, threeNodesGraph);
    u = add_vertex(ui, threeNodesGraph);
    w = add_vertex(wi, threeNodesGraph);
    
    ed = add_edge(v,u,ei,threeNodesGraph).first;
    fd = add_edge(u,w,fi,threeNodesGraph).first;
    gd = add_edge(w,v,gi,threeNodesGraph).first;
    
    
    // defining the test string and the printer
    testStringAdjacent = "   0:   1   2 \n";
    
    
    service = GraphService(threeNodesGraph);
    
    printer = GraphPrinter(threeNodesGraph);
    
}

void newtestclass1::tearDown() {

}


void newtestclass1::test_printAdjacent() {
    
    std::string loeres = printer.printAdjacent(0);


    
    CPPUNIT_ASSERT(loeres.compare(testStringAdjacent) == 0);
    

    
    
}

