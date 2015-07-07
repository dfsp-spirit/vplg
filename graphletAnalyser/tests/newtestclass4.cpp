/*
 * File:   newtestclass4.cpp
 * Author: ben
 *
 * Created on Jul 7, 2015, 4:53:02 PM
 */

#include "newtestclass4.h"


CPPUNIT_TEST_SUITE_REGISTRATION(newtestclass4);

newtestclass4::newtestclass4() {
}

newtestclass4::~newtestclass4() {
}

void newtestclass4::setUp() {
    
    g[graph_bundle].properties["graph_type"] = "albe";
    testNumber = 3;
    teststring0 = "albe";
    serv = ProteinGraphService(g);
    
}

void newtestclass4::tearDown() {
}

void newtestclass4::test_getGraphTypeStr() {
    
    std::string tst = serv.getGraphTypeString();
    
    CPPUNIT_ASSERT(tst.compare(teststring0) == 0);
}

void newtestclass4::test_getGraphTypeInt() {
    
    //CPPUNIT_ASSERT(true);
    CPPUNIT_ASSERT(serv.getGraphTypeInt(teststring0) == testNumber);
}