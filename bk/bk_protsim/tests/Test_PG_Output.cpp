/*
 * File:   Test_PG_Output.cpp
 * Author: julian
 *
 * Created on Jun 12, 2015, 1:57:00 PM
 */

#include "Test_PG_Output.h"


CPPUNIT_TEST_SUITE_REGISTRATION(Test_PG_Output);

Test_PG_Output::Test_PG_Output() {
}

Test_PG_Output::~Test_PG_Output() {
}

void Test_PG_Output::setUp() {
    Graph fst(3);
    fst[0].label = "e";fst[1].label = "e";fst[2].label = "e";
    fst[addEdge(0,1,fst).first].label = "p";
    fst[addEdge(1,2,fst).first].label = "m";
    this->f = fst;
    
    Graph sec(5);
    sec[0].label = "e";sec[1].label = "e";sec[2].label = "e";sec[3].label = "e";sec[4].label = "e";
    sec[addEdge(0,1,sec).first].label = "m";
    sec[addEdge(1,2,sec).first].label = "p";
    sec[addEdge(0,3,sec).first].label = "m";
    sec[addEdge(3,4,sec).first].label = "m";
    this->s = sec;
    
    Graph_p pro(4);
    pro[0].edgeFst = boost::edge(1,2,f).first; pro[0].edgeSec = boost::edge(0,1,s).first;
    pro[1].edgeFst = boost::edge(0,1,f).first; pro[1].edgeSec = boost::edge(1,2,s).first;
    pro[2].edgeFst = boost::edge(0,3,f).first; pro[2].edgeSec = boost::edge(1,2,s).first;
    pro[3].edgeFst = boost::edge(3,4,f).first; pro[3].edgeSec = boost::edge(1,2,s).first;
    pro[addEdge(0,1,pro).first].label = "z";
    this->p = pro;
    
    this->clique = {0,1};
}

void Test_PG_Output::tearDown() {
}

void Test_PG_Output::testGet_common_first() {
    ProductGraph pg(f,s);
    pg.setProductGraph(p);
    std::list<unsigned long> result = PG_Output::get_common_first(pg, clique);
    std::list<unsigned long> check = {0,1,2};
    result.sort(); check.sort();
    CPPUNIT_ASSERT(result == check);
}

void Test_PG_Output::testGet_common_second() {
    ProductGraph pg(f,s);
    pg.setProductGraph(p);
    std::list<unsigned long> result = PG_Output::get_common_second(pg, clique);
    std::list<unsigned long> check = {0,1,2};
    result.sort(); check.sort();
    CPPUNIT_ASSERT(result == check);
}

