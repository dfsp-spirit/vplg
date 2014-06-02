<!DOCTYPE html>
<?php 

if(isset($_GET["q"])) {
	$molecule = $_GET["q"];
} else {
	$molecule = "";
}

?>

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

		<script type="text/javascript" src="./jsmol/JSmol.min.js"></script>

<script type="text/javascript">

var jmolApplet0; // set up in HTML table, below

// logic is set by indicating order of USE -- default is HTML5 for this test page, though

var s = document.location.search;

// Developers: The _debugCode flag is checked in j2s/core/core.z.js, 
// and, if TRUE, skips loading the core methods, forcing those
// to be read from their individual directories. Set this
// true if you want to do some code debugging by inserting
// System.out.println, document.title, or alert commands   7
// anywhere in the Java or Jmol code.

Jmol._debugCode = (s.indexOf("debugcode") >= 0);

jmol_isReady = function(applet) {
	document.title = (applet._id + " - Jmol " + ___JmolVersion)
	Jmol._getElement(applet, "appletdiv").style.border="1px solid grey"
}		

var Info = {
	width: 640,
	height: 480,
	debug: false,
	color: "0xFFFFFF",
	addSelectionOptions: true,
	use: "HTML5",   // JAVA HTML5 WEBGL are all options
	j2sPath: "./jsmol/j2s", // this needs to point to where the j2s directory is.
	jarPath: "./jsmol/java",// this needs to point to where the java directory is.
	jarFile: "./jsmol/JmolAppletSigned.jar",
	isSigned: true,
	///script: "set antialiasDisplay;load jsmol/data/<?php echo $molecule; ?>.mol",
	script: "set antialiasDisplay;load proteins/7tim/tim_A_albe_PG.jmol",
	serverURL: "http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php",
	readyFunction: jmol_isReady,
	disableJ2SLoadMonitor: true,
  disableInitialConsole: true,
  allowJavaScript: true
	//defaultModel: "$dopamine",
	//console: "none", // default will be jmolApplet0_infodiv, but you can designate another div here or "none"
}

$(document).ready(function() {
  $("#appdiv").html(Jmol.getAppletHtml("jmolApplet0", Info))
})
var lastPrompt=0;
</script>

		


	</head>

	<body id="customBackground">
		<noscript>
			<META HTTP-EQUIV="Refresh" CONTENT="0;URL=errorJS.php">
		</noscript>
		<div class="wrapper">

		<?php include('navbar.php'); ?>

		
		<div class="container" id="D3view">
			<h2> 3D View </h2>
			<br>
			<div id="D3viewcontainer">
			    <div id="appdiv"></div>
			</div>
		
		
		
		
		
		
		
		
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
