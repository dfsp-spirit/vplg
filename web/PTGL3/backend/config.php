<?php
/*
 *  postgreSQL Config 
 *  returns DB-values as array

 'host' => '192.168.185.248',
 */

return array(
	## Database settings ##
    'host' => '192.168.185.248',
    'user' => 'vplg',
    'pw' => 'vplg',
    'db' => 'vplg',
    'port' => '5432',
    ## End of database settings ##
	
	## Search Settings ##
    'enable_complex_graphs' => FALSE,
    'enable_motif_search' => FALSE,
	'enable_blast_search' => FALSE
);
?>
