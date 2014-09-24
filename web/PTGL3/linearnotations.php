<!DOCTYPE html>
<?php 
include('./backend/config.php');
include('./backend/get_linearnotations.php');

$title = "Linear notations";
$title = $SITE_TITLE.$TITLE_SPACER.$title;
?>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="">
	<link rel="shortcut icon" href="../../docs-assets/ico/favicon.png">

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
		<h2> Publications related to the VPLG and PTGL</h2>
		<br>
		
		<span id="multipleDownload">Download checked proteins as 
		  <select id="multidown" name="graphtype">
			<option class="downloadOption" value="1">Alpha</option>
			<option class="downloadOption" value="2">Beta</option>
			<option class="downloadOption" value="3">Alpha-Beta</option>
			<option class="downloadOption" value="4">Alpha-Ligand</option>
			<option class="downloadOption" value="5">Beta-Ligand</option>
			<option class="downloadOption" value="6">Alpha-Beta-Ligand</option>
		  </select>
		</span>
		
		<span id="multipleDownload">Download checked proteins as 
		  <select id="multidown" name="notationtype">
			<option class="downloadOption" value="ADJ">ADJ</option>
			<option class="downloadOption" value="RED">RED</option>
			<option class="downloadOption" value="KEY">KEY</option>
			<option class="downloadOption" value="SEQ">SEQ</option>
		  </select>
		</span>
		
		
		<div class="container" id="searchResults">
			<h2> Search Results </h2>
			<?php echo $tableString; /* The table string is constructed in backend/search.php, which is included by this file. */  ?>
		</div><!-- end container and searchResults -->

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
