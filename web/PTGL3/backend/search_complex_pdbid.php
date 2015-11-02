<?php

/** This file provides the complex search results if the user searched for a PDB ID.
 *
 * @author Tim Schäfer <>
 *
 *
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
    if (preg_match('/^[A-Z0-9]{1}$/i', $str)) {
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



if (isset($_GET)) {
    if (isset($_GET["searchComplex"])) {
        $query_pdbid = $_GET["searchComplex"];
        $_SESSION["query_pdbid"] = $query_pdbid;
    }

    if (isset($_GET["stc"])) {
        $stc = $_GET["stc"];
        $_SESSION["stc"] = $stc;
    }

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

$query = "SELECT cg.complexgraph_id, cg.pdb_id, p.title, p.header FROM plcc_complexgraph cg INNER JOIN plcc_protein p ON cg.pdb_id = p.pdb_id WHERE cg.pdb_id = $1 ORDER BY pdb_id";


pg_query($db, "DEALLOCATE ALL");
pg_prepare($db, "searchpdbid", $query);
$result = pg_execute($db, "searchpdbid", array($query_pdbid));


// this counter is used to display alternating table colors
$tableString = "";

if($result) {

  $all_results = pg_fetch_all($result);
  $row_count = count($all_results);

  if ($row_count != 0) {
  // begin to create pager
  $tableString = "<table><tr><th>PDB ID</th><th>Title</th><th>Header</th></tr>";
  
      foreach ($all_results as $arr) {
	  // set protein/chain information for readability		
	  $pdb_id = $arr['pdb_id'];
	  $title = $arr["title"];
	  $header = $arr["header"];
	  $tableString .= "<tr><td>$pdb_id</td><td>$title</td><td>$header</td></tr>";

      }
      $tableString .= '</table>'; // the $tableString var is used in the frontend search.php page to print results
  } else {
      $tableString .= "<h3>Unfortunately there are no search results for your query.</h3>";
      $tableString .= "Please <a href='index.php' title='PTGL'>go back</a> and try an other query.";
  }
  pg_free_result($result); // clean memory
}
pg_close($db); // close connection
//EOF
?>
