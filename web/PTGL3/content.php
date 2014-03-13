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
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap-glyphicons.css">


		<!-- Custom CSS -->
		<link rel="stylesheet" type="text/css" href="custom/css/styles.css">

		 <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
		<!-- Include Modernizr in the head, before any other JS -->
		<script src="bootstrap/js/modernizr-2.6.2.min.js"></script>

                    <!-- Live Search for PDB IDs -->
		<script src="livesearch.js" type="text/javascript"></script>
		


	</head>

	<body id="customBackground">
		<div class="wrapper">
		<div class="container">
		
			<div class="navbar navbar-fixed-top" id="navColor">

				<div class="container">

				<button class="navbar-toggle" data-target=".navbar-responsive-collapse" data-toggle="collapse" type="button">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				

				<a href="index.php" class="navbar-brand"><img src="ADD_IMAGE_HERE" alt="PTGL Logo"></a>
					<div class="nav-collapse collapse navbar-responsive-collapse" id="navbar-example">
						<div class="navbar-scrollspy">
						<ul class="nav navbar-nav">
							<li  class="navbarFont">
								<a href="index.php">Home</a>
							</li>

							<li class="navbarFont">
								<a href="index.php#About">About</a>
							</li>
						
							<li class="navbarFont">
								<a href="index.php#UserGuide">User Guide</a>
							</li>
							
							<li class="navbarFont">
								<a href="index.php#DatabaseFormat">Database Format</a>
							</li>
							
							<li class="dropdown">
								<!-- <strong>caret</strong> creates the little triangle/arrow -->
								<a href="#"  class="navbarFont dropdown-toggle" data-toggle="dropdown"> Services <strong class="caret"></strong></a>
								
								<ul class="dropdown-menu">
									<li>
										<a href="#">Content</a>
									</li>
									
									<li>
										<a href="publications.php">Publications</a>
									</li>
									
									<li>
										<a href="#">File Formats</a>
									</li>
									
									<!-- divider class creates a horizontal line in the dropdown menu -->
									<li class="divider"></li>
									
									<li class="dropdown-header"></li>
									
									<li>
										<a href="contact.php">Contact Us</a>
									</li>
									
									<li>
										<a href="#">Help</a>
									</li>
								</ul><!-- end dropdown menu -->
							</li><!-- end dropdown -->
						</ul><!-- end nav navbar-nav -->
						</div><!-- end navbar-scrollspy -->
					</div><!-- end nav-collapse -->
					<div class="nav-collapse collapse navbar-responsive-collapse">
						<form  class="navbar-form pull-right" action="searchResults.php" method="get">
							<input type="text" class="form-control" name="keyword" id="searchInput" autocomplete="off" placeholder="Enter PDB ID or keyword...">
							<button type="submit" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button>
							<div id="liveSearchResults" class="liveSearchResultsPage"></div>
						</form><!-- end navbar-form -->	
					</div>
				</div><!-- end container -->
			</div><!-- end navbar fixed-top -->
		</div><!-- end container -->
		
		
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
	
	
	<footer id="footer">
		<div class="container">
				<div class="row">
				<div class="col-sm-2">
				</div>
			
				<div class="col-sm-2">
					<a class="footerLink" href="#">Impressum</a>
				</div>
				
				<div class="col-sm-2">
					<a class="footerLink" href="contact.php">Contact</a>
				</div>
				
				<div class="col-sm-2">
					<a class="footerLink" href="http://www.bioinformatik.uni-frankfurt.de" target="_blank">MolBi - Group</a>
				</div>
				
				<div class="col-sm-2">
					<a class="footerLink" href="publications.php">Publications</a>
				</div>
				
				<div class="col-sm-2">
				</div>
			
			<div class="row">
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				<div class="col-sm-1">
				</div>
				
				<div class="col-sm-2 flush-right">
					<br>
					<br>
					Copyright © 2013 [name]
				</div>
			</div>
		</div><!-- end container -->
	</footer>
		
		















		<!-- All Javascript at the bottom of the page for faster page loading -->
		<!-- also needed for the dropdown menus etc. ... -->
		
		<!-- First try for the online version of jQuery-->
		<script src="http://code.jquery.com/jquery.js"></script>
		
		<!-- If no online access, fallback to our hardcoded version of jQuery -->
		<script>window.jQuery || document.write('<script src="bootstrap/js/jquery-1.8.2.min.js"><\/script>')</script>
		
		<!-- Bootstrap JS -->
		<script src="bootstrap/js/bootstrap.min.js"></script>
		
		<!-- Custom JS -->
		<script src="bootstrap/js/script.js"></script>





	</body>
</html>
