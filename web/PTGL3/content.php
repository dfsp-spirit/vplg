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



</head>

<body id="customBackground">
	<noscript>
		<META HTTP-EQUIV="Refresh" CONTENT="0;URL=errorJS.php">
	</noscript>
	<div class="wrapper">

		<?php include('navbar.php'); ?>

	<div class="container" id="contentText">
		<h2> Current holdings </h2>
		<br>

		<h4><font color="red">98,161</font> PDB files</h4>
		<h4><font color="red">274,459</font> Protein chains</h4>
		<h4><font color="red">5,088,843</font> Secondary structure elements (SSEs)</h4>
		<h4><font color="red">4,964,550</font> 3D contacts between SSEs</h4>
		<h4><font color="red"> * 4,964,550</font> parallel contacts</h4>


		<div class="table-responsive" id="contentTable">
			<table class="table table-condensed table-hover borderless">
				<tr>
					<th class="tablecenter">Graph-type</th>
					<th class="tablecenter">Number of graphs</th>
					<th class="tablecenter">Graphs containing beta-barrel</th>
					<th class="tablecenter">Number of SSEs in graphs</th>
				</tr>
				
				<tr class="tablecenter">
					<th class="tablecenter">Alpha</th>
					<td>274,454</td>
					<td>0</td>
					<td>2,357,932</td>

				</tr>
				
				<tr class="tablecenter">
					<th class="tablecenter">Beta</th>
					<td>274,452</td>
					<td>169,560</td>
					<td>2,094,599</td>
				</tr>
				
				<tr class="tablecenter">
					<th class="tablecenter">Alpha-Beta</th>
					<td>274,459</td>
					<td>169,542</td>
					<td>4,452,531</td>
				</tr>
				
				<tr class="tablecenter">
					<th class="tablecenter">Alphalig</th>
					<td>274,454</td>
					<td>0</td>
					<td>2,988,890</td>

				</tr>
				
				<tr class="tablecenter">
					<th class="tablecenter">Betalig</th>
					<td>274,452</td>
					<td>169,560</td>
					<td>2,723,377</td>
				</tr>
				
				<tr class="tablecenter">
					<th class="tablecenter">Alpha-Betalig</th>
					<td>274,459</td>
					<td>169,542</td>
					<td>5,099,843</td>
				</tr>
				
			</table>
		</div><!-- end table-responsive -->
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
