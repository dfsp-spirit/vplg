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




// establish pgsql connection
$conn_string = "host=" . $db_config['host'] . " port=" . $db_config['port'] . " dbname=" . $db_config['db'] . " user=" . $db_config['user'] ." password=" . $db_config['pw'];
$db = pg_connect($conn_string)
                or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            
$query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES";
$result = pg_query($db, $query) 
                  or die($query . ' -> Query failed: ' . pg_last_error());
$data = pg_fetch_all($result);

foreach ($data as $item) {
    echo $item["table_name"]. "\n<br \>";
}

pg_free_result($result); // clean memory
pg_close($db); // close connection


?>