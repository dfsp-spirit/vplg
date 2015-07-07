/*
 * File:   newtestclass3.cpp
 * Author: ben
 *
 * Created on Jun 25, 2015, 1:54:34 PM
 */

#include "newtestclass3.h"


CPPUNIT_TEST_SUITE_REGISTRATION(newtestclass3);

newtestclass3::newtestclass3() {
}

newtestclass3::~newtestclass3() {
}

void newtestclass3::setUp() {
    test_string_float = "[1.,2.33,3.1049]";
    test_string_int = "[1,2,3]";
    test_string_vec_int = "[[1,2,3],[],[1,2,3]]";
    test_string_vec_float = "[[],[1.,2.33,3.1049],[1.,2.33,3.1049]]";
    test_string_info = "\"Graphname\" : \"myGraph\", \"Number of vertices\" : 42, \"Number of edges\" : 84,  \"Absolute Counts\" : {\"2-graphlets\" : [1], \"3-graphlets\" : [1,2], \"4-graphlets\" : [1,2,3], \"5-graphlets\" : [1,2,3,4]}, \"Normalized Counts\" :  {\"2-graphlets\" : [0.1], \"3-graphlets\" : [0.1,0.2],\"4-graphlets\" : [0.1,0.2,0.3], \"5-graphlets\" : null}}";
            
    
    test_vector_float = std::vector<float>();
    test_vector_float.push_back(1);
    test_vector_float.push_back(2.33);
    test_vector_float.push_back(3.1049);
    
    test_vector_int = std::vector<int>();
    
    test_vector_int.push_back(1);
    test_vector_int.push_back(2);
    test_vector_int.push_back(3);
    
    std::vector<int> veci = std::vector<int>();
    std::vector<float> vecf = std::vector<float>();
    
    test_vec_vector_int = std::vector<std::vector<int>>();
    test_vec_vector_int.push_back(test_vector_int);
    test_vec_vector_int.push_back(veci);
    test_vec_vector_int.push_back(test_vector_int);
    
    test_vec_vector_float = std::vector<std::vector<float>>();
    test_vec_vector_float.push_back(vecf);
    test_vec_vector_float.push_back(test_vector_float);
    test_vec_vector_float.push_back(test_vector_float);
    

}

void newtestclass3::tearDown() {
}

void newtestclass3::test_print_float_vector() {
    
    std::string tet = printer.print_float_vector(test_vector_float);
    
    
    CPPUNIT_ASSERT(tet.compare(test_string_float) == 0);
    
}

void newtestclass3::test_print_float_vec_vector() {
    
    std::string tst = printer.print_float_vec_vector(test_vec_vector_float);
    CPPUNIT_ASSERT(tst.compare(test_string_vec_float) == 0);
    
}

void newtestclass3::test_print_int_vector() {
    std::string tst = printer.print_int_vector(test_vector_int);
    
    
    CPPUNIT_ASSERT(tst.compare(test_string_int) == 0);
    
}

void newtestclass3::test_print_int_vec_vector() {
    std::string tst = printer.print_int_vec_vector(test_vec_vector_int);
    
    
    CPPUNIT_ASSERT(tst.compare(test_string_vec_int) == 0);
    
}

void newtestclass3::test_print_vectors_with_info() {
    
    std::vector<int> vec = std::vector<int>();
    std::vector<float> vecf = std::vector<float>();
    std::vector<float> evec = std::vector<float>();
    vec.push_back(1);
    vecf.push_back(0.1);
    std::vector<std::vector<int>> v_vec_i = std::vector<std::vector<int>>();
    std::vector<std::vector<float>> v_vec_f = std::vector<std::vector<float>>();
    v_vec_i.push_back(vec);
    v_vec_f.push_back(vecf);
    vec.push_back(2);
    vecf.push_back(0.2);
    v_vec_i.push_back(vec);
    v_vec_f.push_back(vecf);
    vec.push_back(3);
    vecf.push_back(0.3);
    v_vec_i.push_back(vec);
    v_vec_f.push_back(vecf);
    vec.push_back(4);
    v_vec_i.push_back(vec);
    v_vec_f.push_back(evec);
    
    std::string tst = printer.print_vectors_with_info("myGraph",42,84,v_vec_f,v_vec_i);
    
    CPPUNIT_ASSERT(test_string_info.compare(tst));
    
}