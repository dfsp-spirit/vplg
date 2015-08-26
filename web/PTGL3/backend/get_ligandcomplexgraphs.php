<?php
/** This file retrieves ligand-centered complex graphs from the database, based on a query for a PDB ID.
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


function get_ligandcomplexgraph_query_string($pdb_id) {
   $query = "SELECT lcg.ligandcenteredgraph_id, lcg.pdb_id, lcg.lig_sse_id, lcg.filepath_lcg_png, lcg.filepath_lcg_svg, lcg.filepath_lcg_pdf FROM plcc_ligandcenteredgraph lcg WHERE ( lcg.pdb_id = '" . $pdb_id . "' ) ORDER BY lcg.lig_sse_id";
   return $query;
}

function get_all_chains_of_pdb_query($pdb_id) {
  $query = "SELECT c.chain_id, c.chain_name, c.organism_scientific, c.mol_name FROM plcc_chain c WHERE ( c.pdb_id = '" . $pdb_id . "' )";
  return $query;
}

function get_all_ligands_of_pdb_query($pdb_id) {
  $query = "SELECT s.sse_id, s.lig_name, s.pdb_start, s.pdb_end, s.dssp_start, s.dssp_end, c.chain_name, c.pdb_id FROM plcc_sse s INNER JOIN plcc_chain c ON s.chain_id = c.chain_id WHERE ( c.pdb_id = '" . $pdb_id . "' AND s.sse_type = 3 ) ORDER BY s.dssp_start";
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
        
        $pageload_was_search = TRUE;
	$valid_values = FALSE;
	$pdb_id = $_GET["pdb"];
	if(check_valid_pdbid($pdb_id)) {
		$valid_values = TRUE;				  
	}
}

$num_found = 0;
$num_lig_found = 0;

if($valid_values){
    //echo "valid";
	// connect to DB
	$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	$db = pg_connect($conn_string);
	if(! $db) { array_push($SHOW_ERROR_LIST, "Database connection failed."); }
	//if(! $db) { echo "NO_DB"; }
		
	// determine all chains
	$chains_query = get_all_chains_of_pdb_query($pdb_id);
	$chains_result = pg_query($db, $chains_query);
	$chains = array();
	$tableString = "<div><table id='tblfgresults'><tr><th>PDB ID</th><th>Chain</th><th>Molecule</th><th>Organism</th><th>Go to protein graph</th></tr>\n";
	while ($chains_arr = pg_fetch_array($chains_result, NULL, PGSQL_ASSOC)){
		// data from chains table:
	        $cg_chain_name = $chains_arr['chain_name'];
	        $mol_name = $chains_arr['mol_name'];
	        $organism = $chains_arr['organism_scientific'];
	        $db_chain_id = $chains_arr['chain_id'];
	        array_push($chains, $cg_chain_name);
	        $pdbchain = $pdb_id . $cg_chain_name;
	        $tableString .= "<tr>\n";
		$tableString .= "<td>$pdb_id</td><td>$cg_chain_name</td><td>$mol_name</td><td>$organism</td>";
		$tableString .= "<td><a href='./results.php?q=" . $pdbchain . "' alt='Show protein graph of this chain'>PG of " . $pdb_id . " chain " . $cg_chain_name . "</a></td>\n";
		$tableString .= "</tr>\n";
		
	}
	$tableString .= "</table></div>\n";
	
	
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
	        $lig_sse_db_id = $ligand_arr['sse_id'];
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
		$ligtableString .= "<td><a href='./ligcomplexgraphs.php?pdb=" . $pdb_id . "' alt='Show ligand complex graphs of this ligand'>LCG of ligand " . $lig_name ." (PDB residue " . $lig_pdb_start . ")</a></td>\n";
		$ligtableString .= "</tr>\n";
		
	}
	$ligtableString .= "</table></div>\n";
	
	$query = get_ligandcomplexgraph_query_string($pdb_id);
	
	//echo "query='" . $query . "'\n";
	
	$result = pg_query($db, $query);
	if(! $result) { array_push($SHOW_ERROR_LIST, "Database query failed: '" . pg_last_error($db) . "'"); }
		
	$num_found = 0;
	$img_string = "";
	$html_id = "";
	while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		// data from complexgraph table:
	        $lcg_id = $arr['ligandcenteredgraph_id'];	
	        $lcg_pdb_id = $arr['pdb_id'];
	        $lcg_lig_sse_id = $arr['lig_sse_id'];
		$sse_img_png = $arr['filepath_lcg_png']; // the PNG format image
		$sse_img_pdf = $arr['filepath_lcg_pdf'];
		$sse_img_svg = $arr['filepath_lcg_svg'];


		// ------------------------------------- handle ligand-centered complex graph images ---------------------------
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
		$img_string .= "<br><br><br><h4> Ligand-centered graphs</h4>\n";
		if($sse_image_exists_png) {		    
		    
		    $img_string .= "<div id='sse_cg'><p>Graph for ligand with DSSP identifier " . $lcg_lig_sse_id . ":</p><img src='" . $full_sse_img_path_png . "' width='800'></div><br><br>\n";
		} else {
		    $img_string .= "<b>Image not available:</b> <i>The ligand-centered complex graph for this ligand is not available.</i>";
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
		
		$num_found++;
				
	}		
	
	
	
	
	


} else {
     //echo "invalid";
	$tableString = "";
}

//EOF
?>