<!DOCTYPE html>
<?php 
$SHOW_ERROR_LIST = array();
include('./backend/config.php');
include('./common.php');
$DO_SHOW_ERROR_LIST = $DEBUG_MODE;
include('./backend/display_proteins.php');
$title = "Search results";
$title = $SITE_TITLE.$TITLE_SPACER.$title;

?>
<html>
<head>
	<meta http-equiv="cache-control" content="no-cache">
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="VPLG">
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
	<link rel="stylesheet" type="text/css" href="css/jquery.bxslider.css">
	<link rel="stylesheet" type="text/css" href="css/bxslider-custom.css">

	<!-- Include Modernizr in the head, before any other JS -->
	<script src="js/modernizr-2.6.2.min.js"></script>
</head>
<body id="customBackground">
		<noscript>
			<META HTTP-EQUIV="Refresh" CONTENT="0;URL=errorJS.php">
		</noscript>
		<div class="wrapper">
		
		<?php include('navbar.php'); ?>
		
		
		<br><br><br>
		
		<div id="PageIntro">
		<div class="container" id="pageintro">
		<h2>Search Results</h2>
		All protein graphs of a chain are shown here. You can switch the active graph type in the thumbnail slider below. You can have a look at the next
		result chain using the arrows on the left and right of the screen if you loaded more than one chain.<br><br>The secondary structure element (SSE) type abbreviations are: H = helix, E = beta strand, L = ligand.
		</div><!-- end container-->
		</div><!-- end Home -->
			
		 
		 <!-- TODO: This container should also be generated in display_proteins.php, it should only show up when the listed formats are actually available for download.     -->
		<div class="container" id="proteinDisplay">
		<!--
			<b>Please note:</b> Only <a href="about.php#foldingGraph">folding graphs</a> with more than one SSE are presented. The <a href="about.php#foldingGraph">folding graphs</a> with only one SSE
			are representing just single vertices in the <a href="about.php#proteinGraph">protein graph</a>.
			<p>In the <a href="about.php#key">key</a> notation only folding graphs can be shown that are <a href="">non-bifurcated</a>.</p>
			-->
			
			<span id="multipleDownload">Select protein chains you want to download below first. Then, download all selected proteins in format <select id="multidown" name="multipledownload">
				<option class="downloadOption" value="null">-- Select format --</option>
				<option class="downloadOption" value="pdf">PDF</option>
				<option class="downloadOption" value="svg">SVG</option>
				<option class="downloadOption" value="png">PNG</option>
				<option class="downloadOption" value="gml">GML</option></select>
			</span>
			<br><br>
			<!-- <span>Please <a href="citing.php">cite PTGL</a>, if you are using our data and images.</span> -->
		</div>
		
		<!--BEGIN CAROUSEL -->
		<?php echo $tableString; ?>
		<!--END CAROUSEL -->

		</div><!-- end wrapper -->
		
		<div id="downloadfile"></div>
		
	<?php
	// show red error bar on bottom of screen if enabled and error list is non-empty
	if($DO_SHOW_ERROR_LIST) {
	  show_the_errors($SHOW_ERROR_LIST);  
	}

	?>
		
	<?php include('footer.php'); ?>
		
		<!-- All Javascript at the bottom of the page for faster page loading -->
		<!-- also needed for the dropdown menus etc. ... -->
		
		<!-- Don not use this import, as bxSlider seems not to work with newer versions of jQuery -->
		<!-- First try for the online version of jQuery-->
		<!-- <script src="http://code.jquery.com/jquery.js"></script>-->
		<!-- If no online access, fallback to our hardcoded version of jQuery -->
		<!-- <script>window.jQuery || document.write('<script src="js/jquery-1.8.2.min.js"><\/script>')</script>-->
	
		<!-- First try for the online version of jQuery-->
		<!-- <script src="http://code.jquery.com/jquery.js"></script> -->
		
		<!-- If no online access, fallback to our hardcoded version of jQuery -->
		<script>window.jQuery || document.write('<script src="js/jquery-1.8.2.min.js"><\/script>')</script>

		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>

		<!-- Bootstrap JS -->
		<script src="js/bootstrap.min.js"></script>
		
		<!-- Custom JS -->
		<script src="js/script.js"></script>
			
		<!-- Live Search for PDB IDs -->
		<script src="js/livesearch.js" type="text/javascript"></script>
		
		<!-- bxSlider Javascript file -->
		<script src="js/jquery.bxslider.min.js"></script>	
		<!-- <script src="js/bxslider-custom.js"></script> -->

		<!-- <script src="js/queuedownload.js"></script> -->
		<!-- Dynamic ContentLoader -->
		
<script type="text/javascript">
	
		function resizeWindow(){
			$(window).resize();
		}

		$(document).ready(function () {             

				var viewWidth = $(window).width();
				slider = $('#carouselSlider').bxSlider({
				minSlide: 1,
				maxSlide: 1,
				slideWidth: viewWidth,
				infiniteLoop: false,
				hideControlOnEnd: true /*,
				onSliderLoad: function(){
					setTimeout(resizeWindow,2000);
				} */
			});

				tadas = $('.tada').bxSlider({
				startSlide: 0,
				controls: false,
				minSlide: 1,
				maxSlide: 1,
				slideWidth: 600,
				pagerCustom: '.bx-pager-own'
			});
			
			$('.downloadOption').click( function (){
				
				var downloadType = this.value;
				var proteins = new Array();
				
				if(this.value != "null") {
					$('input[type=checkbox]').each(function () {
						if (this.checked) {
							proteins.push(this.value);
							console.log(this.value + " was added to proteins array");
						}
					})
					
					if(proteins.length > 0) {
					  var dataToSend = {'downloadType' : downloadType, 'proteins[]': proteins};	
					  $.ajax({
						  type: "POST",
						  url: "./backend/createZip.php",
						  data: dataToSend,
						  cache: false,
						  success: function(html){	
							  console.log(html);
							  window.location = './backend/downloadZip.php?dl='+html;
						  }
					  });
					} else {
					  alert("Please select one or more protein chains below.");
					}
				}
			});


			function loadNext() {

				currentSlide = slider.getCurrentSlide();
				var chainIDs = <?php echo json_encode($allChainIDs); ?>;
				console.log("Current Slide: " + currentSlide);
				console.log(chainIDs);
				console.log("Put data into #"+ chainIDs[currentSlide + 1]);
				var dataToSend = {'currentSlide' : currentSlide, 'chainIDs[]' : <?php echo json_encode($allChainIDs); ?> } ;


			//Main Images
				$.ajax({
				  type: "POST",
				  url: "./backend/getMainImage.php",
				  data: dataToSend,
				  cache: false,
				  success: function(html){					
			  		  $("#"+ chainIDs[currentSlide + 1]).html(html);
					  //slider.reloadSlider({
					  //	startSlide: currentSlide
					  //});
				  }
				});
				
				$.ajax({
				type: "POST",
				url: "./backend/getThumbImages.php",
				data: dataToSend,
				cache: false,
				success: function(html){					
					$("#"+ chainIDs[currentSlide + 1]+"_pager").html(html);
					slider.reloadSlider({
						startSlide: currentSlide
					});
				}
				}); 
				
			}	

			/*
			var steps = 0;
			var prevsteps = 0;
			$('body').on('click','a.bx-next', function() {
				console.log("Clicked 'next'");
				steps++;
				if(steps % 1 == 0 && steps > prevsteps){
					console.log("Steps: " + steps);
					console.log("Try to load next...");
					loadNext();
					prevsteps = steps;
				}
				console.log("done..");
			});

			$('body').on('click','a.bx-prev', function() {
				steps--;
				console.log("Steps decremented to: " + steps);
			}); */	
		}); 


		</script>	
		
	</body>
</html>
