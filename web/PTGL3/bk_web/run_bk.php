<?php
/** 
 * @author Tim Schäfer
 */

ini_set('display_errors', 0);
ini_set('display_startup_errors', 0);
ini_set('log_errors', TRUE);
error_reporting(E_ERROR);

$valid_values = FALSE;
// get config values
include('./backend/config.php'); 

if($DEBUG){
	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	ini_set('log_errors', TRUE);
	error_reporting(E_ALL);
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
  if (preg_match('/^[A-Z0-9]{1}$/i', $str)) {
    return true;
  }  
  return false;
}

function get_folding_graph_file_name_no_ext($pdbid, $chain, $graphtype_string, $fg_number) {
  return $pdbid . "_" . $chain . "_" . $graphtype_string . "_FG_" . $fg_number;
}

function get_path_to($pdbid, $chain) {
  $mid2chars = substr($pdbid, 1, 2);
  return $mid2chars . "/" . $pdbid . "/". $chain . "/";
}

function get_folding_graph_path_and_file_name_no_ext($pdbid, $chain, $graphtype_string, $fg_number) {
  $path = get_path_to($pdbid, $chain);
  $fname = get_folding_graph_file_name_no_ext($pdbid, $chain, $graphtype_string, $fg_number);
  return $path . $fname;
}

$pageload_was_search = FALSE;
$valid_values = FALSE;

if(isset($_GET['first_pdbchain']) && isset($_GET['first_graphtype_int']) &&  isset($_GET['second_pdbchain']) && isset($_GET['second_graphtype_int'])){
        $pageload_was_search = TRUE;
	$valid_values = FALSE;
	$first_pdbchain = $_GET["first_pdbchain"];
	$second_pdbchain = $_GET["second_pdbchain"];
	$first_graphtype_int = $_GET["first_graphtype_int"];
	$second_graphtype_int = $_GET["second_graphtype_int"];

	$valid_values_first = FALSE;
	if($first_graphtype_int === "1" || $first_graphtype_int === "2" || $first_graphtype_int === "3" || 
	    $first_graphtype_int === "4" || $first_graphtype_int === "5" || $first_graphtype_int === "6") 
	    { 

			if(strlen($first_pdbchain) === 5) {
				$first_pdb_id = substr($first_pdbchain, 0, 4);
				$first_chain_name = substr($first_pdbchain, 4, 1);						
			
				if(check_valid_pdbid($first_pdb_id) && check_valid_chainid($first_chain_name)) {
					$valid_values_first = TRUE;				  
				}
			}					
	}
	
	$valid_values_second = FALSE;
	if($second_graphtype_int === "1" || $second_graphtype_int === "2" || $second_graphtype_int === "3" || 
	    $second_graphtype_int === "4" || $second_graphtype_int === "5" || $second_graphtype_int === "6") 
	    { 

			if(strlen($second_pdbchain) === 5) {
				$second_pdb_id = substr($second_pdbchain, 0, 4);
				$second_chain_name = substr($second_pdbchain, 4, 1);						
			
				if(check_valid_pdbid($second_pdb_id) && check_valid_chainid($second_chain_name)) {
					$valid_values_second = TRUE;				  
				}
			}					
	}
	
	$valid_values = ($valid_values_first && $valid_values_second);
}

$num_found = 0;

if($valid_values){    	
	// check for the GML files
	$first_graphtype_str = get_graphtype_string($first_graphtype_int);	
	$first_graph_file_name_no_ext = get_protein_graph_path_and_file_name_no_ext($first_pdb_id, $first_chain_name, $first_graphtype_str);
	$first_full_file = $IMG_ROOT_PATH . $first_graph_file_name_no_ext . ".gml";
	if(file_exists($first_full_file)){
		echo "First GML file found";
	}

	$second_graphtype_str = get_graphtype_string($second_graphtype_int);	
	$second_graph_file_name_no_ext = get_protein_graph_path_and_file_name_no_ext($second_pdb_id, $second_chain_name, $second_graphtype_str);
	$second_full_file = $IMG_ROOT_PATH . $second_graph_file_name_no_ext . ".gml";
	if(file_exists($second_full_file)){
		echo "Second GML file found";
	}	


} else {
     //echo "invalid";
	$tableString = "";
}

//EOF
?>