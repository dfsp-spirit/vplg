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
   $query = "SELECT linnot_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, filepath_linnot_image_adj_png, filepath_linnot_image_red_png, filepath_linnot_image_seq_png, filepath_linnot_image_key_png, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_seq, firstvertexpos_key, num_sses FROM (SELECT la.num_sses, la.linnot_id, la.ptgl_linnot_adj, la.ptgl_linnot_red, la.ptgl_linnot_key, la.ptgl_linnot_seq, la.firstvertexpos_adj, la.firstvertexpos_red, la.firstvertexpos_seq, la.firstvertexpos_key, la.filepath_linnot_image_adj_png, la.filepath_linnot_image_red_png, la.filepath_linnot_image_seq_png, la.filepath_linnot_image_key_png, fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, c.pdb_id AS pdb_id FROM plcc_fglinnot la LEFT JOIN plcc_foldinggraph fg ON la.linnot_foldinggraph_id = fg.foldinggraph_id LEFT JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN plcc_chain c ON pg.chain_id=c.chain_id LEFT JOIN plcc_graphtypes gt ON pg.graph_type=gt.graphtype_id WHERE ( graphtype_text = '" . $graphtype_str . "' AND chain_name = '" . $chain_name . "' AND pdb_id = '" . $pdb_id . "' )) bar";
   return $query;
}

function get_graphtype_string($graphtype_int){
	switch ($graphtype_int){
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
    //echo "valid";
	// connect to DB
	$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	$db = pg_connect($conn_string);
		
	$graphtype_str = get_graphtype_string($graphtype_int);
	$query = get_fglinnots_data_query_string($pdb_id, $chain_name, $graphtype_str);
	
	//echo "query='" . $query . "'\n";
	
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
		$tableString .= "PDB " . $pdb_id . " chain " . $chain_name . " fold #" . $fg_number . ", fold name is " . $fold_name . ". $num_sses SSEs. Images: $img_adj, $img_red, $img_seq, $img_key.";
		$tableString .= "</div>";
				
	}
	
	$tableString .= "</div>";


} else {
     //echo "invalid";
	$tableString = "";
}

//EOF
?>