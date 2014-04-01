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
										<a href="about.php"><span class="fa fa-info"></span> About</a>
									</li>
									<li>
										<a href="content.php"><i class="fa fa-briefcase"></i> Content</a>
									</li>
									
									<li>
										<a href="publications.php"><i class="fa fa-copy"></i> Publications</a>
									</li>
																	
									<!-- divider class creates a horizontal line in the dropdown menu -->
									<li class="divider"></li>
									
									<li class="dropdown-header"></li>
									
									<li>
										<a href="contact.php"><i class="fa fa-user"></i> Contact Us</a>
									</li>
									
									<li>
										<a href="#"><i class="fa fa-question"></i> Help</a>
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
		
		
		<div class="container" id="help">
		<h2>Help</h2>
		
		<h3> Table of contents </h3>
			<ul>
				<li class="noBullets"><a href="#helpStandardSearch">Standard Search</a></li>
				<li class="noBullets"><a href="#helpAdvancedSearch">Advanced Search</a></li>
					<ul>
						<li class="noBullets"><a href="#helpPDBID">PDB ID</a></li>
						<li class="noBullets"><a href="#helpTitle">TITLE</a></li>
						<li class="noBullets"><a href="#helpClass">Classification</a></li>
						<li class="noBullets"><a href="#helpHet">HET</a></li>
						<li class="noBullets"><a href="#helpHetn">HETNAME</a></li>
						<li class="noBullets"><a href="#helpScop">SCOP</a></li>
						<li class="noBullets"><a href="#helpScopid">SCOP ID</a></li>
						<li class="noBullets"><a href="#helpCath">CATH</a></li>
						<li class="noBullets"><a href="#helpCathid">CATH ID</a></li>
						<li class="noBullets"><a href="#helpEc">EC</a></li>
						<li class="noBullets"><a href="#helpMol">Molecule</a></li>
						<li class="noBullets"><a href="#helpGra">Graphs</a></li>
					</ul>
				<li class="noBullets"><a href="#helpSearchKey">Search Key</a></li>
						<ul>
							<li class="noBullets"><a href="#helpSimpleQueries">Simple Queries</a></li>
							<li class="noBullets"><a href="#helpAttributes">Attributes</a></li>
							<li class="noBullets"><a href="#helpComplexQueries">Complex Queries</a></li>
							<li class="noBullets"><a href="#helpExamples">Examples</a></li>
						</ul>
				<li class="noBullets"><a href="#helpSearchMotifs">Search Motifs</a></li>
					<ul>
						<li class="noBullets"><a href="#helpUsage">Usage</a></li>
						<li class="noBullets"><a href="#helpProteinStructureMotifs">Protein Structure Motifs</a></li>
						<li class="noBullets"><a href="#helpAlphaMotifs">Alpha Motifs</a></li>
						<li class="noBullets"><a href="#helpBetaMotifs">Beta Motifs</a></li>
						<li class="noBullets"><a href="#helpAlphaBetaMotifs">Alpha Beta Motifs</a></li>
					</ul>
				<li class="noBullets"><a href="#helpSearchSequence">Search Sequence</a></li>
					<ul>
						<li class="noBullets"><a href="#helpQueries">Queries</a></li>
						<li class="noBullets"><a href="#helpAttributes2">Attributes</a></li>
						<li class="noBullets"><a href="#helpExamples2">Examples</a></li>
					</ul>
			
			</ul>
		
		<br>
		<br>
		<a class="anchor" id="helpStandardSearch"></a>
		<h3>Standard Search</h3>
			<p>In order to search for proteins simply enter the protein name or the
			corresponding protein PDB-ID into the search field.</p>
			<p>You can also use the two checkboxes to either remove sequence homologs from
			results or if you only want results that exactly match the input query.</p>
			<p>You can search two or more protein topologies in the 
			PTGL database and get a list with all informations of these topologies.</p>
			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		<br>
		<br>
		<a class="anchor" id="helpAdvancedSearch"></a>
		<h3>Advanced Search</h3>
			<a class="anchor" id="helpPDBID"></a>
			<p><h4>PDB ID</h4> The 4 character unique indentifier for the structure as defined by the PDB. Begins with a number 1-9 followed by 3 alphanumeric characters. </p>
			<p>Examples:</p>
			
			
			
			<br>
			<a class="anchor" id="helpTitle"></a>
			<p><h4>TITLE</h4> The TITLE record contains a title for the experiment or analysis that is represented in the entry. It should identify an entry in the PDB in the same way that a title identifies a paper.The PDB records COMPND, SOURCE, EXPDTA, and REMARKs provide information that may also be found in TITLE. You may think of the title as describing the experiment, and the compound record as describing the molecule(s).</p>
			<p>Examples:</p>
			
			<br>
			<a class="anchor" id="helpClass"></a>
			<p id="scroll2"><h4>Classification</h4> A classification for the molecule. </p>
			<p>Examples:</p>
			
			<br>
			<a class="anchor" id="helpHet"></a>
			<p><h4>HET</h4> The heterogen section of a PDB file contains the complete description of non-standard residues in the entry.</p>
			<p>Examples:</p>
			
			
			<br>
			<a class="anchor" id="helpHetn"></a>
			<p><h4>HETNAME</h4> This record gives the chemical name of the compound with the given HET</p>
			<p>Examples:</p>
			
			<br>
			<a class="anchor" id="helpScop"></a>
			<p><h4>SCOP</h4> SCOP classification.</p>
			<p>Examples:</p>
			
			<br>
			<a class="anchor" id="helpScopid"></a>
			<p><h4>SCOPID</h4> SCOP identifier.</p>
			<p>Examples:</p>
			
			<br>
			<a class="anchor" id="helpCath"></a>
			<p><h4>CATH</h4> CATH classification.</p>
			<p>Examples:</p>
			
			
			<br>
			<a class="anchor" id="helpCathid"></a>
			<p><h4>CATHID</h4> CATH domain identifier.</p>
			<p>Examples:</p>
			
			
			<br>
			<a class="anchor" id="helpEc"></a>
			<p><h4>EC</h4> ENZYME number.</p>
			<p>Examples:</p>
			
			
			<br>
			<a class="anchor" id="helpMol"></a>
			<p><h4>Molecule</h4> Name of the molecule.</p>
			<p>Examples:</p>
			
			<br>
			<a class="anchor" id="helpGra"></a>
			<p><h4>Graphs</h4> A string standing for a Linear Notation of a certain Folding Graph.</p>
			<p>Examples:</p>
		
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		
		
		<br>
		<br>
		<a class="anchor" id="helpSearchKey"></a>
		<h3>Search Key</h3>
		<p>Use this form to search protein topologies.</p>
		<p>Enter keywords known to relate to the biological macromolecules of interest and select the "Search" button.</p>
		
			
		<br>
		<a class="anchor" id="helpSimpleQueries"></a>
		<h4>Simple Queries</h4>
		<p>The simplest query is a single keyword, such as:</p>
		<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>kinase</tt></p>
		<p>Note that partial word searches are supported, hence</p>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>kinas</tt> or
		<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>inase</tt>
		<p>would also return kinase (as well as other things).</p>
		<p id="scroll3">Such queries can be time consuming and result in too many hits of little consequence.</p>
		<p>Queries can be made more meaningful by:</p>
		<ul>
			<li>
				using a phrase e.g. <tt>tyrosine kinase</tt>
			</li>
			<li>
				Distinguishing kinase as the header and a compound <i>attribute</i> which includes 
				the word <tt>kinase</tt> as follows:
			</li>
		</ul>
		<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>header: kinase</tt></p>
		<p>where <tt>header</tt> is an example of an <i>attribute </i>and <tt>kinase </tt>
		is an example of a text string. Simply stated, <u>only</u> <tt>header</tt> 
		fields are searched for the string kinase, not the complete PTGL database. 
		The text string following an attribute can be a phrase containing multiple words.</p>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<a class="anchor" id="helpAttributes"></a>
		<h4>Attributes</h4>
		<p>The following is a list of <b>attributes</b> and their definitions:</p>
		<p><strong>header</strong> Text found in a PTGL database that refers to the PDB-HEADER record, which classifies the macromolecule.</p>
		<p><strong>compound</strong> Text found in a PTGL COMPND record, which describes the macromolecular components or the experiment or analysis of the macromolecule of the PTGL entry.</p>
		<p><strong>het</strong> Text found in the HET records, a 3 letter/number abbreviation for HET groups.</p>
		<p><strong>hetname</strong> Description of the HET group as found in the HET record.</p>
		<p><strong>pdb_id</strong> Text describing the unique PDB identifier as found in the PDB HEADER record and consisting of 4 alphanumeric characters.</p>
		<p><strong>molecule</strong> Text found in the PTGL MOLECULE records which describes the name/function of a certain protein chain.</p>
		<p><strong>scop</strong> Text found in the PTGL SCOP table records CLASS, FOLD, SUPERFAMILY, FAMILY, DOMAIN, SPECIES,
		which describe the different hierarchy levels, domain name or species name of a certain SCOP domain.</p>
		<p><strong>cath</strong> Text found in the PTGL CATH table records CLASS, TOPOLOGY, ARCHITECTURE, HOMOLOGOUS FAMILY, COMM, SOURCE
		which describe the different hierarchy levels, the species and some comments of a certain CATH domain.</p>
		
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<a class="anchor" id="helpComplexQueries"></a>
		<h4>Complex Queries</h4>
		<h5><u>Boolean Expressions</u></h5>
		<p>The attributes can be combined with a Boolean (AND/OR/NOT) operator.</p>
		<p>Examples:</p>
		<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>header: kinase and het: hem</tt></p>
		<p>all topologies whose header contains the word <i>kinase</i> and have the HET-group <i>hem</i>.</p>
		<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>het: hem or hetname: Zn</tt></p>
		<p>all topologies which have a HET group <i>hem</i> or a HETNAME description which contains the word <i>Zn</i>.</p>
		<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>kinase not compound: complexed</tt></p>
		<p>all topologies containing the phrase <i>kinase</i> AND NOT the phrase <i>complexed</i> in the record COMPOUND.</p>
		<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>((header: kinase and het: hem) not molecule: glucose) not compound: alpha-amylase</tt></p>
		<p>all topologies whose HEADER contains <i>kinase</i> and which a HET group <i>hem</i>, that has no MOLECULE whose name contains the word <i>glucose</i> and do not contain the COMPOUND <i>alpha-amylase</i>.</p>

		<h5><u>Additional Information</u></h5>
		<p>The characters *, & and , are ignored if they are included as part of a keyword.</p>
		<p>It is necessary to use ( )'s to maintain the logic of a query that uses more than one "'ot' clause, or use the Refine Your Query option at the top of the Query Results page.</p>
		<p>Search strings should not begin with the 'not' operator. </p>
		<p>Operators should only be used individually (for example, the syntax 'and not' should not be used).</p>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<a class="anchor" id="helpExamples"></a>
		<h4>Examples</h4>
		<p><strong>kinase complexed</strong>&nbsp;&nbsp; Find all topologies in PTGL containing the phrase kinase complexed in the <a href="#helpattributes">HEADER-</a>, <a href="#helpattributes">COMPOUND-</a>, <a href="#helpattributes">MOLECULE-</a>, <a href="#helpattributes">SCOP-</a>, <a href="#helpattributes">CATH-</a> and <a href="#helpattributes">HETNAM-</a>attributes of the PTGL-database.</p>
		<br>
		<p><strong>complexed and kinase</strong>&nbsp;&nbsp; Find all topologies containing the word complexed and the word kinase. This is a larger number of topologies than in the list of entries containing the phrase kinase complexed.</p>
		<br>
		<p><strong>complexed or kinase</strong>&nbsp;&nbsp;	Find all topologies containing the word complexed or the word kinase.</p>
		<br>
		<p><strong>complexed and not kinase</strong>&nbsp;&nbsp; Find all topologies containing the word complexed and not the word kinase.</p>
		<br>
		<p><strong>pdb_id: 4HHB</strong>&nbsp;&nbsp; Find the single structure with the PTGL ID of 4HHB. PDB_ID is an example of an <a href="#helpattributes">attribute</a>.</p>
		<br>
		<p><strong>header:not phosphotransferase</strong>&nbsp;&nbsp; Find all topologies that don't contain the term phosphotransferase in their PTGL HEADER records. HEADER is an example of an <a href="#helpattributes">attribute</a>.</p>
		<br>
		<p><strong>header: phosphotransferase and molecule: glucose</strong>&nbsp;&nbsp; Find all topologies that contain the term phosphotransferase in their PTGL HEADER records AND the term glucose in their PTGL MOLECULE records.</p>
		<br>
		<p><strong>header: phosphotransferase or molecule: glucose</strong>&nbsp;&nbsp; Find all topologies that contain the term phosphotransferase in their PTGL HEADER records OR the term glucose in their PTGL MOLECULE records.</p>
		<br>
		<p><strong>header: phosphotransferase and molecule: glucose and hetname:Zn</strong>&nbsp;&nbsp; Find all topologies that contain the term phosphotransferase in their PTGL HEADER records AND the term glucose in their PTGL MOLECULE records AND the term Zn in their PTGL HETNAME records.</p>
		<br>
		<p><strong>header: phosphotransferase and molecule: glucose and hetname:not Zn</strong>&nbsp;&nbsp; Find all topologies that contain the term phosphotransferase in their PTGL HEADER records AND the term glucose in their PTGL MOLECULE records but not the term Zn in their PTGL HETNAME records.</p>
		<br>
		<p><strong>((header: phosphotransferase and compound:phosphotransferase) and molecule: not glucose) and compound: not alpha-amylase</strong>&nbsp;&nbsp; Find all topologies that contain phosphotransferase in their PTGL HEADER and PTGL COMPOUND records, that do not contain glucose in their PTGL MOLECULE records, and do not contain alpha-amylase in their PTGL COMPOUND records. It is necessary to use ( )'s to maintain the logic of a query that uses more than one "not" clause.</p>
		<br>
		<p><strong>molecule: hemoglobin and header: oxygenase</strong>&nbsp;&nbsp; All topologies of oygenases from hemoglobin chains. </p>
		<br>
		<p><strong>header: kinase and compound: complexed glucose</strong>&nbsp;&nbsp; Protein kinases complexed with glucose.</p>
		<br>
		<p><strong>compound: lysozyme and compound: native</strong>&nbsp;&nbsp; Find native lysozyme topologies. </p>
		<br>
		<p><strong>hetname: FE.</strong>&nbsp;&nbsp; Find all topologies whose FE in the HET group.</p>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		<br>
		<br>
		<a class="anchor" id="helpSearchMotifs"></a>
		<h3>Search Motifs</h3>
		<p>Use this form to search topological protein structure motifs.
		Select one of the protein structure classes, either Alpha Motifs, Beta Motifs or Alpha Beta Motifs.
		<br>
		<p>SearchMotifs provides a search for common protein structure motifs in the PTGL database and returns a list with all 
		informations about these motifs. If, after reading the explanation of using SearchMotifs, you need additional help about 
		the usage, please send an e-mail to <a href="mailto:ina.koch@tfh-berlin.de">ina.koch@tfh-berlin.de</a>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>

		<br>
		<a class="anchor" id="helpUsage"></a>
		<h4>Usage</h4>
		For further information, click on the question mark behind each motif. </p>
		<p>To submit a search request you have to select one of the following protein structure classes, 
		either <a href="#helpAlpha">>Alpha Motifs</a>, <a href="#helpBeta">Beta Motifs</a> or <a href="#helpAlphaBeta">Alpha Beta Motifs</a>. 
		For each of these classes a subset of common folds is defined.</p>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<a class="anchor" id="helpProteinStructureMotifs"></a>
		<h4>Protein Structure Motifs</h4>
		<p>Classification of Protein Structure Motifs is based on the possible composition of secondary structure 
		elements in a protein domain. One differentiates between <a href="#helpAlpha">>Alpha Motifs</a>, which consist solely of alpha helices, 
		<a href="#helpBeta">Beta Motifs</a>, which are composed of beta sheets and <a href="#helpAlphaBeta">Alpha Beta Motifs</a>, a combination 
		of both, alpha helices and beta sheets.</p>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<a class="anchor" id="helpAlphaMotifs"></a>
		<h4>Alpha Motifs</h4>
		<h5><u>Four Helix Bundle</u></h5>
		<p>The Four Helix Bundle is a Protein Motif which consists of 4 alpha helices which arrange in a bundle. 
		There are two types of the Four Helix Bundle which differ in the connections between the alpha helices. 
		The first type of the Four Helix Bundle is all antiparallel (left picture) and the second type has two pairs of parallel helices 
		which have an antiparallel connection (right picture).</p>
		
		<img src="./images/4helixbeide_struktur.jpg" alt="Four helix bundle Typ I/Typ II" title="Four helix bundle Typ I/Typ II" class="img-responsive imgFormHelp"/>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
	
		<br>
		<h5><u>Globin Fold</u></h5>
		<p>The Globin Fold is an alpha helix structure motif which is composed of a bundle consisting 
		of eight alpha helices, which are connected over short loop regions. The helices have not a 
		fixed arrangement (dotted lines in the picture) but the last two helices in sequential order make up an antiparallel pair.</p>
		
		<img src="./images/globin_struktur.jpg" alt="Globin Fold" title="Globin Fold" class="img-responsive imgFormHelp"/>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<a class="anchor" id="helpBetaMotifs"></a>
		<h4>Beta Motifs</h4>
		<h5><u>Up and Down Barrel</u></h5>
		<p>The Up and Down Barrel is composed of a series of antiparallel beta strands which are 
		connected over hydrogen bonds. There are two major families of the Up and Down Barrel, 
		the ten-stranded and the eight-stranded version. The picture below shows the the schematic 
		representation of an eight-stranded type and an example three-dimensional image.</p>
		
		<img src="./images/barrel_struktur.jpg" alt="Up and Down Barrel" title="Up and Down Barrel" class="img-respnsive imgFormHelp"/>
<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		
		<br>
		<h5><u>Immunoglobulin Fold</u></h5>
		<p>The Immunoglobulin Fold is a two-layer sandwich. It consists of usually seven 
		antiparallel beta-strands, which arrange in two beta sheets. The first is composed of 
		four and the second of three strands. Both are connected over a disulfide bond to build 
		the sandwich.</p>
		
		<img src="./images/immuno_struktur.jpg" alt="Immunoglobulin Fold" title="Immunoglobulin Fold" class="img-responsive imgFormHelp"/>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		
		<br>
		<h5><u>Beta Propeller</u></h5>
		<p>This beta motif contains between four and eight beta sheets, which are arrange around 
		the center of the protein. Four antiparallel beta strand form each sheet. One sheet makes 
		up one of the propeller blades. To build a four-bladed propeller four of these sheets 
		are grouped together. The image shows a four-bladed propeller motif.</p>
		
		<img src="./images/propeller_struktur.jpg" alt="Beta Propeller" title="Beta Propeller" class="img-responsive imgFormHelp"/>
<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<h5><u>Jelly Roll</u></h5>
		<p>The Jelly Roll motif has a barrel structure, which seems like a jelly roll. 
		The barrel includes eight beta strands, which build a two-layer sandwich, which each 
		hold four strands.</p>
		
		<img src="./images/jelly_struktur.jpg" alt="Jelly Roll" title="Jelly Roll" class="img-responsive imgFormHelp"/>
<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<a class="anchor" id="helpAlphaBetaMotifs"></a>
		<h4>Alpha Beta Motifs</h4>
		<h5><u>Ubiquitin Roll</u></h5>
		<p>The Ubiquitin Roll motif contains alpha helices and beta strands. Two different types 
		must be considerd. The first is composed of two beta-hairpin motifs and an enclosed 
		alpha helix and the second an alpha helix which is surrounded by two beta sheets.</p>

		<img src="./images/ubib_struktur.jpg" alt="Ubiquitin Roll" title="Ubiquitin Roll" class="img-responsive imgFormHelp"/>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<h5><u>Alpha-Beta Plaits</u></h5>
		<p>This motif has four of five beta strand, which form an antiparallel beta sheet. In-between 
		the sheet are two or more alpha helices. The image shows a schematic arrangement.</p>
		
		<img src="./images/plait_struktur.jpg" alt="Alpha-Beta Plaits" title="Alpha-Beta Plaits" class="img-responsive imgFormHelp"/>
<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<h5><u>Rossman Fold</u></h5>
		<p>The Rossman Fold folds in an open twisted parallel beta sheet, in which the strands lie 
		in-between alpha helices.</p>
		
		<img src="./images/rossman_struktur.jpg" alt="Rossman Fold" title="Rossman Fold" class="img-responsive imgFormHelp"/>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<h5><u>TIM Barrel</u></h5>
		<p>The TIM Barrel includes eight beta strand and eight alpha helices. The eight parallel 
		strands compose the inner part of the structure and the alpha helices lie around them to 
		make up the outer part of the motif.</p>
		
		<img src="./images/tim_struktur.jpg" alt="TIM Barrel" title="TIM Barrel" class="img-responsive imgFormHelp"/>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<br>
		<a class="anchor" id="helpSearchSequence"></a>
		<h3>Search Sequence</h3>
		<p>Search PTGL with your protein sequence using <a href="http://www.ncbi.nlm.nih.gov/BLAST/blastcgihelp.shtml" target="_blank">BLASTP</a>.</p>
		
		<br>
		<a class="anchor" id="helpQueries"></a>
		<h4>Queries</h4>
		<p>The simplest query is a single PDB-ID, such as:</p>
		<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>1kga</tt></p>
		<p>Two or more PDB-IDs can be elected by the following terms:</p>
		<p>using a list like</p>
		<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>1hkb, 1kba, 2cse, 4dfh</tt></p>
		<p>or like</p>
		<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>1hkb; 1kba; 2cse; 4dfh; 1ard</tt></p>
		<p>The PDB-IDs must be separated by , or ;. There is no limit of PDB-IDs.</p>
<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<a class="anchor" id="helpAttributes2"></a>
		<h4>Attributes</h4>
		<p>The following is a list of <b>attributes</b> and their definitions:</p>
		<p><strong>pdb_id</strong>&nbsp;&nbsp; Text describing the unique PDB identifier as found in the PDB HEADER record and 
		consisting of 4 alphanumeric characters.</p>
<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		
		<br>
		<a class="anchor" id="helpExamples2"></a>
		<h4>Examples</h4>
		<p><b>1hkb,1hba,1cse,1ard</b>&nbsp;&nbsp; Find the four topologies <i>1hkb</i>, <i>1hba</i>, <i>1cse</i> and <i>1ard</i> in the PTGL-database</p>
		<p><b>1hkb;1hba;1cse;1ard</b>&nbsp;&nbsp; Find the four topologies <i>1hkb</i>, <i>1hba</i>, <i>1cse</i> and <i>1ard</i> in the PTGL-database></p>
		<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>
		</div>
		</div><!-- end container and contentText -->
	</div><!-- end wrapper -->
	
	
	<footer id="footer">
		<div class="container">
			<div class="row">
				<div class="col-sm-2">
					<a href="about.php" class="footerLink">About</a>
				</div>
			
				<div class="col-sm-2">
					<a class="footerLink" href="help.php">Help</a>
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
					<a href="about.php#linking" class="footerLink">Linking PTGL</a>
				</div>
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
		<script>window.jQuery || document.write('<script src="js/jquery-1.8.2.min.js"><\/script>')</script>
		
		<!-- Bootstrap JS -->
		<script src="js/bootstrap.min.js"></script>
		
		<!-- Custom JS -->
		<script src="js/script.js"></script>


	</body>
</html>
