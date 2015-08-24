<?php session_start(); ?>
<!DOCTYPE html>
<?php 
include('./backend/config.php');
$SHOW_ERROR_LIST = array();
include('./backend/get_complexgraphs.php');
include('./common.php');
$DO_SHOW_ERROR_LIST = $DEBUG_MODE;


$title = "Complex graphs";
$title = $SITE_TITLE.$TITLE_SPACER.$title;

function get_total_complexgraphs_count($db) {
  $query = "SELECT count(cg.complexgraph_id) as count FROM plcc_complexgraph cg";
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
		<h2>The complex graph visualization</h2>
		<br>
		
		<div id="PageIntro">
		<div class="container" id="pageintro">
		A complex graph is a is a graph that considers all chains of a multi-chain protein, or, in general, all protein chains contained in a PDB file. This is especially useful for the analysis of protein complexes.
		It is also of great help when looking at ligands which have contacts with several different chains.
		<br><br>
		Select the PDB IF you are interested in below. You will then be able to see the respective complex graph visualization.
		
		</div><!-- end container-->
		</div><!-- end Home -->
		
		<form class="form-inline" action="foldinggraphs.php" method="get">
			
		<label>Enter PDB identifier, e.g., '7tim' or '4a97':
		<?php
		if(isset($_GET['pdb']) && $_GET['pdb'] != "") {
		  echo '<input type="text" class="form-control" name="pdb" maxlength="4" id="search_complex_graphs_of_pdbid" placeholder="Enter PDB ID" value="' . $_GET['pdb'] . '">';
		}
		else {
		  echo '<input type="text" class="form-control" name="pdb" maxlength="4" id="search_complex_graphs_of_pdbid" placeholder="Enter PDB ID">';
		}
		echo '<input type="hidden" name="graphtype_int" value="6">';
		?>
		</label>
			
		
		
		<button type="submit" id="sendit_all_cgs_of_pdb" "class="btn btn-default">Search <span class="glyphicon glyphicon-search"></span></button><br>

		</form>	
		
		

		
		
		<div class="container" id="searchResults">
			
			<?php 
			      if($pageload_was_search) {
			          if($valid_values) {
				      echo "<h3> Search Results </h3>\n";
				  
				      echo $tableString; /* The table string is constructed in /backend/get_complexgraphs.php, which is included by this file. */  
				
				      if($num_found > 0) {
					echo "<br><h3> Complex graph images </h3><br><p>The images below show the complex graph of all chains of the selected PDB file.</p>\n";
					echo $img_string;
				      }
				      else {
				      
				        $conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
                                        $db = pg_connect($conn_string);
                                        $num_fgraphs = 0;
                                        if($db) {
                                          $num_cgraphs = get_total_complexgraphs_count($db);
                                        }

					echo "<br><h3> No complex graph images found for your query</h3><br><p>Sorry, your query returned no results. (There are $num_cgraphs complex graphs in the database.)</p>\n";
				      }
				  }
				  else {
				       echo "<br><h3> Invalid query</h3><br><p>Sorry, please use another search. (Did you fill out the PDB ID field properly?)</p>\n";
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
