<?php session_start(); ?>
<!DOCTYPE html>
<?php 
include('./backend/config.php'); 
include('./backend/search.php');

$title = "Search for proteins";
$title = $SITE_TITLE.$TITLE_SPACER.$title;

?>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="VPLG">
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
	         			
		
	
		<div class="container">
			<div class="row" id="load">
			<div class="col-lg-3 col-centered">
				<div class="input-group form-group">
					<form name="protChains" class="form-inline" method="get" action="results.php">
						<input type="text" class="form-control" name="q" id="loadInput" autocomplete="off" placeholder="Load proteins...">
						<button type="submit" class="btn btn-default" id="loadButton"><span>Load Proteins</span></button>
				<div class="additionalProteinButtons">
					<button type="button" class="btn btn-default btnSize" id="selectAllBtn"><span>Select all <?php echo $numberOfChains; ?> protein chains</span></button>
					<button type="button" class="btn btn-default btnSize protButton" id="resetBtn"><span>Deselect all</span></button>
				</div>
				</div><!-- end input-group and form-group -->
			</div><!-- end col-centered -->
		</div><!-- end row -->
		</div>
		<div class="container" id="searchResults">
			<?php 
			    $search_type = "advanced";
			    if(count($list_of_search_types) === 1) {
			      $search_type = $list_of_search_types[0];
			    }
			    
			    echo "<h2> Search Results -- $search_type</h2>";
			    ?>
			    
			    <div class="container" id="pageintro">
		All protein chains which match your search are shown below. Select the ones you are interested in by clicking the checkboxes next to them, then click 'Load proteins' above. You can also select all results at once or clear the current selection using the buttons above.
		</div><!-- end container-->
			    
			    <?php
			    
			    echo $tableString; /* The table string is constructed in backend/search.php, which is included by this file. */  
			    
			    //if($numberOfChains <= 4) {
			    //  echo "<p>Your search returned $numberOfChains results. Please modify the query and try again. DEBUG: '$debug_msg'.</p>\n";
			    //}
			?>
			</form>	
		</div><!-- end container and searchResults -->
	</div><!-- end container-->
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
