<!DOCTYPE html>
<?php
include('./backend/display_proteins.php');
?>
<html>
	<head>
		<meta http-equiv="cache-control" content="no-cache">
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="description" content="PTGL">
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
	
		
		<div class="container" id="proteinDisplay">
			<b>Please note:</b> Only <a href="about.php#foldingGraph">folding graphs</a> with more than one SSE are presented. The <a href="about.php#foldingGraph">folding graphs</a> with only one SSE
			are representing just single vertices in the <a href="about.php#proteinGraph">protein graph</a>.
			<p>In the <a href="about.php#key">key</a> notation only folding graphs can be shown that are <a href="">non-bifurcated</a>.</p>
			
			<span id="multipleDownload">Download checked Proteins as <select name="multipledownload">
												<option value="null">-- Select to download --</option>
												<option value="ps">PostScript</option>
												<option value="svg">SVG</option>
												<option value="png">PNG</option></select>		
			<br>
			<span>Please <a href="citing.php">cite PTGL</a> correctly, if you are using our data & images.</span>																					
		</div>
		
		
		
		
		<!--BEGIN CAROUSEL -->
		<?php echo $tableString; ?>
		<!--END CAROUSEL -->
	
		
		
		
		
		
		</div><!-- end wrapper -->
		

	<?php include('footer.php'); ?>
		

		<!-- All Javascript at the bottom of the page for faster page loading -->
		<!-- also needed for the dropdown menus etc. ... -->
		
		
		<!-- Don not use this import, as bxSlider seems not to work with newer versions of jQuery -->
		<!-- First try for the online version of jQuery-->
		<!-- <script src="http://code.jquery.com/jquery.js"></script>-->
		<!-- If no online access, fallback to our hardcoded version of jQuery -->
		<!-- <script>window.jQuery || document.write('<script src="js/jquery-1.8.2.min.js"><\/script>')</script>-->
	
		
		
		<!-- First try for the online version of jQuery-->
		<script src="http://code.jquery.com/jquery.js"></script>
		
		<!-- If no online access, fallback to our hardcoded version of jQuery -->
		<script>window.jQuery || document.write('<script src="js/jquery-1.8.2.min.js"><\/script>')</script>
		
		
		
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>

		<!-- Bootstrap JS -->
		<script src="js/bootstrap.min.js"></script>
		
		<!-- Custom JS -->
		<!-- <script src="js/script.js"></script>
			

		<!-- Live Search for PDB IDs -->
		<!-- <script src="js/livesearch.js" type="text/javascript"></script>
		
		<!-- bxSlider Javascript file -->
		<script src="js/jquery.bxslider.min.js"></script>
		<!-- <script src="js/bxslider-custom.js"></script> -->



		<!-- Dynamic ContentLoader -->
		<script type="text/javascript">

		$(document).ready(function () {             

		var viewWidth = $(window).width();
			slider = $('#carouselSlider').bxSlider({
			minSlide: 1,
			maxSlide: 1,
			slideWidth: viewWidth,
			infiniteLoop: false,
			hideControlOnEnd: true
		});
	
		$('.tada').bxSlider({
			startSlide: 0,
			controls: false,
			minSlide: 1,
			maxSlide: 1,
			slideWidth: 600,
			pagerCustom: '.bx-pager-own'
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
			url: "./backend/getImages.php",
			data: dataToSend,
			cache: false,
			success: function(html){
				$("#"+ chainIDs[currentSlide + 1]).html(html);
				slider.reloadSlider({
    				startSlide: currentSlide
    			});
			}
			}); 
		}	


		var steps = 0;
		var prevsteps = 0;
		$("[class='bx-next']").click( function() {
			steps++;
			if(steps % 1 == 0 && steps > prevsteps){
				loadNext();
				prevsteps = steps;
			}
		});

		$("[class='bx-prev']").click( function() {
			steps--;
		});	
		});


		</script>	

	</body>
</html>
