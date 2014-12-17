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


if(((isset($_GET["st"])) && ($_GET["st"] != "")) || (isset($_SESSION["st"]))){
    if(isset($_SESSION["st"])){
        $st = $_SESSION["st"];
    } else {
        $st = $_GET["st"];
    }
    
    if($st === "keyword"){
        $search_type = "keyword search";
        include('search_keyword.php');
    }
    if($st === "advanced"){
        $search_type = "advanced search";
        include('search_advanced.php');
    }
    if($st === "similarity"){
        $search_type = "BioGraphletSimilarity";
        include('search_similarity.php');
    }     
} else if(((isset($_GET["motif"])) && ($_GET["motif"] != "")) || (isset($_SESSION["motif"]))) {
    $search_type = "motif search";
    include('search_motif.php');
}

else if(((isset($_GET["linnotalphaadj"])) && ($_GET["linnotalphaadj"] != "")) || (isset($_SESSION["linnotalphaadj"])) ||
        ((isset($_GET["linnotalphared"])) && ($_GET["linnotalphared"] != "")) || (isset($_SESSION["linnotalphared"])) ||
        ((isset($_GET["linnotalphakey"])) && ($_GET["linnotalphakey"] != "")) || (isset($_SESSION["linnotalphakey"])) ||
        ((isset($_GET["linnotalphaseq"])) && ($_GET["linnotalphaseq"] != "")) || (isset($_SESSION["linnotalphaseq"])) ||
        
        ((isset($_GET["linnotbetaadj"])) && ($_GET["linnotbetaadj"] != "")) || (isset($_SESSION["linnotbetaadj"])) ||
        ((isset($_GET["linnotbetared"])) && ($_GET["linnotbetared"] != "")) || (isset($_SESSION["linnotbetared"])) ||
        ((isset($_GET["linnotbetakey"])) && ($_GET["linnotbetakey"] != "")) || (isset($_SESSION["linnotbetakey"])) ||
        ((isset($_GET["linnotbetaseq"])) && ($_GET["linnotbetaseq"] != "")) || (isset($_SESSION["linnotbetaseq"])) ||
        
        ((isset($_GET["linnotalbeadj"])) && ($_GET["linnotalbeadj"] != "")) || (isset($_SESSION["linnotalbeadj"])) ||
        ((isset($_GET["linnotalbered"])) && ($_GET["linnotalbered"] != "")) || (isset($_SESSION["linnotalbered"])) ||
        ((isset($_GET["linnotalbekey"])) && ($_GET["linnotalbekey"] != "")) || (isset($_SESSION["linnotalbekey"])) ||
        ((isset($_GET["linnotalbeseq"])) && ($_GET["linnotalbeseq"] != "")) || (isset($_SESSION["linnotalbeseq"])) ||
        
        ((isset($_GET["linnotalphaligadj"])) && ($_GET["linnotalphaligadj"] != "")) || (isset($_SESSION["linnotalphaligadj"])) ||
        ((isset($_GET["linnotalphaligred"])) && ($_GET["linnotalphaligred"] != "")) || (isset($_SESSION["linnotalphaligred"])) ||
        ((isset($_GET["linnotalphaligkey"])) && ($_GET["linnotalphaligkey"] != "")) || (isset($_SESSION["linnotalphaligkey"])) ||
        ((isset($_GET["linnotalphaligseq"])) && ($_GET["linnotalphaligseq"] != "")) || (isset($_SESSION["linnotalphaligseq"])) ||
        
        ((isset($_GET["linnotbetaligadj"])) && ($_GET["linnotbetaligadj"] != "")) || (isset($_SESSION["linnotbetaligadj"])) ||
        ((isset($_GET["linnotbetaligred"])) && ($_GET["linnotbetaligred"] != "")) || (isset($_SESSION["linnotbetaligred"])) ||
        ((isset($_GET["linnotbetaligkey"])) && ($_GET["linnotbetaligkey"] != "")) || (isset($_SESSION["linnotbetaligkey"])) ||
        ((isset($_GET["linnotbetaligseq"])) && ($_GET["linnotbetaligseq"] != "")) || (isset($_SESSION["linnotbetaligseq"])) ||
        
        ((isset($_GET["linnotalbeligadj"])) && ($_GET["linnotalbeligadj"] != "")) || (isset($_SESSION["linnotalbeligadj"])) ||
        ((isset($_GET["linnotalbeligred"])) && ($_GET["linnotalbeligred"] != "")) || (isset($_SESSION["linnotalbeligred"])) ||
        ((isset($_GET["linnotalbeligkey"])) && ($_GET["linnotalbeligkey"] != "")) || (isset($_SESSION["linnotalbeligkey"])) ||
        ((isset($_GET["linnotalbeligseq"])) && ($_GET["linnotalbeligseq"] != "")) || (isset($_SESSION["linnotalbeligseq"])) ) {
    $search_type = "linear notation";
    include('search_linnot.php');
}

else {
    $search_type = "wrong input :(";
}
//EOF
?>
