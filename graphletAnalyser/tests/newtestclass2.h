/*
 * File:   newtestclass2.h
 * Author: ben
 *
 * Created on May 21, 2015, 1:55:53 PM
 */

#ifndef NEWTESTCLASS2_H
#define	NEWTESTCLASS2_H

#include <cppunit/extensions/HelperMacros.h>
#include "../GraphletCounts.h"

using namespace std;

class newtestclass2 : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE(newtestclass2);
    
    

    CPPUNIT_TEST(test_count_connected_2_graphlets);

    
    CPPUNIT_TEST(test_count_connected_3_graphlets0);
    CPPUNIT_TEST(test_count_connected_3_graphlets1);
    
    
    CPPUNIT_TEST(test_count_connected_4_graphlets0);
    CPPUNIT_TEST(test_count_connected_4_graphlets1);
    CPPUNIT_TEST(test_count_connected_4_graphlets2);
    CPPUNIT_TEST(test_count_connected_4_graphlets3);
    CPPUNIT_TEST(test_count_connected_4_graphlets4);
    CPPUNIT_TEST(test_count_connected_4_graphlets5);
    
    
    CPPUNIT_TEST(test_count_connected_5_graphlets0);
    CPPUNIT_TEST(test_count_connected_5_graphlets1);
    CPPUNIT_TEST(test_count_connected_5_graphlets2);
    CPPUNIT_TEST(test_count_connected_5_graphlets3);
    CPPUNIT_TEST(test_count_connected_5_graphlets4);
    CPPUNIT_TEST(test_count_connected_5_graphlets5);
    CPPUNIT_TEST(test_count_connected_5_graphlets6);
    CPPUNIT_TEST(test_count_connected_5_graphlets7);
    CPPUNIT_TEST(test_count_connected_5_graphlets8);
    CPPUNIT_TEST(test_count_connected_5_graphlets9);
    CPPUNIT_TEST(test_count_connected_5_graphlets10);
    CPPUNIT_TEST(test_count_connected_5_graphlets11);
    CPPUNIT_TEST(test_count_connected_5_graphlets12);
    CPPUNIT_TEST(test_count_connected_5_graphlets13);
    CPPUNIT_TEST(test_count_connected_5_graphlets14);
    CPPUNIT_TEST(test_count_connected_5_graphlets15);
    CPPUNIT_TEST(test_count_connected_5_graphlets16);
    CPPUNIT_TEST(test_count_connected_5_graphlets17);
    CPPUNIT_TEST(test_count_connected_5_graphlets18);
    CPPUNIT_TEST(test_count_connected_5_graphlets19);
    CPPUNIT_TEST(test_count_connected_5_graphlets20);
    
    CPPUNIT_TEST(test_normalize_counts);
    
    CPPUNIT_TEST(test_labeled_counts_2);
    CPPUNIT_TEST(test_labeled_counts_3);
    CPPUNIT_TEST(test_labeled_counts_31);
    CPPUNIT_TEST(test_labeled_counts_4);
    
    
    
    

    
    
    CPPUNIT_TEST_SUITE_END();

public:
    newtestclass2();
    virtual ~newtestclass2();
    void setUp();
    void tearDown();

private:
    
    // attributes for constructing test graphs
    vertex_info ti, ui, vi, wi, xi;
    VertexDescriptor t,u,v,w,x;
    edge_info ai,bi,ci,di,ei,fi,gi,hi,ii,ji;
    EdgeDescriptor ad, bd, cd, dd, ed, fd, gd, hd, id, jd;

    Graph fiveNodesGraph;

    //attributes for checking counts
    vector<int> test5CountVector;
    
    vector<float> testNormalizeVector;
    vector<int> testNormalizeVectorInt;
    
    
    string g0_vertex_patterns[4];
    string g1_vertex_patterns[4];
    string g2_vertex_patterns[6];
    string g2_bio_patterns[2];
    string g6_vertex_patterns[10];
    string g6_bio_patterns[2];
  

    
    
    GraphletCounts counter, counter1, counter2;
    
    //test methods
    


    void test_count_connected_2_graphlets();
    
    void test_count_connected_3_graphlets0();
    void test_count_connected_3_graphlets1();
    
    
    //tests for all 6 different connected 4-graphlets
    void test_count_connected_4_graphlets0();
    void test_count_connected_4_graphlets1();
    void test_count_connected_4_graphlets2();
    void test_count_connected_4_graphlets3();
    void test_count_connected_4_graphlets4();
    void test_count_connected_4_graphlets5();
    
    // tests for all 21 different connected 5-graphlets
    void test_count_connected_5_graphlets0();    
    void test_count_connected_5_graphlets1();
    void test_count_connected_5_graphlets2();
    void test_count_connected_5_graphlets3();
    void test_count_connected_5_graphlets4();
    void test_count_connected_5_graphlets5();
    void test_count_connected_5_graphlets6();
    void test_count_connected_5_graphlets7();
    void test_count_connected_5_graphlets8();
    void test_count_connected_5_graphlets9();
    void test_count_connected_5_graphlets10();
    void test_count_connected_5_graphlets11();
    void test_count_connected_5_graphlets12();
    void test_count_connected_5_graphlets13();
    void test_count_connected_5_graphlets14();
    void test_count_connected_5_graphlets15();
    void test_count_connected_5_graphlets16();
    void test_count_connected_5_graphlets17();
    void test_count_connected_5_graphlets18();
    void test_count_connected_5_graphlets19();
    void test_count_connected_5_graphlets20();
    
    void test_normalize_counts();
    
    void test_labeled_counts_2();

    void test_labeled_counts_3();
    void test_labeled_counts_31();
    void test_labeled_counts_4();

    
    
};

#endif	/* NEWTESTCLASS2_H */

