/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package vpg;

import java.io.*;
import java.util.Properties;
import java.util.Map;
import java.util.Set;

/**
 * This class implements a static class that manages program settings. It supports loading them from and
 * saving them to a text file in 'key = value' format.
 *
 */
public class Settings {
    
    /** The settings which are currently in use. */
    static private Properties cfg;
    
    /** The default settings. */
    static private Properties def;
    
    /** The documentation for the settings. */
    static private Properties doc;
    
    static private final String defaultFile = System.getProperty("user.home") + System.getProperty("file.separator") + ".vpg_settings";
    //static private final String defaultFile = "cfg" + System.getProperty("file.separator") + "plcc_settings.cfg";
    static private String configFile;
    
    /**
     * Resets all properties.
     */
    public static void init() {
        cfg = new Properties();
        def = new Properties();
        doc = new Properties();
        
        setDefaults();
    }
    
    
   
    
    /**
     * Returns the default config file location. Note that this may or may not be in use atm. Use getConfigFile() instead if you
     * need the file that is currently in use.
     * @return the path as a String
     */
    public static String getDefaultConfigFilePath() {
        return(defaultFile);
    }

    /**
     * Loads the properties from the file 'file'. Should be called at the start of main to init the settings. These default
     * values could then be overwritten by command line arguments.
     * @param file the configuration file to load
     * @return whether the settings could be loaded from the specified file
     */
    public static Boolean load(String file) {

        Boolean res = false;
        
        if(file.equals("")) {
            file = defaultFile;
        }

        configFile = file;

        cfg = new Properties();

        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(configFile));
            cfg.load(stream);
            stream.close();
            res = true;
        } catch (Exception e) {
            System.err.println("WARNING: Settings: Could not load settings from properties file '" + configFile + "'." );
            res = false;
        }


        System.out.println("[VPG] Loaded " + cfg.size() + " settings from properties file '" + configFile + "'." );
        return(res);
    }


    /**
     * Deletes all currently loaded properties. Note that the settings file is NOT deleted or emptied (unless you call writeToFile() afterwards).
     */
    public static void empty() {
        cfg = new Properties();
    }
    
    
    /**
     * Deletes all default properties. Note that the settings file is NOT deleted or emptied (unless you call writeToFile() afterwards).
     */
    public static void defEmpty() {
        def = new Properties();
    }


    
    /**
     * Reloads the settings from the default settings.
     * @return always true
     */
    public static Boolean resetAll() {
        cfg = new Properties();
        setDefaults();
        
        // make a deep copy of the default settings and assign it to cfg
        for (Map.Entry<Object, Object> entry : def.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            cfg.setProperty(key, value);
        }       
        
        return(true);
    }
    
    
    /**
     * Resets all properties to the default values.
     * @return always true
     */
    public static Boolean setDefaults() {

        def = new Properties();
        String fs = System.getProperty("file.separator");

        // The letter at the 6th position of the setting name always indicates the data type (I=Integer, B=Boolean, F=Float, S=String). It is not
        //  used automatically though, it's just a reminder.

        // general stuff
        defSet("vpg_I_debug_level", "0", "Debug level. 0 is default, setting higher numbers will spam STDOUT with messages and may result in funky stuff. Leave it alone unless you know what you are doing.");
        defSet("vpg_S_java_command", "java", "The command to start java. You need to specify the full path here if java is not in your path.");
        defSet("vpg_S_output_apptag", "[VPG] ", "The VPG application tag. Each line written to STDOUT starts with it.");
        defSet("vpg_S_online_manual_url", "https://sourceforge.net/p/vplg/wiki/Home/", "The URL of the VPLG online help.");
        
        // paths
        defSet("vpg_S_input_dir", System.getProperty("user.home") + fs + "data" + fs + "PDB", "The input data directory where the PDB and DSSP files are.");
        defSet("vpg_S_output_dir", System.getProperty("user.home") + fs + "data" + fs + "VPLG", "The output directory where the graph files and images should be written to.");
        defSet("vpg_S_log_dir", System.getProperty("user.home") + fs + "data" + fs + "VPLG", "The directory where log files should be written to.");
        defSet("vpg_S_last_custom_output_dir", System.getProperty("user.home") + fs + "data" + fs + "VPLG", "The custom output directory where the graph files and images should be written to. Overwritten every time you set it in the form.");
        
        String dsspcmbiExecutable = "dsspcmbi";
        if(System.getProperty("os.name").startsWith("Windows")) {
            dsspcmbiExecutable += ".exe";
        }
        
        defSet("vpg_S_path_dssp", System.getProperty("user.home") + fs + "software" + fs + "dssp" + fs + dsspcmbiExecutable, "The path to the dsspcmbi executable.");
        defSet("vpg_S_path_plcc", System.getProperty("user.home") + fs + "software" + fs + "vplg" + fs + "plcc.jar", "The path to the plcc.jar file. This is part of VPLG.");
        defSet("vpg_S_path_splitpdb", System.getProperty("user.home") + fs + "software" + fs + "vplg" + fs + "splitpdb.jar", "The path to the splitpdb.jar file. This is part of VPLG.");
        
        defSet("vpg_S_load_graph_image", System.getProperty("user.home") + fs + "data" + fs + "VPLG" + fs + "graph.png", "The default path of a graph image that is to be loaded. Will be reset at runtime.");
        
        // internet
        defSet("vpg_S_download_pdbfile_URL", "http://www.rcsb.org/pdb/files/<PDBID>.pdb.gz", "The download URL for PDB files. The part between < and > will be replaced by the PDB ID.");
        defSet("vpg_B_download_pdbid_is_lowercase", "false", "Whether the PDB ID contained in the download URL on the PDB file download server is lowercase.");
        defSet("vpg_S_download_dsspfile_URL", "ftp://ftp.cmbi.ru.nl/pub/molbio/data/dssp/<DSSPID>.dssp", "The download URL for DSSP files. The part between < and > will be replaced by the dssp ID.");
        defSet("vpg_B_download_dsspid_is_lowercase", "true", "Whether the DSSP ID contained in the download URL on the DSSP file download server is lowercase.");                
        
        // UI, confirm and warning dialogs
        defSet("vpg_B_dssp_download_warning", "true", "Whether to show warnings if the current batch settings may result in many DSSP files being downloaded from the internet.");
        
        
        return(true);
    }
    

    /**
     * Returns the VPG version.
     * @return the version string
     */
    public static String getVersion() {
        return("0.23");
    }
    
       
    
    
    /**
     * Returns the VPG apptag that is printed as a prefix for output lines.
     * @return the version string
     */
    public static String getApptag() {
        return("[VPG] ");
    }
    
    
    /**
     * Tries to set the key 'key' in the currently used settings to the default value.
     * @param key the key to set from the defaults
     * @return true if it worked out, i.e., such a key exists in the default settings and it was used. False if no such key exists in the default settings hashmap.
     */
    public static Boolean initSingleSettingFromDefault(String key) {
        if(defContains(key)) {
            cfg.setProperty(key, def.getProperty(key));
            return(true);
        }
        else {
            return(false);
        }
    }

    
    /**
     * Creates a new config file in the default location and fills it with the default values defined in the resetAll() function.
     * @return true if it worked out, false otherwise
     */
    public static Boolean createDefaultConfigFile() {
        if(resetAll()) {
            if(writeToFile(defaultFile)) {
                return(true);
            }
        }
        return(false);
    }


    /**
     * Tries to cast the value of the property key 'key' to Integer and return it. If this fails it is considered a fatal error.
     * @param key the key of the properties hashmap
     * @return the value of the key as an Integer
     */
    public static Integer getInteger(String key) {
        Integer i = null;
        String s = get(key);

        try {
            i = Integer.valueOf(s);
        }
        catch (Exception e) {
            System.err.println(Settings.getApptag() + "ERROR: Settings: Could not load setting '" + key + "' from settings as an Integer, invalid format.");
            System.exit(1);
        }
        return(i);
    }
    
    
    
    /**
     * Determines whether the key 'key' in the currently used settings is at the default setting.
     * @return true if it is in default setting, false if this setting has been changed by the user (via command line or config file)
     */
    public static Boolean isAtDefaultSetting(String key) {
        if(get(key).equals(defGet(key))) {
            return(true);
        }
        return(false);        
    }

    
    /**
     * Tries to cast the value of the property key 'key' to Float and return it. If this fails it is considered a fatal error.
     * @param key the key of the properties hashmap
     * @return the value of the key as a Float
     */
    public static Float getFloat(String key) {
        Float f = null;
        String s = get(key);

        try {
            f = Float.valueOf(s);
        }
        catch (Exception e) {
            System.err.println(Settings.getApptag() + "ERROR: Settings: Could not load setting '" + key + "' from settings as a Float, invalid format.");
            System.exit(1);
        }
        return(f);
    }


    /**
     * Tries to extract the value of the property key 'key' as a Boolean and return it. If this fails it is considered a fatal error.
     * The only accepted string representations of Booleans are "true" and "false".
     * @param key the key of the properties hashmap
     * @return the value of the key as a Boolean
     */
    public static Boolean getBoolean(String key) {
        Boolean b = null;
        String s = null;

        s = get(key);

        if(s.toLowerCase().equals("true")) {
            return(true);
        }
        else if(s.toLowerCase().equals("false")) {
            return(false);
        }
        else {
            System.err.println(Settings.getApptag() + "ERROR: Settings: Could not load setting '" + key + "' from settings as Boolean, invalid format.");
            System.exit(1);
            return(false);      // never reached
        }
    }


    /**
     * Returns the path to the currently used config file as a String.
     * @return The config file path.
     */
    public static String getConfigFile() {
        return(configFile);

    }


    /**
     * Prints all settings to STDOUT.
     */
    public static void printAll() {
        System.out.println("Printing all " + cfg.size() + " settings.");

        for (Object key : cfg.keySet()) {
            System.out.println((String)key + "=" + cfg.get(key));
        }

        System.out.println("Printing of all " + cfg.size() + " settings done.");
    }
    
    
    /**
     * Prints all settings to STDOUT.
     */
    public static void defPrintAll() {
        System.out.println("Printing all " + def.size() + " default settings.");

        for (Object key : def.keySet()) {
            System.out.println((String)key + "=" + def.get(key));
        }

        System.out.println("Printing of all " + def.size() + " default settings done.");
    }

    
    
    /**
     * Retrieves the setting with key 'key' from the settings and returns it as a String. Note that it is considered a fatal error if no such key exists. Ask first using 'contains()' if you're not sure. :)
     * @param key the key to get
     * @return the value of the specified key
     */
    public static String get(String key) {
        
        if(cfg.containsKey(key)) {
            return((String)cfg.getProperty(key));
        }
        else {
            System.out.println(Settings.getApptag() + "INFO: Settings: Setting '" + key + "' not defined in config file. Trying internal default.");
            
            if(initSingleSettingFromDefault(key)) {
                String s = defGet(key);
                
                System.out.println(Settings.getApptag() + "INFO: Settings: Using internal default value '" + s + "' for setting '" + key + "'. Edit config file to override.");
                
                return(s);                
            } else {
                System.err.println(Settings.getApptag() + "ERROR: Settings: No config file or default value for setting '" + key + "' exists, setting invalid.");
                System.exit(1);                
                return("ERROR");    // for the IDE
            }                        
        }
        
    }
    
    /**
     * Retrieves the setting with key 'key' from the default settings and returns it as a String. Note that it is considered a fatal error if no such key exists. Ask first using 'contains()' if you're not sure. :)
     * @param key the key to get
     * @return the value of the specified key
     */
    public static String defGet(String key) {

      
        if(def.containsKey(key)) {
            return((String)def.getProperty(key));
        }
        else {
            System.err.println(Settings.getApptag() + "ERROR: Settings: Could not load default setting '" + key + "' from default settings, no such setting.");
            System.exit(1);
            return(null);        // never reached, for the IDE
        }
        
    }
    

    /**
     * Adds a setting 'key' with value 'value' to the properties object. If a settings with key 'key' already exists, its value gets overwritten.
     * @param key the key which should be set
     * @param value the value for the entry with the given key
     */
    public static void set(String key, String value) {
        cfg.setProperty(key, value);
    }
    
    
    /**
     * Adds a setting 'key' with value 'value' to the default properties object. If a settings with key 'key' already exists, its value gets overwritten.
     * @param key the key which should be set
     * @param value the value for the entry with the given key
     */
    public static void defSet(String key, String value, String documentation) {
        def.setProperty(key, value);
        doc.setProperty(key, documentation);
    }



    /**
     * Determines whether the properties object contains the key 'key'.
     * @param key the key to check for
     * @return true if it contains such a key, false otherwise
     */
    public static Boolean contains(String key) {
        return(cfg.containsKey(key));
    }
    
    /**
     * Determines whether the default properties object contains the key 'key'.
     * @param key the key to check for
     * @return true if it contains such a key, false otherwise
     */
    public static Boolean defContains(String key) {
        return(def.containsKey(key));
    }
    
    
    /**
     * Returns the documentation String for setting 'key'.
     * @param key the setting you want the documentation for
     * @return the documentation as a String if it exists, "n/a" otherwise.
     */
    public static String documentationFor(String key) {
        if(doc.containsKey(key)) {
            return((String)doc.getProperty(key));
        }
        else {            
            return("n/a");
        }        
    }


    /**
     * Saves the current properties to the file 'file' or the default file if 'file' is the empty string ("").
     * @param file the file to write to. If this is the empty String (""), the default file is used instead.
     * @return True if the file was written successfully, false if an error occurred.
     */
    public static Boolean writeToFile(String file) {
        Boolean res = false;
        
        if(file.equals("")) {
            file = defaultFile;
        }

        try {
            cfg.store(new FileOutputStream(file), "These are the settings for plcc. See the documentation for info on them.");
            res = true;
        } catch(Exception e) {
            System.err.println(Settings.getApptag() + "WARNING: Settings: Could not write current properties to file '" + file + "': '" + e.getMessage() + "'.");
            res = false;
        }


        return(res);
    }
    
    
    /**
     * Saves the current default properties to the file 'file' or the default file if 'file' is the empty string ("").
     * TODO: This function could store them sorted alphabetically, with nice comments/documentation.
     * @param file the file to write to. If this is the empty String (""), the default file is used instead.
     * @return True if the file was written successfully, false if an error occurred.
     */
    public static Boolean defWriteToFile(String file) {
        Boolean res = false;
        
        if(file.equals("")) {
            file = defaultFile;
        }

        try {
            def.store(new FileOutputStream(file), "These are the default settings for plcc. See the documentation for info on them.");
            res = true;
        } catch(Exception e) {
            System.err.println(Settings.getApptag() + "WARNING: Settings: Could not write default settings to file '" + file + "'.");
            res = false;
        }


        return(res);
    }
    
    
    
    /**
     * This function is a kind of documentation generator only, it is not used during regular execution of the
     * software. It writes an example config file which includes documented version of all settings. All settings in the file
     * are set to their default value. This file is not meant to be parsed by the software and doing so manually is useless because
     * the settings represent the internal defaults anyway.
     * @param file the full path where to write the example config file
     * @return whether it worked out
     */
    public static Boolean writeDocumentedDefaultFile(String file) {
        
        String contents = "# This is the documented default config file for vpg.";
        
        for (Map.Entry<Object, Object> entry : def.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            
            contents += "\n\n# " + key + ": " + documentationFor(key);
            contents += "\n" + key + "=" + value;            
        }                

        return(writeStringToFile(file, contents));
    }
    
    
    
    /**
     * Writes the String 'data' into the file  at 'filePath', overwriting the file if it exists
     * and creating it if not.
     * @param filePath the target file system location for the file
     * @param data the String to write to the file
     * @return true if it worked out, false otherwise
     */
    public static Boolean writeStringToFile(String filePath, String data) {

        FileWriter fw = null;
        PrintWriter pw = null;
        Boolean res = false;

        try {
            fw = new FileWriter(filePath);
            pw = new PrintWriter(fw);
            pw.print(data);
            pw.close();
            res = true;
        }
        catch (Exception e) {
            System.err.println(Settings.getApptag() + "WARNING: Could not write to file '" + filePath + "'.");
            res = false;
        }
       
        try {
            fw.close();
        } catch(Exception ex) {
            System.err.println(Settings.getApptag() + "WARNING: Could not close FileWriter for file '" + filePath + "'.");
        }
        return(res);
    }                        
    
}
