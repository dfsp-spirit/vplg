<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);


// Define variables

$db_config = include('config.php');     //TODO: Sichern?

// Define functions

function getGraphbyID($pdbID){
	# TODO
}

/*

// try to get _GET
if(isset($_GET)) {
    if(isset($_GET["pcs"])) {$pcs = $_GET["pcs"];};
}

echo "<br /><br /><br /><br /><br />Selected Chains: " . $pcs . "<br />";

$chains = explode(" ", trim($pcs));  #Clip whitespaces and seperate PDB IDs at whitespaces -> Array
var_dump($chains);

foreach ($chains as $value ){
	if (!(strlen($value) == 4) || (!(is_numeric($value[0])))) {
		echo "<br />'" . $value . "' has a wrong PDB-ID format\n<br />";
	}
}


*/
$svg1 = file_get_contents("./proteins/7tim/7tim_A_albe_PG.svg");

$tableString = '		<div id="myCarousel">
					<ul class="bxslider bx-prev bx-next" id="carouselSlider">';
 
/*$tableString .= '		<li>
							<img src="./proteins/7tim/7tim_A_albe_PG.png"></img>
						</li>';
 */
 $tableString .= ' <li>
						<div class="container">
						<h3>1a0c</h3>
						<h4>Protein graph 1a0cA</h4>
						
						<div class="proteingraph">
							<img src="proteins/7tim/7tim_A_albe_PG.png" alt=""/>
						
						</div>
						
						<div class="table-responsive" id="sse">
							<table class="table table-condensed table-hover borderless">
								<tr>
									<th class="tablecenter">Nr.</th>
									<th class="tablecenter">Type</th>
									<th class="tablecenter">Sequence</th>
									<th class="tablecenter">from - to</th>
								</tr>
								<tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr>
								
							</table>
						</div><!-- end table-responsive -->
						
						<p>Select linear notation</p>
						
						
						
						<ul class="bxslider" id="tada">
						  <li><img src="/test.png" /></li>
						  <li><img src="/test.png" /></li>
						  <li><img src="/test.png" /></li>
						</ul>

						
						<div id="bx-pager">
						  <a data-slide-index="0" href=""><img src="/thumbtest.png" /></a>
						  <a data-slide-index="1" href=""><img src="/thumbtest.png" /></a>
						  <a data-slide-index="2" href=""><img src="/thumbtest.png" /></a>
						</div>
						
						
						
							
						</div>
					</li>';
					
					 $tableString .= ' <li>
						<div class="container">
					
						<h4>Alpha1 Motifs</h4>
						<p>Four Helix Bundle (<a href="help.php#helpAlphaMotifs">?</a>) - Globin Foldkasdfjööööööööööööööööööööööööööööööööööölskdjflsdkafjsdöalkfjsdölfkjsdfklsdjflösdkjfsdlkfjsdklfjsdlfkjsdlfsdjfllsdfjsdlfkjsdlfjksdlkfjsdlfkjsdlkfjslödfkjsdlfköjsdklöfjsdklfjsdklfjskdlafjsladkfjasdlkfjsdlkfjsdalkfjsladkfjsladkfjsdlakfjlsdkfjlasdkjflsdkajfasdlkfjlasdfjk (<a href="help.php#helpAlphaMotifs">?</a>)</p>
						
					</div>
					</li>';
					
					 $tableString .= ' <li>
						<div class="container">
					
						<h4>Alpha2 Motifs</h4>
						<p>Four Helix Bundle (<a href="help.php#helpAlphaMotifs">?</a>) - Globin Fold (<a href="help.php#helpAlphaMotifs">?</a>)</p>
						
					</div>
					</li>';
					
					 $tableString .= ' <li>
						<div class="container">
					
						<h4>Alpha3 Motifs</h4>
						<p>Four Helix Bundle (<a href="help.php#helpAlphaMotifs">?</a>) - Globin Fold (<a href="help.php#helpAlphaMotifs">?</a>)</p>
						
					</div>
					</li>';
					
					 $tableString .= ' <li>
						<div class="container">
					
						<h4>Alpha4 Motifs</h4>
						<p>Four Helix Bundle (<a href="help.php#helpAlphaMotifs">?</a>) - Globin Fold (<a href="help.php#helpAlphaMotifs">?</a>)</p>
						
					</div>
					</li>';
					
					 $tableString .= ' <li>
						<div class="container">
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
						
					</div>
					</li>';
 
 
 /*
 
 
 $tableString .= '		<li>
							<img src="./proteins/1gos/1gos_A_albe_PG.png"></img>
						</li>';*/
 
 $tableString .= '	</ul>
				</div>';
/*
	
	// establish pgsql connection
	$conn_string = "host=" . $db_config['host'] . " port=" . $db_config['port'] . " dbname=" . $db_config['db'] . " user=" . $db_config['user'] ." password=" . $db_config['pw'];
	$db = pg_connect($conn_string)
					or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            
	$query = "SELECT * FROM plcc_protein WHERE pdb_id LIKE '%".$keyword."%' OR header LIKE '%".strtoupper($keyword)."%'";
	$result = pg_query($db, $query) 
					  or die($query . ' -> Query failed: ' . pg_last_error());

	$counter = 0;
	$tableString = "";
	while (($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)) && ($counter <= 30)){
		if ($counter % 2 == 0){     // performs alternating orange/white tables
			$class = "Orange";
		} else {
			$class = "White";
		}
		// var_dump($arr);
		$tableString .=	 '<div class="results results'.$class.'">					
						<div class="resultsHeader resultsHeader'.$class.'">
							<div class="resultsId">'.$arr["pdb_id"].'</div>
							<div class="resultsRes">Resolution: '.$arr["resolution"].' &Aring;</div>
							<div class="resultsLink"><a href="http://www.rcsb.org/pdb/explore/explore.do?structureId='.$arr["pdb_id"].'">[PDB]</a>
												<a href="">[PDBSum]</a>
												<a href="">[FASTA]</a></div>
						</div>
						<div class="resultsBody1">
							<div class="resultsTitle">Title</div>
							<div class="resultsTitlePDB">' .$arr["title"]. '</div>
						</div>
						<div class="resultsBody2">
							<div class="resultsClass">Classification</div>
							<div class="resultsClassPDB">'.$arr["header"].'</div>
						</div>
						<div class="resultsBody3">
							<div class="resultsEC">EC#	</div>
							<div class="resultsECNum">#####</div>
						</div>
						<div class="resultsFooter">
							<div class="resultsChain">Chain</div>
							<div class="resultsChainNum"><input type=checkbox id="'.$arr["pdb_id"].'" class="chainCheckBox" name="1" value="'.$arr["pdb_id"].'"/>'.$arr["pdb_id"].'A</div>
							<div class="resultsSCOP">Scop ####</div>
							<div class="resultsCATH">CATH ####</div>
						</div>
					</div>';

		echo $counter;
		$counter++;
	}

	pg_free_result($result); // clean memory
	pg_close($db); // close connection
}
*/
?>