<?php
if(isset($_GET['dl'])){
	$dl = $_GET['dl'];
	if(file_exists($dl)){
		$filename = str_replace('../temp_downloads/', "", $dl);
		header("Content-type: application/zip"); 
		header("Content-Disposition: attachment; filename=$filename");
		header("Content-length: " . filesize($dl));
		header("Pragma: no-cache"); 
		header("Expires: 0"); 
		readfile("$dl");
	}
}
?>
