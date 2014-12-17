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


$debug_msg = "";
$result_comments = array();


if(isset($_GET["next"])) {
	if(is_numeric($_GET["next"]) && $_GET["next"] >= 0){
		$limit_start = $_GET["next"];
		$st = $_SESSION["st"];		
		$motif = $_SESSION["motif"];
		
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

	// motif search
	if(isset($_GET["motif"])) {
		$motif = $_GET["motif"];
		$_SESSION["motif"] = $motif;
	}
	
    // if(isset($_GET["proteincomplexes"])) {$proteincomplexes = $_GET["proteincomplexes"];};
} else {
	// if nothing is set or the query is too short...
	$tableString = "Sorry. Your search term is too short. <br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
	exit;
}

$list_of_search_types = array();


// establish database connection
$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
$db = pg_connect($conn_string);

// later, if no query is set before, there will be no CONCAT/UNION

if($result_set_has_fake_order_field) {
    $query = "SELECT " . $fake_order_field_name . ", chain_id, chain_name, pdb_id, resolution, title, header
                  FROM ( ";
}
else {
    $query = "SELECT chain_id, chain_name, pdb_id, resolution, title, header
                  FROM ( ";
}

// following: the queries for each set parameter

// motif search	
if (isset($motif) && $motif != ""){
        array_push($list_of_search_types, "motif search");
        $motif_id = get_motif_id($motif);
        if($motif_id > 0) {
                $query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
                            FROM plcc_nm_chaintomotif c2m
                            INNER JOIN plcc_chain c
                            ON c2m.chain_id = c.chain_id				   
                            INNER JOIN plcc_protein p
                            ON p.pdb_id = c.pdb_id 
                            WHERE c2m.motif_id = $1";	
        } 
};


// ---------------------------------------------------- end of dynamic query building ----------------------------------


$q_limit = 25;

$count_query = $query . " ) results";

if($result_set_has_fake_order_field) {
  $query .= " ) results
                  ORDER BY " . $fake_order_field_name . " ASC";
}
else {
  $query .= " ) results
                  ORDER BY pdb_id, chain_name";
}

$count_query = str_replace("chain_id, chain_name, pdb_id, resolution, title, header", "COUNT(*)", $count_query);
//print "Count query is '$count_query'";

$query .= " LIMIT " . $q_limit . " OFFSET ".$limit_start;
$motif_id = get_motif_id($motif);

pg_query($db, "DEALLOCATE ALL"); 
pg_prepare($db, "searchmotif", $query);
$result = pg_execute($db, "searchmotif", array($motif_id)); 

pg_prepare($db, "searchmotifCount", $count_query);
$count_result = pg_execute($db, "searchmotifCount", array($motif_id)); 

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

// begin to create pager
$tableString = '<div id="pager">';
if($limit_start >= $q_limit) {
        $tableString .= '<a class="changepage" href="?motif=' . $motif . '&next='.($limit_start - $q_limit).'"><< previous </a>  ';
}

$tableString .= '-- Showing result chains '.$limit_start.' to ';

if($limit_start + $q_limit > $row_count){
        $tableString .= $row_count . ' (of ' . $row_count . ' total) -- ';
} else {
        $tableString .= ($limit_start + $q_limit) . ' (of '.$row_count.' total) -- ';
}

if(($limit_start + $q_limit) < $row_count){
        $tableString .= '<a class="changepage" href="?motif=' . $motif . '&next='.($limit_start + $q_limit).'"> next >></a>';
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
pg_free_result($result); // clean memory
pg_close($db); // close connection

//EOF
?>
