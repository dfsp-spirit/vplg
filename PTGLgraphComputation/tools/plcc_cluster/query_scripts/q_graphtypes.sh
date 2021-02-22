#!/bin/sh

source ./settings

echo "Number of alpha graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 1;'
echo "Number of alpha graphs which contain a beta barrel:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 1 AND g.graph_containsbetabarrel = 1;'
echo "Number of SSEs in alpha graphs:"
psql $PSQL_OPTIONS -c 'SELECT count (s2g.ssetoproteingraph_id) as num_in_alpha FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 1;'

echo "Number of beta graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 2;'
echo "Number of beta graphs which contain a beta barrel:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 2 AND g.graph_containsbetabarrel = 1;'
echo "Number of SSEs in beta graphs:"
psql $PSQL_OPTIONS -c 'SELECT count (s2g.ssetoproteingraph_id) as num_in_beta FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 2;'


echo "Number of albe graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 3;'
echo "Number of albe graphs which contain a beta barrel:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 3 AND g.graph_containsbetabarrel = 1;'
echo "Number of SSEs in albe graphs:"
psql $PSQL_OPTIONS -c 'SELECT count (s2g.ssetoproteingraph_id) as num_in_albe FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 3;'

echo "Number of alphalig graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 4;'
echo "Number of alphalig graphs which contain a beta barrel:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 4 AND g.graph_containsbetabarrel = 1;'
echo "Number of SSEs in alphalig graphs:"
psql $PSQL_OPTIONS -c 'SELECT count (s2g.ssetoproteingraph_id) as num_in_alphalig FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 4;'


echo "Number of betalig graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 5;'
echo "Number of betalig graphs which contain a beta barrel:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 5 AND g.graph_containsbetabarrel = 1;'
echo "Number of SSEs in betalig graphs:"
psql $PSQL_OPTIONS -c 'SELECT count (s2g.ssetoproteingraph_id) as num_in_betalig FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 5;'


echo "Number of albelig graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 6;'
echo "Number of albelig graphs which contain a beta barrel:"
psql $PSQL_OPTIONS -c 'SELECT count(g.graph_id) from plcc_graph g WHERE g.graph_type = 6 AND g.graph_containsbetabarrel = 1;'
echo "Number of SSEs in albelig graphs:"
psql $PSQL_OPTIONS -c 'SELECT count (s2g.ssetoproteingraph_id) as num_in_albelig FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 6;'





