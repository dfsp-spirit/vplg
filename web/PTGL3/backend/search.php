<?php
/**search.php
 * 
 * Author: Daniel Bruneß <dbruness@gmail.com>
 * 
 * 
 * 
 * 
 * 
 */

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);

function get_cath_link($pdbid, $chain = null) {
  if($chain == null) {
    return "http://www.cathdb.info/pdb/" . $pdbid;
  }
  else {
    return "http://www.cathdb.info/chain/" . $pdbid . $chain;
  }
}

$db_config = include('config.php');     //TODO: Sichern?

// try to get POST data
$logic = "OR";
$none_set = true;
if(isset($_POST)) {
    if(isset($_POST["keyword"])) {$keyword = $_POST["keyword"];  $none_set = false;} else {$keyword = "";};
    if(isset($_POST["pdbid"])) {$pdbid = $_POST["pdbid"];  $none_set = false;};
    if(isset($_POST["title"])) {$title = $_POST["title"];  $none_set = false;};
    if(isset($_POST["hasligand"])) {$hasligand = $_POST["hasligand"];  $none_set = false;};
    if(isset($_POST["ligandname"])) {$ligandname = $_POST["ligandname"];  $none_set = false;};
    if(isset($_POST["molecule"])) {$molecule = $_POST["molecule"];  $none_set = false;};
    if(isset($_POST["logic"])) {$logic = $_POST["logic"];};
    if(isset($_POST["proteincomplexes"])) {$proteincomplexes = $_POST["proteincomplexes"];};
} else {
	$tableString = "Sorry. Your search term is too short. <br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
	exit;
}

if (/*(($keyword == "") || (strlen($keyword) <= 2)) || ($none_set == true)*/FALSE) {
	$tableString = "Sorry. Your search term is too short.<br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
	exit;
} else { 	// establish pgsql connection
	
	$conn_string = "host=" . $db_config['host'] . " port=" . $db_config['port'] . " dbname=" . $db_config['db'] . " user=" . $db_config['user'] ." password=" . $db_config['pw'];
	$db = pg_connect($conn_string)
					or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            
	
	
	$firstQuerySet = false;
	$query = "";
	
	if (isset($keyword) && $keyword != "") {
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE p.pdb_id LIKE '%".$keyword."%' 
				   OR p.header LIKE '%".strtoupper($keyword)."%'
				   ORDER BY pdb_id, chain_name";
		$firstQuerySet = true; };
	
	if (isset($pdbid) && $pdbid != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE p.pdb_id = '".$pdbid."'
				   ORDER BY pdb_id, chain_name";
		$firstQuerySet = true; };
	
	if (isset($title) && $title != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE p.title = '".strtoupper($title)."'
				   ORDER BY pdb_id, chain_name";
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
				   WHERE l.ligandtochain_ligandname3 IS".$operator."NULL
				   ORDER BY pdb_id, chain_name"; 
		$firstQuerySet = true; };

	if (isset($ligandname) && $ligandname != "") { 
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_nm_ligandtochain l
				   INNER JOIN plcc_chain c ON l.ligandtochain_chainid = c.chain_id
				   INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id 
				   WHERE l.ligandtochain_ligandname3 = '".$ligandname."'
				   ORDER BY pdb_id, chain_name"; 
		$firstQuerySet = true; };

	if (isset($molecule) && $molecule != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE c.mol_name LIKE '%".$molecule."%'
				   ORDER BY pdb_id, chain_name";
		$firstQuerySet = true; };



	if ($logic == "OR") {
		$query = rtrim($query, " OR ");
	} else {
		$query = rtrim($query, " AND ");
	}

	$result = pg_query($db, $query) or die($query . ' -> Query failed: ' . pg_last_error());

	$counter = 0;
	$tableString = "";
	$numberOfChains = 0;
	$createdHeadlines = Array();
	while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		$numberOfChains++;
		
		$pdb_id =  $arr['pdb_id'];
		$chain_name = $arr['chain_name'];
		$resolution = $arr['resolution'];
		$title = $arr["title"];
		$header = $arr["header"];
		$cathlink = get_cath_link($pdb_id, $chain_name);
		
		// provides alternating orange/white tables
		if ($counter % 2 == 0){
			$class = "Orange";	
		} else {
			$class = "White";
		}
		
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

			array_push($createdHeadlines, $pdb_id);
		}

		$tableString .= '	<div class="resultsFooter">
						<div class="resultsChain">Chain '.$chain_name.'</div>
						<div class="resultsChainNum"><input type=checkbox id="'.$pdb_id . $chain_name. '" class="chainCheckBox" value="'.$pdb_id . $chain_name.'"/>'.$pdb_id . $chain_name.'</div>
						<div class="resultsCATH"><a href="'.$cathlink.'" target="_blank">CATH</a></div>
					</div>';
		

		$counter++;
	}
	$tableString .= ' </div>';
	pg_free_result($result); // clean memory
	pg_close($db); // close connection
}

?>