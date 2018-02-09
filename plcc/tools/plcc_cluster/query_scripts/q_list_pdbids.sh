#!/bin/sh
#
# /jnw 2018
#
# writes a list of PDB IDs wich are included in the database (similar to plcc --report-db-proteins option)
#
# note: looks for chains of a ID! So if the PDB entry is included but (for what reason) no chain it is counted as not included.

source ./settings

echo "Creating list of inserted PDB IDs."
# piped sed removes leading spaces if existing (http://linuxforen.de/forums/showthread.php?241010-shell-leerzeichen-entfernen)
psql $PSQL_OPTIONS -c 'SELECT DISTINCT pdb_id FROM plcc_chain;' | sed 's/^ //' > ./results/list_pdbids.txt

echo "List of PDB IDs written to /results/list_pdbids.txt"
