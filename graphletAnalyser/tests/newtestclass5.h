/*
 * File:   newtestclass5.h
 * Author: ben
 *
 * Created on Jul 7, 2015, 5:02:11 PM
 */

#ifndef NEWTESTCLASS5_H
#define	NEWTESTCLASS5_H

#include <cppunit/extensions/HelperMacros.h>
#include "../GraphPTGLPrinter.h"

class newtestclass5 : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE(newtestclass5);

    CPPUNIT_TEST(test_printVertices);

    CPPUNIT_TEST_SUITE_END();

public:
    newtestclass5();
    virtual ~newtestclass5();
    void setUp();
    void tearDown();

private:
    GraphPTGLPrinter printer;
    Graph g;
    vertex_info vi;
    VertexDescriptor vd;
    std::string teststring0;
    void test_printVertices();
    
};

#endif	/* NEWTESTCLASS5_H */

