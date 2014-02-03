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

		
		<!-- Include Modernizr in the head, before any other JS -->
		<script src="bootstrap/js/modernizr-2.6.2.min.js"></script>
		
		<!-- Live Search for PDB IDs -->
		<script type="text/javascript">
			$(document).ready(function () {                            
				$("input#searchInput").live("keyup", function(e) {                        
					}
				)};
		</script>/* still needs to be modified */	
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
				

				<a href="index.html" class="navbar-brand"><img src="ADD_IMAGE_HERE" alt="PTGL Logo"></a>

					<div class="nav-collapse collapse navbar-responsive-collapse">
						<ul class="nav navbar-nav">
							<li  class="navbarFont">
								<a href="index.html">Home</a>
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
						</form><!-- end navbar-form -->	
					</div>
				</div><!-- end container -->
			</div><!-- end navbar fixed-top -->
		
		<div class="container" id="contactUs">
			<h2> Contact Us </h2>
			<br>
			<h4> List of people associated with PTGL: </h4>
			
			
			
			<div class="contactTable table-responsive">
				<table border="0">
					<colgroup>
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
						<col width="50%">
					</colgroup>						
						
					<tr><th id="test">Prof. Dr. Ina Koch</th><td></td></tr>
					<tr><td>Johann Wolfgang Goethe-University Frankfurt a. Main</td><td>Phone  +49 +69 798-24652</td></tr>
					<tr><td>Faculty of Computer Science and Mathematics, Dept. 12</td><td>Fax      +49 +69 798-24650</td></tr>
					<tr><td>Institute for Computer Science</td><td>e-mail:  ina.koch (at) bioinformatik.uni-frankfurt.de</td></tr>
					<tr><td>Molecular Bioinformatics (MBI)</td><td><a href="http://www.bioinformatik.uni-frankfurt.de">http://www.bioinformatik.uni-frankfurt.de/</a></td></tr>
					<tr><td>Robert-Mayer-Strasse 11-15</td><td></td></tr>
					<tr><td>60325 Frankfurt a. Main</td><td></td></tr>
					<tr><td>Germany</td><td></td></tr>
					<tr><td><br></td><td></td><td></td></tr>
					<tr><th>Dr. Patrick May</th><td></td></tr>
					<tr><td>Max Planck Institute of Molecular Plant Physiology</td><td>Phone  +49 331 567-8615</td></tr>
					<tr><td>Am Muehlenberg 1</td><td>e-mail:  may (at) mpimp-golm.mpg.de</td></tr>
					<tr><td>14476 Potsdam-Golm</td><td><a href="http://bioinformatics.mpimp-golm.mpg.de/group-members/patrick-may">http://bioinformatics.mpimp-golm.mpg.de/group-members/patrick-may</a></td></tr>
					<tr><td>Germany</td><td></td></tr>
					<tr><td><br></td><td> </td><td> </td></tr>
					<tr><th>Dr. Thomas Steinke</th><td></td></tr>
					<tr><td>Zuse Institute Berlin</td><td>e-mail:  steinke (at) zib.de</td></tr>
					<tr><td>Computer Science Research</td><td></td></tr>
					<tr><td>Takustr. 7</td><td></td></tr>
					<tr><td>14195 Berlin</td><td></td></tr>
					<tr><td>Germany</td><td></td></tr>
				
				
				
				</table>
			</div><!-- end ContactTable -->
		
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		</div><!-- end container -->
		</div><!-- end container -->
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