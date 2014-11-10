<?php session_start(); ?>
<!DOCTYPE html>
<?php 
include('./backend/config.php');
include('./backend/get_foldinggraphs.php');

$title = "Folding graphs";
$title = $SITE_TITLE.$TITLE_SPACER.$title;
?>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="PTGL folding graphs">
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

	<div class="container" id="publications">
		<h2>The folding graph visualizations</h2>
		<br>
		
		<div id="PageIntro">
		<div class="container" id="pageintro">
		A folding graph is a <dfn title="A set of vertices in a graph where each vertex is reachable from all other vertices.">connected component</dfn> of a protein graph. Here you can search for all visualizations of the different folding graph types of a protein chain. For each of the folding graph types (e.g., the alpha graph, which only considers alpha helices),
		there are four different visualizations available:<br><br>
		<ul>
		<li>the ADJ notation: all SSEs of the protein graph are shown, order is from N-terminus (left) to C-terminus, and each edge represents a 3D contact between a pair of SSEs.</li>
		<li>the RED notation: only SSEs of the folding graph are shown, order is from N-terminus (left) to C-terminus, and each edge represents a 3D contact between a pair of SSEs.</li>
		<li>the SEQ notation: all SSEs of the protein graph are shown, order is from N-terminus (left) to C-terminus, and each edge represents the sequential order in the folding graph.</li>
		<li>the KEY notation: only SSEs of the folding graph are shown, order is spatial, and each edge represents a 3D contact between a pair of SSEs. Note that it is not possible to define a spatial ordering for <dfn title="A graph is bifurcated if any vertex in the graph has more than 2 neighbors.">bifurcated graphs</dfn>, so such graphs do not have a KEY notation.</li>
		</ul>
		Select the chain, folding graph type and visualization you are interested in below. You will then be able to browse all folding graphs of the selected protein graph in the respective visualization.
		
		</div><!-- end container-->
		</div><!-- end Home -->
		
		<form class="form-inline" action="foldinggraphs.php" method="get">
			
		<label>Enter PDB identifier and chain, e.g., '7timA':
		<?php
		if(isset($_GET['pdbchain']) && $_GET['pdbchain'] != "") {
		  echo '<input type="text" class="form-control" name="pdbchain" id="searchInput" placeholder="Enter PDB ID and chain" value="' . $_GET['pdbchain'] . '">';
		}
		else {
		  echo '<input type="text" class="form-control" name="pdbchain" id="searchInput" placeholder="Enter PDB ID and chain">';
		}
		?>
		</label>
			
		
		<label>Select graph-type: 
		  <select id="multidown" name="graphtype_int">
		  <?php
		     $values = array(1, 2, 3, 4, 5, 6);
		     $labels = array("Alpha", "Beta", "Alpha-Beta", "Alpha-Ligand", "Beta-Ligand", "Alpha-Beta-Ligand");
		     
		     $pre_sel = 1;
		     if(isset($_GET['graphtype_int'])) {
		       $tmp = intval($_GET['graphtype_int']);
		       if($tmp >= 1 && tmp <= 6) {
		         $pre_sel = $tmp;
		       }
		     }
		     
		     for($i = 0; $i < count($values); $i++) {
		       $sel = "";
		       if($pre_sel === $values[$i]) {
		         $sel = " selected='selected' ";
		       }
		       echo "<option class='downloadOption' $sel value='" . $values[$i] . "'>"  . $labels[$i] . "</option>";
		     }
		  ?>			
		  </select>
		</label>
		
		<label> and notation-type: 
		  <select id="multidown" name="notationtype">
		  <?php
		     $values = array("adj", "red", "seq", "key");
		     $labels = array("ADJ", "RED", "SEQ", "KEY");
		     
		     $pre_sel = "adj";
		     if(isset($_GET['notationtype'])) {
		       $tmp = $_GET['notationtype'];
		       if($tmp === "adj" || $tmp === "red" || $tmp === "seq" || $tmp === "key") {
		         $pre_sel = $tmp;
		       }
		     }
		     
		     for($i = 0; $i < count($values); $i++) {
		       $sel = "";
		       if($pre_sel === $values[$i]) {
		         $sel = " selected='selected' ";
		       }
		       echo "<option class='downloadOption' $sel value='" . $values[$i] . "'>"  . $labels[$i] . "</option>";
		     }
		  ?>		  
		  </select>
		</label>
		
		<button type="submit" id="sendit" "class="btn btn-default">Search <span class="glyphicon glyphicon-search"></span></button><br>

		</form>	
		
		

		
		
		<div class="container" id="searchResults">
			<h3> Search Results </h3>
			<?php echo $tableString; /* The table string is constructed in /backend/get_foldinggraphs.php, which is included by this file. */  
			
			      echo "<br><h3> Folding graph images </h3><br><p>The images below show the folding graphs (connected components) of the protein graph. The folding graphs and their linear notations are used to power the motif search and other features of this database server. Note that folding graphs of size 1 (isolated vertices in the graph) are not listed here -- they would not be of any use. In the images, the following abbreviations are used: PG = protein graph, FG = folding graph, SQ = sequential in chain.</p>\n";
			      echo $img_string;
			
			?>
		</div><!-- end container and searchResults -->

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
