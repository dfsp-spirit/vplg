<?php
/** This file provides the complex search results depending on the search query.
 * 
 * It receives the parameters from the complex search box on the frontpage.
 * The results (if we got some) are displayed in a table and the HTML construct is
 * stored in the $tableString, which will be echoed later.
 *   
 *
 * @author Tim Schäfer
 *
 */

ini_set('display_errors', 0);
ini_set('display_startup_errors', 0);
ini_set('log_errors', TRUE);
error_reporting(E_ERROR);


// get config values
include('./backend/config.php');

if($DEBUG){
	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	ini_set('log_errors', TRUE);
	error_reporting(E_ALL);
}


if(isset($_GET["stc"]) && ($_GET["stc"] != "")) {
    $stc = $_GET["stc"];    
}
else if (isset($_SESSION["stc"])){
    $stc = $_SESSION["stc"];
}
else {
    $stc = "";    // wrong input, caught below
}


if($stc === "complex_pdbid"){
    $search_type = "Complex PDB ID";
    include('search_complex_pdbid.php');
}
else {
    $search_type = "wrong input :(";
}


//EOF
?>
