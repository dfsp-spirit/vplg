#!/bin/sh

source ./settings

echo "Number of chains which contain a motif:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif;'

echo "Number of chains which contain an alpha motif:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motiftype_id = 1;'

echo "Number of chains which contain a beta motif:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motiftype_id = 2;'

echo "Number of chains which contain an albe motif:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motiftype_id = 3;'

echo "Number of chains which contain the motif 4helix:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motif_id = 1;'

echo "Number of chains which contain the motif globin:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motif_id = 2;'

echo "Number of chains which contain the motif barrel:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motif_id = 3;'

echo "Number of chains which contain the motif immuno:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motif_id = 4;'

echo "Number of chains which contain the motif propeller:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motif_id = 5;'

echo "Number of chains which contain the motif jelly:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motif_id = 6;'

echo "Number of chains which contain the motif ubi:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motif_id = 7;'

echo "Number of chains which contain the motif plait:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motif_id = 8;'

echo "Number of chains which contain the motif rossman:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motif_id = 9;'

echo "Number of chains which contain the motif tim:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif ctm INNER JOIN plcc_motif m ON ctm.motif_id = m.motif_id WHERE m.motif_id = 10;'

