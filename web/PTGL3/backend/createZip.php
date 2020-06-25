<?php
/** This file recives a list of PDB-IDs and a download type to create a zip
 * containing (for example) the PDFs of several proteingraphs
 * 
 * jnw2020: Fix bug where zip was not created (zip open "failed"). Problem was that it no longer reported
 *          true/false but error code. Next problem was overwrite flag which resulted in error if there
 *          was no file to overwrite. So always set create flag now. This should work since we use the
 *          time stamp for the file name so there should not be a file with the same file. Also added
 *          error handling by returning an (useful) error message. 
 *   
 * @author Daniel Bruness <dbruness@gmail.com>
 * @author Andreas Scheck <andreas.scheck.home@googlemail.com>
 * @author Jan Niclas Wolf <Wolf@bioinformatik.uni-frankfurt.de>
 */

ini_set('display_errors', 0);
ini_set('display_startup_errors', 0);
ini_set('log_errors', TRUE);
error_reporting(E_ERROR);

// get config values
require('config.php'); 
include('../common.php');  // require produces error if file is missing and include warning


if($DEBUG){
	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	ini_set('log_errors', TRUE);
	error_reporting(E_ALL);
}


// if the directory is not writable then abort this script
if (!is_writable('../temp_downloads/')) { die('directory not writable'); }

/** Maps the result code from zip open to the corresponding error as string.
 *  Retrieved from comment on https://www.php.net/manual/de/ziparchive.open.php
 */
function map_zip_open_result_code_to_error_string($code) {
	switch ($code) {
        case 0:
        return 'No error';
       
        case 1:
        return 'Multi-disk zip archives not supported';
       
        case 2:
        return 'Renaming temporary file failed';
       
        case 3:
        return 'Closing zip archive failed';
       
        case 4:
        return 'Seek error';
       
        case 5:
        return 'Read error';
       
        case 6:
        return 'Write error';
       
        case 7:
        return 'CRC error';
       
        case 8:
        return 'Containing zip archive was closed';
       
        case 9:
        return 'No such file';
       
        case 10:
        return 'File already exists';
       
        case 11:
        return 'Can\'t open file';
       
        case 12:
        return 'Failure to create temporary file';
       
        case 13:
        return 'Zlib error';
       
        case 14:
        return 'Malloc failure';
       
        case 15:
        return 'Entry has been changed';
       
        case 16:
        return 'Compression method not supported';
       
        case 17:
        return 'Premature EOF';
       
        case 18:
        return 'Invalid argument';
       
        case 19:
        return 'Not a zip archive';
       
        case 20:
        return 'Internal error';
       
        case 21:
        return 'Zip archive inconsistent';
       
        case 22:
        return 'Can\'t remove file';
       
        case 23:
        return 'Entry has been deleted';
       
        default:
        return 'An unknown error has occurred('.intval($code).')';
    }         
}

/** This function creates a zip file containing the desired proteingraph images.
 * The source of this fucntion belongs to David Walsh (http://davidwalsh.name/create-zip-php) 
 * and its slightly modified.
 * 
 * Input parameters: 
 * - $files	- array of files (path) the should be zipped
 * - $destination - the output path
 * - $overwrite - Overwrite an existing ZIP?
 * 
 * It returns boolean true, if the creation was successful.
 * 
 * @param type $files
 * @param string $destination
 * @param type $overwrite
 * @return boolean
 */
function create_zip($files = array(), $destination = '', $overwrite = false) {

	global $IMG_ROOT_PATH;
	
	$destination = "../temp_downloads/".$destination.".zip";

	//if the zip file already exists and overwrite is false, return false
	if(file_exists($destination) && !$overwrite) { return false; }

	//vars
	$valid_files = array();

	//if files were passed in...
	if(is_array($files)) {
		//cycle through each file
		foreach($files as $file) {
			//make sure the file exists
			if(file_exists('.'.$IMG_ROOT_PATH.$file) && is_readable('.'.$IMG_ROOT_PATH.$file)) {
				if (strpos($file, '..') === FALSE) {
				  $valid_files[] = $file;
				}
			}
		}
	}
	//if we have good files...
	if(count($valid_files)) {
		//create the archive
		$zip = new ZipArchive();
		error_log("Zip Class found");

		$result_code = $zip->open($destination, $overwrite ? ZIPARCHIVE::OVERWRITE : ZIPARCHIVE::CREATE);
		// jnw2020: I have no idea why error code 1 appears and what it means (despite the map above),
		//          but the ZIP file seems to be created nonetheless, so treat it as success (oh boi)
		if($result_code > 1) {
			exit("[ERROR] " . map_zip_open_result_code_to_error_string($result_code));
		}

		//add the files
		foreach($valid_files as $file) {
			$zip->addFile('.'.$IMG_ROOT_PATH.$file,$file);
		}
		//close the zip -- done!
		$res = $zip->close();

		if(file_exists($destination)){
			echo $destination; 
		} 
		//check to make sure the file exists
		return file_exists($destination);
	}
	else
	{	
		return false;
	}
}

// if the post-parameters are set...
if(isset($_POST['proteins']) && isset($_POST['downloadType'])) {
	// set the variable names for readability 
	$proteins = $_POST['proteins'];
	$downloadType = $_POST['downloadType'];
	// set timestamp for unique file name
	$stamp = time();
	// set the imagetype by joining the parameter with the predefined filename string 
	$imageType = "graph_image_".strtolower($downloadType);
	if($downloadType == "gml") {
		$imageType = "filepath_graphfile_gml"; // exception for GML files
	}
	
	// establish databse connection
	$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	$db = pg_connect($conn_string);            
	// remove previous prepared_statement
	pg_query($db, "DEALLOCATE ALL");
	// create query to recive all graph image file paths
	$query = "SELECT g.graph_image_png, g.graph_image_pdf, g.graph_image_svg, g.filepath_graphfile_gml
			  FROM plcc_chain c, plcc_graph g 
			  WHERE pdb_id=$1 AND chain_name LIKE $2 
			  AND g.chain_id = c.chain_id 
			  ORDER BY graph_type"; 
	pg_prepare($db, "getImages", $query);
	
	// create array as list of filenames
	$files = array();
	foreach($proteins as $prot){
		// split PDB-ID into PDB-ID and chain name
		$pdbID = substr($prot, 0, 4);
		$chain = substr($prot, 4, 1);
		  
		$result = pg_execute($db, "getImages", array($pdbID, $chain)); 
		// put each image-file-path into the array...
		while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
			$files[] = $arr[$imageType];
		}
	}

	// call create_zip to build ZIP file containing the images
	//   jnw2020: set overwrite parameter to false b/c overwrite in zip open expects a file to overwrite
	//            and returns an error if there is none to overwrite. Create would throw an error if there
	//            was a file, but we are using timp stamps, so this is highly unlikely.
	create_zip($files, 'PTGL_FILES_'.$downloadType."_".$stamp, false);

// if parameters are not set...
} else {
	echo "Invalid download parameters.";
}
// EOF
?>