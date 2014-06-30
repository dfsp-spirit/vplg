<?php

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

// Define variables

$db_config = include('config.php');     //TODO: Sichern?

phpinfo();

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
	
	#TODO change queries
	$query = "SELECT * FROM plcc_protein, plcc_chain, plcc_nm_ligandtochain WHERE ";
	if (isset($keyword) && $keyword != "") {
		$query .= "plcc_protein.pdb_id LIKE '%".$keyword."%' 
					OR plcc_protein.header LIKE '%".strtoupper($keyword)."%' ".$logic." "; 
	};
	if (isset($pdbid) && $pdbid != "") {   $query .= "plcc_protein.pdb_id LIKE '%".$pdbid."%' ".$logic." "; };
	if (isset($title) && $title != "") {   $query .= "plcc_protein.title LIKE '%".strtoupper($title)."%' ".$logic." "; };
	if (isset($hasligand) && $hasligand == "1") {
		$query .= "ligandtochain_ligandname3 IS NOT NULL
					AND plcc_nm_ligandtochain.ligandtochain_chainid = plcc_chain.chain_id 
					AND plcc_chain.pdb_id = plcc_protein.pdb_id ".$logic." "; 
	};
	if (isset($ligandname) && $ligandname != "") { 
		$query .= "ligandtochain_ligandname3 LIKE '%".$ligandname."' 
					AND plcc_nm_ligandtochain.ligandtochain_chainid = plcc_chain.chain_id 
					AND plcc_chain.pdb_id = plcc_protein.pdb_id ".$logic." "; 
	};
	if (isset($molecule) && $molecule != "") {$query .= "pdb_id LIKE '%".$molecule."%' ".$logic." "; };


	if ($logic == "OR") {
		$query = rtrim($query, " OR ");
	} else {
		$query = rtrim($query, " AND ");
	}

	$result = pg_query($db, $query) or die($query . ' -> Query failed: ' . pg_last_error());

	$counter = 0;
	$tableString = "";
	$numberOfChains = 0;
	while (($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)) && ($counter <= 30)){
		
		$query = "SELECT DISTINCT (chain_name), pdb_id FROM plcc_chain WHERE pdb_id = '".$arr["pdb_id"]."' ORDER BY chain_name";
		if($ligandname != "" ){
			$query = "SELECT DISTINCT (chain_name), pdb_id, ligandtochain_chainid FROM plcc_chain, plcc_nm_ligandtochain WHERE plcc_chain.pdb_id = '".$arr["pdb_id"]."'
					  AND plcc_chain.chain_id = plcc_nm_ligandtochain.ligandtochain_chainid
					  ORDER BY chain_name";
		}
		$result_chains = pg_query($db, $query) 
					  or die($query . ' -> Query failed: ' . pg_last_error());

		// provides alternating orange/white tables
		if ($counter % 2 == 0){$class = "Orange";} else {$class = "White";}

		$tableString .=	 '<div class="results results'.$class.'">					
						<div class="resultsHeader resultsHeader'.$class.'">
							<div class="resultsId">'.$arr["pdb_id"].'</div>
							<div class="resultsRes">Resolution: '.$arr["resolution"].' &Aring;</div>
							<div class="resultsLink"><a href="http://www.rcsb.org/pdb/explore/explore.do?structureId='.$arr["pdb_id"].'" target="_blank">[PDB]</a>
												<a href="http://www.rcsb.org/pdb/download/downloadFile.do?fileFormat=FASTA&compression=NO&structureId='.$arr["pdb_id"].'" target="_blank">[FASTA]</a></div>
						</div>
						<div class="resultsBody1">
							<div class="resultsTitle">Title</div>
							<div class="resultsTitlePDB">' .ucfirst(strtolower($arr["title"])). '</div>
						</div>
						<div class="resultsBody2">
							<div class="resultsClass">Classification</div>
							<div class="resultsClassPDB">'.ucfirst(strtolower($arr["header"])).'</div>
						</div>';
		while ($chains = pg_fetch_array($result_chains, NULL, PGSQL_ASSOC)){
			$numberOfChains++;
			$cathlink = get_cath_link($arr["pdb_id"], $chains["chain_name"]);
			$tableString .= '	<div class="resultsFooter">
							<div class="resultsChain">Chain '.$chains["chain_name"].'</div>
							<div class="resultsChainNum"><input type=checkbox id="'.$arr["pdb_id"] . $chains["chain_name"]. '" class="chainCheckBox" value="'.$arr["pdb_id"] . $chains["chain_name"].'"/>'.$arr["pdb_id"] . $chains["chain_name"].'</div>
							<div class="resultsCATH"><a href="'.$cathlink.'" target="_blank">CATH</a></div>
						</div>';
		}
		$tableString .= ' </div>';

		// $counter++; do not limit displayed proteins
	}

	pg_free_result($result); // clean memory
	pg_close($db); // close connection
}

?>