<!DOCTYPE html>
<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);

include('./backend/config.php'); 

$title = "The Protein Topology Graph Library";
$title = $SITE_TITLE.$TITLE_SPACER.$title;

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
			The Visualization of Protein-Ligand Graphs (VPLG) web server provides a database of protein secondary structure topologies. It is based on protein ligand graphs computed
			by the <a href="http://www.bioinformatik.uni-frankfurt.de/tools/vplg/" target="_blank">VPLG software</a>. VPLG uses a graph-based model to describe the structure of proteins on the super-secondary structure level.
			
			<!--
			A protein ligand graph is
			computed from the atomic coordinates in a PDB file and the secondary structure assignments of the DSSP algorithm. In this graph, vertices
			represent secondary structure elements (SSEs, usually alpha helices and beta strands) or ligand molecules while the edges model contacts and 
			relative orientations between the SSEs.
			-->
			<br /><br />
			
			<p class="imgCenter"><img src="./images/ptgl_overview_trans.png" width="600"></p>
			
			<br /><br />
			This web server allows you to search for protein motifs which can be detected in the graphs. It also implements graph-based similarity measures to search for protein chains which are similar to a given query protein.
			
		</div><!-- end container-->
	</div><!-- end Home -->




		<div class="row" id="search">
		<div class="col-lg-4"></div>
			<div class="col-lg-4">
				<div class="input-group form-group">
					<form class="form-inline" action="search.php" method="get">
						<input type="text" class="form-control" name="keyword" id="searchInput" autocomplete="off" placeholder="Enter PDB ID or keyword...">
						<button type="submit" id="sendit" name="st" value="keyword" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button><br>
						<div id="advancedButton"> Advanced Search <div id="arrow"><strong class="caret"></strong></div></div>
						<div id="liveSearchResults"></div>

						<?php if($ENABLE_COMPLEX_GRAPHS) { ?>
						<label class="checkboxFont">
							<input type="checkbox" id="inlineCheckbox3" name="proteincomplexes" value="1"> Search for Protein Complexes
						</label>
						<?php } ?>
					</form>							
						
					<form class="form-inline" action="search.php" method="get">
						<div id="advancedSearch">

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
								<input class="advancedInput" type="text" id="ligandname" name="ligandname" placeholder="Ligand Name" size="20" maxlength="50"/>
								<i title="Search for proteins which contain a special ligand. Use the 3-letter ligand codes from RCSB Ligand Expo, e.g., 'FAD'. You can also use parts of the ligands long name." style="position:absolute; right:50px;"  class="fa fa-question"></i>
							</label>	
							<label class="advancedLabel">Molecule
								<input class="advancedInput" type="text" id="molecule" name="molecule" placeholder="Molecule" size="20" maxlength="50"/>
								<i title="Search for chains which appear in certain molecules e.g. 'MYOSINE'" style="position:absolute; right:50px;"  class="fa fa-question"></i>
							</label>		
							<button type="submit" name="st" value="advanced" class="btn btn-default advancedInput" style="margin-top:35px;"><span>Search</span></button>
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
				<dt>SearchMotifs</dt>
				<dd>Search for topological protein structure motifs <strong class="caret" id="flipArrow2" ></strong></dd>
				</div>
				<div id="addSearchMotif">
				<br>
					<h4>Alpha Motifs</h4>
					<div class="motifimagecontainer">
					   <div class="motiftext">Four Helix Bundle (<a href="motif_overview.php#4helix">?</a>)</div>
					   <a href="search.php?motif=4helix"><img class="motifimage" src="./images/4helixbeide_struktur.jpg" /></a></div>
					<div class="motifimagecontainer">
					   <div class="motiftext">Globin Fold (<a href="motif_overview.php#globin">?</a>)</div>
					   <a href="search.php?motif=globin"><img class="motifimage" src="./images/globin_struktur.jpg" /></a></div>


					<h4>Beta Motifs</h4>
					<div class="motifimagecontainer">
					   <div class="motiftext">Up and Down Barrel (<a href="motif_overview.php#barrel">?</a>)</div>
					   <a href="search.php?motif=barrel"><img class="motifimage" src="./images/barrel_struktur.jpg" /></a></div>	
					<div class="motifimagecontainer">
					   <div class="motiftext">Immunoglobulin Fold (<a href="motif_overview.php#immuno">?</a>)</div>
					   <a href="search.php?motif=immuno"><img class="motifimage" src="./images/immuno_struktur.jpg" /></a></div>
					<div class="motifimagecontainer">
					   <div class="motiftext">Beta Propeller (<a href="motif_overview.php#propeller">?</a>)</div>
					   <a href="search.php?motif=propeller"><img class="motifimage" src="./images/propeller_struktur.jpg" /></a></div>	
					<div class="motifimagecontainer">
					   <div class="motiftext">Jelly Roll (<a href="motif_overview.php#jelly">?</a>)</div>
					   <a href="search.php?motif=jelly"><img class="motifimage" src="./images/jelly_struktur.jpg" /></a></div>		


					<h4>Alpha Beta Motifs</h4>
					<div class="motifimagecontainer">
					   <div class="motiftext">Ubiquitin Roll (<a href="motif_overview.php#ubi">?</a>)</div>
					   <a href="search.php?motif=ubi"><img class="motifimage" src="./images/ubibeide_struktur.jpg" /></a></div>	
					<div class="motifimagecontainer">
					   <div class="motiftext">Alpha-Beta Plait (<a href="motif_overview.php#plait">?</a>)</div>
					   <a href="search.php?motif=plait"><img class="motifimage" src="./images/plait_struktur.jpg" /></a></div>
					<div class="motifimagecontainer">
					   <div class="motiftext">Rossman Fold (<a href="motif_overview.php#rossman">?</a>)</div>
					   <a href="search.php?motif=rossman"><img class="motifimage" src="./images/rossman_struktur.jpg" /></a></div>	
					<div class="motifimagecontainer">
					   <div class="motiftext">TIM Barrel (<a href="motif_overview.php#tim">?</a>)</div>
					   <a href="search.php?motif=tim"><img class="motifimage" src="./images/tim_struktur.jpg" /></a></div>	

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
				<?php echo "$SITE_TITLE"; ?> is a web-based database application for the analysis protein topologies. It uses a graph-based model to describe the structure
		of protein chains on the super-secondary structure level. A protein graph is computed from the 3D atomic coordinates of a single chain in
		a PDB file and the secondary structure assignments of the DSSP algorithm. In a protein graph graph, vertices represent secondary
		structure elements (SSEs, usually alpha helices and beta strands) or ligand molecules while the edges model contacts and relative orientations between
		them. 
				<a href="about.php">Read more about <?php echo "$SITE_TITLE"; ?>...</a>


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
			
			<div id="PageIntro">
		<div class="container" id="pageintro">
		This user guide explains how to use the different search options. You can click the text boxes to get more detailed information.
		<br><br>
		If you need want to learn about protein graphs, are interested in understanding how this web server works, or need help interpreting the results, see <a href="about.php">the About page</a>.
		</div><!-- end container-->
		</div><!-- end Home -->


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
							<p><strong>PDB identifier</strong> The 4 character unique indentifier for the structure as defined by the PDB. Begins with a number 1-9 followed by 3 alphanumeric characters. </p>
							<p><strong>Title</strong> The TITLE record contains a title for the experiment or analysis that is represented in the entry. It should identify an entry in the PDB in the same way that a title identifies a paper.The PDB records COMPND, SOURCE, EXPDTA, and REMARKs provide information that may also be found in TITLE. You may think of the title as describing the experiment, and the compound record as describing the molecule(s).</p>
							<!--<p id="scroll2"><strong>Classification</strong> A classification for the molecule. </p>
							<p><strong>HET</strong> The heterogen section of a PDB file contains the complete description of non-standard residues in the entry.</p>
							<p><strong>HETNAME</strong> This record gives the chemical name of the compound with the given HET</p>
							<p><strong>SCOP</strong> SCOP classification.</p>
							<p><strong>SCOPID</strong> SCOP identifier.</p>
							<p><strong>CATH</strong> CATH classification.</p>
							<p><strong>CATHID</strong> CATH domain identifier.</p>
							<p><strong>EC</strong> ENZYME number.</p>
							<p><strong>Molecule</strong> Name of the molecule.</p>
							<p><strong>Graphs</strong> A string standing for a Linear Notation of a certain Folding Graph.</p>
							-->
							<p><strong>Has ligand</strong> Choose whether the protein has a ligand or not.</p>
							<p><strong>Ligand name</strong> Search for proteins which contain a special ligand. Use the 3-letter ligand codes from RCSB Ligand Expo, e.g., 'FAD'. You can also use parts of the ligands long name.</p>
							<p><strong>Molecule</strong> Search the PDB MOLECULE record for chain names, e.g., 'MYOSINE'.</p>
																													
							
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
							</div>

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
					</div>
<!--

					<div class="col-lg-4" id="ugSearchs">
						<h3>Search Sequence</h3>
							<p>Search PTGL with your protein sequence using <a href="http://www.ncbi.nlm.nih.gov/BLAST/blastcgihelp.shtml" target="_blank">BLASTP</a>.</p>
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
					</div>
-->

				</div>


<!--

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
							fields are searched for the string kinase, not the complete PTGL database. 
							The text string following an attribute can be a phrase containing multiple words.</p>


					</div>

					<div class="col-lg-4 ugBorder">
						<h3>Attributes</h3>
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
						<br>
						<br>
						<br>
						<br>
					</div>

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
					</div>
				</div>

-->

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
					</div>

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

					</div>

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

					</div>
				</div>


<!--

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
					</div>

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
					</div>

					<div class="col-lg-4">
						<h3>Examples</h3>
						<p><b>1hkb,1hba,1cse,1ard</b> Find the four topologies <i>1hkb</i>, <i>1hba</i>, <i>1cse</i> and <i>1ard</i> in the PTGL-database</p>
						<p><b>1hkb;1hba;1cse;1ard</b> Find the four topologies <i>1hkb</i>, <i>1hba</i>, <i>1cse</i> and <i>1ard</i> in the PTGL-database></p>
					</div>
				</div>

			<br>
			<br>
			<br>
-->
			
			<!-- <p>For a complete documentation with examples of how to search fast and effectively please <a href="help.php">click here</a>. -->

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
