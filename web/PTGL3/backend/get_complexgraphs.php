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

function get_ligand_expo_link($ligand_name3) {
    if(strlen($ligand_name3) === 3 || strlen($ligand_name3) === 2 || strlen($ligand_name3) === 1) {
      return "http://ligand-expo.rcsb.org/pyapps/ldHandler.py?formid=cc-index-search&target=" . $ligand_name3 . "&operation=ccid";
    }
    return false;    
}

function get_complexgraph_query_string($pdb_id, $graphtype_str) {
   $query = "SELECT complexgraph_id, pdb_id, filepath_ssegraph_image_png, filepath_ssegraph_image_svg, filepath_ssegraph_image_pdf, filepath_chaingraph_image_png, filepath_chaingraph_image_svg, filepath_chaingraph_image_pdf FROM (SELECT cg.complexgraph_id, cg.filepath_ssegraph_image_png, cg.filepath_ssegraph_image_svg, cg.filepath_ssegraph_image_pdf, cg.filepath_chaingraph_image_png, cg.filepath_chaingraph_image_svg, cg.filepath_chaingraph_image_pdf, cg.pdb_id AS pdb_id FROM plcc_complexgraph cg WHERE ( cg.pdb_id = '" . $pdb_id . "' )) bar ORDER BY complexgraph_id";
   return $query;
}

function get_all_chains_of_pdb_query($pdb_id) {
  $query = "SELECT c.chain_name, c.organism_scientific, c.mol_name, c.mol_id_pdb FROM plcc_chain c WHERE ( c.pdb_id = '" . $pdb_id . "' )";
  return $query;
}

function get_all_ligands_of_pdb_query($pdb_id) {
  $query = "SELECT s.lig_name, s.pdb_start, s.pdb_end, s.dssp_start, s.dssp_end, c.chain_name, c.pdb_id FROM plcc_sse s INNER JOIN plcc_chain c ON s.chain_id = c.chain_id WHERE ( c.pdb_id = '" . $pdb_id . "' AND s.sse_type = 3 ) ORDER BY s.dssp_start";
  return $query;
}

function get_all_macromolecules_of_pdb_query($pdb_id) {
  $query = "SELECT m.macromolecule_id, m.pdb_id, m.mol_id_pdb, m.mol_name, m.mol_ec_number, m.mol_organism_scientific, m.mol_organism_common, m.mol_chains FROM plcc_macromolecule m WHERE ( m.pdb_id = '" . $pdb_id . "') ORDER BY m.mol_id_pdb";
  return $query;
}

function is_valid_EC_number($ec) {
  return true;
}

function get_link_BRENDA($ec_number, $link_label) {
  if(is_valid_EC_number($ec_number)) {
    return "<a href='http://www.brenda-enzymes.info/enzyme.php?ecno=$ec_number' target='_blank'>$link_label</a>";
  }
  else {
    return "";
  }
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
  return $pdbid . "_complex_chains_" . $graphtype_string . "_CG";
}

function get_path_to($pdbid, $chain) {
  $mid2chars = substr($pdbid, 1, 2);
  return $mid2chars . "/" . $pdbid . "/". $chain . "/";
}

function get_complex_sse_graph_path_and_file_name_no_ext($pdbid, $graphtype_string) {
  $path = get_path_to($pdbid, "ALL");
  $fname = get_complex_sse_graph_file_name_no_ext($pdbid, $graphtype_string);
  return $path . $fname;
}

function get_complex_chains_graph_path_and_file_name_no_ext($pdbid, $graphtype_string) {
  $path = get_path_to($pdbid, "ALL");
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

	$pdb_id = $LOWERCASE_PDB_IDS ? strtolower($pdb_id) : $pdb_id;
	
	if(($graphtype_int === "1" || $graphtype_int === "2" || $graphtype_int === "3" || 
	    $graphtype_int === "4" || $graphtype_int === "5" || $graphtype_int === "6") ) { 

	    if(check_valid_pdbid($pdb_id)) {
		    $valid_values = TRUE;				  
	    }
		
	}
}

$num_found = 0;
$num_lig_found = 0;
$mmtableString = "";

if($valid_values){
    //echo "valid";
	// connect to DB
	$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	$db = pg_connect($conn_string);
	if(! $db) { array_push($SHOW_ERROR_LIST, "Database connection failed."); }
	//if(! $db) { echo "NO_DB"; }
	
	$graphtype_str = get_graphtype_string($graphtype_int);
	
	// determine all chains
	$chains_query = get_all_chains_of_pdb_query($pdb_id);
	$chains_result = pg_query($db, $chains_query);
	$chains = array();
	$tableString = "<div><table id='tblfgresults'><tr><th>PDB ID</th><th>Chain</th><th>Molecule</th><th>Mol ID</th><th>Organism</th><th>Go to protein graph</th></tr>\n";
	while ($chains_arr = pg_fetch_array($chains_result, NULL, PGSQL_ASSOC)){
		// data from chains table:
	        $cg_chain_name = $chains_arr['chain_name'];
	        $mol_name = $chains_arr['mol_name'];
	        $mol_id_pdb = $chains_arr['mol_id_pdb'];
	        $organism = $chains_arr['organism_scientific'];
	        array_push($chains, $cg_chain_name);
	        $pdbchain = $pdb_id . $cg_chain_name;
	        $tableString .= "<tr>\n";
		$tableString .= "<td>$pdb_id</td><td>$cg_chain_name</td><td>$mol_name</td><td>$mol_id_pdb</td><td>$organism</td>";
		$tableString .= "<td><a href='./results.php?q=" . $pdbchain . "' alt='Show protein graph of this chain'>PG of " . $pdb_id . " chain " . $cg_chain_name . "</a></td>\n";
		$tableString .= "</tr>\n";
		
	}
	$tableString .= "</table></div>\n";
	$tableString .= "<br/><br/>\n";
	
	$macromol_query = get_all_macromolecules_of_pdb_query($pdb_id);
	$macromol_result = pg_query($db, $macromol_query);
	if($macromol_result) {
	  $macromolecules = array();
	  $mmtableString = "<div><table id='tblmmgresults' class='results'><tr><th>MOL_ID</th><th>Name</th><th>EC number</th><th>Organism</th><th>Chains</th></tr>\n";
	  while ($mm_arr = pg_fetch_array($macromol_result, NULL, PGSQL_ASSOC)){
		  // data from chains table:
		  $mol_id_pdb = $mm_arr['mol_id_pdb'];
		  $mol_name = $mm_arr['mol_name'];
		  $mol_ec_number = $mm_arr['mol_ec_number'];
		  if(strlen($mol_ec_number) === 0) { $mol_ec_link = '-'; } else { $mol_ec_link = get_link_BRENDA($mol_ec_number, $mol_ec_number); }
		  $mol_organism_scientific = $mm_arr['mol_organism_scientific'];
		  $mol_organism_common = $mm_arr['mol_organism_common'];
		  $mol_chains = $mm_arr['mol_chains'];
		  $mmtableString  .= "<tr>\n";
		  $mmtableString  .= "<td>$mol_id_pdb</td><td>$mol_name</td><td>$mol_ec_link</td><td>$mol_organism_scientific</td>";
		  $mmtableString  .= "<td>$mol_chains</td>\n";
		  $mmtableString .= "</tr>\n";
		  
	  }
	  $mmtableString .= "</table></div>\n";
	}
	
	// determine all ligands (single ligand molecules from SSEs (ligand residues, not ligand types. This means something like ICT-485, not ICT)
	$ligands_query = get_all_ligands_of_pdb_query($pdb_id);
	$ligands_result = pg_query($db, $ligands_query);
	$ligtableString = "<div><table id='tblligresults' class='results'><tr><th>Ligand type</th><th>Chain</th><th>1st PDB residue</th><th>1st DSSP residue</th><th>Go to ligand complex graph</th></tr>\n";
	while ($ligand_arr = pg_fetch_array($ligands_result, NULL, PGSQL_ASSOC)){
		// data from SSE table (not from ligands table):
	        $lig_name = $ligand_arr['lig_name'];
	        $lig_chain_name = $ligand_arr['chain_name'];
	        $lig_pdb_start = $ligand_arr['pdb_start'];
	        $lig_pdb_end = $ligand_arr['pdb_end'];
	        $lig_dssp_start = $ligand_arr['dssp_start'];
	        $lig_dssp_end = $ligand_arr['dssp_end'];
	        
	        $num_lig_found++;
	        
	        $lig_name = trim($lig_name);
                $ligexpo_link = get_ligand_expo_link($lig_name);
		if($ligexpo_link) {
		    $lig_name_link = "<a href='" . $ligexpo_link . "' target='_blank'>" . $lig_name . "</a>";
		}
		else {
		    $lig_name_link = $lig_name;
		}
	        
	        $ligtableString .= "<tr>\n";
		$ligtableString .= "<td>$lig_name_link</td><td>$lig_chain_name</td><td>$lig_pdb_start</td><td>$lig_dssp_start</td>";
		$ligtableString .= "<td><a href='./ligandcenteredgraphs.php?pdb=" . $pdb_id . "' alt='Show ligand complex graphs of PDB file'>LCGs of PDB " . $pdb_id . "</a></td>\n";
		$ligtableString .= "</tr>\n";
		
	}
	$ligtableString .= "</table></div>\n";
	
	$query = get_complexgraph_query_string($pdb_id, $graphtype_str);
	
	//echo "query='" . $query . "'\n";
	
	$result = pg_query($db, $query);
	if(! $result) { array_push($SHOW_ERROR_LIST, "Database query failed: '" . pg_last_error($db) . "'"); }
		
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


		// ------------------------------------- handle SSE-level complex graph images ---------------------------
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
				
		// prepare the image links
		$img_string .= "<br><br><br><h4> SSE level complex graph</h4>\n";
		if($sse_image_exists_png) {		    
		    
		    $img_string .= "<div id='sse_cg'><img src='" . $full_sse_img_path_png . "' width='800'></div><br><br>\n";
		} else {
		    $img_string .= "<b>Image not available:</b> <i>The SSE-level complex graph is not available.</i>";			
		}
		
		// add download links for other formats than PNG (they can directly d/l this from the browser image)
		if($sse_image_exists_svg || $sse_image_exists_pdf || $sse_image_exists_png) {
		  $img_string .= "Download the visualization in formats: ";
		  
		  if($sse_image_exists_png) {
		    $img_string .= ' <a href="' . $full_sse_img_path_png .'" target="_blank">[PNG]</a>';
		  }
		  
		  if($sse_image_exists_svg) {
		    $img_string .= ' <a href="' . $full_sse_img_path_svg .'" target="_blank">[SVG]</a>';
		  }
		  
		  if($sse_image_exists_pdf) {
		    $img_string .= ' <a href="' . $full_sse_img_path_pdf .'" target="_blank">[PDF]</a>';
		  }
		  $img_string .= "<br/>";
		  
		}
		
				// --------------------------------------- handle SSE level graph text files ---------------------------------
		
		// check for graph text files. note that these do NOT exist once per linear notation, but only once per folding graph, so we add them here.
	    // The paths to these are not yet saved in the database.
	    $graph_file_name_no_ext = get_complex_sse_graph_file_name_no_ext($pdb_id, $graphtype_str);
	    $subdir = get_path_to($pdb_id, "ALL");
	
		// GML
		$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".gml";
		if(file_exists($full_file)){		    
			$img_string .= "Download the SSE level complex graph file in formats: ";
			
			$img_string .= ' <a href="' . $full_file .'" target="_blank">[GML]</a>';
			
			// we only check for other formats if GML exists:
			
			// check for TGF
			$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".tgf";
			if(file_exists($full_file)){		    
			    $img_string .= ' <a href="' . $full_file .'" target="_blank">[TGF]</a>';
			}
			// gv
				$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".gv";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[GV]</a>';
				}
				// kavosh
				$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".kavosh";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[Kavosh]</a>';
				}
                // json
				$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".json";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[JSON]</a>';
				}
				// XML / XGMML
				$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".xml";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[XML (XGMML)]</a>';
				}				
                // edge list with separate label file				
				$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".el_edges";
				$full_file2 = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".el_ntl";
				if(file_exists($full_file) && file_exists($full_file2)){
				    $img_string .= ' [EL: <a href="' . $full_file .'" target="_blank">edges</a> <a href="' . $full_file2 .'" target="_blank">labels</a>]';
				}		
			
			$img_string .= '<br><br>';
		} 

		
		
		// ------------------------------------- handle chain-level complex graph images ---------------------------
		$chain_image_exists_png = FALSE;
		$chain_img_link = "";
		$full_chain_img_path_png = $IMG_ROOT_PATH . $chain_img_png;
		if(isset($chain_img_png) && $chain_img_png != "" && file_exists($full_chain_img_path_png)) {
			$chain_image_exists_png = TRUE;
		} else {
		    //echo "File '$full_img_path_png' does not exist.";
		}
		
		$chain_image_exists_pdf = FALSE;
		$full_chain_img_path_pdf = $IMG_ROOT_PATH . $chain_img_pdf;
		if(isset($chain_img_pdf) && $chain_img_pdf != "" && file_exists($full_chain_img_path_pdf)) {
			$chain_image_exists_pdf = TRUE;
		}
		
		$chain_image_exists_svg = FALSE;
		$full_chain_img_path_svg = $IMG_ROOT_PATH . $chain_img_svg;
		if(isset($chain_img_svg) && $chain_img_svg != "" && file_exists($full_chain_img_path_svg)) {
			$chain_image_exists_svg = TRUE;
		}
				
		// prepare the image links
		$img_string .= "<br><br><br><h4> Chain level complex graph</h4>\n";
		if($chain_image_exists_png) {		    
		    
		    $img_string .= "<div id='chain_cg'><img src='" . $full_chain_img_path_png . "' width='800'></div><br><br>\n";
		} else {
		    $img_string .= "<b>Image not available:</b> <i>The chain-level complex graph is not available.</i>";			
		}
		
		// add download links for other formats than PNG (they can directly d/l this from the browser image)
		if($chain_image_exists_svg || $chain_image_exists_pdf || $chain_image_exists_png) {
		  $img_string .= "Download the visualization in formats: ";
		  
		  if($chain_image_exists_png) {
		    $img_string .= ' <a href="' . $full_chain_img_path_png .'" target="_blank">[PNG]</a>';
		  }
		  
		  if($chain_image_exists_svg) {
		    $img_string .= ' <a href="' . $full_chain_img_path_svg .'" target="_blank">[SVG]</a>';
		  }
		  
		  if($chain_image_exists_pdf) {
		    $img_string .= ' <a href="' . $full_chain_img_path_pdf .'" target="_blank">[PDF]</a>';
		  }
		  $img_string .= "<br/>";
		  
		}
		
		
		
		// --------------------------------------- handle chain level graph text files ---------------------------------
		
		// check for graph text files. note that these do NOT exist once per linear notation, but only once per folding graph, so we add them here.
	    // The paths to these are not yet saved in the database.
	    $graph_file_name_no_ext = get_complex_chains_graph_file_name_no_ext($pdb_id, $graphtype_str);
	    $subdir = get_path_to($pdb_id, "ALL");
	
		// GML
		$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".gml";
		if(file_exists($full_file)){		    
			$img_string .= "Download the chain level complex graph file in formats: ";
			
			$img_string .= ' <a href="' . $full_file .'" target="_blank">[GML]</a>';
			
			// we only check for other formats if GML exists:
			
			// check for TGF
			$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".tgf";
			if(file_exists($full_file)){		    
			    $img_string .= ' <a href="' . $full_file .'" target="_blank">[TGF]</a>';
			}
			// gv
				$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".gv";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[GV]</a>';
				}
				// kavosh
				$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".kavosh";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[Kavosh]</a>';
				}
                // json
				$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".json";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[JSON]</a>';
				}
				// XML / XGMML
				$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".xml";
				if(file_exists($full_file)){
				    $img_string .= ' <a href="' . $full_file .'" target="_blank">[XML (XGMML)]</a>';
				}				
                // edge list with separate label file				
				$full_file = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".el_edges";
				$full_file2 = $IMG_ROOT_PATH . $subdir . $graph_file_name_no_ext . ".el_ntl";
				if(file_exists($full_file) && file_exists($full_file2)){
				    $img_string .= ' [EL: <a href="' . $full_file .'" target="_blank">edges</a> <a href="' . $full_file2 .'" target="_blank">labels</a>]';
				}		
			
			$img_string .= '<br><br>';
		} else {
			$img_string .= "DEBUG: Missing chain level file '$full_file'.";
		}
		
		
		$num_found++;
				
	}		
	
	
	
	
	


} else {
     //echo "invalid";
	$tableString = "";
}

//EOF
?>