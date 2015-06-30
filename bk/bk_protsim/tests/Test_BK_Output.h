/*
 *   This file is part of:
 * 
 *   bk_protsim Copyright (C) 2015  Molecular Bioinformatics group, Goethe-University Frankfurt
 * 
 *   Written by Julian Gruber-Roet, maintained by Tim Schaefer.
 *   This program comes with ABSOLUTELY NO WARRANTY.
 *    This is free software, and you are welcome to redistribute it
 *   under certain conditions, see the LICENSE file for details.
 */


/*
 * File:   Test_BK_output.h
 * Author: julian
 *
 * Created on Jun 12, 2015, 12:08:28 PM
 */

#ifndef TEST_BK_OUTPUT_H
#define	TEST_BK_OUTPUT_H

#include <cppunit/extensions/HelperMacros.h>
#include "../BK_Output.h"


class Test_BK_output : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE(Test_BK_output);

    CPPUNIT_TEST(testGet_JSON_all);
    CPPUNIT_TEST(testGet_JSON_larger_than);
    CPPUNIT_TEST(testGet_JSON_largest);
    CPPUNIT_TEST(testGet_formated_pattern);
    CPPUNIT_TEST(testGet_result_all);
    CPPUNIT_TEST(testGet_result_larger_than);
    CPPUNIT_TEST(testGet_result_largest);

    CPPUNIT_TEST_SUITE_END();

public:
    Test_BK_output();
    virtual ~Test_BK_output();
    void setUp();
    void tearDown();

private:
    std::list<std::list<unsigned long>> list;
    
    void testGet_JSON_all();
    void testGet_JSON_larger_than();
    void testGet_JSON_largest();
    void testGet_formated_pattern();
    void testGet_result_all();
    void testGet_result_larger_than();
    void testGet_result_largest();
    
};

#endif	/* TEST_BK_OUTPUT_H */

