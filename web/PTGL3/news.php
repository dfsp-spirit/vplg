<!DOCTYPE html>
<?php 
include('./backend/config.php'); 

$title = "News";
$title = $SITE_TITLE.$TITLE_SPACER.$title;
?>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
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

	<div id="PageIntro">
		<div class="container" id="pageintro">
		<h2> PTGL news</h2>
		<br>
		
		Here we list important information on updates, planned maintenance, and more.
		<br /><br/>

		<ul id="news">
		<li><b>NOV 30, 2015: Major update scheduled<b> We are currently running an update to the latest PDB version on our cluster. The update will be rolled out to the PTGL server in December. This update will add support for complex graphs and ligand-centered graphs.</li>
		<li><b>OCT 27, 2015: New PTGL paper published<b> We have published a paper on the new PTGL in Oxford Bioinformatics. Read it <a href="http://bioinformatics.oxfordjournals.org/content/early/2015/10/27/bioinformatics.btv574" target="_blank">at oxfordjournals.org</a>.</li>
		<li><b>JUN 06, 2015: The PTGL is on Twitter<b> The PTGL now has a twitter account, and you can stay up-to-date by following <a href="https://twitter.com/vplg_project" target="_blank">@vplg_project</a>.</li>
		
		</ul>

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
