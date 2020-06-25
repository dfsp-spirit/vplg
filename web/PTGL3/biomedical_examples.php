<!DOCTYPE html>
<?php 
	include('./backend/config.php'); 

	$SECTION_COUNTER = 0;  # used to increment the sections without having to do it yourself

	$title = "Examples";
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
					<a href="/guide.php"><i class="glyphicon glyphicon-arrow-up"></i> Step-by-step guide <i class="glyphicon glyphicon-arrow-up"></i></a><br>
					Previous section
				</p>
			</div>
			<br>

			<h2> Biomedical examples </h2>
			<h3> Table of contents </h3>
			<ol>
				<li>  <a href="#bioExamplesCPO">Finding structurally similar chloroperoxidases</a> </li>
				<li>  <a href="#bioExamplesBarrel">Up-and-down beta-barrel</a> </li>
				<li>  <a href="#bioExamplesCI">Topology of central subunits of respiratory complex I</a> </li>
			</ol>

			<a class="anchor" id="bioExamplesCPO"></a>
			<h3> <?php echo ++$SECTION_COUNTER ?>. Finding structurally similar chloroperoxidases </h3>

			<h4> Biomedical question </h4>

			<blockquote>
				"Haloperoxidases catalyze the halogenation of organic compounds in the presence of halide ions and peroxides such as H<sub>2</sub>O<sub>2</sub>.
				They are named after the most electronegative halide they are able to oxidize."
				[Hofmann et al., 1998, <i>J.Mol.B.</i>]
			</blockquote>

			<ol>
				<li> What is the structural topology of chloroperoxidase from <i>Pseudomonas fluorescens</i>? </li>
				<li> Which structures have a similar topology? </li>
				<li> With which protein classes are they associated? </li>
			</ol>

			<br>
			<h4> Analysis with PTGL </h4>

			<p>
				The chloroperoxidase from <i>Pseudomonas fluorescens</i> can be found in the <a href="https://www.rcsb.org/" target="_blank">Protein Data Bank</a> via the accession code 1a8s [Hofmann et al., 1998, J.Mol.B].
			</p>

			<center>
				<img src="/images/biomedical_examples/1a8s_cartoon.png" alt="Cartoon-style three-dimensional structure of chloroperoxidase from P. fluorescens" title="Cartoon-style three-dimensional structure of chloroperoxidase from P. fluorescens" class="img-responsive imgFormAboutphp2"/>

				<p>
					Cartoon-style three-dimensional structure of chloroperoxidase from <i>P. fluorescens</i> (1a8s). 
					The chains are colored grey, black and red for coils, strands and helices, respectively.
					Ligands are drawn as sticks and colored magenta.
				</p>
			</center>

			<ol>
				<li> <a href="guide.php#guidePgFg">Search for the Protein Graph</a> of chloroperoxidase from <i>Pseudomonas fluorescens</i> (1a8s): </li>
			</ol>

			<!-- 1a8sA albe PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/a8/1a8s/A/1a8s_A_albe_PG.png" alt="Alpha-Beta Protein Graph of 1a8sA" title="Alpha-Beta-Ligand Protein Graph of 1a8sA" class="img-responsive imgFormAboutphp2"/></p>');
			?>

			<p class="first-li-tab"> Switch to the beta topology: </p>

			<!-- 1a8sA beta PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/a8/1a8s/A/1a8s_A_beta_PG.png" alt="Beta Protein Graph of 1a8sA" title="Beta Protein Graph of 1a8sA" class="img-responsive imgFormAboutphp2"/></p>');
			?>

			<p class="first-li-tab"> 
				Go to the Folding Graph and retrieve the <a href="about.php#red">reduced</a> <a href="about.php#linearNot">linear notation</a> of the beta Folding Graph of 1a8sA:
			</p> 

			<!-- 1a8sA beta RED FG#0 linnot -->
			<p><center>
				Reduced <a href="about.php#linearNot">linear notation</a>: 
				<?php
					$url_linnot = "$SITE_BASE_URL" . '/api/index.php/linnot/1a8s/A/beta/0/red/json';
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

			<ol start="2">
				<li> Use the <a href="guide.php#guideLinnot">linear notation search</a> for the beta reduced <a href="about.php#linearNot">linear notation</a>. </li>
				<li> Browse the results and look for the field 'classification' for each protein. </li>
			</ol>


			<br>
			<h4> Answer </h4>

			<ol>
				<li>
					The protein exhibits an alpha beta class topology with strands and helices. The vertices of the strands are connected across one path of edges, and therefore the strands form a single sheet. The helices are not in spatial neighborhood and show no characteristic topology. The <a href="about.php#betaGraph">beta</a> <a href="about.php#proteinGraph">Protein Graph</a> shows the topology of the alpha-beta hydrolase fold [Homquist, 2000, Curr. Protein Pept. Sc.].
				</li>

				<li> The linear notation search yields 2,078 protein chains with this topology, e.g., chloroperoxidase T from <i>Kitasatospora aureofaciens</i> (1a7u), chloroperoxidase L from <i>Streptomyces lividans</i> (1a88), bromoperoxidase A1 from <i>Kitasatospora aureofaciens</i> (1a8q). </li>

				<li>
					The first 100 results show the following functional classifications that all exhibit the same beta topology: haloperoxidase, aminopeptidase, hydrolase, dehalogenase, immune system, hydrolase/hydrolase inhibitor, transferase, and serine hydrolase.
				</li>
			</ol>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<a class="anchor" id="bioExamplesBarrel"></a>
			<h3> <?php echo ++$SECTION_COUNTER ?>. Up-and-down beta-barrel </h3>

			<h4> Biomedical question </h4>

			<blockquote>
				„The up-and-down beta-barrel is a common folding motif found frequently in proteins that bind and transport 
				hydrophobic ligands. [...] Proteins belonging to this class of up-and-down beta-barrels are found typically to be 
				lipid-binding proteins in which the interior surface forms a cavity or pit that serves as the ligand binding region.“
				[LaLonde et al., 1994, <i>FASEB J.</i>]
			</blockquote>

			<ol>
				<li> Which PDB structures contain an up-and-down beta-barrel fold? </li>
				<li> Which of them contain a ligand in the interior of the beta-barrel? </li>
			</ol>

			<br>
			<h4> Analysis with PTGL </h4>

			<ol>
				<li> Use the <a href="guide.php#guideMotif">motif search</a> for the up-and-down beta-barrel. </li>
				<li> Open beta-ligand Protein Graphs of found chains, e.g., chain A from retinol-binding protein from <i>Sus scrofa domesticus</i> (1aqb).</li>
			</ol>

			<center>
				<img src="/images/biomedical_examples/1aqb_cartoon.png" alt="Cartoon-style three-dimensional structure of retinol-binding protein from Sus scrofa domesticus" title="Cartoon-style three-dimensional structure of retinol-binding protein from Sus scrofa domesticus" class="img-responsive imgFormAboutphp2"/>

				<p>
					Cartoon-style three-dimensional structure of retinol-binding protein from <i>Sus scrofa domesticus</i>.
					Ligands are drawn as sphere or sticks.
				</p>
			</center>

			<!-- 1aqbA beta PG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/aq/1aqb/A/1aqb_A_betalig_PG.png" alt="Beta-ligand Protein Graph of 1aqbA" title="Beta-ligand Protein Graph of 1aqbA" class="img-responsive imgFormAboutphp2"/></p>');
			?>

			<br>
			<h4> Answer </h4>

			<!-- count of chains exhibiting (up-and-down beta-)barrel -->
			<?php
				$url_linnot = "$SITE_BASE_URL" . '/api/index.php/pdbchains_containing_motif/barrel/json';
				$curl = curl_init();
				curl_setopt($curl, CURLOPT_URL, $url_linnot);
				curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
				curl_setopt($curl, CURLOPT_FAILONERROR, 1);

				$result = curl_exec($curl);

				

				if(curl_errno($curl)) {
				    // handle error
				    $chains_barrel_count = "ERROR";
				    echo "ERROR: Please inform the site administrator about this error with message '" . curl_error($curl) . "'";
				} else {
					$chains_barrel_count = substr_count($result, ",") + 1;  # count in json list comma and add one results in number of chains exhibiting motif
				}
			?>

			<ol>
				<li>
					The motif search yields <?php echo $chains_barrel_count; ?> protein chains, e.g., odorant binding protein from nasal mucosa from <i>Sus scrofa</i> (1a3y), retinol-binding protein from <i>Sus scrofa domesticus</i> (1aqb), egg-white avidin from <i>Gallus gallus</i> (1avd), egg-white apo-avidin from <i>Gallus gallus</i> 1ave, beta-lactoglobulin from <i>Bos taurus</i> (1b8e).
				</li>

				<li>
					The up-and-down beta-barrels motif can be seen in the <a href="about.php#proteinGraph">Protein Graphs</a> by numerous strands exhibiting a cycle of antiparallel edges.
					A ligand lies within the center of the barrel if it is connected with multiple up to all strands of a barrel, e.g., 1aqbA, 1brpA, 1bsoA, 1bxwA.
				</li>
			</ol>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<a class="anchor" id="bioExamplesCI"></a>
			<h3> <?php echo ++$SECTION_COUNTER ?>. Topology of central subunits of respiratory complex I </h3>

			<h4> Biomedical question </h4>

			<blockquote>
				„With a molecular mass of about 1 MDa, mitochondrial complex I is the largest multi-subunit complex of the respiratory chain. 
				Complex I couples electron transfer from NADH to ubiquinone with transmembrane proton pumping contributing to the proton motive force used for ATP synthesis. [...] 
				Complex I contributes significantly to the formation of mitochondrial reactive oxygen species (ROS) and is implicated in the pathogenesis of numerous hereditary and degenerative disorders [...]
				Complex I comprises 14 central subunits that execute the core bioenergetic functions and that are conserved from bacteria to humans. [...]
				The mass of the mitochondrial enzyme is almost double compared to its bacterial counterpart, because it comprises some 30 accessory subunits.“
				[Wirth et al., 2016, <i>BBA. Bioenergetics</i>]
			</blockquote>

			<ol>
				<li> What is the topology of the conserved core of human's respiratory complex I? </li>
				<li> Is the topology of the central subunits conserved across complex I of <i>Thermus thermophilus</i>?
			</ol>

			<br>
			<h4> Analysis with PTGL </h4>

			<center>
				<img src="/images/biomedical_examples/5xth_CA-trace.png" alt="C-alpha trace of three-dimensional structure of respiratory supercomplex I1III2IV1 from H. sapiens (5xth)" title="C-alpha trace of three-dimensional structure of respiratory supercomplex I1III2IV1 from H. sapiens (5xth)" class="img-responsive imgFormAboutphp2"/>

				<p>
					C-alpha trace of three-dimensional structure of respiratory supercomplex I1III2IV1 from <i>H. sapiens</i> (5xth). Chains are colored individually.
				</p>
			</center>

			<br>
			<ol>
				<li> Use the <a href="guide.php#guideCg">Complex Graph search</a> for human respiratory supercomplex I1III2IV1 (5xth). </li>
			</ol>

			<!-- 5xth CG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/xt/5xth/ALL/5xth_complex_chains_CG.png" alt="Complex Graph of 5xth" title="Complex Graph of 5xth" class="img-responsive imgFormAboutphp"/></p>');
			?>

			<ol start="2">
				<li> Use the <a href="guide.php#guideCg">Complex Graph search</a> for complex I of <i>T. thermophilus</i> (4hea). </li>
			</ol>

			<center>
				<img src="/images/biomedical_examples/4hea_CA-trace.png" alt="C-alpha trace of three-dimensional structure of two copies of respiratory complex I from T. thermophilus (4hea)" title="C-alpha trace of three-dimensional structure of two copies of respiratory complex I from T. thermophilus (4hea)" class="img-responsive imgFormAboutphp2"/>

				<p>
					C-alpha trace of three-dimensional structure of two copies of respiratory complex I from <i>T. thermophilus</i> (4hea). Chains are colored individually.
				</p>
			</center>

			<!-- 4hea CG -->
			<?php 
				echo('<p class="imgCenter"><img src="'.$IMG_ROOT_PATH.'/he/4hea/ALL/4hea_complex_chains_CG.png" alt="Complex Graph of 4hea" title="Complex Graph of 4hea" class="img-responsive imgFormAboutphp"/></p>');
			?>

			<br>
			<center>
			<p>
				Central subunits of respiratory complex I and their chain names for <i>H. sapiens</i> and <i>T. thermophilus</i>. Subunits of the same row are homologous. [Wirth et al., 2016, <i>BBA. Bioenergetics</i>]
			</p>

			<!-- table created with https://www.tablesgenerator.com/html_tables -->
			<style type="text/css">
			.tg  {border:none;border-collapse:collapse;border-color:#ccc;border-spacing:0;}
			.tg td{background-color:#fff;border-color:#ccc;border-style:solid;border-width:0px;color:#333;
			  font-family:Arial, sans-serif;font-size:14px;overflow:hidden;padding:10px 5px;word-break:normal;}
			.tg th{background-color:#f0f0f0;border-color:#ccc;border-style:solid;border-width:0px;color:#333;
			  font-family:Arial, sans-serif;font-size:14px;font-weight:normal;overflow:hidden;padding:10px 5px;word-break:normal;}
			.tg .tg-buh4{background-color:#f9f9f9;text-align:left;vertical-align:top}
			.tg .tg-0lax{text-align:left;vertical-align:top}
			</style>
			<table class="tg">
			<thead>
			  <tr>
			    <th class="tg-0lax" colspan="2"><b><i>Homo sapiens</i></b> (5xth)</th>
			    <th class="tg-0lax" colspan="2"><b><i>Thermus thermophilus</i></b> (4hea)</th>
			  </tr>
			</thead>
			<tbody>
			  <tr>
			    <td class="tg-buh4"><b>Subunit</b></td>
			    <td class="tg-buh4"><b>Chain ID</b></td>
			    <td class="tg-buh4"><b>Subunit</b></td>
			    <td class="tg-buh4"><b>Chain ID</b></td>
			  </tr>
			  <tr>
			    <td class="tg-0lax">NDUFS1</td>
			    <td class="tg-0lax">M</td>
			    <td class="tg-0lax">Nqo3</td>
			    <td class="tg-0lax">3</td>
			  </tr>
			  <tr>
			    <td class="tg-buh4">NDUFV1</td>
			    <td class="tg-buh4">A</td>
			    <td class="tg-buh4">Nqo1</td>
			    <td class="tg-buh4">1</td>
			  </tr>
			  <tr>
			    <td class="tg-0lax">NDUFS2</td>
			    <td class="tg-0lax">J</td>
			    <td class="tg-0lax">Nqo4</td>
			    <td class="tg-0lax">4</td>
			  </tr>
			  <tr>
			    <td class="tg-buh4">NDUFS3</td>
			    <td class="tg-buh4">P</td>
			    <td class="tg-buh4">Nqo5</td>
			    <td class="tg-buh4">5</td>
			  </tr>
			  <tr>
			    <td class="tg-0lax">NDUFV2</td>
			    <td class="tg-0lax">O<br></td>
			    <td class="tg-0lax">Nqo2</td>
			    <td class="tg-0lax">2</td>
			  </tr>
			  <tr>
			    <td class="tg-buh4">NDUFS8</td>
			    <td class="tg-buh4">B</td>
			    <td class="tg-buh4">Nqo9</td>
			    <td class="tg-buh4">9</td>
			  </tr>
			  <tr>
			    <td class="tg-0lax">NDUFS7</td>
			    <td class="tg-0lax">C</td>
			    <td class="tg-0lax">Nqo6</td>
			    <td class="tg-0lax">6</td>
			  </tr>
			  <tr>
			    <td class="tg-buh4">NU1M (ND1)</td>
			    <td class="tg-buh4">s</td>
			    <td class="tg-buh4">Nqo8</td>
			    <td class="tg-buh4">H</td>
			  </tr>
			  <tr>
			    <td class="tg-0lax">NU2M (ND2)</td>
			    <td class="tg-0lax">i</td>
			    <td class="tg-0lax">Nqo14</td>
			    <td class="tg-0lax">N</td>
			  </tr>
			  <tr>
			    <td class="tg-buh4">NU3M (ND3)</td>
			    <td class="tg-buh4">j</td>
			    <td class="tg-buh4">Nqo7</td>
			    <td class="tg-buh4">A</td>
			  </tr>
			  <tr>
			    <td class="tg-0lax">NU4M (ND4)</td>
			    <td class="tg-0lax">r</td>
			    <td class="tg-0lax">Nqo13</td>
			    <td class="tg-0lax">M</td>
			  </tr>
			  <tr>
			    <td class="tg-buh4">NU5M (ND5)</td>
			    <td class="tg-buh4">l</td>
			    <td class="tg-buh4">Nqo12</td>
			    <td class="tg-buh4">L</td>
			  </tr>
			  <tr>
			    <td class="tg-0lax">NU6M (ND6)</td>
			    <td class="tg-0lax">m</td>
			    <td class="tg-0lax">Nqo10</td>
			    <td class="tg-0lax">J</td>
			  </tr>
			  <tr>
			    <td class="tg-buh4">NULM (ND4L)</td>
			    <td class="tg-buh4">k</td>
			    <td class="tg-buh4">Nqo11</td>
			    <td class="tg-buh4">K</td>
			  </tr>
			</tbody>
			</table>
			</center>

			<br>
			<img src="/images/biomedical_examples/5xth_4hea_central-chains_CG.png" alt="Complex Graphs of central chains of respiratory complex I from H. sapiens (5xth, left) and T. thermophilus (4hea, right) rearranged and colored homologous chains identically" title="Complex Graphs of central chains of respiratory complex I from H. sapiens (5xth, left) and T. thermophilus (4hea, right) rearranged and colored homologous chains identically" class="img-responsive imgFormAboutphp"/>
			<p>
				<center>
					Complex Graphs of central chains of respiratory complex I from <i>H. sapiens</i> (5xth, left) and <i>T. thermophilus</i> (4hea, right), rearranged, and colored homologous chains identically.
				</center>
			</p>

			<br>
			<h4> Answer </h4>

			<ol>
				<li>
					NDUFS2 (J) is a key subunit in contact with seven other subunits: NDUFV1 (A), NU6M (m), NU1M (s), NDUFS7 (C), NDUFS8 (B), NDUFS1 (M), NDUFS3 (P).
					NDUFV1 (A) and NDUFV2 (O) are peripheral, only connected with each other and with NDUFS1 (M).
					NU4M (r) is also peripheral as it is only in contact with NU2M (i) and NU5M (l).
					Other subunits are more central with numbers of contacts between three and five.
				</li>

				<li>
					The topology of the central subunits is fully conserved with one exception.
					In <i>H. sapiens</i>, there is a contact between NU6M (m) and NDUFS2 (J) while in <i>T. thermophilus</i>, there is a contact between the homologue of NU6M, Nqo10 (J), and Nqo12 (L).
				</li>
			</ol>


			<div class="topLink"><a href="#" class="topLink"><i class="fa fa-2x fa-long-arrow-up"></i></a></div>


			<br>
			<div class="sectionPointerBox">
				<p>
					Next section<br>
					<a href="/help.php"><i class="glyphicon glyphicon-arrow-down"></i> Help <i class="glyphicon glyphicon-arrow-down"></i></a>
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