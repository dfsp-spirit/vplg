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


function get_pg_graphlets_query_string($pdb_id, $chain_name, $graphtype_int) {
  $query = "SELECT c.chain_name, c.pdb_id, array_to_json(pgg.graphlet_counts) AS graphlets FROM plcc_graphlets pgg INNER JOIN plcc_graph g ON pgg.graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id WHERE (c.pdb_id = '$pdb_id' AND c.chain_name = '$chain_name' AND g.graph_type = $graphtype_int);";
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
  if (preg_match('/^[A-Z0-9]{1}$/i', $str)) {
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

if(isset($_GET['pdbchain']) && isset($_GET['graphtype_int']) ){
        $pageload_was_search = TRUE;
	$valid_values = FALSE;
	$pdbchain = $_GET["pdbchain"];
	$graphtype_int = $_GET["graphtype_int"];
	if(($graphtype_int === "1" || $graphtype_int === "2" || $graphtype_int === "3" || 
	    $graphtype_int === "4" || $graphtype_int === "5" || $graphtype_int === "6") ) { 

			if(strlen($pdbchain) === 5) {
				$pdb_id = substr($pdbchain, 0, 4);
				$chain_name = substr($pdbchain, 4, 1);						
			
				if(check_valid_pdbid($pdb_id) && check_valid_chainid($chain_name)) {
					$valid_values = TRUE;				  
				}
			}					
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
	
	$graphtype_str = get_graphtype_string($graphtype_int);
	$query = get_pg_graphlets_query_string($pdb_id, $chain_name, $graphtype_int);
	
	
	//echo "query='" . $query . "'\n";
	
	$result = pg_query($db, $query);
    //if(! $result) { echo "NO_RESULT: " .  pg_last_error($db) . "."; }
	if(! $result) { array_push($SHOW_ERROR_LIST, "Database query failed: '" . pg_last_error($db) . "'"); }
	$tableString = "";
	$tableString .= "<div><table id='tblfgresults'>\n";
	$tableString .= "<caption> The graphlets of the PG of PDB $pdb_id chain $chain_name graph type $graphtype_str</caption>\n";
	$tableString .= "<tr>
    <th>PDB</th>
    <th>Chain</th>
    <th>GT</th>
	<th>Graphlets</th>
      </tr>\n";
		
	$num_found = 0;
	$img_string = "";
	$html_id = "";
	while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
	    $res_pdb_id = $arr['pdb_id'];
	    $res_chain_name = $arr['chain_name'];
	    $res_graph_type = $graphtype_str;
	    $res_graphlets_array = json_decode($arr['graphlets']);
	    $res_graphlets = implode("," $res_graphlets_array);  // format array for output

		
		$tableString .= "<tr>\n";
		$tableString .= "<td>$res_pd_id</td><td>$res_chain_name</td><td>$res_graph_type</td><td>$res_graphlets</td>\n";
		$tableString .= "</tr>\n";
				
		
		
		$num_found++;
				
	}		
	
	$tableString .= "</table></div>\n";
	
	if($num_found >= 1) {
	    $tableString .= "<br><br><a href='results.php?q=$pdbchain'>Go to protein graph</a><br><br>";	  	           		 		 
	}
	
	
	


} else {
     //echo "invalid";
	$tableString = "";
}

//EOF
?>