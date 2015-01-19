<?php
/** This file creates the tables for the protein display page dynamically.
 * 
 * It recives the parameter 'q' which contains a list of PDB-IDs (inclusive the chain name)
 * which are should be seperated by a single whitespace. It has no return value but
 * provides the variable $tableString which contains the HTML code for displaying the
 * proteingraphs in several content-sliders.
 *   
 * @author Daniel Bruness <dbruness@gmail.com>
 * @author Andreas Scheck <andreas.scheck.home@googlemail.com>
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


function get_motifs_found_in_chain($db, $pdb_id, $chain_name) {
  $data = array();
  $query = "SELECT p.pdb_id, c.chain_name, m.motif_name FROM plcc_nm_chaintomotif c2m INNER JOIN plcc_motif m ON c2m.motif_id = m.motif_id INNER JOIN plcc_chain c ON c2m.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id WHERE p.pdb_id = '" . $pdb_id . "' AND c.chain_name = '" . $chain_name . "'";
    
  $result = pg_query($db, $query);
  while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		array_push($data, $arr['motif_name']);		
	}
  
  pg_free_result($result);
  return $data;
  
}

function get_motif_abbreviation($motif_name) {
	$motif_abbr = array("Four Helix Bundle" => "4helix",
						"Globin Fold" => "globin",
						"Up and Down Barrel" => "barrel",
						"Immunoglobin Fold" => "immuno",
						"Beta Propeller" => "propeller",
						"Jelly Roll" =>	"jelly",
						"Ubiquitin Roll" => "ubi",
						"Alpha Beta Plait" => "plait",
						"Rossman Fold" => "rossman",
						"TIM Barrel" => "tim"
						);
	
	return $motif_abbr[$motif_name];
}

function get_graphtype_string_from_int($gt_int) {
   if($gt_int === 1){
     return "alpha";
   }
   else if($gt_int === 2){
     return "beta";
   }
   else if($gt_int === 3){
     return "albe";
   }
   else if($gt_int === 4){
     return "alphalig";
   }
   else if($gt_int === 5){
     return "betalig";
   }
   else if($gt_int === 6){
     return "albelig";
   }
   return "unknown_gt";
}

function get_protein_graph_file_name_no_ext($pdbid, $chain, $graphtype_string) {
  return $pdbid . "_" . $chain . "_" . $graphtype_string . "_PG";
}

function get_path_to($pdbid, $chain) {
  $mid2chars = substr($pdbid, 1, 2);
  return $mid2chars . "/" . $pdbid . "/". $chain . "/";
}

function get_protein_graph_path_and_file_name_no_ext($pdbid, $chain, $graphtype_string) {
  $path = get_path_to($pdbid, $chain);
  $fname = get_protein_graph_file_name_no_ext($pdbid, $chain, $graphtype_string);
  return $path . $fname;
}

// the graphtype which should be displayed first. Standard: alpha-graph
// and all other graphtypes
$graphtype			= "alpha"; # alpha-helix is #1
$graphtypes			= array("alpha", "beta", "albe", "alphalig", "betalig", "albelig");

// translate graphtype abbr. to understandable string
$graphtype_dict = array(
	"alpha"		=> "Alpha",
	"beta"		=> "Beta",
	"albe"		=> "Alpha-Beta",
	"alphalig"	=> "Alpha-Ligand",
	"betalig"	=> "Beta-Ligand",
	"albelig"	=> "Alpha-Beta-Ligand"
);

// translate SSE-type abbr. into letters
$sse_type_shortcuts = array(
    1 => "H",
    2 => "E",
    3 => "L",
    4 => "O"
);

// if _GET is set and contains the parameter 'q' then..
if(isset($_GET)) {
    if(isset($_GET["q"])) {
		// .. set the value to $q.
		$q = $_GET["q"];
	}
}

// seperate PDB-IDs at the whitespace and remove unneccessary whitespaces at start and end
// results into an array full of PDB-IDs.

$chains = explode(" ", trim($q));


// establish database connection
$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
$db = pg_connect($conn_string);


// init the variable which counts the number of loaded images. You may need this
// if you want to restrict the number of at-once-loaded images (partially implemented)
$loaded_images = 0;
// this variable contains the PDB-IDs which was handled at the moment. This is also
// used for loading content dynamically.
$allChainIDs = array();

$motif_data_all_chains = array();
$tableString = '<div id="myCarousel">
				      <ul class="bxslider bx-prev bx-next" id="carouselSlider">';

// for each PDB-ID+chainname...
foreach ($chains as $value){
	// check for correct format (maybe check also for correct letters/numbers..?)
	if (!(strlen($value) == 5)) {
		echo "<br />'" . $value . "' has a wrong PDB-ID format\n<br />";
	}
	// if everything is fine..
	else {
		// push current handled ID to the previous mentioned array
		array_push($allChainIDs, $value);
		// Split into PDB-ID and chainname
		$pdb_chain = str_split($value, 4);
		$pdbID = $pdb_chain[0];
		$chainName = $pdb_chain[1];
		
		$motif_list_this_chain = get_motifs_found_in_chain($db, $pdbID, $chainName);
		$motif_data_all_chains[$pdbID . $chainName] = $motif_list_this_chain;
		
		// remove previous prepared_statements from DB
		pg_query($db, "DEALLOCATE ALL");
		$query = "SELECT c.chain_id, g.graph_image_png, g.graph_image_pdf, g.graph_image_svg, g.filepath_graphfile_gml, g.graph_type 
				  FROM plcc_chain c, plcc_graph g 
				  WHERE c.pdb_id LIKE $1 
				  AND c.chain_name LIKE $2 
				  AND g.chain_id = c.chain_id 
				  ORDER BY graph_type"; 
		
		pg_prepare($db, "getChains", $query);		
		$result = pg_execute($db, "getChains", array("%".$pdbID."%", "%".$chainName."%"));  
		// first: get first dataset only (maybe too complicated implementation.. :(  )
		$data = pg_fetch_array($result, NULL, PGSQL_ASSOC);
		$chain_id = (int) $data['chain_id'];

		// query SSE informations/table with chain_id
		$query_SSE = "SELECT * FROM plcc_sse WHERE chain_id = ".$chain_id." ORDER BY position_in_chain";
		$result_SSE = pg_query($db, $query_SSE);
		
		$base_image_exists = FALSE;
		if(isset($data['graph_image_png']) && file_exists($IMG_ROOT_PATH.$data['graph_image_png'])) {
			$base_image_exists = TRUE;
		}
		
		
		if($base_image_exists) {
			$graphtype_int = 1;
			$graphtype_string = get_graphtype_string_from_int($graphtype_int);
		    //$tableString .= '<div id="myCarousel">
			//	      <ul class="bxslider bx-prev bx-next" id="carouselSlider">';
		    
		    // start to fill the html-tableString with content. This string will be echoed later
		    // to display the here created HTML construct.
		    

		    
		    // continue building the HTML string. Not further explained from now on.
		    $tableString .= "<li>\n";
		    $tableString .= '
						    <div class="container">
						    <h4>Protein graph for '.$pdbID.', chain '.$chainName.'</h4>
						    <div class="proteingraph">
						      <div>
							    <input type="checkbox" name="'.$pdbID.$chainName.'" value="'.$pdbID.$chainName.'"> Add to download list
							    <span class="download-options"><a href="3Dview.php?pdbid='.$pdbID.'&chain='.$chainName.'&mode=allgraphs" target="_blank">3D-View [JMOL]</a></span>
						      </div>	
						      <ul id="'.$pdbID.$chainName.'" class="bxslider tada">';

		    // if($loaded_images < 2){		// use this to limit preloaded images
		    $tableString .= '<li><a title="Graph image" href="'.$IMG_ROOT_PATH.$data['graph_image_png'].'" target="_blank">
						      <img src="'.$IMG_ROOT_PATH.$data['graph_image_png'].'" alt="" />
						    </a>
						    <a href="'.$IMG_ROOT_PATH.$data['graph_image_png'].'" target="_blank">Full Size Image</a>
						    <span class="download-options">Download graph image:';
		    

		    // check if downloadable files exist. If so, then add link to file (4x)
		    if(isset($data['graph_image_pdf']) && file_exists($IMG_ROOT_PATH.$data['graph_image_pdf'])) {
			    $tableString .= ' <a href="'.$IMG_ROOT_PATH.$data['graph_image_pdf'].'" target="_blank">[PDF]</a>';
		    }
		    if(isset($data['graph_image_svg']) && file_exists($IMG_ROOT_PATH.$data['graph_image_svg'])){
			    $tableString .= ' <a href="'.$IMG_ROOT_PATH.$data['graph_image_svg'].'" target="_blank">[SVG]</a>';
		    }
		    if(isset($data['graph_image_png']) && file_exists($IMG_ROOT_PATH.$data['graph_image_png'])){
			    $tableString .= ' <a href="'.$IMG_ROOT_PATH.$data['graph_image_png'].'" target="_blank">[PNG]</a>';
		    }
			$tableString .= '</span>';
		    if(isset($data['filepath_graphfile_gml']) && file_exists($IMG_ROOT_PATH.$data['filepath_graphfile_gml'])){
			    $tableString .= '<br><span class="download-options">Download graph file:
								  <a href="'.$IMG_ROOT_PATH.$data['filepath_graphfile_gml'].'" target="_blank">[GML]</a>';
				// check for other formats. The paths to these are not yet saved in the database.
				$graph_file_name_no_ext = get_protein_graph_path_and_file_name_no_ext($pdbID, $chainName, $graphtype_string);
				// tgf
				$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".tgf";
				if(file_exists($full_file)){
				    $tableString .= ' <a href="' . $full_file .'" target="_blank">[TGF]</a>';
				}
                // gv
				$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".gv";
				if(file_exists($full_file)){
				    $tableString .= ' <a href="' . $full_file .'" target="_blank">[GV]</a>';
				}
				// kavosh
				$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".kavosh";
				if(file_exists($full_file)){
				    $tableString .= ' <a href="' . $full_file .'" target="_blank">[Kavosh]</a>';
				}
                // json
				$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".json";
				if(file_exists($full_file)){
				    $tableString .= ' <a href="' . $full_file .'" target="_blank">[JSON]</a>';
				}
				// XML (XGMML)
				$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".xml";
				if(file_exists($full_file)){
				    $tableString .= ' <a href="' . $full_file .'" target="_blank">[XML (XGMML)]</a>';
				}				
                // edge list with separate label file				
				$full_file = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".el_edges";
				$full_file2 = $IMG_ROOT_PATH . $graph_file_name_no_ext . ".el_ntl";
				if(file_exists($full_file) && file_exists($full_file2)){
				    $tableString .= ' [EL: <a href="' . $full_file .'" target="_blank">edges</a> <a href="' . $full_file2 .'" target="_blank">labels</a>]';
				}		
				
				$tableString .= '</span>';
		    }
			$tableString .= '
							 <br><span class="download-options">
							 <a href="foldinggraphs.php?pdbchain='.$pdbID.$chainName.'&graphtype_int='.$graphtype_int.'&notationtype=adj" target="_blank">Go to folding graphs</a>
							 </span>';
							 
		   if(count($motif_list_this_chain) > 0) {
					    $tableString .= "<br><span class='download-options'>";
					    $tableString .= "Detected motifs in this chain: ";
					    for($i = 0; $i < count($motif_list_this_chain); $i++) {
					      $tableString .= "<a href='search.php?motif=" . get_motif_abbreviation($motif_list_this_chain[$i]) . "' target='_blank'>" . $motif_list_this_chain[$i] . "<a/> ";
					    }
					    $tableString .= "</span>";
					}
					
		    $tableString .= '</li>';

		    // }	// use this to limit preloaded images                // Tim says: what dis this supposed to do?!

		    // get the rest of the dataset. Until here we used only the first dataset from the DB.
			
		    while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
			    $tableString .= '<li>';
			    $tableString .= '<a href="'.$IMG_ROOT_PATH.$arr['graph_image_png'].'" target="_blank">
							      <img src="'.$IMG_ROOT_PATH.$arr['graph_image_png'].'" alt="" />
							    </a>
							<a href="'.$IMG_ROOT_PATH.$arr['graph_image_png'].'" target="_blank">Full Size Image</a>
							<span class="download-options">Download graph image: ';
				    
				    // check if downloadable files exist. If so, then add link to file (4x)
				    if(isset($data['graph_image_pdf']) && file_exists($IMG_ROOT_PATH.$data['graph_image_pdf'])) {
						$tableString .= ' <a href="'.$IMG_ROOT_PATH.$data['graph_image_pdf'].'" target="_blank">[PDF]</a>';
					}
					if(isset($data['graph_image_svg']) && file_exists($IMG_ROOT_PATH.$data['graph_image_svg'])){
						$tableString .= ' <a href="'.$IMG_ROOT_PATH.$data['graph_image_svg'].'" target="_blank">[SVG]</a>';
					}
					if(isset($data['graph_image_png']) && file_exists($IMG_ROOT_PATH.$data['graph_image_png'])){
						$tableString .= ' <a href="'.$IMG_ROOT_PATH.$data['graph_image_png'].'" target="_blank">[PNG]</a>';
					}
					$tableString .= '</span>'; // download images span ends
					
					
					if(isset($data['filepath_graphfile_gml']) && file_exists($IMG_ROOT_PATH.$data['filepath_graphfile_gml'])){
						$tableString .= '<br><span class="download-options">Download graph file:
										  <a href="'.$IMG_ROOT_PATH.$data['filepath_graphfile_gml'].'" target="_blank">[GML]</a></span>';
					}
					$tableString .= '
									 <br><span class="download-options">
									 <a href="foldinggraphs.php?pdbchain='.$pdbID.$chainName.'&graphtype_int='.++$graphtype_int.'&notationtype=adj" target="_blank">Go to folding graphs</a>
									 </span>';					
					
					if(count($motif_list_this_chain) > 0) {
					    $tableString .= "<br><span class='download-options'>";
					    $tableString .= "Detected motifs in this chain: ";
					    for($i = 0; $i < count($motif_list_this_chain); $i++) {
					      $tableString .= $motif_list_this_chain[$i] . " ";
					    }
					    $tableString .= "</span>";
					}
					
					
					$tableString .= '</li>';
		    }

		    $tableString .= '</ul>
						    </div>';
		    
						  
						
		} else {
		$tableString .= "<h2>No data found in the database for request protein, sorry.</h2>";
		}
		
				
				$tableString .= '
						<div class="table-responsive" id="sse">
						<table class="table table-condensed table-hover borderless whiteBack">
						<caption>The SSEs of the chain</caption>
						  <tr>
							<th class="tablecenter">SSE #</th>
							<th class="tablecenter">SSE type</th>
							<th class="tablecenter">AA sequence</th>
							<th class="tablecenter">residues in chain</th>
						  </tr>';
		
		// counter to numerate SSEs in the table
		$counter = 1;	
		// create SSE table (displayed to the right of protein graph)
		while ($arr = pg_fetch_array($result_SSE, NULL, PGSQL_ASSOC)){
			$pdb_start = str_replace("-", "", substr($arr["pdb_start"], 1));
			$pdb_end = str_replace("-", "", substr($arr["pdb_end"], 1));
			$tableString .= '<tr class="tablecenter">
									<td>'.$counter.'</td>
									<td>'.$sse_type_shortcuts[$arr["sse_type"]].'</td>
									<td>'.$arr["sequence"].'</td>
									<td>'.$pdb_start.' - '.$pdb_end.'</td>
								</tr>';
			$counter++;
		}
		
		$tableString .= '</table>
						</div><!-- end table-responsive -->
						
						<div id="'.$pdbID.$chainName.'_pager" class="bx-pager-own">';
					
		$tableString .= '<p>- Select topology type -</p>';
		
		// counter for the data-slide-index property of the bxSlider.
		$c = 0;					
		// create thumbails for the (inner) slider. One thumb for each graphtype
		$result_thumbs = pg_execute($db, "getChains", array("%".$pdbID."%", "%".$chainName."%"));
		while ($arr = pg_fetch_array($result_thumbs, NULL, PGSQL_ASSOC)){
			if(isset($arr['graph_image_png']) && file_exists($IMG_ROOT_PATH.$arr['graph_image_png'])) {
			    $tableString .= ' <a class="thumbalign" data-slide-index="'.$c++.'" href=""><img src="'.$IMG_ROOT_PATH.$arr['graph_image_png'].'" width="100px" height="100px" />
								'.$graphtype_dict[$graphtypes[$c-1]].'
							  </a>';
			}
		}
		$tableString .= '</div></li>';
		
		
	}
	$loaded_images++;
}
// last entry for the tableString
$tableString .= '</ul></div>';				
//EOF
?>