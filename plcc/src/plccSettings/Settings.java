/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Jan Niclas Wolf 2020. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author jnw
 */

package plccSettings;

import java.util.ArrayList;

/**
 *
 * @author jnw
 */
public class Settings {
    static ArrayList<Section> sections = initSections();  // holds the setting sections in their correct order
    
    static final String PACKAGE_TAG = "SETTINGS";

    /**
     * Creates all the settings sections.
     * @return settings sections.
     */
    static private ArrayList<Section> initSections() {
        ArrayList<Section> tmpSections = new ArrayList<>();
        
        // add new sections here
        // USER SECTIONS
        tmpSections.add(new Section("General settings", "user"));
        tmpSections.add(new Section("Amino Acid Graphs (AAG)", "user"));
        tmpSections.add(new Section("Protein Graph (PG)", "user"));
        tmpSections.add(new Section("Graph types", "user"));
        tmpSections.add(new Section("Folding Graph (FG)", "user"));
        tmpSections.add(new Section("Complex Graph (CG)", "user"));
        tmpSections.add(new Section("Structure visualization", "user"));
        
        // ADVANCED SECTIONS
        tmpSections.add(new Section("Prints / Error handling", "advanced"));
        tmpSections.add(new Section("Performance", "advanced"));
        tmpSections.add(new Section("Parser", "advanced"));
        tmpSections.add(new Section("Secondary Structure Element (SSE)", "advanced"));
        tmpSections.add(new Section("SSE orientation", "advanced"));
        tmpSections.add(new Section("Contact definition", "advanced"));
        tmpSections.add(new Section("AAG", "advanced"));
        tmpSections.add(new Section("Database (DB) connection", "advanced"));
        tmpSections.add(new Section("DB settings", "advanced"));
        tmpSections.add(new Section("DB structure search", "advanced"));
        tmpSections.add(new Section("FG", "advanced"));
        tmpSections.add(new Section("PG", "advanced"));
        tmpSections.add(new Section("CG", "advanced"));
        tmpSections.add(new Section("Motifs", "advanced"));
        tmpSections.add(new Section("Cluster mode", "advanced"));
        tmpSections.add(new Section("DB: representative chains", "advanced"));
        tmpSections.add(new Section("Image settings", "advanced"));
        tmpSections.add(new Section("Output settings", "advanced"));
        tmpSections.add(new Section("DB: similarity search", "advanced"));        
        
        // DEVELOPER SECTIONS
        tmpSections.add(new Section("Debug", "developer"));
        tmpSections.add(new Section("Disabled", "developer"));
        tmpSections.add(new Section("Alternate AAG", "developer"));
        
        return tmpSections;
    }
}
