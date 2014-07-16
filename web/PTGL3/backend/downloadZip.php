<?php
/** This file redirects and forces a download of the requested ZIP-file.
 *   
 * @author Daniel Bruness <dbruness@gmail.com>
 * @author Andreas Scheck <andreas.scheck.home@googlemail.com>
 */

// if parameter is set correclty..
if(isset($_GET['dl'])){
	$dl = $_GET['dl'];
	// if requested file exists..
	if(file_exists($dl)){
		// change the filename which was previously the whole file path
		$filename = str_replace('../temp_downloads/', "", $dl);
		// change header to force file download
		header("Content-type: application/zip"); 
		header("Content-Disposition: attachment; filename=$filename");
		header("Content-length: " . filesize($dl));
		header("Pragma: no-cache"); 
		header("Expires: 0"); 
		readfile("$dl");
	}
}
//EOF
?>