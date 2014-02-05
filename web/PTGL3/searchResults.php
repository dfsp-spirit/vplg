<!DOCTYPE html>
<?php
include('search.php');
?>
<html>
	<head>
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

					<div class="nav-collapse collapse navbar-responsive-collapse">
						<ul class="nav navbar-nav">
							<li  class="navbarFont">
								<a href="index.php">Home</a>
							</li>

							<li class="navbarFont">
								<a href="#About">About</a>
							</li>
						
							<li class="navbarFont">
								<a href="#User Guide">User Guide</a>
							</li>
							
							<li class="navbarFont">
								<a href="#Database Format">Database Format</a>
							</li>		

							<li class="dropdown">
								<!-- <strong>caret</strong> creates the little triangle/arrow -->
								<a href="#"  class="navbarFont dropdown-toggle" data-toggle="dropdown"> Services <strong class="caret"></strong></a>
								
								<ul class="dropdown-menu">
									<li>
										<a href="#">Content</a>
									</li>
									
									<li>
										<a href="#">Publications</a>
									</li>
									
									<li>
										<a href="#">File Formats</a>
									</li>
									
									<!-- divider class makes a horizontal line in the dropdown menu -->
									<li class="divider"></li>
									
									<li class="dropdown-header"></li>
									
									<li>
										<a href="#">Contact Us</a>
									</li>
									
									<li>
										<a href="#">Help</a>
									</li>
								</ul><!-- end dropdown menu -->
							</li><!-- end dropdown -->
						</ul><!-- end nav navbar-nav -->								
					</div><!-- end nav-collapse -->
					<div class="nav-collapse collapse navbar-responsive-collapse">
						<form  class="navbar-form pull-right" action="searchResults.php" method="post">
							<input type="text" class="form-control" id="searchInput" placeholder="Enter PDB ID or keyword...">
							<button type="submit" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button>
							<div id="liveSearchResults" class="liveSearchResultsPage"></div>
						</form><!-- end navbar-form -->	
					</div>
				</div><!-- end container -->
			</div><!-- end navbar fixed-top -->
		
		
				
			<div class="container" id="searchResults">
					<h2> Search Results </h2>
				<div class="results">
					
				<div class="resultsHeader">
					<div class="resultsId">
					TestID
					</div>
					<div class="resultsRes">
					TestRes
					</div>
					<div class="resultsLink">
					TestLinks
					</div>
				</div><!-- end resultsHeader -->
				
				<div class="resultsBody1">
					<div class="resultsTitle">
					Title
					</div>
					<div class="resultsTitlePDB">
					Full PDB Title
					</div>
				</div><!-- end resultsBody1 -->
				
				<div class="resultsBody2">
					<div class="resultsClass">
					Classification
					</div>
					<div class="resultsClassPDB">
					Classification PDB
					</div>
				</div><!-- end resultsBody2 -->
				
				
				
				<div class="resultsBody3">
					<div class="resultsEC">
					EC number
					</div>
					<div class="resultsECNum">
					ECNum
					</div>
				</div><!-- end resultsBody3 -->
				
				
				<div class="resultsFooter">
					<div class="resultsChain">
					Chain
					</div>
					<div class="resultsChainNum">
					Chain A B C
					</div>
					<div class="resultsSCOP">
					SCOP
					</div>
					<div class="resultsCATH">
					CATH
					</div>
				</div><!-- end resultsFooter -->
				
				
				
				</div><!-- end results -->
			</div><!-- end container and searchResults -->
			
		</div><!-- end container-->

		</div><!-- end wrapper -->
	<footer id="footer">
		<div class="container">
				<div class="row">
				<div class="col-sm-2">
				</div>
			
				<div class="col-sm-2">
					<a href="#">Impressum</a>
				</div>
				
				<div class="col-sm-2">
					<a href="contact.php">Contact</a>
				</div>
				
				<div class="col-sm-2">
					<a href="http://www.bioinformatik.uni-frankfurt.de" target="_blank">MolBi - Group</a>
				</div>
				
				<div class="col-sm-2">
					<a href="#">Publications</a>
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
