/*
 * File:   newtestclass.cpp
 * Author: ben
 *
 * Created on Apr 28, 2015, 2:59:20 PM
 */

#include "newtestclass.h"

using namespace boost;


// needs several mock objects: Graph, GML-File (broken and working)

//Tests should start with the more basic classes and objects
//provisional order:    GraphService (DONE), GMLParser, GraphPrinter, ProteinGraphService,
//                      GMLptglParser, GraphPTGLPrinter, GraphletCounts, main,
//                      Database, oldFunctions


CPPUNIT_TEST_SUITE_REGISTRATION(newtestclass);

newtestclass::newtestclass() {
}

newtestclass::~newtestclass() {
}

void newtestclass::setUp() {
    
    // first graph mock object, a graph with just one node
    vi.id = 0;
    vi.label = "TestVertex";
    vi.properties["sse_type"] = "E";
    v = add_vertex(vi, oneNodeGraph);
    oneNodeGraph[graph_bundle].id = 0;
    oneNodeGraph[graph_bundle].label = "TestGraph";
    oneNodeGraph[graph_bundle].properties["testProperty"] = "TESTTEST";
    
    test_prop_vector = std::vector<std::string>();
    test_prop_vector.push_back("testProperty");

    
    
    // corresponding GraphService mock objects
    service1 = GraphService(oneNodeGraph);

    // vertex vector for testing
    
    testVertexVector = std::vector<int>();
    testVectorDegDist = testVertexVector;
    
    
    ui3.id = 0;
    ui3.properties["prp"] = "H";
    vi3.id = 1;
    vi3.properties["prp"] = "E";
    wi3.id = 2;
    wi3.properties["prp"] = "H";
    
    ei3.source = 0;
    ei3.target = 1;
    
    fi3.source = 1;
    fi3.target = 2;
    
    gi3.source = 2;
    gi3.target = 0;
    
    u3 = add_vertex(ui3, threeNodesGraph);
    v3 = add_vertex(vi3, threeNodesGraph);
    w3 = add_vertex(wi3, threeNodesGraph);
    
    add_edge(u3,v3,ei3,threeNodesGraph);
    add_edge(v3,w3,fi3,threeNodesGraph);
    add_edge(w3,u3,gi3,threeNodesGraph);
    
    test_adjacent_vector = std::vector<int>();
    test_adjacent_vector.push_back(0);
    test_adjacent_vector.push_back(1);
    test_adjacent_vector.push_back(2);
    
    service2 = GraphService(threeNodesGraph);
    
    test_adjacent_vector1 = std::vector<int>();
    test_adjacent_vector1.push_back(1);
    test_adjacent_vector1.push_back(0);
    test_adjacent_vector1.push_back(2);
    
    test_adjacent_vector2 = std::vector<int>();
    test_adjacent_vector2.push_back(2);
    test_adjacent_vector2.push_back(1);
    test_adjacent_vector2.push_back(0);
    
    test_adj_all_vector = std::vector<std::vector<int>>();
    test_adj_all_vector.push_back(test_adjacent_vector);
    test_adj_all_vector.push_back(test_adjacent_vector1);
    test_adj_all_vector.push_back(test_adjacent_vector2);
    
    std::vector<int> vec2 = std::vector<int>(1);
    vec2[0] = 3;
    
    std::vector<int> vec3 = std::vector<int>(2);
    std::vector<int> vec4 = std::vector<int>(6);
    std::vector<int> vec5 = std::vector<int>(21);
    
    std::vector<float> vec2f = std::vector<float>(1);
    vec2f[0] = 0.75;
    
    std::vector<float> vec3f = std::vector<float>(2);
    std::vector<float> vec4f = std::vector<float>(6);
    std::vector<float> vec5f = std::vector<float>(21);
    
    for (int i = 0; i <= 21; i++) {
        
        if (i < 2) {
            vec3[i] = 0;
            vec3f[i] = 0.;
            if (i == 0) {
                vec3[i] = 1;
                vec3f[i] = 0.25;
            }
        }
        
        if (i < 6) {
            vec4[i] = 0;
            vec4f[i] = 0.;
        }
        
        vec5[i] = 0;
        vec5f[i] = 0.;
    }
    
    test_count_vector_int.push_back(vec2);
    test_count_vector_int.push_back(vec3);
    test_count_vector_int.push_back(vec4);
    test_count_vector_int.push_back(vec5);
    
    test_count_vector_float.push_back(vec2f);
    test_count_vector_float.push_back(vec3f);
    test_count_vector_float.push_back(vec4f);
    test_count_vector_float.push_back(vec5f);
    
}

void newtestclass::tearDown() {

}


/* test whether the right string is returned by getName */
void newtestclass::test_get_label() {
    
    
    CPPUNIT_ASSERT(service1.get_label().compare("TestGraph") == 0);
    
}


/* DOESN'T TEST. Needs to be made new */
void newtestclass::test_getPropertyValue() {
    
    CPPUNIT_ASSERT(service1.getPropertyValue("testProperty").compare("TESTTEST") == 0);
    
}

/* test whether the correct number of vertices is returned */
void newtestclass::test_getNumVertices() {
    
    CPPUNIT_ASSERT(service1.getNumVertices() == 1);
    
   
}

/* test whether the correct edges are returned */
void newtestclass::test_getEdges() {
    
    // test, if getEdges returns the right number of edges for the graph with just one vertex
    testEdgeVector = std::vector<std::pair<int,int>>();
    
    CPPUNIT_ASSERT(service1.getEdges() == testEdgeVector);
    

    
    
}


/* test whether the correct property keys are returned */
void newtestclass::test_getGraphProperties() {
    
    
    
    
    CPPUNIT_ASSERT(service1.getGraphProperties() == test_prop_vector);

}


/* test whether the correct numbers of edges are returned */
void newtestclass::test_getNumEdges() {
    
    CPPUNIT_ASSERT(service1.getNumEdges() == 0);
}

/* test whether the right vector of vertex indices is returned */
void newtestclass::test_getVertices() {
    
    testVertexVector.push_back(0);
    
    CPPUNIT_ASSERT(service1.getVertices() == testVertexVector);
    
    
}

/* test whether the right degree distribution is returned */
void newtestclass::test_computeDegreeDist() {
    

    std::vector<int> test_vector = std::vector<int>();
    test_vector.push_back(0);
    
    CPPUNIT_ASSERT(service1.computeDegreeDist() == test_vector);
    
}

void newtestclass::test_get_adjacent() {
    
    CPPUNIT_ASSERT(service2.get_adjacent(0) == test_adjacent_vector);
    
}

void newtestclass::test_get_adjacent_all() {
    
    CPPUNIT_ASSERT(service2.get_adjacent_all() == test_adj_all_vector);
}

void newtestclass::test_get_abs_counts() {
    
    
    
    CPPUNIT_ASSERT(service2.get_abs_counts() == test_count_vector_int);
}

void newtestclass::test_get_norm_counts() {
    
    std::vector<std::vector<float>> vec = service2.get_norm_counts();
    
    for (int i = 0; i< vec.size(); i ++) {
        
        std::vector<float> vvec = vec[i];
        for (int k = 0; k < vvec.size(); k++) {
            std::cout << " " << vvec[k];
        }
        
    }
    
    CPPUNIT_ASSERT(service2.get_norm_counts() == test_count_vector_float);
}

void newtestclass::test_reverse_string() {
    CPPUNIT_ASSERT(service1.reverse_string(test_string) == test_rev_set);
}

void newtestclass::test_compute_CAT() {
    
    CPPUNIT_ASSERT(service1.compute_CAT(test_string) == test_CAT_set);
}

void newtestclass::test_get_length_2_patterns() {
    test_2p_vec = std::vector<std::string>();
    test_2p_vec.push_back("AA");
    test_2p_vec.push_back("AB");
    test_2p_vec.push_back("AC");
    test_2p_vec.push_back("BB");
    test_2p_vec.push_back("BC");
    test_2p_vec.push_back("CC");
    
    std::vector<std::string> t_vec = service1.get_length_2_patterns(test_string);
    
    
    
    
    
    CPPUNIT_ASSERT(t_vec == test_2p_vec);
    
}

void newtestclass::test_get_length_3_patterns() {
    
    std::vector<std::vector<std::string>> testing_vec = std::vector<std::vector<std::string>>();
    
    std::vector<std::string> testing_vec_tri = std::vector<std::string>();
    std::vector<std::string> testing_vec_3p = std::vector<std::string>();
    
    testing_vec_tri.push_back("AAA");
    testing_vec_3p.push_back("AAA");
    
    testing_vec_3p.push_back("ABA");
    testing_vec_3p.push_back("AAB");
    testing_vec_3p.push_back("ABB");
    testing_vec_3p.push_back("BAB");
    
    testing_vec_tri.push_back("ABA");
    testing_vec_tri.push_back("ABB");
    
    
    testing_vec_3p.push_back("ABC");
    testing_vec_3p.push_back("CAB");
    testing_vec_3p.push_back("BCA");
    
    testing_vec_tri.push_back("ABC");
    testing_vec_tri.push_back("CBA");
    
    
    
    testing_vec_3p.push_back("ACA");
    testing_vec_3p.push_back("AAC");
    testing_vec_3p.push_back("ACC");
    testing_vec_3p.push_back("CAC");
    
    testing_vec_tri.push_back("ACA");
    testing_vec_tri.push_back("ACC");
    
    
    
    
    testing_vec_3p.push_back("BBB");
    testing_vec_tri.push_back("BBB");
    
    testing_vec_3p.push_back("BCB");
    testing_vec_3p.push_back("BBC");
    testing_vec_3p.push_back("BCC");
    testing_vec_3p.push_back("CBC");
    
    testing_vec_tri.push_back("BCB");
    testing_vec_tri.push_back("BCC");
    
    
    
    
    
    
    testing_vec_3p.push_back("CCC");
    testing_vec_tri.push_back("CCC");
    
    
    
    testing_vec.push_back(testing_vec_3p);
    testing_vec.push_back(testing_vec_tri);        
    
    std::vector<std::vector<std::string>> tested_vec = service1.get_length_3_patterns(test_string);
    
    CPPUNIT_ASSERT(tested_vec == testing_vec);
    
    
}

void newtestclass::test_get_labeled_abs_counts() {
    CPPUNIT_ASSERT(true);
}

void newtestclass::test_get_labeled_norm_counts() {
    CPPUNIT_ASSERT(true);
}

void newtestclass::test_get_graphlet_identifier() {
    CPPUNIT_ASSERT(true);
}

void newtestclass::test_get_patterns() {
    CPPUNIT_ASSERT(true);
}