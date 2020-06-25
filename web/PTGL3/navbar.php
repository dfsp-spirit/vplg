
<div class="container">
	<div class="navbar navbar-fixed-top" id="navColor">
		<div class="container">
			<button class="navbar-toggle" data-target=".navbar-responsive-collapse" data-toggle="collapse" type="button">
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>

			<!-- <a href="index.php" class="navbar-brand"><img src="./images/ptgl.png" alt="PTGL Logo"></a> -->
			<div class="nav-collapse collapse navbar-responsive-collapse">
				<ul class="nav navbar-nav">
				        
					<li  class="navbarFont">
						<a href="index.php">Search</a>
					</li>		

					<li class="dropdown">
						<!-- <strong>caret</strong> creates the little triangle/arrow -->
						<a href="#"  class="navbarFont dropdown-toggle" data-toggle="dropdown"> About <strong class="caret"></strong></a>
						<ul class="dropdown-menu">
						    <li>
								<a href="about.php"><i class="fa fa-question-circle"></i> What is PTGL </a>
							</li>

							<li>
								<a href="guide.php"><i class="fa fa-question-circle"></i> Step-by-step guide </a>
							</li>

							<li>
								<a href="biomedical_examples.php"><i class="fa fa-question-circle"></i> Biomedial questions </a>
							</li>

							<li>
								<a href="help.php"><i class="fa fa-question-circle"></i> Help</a>
							</li>							
						</ul><!-- end dropdown menu -->
					</li><!-- end dropdown -->

					<li class="dropdown">
						<!-- <strong>caret</strong> creates the little triangle/arrow -->
						<a href="#"  class="navbarFont dropdown-toggle" data-toggle="dropdown"> Retrieve <strong class="caret"></strong></a>
						<ul class="dropdown-menu">
						    <li>
								<a href="proteingraphs.php"><i class="fa fa-briefcase"></i> All protein graphs (PGs) of a chain</a>
							</li>
							<li>
								<a href="foldinggraphs.php"><i class="fa fa-briefcase"></i> All folding graphs (FGs) of a PG</a>
							</li>
							<li>
								<a href="linnots_of_foldinggraph.php"><i class="fa fa-briefcase"></i> All linear notations of a FG</a>
							</li>
							
							<?php if($ENABLE_COMPLEX_GRAPHS) { ?>
							<li>
								<a href="complexgraphs.php"><i class="fa fa-briefcase"></i> The complex graph of a multi-chain protein</a>
							</li>
							<li>
								<a href="ligandcenteredgraphs.php"><i class="fa fa-briefcase"></i> Ligand-centered graphs of all ligands of a multi-chain protein</a>
							</li>
							<?php } ?>
							
						</ul><!-- end dropdown menu -->
					</li><!-- end dropdown -->

					<li class="dropdown">
						<!-- <strong>caret</strong> creates the little triangle/arrow -->
						<a href="#"  class="navbarFont dropdown-toggle" data-toggle="dropdown"> Database <strong class="caret"></strong></a>
						<ul class="dropdown-menu">
							<li>
								<a href="content.php"><i class="fa fa-briefcase"></i> Content overview</a>
							</li>
							<li>
								<a href="linearnotations.php"><i class="fa fa-briefcase"></i> All Linear notations</a>
							</li>
							<li>
								<a href="motif_overview.php"><i class="fa fa-briefcase"></i> Motif overview</a>
							</li>

							<li>
								<a href="ptgl_api.php"><i class="fa fa-briefcase"></i> REST API</a>
							</li>
						</ul><!-- end dropdown menu -->
					</li><!-- end dropdown -->
					<li class="dropdown">
						<!-- <strong>caret</strong> creates the little triangle/arrow -->
						<a href="#"  class="navbarFont dropdown-toggle" data-toggle="dropdown"> Meta <strong class="caret"></strong></a>
						<ul class="dropdown-menu">
						
						        <li>
								<a href="news.php"><span class="fa fa-info"></span> News</a>
							</li>
							<li>
								<a href="about.php"><span class="fa fa-info"></span> About</a>
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
								<a href="imprint.php"><i class="fa fa-user"></i> Imprint</a>
							</li>
							<li>
								<!-- <a href="help.php"><i class="fa fa-question"></i> Help</a> -->
								<a href="index.php#UserGuide"><i class="fa fa-question"></i> Help</a>
							</li>
						</ul><!-- end dropdown menu -->
					</li><!-- end dropdown -->
					
					<?php if($DEBUG) { ?>
					<li class="dropdown">
						<!-- <strong>caret</strong> creates the little triangle/arrow -->
						<a href="#"  class="navbarFont dropdown-toggle" data-toggle="dropdown"> Dev <strong class="caret"></strong></a>
						<ul class="dropdown-menu">
							<li>
								<a href="graphlets_pg.php"><i class="fa fa-briefcase"></i> Graphlets PG</a>
							</li>
							<li>
								<a href="graphlets_cg.php"><i class="fa fa-briefcase"></i> Graphlets CG</a>
							</li>
							<li>
								<a href="graphlets_aag.php"><i class="fa fa-briefcase"></i> Graphlets AAG</a>
							</li>
							<li>
								<a href="bkweb.php"><i class="fa fa-briefcase"></i> BK web (Visor)</a>
							</li>
						</ul><!-- end dropdown menu -->
					</li><!-- end dropdown -->
					<?php } ?>
					
				</ul><!-- end nav navbar-nav -->								
			</div><!-- end nav-collapse -->

			<!-- start live search -->
			<?php 
				if (basename($_SERVER['PHP_SELF']) != "index.php"){
					echo '
						<div class="nav-collapse collapse navbar-responsive-collapse">
							<form  class="navbar-form pull-right" action="search.php" method="get">
								<input type="text" class="form-control" name="keyword" id="searchInput" autocomplete="off" placeholder="Enter PDB ID or keyword...">
								<button type="submit" name="st" value="keyword" id="sendit_nav" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button>
								<div id="liveSearchResults" class="liveSearchResultsPage"></div>
							</form><!-- end navbar-form -->	
						</div> <!-- end live search -->';
				}
			?>
		</div><!-- end container -->
	</div><!-- end navbar fixed-top -->
</div>
