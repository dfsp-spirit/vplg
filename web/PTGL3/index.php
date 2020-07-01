<!DOCTYPE html>
<?php

ini_set('display_errors',1);
ini_set('display_startup_errors', 1);
ini_set('log_errors', TRUE);
error_reporting(E_ALL);

include('./backend/config.php'); 

$title = "The Protein Topology Graph Library";
$title = $SITE_TITLE.$TITLE_SPACER.$title;

if (session_id()) {
	session_unset();
	session_destroy();
}

function check_install($db)  {
    

}

?>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="VPLG PTGL protein graph database">
	<meta name="author" content="The MolBI group">
	<link rel="shortcut icon" href="favicon.ico?v=1.0" type="image/x-icon" />

	<title><?php echo $title; ?></title>

	<!-- Mobile viewport optimized -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scale=1.0, user-scalable=no"/>

	<!-- Bootstrap CSS -->
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="css/bootstrap-glyphicons.css">

	<!-- Custom CSS -->
	<link rel="stylesheet" href="css/font-awesome.css"/>
	<link rel="stylesheet" type="text/css" href="css/styles.css">
	<!--<link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet"> -->

	<!-- Include Modernizr in the head, before any other JS -->
	<script src="js/modernizr-2.6.2.min.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
</head>

<body id="customBackground">
	<noscript>
		<META HTTP-EQUIV="Refresh" CONTENT="0;URL=errorJS.php">
	</noscript>
	<div class="wrapper">

            <?php include('navbar.php'); ?>

	<div id="Home">
		<div class="container" id="intro">
		<!-- Intro message -->
		
		    <?php
			
			  if($SHOW_MAINTENANCE_MESSAGE_ON_FRONTPAGE) {
			      echo "\n" . '<br><div class="boxedred"><p><br>&nbsp;&nbsp;' . "\n";
				  echo $MAINTENANCE_MESSAGE;
				  echo "</p></div><br>\n";
			  }
			
			  if($CHECK_INSTALL_ON_FRONTPAGE) {
			      $conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
			      $db = pg_connect($conn_string);                          
                              
			      $db_ok = $db;
			      $tmp_dir_ok = is_writable('./temp_downloads/');
			      $data_ok = is_dir($IMG_ROOT_PATH);
			      
			      $num_errors_occured = 0;
			      if(!$db_ok) { $num_errors_occured++; }
			      if(!$tmp_dir_ok) { $num_errors_occured++; }
			      if(!$data_ok) { $num_errors_occured++; }
			      
			      			      
			      if($num_errors_occured > 0) {
				  echo "\n" . '<br><div class="boxedred">' . "\n<ul>\n";
				  echo "<p><b>ERROR:</b> Server Installation incomplete,  the server admin needs to fix this. The following $num_errors_occured errors were detected:</p>";
				
				  if(! $db_ok) {
				      echo "<li>The database connection is not configured properly.</li>\n";
				  }
				  
				  // check whether tmp download dir (where the zip files are stored for download) is writable
				  if ( ! $tmp_dir_ok) {
				      echo "<li>The temporary file directory is not writable, ZIP file downloads disabled.</li>\n";
				  }
				  
				  // check for existence of image data directory
				  if ( ! $data_ok) {
				      echo "<li>The data directory does not exist, graph images and other data missing.</li>\n";
				  }
				  
				  echo "</ul></div><br>\n";
			      }
			  }
	
			?>
			
			
			<p>
				The <?php echo $SITE_TITLE; ?> web server provides a database of protein secondary structure topologies [Schäfer et al., 2015, <i>Bioinformatics</i>; May et al., 2010, <i>Nucleic Acids Res.</i>; May et al., 2004, <i>Bioinformatics</i>].
				It is based on protein-ligand graphs computed by the <a href="http://www.bioinformatik.uni-frankfurt.de/tools/vplg/" target="_blank">VPLG software</a>.
				The <?php echo $SITE_TITLE; ?> uses a graph-based model to describe the structure of proteins on the super-secondary structure level.
			</p>
			
			<!--
			A protein ligand graph is
			computed from the atomic coordinates in a PDB file and the secondary structure assignments of the DSSP algorithm. In this graph, vertices
			represent secondary structure elements (SSEs, usually alpha helices and beta strands) or ligand molecules while the edges model contacts and 
			relative orientations between the SSEs.
			-->
			<br /><br />
			
			<p class="imgCenter"><img src="./images/ptgl_overview_trans.png" width="600"></p>
			
			<br /><br />
			This web server allows you to search for <b>protein motifs</b> which can be detected in the graphs. It also provides <b>standardized 2D visualizations</b> of protein graphs and folding graphs.
			In contrast to the manually curated <a href="http://www.cathdb.info/" target="_blank">CATH</a> and <a href="http://scop.mrc-lmb.cam.ac.uk/scop/" target="_blank">SCOP</a> databases, the method used by this server is fully automated.
			Similar servers, which also support substructure search, include <a href="http://prodata.swmed.edu/prosmos/" target="_blank">ProSMoS</a> and <a href="http://munk.csse.unimelb.edu.au/pro-origami/" target="_blank">Pro-Origami</a>.
			
			<?php if($ENABLE_GRAPHLETSIMILARITY_SEARCH) { ?>
			Additionally, this server implements graph-based similarity measures to search for protein chains which are similar to a given query protein.
			<?php } ?>
			
			<br>
			
			
		</div><!-- end container-->
	</div><!-- end Home -->




		<div class="row" id="search">
			<p><center>Need help? See the <a href="about.php#guidePgFg">user guide</a> for a worked out Protein Graph search.</center></p>
		<div class="col-lg-4"></div>
			<div class="col-lg-4">
				<div class="input-group form-group">
					<form class="form-inline" action="search.php" method="get">
						<input type="text" class="form-control" name="keyword" id="searchInput" autocomplete="off" placeholder="Enter PDB ID or keyword...">
						<button type="submit" id="sendit" name="st" value="keyword" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button><br>
						<div id="advancedButton"> Advanced Search <div id="arrow"><strong class="caret"></strong></div></div>
						<div id="liveSearchResults"></div>

						
					</form>							
						
					<form class="form-inline" action="search.php" method="get">
						<div id="advancedSearch">
							<p>	Need help? See the <a href="about.php#guideAdvanced">user guide</a> for a worked out Advanced Search.</p>
							<br>

							<p style="text-align:right;">Hover over for info.</p>

							<?php if($USE_LOGIC_OPERATORS) { ?>
							<label class="advancedLabel">Logic operation:&nbsp;&nbsp;&nbsp;&nbsp;
								<input class="radio" type="radio" name="logic" value="AND"> &nbsp;&nbsp;AND&nbsp;
								<input class="radio" type="radio" name="logic" value="OR" checked> &nbsp;&nbsp;OR&nbsp;
							</label>
							<?php } ?>

							<label class="advancedLabel">PDB Identifier
								<input class="advancedInput" type="text" id="pdbid" name="pdbid" placeholder="PDB ID" size="6" maxlength="4" />
								<i title="Use the PDB ID. For example '7tim' or '8icd'." style="position:absolute; right:50px;"  class="fa fa-question"></i>
							</label>

							<label class="advancedLabel">Title
								<input class="advancedInput" type="text" id="title" name="title" placeholder="Title" size="20" maxlength="50"/>
								<i title="Search for keywords in the protein title, e.g., 'Human'." style="position:absolute; right:50px;"  class="fa fa-question"></i>
							</label>

							<label class="advancedLabel">Has Ligand
								<select class="advancedInput" id="hasligand" name="hasligand">
									<option value="null">Both</option>
									<option value="1">Yes</option>
									<option value="0">No</option>
								</select>
								<i title="Choose whether the protein has a ligand or not." style="position:absolute; right:50px;"  class="fa fa-question"></i>
							</label>
							<label class="advancedLabel">Ligand Name
								<input class="advancedInput" type="text" id="ligandname" name="ligandname" placeholder="Ligand Name" size="20" maxlength="3"/>
								<i title="Search for proteins which contain a special ligand. Use the 3-letter ligand codes from RCSB Ligand Expo, e.g., 'FAD'. You can also use parts of the ligands long name." style="position:absolute; right:50px;"  class="fa fa-question"></i>
							</label>	
							<label class="advancedLabel">Molecule
								<input class="advancedInput" type="text" id="molecule" name="molecule" placeholder="Molecule" size="20" maxlength="50"/>
								<i title="Search for chains which appear in certain molecules e.g. 'MYOSINE'" style="position:absolute; right:50px;"  class="fa fa-question"></i>
							</label>		
							<button type="submit" id="sendit_advanced" name="st" value="advanced" class="btn btn-default advancedInput" style="margin-top:35px;"><span>Search</span></button>
						</div>
					</form>	
				</div><!-- end input-group and form-group -->
			</div><!-- end col-centered -->
			<div class="col-lg-4"></div>
		</div><!-- end row -->

		<div class="row">
			<div class="col-lg-3 col-lg-3-centering col-centered">
				<div class="input-group form-group">
					<form> 
					<dl class="dl-horizontal">
				<div id="additionalSearch">
				<dt>SearchKey</dt>
				<dd>Keyword search <strong class="caret" id="flipArrow" ></strong></dd>
				</div>
				<div id="addSearchKey">
					<dd>Use the standard search form above to search protein topologies by keyword.</dd>
					<dd>Enter PDB IDs like '7tim' or keywords known to relate to the biological macromolecules like 'kinase' <br>
					of interest and select the <span class="glyphicon glyphicon-search"></span>"Search" button.</dd>
				</div>
			</dl></form>

			<?php if($ENABLE_MOTIF_SEARCH) { ?>
			<form>
			<dl class="dl-horizontal">
				<div id="additionalSearch2">
				<dt>Search Motifs</dt>
				<dd>Search for topological protein structure motifs <strong class="caret" id="flipArrow2" ></strong></dd>
				</div>
				<div id="addSearchMotif">
					<p>	Need help? See the <a href="about.php#guideMotif">user guide</a> for a worked out motif search.</p>
				<br>
					<h4>Alpha Motifs</h4>
					<div class="motifimagecontainer">
					   <div class="motiftext">Four Helix Bundle (<a href="motif_overview.php#4helix">?</a>)</div>
					   <a href="search.php?motif=4helix&st=motif"><img class="motifimage" src="./images/4helixbeide_struktur.jpg" /></a></div>
					<div class="motifimagecontainer">
					   <div class="motiftext">Globin Fold (<a href="motif_overview.php#globin">?</a>)</div>
					   <a href="search.php?motif=globin&st=motif"><img class="motifimage" src="./images/globin_struktur.jpg" /></a></div>


					<h4>Beta Motifs</h4>
					<div class="motifimagecontainer">
					   <div class="motiftext">Up and Down Barrel (<a href="motif_overview.php#barrel">?</a>)</div>
					   <a href="search.php?motif=barrel&st=motif"><img class="motifimage" src="./images/barrel_struktur.jpg" /></a></div>	
					<div class="motifimagecontainer">
					   <div class="motiftext">Immunoglobulin Fold (<a href="motif_overview.php#immuno">?</a>)</div>
					   <a href="search.php?motif=immuno&st=motif"><img class="motifimage" src="./images/immuno_struktur.jpg" /></a></div>
					<div class="motifimagecontainer">
					   <div class="motiftext">Beta Propeller (<a href="motif_overview.php#propeller">?</a>)</div>
					   <a href="search.php?motif=propeller&st=motif"><img class="motifimage" src="./images/propeller_struktur.jpg" /></a></div>	
					<div class="motifimagecontainer">
					   <div class="motiftext">Jelly Roll (<a href="motif_overview.php#jelly">?</a>)</div>
					   <a href="search.php?motif=jelly&st=motif"><img class="motifimage" src="./images/jelly_struktur.jpg" /></a></div>		

                                        <?php if($ENABLE_MOTIF_SEARCH_ALPHABETA) { ?>
					   
					<h4>Alpha Beta Motifs</h4>
					
					<div class="motifimagecontainer">
					   <div class="motiftext">Ubiquitin Roll (<a href="motif_overview.php#ubi">?</a>)</div>
					   <a href="search.php?motif=ubi&st=motif"><img class="motifimage" src="./images/ubibeide_struktur.jpg" /></a></div>	
					
					
					<div class="motifimagecontainer">
					   <div class="motiftext">Alpha-Beta Plait (<a href="motif_overview.php#plait">?</a>)</div>
					   <a href="search.php?motif=plait&st=motif"><img class="motifimage" src="./images/plait_struktur.jpg" /></a></div>
					<div class="motifimagecontainer">
					   <div class="motiftext">Rossman Fold (<a href="motif_overview.php#rossman">?</a>)</div>
					   <a href="search.php?motif=rossman&st=motif"><img class="motifimage" src="./images/rossman_struktur.jpg" /></a></div>	
					<div class="motifimagecontainer">
					   <div class="motiftext">TIM Barrel (<a href="motif_overview.php#tim">?</a>)</div>
					   <a href="search.php?motif=tim&st=motif"><img class="motifimage" src="./images/tim_struktur.jpg" /></a></div>	
					   <?php } ?>

				</div>
			</dl>
			</form>
			<?php } ?>

			<?php if($ENABLE_BLAST_SEARCH) { ?>
			<form class="form-inline" action="search.php" method="get">
			<dl class="dl-horizontal">
				<div id="additionalSearch3">
				<dt>SearchSequence</dt>
				<dd>BLAST search for sequences <strong class="caret" id="flipArrow3" ></strong></dd>
				</div>
				<div id="addSearchSequence">
				<p>Search strings are case insensitive.</p>
				</div>

			</dl></form>
			<?php } ?>
			
			</div><!-- end input-group and form-group -->
			
			<?php if($ENABLE_COMPLEX_GRAPHS) { ?>
			<div class="input-group form-group">
			<form class="form-inline" action="complexgraphs.php" method="get">
			<dl class="dl-horizontal">
				<div id="additionalSearchComplex">
				<dt>Complex graphs</dt>
				<dd>Search for protein complexes <strong class="caret" id="flipArrowComplex" ></strong></dd>
				</div>
				<div id="addSearchComplex">
					<p>	Need help? See the <a href="about.php#guideCg">user guide</a> for a worked out Complex Graph search.</p>
					<br>

				<p>Enter a query PDB ID, e.g., '4a97' to display complex information for that PDB file.</p>								                 
						<input type="text" class="form-control" name="pdb" id="searchComplex" autocomplete="off" placeholder="Enter a query PDB ID...">
						<input type="hidden" name="graphtype_int" value="6">
						<button type="submit" id="sendit_complex" name="stc" value="complex_pdbid" class="btn btn-default" ><span class="glyphicon glyphicon-search"></span></button><br>

			</form></div>
			</dl>
			</div><!-- end input-group and form-group -->
			<?php } ?>
			
			
			<?php if($ENABLE_GRAPHLETSIMILARITY_SEARCH) { ?>
			<div class="input-group form-group">
			<form class="form-inline" action="search.php" method="get">
			<dl class="dl-horizontal">
				<div id="additionalSearch4">
				<dt>BioGraphletSimilarity</dt>
				<dd>Protein structure similarity search powered by BioGraphlets <strong class="caret" id="flipArrow4" ></strong></dd>
				</div>
				<div id="addSearchGraphletSimilarity">
				<p>Enter a query PDB ID and chain, e.g., '7timA' to search for similar chains.</p>								                 
						<input type="text" class="form-control" name="graphletsimilarity" id="searchGraphlets" autocomplete="off" placeholder="Enter a query PDB ID and chain...">
						<button type="submit" id="sendit_graphlets" name="st" value="similarity" class="btn btn-default" ><span class="glyphicon glyphicon-search"></span></button><br>

			</form></div>
			</dl>
			</div><!-- end input-group and form-group -->
			<?php } ?>
			
			
			<?php if($ENABLE_CUSTOMLINNOT_SEARCH) { ?>
			<div class="input-group form-group">
			<form class="form-inline" action="search.php" method="get">
			<dl class="dl-horizontal">
				<div id="additionalSearch5">
				<dt>Custom linnot</dt>
				<dd>Search for folding graph linear notation strings <strong class="caret" id="flipArrow5" ></strong></dd>
				</div>
				<div id="addSearchCustomLinnots">
					<p>	Need help? See the <a href="about.php#guideLinnot">user guide</a> for a worked out linear notation search.</p>
					<br>

				<p>Select the graph type and notation, then enter a query linear notation string, e.g., '[e,10ae,-1ae,-7ae]'. Note that this searches the whole database and may take some minutes.</p>
				
				                Search the 
				                <select class="notation">
						  <option value="adj" selected>ADJ</option>
						  <option value="red">RED</option>
						  <option value="seq">SEQ</option>
						  <option value="key">KEY</option>
						</select> 
				                notation of all 
				                <select class="graphtype">
						  <option value="alpha">alpha</option>
						  <option value="beta">beta</option>
						  <option value="albe">albe</option>
						  <option value="alphalig">alphalig</option>
						  <option value="betalig">betalig</option>
						  <option value="albelig" selected>albelig</option>
						</select> 
						graphs in the database for: 
						<input type="text" class="form-control" id="searchLinnots" autocomplete="off" placeholder="Enter query notation string...">
						<!-- <input type="checkbox" name="matching" value="like"> contains -->
						<input type="hidden" name="st" value="customlinnot">
						<!--
						<input type="radio" name="matching" value="like"> as substring
						<input type="radio" name="matching" value="exact" checked> exact
						-->
						<input type="hidden" name="matching" value="exact">
						<button type="submit" id="sendit_linnots" name="linnotalbeligadj" value="linnotalbeligadj" class="btn btn-default" ><span class="glyphicon glyphicon-search"></span></button><br>

			</form></div>
			</dl>
			</div><!-- end input-group and form-group -->
			<?php } ?>
			
			
			<?php if($ENABLE_RANDOM_SEARCH) { ?>
			<div class="input-group form-group">
			<form class="form-inline" action="search.php" method="get">
			<dl class="dl-horizontal">
				<div id="additionalSearch6">
				<dt>Random</dt>
				<dd>Selects a few random protein chains to explore.<strong class="caret" id="flipArrow6" ></strong></dd>
				</div>
				<div id="addSearchRandom">
				Selects and loads some random protein chains from the database. 
				<!-- <input type="hidden" name="num_random" id="num_random" value="10"> -->
				<select name="num_random">
				  <option value="5">5</option>
				  <option value="10" selected>10</option>
				  <option value="15">15</option>
				  <option value="20">20</option>
				  <option value="25">25</option>
				</select> 
				<button type="submit" id="sendit_random" name="st" value="random" class="btn btn-default" ><span class="glyphicon glyphicon-search"></span></button><br>

			</form></div>
			</dl>
			</div><!-- end input-group and form-group -->
			<?php } ?>

			
				
			</div><!-- end col-centered -->
		</div><!-- end row -->




	</div><!-- end container and wrapper -->


<div class="textWrapper">
	<div id="About">
		<div class="line" id="lineAbout">
			<div class="iconPos" id="iconAbout">
				<img  width="100px" height="100px" src="images/icon-blueGlyph.png" alt="Icon">
				<div id="About"></div>
			</div><!-- end iconPos -->
		</div><!-- end line and lineAbout -->

		<div class="container" id="text">
			<h2>Introduction</h2>
				<!--
				PTGL is a web-based database application for protein topologies. In order to define a mathematically unique description of protein topology
				the secondary structure topology of a protein is described by methods of applied graph theory. The <a href="about.php#proteinGraph">Protein graph</a> is defined as an undirected
				labelled graph on three description levels according to the considered secondary structure elements (SSE): the <a href="about.php#alphaGraph">Alpha graph</a>, the <a href="about.php#betaGraph">Beta graph</a>,
				and the <a href="about.php#alphaBetaGraph">Alpha-Beta graph</a>. The connected components of the <a href="about.php#proteinGraph">Protein graph</a> form <a href="about.php#foldingGraph">Folding graphs</a>. A <a href="about.php#proteinGraph">Protein graph</a> can consist of one or more
				<a href="about.php#foldingGraph">Folding graphs</a>. The three graph types were defined for each protein of the <a href="http://www.rcsb.org/pdb/" target="_blank">PDB</a>. For each graph type exists four <a href="about.php#linearNot">linear notations</a> with
				corresponding graphic representations. In PTGL all <a href="about.php#foldingGraph">Folding graphs</a>, all SSEs, and additional protein information are stored for every
				protein structure annotated in <a href="http://www.rcsb.org/pdb/" target="_blank">PDB</a> for which SSEs according DSSP are defined, which is not a NMR structure, has a resolution less than 3.5
				Å and a sequence length of at least 20 amino acids. The database enables the user to search for the topology of a protein or for certain
				topologies and subtopologies using the <a href="about.php#linearNot">linear notations</a>. Additionally, it could be searched for sequence similarity in <a href="http://www.rcsb.org/pdb/" target="_blank">PDB</a> sequences.
				-->
				<!-- old information, we now include the introduction snippet so we have no double text
				<?php echo "$SITE_TITLE"; ?> is a web-based database application for the analysis of protein topologies. It uses a graph-based model to describe the structure
				of protein chains on the super-secondary structure level. A protein graph is computed from the 3D atomic coordinates of a single chain in
				a PDB file and the secondary structure assignments of the DSSP algorithm. In a protein graph, vertices represent secondary
				structure elements (SSEs, usually alpha helices and beta strands) or ligand molecules while the edges model contacts and relative orientations between
				them. 
				-->

				<!-- include the introduction snippet -->
				<?php 
					include("snippets/introduction.php");
				?>
				<a href="about.php#ptgl">Read more about <?php echo "$SITE_TITLE"; ?>...</a>


		</div><!-- end container and text -->
	</div><!-- end About -->

	
	<!-- I add a large space here, so that there is a difference between clicking 'About' and 'UserGuide' in the top navigation bar. -->		
	<!-- <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
	<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br> -->
		
	<div id="UserGuide">
		<div class="line" id="lineUserGuide">
			<div class="iconPos" id="iconUserGuide">
				<img width="100px" height="100px" src="images/icon-greenGlyph.png" alt="Icon"/>
			</div><!-- end iconPos -->
		</div><!-- end line and lineUser Guide -->

		<div class="container" id="text">
			<h2>User Guide</h2>

			<p>
				You can find worked out step-by-step examples with screenshots of the output of the following topics:
				
				<!-- Include table of contents links of the user guide -->
				<?php include("snippets/guide_toc.php"); ?>

				Not found what you were looking for? <a href="contact.php">Contact</a> and tell us!
			</p>
			
		<!--
			<div id="PageIntro">
		<div class="container" id="pageintro">
		This user guide explains how to use the different search options. You can click the text boxes to get more detailed information.
		<br><br>
		If you need want to learn about protein graphs, are interested in understanding how this web server works, or need help interpreting the results, see <a href="about.php">the About page</a>.
		-->

		</div><!-- end container-->
		</div><!-- end Home -->

<!--
	Here was the old code of the about's user guide help texts creating this colored text boxes which could be clicked for more information.
	It can be found in ~niclas/Documents/PTGL/web/archive/index/user_guide_help_boxes.php
-->


		</div><!-- end container and text -->
	</div><!-- end UserGuide -->


</div><!--- end textWrapper -->

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
	<script src="js/userguide.js" type="text/javascript"></script>

	<!-- Live Search for PDB IDs -->
	<script src="js/livesearch.js" type="text/javascript"></script>

</body>
</html>
