#define BOOST_TEST_MODULE Fixtures
#include <boost/test/included/unit_test.hpp>

/** 
    TestDriver.cpp -- Unit tests for GraphletAnalyzer, implemented using the Boost test framework.
	Written by ts, 2015.
	
    In order to run these tests, you need  to:
	1) install libboost-test-dev package on your dev machine
	2) during compilation, add '-lboost_unit_test_framework' to the linker flags, like this: 
	    
		g++ -oGATests -lboost_unit_test_framework TestDriver.cpp
		
	3) Then run ./GATest
			
*/


// ------ example functions that should be tested (we should include these from the GA main source files) ------
int add(int i, int j) {
    return i + j;
}
// ------ end of functions that should be tested ------


// Use a Fixture to prepare a common test environment for all tests in a suite. Note that the constructor is
//   called before the tests, while the destructor is called after the tests. So you can use them to setup/teardown the test environment.
struct TestEnvironment {
    int availableWithinTestFunctions;
    
    TestEnvironment(): availableWithinTestFunctions(4) {
        // constructor, setup stuff here;
        // ...
        BOOST_TEST_MESSAGE("Test environment ready.");      
    }
    
    ~TestEnvironment() {
        // destructor, teardown stuff here:
        // ...
        BOOST_TEST_MESSAGE("Tests done, test environment cleared.");      
    }
};

 
// we can organize tests into test suites, here is the 1st suite:
BOOST_FIXTURE_TEST_SUITE(Maths, TestEnvironment)
 
BOOST_AUTO_TEST_CASE(universeInOrder)
{
    BOOST_CHECK(add(2, 2) == 4);
}
 
BOOST_AUTO_TEST_SUITE_END()
 






// here is a second test suite: 
 
BOOST_AUTO_TEST_SUITE(Physics)
 
BOOST_AUTO_TEST_CASE(specialTheory)
{
    int e = 3;
    int m = 2;
    int c = 4;
 
    BOOST_CHECK(e == m * c * c);
}
 
BOOST_AUTO_TEST_SUITE_END()






// here is a third test suite: 
// this is the first test suite which actually tests stuff from GraphletAnalyzer

BOOST_AUTO_TEST_SUITE(ProteinGraph)

//#include "../ProteinGraph.h"

BOOST_AUTO_TEST_CASE(ProteinGraphConstructor)
{
    //ProteinGraph graph("testdata/testdata_3nodes.gml");
    int e = 3;
    int m = 2;
    int c = 4;
 
    BOOST_CHECK(e == m * c * c);
}
 
BOOST_AUTO_TEST_SUITE_END()

