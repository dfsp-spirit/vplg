/*
 * File:   Test_BK_output.cpp
 * Author: julian
 *
 * Created on Jun 12, 2015, 12:08:29 PM
 */

#include "Test_BK_Output.h"


CPPUNIT_TEST_SUITE_REGISTRATION(Test_BK_output);

Test_BK_output::Test_BK_output() {
}

Test_BK_output::~Test_BK_output() {
}

void Test_BK_output::setUp() {
    this->list.clear();
    list = {{3,4},{3,5},{1,5},{1,6,7},{0,2,4},{0,7,6,2}};
}

void Test_BK_output::tearDown() {
}

void Test_BK_output::testGet_JSON_all() {
    BronKerbosch bk;
    bk.set_result(list);
    std::string result = BK_Output::get_JSON_all(bk);
    std::string check = "[\n\t[\n\t\t3,\n\t\t4\n\t],"
                                    "\n\t[\n\t\t3,\n\t\t5\n\t],"
                                    "\n\t[\n\t\t1,\n\t\t5\n\t],"
                                    "\n\t[\n\t\t1,\n\t\t6,\n\t\t7\n\t],"
                                    "\n\t[\n\t\t0,\n\t\t2,\n\t\t4\n\t],"
                                    "\n\t[\n\t\t0,\n\t\t7,\n\t\t6,\n\t\t2\n\t]\n]\n";
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_JSON_larger_than() {
    BronKerbosch bk;
    bk.set_result(list);
    std::string result = BK_Output::get_JSON_larger_than(bk, 2);
    std::string check = "[\n\t[\n\t\t1,\n\t\t6,\n\t\t7\n\t],"
                                    "\n\t[\n\t\t0,\n\t\t2,\n\t\t4\n\t],"
                                    "\n\t[\n\t\t0,\n\t\t7,\n\t\t6,\n\t\t2\n\t]\n]\n";
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_JSON_largest() {
    BronKerbosch bk;
    bk.set_result(list);
    std::string result = BK_Output::get_JSON_largest(bk);
    std::string check = "[\n\t[\n\t\t0,\n\t\t7,\n\t\t6,\n\t\t2\n\t]\n]\n";
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_formated_pattern() {
    BronKerbosch bk;
    bk.set_result(list);
    std::vector<int> result = BK_Output::get_formated_pattern(bk);
    std::vector<int> check = {0,0,3,2,1};
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_result_all() {
    BronKerbosch bk;
    bk.set_result(list);
    std::list<std::list<unsigned long>> result = BK_Output::get_result_all(bk);
    std::list<std::list<unsigned long>> check = {{3,4},{3,5},{1,5},{1,6,7},{0,2,4},{0,7,6,2}};
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_result_larger_than() {
    BronKerbosch bk;
    bk.set_result(list);
    std::list<std::list<unsigned long> > result = BK_Output::get_result_larger_than(bk, 2);
    std::list<std::list<unsigned long>> check = {{1,6,7},{0,2,4},{0,7,6,2}};
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_result_largest() {
    BronKerbosch bk;
    bk.set_result(list);
    std::list<std::list<unsigned long> > result = BK_Output::get_result_largest(bk);
    std::list<std::list<unsigned long>> check= {{0,7,6,2}};
    CPPUNIT_ASSERT(result == check);
}

