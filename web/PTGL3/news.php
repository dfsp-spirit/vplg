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
		<br/>
		<h2> PTGL news</h2>
		<br>
		
		<p>Here we list important information on PTGL updates, planned maintenance times, and more.</p>
		<br /><br/>

		<h4>MAR 02, 2020: Updating server to latest PDB release</h4>
		<p>We are currently rolling out an Update to the PTGL server. Until finished, the server may be unavailable. This update will also fix some issues with large structures' CGs.</p>
		<br /><br/>

		<h4>SEP 11, 2019: Webserver maintenance from SEP 20-27</h4>
		<p>Our webserver hosting the PTGL will be under maintenance from September 20 to 27. During this time our webserver and all services may be partly unavailable.</p>
		<br /><br/>

		<h4>MAY 14, 2019: Updating server to latest PDB release</h4>
		<p>We are currently running an update to the latest PDB version on our cluster. The update will be rolled out to the PTGL server in the next few weeks. In this time the server may be unavailable. This update will also include large structures (> 99,999 atoms or > 62 chains) for the first time.</p>
		<br /><br/>

		<h4>APR 26, 2019: VPLG code forked to and maintained from new repository</h4>
		<p>The code of VPLG, the command line application which computes all the data in the PTGL database, has been forked to a <a href="https://github.com/MolBIFFM/vplg" target="_blank">new repository</a>. All future developments of the code will be published there.</p>
		<br /><br/>
		
		<h4>FEB 23, 2018: Updating server to latest PDB release</h4>
		<p>We are currently running an update to the latest PDB version on our cluster. The update will be rolled out to the PTGL server in the end of February. In this time the server may be unavailable. This update will also apply minor changes, for example the visualization of complex graphs.</p>
		<br /><br/>
		
		<h4>NOV 30, 2015: Major update scheduled</h4>
		<p>We are currently running an update to the latest PDB version on our cluster. The update will be rolled out to the PTGL server in December. This update will add support for complex graphs and ligand-centered graphs.</p>
		<br /><br/>
		
		
		<h4>OCT 27, 2015: New PTGL paper published</h4>
		<p>We have published a paper on the new PTGL in Oxford Bioinformatics. Read it <a href="http://bioinformatics.oxfordjournals.org/content/early/2015/10/27/bioinformatics.btv574" target="_blank">at oxfordjournals.org</a>.</p>
		<br /><br/>
		
		
		<h4>JUL 29, 2015: VPLG code moved to GitHub, project page stays at SourceForge</h4>
		<p>The code of VPLG, the command line application which computes all the data in the PTGL database, has been <a href="https://github.com/dfsp-spirit/vplg" target="_blank">moved to GitHub</a>. The <a href="https://sourceforge.net/projects/vplg/" target="_blank">project page and the VPLG releases</a> will stay at SourceForge though, so this only affects developers and people interested in the source code.</p>
		<br /><br/>
		
		
		
		<h4>JUN 06, 2015: The PTGL is on Twitter</h4>
		<p>The PTGL now has a twitter account, and you can stay up-to-date by following <a href="https://twitter.com/vplg_project" target="_blank">@vplg_project</a>.</p>
		<br /><br/>
		
		
		<h4>JUN 06, 2015: Server updated to latest PDB release</h4>
		<p>The PTGL has been updated to PDB data from May 2015, and is now online again. Enjoy.</p>
		<br /><br/>
		


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
