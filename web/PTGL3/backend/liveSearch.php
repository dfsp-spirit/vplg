<?php

$db_config = include('config.php');

// establish pgsql connection
$conn_string = "host=" . $db_config['host'] . " port=" . $db_config['port'] . " dbname=" . $db_config['db'] . " user=" . $db_config['user'] ." password=" . $db_config['pw'];
$db = pg_connect($conn_string)
                or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            


// Define Output HTML Formating
$html = '';
$html .= '<div class="result" title="IDStringTool - headerStringTool">';
// $html .= '<a href="urlString" title="IDString - headerStringTool">';
$html .= '<span>IDString - </span>';
$html .= '<span>headerString</span>';
// $html .= '</a>';
$html .= '</div>';

// Get Search
$search_string = preg_replace("/[^A-Za-z0-9]/", " ", $_POST['query']);

// Check Length More Than One Character
if (strlen($search_string) >= 1 && $search_string !== ' ') {
	// Build Query
	$query = "SELECT * FROM plcc_protein WHERE pdb_id LIKE '%".strtolower($search_string)."%' OR header LIKE '%".strtoupper($search_string)."%'";
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
			
			$search_string = strtoupper($search_string);
			$display_header = preg_replace("/".$search_string."/i", "<b class='highlight'>".$search_string."</b>", $display_header);
			$display_url = 'searchResults.php?keyword='. $entry['pdb_id'];

			$output = str_replace('IDStringTool', $entry['pdb_id'], $html);
			$output = str_replace('headerStringTool', $entry['header'], $output);
			$output = str_replace('IDString', $display_id, $output);
			$output = str_replace('headerString', $display_header, $output);		
			// $output = str_replace('urlString', $display_url, $output);

			// Output
			echo($output);
		}
	}else{

		// Format No Results Output
		$output = str_replace('urlString', 'javascript:void(0);', $html);
		$output = str_replace('IDString', '<b>No Results Found.</b>', $output);
		$output = str_replace('headerString', 'Sorry :(', $output);

		// Output
		echo($output);
	}
}

?>