<?php
/** 
 * Basic database function required in many places. 
 * @author Tim Schaefer
 */



/**
  * Connects to the database using the credentials from the $CONFIG dictionary. Returns a connection resource on success or FALSE on failure. Reuses an existing connection if available.
  * @return a connection resource on success or FALSE on connection failure
  */
function connnect_to_db_and_get_handle() {
  $conn_string = "host=" . $CONFIG['host'] . " port=" . $CONFIG['port'] . " dbname=" . $CONFIG['db'] . " user=" . $CONFIG['user'] ." password=" . $CONFIG['pw'];
  $db = pg_connect($conn_string);
  return $db;
}

/**
  * Returns the internal database ID of the chain with given chain name and PDB identifier. Or a value <= 0 on error.
  * @param $db a database connection handle
  * @param $pdb_id the PDB identifier, e.g., "7tim"
  * @param chain_name a PDB chain name, e.g., "A"
  * @return the internal database ID of the chain (int) or a value <= 0 on error
  */
function get_internal_database_id_of_chain($db, $pdb_id, $chain_name) {
  pg_query($db, "DEALLOCATE ALL");
  $query = "SELECT c.chain_id FROM plcc_chain c WHERE pdb_id = $1 AND chain_name = $2;";
  
  pg_prepare($db, "get_chain_id", $query) or die($query . ' -> Query failed: ' . pg_last_error());		
  $result = pg_execute($db, "get_chain_id", array($pdb_id, $chain_name));  
  $data = pg_fetch_array($result, NULL, PGSQL_ASSOC);
  $chain_id = (int) $data['chain_id'];
  return $chain_id;  
}


/**
  * Determines the database code for a given graph type string.
  * @param $graph_type the graph type string, e.g., "alpha" or "albelig"
  * @return the database code for that graph type (int, e.g., 1 for "alpha") or a value <= 0 on error.
  *
  */
function get_graph_type_int($graph_type) {
  if($graph_type == "alpha") {
    return 1;
  }
  else if($graph_type == "beta") {
    return 2;
  }
  else if($graph_type == "albe") {
    return 3;
  }
  else if($graph_type == "alphalig") {
    return 4;
  }
  else if($graph_type == "betalig") {
    return 5;
  }
  else if($graph_type == "albelig") {
    return 6;
  }
  
  return -1;
}

/**
  * Returns the path to the PNG-format image file of the requested graph. This data is retrieved from the database.
  * @param $db a database handle
  * @param $pdb_id the PDB identifier, e.g., "7tim"
  * @param $chain_name a PDB chain name, e.g., "A"
  * @param $graph_type the graph type string, e.g., "alpha" or "albelig"
  * @return the file name or an empty string on error
  */
function get_png_file_name_from_database_of($db, $pdb_id, $chain_name, $graph_type) {
  $db_chain_id = get_internal_database_id_of_chain($db, $pdb_id, $chain_name);
  $graph_type_int = get_graph_type_int($graph_type);
  if($db_chain_id > 0 && $graph_type_int > 0) {
    pg_query($db, "DEALLOCATE ALL");
    $query = "SELECT g.graph_image_png FROM plcc_graph g WHERE (g.chain_id = $1 AND g.graph_type = $2);";
  
    pg_prepare($db, "get_png_graph_image", $query) or die($query . ' -> Query failed: ' . pg_last_error());		
    $result = pg_execute($db, "get_png_graph_image", array($db_chain_id, $graph_type_int));  
    $data = pg_fetch_array($result, NULL, PGSQL_ASSOC);
    $fname = $data['graph_image_png'];
    return $fname;  
  }
  return "";
}

