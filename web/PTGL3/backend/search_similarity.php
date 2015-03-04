<?php
/** This file provides the search results depending on the search query.
 * 
 * It receives the parameters from the search box on the frontpage or the advanced search box.
 * If there are multiple search keywords, the query is joined either with OR or AND.
 * The results (if we got some) are displayed in a table and the HTML construct is
 * stored in the $tableString, which will be echoed later.
 *   
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
  * Returns a graphlet similarity score, a value between 0.0 and 1.0. The higher the value, the large the similarity between the counts in $graphletcounts_a and $graphletcounts_b.
  */
function compute_graphlet_similarity($graphletcounts_a, $graphletcounts_b) {
	if(count($graphletcounts_a) > 0) {
	  if(count($graphletcounts_a) === count($graphletcounts_b)) {
	    $sum_a = array_sum($graphletcounts_a);
	    $sum_b = array_sum($graphletcounts_b);
	    
	    $res = 0.0;
	    for($i = 0; $i < count($graphletcounts_a); $i++) {
	      $score_a = -log($graphletcounts_a[$i] / $sum_a);
	      $score_b = -log($graphletcounts_b[$i] / $sum_b);
	      if(is_infinite($score_a) || is_infinite($score_b)) {
	        continue;
	      }
	      else {
	        $res += abs($score_a - $score_b);
	      }
	    }
	    
	    return $res;
	  }
	}
	else {
	  return 0.0;
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


function get_proteingraph_graphlet_counts($db, $pdb_id, $chain_name, $graph_type) {
  $data = array();
  $query = "SELECT p.pdb_id, c.chain_name, gt.graphtype_text, array_to_json(gl.graphlet_counts) AS graphlet_counts FROM plcc_graphlets gl INNER JOIN plcc_graph g ON gl.graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE (p.pdb_id = '" . $pdb_id . "' AND c.chain_name = '" . $chain_name . "' AND gt.graphtype_text = '" . $graph_type . "' )";
    
  $result = pg_query($db, $query);
  while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		$data['pdb_id'] =  $arr['pdb_id'];
		$data['chain_name'] =  $arr['chain_name'];
		$data['graph_type'] =  $arr['graphtype_text'];		
		$data['graphlet_counts'] = json_decode($arr['graphlet_counts']);
	}
  
  pg_free_result($result);
  return $data;
  
}


function get_proteingraph_database_id($db, $pdb_id, $chain_name, $graph_type) {
  $data = array();
  $query = "SELECT g.graph_id FROM plcc_graph g INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE (p.pdb_id = '" . $pdb_id . "' AND c.chain_name = '" . $chain_name . "' AND gt.graphtype_text = '" . $graph_type . "' )";
    
  $result = pg_query($db, $query);
  while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		$data['graph_id'] =  $arr['graph_id'];
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
  while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		$data = array();
		$data['pdb_id'] =  $arr['pdb_id'];
		$data['chain_name'] =  $arr['chain_name'];
		$data['graph_type'] =  $arr['graphtype_text'];		
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

if(isset($_GET["next"])) {
	if(is_numeric($_GET["next"]) && $_GET["next"] >= 0){
		$limit_start = $_GET["next"];
		$st = $_SESSION["st"];
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

$result_set_has_fake_order_field = FALSE;

if(isset($_GET)) {
    if(isset($_GET["st"])) {
		$st = $_GET["st"];
		$_SESSION["st"] = $st;
	} 
	
	// graphlet similarity search
	if(isset($_GET["graphletsimilarity"])) {
		$graphletsimilarity = $_GET["graphletsimilarity"];
		$_SESSION["graphletsimilarity"] = $graphletsimilarity;
		
		$result_set_has_fake_order_field = TRUE;
		if($USE_PRECOMPUTED_GRAPHLET_SIMILARITY_DATA_FROM_DB) {
		  $fake_order_field_name = "score";
		  $show_similarity_score_from_external_list = FALSE; // we can use the score field from the query
		}
		else {
		  $fake_order_field_name = "fake_order";
		  $show_similarity_score_from_external_list = TRUE; // we need to use the similarity_list array we build in parallel
		}
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
$firstQuerySet = false;

if($result_set_has_fake_order_field) {
    $query = "SELECT " . $fake_order_field_name . ", chain_id, chain_name, pdb_id, resolution, title, header
                  FROM ( ";
}
else {
    $query = "SELECT chain_id, chain_name, pdb_id, resolution, title, header
                  FROM ( ";
}

// following: the queries for each set parameter

//graphletsimilarity
if (isset($graphletsimilarity) && $graphletsimilarity != ""){
        array_push($list_of_search_types, "graphlet-based similarity search");

        // $graphletsimilarity must be set to a valid pdb id + chain, e.g., '7timA'.		

        if(strlen($graphletsimilarity) == 5) {
                $pdb_id = substr($graphletsimilarity, 0, 4);
                $chain_id = substr($graphletsimilarity, 4, 1);						

                if(check_valid_pdbid($pdb_id) && check_valid_chainid($chain_id)) {

                        $graph_type = "albe";


                        if($USE_PRECOMPUTED_GRAPHLET_SIMILARITY_DATA_FROM_DB) {
                              // ------------------- code to use precomputed similarity data from the database ------------- //

                              if($firstQuerySet) { $query .= " UNION "; }

                              $query .= "SELECT gs." . $fake_order_field_name . ", cmp_chain.chain_id, cmp_chain.chain_name, cmp_protein.pdb_id, cmp_protein.resolution, cmp_protein.title, cmp_protein.header
                                        FROM plcc_graphletsimilarity gs
                                        INNER JOIN  plcc_graph src_pg ON gs.graphletsimilarity_sourcegraph = src_pg.graph_id
                                        LEFT JOIN plcc_chain src_chain ON src_pg.chain_id = src_chain.chain_id
                                        LEFT JOIN plcc_protein src_protein ON src_chain.pdb_id = src_protein.pdb_id
                                        LEFT JOIN plcc_graphtypes src_gt ON src_pg.graph_type = src_gt.graphtype_id
                                        JOIN plcc_graph cmp_pg ON gs.graphletsimilarity_targetgraph = cmp_pg.graph_id
                                        LEFT JOIN plcc_chain cmp_chain ON cmp_pg.chain_id = cmp_chain.chain_id
                                        LEFT JOIN plcc_protein cmp_protein ON cmp_chain.pdb_id = cmp_protein.pdb_id
                                        LEFT JOIN plcc_graphtypes cmp_gt ON cmp_pg.graph_type = cmp_gt.graphtype_id
                                        WHERE ( src_chain.pdb_id = '" . $pdb_id . "' 
                                        AND src_chain.chain_name = '" . $chain_id . "'
                                        AND src_gt.graphtype_text = '" . $graph_type . "' ) ";

                              //$result_set_has_fake_order_field = TRUE;	// already set
                              //$fake_order_field_name = "score";		// already set
                              $firstQuerySet = true;	
                        }
                        else {

                              // ------------------------ code for live graphlet similarity computation ------------------ //
                              $pdbchainlist = array();
                              $similarity_list = array();

                              //  get graphlet counts of query protein from DB
                              $query_prot_data = get_proteingraph_graphlet_counts($db, $pdb_id, $chain_id, $graph_type);
                              if(isset($query_prot_data['pdb_id'])) {

                                // get graphlet counts for all protein chains
                                $all_data = get_all_proteingraph_graphlet_counts_for_graphtype($db, $graph_type);

                                // fill array with similar chains based on graphlet distance				


                                // now compare scores and add best scores to $pdbchainlist
                                foreach($all_data as $chaindata) {
                                        if(isset($chaindata['pdb_id'])) {
                                        $similarity_score = compute_graphlet_similarity($query_prot_data['graphlet_counts'], $chaindata['graphlet_counts']);
                                        //  compare to other using some graphlet-based score
                                        //if($similarity_score >= 0.9) {
                                                $found_name = "" . $chaindata['pdb_id'] . $chaindata['chain_name'];
                                                array_push($pdbchainlist, $found_name);
                                                array_push($similarity_list, $similarity_score);
                                                $debug_msg .= "  sim($found_name = $similarity_score) "; 
                                        //}
                                        } 
                                        //else {
                                        //  $debug_msg .= "  chaindata_has_no_pdb_id "; 
                                        //}
                                }

                                array_multisort($similarity_list, $pdbchainlist);	// sort both by the score	

                                $max_results = 25;
                                if(count($similarity_list) > $max_results) {
                                  array_splice($similarity_list, $max_results);
                                  array_splice($pdbchainlist, $max_results);
                                }

                                // add comment, used to output the score in the table later
                                for($i = 0; $i < count($pdbchainlist); $i++) {
                                  $result_comments[$pdbchainlist[$i]] = "Graphlet-based distance to PDB $pdb_id chain $chain_id: " . sprintf('%0.3f', $similarity_list[$i]);
                                }


                                if(count($pdbchainlist) > 0) {
                                  if($firstQuerySet) { $query .= " UNION "; }
                                        $query .= get_multiple_PDB_select_query_in_order($pdbchainlist);
                                        $result_set_has_fake_order_field = TRUE;
                                        $fake_order_field_name = "fake_order";
                                        $firstQuerySet = true;		  
                                } else {
                                  $debug_msg .= "graphletsimilarity: No matching chains with proper similarity score based on graphlet counts found in the " . count($all_data) . " DB entries.";
                                }
                              } else {
                                $debug_msg .= "graphletsimilarity: No graphlet data for query graph '$pdb_id' chain '$chain_id' type '$graph_type' found.";
                              }
                        }

                }
                else {
                  $debug_msg .= "graphletsimilarity: invalid PDB '$pdb_id' and chain '$chain_id'.";
                }
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

if($result_set_has_fake_order_field) {
  $count_query = str_replace("$fake_order_field_name, chain_id, chain_name, pdb_id, resolution, title, header", "COUNT(*)", $count_query);
}
else {
  $count_query = str_replace("chain_id, chain_name, pdb_id, resolution, title, header", "COUNT(*)", $count_query);
}

//print "Count query is '$count_query'";

$query .= " LIMIT " . $q_limit . " OFFSET ".$limit_start;

$result = pg_query($db, $query); // or die($query . ' -> Query failed: ' . pg_last_error());

//if(! $result) {
//  die("FCK: '$query'");
//}

$count_result = pg_query($db, $count_query); // or die($query . ' -> Query failed: ' . pg_last_error());

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


        // else, the comment has already been filled in

        if(isset($result_comments[$pdb_id . $chain_name])) {
          $comment = $result_comments[$pdb_id . $chain_name];
        }

        if($USE_PRECOMPUTED_GRAPHLET_SIMILARITY_DATA_FROM_DB) {
          $comment = "Graphlet-based distance to query chain: " . sprintf('%0.3f', $arr['score']);
        }



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
