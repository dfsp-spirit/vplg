<?php
/** This file provides search for PG graphlets.
 * 
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


function get_cg_graphlets_query_string($pdb_id) {
  $query = "SELECT cg.pdb_id, array_to_json(cgg.complex_graphlet_counts) AS graphlets FROM plcc_complex_graphlets cgg INNER JOIN plcc_complexgraph cg ON cgg.complexgraph_id = cg.complexgraph_id WHERE (cg.pdb_id = '$pdb_id');";
  return $query;
}

function visualize_share($v) {
  $res = "";
  if($v <= 0.0) { return ""; }
  if($v >= 1.0) { return str_repeat(".",100); }
  $times = floor($v * 100);
  return str_repeat(".", $times);
}


function check_valid_pdbid($str) {
  if (preg_match('/^[A-Z0-9]{4}$/i', $str)) {
    return true;
  }  
  return false;
}



function get_path_to($pdbid, $chain) {
  $mid2chars = substr($pdbid, 1, 2);
  return $mid2chars . "/" . $pdbid . "/". $chain . "/";
}


$pageload_was_search = FALSE;
$valid_values = FALSE;

if(isset($_GET['pdb_id'])){
        $pageload_was_search = TRUE;
		$pdb_id = $_GET['pdb_id'];
	$valid_values = FALSE;
			
				if(check_valid_pdbid($pdb_id)) {
					$valid_values = TRUE;				  
				}
								
}

$num_found = 0;

if($valid_values){
    //echo "valid";
	// connect to DB
	$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	$db = pg_connect($conn_string);
	if(! $db) { array_push($SHOW_ERROR_LIST, "Database connection failed."); }
	//if(! $db) { echo "NO_DB"; }
	
	$query = get_cg_graphlets_query_string($pdb_id);
	
	
	//echo "query='" . $query . "'\n";
	
	$result = pg_query($db, $query);
    //if(! $result) { echo "NO_RESULT: " .  pg_last_error($db) . "."; }
	if(! $result) { array_push($SHOW_ERROR_LIST, "Database query failed: '" . pg_last_error($db) . "'"); }
	$tableString = "";
	$tableString .= "<div><table id='tblgraphletresults'>\n";
	$tableString .= "<caption> The graphlets of the CG of PDB $pdb_id </caption>\n";
	$tableString .= "<tr>
    <th>PDB</th>
	<th>Relative graphlet frequency</th>
      </tr>\n";
		
	$num_found = 0;
	$img_string = "";
	$html_id = "";
	while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
	    $res_pdb_id = $arr['pdb_id'];
	    $res_graphlets_array = (array) json_decode($arr['graphlets'], true);
	    $res_graphlets = implode(", ", $res_graphlets_array);  // format array for output
	    //$res_graphlets = $res_graphlets_array;

		
		$tableString .= "<tr>\n";
		$tableString .= "<td>$res_pdb_id</td><td>$res_graphlets</td>\n";
		$tableString .= "</tr>\n";
				
		
		
		$num_found++;
				
	}		
	
	$tableString .= "</table></div>\n";
	
	$tableString .= "<br/><br />\n";
	
	if($num_found == 1 && count($res_graphlets) > 0) {
	    $tableString .= "<div><table id='tblgraphletresultsdetails'>\n";
	  $tableString .= "<caption> Graphlets details of the CG of PDB $pdb_id </caption>\n";
	  $tableString .= "<tr><th>Graphlet</th><th>Relative Count</th><th>Vis</th>\n";
	  foreach ($res_graphlets_array as $k=>$v){
		$tableString .= "<tr><td>$k</td><td>$v</td><td>" . visualize_share($v) . "</td></tr>\n";
	  }
	  $tableString .= "</table></div>\n";
	
	}
		


} else {
     //echo "invalid";
	$tableString = "";
}

//EOF
?>