<?php
/**
 * 
 */
ini_set('display_errors',1); // #TODO Remove these lines later...!
ini_set('display_startup_errors',1);
error_reporting(-1);

$CONFIG				= include('./backend/config.php'); 
$BUILD_FILE_PATH	= $CONFIG['build_file_path'];
$IMG_ROOT_PATH		= $CONFIG['img_root_path'];
$graphtype			= "alpha"; # alpha-helix is #1
$graphtypes			= array("alpha", "beta", "albe", "alphalig", "betalig", "albelig");

$graphtype_dict = array(
	"alpha"		=> "Alpha",
	"beta"		=> "Beta",
	"albe"		=> "Alpha-Beta",
	"alphalig"	=> "Alpha-Ligand",
	"betalig"	=> "Beta-Ligand",
	"albelig"	=> "Alpha-Beta-Ligand"
);

$index = array_search($graphtype, $graphtypes); //remove currently displayed graphtype from graphtype array
if($index !== FALSE){
    unset($graphtypes[$index]);
}

$sse_type_shortcuts = array(
    1 => "H",
    2 => "B",
    3 => "L",
    4 => "O"
);


if(isset($_GET)) {
    if(isset($_GET["q"])) {$q = $_GET["q"];};
}


$chains = explode(" ", trim($q));  #Clip whitespaces and seperate PDB IDs at whitespaces -> Array

$conn_string = "host=" . $CONFIG['host'] . " port=" . $CONFIG['port'] . " dbname=" . $CONFIG['db'] . " user=" . $CONFIG['user'] ." password=" . $CONFIG['pw'];
$db = pg_connect($conn_string)
		or die($CONFIG['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            
	

$tableString = '<div id="myCarousel">
				  <ul class="bxslider bx-prev bx-next" id="carouselSlider">';

$loaded_images = 0;
$allChainIDs = array();
foreach ($chains as $value){
	if (!(strlen($value) == 5)) {
		echo "<br />'" . $value . "' has a wrong PDB-ID format\n<br />";
	}
	else {

		array_push($allChainIDs, $value);
		$pdb_chain = str_split($value, 4);
		$pdbID = $pdb_chain[0];
		$chainName = $pdb_chain[1];

		pg_query($db, "DEALLOCATE ALL");
		$query = "SELECT * FROM plcc_chain, plcc_graph 
				  WHERE pdb_id LIKE $1 
				  AND chain_name LIKE $2 
				  AND plcc_graph.chain_id = plcc_chain.chain_id 
				  ORDER BY graph_type"; 
		
		pg_prepare($db, "getChains", $query) or die($query . ' -> Query failed: ' . pg_last_error());		
		$result = pg_execute($db, "getChains", array("%".$pdbID."%", "%".$chainName."%"));  
		$data = pg_fetch_array($result, NULL, PGSQL_ASSOC);
		$chain_id = (int) $data['chain_id'];

		
		$query_SSE = "SELECT * FROM plcc_sse WHERE chain_id = ".$chain_id." ORDER BY position_in_chain";
		$result_SSE = pg_query($db, $query_SSE) 
					  or die($query . ' -> Query failed: ' . pg_last_error());
		
		$tableString .= '<li>
						<div class="container">
						<h4>Protein graph for '.$pdbID.', chain '.$chainName.'</h4>
						<div class="proteingraph">
						  <div>
							<input type="checkbox" name="'.$pdbID.$chainName.'" value="'.$pdbID.$chainName.'"> Enqueue in Downloadlist
						 	<span class="download-options"><a href="3Dview.php?pdbid='.$pdbID.'&chain='.$chainName.'&mode=allgraphs" target="_blank">3D-View [JMOL]</a></span>
						  </div>	
						  <ul id="'.$pdbID.$chainName.'" class="bxslider tada">';

					// if($loaded_images < 2){		// use this to limit preloaded images
		$tableString .= '<li><a title="Loaded_'.$loaded_images.'" href="./data/'.$data['graph_image_png'].'" target="_blank">
						  <img src="./data/'.$data['graph_image_png'].'" alt="" />
						</a>
						<a href="./data/'.$data['graph_image_png'].'" target="_blank">Full Size Image</a>
						<span class="download-options">Download Graph: ';

		if(isset($data['graph_image_eps']) && file_exists("./data/".$data['graph_image_eps'])) {
			$tableString .= '<a href="./data/'.$data['graph_image_eps'].'" target="_blank">[EPS]</a>';
		}
		if(isset($data['graph_image_svg']) && file_exists("./data/".$data['graph_image_svg'])){
			$tableString .= '<a href="./data/'.$data['graph_image_svg'].'" target="_blank">[SVG]</a>';
		}
		if(isset($data['graph_image_png']) && file_exists("./data/".$data['graph_image_png'])){
			$tableString .= '<a href="./data/'.$data['graph_image_png'].'" target="_blank">[PNG]</a>';
		}
		$tableString .= '</span></li>';

	// } // use this to limit preloaded images

		while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
			// if($loaded_images < 2){  // Use this to limit the amount of loaded images
			$tableString .= '<li>';
			$tableString .= '<a href="./data/'.$arr['graph_image_png'].'" target="_blank">
							   <img src="./data/'.$arr['graph_image_png'].'" alt="" />
							 </a>
						     <a href="./data/'.$arr['graph_image_png'].'" target="_blank">Full Size Image</a>
						     <span class="download-options">Download Graph: ';

			if(isset($data['graph_image_eps']) && file_exists("./data/".$data['graph_image_eps'])){
				$tableString .= '<a href="./data/'.$data['graph_image_eps'].'" target="_blank">[EPS]</a>';
			}
			if(isset($data['graph_image_svg']) && file_exists("./data/".$data['graph_image_svg'])){
				$tableString .= '<a href="./data/'.$data['graph_image_svg'].'" target="_blank">[SVG]</a>';
			}
			if(isset($data['graph_image_png']) && file_exists("./data/".$data['graph_image_png'])){
				$tableString .= '<a href="./data/'.$data['graph_image_png'].'" target="_blank">[PNG]</a>';
			}
			$tableString .= '</span>';
			$tableString .= '</li>';
		// } 
		}

		$tableString .= '</ul>
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
		
		while ($arr = pg_fetch_array($result_SSE, NULL, PGSQL_ASSOC)){
			$pdb_start = str_replace("-", "", substr($arr["pdb_start"], 1));
			$pdb_end = str_replace("-", "", substr($arr["pdb_end"], 1));
			$tableString .= '<tr class="tablecenter">
									<td>'.$counter.'</td>
									<td>'.$sse_type_shortcuts[$arr["sse_type"]].'</td>
									<td>'.$arr["sequence"].'</td>
									<td>'.$pdb_start.' - '.$pdb_end.'</td>
								</tr>';
			$counter++;
		}
		
		$tableString .= '</table>
						</div><!-- end table-responsive -->
						<div id="'.$pdbID.$chainName.'_pager" class="bx-pager-own">';
					
		// if($loaded_images < 2){ // use this to limit preloaded images
		$tableString .= '<p>- Select topology type -</p>
						<a class="thumbalign" data-slide-index="0" href=""><img src="./data/'.$pdbID.'_'.$chainName.'_'.$graphtype.'_PG.png" width="100px" height="100px" />
						'.$graphtype_dict[$graphtype].'</a>';
		$c = 1;					
		foreach ($graphtypes as $gt){
			$tableString .= ' <a class="thumbalign" data-slide-index="'.$c++.'" href=""><img src="./data/'.$pdbID.'_'.$chainName.'_'.$gt.'_PG.png" width="100px" height="100px" />
								'.$graphtype_dict[$gt].'
							  </a>';
		}
		//}
		$tableString .= '</div></li>';
	}
	$loaded_images++;
}

 $tableString .= '</ul></div>';				

?>