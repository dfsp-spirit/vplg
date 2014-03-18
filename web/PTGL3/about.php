 <!DOCTYPE html>

<html>
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="description" content="">
		<meta name="author" content="">
		<link rel="shortcut icon" href="../../docs-assets/ico/favicon.png">

		<title>PTGL 2.0</title>


		<!-- Mobile viewport optimized -->
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scale=1.0, user-scalable=no"/>
		

		<!-- Bootstrap CSS -->
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap-glyphicons.css">


		<!-- Custom CSS -->
		<link rel="stylesheet" type="text/css" href="custom/css/styles.css">

		 <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
		<!-- Include Modernizr in the head, before any other JS -->
		<script src="bootstrap/js/modernizr-2.6.2.min.js"></script>

                    <!-- Live Search for PDB IDs -->
		<script src="livesearch.js" type="text/javascript"></script>
		


	</head>

	<body id="customBackground">
		<div class="wrapper">
		<div class="container">
		
			<div class="navbar navbar-fixed-top" id="navColor">

				<div class="container">

				<button class="navbar-toggle" data-target=".navbar-responsive-collapse" data-toggle="collapse" type="button">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				

				<a href="index.php" class="navbar-brand"><img src="ADD_IMAGE_HERE" alt="PTGL Logo"></a>
					<div class="nav-collapse collapse navbar-responsive-collapse" id="navbar-example">
						<div class="navbar-scrollspy">
						<ul class="nav navbar-nav">
							<li  class="navbarFont">
								<a href="index.php">Home</a>
							</li>

							<li class="navbarFont">
								<a href="index.php#About">About</a>
							</li>
						
							<li class="navbarFont">
								<a href="index.php#UserGuide">User Guide</a>
							</li>
							
							<li class="navbarFont">
								<a href="index.php#DatabaseFormat">Database Format</a>
							</li>
							
							<li class="dropdown">
								<!-- <strong>caret</strong> creates the little triangle/arrow -->
								<a href="#"  class="navbarFont dropdown-toggle" data-toggle="dropdown"> Services <strong class="caret"></strong></a>
								
								<ul class="dropdown-menu">
									<li>
										<a href="content.php">Content</a>
									</li>
									
									<li>
										<a href="publications.php">Publications</a>
									</li>
									
									<li>
										<a href="#">File Formats</a>
									</li>
									
									<!-- divider class creates a horizontal line in the dropdown menu -->
									<li class="divider"></li>
									
									<li class="dropdown-header"></li>
									
									<li>
										<a href="contact.php">Contact Us</a>
									</li>
									
									<li>
										<a href="help.php">Help</a>
									</li>
								</ul><!-- end dropdown menu -->
							</li><!-- end dropdown -->
						</ul><!-- end nav navbar-nav -->
						</div><!-- end navbar-scrollspy -->
					</div><!-- end nav-collapse -->
					<div class="nav-collapse collapse navbar-responsive-collapse">
						<form  class="navbar-form pull-right" action="searchResults.php" method="get">
							<input type="text" class="form-control" name="keyword" id="searchInput" autocomplete="off" placeholder="Enter PDB ID or keyword...">
							<button type="submit" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button>
							<div id="liveSearchResults" class="liveSearchResultsPage"></div>
						</form><!-- end navbar-form -->	
					</div>
				</div><!-- end container -->
			</div><!-- end navbar fixed-top -->
		</div><!-- end container -->
		
		
		<div class="container" id="about">
			<h2> About </h2>
			
			<h3> Table of contents </h3>
			
			<ul>
				<li class="noBullets"> <a href="#ptgl">What is PTGL?</a> </li>
				<li class="noBullets">
					<ul>
						<li class="noBullets">  <a href="#proteinGraph">Protein Graphs</a> </li>
						<li class="noBullets">
							<ul>
								<li class="noBullets">  <a href="#alphaGraph">Alpha</a> </li>
								<li class="noBullets">  <a href="#betaGraph">Beta</a> </li>
								<li class="noBullets">  <a href="#alphaBetaGraph">Alpha-Beta</a> </li>
							</ul>
						<li  class="noBullets">  <a href="#foldingGraph">Folding Graphs</a> </li>
						<li class="noBullets">  <a href="#linearNot">Linear Notation</a> </li>
						<ul>
							<li class="noBullets">  <a href="#adj">Adjacent notation</a> </li>
							<li class="noBullets">  <a href="#red">Reduced notation</a> </li>
							<li class="noBullets">  <a href="#key">Key notation</a> </li>
							<li class="noBullets">  <a href="#seq">Sequence notation</a> </li>
						</ul>
						</li>
					</ul>
				<li class="noBullets"> <a href="#linking">Linking PTGL</a> </li>
				</li>
				
			
			
			<a name="ptgl"></a>
			<br>
			<br>	
			<h3> <u>What is PTGL?</u> </h3>
			
			PTGL is a web-based database application for protein topologies. In order to define a mathematically unique description of protein topology
			the secondary structure topology of a protein is described by methods of applied graph theory. The <a href="#proteinGraph">Protein graph</a> is defined as an undirected
			labelled graph on three description levels according to the considered secondary structure elements (SSE): the <a href="#alphaGraph">Alpha graph</a>, the <a href="#betaGraph">Beta graph</a>,
			and the <a href="#alphaBetaGraph">Alpha-Beta graph</a>. The connected components of the <a href="#proteinGraph">Protein graph</a> form <a href="#foldingGraph">Folding graphs</a>. A <a href="#proteinGraph">Protein graph</a> can consist of one or more
			<a href="#foldingGraph">Folding graphs</a>. The three graph types were defined for each protein of the <a href="http://www.rcsb.org/pdb/" target="_blank">PDB</a>. For each graph type exists four <a href="#linearNot">linear notations</a> with
			corresponding graphic representations. In PTGL all <a href="#foldingGraph">Folding graphs</a>, all SSEs, and additional protein information are stored for every
			protein structure annotated in <a href="http://www.rcsb.org/pdb/" target="_blank">PDB</a> for which SSEs according DSSP are defined, which is not a NMR structure, has a resolution less than 3.5
			Å and a sequence length of at least 20 amino acids. The database enables the user to search for the topology of a protein or for certain
			topologies and subtopologies using the <a href="#linearNot">linear notations</a>. Additionally, it could be searched for sequence similarity in <a href="http://www.rcsb.org/pdb/" target="_blank">PDB</a> sequences.
			
			<a name="proteinGraph"></a>
			<br>
			<br>
			<h4> <u>Protein Graphs</u> </h4>
			
			<p>Using <a href="http://www.rcsb.org/pdb/" target="_blank">PDB</a> structure data the SSEs are defined according to the assignment of the <a href="http://swift.cmbi.ru.nl/gv/dssp/" target="_blank">DSSP</a>-algorithm with some modifications. Then, the spatial
			contacts between the SSEs are generated according <a href="publications.php">KOCH et al.</a>. These information form the basis for the description of protein structures as
			graphs.</p>
			A Protein graph is defined as labelled undirected graph. In the case of the Protein graph the vertices correspond to the SSEs, helices and
			strands. The edges of the Protein graph represent spatial adjacencies of SSEs. These adjacencies are defined through contacts between SSEs.
			According to the type of atoms forming the contact, there are backbone-backbone-contacts, sidechain-sidechain-contacts, and sidechain-backbone
			contacts. Two vertices are connected, if there are at least two backbone-backbone-contacts or two sidechain-backbone-contacts or three
			sidechain-sidechain contacts. The vertices of the Protein graph are enumerated as they occur in the sequence from the N- to the C-terminus.
			According to this direction two spatial neighboured SSEs, which are connected, could have a parallel (p), anti-parallel (a), or mixed (m)
			neighbourhood. If only helix or strand topology is of interest, the graph modelling allows to exclude the non-interesting SSE type.
			According to the SSE type of interest the Protein graph can be defined as <a href="#alphaGraph">Alpha</a>, <a href="#betaGraph">Beta</a>, or <a href="#alphaBetaGraph">Alpha-Beta</a> graph (see example below). SSEs
			are ordered as red circles (helices) or black quadrats (strands) on a straight line according to their sequential order from the N- to
			the C-terminus. The spatial neighbourhoods are drawn as arcs between SSEs. The edges are coloured according to their labelling, red for
			parallel, green for mixed, and blue for anti-parallel neighbourhood.
			
			
			<a name="alphaGraph"></a>
			<br>
			<br>
			<h5> <u>Alpha Graph</u> </h5>
			The Alpha-Graph of the protein 1TIM chain A consisting only of 13 helices.
			<p class="imgCenter"><img src="./images/1timA_alph.0.png" alt="Alpha Graph of 1timA" title="Alpha Graph of 1timA" class="img-responsive imgFormAboutphp"/></p>
			
			
			<a name="betaGraph"></a>
			<br>
			<br>
			<h5> <u>Beta Graph</u> </h5>
			The Beta-Graph of the protein 1TIM chain A consisting only of 8 strands.
			<p class="imgCenter"><img src="./images/1timA_beta.0.png" alt="Beta Graph of 1timA" title="Beta Graph of 1timA" class="img-responsive imgFormAboutphp"/></p>
			
			<a name="alphaBetaGraph"></a>
			<br>
			<br>
			<h5> <u>Alpha-Beta Graph</u> </h5>
			The Alpha-Beta Graph of the protein 1TIM chain A consisting of 21 SSEs (13 helices and 8 strands). 
			<p class="imgCenter"><img class="img-responsive imgFormAboutphp" src="./images/1timA_albe.0.png" alt="Alpha-Beta Graph of 1timA" title="Alpha-Beta Graph of 1timA"></p>
			
			
			<a name="foldingGraph"></a>
			<br>
			<br>
			<h4> <u>Folding Graphs</u> </h4>
			<p>A connected component of the <a href="#proteinGraph">Protein graph</a> is called Folding graph. Folding graphs are denoted with capital letters in alphabetical order
			according to their occurrence in the sequence, beginning at the N-terminus.</p>
			
			<p><a href="#proteinGraph">Protein graphs</a> are built of one or more Folding graphs. Below, you find the <a href="#rasmolbec">schematic representation</a> of the antigen receptor protein 1BEC
			(figure from the <a href="http://www.fli-leibnitz.de/cgi-bin/ImgLib.pl?Code=1BEC">Jena Library of Biological Macromoleculs</a>). Helices are coloured red and strands blue. 1BEC is a transport membrane protein
			that detects foreign molecules at the cell surface. It has two domains, which are represented by the Folding graphs A and E, which are mainly
			built by strands. The protein consists of one chain A and exhibits six Folding graphs. Two large Folding graphs (Folding graphs 1BEC_A and
			1BEC_E), and four Folding graphs 1BEC_B, 1BEC_C, 1BEC_D, and 1BEC_F consisting only of a single helix (see <href="#alphaBeta1bec">Protein graph of 1bec</a>: helices 9,
			11, 14, and 22). Folding graphs consisting of only one SSE are found mostly at the protein surface and not in the protein core.</p>
			Especially in beta-sheet containing Folding graphs, the maximal vertex degree of the Folding graphs is not larger than two. Thus, we distinguish
			between so-called bifurcated and non-bifurcated topological structures. A <a href="#proteinGraph">Protein graph</a> or a Folding graph is called bifucated, if there is any
			vertex degree greater than 2, if not, the graph is non- bifurcated. 
			
			<a name="rasmolbec"></a>
			<br>
			<br>
			<h5> <u>3D structure of 1BEC</u> </h5>
				<p><img src="./images/1bec.gif" alt="3D structure of 1BEC" title="3D structure of 1BEC" class="img-responsive imgFormAboutphp"/></p>
			
			
			<a name="alphaBeta1bec"></a>
			<h5> <u>Alpha-Beta Protein graph of 1BEC</u> </h5>
				<p><img src="./images/1becA_albe.0.png" alt="Alpha-Beta Protein graph of 1BEC" title="Alpha-Beta Protein graph of 1BEC" class="img-responsive imgFormAboutphp"/></p>
			<h5> <u>Alpha-Beta Folding graph A of 1BEC</u> </h5>
				<p><img src="./images/1becAAa_al.0.png" alt="Alpha-Beta Folding graph A of 1BEC" title="Alpha-Beta Folding graph A of 1BEC" class="img-responsive imgFormAboutphp"/></p>
			<h5> <u>Alpha-Beta Folding graph B of 1BEC</u> </h5>
				<p><img src="./images/1becAEa_al.0.png" alt="Alpha-Beta Folding graph B of 1BEC" title="Alpha-Beta Folding graph B of 1BEC" class="img-responsive imgFormAboutphp"/></p>
			
			<a name="linearNot"></a>
			<br>
			<br>
			<h4> <u>Linear Notations</u> </h4>
			<p>A notation serves as a unique, canonical, and linear description and classification of structures. The notations for Folding graphs reveal to
			the feature of protein structure as a linear sequence of amino acids, and describe the arrangement of SSEs correctly and completely.</p>
			<p>There are two possibilities of representing Protein graphs: first, one can order the SSEs in one line according to their occurrence in sequence,
			or second, according to their occurrence in space. In the first case, the <a href="#adj">adjacent</a> notation, ADJ, the <a href="#red">reduced</a> notation, RED, and the <a href="#seq">sequence</a>
			notation, SEQ, SSEs are ordered as points on a straight line according to their sequential order from the N- to the C-terminus.</p>
			<p>It is difficult to draw the spatial arrangements of the SSEs in a straight line, because in most proteins SSEs exhibit more than two spatial
			neighbours. Therefore, the second description type, the <a href="#key">key</a> notation, KEY, can be drawn only for non-bifurcated Folding graphs. Helices and
			strands are represented by cylinders and arrows, respectively. The sequential neighbourhood is described by arcs between arrows and cylinders.</p>
			The notations are written in different brackets: [] denote non-bifurcated, {} bifurcated folding graphs, and () indicate barrel structures. 
			
			<a name="adj"></a>
			<a name="red"></a>
			<br>
			<br>
			<h5> <u>The adjucent and reduced notation</u> </h5>
			<p>All vertices of the <a href="#proteinGraph">Protein graph</a> are considered in the adjacent (ADJ) notation of a Folding graph. SSEs of the Folding graph are ordered
			according to their occurrence in the sequence. Beginning with the first SSE and following the spatial neighbourhoods the sequential distances
			are noted followed by the neighbourhood type.</p>
			The reduced (RED) notation is the same as for ADJ notation, but only those SSEs of the considered Folding graph count. See below, the ADJ and
			RED notations of the Beta-Folding graph E in human alpha thrombin chain B(1D3T). The beta sheet consists of six strands arranged both in
			parallel with one additional mixed edge to helix 12. 

			<h5> <u>ADJ Notation</u> </h5>
				<p><img src="./images/1d3tBEa_albe.png" alt="Adjacent notation" title="Adjacent notation" class="img-responsive imgFormAboutphp"/></p>
			<h5> <u>RED Notation</u> </h5>
				<p><img src="./images/1d3tBEr_albe.png" alt="Reduced notation" title="Reduced notation" class="img-responsive imgFormAboutphp"/></p>
			<a name="key"></a>
			<br>
			<br>
			<h5> <u>KEY Notation</u> </h5>
			The KEY notation is very close to the topology diagrams of biologists, e.g. Brändén and Tooze (1999). Topologies are described by diagrams of
			arrows for strands and cylinders for helices. As in the <a href="#red">RED</a> notation SSEs of the considered Folding graph are taken into account. SSEs are
			ordered spatially and are connected in sequential order. Beginning with the first SSE in the sequence and following the sequential edges,
			the spatial distances are noted; in <a href="#alphaBetaGraph">Alpha-Beta</a> graphs followed by the type of the SSE, h for a helix and e for a strand. If the arrangement
			of SSEs is parallel an x is noted (Richardson(1977)). In this case the protein chain moves on the other side of the sheet by crossing the
			sheet (cross over). Antiparallel arrangements are called same end, and are more stable, Chothia and Finkelstein (1990). Mixed arrangements
			are defined as same end. The notation starts with the type of the first SSE. See the KEY notation of the <a href="#alphaBetaGraph">Alpha-Beta</a> <a href="#foldingGraph">Folding graph</a> B chain
			B of the histocompatibility antigen (1IEB). The Folding graph consists of 3 helices and 4 strands. This topology exhibits one cross over
			connection from helix 6 to helix 7 and forms an <a href="#alphaBetaGraph">Alpha-Beta</a> barrel structure. 
			
			<p><img src="./images/1iebBk.png" alt="Key notation" title="Key notation" class="img-responsive imgFormAboutphp"/></p>
			
			<a name="seq"></a>
			<br>
			<br>
			<h5> <u>SEQ Notation</u> </h5>
			This notation is the same as the <a href="#adj">ADJ</a> notation, but the sequential differences are counted. Although the SEQ notation is trivial, the notation
			can be useful, for example, searching for ψ-loops requires a special SEQ notation. 
			
			<p><img src="./images/1ars_Bs_beta.png" alt="Sequence notation" title="Sequence notation" class="img-responsive imgFormAboutphp"/></p>
			
			<a name="linking"></a>
			<br>
			<br>
			<h4> <u>Linking PTGL</u> </h4>
			<p>You can link PTGL in two ways:</p>
			<p><b>1.</b> Link to a certain PDB-id, chain id, <a href="#proteinGraph">graph type</a> and <a href="#linearNot">notation type</a>, e.g. PDB-id=1g3e, chain id=A, graph type=Alpha-Beta, and notation type=KEY, then the link is:</p>
			<br>
			<p>The encoding is the following:</p>
			<div class="table-responsive" id="aboutTable">
				<table class="table table-condensed table-hover borderless">
					<tr class="tablecenter">
						<th class="tablecenter">parameter</th>
						<th class="tablecenter">allowed values</th>
						<th class="tablecenter">description</th>
						<tr class="tablecenter">
						</tr>
						<tr class="tablecenter">
							<td></td>
							<td>z</td>
							<td><a href="#alphaBetaGraph">Alpha-Beta</td>
						</tr>
						<tr class="tablecenter">
							<td>topology</td>
							<td>a</td>
							<td><a href="#alphaGraph">Alpha</a></td>
						</tr>
						<tr class="tablecenter">
							<td></td>
							<td>b</td>
							<td><a href="betaGraph">Beta</a></td>
						</tr>
						<tr class="tablecenter">
							<td></td>
							<td>1</td>
							<td><a href="#adj">ADJ</a></td>
						</tr>
						<tr class="tablecenter">
							<td>rep</td>
							<td>2</td>
							<td><a href="#red">RED</a></td>
						</tr>
						<tr class="tablecenter">
							<td></td>
							<td>3</td>
							<td><a href="#key">KEY</a></td>
						</tr>
						<tr class="tablecenter">
							<td></td>
							<td>4</td>
							<td><a href="#seq">SEQ</a></td>
						</tr>
					</tr>
				</table>
			</div>
			
			<p><b>protlist	&#x3c;pdb-id&#x3e;&#x3c;chain-id&#x3e;	  e.g. 1g3eA</b></p>
			
			
			<p><b>2.</b> If you only have the PDB-id you can link as follows:</p>
			
			
		
		
		
		
		
		
		</div><!-- end container and contentText -->
	</div><!-- end wrapper -->
	
	
	<footer id="footer">
		<div class="container">
				<div class="row">
				<div class="col-sm-2">
				</div>
			
				<div class="col-sm-2">
					<a class="footerLink" href="#">Impressum</a>
				</div>
				
				<div class="col-sm-2">
					<a class="footerLink" href="contact.php">Contact</a>
				</div>
				
				<div class="col-sm-2">
					<a class="footerLink" href="http://www.bioinformatik.uni-frankfurt.de" target="_blank">MolBi - Group</a>
				</div>
				
				<div class="col-sm-2">
					<a class="footerLink" href="publications.php">Publications</a>
				</div>
				
				<div class="col-sm-2">
				</div>
			
			<div class="row">
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				
				<div class="col-sm-2 flush-right">
					<br>
					<br>
					Copyright © 2013 [name]
				</div>
			</div>
		</div><!-- end container -->
	</footer>
		
		















		<!-- All Javascript at the bottom of the page for faster page loading -->
		<!-- also needed for the dropdown menus etc. ... -->
		
		<!-- First try for the online version of jQuery-->
		<script src="http://code.jquery.com/jquery.js"></script>
		
		<!-- If no online access, fallback to our hardcoded version of jQuery -->
		<script>window.jQuery || document.write('<script src="bootstrap/js/jquery-1.8.2.min.js"><\/script>')</script>
		
		<!-- Bootstrap JS -->
		<script src="bootstrap/js/bootstrap.min.js"></script>
		
		<!-- Custom JS -->
		<script src="bootstrap/js/script.js"></script>





	</body>
</html>
