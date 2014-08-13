<?php
/** This file gets some chain ids via AJAX to save them in SESSION
 *   
 * @author Daniel Bruness <dbruness@gmail.com>
 * 
 */
session_start();
if(isset($_POST)){
	if(isset($_POST["chains"])){
		$chains = $_POST["chains"];
		
		if(isset($_SESSION["chains"])){
			$prev_chains = $_SESSION["chains"];
			unset($_SESSION["chains"]);
		} else {
			$prev_chains = array();
		}
		
		$all_chains = array_merge($chains, $prev_chains);
		$unique_chains = array_unique($all_chains);
		echo $unique_chains;
		
		$_SESSION["chains"] = $unique_chains;
	}
}
//EOF
?>