<?php
/** This file receives meta information about motifs from the database and saves them in variables for other sites to use
*/
	include("config.php");


	function get_all_motif_names() {
  		$motif_names = array("4helix", "barrel", "globin", "immuno", "jelly", "plait", "propeller", "rossman", "tim", "ubi");
  		return $motif_names;
	}

	function get_motif_data($db, $motiv_abbreviation) {
	  $motif_data = array();
	  $query = "SELECT count(c2m.chaintomotif_id) as count, m.motif_name, t.motiftype_name, m.motif_abbreviation FROM plcc_nm_chaintomotif c2m INNER JOIN plcc_motif m ON c2m.motif_id=m.motif_id INNER JOIN plcc_motiftype t ON m.motiftype_id = t.motiftype_id WHERE m.motif_abbreviation = '" . $motiv_abbreviation . "' GROUP BY m.motif_name, m.motif_abbreviation, t.motiftype_name;";
	  $result = pg_query($db, $query);
	  
	  $arr = pg_fetch_array($result, NULL, PGSQL_ASSOC);
	  $motif_data['count'] = $arr['count'];
	  $motif_data['motif_name'] = $arr['motif_name'];
	  $motif_data['motiftype_name'] = $arr['motiftype_name'];
	  $motif_data['motif_abbreviation'] = $arr['motif_abbreviation'];
	  
	  return $motif_data;
	}


	$motif_names = get_all_motif_names();
	$all_motif_counts = array();
	foreach($motif_names as $motif) {
	          
		if( ! $ENABLE_MOTIF_SEARCH_ALPHABETA) {
		             
			if($motif === "plait" || $motif === "ubi" || $motif === "tim" || $motif === "rossman") {
			    continue;
			}
		}
		            
		$motif_data = get_motif_data($db, $motif);
		$all_motif_counts[$motif] = 0;	      	            	            
		            
		if(isset($motif_data['motif_name']) && ( ! empty($motif_data['motif_name']))) {
		    $all_motif_counts[$motif] = $motif_data['count'];
		}
	}	         

?>
