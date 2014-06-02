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
			
			<h4><font color="red">54859</font> Proteins</h4>
			<h4><font color="red">2094546</font> Secondary Structure Elements</h4>
				
		
			<div class="table-responsive" id="contentTable">
				<table class="table table-condensed table-hover borderless">
					<tr>
						<th class="tablecenter">Graph-type</th>
						<th class="tablecenter">Notation-type</th>
						<th class="tablecenter">Total</th>
						<th class="tablecenter">Non-redundant</th>
						<th class="tablecenter">Barrels</th>
					</tr>
					<tr class="tablecenter">
						<th class="tablecenter">Alpha</th>
						<td>ADJ</td>
						<td>636820</td>
						<td>26984</td>
						<td>1063</td>
					</tr>
					<tr class="tablecenter">
						<td></td>
						<td>RED</td>
						<td>636820</td>
						<td>1772</td>
						<td>385</td>
					</tr>
					<tr class="tablecenter">
						<td></td>
						<td>KEY</td>
						<td>636820</td>
						<td>887</td>
						<td>191</td>
					</tr>
					<tr class="tablecenter">
						<td></td>
						<td>SEQ</td>
						<td>636820</td>
						<td>9054</td>
						<td>479</td>
					</tr>
					<tr class="tablecenter">
						<th class="tablecenter">Beta</th>
						<td>ADJ</td>
						<td>199122</td>
						<td>13418</td>
						<td>1220</td>
					</tr>
					<tr class="tablecenter">
						<td></td>
						<td>RED</td>
						<td>199122</td>
						<td>9528</td>
						<td>705</td>
					</tr>
					<tr class="tablecenter">
						<td></td>
						<td>KEY</td>
						<td>199122</td>
						<td>2470</td>
						<td>696</td>
					</tr>
					<tr class="tablecenter">
						<td></td>
						<td>SEQ</td>
						<td>199122</td>
						<td>3262</td>
						<td>435</td>
					</tr>
					<tr class="tablecenter">
						<th class="tablecenter">Alpha-Beta</th>
						<td>ADJ</td>
						<td>606816</td>
						<td>77127</td>
						<td>1494</td>
					</tr>
					<tr class="tablecenter">
						<td></td>
						<td>RED</td>
						<td>606816</td>
						<td>62500</td>
						<td>761</td>
					</tr>
					<tr class="tablecenter">
						<td></td>
						<td>KEY</td>
						<td>606816</td>
						<td>3109</td>
						<td>666</td>
					</tr>
					<tr class="tablecenter">
						<td></td>
						<td>SEQ</td>
						<td>606816</td>
						<td>43798</td>
						<td>1040</td>
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
