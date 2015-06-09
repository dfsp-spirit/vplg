#!/bin/sh
# doTests.sh -- compile and run the unit tests for GraphletAnalyzer
# written by TS, 2015

APPTAG="[dT] "

if [ "$1" = "--compile" ]; then
    echo "$APPTAG Compiling tests to 'GATests'..."
    g++ -oGATests -lboost_unit_test_framework TestDriver.cpp
    RETVAL=$?
    if [ $RETVAL -ne 0 ]; then
      echo "$APPTAG ERROR: Compilation failed (return value was: $RETVAL). Skipping tests and exiting."
      exit 1
    fi
fi

echo "$APPTAG Running tests..."

./GATests --log_level=test_suite

echo "$APPTAG All done, exiting."
exit 0
