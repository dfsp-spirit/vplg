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
    std::list<unsigned long> l1; l1.push_back((unsigned long) 2);
                                                  l1.push_back((unsigned long)3);
    std::list<unsigned long> l2; l2.push_back((unsigned long)1);
    std::list<unsigned long> l3; l3.push_back((unsigned long)0);
    list.push_back(l1);
    list.push_back(l2);
    list.push_back(l3);
}

void Test_BK_output::tearDown() {
}

void Test_BK_output::testGet_JSON_all() {
    BronKerbosch bk;
    bk.set_result(list);
    std::string result = BK_Output::get_JSON_all(bk);
    std::string check = "[\n\t[\n\t\t2,\n\t\t3\n\t],\n\t[\n\t\t1\n\t],\n\t[\n\t\t0\n\t]\n]\n";
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_JSON_larger_than() {
    BronKerbosch bk;
    bk.set_result(list);
    std::string result = BK_Output::get_JSON_larger_than(bk, 1);
    std::string check = "[\n\t[\n\t\t2,\n\t\t3\n\t]\n]\n";
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_JSON_largest() {
    BronKerbosch bk;
    bk.set_result(list);
    std::string result = BK_Output::get_JSON_largest(bk);
    std::string check = "[\n\t[\n\t\t2,\n\t\t3\n\t]\n]\n";
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_formated_pattern() {
    BronKerbosch bk;
    bk.set_result(list);
    std::vector<int> result = BK_Output::get_formated_pattern(bk);
    std::vector<int> check = {0,2,1};
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_result_all() {
    BronKerbosch bk;
    bk.set_result(list);
    std::list<std::list<unsigned long>> result = BK_Output::get_result_all(bk);
    std::list<std::list<unsigned long>> check;
    std::list<unsigned long> l1; l1.push_back((unsigned long) 2);
                                                  l1.push_back((unsigned long)3);
    std::list<unsigned long> l2; l2.push_back((unsigned long)1);
    std::list<unsigned long> l3; l3.push_back((unsigned long)0);
    check.push_back(l1);
    check.push_back(l2);
    check.push_back(l3);
    result.sort(); check.sort();
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_result_larger_than() {
    BronKerbosch bk;
    bk.set_result(list);
    std::list<std::list<unsigned long> > result = BK_Output::get_result_larger_than(bk, 1);
    std::list<std::list<unsigned long>> check;
    std::list<unsigned long> l1; l1.push_back((unsigned long) 2);
                                                  l1.push_back((unsigned long)3);
    check.push_back(l1);
    result.sort(); check.sort();
    CPPUNIT_ASSERT(result == check);
}

void Test_BK_output::testGet_result_largest() {
    BronKerbosch bk;
    bk.set_result(list);
    std::list<std::list<unsigned long> > result = BK_Output::get_result_largest(bk);
    std::list<std::list<unsigned long>> check;
    std::list<unsigned long> l1; l1.push_back((unsigned long) 2);
                                                  l1.push_back((unsigned long)3);
    check.push_back(l1);
    result.sort(); check.sort();
    CPPUNIT_ASSERT(result == check);
}

