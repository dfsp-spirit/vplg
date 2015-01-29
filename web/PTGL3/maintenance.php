<!DOCTYPE html>
<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);

include('./backend/config.php'); 
$SHOW_ERROR_LIST = array();
include('./common.php');
$DO_SHOW_ERROR_LIST = $DEBUG_MODE;


$title = "Maintenance";
$title = $SITE_TITLE.$TITLE_SPACER.$title;


/**
  * Runs the given query, which MUST be secured, i.e., not contain any user supplied parts. ONLY USE THIS FOR INTERNAL QUERIES, OR IT WILL ALLOW SQL INJECTION!
  * The query is expected to return a single result (one row with one field), like a count query does.
  * @param $db the db connection to run the query on
  * @param $sql_query the SQL query, something like "SELECT count(field_a) from table_b"
  * @return a result array if the query was executed successfully. if an error occured, a string containing an error message is returned instead.
  */
function handle_fixed_query_one_result($db, $sql_query, $result_field_names = array()) {
    if(! $db) {
        return "ERROR: Database connection failed.";
    }
    $ret = array();
    $result = pg_query($db, $sql_query);    

    $ret = array();
    
    if(! $result) { 
        $ret = "DB ERROR: '" . pg_last_error($db) . "'";
    } 
    else {				
        $all_data = pg_fetch_all($result);
        if(count($all_data) == 1) {
            $row = $all_data[0];
            if(count($row) == 1) {
                $ret = array();
                foreach($result_field_names as $f) {
                    $ret[$f] = $row[$f];
                }
            }
        }
    }
    return $ret;
}

/**
 * Returns a line of php code with a var of the given name with the given value.
 */
function get_string_var_line($name, $value) {
    return '$' . $name . ' = "' . $value . '";' . "\n";
}

/**
 * Returns a line of php code with a var of the given name with the given value.
 */
function get_int_var_line($name, $value) {
    return '$' . $name . ' = ' . $value . ';' . "\n";
}

/**
 * Returns a line of php code with a var of the given name with the given value.
 */
function get_int_array_line($name, $values = array()) {
    $str = '$' . $name . ' = array(';
    for($i = 0; $i < count($values); $i++) {
        $v = $values[$i];
        $str .= $v;
        if($i < count($values) - 1) {
            $str .= ",";
        }
    }
    $str .= ');' . "\n";
    return $str;
}

/**
 * Returns a line of php code with a var of the given name with the given value.
 */
function get_string_array_line($name, $values = array()) {
    $str = '$' . $name . ' = array(';
    for($i = 0; $i < count($values); $i++) {
        $v = $values[$i];
        $str .= '"' . $v . '"';
        if($i < count($values) - 1) {
            $str .= ", ";
        }
    }
    $str .= ');' . "\n";
    return $str;
}


/**
 * Translates the graph type int constant to the string, e.g., 1 => alpha, 2 => beta, ...
 */
function get_graphtype_abbr($graphtype_int){
	switch ($graphtype_int){
		case 1:
			return "alpha";
			break;
		case 2:
			return "beta";
			break;
		case 3:
			return "albe";
			break;
		case 4:
			return "alphalig";
			break;
		case 5:
			return "betalig";
			break;
		case 6:
			return "albelig";
			break;
	}
}

/**
  * Checks the given credentials (both of which are expected to be MD5 encoded).
  */
function check_auth($id, $token)  {
    if(md5($id) === "4d682ec4eed27c53849758bc13b6e179" || md5($id) === "d77d5e503ad1439f585ac494268b351b") {
        if(md5($token) === "c8bd0177e53c5d2fec5d7e8cba43c505") {
            return TRUE;
        }
    }
    return FALSE;
}


/**
 * Builds and returns an SQL query string for the linear notations of the given graph type and notation.
 */
function get_all_linnots_query_string($graphtype_int, $notation){
    $query = "SELECT DISTINCT fglin.ptgl_linnot_%s
    FROM plcc_fglinnot fglin
    INNER JOIN plcc_foldinggraph fg
    ON fglin.linnot_foldinggraph_id = fg.foldinggraph_id
    INNER JOIN plcc_graph g
    ON fg.parent_graph_id = g.graph_id
    WHERE g.denorm_graph_type = %s
    ORDER BY fglin.ptgl_linnot_%s ASC";
    $query = sprintf($query, $notation, $graphtype_int, $notation);
return $query;	
} 

/**
 * Builds and returns an SQL query string for the linear notations of the given graph type and notation, making use of the new denormalized DB fields. Should thus be faster than the other version.
 */
function get_all_linnots_query_string_denormalized($graphtype_int, $notation){
	$query =   "SELECT DISTINCT fglin.ptgl_linnot_%s
				FROM plcc_fglinnot fglin				
				WHERE fglin.denorm_graph_type = %s
				ORDER BY fglin.ptgl_linnot_%s ASC";
	
	$query = sprintf($query, $notation, $graphtype_int, $notation);
	return $query;			
}

/**
  * Builds the filename of the linear notation $notation of the given graph type.
  * @param $graphtype_int the graph type as an integer (1=alpha, 2=beta, ...)
  * @param $notation the notation as a string, e.g., "adj" or "key"
  * @return the filne name, including file extension but with no path
  */
function get_linnots_filename($graphtype_int, $notation) {
    $graphtype = get_graphtype_abbr($graphtype_int);
    return "linnots_" . $notation . "_" . $graphtype . ".lst";
}


?>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="VPLG PTGL protein graph database">
	<meta name="author" content="The MolBI group">
	<link rel="shortcut icon" href="favicon.ico?v=1.0" type="image/x-icon" />

	<title><?php echo $title; ?></title>

	<!-- Mobile viewport optimized -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scale=1.0, user-scalable=no"/>

	<!-- Bootstrap CSS -->
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="css/bootstrap-glyphicons.css">

	<!-- Custom CSS -->
	<link rel="stylesheet" href="css/font-awesome.css"/>
	<link rel="stylesheet" type="text/css" href="css/styles.css">
	<!--<link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet"> -->

	<!-- Include Modernizr in the head, before any other JS -->
	<script src="js/modernizr-2.6.2.min.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
</head>

<body id="customBackground">
	<noscript>
		<META HTTP-EQUIV="Refresh" CONTENT="0;URL=errorJS.php">
	</noscript>
	<div class="wrapper">

            <?php include('./navbar.php'); ?>

	<div id="Home">
		<div class="container" id="intro">
		<!-- Intro message -->
		    <h2><?php echo $SITE_TITLE; ?> Maintenance</h2>
		    
		    This is the <?php echo $SITE_TITLE; ?> maintenance page.
		    
		    <br><br>
		    
		    <h3>Status</h3>
		
		    <?php
			
			  if($SHOW_MAINTENANCE_MESSAGE_ON_FRONTPAGE) {
			      echo "\n" . '<br><div class="boxedred"><p><br>&nbsp;&nbsp;' . "\n";
				  echo "The server is in maintenance mode, and the maintenance message is shown on the front page atm.";
				  echo "</p></div><br>\n";
			  }
			  else {
			        echo "<p>This server is NOT in maintenance mode atm.</p>\n";
			  }
			  
			  if($DEBUG_MODE) {
			      echo "<p><span style='color:darkorange;'>This server is in debug mode atm.</span></p>\n";
			  }
			  else {
			      echo "<p>This server is NOT in debug mode atm.</p>\n";
			  }
			  
			  
			  $conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
			  $db = pg_connect($conn_string);                          
			
			  if($CHECK_INSTALL_ON_MAINTENANCEPAGE) {
			      
			      
			      $db_ok = $db;
			      $tmp_dir_ok = is_writable('./temp_downloads/');
			      $tmp_data_dir_ok = is_writable('./temp_data/');
			      $data_ok = is_dir($IMG_ROOT_PATH);
			      
			      $num_errors_occured = 0;
			      if(!$db_ok) { $num_errors_occured++; }
			      if(!$tmp_dir_ok) { $num_errors_occured++; }
			      if(!$tmp_data_dir_ok) { $num_errors_occured++; }
			      if(!$data_ok) { $num_errors_occured++; }
			      
			      			      
			      if($num_errors_occured > 0) {
				  echo "\n" . '<br><div class="boxedred">' . "\n<ul>\n";
				  echo "<p><b>ERROR:</b> Server Installation incomplete,  the server admin needs to fix this. The following $num_errors_occured errors were detected:</p>";
				
				  if(! $db_ok) {
				      echo "<li>The database connection is not configured properly.</li>\n";
				  }
				  
				  // check whether tmp download dir (where the zip files are stored for download) is writable
				  if ( ! $tmp_dir_ok) {
				      echo "<li>The temporary download file directory is not writable, ZIP file downloads disabled.</li>\n";
				  }
				  
				  // check whether tmp download dir (where the zip files are stored for download) is writable
				  if ( ! $tmp_data_dir_ok) {
				      echo "<li>The temporary data directory is not writable, cannot update linnot files and stats by running admin tasks.</li>\n";
				  }
				  
				  // check for existence of image data directory
				  if ( ! $data_ok) {
				      echo "<li>The data directory does not exist, graph images and other data missing.</li>\n";
				  }
				  
				  echo "</ul></div><br>\n";
			      } else {
			          echo "<p>Server installation OK.</p>\n";
			      }
			  }
	
			?>
			
			<br><br>
		    
		    <h3>Tasks</h3>
		    <p>You need to provide your admin ID and the your secret admin token to start tasks.</p>		    
		    <br><br>
		    <p>
		    <b>Linnot list</b> -- generate new linear notations file from the database.</p>
		     <form action="maintenance.php" method="POST">
		     Admin ID:
                     <input type="input" name="admin_id" value="">                     
                     Admin token:
                     <input type="password" name="admin_token" value="">
                     <input type="hidden" name="task" value="linnot_list">
                     <input type="submit" value="Start linnot task" onclick="return confirm('Linear Notations Task: This may take some time, it also places load on the server. Are you sure?')">
                     </form> 
                     
                     <br><br>
                     <b>Content stats</b> -- generate new statistics on the contents of the whole database for the content page.
                     
                     <form action="maintenance.php" method="POST">
		     Admin ID:
                     <input type="input" name="admin_id" value="">                     
                     Admin token:
                     <input type="password" name="admin_token" value="">
                     <input type="hidden" name="task" value="content_stats">
                     <input type="submit" value="Start content stats task" onclick="return confirm('Content Stats Task: This may take some time, it also places load on the server. Are you sure?')">
                     </form> 
		   
		   
		   
		   <br><br>		   
		   <h3>Task handling results</h3>
		   <p>
		   <?php
		   
		   // handle tasks
		   if(isset($_POST['task'])) {
		       if(check_auth($_POST['admin_id'], $_POST['admin_token'])) {
		           echo "Task requested: ";
		           $task = $_POST['task'];
		           if($task === "linnot_list") {
		               echo "Linnot list task...<br>";
		               $notations = array("adj", "red", "seq", "key");
		               $graphtypes_int = array(1, 2, 3, 4, 5, 6);
		               
		               foreach($notations as $notation) {
		                      foreach($graphtypes_int as $graphtype_int) {
					  if($USE_DENORMALIZED_DB_FIELDS) {
					      $query = get_all_linnots_query_string_denormalized($graphtype_int, $notation);
					  } else {
					      $query = get_all_linnots_query_string($graphtype_int, $notation);
					  }
					  $result = pg_query($db, $query);
					  $all_data = pg_fetch_all($result);
		    
					  if(! $all_data) { 
					      echo "ERROR: '" . pg_last_error() . "'.";
					  } else {					  
					      $result_string = "";
					      
					      foreach($all_data as $row) {
						      $result_string .= $row['ptgl_linnot_'.$notation] . "\n";
					      }
					      $filename = "./temp_data/" . get_linnots_filename($graphtype_int, $notation);
					      $num_bytes_written = file_put_contents($filename, $result_string);
					      echo "Wrote $num_bytes_written bytes (" . count($all_data) . " linnots) to file $filename.<br>\n";
					  }
				      }
			      }
	
		           }
		           
		           if($task === "content_stats") {
		               echo "Content statistics task...<br>";
		               
		               $filename = "./temp_data/content_data2.php";
		               $result_string_stats = '<?php' . "\n";
		               $result_string_stats .= '/* This file is auto-generated by the maintenance page of the PTGL, do not edit it manually. */' . "\n";
		               
		               $all_queries_ok = TRUE;
		               
		               
		               // ----- get the number of PDB files in the database -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count(pdb_id) as cnt from plcc_protein;", array("cnt"));
		               if(is_array($res)) {
		                   $num_pdb_files = $res["cnt"];
		                   //echo "yes, $num_pdb_files PDB files.";
		                   $result_string_stats .= get_int_var_line("num_pdb_files", $num_pdb_files);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "PDB file count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               // ----- get the number of PDB chains in the database -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count(chain_id) as cnt from plcc_chain;", array("cnt"));
		               if(is_array($res)) {
		                   $num_pdb_chains = $res["cnt"];
		                   $result_string_stats .= get_int_var_line("num_pdb_chains", $num_pdb_chains);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "PDB chain count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               // ----- get the number of SSEs in the database -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count(sse_id) as cnt from plcc_sse;", array("cnt"));
		               if(is_array($res)) {
		                   $num_sses = $res["cnt"];
		                   $result_string_stats .= get_int_var_line("num_sses", $num_sses);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "SSE count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               // ----- get the number of total contacts (intrachain) in the database -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count(contact_id) as cnt from plcc_contact;", array("cnt"));
		               if(is_array($res)) {
		                   $num_intrachain_contacts = $res["cnt"];
		                   $result_string_stats .= get_int_var_line("num_intrachain_contacts", $num_intrachain_contacts);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "Intrachain contact count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               // get the counts for all 3 SSE types
		               // ----- get the number of helices in the database -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count(sse_id) as cnt from plcc_sse WHERE sse_type = 1;", array("cnt"));
		               if(is_array($res)) {
		                   $num_helices = $res["cnt"];
		                   $result_string_stats .= get_int_var_line("num_helices", $num_helices);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "Helix count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               // ----- get the number of strands in the database -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count(sse_id) as cnt from plcc_sse WHERE sse_type = 2;", array("cnt"));
		               if(is_array($res)) {
		                   $num_strands = $res["cnt"];
		                   $result_string_stats .= get_int_var_line("num_strands", $num_strands);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "Strand count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               // ----- get the number of ligands in the database -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count(sse_id) as cnt from plcc_sse WHERE sse_type = 3;", array("cnt"));
		               if(is_array($res)) {
		                   $num_ligands = $res["cnt"];
		                   $result_string_stats .= get_int_var_line("num_ligands", $num_ligands);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "Ligand count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }

		               
		               // ----- get the number of SSEs in alpha graphs -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count (s2g.ssetoproteingraph_id) as num_in_graphtype FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 1;", array("num_in_graphtype"));
		               if(is_array($res)) {
		                   $num_sses_in_alphagraphs = $res["num_in_graphtype"];
		                   $result_string_stats .= get_int_var_line("num_sses_in_alphagraphs", $num_sses_in_alphagraphs);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "SSEs in alpha graphs count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               // ----- get the number of SSEs in beta graphs -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count (s2g.ssetoproteingraph_id) as num_in_graphtype FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 2;", array("num_in_graphtype"));
		               if(is_array($res)) {
		                   $num_sses_in_betagraphs = $res["num_in_graphtype"];
		                   $result_string_stats .= get_int_var_line("num_sses_in_betagraphs", $num_sses_in_betagraphs);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "SSEs in beta graphs count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               // ----- get the number of SSEs in albe graphs -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count (s2g.ssetoproteingraph_id) as num_in_graphtype FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 3;", array("num_in_graphtype"));
		               if(is_array($res)) {
		                   $num_sses_in_albegraphs = $res["num_in_graphtype"];
		                   $result_string_stats .= get_int_var_line("num_sses_in_albegraphs", $num_sses_in_albegraphs);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "SSEs in albe graphs count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               // ----- get the number of SSEs in alphalig graphs -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count (s2g.ssetoproteingraph_id) as num_in_graphtype FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 4;", array("num_in_graphtype"));
		               if(is_array($res)) {
		                   $num_sses_in_alphaliggraphs = $res["num_in_graphtype"];
		                   $result_string_stats .= get_int_var_line("num_sses_in_alphaliggraphs", $num_sses_in_alphaliggraphs);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "SSEs in alphalig graphs count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               // ----- get the number of SSEs in betalig graphs -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count (s2g.ssetoproteingraph_id) as num_in_graphtype FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 5;", array("num_in_graphtype"));
		               if(is_array($res)) {
		                   $num_sses_in_betaliggraphs = $res["num_in_graphtype"];
		                   $result_string_stats .= get_int_var_line("num_sses_in_betaliggraphs", $num_sses_in_betaliggraphs);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "SSEs in betalig graphs count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               // ----- get the number of SSEs in albelig graphs -----
		               $res = array();
		               $res = handle_fixed_query_one_result($db, "SELECT count (s2g.ssetoproteingraph_id) as num_in_graphtype FROM plcc_nm_ssetoproteingraph s2g INNER JOIN plcc_graph g ON s2g.graph_id = g.graph_id WHERE g.graph_type = 6;", array("num_in_graphtype"));
		               if(is_array($res)) {
		                   $num_sses_in_albeliggraphs = $res["num_in_graphtype"];
		                   $result_string_stats .= get_int_var_line("num_sses_in_albeliggraphs", $num_sses_in_albeliggraphs);
		               } else {
		                   array_push($SHOW_ERROR_LIST, "SSEs in albelig graphs count database query failed: '" . $res[0] . "'");
		                   $all_queries_ok = FALSE;
		               }
		               
		               
		               
		               
		               
		               
		               //$result_string_stats .= get_string_var_line("a", "b");
		               //$result_string_stats .= get_int_var_line("b", 1244);
		               //$result_string_stats .= get_int_array_line("b", array(1244, 343, 3243, 577));
		               //$result_string_stats .= get_string_array_line("b", array("hal", "lo", "du", "held"));
		               
		               $result_string_stats .= '?>' . "\n";
		               if($all_queries_ok) {
			           $num_bytes_written = file_put_contents($filename, $result_string_stats);
			           echo "Wrote $num_bytes_written bytes to file '$filename'.<br>\n";
			       }
			       else {
			           echo "ERROR: Some queries failed, did NOT write any results to file '$filename'.<br>\n";
			       }
		           }
		           
		       echo "<br>\n";
		       } else {
		           sleep(3);
		           echo "Task requested with invalid AUTH, ignored.<br>\n";
		           //echo "mda: " . md5($_POST['admin_id']) . "<br>\n";
		           //echo "mdt: " . md5($_POST['admin_token']) . "<br>\n";
		       }
		   } else {
		       echo "<i>No task requested.</i>";
		   }
		   
		   
		   ?>
		   </p><br><br><br>
		   
		</div>	
		
	</div>

</div><!-- end wrapper -->

<?php
// show red error bar on bottom of screen if enabled and error list is non-empty
if($DO_SHOW_ERROR_LIST) {
  show_the_errors($SHOW_ERROR_LIST);  
}
?>


<?php include('footer.php'); ?>
	<!-- All Javascript at the bottom of the page for faster page loading -->
	<!-- also needed for the dropdown menus etc. ... -->

	<!-- First try for the online version of jQuery-->
	<script src="http://code.jquery.com/jquery.js"></script>

	<!-- If no online access, fallback to our hardcoded version of jQuery -->
	<script>window.jQuery || document.write('<script src="js/jquery-1.8.2.min.js"><\/script>')</script>

	<!-- Bootstrap JS -->
	<script src="js/bootstrap.min.js"></script>

	<!-- Custom JS -->
	<script src="js/script.js"></script>
	<script src="js/userguide.js" type="text/javascript"></script>

	<!-- Live Search for PDB IDs -->
	<script src="js/livesearch.js" type="text/javascript"></script>

</body>
</html>
