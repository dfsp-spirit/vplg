/*
 * File:   newtestclass3.h
 * Author: ben
 *
 * Created on Jun 25, 2015, 1:54:34 PM
 */

#ifndef NEWTESTCLASS3_H
#define	NEWTESTCLASS3_H

#include <cppunit/extensions/HelperMacros.h>
#include "../JSON_printer.h"

class newtestclass3 : public CPPUNIT_NS::TestFixture {
    CPPUNIT_TEST_SUITE(newtestclass3);

    CPPUNIT_TEST(test_print_int_vector);
    CPPUNIT_TEST(test_print_float_vector);
    CPPUNIT_TEST(test_print_int_vec_vector);
    CPPUNIT_TEST(test_print_float_vec_vector);
    CPPUNIT_TEST(test_print_vectors_with_info);
    CPPUNIT_TEST(test_print_labeled_counts);

    CPPUNIT_TEST_SUITE_END();

public:
    newtestclass3();
    virtual ~newtestclass3();
    void setUp();
    void tearDown();
    std::string test_string_float, test_string_int, test_string_vec_float, test_string_vec_int, test_string_info, test_string_lab;
    std::vector<float> test_vector_float;
    std::vector<int> test_vector_int;
    std::vector<std::vector<float>> test_vec_vector_float;
    std::vector<std::vector<int>> test_vec_vector_int;
    JSON_printer printer;
    

private:
    void test_print_float_vector();
    void test_print_int_vector();
    void test_print_int_vec_vector();
    void test_print_float_vec_vector();
    void test_print_vectors_with_info();
    void test_print_labeled_counts();
};

#endif	/* NEWTESTCLASS3_H */

