<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="">
	<link rel="shortcut icon" href="../../docs-assets/ico/favicon.png">

	<title>PTGL 3.0 -- Publications</title>

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
		<h2> Publications </h2>
		<br>

		<ul id="publicationsList">
			<li>Koch I, Kreuchwig A, May P (2013). Hierarchical representation of supersecondary structures using a graph-theoretical approach. Journal of Methods in molecular biology 2013;932:7-33. (<a href="http://www.ncbi.nlm.nih.gov/pubmed/22987344">/pubmed/22987344</a>).</li>
			<li>Schäfer T, May P, Koch I (2012). Computation and Visualization of Protein Topology Graphs Including Ligand Information. German Conference on Bioinformatics 2012; 108-118. (<a href="http://drops.dagstuhl.de/opus/volltexte/2012/3722">DROPS</a>).</li>
			<li>May P, Kreuschwig A, Steinke T, Koch I (2009). PTGL - a database for secondary structure-based protein topologies. Nucleic Acids Research, <a href="http://nar.oxfordjournals.org/cgi/content/abstract/gkp980?ijkey=LzGHNiRmy73nxhR&keytype=ref">10.1093/nar/gkp980</a> (Database issue 2010). </li>
			<li>May P, Barthel S, Koch I (2004). PTGL - Protein Topology Graph Library. Bioinformatics 20(17):3277-3279.</li>
			<li>Koch I, Lengauer T (1997) Detection of distant structural similarities in a set of proteins using a fast graph-based method. Proceedings of the Fifth International Conference on Intelligent Systems for Molecular Biology, 21.-26 Juni, Halkidiki, Greece AAAI Press, California. eds. T. Gaasterland, P. Karp, K. Karplus, C. Ouzounis, C. Sander, A. Valencia:167-187. </li>
			<li>Koch I, Wanke E, Lengauer T (1996) An algorithm for finding maximal common subtopologies in a set of proteins. Journal of Computational Biology 3(2):289-306. </li>
			<li>Koch I, Kaden F, Selbig J (1992) Analysis of Protein Sheet Topologies by Graph Theoretical Methods. Proteins: Structure, Function, and Genetics 12:314--323.</li>
			<li>Kaden K, Koch I, Selbig J (1990) Knowledge-based prediction of protein structures. Journal of Theoretical Biology 147(1):85-100.</li>
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
