/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package vpg;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;


/**
 * A general utility class used for input / output operations.
 * @author spirit
 */
public class IO {
    
 

 
    public static String server = "ftp.server.com";
    public static String userName = "username";
    public static String password = "password";
    public static String fileName = "index.html";
 
    
    
    /**
     * 
     * @param ftpUrl the FTP URL, e.g. "ftp://" + userName + ":" + password + "@" + server + "/public_html/" + fileName + "".
     * @param targetFile the local path where the file should be saved
     * @return a list of error messages. If this list is empty, everything worked out.
     */
    public static ArrayList<String> wget(URL url, String targetFile) {
 
        System.out.println(Settings.getApptag() + "Connecting to remote server to download file...");    
        ArrayList<String> errors = new ArrayList<String>();
        
        File outFile = new File(targetFile);
        if(outFile.exists()) {
            errors.add("Output file '" + targetFile + "' already exists, aborting download.");
            return errors;
        }
 
        try {
            //Connection String
            String fileName = url.getPath();
            URLConnection con = url.openConnection();

            BufferedInputStream in = new BufferedInputStream(con.getInputStream());

            //System.out.println("Downloading file.");

            FileOutputStream out = new FileOutputStream(targetFile);

            int i = 0;
            byte[] bytesIn = new byte[1024];
            while ((i = in.read(bytesIn)) >= 0) {
                out.write(bytesIn, 0, i);
            }
            out.close();
            in.close();

        } catch (Exception e) {
            System.err.println(Settings.getApptag() + "ERROR: Could not download file '" + fileName + "' from '" + url.toString() + "': '" + e.getMessage() + "'.");
            errors.add("ERROR downloading file '" + fileName + "': '" + e.getMessage() + "'.");
        }
        
        return errors; 
 
    }
    
    
    /**
     * Constructs the PDB file download URL for the given PDB ID using the template URL from settings.
     * @param pdbid the pdbid to insert into the template URL string
     * @return the URL or null if an invalid URL resulted from template and/or pdbid
     */
    public static URL getDownloadUrlPDB(String pdbid) {
        String urlString = Settings.get("vpg_S_download_pdbfile_URL");
        
        if(Settings.getBoolean("vpg_B_download_pdbid_is_lowercase")) {
            pdbid = pdbid.toLowerCase();
        }
        
        urlString = urlString.replace("<PDBID>", pdbid);
        
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (Exception e) {
            System.err.println(Settings.getApptag() + "ERROR creating PDB URL: '" + e.getMessage() + "'.");
            return null;
        }
        return(url);
    }
    
    
    
    /**
     * Constructs the DSSP file download URL for the given PDB ID using the template URL from settings.
     * @param pdbid the pdbid to insert into the template URL string
     * @return the URL or null if an invalid URL resulted from template and/or pdbid
     */
    public static URL getDownloadUrlDSSP(String pdbid) {
        String urlString = Settings.get("vpg_S_download_dsspfile_URL");
        
        if(Settings.getBoolean("vpg_B_download_dsspid_is_lowercase")) {
            pdbid = pdbid.toLowerCase();
        }
        
        urlString = urlString.replace("<DSSPID>", pdbid);
        
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (Exception e) {
            System.err.println(Settings.getApptag() + "ERROR creating DSSP URL: '" + e.getMessage() + "'.");
            return null;
        }
        return(url);
    }
    
    
    /**
     * Determines whether the file ends with one of the given extensions.
     * @param file the file path
     * @param validExtensions a list of extensions to check for
     * @return true if the file ends with one of the extensions, false otherwise
     */
    public static Boolean fileHasExtension(String file, String[] validExtensions) {
        Boolean res = false;

        for(String ext : validExtensions) {
            if(file.endsWith(ext)) {
                res = true;
                break;
            }
        }

        return(res);
    }
    
    
    /**
     * Determines whether a file name ends with a file extensions that suggests it was an image file.
     * @param file the path to the file
     * @return true if it ends with an image file extension: .jpg, .jpeg,  .png or .gif
     */
    public static Boolean fileIsImage(String file) {
        Boolean res = false;
        String[] validExtensions = new String[] { ".jpg", ".jpeg", ".png", ".gif" };

        for(String ext : validExtensions) {
            if(file.endsWith(ext)) {
                res = true;
                break;
            }
        }

        return(res);
    }
    
    
    
    /**
     * Returns the path to the plcc configuration file.
     * @return the absolute path to the configuration file as a string
     */
    public static String getPlccConfigFilePath() {
        return(System.getProperty("user.home") + System.getProperty("file.separator") + ".plcc_settings");
    }
    
    /**
     * Gets properties (usually settings) from a file.
     * @param configFile the file to parse
     * @return the properties
     */
    public static Properties getSettingsFromFile(File configFile) {
        
        Properties cfg = new Properties();
        

        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(configFile));
            cfg.load(stream);
            stream.close();
        } catch (Exception e) {
            System.err.println("WARNING: IO: Could not load settings from properties file '" + configFile + "': '" + e.getMessage() + "'." );
        }
        
        return cfg;        
    }
    
    /**
     * Checks the format of the given PDB identifier.
     * @param pdbid the pdb identifier
     * @return a list of warning messages. If the list is of length 0, the format is ok.
     */
    public static ArrayList<String> checkPdbIdFormat(String pdbid) {
        ArrayList<String> warnings = new ArrayList<String>();
        
        if(pdbid.isEmpty()) {
            warnings.add("ERROR: PDB identifier is empty.");
        }
        
        if(pdbid.length() != 4) {
            warnings.add("ERROR: PDB identifier has wrong length, expected 4 characters.");
        }
        
        return warnings;
    }
    
    
    /**
     * Executes the command given by a list of arguments.
     * @param cmd the command as a list of arguments
     * @param workingDir the working directory for the process
     * @return an array of 2 string, first is input and second is error
     * @throws Exception if something went wrong with the process of the workingDir does not exist
     */
    public static String[] execCmd(String[] cmd, File workingDir) throws Exception {
        
        if(! (workingDir.isDirectory() && workingDir.canRead())) {
            throw new IllegalArgumentException("Working directory '" + workingDir.getAbsolutePath() + "' does not exist.");
        }
        
        String line;
        String[] inputAndError = new String[3];
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date dateStart = new Date();
        
        System.out.println(Settings.getApptag() + "Starting external process in working directory '" + workingDir + "' at " + dateFormat.format(dateStart) + "...");
        
        Process p = Runtime.getRuntime().exec(cmd, null, workingDir);
        BufferedReader brInput = new BufferedReader(new InputStreamReader(p.getInputStream()));        
        BufferedReader brError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        String input = "";
        String error = "";
        
        while ((line = brInput.readLine()) != null) {
            input += line + "\n";
        }            
        brInput.close();

        while ((line = brError.readLine()) != null) {
            error += line + "\n";
        }
        brError.close();

        p.waitFor();
        Integer res = p.exitValue();
        
        Date dateEnd = new Date();
        System.out.println(Settings.getApptag() + "Process finished at " + dateFormat.format(dateEnd) + ".");
        
        inputAndError[0] = input;
        inputAndError[1] = error;
        inputAndError[2] = res.toString();
        //}
        //catch (Exception err) {
        //    System.err.println(Settings.getApptag() + "ERROR running process: '" + err.getMessage() + "'.");
        //}
        
        return inputAndError;
    }
          
    
}
