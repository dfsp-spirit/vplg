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
        switch(name) {
            case "dummy":
                // init corresponding settings
        }
    }
}
