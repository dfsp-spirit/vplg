#!/bin/sh

source ./settings

echo "Average length of an SSE (in residues):"
psql $PSQL_OPTIONS -c 'SELECT avg(dssp_end - dssp_start) + 1 as avg_length from plcc_sse;'

echo "Average length of a helix SSE:"
psql $PSQL_OPTIONS -c 'SELECT avg(dssp_end - dssp_start) + 1 as avg_length from plcc_sse WHERE sse_type = 1;'

echo "Average length of a beta sheet SSE:"
psql $PSQL_OPTIONS -c 'SELECT avg(dssp_end - dssp_start) + 1 as avg_length from plcc_sse WHERE sse_type = 2;'

echo "Average length of a ligand SSE:"
psql $PSQL_OPTIONS -c 'SELECT avg(dssp_end - dssp_start) + 1 as avg_length from plcc_sse WHERE sse_type = 3;'

echo "Average length of other SSEs:"
psql $PSQL_OPTIONS -c 'SELECT avg(dssp_end - dssp_start) + 1 as avg_length from plcc_sse WHERE sse_type = 4;'

