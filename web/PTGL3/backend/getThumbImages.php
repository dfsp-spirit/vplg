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
 
function construct_png_file_name_of($pdbid, $chain, $graphtype) {
  $valid_pdbid = FALSE;
  $valid_chain = FALSE;
  $valid_graphtype = TRUE; // TODO: check this
  
  $base_data_dir = "./data";
  
  if(ctype_alnum($pdbid) && strlen($pdbid) == 4) { $valid_pdbid = TRUE; }
  if(ctype_alnum($chain) && strlen($chain) == 1) { $valid_chain = TRUE; }
  
  if($valid_pdbid) {
    $mid_chars = substr($pdbid, 1, 2);
    if($valid_chain) {
        $link = $base_data_dir . "/" . $mid_chars . "/" . $pdbid . "/" . $chain . "/";
    }
    else {
        $link = $base_data_dir . "/" . $mid_chars . "/" . $pdbid . "/";
    }
    
    if (file_exists($link)) {
        if ($valid_chain) {
            $link .= $chain . "/" . $pdbid . '_' . $chain . '_' . $graphtype . '_PG.png';
            if(file_exists($link)) {
	      return $link;
            }
            else {
	      return ""; // image not found on disk
            }
        }
        else {
            return "";	// invalid chain
        }
    }
    else {
        return "";	// no pdb and chain dir found on disk
    }
  }
  else { 
    return ""; // invalid pdb id
  }
  return "";
}

/**
  * Returns a relative file system path to an image file or the empty string if no path could be determined / the file was missing on disk.
  */
function get_valid_png_file_name_on_disk_of($pdbid, $chain, $graphtype) {
  //return get_png_file_name_from_database_of($pdbid, $chain, $graphtype);	//TODO: implement and use this one!
  return construct_png_file_name_of($pdbid, $chain, $graphtype);
}

function get_png_file_name_from_database_of($pdbid, $chain, $graphtype) {
  die("get_png_file_name_from_database_of(): Daniel, please implement me!!!11!11!\n");
}

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

$gt_file_alpha = get_valid_png_file_name_on_disk_of($pdbID, $chainName, $graphtype);	// also ensures that the file exists on disk!

echo "\n <!-- DEBUG: The file is at: '" . $gt_file_alpha . "'. --> \n"; 

$output = '<p>- Select topology type -</p>
				<a class="thumbalign" data-slide-index="0" href="">
					<img src="' . $gt_file_alpha . '" width="100px" height="100px" />'
					.$graphtype_dict[$graphtype].'</a>';

$c = 1;					
foreach ($graphtypes as $gt){
        $gt_file = get_valid_png_file_name_on_disk_of($pdbID, $chainName, $gt);	// also ensures that the file exists on disk!
        if(strlen($gt_file) > 0) {	
	  $output .= ' <a class="thumbalign" data-slide-index="'.$c++.'" href=""><img src="'. $gt_file . '" width="100px" height="100px" />
						'.$graphtype_dict[$gt].'
					  </a>
					';
	}
}

echo $output;

?>