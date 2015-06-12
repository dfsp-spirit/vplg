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
}

void Test_ProductGraph::tearDown() {
}

void Test_ProductGraph::testProductGraph() {
    Graph fstGraph;
    Graph secGraph;
    ProductGraph productGraph(fstGraph, secGraph);
    if (true /*check result*/) {
        CPPUNIT_ASSERT(false);
    }
}

