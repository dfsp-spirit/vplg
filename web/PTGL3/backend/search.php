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
 */

ini_set('display_errors', 0);
ini_set('display_startup_errors', 0);
ini_set('log_errors', TRUE);
error_reporting(E_ERROR);

// get config values
$CONFIG			= include('./backend/config.php'); 
$DB_HOST		= $CONFIG['host'];
$DB_PORT		= $CONFIG['port'];
$DB_NAME		= $CONFIG['db'];
$DB_USER		= $CONFIG['user'];
$DB_PASSWORD	= $CONFIG['pw'];
$BUILD_FILE_PATH	= $CONFIG['build_file_path'];
$IMG_ROOT_PATH		= $CONFIG['img_root_path'];
$DEBUG			= $CONFIG['debug'];

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
if(isset($_POST)) {
    if(isset($_POST["keyword"])) {
		$keyword = $_POST["keyword"];
		$_SESSION["keyword"] = $keyword;
	} 
    
	if(isset($_POST["pdbid"])) {
		$pdbid = $_POST["pdbid"];
		$_SESSION["pdbid"] = $pdbid;
	}
	
    if(isset($_POST["title"])) {
		$title = $_POST["title"];
		$_SESSION["title"] = $title;
	}
	
    if(isset($_POST["hasligand"])) {
		$hasligand = $_POST["hasligand"];
		$_SESSION["hasligand"] = $hasligand;
	}
	
    if(isset($_POST["ligandname"])) {
		$ligandname = $_POST["ligandname"];
		$_SESSION["ligandname"] = $ligandname;
	}
	
    if(isset($_POST["molecule"])) {
		$molecule = $_POST["molecule"];
		$_SESSION["molecule"] = $molecule;
	}
	
    if(isset($_POST["logic"])) {
		$logic = $_POST["logic"];
		$_SESSION["logic"] = $logic;
	}
	
	if(isset($_POST["linnotalphaadj"])) {
		$linnotalphaadj = $_POST["linnotalphaadj"];
		$_SESSION["linnotalphaadj"] = $linnotalphaadj;
	}
	if(isset($_POST["linnotalphared"])) {
		$linnotalphared = $_POST["linnotalphared"];
		$_SESSION["linnotalphared"] = $linnotalphared;
	}
	if(isset($_POST["linnotalphaseq"])) {
		$linnotalphaseq = $_POST["linnotalphaseq"];
		$_SESSION["linnotalphaseq"] = $linnotalphaseq;
	}
	if(isset($_POST["linnotalphakey"])) {
		$linnotalphakey = $_POST["linnotalphakey"];
		$_SESSION["linnotalphakey"] = $linnotalphakey;
	}
	
	if(isset($_POST["linnotbetaadj"])) {
		$linnotbetaadj = $_POST["linnotbetaadj"];
		$_SESSION["linnotbetaadj"] = $linnotbetaadj;
	}
	if(isset($_POST["linnotbetared"])) {
		$linnotbetared = $_POST["linnotbetared"];
		$_SESSION["linnotbetared"] = $linnotbetared;
	}
	if(isset($_POST["linnotbetaseq"])) {
		$linnotbetaseq = $_POST["linnotbetaseq"];
		$_SESSION["linnotbetaseq"] = $linnotbetaseq;
	}
	if(isset($_POST["linnotbetakey"])) {
		$linnotbetakey = $_POST["linnotbetakey"];
		$_SESSION["linnotbetakey"] = $linnotbetakey;
	}
	
	if(isset($_POST["linnotalbeadj"])) {
		$linnotalbeadj = $_POST["linnotalbeadj"];
		$_SESSION["linnotalbeadj"] = $linnotalbeadj;
	}
	if(isset($_POST["linnotalbered"])) {
		$linnotalbered = $_POST["linnotalbered"];
		$_SESSION["linnotalbered"] = $linnotalbered;
	}
	if(isset($_POST["linnotalbeseq"])) {
		$linnotalbeseq = $_POST["linnotalbeseq"];
		$_SESSION["linnotalbeseq"] = $linnotalbeseq;
	}
	if(isset($_POST["linnotalbekey"])) {
		$linnotalbekey = $_POST["linnotalbekey"];
		$_SESSION["linnotalbekey"] = $linnotalbekey;
	}
	
	// ligand versions follow:
	
	
	if(isset($_POST["linnotalphaligadj"])) {
		$linnotalphaligadj = $_POST["linnotalphaligadj"];
		$_SESSION["linnotalphaligadj"] = $linnotalphaligadj;
	}
	if(isset($_POST["linnotalphaligred"])) {
		$linnotalphaligred = $_POST["linnotalphaligred"];
		$_SESSION["linnotalphaligred"] = $linnotalphaligred;
	}
	if(isset($_POST["linnotalphaligseq"])) {
		$linnotalphaligseq = $_POST["linnotalphaligseq"];
		$_SESSION["linnotalphaligseq"] = $linnotalphaligseq;
	}
	if(isset($_POST["linnotalphaligkey"])) {
		$linnotalphaligkey = $_POST["linnotalphaligkey"];
		$_SESSION["linnotalphaligkey"] = $linnotalphaligkey;
	}
	
	if(isset($_POST["linnotbetaligadj"])) {
		$linnotbetaligadj = $_POST["linnotbetaligadj"];
		$_SESSION["linnotbetaligadj"] = $linnotbetaligadj;
	}
	if(isset($_POST["linnotbetaligred"])) {
		$linnotbetaligred = $_POST["linnotbetaligred"];
		$_SESSION["linnotbetaligred"] = $linnotbetaligred;
	}
	if(isset($_POST["linnotbetaligseq"])) {
		$linnotbetaligseq = $_POST["linnotbetaligseq"];
		$_SESSION["linnotbetaligseq"] = $linnotbetaligseq;
	}
	if(isset($_POST["linnotbetaligkey"])) {
		$linnotbetaligkey = $_POST["linnotbetaligkey"];
		$_SESSION["linnotbetaligkey"] = $linnotbetaligkey;
	}
	
	if(isset($_POST["linnotalbeligadj"])) {
		$linnotalbeligadj = $_POST["linnotalbeligadj"];
		$_SESSION["linnotalbeligadj"] = $linnotalbeligadj;
	}
	if(isset($_POST["linnotalbeligred"])) {
		$linnotalbeligred = $_POST["linnotalbeligred"];
		$_SESSION["linnotalbeligred"] = $linnotalbeligred;
	}
	if(isset($_POST["linnotalbeligseq"])) {
		$linnotalbeligseq = $_POST["linnotalbeligseq"];
		$_SESSION["linnotalbeligseq"] = $linnotalbeligseq;
	}
	if(isset($_POST["linnotalbeligkey"])) {
		$linnotalbeligkey = $_POST["linnotalbeligkey"];
		$_SESSION["linnotalbeligkey"] = $linnotalbeligkey;
	}
	
	
    // if(isset($_POST["proteincomplexes"])) {$proteincomplexes = $_POST["proteincomplexes"];};
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
		$query .= get_linnot_query_string("adj", $linnotbetaadj, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetared) && $linnotbetared != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("red", $linnotbetared, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetaseq) && $linnotbetaseq != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("seq", $linnotbetaseq, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetakey) && $linnotbetakey != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("key", $linnotbetakey, 1, true);
		$firstQuerySet = true; 
	};
	
	// ----- linnot albe queries -----
	
	if (isset($linnotalbeadj) && $linnotalbeadj != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("adj", $linnotalbeadj, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbered) && $linnotalbered != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("red", $linnotalbered, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbeseq) && $linnotalbeseq != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("seq", $linnotalbeseq, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbekey) && $linnotalbekey != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("key", $linnotalbekey, 1, true);
		$firstQuerySet = true; 
	};
	
	// ----- linnot alphalig queries -----
	
	if (isset($linnotalphaligadj) && $linnotalphaligadj != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("adj", $linnotalphaligadj, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalphaligred) && $linnotalphaligred != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("red", $linnotalphaligred, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalphaligseq) && $linnotalphaligseq != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("seq", $linnotalphaligseq, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalphaligkey) && $linnotalphaligkey != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("key", $linnotalphaligkey, 1, true);
		$firstQuerySet = true; 
	};
	
	// ----- linnot betalig queries -----
	
	if (isset($linnotbetaligadj) && $linnotbetaligadj != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("adj", $linnotbetaligadj, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetaligred) && $linnotbetaligred != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("red", $linnotbetaligred, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetaligseq) && $linnotbetaligseq != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("seq", $linnotbetaligseq, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotbetaligkey) && $linnotbetaligkey != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("key", $linnotbetaligkey, 1, true);
		$firstQuerySet = true; 
	};
	
	// ----- linnot albelig queries -----
	
	if (isset($linnotalbeligadj) && $linnotalbeligadj != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("adj", $linnotalbeligadj, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbeligred) && $linnotalbeligred != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("red", $linnotalbeligred, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbeligseq) && $linnotalbeligseq != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("seq", $linnotalbeligseq, 1, true);
		$firstQuerySet = true; 
	};
	
	if (isset($linnotalbeligkey) && $linnotalbeligkey != ""){
		if($firstQuerySet) { $query .= " UNION "; }
		$query .= get_linnot_query_string("key", $linnotalbeligkey, 1, true);
		$firstQuerySet = true; 
	};
	
	
	
	$count_query = $query . " ) results";
	$query .= " ) results
			  ORDER BY pdb_id, chain_name";

	$count_query = str_replace("chain_id, chain_name, pdb_id, resolution, title, header", "COUNT(*)", $count_query);
	$query .= " LIMIT 50 OFFSET ".$limit_start;
  
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
	if($limit_start >= 50) {
		$tableString .= '<a class="changepage" href="?next='.($limit_start - 50).'"><< previous </a>  ';
	}

	$tableString .= '-- Showing results '.$limit_start.' to ';
	
	if($limit_start + 50 > $row_count){
		$tableString .= $row_count . ' (of '.$row_count.') -- ';
	} else {
		$tableString .= ($limit_start + 50) . ' (of '.$row_count.') -- ';
	}
	
	if(($limit_start + 50) < $row_count){
		$tableString .= '<a class="changepage" href="?next='.($limit_start + 50).'"> next >></a>';
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
