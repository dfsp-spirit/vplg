/*
 * File:   Test_PG_Output.h
 * Author: julian
 *
 * Created on Jun 12, 2015, 1:56:58 PM
 */

#ifndef TEST_PG_OUTPUT_H
#define	TEST_PG_OUTPUT_H

#include <cppunit/extensions/HelperMacros.h>
#include "../PG_Output.h"

class Test_PG_Output : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE(Test_PG_Output);

    CPPUNIT_TEST(testGet_common_first);
    CPPUNIT_TEST(testGet_common_second);

    CPPUNIT_TEST_SUITE_END();

public:
    Test_PG_Output();
    virtual ~Test_PG_Output();
    void setUp();
    void tearDown();

private:
    Graph_p p;
    Graph f;
    Graph s;
    std::list<unsigned long> clique;
    
    void testGet_common_first();
    void testGet_common_second();

};

#endif	/* TEST_PG_OUTPUT_H */

