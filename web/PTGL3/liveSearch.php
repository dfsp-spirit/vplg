<?php

$db_config = include('config.php');

// establish pgsql connection
$conn_string = "host=" . $db_config['host'] . " port=" . $db_config['port'] . " dbname=" . $db_config['db'] . " user=" . $db_config['user'] ." password=" . $db_config['pw'];
$db = pg_connect($conn_string)
                or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            


// Define Output HTML Formating
$html = '';
$html .= '<p class="result">';
$html .= '<a href="urlString">';
$html .= '<span>IDString - </span>';
$html .= '<span>headerString</span>';
$html .= '</a>';
$html .= '</p>';

// Get Search
$search_string = preg_replace("/[^A-Za-z0-9]/", " ", $_POST['query']);

// Check Length More Than One Character
if (strlen($search_string) >= 1 && $search_string !== ' ') {
	// Build Query
	$query = "SELECT * FROM plcc_protein WHERE pdb_id LIKE '%".$search_string."%' OR header LIKE '%".strtoupper($search_string)."%'";
	$result = pg_query($db, $query) 
                  or die($query . ' -> Query failed: ' . pg_last_error());
	$data = pg_fetch_all($result);

	// Check If We Have Results
	if (isset($data)) {
		foreach ($data as $entry) {

			// Format Output Strings And Hightlight Matches
			$display_id = $entry['pdb_id'];
			$display_header = $entry['header'];
			$display_url = 'searchResults.php?keyword='. $entry['pdb_id'];

			$output = str_replace('IDString', $display_id, $html);
			$output = str_replace('headerString', $display_header, $output);
			$output = str_replace('urlString', $display_url, $output);

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