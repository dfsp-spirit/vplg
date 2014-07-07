<?php
/**
 *  postgreSQL Config 
 *  returns DB-values as array
 *
 *
 */

return array(
	//---- Database settings ----//
    'host'	=> '192.168.185.248', // UNI_DB 192.168.185.248
    'user'	=> 'vplg',
    'pw'	=> 'vplg',
    'db'	=> 'vplg',
    'port'	=> '5432',
    //---- End of database settings ----//
	
	
	//---- Search Settings ----//
    'enable_complex_graphs'	=> FALSE,
    'enable_motif_search'	=> FALSE,
	'enable_blast_search'	=> FALSE,
	'use_logic_operators'	=> FALSE,
	//---- End of search settings ----//
	
	
	//---- File settings ----//
	'build_file_path'	=> FALSE,
	'img_root_path'		=> './data/'
	//---- End of file settings ----//
);
?>
