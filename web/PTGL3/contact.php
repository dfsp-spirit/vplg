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
										<a href="about.php"><span class="fa fa-info"></span> About</a>
									</li>
									<li>
										<a href="content.php"><i class="fa fa-briefcase"></i> Content</a>
									</li>
									
									<li>
										<a href="publications.php"><i class="fa fa-copy"></i> Publications</a>
									</li>
																	
									<!-- divider class creates a horizontal line in the dropdown menu -->
									<li class="divider"></li>
									
									<li class="dropdown-header"></li>
									
									<li>
										<a href="#"><i class="fa fa-user"></i> Contact Us</a>
									</li>
									
									<li>
										<a href="help.php"><i class="fa fa-question"></i> Help</a>
									</li>
								</ul><!-- end dropdown menu -->
							</li><!-- end dropdown -->
						</ul><!-- end nav navbar-nav -->								
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
		
		<div class="container" id="contactUs">
			<h2> Contact Us </h2>
			<br>
			<h4> List of people associated with PTGL </h4>
			
			
			
			
			
			
		<div id="contact">
		<div class="row" id="member1">
			<div class="col-sm-6">
				 <div class="leftColumn">
					<strong>Prof. Dr. Ina Koch</strong>
					<br>
					Johann Wolfgang Goethe-University Frankfurt a. Main
					<br>
					Faculty of Computer Science and Mathematics, Dept. 12
					<br>
					Institute for Computer Science
					<br>
					Molecular Bioinformatics (MBI)
					<br>
					Robert-Mayer-Strasse 11-15
					<br>
					60325 Frankfurt a. Main
					<br>
					Germany
				</div><!-- end leftColumn -->
			</div><!-- end col-sm-6 -->
				
			<div class="col-sm-6">
				<div class="rightColumn">
					<br>
					<i class="fa fa-phone"></i> Phone  +49 +69 798-24652
					<br>
					Fax    +49 +69 798-24650
					<br>
					<i class="fa fa-envelope-o"></i> e-mail:  ina.koch (at) bioinformatik.uni-frankfurt.de
					<br>
					<i class="fa fa-external-link"><a href="http://www.bioinformatik.uni-frankfurt.de" target="_blank"></i> http://www.bioinformatik.uni-frankfurt.de/</a>
				</div><!-- end rightColumn -->
			</div><!-- end col-sm-6 -->
		</div><!-- end row -->

		<div class="row" id="member2">
			<div class="col-sm-6">
				<div class="leftColumn">
					<strong>Dr. Patrick May</strong>
					<br>
					Max Planck Institute of Molecular Plant Physiology
					<br>
					Am Muehlenberg 1
					<br>
					14476 Potsdam-Golm
					<br>
					Germany
				</div><!-- end leftColumn-->
			</div><!-- end col-sm-6 -->
		
			<div class="col-sm-6">
				<div class="rightColumn">
					<br>
					<i class="fa fa-phone"></i> Phone  +49 331 567-8615
					<br>
					<i class="fa fa-envelope-o"></i> e-mail:  may (at) mpimp-golm.mpg.de
					<br>
					<i class="fa fa-external-link"><a href="http://bioinformatics.mpimp-golm.mpg.de/group-members/patrick-may" target="_blank"></i> http://bioinformatics.mpimp-golm.mpg.de/group-members/patrick-may</a>
				</div><!-- end rightColumn-->
			</div><!-- end col-sm-6 -->
		</div><!-- end row -->
		
		<div class="row" id="member3">
			<div class="col-sm-6">
				<div class="leftColumn">
					<strong>Dr. Thomas Steinke</strong>
					<br>
					Zuse Institute Berlin
					<br>
					Computer Science Research
					<br>
					Takustrasse 7
					<br>
					14195 Berlin
					<br>
					Germany
				</div><!-- end leftColumn -->
			</div><!-- end col-sm-6 -->
			
			<div class="col-sm-6">
				<div class="rightColumn">
					<br>
					<i class="fa fa-envelope-o"></i> e-mail:  steinke (at) zib.de
				</div><!-- end rightColumn -->
			</div><!-- end col-sm-6 -->
		</div><!-- end row -->
		</div><!-- end contact -->
		
	</div><!-- end container -->
	</div><!-- end container -->
	</div><!-- end wrapper -->
	
	
	<footer id="footer">
		<div class="container">
			<div class="row">
				<div class="col-sm-2">
					<a href="about.php" class="footerLink">About</a>
				</div>
			
				<div class="col-sm-2">
					<a class="footerLink" href="help.php">Help</a>
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
					<a href="about.php#linking" class="footerLink">Linking PTGL</a>
				</div>
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
		<script>window.jQuery || document.write('<script src="js/jquery-1.8.2.min.js"><\/script>')</script>
		
		<!-- Bootstrap JS -->
		<script src="js/bootstrap.min.js"></script>
		
		<!-- Custom JS -->
		<script src="js/script.js"></script>


	</body>
</html>