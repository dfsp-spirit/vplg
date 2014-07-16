<?php
/** This file is neccessary if the images should be loaded dynamically.
 * 
 * In fact this file could be deleted, its condition is worse than getMainImage.php
 * 
 * Unfortunatly this version does NOT work correctly and is pretty useless at this
 * point. In fact it should work like the display_proteins.php script. 
 * The idea was, that this file is called by an AJAX request, get the neccessary images
 * and put the new createt HTML string into the existing HTML structure. Well, its twisted...
 * 
 * It receives an array of PDB-IDs which where selected by the user. Also, it gets an value
 * of the currently selected protein-slide (int). So you should be able to load
 * the correct and now needed images. Somehow, that was the idea. 
 * 
 * The main problem is, that loading new images corrupts the slider... :(
 *   
 * @author Daniel Bruness <dbruness@gmail.com>
 * @author Andreas Scheck <andreas.scheck.home@googlemail.com>
 */

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


$output = '<p>- Select topology type -</p>
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