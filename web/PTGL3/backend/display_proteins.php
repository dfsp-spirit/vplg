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
 * 
 * Changed by Jan Niclas Wolf (jnw) <Wolf@bioinformatik.uni-frankfurt.de>
 *  2019:   - fix display of chains with IDs of > 1 char (max 4 currently)
 *          - clearer error messages
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


function get_ligand_expo_link($ligand_name3) {
    if(strlen($ligand_name3) === 3 || strlen($ligand_name3) === 2 || strlen($ligand_name3) === 1) {
      return "http://ligand-expo.rcsb.org/pyapps/ldHandler.py?formid=cc-index-search&target=" . $ligand_name3 . "&operation=ccid";
    }
    return false;    
}


// sse_type must be "H", "E", "L" or "O"
function get_sse_tooltip_div_for($sse_type) {
    if($sse_type === "H") {
        return '<div title="Helix">' . $sse_type . '</div>';
    }
    else if($sse_type === "E") {
        return '<div title="Beta strand">' . $sse_type . '</div>';
    }
    else if($sse_type === "L") {
        return '<div title="Ligand">' . $sse_type . '</div>';
    }
    else {
        return '<div title="Other SSE">' . $sse_type . '</div>';
    }
}

function get_motifs_found_in_chain($db, $pdb_id, $chain_name) {
    

    
  $arr = array();
  $query = "SELECT p.pdb_id, c.chain_name, m.motif_name FROM plcc_nm_chaintomotif c2m INNER JOIN plcc_motif m ON c2m.motif_id = m.motif_id INNER JOIN plcc_chain c ON c2m.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id WHERE p.pdb_id = '" . $pdb_id . "' AND c.chain_name = '" . $chain_name . "'";
    
  $result = pg_query($db, $query);
  while ($row = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		array_push($arr, $row['motif_name']);		
	}
  
  pg_free_result($result);
  return $arr;
  
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
   return "INVALIDGRAPHTYPE";
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

$aa_names_1_to_full = array(
    "A" => "Alanine",
    "R" => "Arginine",
    "N" => "Asparagine",
    "D" => "Aspartic_acid",
    "C" => "Cysteine",
    "E" => "Glutamic_acid",
    "Q" => "Glutamine",
    "G" => "Glycine",
    "H" => "Histidine",
    "I" => "Isoleucine",
    "L" => "Leucine",
    "K" => "Lysine",
    "M" => "Methionine",
    "F" => "Phenylalanine",
    "P" => "Proline",
    "S" => "Serine",
    "T" => "Threonine",
    "W" => "Tryptophan",
    "Y" => "Tyrosine",
    "V" => "Valine",
    // the following are not AAs, just to prevent php warnings
    "X" => "X",
    "B" => "B",
    "J" => "J",
    "N" => "N",
    "O" => "O",
    "U" => "U",
    "Z" => "Z"
);


function get_AA_tooltip_div_for_single_AA_name_one_letter($aa) {
global $aa_names_1_to_full;
    if($aa_names_1_to_full[$aa]) {
        return '<span title=' . $aa_names_1_to_full[$aa] . '>' . $aa . '</span>';
    }
    else {
        return "$aa";
    }   
}


// adds a tooltip with the full AA name to each character (AA 1-letter name) in the string
function get_full_tooltip_AA_html_from_seq($aa_seq) {
    $str_arr = str_split($aa_seq);
    $res = "";
    foreach($str_arr as $aa) {
        $res .= get_AA_tooltip_div_for_single_AA_name_one_letter($aa);
    }
    return $res;
}


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
// this variable contains the PDB-IDs which was handled at the moment. This is also
// used for loading content dynamically.
$allChainIDs = array();

$motif_data_all_chains = array();
$tableString = '<div id="myCarousel"> 
                    <!-- start outer slider, the one that changes between chains -->
                    <ul class="bxslider bx-prev bx-next" id="carouselSlider">';

// for each PDB-ID+chainname...
$recent_chain_id = "";
foreach ($chains as $value){
    $new_chain = true;
    // check for correct format (maybe check also for correct letters/numbers..?)
    //  jnw_2019: b/c of large structures in mmCIF format up to 4-char chain IDs are allowed
    if (!(strlen($value) <= 8)) {
            //echo "<br />'" . $value . "' has a wrong PDB-ID format\n<br />";
            array_push($SHOW_ERROR_LIST, "PDB chain '" . $value . "' has a wrong PDB-ID and chain format, expected something like '7timA'. Up to 4 character chains are allowed.");
            continue;
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
        $result = pg_execute($db, "getChains", array("%".$pdbID."%", $chainName));  
      
        $tableString .= '
                        <li> <!-- start element of outer slider, a chain -->
                        <div class="container">
                        <h4>Protein graph for '.$pdbID.', chain '.$chainName.'</h4>
                        <div class="proteingraph">
                          <div>
                            <input type="checkbox" name="'.$pdbID.$chainName.'" value="'.$pdbID.$chainName.'"> Add to download list
                            <span class="download-options"><a href="3Dview.php?pdbid='.$pdbID.'&chain='.$chainName.'&mode=allgraphs" target="_blank">3D-View [JMOL]</a></span>
                          </div>	
                          <ul id="'.$pdbID.$chainName.'" class="bxslider tada"> <!-- start inner slider, the ones that changes between graph types -->' . "\n";
        
        $content_available = FALSE;
        while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
            $content_available = TRUE;
            // get unique DB chain ID
            $chain_id = (int) $arr['chain_id'];
            $graphtype_int = intval($arr["graph_type"]);
            $query_SSE = "SELECT * FROM plcc_sse WHERE chain_id = ".$chain_id." ORDER BY position_in_chain";
            $result_SSE = pg_query($db, $query_SSE);
            
            $base_image_exists = FALSE;
            if(isset($arr['graph_image_png']) && file_exists($IMG_ROOT_PATH.$arr['graph_image_png'])) {
                $base_image_exists = TRUE;
            }
            $graphtype_string = get_graphtype_string_from_int($graphtype_int);
                       
            if($base_image_exists) {
                             
                // start to fill the html-tableString with content. This string will be echoed later
                // to display the here created HTML construct.

                // continue building the HTML string. Not further explained from now on.

                $tableString .= '<li> <!-- start element of inner slider, a graph type -->' . "\n" .'
                                    <a title="Graph image" href="'.$IMG_ROOT_PATH.$arr['graph_image_png'].'" target="_blank">
                                        <img src="'.$IMG_ROOT_PATH.$arr['graph_image_png'].'" alt="" />
                                    </a>
                                    <a href="'.$IMG_ROOT_PATH.$arr['graph_image_png'].'" target="_blank">Full Size Image</a>
                                    <span class="download-options">Download graph image:';

                // check if downloadable files exist. If so, then add link to file (4x)
                if(isset($arr['graph_image_pdf']) && file_exists($IMG_ROOT_PATH.$arr['graph_image_pdf'])) {
                    $tableString .= ' <a href="'.$IMG_ROOT_PATH.$arr['graph_image_pdf'].'" target="_blank">[PDF]</a>';
                }
                if(isset($arr['graph_image_svg']) && file_exists($IMG_ROOT_PATH.$arr['graph_image_svg'])){
                    $tableString .= ' <a href="'.$IMG_ROOT_PATH.$arr['graph_image_svg'].'" target="_blank">[SVG]</a>';
                }
                if(isset($arr['graph_image_png']) && file_exists($IMG_ROOT_PATH.$arr['graph_image_png'])){
                    $tableString .= ' <a href="'.$IMG_ROOT_PATH.$arr['graph_image_png'].'" target="_blank">[PNG]</a>';
                }
                    $tableString .= '</span>';
                if(isset($arr['filepath_graphfile_gml']) && file_exists($IMG_ROOT_PATH.$arr['filepath_graphfile_gml'])){
                    $tableString .= '<br><span class="download-options">Download graph file:
                                     <a href="'.$IMG_ROOT_PATH.$arr['filepath_graphfile_gml'].'" target="_blank">[GML]</a>';
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
                                <br>
                                <a href="foldinggraphs.php?pdbchain='.$pdbID.$chainName.'&graphtype_int='.$graphtype_int.'&notationtype=adj" target="_blank">Go to folding graphs</a>
                                ';

                if(count($motif_list_this_chain) > 0) {
                    $tableString .= "<br>";
                    $tableString .= "Detected motifs in this chain: ";
                    for($i = 0; $i < count($motif_list_this_chain); $i++) {
                      $tableString .= "<a href='search.php?st=motif&motif=" . get_motif_abbreviation($motif_list_this_chain[$i]) . "' target='_blank'>" . $motif_list_this_chain[$i] . "</a>";
                      if($i < count($motif_list_this_chain) - 1) {
                        $tableString .= ", ";
                      }
                    }
                    $tableString .= "<br>";
                }

                $tableString .= '</li> <!-- closing inner slider element -->' . "\n";

            } else {
                $tableString .= "<li><h5>No data found in the database for requested protein $pdbID chain $chainName, sorry. Maybe it only contains RNA/DNA?</h5>";
            }
   
        } // end fetch chain data (images etc..)
        
        if($content_available){
            $tableString .= '</ul> <!-- closing inner slider list -->' . "\n";
            $tableString .= '</div> <!-- closing inner slider div -->' . "\n"; 

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

            while ($arrSSE = pg_fetch_array($result_SSE, NULL, PGSQL_ASSOC)){

                $pdb_start = str_replace("-", "", substr($arrSSE["pdb_start"], 1));
                $pdb_end = str_replace("-", "", substr($arrSSE["pdb_end"], 1));
                
                // show the ligand name instead of the AA string for ligands
                $AA_seq_or_ligand_name3 = $arrSSE["sequence"];
                if($AA_seq_or_ligand_name3 === "J") { // "J" means it is a ligand
                    $lig_name = trim($arrSSE["lig_name"]);
                    $ligexpo_link = get_ligand_expo_link($lig_name);
                    if($ligexpo_link) {
                        $AA_seq_or_ligand_name3 = "ligand: <a href='" . $ligexpo_link . "' target='_blank'>" . $lig_name . "</a>";
                    }
                    else {
                        $AA_seq_or_ligand_name3 = "ligand: " . $lig_name;
                    }
                }
                else {
                     $AA_seq_or_ligand_name3 = get_full_tooltip_AA_html_from_seq($AA_seq_or_ligand_name3);
                }
                
                // show a tooltip of the SSE type in the table
                $sse_type_text = get_sse_tooltip_div_for($sse_type_shortcuts[intval($arrSSE["sse_type"])]);
                
                $tableString .= '<tr class="tablecenter">
                                    <td>' . $counter . '</td>
                                    <td>' . $sse_type_text . '</td>
                                    <td>' . $AA_seq_or_ligand_name3 . '</td>
                                    <td>' . $pdb_start . ' - ' . $pdb_end . '</td>
                                 </tr>';
                $counter++;
            }

            $tableString .= '</table>
                             </div><!-- end table-responsive div -->' . "\n";


            $tableString .= '<div id="'.$pdbID.$chainName.'_pager" class="bx-pager-own">';
            $tableString .= '<p>- Select topology type -</p>';

            // counter for the data-slide-index property of the bxSlider.
            $c = 0;					
            // create thumbails for the (inner) slider. One thumb for each graphtype
            $result_thumbs = pg_execute($db, "getChains", array("%".$pdbID."%", $chainName));
            while ($arr = pg_fetch_array($result_thumbs, NULL, PGSQL_ASSOC)){
                if(isset($arr['graph_image_png']) && file_exists($IMG_ROOT_PATH.$arr['graph_image_png'])) {
                    $tableString .= ' <a class="thumbalign" data-slide-index="'.$c++.'" href=""><img src="'.$IMG_ROOT_PATH.$arr['graph_image_png'].'" width="100px" height="100px" />
                                    '.$graphtype_dict[$graphtypes[$c-1]].'
                                    </a>';
                }
            }
        } else {
            $tableString .= "<div><h5>No data found in the database for requested protein $pdbID chain $chainName, sorry. Maybe it only contains RNA/DNA?</h5></div>";
        }            
            $tableString .= '</div> <!-- end pager div, the one to select graph type -->' . "\n";
            $tableString .= '</div> <!-- end proteingraph div -->' . "\n";
            //$tableString .= '</div> <!-- end container div -->' . "\n";
            $tableString .= '</li> <!-- end of outer slider element -->' . "\n";

    } //end if pdb id is correct..
} // for each chain


// last entry for the tableString
$tableString .= '</ul></div>';				
?>
