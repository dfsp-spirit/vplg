<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);


// Define variables

$db_config = include('./backend/config.php');     //TODO: Sichern?
$sse_type = "alpha"; # alpha-helix is #1

$sse_type_shortcuts = [
    1 => "H",
    2 => "B",
    3 => "L",
    4 => "O"
];

// Define functions

function getGraphByID($pdbID){
	# TODO
}



// try to get _GET
if(isset($_GET)) {
    if(isset($_GET["pcs"])) {$pcs = $_GET["pcs"];};
}


$chains = explode(" ", trim($pcs));  #Clip whitespaces and seperate PDB IDs at whitespaces -> Array

$conn_string = "host=" . $db_config['host'] . " port=" . $db_config['port'] . " dbname=" . $db_config['db'] . " user=" . $db_config['user'] ." password=" . $db_config['pw'];
	$db = pg_connect($conn_string)
					or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            
	

$tableString = '<div id="myCarousel">
				  <ul class="bxslider bx-prev bx-next" id="carouselSlider">';


foreach ($chains as $value){
	if (!(strlen($value) == 5)) {
		echo "<br />'" . $value . "' has a wrong PDB-ID format\n<br />";
	}
	else {
		$pdb_chain = str_split($value, 4);
		$query = "SELECT * FROM plcc_chain WHERE pdb_id LIKE '%".$pdb_chain[0]."%' AND chain_name LIKE '%".$pdb_chain[1]."%'"; #select chain_id only?
		$result = pg_query($db, $query) 
					  or die($query . ' -> Query failed: ' . pg_last_error());		  
		$chain_id = pg_fetch_array($result, NULL, PGSQL_ASSOC);
		$chain_id = (int) $chain_id['chain_id'];
		var_dump($chain_id);
		echo $chain_id;
		echo "string";
		
		$query = "SELECT * FROM plcc_sse WHERE chain_id = ".$chain_id."";
		$result = pg_query($db, $query) 
					  or die($query . ' -> Query failed: ' . pg_last_error());
		$tableString .= '<li>
						<div class="container">
						<p>Select topology type</p>
						
						
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
						<h4>Protein graph '.$value.'</h4>
						 <div class="proteingraph">
							<img src="./proteins/'.$pdb_chain[0].'/'.$pdb_chain[0].'_'.$pdb_chain[1].'_'.$sse_type.'_PG.png" alt=""/>
						 </div>
						
						 <div class="table-responsive" id="sse">
							<table class="table table-condensed table-hover borderless whiteBack">
								<tr>
									<th class="tablecenter">Nr.</th>
									<th class="tablecenter">Type</th>
									<th class="tablecenter">Sequence</th>
									<th class="tablecenter">from - to</th>
								</tr>';
		$counter = 1;						
		while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
			$tableString .= '<tr class="tablecenter">
									<td>'.$counter.'</td>
									<td>'.$sse_type_shortcuts[$arr["sse_type"]].'</td>
									<td>'.$arr["sequence"].'</td>
									<td>'.$arr["dssp_start"].' - '.$arr["dssp_end"].'</td>
								</tr>';

			$counter++;
		}
		
		$tableString .= '</table>
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
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1">
						    reduced
						  </label>
						</div>
						  
						  <div class="radio">
						  <label>
						    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1">
						    key
						  </label>
						</div>
						  
						  <div class="radio">
							  <label>
							    <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1">
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

					</li>';
	}
}

 $tableString .= '	</ul>
					</div>';				

?>