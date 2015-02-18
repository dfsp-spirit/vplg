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
 * @author Tim Schäfer <>
 *
 * Tim added options to search for folding graph linear notations, SEPT 2014.
 * Tim added options to search for motifs, OCT 2014.
 * Tim added options to search for graphlet-based similarity to a query protein, OCT 2014.
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


if(isset($_GET["st"]) && ($_GET["st"] != "")) {
    $st = $_GET["st"];    
}
else if (isset($_SESSION["st"])){
    $st = $_SESSION["st"];
}
else {
    $st = "";    // wrong input, caught below
}


if($st === "keyword"){
    $search_type = "Keyword search";
    include('search_keyword.php');
}
else if($st === "advanced"){
    $search_type = "Advanced search";
    include('search_advanced.php');
}
else if($st === "similarity"){
    $search_type = "BioGraphletSimilarity";
    include('search_similarity.php');
}
else if($st === "random"){
    $search_type = "Random search";
    include('search_random.php');
}
else if($st === "motif"){
    $search_type = "Motif search";
    include('search_motif.php');
}
else if($st === "customlinnot"){
    $search_type = "Linear notation search";
    include('search_linnot.php');
}
else {
    $search_type = "wrong input :(";
}


//EOF
?>
