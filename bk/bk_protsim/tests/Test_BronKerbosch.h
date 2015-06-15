/*
 * File:   Test_BronKerbosch.h
 * Author: julian
 *
 * Created on Jun 12, 2015, 2:45:18 PM
 */

#ifndef TEST_BRONKERBOSCH_H
#define	TEST_BRONKERBOSCH_H

#include <cppunit/extensions/HelperMacros.h>
#include "../BronKerbosch.h"

class Test_BronKerbosch : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE(Test_BronKerbosch);

    CPPUNIT_TEST(testRun);

    CPPUNIT_TEST_SUITE_END();

public:
    Test_BronKerbosch();
    virtual ~Test_BronKerbosch();
    void setUp();
    void tearDown();

private:
    Graph_p p;
    void testRun();

};

#endif	/* TEST_BRONKERBOSCH_H */

