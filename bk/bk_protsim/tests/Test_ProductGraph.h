/*
 * File:   Test_ProductGraph.h
 * Author: julian
 *
 * Created on Jun 12, 2015, 3:02:50 PM
 */

#ifndef TEST_PRODUCTGRAPH_H
#define	TEST_PRODUCTGRAPH_H

#include <cppunit/extensions/HelperMacros.h>
#include "../ProductGraph.h"

class Test_ProductGraph : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE(Test_ProductGraph);

    CPPUNIT_TEST(testRun);

    CPPUNIT_TEST_SUITE_END();

public:
    Test_ProductGraph();
    virtual ~Test_ProductGraph();
    void setUp();
    void tearDown();

private:
    Graph s,f;
    void testRun();

};

#endif	/* TEST_PRODUCTGRAPH_H */

