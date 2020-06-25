<!DOCTYPE html>
<?php 
include('./backend/config.php'); 

$title = "About";
$title = $SITE_TITLE.$TITLE_SPACER.$title;
?>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta http-equiv="Cache-control" content="public">
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
			<h2> What is PTGL? </h2>
			<h3> Table of contents </h3>
			<ul>
				<li class="noBullets">  <a href="#overview">Overview</a> </li>
				<li class="noBullets">  <a href="#contactDefinition">Contact definition</a> </li>
				<li class="noBullets">  <a href="#proteinGraph">Protein Graphs</a> </li>
				<ul>
				    <li class="noBullets">  <a href="#pgVisualization">Visualization</a> </li>
				    <li class="noBullets">  <a href="#graphTypes">Graph types</a> </li>
				</ul>

				<li class="noBullets">  <a href="#foldingGraph">Folding Graphs</a> </li>
				<ul>
					<li class="noBullets">  <a href="#fgVisualization">Visualization</a> </li>
					<li class="noBullets">  <a href="#linearNot">Linear notation</a> </li>
					<li class="noBullets">  <a href="#fgGraphTypes">Notation types</a> </li>
				</ul>

				<li class="noBullets">  <a href="#complexGraph">Complex Graphs</a> </li>
				<li class="noBullets">  <a href="#motifs">Motifs</a> </li>
					<ul>
						<li class="noBullets"> <a href="#motifsAlpha">Alpha motifs</a> </li>
						<li class="noBullets"> <a href="#motifsBeta">Beta motifs</a> </li>
					</ul>

				<li class="noBullets">  <a href="#publications">Publications</a> </li>
			</ul>

			<br>	
			<a class="anchor" id="overview"></a>
			<h3> Overview </h3>

			<!-- include the introduction snippet -->
			<?php 
				include("snippets/introduction.php");
			?>

			<br>	
			<p class="imgCenter">
				<img src="./images/how_vplg_works.png" alt="Protein Graph computation" title="Protein Graph computation" class="img-responsive imgFormAboutphp"/>
			</p>

			<p>
				<center>
					a) Sphere-style, b) stick-style and c) cartoon-style three-dimensional protein structure representation. d) Red and black circles denote helices and strands, respectively. e) Lines connecting the circles denote spatial neighborhood. f) Protein Graph visualization.
				</center>
			</p> 


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<br>
			<a class="anchor" id="contactDefinition"></a>
			<h3> Contact definition </h3>

			<p>
				We define contacts on different levels of abstraction or scales, respectively.

				<h4> Atom </h4>
				Atoms are modelled as hard spheres with a radius of 2 Å for atoms of amino acids and of 3 Å for ligand atoms.
				An atom-atom contact is defined if two hard spheres overlap.
				For atoms of amino acids the contact is differentiated depending on the position of the involved atoms in the amino acid backbone or side chain.
				This yields atom level contacts of the following types:

				<ul>
				  <li>BB: backbone - backbone contact</li>
				  <li>BC: backbone - side chain contact</li>
				  <li>CC: side chain - side chain contact</li>
				  <li>LB: ligand - backbone contact</li>
				  <li>LC: ligand - side chain contact</li>
				  <li>LL: ligand - ligand contact</li>
				  <li>LX: ligand - non-ligand contact, i.e., LX = LB or LC</li>
				</ul>

				<h4> Residue </h4>
				A residue-residue contact is defined if two residues share an atom contact.

				<h4> Secondary structure element </h4>
				Depending on the type of the secondary structure elements (SSEs), we applied a rule set:
				<table border="1px" padding="15px">
				<tr><th>SSE 1 type</th><th>SSE 2 type</th><th>Required contacts</th><tr>
				<tr><td>Beta strand</td><td>Beta strand</td><td>BB &gt; 1 or BC &gt; 2</td><tr>
				<tr><td>Helix</td><td>Beta strand</td><td>BB &gt; 1 or BC &gt; 3 or CC &gt; 3</td><tr>
				<tr><td>Helix</td><td>Helix</td><td>BC &gt; 3 or CC &gt; 3</td><tr>
				<tr><td>Ligand</td><td><i>Any type</i></td><td>LX &gt;= 1</td><tr>
				</table>

				For more details on the contact definition, please see the following publication: <b>Schäfer T, May P, Koch I (2012). <i>Computation and Visualization of Protein Topology Graphs Including Ligand Information.</i> German Conference on Bioinformatics 2012; 108-118</b>.

				<h4> Chain </h4>
				A chain-chain contact is defined if two chains share atleat one residue-residue contact-

			</p>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<br>
			<a class="anchor" id="proteinGraph"></a>
			<h3> Protein Graphs </h3>
			
			<p>
			A Protein Graph is defined as labeled, undirected graph. The vertices correspond to the secondary structure elements or ligands, and they are labeled with
			the secondary structure element type (alpha helix, beta strand or ligand). The vertices of the Protein Graph are enumerated as they occur in the sequence from the N- to the C-terminus.
			</p>
			
			<p>
			The edges of the Protein Graph represent spatial adjacencies of secondary structure elements (see <a href="#contactDefinition">contact definition</a>).
			According to the direction of the spatical adjacent SEEs, their orientation to each other can be parallel (p), anti-parallel (a), or mixed (m). 
			</p>

			<br>
			<a class="anchor" id="pgVisualization"></a>
			<h4> Visualization </h4>

			<p>
				In the graph visualizations available on the PTGL server, the secondary structure elements are ordered as red circles (helices), black quadrats (strands), or magenta rings (ligands) on a straight line according to their sequential order from the N- to	the C-terminus.
				The spatial neighborhoods are drawn as arcs between secondary structure elements.
				The edges are colored according to their labeling as parallel (red), anti-parallel (blue), mixed (green) or ligand (magenta).
				This is the key for the images:
			</p>
			
			<p class="imgCenter"><img src="./images/vplg_legend.png" alt="PTGL graph image key" title="PTGL graph image key" class="img-responsive imgFormAboutphp2"/></p>

			<p>
				Below the vertices there are the numbers of the secondary structure elements for this type of Protein Graph (PG) and for the occurrence in the sequence of the whole list of secondary structure elements (SQ).
			</p>
			
			<!--
				Old PG image, not removed so far from img folder and css style file
			<p class="imgCenter"><img src="./images/protein_graph.png" alt="Protein Graph" title="Protein Graph" class="img-responsive imgFormAboutphp2"/></p>
			-->
			
			<br>
			<a class="anchor" id="graphTypes"></a>
			<h4> Graph types </h4>
			
			<p>		
			If only a certain secondary structure element type is of interest, the graph modelling allows to exclude the non-interesting secondary structure element types.
			According to the secondary structure element type of interest, the Protein Graph can be defined as an <a href="#alphaGraph">Alpha graph</a>, <a href="#betaGraph">Beta graph</a>, or <a href="#alphaBetaGraph">Alpha-Beta graph</a>.
			If you are interested in the ligands as well, you can also use the <a href="#alphaLigGraph">Alpha-Ligand graph</a>, the <a href="#betaLigGraph">Beta-Ligand graph</a>,
			and the <a href="#alphaBetaLigGraph">Alpha-Beta-Ligand graph</a>.</p>
			
			<p>
			The Alpha graph contains only alpha helices and the contacts between them. The Alpha-Beta graph contains alpha helices, beta strands and the contacts betweem them, and so on.
			</p>

			<p>
				As an example for Protein Graphs and their graph types, we present the structure with PDB ID 7tim [Davenport et al., 1991, Biochemistry].
			</p>

			<br>
			<a class="anchor" id="alphaGraph"></a>
			<h5> <b>Alpha Graph </b></h5>
			The Alpha-Graph of the protein 7tim chain A consisting of 13 helices.

			<!-- 7timA alpha PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/ti/7tim/A/7tim_A_alpha_PG.png" alt="Alpha Graph of 7timA" title="Alpha Graph of 7timA" class="img-responsive imgFormAboutphp2"/></p>');
			?>

			<br>
			<a class="anchor" id="betaGraph"></a>
			<h5><b> Beta Graph </b></h5>
			The Beta-Graph of the protein 7tim chain A consisting of 8 strands.  Note the beta barrel in the protein, which is clearly visible as a circle of parallel beta-strands in this graph.

			<!-- 7timA beta PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/ti/7tim/A/7tim_A_beta_PG.png" alt="Beta Graph of 7timA" title="Beta Graph of 7timA" class="img-responsive imgFormAboutphp2"/></p>');
			?>

			<br>
			<a class="anchor" id="alphaBetaGraph"></a>
			<h5><b> Alpha-Beta Graph </b></h5>
			The Alpha-Beta Graph of the protein 7tim chain A consisting of 21 secondary structure elements (13 helices and 8 strands). 

			<!-- 7timA albe PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/ti/7tim/A/7tim_A_albe_PG.png" alt="Alpha-Beta Graph of 7timA" title="Alpha-Beta Graph of 7timA" class="img-responsive imgFormAboutphp2"/></p>');
			?>
			
			<br>
			<a class="anchor" id="alphaLigGraph"></a>
			<h5><b> Alpha-Ligand Graph </b></h5>
			The Alpha-Ligand Graph of the protein 7tim chain A consisting of 13 helices and 1 ligand.
			
			<!-- 7timA alphalig PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/ti/7tim/A/7tim_A_alphalig_PG.png" alt="Alpha-Ligand Graph of 7timA" title="Alpha-Ligand Graph of 7timA" class="img-responsive imgFormAboutphp2"/></p>');
			?>

			<br>
			<a class="anchor" id="betaLigGraph"></a>
			<h5><b> Beta-Ligand Graph </b></h5>
			The Beta-Ligand-Graph of the protein 7tim chain A consisting of 8 strands and 1 ligand.

			<!-- 7timA betalig PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/ti/7tim/A/7tim_A_betalig_PG.png" alt="Beta-Ligand Graph of 7timA" title="Beta-Ligand Graph of 7timA" class="img-responsive imgFormAboutphp2"/></p>');
			?>

			<br>
			<a class="anchor" id="alphaBetaLigGraph"></a>
			<h5><b> Alpha-Beta-Ligand Graph </b></h5>
			The Alpha-Beta-Ligand Graph of the protein 7tim chain A consisting of 22 secondary structure elements (13 helices, 8 strands and 1 ligand). 

			<!-- 7timA albelig PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/ti/7tim/A/7tim_A_albelig_PG.png" alt="Alpha-Beta-Ligand Graph of 7timA" title="Alpha-Beta-Ligand Graph of 7timA" class="img-responsive imgFormAboutphp2"/></p>');
			?>
			
			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
			
			
			<br>
			<a class="anchor" id="foldingGraph"></a>
			<h3>Folding Graphs</h3>

			<p>
				A connected component of a <a href="#proteinGraph">Protein Graph</a> is called Folding Graph.
				Folding Graphs are denoted with capital letters in alphabetical order according to their occurrence in the sequence, beginning at the N-terminus.
				Folding Graphs consisting of only one secondary structure element are found mostly at the protein surface and not in the protein core.
				Especially in beta-sheet containing Folding Graphs, the maximal vertex degree of the Folding Graphs is rarely larger than two.
				Thus, we distinguish between so-called bifurcated and non-bifurcated topological structures.
				A <a href="#proteinGraph">Protein Graph</a> or a Folding Graph is called bifurcated, if the vertex degree is greater than two, and called non- bifurcated otherwise.
			</p>

			<br>
			<a class="anchor" id="fgVisualization"></a>
			<h4> Visualization </h4>

			<p>
				In the graph visualizations available on the PTGL server, Folding Graphs follow the <a href="#pgVisualization">Protein Graph visualization</a>. The footer additionally contains the number of the secondary structure element in the Folding Graph (FG).
			</p>

			<p>
				As an example we present an antigen receptor protein structure with PDB ID 1bec [Bentley et al., 1995, Science].
				1bec is a transport membrane protein that detects foreign molecules at the cell surface.
				The protein consists of one chain A and exhibits three Folding Graphs.
				It has two domains, which are represented by the Folding Graphs A and C, which are mainly built by strands.
				Two Folding Graphs (Folding Graphs 1bec_A and 1bec_C) are large enough to be of interest, and one Folding Graph (1bec_B) consists only of a single helix (see <a href="#alphaBeta1bec">Protein Graph of 1bec</a>: helix 13).
			</p>
			
			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>

			<br>
			<a class="anchor" id="rasmolbec"></a>
			<h5><b> 3D structure of 1bec: </b></h5>
			<p class="imgCenter"><img src="./images/1bec_structure.png" alt="3D structure of 1bec" title="3D structure of 1bec" class="img-responsive imgFormAboutphp2"/></p>

			<a class="anchor" id="alphaBeta1bec"></a>
			<h5><b> Alpha-Beta Protein Graph of 1bec: </b></h5>
			<!-- 1bec albe PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/be/1bec/A/1bec_A_albe_PG.png" alt="Alpha-Beta Protein Graph of 1becA" title="Alpha-Beta Protein Graph of 1becA" class="img-responsive imgFormAboutphp2"/></p>');
			?>
			
			<h5><b> Alpha-Beta Folding Graph A of 1bec: </b></h5>
			<!-- 1bec albe RED FG#0 -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/be/1bec/A/1bec_A_albe_FG_0_RED.png" alt="Alpha-Beta Folding Graph A of 1becA" title="Alpha-Beta Folding Graph A of 1becA" class="img-responsive imgFormAboutphp2"/></p>');
			?>		

			<h5><b> Alpha-Beta Folding Graph C of 1bec: </b></h5>
			<!-- 1bec albe RED FG#2 -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/be/1bec/A/1bec_A_albe_FG_2_RED.png" alt="Alpha-Beta Folding Graph C of 1becA" title="Alpha-Beta Folding Graph C of 1becA" class="img-responsive imgFormAboutphp2"/></p>');
			?>	


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<br>
			<a class="anchor" id="linearNot"></a>
			<h4>Linear Notations </h4>

			<p>
				A notation serves as a unique, canonical, and linear description and classification of structures.
				The notations for Folding Graphs resemble a protein structure as a linear sequence of secondary structure elements and describe the arrangement of secondary structure elements uniquely.
				Linear notations enable you to search the <?php echo "$SITE_TITLE"; ?> for protein motifs.
				Searching for a structure, SQL-based string matching in the linear notation strings is used to find all folding graphs which match the query.
			</p>

			<p>
				The linear notations are written in different brackets:
				<ul>
					<li> [] denote <a href="#foldingGraph">non-bifurcated Folding Graphs</a>, </li>
					<li> {} denote <a href="#foldingGraph">bifurcated Folding Graphs</a>, and </li>
					<li> () denote barrel structures. </li>
				</ul>
			</p>

			<p>
				Secondary structure elements are denoted by single characters:
				<ul>
					<li> h denotes helices, </li>
					<li> e denotes strands, and </li>
					<li> l denotes ligands. </li>
				</ul>

				For alpha or beta Folding Graphs, the characters of secondary structure elements not described are left out.
			</p>

			<p>
				Edges are completely described by the start and end vertex and their label, i.e., parallel, antiparallel, mixed or ligand.
				The start and end vertex are saved implicitly as the linear notation traverses the graph and only saves in which direction, i.e. "+" towards C-terminus and "-" towards N-terminus, the edge goes.
				Edge labels are denoted by single characters:
				<ul>
					<li> p denotes parallel, </li>
					<li> a denotes antiparallel, </li>
					<li> m denotes mixed, and </li>
					<li> j denotes ligand edges. </li>
				</ul>

			

			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<br>
			<a class="anchor" id="fgGraphTypes"></a>
			<h4> Notation types </h4>

			<p>
				There are two possibilities of representing Folding Graphs: first, one can order the secondary structure elements in one line according to their occurrence in sequence, or second, according to their occurrence in space.
				In the first case, the <a href="#adj">adjacent</a> (ADJ), the <a href="#red">reduced</a> (RED), and the <a href="#seq">sequence</a> (SEQ) Folding Graphs, secondary structure elements are ordered as points on a straight line according to their sequential order from the N- to the C-terminus.
				In the second case, the <a href="#key">key</a> (KEY) Folding Graph, secondary structure elements are represented as red rectangles and black arrows for helices and strands, respectively. They are ordered in a straight line corresponding to their spatial arrangement.
				This is difficult, because in most proteins, secondary structure elements exhibit more than two spatial neighbours.
				Therefore, KEY Folding Graphs can only be drawn for <a href="#foldingGraph">non-bifurcated Folding Graphs</a>.
			</p>

			<p>
				Folding Graphs for the different <a href="#graphTypes">graph types</a> can be derived from the different <a href="#proteinGraph">Protein Graph</a> types: alpha, beta, alpha-beta, alpha-ligand, beta-ligand and alpha-beta-ligand.
			</p>

			<br>
			<a class="anchor" id="fgGraphTypesADJ"></a>
			<h5><b> Adjacent (ADJ) Folding Graphs</b></h5>

			<p>
				Secondary structure elements are ordered by their occurence in the sequence, from N- to C-terminus. 
				All vertices of the <a href="#proteinGraph">Protein Graph</a> are considered in the adjacent notation of a Folding Graph.
				This means that adjacent Folding Graphs account for secondary structure elements laying between the secondary structure elements of the Folding Graph without being connected to one of them.
				Vertices of the Protein Graph that are unconnected to vertices of the Folding Graph are colored grey.
			</p>

			<!-- 1d3tB albe ADJ FG#0 -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/d3/1d3t/B/1d3t_B_albe_FG_0_ADJ.png" alt="ADJ Alpha-Beta Folding Graph of 1d3tB" title="ADJ Alpha-Beta Folding Graph of 1d3tB" class="img-responsive imgFormAboutphp2"/></p>');
			?>

			<!-- 1d3tB albe ADJ FG#0 linnot -->
			<p><center>
				Adjacent <a href="#linearNot">linear notation</a>: 
				<?php
					$url_linnot = "$SITE_BASE_URL" . '/api/index.php/linnot/1d3t/B/albe/0/adj/json';
					$curl = curl_init();
					curl_setopt($curl, CURLOPT_URL, $url_linnot);
					curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
					curl_setopt($curl, CURLOPT_FAILONERROR, 1);

					$result = curl_exec($curl);

					if(curl_errno($curl)) {
					    // handle error
					    echo "ERROR: Please inform the site administrator about this error with message '" . curl_error($curl) . "'";
					}
					else {
					    // result
					    echo json_decode($result);
					}
				?>
			</center></p>

			<br>
			<a class="anchor" id="fgGraphTypesRED"></a>
			<h5><b> Reduced (RED) Folding Graphs </b></h5>

			<p>
				Secondary structure elements are ordered by their occurence in the sequence, from N- to C-terminus.
				Reduced Folding Graphs are the same as adjacent Folding Graphs, but only those secondary structure elements part of the Folding Graph are considered.
			</p>

			<!-- 1d3tB albe RED FG#0 -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/d3/1d3t/B/1d3t_B_albe_FG_0_RED.png" alt="ADJ Alpha-Beta Folding Graph of 1d3tB" title="ADJ Alpha-Beta Folding Graph of 1d3tB" class="img-responsive imgFormAboutphp2"/></p>');
			?>

			<!-- 1d3tB albe RED FG#0 linnot -->
			<p><center>
				Reduced <a href="#linearNot">linear notation</a>: 
				<?php
					$url_linnot = "$SITE_BASE_URL" . '/api/index.php/linnot/1d3t/B/albe/0/red/json';
					$curl = curl_init();
					curl_setopt($curl, CURLOPT_URL, $url_linnot);
					curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
					curl_setopt($curl, CURLOPT_FAILONERROR, 1);

					$result = curl_exec($curl);

					if(curl_errno($curl)) {
					    // handle error
					    echo "ERROR: Please inform the site administrator about this error with message '" . curl_error($curl) . "'";
					}
					else {
					    // result
					    echo json_decode($result);
					}
				?>
			</center></p>

			<br>
			<a class="anchor" id="fgGraphTypesSEQ"></a>
			<h5><b> Sequential (SEQ) Folding Graphs </b></h5>

			<p>
				Secondary structure elements are ordered by their occurence in the sequence, from N- to C-terminus.
				Sequential Folding Graphs are the same as adjacent Folding Graphs, but the edges stand for sequential instead of spatial neighborhood.
				Because vertices from the Protein Graph that are unconnected to vertices of the Folding Graph are included, but left out from the sequential neighborhood consideration, they are bypassed in the sequence of edges.
			</p>

			<p>
				Although the sequence notation is trivial, the graphs can be useful, for example, searching for ψ-loops requires a special SEQ notation.
			</p>

			<!-- 1d3tB albe SEQ FG#0 -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/d3/1d3t/B/1d3t_B_albe_FG_0_SEQ.png" alt="ADJ Alpha-Beta Folding Graph of 1d3tB" title="ADJ Alpha-Beta Folding Graph of 1d3tB" class="img-responsive imgFormAboutphp2"/></p>');
			?>

			<!-- 1d3tB albe SEQ FG#0 linnot -->
			<p><center>
				Sequential <a href="#linearNot">linear notation</a>: 
				<?php
					$url_linnot = "$SITE_BASE_URL" . '/api/index.php/linnot/1d3t/B/albe/0/seq/json';
					$curl = curl_init();
					curl_setopt($curl, CURLOPT_URL, $url_linnot);
					curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
					curl_setopt($curl, CURLOPT_FAILONERROR, 1);

					$result = curl_exec($curl);

					if(curl_errno($curl)) {
					    // handle error
					    echo "ERROR: Please inform the site administrator about this error with message '" . curl_error($curl) . "'";
					}
					else {
					    // result
					    echo json_decode($result);
					}
				?>
			</center></p>

			<br>
			<a class="anchor" id="fgGraphTypesKEY"></a>
			<h5><b> KEY Folding Graphs </b></h5>

			<p>
				KEY Folding Graphs can only be created for <a href="#foldingGraph">non-bifurcated Folding Graphs</a>.
				KEY Folding Grapgs are very close to the topology diagrams of biologists, e.g. Brändén and Tooze (1999).
				Topologies are described by diagrams of	black arrows for strands and red rectangles for helices.
				As in <a href="#red">reduced</a> Folding Graphs, only secondary structure elements of the Folding Graph are considered.
				Secondary structure elements are ordered spatially and connected in sequential order.
				See the KEY Folding Graph of the <a href="#alphaBetaGraph">Alpha-Beta</a> <a href="#foldingGraph">Folding Graph</a> B of a histocompatibility antigen 1iebB.
				The Folding Graph consists of three helices and four strands. This topology exhibits one cross-over connection from helix 6 to helix 7 and forms an <a href="#alphaBetaGraph">Alpha-Beta</a> barrel structure. 
			</p>

			<p>
				<b>Linear notation characteristics</b>: If the arrangement of secondaray structure elements is parallel, an x is noted (Richardson, 1977).
				In this case, the protein chain moves to the other side of the sheet by crossing the sheet (cross over).
				Antiparallel arrangements are called same end and are more stable (Chothia and Finkelstein , 1990). Mixed arrangements are defined as same end.
			</p>

			<!-- 3j9yO albe KEY FG#1 -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/j9/3j9y/O/3j9y_O_albe_FG_1_KEY.png" alt="KEY Alpha-Beta Folding Graph of 3j9yO" title="AKEY Alpha-Beta Folding Graph of 3j9yO" class="img-responsive imgFormAboutphp2"/></p>');
			?>

			<!-- 3j9yO albe KEY FG#1 linnot -->
			<p><center>
				KEY <a href="#linearNot">linear notation</a>: 
				<?php
					$url_linnot = "$SITE_BASE_URL" . '/api/index.php/linnot/3j9y/O/albe/1/key/json';
					$curl = curl_init();
					curl_setopt($curl, CURLOPT_URL, $url_linnot);
					curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
					curl_setopt($curl, CURLOPT_FAILONERROR, 1);

					$result = curl_exec($curl);

					if(curl_errno($curl)) {
					    // handle error
					    echo "ERROR: Please inform the site administrator about this error with message '" . curl_error($curl) . "'";
					}
					else {
					    // result
					    echo json_decode($result);
					}
				?>
			</center></p>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>

			<br>
			<a class="anchor" id="complexGraph"></a>
			<h3> Complex Graphs </h3>
			<p>
				A Complex Graph is defined as undirected graph.
				The vertices correspond to protein chains and are named by their author-provided chain ID from the PDB file.
				Edges denote a spatial contact and the edge weight corresponds to the number of residue-residue contacts.
				Below the graph there is a label per vertex for the number of the vertex (C#), chain name (CN) and its molecule identifier (ML).
			</p>

			<p><img src="./images/4a97_CG.png" alt="Complex Graph" title="Complex Graph" class="img-responsive imgFormAboutphp2"/></p>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<br>
			<a class="anchor" id="motifs"></a>
			<h3> Motifs </h3>

			<!-- receives meta info from db about motifs -->
			<?php include('backend/get_motif_meta-info.php'); ?>
			
			<p>
				A motif is a common supersecondary structure.
				A motif consists of only a few secondary structure elements, and it may occur with very different functions.
				<?php echo "$SITE_TITLE"; ?> implements motif detection in <a href="#proteinGraph">Protein Graphs</a> for some chosen motifs based on the <a href="#linearNot">linear notations</a> of <a href="#foldingGraph">folding graphs</a>.
				This enables the search for all chains containing one of the predefined motifs.
				If you want to search for an arbitrary arrangement of secondary structure elements, use the <a href="guide.php#guideLinnot">linear notation search</a>.
			</p>

			<!-- Description of single motifs including example image -->
			<?php include("motif_info.php"); ?>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>

			<!-- Step-by-step guide  -> now an own page
			<br>
			<a class="anchor" id="guide"></a>
			<h2>Step-by-step guide</h2>

			<?php 
				/* now an own page 
				include("guide.php"); 
				*/
			?>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
			-->
			
			<!-- Biomedical examples -> now an own page
			<br>
			<a class="anchor" id="bioExamples"></a>
			<h2>Biomedically relevant examples</h2>

			 
			<?php
				/* now an own page
				include("biomedical_examples.php");
				*/
			?>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
			-->

			<br>
			<a class="anchor" id="publications"></a>
			<h3> Publications </h3>

			<!-- Description of single motifs including example image -->
			<?php include("snippets/publication_list.php"); ?>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>

			<br>
			<div class="sectionPointerBox">
				<p>
					Next section <br>
					<a href="/guide.php"><i class="glyphicon glyphicon-arrow-down"></i> Step-by-step guide <i class="glyphicon glyphicon-arrow-down"></i></a>
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
