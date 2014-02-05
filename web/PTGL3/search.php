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
    if(isset($_GET["exact"])) {$exactSearch = $_GETT["exact"];};
    if(isset($_GET["SelectRedund"])) {$selectRedund = $_GET["SelectRedund"];};
}

echo "Search Query: " . $keyword . "<br />Exact -> " . $exactSearch . "<br />Search Redundant -> ". $selectRedund . "<br />";

if (strlen($keyword) == 4) {
    if (is_numeric($keyword[0])) {
        echo 'Might be a PDB ID. URL: <a href="http://www.rcsb.org/pdb/explore/explore.do?structureId=' . $keyword . '">' . $keyword .'</a>';
    }
}




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
	$tableString .= '<table border="0" class="results' .$class.'">
					<colgroup>
						<col width="100">						
					</colgroup>
					<tr class="tableHeader' .$class.'"> 
						<td class="tableName">'.$arr["pdb_id"].'</td> 
						<td><i>Resolution:</i></td>
						<td>' .$arr["resolution"]. '</td> 
					</tr>
					<tr>
						<td class="tableCategories">Title</td>
						<td>' .$arr["title"]. '</td>
						<td></td>
						<td></td>
					</tr>
					<tr> 
						<td class="tableCategories">Classification</td> 
						<td>'.$arr["header"].'</td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<td class="tableCategories">EC number</td>
						<td>#####</td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<td class="tableCategories">Chain A</td>
						<td>Checkbox <br> SCOP</td>
						<td>####</td>
						<td></td>
					</tr>
				</table>';
	echo $counter;
	$counter++;
}

pg_free_result($result); // clean memory
pg_close($db); // close connection


?>