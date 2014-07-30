<?php
/** This file provides the search results depending on the search query.
 * 
 * It receives the parameters from the search box on the frontpage or the advanced search box.
 * If there are multiple search keywords, the query is joined either with OR or AND.
 * The results (if we got some) are displayed in a table and the HTML construct is
 * stored in the $tableString, which will be echoed later.
 *   
 * @author Daniel Bruness <dbruness@gmail.com>
 * @author Andreas Scheck <andreas.scheck.home@googlemail.com>
 */

ini_set('display_errors', 0);
ini_set('display_startup_errors', 0);
ini_set('log_errors', TRUE);
error_reporting(E_ERROR);

// get config values
$CONFIG				= include('./backend/config.php'); 
$DB_HOST		= $CONFIG['host'];
$DB_PORT		= $CONFIG['port'];
$DB_NAME		= $CONFIG['db'];
$DB_USER		= $CONFIG['user'];
$DB_PASSWORD	= $CONFIG['pw'];
$BUILD_FILE_PATH	= $CONFIG['build_file_path'];
$IMG_ROOT_PATH		= $CONFIG['img_root_path'];
$DEBUG			= $CONFIG['debug'];

if($DEBUG){
	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	ini_set('log_errors', TRUE);
	error_reporting(E_ALL);
}


/** The function creates a CATH link with a PDB-ID and optional a chain-name
 * 
 * @param type $pdbid
 * @param type $chain
 * @return type
 */
function get_cath_link($pdbid, $chain = null) {
	// if no chainname is given...
	if($chain == null) {
	  return "http://www.cathdb.info/pdb/" . $pdbid;
	}
	else {
	  return "http://www.cathdb.info/chain/" . $pdbid . $chain;
	}
}


// set standard logic operator to "OR"
$logic = "OR";
// check if parameters are set. If so, set the associated variable with the value
if(isset($_POST)) {
    if(isset($_POST["keyword"])) {$keyword = $_POST["keyword"];} else {$keyword = "";};
    if(isset($_POST["pdbid"])) {$pdbid = $_POST["pdbid"];};
    if(isset($_POST["title"])) {$title = $_POST["title"];};
    if(isset($_POST["hasligand"])) {$hasligand = $_POST["hasligand"];};
    if(isset($_POST["ligandname"])) {$ligandname = $_POST["ligandname"];};
    if(isset($_POST["molecule"])) {$molecule = $_POST["molecule"];};
    if(isset($_POST["logic"])) {$logic = $_POST["logic"];};
    // if(isset($_POST["proteincomplexes"])) {$proteincomplexes = $_POST["proteincomplexes"];};
} else {
	// if nothing is set or the query is too short...
	$tableString = "Sorry. Your search term is too short. <br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
	exit;
}

if (($none_set == true)) { // #TODO redefine this check...
	$tableString = "Sorry. Your search term is too short.<br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
	exit;
} else {
	// establish database connection
	$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	$db = pg_connect($conn_string)
			or die($DB_NAME . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            

	// later, if no query is set before, there will be no CONCAT/UNION
	$firstQuerySet = false;
	$query = "SELECT chain_id, chain_name, pdb_id, resolution, title, header
			  FROM ( ";
	
	// following: the queries for each set parameter
	if (isset($keyword) && $keyword != "") {
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE p.pdb_id LIKE '%".$keyword."%' 
				   OR p.header LIKE '%".strtoupper($keyword)."%'";
		$firstQuerySet = true; };
	
	if (isset($pdbid) && $pdbid != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE p.pdb_id = '".$pdbid."'";
		$firstQuerySet = true; };
	
	if (isset($title) && $title != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE p.title = '".strtoupper($title)."'";
		$firstQuerySet = true; };
	
	if (isset($hasligand) && $hasligand != "null") {
		if($hasligand == "1") {
			$operator = " NOT ";
		} else if ($hasligand == "0"){
			$operator = " ";
		}
		
		if($firstQuerySet) { $query .= " UNION "; }
			$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
					FROM plcc_nm_ligandtochain l
					INNER JOIN plcc_chain c ON l.ligandtochain_chainid = c.chain_id
					INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id 
					WHERE l.ligandtochain_ligandname3 IS".$operator."NULL"; 
			$firstQuerySet = true; 
		 
	};

	if (isset($ligandname) && $ligandname != "") { 
		if($firstQuerySet) { $query .= " UNION "; }
		/*$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_nm_ligandtochain l
				   INNER JOIN plcc_chain c ON l.ligandtochain_chainid = c.chain_id
				   INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id 
				   WHERE l.ligandtochain_ligandname3 = '".$ligandname."'
				   ORDER BY pdb_id, chain_name"; */
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_nm_ligandtochain l2c ON c.chain_id = l2c.ligandtochain_chainid
				   INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id 
				   WHERE l2c.ligandtochain_ligandname3 = '".$ligandname."'"; 
		$firstQuerySet = true; 	
	};

	if (isset($molecule) && $molecule != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE c.mol_name LIKE '%".$molecule."%'";
		$firstQuerySet = true; };

	$query .= " ) results
			  ORDER BY pdb_id, chain_name";
  

	$result = pg_query($db, $query); // or die($query . ' -> Query failed: ' . pg_last_error());

	// this counter is used to display alternating table colors
	$counter = 0;
	$tableString = "";
	$createdHeadlines = Array();
	$numberOfChains = 0;
	while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		// set protein/chain information for readability		
		$pdb_id =  $arr['pdb_id'];
		$chain_name = $arr['chain_name'];
		$resolution = $arr['resolution'];
		$title = $arr["title"];
		$header = $arr["header"];
		$cathlink = get_cath_link($pdb_id, $chain_name);
		
		// provides alternating blue/white tables
		if ($counter % 2 == 0){
			$class = "Orange";	// the CSS class is still called orange...
		} else {
			$class = "White";
		}
		
		// if the headline of the PDB-ID is NOT created yet...
		if(!in_array($pdb_id, $createdHeadlines)){
			$tableString .=	 '<div class="results results'.$class.'">					
							<div class="resultsHeader resultsHeader'.$class.'">
								<div class="resultsId">'.$pdb_id.'</div>
								<div class="resultsRes">Resolution: '.$resolution.' &Aring;</div>
								<div class="resultsLink"><a href="http://www.rcsb.org/pdb/explore/explore.do?structureId='.$pdb_id.'" target="_blank">[PDB]</a>
													<a href="http://www.rcsb.org/pdb/download/downloadFile.do?fileFormat=FASTA&compression=NO&structureId='.$pdb_id.'" target="_blank">[FASTA]</a></div>
							</div>
							<div class="resultsBody1">
								<div class="resultsTitle">Title</div>
								<div class="resultsTitlePDB">' .ucfirst(strtolower($title)). '</div>
							</div>
							<div class="resultsBody2">
								<div class="resultsClass">Classification</div>
								<div class="resultsClassPDB">'.ucfirst(strtolower($header)).'</div>
							</div>';
			// now the headline is created, so push the PDB-ID to the createdHeadlines array
			array_push($createdHeadlines, $pdb_id);
		}
		// if the headline is already there..
		$tableString .= '	<div class="resultsFooter">
						<div class="resultsChain">Chain '.$chain_name.'</div>
						<div class="resultsChainNum"><input type=checkbox id="'.$pdb_id . $chain_name. '" class="chainCheckBox" value="'.$pdb_id . $chain_name.'"/>'.$pdb_id . $chain_name.'</div>
						<div class="resultsCATH"><a href="'.$cathlink.'" target="_blank">CATH</a></div>
					</div>';
		
		$numberOfChains++;
		$counter++;
	}
	$tableString .= ' </div>';
	pg_free_result($result); // clean memory
	pg_close($db); // close connection
}
//EOF
?>