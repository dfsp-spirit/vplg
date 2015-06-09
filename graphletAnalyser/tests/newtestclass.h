/*
 * File:   newtestclass.h
 * Author: ben
 *
 * Created on Apr 28, 2015, 2:59:20 PM
 */

#ifndef NEWTESTCLASS_H
#define	NEWTESTCLASS_H

#include <cppunit/extensions/HelperMacros.h>
#include "../GraphService.h"

class newtestclass : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE(newtestclass);

    CPPUNIT_TEST(test_getName);
    CPPUNIT_TEST(test_getPropertyValue);
    CPPUNIT_TEST(test_getNumVertices);
    CPPUNIT_TEST(test_getEdges);
    CPPUNIT_TEST(test_getGraphProperties);
    CPPUNIT_TEST(test_getNumEdges);
    CPPUNIT_TEST(test_getVertices);
    CPPUNIT_TEST(test_computeDegreeDist);
    
    CPPUNIT_TEST_SUITE_END();

public:
    newtestclass();
    virtual ~newtestclass();
    void setUp();
    void tearDown();

private:
    
    Graph oneNodeGraph;
    Graph twoNodesGraph1;
    Graph twoNodesGraph2;
    GraphService service1, service2, service3;
    VertexDescriptor v, u;
    vertex_info vi, ui;
    edge_info ei, fi;
    std::vector<std::pair<int,int>> testEdgeVector;
    std::vector<int> testVertexVector;
    std::vector<int> testVectorDegDist;

    void test_getName();
    void test_getPropertyValue();
    void test_getNumVertices();
    void test_getEdges();
    void test_getGraphProperties();
    void test_getNumEdges();
    void test_getVertices();
    void test_computeDegreeDist();
};

#endif	/* NEWTESTCLASS_H */

