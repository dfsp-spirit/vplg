<?php
ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);

// Define variables

$db_config = include('config.php');     //TODO: Sichern?

$keyword = "";
$byID = 0;
$selectRedund = 0;
$exactSearch = 0;


// try to get _POST
if(isset($_POST)) {
    try {
        $keyword = $_POST["keyword"];
        $byID = $_POST["id"];
        $exactSearch = $_POST["exact"];
        $selectRedund = $_POST["SelectRedund"];
        
    } catch (Exception $exc) {
        echo $exc->getTraceAsString();
    }
}

// establish pgsql connection
$db = pg_connect($db_config['host'], $db_config['port'], $db_config['db'], $db_config['user'], $db_config['pw'])
            or die($db_config['db'] . ' -> Connection error: ' . pg_last_error());

$query = "SELECT * FROM banana WHERE id >= 1";  // e.g.
$result = pg_query($query) 
                  or die($query . ' -> Query failed: ' . pg_last_error());


pg_free_result($result); // clean memory
pg_close($db); // close connection


?>