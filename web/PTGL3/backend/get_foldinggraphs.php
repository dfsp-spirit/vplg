<?php
/** This file provides ...####
 * 
 * @author Daniel Bruness <dbruness@gmail.com>
 * @author Tim Schäfer
 */

ini_set('display_errors', 0);
ini_set('display_startup_errors', 0);
ini_set('log_errors', TRUE);
error_reporting(E_ERROR);

// get config values
include('./backend/config.php'); 

if($DEBUG){
	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	ini_set('log_errors', TRUE);
	error_reporting(E_ALL);
}

function get_fglinnots_data_query_string($pdb_id, $chain_name, $graphtype_str) {
   $query = "TODO";
   return $query;
}

function get_graphtype_string($graphtype_int){
	switch ($graphtype){
		case 1:
			return "alpha";
			break;
		case 2:
			return "beta";
			break;
		case 3:
			return "albe";
			break;
		case 4:
			return "alphalig";
			break;
		case 5:
			return "betalig";
			break;
		case 6:
			return "albelig";
			break;
	}
}

function check_valid_pdbid($str) {
  if (preg_match('/^[A-Z0-9]{4}$/i', $str)) {
    return true;
  }  
  return false;
}

function check_valid_chainid($str) {
  if (preg_match('/^[A-Z]{1}$/i', $str)) {
    return true;
  }  
  return false;
}


if(isset($_GET['pdbchain']) && isset($_GET['graphtype_int']) && isset($_GET['notationtype'])){
	$valid_values = FALSE;
	$pdbchain = $_GET["pdbchain"];
	$graphtype_int = $_GET["graphtype_int"];
	$notation = $_GET["notationtype"];
	if(($graphtype_int === "1" || $graphtype_int === "2" || $graphtype_int === "3" || 
	    $graphtype_int === "4" || $graphtype_int === "5" || $graphtype_int === "6") &&
	   ($notation === "adj" || $notation === "red" ||
		$notation === "key" || $notation === "seq")) { 

			if(strlen($pdbchain) === 5) {
				$pdb_id = substr($pdbchain, 0, 4);
				$chain_name = substr($pdbchain, 4, 1);						
			
				if(check_valid_pdbid($pdb_id) && check_valid_chainid($chain_name)) {
					$valid_values = TRUE;				  
				}
			}					
	}
}


if($valid_values){
	// connect to DB
	$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	$db = pg_connect($conn_string);
		
	$graphtype_str = get_graphtype_string($graphtype_int);
	$query = get_fglinnots_data_query_string($pdb_id, $chain_name, $graphtype_str);
	
	$result = pg_query($db, $query);
		
	
	$tableString .= "<div>";
		
	while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
	
		// data from foldinggraph table:
	    $fg_number = $arr['fg_number'];	
		$fold_name = $arr['fold_name'];
		// data from linnot table:
		$img_adj = $arr['filepath_linnot_image_adj_png'];
		$img_red = $arr['filepath_linnot_image_red_png'];
		$img_seq = $arr['filepath_linnot_image_seq_png'];
		$img_key = $arr['filepath_linnot_image_key_png'];
		$num_sses = $arr['num_sses'];
		
		$tableString .= "<div class='string_row'>";
		$tableString .= "Fold #" . $fg_number . ", fold name is " . $fold_name . ".";
		$tableString .= "</div>";
				
	}
	
	$tableString .= "</div>";


} else {
	$tableString = "";
}

//EOF
?>