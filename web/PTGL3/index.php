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
				

				<a href="#" class="navbar-brand"><img src="ADD_IMAGE_HERE" alt="PTGL Logo"></a>
					<div class="nav-collapse collapse navbar-responsive-collapse" id="navbar-example">
						<div class="navbar-scrollspy">
						<ul class="nav navbar-nav">
							<li  class="active navbarFont">
								<a href="#Home">Home</a>
							</li>

							<li class="navbarFont">
								<a href="#About">About</a>
							</li>
						
							<li class="navbarFont">
								<a href="#UserGuide">User Guide</a>
							</li>
							
							<li class="navbarFont">
								<a href="#DatabaseFormat">Database Format</a>
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
										<a href="#">Help</a>
									</li>
								</ul><!-- end dropdown menu -->
							</li><!-- end dropdown -->
						</ul><!-- end nav navbar-nav -->
						</div><!-- end navbar-scrollspy -->
					</div><!-- end nav-collapse -->
				</div><!-- end container -->
			</div><!-- end navbar fixed-top -->
		
		<div id="Home">
			<div class="container" id="intro">
			<!-- Intro message -->
				PTGL stands for Protein Topology Graph Library. It is a database application for protein topologies.
			</div><!-- end container-->
		</div><!-- end Home -->
		
		
		
						
			<div class="row" id="search">
				<div class="col-lg-3 col-centered">
					<div class="input-group form-group">
						<form class="form-inline" action="searchResults.php" method="get">
							<input type="text" class="form-control" name="keyword" id="searchInput" autocomplete="off" placeholder="Enter PDB ID or keyword...">
							<button type="submit" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button>
							<div id="advancedButton"> Advanced Search  <div id="arrow"><strong class="caret"></strong></div></div>
							<div id="liveSearchResults"></div>
							<label class="checkboxFont">
								<input type="checkbox" id="inlineCheckbox3" name="SelectRedund" value="1"> Remove sequence homologs </input>
							</label>
							<label class="checkboxFont">
								<input type="checkbox" id="matchexactCB" name="exact" value="1"> Match exact word </input>
							</label>
							<div id="advancedSearch">
								<label class="advancedLabel">PDB Identifier
									<input class="advancedInput" type="text" id="bla" name="bla" placeholder="PDB ID" size="6" maxlength="4" />
								</label>
								<label class="advancedLabel">Title
									<input class="advancedInput" type="text" id="bla1" name="bla" placeholder="" size="20" maxlength="50"/>
								</label>
								<label class="advancedLabel">Het
									<input class="advancedInput" type="text" id="bla2" name="bla" placeholder=""" size="20" maxlength="50"/>
								</label>
								<label class="advancedLabel">Hetname
									<input class="advancedInput" type="text" id="bla3" name="bla" placeholder="" size="20" maxlength="50"/>
								</label>	
								<label class="advancedLabel">SCOP
									<input class="advancedInput" type="text" id="bla4" name="bla" placeholder="" size="20" maxlength="50"/>
								</label>
								<label class="advancedLabel">SCOP ID
									<input class="advancedInput" type="text" id="bla5" name="bla" placeholder="" size="4" maxlength="4"/>
									<input class="advancedInput" type="text" id="bla5a" name="bla" placeholder="" size="4" maxlength="4"/>
									<input class="advancedInput" type="text" id="bla5b" name="bla" placeholder="" size="4" maxlength="4"/>
									<input class="advancedInput" type="text" id="bla5c" name="bla" placeholder="" size="4" maxlength="4"/>
								</label>
								<label class="advancedLabel">CATH
									<input class="advancedInput" type="text" id="bla6" name="bla" placeholder="" size="20" maxlength="50"/>
								</label>
								<label class="advancedLabel">CATH ID
									<input class="advancedInput" type="text" id="bla7" name="bla" placeholder="" size="4" maxlength="4"/>
									<input class="advancedInput" type="text" id="bla7a" name="bla" placeholder="" size="4" maxlength="4"/>
									<input class="advancedInput" type="text" id="bla7b" name="bla" placeholder="" size="4" maxlength="4"/>
									<input class="advancedInput" type="text" id="bla7c" name="bla" placeholder="" size="4" maxlength="4"/>
								</label>
								<label class="advancedLabel">EC
									<input class="advancedInput" type="text" id="bla8" name="bla" placeholder="" size="4" maxlength="4"/>
									<input class="advancedInput" type="text" id="bla8a" name="bla" placeholder="" size="4" maxlength="4"/>
									<input class="advancedInput" type="text" id="bla8b" name="bla" placeholder="" size="4" maxlength="4"/>
									<input class="advancedInput" type="text" id="bla8c" name="bla" placeholder="" size="4" maxlength="4"/>
								</label>	
								<label class="advancedLabel">Molecule
									<input class="advancedInput" type="text" id="bla9" name="bla" placeholder="" size="20" maxlength="50"/>
								</label>		
								<label class="advancedLabel">Classification
									<input class="advancedInput" type="text" id="bla00" name="bla" placeholder="" size="20" maxlength="50"/>
								</label>		
								<label class="advancedLabel">Graphs
									<input class="advancedInput" type="text" id="bla01" name="bla" placeholder="" size="20" maxlength="50"/>
								</label>
								<button type="submit" class="btn btn-default advancedInput" style="margin-top:10px;"><span>Search</span></button>
							</div>
						</form>	
					</div><!-- end input-group and form-group -->
				</div><!-- end col-centered -->
			</div><!-- end row -->
					
			
			<div class="row">
				<div class="col-lg-3 col-centered">
					<div class="input-group form-group">
						<form>
						<dl class="dl-horizontal">
					<dt>SearchKey</dt>
					<dd>Keyword search form with examples</dd>
				</dl>
				
				<dl class="dl-horizontal">
					<dt>SearchMotifs</dt>
					<dd>Search form for topological protein structure motifs</dd>
				</dl>
				
				<dl class="dl-horizontal">
					<dt>SearchSequence</dt>
					<dd>Blast search for sequences</dd>
				</dl>
				</form>
					</div><!-- end input-group and form-group -->
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
				<h2>About</h2>
					PTGL is a web-based database application for protein topologies. In order to define a mathematically unique description of protein topology
					the secondary structure topology of a protein is described by methods of applied graph theory. The <a href="about.php#proteinGraph">Protein graph</a> is defined as an undirected
					labelled graph on three description levels according to the considered secondary structure elements (SSE): the <a href="about.php#alphaGraph">Alpha graph</a>, the <a href="about.php#betaGraph">Beta graph</a>,
					and the <a href="about.php#alphaBetaGraph">Alpha-Beta graph</a>. The connected components of the <a href="about.php#proteinGraph">Protein graph</a> form <a href="about.php#foldingGraph">Folding graphs</a>. A <a href="about.php#proteinGraph">Protein graph</a> can consist of one or more
					<a href="about.php#foldingGraph">Folding graphs</a>. The three graph types were defined for each protein of the <a href="http://www.rcsb.org/pdb/" target="_blank">PDB</a>. For each graph type exists four <a href="about.php#linearNot">linear notations</a> with
					corresponding graphic representations. In PTGL all <a href="about.php#foldingGraph">Folding graphs</a>, all SSEs, and additional protein information are stored for every
					protein structure annotated in <a href="http://www.rcsb.org/pdb/" target="_blank">PDB</a> for which SSEs according DSSP are defined, which is not a NMR structure, has a resolution less than 3.5
					Å and a sequence length of at least 20 amino acids. The database enables the user to search for the topology of a protein or for certain
					topologies and subtopologies using the <a href="about.php#linearNot">linear notations</a>. Additionally, it could be searched for sequence similarity in <a href="http://www.rcsb.org/pdb/" target="_blank">PDB</a> sequences.
					<a href="about.php">Read more...</a>
			
			
			</div><!-- end container and text -->
		</div><!-- end About -->
		
		<div id="UserGuide">
			<div class="line" id="lineUserGuide">
				<div class="iconPos" id="iconUserGuide">
					<img width="100px" height="100px" src="images/icon-greenGlyph.png" alt="Icon"/>
				</div><!-- end iconPos -->
			</div><!-- end line and lineUser Guide -->
			
			<div class="container" id="text">
				<h2>User Guide</h2>
					<p>
						Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam 						erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata 						sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor 						invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet 						clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing 					elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et 					justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
					</p>
					<p>
						Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam 						erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata 						sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor 						invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet 						clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing 					elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et 					justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
					</p>
					<p>
						Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam 						erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata 						sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor 						invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet 						clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing 					elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et 					justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
					</p>
					<p>
						Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam 						erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata 						sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor 						invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet 						clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing 					elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et 					justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
					</p>
			</div><!-- end container and text -->
		</div><!-- end UserGuide -->
		
		
		<div id="DatabaseFormat">
			<div class="line" id="lineDatabaseFormat">
				<div class="iconPos" id="iconDatabaseFormat">
					<img width="100px" height="100px" src="images/icon-yellowGlyph.png" alt="Icon"/>
				</div><!-- end iconPos -->
			</div><!-- end line and lineDatabaseFormat -->
			
			<div class="container" id="text">
			<h2>Database Format</h2>
				<p>
					Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam 						erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata 						sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor 						invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet 						clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing 					elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et 					justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
				</p>
				<p>
						Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam 						erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata 						sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor 						invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet 						clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing 					elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et 					justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
					</p>
					<p>
						Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam 						erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata 						sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor 						invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet 						clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing 					elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et 					justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
					</p>
		</div><!-- end container and text -->
	</div><!-- end DatabaseFormat -->
	</div><!--- end textWrapper -->
	
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
