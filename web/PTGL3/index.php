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
                                    
                                    
                                    </script>
                
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
				

				<a href="#" class="navbar-brand"><img src="ADD_IMAGE_HERE" alt="PTGL Logo"></a>

					<div class="nav-collapse collapse navbar-responsive-collapse">
						<ul class="nav navbar-nav">
							<li  class="active navbarFont">
								<a href="#">Home</a>
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
									
									<!-- divider class creates a horizontal line in the dropdown menu -->
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
				</div><!-- end container -->
			</div><!-- end navbar fixed-top -->
		
		
		
						
			<div class="row" id="search">
				<div class="col-lg-3 col-centered">
					<div class="input-group form-group">
						<form class="form-inline" action="searchResults.php" method="post">
							<input type="text" class="form-control" name="keyword" id="searchInput" placeholder="Enter PDB ID or keyword...">
							<button type"submit" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button>
						</form>
						<form>
							<label class="checkboxFont">
								<input type="checkbox" id="inlineCheckbox3" value="option3"> Remove sequence homologs </input>
							</label>
							<label class="checkboxFont">
								<input type="checkbox" id="matchexactCB" name="exact" value="1"> Match exact word </input>
							</label>
						</form>	
					</div><!-- end input-group and form-group -->
				</div><!-- end col-centered -->
			</div><!-- end row -->
					
			
			<div class="row">
				<div class="col-lg-3 col-centered">
					<div class="input-group form-group">
						<form>
						<dl class="dl-horizontal">
					<dt>SearchKey</dt>
					<dd>Keyword search form with examples</dd>
				</dl>
				
				<dl class="dl-horizontal">
					<dt>SearchMotifs</dt>
					<dd>Search form for topological protein structure motifs</dd>
				</dl>
				
				<dl class="dl-horizontal">
					<dt>SearchSequence</dt>
					<dd>Blast search for sequences</dd>
				</dl>
				</form>
					</div><!-- end input-group and form-group -->
				</div><!-- end col-centered -->
			</div><!-- end row -->
		
		
		
		
		</div><!-- end container and wrapper -->
			
			
	<div class="textWrapper">
		<div class="line" id="lineAbout">
			<div class="iconPos" id="iconAbout">
				<img  width="100px" height="100px" src="images/icon-blueGlyph.png" alt="Icon">
				<a name="About"></a>
			</div><!-- end iconPos -->
		</div><!-- end line and lineAbout -->
			
		<div class="container" id="text">
			<h2>About</h2>
				<p>
					Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna 							aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea 							takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy 							eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores 							et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, 						consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. 						At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum 						dolor sit amet.   
				</p>
		</div><!-- end container and text -->

		<div class="line" id="lineUserGuide">
			<div class="iconPos" id="iconUserGuide">
				<img width="100px" height="100px" src="images/icon-greenGlyph.png" alt="Icon"/>
				<a name="User Guide"></a>
			</div><!-- end iconPos -->
		</div><!-- end line and lineUser Guide -->
		
		<div class="container" id="text">
			<h2>User Guide</h2>
				<p>
					Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam 						erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata 						sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor 						invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet 						clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing 					elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et 					justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
				</p>
		</div><!-- end container and text -->
			
		<div class="line" id="lineDatabaseFormat">
			<div class="iconPos" id="iconDatabaseFormat">
				<img width="100px" height="100px" src="images/icon-yellowGlyph.png" alt="Icon"/>
				<a name="Database Format"></a>
			</div><!-- end iconPos -->
		</div><!-- end line and lineDatabaseFormat -->
		
		<div class="container" id="text">
		<h2>Database Format</h2>
			<p>
				Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam 						erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata 						sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor 						invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet 						clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing 					elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et 					justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
			</p>
	</div><!-- end container and text -->
	</div><!--- end textWrapper -->
	
	</div><!-- end wrapper -->
	
	
	<footer id="footer">
		<div class="container">
			<p> 
				Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam 					erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus 					est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut 					labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd 					gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
			</p>
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
