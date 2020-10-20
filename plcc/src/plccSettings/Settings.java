/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Jan Niclas Wolf 2020. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author jnw
 */

package plccSettings;

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
    static ArrayList<Section> sections = initSections();  // holds the setting sections in their correct order
    static HashMap<String, String> mapSettingsSection;  // maps the settings names (=keys) to the section they are in
    
    static final String PACKAGE_TAG = "SETTINGS";
    static final String FS = System.getProperty("file.separator");
    static private final File DEFAULT_FILE = new File(System.getProperty("user.home") + FS  + "plcc_settings.txt");

    /**
     * Creates all the settings sections.
     * @return settings sections.
     */
    static private ArrayList<Section> initSections() {
        ArrayList<Section> tmpSections = new ArrayList<>();
        
        // add new sections here
        // USER SECTIONS
        tmpSections.add(new Section("General settings", "User"));
        tmpSections.add(new Section("Amino Acid Graphs (AAG)", "User"));
        tmpSections.add(new Section("Protein Graph (PG)", "User"));
        tmpSections.add(new Section("Graph types", "User"));
        tmpSections.add(new Section("Folding Graph (FG)", "User"));
        tmpSections.add(new Section("Complex Graph (CG)", "User"));
        tmpSections.add(new Section("Structure visualization", "User"));
        
        // ADVANCED SECTIONS
        tmpSections.add(new Section("Prints / Error handling", "Advanced"));
        tmpSections.add(new Section("Performance", "Advanced"));
        tmpSections.add(new Section("Parser", "Advanced"));
        tmpSections.add(new Section("Secondary Structure Element (SSE)", "Advanced"));
        tmpSections.add(new Section("SSE orientation", "Advanced"));
        tmpSections.add(new Section("Contact definition", "Advanced"));
        tmpSections.add(new Section("AAG", "Advanced"));
        tmpSections.add(new Section("Database (DB) connection", "Advanced"));
        tmpSections.add(new Section("DB settings", "Advanced"));
        tmpSections.add(new Section("DB structure search", "Advanced"));
        tmpSections.add(new Section("FG", "Advanced"));
        tmpSections.add(new Section("PG", "Advanced"));
        tmpSections.add(new Section("CG", "Advanced"));
        tmpSections.add(new Section("Motifs", "Advanced"));
        tmpSections.add(new Section("Cluster mode", "Advanced"));
        tmpSections.add(new Section("DB: representative chains", "Advanced"));
        tmpSections.add(new Section("Image settings", "Advanced"));
        tmpSections.add(new Section("Output settings", "Advanced"));
        tmpSections.add(new Section("DB: similarity search", "Advanced"));        
        
        // DEVELOPER SECTIONS
        tmpSections.add(new Section("Debug", "Developer"));
        tmpSections.add(new Section("Disabled", "Developer"));
        tmpSections.add(new Section("Alternate AAG", "Developer"));
        
        return tmpSections;
    }
    
    
    public static void init() {
        // read settings in if existing and create otherwise
        if (io.IO.fileExistsIsFileAndCanRead(DEFAULT_FILE)) {
            Properties readProperties = io.IO.readPropertiesFromFile(DEFAULT_FILE.getAbsolutePath());
            Enumeration<String> enumer = (Enumeration<String>) readProperties.propertyNames();  // seems we have to create it outside the loop head
            while (enumer.hasMoreElements()) {
                // TODO set values
            }
        } else {
            // create default settings file
            if (io.IO.writeStringToFile(asFormattedString(), DEFAULT_FILE.getAbsolutePath(), false) != true) {
                DP.getInstance().w(PACKAGE_TAG, "Did not found default file and could not create it at '" + DEFAULT_FILE.getAbsolutePath() + "'. " +
                        "Trying to go on.");
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
                settingsStr += "# " + tmpSetting.documentation + "\n";  // documentation
                settingsStr += "# Default: '" + tmpSetting.getDefaultValue() + "'\n";  // default value
                settingsStr += tmpSetting.name + "=" + tmpSetting.getValue() + "\n\n";  // key-value pair
            }
        }
        
        return settingsStr;
    }
    
    
    static public void setOverwrittenValue(String key, String value) {
        
    }
}
