/*
 * File:   Test_BronKerbosch.cpp
 * Author: julian
 *
 * Created on Jun 12, 2015, 2:45:19 PM
 */

#include "Test_BronKerbosch.h"



CPPUNIT_TEST_SUITE_REGISTRATION(Test_BronKerbosch);

Test_BronKerbosch::Test_BronKerbosch() {
}

Test_BronKerbosch::~Test_BronKerbosch() {
}

void Test_BronKerbosch::setUp() {
}

void Test_BronKerbosch::tearDown() {
}

void Test_BronKerbosch::testRun() {
    BronKerbosch bronKerbosch;
    bronKerbosch.run();
    if (true /*check result*/) {
        CPPUNIT_ASSERT(false);
    }
}

