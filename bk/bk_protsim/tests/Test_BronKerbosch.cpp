/*
 * File:   Test_BronKerbosch.cpp
 * Author: julian
 *
 * Created on Jun 12, 2015, 2:45:19 PM
 */

#include "Test_BronKerbosch.h"



CPPUNIT_TEST_SUITE_REGISTRATION(Test_BronKerbosch);

Test_BronKerbosch::Test_BronKerbosch() {
}

Test_BronKerbosch::~Test_BronKerbosch() {
}

void Test_BronKerbosch::setUp() {
    Graph_p pro(8);
    pro[addEdge(0,2,pro).first].label = "z";
    pro[addEdge(0,4,pro).first].label = "u";
    pro[addEdge(0,6,pro).first].label = "u";
    pro[addEdge(0,7,pro).first].label = "z";
    pro[addEdge(1,5,pro).first].label = "z";
    pro[addEdge(1,6,pro).first].label = "z";
    pro[addEdge(1,7,pro).first].label = "u";
    pro[addEdge(2,4,pro).first].label = "z";
    pro[addEdge(2,6,pro).first].label = "z";
    pro[addEdge(2,7,pro).first].label = "u";
    pro[addEdge(3,4,pro).first].label = "z";
    pro[addEdge(3,5,pro).first].label = "z";
    pro[addEdge(6,7,pro).first].label = "z";
    this->p = pro;
}

void Test_BronKerbosch::tearDown() {
}

void Test_BronKerbosch::testRun() {
    BronKerbosch bk(this->p);
    bk.run();
    std::list<std::list<unsigned long>> result = bk.get_result_list();
    
    std::list<std::list<unsigned long>> check = {{3,4},{3,5},{1,5},{1,6,7},{0,2,4},{0,7,6,2}};
    
    CPPUNIT_ASSERT(check == result);
}

