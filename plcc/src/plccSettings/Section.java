/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Jan Niclas Wolf 2020. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author jnw
 */

package plccSettings;

import java.util.ArrayList;
import tools.DP;

/**
 *
 * @author jnw
 */
public class Section {
    
    String name;  // name of the section
    String type;  // either user, advanced or developer
    ArrayList<Setting> settings;  // the settings that are part of this section
    
    static final ArrayList<String> ALLOWED_TYPES = new ArrayList<String>() {
        {
            add("user");
            add("advanced");
            add("developer");
        }
    };
    
    /**
     * Creates a new settings section-
     * @param name
     * @param type either user, advanced or developer
     */
    public Section(String name, String type) {
        this.name = name;
        
        // check that data type is allowed to avoid sloppy programming
        if (ALLOWED_TYPES.contains(type)) {
            this.type = type;
        } else {
            DP.getInstance().e(Settings.PACKAGE_TAG, "Creating the setting '" + name + " failed, because the data type is not allowed or correctly formatted. "
                    + "This is an error in the code, please inform the developer of this software. Exiting now.");
            this.type = "user";  // for the IDE
            System.exit(1);
        }
        
        this.type = type;
        
        init();
    }
    
    
    /**
     * Initializes a settings section, i.e., creates the corresponding settings.
     * Should only be called when the name of the section is set.
     */
    private void init() {
        settings = new ArrayList<>();
        switch(name) {
            case "General settings":
                settings.add(new Setting("plcc_B_use_mmCIF_parser", 'B', "false", "Whether the mmCIF parser should be used on the provided PDB coordinates file."));
                settings.add(new Setting("plcc_S_output_dir", 'S', ".", "Output directory for all created files."));
                settings.add(new Setting("plcc_B_calc_draw_graphs", 'B', "true", "Whether graphs are computed and output."));
                settings.add(new Setting("plcc_B_draw_graphs", 'B', "true", "Whether graphs are output."));
                settings.add(new Setting("plcc_B_force_chain", 'B', "false", "Whether to force parsing and processing only a certain PDB chain."));
                settings.add(new Setting("plcc_S_forced_chain_id", 'S', "A", "The forced chain ID, only used when plcc_B_force_chain is true."));
            // TODO add rest
        }
    }
}
