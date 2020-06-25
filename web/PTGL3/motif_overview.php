<?php session_start(); ?>
<!DOCTYPE html>
<?php 
include('./backend/config.php');
include('./common.php');
include('./backend/get_motif_meta-info.php');
$SHOW_ERROR_LIST = array();
$DO_SHOW_ERROR_LIST = $DEBUG_MODE;

$title = "Protein structural motif overview";
$title = $SITE_TITLE.$TITLE_SPACER.$title;




function get_total_chains_count($db) {
  $query = "SELECT count(c.chain_id) as count FROM plcc_chain c";
  $result = pg_query($db, $query);  
  $arr = pg_fetch_array($result, NULL, PGSQL_ASSOC);
  return $arr['count'];
}

function get_motif_fullname($db, $motiv_abbreviation) {
  
  $query = "SELECT m.motif_name FROM plcc_motif m WHERE m.motif_abbreviation = '" . $motiv_abbreviation . "'";
  $result = pg_query($db, $query);
  
  $arr = pg_fetch_array($result, NULL, PGSQL_ASSOC);  
  return $arr['motif_name'];
}

?>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="PTGL folding graphs">
	<meta http-equiv="Cache-control" content="public">
	<meta name="author" content="">
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

		<!-- DEBUG TODELETE -->
		<p> DEBUG </p>
		<?php 
			echo( implode(", ", $motif_names) . "<br>");
			echo( implode(", ", $all_motif_counts) );
		?>

		<h2><?php print "$SITE_TITLE"; ?> Structural motif overview</h2>
		<br>
		
		<div id="PageIntro">
		<div class="container" id="pageintro">
		A motif is a common supersecondary structure. A motif consists of only a few SSEs, and it may occur in protein chains with very different functions. This server implements motif
		detection in protein graphs based on the linear notations of folding graphs.
		
		</div><!-- end container-->
		</div><!-- end Home -->
		<?php
		  $conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	          $db = pg_connect($conn_string);
		?>
		
		<table id="tblmotifoverview">
		<tr>
		    <th>Motif</th><th>Type</th><th>Motif abbreviation</th><th>Chains with motif in the database (of <?php print get_total_chains_count($db);?> chains total)</th><th>Short info on motif</th>
		</tr>
		<?php		  		
	          
	          foreach($motif_names as $motif) {
	          
	            if( ! $ENABLE_MOTIF_SEARCH_ALPHABETA) {
	             
	              if($motif === "plait" || $motif === "ubi" || $motif === "tim" || $motif === "rossman") {
	                  continue;
	              }
	            }      	            	            
	            
	            if(isset($motif_data['motif_name']) && ( ! empty($motif_data['motif_name']))) {
	                print "<tr><td>" . $motif_data['motif_name'] . "</td><td>" . $motif_data['motiftype_name'] . "</td><td>" . $motif_data['motif_abbreviation'] . "</td><td><a href='search.php?st=motif&motif=" . $motif . "'>" . $motif_data['count'] . "</a></td><td><a href='#" . $motif_data['motif_abbreviation'] ."'>" . $motif . " info</a></td></tr>\n";
	            } else {	            
	                print "<tr><td>" . get_motif_fullname($db, $motif) . "</td><td>-</td><td>" . $motif . "</td><td>0</td><td><a href='#" . $motif ."'>" . $motif . " info</a></td></tr>\n";
	            }
	          }	          	
		?>
		
		
		</table>
		
		
		<br><br><br><br><br><br>
		
		<h2>Motif info</h2>
		
		
		<?php include("motif_info.php"); ?>
		
<?php
// show red error bar on bottom of screen if enabled and error list is non-empty
if($DO_SHOW_ERROR_LIST) {
  show_the_errors($SHOW_ERROR_LIST);  
}
?>

		
										
</div><!-- end container and contentText -->
</div><!-- end wrapper -->

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
