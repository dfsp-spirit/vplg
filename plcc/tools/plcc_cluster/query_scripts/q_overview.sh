#!/bin/sh

source ./settings

echo "Number of pdb_ids:"
psql $PSQL_OPTIONS -c 'SELECT count(pdb_id) from plcc_protein;'

echo "Number of chains:"
psql $PSQL_OPTIONS -c 'SELECT count(chain_id) from plcc_chain;'

echo "Number of SSEs:"
psql $PSQL_OPTIONS -c 'SELECT count(sse_id) from plcc_sse;'

echo "Number of SSEs of type 1 (helix)":
psql $PSQL_OPTIONS -c 'SELECT count(sse_id) from plcc_sse WHERE sse_type = 1;'

echo "Number of SSEs of type 2 (beta strand)":
psql $PSQL_OPTIONS -c 'SELECT count(sse_id) from plcc_sse WHERE sse_type = 2;'

echo "Number of SSEs of type 3 (ligand)":
psql $PSQL_OPTIONS -c 'SELECT count(sse_id) from plcc_sse WHERE sse_type = 3;'

echo "Number of SSEs of type 4 (other)":
psql $PSQL_OPTIONS -c 'SELECT count(sse_id) from plcc_sse WHERE sse_type = 4;'


