<?php

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

$currentSlide = $_POST['currentSlide'];
$chainIDs = $_POST['chainIDs'];
$loadSlideNumner = $currentSlide + 1;
$pdb_chain = str_split($chainIDs[$loadSlideNumner], 4);


$pdbID = $pdb_chain[0];
$chainName = $pdb_chain[1];


$output .= '<p>- Select topology type -</p>
				<a class="thumbalign" data-slide-index="0" href="">
					<img src="./data/'.$pdbID.'_'.$chainName.'_'.$graphtype.'_PG.png" width="100px" height="100px" />'
					.$graphtype_dict[$graphtype].'</a>';

$c = 1;					
foreach ($graphtypes as $gt){
	$output .= ' <a class="thumbalign" data-slide-index="'.$c++.'" href=""><img src="./data/'.$pdbID.'_'.$chainName.'_'.$gt.'_PG.png" width="100px" height="100px" />
						'.$graphtype_dict[$gt].'
					  </a>
					';
}

echo $output;

?>