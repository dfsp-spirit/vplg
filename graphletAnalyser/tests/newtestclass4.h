/*
 * File:   newtestclass4.h
 * Author: ben
 *
 * Created on Jul 7, 2015, 4:53:02 PM
 */

#ifndef NEWTESTCLASS4_H
#define	NEWTESTCLASS4_H

#include <cppunit/extensions/HelperMacros.h>
#include "../ProteinGraphService.h"


class newtestclass4 : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE(newtestclass4);

    CPPUNIT_TEST(test_getGraphTypeStr);
    CPPUNIT_TEST(test_getGraphTypeInt);

    CPPUNIT_TEST_SUITE_END();

public:
    newtestclass4();
    virtual ~newtestclass4();
    void setUp();
    void tearDown();

private:
    
    
    ProteinGraphService serv;
    Graph g;
    std::string teststring0;
    int testNumber;
    vertex_info vi;
    
    void test_getGraphTypeInt();
    void test_getGraphTypeStr();
};

#endif	/* NEWTESTCLASS4_H */

