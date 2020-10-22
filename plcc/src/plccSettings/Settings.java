/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Jan Niclas Wolf 2020. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author jnw
 */

package plccSettings;

import graphdrawing.DrawTools.IMAGEFORMAT;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import tools.DP;

/**
 *
 * @author jnw
 */
public class Settings {
    
    static ArrayList<Section> sections;  // holds the setting sections in their correct order
    // WARNING: Following data structures MUST be filled after sections have been filled
    static HashMap<String, String> mapSettingNameToSectionName;  // Maps settings names (=keys) to the section names they are in
    static HashMap<String, Integer> mapSectionNameToSectionIndex;  // Maps the section names (=key) to their index in the ArrayList
    
    static Boolean createdDefaultFile = false;
    static int numLoadedSettings = 0;
    
    static final String PACKAGE_TAG = "SETTINGS";
    static final String FS = System.getProperty("file.separator");
    static private final File DEFAULT_FILE = new File(System.getProperty("user.home") + FS  + "plcc_settings.txt");
    
    // init all the static fields
    static {
        // first, init sections
        initSections();
        
        // WARNING: Following functions filling the data structures MUST be called after creation of sections
        // second, init names map
        initNamesMap();
        
        // third, init index map
        initIndexMap();
    }

    /**
     * Creates all the settings sections.
     * @return settings sections.
     */
    static private void initSections() {
        sections = new ArrayList<>();
        
        // add new sections here
        // USER SECTIONS
        sections.add(new Section("General settings", "User"));
        sections.add(new Section("Amino Acid Graphs (AAG)", "User"));
        sections.add(new Section("Protein Graph (PG)", "User"));
        sections.add(new Section("Graph types", "User"));
        sections.add(new Section("Folding Graph (FG)", "User"));
        sections.add(new Section("Complex Graph (CG)", "User"));
        sections.add(new Section("Structure visualization", "User"));
        
        // ADVANCED SECTIONS
        sections.add(new Section("Prints / Error handling", "Advanced"));
        sections.add(new Section("Performance", "Advanced"));
        sections.add(new Section("Parser", "Advanced"));
        sections.add(new Section("Secondary Structure Element (SSE)", "Advanced"));
        sections.add(new Section("SSE orientation", "Advanced"));
        sections.add(new Section("Contact definition", "Advanced"));
        sections.add(new Section("AAG", "Advanced"));
        sections.add(new Section("Database (DB) connection", "Advanced"));
        sections.add(new Section("DB settings", "Advanced"));
        sections.add(new Section("DB structure search", "Advanced"));
        sections.add(new Section("FG", "Advanced"));
        sections.add(new Section("PG", "Advanced"));
        sections.add(new Section("CG", "Advanced"));
        sections.add(new Section("Motifs", "Advanced"));
        sections.add(new Section("Cluster mode", "Advanced"));
        sections.add(new Section("DB: representative chains", "Advanced"));
        sections.add(new Section("Image settings", "Advanced"));
        sections.add(new Section("Output settings", "Advanced"));
        sections.add(new Section("DB: similarity search", "Advanced"));        
        
        // DEVELOPER SECTIONS
        sections.add(new Section("Debug", "Developer"));
        sections.add(new Section("Disabled", "Developer"));
        sections.add(new Section("Alternate AAG", "Developer"));
    }
    
    
    /**
     * Creates a map for settings names to section names. Always call after sections are created!
     */
    static private void initNamesMap() {
        mapSettingNameToSectionName = new HashMap<>();
        for (Section tmpSection : sections) {
            for (Setting tmpSetting : tmpSection.settings) {
                mapSettingNameToSectionName.put(tmpSetting.getName(), tmpSection.name);
            }
        }
    }
    
    
    /**
     * Creates a map for section names to their index. Always call after sections are created!
     */
    static private void initIndexMap() {
        mapSectionNameToSectionIndex = new HashMap<>();
        for (int i = 0; i < sections.size(); i++) {
            mapSectionNameToSectionIndex.put(sections.get(i).name, i);
        }
    }
    
    
    public static void init() {
        // read settings in if existing and create otherwise
        if (io.IO.fileExistsIsFileAndCanRead(DEFAULT_FILE)) {
            Properties readProperties = io.IO.readPropertiesFromFile(DEFAULT_FILE.getAbsolutePath());
            @SuppressWarnings("unchecked")  // skip warning associated to next line, according to https://www.boraji.com/how-to-iterate-properites-in-java
            Enumeration<String> enumer = (Enumeration<String>) readProperties.propertyNames();  // seems we have to create it outside the loop head
            while (enumer.hasMoreElements()) {
                String key = enumer.nextElement();
                String value = readProperties.getProperty(key);
                if (set(key, value)) { numLoadedSettings++; } 
            }
        } else {
            // create default settings file
            DP.getInstance().w(PACKAGE_TAG, "Could not load settings from properties file, trying to create it.");
            if (io.IO.writeStringToFile(asFormattedString(), DEFAULT_FILE.getAbsolutePath(), false)) {
                createdDefaultFile = true;
            } else {
                DP.getInstance().w(PACKAGE_TAG, "Did not found default file and could not create it at '" + DEFAULT_FILE.getAbsolutePath() + "'. " +
                        "Trying to go on and using default values.");
            }
        }
    }
    
    
    static public String asFormattedString() {
        String settingsStr = "";
        String lastSectionType = "";  // used to track in which type of section we are: User, Advanced, Developer

        // header
        settingsStr += "##### PLCC SETTINGS #####\n\n";
        settingsStr += "# This file contains the settings for VPLG's PLCC as key-value pairs per line. The character atfer 'plcc' indicates which data type is expected: "
                + "B(oolean), S(tring), I(nteger) or F(loat)\n";
        settingsStr += "# The file is structured in sections which either belong to user, advanced or developer settings.\n\n";
        
        // sections
        for (int i = 0; i < sections.size(); i++) {
            Section tmpSection = sections.get(i);
            
            // print type if encountering new one
            if (! lastSectionType.equals(tmpSection.type)) {
                settingsStr += "\n### " + tmpSection.type + " settings ###\n";
                lastSectionType = tmpSection.type;
            }
            
            settingsStr += "\n## " + tmpSection.name + " ##\n\n";
            
            // settings
            for (int j = 0; j < tmpSection.settings.size(); j++) {
                Setting tmpSetting = tmpSection.settings.get(j);
                settingsStr += "# " + tmpSetting.getDocumentation() + "\n";  // documentation
                settingsStr += "# Default: '" + tmpSetting.getDefaultValue() + "'\n";  // default value
                settingsStr += tmpSetting.getName() + "=" + tmpSetting.getValue() + "\n\n";  // key-value pair
            }
        }
        
        return settingsStr;
    }
    
    
    static private Setting getSettingByName(String name) {
        if (mapSettingNameToSectionName.containsKey(name)) {
            return sections.get(mapSectionNameToSectionIndex.get(mapSettingNameToSectionName.get(name))).getSetting(name);
        } else {
            return null;
        }
    }
    
    
    /**
     * Retrieves the setting with key 'key' from the settings and returns it as a String. Note that it is considered a fatal error if no such key exists. Ask first using 'contains()' if you're not sure. :)
     * @param key the key to get
     * @return the value of the specified key
     */
    public static String get(String key) {
        if(mapSettingNameToSectionName.containsKey(key)) {
            return(getSettingByName(key).getValue());
        }
        else {
            DP.getInstance().e(PACKAGE_TAG, "No config file or default value for setting '" + key + "' exists, setting invalid. "
                    + "Please inform a developer.  Exiting now.");
            System.exit(1);                
            return("ERROR");    // for the IDE                    
        }
        
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
        catch (NumberFormatException e) {
            DP.getInstance().e(PACKAGE_TAG, "Could not load setting '" + key + "' from settings as an Integer, invalid format. Exiting now.");
            System.exit(1);
        }
        return(i);
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
        catch (NumberFormatException e) {
            DP.getInstance().e(PACKAGE_TAG, "Could not load setting '" + key + "' from settings as an Float, invalid format. Exiting now.");
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
            DP.getInstance().e(PACKAGE_TAG, "Could not load setting '" + key + "' from settings as an Boolean, invalid format. Exiting now.");
            System.exit(1);
            return(false);      // never reached
        }
    }
    
    
    static public Boolean set(String key, String value) {
        Setting targetSetting = getSettingByName(key);
        if (targetSetting != null) {
            return getSettingByName(key).setOverwrittenValue(value);
        } else {
            DP.getInstance().w(PACKAGE_TAG, "Could not find setting '" + key + "' to overwrite its value. Going on without changes to the settings.");
            return false;
        }
    }
    
    
    /**
     * Returns the version string. This is NOT guaranteed to be a number.
     * @return the PLCC version
     */
    public static String getVersion() {
        return("0.98.3");
    }
    
    
    /**
     * Returns the application tag that is printed as a prefix for all output lines.
     * @return the apptag
     */
    public static String getApptag() {
        return("[PLCC] ");
    }
    
    
    /**
     * Returns the default config file location. Note that this may or may not be in use atm. Use getConfigFile() instead if you
     * need the file that is currently in use.
     * @return the path as a String
     */
    public static String getDefaultConfigFilePath() {
        return(DEFAULT_FILE.getAbsolutePath());
    }
    
    
    /**
     * Creates an array of the output image formats for protein graphs which are set in the settings.
     * @return the output formats, collected from settings like 'plcc_B_img_output_format_PNG'
     */
    public static IMAGEFORMAT[] getProteinGraphOutputImageFormats() {
        ArrayList<IMAGEFORMAT> formats = new ArrayList<>();
        
        if(getBoolean("plcc_B_img_output_format_PNG")) {
            formats.add(IMAGEFORMAT.PNG);
        }
        if(getBoolean("plcc_B_img_output_format_PDF")) {
            formats.add(IMAGEFORMAT.PDF);
        }
        // --- ignore SVG because it is always produced ---
        //if(Settings.getBoolean("plcc_B_img_output_format_SVG")) {
        //    formats.add(IMAGEFORMAT.SVG);
        //}
        
        return (IMAGEFORMAT[])formats.toArray(new IMAGEFORMAT[formats.size()]);
    }
    
    /**
     * Creates an array of the output image formats for aa graphs which are set in the settings.
     * @return the output formats, collected from settings like 'plcc_B_img_AAG_output_format_PNG'
     */
    public static IMAGEFORMAT[] getAminoAcidGraphOutputImageFormats() {
        ArrayList<IMAGEFORMAT> formats = new ArrayList<>();
        
        if(getBoolean("plcc_B_img_AAG_output_format_PNG")) {
            formats.add(IMAGEFORMAT.PNG);
        }
        if(getBoolean("plcc_B_img_AAG_output_format_PDF")) {
            formats.add(IMAGEFORMAT.PDF);
        }
       
        
        return (IMAGEFORMAT[])formats.toArray(new IMAGEFORMAT[formats.size()]);
    }
    
    /**
     * Creates an array of the output image formats for folding graphs which are set in the settings.
     * @return the output formats, collected from settings like 'plcc_B_img_FG_output_format_PNG'
     */
    public static IMAGEFORMAT[] getFoldingGraphOutputImageFormats() {
        ArrayList<IMAGEFORMAT> formats = new ArrayList<>();
        
        if(getBoolean("plcc_B_img_FG_output_format_PNG")) {
            formats.add(IMAGEFORMAT.PNG);
        }
        if(getBoolean("plcc_B_img_FG_output_format_PDF")) {
            formats.add(IMAGEFORMAT.PDF);
        }
        // --- ignore SVG because it is always produced ---
        //if(Settings.getBoolean("plcc_B_img_FG_output_format_SVG")) {
        //    formats.add(IMAGEFORMAT.SVG);
        //}
        
        return (IMAGEFORMAT[])formats.toArray(new IMAGEFORMAT[formats.size()]);
    }
    
    /**
     * Creates an array of the output image formats for complex graphs which are set in the settings.
     * @return the output formats, collected from settings like 'plcc_B_img_CG_output_format_PNG'
     */
    public static IMAGEFORMAT[] getComplexGraphOutputImageFormats() {
        ArrayList<IMAGEFORMAT> formats = new ArrayList<IMAGEFORMAT>();
        
        if(getBoolean("plcc_B_img_CG_output_format_PNG")) {
            formats.add(IMAGEFORMAT.PNG);
        }
        if(getBoolean("plcc_B_img_CG_output_format_PDF")) {
            formats.add(IMAGEFORMAT.PDF);
        }
        // --- ignore SVG because it is always produced ---
        //if(Settings.getBoolean("plcc_B_img_CG_output_format_SVG")) {
        //    formats.add(IMAGEFORMAT.SVG);
        //}
        
        return (IMAGEFORMAT[])formats.toArray(new IMAGEFORMAT[formats.size()]);
    }
    
    
    
    // ### simple getter and setter
    public static int getNumLoadedSettings() { return numLoadedSettings; }
    public static Boolean getCreatedDefaultFile() { return createdDefaultFile; }
}
