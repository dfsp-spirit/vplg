<!DOCTYPE html>
<?php 
include('./backend/config.php'); 

$title = "Citing";
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

	<div class="container" id="citing">
		<h2> Citing VPLG</h2>
		<br>
		<!--
		<p>A publication explaining the method in detail has been published in 2013:</p>
		<p>BibTeX - Entry:</p>

		<div id="bibtex">
		@incollection{<br/>
		year={2013},<br/>
		isbn={978-1-62703-064-9},<br/>
		booktitle={Protein Supersecondary Structures},<br/>
		volume={932},<br/>
		series={Methods in Molecular Biology},<br/>
		editor={Kister, Alexander E.},<br/>
		doi={10.1007/978-1-62703-065-6_2},<br/>
		title={Hierarchical Representation of Supersecondary Structures Using a Graph-Theoretical Approach},<br/>
		url={http://dx.doi.org/10.1007/978-1-62703-065-6_2},<br/>
		publisher={Humana Press},<br/>
		keywords={Graph-theory; Supersecondary structure; Protein graph; Folding graph; Adjacent notation; Reduced notation; Key notation; Sequence notation; Greek key; Four-helix bundle; Globin fold; Up-and-down barrel; Immunoglobulin fold; β-Propeller; Jelly roll; Rossman fold; TIM barrel; Ubiquitin roll; αβ-Plaits},<br/>
		author={Koch, Ina and Kreuchwig, Annika and May, Patrick},<br/>
		pages={7-33},<br/>
		language={English}<br/>
		}<br/>
		</div>
		<br>
		<p>Please use this BibTeX-entry for citing.</p>
-->

	<!-- BiBTex Entry Tim -->

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
  <br>
  
  <div id="bibtex">
        @article{May01012010,<br>
        author = {May, Patrick and Kreuchwig, Annika and Steinke, Thomas and Koch, Ina}, <br>
        title = {PTGL: a database for secondary structure-based protein topologies},<br>
        volume = {38}, <br>
        number = {suppl 1}, <br>
        pages = {D326-D330}, <br>
        year = {2010}, <br>
        doi = {10.1093/nar/gkp980}, <br>
        URL = {http://nar.oxfordjournals.org/content/38/suppl_1/D326.abstract}, <br>
        eprint = {http://nar.oxfordjournals.org/content/38/suppl_1/D326.full.pdf+html}, <br>
        journal = {Nucleic Acids Research} <br>
        }<br>
  </div>
  <br>
		<p>Please use one of this BibTeX-entries for citing.</p>


	
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