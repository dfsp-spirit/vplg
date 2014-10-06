<?php
/** This file redirects and forces a download of the requested ZIP-file.
 *   
 * @author Daniel Bruness <dbruness@gmail.com>
 * @author Andreas Scheck <andreas.scheck.home@googlemail.com>
 */

 
function endsWith($haystack, $needle)
{
    return $needle === "" || substr($haystack, -strlen($needle)) === $needle;
}
 
// if parameter is set correclty..
if(isset($_GET['dl'])){
	$dl = $_GET['dl'];
	// if requested file exists..
	
	if(file_exists($dl)){
		// change the filename which was previously the whole file path
		
		$filename = str_replace('../temp_downloads/', "", $dl);
		//echo "filename is '$filename', dl is '$dl'.";
		// change header to force file download
		
		if(endsWith($filename, ".zip")) {
		
		  if(strpos($file, '..') === FALSE) {		
		    header("Content-type: application/zip"); 
		    header("Content-Disposition: attachment; filename=$filename");
		    header("Content-length: " . filesize($dl));
		    header("Pragma: no-cache"); 
		    header("Expires: 0"); 
		    readfile("$dl");
		  } else {
		    echo "ERROR: Filename '$filename' contains invalid chars.";
		  }
		}
		else {
		  echo "ERROR: Filename does not end with '.zip'.";
		}
	}
}
//EOF
?>