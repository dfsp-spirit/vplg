#!/bin/sh

source ./settings

echo "Number of total contacts:"
psql $PSQL_OPTIONS -c 'SELECT count(contact_id) as num_contacts from plcc_contact;'

echo "Number of mixed contacts:"
psql $PSQL_OPTIONS -c 'SELECT count(contact_id) as num_contacts from plcc_contact where contact_type = 1;'

echo "Number of parallel contacts:"
psql $PSQL_OPTIONS -c 'SELECT count(contact_id) as num_contacts from plcc_contact where contact_type = 2;'

echo "Number of antiparallel contacts:"
psql $PSQL_OPTIONS -c 'SELECT count(contact_id) as num_contacts from plcc_contact where contact_type = 3;'

echo "Number of ligand contacts:"
psql $PSQL_OPTIONS -c 'SELECT count(contact_id) as num_contacts from plcc_contact where contact_type = 4;'

