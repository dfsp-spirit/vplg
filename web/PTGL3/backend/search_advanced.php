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
	    $query .= " OR ";
	  }
	}
	
	$query .= " )";
	
	return $query;
}

function get_multiple_PDB_select_query_in_order($pdb_chain_list_in_order) {

   $valid_pdb_ids = array();
   $valid_pdb_chains = array();
   foreach($pdb_chain_list_in_order as $pdbchain) {
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
   
   $first_pdb_id = $valid_pdb_ids[0];
   $first_chain_id = $valid_pdb_chains[0];
   $query = "SELECT 0 as fake_order, c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE ( p.pdb_id = '" . $first_pdb_id . "' AND c.chain_name = '" . $first_chain_id . "' ) ";
				   
				   
    for($i = 1; $i < count($valid_pdb_ids); $i++) {
      $pdb_id = $valid_pdb_ids[$i];
      $chain_id = $valid_pdb_chains[$i];
      $query .= " UNION SELECT " . $i . " as fake_order, c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE ( p.pdb_id = '" . $pdb_id . "' AND c.chain_name = '" . $chain_id . "' ) ";
    }
    
    //$query .= " ORDER BY order";      // the order has to be added at the very end (this may get more UNIONs from other parts!)
    
    return $query;
}


$debug_msg = "";
$result_comments = array();

ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	ini_set('log_errors', TRUE);
	error_reporting(E_ALL);


if(isset($_GET["next"])) {
	if(is_numeric($_GET["next"]) && $_GET["next"] >= 0){
		$limit_start = $_GET["next"];
		$st = $_SESSION["st"];
                $keyword = $_SESSION["keyword"];
		$pdbid = $_SESSION["pdbid"];
		$title = $_SESSION["title"];
		$hasligand = $_SESSION["hasligand"];
		$ligandname = $_SESSION["ligandname"];
		$molecule = $_SESSION["molecule"];
		$logic = $_SESSION["logic"];
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

$result_set_has_fake_order_field = FALSE;

if(isset($_GET)) {
    if(isset($_GET["st"])) {
		$st = $_GET["st"];
		$_SESSION["st"] = $st;
	} 
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
	
    // if(isset($_GET["proteincomplexes"])) {$proteincomplexes = $_GET["proteincomplexes"];};
} else {
	// if nothing is set or the query is too short...
	$tableString = "Sorry. Your search term is too short. <br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
	exit;
}


// establish database connection
$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
$db = pg_connect($conn_string);

// later, if no query is set before, there will be no CONCAT/UNION
$firstQuerySet = false;
$list_of_search_types = array();
$query_parameters = array();

$query = "SELECT chain_id, chain_name, pdb_id, resolution, title, header
          FROM ( ";

$param_value = 1;
// following: the queries for each set parameter
if (isset($keyword) && $keyword != "") {
        array_push($list_of_search_types, "keyword search");
        $query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
                           FROM plcc_chain c
                           INNER JOIN plcc_protein p
                           ON p.pdb_id = c.pdb_id 
                           WHERE p.pdb_id LIKE $" .$param_value++. "
                           OR p.header LIKE $". $param_value++;
        array_push($query_parameters, "%".$keyword."%");
        array_push($query_parameters, "%".strtoupper($keyword)."%");
        $firstQuerySet = true; };

if (isset($pdbid) && $pdbid != ""){
        array_push($list_of_search_types, "PDB ID search");
        if($firstQuerySet) { $query .= " INTERSECT "; }
        $query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
                           FROM plcc_chain c
                           INNER JOIN plcc_protein p
                           ON p.pdb_id = c.pdb_id 
                           WHERE p.pdb_id = $". $param_value++;
        array_push($query_parameters, $pdbid);
        $firstQuerySet = true; };

if (isset($title) && $title != ""){
        array_push($list_of_search_types, "title search");
        if($firstQuerySet) { $query .= " INTERSECT "; }
        $query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
                           FROM plcc_chain c
                           INNER JOIN plcc_protein p
                           ON p.pdb_id = c.pdb_id 
                           WHERE p.title LIKE $". $param_value++;
        array_push($query_parameters, "%".strtoupper($title)."%");
        $firstQuerySet = true; };

if (isset($hasligand) && $hasligand != "null") {
        array_push($list_of_search_types, "hasligand search");
		if($firstQuerySet) { $query .= " INTERSECT "; }
        if($hasligand == "1") {
				$query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
                                FROM plcc_nm_ligandtochain l
                                INNER JOIN plcc_chain c ON l.ligandtochain_chainid = c.chain_id
                                INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id 
                                WHERE l.ligandtochain_ligandname3 IS NOT NULL"; 
				
        } else if ($hasligand == "0"){
                $query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
								FROM plcc_chain c
								INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id
								WHERE c.chain_id NOT IN (SELECT ligandtochain_chainid FROM plcc_nm_ligandtochain)"; 
		}
		$firstQuerySet = true; 
};

if (isset($ligandname) && $ligandname != "") { 
        array_push($list_of_search_types, "ligand name search");
        if($firstQuerySet) { $query .= " INTERSECT "; }
        $query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
                           FROM plcc_chain c
                           INNER JOIN plcc_nm_ligandtochain l2c ON c.chain_id = l2c.ligandtochain_chainid
                           INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id 
                           WHERE trim(both ' ' from l2c.ligandtochain_ligandname3) = $". $param_value++;
        array_push($query_parameters, strtoupper($ligandname));
        $firstQuerySet = true; 	
};

if (isset($molecule) && $molecule != ""){
        array_push($list_of_search_types, "molecule search");
        if($firstQuerySet) { $query .= " INTERSECT "; }
        $query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
                           FROM plcc_chain c
                           INNER JOIN plcc_protein p
                           ON p.pdb_id = c.pdb_id 
                           WHERE c.mol_name LIKE $". $param_value++;
        array_push($query_parameters, "%".$molecule."%");
        $firstQuerySet = true; 
};


// ---------------------------------------------------- end of dynamic query building ----------------------------------


$q_limit = 25;

$count_query = $query . " ) results";
$query .= " ) results
            ORDER BY pdb_id, chain_name";
$count_query = str_replace("chain_id, chain_name, pdb_id, resolution, title, header", "COUNT(*)", $count_query);


//print "Count query is '$count_query'";

$query .= " LIMIT " . $q_limit . " OFFSET ".$limit_start;

pg_query($db, "DEALLOCATE ALL"); 
pg_prepare($db, "searchadvanced", $query);
$result = pg_execute($db, "searchadvanced", $query_parameters);  

//if(! $result) {
//  die("FCK: '$query'");
//}
pg_prepare($db, "searchadvancedCount", $count_query);
$count_result = pg_execute($db, "searchadvancedCount", $query_parameters);

$row_count = pg_fetch_array($count_result, NULL, PGSQL_ASSOC);
if(isset($row_count["count"])) {
  $row_count = $row_count["count"];
} else {
  $row_count = 0;
}

// this counter is used to display alternating table colors
$counter = 0;
$createdHeadlines = Array();
$numberOfChains = 0;

if($row_count != 0){
// begin to create pager
$tableString = '<div id="pager">';
if($limit_start >= $q_limit) {
        $tableString .= '<a class="changepage" href="?next='.($limit_start - $q_limit).'"><< previous </a>  ';
}

$tableString .= '-- Showing result chains '.($row_count == 0 ? 0 : $limit_start + 1).' to ';

if($limit_start + $q_limit > $row_count){
        $tableString .= $row_count . ' (of ' . $row_count . ' total) -- ';
} else {
        $tableString .= ($limit_start + $q_limit) . ' (of '.$row_count.' total) -- ';
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
        $comment = "";


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
                                        <div class="resultsComment">' . $comment . '</div>
                                </div>';

        $numberOfChains++;

}
$tableString .= ' </div>';	// the $tableString var is used in the frontend search.php page to print results

} else {
    $tableString .= "<h3>Unfortunately there are no search results for your query.</h3>";
    $tableString .= "Please <a href='index.php' title='PTGL'>go back</a> and try an other query.";
}

pg_free_result($result); // clean memory
pg_close($db); // close connection


//EOF
?>
