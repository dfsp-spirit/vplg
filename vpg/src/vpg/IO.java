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
import java.util.*;


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
     * Writes the string 'text' to the text file 'targetFile'. Tries to create the file and overwrite stuff in it.
     * @return true if it worked out, false otherwise. Will spit warning to STDERR if things go wrong.
     */ 
    public static Boolean stringToTextFile(String targetFile, String text) {
        String file = targetFile;
        FileWriter fw = null;
        PrintWriter pw = null;

        try {
            fw = new FileWriter(file);
            pw = new PrintWriter(fw);

        }
        catch (Exception e) {
            System.err.println(Settings.getApptag() + "ERROR: Could not write to file '" + file + "': " + e.getMessage() + ".");            
            return(false);
        }

        pw.print(text);      
        pw.close();  // If it isn't closed it won't flush the buffer and large parts of the file will be missing!

        try {
            fw.close();
        } catch(Exception ex) {
            System.err.println(Settings.getApptag() + "WARNING: Could not close FileWriter for file '" + file + "': " + ex.getMessage() + ".");
            return(false);
        }
        
        return(true);
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
    
    
   /**
     * Reads the target text file and returns the data in it.
     * @param file Path to a readable text file. Does NOT test whether it exist, do that earlier.
     * @return the data as a multi line string 
     */
    public static String slurpFileSingString(String file) {

        String lines = "";
        Integer numLines = 0;

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = in.readLine()) != null) {
                numLines++;
                lines += line + "\n";
            }
	} catch (IOException e) {
            System.err.println("ERROR: Could not read text file '" + file + "'.");
            e.printStackTrace();
            System.exit(1);
	}

        return(lines);
    }
    
    /**
     * Parses the plcc format string for meta data and returns it in a HashMap. Everyline in the string that is in the correct format ("> key > value") is parsed and the resulting (key, value) pair
     * is added to the returned HashMap. This function guarantees that the HashMap contains at least one key: format_version. If it is not found in the file,
     * it is set to '1' because version 1 is the only version which did NOT have this field.
     * 
     * @param graphString the graph string to scan
     * @return the meta data in a HashMap.
     */
    public static HashMap<String, String> getMetaData(String graphString) {
        
        HashMap<String, String> md = new HashMap<String, String>();
        
        
        ArrayList<String> lines = multiLineStringToStringList(graphString);
        
        // other vars
        String l = null;            // a line!
        String empty, key, value;
        empty = key = value = null;
        String [] words;

        Integer curLine = 0;
        Boolean error = false;
        
        // Get all met data entries
        for(Integer i = 0; i < lines.size(); i++) {

            curLine++;            
            
            // remove all whitespace from the line, it is not needed and will make splitting way easier later
            l = lines.get(i).replaceAll("\\s*","");
                                    
            if(l.startsWith(">")) {
                
                //System.out.println("[SSE] * Handling line #" + curLine + " of the " + lines.size() + " lines.");
                //System.out.println("[SSE]   Line: '" + l + "'");
                
                try {
                    words = l.split("\\>");
                    
                    if(words.length != 3) {
                        System.err.println(Settings.getApptag() + "ERROR: PLCC_FORMAT: Hit meta data line containing " + words.length + " fields at line #" + curLine + " (expected 3).");
                        error = true;
                    }
                    
                    empty = words[0];           // the empty string, leftmost field
                    key = words[1];
                    value = words[2];                    
                } catch(Exception e) {
                    System.err.println(Settings.getApptag() + "ERROR: PLCC_FORMAT: Broken meta data line encountered at line #" + curLine + ". Ignoring.");
                    error = false;              // may have been set before exception!
                    continue;
                }
                
                if(error) {
                    System.err.println(Settings.getApptag() + "ERROR: PLCC_FORMAT: Broken meta data line encountered at line #" + curLine + ", wrong number of fields. Ignoring.");
                    error = false;
                    continue;
                }
                
                md.put(key, value);
            }
        }
        
        
        return(md);
    }
    
    
    /**
     * Converts a multi-line string to a list of strings, split at the newlines.
     * @param s the input string
     * @return the list of lines
     */
    public static ArrayList<String> multiLineStringToStringList(String s) {
        
        String lines[] = s.split("\\r?\\n");
        
        return(new ArrayList<String>(Arrays.asList(lines)));
    }
    
    
    /**
     * Formats the string hasmap.
     * @param md the HashMap of strings
     * @return a formatted multi-line string of md
     */
    public static String getFormattedMetaDataString(HashMap<String, String> md) {
        if(md.keySet().size() < 1) {
            return("No Graph info found.");
        }
        
        String mdf = "Graph info:\n";
        String value;
        
        for(String key : md.keySet()) {
            value = (String)md.get(key);
            mdf += " " + key + ": " + value + "\n";
        }
        
        return mdf;
    }
          
    
}
