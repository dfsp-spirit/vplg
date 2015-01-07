<?php
/** This file provides ...####
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

function get_fglinnots_data_query_string($pdb_id, $chain_name, $graphtype_str) {
   $query = "SELECT linnot_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, filepath_linnot_image_adj_png, filepath_linnot_image_red_png, filepath_linnot_image_seq_png, filepath_linnot_image_key_png, filepath_linnot_image_adj_svg, filepath_linnot_image_red_svg, filepath_linnot_image_seq_svg, filepath_linnot_image_key_svg, filepath_linnot_image_adj_pdf, filepath_linnot_image_red_pdf, filepath_linnot_image_seq_pdf, filepath_linnot_image_key_pdf, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_seq, firstvertexpos_key, num_sses, sse_string FROM (SELECT la.num_sses, la.linnot_id, la.ptgl_linnot_adj, la.ptgl_linnot_red, la.ptgl_linnot_key, la.ptgl_linnot_seq, la.firstvertexpos_adj, la.firstvertexpos_red, la.firstvertexpos_seq, la.firstvertexpos_key, la.filepath_linnot_image_adj_png, la.filepath_linnot_image_red_png, la.filepath_linnot_image_seq_png, la.filepath_linnot_image_key_png, la.filepath_linnot_image_adj_svg, la.filepath_linnot_image_red_svg, la.filepath_linnot_image_seq_svg, la.filepath_linnot_image_key_svg, la.filepath_linnot_image_adj_pdf, la.filepath_linnot_image_red_pdf, la.filepath_linnot_image_seq_pdf, la.filepath_linnot_image_key_pdf, fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, c.pdb_id AS pdb_id FROM plcc_fglinnot la LEFT JOIN plcc_foldinggraph fg ON la.linnot_foldinggraph_id = fg.foldinggraph_id LEFT JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN plcc_chain c ON pg.chain_id=c.chain_id LEFT JOIN plcc_graphtypes gt ON pg.graph_type=gt.graphtype_id WHERE ( graphtype_text = '" . $graphtype_str . "' AND chain_name = '" . $chain_name . "' AND pdb_id = '" . $pdb_id . "' )) bar ORDER BY fg_number";
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
$current_fold_number = 0;

if(isset($_GET['pdbchain']) && isset($_GET['graphtype_int'])){
        $pageload_was_search = TRUE;
	$valid_values = FALSE;
	$pdbchain = $_GET["pdbchain"];
	$graphtype_int = $_GET["graphtype_int"];
	if($graphtype_int === "1" || $graphtype_int === "2" || $graphtype_int === "3" || 
	    $graphtype_int === "4" || $graphtype_int === "5" || $graphtype_int === "6")  { 

			if(strlen($pdbchain) === 5) {
				$pdb_id = substr($pdbchain, 0, 4);
				$chain_name = substr($pdbchain, 4, 1);						
			
				if(check_valid_pdbid($pdb_id) && check_valid_chainid($chain_name)) {
					$valid_values = TRUE;	
                    
					// determine whether a specific fold number was requested. if not, assume the first fold.
					if(isset($_GET['fold_number'])) {
					    $current_fold_number = intval($_GET['fold_number']);
					}
					else {
					    $current_fold_number = 0;
					}
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
	//if(! $db) { echo "NO_DB"; }
	
	$graphtype_str = get_graphtype_string($graphtype_int);
	$query = get_fglinnots_data_query_string($pdb_id, $chain_name, $graphtype_str);
	
	//echo "query='" . $query . "'\n";
	
	$result = pg_query($db, $query);
    //if(! $result) { echo "NO_RESULT: " .  pg_last_error($db) . "."; }
	
	$tableString .= "<div><table id='tblfgresults'>\n";
	$tableString .= "<caption> Overview of all folding graphs of the $graphtype_str protein graph of PDB $pdb_id chain $chain_name and available linnot images: </caption>\n";
	$tableString .= "<tr>
    <th>FG#</th>
    <th>Fold name</th>
    <th># SSEs</th>
	<th>SSE string (N to C)</th>
	<th>DEF image</th>
	<th>ADJ image</th>
	<th>RED image</th>
	<th>SEQ image</th>
	<th>KEY image</th>
	<th>Link</th>	
      </tr>\n";
		
	$num_found = 0;
	$img_string = "";
	$html_id = "";
	$all_rows = pg_fetch_all($result);
	
	//echo "TAGTAG Found " . count($all_rows) . " rows total.\n";
	
	foreach($all_rows as $arr) {
		// data from foldinggraph table:
	    $fg_number = intval($arr['fg_number']);	
		$fold_name = $arr['fold_name'];
		$num_sses = $arr['num_sses'];
		$sse_string = $arr['sse_string'];
		// data from linnot table:				
		$img_def = $arr['filepath_linnot_image_def_png']; // the PNG format image
        $img_adj = $arr['filepath_linnot_image_adj_png'];
		$img_red = $arr['filepath_linnot_image_red_png'];
		$img_seq = $arr['filepath_linnot_image_seq_png'];
		$img_key = $arr['filepath_linnot_image_key_png'];

		// DEF
		$image_exists_def = FALSE;
		$img_link_def = "no";
		$full_img_path_def = $IMG_ROOT_PATH . $img_def;
		if(isset($img_def) && $img_def != "" && file_exists($full_img_path_def)) {
			$image_exists_def = TRUE;
			$img_link_def = "yes";
			if($fg_number === $current_fold_number) {
			    $img_link_def = '<a href="#def">yes</a>';
			}
		}
		
		// ADJ
		$image_exists_adj = FALSE;
		$img_link_adj = "no";
		$full_img_path_adj = $IMG_ROOT_PATH . $img_adj;
		if(isset($img_adj) && $img_adj != "" && file_exists($full_img_path_adj)) {
			$image_exists_adj = TRUE;
			$img_link_adj = "yes";
			if($fg_number === $current_fold_number) {
			    $img_link_adj = '<a href="#adj">yes</a>';
			}
		}
		
		// RED
		$image_exists_red = FALSE;
		$img_link_red = "no";
		$full_img_path_red = $IMG_ROOT_PATH . $img_red;
		if(isset($img_red) && $img_red != "" && file_exists($full_img_path_red)) {
			$image_exists_red = TRUE;
			$img_link_red = "yes";
			if($fg_number === $current_fold_number) {
			    $img_link_red = '<a href="#red">yes</a>';
			}
		}
		
		// SEQ
		$image_exists_seq = FALSE;
		$img_link_seq = "no";
		$full_img_path_seq = $IMG_ROOT_PATH . $img_seq;
		if(isset($img_seq) && $img_seq != "" && file_exists($full_img_path_seq)) {
			$image_exists_seq = TRUE;
			$img_link_seq = "yes";
			if($fg_number === $current_fold_number) {
			    $img_link_seq = '<a href="#seq">yes</a>';
			}
		}
		
		// KEY
		$image_exists_key = FALSE;
		$img_link_key = "no";
		$full_img_path_key = $IMG_ROOT_PATH . $img_key;
		if(isset($img_key) && $img_key != "" && file_exists($full_img_path_key)) {
			$image_exists_key = TRUE;
			$img_link_key = "yes";
			if($fg_number === $current_fold_number) {
			    $img_link_key = '<a href="#key">yes</a>';
			}
		}
		
		
		$tableString .= "<tr>\n";
		$tableString .= "<td>$fg_number</td><td>$fold_name</td><td>$num_sses</td><td>$sse_string</td><td>$img_link_def</td><td>$img_link_adj</td><td>$img_link_red</td><td>$img_link_seq</td><td>$img_link_key</td>";
		if($fg_number === $current_fold_number) {
		    $tableString .= "<td><i>this page</i></td>\n";
		} else {
		    $tableString .= "<td><a href='./linnots_of_foldinggraph.php?pdbchain=" . $pdbchain . '&graphtype_int=' . $graphtype_int . '&fold_number=' . $fg_number . "' alt='Show other fold'>Fold $fg_number<a></td>\n";
		}
		
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