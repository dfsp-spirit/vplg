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
    CPPUNIT_TEST(testGet_vertex_ids_first);
    CPPUNIT_TEST(testGet_vertex_ids_second);
    CPPUNIT_TEST(testGet_JSON_vertex_ids_first);
    CPPUNIT_TEST(testGet_JSON_vertex_ids_second);

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
    
    void testGet_vertex_ids_first();
    void testGet_vertex_ids_second();
    
    void testGet_JSON_vertex_ids_first();
    void testGet_JSON_vertex_ids_second();

};

#endif	/* TEST_PG_OUTPUT_H */

