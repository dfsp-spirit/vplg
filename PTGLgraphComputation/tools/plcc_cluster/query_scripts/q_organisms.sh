#!/bin/sh

source ./settings

echo "Number of organism occurences in chains:"
psql $PSQL_OPTIONS -c 'SELECT count(organism_scientific) AS occurences, organism_scientific FROM plcc_chain GROUP BY organism_scientific ORDER BY occurences DESC LIMIT 20;'



