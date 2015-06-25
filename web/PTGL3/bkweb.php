<?php session_start(); ?>
<!DOCTYPE html>
<?php 
include('./backend/config.php');
$SHOW_ERROR_LIST = array();


$title = "BKweb -- Protein graph comparison";
$title = $SITE_TITLE.$TITLE_SPACER.$title;
?>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="PTGL folding graphs">
	<meta name="author" content="">
	<meta http-equiv="Cache-control" content="public">
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
		<h2>Protein ligand graph comparison</h2>
		<br>
		
		<div id="PageIntro">
		<div class="container" id="pageintro">
		This service allows you to find maximum common substructures in two graphs G1 and G2. It is based on searching cliques in the compatibility graph GC of G1 and G2.
		
		Enter the PDB and chain of the protein ligand graphs you want to compare below.
		
		</div><!-- end container-->
		</div><!-- end Home -->
		
		<form class="form-inline" action="bkweb.php" method="get">
			
		<label>Enter PDB identifier and chain of the first graph, e.g., '7timA':
		<?php
		if(isset($_GET['first_pdbchain']) && $_GET['first_pdbchain'] != "") {
		  echo '<input type="text" class="form-control" name="first_pdbchain" maxlength="5" id="first_pdbchain" placeholder="Enter PDB ID and chain" value="' . $_GET['first_pdbchain'] . '">';
		}
		else {
		  echo '<input type="text" class="form-control" name="first_pdbchain" maxlength="5" id="first_pdbchain" placeholder="Enter PDB ID and chain">';
		}
		?>
		</label>
		
		<label>Select graph-type to use for first graph: 
		  <select id="first_graphtype_int" name="first_graphtype_int">
		  <?php
		     $values = array(1, 2, 3, 4, 5, 6);
		     $labels = array("Alpha", "Beta", "Alpha-Beta", "Alpha-Ligand", "Beta-Ligand", "Alpha-Beta-Ligand");
		     
		     $pre_sel = 3;
		     if(isset($_GET['first_graphtype_int'])) {
		       $tmp = intval($_GET['first_graphtype_int']);
		       if($tmp >= 1 && $tmp <= 6) {
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
		
		<br><br>
		
		<label>Enter PDB identifier and chain of the second graph, e.g., '7timB':
		<?php
		if(isset($_GET['second_pdbchain']) && $_GET['second_pdbchain'] != "") {
		  echo '<input type="text" class="form-control" name="second_pdbchain" maxlength="5" id="second_pdbchain" placeholder="Enter PDB ID and chain" value="' . $_GET['second_pdbchain'] . '">';
		}
		else {
		  echo '<input type="text" class="form-control" name="second_pdbchain" maxlength="5" id="second_pdbchain" placeholder="Enter PDB ID and chain">';
		}
		?>
		</label>
		
		<label>Select graph-type to use for second graph: 
		  <select id="second_graphtype_int" name="second_graphtype_int">
		  <?php
		     $values = array(1, 2, 3, 4, 5, 6);
		     $labels = array("Alpha", "Beta", "Alpha-Beta", "Alpha-Ligand", "Beta-Ligand", "Alpha-Beta-Ligand");
		     
		     $pre_sel = 3;
		     if(isset($_GET['second_graphtype_int'])) {
		       $tmp = intval($_GET['second_graphtype_int']);
		       if($tmp >= 1 && $tmp <= 6) {
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

		<br><br>
		Note: Submitting the search will gather the graph data, compute a compatibility graph and run a variant of the Bron-Kerbosch algorithm on it. This may take some time, depending on the graph sizes. You will be presented a list of common substructures when the algorithm has finished.
		<br><br>
							
		<button type="submit" id="sendit_all_pgs_of_chain" "class="btn btn-default">Run comparison algorithm <span class="glyphicon glyphicon-search"></span></button><br>

		</form>	
							

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
