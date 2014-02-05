<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);


// Define variables

$db_config = include('config.php');     //TODO: Sichern?

$keyword = "";
$selectRedund = 0;
$exactSearch = 0;


// try to get _GET
if(isset($_GET)) {
    if(isset($_GET["keyword"])) {$keyword = $_GET["keyword"];};
    if(isset($_GET["exact"])) {$exactSearch = $_GET["exact"];};
    if(isset($_GET["SelectRedund"])) {$selectRedund = $_GET["SelectRedund"];};
}

echo "<br /><br /><br /><br /><br />Search Query: " . $keyword . "<br />Exact -> " . $exactSearch . "<br />Search Redundant -> ". $selectRedund . "<br />";

if (strlen($keyword) == 4) {
    if (is_numeric($keyword[0])) {
        echo 'Might be a PDB ID. URL: <a href="http://www.rcsb.org/pdb/explore/explore.do?structureId=' . $keyword . '">' . $keyword .'</a>';
    }
}



if (($keyword == "") || (strlen($keyword) <= 2)) {
	$tableString = "Sorry. Your search term is too short. <br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
} else {
	
	// establish pgsql connection
	$conn_string = "host=" . $db_config['host'] . " port=" . $db_config['port'] . " dbname=" . $db_config['db'] . " user=" . $db_config['user'] ." password=" . $db_config['pw'];
	$db = pg_connect($conn_string)
					or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            
	$query = "SELECT * FROM plcc_protein WHERE pdb_id LIKE '%".$keyword."%' OR header LIKE '%".strtoupper($keyword)."%'";
	$result = pg_query($db, $query) 
					  or die($query . ' -> Query failed: ' . pg_last_error());

	$counter = 0;
	$tableString = "";
	while (($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)) && ($counter <= 30)){
		if ($counter % 2 == 0){     // performs alternating orange/white tables
			$class = "Orange";
		} else {
			$class = "White";
		}
		// var_dump($arr);
		$tableString .=	 '<div class="results results'.$class.'">					
						<div class="resultsHeader resultsHeader'.$class.'">
							<div class="resultsId">'.$arr["pdb_id"].'</div>
							<div class="resultsRes">Resolution: '.$arr["resolution"].'</div>
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
						</div>
						<div class="resultsFooter">
							<div class="resultsChain">Chain</div>
							<div class="resultsChainNum">####</div>
							<div class="resultsSCOP">Scop ####</div>
							<div class="resultsCATH">CATH ####</div>
						</div>
					</div>';

		echo $counter;
		$counter++;
	}

	pg_free_result($result); // clean memory
	pg_close($db); // close connection
}

?>