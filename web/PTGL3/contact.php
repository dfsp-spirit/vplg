<!DOCTYPE html>
<?php 
include('./backend/config.php'); 

$title = "Contact";
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

	<div class="container" id="contactUs">
		<h2> Contact Us </h2>
		<br>
		<h4> List of people associated with VPLG </h4>


	<div id="contact">
	<div class="row" id="member1">
		<div class="col-sm-6">
			 <div class="leftColumn">
				<strong>Prof. Dr. Ina Koch</strong>
				<br>
				Johann Wolfgang Goethe-University Frankfurt am Main
				<br>
				Faculty of Computer Science and Mathematics, Dept. 12
				<br>
				Institute for Computer Science
				<br>
				Molecular Bioinformatics (MBI)
				<br>
				Robert-Mayer-Strasse 11-15
				<br>
				60325 Frankfurt am Main
				<br>
				Germany
			</div><!-- end leftColumn -->
		</div><!-- end col-sm-6 -->

		<div class="col-sm-6">
			<div class="rightColumn">
				<br>
				<i class="fa fa-phone"></i> Phone  +49 +69 798-24652
				<br>
				Fax    +49 +69 798-24650
				<br>
				<i class="fa fa-envelope-o"></i> e-mail:  ina.koch (at) bioinformatik.uni-frankfurt.de
				<br>
				<i class="fa fa-external-link"><a href="http://www.bioinformatik.uni-frankfurt.de" target="_blank"></i> http://www.bioinformatik.uni-frankfurt.de/</a>
			</div><!-- end rightColumn -->
		</div><!-- end col-sm-6 -->
	</div><!-- end row -->

	<div class="row" id="member3">
		<div class="col-sm-6">
			<div class="leftColumn">
				<strong>Tim Schäfer</strong>
				<br>
				Johann Wolfgang Goethe-University Frankfurt am Main
				<br>
				Faculty of Computer Science and Mathematics
				<br>
				Institute for Computer Science
				<br>
				Molecular Bioinformatics (MBI)
				<br>
				Robert-Mayer-Strasse 11-15
				<br>
				60325 Frankfurt am Main
				<br>
				Germany
			</div><!-- end leftColumn -->
		</div><!-- end col-sm-6 -->

		<div class="col-sm-6">
			<div class="rightColumn">
				<br>
				<i class="fa fa-phone"></i> Phone  +49 +69 798-24655
				<br>
				<i class="fa fa-envelope-o"></i> e-mail:  tim.schaefer (at) bioinformatik.uni-frankfurt.de
				<br>
				<i class="fa fa-external-link"><a href="http://www.bioinformatik.uni-frankfurt.de/tools/vplg/" target="_blank"></i>http://www.bioinformatik.uni-frankfurt.de/tools/vplg/</a>
			</div><!-- end rightColumn -->
		</div><!-- end col-sm-6 -->
	</div><!-- end row -->
	</div><!-- end contact -->
	
	
	<div id="PageIntro">
		<div class="container" id="pageintro">
		<strong>Authors</strong><br>
		The VPLG software and the database were written and designed by Tim Schäfer. The web interface was written by Daniel Bruneß, Andreas Scheck and Tim Schäfer.
		</div><!-- end container-->
		</div><!-- end Home -->

</div><!-- end container -->
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