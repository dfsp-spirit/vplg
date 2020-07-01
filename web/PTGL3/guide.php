<!DOCTYPE html>
<?php 
	include('./backend/config.php'); 

	$SECTION_COUNTER = 0;  # used to increment the sections without having to do it yourself

	$title = "Guide";
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

		<div class="container" id="about">
			<div class="sectionPointerBox">
				<p>
					<a href="/about.php"><i class="glyphicon glyphicon-arrow-up"></i> What is PTGL? <i class="glyphicon glyphicon-arrow-up"></i></a><br>
					Previous section
				</p>
			</div>
			<br>

			<h2> Step-by-step guide </h2>

			<p>
				Download this guide as: <?php echo ("<a href=files/PTGL_step-by-step_guide.pdf>[PDF]</a>"); ?>.
			</p>

			<h3> Table of contents </h3>
			<?php include("snippets/guide_toc.php"); ?>


			<a class="anchor" id="guidePgFg"></a>
			<h3> <?php echo ++$SECTION_COUNTER ?>: Protein and Folding Graph of a PDB ID </h3>

			<p>
				First, open the PTGL website under <?php echo ("<a href=$SITE_BASE_URL>$SITE_BASE_URL</a>"); ?>. 
				You will see this page:
			</p>

			<img src="./images/guide/PG_FG/page_index_top.png" alt="index.php webpage example" title="index.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				<ol>
					<li> In the center search field, type in your PDB ID, e.g., 7tim. </li>
					<li> Hit the "search" button. You will see a list of chains: </li>
				</ol>
			</p>

			<img src="./images/guide/PG_FG/page_search.png" alt="search.php webpage example" title="search.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				<ol>
					<li> Either hit the "select all protein chains" button or select the chains of interest by their check boxes in the list at the bottom. </li>
					<li> Hit the "load proteins" button. You will see the <a href="about.php#proteinGraph">Protein Graph</a> of the first selected chain:</li>
				</ol>	
			</p>

			<img src="./images/guide/PG_FG/page_results_7timA-albe.png" alt="result.php webpage example" title="result.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				The page contains:
				<ol type="A">
					<li> The <a href="about.php#proteinGraph">Protein Graph</a> Visualization in the center. </li>
					<li> The list of contained secondary structure elements on the right. </li>
				</ol>

				Now:

				<ol>
					<li> Select the <a href="about.php#graphTypes">alpha-beta-ligand</a> topology type at the bottom. The image slides to the respective visualization. </li>
					<li> Hit the "next protein chain" button in the center of the right edge. The page slides to the respective protein chain: </li>
				</ol>	
			</p>

			<img src="./images/guide/PG_FG/page_results_7timB-albelig.png" alt="result.php webpage example" title="result.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				<ol>
					<li> Download the graph visualization as PNG file with the link beneath the image. </li>
					<li> Follow the link beneath the visualization on the left to open the <a href="about.php#foldingGraph">Folding Graphs</a> of this chain. You will see this page: </li>
				</ol>
			</p>

			<img src="./images/guide/PG_FG/page_foldinggraphs_7timB-albelig-adj.png" alt="foldinggraphs.php webpage example" title="foldinggraphs.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				The page contains:
				<ol type="A">
					<li> 
						A table of all <a href="about.php#foldingGraph">Folding Graphs</a> for this chain in the center. For each <a href="about.php#foldingGraph">Folding Graph</a>, it lists the Folding Graph number (FG#), fold name, number of secondary structure elements (#SSEs), the sequence of secondary structure elements from N- to C-terminus (SSE string), the vertex number of the first vertex in the parent <a href="about.php#proteinGraph">Protein Graphs</a> (PG), the <a href="about.php#linearNot">linear notation</a>, a link to the graph visualization and a link to the overview of the <a href="about.php#linearNot">linear notations</a> (linnot). 
					</li>
					<li> The <a href="about.php#foldingGraph">Folding Graph</a> visualization at the bottom. </li>
				</ol>

				You can choose different Folding Graphs to be loaded above the table:
				<ol>
					<li> From the graph-type drop-down menu, choose the <a href="about.php#graphTypes">beta</a> topology. </li>
					<li> From the notation-type drop-down menu, choose the <a href="about.php#fgGraphTypesRED">RED</a> notation. </li>
					<li> Hitting the "search" button you will see the page containing this graph visualization: </li>
				</ol>
			</p>

			<!-- 7timB beta RED FG#0 -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/ti/7tim/B/7tim_B_beta_FG_0_RED.png" alt="RED Beta Folding Graph of 7timB" title="RED Beta Folding Graph of 7timB" class="img-responsive imgFormAboutphp2"/></p>');
			?>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<br>
			<a class="anchor" id="guideLinnot"></a>
			<h3> <?php echo ++$SECTION_COUNTER ?>: Search proteins exhibiting a structural topology by their linear notation </h3>

			<p>
				Open the <?php echo ($SITE_TITLE) ?> website under <?php echo ("<a href=$SITE_BASE_URL>$SITE_BASE_URL</a>"); ?>. You will see this page: </li>
			</p>

			<img src="./images/guide/linnot/page_index.png" alt="index.php webpage example" title="index.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				<ol>
					<li> Open the drop-down menu of the <a href="about.php#linearNot">linear notation</a> search by clicking anywhere on the respective line. </li>
				</ol>
			</p>

			<img src="./images/guide/linnot/page_index_linnot-search.png" alt="index.php webpage's linear search menu" title="index.php webpage's linear search menu" class="img-responsive imgScreenshot"/>

			<p>
				<ol>
					<li> In the notation-type drop-down menu, choose reduced (<a href="about.php#fgGraphTypesRED">RED</a>). </li>
					<li> In the graph-type drop-down menu, choose <a href="about.php#graphTypes">beta</a>. </li>
					<li> Type '(1a,1a,1a,1a,1a,1a,1a,-7a)' in the search field. </li>
					<li> Hit the "search" button. </li>
				</ol>
			</p>

			<img src="./images/guide/linnot/page_search.png" alt="search.php webpage example" title="search.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				<ol>
					<li> Select the protein chain 1aveA by clicking the respective check box. </li>
					<li> Hitting the "load proteins" button you will see the page containing this graph visualization: </li>
				</ol>
			</p>

			<!-- 1aveA albe PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/av/1ave/A/1ave_A_albe_PG.png" alt="Alpha-beta Protein Graph of 1aveA" title="Alpha-beta Protein Graph of 1aveA" class="img-responsive imgFormAboutphp2"/></p>');
			?>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<br>
			<a class="anchor" id="guideMotif"></a>
			<h3> <?php echo ++$SECTION_COUNTER ?>: Search structures containing a predefined motif </h3>

			<p>
				Open the <?php echo ($SITE_TITLE) ?> website under <?php echo ("<a href=$SITE_BASE_URL>$SITE_BASE_URL</a>"); ?>. You will see this page: </li>
			</p>

			<img src="./images/guide/motif/page_index.png" alt="index.php webpage example" title="index.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				<ol>
					<li> Open the drop-down menu of the <a href="about.php#motifs">motif</a> search by clicking anywhere on the respective line. </li>
				</ol>
			</p>

			<img src="./images/guide/motif/page_index_motif-search.png" alt="index.php webpage's motif search menu" title="index.php webpage's motif search menu" class="img-responsive imgScreenshot"/>

			<p>
				<ol>
					<li> Search for the <a href="about.php#propeller">beta propeller</a> <a href="about.php#motifs">motif</a> by clicking on the respective image. </li>
				</ol>
			</p>

			<img src="./images/guide/motif/page_search.png" alt="search.php webpage example" title="search.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				<ol>
					<li> Select the protein chain 1a0rB by clicking the respective check box. </li>
					<li> Hitting the "load proteins" button you will see the page containing this graph visualization: </li>
				</ol>
			</p>

			<!-- 1a0rB albe PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/a0/1a0r/B/1a0r_B_albe_PG.png" alt="Alpha-beta Protein Graph of 1a0rB" title="Alpha-beta Protein Graph of 1a0rB" class="img-responsive imgFormAboutphp2"/></p>');
			?>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<a class="anchor" id="guideAdvanced"></a>
			<h3> <?php echo ++$SECTION_COUNTER ?>: Advanced search </h3>

			<p>
				First, open the PTGL website under <?php echo ("<a href=$SITE_BASE_URL>$SITE_BASE_URL</a>"); ?>. 
				You will see this page:
			</p>

			<img src="./images/guide/advanced/page_index.png" alt="index.php webpage example" title="index.php webpage example" class="img-responsive imgScreenshot"/>

			<ol>
				<li> Open the drop-down menu of the Advanced Search by clicking anywhere on the respective link. </li>
			</ol>

			<img src="./images/guide/advanced/page_index_advanced-search.png" alt="index.php's advanced search example" title="index.php's advanced search example" class="img-responsive imgScreenshot"/>

			<p>
				The drop-down menu contains the following fields:

				<ol type="A">
					<li>
						The 4-character PDB identifier (ID).
						Using this field is the same as using the main search field.
						Combining this field with other advanced search fields makes no sense, because a PDB ID already unambigously identifies a protein structure.
						Using this field may still be useful, because it only searches for the PDB ID and on the contrary to the main search field neglects key words.
					</li>

					<li>
						The author-chosen title of the PDB entry.
						The title often contains the name of the experiment, of the protein or of the source organism.
						The search finds titles containing the entered text. 
					</li>

					<li>
						Choose whether the protein should include a ligand or not.
						Water is not treated as ligand.
					</li>

					<li>
						The ligand name.
						The search finds results for the 3-character ligand code from the PDB or parts of the ligand full name.
					</li>

					<li>
						The molecule name.
						The search finds results for the molecule / chain names of the PDB entry.
					</li>
				</ol>

				Now:

				<ol>
					<li> Type 'Plasmodium falciparum' in the title search field. </li>
					<li> Choose 'Yes' from the ligand drop-down menu. </li>
					<li> Type 'PGA' in the ligand name search field. </li>
					<li> Type 'TRIOSEPHOSPATE ISOMERASE' in the molecule search field. </li>
					<li> Hit the "search button". You will see this page: </li>
				</ol>
			</p>

			<img src="./images/guide/advanced/page_search.png" alt="search.php webpage example" title="search.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				All the presented protein chains apply to the criteria of your Advanced Search. Now:
				<ol>
					<li> Hit the "select all protein chains" button. </li>
					<li> Hit the "load proteins" button. You will see the page containing this <a href="about.php#proteinGraph">Protein Graph</a>: </li>
				</ol>
			</p>

			<!-- 1lyxA albe PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/ly/1lyx/A/1lyx_A_albe_PG.png" alt="Alpha-beta Protein Graph of 1lyxA" title="Alpha-beta Protein Graph of 1lyxA" class="img-responsive imgFormAboutphp2"/></p>');
			?>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<br>
			<a class="anchor" id="guideCg"></a>
			<h3> <?php echo ++$SECTION_COUNTER ?>: Complex Graph of a PDB ID </h3>

			<p>
				Open the <?php echo ($SITE_TITLE) ?> website under <?php echo ("<a href=$SITE_BASE_URL>$SITE_BASE_URL</a>"); ?>. You will see this page: </li>
			</p>

			<img src="./images/guide/CG/page_index.png" alt="index.php webpage example" title="index.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				<ol>
					<li> Open the drop-down menu of the <a href="about.php#complexGraph">Complex Graph</a> search by clicking anywhere on the respective line. </li>
				</ol>
			</p>

			<img src="./images/guide/CG/page_index_CG-search.png" alt="index.php webpage's CG search menu" title="index.php webpage's CG search menu" class="img-responsive imgScreenshot"/>

			<p>
				<ol>
					<li> Type in the search field the PDB ID 6ebl. </li>
					<li> Hitting the "search" button, you see this page: </li>
				</ol>
			</p>

			<img src="./images/guide/CG/page_complexgraphs.png" alt="complexgraphs.php webpage example" title="complexgraphs.php webpage example" class="img-responsive imgScreenshot"/>

			<p>
				The page contains the following information:

				<ol type="A">
					<li> 
						A table of all chains of this complex. For each chain, it lists the PDB ID, Chain ID, Molecule name, Molecule (Mol) ID, source organism, and a link to the <a href="about.php#proteinGraph">Protein Graph</a>.
					</li>

					<li>
						A table of all macromolecules of this complex. For each macromolecule, its lists the macromolecule ID (MOL_ID), the name, the enzyme class (EC) number, the source organism and the homologous chains.
					</li>

					<li>
						The visualization of the secondary structure-level Complex Graph. It corresponds to a <a href="about.php#proteinGraph">Protein Graph</a> extended by all secondary structure elements of the complex.
					</li>

					<li>
						The visualization of the <a href="about.php#complexGraph">Complex Graph</a>.
					</li>

					<li>
						The download links for the graphs and their visualization in different file types.
					</li>
				</ol>
			</p>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<br>
			<div class="sectionPointerBox">
				<p>
					Next section<br>
					<a href="/biomedical_examples.php"><i class="glyphicon glyphicon-arrow-down"></i> Biomedical examples <i class="glyphicon glyphicon-arrow-down"></i></a>
				</p>
			</div>
			<br>

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