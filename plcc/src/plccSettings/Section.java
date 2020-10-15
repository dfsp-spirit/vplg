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
                break;
                
            case "Amino Acid Graphs (AAG)":
                settings.add(new Setting("plcc_B_draw_aag", 'B', "false", "Whether Amino Acid Graph visualizations are output. Uses grid visualization."));
                settings.add(new Setting("plcc_B_aminoacidgraphs_include_ligands", 'B', "false", "Whether Amino Acid Graphs should include ligands."));
                settings.add(new Setting("plcc_B_quit_after_aag", 'B', "false", "Whether to quit the program after computation of Amino Acid Graphs."));
                settings.add(new Setting("plcc_B_img_AAG_output_format_PNG", 'B', "false", "Whether to write Amino Acid Graph visualizations in PNG format."));
                settings.add(new Setting("plcc_B_img_AAG_output_format_PDF", 'B', "false", "Whether to write Amino Acid graph visualizations in PDF format."));
                // Removed: Currently not used as SVG is base for conversion to other file formats and therefore always created.
                //settings.add(new Setting("plcc_B_img_AAG_output_format_SVG", 'B', "true", "Whether to write amino acid graph visualizations in PDF format. Note that this setting currently has no effect, SVG is always generated. The other formats get converted from the SVG."));
                break;
            
            case "Protein Graph (PG)":
                settings.add(new Setting("plcc_B_skip_empty_chains", 'B', "true", "Whether to skip chains without any DSSP SSEs (i.e., contain only ligands) in Protein Graphs."));
                settings.add(new Setting("plcc_B_img_output_format_PNG", 'B', "true", "Whether to write Protein Graph visualizations in PNG format."));
                settings.add(new Setting("plcc_B_img_output_format_PDF", 'B', "false", "Whether to write Protein Graph visualizations in PDF format."));
                // Removed: Currently not used as SVG is base for conversion to other file formats and therefore always created.
                //settings.add(new Setting("plcc_B_img_output_format_SVG", 'B', "true", "Whether to write protein graph visualizations in SVG format. Note that this setting currently has no effect, SVG is always generated. The other formats get converted from the SVG."));
                break;
            
            case "Graph types":
                settings.add(new Setting("plcc_B_graphtype_albe", 'B', "true", "Whether alpha-beta graphs are output."));
                settings.add(new Setting("plcc_B_graphtype_albelig", 'B', "true", "Whether alpha-beta graphs with ligands are output."));
                settings.add(new Setting("plcc_B_graphtype_alpha", 'B', "true", "Whether alpha graphs are output."));
                settings.add(new Setting("plcc_B_graphtype_alphalig", 'B', "true", "Whether alpha graphs with ligands are output."));
                settings.add(new Setting("plcc_B_graphtype_beta", 'B', "true", "Whether beta graphs are output."));
                settings.add(new Setting("plcc_B_graphtype_betalig", 'B', "true", "Whether beta graphs with ligands are output."));
                break;
                
            case "Folding Graph (FG)":
                settings.add(new Setting("plcc_B_folding_graphs", 'B', "false", "Whether Folding Graphs (connected components of a Protein Graph) are computed. This does NOT mean they are drawn."));
                settings.add(new Setting("plcc_B_draw_folding_graphs", 'B', "true", "Whether Folding Graphs visualizations are output."));
                settings.add(new Setting("plcc_B_img_FG_output_format_PNG", 'B', "true", "Whether to write Folding Graph visualizations in PNG format."));
                settings.add(new Setting("plcc_B_img_FG_output_format_PDF", 'B', "false", "Whether to write Folding Graph visualizations in PDF format."));
                // Removed: Currently not used as SVG is base for conversion to other file formats and therefore always created.
                //settings.add(new Setting("plcc_B_img_FG_output_format_SVG", 'B', "true", "Whether to write Folding Graph visualizations in PNG format. Note that this setting currently has no effect, SVG is always generated. The other formats get converted from the SVG."));
                settings.add(new Setting("plcc_B_foldgraphtype_KEY", 'B', "true", "Whether KEY notation of Folding Graphs is computed and output (only applies if 'plcc_B_folding_graphs' is 'true')."));
                settings.add(new Setting("plcc_B_foldgraphtype_ADJ", 'B', "true", "Whether ADJ notation of Folding Graphs is computed and output (only applies if 'plcc_B_folding_graphs' is 'true')."));
                settings.add(new Setting("plcc_B_foldgraphtype_RED", 'B', "true", "Whether RED notation of Folding Graphs is computed and output (only applies if 'plcc_B_folding_graphs' is 'true')."));
                settings.add(new Setting("plcc_B_foldgraphtype_SEQ", 'B', "true", "Whether SEQ notation of Folding Graphs is computed and output (only applies if 'plcc_B_folding_graphs' is 'true')."));
                settings.add(new Setting("plcc_B_foldgraphtype_DEF", 'B', "false", "Whether DEF notation of Folding Graphs is computed and output (only applies if 'plcc_B_folding_graphs' is 'true')."));
                break;
                
            case "Complex Graph (CG)":
                settings.add(new Setting("plcc_B_complex_graphs", 'B', "false", "Whether Complex Graphs are computed and drawn."));
                settings.add(new Setting("plcc_I_cg_contact_threshold", 'I', "1", "The lowest number of interchain residue contacts where an edge in Complex Graphs is drawn."));
                settings.add(new Setting("plcc_B_img_CG_output_format_PNG", 'B', "true", "Whether to write Complex Graph visualizations in PNG format."));
                settings.add(new Setting("plcc_B_img_CG_output_format_PDF", 'B', "false", "Whether to write Complex Graph visualizations in PDF format."));
                // Removed: Currently not used as SVG is base for conversion to other file formats and therefore always created.
                //settings.add(new Setting("plcc_B_img_CG_output_format_SVG", 'B', "true", "Whether to write Complex Graph visualizations in SVG format.  Note that this setting currently has no effect, SVG is always generated. The other formats get converted from the SVG."));

            case "Structure visualization":
                settings.add(new Setting("plcc_B_Jmol_graph_vis_commands", 'B', "true", "Whether to compute and print Jmol commands to visualize Protein Graphs in 3D."));
                settings.add(new Setting("plcc_B_Jmol_graph_vis_resblue_commands", 'B', "false", "Whether to compute and print Jmol commands to color the residues of Protein Graphs blue in 3D."));
            
            case "Prints / Error handling":
                settings.add(new Setting("plcc_B_silent", 'B', "false", "Whether to suppress all output. Cluster mode, not recommended for normal usage."));
                settings.add(new Setting("plcc_B_only_essential_output", 'B', "true", "Whether to print only high-level status information."));
                settings.add(new Setting("plcc_B_print_contacts", 'B', "false", "Whether to print residue contacts to stdout (slower)"));
                settings.add(new Setting("plcc_B_no_warn", 'B', "false", "Whether to suppress all warnings."));
                settings.add(new Setting("plcc_B_no_parse_warn", 'B', "true", "Whether to suppress all warnings related to parsing of atoms and other data from the PDB and DSSP input files."));
                settings.add(new Setting("plcc_B_no_not_impl_warn", 'B', "true", "Whether to suppress all warnings related to not implemented function."));
                settings.add(new Setting("plcc_B_no_chain_break_info", 'B', "false", "Whether to suppress chain break info while parsing DSSP file (handy for some DSSP files for CIF data)."));
                settings.add(new Setting("plcc_B_print_silent_notice", 'B', "true", "Whether to print a single line informing the user that silent mode is set in silent mode (includes PDB ID of current file)."));
                settings.add(new Setting("plcc_B_warn_cfg_fallback_to_default", 'B', "true", "Whether to print warnings when a setting is not defined in the config file and internal defaults are used."));
                settings.add(new Setting("plcc_B_split_dsspfile_warning", 'B', "false", "Whether to show a warning about splitting the DSSP file when multiple models are detected in a PDB file."));
                
            case "Performance":
                settings.add(new Setting("plcc_B_separate_contacts_by_chain", 'B', "false", "Whether to compute atom contacts separated by chain. Faster but does not detect contacts between different chains and thus cannot be used for Complex Graph computation."));
                settings.add(new Setting("plcc_F_abort_if_pdb_resolution_worse_than", 'F', "10.0", "Abort all processing of PDB files with worse resolution than provided "
                        + "with this setting in Angstroem as a float. If set to 20.0, the program will terminate and NOT further process a PDB file which is detected to have a resolution of more than 20 A. "
                        + "Set to a negative value like -1.0 to disable this, and thus parse all PDB files, no matter the resolution."));
                settings.add(new Setting("plcc_I_abort_if_num_molecules_below", 'I', "30", "Abort all processing of PDB files with too few molecules as defined by the given value of the setting as integer. "
                        + "If set to 30, the program will terminate and NOT further process a PDB file which is detected to have less than 30 molecules. "
                        + "Set to a negative value like -1 to disable this, and thus parse all PDB files, no matter the molecule count."));
                settings.add(new Setting("plcc_B_skip_too_large", 'B', "false", "Whether to abort if the protein has more than 'plcc_I_skip_num_atoms_threshold' atoms."));
                settings.add(new Setting("plcc_I_skip_num_atoms_threshold", 'I', "80000", "The maximal number of atoms per PDB file if 'plcc_B_skip_too_large' is true. In that case, PLCC will abort for PDB files with more atoms."));
                settings.add(new Setting("plcc_B_chain_spheres_speedup", 'B', "true", "Whether to use contact computation speedup based on comparison of chain spheres."));
                settings.add(new Setting("plcc_B_centroid_method", 'B', "true", "Whether to use centroid of atoms instead of C_alpha for contact computation. Recommended use only with plcc_B_chain_spheres_speedup."));
                settings.add(new Setting("plcc_S_temp_dir", 'S', ".", "The directory where temporary files can be created. You need write access to it, of course."));
                
            // TODO add rest
                
            default:
                DP.getInstance().e(Settings.PACKAGE_TAG, "Settings section with name '" + name + "' not implemented. Please contact a developer.");
        }
    }
}
