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

		 <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
		<!-- Include Modernizr in the head, before any other JS -->
		<script src="js/modernizr-2.6.2.min.js"></script>

                    <!-- Live Search for PDB IDs -->
		<script src="js/livesearch.js" type="text/javascript"></script>
		


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
										<a href="#">Help</a>
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
		
		
		
		<h3>Standard Search</h3>
			<p>In order to search for proteins simply enter the protein name or the
			corresponding protein PDB-ID into the search field.</p>
			<p>You can also use the two checkboxes to either remove sequence homologs from
			results or if you only want results that exactly match the input query.</p>
			
			
		<h3>Advanced Search</h3>
			<a class="anchor" id="helpPDBID"></a>
			<p><h4>PDB ID</h4> The 4 character unique indentifier for the structure as defined by the PDB. Begins with a number 1-9 followed by 3 alphanumeric characters. </p>
			<p>Examples:</p>
			
			
			
			
			<a class="anchor" id="helpTitle"></a>
			<p><h4>TITLE</h4> The TITLE record contains a title for the experiment or analysis that is represented in the entry. It should identify an entry in the PDB in the same way that a title identifies a paper.The PDB records COMPND, SOURCE, EXPDTA, and REMARKs provide information that may also be found in TITLE. You may think of the title as describing the experiment, and the compound record as describing the molecule(s).</p>
			<p>Examples:</p>
			
			
			<a href="" class="anchor" id="helpClass"></a>
			<p id="scroll2"><h4>Classification</h4> A classification for the molecule. </p>
			<p>Examples:</p>
			
			
			<a href="" class="anchor" id="helpHet"></a>
			<p><h4>HET</h4> The heterogen section of a PDB file contains the complete description of non-standard residues in the entry.</p>
			<p>Examples:</p>
			
			
			
			<a href="" class="anchor" id="helpHetn"></a>
			<p><h4>HETNAME</h4> This record gives the chemical name of the compound with the given HET</p>
			<p>Examples:</p>
			
			
			<a href="" class="anchor" id="helpScop"></a>
			<a href="" class="anchor" id="helpMol"></a>
			<p><h4>SCOP</h4> SCOP classification.</p>
			<p>Examples:</p>
			
			
			<a href="" class="anchor" id="helpScopid"></a>
			<p><h4>SCOPID</h4> SCOP identifier.</p>
			<p>Examples:</p>
			
			
			<a href="" class="anchor" id="helpCath"></a>
			<p><h4>CATH</h4> CATH classification.</p>
			<p>Examples:</p>
			
			
			
			<a href="" class="anchor" id="helpCathid"></a>
			<p><h4>CATHID</h4> CATH domain identifier.</p>
			<p>Examples:</p>
			
			
			
			<a href="" class="anchor" id="helpEc"></a>
			<p><h4>EC</h4> ENZYME number.</p>
			<p>Examples:</p>
			
			
			
			<a href="" class="anchor" id="helpMol"></a>
			<p><h4>Molecule</h4> Name of the molecule.</p>
			<p>Examples:</p>
			
			
			<a href="" class="anchor" id="helpGra"></a>
			<p><h4>Graphs</h4> A string standing for a Linear Notation of a certain Folding Graph.</p>
			<p>Examples:</p>
		
		
		
		
		
		
		
		<h3>Search Key</h3>
		<p>Use this form to search protein topologies.</p>
		<p>Enter keywords known to relate to the biological macromolecules of interest and select the "Search" button.</p>
		
		<br>
		<br>
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
		
		
		<br>
		<br>
		<a href="" class="anchor" id="helpattributes"></a>
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
		
		
		
		<br>
		<br>
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

		<br>
		<br>
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
		
		
		<h3>Search Motifs</h3>
		<h3>Search Sequence</h3>
			
		
		
		
		
		
		</div>
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
		<script>window.jQuery || document.write('<script src="js/jquery-1.8.2.min.js"><\/script>')</script>
		
		<!-- Bootstrap JS -->
		<script src="js/bootstrap.min.js"></script>
		
		<!-- Custom JS -->
		<script src="js/script.js"></script>


	</body>
</html>
