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
 *
 * Tim added options to search for folding graph linear notations, SEPT 2014.
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

/** The function creates a CATH link with a PDB-ID and optional a chain-name
 * 
 * @param type $pdbid
 * @param type $chain
 * @return type
 */
function get_cath_link($pdbid, $chain = null) {
	// if no chainname is given...
	if($chain === null) {
	  return "http://www.cathdb.info/pdb/" . $pdbid;
	}
	else {
	  return "http://www.cathdb.info/chain/" . $pdbid . $chain;
	}
}

/**
  * Returns the SQL query string to get the linear notation of the requested type.
  * @param $linnot_type the linear notation type. This is used to create the colum name and has to be 'adj', 'red', 'key' or 'seq'.
  * @param $linnot_query_string the linear notation you are searching for (e.g., '{1h,3hp}'). You can provide a prepared statement template like '$1', but then you should set $bool_enclose_linnot_query_string_in_ticks to false.
  * @param $query_graph_type_int the graph type. 1=alpha, 2=beta, 3=albe, 4=alphalig, 5=betalig, 6=albelig.
  * @param $bool_enclose_linnot_query_string_in_ticks whether whether to enclose the linnot_query_string in ticks ('). The ticks must not be there if this is used in a prepared statement, where $linnot_query_string is set to something like '$3'.
  */
function get_linnot_query_string($linnot_type, $linnot_query_string, $query_graph_type_int, $bool_enclose_linnot_query_string_in_ticks = true) {
    $tick = "'";
	
	if(! $bool_enclose_linnot_query_string_in_ticks) {
	    $tick = "";
	}
	
	if(! ($linnot_type === "adj" || $linnot_type === "red" || $linnot_type === "seq" || $linnot_type === "key")) {
	  return '';
	}
	
    $res = "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_fglinnot ln
				   INNER JOIN plcc_foldinggraph fg
				   ON ln.linnot_foldinggraph_id = fg.foldinggraph_id
				   INNER JOIN plcc_graph pg
				   ON fg.parent_graph_id = pg.graph_id
				   INNER JOIN plcc_chain c
				   ON pg.chain_id = c.chain_id
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE ( ln.ptgl_linnot_" . $linnot_type . " = " . $tick . $linnot_query_string . $tick . " AND pg.graph_type = " . $query_graph_type_int . " )";
				   
    return $res;
}

function check_valid_pdbid($str) {
  if (preg_match('/^[A-Z0-9]{4}$/i', $str)) {
    return true;
  }  
  return false;
}

function check_valid_chainid($str) {
  if (preg_match('/^[A-Z]{1}$/i', $str)) {
    return true;
  }  
  return false;
}



/**
  * the $pdb_chain_list should be a string array, where each string is of length 5 and represents a PDB id and chain, e.g., '7timA' for PDB 7tim chain A.
  */
function get_multiple_PDB_select_query($pdb_chain_list) {

   $valid_pdb_ids = array();
   $valid_pdb_chains = array();
   foreach($pdb_chain_list as $pdbchain) {
     if(strlen($pdbchain) == 5) {
	   $pdb_id = substr($pdbchain, 0, 4);
	   $chain_id = substr($pdbchain, 4, 1);
	   if(check_valid_pdbid($pdb_id) && check_valid_chainid($chain_id)) {
	     array_push($valid_pdb_ids, $pdb_id);
		 array_push($valid_pdb_chains, $chain_id);
	   }	 
	 }
   }
   
   if(count($valid_pdb_ids) <= 0) {
     return "";
   }
   
   $query = "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE ( ";
	for($i = 0; $i < count($valid_pdb_ids); $i++) {
	  $pdb_id = $valid_pdb_ids[$i];
	  $chain_id = $valid_pdb_chains[$i];
	  $query .= " ( p.pdb_id = '" . $pdb_id . "' AND c.chain_name = '" . $chain_id . "' )";
	  if($i < count($valid_pdb_ids) - 1) {
	    $query .= " OR "
	  }
	}
	
	$query .= " )";
	
	return $query;
}

/** 
  * Returns the mitif code or a value < 0 if the abbriv is invalid.
  */
function get_motif_id($motif_abbreviation) {
	$motif_id = -1;
	if($motif_abbreviation === "4helix") {
		$motif_id = 1;
	}
	else if($motif_abbreviation === "globin") {
		$motif_id = 2;
	}
	else if($motif_abbreviation === "barrel") {
		$motif_id = 3;
	}
	else if($motif_abbreviation === "immuno") {
		$motif_id = 4;
	}
	else if($motif_abbreviation === "propeller") {
		$motif_id = 5;
	}
	else if($motif_abbreviation === "jelly") {
		$motif_id = 6;
	}
	else if($motif_abbreviation === "ubi") {
		$motif_id = 7;
	}
	else if($motif_abbreviation === "plait") {
		$motif_id = 8;
	}
	else if($motif_abbreviation === "rossman") {
		$motif_id = 9;
	}
	else if($motif_abbreviation === "tim") {
		$motif_id = 10;
	}
	else {
		$motif_id = -1;
	}
	return $motif_id;
}

function get_motif_query_string($motif_id) {   
	
    $res = "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_nm_chaintomotif c2m
				   INNER JOIN plcc_chain c
				   ON c2m.chain_id = c.chain_id				   
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE c2m.motif_id = " . $motif_id . "";
   return $res;
}

if(isset($_GET["next"])) {
	if(is_numeric($_GET["next"]) && $_GET["next"] >= 0){
		$limit_start = $_GET["next"];
		$keyword = $_SESSION["keyword"];
		$pdbid = $_SESSION["pdbid"];
		$title = $_SESSION["title"];
		$hasligand = $_SESSION["hasligand"];
		$ligandname = $_SESSION["ligandname"];
		$molecule = $_SESSION["molecule"];
		$logic = $_SESSION["logic"];
		$linnotalphaadj = $_SESSION["linnotalphaadj"];
		$linnotalphared = $_SESSION["linnotalphared"];
		$linnotalphaseq = $_SESSION["linnotalphaseq"];
		$linnotalphakey = $_SESSION["linnotalphakey"];
		$linnotbetaadj = $_SESSION["linnotbetaadj"];
		$linnotbetared = $_SESSION["linnotbetared"];
		$linnotbetaseq = $_SESSION["linnotbetaseq"];
		$linnotbetakey = $_SESSION["linnotbetakey"];
		$linnotalbeadj = $_SESSION["linnotalbeadj"];
		$linnotalbered = $_SESSION["linnotalbered"];
		$linnotalbeseq = $_SESSION["linnotalbeseq"];
		$linnotalbekey = $_SESSION["linnotalbekey"];
		$linnotalphaligadj = $_SESSION["linnotalphaligadj"];
		$linnotalphaligred = $_SESSION["linnotalphaligred"];
		$linnotalphaligseq = $_SESSION["linnotalphaligseq"];
		$linnotalphaligkey = $_SESSION["linnotalphaligkey"];
		$linnotbetaligadj = $_SESSION["linnotbetaligadj"];
		$linnotbetaligred = $_SESSION["linnotbetaligred"];
		$linnotbetaligseq = $_SESSION["linnotbetaligseq"];
		$linnotbetaligkey = $_SESSION["linnotbetaligkey"];
		$linnotalbeligadj = $_SESSION["linnotalbeligadj"];
		$linnotalbeligred = $_SESSION["linnotalbeligred"];
		$linnotalbeligseq = $_SESSION["linnotalbeligseq"];
		$linnotalbeligkey = $_SESSION["linnotalbeligkey"];		
		$motif = $_SESSION["motif"];
		$graphletsimilarity = $_SESSION["graphletsimilarity"];
		
	} else {
		$limit_start = 0;
		session_unset();
	}
} else {
	$limit_start = 0;
	session_unset();
}

// give the selected chains to JS
if(isset($_SESSION["chains"])){
	echo '<script type="text/javascript">';
	echo ('window.checkedChains = new Array(\''. implode('\',\'', $_SESSION["chains"]) .'\');');
	echo '</script>';
}

// check if parameters are set. If so, set the associated variable with the value
if(isset($_GET)) {
    if(isset($_GET["keyword"])) {
		$keyword = $_GET["keyword"];
		$_SESSION["keyword"] = $keyword;
	} 
    
	if(isset($_GET["pdbid"])) {
		$pdbid = $_GET["pdbid"];
		$_SESSION["pdbid"] = $pdbid;
	}
	
    if(isset($_GET["title"])) {
		$title = $_GET["title"];
		$_SESSION["title"] = $title;
	}
	
    if(isset($_GET["hasligand"])) {
		$hasligand = $_GET["hasligand"];
		$_SESSION["hasligand"] = $hasligand;
	}
	
    if(isset($_GET["ligandname"])) {
		$ligandname = $_GET["ligandname"];
		$_SESSION["ligandname"] = $ligandname;
	}
	
    if(isset($_GET["molecule"])) {
		$molecule = $_GET["molecule"];
		$_SESSION["molecule"] = $molecule;
	}
	
    if(isset($_GET["logic"])) {
		$logic = $_GET["logic"];
		$_SESSION["logic"] = $logic;
	}
	
	if(isset($_GET["linnotalphaadj"])) {
		$linnotalphaadj = $_GET["linnotalphaadj"];
		$_SESSION["linnotalphaadj"] = $linnotalphaadj;
	}
	if(isset($_GET["linnotalphared"])) {
		$linnotalphared = $_GET["linnotalphared"];
		$_SESSION["linnotalphared"] = $linnotalphared;
	}
	if(isset($_GET["linnotalphaseq"])) {
		$linnotalphaseq = $_GET["linnotalphaseq"];
		$_SESSION["linnotalphaseq"] = $linnotalphaseq;
	}
	if(isset($_GET["linnotalphakey"])) {
		$linnotalphakey = $_GET["linnotalphakey"];
		$_SESSION["linnotalphakey"] = $linnotalphakey;
	}
	
	if(isset($_GET["linnotbetaadj"])) {
		$linnotbetaadj = $_GET["linnotbetaadj"];
		$_SESSION["linnotbetaadj"] = $linnotbetaadj;
	}
	if(isset($_GET["linnotbetared"])) {
		$linnotbetared = $_GET["linnotbetared"];
		$_SESSION["linnotbetared"] = $linnotbetared;
	}
	if(isset($_GET["linnotbetaseq"])) {
		$linnotbetaseq = $_GET["linnotbetaseq"];
		$_SESSION["linnotbetaseq"] = $linnotbetaseq;
	}
	if(isset($_GET["linnotbetakey"])) {
		$linnotbetakey = $_GET["linnotbetakey"];
		$_SESSION["linnotbetakey"] = $linnotbetakey;
	}
	
	if(isset($_GET["linnotalbeadj"])) {
		$linnotalbeadj = $_GET["linnotalbeadj"];
		$_SESSION["linnotalbeadj"] = $linnotalbeadj;
	}
	if(isset($_GET["linnotalbered"])) {
		$linnotalbered = $_GET["linnotalbered"];
		$_SESSION["linnotalbered"] = $linnotalbered;
	}
	if(isset($_GET["linnotalbeseq"])) {
		$linnotalbeseq = $_GET["linnotalbeseq"];
		$_SESSION["linnotalbeseq"] = $linnotalbeseq;
	}
	if(isset($_GET["linnotalbekey"])) {
		$linnotalbekey = $_GET["linnotalbekey"];
		$_SESSION["linnotalbekey"] = $linnotalbekey;
	}
	
	// ligand versions follow:
	
	
	if(isset($_GET["linnotalphaligadj"])) {
		$linnotalphaligadj = $_GET["linnotalphaligadj"];
		$_SESSION["linnotalphaligadj"] = $linnotalphaligadj;
	}
	if(isset($_GET["linnotalphaligred"])) {
		$linnotalphaligred = $_GET["linnotalphaligred"];
		$_SESSION["linnotalphaligred"] = $linnotalphaligred;
	}
	if(isset($_GET["linnotalphaligseq"])) {
		$linnotalphaligseq = $_GET["linnotalphaligseq"];
		$_SESSION["linnotalphaligseq"] = $linnotalphaligseq;
	}
	if(isset($_GET["linnotalphaligkey"])) {
		$linnotalphaligkey = $_GET["linnotalphaligkey"];
		$_SESSION["linnotalphaligkey"] = $linnotalphaligkey;
	}
	
	if(isset($_GET["linnotbetaligadj"])) {
		$linnotbetaligadj = $_GET["linnotbetaligadj"];
		$_SESSION["linnotbetaligadj"] = $linnotbetaligadj;
	}
	if(isset($_GET["linnotbetaligred"])) {
		$linnotbetaligred = $_GET["linnotbetaligred"];
		$_SESSION["linnotbetaligred"] = $linnotbetaligred;
	}
	if(isset($_GET["linnotbetaligseq"])) {
		$linnotbetaligseq = $_GET["linnotbetaligseq"];
		$_SESSION["linnotbetaligseq"] = $linnotbetaligseq;
	}
	if(isset($_GET["linnotbetaligkey"])) {
		$linnotbetaligkey = $_GET["linnotbetaligkey"];
		$_SESSION["linnotbetaligkey"] = $linnotbetaligkey;
	}
	
	if(isset($_GET["linnotalbeligadj"])) {
		$linnotalbeligadj = $_GET["linnotalbeligadj"];
		$_SESSION["linnotalbeligadj"] = $linnotalbeligadj;
	}
	if(isset($_GET["linnotalbeligred"])) {
		$linnotalbeligred = $_GET["linnotalbeligred"];
		$_SESSION["linnotalbeligred"] = $linnotalbeligred;
	}
	if(isset($_GET["linnotalbeligseq"])) {
		$linnotalbeligseq = $_GET["linnotalbeligseq"];
		$_SESSION["linnotalbeligseq"] = $linnotalbeligseq;
	}
	if(isset($_GET["linnotalbeligkey"])) {
		$linnotalbeligkey = $_GET["linnotalbeligkey"];
		$_SESSION["linnotalbeligkey"] = $linnotalbeligkey;
	}
	
	// motif search
	if(isset($_GET["motif"])) {
		$motif = $_GET["motif"];
		$_SESSION["motif"] = $motif;
	}
	
	// graphlet similarity search
	if(isset($_GET["graphletsimilarity"])) {
		$graphletsimilarity = $_GET["graphletsimilarity"];
		$_SESSION["graphletsimilarity"] = $graphletsimilarity;
	}
	
	
	
	
    // if(isset($_GET["proteincomplexes"])) {$proteincomplexes = $_GET["proteincomplexes"];};
} else {
	// if nothing is set or the query is too short...
	$tableString = "Sorry. Your search term is too short. <br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
	exit;
}

if (($none_set == true)) { // #TODO redefine this check...
	$tableString = "Sorry. Your search term is too short.<br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
	exit;
} else {
	// establish database connection
	$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	$db = pg_connect($conn_string);
	
	// later, if no query is set before, there will be no CONCAT/UNION
	$firstQuerySet = false;
	$query = "SELECT chain_id, chain_name, pdb_id, resolution, title, header
			  FROM ( ";
	
	// following: the queries for each set parameter
	if (isset($keyword) && $keyword != "") {
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE p.pdb_id LIKE '%".$keyword."%' 
				   OR p.header LIKE '%".strtoupper($keyword)."%'";
		$firstQuerySet = true; };
	
	if (isset($pdbid) && $pdbid != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE p.pdb_id = '".$pdbid."'";
		$firstQuerySet = true; };
	
	if (isset($title) && $title != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE p.title LIKE '%".strtoupper($title)."%'";
		$firstQuerySet = true; };
	
	if (isset($hasligand) && $hasligand != "null") {
		if($hasligand == "1") {
			$operator = " NOT ";
		} else if ($hasligand == "0"){
			$operator = " ";
		}
		
		if($firstQuerySet) { $query .= " UNION "; }
			$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
					FROM plcc_nm_ligandtochain l
					INNER JOIN plcc_chain c ON l.ligandtochain_chainid = c.chain_id
					INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id 
					WHERE l.ligandtochain_ligandname3 IS".$operator."NULL"; 
			$firstQuerySet = true; 
		 
	};

	if (isset($ligandname) && $ligandname != "") { 
		if($firstQuerySet) { $query .= " UNION "; }
		/*$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_nm_ligandtochain l
				   INNER JOIN plcc_chain c ON l.ligandtochain_chainid = c.chain_id
				   INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id 
				   WHERE l.ligandtochain_ligandname3 = '".$ligandname."'
				   ORDER BY pdb_id, chain_name"; */
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_nm_ligandtochain l2c ON c.chain_id = l2c.ligandtochain_chainid
				   INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id 
				   WHERE l2c.ligandtochain_ligandname3 = '".$ligandname."'"; 
		$firstQuerySet = true; 	
	};

	if (isset($molecule) && $molecule != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE c.mol_name LIKE '%".$molecule."%'";
		$firstQuerySet = true; 
	};
	
	// ----- linnot alpha queries -----
	
	if (isset($linnotalphaadj) && $linnotalphaadj != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("adj", $linnotalphaadj, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalphared) && $linnotalphared != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("red", $linnotalphared, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalphaseq) && $linnotalphaseq != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("seq", $linnotalphaseq, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalphakey) && $linnotalphakey != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("key", $linnotalphakey, 1, true);
		$firstQuerySet = true; 
	};
	
	// ----- linnot beta queries -----
	
	if (isset($linnotbetaadj) && $linnotbetaadj != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("adj", $linnotbetaadj, 2, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetared) && $linnotbetared != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("red", $linnotbetared, 2, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetaseq) && $linnotbetaseq != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("seq", $linnotbetaseq, 2, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetakey) && $linnotbetakey != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("key", $linnotbetakey, 2, true);
		$firstQuerySet = true; 
	};
	
	// ----- linnot albe queries -----
	
	if (isset($linnotalbeadj) && $linnotalbeadj != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("adj", $linnotalbeadj, 3, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbered) && $linnotalbered != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("red", $linnotalbered, 3, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbeseq) && $linnotalbeseq != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("seq", $linnotalbeseq, 3, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbekey) && $linnotalbekey != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("key", $linnotalbekey, 3, true);
		$firstQuerySet = true; 
	};
	
	// ----- linnot alphalig queries -----
	
	if (isset($linnotalphaligadj) && $linnotalphaligadj != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("adj", $linnotalphaligadj, 4, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalphaligred) && $linnotalphaligred != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("red", $linnotalphaligred, 4, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalphaligseq) && $linnotalphaligseq != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("seq", $linnotalphaligseq, 4, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalphaligkey) && $linnotalphaligkey != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("key", $linnotalphaligkey, 4, true);
		$firstQuerySet = true; 
	};
	
	// ----- linnot betalig queries -----
	
	if (isset($linnotbetaligadj) && $linnotbetaligadj != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("adj", $linnotbetaligadj, 5, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetaligred) && $linnotbetaligred != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("red", $linnotbetaligred, 5, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetaligseq) && $linnotbetaligseq != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("seq", $linnotbetaligseq, 5, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetaligkey) && $linnotbetaligkey != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("key", $linnotbetaligkey, 5, true);
		$firstQuerySet = true; 
	};
	
	// ----- linnot albelig queries -----
	
	if (isset($linnotalbeligadj) && $linnotalbeligadj != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("adj", $linnotalbeligadj, 6, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbeligred) && $linnotalbeligred != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("red", $linnotalbeligred, 6, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbeligseq) && $linnotalbeligseq != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("seq", $linnotalbeligseq, 6, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbeligkey) && $linnotalbeligkey != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("key", $linnotalbeligkey, 6, true);
		$firstQuerySet = true; 
	};
	
	// motif search	
	if (isset($motif) && $motif != ""){
	
		$motif_id = get_motif_id($motif);
		if($motif_id > 0) {
			if($firstQuerySet) { $query .= " UNION "; }
			$query .= get_motif_query_string($motif_id);
			$firstQuerySet = true; 			
		} 
	};
	
	//graphletsimilarity
	if (isset($graphletsimilarity) && $graphletsimilarity != ""){
	
	    $pdbchainlist = array();		
		// TODO: fill array with similar chains based on graphlet distance here
		
		if(count($pdbchainlist) > 0) {
		  if($firstQuerySet) { $query .= " UNION "; }
			$query .= get_multiple_PDB_select_query($pdbchainlist);
			$firstQuerySet = true;		  
		}		
	};
	
	
	
	
	
	// ---------------------------------------------------- end of dynamic query building ----------------------------------
	
	
	$q_limit = 25;
	
	$count_query = $query . " ) results";
	$query .= " ) results
			  ORDER BY pdb_id, chain_name";

	$count_query = str_replace("chain_id, chain_name, pdb_id, resolution, title, header", "COUNT(*)", $count_query);
	$query .= " LIMIT " . $q_limit . " OFFSET ".$limit_start;
  
	$result = pg_query($db, $query); // or die($query . ' -> Query failed: ' . pg_last_error());
	$count_result = pg_query($db, $count_query); // or die($query . ' -> Query failed: ' . pg_last_error());

	$row_count = pg_fetch_array($count_result, NULL, PGSQL_ASSOC);
	$row_count = $row_count["count"];

	// this counter is used to display alternating table colors
	$counter = 0;
	$createdHeadlines = Array();
	$numberOfChains = 0;

	// begin to create pager
	$tableString = '<div id="pager">';
	if($limit_start >= $q_limit) {
		$tableString .= '<a class="changepage" href="?next='.($limit_start - $q_limit).'"><< previous </a>  ';
	}

	$tableString .= '-- Showing results '.$limit_start.' to ';
	
	if($limit_start + $q_limit > $row_count){
		$tableString .= $row_count . ' (of '.$row_count.') -- ';
	} else {
		$tableString .= ($limit_start + $q_limit) . ' (of '.$row_count.') -- ';
	}
	
	if(($limit_start + $q_limit) < $row_count){
		$tableString .= '<a class="changepage" href="?next='.($limit_start + $q_limit).'"> next >></a>';
	}
	$tableString .= '</div>';
	// EOPager

	while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		// set protein/chain information for readability		
		$pdb_id =  $arr['pdb_id'];
		$chain_name = $arr['chain_name'];
		$resolution = $arr['resolution'];
		$title = $arr["title"];
		$header = $arr["header"];
		$cathlink = get_cath_link($pdb_id, $chain_name);
		
		// provides alternating blue/white tables
		if ($counter % 2 == 0){
			$class = "Orange";	// the CSS class is still called orange...
		} else {
			$class = "White";
		}
		
		// if the headline of the PDB-ID is NOT created yet...
		if(!in_array($pdb_id, $createdHeadlines)){
			$tableString .=	 '<div class="results results'.$class.'">					
							<div class="resultsHeader resultsHeader'.$class.'">
								<div class="resultsId">'.$pdb_id.'</div>
								<div class="resultsRes">Resolution: '.$resolution.' &Aring;</div>
								<div class="resultsLink"><a href="http://www.rcsb.org/pdb/explore/explore.do?structureId='.$pdb_id.'" target="_blank">[PDB]</a>
													<a href="http://www.rcsb.org/pdb/download/downloadFile.do?fileFormat=FASTA&compression=NO&structureId='.$pdb_id.'" target="_blank">[FASTA]</a></div>
							</div>
							<div class="resultsBody1">
								<div class="resultsTitle">Title</div>
								<div class="resultsTitlePDB">' .ucfirst(strtolower($title)). '</div>
							</div>
							<div class="resultsBody2">
								<div class="resultsClass">Classification</div>
								<div class="resultsClassPDB">'.ucfirst(strtolower($header)).'</div>
							</div>';
			// now the headline is created, so push the PDB-ID to the createdHeadlines array
			array_push($createdHeadlines, $pdb_id);
			$counter++;
		}
		// if the headline is already there..
		$tableString .= '	<div class="resultsFooter">
						<div class="resultsChain">Chain '.$chain_name.'</div>
						<div class="resultsChainNum"><input type=checkbox id="'.$pdb_id . $chain_name. '" class="chainCheckBox" value="'.$pdb_id . $chain_name.'"/>'.$pdb_id . $chain_name.'</div>
						<div class="resultsCATH"><a href="'.$cathlink.'" target="_blank">CATH</a></div>
					</div>';
		
		$numberOfChains++;
		
	}
	$tableString .= ' </div>';
	pg_free_result($result); // clean memory
	pg_close($db); // close connection
}
//EOF
?>
