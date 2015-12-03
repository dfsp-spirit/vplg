#!/bin/bash
for FILE in *.gml; do echo "Running GA for file '$FILE'..."; ./graphletanalyser --sse_graph --silent --timerun $FILE; done
