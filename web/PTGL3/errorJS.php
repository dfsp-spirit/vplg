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
</head>

<body id="customBackground">
	<div class="wrapper">
        <div class="container">
            <br><br>
            <div class="container" id="intro">
                <div class="boxedred" style="padding: 5px;">
                <h3>Dear User</h3>
                <p>Unfortunately your browser doesn't support JavaScript or you deactivated it.
                    To use the full functionality of our site we ask you to activate JavaScript
                    and to go back to the main page.</p>
                <p>Thank you!</p>
                <a href="http://ptgl.uni-frankfurt.de" title="Home">PTGL main page</a>
                </div><br><br>
            </div>    
            
            
	<div class="navbar navbar-fixed-top" id="navColor">
		<div class="container">
			<button class="navbar-toggle" data-target=".navbar-responsive-collapse" data-toggle="collapse" type="button">
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<!-- <a href="index.php" class="navbar-brand"><img src="./images/ptgl.png" alt="PTGL Logo"></a> -->
			<div class="nav-collapse collapse navbar-responsive-collapse">
				<ul class="nav navbar-nav">
					<li  class="navbarFont">
						<a href="index.php">PTGL</a>
					</li>
				</ul><!-- end nav navbar-nav -->								
			</div><!-- end nav-collapse -->
		</div><!-- end container -->
	</div><!-- end navbar fixed-top -->
</div>

	<div id="Home">
		<div class="container" id="intro">
			
			The <?php echo $SITE_TITLE; ?> web server provides a database of protein secondary structure topologies. It is based on protein ligand graphs computed
			by the <a href="http://www.bioinformatik.uni-frankfurt.de/tools/vplg/" target="_blank">VPLG software</a>. The <?php echo $SITE_TITLE; ?> uses a graph-based model to describe the structure of proteins on the super-secondary structure level.
			
			<!--
			A protein ligand graph is
			computed from the atomic coordinates in a PDB file and the secondary structure assignments of the DSSP algorithm. In this graph, vertices
			represent secondary structure elements (SSEs, usually alpha helices and beta strands) or ligand molecules while the edges model contacts and 
			relative orientations between the SSEs.
			-->
			<br /><br />
			
			<p class="imgCenter"><img src="./images/ptgl_overview_trans.png" width="600"></p>
			
			<br /><br />
			This web server allows you to search for <b>protein motifs</b> which can be detected in the graphs. It also provides <b>standardized 2D visualizations</b> of protein graphs and folding graphs.
			In contrast to the manually curated <a href="http://www.cathdb.info/" target="_blank">CATH</a> and <a href="http://scop.mrc-lmb.cam.ac.uk/scop/" target="_blank">SCOP</a> databases, the method used by this server is fully automated.
			Similar servers, which also support substructure search, include <a href="http://prodata.swmed.edu/prosmos/" target="_blank">ProSMoS</a> and <a href="http://munk.csse.unimelb.edu.au/pro-origami/" target="_blank">Pro-Origami</a>.
			
			<?php if($ENABLE_GRAPHLETSIMILARITY_SEARCH) { ?>
			Additionally, this server implements graph-based similarity measures to search for protein chains which are similar to a given query protein.
			<?php } ?>
			
			<br>
			
			
		</div><!-- end container-->
	</div><!-- end Home -->
	</div><!-- end container and wrapper -->
</div><!-- end wrapper -->


<?php include('footer.php'); ?>

</body>
</html>
