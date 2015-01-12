<!DOCTYPE html>
<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);

include('./backend/config.php'); 

$title = "Maintenance";
$title = $SITE_TITLE.$TITLE_SPACER.$title;


function check_install($db)  {
    

}

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

            <?php include('./navbar.php'); ?>

	<div id="Home">
		<div class="container" id="intro">
		<!-- Intro message -->
		    <h2><?php echo $SITE_TITLE; ?> Maintenance</h2>
		    
		    This is the <?php echo $SITE_TITLE; ?> maintenance page.
		    
		    <br><br>
		    
		    <h3>Status</h3>
		
		    <?php
			
			  if($SHOW_MAINTENANCE_MESSAGE_ON_FRONTPAGE) {
			      echo "\n" . '<br><div class="boxedred"><p><br>&nbsp;&nbsp;' . "\n";
				  echo "The server is in maintenance mode, and the maintenance message is shown on the front page atm.";
				  echo "</p></div><br>\n";
			  }
			  else {
			        echo "<p>This server is NOT in maintenance mode atm.</p>\n";
			  }
			  
			  $debug_text = "off";
			  if($DEBUG_MODE) {
			      $debug_text = "on";
			  }
			  echo "<p>Debug mode is $debug_text.</p>\n";
			  
			  
			  $conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
			  $db = pg_connect($conn_string);                          
			
			  if($CHECK_INSTALL_ON_MAINTENANCEPAGE) {
			      
			      
			      $db_ok = $db;
			      $tmp_dir_ok = is_writable('./temp_downloads/');
			      $data_ok = is_dir($IMG_ROOT_PATH);
			      
			      $num_errors_occured = 0;
			      if(!$db_ok) { $num_errors_occured++; }
			      if(!$tmp_dir_ok) { $num_errors_occured++; }
			      if(!$data_ok) { $num_errors_occured++; }
			      
			      			      
			      if($num_errors_occured > 0) {
				  echo "\n" . '<br><div class="boxedred">' . "\n<ul>\n";
				  echo "<p><b>ERROR:</b> Server Installation incomplete,  the server admin needs to fix this. The following $num_errors_occured errors were detected:</p>";
				
				  if(! $db_ok) {
				      echo "<li>The database connection is not configured properly.</li>\n";
				  }
				  
				  // check whether tmp download dir (where the zip files are stored for download) is writable
				  if ( ! $tmp_dir_ok) {
				      echo "<li>The temporary file directory is not writable, ZIP file downloads disabled.</li>\n";
				  }
				  
				  // check for existence of image data directory
				  if ( ! $data_ok) {
				      echo "<li>The data directory does not exist, graph images and other data missing.</li>\n";
				  }
				  
				  echo "</ul></div><br>\n";
			      } else {
			          echo "<p>Server installation OK.</p>\n";
			      }
			  }
	
			?>
			
			<br><br>
		    
		    <h3>Tasks</h3>
		    <p>You need to provide your admin ID and the your secret admin token to start tasks.</p>		    
		    <br><br>
		    <p>
		    <b>Linnot list</b> -- generate new linear notations file from the database.
		     <form action="maintenance.php" method="POST">
		     Admin ID:
                     <input type="input" name="admin_id" value="">                     
                     Admin token:
                     <input type="password" name="admin_token" value="">                     
                     <input type="submit" value="Start linnot task" onclick="return confirm('Are you sure?')">
                     </form> 
		   </p>
			
		</div>
	</div>

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
