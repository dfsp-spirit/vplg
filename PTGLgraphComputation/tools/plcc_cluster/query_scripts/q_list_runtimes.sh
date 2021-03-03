source ./settings

echo "Getting all PDB IDs and their runtime for insertion in seconds"
psql $PSQL_OPTIONS -c 'SELECT pdb_id, runtime_secs FROM plcc_protein;' | sed 's/^ //' > ./results/list_runtimes.txt
