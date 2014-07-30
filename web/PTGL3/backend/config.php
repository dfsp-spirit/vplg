<?php
/** This file stores the configuration and the database connection information
 *  in an array in returns it.
 *   
 * @author Daniel Bruness <dbruness@gmail.com>
 * @author Andreas Scheck <andreas.scheck.home@googlemail.com>
 */

/**
 * Returns the configuration data in an associative array
 */
return array(
	//---- Database settings ----//
    'host'	=> '192.168.185.248',
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
	'img_root_path'		=> './data/',
	//---- End of file settings ----//
	
	
	//---- Debug settings ----//
	'debug_mode' => TRUE
	//---- End of debug settings ----//
);
//EOF
?>