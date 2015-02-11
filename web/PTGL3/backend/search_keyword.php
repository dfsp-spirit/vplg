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
    if ($chain === null) {
        return "http://www.cathdb.info/pdb/" . $pdbid;
    } else {
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

function get_proteingraph_database_id($db, $pdb_id, $chain_name, $graph_type) {
    $data = array();
    $query = "SELECT g.graph_id FROM plcc_graph g INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE (p.pdb_id = '" . $pdb_id . "' AND c.chain_name = '" . $chain_name . "' AND gt.graphtype_text = '" . $graph_type . "' )";

    $result = pg_query($db, $query);
    while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)) {
        $data['graph_id'] = $arr['graph_id'];
    }

    pg_free_result($result);
    return $data;
}

/**
 * Returns the graphlet counts of all graphs of the specified type in the whole DB (all protein chains).
 */
function get_all_proteingraph_graphlet_counts_for_graphtype($db, $graph_type) {
    $all_data = array();

    $query = "SELECT p.pdb_id, c.chain_name, gt.graphtype_text, array_to_json(gl.graphlet_counts) AS graphlet_counts FROM plcc_graphlets gl INNER JOIN plcc_graph g ON gl.graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE ( gt.graphtype_text = '" . $graph_type . "' )";

    $result = pg_query($db, $query);
    while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)) {
        $data = array();
        $data['pdb_id'] = $arr['pdb_id'];
        $data['chain_name'] = $arr['chain_name'];
        $data['graph_type'] = $arr['graphtype_text'];
        $data['graphlet_counts'] = json_decode($arr['graphlet_counts']);
        array_push($all_data, $data);
    }

    pg_free_result($result);
    return $all_data;
}

/**
 * the $pdb_chain_list should be a string array, where each string is of length 5 and represents a PDB id and chain, e.g., '7timA' for PDB 7tim chain A.
 */
function get_multiple_PDB_select_query($pdb_chain_list) {

    $valid_pdb_ids = array();
    $valid_pdb_chains = array();
    foreach ($pdb_chain_list as $pdbchain) {
        if (strlen($pdbchain) == 5) {
            $pdb_id = substr($pdbchain, 0, 4);
            $chain_id = substr($pdbchain, 4, 1);
            if (check_valid_pdbid($pdb_id) && check_valid_chainid($chain_id)) {
                array_push($valid_pdb_ids, $pdb_id);
                array_push($valid_pdb_chains, $chain_id);
            }
        }
    }

    if (count($valid_pdb_ids) <= 0) {
        return "";
    }

    $query = "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
				   FROM plcc_chain c
				   INNER JOIN plcc_protein p
				   ON p.pdb_id = c.pdb_id 
				   WHERE ( ";
    for ($i = 0; $i < count($valid_pdb_ids); $i++) {
        $pdb_id = $valid_pdb_ids[$i];
        $chain_id = $valid_pdb_chains[$i];
        $query .= " ( p.pdb_id = '" . $pdb_id . "' AND c.chain_name = '" . $chain_id . "' )";
        if ($i < count($valid_pdb_ids) - 1) {
            $query .= " OR ";
        }
    }

    $query .= " )";

    return $query;
}

$debug_msg = "";
$result_comments = array();


if (isset($_GET["next"])) {
    if (is_numeric($_GET["next"]) && $_GET["next"] >= 0) {
        $limit_start = $_GET["next"];
        $keyword = $_SESSION["keyword"];
        $st = $_SESSION["st"];
    } else {
        $limit_start = 0;
        session_unset();
    }
} else {
    $limit_start = 0;
    session_unset();
}

// give the selected chains to JS
if (isset($_SESSION["chains"])) {
    echo '<script type="text/javascript">';
    echo ('window.checkedChains = new Array(\'' . implode('\',\'', $_SESSION["chains"]) . '\');');
    echo '</script>';
}

// check if parameters are set. If so, set the associated variable with the value

$result_set_has_fake_order_field = FALSE;

if (isset($_GET)) {
    if (isset($_GET["keyword"])) {
        $keyword = $_GET["keyword"];
        $_SESSION["keyword"] = $keyword;
    }

    if (isset($_GET["st"])) {
        $st = $_GET["st"];
        $_SESSION["st"] = $st;
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
$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER . " password=" . $DB_PASSWORD;
$db = pg_connect($conn_string);

// later, if no query is set before, there will be no CONCAT/UNION
$firstQuerySet = false;

$query = "SELECT chain_id, chain_name, pdb_id, resolution, title, header
          FROM ( ";


// following: the queries for each set parameter
if (isset($keyword) && $keyword != "") {
    $keyword = strtolower($keyword);
    $query .= "SELECT c.chain_id, c.chain_name, p.pdb_id, p.resolution, p.title, p.header
                           FROM plcc_chain c
                           INNER JOIN plcc_protein p
                           ON p.pdb_id = c.pdb_id 
                           WHERE p.pdb_id LIKE $1 
                           OR p.header LIKE $2";
}

$q_limit = 25;

$count_query = $query . " ) results";
$query .= " ) results
          ORDER BY pdb_id, chain_name";

$count_query = str_replace("chain_id, chain_name, pdb_id, resolution, title, header", "COUNT(*)", $count_query);
$query .= " LIMIT " . $q_limit . " OFFSET " . $limit_start;

pg_query($db, "DEALLOCATE ALL");
pg_prepare($db, "searchkeyword", $query);
$result = pg_execute($db, "searchkeyword", array("%" . $keyword . "%", "%" . strtoupper($keyword) . "%"));

pg_prepare($db, "searchkeywordCount", $count_query);
$count_result = pg_execute($db, "searchkeywordCount", array("%" . $keyword . "%", "%" . strtoupper($keyword) . "%"));

$row_count = pg_fetch_array($count_result, NULL, PGSQL_ASSOC);
if (isset($row_count["count"])) {
    $row_count = $row_count["count"];
} else {
    $row_count = 0;
}

// this counter is used to display alternating table colors
$counter = 0;
$createdHeadlines = Array();
$numberOfChains = 0;

if ($row_count != 0) {
// begin to create pager
    $tableString = '<div id="pager">';
    if ($limit_start >= $q_limit) {
        $tableString .= '<a class="changepage" href="?next=' . ($limit_start - $q_limit) . '"><< previous </a>  ';
    }

    $tableString .= '-- Showing result chains ' . ($row_count == 0 ? 0 : $limit_start + 1) . ' to ';

    if ($limit_start + $q_limit > $row_count) {
        $tableString .= $row_count . ' (of ' . $row_count . ' total) -- ';
    } else {
        $tableString .= ($limit_start + $q_limit) . ' (of ' . $row_count . ' total) -- ';
    }

    if (($limit_start + $q_limit) < $row_count) {
        $tableString .= '<a class="changepage" href="?next=' . ($limit_start + $q_limit) . '"> next >></a>';
    }
    $tableString .= '</div>';
// EOPager

    while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)) {
        // set protein/chain information for readability		
        $pdb_id = $arr['pdb_id'];
        $chain_name = $arr['chain_name'];
        $resolution = $arr['resolution'];
        $title = $arr["title"];
        $header = $arr["header"];
        $cathlink = get_cath_link($pdb_id, $chain_name);
        $comment = "";

        // provides alternating blue/white tables
        if ($counter % 2 == 0) {
            $class = "Orange"; // the CSS class is still called orange...
        } else {
            $class = "White";
        }

        // if the headline of the PDB-ID is NOT created yet...
        if (!in_array($pdb_id, $createdHeadlines)) {
            $tableString .= '<div class="results results' . $class . '">					
                                                <div class="resultsHeader resultsHeader' . $class . '">
                                                        <div class="resultsId">' . $pdb_id . '</div>
                                                        <div class="resultsRes">Resolution: ' . $resolution . ' &Aring;</div>
                                                        <div class="resultsLink"><a href="http://www.rcsb.org/pdb/explore/explore.do?structureId=' . $pdb_id . '" target="_blank">[PDB]</a>
                                                                                                <a href="http://www.rcsb.org/pdb/download/downloadFile.do?fileFormat=FASTA&compression=NO&structureId=' . $pdb_id . '" target="_blank">[FASTA]</a></div>
                                                </div>
                                                <div class="resultsBody1">
                                                        <div class="resultsTitle">Title</div>
                                                        <div class="resultsTitlePDB">' . ucfirst(strtolower($title)) . '</div>
                                                </div>
                                                <div class="resultsBody2">
                                                        <div class="resultsClass">Classification</div>
                                                        <div class="resultsClassPDB">' . ucfirst(strtolower($header)) . '</div>
                                                </div>';
            // now the headline is created, so push the PDB-ID to the createdHeadlines array
            array_push($createdHeadlines, $pdb_id);
            $counter++;
        }
        // if the headline is already there..
        $tableString .= '	<div class="resultsFooter">
                                        <div class="resultsChain">Chain ' . $chain_name . '</div>
                                        <div class="resultsChainNum"><input type=checkbox id="' . $pdb_id . $chain_name . '" class="chainCheckBox" value="' . $pdb_id . $chain_name . '"/>' . $pdb_id . $chain_name . '</div>
                                        <div class="resultsCATH"><a href="' . $cathlink . '" target="_blank">CATH</a></div>
                                        <div class="resultsComment">' . $comment . '</div>
                                </div>';

        $numberOfChains++;
    }
    $tableString .= ' </div>'; // the $tableString var is used in the frontend search.php page to print results
} else {
    $tableString .= "<h3>Unfortunately there are no search results for your query.</h3>";
    $tableString .= "Please <a href='index.php' title='PTGL'>go back</a> and try an other query.";
}
pg_free_result($result); // clean memory
pg_close($db); // close connection
//EOF
?>
