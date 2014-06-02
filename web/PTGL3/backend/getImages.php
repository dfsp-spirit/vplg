<?php

$db_config = include('config.php');

// establish pgsql connection
$conn_string = "host=" . $db_config['host'] . " port=" . $db_config['port'] . " dbname=" . $db_config['db'] . " user=" . $db_config['user'] ." password=" . $db_config['pw'];
$db = pg_connect($conn_string)
                or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            


$currentSlide = $_POST['currentSlide'];
$chainIDs = $_POST['chainIDs'];
$loadSlideNumner = $currentSlide + 1;
$pdb_chain = str_split($chainIDs[$loadSlideNumner], 4);

$pdb_id = $pdb_chain[0];
$chain = $pdb_chain[1];

/*
if (strlen($search_string) >= 1 && $search_string !== ' ') {
	// Build Query
	$query = "SELECT 'imagepath' FROM plcc_protein WHERE pdb_id LIKE '%".strtolower($search_string)."%' OR header LIKE '%".strtoupper($search_string)."%'";
	$result = pg_query($db, $query) 
                  or die($query . ' -> Query failed: ' . pg_last_error());
	$data = pg_fetch_all($result);

	// Check If We Have Results
	if (isset($data)) {
		foreach ($data as $entry) {

			// Format Output Strings And Hightlight Matches
			$search_string = strtolower($search_string);
			$display_id = preg_replace("/".$search_string."/i", "<b class='highlight'>".$search_string."</b>", $entry['pdb_id']);
			//$display_id = $entry['pdb_id'];
			
			if( strlen($entry['header']) >= 25){
				$display_header = substr($entry['header'], 0, 25). '...'; // Cut header to 25 signs, add dots
			} else {
				$display_header = $entry['header'];
			}
			


	}
}

*/

$output = '<a href="./proteins/'.$pdb_id.'/'.$pdb_id.'_'.$chain.'_alpha_PG.png" target="_blank">
			<img src="./proteins/'.$pdb_id.'/'.$pdb_id.'_'.$chain.'_alpha_PG.png" alt="" />
			 </a>
			 <a href="./proteins/'.$pdb_id.'/'.$pdb_id.'_'.$chain.'_alpha_PG.png" target="_blank">Full Size Image</a>
			 <span class="download-options">Download Graph: [GML] [PS] [else]</span>';

echo $output;

?>