<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);


// Define variables

$db_config = include('config.php');     //TODO: Sichern?

// Define functions

function getGraphbyID($pdbID){
	# TODO
}

/*

// try to get _GET
if(isset($_GET)) {
    if(isset($_GET["pcs"])) {$pcs = $_GET["pcs"];};
}

echo "<br /><br /><br /><br /><br />Selected Chains: " . $pcs . "<br />";

$chains = explode(" ", trim($pcs));  #Clip whitespaces and seperate PDB IDs at whitespaces -> Array
var_dump($chains);

foreach ($chains as $value ){
	if (!(strlen($value) == 4) || (!(is_numeric($value[0])))) {
		echo "<br />'" . $value . "' has a wrong PDB-ID format\n<br />";
	}
}


*/
$svg1 = file_get_contents("./proteins/7tim/7tim_A_albe_PG.svg");

$tableString = '		<div id="myCarousel">
					<ul class="bxslider bx-prev bx-next" id="carouselSlider">';
 
/*$tableString .= '		<li>
							<img src="./proteins/7tim/7tim_A_albe_PG.png"></img>
						</li>';
 */
 $tableString .= ' <li>
						<div class="container">
						<p>Select topology type</p>
						
						<form class="form-inline" role="form">
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    Alpha-Beta
						  </label>
						</div>
						
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    Alpha
						  </label>
						</div>
						  
						  <div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    Beta
						  </label>
						</div>
						
						</form>
						
						<h3>1a0c</h3>
						<h4>Protein graph 1a0cA</h4>
						
						<div class="proteingraph">
							<img src="proteins/7tim/7tim_A_albe_PG.png" alt=""/>
						
						</div>
						
						<div class="table-responsive" id="sse">
							<table class="table table-condensed table-hover borderless">
								<tr>
									<th class="tablecenter">Nr.</th>
									<th class="tablecenter">Type</th>
									<th class="tablecenter">Sequence</th>
									<th class="tablecenter">from - to</th>
								</tr>
								<tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>2</td>
									<td>E</td>
									<td>FSIA</td>
									<td>43 - 46</td>
								</tr><tr class="tablecenter">
									<td>3</td>
									<td>H</td>
									<td>YWHTF</td>
									<td>47 - 51</td>
								</tr><tr class="tablecenter">
									<td>4</td>
									<td>H</td>
									<td>PWNH</td>
									<td>67 - 70</td>
								</tr><tr class="tablecenter">
									<td>5</td>
									<td>H</td>
									<td>PMDIAKARVEAAFEFFDKI</td>
									<td>74 - 92</td>
								</tr><tr class="tablecenter">
									<td>6</td>
									<td>E</td>
									<td>YFCFH</td>
									<td>96 - 100</td>
								</tr><tr class="tablecenter">
									<td>7</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>8</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>9</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>10</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>11</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr>
								</tr><tr class="tablecenter">
									<td>12</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>13</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>14</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>15</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>16</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>17</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>18</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>19</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>20</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>21</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr>
							</table>
						</div><!-- end table-responsive -->
					
					
					
						<p>Select linear notation:</p>
						<div id="selectNot">
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    adjacent
						  </label>
						</div>
						
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    reduced
						  </label>
						</div>
						  
						  <div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    key
						  </label>
						</div>
						  
						  <div class="radio">
							  <label>
							    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
							    sequence
							  </label>
						</div>
						</div><!-- end selectNot -->		
					
					
					<ul class="bxslider" id="tada">
						  <li><img src="test.png" /></li>
						  <li><img src="test.png" /></li>
						  <li><img src="test.png" /></li>
					</ul>

						
						
						<div id="bx-pager">
						
						  <a class="thumbalign" data-slide-index="0" href=""><img src="thumbtest.png" /></a>
						 
						  <a class="thumbalign" data-slide-index="1" href=""><img src="thumbtest.png" /></a>
						  
						  <a class="thumbalign" data-slide-index="2" href=""><img src="thumbtest.png" /></a>
						
						</div>
						
						
						
						
						
						
						</div>
					
						
					</li>';
						$tableString .= ' <li>
						<div class="container">
						<p>Select topology type</p>
						
						<form class="form-inline" role="form">
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    Alpha-Beta
						  </label>
						</div>
						
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    Alpha
						  </label>
						</div>
						  
						  <div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    Beta
						  </label>
						</div>
						
						</form>
						
						<h3>1a0c</h3>
						<h4>Protein graph 1a0cB</h4>
						
						<div class="proteingraph">
							<img src="proteins/7tim/7tim_A_albe_PG.png" alt=""/>
						
						</div>
						
						<div class="table-responsive" id="sse">
							<table class="table table-condensed table-hover borderless">
								<tr>
									<th class="tablecenter">Nr.</th>
									<th class="tablecenter">Type</th>
									<th class="tablecenter">Sequence</th>
									<th class="tablecenter">from - to</th>
								</tr>
								<tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>2</td>
									<td>E</td>
									<td>FSIA</td>
									<td>43 - 46</td>
								</tr><tr class="tablecenter">
									<td>3</td>
									<td>H</td>
									<td>YWHTF</td>
									<td>47 - 51</td>
								</tr><tr class="tablecenter">
									<td>4</td>
									<td>H</td>
									<td>PWNH</td>
									<td>67 - 70</td>
								</tr><tr class="tablecenter">
									<td>5</td>
									<td>H</td>
									<td>PMDIAKARVEAAFEFFDKI</td>
									<td>74 - 92</td>
								</tr><tr class="tablecenter">
									<td>6</td>
									<td>E</td>
									<td>YFCFH</td>
									<td>96 - 100</td>
								</tr><tr class="tablecenter">
									<td>7</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>8</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>9</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>10</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>11</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr>
								</tr><tr class="tablecenter">
									<td>12</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>13</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>14</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>15</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>16</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>17</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>18</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>19</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>20</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>21</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr>
							</table>
						</div><!-- end table-responsive -->
					
					
					
						<p>Select linear notation:</p>
						<div id="selectNot">
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    adjacent
						  </label>
						</div>
						
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    reduced
						  </label>
						</div>
						  
						  <div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    key
						  </label>
						</div>
						  
						  <div class="radio">
							  <label>
							    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
							    sequence
							  </label>
						</div>
						</div><!-- end selectNot -->		
					</div>
					
						
					</li>';
					
					
					
					
					$tableString .= ' <li>
						<div class="container">
						<p>Select topology type</p>
						
						<form class="form-inline" role="form">
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    Alpha-Beta
						  </label>
						</div>
						
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    Alpha
						  </label>
						</div>
						  
						  <div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    Beta
						  </label>
						</div>
						
						</form>
						
						<h3>1a0c</h3>
						<h4>Protein graph 1a0cC</h4>
						
						<div class="proteingraph">
							<img src="proteins/7tim/7tim_A_albe_PG.png" alt=""/>
						
						</div>
						
						<div class="table-responsive" id="sse">
							<table class="table table-condensed table-hover borderless">
								<tr>
									<th class="tablecenter">Nr.</th>
									<th class="tablecenter">Type</th>
									<th class="tablecenter">Sequence</th>
									<th class="tablecenter">from - to</th>
								</tr>
								<tr class="tablecenter">
									<td>1</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>2</td>
									<td>E</td>
									<td>FSIA</td>
									<td>43 - 46</td>
								</tr><tr class="tablecenter">
									<td>3</td>
									<td>H</td>
									<td>YWHTF</td>
									<td>47 - 51</td>
								</tr><tr class="tablecenter">
									<td>4</td>
									<td>H</td>
									<td>PWNH</td>
									<td>67 - 70</td>
								</tr><tr class="tablecenter">
									<td>5</td>
									<td>H</td>
									<td>PMDIAKARVEAAFEFFDKI</td>
									<td>74 - 92</td>
								</tr><tr class="tablecenter">
									<td>6</td>
									<td>E</td>
									<td>YFCFH</td>
									<td>96 - 100</td>
								</tr><tr class="tablecenter">
									<td>7</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>8</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>9</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>10</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr><tr class="tablecenter">
									<td>11</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr>
								</tr><tr class="tablecenter">
									<td>12</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>13</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>14</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>15</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>16</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>17</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>18</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>19</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>20</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr></tr><tr class="tablecenter">
									<td>21</td>
									<td>H</td>
									<td>MEEHL</td>
									<td>37 - 41</td>
								</tr>
							</table>
						</div><!-- end table-responsive -->
					
					
					
						<p>Select linear notation:</p>
						<div id="selectNot">
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    adjacent
						  </label>
						</div>
						
						<div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    reduced
						  </label>
						</div>
						  
						  <div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
						    key
						  </label>
						</div>
						  
						  <div class="radio">
							  <label>
							    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
							    sequence
							  </label>
						</div>
						</div><!-- end selectNot -->		
					
					
				</div>
				</div>
					
						
					</li>';
				
					

 /*
 
 
 $tableString .= '		<li>
							<img src="./proteins/1gos/1gos_A_albe_PG.png"></img>
						</li>';*/
 
 $tableString .= '	</ul>
				</div>';
/*
	
	// establish pgsql connection
	$conn_string = "host=" . $db_config['host'] . " port=" . $db_config['port'] . " dbname=" . $db_config['db'] . " user=" . $db_config['user'] ." password=" . $db_config['pw'];
	$db = pg_connect($conn_string)
					or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            
	$query = "SELECT * FROM plcc_protein WHERE pdb_id LIKE '%".$keyword."%' OR header LIKE '%".strtoupper($keyword)."%'";
	$result = pg_query($db, $query) 
					  or die($query . ' -> Query failed: ' . pg_last_error());

	$counter = 0;
	$tableString = "";
	while (($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)) && ($counter <= 30)){
		if ($counter % 2 == 0){     // performs alternating orange/white tables
			$class = "Orange";
		} else {
			$class = "White";
		}
		// var_dump($arr);
		$tableString .=	 '<div class="results results'.$class.'">					
						<div class="resultsHeader resultsHeader'.$class.'">
							<div class="resultsId">'.$arr["pdb_id"].'</div>
							<div class="resultsRes">Resolution: '.$arr["resolution"].' &Aring;</div>
							<div class="resultsLink"><a href="http://www.rcsb.org/pdb/explore/explore.do?structureId='.$arr["pdb_id"].'">[PDB]</a>
												<a href="">[PDBSum]</a>
												<a href="">[FASTA]</a></div>
						</div>
						<div class="resultsBody1">
							<div class="resultsTitle">Title</div>
							<div class="resultsTitlePDB">' .$arr["title"]. '</div>
						</div>
						<div class="resultsBody2">
							<div class="resultsClass">Classification</div>
							<div class="resultsClassPDB">'.$arr["header"].'</div>
						</div>
						<div class="resultsBody3">
							<div class="resultsEC">EC#	</div>
							<div class="resultsECNum">#####</div>
						</div>
						<div class="resultsFooter">
							<div class="resultsChain">Chain</div>
							<div class="resultsChainNum"><input type=checkbox id="'.$arr["pdb_id"].'" class="chainCheckBox" name="1" value="'.$arr["pdb_id"].'"/>'.$arr["pdb_id"].'A</div>
							<div class="resultsSCOP">Scop ####</div>
							<div class="resultsCATH">CATH ####</div>
						</div>
					</div>';

		echo $counter;
		$counter++;
	}

	pg_free_result($result); // clean memory
	pg_close($db); // close connection
}
*/
?>