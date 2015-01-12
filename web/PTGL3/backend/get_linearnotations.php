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

// how many notations on one page?...
$q_limit = 50;
$valid_values = FALSE;


function get_graphtype_abbr($graphtype_int){
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

function get_query_string($graphtype_int, $notation, $q_limit, $limit_start){
$query = "SELECT DISTINCT fglin.ptgl_linnot_%s
FROM plcc_fglinnot fglin
INNER JOIN plcc_foldinggraph fg
ON fglin.linnot_foldinggraph_id = fg.foldinggraph_id
INNER JOIN plcc_graph g
ON fg.parent_graph_id = g.graph_id
WHERE g.graph_type = %s
ORDER BY fglin.ptgl_linnot_%s ASC
LIMIT %d OFFSET %d";
$query = sprintf($query, $notation, $graphtype_int, $notation, $q_limit, $limit_start);
return $query;	
} 


function get_query_string_denormalized($graphtype_int, $notation, $q_limit, $limit_start){
	$query =   "SELECT DISTINCT fglin.ptgl_linnot_%s
				FROM plcc_fglinnot fglin				
				WHERE fglin.graph_type = %s
				ORDER BY fglin.linnot_id ASC
				LIMIT %d OFFSET %d";
	
	$query = sprintf($query, $notation, $graphtype_int, $q_limit, $limit_start);
	return $query;			
}
function get_count_query_string($graphtype_int, $notation){
$query = "SELECT COUNT(DISTINCT fglin.ptgl_linnot_%s)
FROM plcc_fglinnot fglin
INNER JOIN plcc_foldinggraph fg
ON fglin.linnot_foldinggraph_id = fg.foldinggraph_id
INNER JOIN plcc_graph g
ON fg.parent_graph_id = g.graph_id
WHERE g.graph_type = %s";
$query = sprintf($query, $notation, $graphtype_int);
return $query;	
} 


function get_count_query_string_denormalized($graphtype_int, $notation){
	$query =   "SELECT COUNT(fglin.ptgl_linnot_%s)
				FROM plcc_fglinnot fglin				
				WHERE fglin.graph_type = %s";
	
	$query = sprintf($query, $notation, $graphtype_int);
	return $query;			
}

if(isset($_GET["next"])) {
	if(is_numeric($_GET["next"]) && $_GET["next"] >= 0){
		$limit_start = $_GET["next"];
		$notation = $_SESSION["notation"];
		$graphtype = $_SESSION["graphtype"];
		$valid_values = TRUE;
	} else {
		$limit_start = 0;
		session_unset();
	}
} else {
	$limit_start = 0;
	session_unset();
}


if(isset($_GET['graphtype']) && isset($_GET['notationtype'])){
	$valid_values = FALSE;
	$graphtype = $_GET["graphtype"];
	$notation = $_GET["notationtype"];
	if(($graphtype === "1" || $graphtype === "2" || $graphtype === "3" || 
	    $graphtype === "4" || $graphtype === "5" || $graphtype === "6") &&
	   ($notation === "adj" || $notation === "red" ||
		$notation === "key" || $notation === "seq")) { 

			$valid_values = TRUE;
			$_SESSION["graphtype"] = $graphtype;
			$_SESSION["notation"] = $notation;
	}
}


if($valid_values){
	// connect to DB
	$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	$db = pg_connect($conn_string);
		
	//$query = get_query_string($graphtype, $notation, $q_limit, $limit_start);
	//$count_query = get_count_query_string($graphtype, $notation);
	if($USE_DENORMALIZED_DB_FIELDS) {
	    $query = get_query_string_denormalized($graphtype, $notation, $q_limit, $limit_start);
	    $count_query = get_count_query_string_denormalized($graphtype, $notation);
	} else {
	    $query = get_query_string($graphtype, $notation, $q_limit, $limit_start);
	    $count_query = get_count_query_string($graphtype, $notation);
	}

	
	$count_result = pg_query($db, $count_query);
	$row_count = pg_fetch_array($count_result, NULL, PGSQL_ASSOC);
	$row_count = $row_count["count"];
	//$row_count = 100;
	$result = pg_query($db, $query);
	
	// begin to create pager
	$tableString = '<div id="pager">';
	if($limit_start >= $q_limit) {
		$tableString .= '<a class="changepage" href="?next='.($limit_start - $q_limit).'"><< previous </a>  ';
	}

	$tableString .= '-- Showing results '.$limit_start.' to ';
	
	if($limit_start + $q_limit > $row_count){
		$tableString .= $row_count . ' (of '.$row_count.') -- ';
	} else {
		$tableString .= ($limit_start + $q_limit) . ' (of '.$row_count.') -- ';
	}
	
	if(($limit_start + $q_limit) < $row_count){
		$tableString .= '<a class="changepage" href="?next='.($limit_start + $q_limit).'"> next >></a>';
	}
	$tableString .= '</div>';
	// EOPager
	
	$tableString .= "<div id='linnot_table'>";
	
	$parameter = "linnot".get_graphtype_abbr($graphtype).$notation;
	
	$all_data = pg_fetch_all($result);
	
	if(! $all_data) { echo "ERROR: '" . pg_last_error() . "'."; }
	
	foreach($all_data as $arr) {
		$linnot_string = $arr['ptgl_linnot_'.$notation];
		$tableString .= "<div class='string_row'>";
		$tableString .= "<a href='search.php?".$parameter."=".$linnot_string."' >". $linnot_string . "</a>";
		$tableString .= "</div>";
	}
	
	/*
	while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		$linnot_string = $arr['ptgl_linnot_'.$notation];

		$tableString .= "<div class='string_row'>";
		$tableString .= "<a href='search.php?".$parameter."=".$linnot_string."' >". $linnot_string . "</a>";
		$tableString .= "</div>";
				
	}
	*/
	
	$tableString .= "</div>";


}

//EOF
?>