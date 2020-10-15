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
                settings.add(new Setting("plcc_B_draw_graphs", 'B', "true", "Whether graph visualizations are output."));
                settings.add(new Setting("plcc_B_force_chain", 'B', "false", "Whether to force parsing and processing only a certain PDB chain."));
                settings.add(new Setting("plcc_S_forced_chain_id", 'S', "A", "The forced chain ID, only used when plcc_B_force_chain is true."));
            case "Amino Acid Graphs (AAG)":
                settings.add(new Setting("plcc_B_draw_aag", 'B', "false", "Whether amino acid graph visualizations are output. Uses grid visualization."));
                settings.add(new Setting("plcc_B_aminoacidgraphs_include_ligands", 'B', "false", "Whether amino acid graphs should include ligands."));
                settings.add(new Setting("plcc_B_quit_after_aag", 'B', "false", "Whether to quit the program after computation of amino acid graphs."));
                settings.add(new Setting("plcc_B_img_AAG_output_format_PNG", 'B', "false", "Whether to write amino acid graph visualizations in PNG format."));
                settings.add(new Setting("plcc_B_img_AAG_output_format_PDF", 'B', "false", "Whether to write amino acid graph visualizations in PDF format."));
                // Removed: Currently not used as SVG is base for conversion to other file formats and therefore always created.
                //settings.add(new Setting("plcc_B_img_AAG_output_format_SVG", 'B', "true", "Whether to write amino acid graph visualizations in PDF format. Note that this setting currently has no effect, SVG is always generated. The other formats get converted from the SVG."));
            case "Protein Graph (PG)":
                settings.add(new Setting("plcc_B_skip_empty_chains", 'B', "true", "Whether to skip chains without any DSSP SSEs (i.e., contain only ligands) in PGs."));
                settings.add(new Setting("plcc_B_img_output_format_PNG", 'B', "true", "Whether to write protein graph visualizations in PNG format."));
                settings.add(new Setting("plcc_B_img_output_format_PDF", 'B', "false", "Whether to write protein graph visualizations in PDF format."));
                // Removed: Currently not used as SVG is base for conversion to other file formats and therefore always created.
                //settings.add(new Setting("plcc_B_img_output_format_SVG", 'B', "true", "Whether to write protein graph visualizations in SVG format. Note that this setting currently has no effect, SVG is always generated. The other formats get converted from the SVG."));
            // TODO add rest
        }
    }
}
