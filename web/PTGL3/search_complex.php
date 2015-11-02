<?php 
if(!session_id()) {session_start();} 
//echo "<pre>";
//print_r($_SESSION);
//echo "</pre>";
$SHOW_ERROR_LIST = array();
$numberOfChains = 0;
$tableString = "";

include('./backend/config.php'); 
include('./common.php');
include('./backend/search_complex_backend.php');
$DO_SHOW_ERROR_LIST = $DEBUG_MODE;
$title = "Search for protein complexes";
$title = $SITE_TITLE.$TITLE_SPACER.$title;


?>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="VPLG">
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
	         			
		
			
		<div class="container" id="searchResults">
			<?php
			    echo "<h2> Search Results -- $search_type</h2>";
			?>
			    
			    <div class="container" id="pageintro">
			
			<?php
			if($tableString) {
		echo "All protein complexes which match your search are shown below. Click a data set to load it.";
		} else {
		    echo "Missing query data. Please <a href='./index.php'>try a new search</a>.";
		}
		?>
		</div><!-- end container-->
			    
			    <?php
			    
			    echo $tableString; /* The table string is constructed in backend/search_complex_backend.php, which is included by this file. */  
			?>
				
		
	</div><!-- end container-->
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
