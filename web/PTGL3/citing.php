<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="">
	<link rel="shortcut icon" href="../../docs-assets/ico/favicon.png">

	<title>PTGL 3.0 -- Citing PTGL and VPLG</title>

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

	<div class="container" id="citing">
		<h2> Citing PTGL </h2>
		<br>
		<p>A publication explaining the method in detail has been published in 'Methods in Molecular Biology
			932 (Protein Supersecondary Structurs) by editor A.E. Kister in 2013.</p>
		<p>BibTeX - Entry:</p>

		<div id="bibtex">
			@PROCEEDINGS{kister:protein_supersecondary_structures,<br>
			EDITOR      = "Alexander E. Kister",<br>
			TITLE       = "Protein Supersecondary Structures",<br>
			ADDRESS     = "Piscataway, NJ",<br>
			YEAR        = 2013,<br>
			}<br>
			<br>
			@INPROCEEDINGS{koch_et_al:hierarchical_representation_of_supersecondary_structures,<br>
			AUTHOR       = "Koch, Ina and Kreuchwig, Annika and May, Patrick",<br>
			TITLE        = "Hierarchical Representation of Supersecondary Structures Using a<br>
							 Graph-Theoretical Approach",<br>
			PAGES        = "7 -- 33",<br>
			ADRESS     = "Frankfurt",<br>
			YEAR		= "2013",<br>
			}<br>

		</div>
		<br>
		<p>Please use this BibTeX-entry for citing.</p>


	<!-- BiBTex Entry Tim 

  <div id="bibtex">
	@InProceedings{schfer_et_al:OASIcs:2012:3722,<br>
	author =    {Tim Sch{\"a}fer and Patrick May and Ina Koch},<br>
	title = {{Computation and Visualization of Protein Topology Graphs Including Ligand Information}},<br>
	booktitle = {German Conference on Bioinformatics 2012},<br>
	pages = {108--118},<br>
	series =    {OpenAccess Series in Informatics (OASIcs)},<br>
	ISBN =  {978-3-939897-44-6},<br>
	ISSN =  {2190-6807},<br>
	year =  {2012},<br>
	volume =    {26},<br>
	editor =    {Sebastian B{\"o}cker and Franziska Hufsky and Kerstin Scheubert and Jana Schleicher and Stefan Schuster},<br>
	publisher = {Schloss Dagstuhl--Leibniz-Zentrum fuer Informatik},<br>
	address =   {Dagstuhl, Germany},<br>
	URL =       {http://drops.dagstuhl.de/opus/volltexte/2012/3722},<br>
	URN =       {urn:nbn:de:0030-drops-37226},<br>
	doi =       {http://dx.doi.org/10.4230/OASIcs.GCB.2012.108},<br>
	annote =    {Keywords: protein structure, graph theory, ligand, secondary structure, protein ligang graph}<br>
	}<br>
  </div>

	-->
	
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