/*
 * File:   newtestclass5.cpp
 * Author: ben
 *
 * Created on Jul 7, 2015, 5:02:11 PM
 */

#include "newtestclass5.h"

using namespace boost;

CPPUNIT_TEST_SUITE_REGISTRATION(newtestclass5);

newtestclass5::newtestclass5() {
}

newtestclass5::~newtestclass5() {
}

void newtestclass5::setUp() {
    
    
    vi.id = 0;
    vi.properties["sse_type"] = "H";
    vd = add_vertex(vi,g);
    
    printer = GraphPTGLPrinter(g);
    teststring0 = "Iterate over the vertices and print their properties:\n  vertex  0 has  sse_type = H\n\n";
    
}

void newtestclass5::tearDown() {
}

void newtestclass5::test_printVertices() {
    
    std::string tst = printer.printVertices();
    
    
    CPPUNIT_ASSERT(tst.compare(teststring0) == 0);
    
    
    
}