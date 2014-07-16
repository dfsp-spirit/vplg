<?php
/** This file recives a list of PDB-IDs and a download type to create a zip
 * containing (for example) the PDFs of several proteingraphs
 *   
 * @author Daniel Bruness <dbruness@gmail.com>
 * @author Andreas Scheck <andreas.scheck.home@googlemail.com>
 */

// ini_set('display_errors',1);
// ini_set('display_startup_errors',1);
// error_reporting(-1);

// get databse connection values
$CONFIG			= include('config.php');
$DB_HOST		= $CONFIG['host'];
$DB_PORT		= $CONFIG['port'];
$DB_NAME		= $CONFIG['db'];
$DB_USER		= $CONIFG['user'];
$DB_PASSWORD	= $CONFIG['pw'];


// if the directory is not writable then abort this script
if (!is_writable('../temp_downloads/')) { die('directory not writable'); }

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
function create_zip($files = array(),$destination = '',$overwrite = false) {
	//if the zip file already exists and overwrite is false, return false
	$destination = "../temp_downloads/".$destination.".zip";
	if(file_exists($destination) && !$overwrite) { return false; }
	//vars
	$valid_files = array();
	//if files were passed in...
	if(is_array($files)) {
		//cycle through each file
		foreach($files as $file) {
			//make sure the file exists
			if(file_exists('../data/'.$file) && is_readable('../data/'.$file)) {
				$valid_files[] = $file;
			}
		}
	}
	//if we have good files...
	if(count($valid_files)) {
		//create the archive
		$zip = new ZipArchive();
		error_log("Zip Class found");
		if($zip->open($destination,$overwrite ? ZIPARCHIVE::OVERWRITE : ZIPARCHIVE::CREATE) !== true) {
			exit('cannot create zip');
		}

		//add the files
		foreach($valid_files as $file) {
			$zip->addFile('../data/'.$file,$file);

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
		$imageType = "graph_string_".strtolower($downloadType); // exception for GML files
	}
	
	// establish databse connection
	$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
	$db = pg_connect($conn_string)
			or die($DB_NAME . ' -> Connection error: ' . pg_last_error() . pg_result_error() . pg_result_error_field() . pg_result_status() . pg_connection_status() );            
	// remove previous prepared_statement
	pg_query($db, "DEALLOCATE ALL");
	// create query to recive all graph image file paths
	$query = "SELECT * FROM plcc_chain, plcc_graph WHERE pdb_id=$1 AND chain_name LIKE $2 AND plcc_graph.chain_id = plcc_chain.chain_id ORDER BY graph_type"; 
	pg_prepare($db, "getImages", $query) or die($query . ' -> Query failed: ' . pg_last_error());		
	
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
	create_zip($files, 'PTGL_FILES_'.$downloadType."_".$stamp, true);

// if parameters are not set...
} else {
	echo "<script> alert('Error!');</script>";
}
// EOF
?>