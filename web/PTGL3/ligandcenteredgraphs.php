<?php session_start(); ?>
<!DOCTYPE html>
<?php 
include('./backend/config.php');
$SHOW_ERROR_LIST = array();
include('./backend/get_ligandcomplexgraphs.php');
include('./common.php');
$DO_SHOW_ERROR_LIST = $DEBUG_MODE;


$title = "Ligand-centered graphs";
$title = $SITE_TITLE.$TITLE_SPACER.$title;

function get_total_ligancenteredgraphs_count($db) {
  $query = "SELECT count(lcg.ligandcenteredgraph_id) as count FROM plcc_ligandcenteredgraph lcg";
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
		<h2>Ligand-centered graph visualization</h2>
		<br>
		
		<div id="PageIntro">
		<div class="container" id="pageintro">
		A ligand-centered graph (LCG) is a graph that considers all chains a ligand is in contact with. One such graph exists for <i>every ligand</i> of a PDB file (<b>not</b> for every chain). Ligand-centered graphs always show the helices, strands and ligands of all considered chains.<br/><br/> Note that ligands-centered graphs are required only for a subset of the ligands in the PTGL. For single chain proteins, or ligands which only have contacts to SSEs from
		the same chain, the albelig (alpha-beta-ligand) protein graph (PG) is identical to the ligand-centered graph. In such a case, a link to the albelig graph is provided.		
		<br><br>
		Enter the PDB identifier you are interested in below to see one ligand-centered graph for each ligand of that protein.
		
		<br><br>
		<!--
		<div class="boxedred"><p>NOTICE: This feature is still under development. Data is only available for protein 4a97 atm. Data will be available for all proteins after the next PTGL update.</p></div>
		-->		

		
		</div><!-- end container-->
		</div><!-- end Home -->
		
		<form class="form-inline" action="ligandcenteredgraphs.php" method="get">
			
		<label>Enter PDB identifier, e.g., '7tim' or '4a97':
		<?php
		if(isset($_GET['pdb']) && $_GET['pdb'] != "") {
		  echo '<input type="text" class="form-control" name="pdb" maxlength="4" id="pdb" autocomplete="off" placeholder="Enter PDB ID" value="' . $_GET['pdb'] . '">';
		}
		else {
		  echo '<input type="text" class="form-control" name="pdb" maxlength="4" id="pdb" autocomplete="off" placeholder="Enter PDB ID">';
		}
		echo '<div id="liveSearchResultsLigandCentered"></div>' . "\n";
		?>
		</label>
			
		
		
		<button type="submit" id="sendit_ligandcentered" class="btn btn-default">Search <span class="glyphicon glyphicon-search"></span></button><br>

		</form>	
		
		

		
		
		<div class="container" id="searchResults">
			
			<?php 
			      if($pageload_was_search) {
			          if($valid_values) {
				      echo "<h3> Search Results </h3>\n";
				  
				      echo "<h4> Chains contained in the PDB file </h4>\n";
				      // the table listing all chains of the protein
				      echo $tableString; /* The table string is constructed in /backend/get_ligandcomplexgraphs.php, which is included by this file. */  
				
				      if($num_found > 0 || $num_lig_nolcg_found > 0) {					
					
					echo "<br><br><h3> Ligands containted in PDB file</h3>";
					if($num_lig_found > 0) {					  
					  echo $ligtableString;
					  
					  
					  echo "<br><h3> Ligand-centered graph images </h3>\n";
					  
					  if($num_found > 0) {
					    echo "<br><p>The images below show the ligand-centered graphs of all ligands of the PDB file which have contacts to multiple chains.</p>\n";
					    echo $img_string;
					  }
					  else {
					    echo "<br><p>The PDB file contains $num_lig_nolcg_found ligands, but they do not have any contacts to other chains. You can see all contacts of the ligands in the albelig protein graphs linked in the table below.</p>\n";
					  }
					  
					  if($num_lig_nolcg_found > 0) {
					      echo $no_lcg_table_string;
					  }
					}
					else {
					  echo "<p>No ligands contained in PDB file.</p>\n";
					}
				      }
				      else {
				      
				        $conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
                                        $db = pg_connect($conn_string);
                                        $num_lcgraphs = 0;
                                        if($db) {
                                          $num_lcgraphs = get_total_ligancenteredgraphs_count($db);
                                        }

					echo "<br><h3> No ligand-centered graph images found for your query</h3><br><p>Sorry, your query returned no results. (There are $num_lcgraphs ligand-centered graphs in the database.)</p>\n";
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
