#!/bin/sh

# piped sed removes leading spaces if existing (http://linuxforen.de/forums/showthread.php?241010-shell-leerzeichen-entfernen)

source ./settings

echo "Creating list of inserted PDB IDs (in plcc_protein)."
psql $PSQL_OPTIONS -c 'SELECT pdb_id FROM plcc_protein;' | sed 's/^ //' > ./results/list_protein_pdbids.txt

echo "Creating list of inserted PDB IDs (atleast 1 chain present)."
psql $PSQL_OPTIONS -c 'SELECT DISTINCT pdb_id FROM plcc_chain;' | sed 's/^ //' > ./results/list_chain_pdbids.txt

echo "Creating list of successfully inserted PDB IDs (insert_completed = t)."
psql $PSQL_OPTIONS -c "SELECT pdb_id FROM plcc_protein WHERE insert_completed = 't';" | sed 's/^ //' > ./results/list_successful_pdbids.txt

echo "Creating list of successfully inserted non-large PDB IDs (insert_completed = t)."
psql $PSQL_OPTIONS -c "SELECT pdb_id FROM plcc_protein WHERE insert_completed = 't' AND is_large_structure = 'f';" | sed 's/^ //' > ./results/list_successful_non-large_pdbids.txt

echo "Creating list of successfully inserted large PDB IDs (insert_completed = t)."
psql $PSQL_OPTIONS -c "SELECT pdb_id FROM plcc_protein WHERE insert_completed = 't' AND is_large_structure = 't';" | sed 's/^ //' > ./results/list_successful_large_pdbids.txt

echo "Creating list of aborted PDB IDs (insert_completed = f)."
psql $PSQL_OPTIONS -c "SELECT pdb_id FROM plcc_protein WHERE insert_completed = 'f';" | sed 's/^ //' > ./results/list_aborted_pdbids.txt


