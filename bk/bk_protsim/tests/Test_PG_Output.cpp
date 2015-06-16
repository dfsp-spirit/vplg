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
    Graph fst(4);
    fst[0].label = "h";fst[1].label = "e";fst[2].label = "e";fst[3].label = "e";
    fst[0].id = 10;fst[1].id = 20;fst[2].id = 30;fst[3].id = 40;
    fst[addEdge(0,1,fst).first].label = "p";
    fst[addEdge(1,2,fst).first].label = "m";
    fst[addEdge(2,3,fst).first].label = "m";
    fst[addEdge(3,0,fst).first].label = "m";
    this->f = fst;
    
    Graph sec(5);
    sec[0].label = "h";sec[1].label = "e";sec[2].label = "e";sec[3].label = "e";sec[4].label = "e";
    sec[0].id = 11;sec[1].id = 22;sec[2].id = 33;sec[3].id = 44;sec[4].id = 55;
    sec[addEdge(0,1,sec).first].label = "p";
    sec[addEdge(1,2,sec).first].label = "m";
    sec[addEdge(2,3,sec).first].label = "m";
    sec[addEdge(3,0,sec).first].label = "m";
    sec[addEdge(4,2,sec).first].label = "m";
    this->s = sec;
    
    Graph_p pro(8);
    pro[0].edgeFst = boost::edge(3,0,fst).first; pro[0].edgeSec = boost::edge(3,0,sec).first;
    pro[1].edgeFst = boost::edge(2,3,fst).first; pro[1].edgeSec = boost::edge(4,2,sec).first;
    pro[2].edgeFst = boost::edge(2,3,fst).first; pro[2].edgeSec = boost::edge(2,3,sec).first;
    pro[3].edgeFst = boost::edge(2,3,fst).first; pro[3].edgeSec = boost::edge(1,2,sec).first;
    pro[4].edgeFst = boost::edge(1,2,fst).first; pro[4].edgeSec = boost::edge(4,2,sec).first;
    pro[5].edgeFst = boost::edge(1,2,fst).first; pro[5].edgeSec = boost::edge(2,3,sec).first;
    pro[6].edgeFst = boost::edge(1,2,fst).first; pro[6].edgeSec = boost::edge(1,2,sec).first;
    pro[7].edgeFst = boost::edge(0,1,fst).first; pro[7].edgeSec = boost::edge(0,1,sec).first;
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
    
    this->clique = {0,2,4};
}

void Test_PG_Output::tearDown() {
}

void Test_PG_Output::testGet_common_first() {
    ProductGraph pg(f,s);
    pg.setProductGraph(p);
    std::list<unsigned long> result = PG_Output::get_common_first(pg, clique);
    result.sort();
    std::list<unsigned long> check = {0,1,2,3};
    CPPUNIT_ASSERT(result == check);
}

void Test_PG_Output::testGet_common_second() {
    ProductGraph pg(f,s);
    pg.setProductGraph(p);
    std::list<unsigned long> result = PG_Output::get_common_second(pg, clique);
    result.sort();
    std::list<unsigned long> check = {0,2,3,4};
    CPPUNIT_ASSERT(result == check);
}

void Test_PG_Output::testGet_vertex_ids_first() {
    ProductGraph pg(f,s);
    pg.setProductGraph(p);
    std::list<int> result = PG_Output::get_vertex_ids_first(pg, clique);
    result.sort();
    std::list<int> check = {10,20,30,40};
    CPPUNIT_ASSERT(result == check);
}
    
void Test_PG_Output::testGet_vertex_ids_second() {
    ProductGraph pg(f,s);
    pg.setProductGraph(p);
    std::list<int> result = PG_Output::get_vertex_ids_second(pg, clique);
    result.sort();
    std::list<int> check = {11,33,44,55};
    CPPUNIT_ASSERT(result == check);
}

void Test_PG_Output::testGet_JSON_vertex_ids_first() {
   ProductGraph pg(f,s);
    pg.setProductGraph(p);
    std::string result = PG_Output::get_JSON_vertex_ids_first(pg, clique);
    std::string check = "[\n\t10,\n\t20,\n\t30,\n\t40\n]";
    CPPUNIT_ASSERT(result == check);
}

void Test_PG_Output::testGet_JSON_vertex_ids_second() {
   ProductGraph pg(f,s);
    pg.setProductGraph(p);
    std::string result = PG_Output::get_JSON_vertex_ids_second(pg, clique);
    std::string check = "[\n\t11,\n\t33,\n\t44,\n\t55\n]";
    CPPUNIT_ASSERT(result == check);
}