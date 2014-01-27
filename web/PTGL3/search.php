<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);


// Define variables

$db_config = include('config.php');     //TODO: Sichern?

$keyword = "";
$selectRedund = 0;
$exactSearch = 0;


// try to get _POST
if(isset($_POST)) {
    if(isset($_POST["keyword"])) {$keyword = $_POST["keyword"];};
    if(isset($_POST["exact"])) {$exactSearch = $_POST["exact"];};
    if(isset($_POST["SelectRedund"])) {$selectRedund = $_POST["SelectRedund"];};
}

echo "Search Query: " . $keyword . "<br />Exact -> " . $exactSearch . "<br />Search Redundant -> ". $selectRedund . "<br />";

if (strlen($keyword) == 4) {
    if (is_numeric($keyword[0])) {
        echo 'Might be a PDB ID. URL: <a href="http://www.rcsb.org/pdb/explore/explore.do?structureId=' . $keyword . '">' . $keyword .'</a>';
    }
}



/*
// establish pgsql connection
$db = pg_pconnect($db_config['host'], $db_config['port'], $db_config['db'], $db_config['user'], $db_config['pw'])
            or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );

$query = "SELECT * FROM banana WHERE id >= 1";  // e.g.
$result = pg_query($query) 
                  or die($query . ' -> Query failed: ' . pg_last_error());


pg_free_result($result); // clean memory
pg_close($db); // close connection
*/

?>