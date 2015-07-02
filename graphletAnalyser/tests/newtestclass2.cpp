/*
 * File:   newtestclass2.cpp
 * Author: ben
 *
 * Created on May 21, 2015, 1:55:53 PM
 */

#include "newtestclass2.h"
using namespace boost;

// TODO: take care of warning message

CPPUNIT_TEST_SUITE_REGISTRATION(newtestclass2);

newtestclass2::newtestclass2() {
}

newtestclass2::~newtestclass2() {
}

void newtestclass2::setUp() {
    
    //setting up nodes
    ti.id = 0;
    ui.id = 1;
    vi.id = 2;
    wi.id = 3;
    xi.id = 4;

    
    GraphletCounts counter();
    
    // graph for counting graphlets
    // graph will be initialized as a only with nodes
    // edges will be added in testing methods successively
    
    t = add_vertex(ti, fiveNodesGraph);
    u = add_vertex(ui, fiveNodesGraph);
    v = add_vertex(vi, fiveNodesGraph);
    w = add_vertex(wi, fiveNodesGraph);
    x = add_vertex(xi, fiveNodesGraph);
    
    ai.source = 0;
    ai.target = 1;
    
    bi.source = 1;
    bi.target = 2;
    
    ci.source = 2;
    ci.target = 3;
    
    di.source = 3;
    di.target = 4;
    
    ei.source = 4;
    ei.target = 0;
    
    fi.source = 0;
    fi.target = 3;
    
    gi.source = 0;
    gi.target = 2;
    
    hi.source = 1;
    hi.target = 4;
    
    ii.source = 1;
    ii.target = 3;
    
    ji.source = 2;
    ji.target = 4;
    
    testNormalizeVector = vector<float>();
    testNormalizeVectorInt = vector<int>();
    
    // the following lines are more or less copied from the original code by tatiana
    

    
        // all possible labeling of g1 graphlet (triangle) concerning the symmetry
    g1_vertex_patterns[0] = "HHH";
    g1_vertex_patterns[1] = "HHE";
    g1_vertex_patterns[2] = "HEE";
    g1_vertex_patterns[3] = "EEE";
    
    
    string patterns6[] = { "HHH","HHE","EHE","HEH","HEE","EEE" };
    string patterns2[] = { "EaHaE",    // beta-alpha-beta motif
                                    "EaEaE" };  // beta-beta-beta motif
    // all possible labeling of g2 graphlet (2-path) concerning the symmetry
    g2_vertex_patterns[0] = patterns6[0];
    g2_vertex_patterns[1] = patterns6[1];
    g2_vertex_patterns[2] = patterns6[2];
    g2_vertex_patterns[3] = patterns6[3];
    g2_vertex_patterns[4] = patterns6[4];
    g2_vertex_patterns[5] = patterns6[5];
    
    // bio-motivated labeling of graphlets
    // currently implemented are the beta-alpha-beta and beta-beta-beta motifs
    // NOTE: also check if its composing vertices are adjacent
    g2_bio_patterns[0]    = patterns2[0];
    g2_bio_patterns[1]    = patterns2[1];
            
    string patterns4[] = { "HH", "EH", "HE", "EE" };
    
    g0_vertex_patterns[0] = patterns4[0];
    g0_vertex_patterns[1] = patterns4[1];
    g0_vertex_patterns[2] = patterns4[2];
    g0_vertex_patterns[3] = patterns4[3];
    
    /*
     * NOTE the correspondence 
     *   lcount[0..3]   := g1_vertex_patterns[0..3] 
     *   lcount[4..9]   := g2_vertex_patterns[0..6]
     *   lcount[10..11] := g2_bio_patterns[0..1]
     */
    
    string patterns10[] = { "HHHH","HHHE", "EHHE",
                                    "HHEH","HHEE","EHEH","EHEE",
                                    "HEEH","HEEE",       "EEEE"};
    
        // all possible labeling of g6 graphlet (3-path) concerning the symmetry
    g6_vertex_patterns[0] = patterns10[0];
    g6_vertex_patterns[1] = patterns10[1];
    g6_vertex_patterns[2] = patterns10[2];
    g6_vertex_patterns[3] = patterns10[3];
    g6_vertex_patterns[4] = patterns10[4];
    g6_vertex_patterns[5] = patterns10[5];
    g6_vertex_patterns[6] = patterns10[6];
    g6_vertex_patterns[7] = patterns10[7];
    g6_vertex_patterns[8] = patterns10[8];
    g6_vertex_patterns[9] = patterns10[9];
    
    
    
    
    patterns2[0] ="EaEaEaE";  // greek key
    patterns2[1] = "EpEpEpE"; // 4-beta-barrel, non-adjacent
    
    // bio-motivated labeling of graphlets
    // currently implemented is the greek key and 4-beta-barrel motifs
    // NOTE: also check if its composing vertices are adjacent for greek key
    //       no adjacency check for 4-beta-barrel motif since there are possible SSEs in between 
    g6_bio_patterns[0] = patterns2[0];
    g6_bio_patterns[1] = patterns2[1];
    
    /*
     * NOTE the correspondence 
     *   lcount[0..9] := g6_vertex_patterns[0..5]
     *   lcount[10]   := g6_bio_patterns[0]
     */
    
    
    ///____________________________________ end of copied lines
    
}

void newtestclass2::tearDown() {
    

}



void newtestclass2::test_count_connected_2_graphlets() {
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    vector<string> label_vector = vector<string>();
    
    testVector = counter.count_connected_2_graphlets(testGraph, "", label_vector);
    
    CPPUNIT_ASSERT(testVector[0] == 3);
    
}

void newtestclass2::test_count_connected_3_graphlets0() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    vector<vector<string>> str_vec = vector<vector<string>>();
    
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    testVector = counter.count_connected_3_graphlets(testGraph, "", str_vec);
    
    CPPUNIT_ASSERT(testVector[0] == 1);


}

void newtestclass2::test_count_connected_3_graphlets1() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    vector<vector<string>> str_vec = vector<vector<string>>();
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    
    testVector = counter.count_connected_3_graphlets(testGraph, "", str_vec);
    
    CPPUNIT_ASSERT(testVector[1] == 1);


}

void newtestclass2::test_count_connected_4_graphlets0() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    vector<vector<string>> lab = vector<vector<string>>();
    
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, "",lab);
    
    CPPUNIT_ASSERT(testVector[0] == 1);
    
}

void newtestclass2::test_count_connected_4_graphlets1() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    vector<vector<string>> lab = vector<vector<string>>();
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, "", lab);
    
    CPPUNIT_ASSERT(testVector[1] == 1);
    
    

}

void newtestclass2::test_count_connected_4_graphlets2() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    vector<vector<string>> lab = vector<vector<string>>();
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, "", lab);
    
    CPPUNIT_ASSERT(testVector[2] == 1);
    
    

}

void newtestclass2::test_count_connected_4_graphlets3() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    vector<vector<string>> lab = vector<vector<string>>();
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, "", lab);
    
    CPPUNIT_ASSERT(testVector[3] == 1);
    

}

void newtestclass2::test_count_connected_4_graphlets4() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    vector<vector<string>> lab = vector<vector<string>>();
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, "",lab);
    
    CPPUNIT_ASSERT(testVector[4] == 1);

}

void newtestclass2::test_count_connected_4_graphlets5() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    vector<vector<string>> lab = vector<vector<string>>();
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    
    testVector = counter.count_connected_4_graphlets(testGraph, "",lab);
    
    CPPUNIT_ASSERT(testVector[5] == 1);

}
void newtestclass2::test_count_connected_5_graphlets0() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    ad = add_edge(t,u,ai,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[0] == 1);

}

void newtestclass2::test_count_connected_5_graphlets1() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();

    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    bd = add_edge(v,w,bi,testGraph).first;
    ad = add_edge(t,u,ai,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[1] == 1);
}

void newtestclass2::test_count_connected_5_graphlets2() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[2] == 1);
}

void newtestclass2::test_count_connected_5_graphlets3() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    ad = add_edge(t,u,ai,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[3] == 1);
}

void newtestclass2::test_count_connected_5_graphlets4() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[4] == 1);
}

void newtestclass2::test_count_connected_5_graphlets5() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[5] == 1);
}

void newtestclass2::test_count_connected_5_graphlets6() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[6] == 1);
}

void newtestclass2::test_count_connected_5_graphlets7() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    
    
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[7] == 1);
}

void newtestclass2::test_count_connected_5_graphlets8() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[8] == 1);
}

void newtestclass2::test_count_connected_5_graphlets9() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[9] == 1);
}

void newtestclass2::test_count_connected_5_graphlets10() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;

    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[10] == 1);
}

void newtestclass2::test_count_connected_5_graphlets11() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;

    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[11] == 1);
}

void newtestclass2::test_count_connected_5_graphlets12() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[12] == 1);
}

void newtestclass2::test_count_connected_5_graphlets13() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    
    gd = add_edge(t,v,gi,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    fd = add_edge(t,w,fi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[13] == 1);
}

void newtestclass2::test_count_connected_5_graphlets14() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    gd = add_edge(t,v,gi,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    fd = add_edge(t,w,fi,testGraph).first;
    id = add_edge(u,w,ii,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[14] == 1);
}

void newtestclass2::test_count_connected_5_graphlets15() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    hd = add_edge(x,u,hi,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    jd = add_edge(x,v,ji,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[15] == 1);
}

void newtestclass2::test_count_connected_5_graphlets16() {
    //CHECKED
    
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,t,di,testGraph).first;
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[16] == 1);
}

void newtestclass2::test_count_connected_5_graphlets17() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    hd = add_edge(u,x,hi,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[17] == 1);
}

void newtestclass2::test_count_connected_5_graphlets18() {
    //CHECKED
    
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    fd = add_edge(t,w,fi,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[18] == 1);
}

void newtestclass2::test_count_connected_5_graphlets19() {
    //CHECKED
    
    
    Graph testGraph = fiveNodesGraph;
    
    ad = add_edge(t,u,ai,testGraph).first;
    ed = add_edge(x,t,ai,testGraph).first;
    jd = add_edge(v,x,ji,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    
    vector<int> testVector = vector<int>();
    
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[19] == 1);
}

void newtestclass2::test_count_connected_5_graphlets20() {
    //CHECKED
    
    Graph testGraph = fiveNodesGraph;
    vector<int> testVector = vector<int>();
    
    ad = add_edge(t,u,ai,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    fd = add_edge(t,w,fi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    
    
    
    
    testVector = counter.count_connected_5_graphlets(testGraph);
    
    CPPUNIT_ASSERT(testVector[20] == 1);
}

void newtestclass2::test_normalize_counts() {
    
    testNormalizeVector.push_back(0.1);
    testNormalizeVector.push_back(0);
    testNormalizeVector.push_back(0);
    testNormalizeVector.push_back(0.3);
    testNormalizeVector.push_back(0.4);
    testNormalizeVector.push_back(0.2);
    
    testNormalizeVectorInt.push_back(1);
    testNormalizeVectorInt.push_back(0);
    testNormalizeVectorInt.push_back(0);
    testNormalizeVectorInt.push_back(3);
    testNormalizeVectorInt.push_back(4);
    testNormalizeVectorInt.push_back(2);
    
    
    
    vector<float> testVector = counter.normalize_counts(testNormalizeVectorInt, 10.0);
    
    CPPUNIT_ASSERT(testVector == testNormalizeVector);
    
}



/*  // all possible labeling of g1 graphlet (triangle) concerning the symmetry
    string g1_vertex_patterns[] = { "HHH","HHE","HEE","EEE" };
    
    // all possible labeling of g2 graphlet (2-path) concerning the symmetry
    string g2_vertex_patterns[] = { "HHH","HHE","EHE","HEH","HEE","EEE" };
    
    // bio-motivated labeling of graphlets
    // currently implemented are the beta-alpha-beta and beta-beta-beta motifs
    // NOTE: also check if its composing vertices are adjacent
    string g2_bio_patterns[]    = { "EaHaE",    // beta-alpha-beta motif
                                    "EaEaE" }; */



void newtestclass2::test_labeled_counts_2() {
    
    Graph testGraph;
    
    ti.id = 0;
    ui.id = 1;
    vi.id = 2;
    wi.id = 3;
    xi.id = 4;

    
    ti.properties["sse_type"] = "H";
    ui.properties["sse_type"] = "H";
    vi.properties["sse_type"] = "E";
    
    
    t = add_vertex(ti, testGraph);
    u = add_vertex(ui, testGraph);
    v = add_vertex(vi, testGraph);
    w = add_vertex(wi, testGraph);
    x = add_vertex(xi, testGraph);

    vector<int> testVector = vector<int>();
    vector<int> test_vector2 = vector<int>();
    test_vector2.push_back(1);
    test_vector2.push_back(2);
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    gd = add_edge(t,v,gi,testGraph).first;
    
    vector<string> label_vector = vector<string>();
    label_vector.push_back("HH");
    label_vector.push_back("HE");
    
    
    
    counter.count_connected_2_graphlets(testGraph, "sse_type", label_vector);
    testVector = counter.get_labeled_2_countsABS("sse_type", label_vector);
    
    
    
    CPPUNIT_ASSERT(testVector[0] == test_vector2[0]);
    CPPUNIT_ASSERT(testVector[1] == test_vector2[1]);
    
    
}

void newtestclass2::test_labeled_counts_3() {
    
    Graph testGraph;
    
    ti.id = 0;
    ui.id = 1;
    vi.id = 2;
    wi.id = 3;
    xi.id = 4;

    
    ti.properties["sse_type"] = "H";
    ui.properties["sse_type"] = "E";
    vi.properties["sse_type"] = "E";
    wi.properties["sse_type"] = "H";
    xi.properties["sse_type"] = "H";
    
    
    t = add_vertex(ti, testGraph);
    u = add_vertex(ui, testGraph);
    v = add_vertex(vi, testGraph);
    w = add_vertex(wi, testGraph);
    x = add_vertex(xi, testGraph);

    vector<vector<int>> testVector = vector<vector<int>>();
    vector<int> test_vector2 = vector<int>();
    vector<vector<int>> test_vector3 = vector<vector<int>>();
    test_vector2.push_back(1);
    test_vector3.push_back(test_vector2);
    vector<int> test_vector4 = vector<int>();
    test_vector4.push_back(3);
    test_vector3.push_back(test_vector4);
    
    ai.source = 0;
    ai.target = 1;
    bi.source = 1;
    bi.target = 2;
    ci.source = 2;
    ci.target = 3;
    di.source = 4;
    di.target = 0;
    ei.source = 4;
    ei.target = 1;
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(x,t,di,testGraph).first;
    ed = add_edge(x,u,ei,testGraph).first;
    
    vector<string> label_vector = vector<string>();
    vector<vector<string>> labels = vector<vector<string>>();
    label_vector.push_back("HHE");
    labels.push_back(label_vector);
    label_vector = vector<string>();
    label_vector.push_back("HEE");
    labels.push_back(label_vector);
    
    
    counter.count_connected_3_graphlets(testGraph, "sse_type", labels);
    testVector = counter.get_labeled_3_countsABS("sse_type", labels);

    
    CPPUNIT_ASSERT(testVector == test_vector3);
    
    
}


void newtestclass2::test_labeled_counts_31() {
    
    Graph testGraph;
    
    ti.id = 0;
    ui.id = 1;
    vi.id = 2;
    wi.id = 3;
    xi.id = 4;

    
    ti.properties["sse_type"] = "H";
    ui.properties["sse_type"] = "E";
    vi.properties["sse_type"] = "E";
    wi.properties["sse_type"] = "E";
    xi.properties["sse_type"] = "E";
    
    
    t = add_vertex(ti, testGraph);
    u = add_vertex(ui, testGraph);
    v = add_vertex(vi, testGraph);
    w = add_vertex(wi, testGraph);
    x = add_vertex(xi, testGraph);

    vector<vector<int>> testVector = vector<vector<int>>();
    vector<int> test_vector2 = vector<int>();
    vector<vector<int>> test_vector3 = vector<vector<int>>();
    test_vector3.push_back(test_vector2);
    vector<int> test_vector4 = vector<int>();
    test_vector4.push_back(2);
    test_vector4.push_back(2);
    test_vector3.push_back(test_vector4);
    
    ai.source = 0;
    ai.target = 1;
    bi.source = 1;
    bi.target = 2;
    ci.source = 2;
    ci.target = 3;
    di.source = 4;
    di.target = 0;
    ei.source = 4;
    ei.target = 3;
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(x,t,di,testGraph).first;
    ed = add_edge(x,w,ei,testGraph).first;
    
    vector<string> label_vector = vector<string>();
    vector<vector<string>> labels = vector<vector<string>>();
    labels.push_back(label_vector);
    label_vector.push_back("HEE");
    label_vector.push_back("EEE");
    labels.push_back(label_vector);
    
    
    counter.count_connected_3_graphlets(testGraph, "sse_type", labels);
    testVector = counter.get_labeled_3_countsABS("sse_type", labels);

    
    CPPUNIT_ASSERT(testVector == test_vector3);
    
    
}

void newtestclass2::test_labeled_counts_4() {
    Graph testGraph;
    
    ti.id = 0;
    ui.id = 1;
    vi.id = 2;
    wi.id = 3;
    xi.id = 4;

    
    ti.properties["sse_type"] = "H";
    ui.properties["sse_type"] = "E";
    vi.properties["sse_type"] = "E";
    wi.properties["sse_type"] = "E";
    xi.properties["sse_type"] = "E";
    
    
    t = add_vertex(ti, testGraph);
    u = add_vertex(ui, testGraph);
    v = add_vertex(vi, testGraph);
    w = add_vertex(wi, testGraph);
    x = add_vertex(xi, testGraph);

    vector<vector<int>> testVec = vector<vector<int>>(6);
    vector<vector<int>> testVector;
    vector<int> inner_test_vector = vector<int>();
    testVec[0] = inner_test_vector;
    testVec[1] = inner_test_vector;
    testVec[2] = inner_test_vector;
    testVec[3] = inner_test_vector;
    testVec[4] = inner_test_vector;
    inner_test_vector.push_back(2);
    inner_test_vector.push_back(1);
    testVec[5] = inner_test_vector;
    
    ai.source = 0;
    ai.target = 1;
    bi.source = 1;
    bi.target = 2;
    ci.source = 2;
    ci.target = 3;
    di.source = 3;
    di.target = 4;
    ei.source = 4;
    ei.target = 0;
    
    ad = add_edge(t,u,ai,testGraph).first;
    bd = add_edge(u,v,bi,testGraph).first;
    cd = add_edge(v,w,ci,testGraph).first;
    dd = add_edge(w,x,di,testGraph).first;
    ed = add_edge(x,t,ei,testGraph).first;
    
    vector<string> label_vector = vector<string>();
    vector<vector<string>> labels = vector<vector<string>>(6);
    labels[0] = label_vector;
    labels[1] = label_vector;
    labels[2] = label_vector;
    labels[3] = label_vector;
    labels[4] = label_vector;
    label_vector.push_back("HEEE");
    label_vector.push_back("EEEE");
    labels[5] = label_vector;
    
    
    counter.count_connected_4_graphlets(testGraph, "sse_type", labels);
    testVector = counter.get_labeled_4_countsABS("sse_type", labels);

    CPPUNIT_ASSERT(testVector == testVec);
    
    
}