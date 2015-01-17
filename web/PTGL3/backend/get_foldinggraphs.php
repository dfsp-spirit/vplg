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

$valid_values = FALSE;
// get config values
include('./backend/config.php'); 

if($DEBUG){
	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	ini_set('log_errors', TRUE);
	error_reporting(E_ALL);
}


function get_fglinnots_data_query_string($pdb_id, $chain_name, $graphtype_str) {
   $query = "SELECT linnot_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, filepath_linnot_image_adj_png, filepath_linnot_image_red_png, filepath_linnot_image_seq_png, filepath_linnot_image_key_png, filepath_linnot_image_adj_svg, filepath_linnot_image_red_svg, filepath_linnot_image_seq_svg, filepath_linnot_image_key_svg, filepath_linnot_image_adj_pdf, filepath_linnot_image_red_pdf, filepath_linnot_image_seq_pdf, filepath_linnot_image_key_pdf, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_seq, firstvertexpos_key, num_sses, sse_string FROM (SELECT la.num_sses, la.linnot_id, la.ptgl_linnot_adj, la.ptgl_linnot_red, la.ptgl_linnot_key, la.ptgl_linnot_seq, la.firstvertexpos_adj, la.firstvertexpos_red, la.firstvertexpos_seq, la.firstvertexpos_key, la.filepath_linnot_image_adj_png, la.filepath_linnot_image_red_png, la.filepath_linnot_image_seq_png, la.filepath_linnot_image_key_png, la.filepath_linnot_image_adj_svg, la.filepath_linnot_image_red_svg, la.filepath_linnot_image_seq_svg, la.filepath_linnot_image_key_svg, la.filepath_linnot_image_adj_pdf, la.filepath_linnot_image_red_pdf, la.filepath_linnot_image_seq_pdf, la.filepath_linnot_image_key_pdf, fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, c.pdb_id AS pdb_id FROM plcc_fglinnot la LEFT JOIN plcc_foldinggraph fg ON la.linnot_foldinggraph_id = fg.foldinggraph_id LEFT JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN plcc_chain c ON pg.chain_id=c.chain_id LEFT JOIN plcc_graphtypes gt ON pg.graph_type=gt.graphtype_id WHERE ( graphtype_text = '" . $graphtype_str . "' AND chain_name = '" . $chain_name . "' AND pdb_id = '" . $pdb_id . "' )) bar ORDER BY fg_number";
   return $query;
}

function get_fglinnots_data_query_string_denormalized($pdb_id, $chain_name, $graphtype_str) {
   $query = "SELECT linnot_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, filepath_linnot_image_adj_png, filepath_linnot_image_red_png, filepath_linnot_image_seq_png, filepath_linnot_image_key_png, filepath_linnot_image_adj_svg, filepath_linnot_image_red_svg, filepath_linnot_image_seq_svg, filepath_linnot_image_key_svg, filepath_linnot_image_adj_pdf, filepath_linnot_image_red_pdf, filepath_linnot_image_seq_pdf, filepath_linnot_image_key_pdf, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_seq, firstvertexpos_key, num_sses, sse_string FROM (SELECT la.num_sses, la.linnot_id, la.ptgl_linnot_adj, la.ptgl_linnot_red, la.ptgl_linnot_key, la.ptgl_linnot_seq, la.firstvertexpos_adj, la.firstvertexpos_red, la.firstvertexpos_seq, la.firstvertexpos_key, la.filepath_linnot_image_adj_png, la.filepath_linnot_image_red_png, la.filepath_linnot_image_seq_png, la.filepath_linnot_image_key_png, la.filepath_linnot_image_adj_svg, la.filepath_linnot_image_red_svg, la.filepath_linnot_image_seq_svg, la.filepath_linnot_image_key_svg, la.filepath_linnot_image_adj_pdf, la.filepath_linnot_image_red_pdf, la.filepath_linnot_image_seq_pdf, la.filepath_linnot_image_key_pdf, fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, la.denorm_chain_name AS des_chain_name, c.pdb_id AS pdb_id, la.denorm_graph_type AS des_graph_type, la.denorm_pdb_id as des_pdb_id FROM plcc_fglinnot la LEFT JOIN plcc_foldinggraph fg ON la.linnot_foldinggraph_id = fg.foldinggraph_id LEFT JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN plcc_chain c ON pg.chain_id=c.chain_id LEFT JOIN plcc_graphtypes gt ON pg.graph_type=gt.graphtype_id WHERE ( gt.graphtype_text = '" . $graphtype_str . "' AND c.chain_name = '" . $chain_name . "' AND c.pdb_id = '" . $pdb_id . "' )) bar ORDER BY fg_number";
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

if(isset($_GET['pdbchain']) && isset($_GET['graphtype_int']) && isset($_GET['notationtype'])){
        $pageload_was_search = TRUE;
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

$num_found = 0;

if($valid_values){
    //echo "valid";
	// connect to DB
	$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	$db = pg_connect($conn_string);
	if(! $db) { array_push($SHOW_ERROR_LIST, "Database connection failed."); }
	//if(! $db) { echo "NO_DB"; }
	
	$graphtype_str = get_graphtype_string($graphtype_int);
	if($USE_DENORMALIZED_DB_FIELDS) {
	    $query = get_fglinnots_data_query_string_denormalized($pdb_id, $chain_name, $graphtype_str);
	} else {
	    $query = get_fglinnots_data_query_string($pdb_id, $chain_name, $graphtype_str);
	}
	
	//echo "query='" . $query . "'\n";
	
	$result = pg_query($db, $query);
    //if(! $result) { echo "NO_RESULT: " .  pg_last_error($db) . "."; }
	if(! $result) { array_push($SHOW_ERROR_LIST, "Database query failed: '" . pg_last_error($db) . "'"); }
	
	$tableString .= "<div><table id='tblfgresults'>\n";
	$tableString .= "<caption> The $notation $graphtype_str folding graphs of PDB $pdb_id chain $chain_name </caption>\n";
	$tableString .= "<tr>
    <th>FG#</th>
    <th>Fold name</th>
    <th># SSEs</th>
	<th>SSE string (N to C)</th>
	<th>First vertex # in parent PG</th>
	<th>Notation $notation </th>
	<th>Image available</th>
	<th>Linnot overview page</th>
      </tr>\n";
		
	$num_found = 0;
	$img_string = "";
	$html_id = "";
	while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		// data from foldinggraph table:
	    $fg_number = $arr['fg_number'];	
		$fold_name = $arr['fold_name'];
		// data from linnot table:		
		$num_sses = $arr['num_sses'];
        $not_string = $arr['ptgl_linnot_' . $notation];		
		$firstvert = $arr['firstvertexpos_' . $notation];					
		$firstvert_show = (intval($firstvert) + 1); // the value in the DB is the index, starting with 0 instead of 1
		$img_png = $arr['filepath_linnot_image_' . $notation . '_png']; // the PNG format image
		$img_pdf = $arr['filepath_linnot_image_' . $notation . '_pdf'];
		$img_svg = $arr['filepath_linnot_image_' . $notation . '_svg'];
		$sse_string = $arr['sse_string'];

		$image_exists_png = FALSE;
		$img_link = "";
		$full_img_path_png = $IMG_ROOT_PATH.$img_png;
		if(isset($img_png) && $img_png != "" && file_exists($full_img_path_png)) {
			$image_exists_png = TRUE;
			$html_id = "fg_image_number_" . $fg_number;
			$img_link = "<a href='#" . $html_id . "'>Fold $fold_name</a>";
		} else {
		    $img_link = "-";
		    //echo "File '$full_img_path_png' does not exist.";
		}
		
		$image_exists_pdf = FALSE;
		$full_img_path_pdf = $IMG_ROOT_PATH.$img_pdf;
		if(isset($img_pdf) && $img_pdf != "" && file_exists($full_img_path_pdf)) {
			$image_exists_pdf = TRUE;
		}
		
		$image_exists_svg = FALSE;
		$full_img_path_svg = $IMG_ROOT_PATH.$img_svg;
		if(isset($img_svg) && $img_svg != "" && file_exists($full_img_path_svg)) {
			$image_exists_svg = TRUE;
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