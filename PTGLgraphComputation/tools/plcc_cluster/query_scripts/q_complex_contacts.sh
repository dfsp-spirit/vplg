#!/bin/sh

source ./settings

echo "Number of PDB files in database:"
psql $PSQL_OPTIONS -c 'SELECT count(pdb_id) from plcc_protein;'

echo "Number of chains:"
psql $PSQL_OPTIONS -c 'SELECT count(chain_id) from plcc_chain;'

echo "Total number of complex contacts:"
psql $PSQL_OPTIONS -c "SELECT count(complex_contact_id) FROM plcc_complex_contact;"

echo "Subcontacts: number of complex contacts which include HH contacts:"
psql $PSQL_OPTIONS -c "SELECT count(complex_contact_id) FROM plcc_complex_contact WHERE (contact_num_HH > 0);"
echo "Subcontacts: Sum of all HH contacts:"
psql $PSQL_OPTIONS -c "SELECT sum(contact_num_HH) FROM plcc_complex_contact;"

echo "Subcontacts: number of complex contacts which include HS contacts:"
psql $PSQL_OPTIONS -c "SELECT count(complex_contact_id) FROM plcc_complex_contact WHERE (contact_num_HS > 0);"
echo "Subcontacts: Sum of all HS contacts:"
psql $PSQL_OPTIONS -c "SELECT sum(contact_num_HS) FROM plcc_complex_contact;"

echo "Subcontacts: number of complex contacts which include HL contacts:"
psql $PSQL_OPTIONS -c "SELECT count(complex_contact_id) FROM plcc_complex_contact WHERE (contact_num_HL > 0);"
echo "Subcontacts: Sum of all HL contacts:"
psql $PSQL_OPTIONS -c "SELECT sum(contact_num_HL) FROM plcc_complex_contact;"

echo "Subcontacts: number of complex contacts which include SS contacts:"
psql $PSQL_OPTIONS -c "SELECT count(complex_contact_id) FROM plcc_complex_contact WHERE (contact_num_SS > 0);"
echo "Subcontacts: Sum of all SS contacts:"
psql $PSQL_OPTIONS -c "SELECT sum(contact_num_SS) FROM plcc_complex_contact;"

echo "Subcontacts: number of complex contacts which include SL contacts:"
psql $PSQL_OPTIONS -c "SELECT count(complex_contact_id) FROM plcc_complex_contact WHERE (contact_num_SL > 0);"
echo "Subcontacts: Sum of all SL contacts:"
psql $PSQL_OPTIONS -c "SELECT sum(contact_num_SL) FROM plcc_complex_contact;"

echo "Subcontacts: number of complex contacts which include LL contacts:"
psql $PSQL_OPTIONS -c "SELECT count(complex_contact_id) FROM plcc_complex_contact WHERE (contact_num_LL > 0);"
echo "Subcontacts: Sum of all LL contacts:"
psql $PSQL_OPTIONS -c "SELECT sum(contact_num_LL) FROM plcc_complex_contact;"

echo "Subcontacts: number of complex contacts which include DS contacts:"
psql $PSQL_OPTIONS -c "SELECT count(complex_contact_id) FROM plcc_complex_contact WHERE (contact_num_DS > 0);"
echo "Subcontacts: Sum of all DS contacts:"
psql $PSQL_OPTIONS -c "SELECT sum(contact_num_DS) FROM plcc_complex_contact;"










