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
		<link rel="stylesheet" href="css/font-awesome.css"/>

		 <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
		<!-- Include Modernizr in the head, before any other JS -->
		<script src="js/modernizr-2.6.2.min.js"></script>

                    <!-- Live Search for PDB IDs -->
		<script src="js/livesearch.js" type="text/javascript"></script>

		<script type="text/javascript" src="./jsmol/JSmol.min.js"></script>
</head>
<body id="customBackground">
	
<script language="JavaScript" type="text/javascript">
<!--
function getParameterByName(name) {
name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
results = regex.exec(location.search);
return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
// -->
</script>	

<div class="wrapper">

<?php include('navbar.php'); ?>


<div class="container" id="D3view">
	<h2> 3D visualization of <span id="headlineChain"</span></h2>
	<br>
	<div id="D3viewcontainer">
		<div id="appdiv">
	
<script language="JavaScript" type="text/javascript">
<!--
var pdb = getParameterByName('pdbid');
var mode = getParameterByName('mode');
var pdbInvalidorUnset = true;
var pdbUnset = true;
var loadModel=":caffeine"
if(pdb != "") {
  pdbUnset = false;
  if(pdb.length == 4) {
    if(pdb.match(/^[0-9a-z]+$/)) {
      loadModel="=" + pdb;
      pdbInvalidorUnset = false;
    }
  }
}

document.getElementById('headlineChain').innerHTML = pdb; // .toUppserCase or not?!

if(mode == "") {
  mode="structure";
}
var selectavailable = false;
if(mode == "structure") {
  selectavailable=true;
}
var version = getParameterByName('version');
if(version == "") {
  version = "html5";
}

var InfoJavaPlugin = {
  addSelectionOptions: selectavailable,
  color: "#FFFFFF",
  debug: false,
  defaultModel: loadModel,
  width: 800,
  height: 600,
  isSigned: false,             // Java only
  jarFile: "jsmol/JmolApplet0.jar",  // Java only
  jarPath: "./jsmol",                // Java only
  memoryLimit: 128,            // Java only
  readyFunction: null,
  script: null,
  serverURL: "./vplgweb/jsmol/php/jsmol.php",
  src: null,
  use: "Java noWebGL noHTML5 noImage"
};	 

var InfoHTML5 = {
  addSelectionOptions: selectavailable,
  color: "#FFFFFF",
  debug: false,
  defaultModel: loadModel,
  width: 800,
  height: 600,
  j2sPath: "jsmol/j2s",              // HTML5 only
  readyFunction: null,
  script: null,
  serverURL: "./jsmol/php/jsmol.php",
  src: null,
  use: "HTML5 Image"
};	 

Jmol.setButtonCss(null, "style='font-family:Arial,sans-serif;'");
Jmol.setAppletCss(null, "style='font-family:Arial,sans-serif;margin-left=100px;'");
Jmol.setCheckboxCss(null, "style='font-family:Arial,sans-serif;'");
Jmol.setLinkCss(null, "style='font-family:Arial,sans-serif;'");
Jmol.setMenuCss(null, "style='font-family:Arial,sans-serif;'");
Jmol.setRadioCss(null, "style='font-family:Arial,sans-serif;'");
Jmol.setGrabberOptions([ ["$", "DB: Small molecules at NCI"], [":", "DB: Small molecules at PubChem"], ["==", "DB: Ligands at PDB"], ["=", "DB: Macromolecules at PDB"] ]);

if(version == "java") {
Jmol.getApplet("myJmol", InfoJavaPlugin);
}
else {
  Jmol.getApplet("myJmol", InfoHTML5);
}
Jmol.jmolBr();
document.write('<p class="tiny">Jmol interactive scripting window:</p>');
Jmol.jmolCommandInput("myJmol", "Execute Jmol command", "90%", "jmol_cmd", "Jmol command prompt")
document.write('<p class="tiny">');
document.write('Selection examples: Try "select 1-20:A; color red;" to color residues 1 to 20 of chain A red.<br/>Display/hide examples: Try "display :A" to show only chain A, "display [ALA]" to show only alanine residues and "display *" to reset.<br/>Rendering examples: Try "spacefill 100%" for space-filling, "wireframe 0.15; spacefill 20%;" for Ball-and-stick, "wireframe 0.0; spacefill 0%;" to reset. See the <a href="http://www.jmol.org" target="_blank">Jmol documentation</a> for more info.');
document.write('</p>');
if(pdbInvalidorUnset) {
    document.write('<p>');
    if(pdbUnset) {
        document.write('No PDB ID specified.');
    }
    else {
        document.write('Invalid PDB ID specified.');
    }
    document.write(' Loading default model caffeine.');
    document.write('</p>');
}
// -->
</script>
		<noscript>
			<META HTTP-EQUIV="Refresh" CONTENT="0;URL=errorJS.php">
		</noscript>
<?php
function getJmolFileName($pdbid, $chain, $graphtype) {
  return $pdbid . "_" . $chain . "_" . $graphtype . "_PG.jmol";
}
function writeJmolButtonJs($jmolcommands, $label) {
  echo "Jmol.jmolButton(myJmol, '" . $jmolcommands . "', '" . $label . "');";
}
$mode = $_GET['mode'];
$pdbid = $_GET['pdbid'];
$chain = $_GET['chain'];
$chain = strtoupper($chain);
$graphtype = $_GET['graphtype'];
if($mode == "graph" || $mode == "allgraphs") {
    $graphtypes = array();
    if($mode == "graph") {
        $graphtypes = array($graphtype);
    }
    if($mode == "allgraphs") {
        $graphtypes = array('alpha', 'beta', 'albe', 'alphalig', 'betalig', 'albelig');
    }
    $valid_pdbid = FALSE;
    $valid_chain = FALSE;
    if(ctype_alnum($pdbid) && strlen($pdbid) == 4) { $valid_pdbid = TRUE; }
    if(ctype_alnum($chain) && strlen($chain) == 1) { $valid_chain = TRUE; }
    if($valid_pdbid && $valid_chain) { echo "<p>Visualization options for chain $chain of PDB $pdbid:</p>";  }
    foreach ($graphtypes as $graphtype) {
        $valid_graphtype = FALSE;
        $valid_all = FALSE;
        if($graphtype == "alpha" || $graphtype == "beta" || $graphtype == "albe" || $graphtype == "alphalig" || $graphtype == "betalig" || $graphtype == "albelig") { $valid_graphtype = TRUE; }
        $link = "";
        if($valid_pdbid) {
            $mid_chars = substr($pdbid, 1, 2);
            if($valid_chain) {
                if($valid_graphtype) {
                    $valid_all = TRUE;
                    $jmolfile = getJmolFileName($pdbid, $chain, $graphtype);
                    $link = "./data/". $jmolfile ;
                }
                else {
                    echo "<p>ERROR: Graph visualization: PDB ID and Chain are valid but graph type is not.</p>";
                }
            }
            else {
                echo "<p>ERROR: Graph visualization: PDB ID is valid but chain is not. Cannot show graphs.</p>";
            }
            if ($valid_all) {
                if (file_exists($link)) {
                    $command = file_get_contents($link);
                    $label = "Visualize $graphtype graph";
                    
?>
                    <script language="JavaScript" type="text/javascript">
                    <!--
                    <?php
                    writeJmolButtonJs($command, $label);
                    
?>
                    // -->
                    </script>
                    <?php
                }
                else {
                    echo "<p>INFO: Graph visualization: No visualization available for $graphtype graph of protein $pdbid chain $chain.</p>";
                }
            }
            else {
                echo "<p>ERROR: Graph visualization: Some query parameters were invalid.</p>";
            }
        }
        else {
            echo "<p>ERROR: Graph visualization: The given PDB ID is invalid. Invalid query.</p>";
        }
    }
}

?>
		
	</div>
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
