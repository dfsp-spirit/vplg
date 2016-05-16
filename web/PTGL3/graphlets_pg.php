<?php session_start(); ?>
<!DOCTYPE html>
<?php 
include('./backend/config.php');
$SHOW_ERROR_LIST = array();
include('./backend/get_graphlets_pg.php');
include('./common.php');
$DO_SHOW_ERROR_LIST = $DEBUG_MODE;


$title = "Graphlets for Protein graphs";
$title = $SITE_TITLE.$TITLE_SPACER.$title;

function get_total_graphlet_pg_count($db) {
  $query = "SELECT count(pgg.graphlet_id) as count FROM plcc_graphlets pgg";
  $result = pg_query($db, $query);  
  $arr = pg_fetch_array($result, NULL, PGSQL_ASSOC);
  return $arr['count'];
}


?>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="PTGL folding graphs">
	<meta name="author" content="">
	<meta http-equiv="Cache-control" content="public">
	<link rel="shortcut icon" href="favicon.ico?v=1.0" type="image/x-icon" />

	<title><?php echo $title; ?></title>

	<!-- Mobile viewport optimized -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scale=1.0, user-scalable=no"/>

	<!-- Bootstrap CSS -->
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="css/bootstrap-glyphicons.css">

	<!-- Custom CSS -->
	<link rel="stylesheet" type="text/css" href="css/styles.css">
	<link rel="stylesheet" href="css/font-awesome.css"/>

	<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
	<!-- Include Modernizr in the head, before any other JS -->
	<script src="js/modernizr-2.6.2.min.js"></script>

	<!-- Live Search for PDB IDs -->
	<script src="js/livesearch.js" type="text/javascript"></script>
</head>
<body id="customBackground">
	<noscript>
		<META HTTP-EQUIV="Refresh" CONTENT="0;URL=errorJS.php">
	</noscript>
	<div class="wrapper">

	<?php include('navbar.php'); ?>

	<div class="container" id="publications">
		<h2>Graphlets for a protein graph</h2>
		<br>
		
		<div id="PageIntro">
		<div class="container" id="pageintro">		
		Select the chain and graph type you are interested in below. You will then be able to see the graphlet distribution. Note that by default, graphlets are only computed for  albe graphs.
		
		</div><!-- end container-->
		</div><!-- end Home -->
		
		<form class="form-inline" action="graphlets_pg.php" method="get">
			
		<label>Enter PDB identifier and chain, e.g., '7timA':
		<?php
		if(isset($_GET['pdbchain']) && $_GET['pdbchain'] != "") {
		  echo '<input type="text" class="form-control" name="pdbchain" maxlength="5" id="search_fgs_of_pg_pdbchain" placeholder="Enter PDB ID and chain" value="' . $_GET['pdbchain'] . '">';
		}
		else {
		  echo '<input type="text" class="form-control" name="pdbchain" maxlength="5" id="search_fgs_of_pg_pdbchain" placeholder="Enter PDB ID and chain">';
		}
		?>
		</label>
			
		
		<label>Select graph-type: 
		  <select id="multidown" name="graphtype_int">
		  <?php
		     $values = array(1, 2, 3, 4, 5, 6);
		     $labels = array("Alpha", "Beta", "Alpha-Beta", "Alpha-Ligand", "Beta-Ligand", "Alpha-Beta-Ligand");
		     
		     $pre_sel = 3;
		     if(isset($_GET['graphtype_int'])) {
		       $tmp = intval($_GET['graphtype_int']);
		       if($tmp >= 1 && $tmp <= 6) {
		         $pre_sel = $tmp;
		       }
		     }
		     
		     for($i = 0; $i < count($values); $i++) {
		       $sel = "";
		       if($pre_sel === $values[$i]) {
		         $sel = " selected='selected' ";
		       }
		       echo "<option class='downloadOption' $sel value='" . $values[$i] . "'>"  . $labels[$i] . "</option>";
		     }
		  ?>			
		  </select>
		</label>
		
		
		<button type="submit" id="sendit_all_graphlets_of_pg" "class="btn btn-default">Search <span class="glyphicon glyphicon-search"></span></button><br>

		</form>	
		
		

		
		
		<div class="container" id="searchResults">
			
			<?php 
			      if($pageload_was_search) {
			          if($valid_values) {
				      echo "<h3> Search Results </h3>\n";
				  
				      echo $tableString; /* The table string is constructed in /backend/get_graphlets_pg.php, which is included by this file. */  
				
				      if($num_found > 0) {
				      }
				      else {	
				        $conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
                                        $db = pg_connect($conn_string);
                                        $num_pg_graphlets = 0;
                                        if($db) {
                                          $num_pg_graphlets = get_total_graphlet_pg_count($db);
                                        }
					echo "<br><h3> No graphlets found for your query</h3><br><p>Sorry, your query returned no results. (There are $num_pg_graphlets PG graphlet counts in the database.)</p>\n";
				      }
				  }
				  else {
				       echo "<br><h3> Invalid query</h3><br><p>Sorry, please use another search. (Did you fill out the PDB ID and chain field properly?)</p>\n";
				  }
			      }
			
			?>
		</div><!-- end container and searchResults -->

</div><!-- end container and contentText -->
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
</body>
</html>
