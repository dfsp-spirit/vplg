#!/bin/sh

source ./settings

echo "Number of HEM ligands:"
psql $PSQL_OPTIONS -c "SELECT count(sse_id) FROM plcc_sse WHERE lig_name = 'HEM';"

#echo "Number of total HEM contacts:"
#psql $PSQL_OPTIONS -c "SELECT count(contact_id) FROM ligand_type_contacts WHERE sse1_name = 'HEM' OR sse2_name = 'HEM';"

#echo "Number of HEM contacts with helices:"
#psql $PSQL_OPTIONS -c "SELECT count(contact_id) FROM ligand_type_contacts WHERE ((sse1_name = 'HEM' OR sse2_name = 'HEM') AND (sse1_type = 1 OR sse2_type = 1));"

#echo "Number of HEM contacts with beta strands:"
#psql $PSQL_OPTIONS -c "SELECT count(contact_id) FROM ligand_type_contacts WHERE ((sse1_name = 'HEM' OR sse2_name = 'HEM') AND (sse1_type = 2 OR sse2_type = 2));"

#echo "Number of HEM contacts with ligands:"
#psql $PSQL_OPTIONS -c "SELECT count(contact_id) FROM ligand_type_contacts WHERE ((sse1_name = 'HEM' OR sse2_name = 'HEM') AND (sse1_type = 3 AND sse2_type = 3));"

#echo "List of all ligands which have contacts to HEM groups:"
#psql $PSQL_OPTIONSq -c "SELECT DISTINCT sse1_name, sse2_name FROM ligand_type_contacts WHERE ((sse1_name = 'HEM' OR sse2_name = 'HEM') AND (sse1_name IS NOT NULL AND sse2_name IS NOT NULL)) ORDER BY sse1_name;"

echo "The number of SSEs which do not have ANY contacts (i.e., are isolated):"
psql $PSQL_OPTIONS -c "SELECT count(sse_id) FROM plcc_sse as a WHERE NOT EXISTS (SELECT * FROM plcc_contact as b WHERE b.sse1 = a.sse_id OR b.sse2 = a.sse_id);"

echo "The number of ligands:"
psql $PSQL_OPTIONS -c "SELECT count(sse_id) FROM plcc_sse WHERE sse_type = 3;"

echo "The number of ligands which DO have contacts to other SSEs:"
psql $PSQL_OPTIONS -c "SELECT count(sse_id) FROM plcc_sse as a WHERE sse_type=3 AND EXISTS (SELECT * FROM plcc_contact as b WHERE b.sse1 = a.sse_id OR b.sse2 = a.sse_id);"

echo "The number of ligands which do NOT have any contacts:"
psql $PSQL_OPTIONS -c "SELECT count(sse_id) FROM plcc_sse as a WHERE sse_type=3 AND NOT EXISTS (SELECT * FROM plcc_contact as b WHERE b.sse1 = a.sse_id OR b.sse2 = a.sse_id);"

#SELECT sse_id FROM plcc_sse WHERE ( sse_type = 3 ) EXCEPT ( SELECT sse1 FROM plcc_contact UNION SELECT sse2 FROM plcc_contact );






