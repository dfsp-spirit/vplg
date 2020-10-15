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
        tmpSections.add(new Section("General settings", "user"));
        tmpSections.add(new Section("Amino Acid Graphs (AAG)", "user"));
        tmpSections.add(new Section("Protein Graph (PG)", "user"));
        tmpSections.add(new Section("Graph types", "user"));
        tmpSections.add(new Section("Folding Graph (FG)", "user"));
        tmpSections.add(new Section("Complex Graph (CG)", "user"));
        tmpSections.add(new Section("Structure visualization", "user"));
        
        return tmpSections;
    }
}
