#!/bin/bash
echo "===PG tests==="
for FILE in *_PG.gml; do echo "Running GA for PG file '$FILE'..."; ./graphletanalyser --sse_graph --silent --timerun $FILE; done


echo "===CG tests==="
for FILE in *_CG.gml; do echo "Running GA for CG file '$FILE'..."; ./graphletanalyser --complex_graph --silent --timerun $FILE; done


echo "===AAG tests==="
for FILE in *_aagraph.gml; do echo "Running GA for AAG file '$FILE'..."; ./graphletanalyser --aa_graph --silent --timerun $FILE; done

