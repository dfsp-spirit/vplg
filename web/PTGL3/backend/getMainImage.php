<?php

$db_config = include('config.php'); 
$graphtype = "alpha"; # alpha-helix is #1
$graphtypes = array("alpha", "beta", "albe", "alphalig", "betalig", "albelig");

$graphtype_dict = array(
	"alpha" => "Alpha",
	"beta" => "Beta",
	"albe" => "Alpha-Beta",
	"alphalig" => "Alpha-Ligand",
	"betalig" => "Beta-Ligand",
	"albelig" => "Alpha-Beta-Ligand"
);

$index = array_search($graphtype, $graphtypes); //remove currently displayed graphtype from graphtype array
if($index !== FALSE){
    unset($graphtypes[$index]);
}

$conn_string = "host=" . $db_config['host'] . " port=" . $db_config['port'] . " dbname=" . $db_config['db'] . " user=" . $db_config['user'] ." password=" . $db_config['pw'];
$db = pg_connect($conn_string)
		or die($db_config['db'] . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            
	


$currentSlide = $_POST['currentSlide'];
$chainIDs = $_POST['chainIDs'];
$loadSlideNumner = $currentSlide + 1;
$pdb_chain = str_split($chainIDs[$loadSlideNumner], 4);

$pdb_id = $pdb_chain[0];
$chain = $pdb_chain[1];

$alpha_image_link = "./data/".$pdb_id."_".$chain."_alpha_PG.png";




pg_query($db, "DEALLOCATE ALL");
$query = "SELECT * FROM plcc_chain, plcc_graph WHERE pdb_id LIKE $1 AND chain_name LIKE $2 AND plcc_graph.chain_id = plcc_chain.chain_id ORDER BY graph_type"; 
pg_prepare($db, "getChains", $query) or die($query . ' -> Query failed: ' . pg_last_error());		
$result = pg_execute($db, "getChains", array("%".$pdbID."%", "%".$chainName."%"));  
$data = pg_fetch_array($result, NULL, PGSQL_ASSOC);
$chain_id = (int) $data['chain_id'];

/*
$output = '<a href="./data/'.$data['graph_image_png'].'" target="_blank">
			<img src="./data/'.$data['graph_image_png'].'" alt="" />
			 </a>
			 <a href="./data/'.$data['graph_image_png'].'" target="_blank">Full Size Image</a>
			 <span class="download-options">Download Graph: [GML] [PS] [else]</span>';
*/

$output = '<li>
<a title="Loaded_'.$loaded_images.'" href="./data/'.$data['graph_image_png'].'" target="_blank">
	<img src="./data/'.$data['graph_image_png'].'" alt="" />
</a>
<a href="./data/'.$data['graph_image_png'].'" target="_blank">Full Size Image</a>
<span class="download-options">Download Graph: ';

if(isset($data['graph_image_eps']) && file_exists("./data/".$data['graph_image_eps'])) {
	$output .= '
<a href="./data/'.$data['graph_image_eps'].'" target="_blank">[EPS]</a>';
}

if(isset($data['graph_image_svg']) && file_exists("./data/".$data['graph_image_svg'])){
	$output .= '
<a href="./data/'.$data['graph_image_svg'].'" target="_blank">[SVG]</a>';
}
if(isset($data['graph_image_png']) && file_exists("./data/".$data['graph_image_png'])){
	$output .= '
<a href="./data/'.$data['graph_image_png'].'" target="_blank">[PNG]</a>';
}

$output .= '
</span></li>';

while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
	$output .= '<li>';
	$output .= '<a href="./data/'.$arr['graph_image_png'].'" target="_blank">
					<img src="./data/'.$arr['graph_image_png'].'" alt="" />
					</a>
				<a href="./data/'.$arr['graph_image_png'].'" target="_blank">Full Size Image</a>
				<span class="download-options">Download Graph: ';

	if(isset($data['graph_image_eps']) && file_exists("./data/".$data['graph_image_eps'])){
		$output .= '<a href="./data/'.$data['graph_image_eps'].'" target="_blank">[EPS]</a>';
	}
	if(isset($data['graph_image_svg']) && file_exists("./data/".$data['graph_image_svg'])){
		$output .= '<a href="./data/'.$data['graph_image_svg'].'" target="_blank">[SVG]</a>';
	}
	if(isset($data['graph_image_png']) && file_exists("./data/".$data['graph_image_png'])){
		$output .= '<a href="./data/'.$data['graph_image_png'].'" target="_blank">[PNG]</a>';
	}
	$output .= '</span>';
	$output .= '</li>';
	
}





echo $output;

?>