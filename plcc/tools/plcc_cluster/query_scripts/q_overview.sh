#!/bin/sh

source ./settings

echo "Number of pdb_ids:"
psql $PSQL_OPTIONS -c 'SELECT count(pdb_id) from plcc_protein;'

echo "Number of chains:"
psql $PSQL_OPTIONS -c 'SELECT count(chain_id) from plcc_chain;'

echo "Number of protein graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(graph_id) from plcc_graph;'

echo "Number of alpha protein graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(graph_id) from plcc_graph g WHERE g.graph_type = 1;'

echo "Number of beta protein graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(graph_id) from plcc_graph g WHERE g.graph_type = 2;'

echo "Number of albe protein graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(graph_id) from plcc_graph g WHERE g.graph_type = 3;'

echo "Number of alphalig protein graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(graph_id) from plcc_graph g WHERE g.graph_type = 4;'

echo "Number of betalig protein graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(graph_id) from plcc_graph g WHERE g.graph_type = 5;'

echo "Number of albelig protein graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(graph_id) from plcc_graph g WHERE g.graph_type = 6;'

echo "Number of folding graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph;'

echo "Number of folding graphs of size 1:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph WHERE ( char_length(sse_string) = 1 );'

echo "Number of alpha-PG-based folding graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 1 ) ;'

echo "Number of alpha-PG-based folding graphs of size 1:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 1 AND char_length(fg.sse_string) = 1 ) ;'

echo "Number of beta-PG-based folding graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 2 ) ;'

echo "Number of beta-PG-based folding graphs of size 1:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 2 AND char_length(fg.sse_string) = 1 ) ;'

echo "Number of albe-PG-based folding graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 3 ) ;'

echo "Number of albe-PG-based folding graphs of size 1:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 3 AND char_length(fg.sse_string) = 1 ) ;'

echo "Number of alphalig-PG-based folding graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 4 ) ;'

echo "Number of alphalig-PG-based folding graphs of size 1:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 4 AND char_length(fg.sse_string) = 1 ) ;'

echo "Number of betalig-PG-based folding graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 5 ) ;'

echo "Number of betalig-PG-based folding graphs of size 1:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 5 AND char_length(fg.sse_string) = 1 ) ;'

echo "Number of albelig-PG-based folding graphs:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 6 ) ;'

echo "Number of albelig-PG-based folding graphs of size 1:"
psql $PSQL_OPTIONS -c 'SELECT count(foldinggraph_id) from plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id WHERE ( g.graph_type = 6 AND char_length(fg.sse_string) = 1 ) ;'


echo "Number of SSEs:"
psql $PSQL_OPTIONS -c 'SELECT count(sse_id) from plcc_sse;'

echo "Number of SSEs of type 1 (helix)":
psql $PSQL_OPTIONS -c 'SELECT count(sse_id) from plcc_sse WHERE sse_type = 1;'

echo "Number of SSEs of type 2 (beta strand)":
psql $PSQL_OPTIONS -c 'SELECT count(sse_id) from plcc_sse WHERE sse_type = 2;'

echo "Number of SSEs of type 3 (ligand)":
psql $PSQL_OPTIONS -c 'SELECT count(sse_id) from plcc_sse WHERE sse_type = 3;'

echo "Number of SSEs of type 4 (other)":
psql $PSQL_OPTIONS -c 'SELECT count(sse_id) from plcc_sse WHERE sse_type = 4;'

echo "Number of chains which contain a motif:"
psql $PSQL_OPTIONS -c 'SELECT count(chaintomotif_id) from plcc_nm_chaintomotif;'



