<?php
/** This file is used for the liveSearch on the frontpage and the navigation-bar.
 * 
 * It receives the query string which is provided by the users input. This comes with AJAX
 * After each hit key, this file is called and tries to find any results in the database.
 *    
 * @author Daniel Bruness <dbruness@gmail.com>
 * @author Andreas Scheck <andreas.scheck.home@googlemail.com>
 */

ini_set('display_errors', 0);
ini_set('display_startup_errors', 0);
ini_set('log_errors', TRUE);
error_reporting(E_ERROR);

// get config values
include('config.php'); 

if($DEBUG){
	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	ini_set('log_errors', TRUE);
	error_reporting(E_ALL);
}

// establish database connection
$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
$db = pg_connect($conn_string);

echo pg_last_error($db);

// Define Output HTML Formating which will be displayed in the grey box below the search field.
$html = '';
$html .= '<div class="result" title="IDStringTool - headerStringTool">';
$html .= '<span>IDString - </span>';
$html .= '<span>headerString</span>';
$html .= '</div>';

// set the search string and remove all none alphanumeric signs
$search_string = preg_replace("/[^A-Za-z0-9]/", " ", $_POST['query']);

// if searchstring-length is > 1 and not only 1 whitespace...
if (strlen($search_string) >= 1 && $search_string !== ' ') {
	// build query
	$query = "SELECT p.pdb_id, p.header 
			  FROM plcc_protein p 
			  WHERE p.pdb_id LIKE '%".strtolower($search_string)."%' OR header LIKE '%".strtoupper($search_string)."%'";
	$result = pg_query($db, $query);
	$data = pg_fetch_all($result);

	// check if we have results
	if (isset($data) && $data) {
		foreach ($data as $entry) {
			// format output strings and hightlight matches
			$search_string = strtolower($search_string);
			$display_id = preg_replace("/".$search_string."/i", "<b class='highlight'>".$search_string."</b>", $entry['pdb_id']);
			
			// if PDB header is too long, shorten it yeah!
			if( strlen($entry['header']) >= 25){
				$display_header = substr($entry['header'], 0, 25). '...'; // Cut header to 25 signs, add dots
			} else {
				$display_header = $entry['header'];
			}
			
			// replace the placeholders in the predefined output string with the
			// correct values.
			$search_string = strtoupper($search_string);
			$display_header = preg_replace("/".$search_string."/i", "<b class='highlight'>".$search_string."</b>", $display_header);
			$display_url = 'searchResults.php?keyword='. $entry['pdb_id'];

			$output = str_replace('IDStringTool', $entry['pdb_id'], $html);
			$output = str_replace('headerStringTool', $entry['header'], $output);
			$output = str_replace('IDString', $display_id, $output);
			$output = str_replace('headerString', $display_header, $output);		

			// Output/return the HTML string with highlights
			echo $output;
		}
	}else{

		// format output if there are no results
		$output = str_replace('urlString', 'javascript:void(0);', $html);
		$output = str_replace('IDString', '<b>No Results Found.</b>', $output);
		$output = str_replace('headerString', 'Sorry :(', $output);

		// Output/return it
		echo $output;
	}
}
//EOF
?>