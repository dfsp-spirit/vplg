<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="">
	<link rel="shortcut icon" href="../../docs-assets/ico/favicon.png">

	<title>VPLG -- Motif search</title>

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

	<div class="container" id="text">
			<h2>User Guide</h2>
				<div class="row" id="scroll1">
					<div class="col-lg-3 col-lg-3-centering col-centered">
						<div class="ugCentering" id="ugStandardSearch">
							<h3 class="ugCenterHeadings">Standard Search</h3>
							<p>In order to search for proteins simply enter the protein name or the
							corresponding protein PDB-ID into the search field.</p>
							<p>You can also use the two checkboxes to either remove sequence homologs from
							results or if you only want results that exactly match the input query.</p>
						</div><!-- end ugCentering -->
					</div><!-- end col-centered -->
				</div><!-- end row -->

				<div class="row">
					<div class="col-lg-3 col-lg-3-centering col-centered">
						<div class="ugCentering" id="ugAdvancedSearch">
							<h3 class="ugCenterHeadings">Advanced Search</h3>
							<p><strong>PDB ID</strong> The 4 character unique indentifier for the structure as defined by the PDB. Begins with a number 1-9 followed by 3 alphanumeric characters. </p>
							<p><strong>TITLE</strong> The TITLE record contains a title for the experiment or analysis that is represented in the entry. It should identify an entry in the PDB in the same way that a title identifies a paper.The PDB records COMPND, SOURCE, EXPDTA, and REMARKs provide information that may also be found in TITLE. You may think of the title as describing the experiment, and the compound record as describing the molecule(s).</p>
							<p id="scroll2"><strong>Classification</strong> A classification for the molecule. </p>
							<p><strong>HET</strong> The heterogen section of a PDB file contains the complete description of non-standard residues in the entry.</p>
							<p><strong>HETNAME</strong> This record gives the chemical name of the compound with the given HET</p>
							<p><strong>SCOP</strong> SCOP classification.</p>
							<p><strong>SCOPID</strong> SCOP identifier.</p>
							<p><strong>CATH</strong> CATH classification.</p>
							<p><strong>CATHID</strong> CATH domain identifier.</p>
							<p><strong>EC</strong> ENZYME number.</p>
							<p><strong>Molecule</strong> Name of the molecule.</p>
							<p><strong>Graphs</strong> A string standing for a Linear Notation of a certain Folding Graph.</p>
							</div><!-- end ugCentering -->
					</div><!-- end col-centered -->
				</div><!-- end row -->


				<div class="row" id="ugSearchkms">
					<div class="col-lg-4" id="ugSearchk">
						<h3>Search Key</h3>
							<p>Use this form to search protein topologies.</p>
							<p>Enter keywords known to relate to the biological macromolecules of interest
							and select the "Search" button.</p>
							<br>
							<p></p>
							<br id="scroll3">
							<br>
							<br>
							<br>
							<br>
							<br>
							<br>
							<br>
							<br>
							</div><!-- end col-centered -->

					<div class="col-lg-4" id="ugSearchm">
						<h3>Search Motifs</h3>
							<p>Use this form to search topological protein structure motifs.
							Select one of the protein structure classes, either Alpha Motifs, Beta Motifs or Alpha Beta Motifs.
							For further information, click on the question mark behind each motif. </p>
							<p>To submit a search request you have to select one of the following protein stucture classes, 
							either <strong>Alpha Motifs</strong>, <strong>Beta Motifs</strong> or <strong>Alpha Beta Motifs</strong>. 
							For each of these classes a subset of common folds is defined.</p>
							<p>Classification of Protein Structure Motifs is based on the possible composition of secondary structure 
							elements in a protein domain. One differentiates between Alpha Motifs, which consist solely of alpha helices, 
							Beta Motifs, which are composed of beta sheets and Alpha Beta Motifs, a combination of both, alpha helices and 
							beta sheets.</p>
					</div><!-- end col-centered -->

					<div class="col-lg-4" id="ugSearchs">
						<h3>Search Sequence</h3>
							<p>Search VPLG with your protein sequence using <a href="http://www.ncbi.nlm.nih.gov/BLAST/blastcgihelp.shtml" target="_blank">BLASTP</a>.</p>
							<br>
							<br>
							<br>
							<br>
							<br id="scroll3">
							<br>
							<br>
							<br>
							<br>
							<br>
							<br>
							<br>
							<br>
							<br>
					</div><!-- end col-centered -->
				</div><!-- end row -->

				<div class="row" id="ugSearchkms1">
					<div class="col-lg-4">
						<h3>Simple Queries</h3>
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
							fields are searched for the string kinase, not the complete VPLG database. 
							The text string following an attribute can be a phrase containing multiple words.</p>


					</div><!-- end col-centered -->

					<div class="col-lg-4 ugBorder">
						<h3>Attributes</h3>
							<p>The following is a list of <b>attributes</b> and their definitions:</p>
							<p><strong>header</strong> Text found in a VPLG database that refers to the PDB-HEADER record, which classifies the macromolecule.</p>
							<p><strong>compound</strong> Text found in a VPLG COMPND record, which describes the macromolecular components or the experiment or analysis of the macromolecule of the VPLG entry.</p>
							<p><strong>het</strong> Text found in the HET records, a 3 letter/number abbreviation for HET groups.</p>
							<p><strong>hetname</strong> Description of the HET group as found in the HET record.</p>
							<p><strong>pdb_id</strong> Text describing the unique PDB identifier as found in the PDB HEADER record and consisting of 4 alphanumeric characters.</p>
							<p><strong>molecule</strong> Text found in the VPLG MOLECULE records which describes the name/function of a certain protein chain.</p>
							<p><strong>scop</strong> Text found in the VPLG SCOP table records CLASS, FOLD, SUPERFAMILY, FAMILY, DOMAIN, SPECIES,
							which describe the different hierarchy levels, domain name or species name of a certain SCOP domain.</p>
							<p><strong>cath</strong> Text found in the VPLG CATH table records CLASS, TOPOLOGY, ARCHITECTURE, HOMOLOGOUS FAMILY, COMM, SOURCE
							which describe the different hierarchy levels, the species and some comments of a certain CATH domain.</p>
						<br>
						<br>
						<br>
						<br>
						<br>
						<br>
					</div><!-- end col-centered -->

					<div class="col-lg-4">
						<h3>Complex Queries</h3>
							<h4>Boolean Expressions</h3>
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

							<h4>Additional Information</h4>
							<p>The characters *, & and , are ignored if they are included as part of a keyword.</p>
							<p>It is necessary to use ( )'s to maintain the logic of a query that uses more than one "'ot' clause, or use the Refine Your Query option at the top of the Query Results page.</p>
							<p>Operators should only be used individually (for example, the syntax 'and not' should not be used).</p>
							<p>Search strings should not begin with the 'not' operator. </p>
					</div><!-- end col-centered -->
				</div><!-- end row -->



				<div class="row" id="ugSearchkms2">
					<div class="col-lg-4">
						<h3>Alpha Motifs</h3>
							<h4>Four Helix Bundle</h4>
							<p>The Four Helix Bundle is a Protein Motif which consists of 4 alpha helices which arrange in a bundle. 
							There are two types of the Four Helix Bundle which differ in the connections between the alpha helices. 
							The first type of the Four Helix Bundle is all antiparallel and the second type has two pairs of parallel helices 
							which have an antiparallel connection.</p>

							<h4>Globin Fold</h4>
							<p>The Globin Fold is an alpha helix structure motif which is composed of a bundle consisting 
							of eight alpha helices, which are connected over short loop regions. The helices have not a 
							fixed arrangement but the last two helices in sequential order make up an antiparallel pair.</p> 
					</div><!-- end col-centered -->

					<div class="col-lg-4 ugBorder">
						<h3>Beta Motifs</h3>
							<h4>Up and Down Barrel</h4>
							<p>The Up and Down Barrel is composed of a series of antiparallel beta strands which are 
							connected over hydrogen bonds. There are two major families of the Up and Down Barrel, 
							the ten-stranded and the eight-stranded version.</p>

							<h4>Immunoglobulin Fold</h4>
							<p>The Immunoglobulin Fold is a two-layer sandwich. It consists of usually seven 
							antiparallel beta-strands, which arrange in two beta sheets. The first is composed of 
							four and the second of three strands. Both are connected over a disulfide bond to build 
							the sandwich.</p>

							<h4>Beta Propeller</h4>
							<p>This beta motif contains between four and eight beta sheets, which are arrange around 
							the center of the protein. Four antiparallel beta strand form each sheet. One sheet makes 
							up one of the propeller blades. To build a four-bladed propeller four of these sheets 
							are grouped together.</p>

							<h4>Jelly Roll</h4>
							<p>The Jelly Roll motif has a barrel structure, which seems like a jelly roll. 
							The barrel includes eight beta strands, which build a two-layer sandwich, which each 
							hold four strands.</p>

					</div><!-- end col-centered -->

					<div class="col-lg-4">
						<h3>Alpha-Beta Motifs</h3>
							<h4>Ubiquitin Roll</h4>
							<p>The Ubiquitin Roll motif contains alpha helices and beta strands. Two different types 
							must be considerd. The first is composed of two beta-hairpin motifs and an enclosed 
							alpha helix and the second an alpha helix which is surrounded by two beta sheets.</p>

							<h4>Alpha-Beta Plaits</h4>
							<p>This motif has four of five beta strand, which form an antiparallel beta sheet. In-between 
							the sheet are two or more alpha helices.</p>

							<h4>Rossman Fold</h4>
							<p>The Rossman Fold folds in an open twisted parallel beta sheet, in which the strands lie 
							in-between alpha helices.</p>

							<h4>TIM Barrel</h4>
							<p>The TIM Barrel includes eight beta strand and eight alpha helices. The eight parallel 
							strands compose the inner part of the structure and the alpha helices lie around them to 
							make up the outer part of the motif.</p>

					</div><!-- end col-centered -->
				</div><!-- end row -->

			<div class="row" id="ugSearchkms3">
					<div class="col-lg-4">
						<h3>Queries</h3>
						<p>The simplest query is a single PDB-ID, such as:</p>
						<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>1kga</tt></p>
						<p>Two or more PDB-IDs can be elected by the following terms:</p>
						<p>using a list like</p>
						<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>1hkb, 1kba, 2cse, 4dfh</tt></p>
						<p>or like</p>
						<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<tt>1hkb; 1kba; 2cse; 4dfh; 1ard</tt></p>
						<p>The PDB-IDs must be separated by , or ;. There is no limit of PDB-IDs.</p>
					</div><!-- end col-centered -->

					<div class="col-lg-4 ugBorder">
						<h3>Attributes</h3>
						<p>The following is a list of <b>attributes</b> and their definitions:</p>
						<p><strong>pdb_id</strong> Text describing the unique PDB identifier as found in the PDB HEADER record and 
						consisting of 4 alphanumeric characters.</p>
						<br>
						<br>
						<br>
						<br>
						<br>
						<br>
						<br>
						<br>
						<br>
					</div><!-- end col-centered -->

					<div class="col-lg-4">
						<h3>Examples</h3>
						<p><b>1hkb,1hba,1cse,1ard</b> Find the four topologies <i>1hkb</i>, <i>1hba</i>, <i>1cse</i> and <i>1ard</i> in the VPLG-database</p>
						<p><b>1hkb;1hba;1cse;1ard</b> Find the four topologies <i>1hkb</i>, <i>1hba</i>, <i>1cse</i> and <i>1ard</i> in the VPLG-database></p>
					</div><!-- end col-centered -->
				</div><!-- end row -->

			<br>
			<br>
			<br>
			<p>For a complete documentation with examples of how to search fast and effectively please <a href="help.php">click here</a>.

		</div><!-- end container and text -->
	</div><!-- end UserGuide -->
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
			<!-- Custom JS -->
	<script src="js/script.js"></script>
	<script src="js/userguide.js" type="text/javascript"></script>
</body>
</html>
