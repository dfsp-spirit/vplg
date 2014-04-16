<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);


// Define variables

$db_config = include('config.php');     //TODO: Sichern?


// try to get POST data
$none_set = true;
if(isset($_POST)) {
    if(isset($_POST["keyword"])) {$keyword = $_POST["keyword"];  $none_set = false;} else {$keyword = "";};
    if(isset($_POST["pdbid"])) {$pdbid = $_POST["pdbid"];  $none_set = false;};
    if(isset($_POST["title"])) {$title = $_POST["title"];  $none_set = false;};
    if(isset($_POST["het"])) {$het = $_POST["het"];  $none_set = false;};
    if(isset($_POST["hetname"])) {$hetname = $_POST["hetname"];  $none_set = false;};
    if(isset($_POST["scop"])) {$scop = $_POST["scop"];  $none_set = false;};
    if(isset($_POST["scopid"])) {$scopid = $_POST["scopid"];  $none_set = false;};
    if(isset($_POST["cath"])) {$cath = $_POST["cath"];  $none_set = false;};
    if(isset($_POST["cathid"])) {$cathid = $_POST["cathid"];  $none_set = false;};
    if(isset($_POST["ec"])) {$ec = $_POST["ec"];  $none_set = false;};
    if(isset($_POST["molecule"])) {$molecule = $_POST["molecule"];  $none_set = false;};
    if(isset($_POST["classification"])) {$classification = $_POST["classification"];  $none_set = false;};
    if(isset($_POST["graphs"])) {$graphs = $_POST["graphs"];  $none_set = false;};
    if(isset($_POST["logic"])) {$logic = $_POST["logic"];  $none_set = false;};
} else {
	$tableString = "Sorry. Your search term is too short. <br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
}

if ((($keyword == "") || (strlen($keyword) <= 2)) && ($none_set == true)) {
	$tableString = "Sorry. Your search term is too short.<br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
} else { 	// establish pgsql connection
	
	$conn_string = "host=" . $db_config['host'] . " port=" . $db_config['port'] . " dbname=" . $db_config['db'] . " user=" . $db_config['user'] ." password=" . $db_config['pw'];
	$db = pg_connect($conn_string)
					or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            
	
	#TODO change queries
	$query = "SELECT * FROM plcc_protein WHERE ";
	if (isset($keyword) && ($keyword != "")) { $query .= "pdb_id LIKE '%".$keyword."%' OR header LIKE '%".strtoupper($keyword)."%' ".$logic." "; };
	if (isset($pdbid)) {   $query .= "pdb_id LIKE '%".$pdbid."%' ".$logic." "; };
	if (isset($title)) {   $query .= "title LIKE '%".$title."%' ".$logic." "; };
	if (isset($het)) {     $query .= "pdb_id LIKE '%".$het."%' ".$logic." "; };
	if (isset($hetname)) { $query .= "pdb_id LIKE '%".$hetname."%' ".$logic." "; };
	if (isset($scop)) {    $query .= "pdb_id LIKE '%".$scop."%' ".$logic." "; };
	if (isset($scopid)) {  $query .= "pdb_id LIKE '%".$scopid."%' ".$logic." "; };
	if (isset($cath)) {    $query .= "pdb_id LIKE '%".$cath."%' ".$logic." "; };
	if (isset($cathid)) {  $query .= "pdb_id LIKE '%".$cathid."%' ".$logic." "; };
	if (isset($ec)) {      $query .= "pdb_id LIKE '%".$ec."%' ".$logic." "; };
	if (isset($molecule)) {$query .= "pdb_id LIKE '%".$molecule."%' ".$logic." "; };
	if (isset($classification)) {$query .= "pdb_id LIKE '%".$classification."%' ".$logic." "; };
	if (isset($graphs)) {  $query .= "pdb_id LIKE '%".$graphs."%' ".$logic." "; };

	if ($logic == "OR") {
		$query = rtrim($query, " OR ");
	} else {
		$query = rtrim($query, " AND ");
	}
	echo "<br><br><br>.....<br>".$query;

	$result = pg_query($db, $query) or die($query . ' -> Query failed: ' . pg_last_error());

	$counter = 0;
	$tableString = "";
	while (($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)) && ($counter <= 30)){
		
		$query = "SELECT * FROM plcc_chain WHERE pdb_id = '".$arr["pdb_id"]."' ORDER BY chain_name";
		$result_chains = pg_query($db, $query) 
					  or die($query . ' -> Query failed: ' . pg_last_error());

		// provides alternating orange/white tables
		if ($counter % 2 == 0){$class = "Orange";} else {$class = "White";}

		$tableString .=	 '<div class="results results'.$class.'">					
						<div class="resultsHeader resultsHeader'.$class.'">
							<div class="resultsId">'.$arr["pdb_id"].'</div>
							<div class="resultsRes">Resolution: '.$arr["resolution"].' &Aring;</div>
							<div class="resultsLink"><a href="http://www.rcsb.org/pdb/explore/explore.do?structureId='.$arr["pdb_id"].'">[PDB]</a>
												<a href="">[PDBSum]</a>
												<a href="">[FASTA]</a></div>
						</div>
						<div class="resultsBody1">
							<div class="resultsTitle">Title</div>
							<div class="resultsTitlePDB">' .$arr["title"]. '</div>
						</div>
						<div class="resultsBody2">
							<div class="resultsClass">Classification</div>
							<div class="resultsClassPDB">'.$arr["header"].'</div>
						</div>
						<div class="resultsBody3">
							<div class="resultsEC">EC#	</div>
							<div class="resultsECNum">#####</div>
						</div>';
		while ($chains = pg_fetch_array($result_chains, NULL, PGSQL_ASSOC)){
				
			$tableString .= '	<div class="resultsFooter">
							<div class="resultsChain">Chain '.$chains["chain_name"].'</div>
							<div class="resultsChainNum"><input type=checkbox id="'.$arr["pdb_id"] . $chains["chain_name"]. '" class="chainCheckBox" value="'.$arr["pdb_id"] . $chains["chain_name"].'"/>'.$arr["pdb_id"] . $chains["chain_name"].'</div>
							<div class="resultsSCOP">Scop ####</div>
							<div class="resultsCATH">CATH ####</div>
						</div>';
		}
		$tableString .= ' </div>';

		echo $counter;
		$counter++;
	}

	pg_free_result($result); // clean memory
	pg_close($db); // close connection
}

?>