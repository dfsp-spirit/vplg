<?php
/** This file is neccessary if the images should be loaded dynamically.
 * 
 * Unfortunatly this version does NOT work correctly and is pretty useless at this
 * point. In fact it should work like the display_proteins.php script. 
 * The idea was, that this file is called by an AJAX request, get the neccessary images
 * and put the new createt HTML string into the existing HTML structure. Well, its twisted...
 * 
 * It receives an array of PDB-IDs which where selected by the user. Also, it gets an value
 * of the currently selected protein-slide (int). So you should be able to load
 * the correct and now needed images. Somehow, that was the idea. 
 * 
 * The main problem is, that loading new images corrupts the slider... :(
 *   
 * @author Daniel Bruness <dbruness@gmail.com>
 * @author Andreas Scheck <andreas.scheck.home@googlemail.com>
 */

// ini_set('display_errors',1); // #TODO Remove these lines later...!
// ini_set('display_startup_errors',1);
// error_reporting(-1);

// get config values
$CONFIG				= include('./backend/config.php'); 
$DB_HOST		= $CONFIG['host'];
$DB_PORT		= $CONFIG['port'];
$DB_NAME		= $CONFIG['db'];
$DB_USER		= $CONFIG['user'];
$DB_PASSWORD	= $CONFIG['pw'];
$BUILD_FILE_PATH	= $CONFIG['build_file_path'];
$IMG_ROOT_PATH		= $CONFIG['img_root_path'];

// the graphtype which should be displayed first. Standard: alpha-graph
// and all other graphtypes
$graphtype			= "alpha"; # alpha-helix is #1
$graphtypes			= array("alpha", "beta", "albe", "alphalig", "betalig", "albelig");

//remove currently displayed graphtype from graphtype array
// to be honest: not sure if neccessary...!
$index = array_search($graphtype, $graphtypes);
if($index !== FALSE){
    unset($graphtypes[$index]);
} 

// translate graphtype abbr. to understandable string
$graphtype_dict = array(
	"alpha"		=> "Alpha",
	"beta"		=> "Beta",
	"albe"		=> "Alpha-Beta",
	"alphalig"	=> "Alpha-Ligand",
	"betalig"	=> "Beta-Ligand",
	"albelig"	=> "Alpha-Beta-Ligand"
);

// establish database connection
$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
$db = pg_connect($conn_string)
		or die($DB_NAME . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            
	
// set the variables for the current slide
// and the list of PDB-IDs
$currentSlide = $_POST['currentSlide'];
$chainIDs = $_POST['chainIDs'];
// determine which slide (images) should be loaded
$loadSlideNumner = $currentSlide + 1;
// split the needed PDB ID into PDB-ID and chain name
$pdb_chain = str_split($chainIDs[$loadSlideNumner], 4);
$pdb_id = $pdb_chain[0];
$chain = $pdb_chain[1];

// query the database as we did in display_proteins.php
pg_query($db, "DEALLOCATE ALL");
$query = "SELECT * FROM plcc_chain, plcc_graph WHERE pdb_id LIKE $1 AND chain_name LIKE $2 AND plcc_graph.chain_id = plcc_chain.chain_id ORDER BY graph_type"; 
pg_prepare($db, "getChains", $query) or die($query . ' -> Query failed: ' . pg_last_error());		
$result = pg_execute($db, "getChains", array("%".$pdbID."%", "%".$chainName."%"));  
$data = pg_fetch_array($result, NULL, PGSQL_ASSOC);
$chain_id = (int) $data['chain_id'];

// former $output...
$output =	'<li>
			<a title="Loaded_'.$loaded_images.'" href="./data/'.$data['graph_image_png'].'" target="_blank">
			<img src="./data/'.$data['graph_image_png'].'" alt="" />
			</a>
			<a href="./data/'.$data['graph_image_png'].'" target="_blank">Full Size Image</a>
			<span class="download-options">Download Graph: ';

// check if downloadable files exist. If so, then add link to file (4x)
if(isset($data['graph_image_pdf']) && file_exists($IMG_ROOT_PATH.$data['graph_image_pdf'])) {
	$output .= '<a href="./data/'.$data['graph_image_pdf'].'" target="_blank">[PDF]</a>';
}
if(isset($data['graph_image_svg']) && file_exists($IMG_ROOT_PATH.$data['graph_image_svg'])){
	$output .= '<a href="./data/'.$data['graph_image_svg'].'" target="_blank">[SVG]</a>';
}
if(isset($data['graph_image_png']) && file_exists($IMG_ROOT_PATH.$data['graph_image_png'])){
	$output .= '<a href="./data/'.$data['graph_image_png'].'" target="_blank">[PNG]</a>';
}
if(isset($data['graph_string_gml']) && file_exists($IMG_ROOT_PATH.$data['graph_string_gml'])){
	$output .= '<a href="./data/'.$data['graph_string_gml'].'" target="_blank">[GML]</a>';
}

$output .= '</span></li>';


while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
	$output .= '<li>';
	$output .= '<a href="./data/'.$arr['graph_image_png'].'" target="_blank">
					<img src="./data/'.$arr['graph_image_png'].'" alt="" />
					</a>
				<a href="./data/'.$arr['graph_image_png'].'" target="_blank">Full Size Image</a>
				<span class="download-options">Download Graph: ';

	// check if downloadable files exist. If so, then add link to file (4x)
	if(isset($data['graph_image_pdf']) && file_exists($IMG_ROOT_PATH.$data['graph_image_pdf'])) {
		$output .= '<a href="./data/'.$data['graph_image_pdf'].'" target="_blank">[PDF]</a>';
	}
	if(isset($data['graph_image_svg']) && file_exists($IMG_ROOT_PATH.$data['graph_image_svg'])){
		$output .= '<a href="./data/'.$data['graph_image_svg'].'" target="_blank">[SVG]</a>';
	}
	if(isset($data['graph_image_png']) && file_exists($IMG_ROOT_PATH.$data['graph_image_png'])){
		$output .= '<a href="./data/'.$data['graph_image_png'].'" target="_blank">[PNG]</a>';
	}
	if(isset($data['graph_string_gml']) && file_exists($IMG_ROOT_PATH.$data['graph_string_gml'])){
		$output .= '<a href="./data/'.$data['graph_string_gml'].'" target="_blank">[GML]</a>';
	}
	$output .= '</span></li>';	
}
// return HTML string to the AJAX request
echo $output;
//EOF
?>