<?php include_once("./backend/analyticstracking.php") ?>

<div class="container">
	<div class="navbar navbar-fixed-top" id="navColor">
		<div class="container">
			<button class="navbar-toggle" data-target=".navbar-responsive-collapse" data-toggle="collapse" type="button">
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>

			<a href="index.php" class="navbar-brand"><img src="./images/molbi.png" alt="PTGL Logo"></a>
			<div class="nav-collapse collapse navbar-responsive-collapse">
				<ul class="nav navbar-nav">
					<li  class="navbarFont">
						<a href="index.php">Home</a>
					</li>
					<li class="navbarFont">
						<a href="about.php">About</a>
					</li>						
					<li class="navbarFont">
						<a href="index.php#UserGuide">User Guide</a>
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
								<a href="linearnotations.php"><i class="fa fa-briefcase"></i> Linear notations</a>
							</li>
							<li>
								<a href="foldinggraphs.php"><i class="fa fa-briefcase"></i> Folding graph visualization</a>
							</li>
							<li>
								<a href="publications.php"><i class="fa fa-copy"></i> Publications</a>
							</li>
							<li>
								<a href="citing.php"><i class="fa fa-bookmark"></i> Citing</a>
							</li>
							<!-- divider class creates a horizontal line in the dropdown menu -->
							<li class="divider"></li>
							<li class="dropdown-header"></li>
							<li>
								<a href="contact.php"><i class="fa fa-user"></i> Contact Us</a>
							</li>
							<li>
								<!-- <a href="help.php"><i class="fa fa-question"></i> Help</a> -->
								<a href="index.php#UserGuide"><i class="fa fa-question"></i> Help</a>
							</li>
						</ul><!-- end dropdown menu -->
					</li><!-- end dropdown -->
				</ul><!-- end nav navbar-nav -->								
			</div><!-- end nav-collapse -->

			<!-- start live search -->
			<?php 
				if (basename($_SERVER['PHP_SELF']) != "index.php"){
					echo '
						<div class="nav-collapse collapse navbar-responsive-collapse">
							<form  class="navbar-form pull-right" action="search.php" method="post">
								<input type="text" class="form-control" name="keyword" id="searchInput" autocomplete="off" placeholder="Enter PDB ID or keyword...">
								<button type="submit" id="sendit_nav" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button>
								<div id="liveSearchResults" class="liveSearchResultsPage"></div>
							</form><!-- end navbar-form -->	
						</div> <!-- end live search -->';
				}
			?>
		</div><!-- end container -->
	</div><!-- end navbar fixed-top -->
</div>