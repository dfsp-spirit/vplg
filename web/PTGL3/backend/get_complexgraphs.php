<?php
/** This file retrieces complex graphs from the database, based on a query for a PDB ID.
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


function get_complexgraph_query_string($pdb_id, $graphtype_str) {
   $query = "SELECT complexgraph_id, pdb_id, filepath_ssegraph_image_png, filepath_ssegraph_image_svg, filepath_ssegraph_image_pdf, filepath_chaingraph_image_png, filepath_chaingraph_image_svg, filepath_chaingraph_image_pdf FROM (SELECT cg.filepath_ssegraph_image_png, cg.filepath_ssegraph_image_svg, cg.filepath_ssegraph_image_pdf, cg.filepath_chaingraph_image_png, cg.filepath_chaingraph_image_svg, cg.filepath_chaingraph_image_pdf, c.pdb_id AS pdb_id FROM plcc_complexgraph cg LEFT JOIN plcc_protein p ON cg.pdb_id = p.pdb_id WHERE ( pdb_id = '" . $pdb_id . "' )) bar ORDER BY complexgraph_id";
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


function get_complex_sse_graph_file_name_no_ext($pdbid, $graphtype_string) {
  return $pdbid . "_complex_sses_" . $graphtype_string . "_CG";
}

function get_complex_chains_graph_file_name_no_ext($pdbid, $graphtype_string) {
  return $pdbid . "_complex_chains_CG";
}

function get_path_to($pdbid, $chain) {
  $mid2chars = substr($pdbid, 1, 2);
  return $mid2chars . "/" . $pdbid . "/". $chain . "/";
}

function get_complex_sse_graph_path_and_file_name_no_ext($pdbid, $graphtype_string) {
  $path = get_path_to($pdbid, $chain);
  $fname = get_complex_sse_graph_file_name_no_ext($pdbid, $graphtype_string);
  return $path . $fname;
}

function get_complex_chains_graph_path_and_file_name_no_ext($pdbid, $graphtype_string) {
  $path = get_path_to($pdbid, $chain);
  $fname = get_complex_chains_graph_file_name_no_ext($pdbid, $graphtype_string);
  return $path . $fname;
}

$pageload_was_search = FALSE;
$valid_values = FALSE;

if(isset($_GET['pdb'])){

        if(isset($_GET['graphtype_int'])) {
          $graphtype_int = $_GET["graphtype_int"];
        }
        else {
          $graphtype_int = 6;
        }
        
        $pageload_was_search = TRUE;
	$valid_values = FALSE;
	$pdb_id = $_GET["pdb"];
	if(($graphtype_int === "1" || $graphtype_int === "2" || $graphtype_int === "3" || 
	    $graphtype_int === "4" || $graphtype_int === "5" || $graphtype_int === "6") ) { 

	    if(check_valid_pdbid($pdb_id)) {
		    $valid_values = TRUE;				  
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
	$query = get_complexgraph_query_string($pdb_id, $graphtype_str);
	
	//echo "query='" . $query . "'\n";
	
	$result = pg_query($db, $query);
    //if(! $result) { echo "NO_RESULT: " .  pg_last_error($db) . "."; }
	if(! $result) { array_push($SHOW_ERROR_LIST, "Database query failed: '" . pg_last_error($db) . "'"); }
	$tableString = "";
		
	$num_found = 0;
	$img_string = "";
	$html_id = "";
	while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		// data from complexgraph table:
	        $complexgraph_id = $arr['complexgraph_id'];	
	        $cg_pdb_id = $arr['pdb_id'];
		$sse_img_png = $arr['filepath_ssegraph_image_png']; // the PNG format image
		$sse_img_pdf = $arr['filepath_ssegraph_image_pdf'];
		$sse_img_svg = $arr['filepath_ssegraph_image_svg'];
		$chain_img_png = $arr['filepath_chaingraph_image_png']; // the PNG format image
		$chain_img_pdf = $arr['filepath_chaingraph_image_pdf'];
		$chain_img_svg = $arr['filepath_chaingraph_image_svg'];


		$sse_image_exists_png = FALSE;
		$sse_img_link = "";
		$full_sse_img_path_png = $IMG_ROOT_PATH . $sse_img_png;
		if(isset($sse_img_png) && $sse_img_png != "" && file_exists($full_sse_img_path_png)) {
			$sse_image_exists_png = TRUE;
		} else {
		    //echo "File '$full_img_path_png' does not exist.";
		}
		
		$sse_image_exists_pdf = FALSE;
		$full_sse_img_path_pdf = $IMG_ROOT_PATH . $sse_img_pdf;
		if(isset($sse_img_pdf) && $sse_img_pdf != "" && file_exists($full_sse_img_path_pdf)) {
			$sse_image_exists_pdf = TRUE;
		}
		
		$sse_image_exists_svg = FALSE;
		$full_sse_img_path_svg = $IMG_ROOT_PATH . $sse_img_svg;
		if(isset($sse_img_svg) && $sse_img_svg != "" && file_exists($full_sse_img_path_svg)) {
			$sse_image_exists_svg = TRUE;
		}
		
		$tableString .= "<tr>\n";
		$tableString .= "<td>$fg_number</td><td>$fold_name</td><td>$num_sses</td><td>$sse_string</td><td>$firstvert_show</td><td>$not_string</td><td>$img_link</td>";
		$tableString .= "<td><a href='./linnots_of_foldinggraph.php?pdbchain=" . $pdbchain . '&graphtype_int=' . $graphtype_int . '&fold_number=' . $fg_number . "' alt='Show all linear notations of this FG'>Go to linnots<a></td>\n";
		$tableString .= "</tr>\n";
		
		// prepare the image links
		$img_string .= "<br><br><br><h4> Fold number $fg_number (fold name: $fold_name)</h4>\n";
		if($image_exists_png) {		    
		    $img_string .= "The $notation $graphtype_str folding graph $fold_name (#$fg_number) of PDB $pdb_id chain $chain_name: ";
		    $img_string .= "<div id='" . $html_id . "'><img src='" . $full_img_path_png . "' width='800'></div><br><br>\n";
		} else {
		    $reason = "";
			if($num_sses <= 3) { 
			    $reason = " because it only has $num_sses SSE"; 
				if($num_sses != 1) {
				    $reason .= "s";
                }				
			}
			
		    $img_string .= "<b>Image not available:</b> <i>The $notation $graphtype_str folding graph $fold_name (#$fg_number) of PDB $pdb_id chain $chain_name is not available" . $reason . ".</i>";			
		}
		$img_string .= ($num_sses == 1 ? "<br>SSE number of fold in parent graph: $firstvert_show<br>" : "<br>Number of first SSE of fold in parent graph: $firstvert_show<br>");
		
		// add download links for other formats than PNG (they can directly d/l this from the browser image)
		if($image_exists_svg || $image_exists_pdf || $image_exists_png) {
		  $img_string .= "Download the visualization of fold $fold_name in formats: ";
		  
		  if($image_exists_png) {
		    $img_string .= ' <a href="' . $full_img_path_png .'" target="_blank">[PNG]</a>';
		  }
		  
		  if($image_exists_svg) {
		    $img_string .= ' <a href="' . $full_img_path_svg .'" target="_blank">[SVG]</a>';
		  }
		  
		  if($image_exists_pdf) {
		    $img_string .= ' <a href="' . $full_img_path_pdf .'" target="_blank">[PDF]</a>';
		  }
		  $img_string .= "<br/>";
		  
		}				
		
		// check for graph text files. note that these do NOT exist once per linear notation, but only once per folding graph, so we add them here.
	    // The paths to these are not yet saved in the database.
	    $graph_file_name_no_ext = get_folding_graph_path_and_file_name_no_ext($pdb_id, $chain_name, $graphtype_str, $fg_number);
	
		// GML
		$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".gml";
		if(file_exists($full_file)){		    
			$img_string .= "Download the graph file of fold $fold_name in formats: ";
			
			$img_string .= ' <a href="' . $full_file .'" target="_blank">[GML]</a>';
			
			// we only check for other formats if GML exists:
			
			// check for TGF
			$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".tgf";
			if(file_exists($full_file)){		    
			    $img_string .= ' <a href="' . $full_file .'" target="_blank">[TGF]</a>';
			}
			// gv
				$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".gv";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[GV]</a>';
				}
				// kavosh
				$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".kavosh";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[Kavosh]</a>';
				}
                // json
				$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".json";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[JSON]</a>';
				}
				// XML / XGMML
				$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".xml";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[XML (XGMML)]</a>';
				}				
                // edge list with separate label file				
				$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".el_edges";
				$full_file2 = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".el_ntl";
				if(file_exists($full_file) && file_exists($full_file2)){
				    $img_string .= ' [EL: <a href="' . $full_file .'" target="_blank">edges</a> <a href="' . $full_file2 .'" target="_blank">labels</a>]';
				}		
			
			$img_string .= '<br><br>';
		}
		
		
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