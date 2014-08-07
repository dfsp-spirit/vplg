#!/bin/sh

source ./settings

echo "Number of pdb_ids:"
psql $PSQL_OPTIONS -c 'SELECT count(pdb_id) from plcc_protein;'

echo "Occurences of functions (in PDB header):"
psql $PSQL_OPTIONS -c 'SELECT count(header) as occurences, header from plcc_protein GROUP BY header ORDER BY occurences DESC LIMIT 20;'

#echo "PDBIDs and protein functions by ligand type"
#psql $PSQL_OPTIONS -c 'SELECT plcc_sse.lig_name as ligand_name, plcc_protein.header as prot_function, plcc_protein.pdb_id as PDBID FROM plcc_sse INNER JOIN plcc_chain ON plcc_sse.chain_id = plcc_chain.chain_id INNER JOIN plcc_protein ON plcc_protein.pdb_id = plcc_chain.pdb_id WHERE plcc_sse.lig_name IS NOT NULL ORDER BY plcc_sse.lig_name;'
