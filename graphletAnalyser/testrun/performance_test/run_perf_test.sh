#!/bin/bash
for FILE in *_PG.gml; do echo "Running GA for PG file '$FILE'..."; ./graphletanalyser --sse_graph --silent --timerun $FILE; done
