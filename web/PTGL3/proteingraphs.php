<?php session_start(); ?>
<!DOCTYPE html>
<?php 
include('./backend/config.php');
$SHOW_ERROR_LIST = array();


$title = "Protein graphs";
$title = $SITE_TITLE.$TITLE_SPACER.$title;
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
		<h2>All protein graphs of a chain</h2>
		<br>
		
		<div id="PageIntro">
		<div class="container" id="pageintro">
		For each protein chain in the database, the following protein graph types are available:<br><br>
		<ul>
		<li>the alpha graph: Contains all helices of the chain (vertices) and their 3D contacts (edges).</li>
		<li>the beta graph: Contains all beta strands of the chain and their 3D contacts.</li>
		<li>the the alpha-beta graph: Also known as the albe graph. Contains all helices and strands of the chain and their 3D contacts.</li>
		<li>the alpha-ligand graph: Contains all helices and ligands of the chain and their 3D contacts.</li>
		<li>the beta-ligand graph: Contains all beta strands and ligands of the chain and their 3D contacts.</li>
		<li>the the alpha-beta-ligand graph: Also known as the albelig graph. Contains all helices, strands, and ligands of the chain and their 3D contacts.</li>

		</ul>
		Enter the PDB and chain below. You will then be able to browse all protein graph types.
		
		</div><!-- end container-->
		</div><!-- end Home -->
		
		<form class="form-inline" action="results.php" method="get">
			
		<label>Enter PDB identifier and chain, e.g., '7timA':
		<?php
		  echo '<input type="text" class="form-control" maxlength="5" name="q" id="search_pgs_of_chain_pdbchain" placeholder="Enter PDB ID and chain">';		
		?>
		</label>
							
		<button type="submit" id="sendit_all_pgs_of_chain" "class="btn btn-default">Search <span class="glyphicon glyphicon-search"></span></button><br>

		</form>	
							

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
