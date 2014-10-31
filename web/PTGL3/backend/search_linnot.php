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


/**
  * Returns the SQL query string to get the linear notation of the requested type.
  * @param $linnot_type the linear notation type. This is used to create the colum name and has to be 'adj', 'red', 'key' or 'seq'.
  * @param $linnot_query_string the linear notation you are searching for (e.g., '{1h,3hp}'). You can provide a prepared statement template like '$1', but then you should set $bool_enclose_linnot_query_string_in_ticks to false.
  * @param $query_graph_type_int the graph type. 1=alpha, 2=beta, 3=albe, 4=alphalig, 5=betalig, 6=albelig.
  * @param $bool_enclose_linnot_query_string_in_ticks whether whether to enclose the linnot_query_string in ticks ('). The ticks must not be there if this is used in a prepared statement, where $linnot_query_string is set to something like '$3'.
  */
function get_linnot_query_string($linnot_type, $linnot_query_string, $query_graph_type_int, $bool_enclose_linnot_query_string_in_ticks = true) {
    // $linnot_query_string is unused due to parameter for prepared statement
    $bool_enclose_linnot_query_string_in_ticks = false;  //ticks don't work with the prepared statement?? #TimMussGucken
    
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
				   WHERE ( ln.ptgl_linnot_" . $linnot_type . " = " . $tick . "$1" . $tick . " AND pg.graph_type = " . $query_graph_type_int . " )";
				   
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



$debug_msg = "";
$result_comments = array();


if(isset($_GET["next"])) {
	if(is_numeric($_GET["next"]) && $_GET["next"] >= 0){
		$limit_start = $_GET["next"];
		$st = $_SESSION["st"];
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

$result_set_has_fake_order_field = FALSE;

if(isset($_GET)) {
    if(isset($_GET["st"])) {
		$st = $_GET["st"];
		$_SESSION["st"] = $st;
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
	
	
    // if(isset($_GET["proteincomplexes"])) {$proteincomplexes = $_GET["proteincomplexes"];};
} else {
	// if nothing is set or the query is too short...
	$tableString = "Sorry. Your search term is too short. <br>\n";
	$tableString .= '<a href="./index.php">Go back</a> or use the query box in the upper right corner!';
	exit;
}

$query_parameters = array();

// establish database connection
$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
$db = pg_connect($conn_string);


if($result_set_has_fake_order_field) {
    $query = "SELECT " . $fake_order_field_name . ", chain_id, chain_name, pdb_id, resolution, title, header
                  FROM ( ";
}
else {
    $query = "SELECT chain_id, chain_name, pdb_id, resolution, title, header
                  FROM ( ";
}

// ----- linnot alpha queries -----

if (isset($linnotalphaadj) && $linnotalphaadj != ""){
        array_push($query_parameters, $linnotalphaadj);
        $query .= get_linnot_query_string("adj", $linnotalphaadj, 1, true); 
};

if (isset($linnotalphared) && $linnotalphared != ""){
        array_push($query_parameters, $query_parameters);
        $query .= get_linnot_query_string("red", $linnotalphared, 1, true);
};

if (isset($linnotalphaseq) && $linnotalphaseq != ""){
        array_push($query_parameters, $linnotalphaseq);
        $query .= get_linnot_query_string("seq", $linnotalphaseq, 1, true);
};

if (isset($linnotalphakey) && $linnotalphakey != ""){
        array_push($query_parameters, $linnotalphakey);
        $query .= get_linnot_query_string("key", $linnotalphakey, 1, true);
};

// ----- linnot beta queries -----

if (isset($linnotbetaadj) && $linnotbetaadj != ""){
        array_push($query_parameters, $linnotbetaadj);      
        $query .= get_linnot_query_string("adj", $linnotbetaadj, 2, true);        
};

if (isset($linnotbetared) && $linnotbetared != ""){
        array_push($query_parameters, $linnotbetared);
        $query .= get_linnot_query_string("red", $linnotbetared, 2, true);
        
};

if (isset($linnotbetaseq) && $linnotbetaseq != ""){
        array_push($query_parameters, $linnotbetaseq);
        $query .= get_linnot_query_string("seq", $linnotbetaseq, 2, true);
        
};

if (isset($linnotbetakey) && $linnotbetakey != ""){
        array_push($query_parameters, $linnotbetakey);
        $query .= get_linnot_query_string("key", $linnotbetakey, 2, true);
};

// ----- linnot albe queries -----

if (isset($linnotalbeadj) && $linnotalbeadj != ""){
        array_push($query_parameters, $linnotalbeadj);
        $query .= get_linnot_query_string("adj", $linnotalbeadj, 3, true);     
};

if (isset($linnotalbered) && $linnotalbered != ""){
        array_push($query_parameters, $linnotalbered);
        $query .= get_linnot_query_string("red", $linnotalbered, 3, true);
        
};

if (isset($linnotalbeseq) && $linnotalbeseq != ""){
        array_push($query_parameters, $linnotalbeseq);
        $query .= get_linnot_query_string("seq", $linnotalbeseq, 3, true);
};

if (isset($linnotalbekey) && $linnotalbekey != ""){
        array_push($query_parameters, $linnotalbekey);   
        $query .= get_linnot_query_string("key", $linnotalbekey, 3, true);   
};

// ----- linnot alphalig queries -----

if (isset($linnotalphaligadj) && $linnotalphaligadj != ""){
        array_push($query_parameters, $linnotalphaligadj);       
        $query .= get_linnot_query_string("adj", $linnotalphaligadj, 4, true);       
};

if (isset($linnotalphaligred) && $linnotalphaligred != ""){
        array_push($query_parameters, $linnotalphaligred);        
        $query .= get_linnot_query_string("red", $linnotalphaligred, 4, true);      
};

if (isset($linnotalphaligseq) && $linnotalphaligseq != ""){
        array_push($query_parameters, $linnotalphaligseq);      
        $query .= get_linnot_query_string("seq", $linnotalphaligseq, 4, true);      
};

if (isset($linnotalphaligkey) && $linnotalphaligkey != ""){
        array_push($query_parameters, $linnotalphaligkey);       
        $query .= get_linnot_query_string("key", $linnotalphaligkey, 4, true);       
};

// ----- linnot betalig queries -----

if (isset($linnotbetaligadj) && $linnotbetaligadj != ""){
        array_push($query_parameters, $linnotbetaligadj);        
        $query .= get_linnot_query_string("adj", $linnotbetaligadj, 5, true);        
};

if (isset($linnotbetaligred) && $linnotbetaligred != ""){
        array_push($query_parameters, $linnotbetaligred);        
        $query .= get_linnot_query_string("red", $linnotbetaligred, 5, true);        
};

if (isset($linnotbetaligseq) && $linnotbetaligseq != ""){
        array_push($query_parameters, $linnotbetaligseq);     
        $query .= get_linnot_query_string("seq", $linnotbetaligseq, 5, true);       
};

if (isset($linnotbetaligkey) && $linnotbetaligkey != ""){
        array_push($query_parameters, $linnotbetaligkey);        
        $query .= get_linnot_query_string("key", $linnotbetaligkey, 5, true);
};

// ----- linnot albelig queries -----

if (isset($linnotalbeligadj) && $linnotalbeligadj != ""){
        array_push($query_parameters, $linnotalbeligadj);      
        $query .= get_linnot_query_string("adj", $linnotalbeligadj, 6, true);
};

if (isset($linnotalbeligred) && $linnotalbeligred != ""){
        array_push($query_parameters, $linnotalbeligred);      
        $query .= get_linnot_query_string("red", $linnotalbeligred, 6, true);       
};

if (isset($linnotalbeligseq) && $linnotalbeligseq != ""){
        array_push($query_parameters, $linnotalbeligseq);     
        $query .= get_linnot_query_string("seq", $linnotalbeligseq, 6, true);      
};

if (isset($linnotalbeligkey) && $linnotalbeligkey != ""){
        array_push($query_parameters, $linnotalbeligkey);      
        $query .= get_linnot_query_string("key", $linnotalbeligkey, 6, true);      
};

// ---------------------------------------------------- end of dynamic query building ----------------------------------


$q_limit = 25;

$count_query = $query . " ) results";


$query .= " ) results
              ORDER BY pdb_id, chain_name";

$count_query = str_replace("chain_id, chain_name, pdb_id, resolution, title, header", "COUNT(*)", $count_query);

$query .= " LIMIT " . $q_limit . " OFFSET ".$limit_start;

pg_query($db, "DEALLOCATE ALL"); 
pg_prepare($db, "searchlinnot", $query);
$result = pg_execute($db, "searchlinnot", $query_parameters);

pg_prepare($db, "searchlinnotCount", $count_query);
$count_result = pg_query($db, "searchlinnotCount", $query_parameters);

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
        $tableString .= '<a class="changepage" href="?next='.($limit_start - $q_limit).'"><< previous </a>  ';
}

$tableString .= '-- Showing result chains '.$limit_start.' to ';

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


        // else, the comment has already been filled in

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
