#!/bin/sh
echo "Counting proteins..."
NUMPROT=$(find -mindepth 2 -maxdepth 2 -type d | wc -l)
echo "Found $NUMPROT proteins."

