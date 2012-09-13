/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package plcc;

// imports
import java.util.*;
import java.io.*;
import java.nio.channels.*;
import java.util.Locale;
//import java.net.*;
//import org.jgrapht.*;
//import org.jgrapht.graph.*;
//import org.jgrapht.alg.ConnectivityInspector;

import java.util.*;
import java.io.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
//import java.awt.*;
import java.awt.image.*;
import java.io.*;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import similarity.CompareOneToDB;
import similarity.Similarity;


/**
 * This is the Main class of plcc.
 *
 * The program 'plcc' (Protein ligand contact calculation) does a bit more than its name implies. It reads a PDB file
 * and calculates the contacts between all protein residues, treating their atoms as solid spheres with a radius of 2
 * Angstroem. It also calculates the contacts of HET groups (potential ligands) with the protein, but ignores stuff
 * like water (HOH, DOD), artificial atoms (Q) and solvent like methanol. Hydrogen atoms are also ignored if present
 * in the PDB file.
 *
 * If told so via command line parameters, the program also calculates the secondary structure elements (SSEs) based
 * on the results of the DSSP algorithm (the program 'dsspcmbi', see Kabsch & Sander 1983, 'Dictionary of protein secondary
 * structure: pattern recognition of hydrogen-bonded and geometrical features') and the contacts between them.
 *
 * It then creates a graph of the SSE connections, similar to the description in Koch 1997, 'Ein graphentheoretischer Ansatz
 * zum paarweisen und multiplen Vergleich von Proteinstrukturen' and draws it.
 *
 */

public class Main {


    // declare class vars

    // the array used to store statistics on contacts between the different AA types
    static Integer NUM_AAs = 20 + 1 + 1;    // These are 20 real AAs, 1 extra for ligands and 1 for the total number count at index 0 (1st AA starts at index 1).
    static Integer MAX_ATOMS_PER_AA = 15;   // As everybody knows, TRP is the AA with most atoms and
                                            //  has 27 of them, but we ignore 'H' atoms (they are not
                                            //  included in most PDB files and if so, we filter them out.
                                            //  So only 14 of those atoms remain (but index starts at 1).

    static Integer[][][][] contact;

    static Integer globalMaxCenterSphereRadius;


    // This is required for the additional speedup during the calculation of residue contacts that
    //  allows us to skip the next few residues if the distance between sequential neighbor residues
    //  is large (I. Koch). It is set by setGlobalMaxSeqNeighborResDist() and used by the function
    //  Main.calculateAllContacts().
    static Integer globalMaxSeqNeighborResDist;
    
    static final String version = Settings.getVersion();
    
    static ArrayList<File> deleteFilesOnExit;
    
    /** Whether the PDB file name given on the command line is used. This is not the case for command lines which only operate on the database or which need no input file (e.g., --recreate-tables). */
    static Boolean useFileFromCommandline = true;






    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        System.out.println("[======================== plcc -- Protein-Ligand Contact Calculation ========================]");
        System.out.println("Init... (Version " +  version + ")");
        
        // *************************************************** load default settings from config file *************************************

        // The settings are defined in the Settings class. They are loaded from the config file below and can then be overwritten
        //  by command line arguments.
        Settings.init();
        
        // Check library dir, warn if not there
        File libDir = new File("lib");
        if( ! libDir.exists()) {
            System.err.println("WARNING: Library directory '" + libDir.toString() + "' not found. Libraries missing, will crash if functionality of them is required during this run.");
            System.err.println("WARNING: Was this program executed from its installation directory? If not you need to copy the lib/ directory to the working directory.");
            System.err.println("INFO: The Java library path is set to: '" + System.getProperty("java.library.path") + "'.");
            System.err.println("INFO: The Java classloader path is set to: '" + System.getProperty("java.class.path") + "'.");
        }

        if(Settings.load("")) {             // Empty string means that the default file of the Settings class is used
            //System.out.println("  Settings loaded from properties file.");
        }
        else {
            System.err.println("WARNING: Could not load settings from properties file, trying to create it.");
            if(Settings.createDefaultConfigFile()) {
                System.out.println("  Default config file created, will use it from now on.");
            } else {
                System.err.println("WARNING: Could not create default config file, check permissions. Using internal default settings.");
            }
            Settings.resetAll();        // init settings with internal defaults for this run
        }

        //Settings.printAll();


        String fs = System.getProperty("file.separator");

        

        // ****************************************************    declarations    **********************************************************

        ArrayList<Model> models = new ArrayList<Model>();
        ArrayList<Chain> chains = new ArrayList<Chain>();
        ArrayList<Residue> residues = new ArrayList<Residue>();
        ArrayList<Atom> atoms = new ArrayList<Atom>();
        ArrayList<SSE> dsspSSEs = new ArrayList<SSE>();
        ArrayList<SSE> ptglSSEs = new ArrayList<SSE>();
        ArrayList<String> allModelsIDsOfWholePDBFile = new ArrayList<String>();
        deleteFilesOnExit = new ArrayList<File>();
        
        String pdbid = "";
        String dsspFile = "";
        String dsspLigFile = "";
        String pdbFile = "";
        String outputDir = ".";
        String pdbIdDotGeoFile = "";
        String pdbIdDotGeoLigFile = "";
        String conDotSetFile = "";
        String chainsFile = "";
        String ligandsFile = "";
        String modelsFile = "";
        String resMapFile = "";
        
        Boolean compareResContacts = false;
        String compareResContactsFile = "";
        Boolean compareSSEContacts = false;
        String compareSSEContactsFile = "";                


        ArrayList<ResContactInfo> cInfo;
        
        // init contact statistics array
        contact = new Integer[NUM_AAs][NUM_AAs][MAX_ATOMS_PER_AA][MAX_ATOMS_PER_AA];
        for(Integer i = 0; i < NUM_AAs; i++) {
            for(Integer j = 0; j < NUM_AAs; j++) {
                for(Integer k = 0; k < MAX_ATOMS_PER_AA; k++) {
                    for(Integer l = 0; l < MAX_ATOMS_PER_AA; l++) {
                        contact[i][j][k][l] = 0;
                    }
                }
            }
        }




        // ****************************************************    parse args    **********************************************************
        if(args.length > 0) {

            if(args[0].equals("-h") || args[0].equals("--help")) {
                usage();
                System.exit(0);
            }

            // get pdbid from first arg
            pdbid = args[0];

            Integer expectedLengthPDBID = 4;
            if(pdbid.length() != expectedLengthPDBID) {
                System.err.println("WARNING: The given PDB identifier '" + pdbid + "' has an unusual length of " + pdbid.length() + " characters, expected " + expectedLengthPDBID + ".");
            }
            
            // set default file names from the pdb id (these may be overwritten by args later)
            pdbFile = pdbid + ".pdb";
            dsspFile = pdbid + ".dssp";

            // parse the rest of the arguments, if any
            if(args.length > 1) {

                for (Integer i = 1; i < args.length; i++) {

                    String s = args[i];

                    if(s.equals("-h") || s.equals("--help")) {
                        usage();
                        System.exit(0);
                    }

                    if(s.equals("-d") || s.equals("--dsspfile")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            dsspFile = args[i+1];
                        }
                    }
                    
                    if(s.equals("--gz-dsspfile")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            String dsspFilenameGZ = args[i+1];
                            File dsspFileGZ = new File(dsspFilenameGZ);                            
                            File dsspFileUnpacked = null;
                            try {
                                // gunzip the file
                                //String tmpDir = dsspFileGZ.getAbsoluteFile().getParent();
                                String tmpDir = Settings.get("plcc_S_temp_dir");                                
                                dsspFileUnpacked = IO.unGzip(dsspFileGZ, new File(tmpDir));
                                deleteFilesOnExit.add(dsspFileUnpacked);
                                dsspFile = dsspFileUnpacked.toString();
                            }
                            catch(Exception e) {
                                System.err.println("ERROR: Could not extract input DSSP file in gz format at '" + dsspFilenameGZ + "'.");
                                //System.err.println("ERROR: The message was '" + e.getMessage() + "'.");                                
                                System.exit(1);
                            }
                        }
                    }
                    
                    if(s.equals("--gz-pdbfile")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            String pdbFilenameGZ = args[i+1];
                            File pdbFileGZ = new File(pdbFilenameGZ);
                            File pdbFileUnpacked = null;                                                        
                            try {
                                //String tmpDir = pdbFileGZ.getAbsoluteFile().getParent();
                                String tmpDir = Settings.get("plcc_S_temp_dir");
                                //System.out.println("sourcedir='" + sourceDir + "'");
                                // gunzip the file
                                pdbFileUnpacked = IO.unGzip(pdbFileGZ, new File(tmpDir));
                                deleteFilesOnExit.add(pdbFileUnpacked);
                                pdbFile = pdbFileUnpacked.toString();
                            }
                            catch(Exception e) {
                                System.err.println("ERROR: Could not extract input PDB file in gz format at '" + pdbFilenameGZ + "'.");
                                //System.err.println("ERROR: The message was '" + e.getMessage() + "'.");
                                System.exit(1);
                            }
                        }
                    }

                    
                    if(s.equals("-p") || s.equals("--pdbfile")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            pdbFile = args[i+1];
                        }
                    }
                    
                    
                    if(s.equals("-C") || s.equals("--create-config")) {
                        // The config file has already been created before parsing the command line if it did not exist, so we just do nothing here.
                        System.out.println("Tried to create PLCC config file at '" + Settings.getDefaultConfigFilePath() + "' (see above). Exiting.");
                        System.exit(0);
                    }
                    
                    
                    if(s.equals("-L") || s.equals("--lig-filter")) {
                        if(args.length <= i+2 ) {
                            syntaxError("The --lig-filter option requires two arguments, you can set one of them to zero if you want only one border.");
                        }
                        else {
                            Integer min = 0;
                            Integer max = 0;
                            try {
                                min = Integer.parseInt(args[i+1]);
                                max = Integer.parseInt(args[i+2]);                                
                            } catch (Exception e) {
                                syntaxError("The minimum and maximum atom count for ligands have to be numeric / integers.");
                            }
                            
                            if(min > max && max != 0) {
                                syntaxError("Minimum ligand atom number must not be greater than maximum ligand atom number (unless max = 0).");
                            } else {
                                Settings.set("plcc_I_lig_min_atoms", min.toString());
                                Settings.set("plcc_I_lig_max_atoms", max.toString());
                            }
                        }
                    }
                    
                    
                    if(s.equals("-S") || s.equals("--sim-measure")) {
                        System.out.println("Setting similarity measure to " + args[i+1] + ".");
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            Settings.set("plcc_S_search_similar_method", args[i+1]);
                        }
                    }
                    
                    
                    
                    
                    if(s.equals("-M") || s.equals("--similar")) {
                        useFileFromCommandline = false;
                        if(args.length <= i+3 ) {
                            syntaxError();
                        }
                        else {                            
                            Settings.set("plcc_B_search_similar", "true");
                            Settings.set("plcc_B_search_similar_PDBID", args[i+1]);
                            Settings.set("plcc_B_search_similar_chainID", args[i+2]);
                            Settings.set("plcc_S_search_similar_graphtype", args[i+3]);                            
                        }
                    }
                    
                                                                               
                    if(s.equals("-v") || s.equals("--del-db-protein")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            String pdbidToDelete = args[i+1];
                            Integer numRows = 0;
                            System.out.println("Deleting protein with PDB identifier '" + pdbidToDelete + "' from database...");
                            
                            if(DBManager.init(Settings.get("plcc_S_db_name"), Settings.get("plcc_S_db_host"), Settings.getInteger("plcc_I_db_port"), Settings.get("plcc_S_db_username"), Settings.get("plcc_S_db_password"))) {
                                try {
                                    
                                    numRows = DBManager.deletePdbidFromDB(pdbidToDelete);
                                } catch(Exception e) {
                                    System.err.println("ERROR: Deleting protein failed: '" + e.getMessage() + "'. Exiting.");
                                    System.exit(1);
                                }
                            }
                                                        
                            if(numRows > 0) {
                                System.out.println("Protein deleted from database, " + numRows + " rows affected. Exiting.");
                            }
                            else {
                                System.out.println("Protein was not in the database, " + numRows + " rows affected. Exiting.");
                            }
                            System.exit(0);
                        }
                    }
                    
                    
                    if(s.equals("-j") || s.equals("--ddb")) {
                        if(args.length <= i+4 ) {
                            syntaxError();
                        }
                        else {
                            Settings.set("plcc_B_useDB", "true");
                            String a_pdbid = args[i+1];
                            String a_chain = args[i+2];
                            String a_gt = args[i+3];
                            String a_outFile = args[i+4];
                                                        
                            System.out.println("Retrieving " + a_gt + " graph of PDB entry " + a_pdbid + ", chain " + a_chain + " from database.");
                            
                            if(DBManager.init(Settings.get("plcc_S_db_name"), Settings.get("plcc_S_db_host"), Settings.getInteger("plcc_I_db_port"), Settings.get("plcc_S_db_username"), Settings.get("plcc_S_db_password"))) {
                                drawPlccGraphFromDB(a_pdbid, a_chain, a_gt, a_outFile + Settings.get("plcc_S_img_output_fileext"), false);
                                System.out.println("Handled " + a_gt + " graph of PDB entry " + a_pdbid + ", chain " + a_chain + ", exiting.");
                                System.exit(0);
                            }
                            else {
                                System.exit(1);
                            }                                                                                    
                        }
                    }
                    
                    
                    if(s.equals("-x") || s.equals("--check-rescts")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            compareResContacts = true;
                            compareResContactsFile = args[i+1];
                        }
                    }
                    
                    if(s.equals("-X") || s.equals("--check-ssects")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            Settings.set("plcc_B_debug_compareSSEContacts", "true");
                            Settings.set("plcc_S_debug_compareSSEContactsFile", args[i+1]);
                        }
                    }                                        
                    
                    
                    if(s.equals("-y") || s.equals("--write-geodat")) {
                        Settings.set("plcc_B_ptgl_geodat_output", "true");
                    }
                    
                    if(s.equals("-z") || s.equals("--ramaplot")) {
                        Settings.set("plcc_B_ramachandran_plot", "true");
                    }
                    

                    if(s.equals("-o") || s.equals("--outputdir")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            outputDir = args[i+1];
                            Settings.set("plcc_S_output_dir", outputDir);
                        }
                    }

                    //if(s.equals("-i") || s.equals("--ignore_ligands_geolig")) {
                    //    System.out.println("WARNING: Ignoring of ligands not fully implemented yet. Contacts still include them.");
                    //    System.setProperty("plcc.useLigands", "false");
                    //}

                    if(s.equals("-r") || s.equals("--recreate-tables")) {
                        System.out.println("Recreating DB tables only (-r)...");
                        if(DBManager.init(Settings.get("plcc_S_db_name"), Settings.get("plcc_S_db_host"), Settings.getInteger("plcc_I_db_port"), Settings.get("plcc_S_db_username"), Settings.get("plcc_S_db_password"))) {
                            if(DBManager.dropTables()) {
                                System.out.println("  DB: Tried to drop statistics tables (no error messages => OK).");
                            }
                            if(DBManager.createTables()) {
                                System.out.println("  DB: Tried to create statistics tables (no error messages => OK).");
                            }
                        }
                        else {
                            System.err.println("ERROR: Could not modify tables, DB connection failed.");
                        }
                        // exit
                        System.out.println("Done recreating DB tables, exiting.");
                        System.exit(0);
                    }
                    
                    if(s.equals("-s") || s.equals("--showonscreen")) {
                        Settings.set("plcc_B_print_contacts", "true");
                    }

                    if(s.equals("-a") || s.equals("--include-coils")) {
                        Settings.set("plcc_B_include_coils", "true");
                    }
                    
                    if(s.equals("-B") || s.equals("--force-backbone")) {
                        Settings.set("plcc_B_forceBackboneContacts", "true");
                    }
                   

                    if(s.equals("-w") || s.equals("--dont-write-images")) {
                        Settings.set("plcc_B_draw_graphs", "false");
                    }

                    if(s.equals("-c") || s.equals("--dont-calc-graphs")) {
                        Settings.set("plcc_B_calc_draw_graphs", "false");
                    }
                    
                    if(s.equals("--no-ptgl")) {
                        Settings.set("plcc_B_strict_ptgl_behaviour", "false");
                    }
                    

                    if(s.equals("-u") || s.equals("--use-database")) {
                        Settings.set("plcc_B_useDB", "true");
                    }
                    
                    if(s.equals("-f") || s.equals("--folding-graphs")) {
                        Settings.set("plcc_B_folding_graphs", "true");
                    }
                    
                    if(s.equals("-k") || s.equals("--img-dir-tree")) {
                        Settings.set("plcc_B_output_images_dir_tree", "true");
                    }
                    
                    if(s.equals("-K") || s.equals("--graph-dir-tree")) {
                        Settings.set("plcc_B_output_textfiles_dir_tree", "true");
                    }
                    
                    
                    
                    if(s.equals("-m") || s.equals("--image-format")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            String format = args[i+1].toUpperCase();
                            
                            
                            if((format.equals("PNG"))) {
                                Settings.set("plcc_S_img_output_format", "PNG");
                                Settings.set("plcc_S_img_output_fileext", ".png");
                            }
                            else if((format.equals("JPG"))) {
                                Settings.set("plcc_S_img_output_format", "JPG");
                                Settings.set("plcc_S_img_output_fileext", ".jpg");
                            }
                            else if((format.equals("TIF"))) {
                                Settings.set("plcc_S_img_output_format", "TIF");
                                Settings.set("plcc_S_img_output_fileext", ".tif");
                            }
                            else {
                                System.err.println("ERROR: Requested image output format '" + format + "' invalid. Use 'PNG' or 'SVG' for bitmap or vector output.");
                                syntaxError();
                            }                            
                        }
                    }
                    
                    
                    if(s.equals("--contact-level-debugging")) {                                                                        
                        Settings.set("plcc_B_contact_debug_dysfunct", "true");
                    }
                    
                    

                    if(s.equals("-n") || s.equals("--textfiles")) {
                        Settings.set("plcc_B_ptgl_text_output", "true");
                    }
                    
                    if(s.equals("-q") || s.equals("--fg-notations")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            // We want to calculate folding graphs!
                            Settings.set("plcc_B_folding_graphs", "true");
                            
                            // If the user specifies the folding graph types manually, all those
                            // which are NOT listed default to 'off':                                                        
                            Settings.set("plcc_B_foldgraphtype_KEY", "false");
                            Settings.set("plcc_B_foldgraphtype_ADJ", "false");
                            Settings.set("plcc_B_foldgraphtype_RED", "false");
                            Settings.set("plcc_B_foldgraphtype_SEQ", "false");
                            
                            // Now add the listed ones back:
                            String types = args[i+1].toLowerCase();
                            
                            Integer nv = 0; // number of valid folding graph identifiers
                            
                            if(types.contains("k")) { Settings.set("plcc_B_foldgraphtype_KEY", "true"); nv++; }
                            if(types.contains("a")) { Settings.set("plcc_B_foldgraphtype_ADJ", "true"); nv++; }
                            if(types.contains("r")) { Settings.set("plcc_B_foldgraphtype_RED", "true"); nv++; }
                            if(types.contains("s")) { Settings.set("plcc_B_foldgraphtype_SEQ", "true"); nv++; }
                            
                            // sanity check
                            if(nv != types.length()) {
                                System.err.println("WARNING: List of folding graph notations given on command line '" + types + "' contains invalid chars (" + types.length() + " given, " + nv + " valid).");
                                System.err.println("WARNING: Valid chars: 'k' => KEY, 'a' => ADJ, 'r' => RED, 's' => SEQ. Example: '-q kr'");
                                
                                if(nv <= 0) {
                                    syntaxError();
                                }
                            }
                        }
                        
                    }
                    

                    if(s.equals("-g") || s.equals("--sse-graphtypes")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            // We want to calculate SSE graphs!
                            Settings.set("plcc_B_calc_draw_graphs", "true");
                            
                            // If the user specifies the folding graph types manually, all those
                            // which are NOT listed default to 'off':                                                        
                            Settings.set("plcc_B_graphtype_albe", "false");
                            Settings.set("plcc_B_graphtype_albelig", "false");
                            Settings.set("plcc_B_graphtype_alpha", "false");
                            Settings.set("plcc_B_graphtype_alphalig", "false");
                            Settings.set("plcc_B_graphtype_beta", "false");
                            Settings.set("plcc_B_graphtype_betalig", "false");
                            
                            // Now add the listed ones back:
                            String types = args[i+1].toLowerCase();
                            
                            Integer nv = 0; // number of valid folding graph identifiers
                            
                            if(types.contains("a")) { Settings.set("plcc_B_graphtype_alpha", "true"); nv++; }
                            if(types.contains("b")) { Settings.set("plcc_B_graphtype_beta", "true"); nv++; }
                            if(types.contains("c")) { Settings.set("plcc_B_graphtype_albe", "true"); nv++; }
                            if(types.contains("d")) { Settings.set("plcc_B_graphtype_alphalig", "true"); nv++; }
                            if(types.contains("e")) { Settings.set("plcc_B_graphtype_betalig", "true"); nv++; }
                            if(types.contains("f")) { Settings.set("plcc_B_graphtype_albelig", "true"); nv++; }
                            
                            // sanity check
                            if(nv != types.length()) {
                                System.err.println("WARNING: List of folding graph notations given on command line '" + types + "' contains invalid chars (" + types.length() + " given, " + nv + " valid).");
                                System.err.println("WARNING: Valid chars: 'a' => alpha, 'b' => beta, 'c' => albe, 'd' => alphalig, 'e' => betalig, 'f' => albelig. Example: '-g ace'");
                                
                                if(nv <= 0) {
                                    syntaxError();
                                }
                            }
                        }
                        
                    }
                    
                    
                    if(s.equals("-O") || s.equals("--outputformats")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            
                            // If the user specifies the graph output formats manually, all those
                            // which are NOT listed default to 'off':                                                        
                            Settings.set("plcc_B_output_GML", "false");
                            Settings.set("plcc_B_output_TGF", "false");
                            Settings.set("plcc_B_output_DOT", "false");
                            Settings.set("plcc_B_output_kavosh", "false");
                            Settings.set("plcc_B_output_plcc", "false");
                            
                            // Now add the listed ones back:
                            String types = args[i+1].toLowerCase();
                            
                            if(types.equals("x")) {
                                // Do nothing more, x means the user wants none of the formats
                            } else {
                            
                                Integer nv = 0; // number of valid folding graph identifiers

                                if(types.contains("g")) { Settings.set("plcc_B_output_GML", "true"); nv++; }
                                if(types.contains("t")) { Settings.set("plcc_B_output_TGF", "true"); nv++; }
                                if(types.contains("d")) { Settings.set("plcc_B_output_DOT", "true"); nv++; }
                                if(types.contains("k")) { Settings.set("plcc_B_output_kavosh", "true"); nv++; }
                                if(types.contains("p")) { Settings.set("plcc_B_output_plcc", "true"); nv++; }

                                // sanity check
                                if(nv != types.length()) {
                                    System.err.println("WARNING: List of output formats given on command line '" + types + "' contains invalid chars (" + types.length() + " given, " + nv + " valid).");
                                    System.err.println("WARNING: Valid chars: 'g' => GML, 't' => TGF, 'd' => DOT lang, 'k' => kavosh edge list, 'p' => PLCC. Example: '-O tgp'");

                                    if(nv <= 0) {
                                        syntaxError();
                                    }
                                }
                            }
                        }
                        
                    }
                    
                    
                    

                    if(s.equals("--debug") || s.equals("-D")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            Settings.set("plcc_I_debug_level", args[i+1]);
                        }
                    }
                    
                    if(s.equals("--force-chain") || s.equals("-e")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            Settings.set("plcc_B_force_chain", "true");
                            Settings.set("plcc_S_forced_chain_id", args[i+1]);                            
                        }
                    }
                    
                    
                    

                    if(s.equals("-t") || s.equals("--draw-tgf-graph")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            Settings.set("plcc_B_graphimg_header", "false");
                            System.out.println("Drawing custom graph in TGF format from file '" + args[i+1] + "'.");
                            drawTGFGraph(args[i+1], args[i+1] + Settings.get("plcc_S_img_output_fileext"));
                            System.out.println("Done drawing TGF graph, exiting.");
                            System.exit(1);
                        }
                    }
                    
                    if(s.equals("-l") || s.equals("--draw-plcc-graph")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            System.out.println("Drawing protein graph in plcc format from file '" + args[i+1] + "'.");
                            drawPlccGraphFromFile(args[i+1], args[i+1] + Settings.get("plcc_S_img_output_fileext"), false);
                            System.out.println("Handled plcc graph file '" + args[i+1] + "', exiting.");
                            System.exit(1);
                        }
                    }
                    
                    if(s.equals("-b") || s.equals("--draw-plcc-fgs")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            Settings.set("plcc_B_folding_graphs", "true");
                            Settings.set("plcc_B_draw_graphs", "true");                            
                            System.out.println("Drawing protein graph and folding graphs in plcc format from file '" + args[i+1] + "'.");
                            drawPlccGraphFromFile(args[i+1], args[i+1] + Settings.get("plcc_S_img_output_fileext"), true);
                            System.out.println("Handled plcc graph file '" + args[i+1] + " and folding graphs', exiting.");
                            System.exit(1);
                        }
                    }


                } //end for loop
            }

            
        } else {
            usage_short();      // the first argument (pdbid) is required!
            System.exit(0);
        }

        // This check is rather useless and it will break PDB files that were split into multiple files (one for each
        //  model) and renames, e.g. "2kos_1.pdb" for model 1 of protein 2kos. It is therefore disabaled atm.
        //if(pdbid.length() != 4) {
        //    System.err.println("ERROR: pdbid '" + pdbid + "' should be 4 characters long (but is " + pdbid.length() + ").");
        //    System.exit(1);
        //}

        // ****************************************************    test for required files    **********************************************************

        if(Settings.getBoolean("plcc_B_contact_debug_dysfunct")) {
            print_debug_malfunction_warning();            
        }
        
        if(Settings.getInteger("plcc_I_debug_level") > 0) {
            System.out.println("  Debug level set to " + Settings.getInteger("plcc_I_debug_level") + ".");
        }
        
        if(useFileFromCommandline) {
            System.out.println("  Using PDB file '" + pdbFile + "', dssp file '" + dsspFile + "', output directory '" + outputDir + "'.");
        }
        
        if(Settings.getInteger("plcc_I_debug_level") > 0) {
            Settings.writeDocumentedDefaultFile(System.getProperty("user.home") + System.getProperty("file.separator") + ".plcc_example_settings");
        }
        
        
        if(Settings.getBoolean("plcc_B_search_similar")) {
            System.out.println("Searching for proteins similar to PDB ID '" + Settings.get("plcc_B_search_similar_PDBID") + "' chain '" + Settings.get("plcc_B_search_similar_chainID") + "' graph type '" + Settings.get("plcc_S_search_similar_graphtype") + "'.");
            
            if(DBManager.init(Settings.get("plcc_S_db_name"), Settings.get("plcc_S_db_host"), Settings.getInteger("plcc_I_db_port"), Settings.get("plcc_S_db_username"), Settings.get("plcc_S_db_password"))) {
                
                if(Settings.get("plcc_S_search_similar_method").equals(Similarity.SIMILARITYMETHOD_STRINGSSE)) {
                    System.out.println("Using similarity method '" + Settings.get("plcc_S_search_similar_method") + "'.");
                
                    String patternSSEString = null;
                    try {
                        patternSSEString = DBManager.getSSEString(Settings.get("plcc_B_search_similar_PDBID"), Settings.get("plcc_B_search_similar_chainID"), Settings.get("plcc_S_search_similar_graphtype"));
                    } catch (Exception e) {
                        System.err.println("ERROR: DB: Could not retrieve SSE string for requested graph from database, exiting.");
                        System.exit(1);
                    }

                    if(patternSSEString == null) {
                        System.err.println("ERROR: DB: SSE string for requested graph is not in the database, exiting.");
                        System.exit(1);
                    } else {
                        System.out.println("Using pattern SSEstring '" + patternSSEString + "'.");
                    }

                    CompareOneToDB.performSSEStringComparison(patternSSEString);
                } else if (Settings.get("plcc_S_search_similar_method").equals(Similarity.SIMILARITYMETHOD_GRAPHSET)) {
                    System.out.println("Using similarity method '" + Settings.get("plcc_S_search_similar_method") + "'.");
                    CompareOneToDB.performGraphSetComparison();                    
                }
                else if (Settings.get("plcc_S_search_similar_method").equals(Similarity.SIMILARITYMETHOD_GRAPHCOMPAT)) {
                    System.out.println("Using similarity method '" + Settings.get("plcc_S_search_similar_method") + "'.");
                    CompareOneToDB.performGraphCompatGraphComparison();                    
                }
                else {
                    System.err.println("ERROR: Invalid similarity method: '" + Settings.get("plcc_S_search_similar_method") + "'. Use --help for info on valid settings.");
                    System.exit(1);
                }
                
                System.exit(0);
            } else {
                System.err.println("ERROR: Could not connect to DB, exiting.");
                System.exit(1);
            }            
            
        }
                

        File input_file;
        File output_dir;
        
        // pdb file
        input_file = new File(pdbFile);

        if(! (input_file.exists() && input_file.isFile())) {
            System.err.println("ERROR: pdbfile '" + pdbFile + "' not found. Exiting.");
            System.exit(1);
        }

        // dssp file
        input_file = new File(dsspFile);
        if(! (input_file.exists() && input_file.isFile())) {
            System.err.println("ERROR: dsspfile '" + dsspFile + "' not found. Exiting.");
            System.exit(1);
        }

        output_dir = new File(outputDir);
        if(! (output_dir.exists() && output_dir.isDirectory())) {
            System.err.println("ERROR: output directory '" + outputDir + "' not found. Exiting.");
            System.exit(1);
        }


        System.out.println("  Checked required files and directories, looks good.");

        pdbIdDotGeoFile = output_dir + fs + pdbid.toLowerCase() + ".geo";               // holds info on contacts between residues of the PDB file
        pdbIdDotGeoLigFile = output_dir + fs + pdbid.toLowerCase() + ".geolig";         // holds info on contacts between residues + ligands of the PDB file
        conDotSetFile = output_dir + fs + pdbid.toLowerCase() + ".contactstats";        // holds statistics on atom contacts by residue type
        dsspLigFile = output_dir + fs + pdbid.toLowerCase() + ".dssplig";
        chainsFile = output_dir + fs + pdbid.toLowerCase() + ".chains";
        ligandsFile = output_dir + fs + pdbid.toLowerCase() + ".ligands";
        modelsFile = output_dir + fs + pdbid.toLowerCase() + ".models";
        resMapFile = output_dir + fs + pdbid.toLowerCase();         // chain and file extension is added later 

        if(Settings.getBoolean("plcc_B_ptgl_text_output")) {
            System.out.println("  Using output files:\n    * " + pdbIdDotGeoFile + " for contact data\n    * " + pdbIdDotGeoLigFile + " for lig contact data");
            System.out.println("    * " + conDotSetFile + " for contact statistics\n    * " + dsspLigFile + " for DSSP ligand file.");
        }


        // ************************************** check database connection ******************************************        

        if(Settings.getBoolean("plcc_B_useDB")) {
            String plcc_db_name = Settings.get("plcc_S_db_name");
            String plcc_db_host = Settings.get("plcc_S_db_host");
            Integer plcc_db_port = Settings.getInteger("plcc_I_db_port");
            String plcc_db_username = Settings.get("plcc_S_db_username");
            String plcc_db_password = Settings.get("plcc_S_db_password");
            System.out.println("  Checking database connection to host '" + plcc_db_host + "' on port '" + plcc_db_port + "'...");
            if(DBManager.init(plcc_db_name, plcc_db_host, plcc_db_port, plcc_db_username, plcc_db_password)) {
                System.out.println("  -> Database connection OK.");
            }
            else {
                System.out.println("  -> Database connection FAILED.");
                System.err.println("WARNING: Could not establish database connection, not writing anything to the DB.");
                Settings.set("plcc_B_useDB", "false");
            }
        }
        else {
            System.out.println("  Not using the database as requested by options.");
        }

        // **************************************    here we go: parse files and get data    ******************************************
        System.out.println("Getting data...");
        FileParser.initData(pdbFile, dsspFile);

        allModelsIDsOfWholePDBFile = FileParser.getAllModelIDsFromWholePdbFile();

        // print all model IDs from the PDB file (not just the handled model)
        System.out.println("    PDB: Found the following NMR models in the whole PDB file:");
        System.out.print("    PDB:");
        
        if(allModelsIDsOfWholePDBFile.size() > 0) {
        
            for(Integer i = 0; i < allModelsIDsOfWholePDBFile.size(); i++) {
                System.out.print("  " + allModelsIDsOfWholePDBFile.get(i));
            }
            System.out.print("\n");
        }
        else {
            System.out.print(" <None> (No NMR data, an artificial default model was added.)\n");
        }
        
        models = FileParser.getModels();    // doesn't do much anymore since only the PDB lines of model 1 are currently in there
        chains = FileParser.getChains();
        residues = FileParser.getResidues();
        atoms = FileParser.getAtoms();

        // This is now done, separate for each chain, by a function in Main.java below
        // dsspSSEs = FileParser.getDsspSSEs(); 
        // ptglSSEs = FileParser.getPtglSSEs();
        

        if(chains.size() < 1) { System.out.println("WARNING: Input files contain no chains."); }
        if(residues.size() < 1) { System.out.println("WARNING: Input files contain no residues."); }
        if(atoms.size() < 1) { System.out.println("WARNING: Input files contain no atoms."); }



        // DEBUG: print all SSEs
        //System.out.println("Printing all " + SSEs.size() + " SSEs according to DSSP definition:");
        //for(Integer i = 0; i < SSEs.size(); i++) {
        //    System.out.println("  " + SSEs.get(i));
        //}

        
        if(Settings.getBoolean("plcc_B_contact_debug_dysfunct")) {
            // Use fake values to disable residue skipping and prevent contact calculations before the
            //  residue level contact function is called for all residues (less cluttered debug output).
            globalMaxCenterSphereRadius = 1000;
            globalMaxSeqNeighborResDist = 1000;
        }
        else {
            // All residues exist, we can now calculate their maximal center sphere radius
            globalMaxCenterSphereRadius = getGlobalMaxCenterSphereRadius(residues);
            System.out.println("  Maximal center sphere radius for all residues is " + globalMaxCenterSphereRadius + ".");

            // ... and the maximal distance between neighbors in the AA sequence
            globalMaxSeqNeighborResDist = getGlobalMaxSeqNeighborResDist(residues);        
            System.out.println("  Maximal distance between residues that are sequence neighbors is " + globalMaxSeqNeighborResDist + ".");
        }
        // ... and fill in the frequencies of all AAs in this protein.
        getAADistribution(residues);

        System.out.println("Received all data (" + models.size() + " Models, " + chains.size() + " Chains, " + residues.size() + " Residues, " + atoms.size() + " Atoms).");        


        // **************************************    calculate the contacts    ******************************************


        System.out.println("Calculating residue contacts...");

        cInfo = calculateAllContacts(residues);
        
        // DEBUG: compare computed contacts with those from a geom_neo file
        if(compareResContacts) {
            System.out.println("DEBUG: Comparing calculated residue contacts with those in the file '" + compareResContactsFile + "'.");
            FileParser.compareResContactsWithPdbidDotGeoFile(compareResContactsFile, false, cInfo);
        }
        

        System.out.println("Received data on " + cInfo.size() + " residue contacts that have been confirmed on atom level.");
        
        
        // **************************************    output of the results    ******************************************
        
        // print overview to STDOUT
        if(Settings.getBoolean("plcc_B_print_contacts")) {
            System.out.println("Showing contact overview...");
            showContactOverview(cInfo);
        }

        if(Settings.getBoolean("plcc_B_print_contacts")) {
            System.out.println("Showing statistics overview...");
            showContactStatistics(residues.size());
        }
        
        // write the detailed and formated results to the output file
        if(Settings.getBoolean("plcc_B_ptgl_text_output")) {
            System.out.println("Writing residue contact info file...");
            writeContacts(cInfo, pdbIdDotGeoFile, false);

            if(Settings.getBoolean("plcc_B_write_lig_geolig")) {
                System.out.println("Writing full contact info file including ligands...");
                writeContacts(cInfo, pdbIdDotGeoLigFile, true);
            }

            System.out.println("Writing statistics file...");
            writeStatistics(conDotSetFile, pdbid, residues.size());

            // write the dssplig file
            System.out.println("Writing DSSP ligand file...");
            //writeDsspLigFile(dsspFile, dsspLigFile, cInfo, residues);
            writeOrderedDsspLigFile(dsspFile, dsspLigFile, residues);

            // write chains file
            System.out.println("Writing chain file...");
            writeChains(chainsFile, pdbid, chains);

            // write ligand file
            System.out.println("Writing ligand file...");
            writeLigands(ligandsFile, pdbid, residues);
            
            // write residue mapping files
            System.out.println("Writing residue mapping files...");
            for(Chain c : chains) {
                writeResMappings(resMapFile + "_" + c.getPdbChainID() + ".resmap", c);
            }
            
            
            

            // write models file
            System.out.println("Writing models file...");
            writeModels(modelsFile, pdbid, allModelsIDsOfWholePDBFile);

            // generate the PyMol selection script
            System.out.println("Generating Pymol script to highlight protein-ligand contacts...");
            // String pms = getPymolSelectionScript(cInfo);             // old version: all protein residues on 1 selection
            String pms = getPymolSelectionScriptByLigand(cInfo);        // new version: a separate selection of protein residues for each ligand


            //System.out.println("***** Pymol script follows: *****");
            //System.out.print(pms);
            //System.out.println("***** End of Pymol script. *****");

            String pmsFile = outputDir + fs + pdbid + ".pymol";

            if(writeStringToFile(pmsFile, pms)) {
                System.out.println("  PyMol script written to file '" + pmsFile + "'.");
            }
            else {
                System.err.println("ERROR: Could not write PyMol script to file '" + pmsFile + "'.");
            }

        }
        else {
            System.out.println("  Not writing any interim results to text files as requested (geom_neo compatibility mode off).");
        }
        
        if(Settings.getBoolean("plcc_B_contact_debug_dysfunct")) {
            print_debug_malfunction_warning();
            System.out.println("WARNING: ABORTING execution here due to DEBUG settings, results incomplete.");
            System.exit(1);
        }
        
        ArrayList<Chain> handleChains = new ArrayList<Chain>();
        if(Settings.getBoolean("plcc_B_force_chain")) {
        
            System.out.println(" Forced handling of the chain with chain ID '" + Settings.get("plcc_S_forced_chain_id") + "' only.");
            
            for(Chain c : chains) {
                if(c.getPdbChainID().equals(Settings.get("plcc_S_forced_chain_id"))) {
                    handleChains.add(c);
                }
            }
            
        }
        else {
            handleChains = chains;
        }
        
        if(handleChains.size() < 1) {
            System.err.println("WARNING: No chains to handle found in input data (" + chains.size() + " chains total).");
            System.exit(1);
        }
        

        // *************************************** ramachandran plots for chains  *********************************//
        
        Boolean drawRPlots = Settings.getBoolean("plcc_B_ramachandran_plot");
        String plotPath, label;
        if(drawRPlots) {
            for(Chain c : handleChains) {
                plotPath = outputDir + fs + pdbid + "_" + c.getPdbChainID() + "_plot";
                label = "Ramachandran plot of PDB entry " + pdbid + ", chain " + c.getPdbChainID() + "";
                drawRamachandranPlot(plotPath, c.getResidues(), label);
            }
        }
        

        // ********************************************** SSE stuff and graph computation ******************************************//

        if(Settings.getBoolean("plcc_B_calc_draw_graphs")) {
            System.out.println("  Calculating SSE graphs.");
            calculateSSEGraphsForChains(handleChains, residues, cInfo, pdbid, outputDir);
        }
        else {
            System.out.println("  Not calculating SSEs and not drawing graphs as requested.");
        }

        //drawTGFGraph("graph.tgf", "graph.tgf.png");       //DEBUG
        //dbTesting();        //DEBUG

        // ****************************************************    all done    **********************************************************
        
        if(Settings.getBoolean("plcc_B_contact_debug_dysfunct")) {
            print_debug_malfunction_warning();            
        }
        
        if(deleteFilesOnExit.size() > 0) {
            System.out.print("Deleting " + deleteFilesOnExit.size() + " temporary files... ");
            Integer numDel = IO.deleteFiles(deleteFilesOnExit);
            System.out.print(numDel + " ok.\n");
        }
        System.out.println("All done, exiting.");
        System.exit(0);

    }


    /**
     * Draws the image of a graph from the file 'tpgFile' (which is expected to contain a graph in the Trivial Graph Format) and writes it to the PNG file img.
     * @param tgfFile the path to the TGF file which contains the graph to draw
     * @param img the output path where the resulting image should be written
     */
    public static void drawTGFGraph(String tgfFile, String img) {

        //System.out.println("Testing tgf implementation using file '" + tgfFile + "'.");

        ProtGraph pg = ProtGraphs.fromTrivialGraphFormatFile(tgfFile);
        System.out.println("  Loaded graph with " + pg.numVertices() + " vertices and " + pg.numEdges() + " edges, drawing to file '" + img + "'.");
        pg.drawProteinGraph(img, true);
        //pg.print();
        System.out.println("  Graph image written to file '" + img + "'.");
    }
    
    
    /**
     * Draws the image of a graph from the file 'plccGraphFile' (which is expected to contain a graph in PLCC format) and writes it to the PNG file 'img'.
     * @param plccGraphFile the input file in plcc format
     * @param img the path where to write the output image (including the file extension)
     */
    public static void drawPlccGraphFromFile(String plccGraphFile, String img, Boolean drawFoldingGraphsAsWell) {

        //System.out.println("Testing plcc graph reading implementation using file '" + plccGraphFile + "'.");

        String graphString = FileParser.slurpFileSingString(plccGraphFile);
        
        if(Settings.getInteger("plcc_I_debug_level") > 0) {
            ProtGraphs.printPlccMetaData(graphString);
        }
                
        ProtGraph pg = ProtGraphs.fromPlccGraphFormatString(graphString);
        System.out.println("  Loaded graph with " + pg.numVertices() + " vertices and " + pg.numEdges() + " edges, drawing to file '" + img + "'.");
        if(pg.drawProteinGraph(img, false)) {
            System.out.println("  Protein graph image written to file '" + img + "'.");
            
            if(drawFoldingGraphsAsWell) {
                calculateFoldingGraphsForSSEGraph(pg, Settings.get("plcc_S_output_dir"));
            }
        }
        else {
            System.err.println("ERROR: Drawing of graph failed.");
        }       
    }
    
    
    /**
     * Draws the image of a graph from the file 'plccGraphFile' (which is expected to contain a graph in PLCC format) and writes it to the PNG file 'img'.
     * @param plccGraphFile the input file in plcc format
     * @param img the path where to write the output image (including the file extension)
     */
    public static void drawPlccGraphFromDB(String g_pdbid, String g_chainid, String g_graphtype, String outputimg, Boolean drawFoldingGraphsAsWell) {

        System.out.println("Retrieving " + g_graphtype + " graph for PDB entry " + g_pdbid + " chain " + g_chainid + " from DB.");
        
        String graphString = null;
        try { graphString = DBManager.getGraphString(g_pdbid, g_chainid, g_graphtype); }
        catch (SQLException e) { System.err.println("ERROR: SQL: Drawing of graph from DB failed: '" + e.getMessage() + "'."); return; }
        
        if(graphString == null) {
            System.err.println("ERROR: No such graph in the database, exiting.");
            System.exit(1);
        }
        
        ProtGraph pg = ProtGraphs.fromPlccGraphFormatString(graphString);
        System.out.println("  Loaded graph with " + pg.numVertices() + " vertices and " + pg.numEdges() + " edges, drawing to file '" + outputimg + "'.");
        if(pg.drawProteinGraph(outputimg, false)) {
            System.out.println("  Protein graph image written to file '" + outputimg + "'.");
            
            if(drawFoldingGraphsAsWell) {
                calculateFoldingGraphsForSSEGraph(pg, Settings.get("plcc_S_output_dir"));
            }
        }
        else {
            System.err.println("ERROR: Drawing of graph from DB failed.");
        }       
    }


    /**
     * Just a test function for the DBManager class, does not do anything useful. Creates some tables, inserts stuff and deletes it when it is done.
     */
    public static void dbTesting() {


        System.out.println("Starting DB tests.");
        ArrayList<ArrayList<String>> tableData;
        Integer numRows = null;

        if(Settings.getBoolean("plcc_B_useDB")) {
            System.out.println("DB OK");

            System.out.println("Starting test: INSERT");
            numRows = DBManager.doInsertQuery("INSERT INTO test (name, age) VALUES ('john', 29);");
            System.out.println("Test INSERT done, " + numRows + " affected rows.");
            numRows = DBManager.doInsertQuery("INSERT INTO test (name, age) VALUES ('john', 28);");
            System.out.println("Test INSERT done, " + numRows + " affected rows.");
            numRows = DBManager.doInsertQuery("INSERT INTO test (name, age) VALUES ('john', 27);");
            System.out.println("Test INSERT done, " + numRows + " affected rows.");


            System.out.println("Starting test: UPDATE");
            numRows = DBManager.doUpdateQuery("UPDATE test SET age = 30 WHERE name = 'john' AND age = 29;");
            System.out.println("Test UPDATE done, " + numRows + " affected rows.");


            System.out.println("Starting test: SELECT");            
            try {
                tableData = DBManager.doSelectQuery("SELECT name, age FROM test WHERE name = 'john';");
                
                System.out.print("----------\n");
                
                for(ArrayList<String> row : tableData) {
                    for(String value : row) {
                        System.out.print(value + " ");
                    }
                    System.out.print("\n");
                }
                
                System.out.print("----------\n");
            } catch(Exception e) {
                System.err.println("ERROR: SELECT query failed.");
                e.printStackTrace();
            }


            System.out.println("Starting test: DELETE");
            numRows = DBManager.doDeleteQuery("DELETE FROM test WHERE name = 'john' AND age = 30;");
            System.out.println("Test DELETE done, " + numRows + " affected rows.");


            try {
                tableData = DBManager.doSelectQuery("SELECT name, age FROM test WHERE name = 'john';");

                System.out.print("----------\n");

                for(ArrayList<String> row : tableData) {
                    for(String value : row) {
                        System.out.print(value + " ");
                    }
                    System.out.print("\n");
                }

                System.out.print("----------\n");
            } catch(Exception e) {
                System.err.println("ERROR: SELECT query failed.");
                e.printStackTrace();
            }

            
            numRows = DBManager.doDeleteQuery("DELETE FROM test WHERE name = 'john';");
            System.out.println("Test DELETE done, " + numRows + " affected rows. All tests done.");
        }
        else {
            System.err.println("WARNING: dbTesting(): Not using DB or DB connection failed. Skipping DB tests.");
            System.exit(1);
        }
    }


    /**
     * Calculates all SSE graph types which are configured in the config file for all given chains.
     * @param allChains a list of chains, each chain will be handled separately
     * @param resList a list of residues
     * @param resContacts a list of residue contacts
     * @param pdbid the PDBID of the protein, required to name files properly etc.
     * @param outputDir where to write the output files. the filenames are deduced from graph type and pdbid.
     */
    public static void calculateSSEGraphsForChains(ArrayList<Chain> allChains, ArrayList<Residue> resList, ArrayList<ResContactInfo> resContacts, String pdbid, String outputDir) {

        Chain c;
        ArrayList<SSE> chainDsspSSEs = new ArrayList<SSE>();
        ArrayList<SSE> chainLigSSEs = new ArrayList<SSE>();
        ArrayList<SSE> chainPtglSSEs = new ArrayList<SSE>();
        ArrayList<SSE> allChainSSEs = new ArrayList<SSE>();
        

        System.out.println("Calculating SSEs for all chains of protein " + pdbid + "...");

        HashMap<String, String> md = FileParser.getPDBMetaData();
        Double res = -1.0;
        try {
            res = Double.valueOf(md.get("resolution"));
        } catch (Exception e) {
            res = -1.0;
            System.err.println("WARNING: Could not determine resolution of PDB file for protein '" + pdbid + "', assuming NMR with resolution '" + res + "'.");
            
        }

        //pdb_id, title, header, keywords, experiment, resolution
        if(Settings.getBoolean("plcc_B_useDB")) {
            // Try to delete the protein from the DB in case it is already in there. This won't hurt if it is not.
            DBManager.deletePdbidFromDB(pdbid);
            try {
                DBManager.writeProteinToDB(pdbid, md.get("title"), md.get("header"), md.get("keywords"), md.get("experiment"), res);
                System.out.println("  Info on protein '" + pdbid + "' written to DB.");
            }catch (Exception e) {
                System.err.println("WARNING: Could not write info on protein '" + pdbid + "' to DB.");
            }
        }

        
        // handle all chains
        for(Integer i = 0; i < allChains.size(); i++) {
            c = allChains.get(i);
            System.out.println("  +++++ Handling chain '" + c.getPdbChainID() + "'. +++++");

            ProtMetaInfo pmi = FileParser.getMetaInfo(pdbid, c.getPdbChainID());
            //pmi.print();

            if(Settings.getBoolean("plcc_B_useDB")) {
                try {
                    if(DBManager.writeChainToDB(c.getPdbChainID(), pdbid, pmi.getMolName(), pmi.getOrgScientific(), pmi.getOrgCommon())) {
                        System.out.println("    Info on chain '" + c.getPdbChainID() + "' of protein '" + pdbid + "' written to DB.");
                    }
                    else {
                        System.err.println("WARNING: Could not write info on chain '" + c.getPdbChainID() + "' of protein '" + pdbid + "' to DB.");
                    }
                }
                catch(Exception e) {
                    System.err.println("WARNING: DB: Could not reset DB connection.");
                }
            }

            // determine SSEs for this chain
            System.out.println("    Creating all SSEs for chain '" + c.getPdbChainID() + "' consisting of " + c.getResidues().size() + " residues.");
            chainDsspSSEs = createAllDsspSSEsFromResidueList(c.getResidues());
            
            if(Settings.getInteger("plcc_I_debug_level") > 0) {
                printSSEList(chainDsspSSEs, "DSSP");
            }
            
            chainPtglSSEs = createAllPtglSSEsFromDsspSSEList(chainDsspSSEs);
            
            if(Settings.getInteger("plcc_I_debug_level") > 0) {
                printSSEList(chainPtglSSEs, "PTGL");
            }
            
            chainLigSSEs = createAllLigandSSEsFromResidueList(c.getResidues(), chainDsspSSEs);
            allChainSSEs = mergeSSEs(chainPtglSSEs, chainLigSSEs);
            System.out.println("    Added " + chainLigSSEs.size() + " ligand SSEs to the SSE list, now at " + allChainSSEs.size() + " SSEs.");

            System.out.print("    SSEs: ");
            for(Integer j = 0; j < allChainSSEs.size(); j++) {
                System.out.print(allChainSSEs.get(j).getSseType());
            }
            System.out.print("\n");

            // SSEs have been calculated, now assign the PTGL labels and sequential numbers on the chain
            for(Integer j = 0; j < allChainSSEs.size(); j++) {
                allChainSSEs.get(j).setSeqSseChainNum(j + 1);   // This is the correct value, determined from the list of all valid SSEs of this chain
                allChainSSEs.get(j).setSseIDPtgl(getPtglSseIDForNum(j));

                if(Settings.getBoolean("plcc_B_useDB")) {
                    try {
                       DBManager.writeSSEToDB(pdbid, c.getPdbChainID(), allChainSSEs.get(j).getStartDsspNum(), allChainSSEs.get(j).getEndDsspNum(), allChainSSEs.get(j).getStartPdbResID(), allChainSSEs.get(j).getEndPdbResID(), allChainSSEs.get(j).getAASequence(), allChainSSEs.get(j).getSSETypeInt(), allChainSSEs.get(j).getLigandName3()); 
                       //System.out.println("  Info on SSE #" + (j + 1) + " of chain '" + c.getPdbChainID() + "' of protein '" + pdbid + "' written to DB.");
                    }
                    catch(Exception e) {
                        System.err.println("WARNING: Could not write info on SSE # " + j + " of chain '" + c.getPdbChainID() + "' of protein '" + pdbid + "' to DB.");
                    }
                }
            }


            //printSSEList(chainDsspSSEs, "DSSP SSEs of chain '" + c.getPdbChainID() + "'");
            //printSSEList(chainPtglSSEs, "PTGL SSEs of chain '" + c.getPdbChainID() + "'");
            //printSSEList(chainLigSSEs, "Ligand SSEs of chain '" + c.getPdbChainID() + "'");
            //printSSEList(allChainSSEs, "All SSEs of chain '" + c.getPdbChainID() + "'");


            // ************* Calculate the different graph types *************** //
            //List<String> graphTypes = Arrays.asList("albe", "albelig", "beta", "betalig", "alpha", "alphalig");       // old hardcoded stuff
            //List<String> graphTypes = Arrays.asList("albelig");                                                       // old hardcoded stuff
            
            // read the list of requested graph types from the settings
            List<String> graphTypes = new ArrayList<String>();

            if(Settings.getBoolean("plcc_B_graphtype_albe")) { graphTypes.add("albe"); }
            if(Settings.getBoolean("plcc_B_graphtype_albelig")) { graphTypes.add("albelig"); }
            if(Settings.getBoolean("plcc_B_graphtype_alpha")) { graphTypes.add("alpha"); }
            if(Settings.getBoolean("plcc_B_graphtype_alphalig")) { graphTypes.add("alphalig"); }
            if(Settings.getBoolean("plcc_B_graphtype_beta")) { graphTypes.add("beta"); }
            if(Settings.getBoolean("plcc_B_graphtype_betalig")) { graphTypes.add("betalig"); }
            
            
            String fileNameWithExtension = null;
            String fileNameWithoutExtension = null;
            String filePath = null;
            String imgFile = null;
            String plccGraphFile = null;
            String fs = System.getProperty("file.separator");


            for(String gt : graphTypes) {

                // create the protein graph for this graph type

                ProtGraph pg = calcGraphType(gt, allChainSSEs, c, resContacts, pdbid);
                pg.setInfo(pdbid, c.getPdbChainID(), gt);
                
                
                if(Settings.getBoolean("plcc_B_debug_compareSSEContacts")) {
                    if(gt.equals("albe")) {
                        System.out.println("Comparing calculated SSE contacts with those in the file '" + Settings.get("plcc_S_debug_compareSSEContactsFile") + "'...");
                        FileParser.compareSSEContactsWithGeoDatFile(Settings.get("plcc_S_debug_compareSSEContactsFile"), pg);
                    }        
                    else {
                        System.out.println("INFO: SSE contact comparison request ignored since this is not an albe graph.");
                    }
                }
                
                Integer isoLig = pg.numIsolatedLigands();
                String coilsUsed = "";
                if(Settings.getBoolean("plcc_B_include_coils")) {
                    coilsUsed = " including coils";
                }
                if(isoLig > 0) {
                    System.out.println("      The " + gt + " graph of " + pdbid + " chain " + c.getPdbChainID() + coilsUsed + " contains " + isoLig + " isolated ligands.");
                }
                
                // DEBUG: calculate distance matrix of the graph
                //pg.calculateDistancesWithinGraph();
                //pg.printDistMatrix();

                // draw the protein graph image

                filePath = outputDir;
                String coils = "";
                if(Settings.getBoolean("plcc_B_include_coils")) {
                    //System.out.println("  Considering coils, this may fragment SSEs.");
                    coils = "_coils";
                }
                fileNameWithoutExtension = pdbid + "_" + c.getPdbChainID() + "_" + gt + coils + "_PG";
                fileNameWithExtension = fileNameWithoutExtension + Settings.get("plcc_S_img_output_fileext");
                
                //pg.toFile(file + ".ptg");
                //pg.print();
                File targetDir;
                String dirStructure;
                // Create the file in a subdir tree based on the protein meta data if requested
                if(Settings.getBoolean("plcc_B_output_images_dir_tree") || Settings.getBoolean("plcc_B_output_textfiles_dir_tree")) {
                   
                    if(! (pdbid.length() == 4)) {
                        System.err.println("ERROR: PDB ID of length 4 required to output images in directory tree, using default '" + outputDir + "'.");
                        dirStructure = outputDir;
                        System.exit(1);
                    } else {                    
                        String mid2Chars = pdbid.substring(1, 3);                    
                        dirStructure = outputDir + fs + mid2Chars + fs + pdbid;
                        
                    }
                    
                    targetDir = new File(dirStructure);
                    if(targetDir.isDirectory()) {
                        // dir already exsts
                        if( ! targetDir.canWrite()) {
                            System.err.println("ERROR: Cannot write to existing output directory '" + targetDir.getAbsolutePath() + "'.");
                        } else {
                            // all ok, it exists and we can write to it
                            filePath = dirStructure;    
                        }
                    } else {
                        if(targetDir.isFile()) {
                            System.err.println("ERROR: Cannot create output directory '" + targetDir.getAbsolutePath() + "', file with that name exists. Using default '" + filePath + "'.");
                        }
                        
                        try {
                            Boolean resMkdir = targetDir.mkdirs();
                            if(resMkdir) {
                                // all ok, we created it (and thus can write to it)
                                filePath = dirStructure;    
                            }
                        }catch(Exception e) {
                            System.err.println("ERROR: Could not create required directory structure to output images under '" + outputDir + "', aborting. Using default '" + filePath + "'.");
                            System.err.println("ERROR: The error was '" + e.getMessage() + "'.");
                            //System.exit(1);
                        }
                    }
                }
                
                if(Settings.getBoolean("plcc_B_output_GML")) {
                    String gmlfFile = filePath + fs + fileNameWithoutExtension + ".gml";
                    IO.stringToTextFile(gmlfFile, pg.toGraphModellingLanguageFormat());
                }
                if(Settings.getBoolean("plcc_B_output_TGF")) {
                    String tgfFile = filePath + fs + fileNameWithoutExtension + ".tgf";
                    IO.stringToTextFile(tgfFile, pg.toTrivialGraphFormat());
                }
                if(Settings.getBoolean("plcc_B_output_DOT")) {
                    String dotLangFile = filePath + fs + fileNameWithoutExtension + ".gv";
                    IO.stringToTextFile(dotLangFile, pg.toDOTLanguageFormat());
                }
                if(Settings.getBoolean("plcc_B_output_kavosh")) {
                    String kavoshFile = filePath + fs + fileNameWithoutExtension + ".kavosh";
                    IO.stringToTextFile(kavoshFile, pg.toKavoshFormat());
                }
                // write the SSE info text file for the image (plcc graph format file)
                if(Settings.getBoolean("plcc_B_output_plcc")) {
                    plccGraphFile = filePath + fs + fileNameWithoutExtension + ".plg";
                    IO.stringToTextFile(plccGraphFile, pg.toVPLGGraphFormat());
                }                                                                                    
                
                imgFile = filePath + fs + fileNameWithExtension;
                
                // test spatial ordering, not used for anything atm
                // TODO: remove this, it's only a test and takes time
                /*
                if(pg.size > 0) {                                    
                    ArrayList<Integer> spatOrder = pg.getSpatialOrderingOfVertexIndices();
                    if(spatOrder.size() == pg.size && pg.size > 1) {
                        String order = "      The " + gt + " graph of chain " + c.getPdbChainID() + " with " + pg.size + " vertices has a valid linear spatial ordering: [";
                        for(Integer s = 0; s < spatOrder.size(); s++) {
                            order += " " + spatOrder.get(s);
                        }
                        order += " ]";
                        System.out.println(order);
                        
                        // if its has a spatial ordering, it has a KEY notation
                        System.out.println("      Key notation is: '" + pg.getNotationKEY(true) + "'.");
                    }
                    
                }
                */

                if(Settings.getBoolean("plcc_B_draw_graphs")) {
                    if(pg.drawProteinGraph(imgFile, false)) {
                        System.out.println("      Image of graph written to file '" + imgFile + "'.");
                    }                   
                }
                else {
                    System.out.println("      Image and graph output disabled, not drawing and writing protein graph files.");
                }
                
                // But we may need to write the graph to the database
                if(Settings.getBoolean("plcc_B_useDB")) {
                    try { 
                        DBManager.writeGraphToDB(pdbid, c.getPdbChainID(), ProtGraphs.getGraphTypeCode(gt), pg.toVPLGGraphFormat(), pg.getSSEStringSequential()); 
                        System.out.println("      Inserted '" + gt + "' graph of PDB ID '" + pdbid + "' chain '" + c.getPdbChainID() + "' into DB.");
                    }
                    catch(SQLException e) { 
                        System.err.println("ERROR: Failed to insert '" + gt + "' graph of PDB ID '" + pdbid + "' chain '" + c.getPdbChainID() + "' into DB."); 
                    }
                }
                
                if(Settings.getInteger("plcc_I_debug_level") > 0) {
                    System.out.println("      Graph plus string is '" + pg.getGraphPlusString() + "'.");
                }

                /* ----------------------------------------------- Folding graphs ---------------------------------------------- */

                if(Settings.getBoolean("plcc_B_folding_graphs")) {
                    calculateFoldingGraphsForSSEGraph(pg, outputDir);                                    
                }
                else {
                    //System.out.println("      Not handling folding graphs.");
                }

                // DEBUG: a test only, makes no sense here
                //System.out.println("Detecting maximal cliques in " + graphType + " graph of chain " + c.getPdbChainID() + " for fun.");
                //ArrayList<Set<Integer>> mcs = pg.getMaximalCliques();
                //for(Set s : mcs) {
                //    System.out.println("  Found maximal clique of size " + s.size() + ".");
                //}
                
            }
            System.out.println("  +++++ All " + graphTypes.size() + " protein graphs of chain " + c.getPdbChainID() + " handled. +++++");
        }
        System.out.println("All " + allChains.size() + " chains done.");
    }
    
    
    
    /**
     * Calculates all requested folding graphs for the protein graph (or 'SSE graph') pg. Which graphs are drawn is determined by the 
     * setting on the command line / configuration file.
     * @param pg the protein graphs
     * @param outputDir the file system path where to write the image files
     */
    public static void calculateFoldingGraphsForSSEGraph(ProtGraph pg, String outputDir) {
        //System.out.println("Searching connected components in " + graphType + " graph of chain " + c.getPdbChainID() + ".");
        ArrayList<FoldingGraph> ccs = pg.getConnectedComponents();
        FoldingGraph fg;           // A connected component of a protein graph is a folding graph
        String fgFile = null;

        //System.out.println("Found " + ccs.size() + " connected components in " + graphType + " graph of chain " + c.getPdbChainID() + ".");
        for(Integer j = 0; j < ccs.size(); j++) {
            fg = ccs.get(j);

            if(fg.numVertices() < Settings.getInteger("plcc_I_min_fgraph_size_draw")) {
                //System.out.println("        Ignoring folding graph #" + j + " of size " + fg.numVertices() + ", minimum size is " + Settings.getInteger("plcc_I_min_fgraph_size_draw") + ".");
                continue;
            }

            // DEBUG
            // fg.calculateDistancesWithinGraph();
            // fg.printDistMatrix();
            
            // write plcc graph format file
            String plccGraphFile = outputDir + System.getProperty("file.separator") + pg.getPdbid() + "_" + pg.getChainid() + "_" + pg.getGraphType() + "_FG_" + j + ".fg";
            if(writeStringToFile(plccGraphFile, (fg.toVPLGGraphFormat()))) {
                System.out.println("      Plcc format folding graph file written to '" + plccGraphFile + "'.");
            } else {
                System.err.println("WARNING: Could not write Plcc format folding graph file to '" + plccGraphFile + "'.");
            }
            
            
            System.out.println("        Handling folding Graph #" + j + " containing " + fg.numVertices() + " vertices and " + fg.numEdges() + " edges (" + fg.numSSEContacts() + " SSE contacts).");
            
            // test spatial ordering, not used for anything atm
            // TODO: remove this, it's only a test and takes time
            if(fg.size > 0) { 
                //System.out.println("        Testing folding graph with " + fg.size + " vertices for spatial ordering...");
                ArrayList<Integer> spatOrder = fg.getSpatialOrderingOfVertexIndices();
                String order;
                if(spatOrder.size() == fg.size) {
                    order = "        Folding graph #" + j + " with " + fg.size + " vertices has a valid linear spatial ordering: ";                                        
                    order += " [";
                    for(Integer s = 0; s < spatOrder.size(); s++) {
                        order += " " + spatOrder.get(s);
                    }
                    order += " ]";
                
                    System.out.println(order);
                    
                    System.out.println("        Key notation is: '" + fg.getNotationKEY(true) + "'.");
                }
            }
            
            // Draw all folding graphs in all notations
            //List<String> notations = Arrays.asList("KEY", "ADJ", "RED", "SEQ");
            ArrayList<String> notations = new ArrayList<String>();

            if(Settings.getBoolean("plcc_B_foldgraphtype_KEY")) { notations.add("KEY"); }
            if(Settings.getBoolean("plcc_B_foldgraphtype_ADJ")) { notations.add("ADJ"); }
            if(Settings.getBoolean("plcc_B_foldgraphtype_RED")) { notations.add("RED"); }
            if(Settings.getBoolean("plcc_B_foldgraphtype_SEQ")) { notations.add("SEQ"); }                                                

            for(String nt : notations) {

                if(Settings.getBoolean("plcc_B_draw_graphs")) {

                    fgFile = outputDir + System.getProperty("file.separator") + pg.getPdbid() + "_" + pg.getChainid() + "_" + pg.getGraphType() + "_FG_" + j + "_" + nt + ".png"; //Settings.get("plcc_S_img_output_fileext");
                    if(fg.drawFoldingGraph(nt, fgFile)) {
                        System.out.println("         -Folding graph #" + j + " of the " + pg.getGraphType() + " graph of chain " + pg.getChainid() + " written to file '" + fgFile + "' in " + nt + " notation.");
                    }
                    else {
                        if(Settings.getInteger("plcc_I_debug_level") > 0) {
                            System.err.println("NOTE: Could not draw notation " + nt + " of folding graph #" + j + " of the " + pg.getGraphType() + " graph of chain " + pg.getChainid() + ". (Tried to write to file '" + fgFile + "'.)");
                        }
                    }
                }
                else {
                    //System.out.println("         Image output disabled, not drawing folding graph.");
                }
            }
        }        
    }
    

    /**
     * Determines the PTGL SSE ID for the SSE at sequential position seqNum in the primary sequence.
     *
     * @param seqNum the sequential SSE number
     * @return The PTGL SSE ID (e.g. "A" for the SSE# 1, "D" for SSE #4).
     */
    public static String getPtglSseIDForNum(Integer seqNum) {
        String alph = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        if(seqNum < alph.length()) {
            return("" + alph.charAt(seqNum));
        }
        else {
            return("" + alph.charAt(seqNum % alph.length()));
        }
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
            System.err.println("WARNING: Could not write to file '" + filePath + "'.");
            res = false;
        }
       
        try {
            fw.close();
        } catch(Exception ex) {
            System.err.println("WARNING: Could not close FileWriter for file '" + filePath + "'.");
        }
        return(res);
    }

    
    /**
     * Calculates the requested type of protein graph for the given list of SSEs (usually all SSEss of a chain) and residue contacts.
     * @param graphType the requested graph type
     * @param allChainSSEs the SSEs of a chain
     * @param c the chain
     * @param resContacts a list of residue contacts (between residues of c)
     * @param pdbid the PDBID of the protein the chain c belongs to
     * @return the resulting protein graph
     */
    public static ProtGraph calcGraphType(String graphType, ArrayList<SSE> allChainSSEs, Chain c, ArrayList<ResContactInfo> resContacts, String pdbid) {

        ContactMatrix chainCM;

        System.out.println("    ----- Calculating " + graphType + " graph of chain " + c.getPdbChainID() + ". -----");

        ArrayList<String> keepSSEs = new ArrayList<String>();
        ArrayList<SSE> filteredChainSSEs;

        // Check whether coils should be kept
        if(Settings.getBoolean("plcc_B_include_coils")) {
            keepSSEs.add(Settings.get("plcc_S_coilSSECode"));
        }

        // Filter SSEs depending on the requested graph type
        if(graphType.equals("albe")) {
            keepSSEs.add("E");
            keepSSEs.add("H");
        }
        else if(graphType.equals("alpha")) {
            keepSSEs.add("H");
        }
        else if(graphType.equals("beta")) {
            keepSSEs.add("E");            
        }
        else if(graphType.equals("albelig")) {
            keepSSEs.add("E");
            keepSSEs.add("H");
            keepSSEs.add("L");
        }
        else if(graphType.equals("alphalig")) {
            keepSSEs.add("H");
            keepSSEs.add("L");
        }
        else if(graphType.equals("betalig")) {
            keepSSEs.add("E");
            keepSSEs.add("L");
        }
        else {
            System.err.println("ERROR: calcGraphType(): Graph type '" + graphType + "' invalid.");
            System.exit(1);
        }

        // Filters have been configured, now do the actual filtering.
        filteredChainSSEs = filterAllSSEsButList(allChainSSEs, keepSSEs);

        // The SSEs should already be ordered anyway
        Collections.sort(filteredChainSSEs, new SSEComparator());
        
        // SSE list has been filtered, let's go

        // Calculate SSE level contacts
        chainCM = new ContactMatrix(filteredChainSSEs, pdbid);
        chainCM.restrictToChain(c.getPdbChainID());
        chainCM.fillFromContactList(resContacts, keepSSEs);
        //chainCM.printTotalContactMatrix("TT");
        chainCM.calculateSSEContactMatrix();
        //chainCM.printSSEMatrix();
        
        Boolean computeAll = false;
        computeAll = true;  // DEBUG
        
        chainCM.calculateSSESpatialRelationMatrix(resContacts, computeAll);
        //chainCM.printSpatialRelationMatrix();
        //chainCM.printResContMatrix();
        //chainCM.printTotalContactMatrix("TT");
        
        if(Settings.getBoolean("plcc_B_ptgl_geodat_output")) {
            String gdf = Settings.get("plcc_S_output_dir") + System.getProperty("file.separator") + pdbid + "_" + c.getPdbChainID() + "_" + graphType + ".geodat";            
            if(writeStringToFile(gdf, chainCM.toGeodatFormat(false, true))) {
                System.out.println("  Wrote SSE level contacts for chain " + chainCM.getChain() + " in geo.dat format to file '" + gdf + "'.");
            }
            else {
                System.err.println("WARNING: Failed to write SSE level contacts in geo.dat format to file '" + gdf + "'. Check permissions.");
            }
        }
        

        //UndirectedGraph<String, LabeledEdge> g = chainCM.toJGraph();

        // We only write the SSE contacts for the albelig graph because it contains all SSEs we are interested in.
        //  Writing them for all makes them appear multiple times.
        if(Settings.getBoolean("plcc_B_useDB") && graphType.equals("albelig")) {
            chainCM.writeContactStatisticsToDB();
        }


        ProtGraph pg = chainCM.toProtGraph();
        pg.declareProteinGraph();

        
        if(Settings.getBoolean("plcc_B_forceBackboneContacts")) {
            System.out.println("      Adding backbone contacts to consecutive SSEs of the " + graphType + " graph.");
            pg.addFullBackboneContacts();            
        }

        //System.out.println("    ----- Done with " + graphType + " graph of chain " + c.getPdbChainID() + ". -----");
        return(pg);

    }


    
    /**
     * Filters a list of SSEs by type, returning a list containing all SSEs that have of the SSE types defined
     * in keepSSEs.
     * @return A filtered list of SSEs.
     */
    public static ArrayList<SSE> filterAllSSEsButList(ArrayList<SSE> sses, ArrayList<String> keepSSEs) {
        
        ArrayList<SSE> kept = new ArrayList<SSE>();

        if(keepSSEs.size() < 1) {
            System.err.println("WARNING: filterAllSSEsButList(): list keepSSEs is empty, removing *all* SSEs.");
        }

        for(Integer i = 0; i < sses.size(); i++) {
            if(keepSSEs.contains(sses.get(i).getSseType())) {
                kept.add(sses.get(i));
            }
        }

        return(kept);
    }


    /**
     * Prints short info on how to get help on command line options and exits the program.
     */
    public static void syntaxError() {
        System.err.println("ERROR: Invalid command line. Use '-h' or --help' for info on how to run this program.");
        System.exit(1);
    }
    
    /**
     * Prints short info on how to get help on command line options and exits the program.
     */
    public static void syntaxError(String hint) {
        System.err.println("ERROR: Invalid command line. Use '-h' or --help' for info on how to run this program.");
        System.err.println("ERROR: Hint: '" + hint + "'");
        System.exit(1);
    }

    /**
     * Writes the a priori frequencies of the amino acids into the contact[][][][] array.
     * @param res the residue list which is used to determine the AA frequencies
     */
    public static void getAADistribution(ArrayList<Residue> res) {

        Residue r = null;
        Integer rID = -1;

        for(Integer i = 0; i < res.size(); i++) {
            r = res.get(i);
            rID = r.getInternalID();

            contact[rID][0][0][0]++;

        }
    }

    /**
     * Calculates all contacts between the residues in res.
     * @param res A list of Residue objects.
     * @return A list of ResContactInfo object, each representing a pair of residues that are in contact.
     */
    public static ArrayList<ResContactInfo> calculateAllContacts(ArrayList<Residue> res) {
        
                
        Residue a, b;
        Integer rs = res.size();
        
        if(Settings.getBoolean("plcc_B_contact_debug_dysfunct")) {
            rs = 2;
            System.out.println("DEBUG: Limiting residue contact computation to the first " + rs + " residues.");            
        }        

        Integer numResContactsChecked, numResContactsPossible, numResContactsImpossible, numCmpSkipped;
        numResContactsChecked = numResContactsPossible = numResContactsImpossible = numCmpSkipped = 0;

        Integer numResToSkip, spaceBetweenResidues;
        ResContactInfo rci;
        ArrayList<ResContactInfo> contactInfo = new ArrayList<ResContactInfo>();

        Integer atomRadius = Settings.getInteger("plcc_I_atom_radius");
        Integer atomRadiusLig = Settings.getInteger("plcc_I_lig_atom_radius");

        System.out.println("  Atom radius set to " + atomRadius + " for protein atoms, " + atomRadiusLig + " for ligand atoms (unit is 1/10th Angstroem).");

        Integer globalMaxCollisionRadius = globalMaxCenterSphereRadius + atomRadius;
        Integer globalMaxCenterSphereDiameter = globalMaxCollisionRadius * 2;
        Integer numIgnoredLigandContacts = 0;

        for(Integer i = 0; i < rs; i++) {

            a = res.get(i);
            numResToSkip = 0;

            
            
            for(Integer j = i + 1; j < rs; j++) {

                b = res.get(j);
                
                // DEBUG
                if(Settings.getInteger("plcc_I_debug_level") >= 1) {
                    System.out.println("  Checking DSSP pair " + a.getDsspResNum() + "/" + b.getDsspResNum() + "...");
                    //System.out.println("    " + a.getAtomsString());
                    //System.out.println(a.atomInfo());
                    //System.out.println("    " + b.getAtomsString());
                    //System.out.println(b.atomInfo());
                }                

                numResContactsChecked++;

                // We only need to check on atom level if the center spheres overlap
                if(a.contactPossibleWithResidue(b)) {                                        
                    numResContactsPossible++;

                    //System.out.println("    DSSP res# " + a.getDsspResNum() + "/" + b.getDsspResNum() + ": Collision spheres overlap, checking on atom level.");

                    rci = calculateAtomContactsBetweenResidues(a, b);
                    if( rci != null) {
                        // There were atoms contacts!

                        if(Settings.getBoolean("plcc_B_write_lig_geolig")) {
                            // Just add it without asking questions about the residue types
                            contactInfo.add(rci);
                        }
                        else {
                            // We should ignore ligand contacts
                            if(a.getType().equals(1) || b.getType().equals(1)) {
                                // This IS a ligand contact so ignore it
                                numIgnoredLigandContacts++;
                                // System.out.println("  Ignored ligand contact between DSSP residues " + a.getDsspResNum() + " and " + b.getDsspResNum() + ".");
                            }
                            else {
                                // This is NOT a ligand contact so add it
                                contactInfo.add(rci);
                            }
                        }
                    }
                }
                else {
                    numResContactsImpossible++;
                    //System.out.println("    DSSP res# " + a.getDsspResNum() + "/" + b.getDsspResNum() + ": No atom contact possible, skipping atom level checks.");

                    // Further speedup: If the distance of a residue to another Residue is very large we
                    //  may be able to skip some of the next residues (I. Koch):
                    //  If the distance between them is

                    spaceBetweenResidues = a.resCenterDistTo(b) - (2 * atomRadius + a.getCenterSphereRadius() + b.getCenterSphereRadius());
                    if(spaceBetweenResidues > globalMaxSeqNeighborResDist) {
                        // In this case we can skip at least one residue.

                        // How often does the max dist between to sequential neighbor residues fit into the space between these two residues 'a' and 'b' ? If it fits in there n times we can
                        //  skip the next n residues: even if they were arranged in a straight line from 'a' to 'b' they could not reach it!
                        // numResToSkip = spaceBetweenResidues / globalMaxCenterSphereDiameter;
                        numResToSkip = spaceBetweenResidues / globalMaxSeqNeighborResDist;

                        // System.out.println("  Residue skipping kicked in for DSSP res " + a.getDsspResNum() + ", skipped " + numResToSkip + " residues after " + b.getDsspResNum() + " in distance " + a.resCenterDistTo(b) + ".");
                        j += numResToSkip;
                        numCmpSkipped += numResToSkip;

                    }
                }

            }

        }


        System.out.println("  Checked " + numResContactsChecked + " contacts for " + rs + " residues: " + numResContactsPossible + " possible, " + contactInfo.size() + " found, " + numResContactsImpossible + " impossible (collison spheres check).");
        System.out.println("  Did not check " + numCmpSkipped + " contacts (skipped by seq neighbors check), would have been " + (numResContactsChecked + numCmpSkipped)  + ".");

        if( ! Settings.getBoolean("plcc_B_write_lig_geolig")) {
            System.out.println("  Configured to ignore ligands, ignored " + numIgnoredLigandContacts + " ligand contacts.");
        }

        return(contactInfo);

    }

    
    /**
     * Calculates the atom contacts between the residues 'a' and 'b'.
     * @param a one of the residues of the residue pair
     * @param b one of the residues of the residue pair
     * @return A ResContactInfo object with information on the atom contacts between 'a' and 'b'.
     */
    public static ResContactInfo calculateAtomContactsBetweenResidues(Residue a, Residue b) {

        
        ArrayList<Atom> atoms_a = a.getAtoms();
        ArrayList<Atom> atoms_b = b.getAtoms();

        Atom x, y;
        Integer dist = null;
        Integer CAdist = a.resCenterDistTo(b);
        ResContactInfo result = null;


        Integer[] numPairContacts = new Integer[12];
        // The positions in the numPairContacts array hold the number of contacts of each type for a pair of residues:
        // Some cheap vars to make things easier to understand (a replacement for #define):
        Integer TT = 0;         //  0 = total number of contacts            (all residue type combinations)
        Integer BB = 1;         //  1 = # of backbone-backbone contacts     (protein - protein only)
        Integer CB = 2;         //  2 = # of sidechain-backbone contacts    (protein - protein only)
        Integer BC = 3;         //  3 = # of backbone-sidechain contacts    (protein - protein only)
        Integer CC = 4;         //  4 = # of sidechain-sidechain contacts   (protein - protein only)
        Integer HB = 5;         //  5 = # of H-bridge contacts 1, N=>0      (protein - protein only)
        Integer BH = 6;         //  6 = # of H-bridge contacts 2, 0=>N      (protein - protein only)
        Integer BL = 7;         //  7 = # of backbone-ligand contacts       (protein - ligand only)
        Integer LB = 8;         //  8 = # of ligand-backbone contacts       (protein - ligand only)
        Integer CL = 9;         //  9 = # of sidechain-ligand contacts      (protein - ligand only)
        Integer LC = 10;        // 10 = # of ligand-sidechain contacts      (protein - ligand only)
        Integer LL = 11;        // 11 = # of ligand-ligand contacts         (ligand - ligand only)


        Integer numTotalLigContactsPair = 0;



        Integer[] minContactDistances = new Integer[numPairContacts.length];
        // Holds the minimal distances of contacts of the appropriate type (see numPairContacts, index 0 is unused)

        Integer[] contactAtomNumInResidueA = new Integer[numPairContacts.length];
        // Holds the number Atom x has in its residue a for the contact with minimal distance of that type.
        // See minContactDistances and numPairContacts; index 0 is unused; index 5 + 6 are also unused (atom is obvious + always the same)

        Integer[] contactAtomNumInResidueB = new Integer[numPairContacts.length];
        // Holds the number Atom y has in its residue b for the contact with minimal distance of that type.
        // See minContactDistances and numPairContacts; index 0 is unused; index 5 + 6 are also unused (atom is obvious + always the same)
        

        for(Integer k = 0; k < numPairContacts.length; k++) {      // init all arrays
            numPairContacts[k] = 0;
            minContactDistances[k] = -1;    // We are looking for the smallest distance >= 0 in this function so do NOT set '0' as the initial value or everything will get fucked up!
            contactAtomNumInResidueA[k] = -1;   // We HAVE to assign '-1', NOT any other value here! See comments below for details.
            contactAtomNumInResidueB[k] = -1;   // We HAVE to assign '-1', NOT any other value here! See comments below for details.
            // The initial value of '-1' is required for the atom index arrays because our index
            // starts at '0', but geom_neo treads a '0' in a line of <pdbid>.geo as 'no contact' because
            // it starts its index at '1'.
            // This problem is solved by the functions in ResContactInfo: they return (our_index + 1). This
            // means that:
            //   1) If no contact was detected, our_index is -1 and they return 0, which means 'no contact' to geom_neo.
            //   2) If a contact was detected, our_index is converted to the geom_neo index. :)
        }

        // We assume that the first 5 atoms (index 0..4) in a residue that is an AA are backbone atoms,
        //  while all other (6..END) are assumed to be side chain atoms.
        //  The backbone atoms should have atom names ' N  ', ' CA ', ' C  ' and ' O  ' but we don't check
        //  this atm because geom_neo doesn't do that and we want to stay compatible.
        //  Of course, all of this only makes sense for resides that are AAs, not for ligands. We care for that.
        Integer numOfLastBackboneAtomInResidue = 4;
        Integer atomIndexOfBackboneN = 0;       // backbone nitrogen atom index
        Integer atomIndexOfBackboneO = 3;       // backbone oxygen atom index

        Integer aIntID = a.getInternalID();     // Internal AA ID (ALA=1, ARG=2, ...)
        Integer bIntID = b.getInternalID();
        Integer statAtomIDi, statAtomIDj;



        // Iterate through all atoms of the two residues and check contacts for all pairs
        for(Integer i = 0; i < atoms_a.size(); i++) {

            x = atoms_a.get(i);
            
            if(i >= MAX_ATOMS_PER_AA && a.isAA()) {
                System.err.println("WARNING: The AA residue " + a.getUniquePDBName() + " of type " + a.getName3() + " has more atoms than allowed, skipping atom #" + i + " of type " + x + ".");
                continue;
            }

            for(Integer j = 0; j < atoms_b.size(); j++) {
                
                y = atoms_b.get(j);

                if(j >= MAX_ATOMS_PER_AA && b.isAA()) {
                    System.err.println("WARNING: The AA residue " + b.getUniquePDBName() + " of type " + b.getName3() + " has more atoms than allowed, skipping atom #" + j + " of type " + y + ".");
                    continue;
                }
                                

                // Check whether a contact exist. If so, classify it. Note that the code of geom_neo works based on the
                //  position of an atom in the atom list of its residue (e.g., it assumes that the 2nd atom of an AA is
                //  the C alpha atom. While this seems to hold for many PDB files it will produce wrong results if atoms
                //  are missing from the PDB file or other weird stuff is going on in there.

                //System.out.println("      Checking atom pair " + x.getChemSym() + " and " + y.getChemSym() + " with residue index " + i + " and " + j + " of residues " + a.getFancyName() + " and " + b.getFancyName() + ".");
                //System.out.println("        " + x);
                //System.out.println("        " + y);

                dist = x.distToAtom(y);

                if(x.atomContactTo(y)) {             // If a contact is detected, Atom.atomContactTo() returns true
                                                    

                    // The van der Waals radii spheres overlap, contact found.
                    numPairContacts[TT]++;   // update total number of contacts for this residue pair
                    
                    // DEBUG
                    //System.out.println("DEBUG: Atom contact in distance " + dist + " between atom " + x + " and " + y + ".");


                    // Update contact statistics.
                    statAtomIDi = i + 1;    // The field '0' is used for all contacs and we need to follow geom_neo conventions so we start the index at 1 instead of 0.
                    statAtomIDj = j + 1;
                    if(x.isLigandAtom()) { statAtomIDi = 1; }       // Different ligands can have different numbers of atoms and separating them just makes no sense. We assign all contacts to the first atom.
                    if(y.isLigandAtom()) { statAtomIDj = 1; }
                    contact[0][0][0][0]++;                 // update global total number of contacts
                    contact[aIntID][bIntID][0][0]++;       // contacts AA type a <-> AA type b
                    contact[bIntID][aIntID][0][0]++;       // contacts AA type b <-> AA type a

                    //System.out.println("DEBUG: a=" + aIntID + ",b=" + bIntID + ",i=" + statAtomIDi + ",j=" + statAtomIDj + ". Residues=" + a.getFancyName() + "," + b.getFancyName() + ".");
                    contact[aIntID][bIntID][statAtomIDi][statAtomIDj]++;       // contacts of atoms of AAs
                    contact[bIntID][aIntID][statAtomIDj][statAtomIDi]++;

                    contact[aIntID][bIntID][statAtomIDi][0]++;                 // total number of contacts for atom x of this AA
                    contact[bIntID][aIntID][0][statAtomIDi]++;

                    contact[aIntID][bIntID][statAtomIDj][0]++;                 // total number of contacts for atom x of this AA
                    contact[bIntID][aIntID][0][statAtomIDj]++;
                    
                    // Determine the contact type.                    
                    if(x.isProteinAtom() && y.isProteinAtom()) {
                        // *************************** protein - protein contact *************************


                        // Check the exact contact type
                        if(i <= numOfLastBackboneAtomInResidue && j <= numOfLastBackboneAtomInResidue) {
                            // to be precise, this is a backbone - backbone contact
                            numPairContacts[BB]++;

                            // update data if this is the first contact of this type or if it is better (smaller distance) than the old contact
                            if((minContactDistances[BB] < 0) || dist < minContactDistances[BB]) {
                                minContactDistances[BB] = dist;
                                contactAtomNumInResidueA[BB] = i;
                                contactAtomNumInResidueB[BB] = j;
                            }

                        }
                        else if(i > numOfLastBackboneAtomInResidue && j <= numOfLastBackboneAtomInResidue) {
                            // to be precise, this is a chain - backbone contact
                            numPairContacts[CB]++;

                            // update data if this is the first contact of this type or if it is better (smaller distance) than the old contact
                            if((minContactDistances[CB] < 0) || dist < minContactDistances[CB]) {
                                minContactDistances[CB] = dist;
                                contactAtomNumInResidueA[CB] = i;
                                contactAtomNumInResidueB[CB] = j;
                            }

                        }
                        else if(i <= numOfLastBackboneAtomInResidue && j > numOfLastBackboneAtomInResidue) {
                            // to be precise, this is a backbone - chain contact
                            numPairContacts[BC]++;

                            // update data if this is the first contact of this type or if it is better (smaller distance) than the old contact
                            if((minContactDistances[BC] < 0) || dist < minContactDistances[BC]) {
                                minContactDistances[BC] = dist;
                                contactAtomNumInResidueA[BC] = i;
                                contactAtomNumInResidueB[BC] = j;
                            }
                        }
                        else if(i > numOfLastBackboneAtomInResidue && j > numOfLastBackboneAtomInResidue) {
                            // to be precise, this is a chain - chain contact
                            numPairContacts[CC]++;          // 'C' instead of 'S' for side chain pays off

                            // update data if this is the first contact of this type or if it is better (smaller distance) than the old contact
                            if((minContactDistances[CC] < 0) || dist < minContactDistances[CC]) {
                                minContactDistances[CC] = dist;
                                contactAtomNumInResidueA[CC] = i;
                                contactAtomNumInResidueB[CC] = j;
                            }
                        }
                        else {
                            System.err.println("ERROR: Congrats, you found a bug in the atom contact type determination code (res " + a.getPdbResNum() + " atom " + i + " / res " + b.getPdbResNum() + " atom " + j + ").");
                            System.err.println("ERROR: Atom types are: i (PDB atom #" + x.getPdbAtomNum() + ") => " + x.getAtomType() + ", j (PDB atom #" + y.getPdbAtomNum() + ") => " + y.getAtomType() + ".");
                            System.exit(-2);
                        }

                        // Check for H bridges separately
                        if(i.equals(atomIndexOfBackboneN) && j.equals(atomIndexOfBackboneO)) {
                            // H bridge from backbone atom 'N' of residue a to backbone atom 'O' of residue b.
                            numPairContacts[HB]++;
                            // There can only be one of these so if we found it, simply update the distance.
                            minContactDistances[HB] = dist;
                        }

                        if(i.equals(atomIndexOfBackboneO) && j.equals(atomIndexOfBackboneN)) {
                            // H bridge from backbone atom 'O' of residue a to backbone atom 'N' of residue b.
                            numPairContacts[BH]++;
                            // There can only be one of these so if we found it, simply update the distance.
                            minContactDistances[BH] = dist;
                        }

                    }
                    
                    else if(x.isProteinAtom() && y.isLigandAtom()) {
                        // *************************** protein - ligand contact *************************
                        numTotalLigContactsPair++;

                        // Check the exact contact type
                        if(i <= numOfLastBackboneAtomInResidue) {
                            // to be precise, this is a backbone - ligand contact
                            numPairContacts[BL]++;

                            // update data if this is the first contact of this type or if it is better (smaller distance) than the old contact
                            if((minContactDistances[BL] < 0) || dist < minContactDistances[BL]) {
                                minContactDistances[BL] = dist;
                                contactAtomNumInResidueA[BL] = i;
                                contactAtomNumInResidueB[BL] = j;
                            }

                        }
                        else {
                            // to be precise, this is a side chain - ligand contact
                            numPairContacts[CL]++;

                            // update data if this is the first contact of this type or if it is better (smaller distance) than the old contact
                            if((minContactDistances[CL] < 0) || dist < minContactDistances[CL]) {
                                minContactDistances[CL] = dist;
                                contactAtomNumInResidueA[CL] = i;
                                contactAtomNumInResidueB[CL] = j;
                            }
                        }

                    }
                    else if(x.isLigandAtom() && y.isProteinAtom()) {
                        // *************************** ligand - protein contact *************************
                        numTotalLigContactsPair++;

                        // Check the exact contact type
                        if(j <= numOfLastBackboneAtomInResidue) {
                            // to be precise, this is a ligand - backbone contact
                            numPairContacts[LB]++;

                            // update data if this is the first contact of this type or if it is better (smaller distance) than the old contact
                            if((minContactDistances[LB] < 0) || dist < minContactDistances[LB]) {
                                minContactDistances[LB] = dist;
                                contactAtomNumInResidueA[LB] = i;
                                contactAtomNumInResidueB[LB] = j;
                            }

                        }
                        else {
                            // to be precise, this is a ligand - side chain contact
                            numPairContacts[LC]++;

                            // update data if this is the first contact of this type or if it is better (smaller distance) than the old contact
                            if((minContactDistances[LC] < 0) || dist < minContactDistances[LC]) {
                                minContactDistances[LC] = dist;
                                contactAtomNumInResidueA[LC] = i;
                                contactAtomNumInResidueB[LC] = j;
                            }
                        }
                            
                    }
                    else if(x.isLigandAtom() && y.isLigandAtom()) {
                        // *************************** ligand - ligand contact *************************
                        numTotalLigContactsPair++;

                        // no choices here, ligands have no sub type
                        numPairContacts[LL]++;
                        
                        // update data if this is the first contact of this type or if it is better (smaller distance) than the old contact
                        if((minContactDistances[LL] < 0) || dist < minContactDistances[LL]) {
                            minContactDistances[LL] = dist;
                            contactAtomNumInResidueA[LL] = i;
                            contactAtomNumInResidueB[LL] = j;
                        }
                        

                    }
                    else {
                        // *************************** unknown contact, wtf? *************************
                        // This branch should never be hit because atoms of type OTHER are ignored while creating the list of Atom objects
                        System.out.println("WARNING: One of the atoms " + x.getPdbAtomNum() + " and " + y.getPdbAtomNum() + " is of type UNKNOWN. Bug?");
                    }                                                            
                }                
                else {
                    // No atom contact for these 2 atoms, but there could be contacts between others
                }
            }
        }


        // Iteration through all atoms of the two residues is done
        if(numPairContacts[TT] > 0) {
            result = new ResContactInfo(numPairContacts, minContactDistances, contactAtomNumInResidueA, contactAtomNumInResidueB, a, b, CAdist, numTotalLigContactsPair);
        }
        else {
            result = null;
        }

        return(result);         // This is null if no contact was detected
        
    }


    /**
     * Prints an overview of all contacts to STDOUT.
     * @param rciList the residue contact information to consider
     */
    public static void showContactOverview(ArrayList<ResContactInfo> rciList) {

        ArrayList<ResContactInfo> contacts = rciList;
        ResContactInfo rci;
        Integer contactNum;

        System.out.println("  Numbr TypeA TypeB ResA# ResB# ResA ResB Dist #Cont");

        for(Integer i = 0; i < rciList.size(); i++) {

            rci = rciList.get(i);
            contactNum = i + 1;

            
            System.out.printf("   %4d   %3s   %3s   %3d   %3d  %3s  %3s  %3d    %2d\n", contactNum, rci.getResTypeStringA(), rci.getResTypeStringB(), rci.getDsspResNumResA(), rci.getDsspResNumResB(), rci.getResName3A(), rci.getResName3B(), rci.getResPairDist(), rci.getNumContactsTotal());

        }

    }



    /**
     * Prints an overview of the contact statistics to STDOUT.
     * @param tnr the total number of residues, only required to be printed.
     */
    public static void showContactStatistics(final Integer tnr) {

        final Integer numTotalRes = tnr;

        System.out.printf("%10d - total number of contacts\n%10d - total number of residues\n", contact[0][0][0][0], numTotalRes);

        // ***** Print a priori frequencies row *****
        System.out.println("Amino acid a priori frequencies:");

        // header
        for(Integer i = 1; i < NUM_AAs; i++) {
            // header
            System.out.printf("%3s ", AminoAcid.intIDToName3(i));
        }
        System.out.print("\n");
        
        // frequencies
        for(Integer i = 1; i < NUM_AAs; i++) {
            System.out.printf("%3d ", contact[i][0][0][0]);
        }
        System.out.print("\n\n");


        // ***** Print contacts statistics matrix *****
        System.out.println("Amino acid atom level contact matrix:");


        // header
        System.out.print("    ");
        for(Integer i = 1; i < NUM_AAs; i++) {
            // header
            System.out.printf("%3s ", AminoAcid.intIDToName3(i));
        }
        System.out.print("\n");

        // Iterate through all amino acid pairs...
        for(Integer i = 1; i < NUM_AAs; i++) {

            System.out.printf("%3s ", AminoAcid.intIDToName3(i));

            for(Integer j = 1; j < NUM_AAs; j++ ) {

                System.out.printf("%3d ", contact[i][j][0][0]);

            }

            System.out.print("\n");
        }
    }


    /**
     * Writes formated contact data to file '<pdbid>.geo'.
     * @param rciList the list of resContactInfo objects representing the contacts to consider
     * @param gf the path to the geoFile
     * @param useLigands whether to use .geolig format (instead of .geo format) and consider ligand contacts (they will be 
     * ignored if this is 'false'). If this is true, each line will have additional fields at the
     * end which contain the ligand info.
     */
    public static void writeContacts(ArrayList<ResContactInfo> rciList, String gf, Boolean useLigands) {

        ArrayList<ResContactInfo> contacts = rciList;
        ResContactInfo rci = null;
        Integer contactNum = 0;
        String geoFile = gf;
        FileWriter geoFW = null;
        PrintWriter geoFH = null;

        Integer fHB1Dist, fHB2Dist, fCenterSphereRadiusResA, fCenterSphereRadiusResB, fResPairDist;
        Integer fBBContactDist, fBCContactDist, fCBContactDist, fCCContactDist;
        Integer fBLContactDist, fLBContactDist, fCLContactDist, fLCContactDist, fLLContactDist;

        System.out.println("  Contact output file set to '" + geoFile + "'.");

        try {
            geoFW = new FileWriter(geoFile);
            geoFH = new PrintWriter(geoFW);

            // Remove this header, it is only there for debugging purposes
            //if(Settings.getInteger("plcc_I_debug_level") >= 1) {
            //    geoFH.print("CNUM RNA RNB ID RA ID RA DST HB1 HB2 BBD IA IB CBD IA IB BCD IA IB CCD IA IB TT\n");
            //}

        }
        catch (Exception e) {
            System.err.println("ERROR: Could not write to file '" + geoFile + "'.");
            e.printStackTrace();
            System.exit(-1);
        }
            
        for(Integer i = 0; i < contacts.size(); i++) {

            rci = contacts.get(i);
            contactNum = i + 1;

            //System.out.println("    Writing contact " + contactNum + ".");

            // The format of the resulting file is explained in detail in the file 'doc/help_pdbid.geo'.

            try {
                // prepare the values that need to be adapted to the geom_neo output format
                fHB1Dist = rci.getHB1Dist(); if(fHB1Dist > 999) { fHB1Dist = 999; } if(fHB1Dist < 0) { fHB1Dist = 0; }
                fHB2Dist = rci.getHB2Dist(); if(fHB2Dist > 999) { fHB2Dist = 999; } if(fHB2Dist < 0) { fHB2Dist = 0; }
                fCenterSphereRadiusResA = rci.getCenterSphereRadiusResA(); if(fCenterSphereRadiusResA > 99) { fCenterSphereRadiusResA = 99; }
                fCenterSphereRadiusResB = rci.getCenterSphereRadiusResB(); if(fCenterSphereRadiusResB > 99) { fCenterSphereRadiusResB = 99; }
                fResPairDist = rci.getResPairDist(); if(fResPairDist > 999) { fResPairDist = 999; }

                fBBContactDist = rci.getBBContactDist(); if(fBBContactDist > 999) { fBBContactDist = 999; } if(fBBContactDist < 0) { fBBContactDist = 0; }
                fBCContactDist = rci.getBCContactDist(); if(fBCContactDist > 999) { fBCContactDist = 999; } if(fBCContactDist < 0) { fBCContactDist = 0; }
                fCBContactDist = rci.getCBContactDist(); if(fCBContactDist > 999) { fCBContactDist = 999; } if(fCBContactDist < 0) { fCBContactDist = 0; }
                fCCContactDist = rci.getCCContactDist(); if(fCCContactDist > 999) { fCCContactDist = 999; } if(fCCContactDist < 0) { fCCContactDist = 0; }


                // print first part of the line
                geoFH.printf("%-4d %3d %-3d %2d %-2d %2d %-2d %3d %3d %-3d ",
                            contactNum,
                            rci.getDsspResNumResA(),
                            rci.getDsspResNumResB(),
                            rci.getAAIDResA(),
                            fCenterSphereRadiusResA,
                            rci.getAAIDResB(),
                            fCenterSphereRadiusResB,
                            fResPairDist,
                            fHB1Dist,
                            fHB2Dist
                );
                // print 2nd part of the line (split into 2 function calls only to increase readability a bit)

                geoFH.printf("%3d %2d %-2d %3d %2d %-2d %3d %2d %-2d %3d %2d %-2d %2d",
                            fBBContactDist,
                            rci.getBBContactAtomNumA(),
                            rci.getBBContactAtomNumB(),

                            fCBContactDist,
                            rci.getCBContactAtomNumA(),
                            rci.getCBContactAtomNumB(),

                            fBCContactDist,
                            rci.getBCContactAtomNumA(),
                            rci.getBCContactAtomNumB(),

                            fCCContactDist,
                            rci.getCCContactAtomNumA(),
                            rci.getCCContactAtomNumB(),

                            rci.getNumContactsTotal()
                );

                // print the ligand contact part of the line
                if(useLigands) {

                    geoFH.printf(" ");

                    // prepare ligand values
                    fBLContactDist = rci.getBLContactDist(); if(fBLContactDist > 999) { fBLContactDist = 999; } if(fBLContactDist < 0) { fBLContactDist = 0; }
                    fLBContactDist = rci.getLBContactDist(); if(fLBContactDist > 999) { fLBContactDist = 999; } if(fLBContactDist < 0) { fLBContactDist = 0; }
                    fCLContactDist = rci.getCLContactDist(); if(fCLContactDist > 999) { fCLContactDist = 999; } if(fCLContactDist < 0) { fCLContactDist = 0; }
                    fLCContactDist = rci.getLCContactDist(); if(fLCContactDist > 999) { fLCContactDist = 999; } if(fLCContactDist < 0) { fLCContactDist = 0; }
                    fLLContactDist = rci.getLLContactDist(); if(fLLContactDist > 999) { fLLContactDist = 999; } if(fLLContactDist < 0) { fLLContactDist = 0; }

                    // write ligand data
                    geoFH.printf("%3d %3d %3d %3d %3d %3d %3d %3d %3d %3d %3d %3d %3d %3d %3d %3d",
                            fBLContactDist,
                            rci.getBLContactAtomNumA(),
                            rci.getBLContactAtomNumB(),

                            fLBContactDist,
                            rci.getLBContactAtomNumA(),
                            rci.getLBContactAtomNumB(),

                            fCLContactDist,
                            rci.getCLContactAtomNumA(),
                            rci.getCLContactAtomNumB(),

                            fLCContactDist,
                            rci.getLCContactAtomNumA(),
                            rci.getLCContactAtomNumB(),

                            fLLContactDist,
                            rci.getLLContactAtomNumA(),
                            rci.getLLContactAtomNumB(),

                            rci.getNumLigContactsTotal()
                    );
                }
                geoFH.print("\n");


            } catch (Exception ew) {
                System.err.println("ERROR: Could not write info on contact " + contactNum + " to file '" + geoFile + "'.");
                ew.printStackTrace();
                System.exit(1);
            }


        }

        // Remove this footer, is it there for DEBUG only
        //if(Settings.getInteger("plcc_I_debug_level") >= 1) {
        //    geoFH.print("EOF\n");
        //}
        

        geoFH.close();  // If it isn't closed it won't flush the buffer and large parts of the file will be missing!
        try {
            geoFW.close();
        } catch(Exception ex) {
            System.err.println("ERROR: Could not close FileWriter for file '" + geoFile + "'.");
            ex.printStackTrace();
            System.exit(1);
        }

        System.out.println("  Wrote contact info on " + contactNum + " residue pairs to file '" + geoFile + "'.");

    }


    /**
     * Writes formated statistics to file 'cf', which usually should be 'con.set' (even though Windows may cry at this).
     * @param cf the output path where to write the conset file
     * @param pdb the pdbid of the protein, required for output only
     * @param tnr the total number of residues, required for output only
     */
    public static void writeStatistics(String cf, final String pdb, final Integer tnr) {



        String consetFile = cf;
        FileWriter conFW = null;
        PrintWriter conFH = null;
        Integer numTotalRes = tnr;

        System.out.println("  Statistics output file set to '" + consetFile + "'.");

        try {
            conFW = new FileWriter(consetFile);
            conFH = new PrintWriter(conFW);

        }
        catch (Exception e) {
            System.err.println("ERROR: Could not write to file '" + consetFile + "'.");
            e.printStackTrace();
            System.exit(1);
        }

        conFH.println("AA distribution and contact statistics for protein " + pdb + ".\n");
        conFH.printf("%10d - total number of atom level contacts\n%10d - total number of residues\n\n", contact[0][0][0][0], numTotalRes);
  
        for(Integer i = 1; i < NUM_AAs; i++) {
            conFH.printf("%10d - a priori frequency of amino acid %2d (%s)\n", contact[i][0][0][0], i, AminoAcid.intIDToName3(i));
        }
        conFH.print("\n");

        // Iterate through all amino acid pairs...
        for(Integer i = 1; i < NUM_AAs; i++) {

            for(Integer j = 1; j < NUM_AAs; j++ ) {

                conFH.printf("%6d - contacts between amino acids %2d (%s) and %2d (%s)\n", contact[i][j][0][0], i, AminoAcid.intIDToName3(i), j, AminoAcid.intIDToName3(j));

                // ... and through all their atoms
                for(Integer k = 0; k <= AminoAcid.atomCountOfID(i); k++ ) {

                    conFH.printf("\n");

                    if( k == 1 ) { conFH.printf("\n"); }

                    for(Integer l = 0; l <= AminoAcid.atomCountOfID(i); l++) {

                        if( l == 1 ) { conFH.printf("      "); }

                        // prints contact statistics for the atoms
                        conFH.printf("%6d ", contact[i][j][k][l]);
                    }
                }
                conFH.printf("\n\n");
            }
        }

      
        conFH.close();  // If it isn't closed it won't flush the buffer and large parts of the file will be missing!

        try {
            conFW.close();
        } catch(Exception ex) {
            System.err.println("ERROR: Could not close FileWriter for file '" + consetFile + "'.");
            ex.printStackTrace();
            System.exit(1);
        }

        System.out.println("  Wrote contact statistics to file '" + consetFile + "'.");
        
    }



    /**
     * Determines the maximum center sphere radius of all residues. This is the distance from the (center of the) central atom to the (center of the) atom which
     * is farthest from that atom.
     * @param res a residue list
     * @return the radius of the largest center sphere
     */
    public static Integer getGlobalMaxCenterSphereRadius(ArrayList<Residue> res) {

        Residue r = null;
        Integer maxRad, curRad;
        maxRad = curRad = 0;

        for(Integer i = 0; i < res.size(); i++) {
            r = res.get(i);
            curRad = r.getCenterSphereRadius();

            if(curRad > maxRad) {
                maxRad = curRad;
            }
        }
     
        return(maxRad);
    }

    
    /**
     * Determines the maximum 3D distance between residues which are sequential neighbors. Note that the distance can be pretty large because
     * of ligands which are always listed at the end even though they may not be in the vicinity of the last protein residue, i.e., the one
     * directly preceeding them.
     * @param res a list of residues which to consider
     * @return the maximum distance between two consecutive protein residues in the primary sequence
     */
    public static Integer getGlobalMaxSeqNeighborResDist(ArrayList<Residue> res) {

        Residue r, s;
        r = s = null;
        Integer maxDist, curDist, rID, sID, rT, sT;
        maxDist = curDist = rID = sID = rT = sT = 0;


        // Iterate through residues in sequential order (DSSP numbering) and determine
        //  the maximal distance (center to center) of all pairs of residues that are
        //  neighbors in the amino acid sequence.
        for(Integer i = 0; i < res.size() - 1; i++) {

            r = res.get(i);         // Is this really DSSP ordering? how can we check quickly?
            s = res.get(i + 1);     // NOTE: Yes, it is DSSP ordering since residues are parsed from the
                                    //        DSSP file and residues in there are in DSSP ordering.

            curDist = r.resCenterDistTo(s);

            if(curDist > maxDist) {
                maxDist = curDist;
                rID = r.getDsspResNum();
                rT = r.getType();
                sID = s.getDsspResNum();
                sT = s.getType();
            }
        }

        System.out.println("  Neighbor residues " + rID + " (type " + rT + ") and " + sID + " (type " + sT + ") found in distance " + maxDist + ".");
        return(maxDist);
    }


    /**
     * Writes the chains file, a file containing a list of all chain identifiers, separated by spaces.
     * @param chainsFile the path to the output file
     * @param pdbid the PDB ID of the protein, required for output only
     * @param chains a list of Chain objects, should be all chains of this protein
     */
    public static void writeChains(String chainsFile, String pdbid, ArrayList<Chain> chains) {

        FileWriter chainFW = null;
        PrintWriter chainFH = null;

        // open files
        try {
            chainFW = new FileWriter(chainsFile);
            chainFH = new PrintWriter(chainFW);

        }
        catch (Exception e) {
            System.err.println("ERROR: Could not write to file '" + chainsFile + "'.");
            e.printStackTrace();
            System.exit(1);
        }


        // finally: write stuff
        //chainFH.printf("%s: ", pdbid);

        for(Integer i = 0; i < chains.size(); i++) {
            chainFH.printf("%s ", chains.get(i).getPdbChainID());
        }

        //chainFH.printf("%d\n", chains.size());
        //chainFH.print("# Info is always parsed from the first non-comment line of this file so don't mess that up.\n");
        //chainFH.print("# Line format is as follows: '<pdbid>: [<chainID> ...] <total_number_of_chains>'\n");

        chainFH.close();  // If it isn't closed it won't flush the buffer and large parts of the file will be missing!

        try {
            chainFW.close();
        } catch(Exception ex) {
            System.err.println("ERROR: Could not close FileWriter for file '" + chainsFile + "'.");
            ex.printStackTrace();
            System.exit(1);
        }

        System.out.println("  Wrote chain info to file '" + chainsFile + "'.");       

    }
    
    
    
    /**
     * Writes the residue info file that maps PDB residue IDs to DSSP residue IDs for all residues of the given chain. 
     * @param mapFile the path to the output file
     * @param c the chain to consider (all residues of this chain will be used)
     */
    public static void writeResMappings(String mapFile, Chain c) {

        FileWriter mapFW = null;
        PrintWriter mapFH = null;
        ArrayList<Residue> res = c.getResidues();

        // open files
        try {
            mapFW = new FileWriter(mapFile);
            mapFH = new PrintWriter(mapFW);

        }
        catch (Exception e) {
            System.err.println("ERROR: Could not write to file '" + mapFile + "'.");
            e.printStackTrace();
            System.exit(-1);
        }


        for (Residue r : res) {
            mapFH.print("PDB|" + r.getPdbResNum() + "|DSSP|" + r.getDsspResNum() + "\n");
        }
        
        //chainFH.printf("%d\n", chains.size());
        //chainFH.print("# Info is always parsed from the first non-comment line of this file so don't mess that up.\n");
        //chainFH.print("# Line format is as follows: '<pdbid>: [<chainID> ...] <total_number_of_chains>'\n");

        mapFH.close();  // If it isn't closed it won't flush the buffer and large parts of the file will be missing!

        try {
            mapFW.close();
        } catch(Exception ex) {
            System.err.println("ERROR: Could not close FileWriter for file '" + mapFile + "'.");
            ex.printStackTrace();
            System.exit(-1);
        }

        System.out.println("  Wrote PDB/DSSP residue mapping info to file '" + mapFile + "'.");       

    }
    

     /**
     * Writes the ligand file.
     * @param ligFile the path to the output ligand file
     * @param pdbid the PDB ID of the current protein
     * @param ligands a list of residues which are expected to be ligands
     */
    public static void writeLigands(String ligFile, String pdbid, ArrayList<Residue> ligands) {

        FileWriter ligFW = null;
        PrintWriter ligFH = null;
        Residue r = null;

        // open files
        try {
            ligFW = new FileWriter(ligFile);
            ligFH = new PrintWriter(ligFW);

        }
        catch (Exception e) {
            System.err.println("ERROR: Could not write to file '" + ligFile + "'.");
            e.printStackTrace();
            System.exit(-1);
        }


        // finally: write stuff
        for(Integer i = 0; i < ligands.size(); i++) {

            r = ligands.get(i);

            if(r.isLigand()) {
                ligFH.printf("%s %s %s%d %d %s %s %s %s\n", r.getModelID(), r.getChainID(), r.getChainID(), r.getPdbResNum(), r.getDsspResNum(), r.getName3(), r.getLigName(), r.getLigFormula(), r.getLigSynonyms());
            }
        }

        ligFH.close();  // If it isn't closed it won't flush the buffer and large parts of the file will be missing!

        try {
            ligFW.close();
        } catch(Exception ex) {
            System.err.println("ERROR: Could not close FileWriter for file '" + ligFile + "'.");
            ex.printStackTrace();
            System.exit(-1);
        }

        System.out.println("  Wrote ligand info to file '" + ligFile + "'.");

    }



    /**
     * Writes the model file.
     * @param modelsFile the output file path
     * @param the PDB ID of the current protein
     * @param modelIDs a list of String which represent the IDs of all models contained in the current PDB file
     */
    public static void writeModels(String modelsFile, String pdbid, ArrayList<String> modelIDs) {

        FileWriter modelFW = null;
        PrintWriter modelFH = null;

        // open files
        try {
            modelFW = new FileWriter(modelsFile);
            modelFH = new PrintWriter(modelFW);

        }
        catch (Exception e) {
            System.err.println("ERROR: Could not write to file '" + modelsFile + "'.");
            e.printStackTrace();
            System.exit(-1);
        }


        // finally: write stuff

        for(Integer i = 0; i < modelIDs.size(); i++) {
            modelFH.printf("%s ", modelIDs.get(i));
        }

        modelFH.close();  // If it isn't closed it won't flush the buffer and large parts of the file will be missing!

        try {
            modelFW.close();
        } catch(Exception ex) {
            System.err.println("ERROR: Could not close FileWriter for file '" + modelsFile + "'.");
            ex.printStackTrace();
            System.exit(-1);
        }

        System.out.println("  Wrote model info to file '" + modelsFile + "'.");



    }


    /**
     * Writes a modified version of the original DSSP file. This version includes info on the ligand residues that is appended after the last line of the regular DSSP file.
     * @param dsspFile the input DSSP file that will be parsed for info
     * @param dsspLigFile the output path of the DSSPLig file, which is generated by this function by adding ligand info lines to the DSSP data
     * @param contacts the contacts to consider
     * @param res the residues to consider
     */
    public static Boolean writeDsspLigFile(String dsspFile, String dsspLigFile, ArrayList<ResContactInfo> contacts, ArrayList<Residue> res) {
        
        System.err.println("WARNING: writeDsspLigFile(): This function is deprecated, use writeOrderedDsspLigFile() instead.\n");
        
        FileWriter dsspLigFW = null;
        PrintWriter dsspLigFH = null;

        // copy the DSSP file first, we will then append the ligand-specific lines to the copied file
        try {
            copyFile(new File(dsspFile), new File(dsspLigFile));
        } catch (Exception ef) {
            System.err.println("ERROR: Could not copy file '" + dsspFile + "' to '" + dsspLigFile + "'.");
            ef.printStackTrace();
            System.exit(-1);
        }

        System.out.println("  DSSP ligand output file set to '" + dsspLigFile + "', file created.");

        // open files
        try {
            dsspLigFW = new FileWriter(dsspLigFile, true);  // The 2nd (boolean) parameter sets APPEND mode
            dsspLigFH = new PrintWriter(dsspLigFW);

        }
        catch (Exception e) {
            System.err.println("ERROR: Could not write to file '" + dsspLigFile + "'.");
            e.printStackTrace();
            System.exit(-1);
        }

        // now get and write the ligand lines
        Residue r;
        Locale loc = Locale.ENGLISH;
        for(Integer i = 0; i < res.size(); i++) {

            r = res.get(i);

            if(r.isLigand()) {

                // Now write the line for this ligand in DSSP format (see http://swift.cmbi.ru.nl/gv/dssp/).
                // We do this in several printf() commands to make this code easier to read even though it may be slower (I guess the FileWrite has buffering so it shouldn't be that bad).
                // example line follows after header line (between the ticks):
                //         #  RESIDUE AA STRUCTURE BP1 BP2  ACC     N-H-->O    O-->H-N    N-H-->O    O-->H-N    TCO  KAPPA ALPHA  PHI   PSI    X-CA   Y-CA   Z-CA
                //      '   47   47 A E  E    S-c   29   0A  71    -19,-1.8   -17,-2.8     2,-0.0     2,-0.3  -0.981  70.4-156.7-136.2 150.2   43.9  -12.9   14.8'

                // Print DSSP residue number, PDB residue number, chain, AA name in 1 letter code and SSE summary letter for ligand
                //      '   47   47 A E  E'
                dsspLigFH.printf(loc, "  %3d  %3d %1s %1s  %1s", r.getDsspResNum(), r.getPdbResNum(), r.getChainID(), r.getAAName1(), Settings.get("plcc_S_ligSSECode"));

                // Print structure detail block (empty for ligand), beta bridge 1 partner residue number (always 0 for ligands), beta bridge 2 partner residue number (always 0 for ligands),
                //  bet sheet label (empty (" ") for ligands) and solvent accessible surface (SAS) of this residue (not required by PTGL, just set to some value)
                //      '   47   47 A E  E    S-c   29   0A  71'
                dsspLigFH.printf(loc, " %7s %3d %3d%1s %3d", "       ", 0, 0, " ", Settings.getInteger("plcc_I_ligSAS"));

                // Print the 4 possible H-bridges: N-H-->O, O-->H-N, N-H-->O, O-->H-N. First value (%4d) is the relative residue to which the H bond leads (e.g.: '-15' := 15 reasidues before this one, '4':= 4 residues after this one).
                //  The float value (%3.1f) is the strength of the H bond in kcal/mol. We dont use these and leave them empty for now.
                //      '   47   47 A E  E    S-c   29   0A  71    -19,-1.8   -17,-2.8     2,-0.0     2,-0.3'
                dsspLigFH.printf(loc, "   %4d,%4.1f  %4d,%4.1f  %4d,%4.1f  %4d,%4.1f", 0, 0.0, 0, 0.0, 0, 0.0, 0, 0.0);

                // Print the TCO, KAPPA, ALPHA, PHI and PSI angles. Then the center atom coordinates. That's it.
                dsspLigFH.printf(loc, "  %6.3f%6.1f%6.1f%6.1f%6.1f %6.1f %6.1f %6.1f\n", -0.5, 55.5, 55.5, 55.5, 55.5, (r.getCenterAtom().getCoordX() / 10.0f), (r.getCenterAtom().getCoordY() / 10.0f), (r.getCenterAtom().getCoordZ() / 10.0f));
            }

        }

        dsspLigFH.close();  // If it isn't closed it won't flush the buffer and large parts of the file will be missing!

        try {
            dsspLigFW.close();
        } catch(Exception ex) {
            System.err.println("ERROR: Could not close FileWriter for file '" + dsspLigFile + "'.");
            ex.printStackTrace();
            System.exit(-1);
        }

        System.out.println("  Wrote DSSP ligand info to file '" + dsspLigFile + "'.");


        return(true);
    }

    
    /**
     * Writes an ordered dssplig file. Ordered means that the ligand residues are not simply appended to the end of the file, but each ligand is inserted as the
     * last residue of the chain it is associated with. This is required for PTGL compatibility (don't ask).
     * @param dsspFile the input DSSP file that will be parsed for info
     * @param dsspLigFile the output path of the DSSPLig file, which is generated by this function by adding ligand info lines to the DSSP data
     * @param res the residues to consider
     * @return true if it worked out. Note though that this is considered critical.
     * 
     */
    public static Boolean writeOrderedDsspLigFile(String dsspFile, String dsspLigFile, ArrayList<Residue> res) {

        File dFile = new File(dsspFile);
        File dligFile = new File(dsspLigFile);

        // copy the DSSP file first, we will then append the ligand-specific lines to the copied file
        try {
            copyFile(dFile, dligFile);
        } catch (Exception ef) {
            System.err.println("ERROR: Could not copy file '" + dsspFile + "' to '" + dsspLigFile + "'.");
            ef.printStackTrace();
            System.exit(-1);
        }

        System.out.println("  DSSP ligand output file set to '" + dsspLigFile + "', file created.");

        Residue r;

        for(Integer i = 0; i < res.size(); i++) {

            r = res.get(i);

            if(r.isLigand()) {
                
                dligFile = new File(dsspLigFile);

                try {
                    insertLigandLineIntoDsspligFile(dligFile, getLastLineOfChain(dligFile, r.getChainID()), r);
                } catch(Exception cf) {
                    System.err.println("ERROR: Failed to insert line for ligand residue '" + r.getFancyName() + "' into dssplig file.");
                    cf.printStackTrace();
                    System.exit(-1);
                }

            }
        }

        return(true);

    }


    /**
     * Determines the last line number in a DSSP file that contains information on a residue that is part of chain 'chainID' (i.e., on the last residue of that chain).
     * @param dsspligFile the DSSPLig file to parse
     * @param chainID the chain ID to consider
     * @return the line number of the last line belonging to chain chainID
     */
    public static Integer getLastLineOfChain(File dsspligFile, String chainID) {

        Integer ln = -1;
        String line = "";
        String curChain = "";
        Boolean dataFound = false;

        ArrayList<String> dl = FileParser.slurpFile(dsspligFile.getAbsolutePath());

        for(Integer i = 0; i < dl.size(); i++) {
            line = dl.get(i);

            if(dataFound) {
                curChain = line.substring(11, 12);
                if(curChain.equals(chainID)) {
                    ln = i;
                }
            }
            else {
                if(line.charAt(2) == '#') {
                    dataFound = true;
                }
            }
            

        }

        if(ln == - 1) {
            System.err.println("ERROR: getLastLineofChain(): DSSPLIG file '" + dsspligFile.getName() + "' contains no information on requested chain '" + chainID + "'.");
            System.exit(-1);
        }
        else {
            //System.out.println("DEBUG: Last line of chain '" + chainID + "' in DSSPLIG file '" + dsspligFile.getName() + "' is " + ln + ".");
        }
        return(ln);
    }



    /**
     * Inserts information on the residue 'r' (which is expected to be a ligand residue) into the DSSP file 'inFile' at line number 'lineno'. Obviously, the line numbers of all 
     * following lines will be increased by 1.
     * @param inFile the input file that will be changed
     * @param lineno the line number
     * @param r the residue that should be added at the specified line number
     */
    public static void insertLigandLineIntoDsspligFile(File inFile, int lineno, Residue r)
       throws Exception {
     // temp file
     File outFile = new File("dssplig.tmp");

     // input
     FileInputStream fis  = new FileInputStream(inFile);
     BufferedReader in = new BufferedReader
         (new InputStreamReader(fis));

     // output
     FileOutputStream fos = new FileOutputStream(outFile);
     PrintWriter out = new PrintWriter(fos);

     String thisLine = "";
     Locale loc = Locale.ENGLISH;       // required for proper format!
     int i = 0;
     while ((thisLine = in.readLine()) != null) {
         
       out.println(thisLine);

       if(i == lineno) {
           // Now write the line for this ligand in DSSP format (see http://swift.cmbi.ru.nl/gv/dssp/).
           
            // We do this in several printf() commands to make this code easier to read even though it may be slower (I guess the FileWrite has buffering so it shouldn't be that bad).
            // example line follows after header line (between the ticks):
            //         #  RESIDUE AA STRUCTURE BP1 BP2  ACC     N-H-->O    O-->H-N    N-H-->O    O-->H-N    TCO  KAPPA ALPHA  PHI   PSI    X-CA   Y-CA   Z-CA
            //      '   47   47 A E  E    S-c   29   0A  71    -19,-1.8   -17,-2.8     2,-0.0     2,-0.3  -0.981  70.4-156.7-136.2 150.2   43.9  -12.9   14.8'

            // Print DSSP residue number, PDB residue number, chain, AA name in 1 letter code and SSE summary letter for ligand
            //      '   47   47 A E  E'
            out.printf(loc, "  %3d  %3d %1s %1s  %1s", r.getDsspResNum(), r.getPdbResNum(), r.getChainID(), r.getAAName1(), Settings.get("plcc_S_ligSSECode"));

            // Print structure detail block (empty for ligand), beta bridge 1 partner residue number (always 0 for ligands), beta bridge 2 partner residue number (always 0 for ligands),
            //  bet sheet label (empty (" ") for ligands) and solvent accessible surface (SAS) of this residue (not required by PTGL, just set to some value)
            //      '   47   47 A E  E    S-c   29   0A  71'
            out.printf(loc, " %7s %3d %3d%1s %3d", "       ", 0, 0, " ", Settings.getInteger("plcc_I_ligSAS"));

            // Print the 4 possible H-bridges: N-H-->O, O-->H-N, N-H-->O, O-->H-N. First value (%4d) is the relative residue to which the H bond leads (e.g.: '-15' := 15 reasidues before this one, '4':= 4 residues after this one).
            //  The float value (%3.1f) is the strength of the H bond in kcal/mol. We dont use these and leave them empty for now.
            //      '   47   47 A E  E    S-c   29   0A  71    -19,-1.8   -17,-2.8     2,-0.0     2,-0.3'
            out.printf(loc, "   %4d,%4.1f  %4d,%4.1f  %4d,%4.1f  %4d,%4.1f", 0, 0.0, 0, 0.0, 0, 0.0, 0, 0.0);

            // Print the TCO, KAPPA, ALPHA, PHI and PSI angles. Then the center atom coordinates. That's it.
            out.printf(loc, "  %6.3f%6.1f%6.1f%6.1f%6.1f %6.1f %6.1f %6.1f\n", -0.5, 55.5, 55.5, 55.5, 55.5, (r.getCenterAtom().getCoordX() / 10.0f), (r.getCenterAtom().getCoordY() / 10.0f), (r.getCenterAtom().getCoordZ() / 10.0f));
       }
       
       i++;
    }

    out.flush();
    out.close();
    in.close();

    inFile.delete();
    outFile.renameTo(inFile);
  }


    /**
     * Copies file 'in' to file 'out' on file system level.
     * @param in the input file to be copied
     * @param out the output file
     */
    public static void copyFile(File in, File out)
        throws IOException
    {
        FileChannel inChannel = new
            FileInputStream(in).getChannel();
        FileChannel outChannel = new
            FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(),
                    outChannel);
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }


    /**
     * Prints a short note on how to use this program to STDOUT. Called if the user runs the program without command line arguments (most likely because he does not know about them).
     */
    public static void usage_short() {
        System.out.println("This program is part of VPLG, http://vplg.sourceforge.net. Copyright Tim Schaefer 2012.");
        System.out.println("VPLG is free software and comes without any warranty. See LICENSE for details.");        
        System.out.println("USAGE:         java -jar plcc.jar <pdbid> [OPTIONS]");
        System.out.println("DETAILED HELP: java -jar plcc.jar --help");
    }
    
    
    /**
     * Prints detailed usage info to STDOUT.
     */
    public static void usage() {
        System.out.println("USAGE: java -jar plcc.jar <pdbid> [OPTIONS]");
        System.out.println("       java -jar plcc.jar --help");
        System.out.println("valid OPTIONS are: ");
        System.out.println("-a | --include-coils       : convert the SSE type of all ignored residues to C (coil) and include coils in the graphs [EXPERIMENTAL]");
        System.out.println("-b | --draw-plcc-fgs <f>   : read graph in plcc format from file <f> and draw it and all its folding graphs, then exit (pdbid will be ignored)*");
        System.out.println("-B | --force-backbone      : add contacts of special type 'backbone' between all SSEs of a graph in sequential order (N to C terminus)");
        System.out.println("-c | --dont-calc-graphs    : do not calculate SSEs contact graphs, stop after residue level contact computation");
        System.out.println("-C | --create-config       : creates a default config file if none exists yet, then exits.*");
        System.out.println("-D | --debug <level>       : set debug level (0: off, 1: normal debug output. >=2: detailed debug output)  [DEBUG]");
        System.out.println("-d | --dsspfile <dsspfile> : use input DSSP file <dsspfile> (instead of assuming '<pdbid>.dssp')");
        System.out.println("     --gz-dsspfile <f>     : use gzipped input DSSP file <f>.");
        System.out.println("-e | --force-chain <c>     : only handle the chain with chain ID <c>.");
        System.out.println("-f | --folding-graphs      : also handle foldings graphs (connected components of the protein graph)");
        System.out.println("-g | --sse-graphtypes <l>  : compute only the SSE graphs in list <l>, e.g. 'abcdef' = alpha, beta, alhpabeta, alphalig, betalig and alphabetalig.");
        System.out.println("-h | --help                : show this help message and exit");
        System.out.println("-i | --ignoreligands       : ignore ligand contacts in geom_neo format output files [DEBUG]");
        System.out.println("-j | --ddb <p> <c> <gt> <f>: get the graph type <gt> of chain <c> of pdbid <p> from the DB and draw it to file <f> (omit the file extension)*");
        System.out.println("-k | --img-dir-tree        : do write the output images to a PDB-style sudbir tree of the output dir (e.g., <OUTDIR>/ic/8icd/<outfile>)");
        System.out.println("-K | --graph-dir-tree      : do write the output graph files to a PDB-style sudbir tree of the output dir ");
        System.out.println("-l | --draw-plcc-graph <f> : read graph in plcc format from file <f> and draw it to <f>.png, then exit (<pdbid> will be ignored)*");                
        System.out.println("-L | --lig-filter <i> <a>  : only consider ligands which have at least <i> and at most <a> atoms. A setting of zero means no limit.");                
        System.out.println("-m | --image-format <f>    : write output images in format <f>, which can be 'PNG' or 'JPG' (SVG vector format is always written).");
        System.out.println("-M | --similar <p> <c> <g> : find the proteins which are most similar to pdbid <p> chain <c> graph type <g> in the database.");
        System.out.println("-n | --textfiles           : write meta data, debug info and interim results like residue contacts to text files (slower)");
        System.out.println("-o | --outputdir <dir>     : write output files to directory <dir> (instead of '.', the current directory)");
        System.out.println("-O | --outputformats <list>: write only graph output formats in <list>, where g=GML, t=TGF, d=DOT language, e=Kavosh edge list, p=PLCC. Specify 'x' for none.");
        System.out.println("-p | --pdbfile <pdbfile>   : use input PDB file <pdbfile> (instead of assuming '<pdbid>.pdb')");
        System.out.println("     --gz-pdbfile <f>      : use gzipped input PDB file <f>.");
        System.out.println("-q | --fg-notations <list> : draw only the folding graph notations in <list>, e.g. 'kars' = KEY, ADJ, RED and SEQ.");
        System.out.println("-r | --recreate-tables     : drop and recreate DB statistics tables, then exit (see -u)*");
        System.out.println("-s | --showonscreen        : show an overview of the residue contact results on STDOUT during compuation  [DEBUG]");
        System.out.println("-S | --sim-measure <m>     : use similarity measure <m>. Valid settings include 'string_sse', 'graph_set' and 'graph_compat'.");
        System.out.println("-t | --draw-tgf-graph <f>  : read graph in TGF format from file <f> and draw it to <f>.png, then exit (pdbid will be ignored)*");
        System.out.println("-u | --use-database        : write SSE contact data to database [requires DB credentials in cfg file]");                       
        System.out.println("-v | --del-db-protein <p>  : delete the protein chain with PDBID <p> from the database [requires DB credentials in cfg file]");
        System.out.println("-w | --dont-write-images   : do not draw the SSE graphs and write them to image files [DEBUG]");                             
        System.out.println("-x | --check-rescts <f>    : compare the computed residue level contacts to those in geom_neo format file <f> and print differences");
        System.out.println("-X | --check-ssects <f>    : compare the computed SSE level contacts to those in bet_neo format file <f> and print differences");
        System.out.println("-y | --write-geodat        : write the computed SSE level contacts in geo.dat format to a file (file name: <pdbid>_<chain>.geodat)");        
        System.out.println("-z | --ramaplot            : draw a ramachandran plot of each chain to the file '<pdbid>_<chain>_plot.svg'");        
        System.out.println("");
        System.out.println("EXAMPLES: java -jar plcc.jar 8icd");
        System.out.println("          java -jar plcc.jar 8icd -D 2 -d /tmp/dssp/8icd.dssp -p /tmp/pdb/8icd.pdb");
        System.out.println("          java -jar plcc.jar 8icd -o /tmp");
        System.out.println("          java -jar plcc.jar none -l prot_graph_3kmf_A.plg");
        System.out.println("          java -jar plcc.jar none -m PNG -ddb 8icd A albelig ~/img/protein_graph");        
        System.out.println("");
        System.out.println("REQUIRED INPUT FILES: This program requires the PDB file and the DSSP file of a protein.");
        System.out.println("                      This does not apply to options that don't use it (marked with * above), of course.");
        System.out.println("                      A PDBID still has to be given as first argument, it will be ignored though (use 'none').");
        System.out.println("");
        System.out.println("NOTES: ");
        System.out.println("       -The DSSP program assumes that the input PDB file only has a single model.");
        System.out.println("        You have to split PDB files with multiple models up BEFORE running DSSP (use 'splitpdb').");
        System.out.println("        If you don't do this, the broken DSSP file will get this program into trouble.");
        System.out.println("       -See the config file '" + Settings.getConfigFile() + "' in your userhome to set advanced options.");
        System.out.println("       -If all the parameters above scare you try 'java -jar plcc.jar <PDBID>' for a start.");
    }

    /**
     * Generates a script for the PyMol protein visualization software that selects all ligands and the residues they are in contact with
     * according to the residue contact calculations of this program.
     * @param contacts the contacts to consider for the script.
     * @return the script as a single string. note that the string may consist of multiple lines.
     */
    public static String getPymolSelectionScript(ArrayList<ResContactInfo> contacts) {

        ArrayList<Residue> protRes = new ArrayList<Residue>();
        ArrayList<Residue> ligRes = new ArrayList<Residue>();

        String script = "";
        String scriptProt = "";
        String scriptLig = "";

        ResContactInfo c = null;
        // Select all residues of the protein that have ligand contacts

        for (Integer i = 0; i < contacts.size(); i++) {
            c = contacts.get(i);
            if(c.getNumLigContactsTotal() > 0) {
                // This is a ligand contact, add the residues to one of the lists depending on their type (ligand or protein residue)

                // Handle resA
                if(c.getResA().isAA()) {
                    if( ! protRes.contains(c.getResA())) {
                        protRes.add(c.getResA());
                    }
                }
                if(c.getResA().isLigand()) {
                    if( ! ligRes.contains(c.getResA())) {
                        ligRes.add(c.getResA());
                    }
                }

                // Handle resB
                if(c.getResB().isAA()) {
                    if( ! protRes.contains(c.getResB())) {
                        protRes.add(c.getResB());
                    }
                }
                if(c.getResB().isLigand()) {
                    if( ! ligRes.contains(c.getResB())) {
                        ligRes.add(c.getResB());
                    }
                }

            }
        }

        // Now create the scripts...

        // Handle protein residues
        if(protRes.isEmpty()) {
            scriptProt = "select none";
        } else {

            scriptProt = "select contact_res, resi ";
            for(Integer j = 0; j < protRes.size(); j++) {
                scriptProt += protRes.get(j).getPdbResNum();

                if(j < (protRes.size() - 1)) {
                    scriptProt += "+";
                }
            }
        }

        // Handle ligands
        if(ligRes.isEmpty()) {
            scriptLig = "select none";
        } else {

            scriptLig = "";
            for(Integer k = 0; k < ligRes.size(); k++) {

                scriptLig += "select lig_" + ligRes.get(k).getName3().trim() + ", resi " + ligRes.get(k).getPdbResNum() + "\n";

            }
        }


        script = scriptProt + "\n" + scriptLig;
        return(script);
    }


    /**
     * Creates the PyMOL script, but with a separate selection of contact residues for each ligand.
     * @param contacts the contacts to consider for the script.
     * @return the PyMol script as a string, which may consist of more than one line.
     */
    public static String getPymolSelectionScriptByLigand(ArrayList<ResContactInfo> contacts) {

        ArrayList<Residue> protRes = new ArrayList<Residue>();
        ArrayList<Residue> ligRes = new ArrayList<Residue>();
        ArrayList<Residue> ligCont = new ArrayList<Residue>();
     
        String scriptLig = "";
        String scriptThisLigCont = "";

        ResContactInfo c = null;
        Residue r = null;
        // Select all residues of the protein that have ligand contacts

        for (Integer i = 0; i < contacts.size(); i++) {
            c = contacts.get(i);
            if(c.getNumLigContactsTotal() > 0) {
                // This is a ligand contact, add the residues to one of the lists depending on their type (ligand or protein residue)

                // Handle resA
                if(c.getResA().isAA()) {
                    if( ! protRes.contains(c.getResA())) {
                        protRes.add(c.getResA());
                    }
                }
                if(c.getResA().isLigand()) {
                    if( ! ligRes.contains(c.getResA())) {
                        ligRes.add(c.getResA());
                    }
                }

                // Handle resB
                if(c.getResB().isAA()) {
                    if( ! protRes.contains(c.getResB())) {
                        protRes.add(c.getResB());
                    }
                }
                if(c.getResB().isLigand()) {
                    if( ! ligRes.contains(c.getResB())) {
                        ligRes.add(c.getResB());
                    }
                }

            }
        }

        // Now create the scripts...

        
        // Handle ligands
        if(ligRes.isEmpty()) {
            // None of the residue contacts include ligands.
            return("select none");
        } else {

            scriptLig = "";
            for(Integer k = 0; k < ligRes.size(); k++) {

                r = ligRes.get(k);
                scriptLig += "select lig_" + r.getName3().trim() + r.getPdbResNum() + ", chain " + r.getChainID() + " and resi " + r.getPdbResNum() + "\n";

                // create the list of contact residues for this ligand
                ligCont = new ArrayList<Residue>();
                for(Integer j = 0; j < contacts.size(); j++) {

                    c = contacts.get(j);

                    if(c.getDsspResNumResA().equals(r.getDsspResNum())) {
                        // first residue A is this ligand, so the other one is the contact residue
                        ligCont.add(c.getResB());
                    }
                    else if(c.getDsspResNumResB().equals(r.getDsspResNum())) {
                        // second residue B is this ligand, so the other one is the contact residue
                        ligCont.add(c.getResA());
                    }
                    else {
                        // The current ligand is not involved in this contact
                    }
                }

                
                // Now create the PyMOL script of the contact residues of this ligand
                if(ligCont.isEmpty()) {
                    // The list of contact residues should not be empty at this time.
                    scriptThisLigCont = "";
                    System.err.println("WARNING: getPymolSelectionScriptByLigand(): Residue without contacts in contact list. Bug?");
                } else {

                    scriptThisLigCont = "select lig_" + r.getName3().trim() + r.getPdbResNum() + "_contacts,";

                    for(Integer j = 0; j < ligCont.size(); j++) {
                        scriptThisLigCont += " (resi " + ligCont.get(j).getPdbResNum() + " and chain " + ligCont.get(j).getChainID() + ")";

                        if(j < (ligCont.size() - 1)) {
                            scriptThisLigCont += " or";
                        }
                    }

                    scriptLig += scriptThisLigCont + "\n";
                }

                // That's it for this ligand.
                // System.out.println("Ligand " + r.getFancyName() + " (chain " + r.getChainID() + ") has " + ligCont.size() + " contacts on residue level.");

            }
        }

        return(scriptLig);
    }

    
    /**
     * Creates all SSEs according to DSSP definition from a list of residues. The list is expected to be ordered like in the DSSP file.
     * @param resList the residue list to consider
     * @return a list of secondary structure elements (SSEs)
     */
    private static ArrayList<SSE> createAllDsspSSEsFromResidueList(ArrayList<Residue> resList) {

        ArrayList<SSE> dsspSSElist = new ArrayList<SSE>();
        String lastResString = "some initial value that is not a valid residue string";
        String curResString = "";           // Doesn't matter, will be overwritten before 1st comparison
        SSE curSSE, lastSSE;
        curSSE = lastSSE = null;

        if(resList.size() < 1) {
            System.err.println("WARNING: createAllDsspSSEsFromResidueList() Creating empty list of SSEs: residue list is empty.");
            //System.out.println("      Found " + dsspSSElist.size() + " SSEs according to DSSP definition.");
            return(dsspSSElist);
        }

        // Note that the list of residues is ORDERED in dssp order because the residues were read from the DSSP file.
        Residue curResidue, lastResidue;
        curResidue = lastResidue = null;
        String coil = Settings.get("plcc_S_coilSSECode");

        for(Integer i = 0; i < resList.size(); i++) {

            curResidue = resList.get(i);
            curResString = curResidue.getSSEString();

            // If coiled regions should be included, this line keeps them from being ignored below.
            // In this case, we also assign the SSE type "coil" to all SSEs which would otherwise be
            // ignored later by the getImportantSSEs() filter function. This way, each residue of the
            // protein is assigned some SSE type (H, G, L or C) and all residues appear in the graph.
            if(Settings.getBoolean("plcc_B_include_coils")) {

                if(curResString.equals(" ") || curResString.equals("B") || curResString.equals("S") || curResString.equals("T") || curResString.equals("I") || curResString.equals("G")) {
                    curResString = coil;
                    curResidue.setSSEString(coil);
                }
                
            }

            //System.out.println("   *At DSSP residue " + curResidue.getDsspResNum() + ", PDB name is " + curResidue.getFancyName() + ", SSE string is '" + curResString + "'.");

            if( ! curResString.equals(lastResString)) {

                // The SSE string is different so the old SSE has ended.
                //System.out.println("    New SSE starts at residue " + curResidue.getFancyName() + " (may be invalid SSE though).");

                

                // Create a new SSE for the residue if this residue is part of any SSE (DSSP does NOT assign an SSE to all residues)
                if( ! curResidue.getSSEString().equals(" ")) {

                    //System.out.println("      Residue " + curResidue.getFancyName() + " is part of a valid SSE of type '" + curResidue.getSSEString() + "'.");
                    // This is a valid SSE according to DSSP so create it
                    curSSE = new SSE(curResidue.getSSEString());

                    //System.out.println("      Created new SSE.");

                    // set SSE properties
                    curSSE.addResidue(curResidue);
                    curSSE.setSeqSseNumDssp(dsspSSElist.size() + 1);
                    curSSE.setSseType(curResidue.getSSEString());

                    // set Residue properties
                    curResidue.setSSE(curSSE);
                    curResidue.setDsspSseState(true);


                    // Add the new SSE to the list of SSEs. Ignore ligands though, they are handled later because they are special.
                    if(! curSSE.getSseType().equals("L")) {
                        dsspSSElist.add(curSSE);
                        //System.out.println("      Added new SSE of type '" + curSSE.getSseType() + "', " + s_allDsspSSEs.size() + " SSEs found so far.");
                    }

                }
                else {

                    // This residue is not part of any SSE according to the DSSP definition
                    curResidue.setDsspSseState(false);
                    //System.out.println("      Residue " + curResidue.getFancyName() + " is NOT part of any valid SSE.");
                }

            }
            else {
                // We're still in the old SSE.
                //System.out.println("    Residue " + curResidue.getFancyName() + " is NOT the start of a new SSE.");

                // Set the proper SSE for the residue if this residue is part of any SSE (DSSP does NOT assign an SSE to all residues)
                if( ! curResidue.getSSEString().equals(" ")) {

                    // This is a valid SSE according to DSSP
                    curResidue.setSSE(curSSE);
                    curResidue.setDsspSseState(true);
                    curSSE.addResidue(curResidue);
                    //System.out.println("      Residue " + curResidue.getFancyName() + " added to an existing SSE.");
                }
                else {

                    // This residue is not part of any SSE according to the DSSP definition
                    curResidue.setDsspSseState(false);
                    //System.out.println("      Residue " + curResidue.getFancyName() + " is NOT part of any valid SSE (neither was the last residue).");
                }
            }


            // update for next iteration of loop
            lastResString = curResString;
        }

        //System.out.println("      Found " + dsspSSElist.size() + " SSEs according to DSSP definition.");
        return(dsspSSElist);

    }

    
    /**
     * Returns a list containing all residues from resList that are part of chain cID.
     * @param resList the residue list
     * @param cID the chain ID
     * @return the filtered list of residues
     */
    public static ArrayList<Residue> filterResidueListByChain(ArrayList<Residue> resList, String cID) {
        ArrayList<Residue> filteredList = new ArrayList<Residue>();
        Residue r;

        for(Integer i = 0; i < resList.size(); i++) {
            r = resList.get(i);
            if(r.getChainID().equals(cID)) {
                filteredList.add(r);
            }
        }
        
        return(filteredList);
    }


    /**
     * Creates all the ligand SSEs and adds them to the list of SSEs. Assumes that the ligands
     * are already in the list of residues (i.e., createAllLigandResiduesFromPdbData() has already been called).
     * @param resList the residue list
     * @param dsspSSElist the list of all SSEs according to DSSP definition
     * @return a list of all ligand SSEs that are created from the ligand residues in the residue list.
     */
    private static ArrayList<SSE> createAllLigandSSEsFromResidueList(ArrayList<Residue> resList, ArrayList<SSE> dsspSSElist) {

        ArrayList<SSE> ligSSElist = new ArrayList<SSE>();
        Residue r;
        SSE s;
        Integer ligSSECount = 1;
        
        // Use the min and max atoms setting for ligands
        Integer ligMinAtoms = Settings.getInteger("plcc_I_lig_min_atoms");
        Integer ligMaxAtoms = Settings.getInteger("plcc_I_lig_max_atoms");
        Boolean noMax = false;
        if(ligMinAtoms <= 0) {
            ligMinAtoms = 1;    // 1 means no filtering because every ligand has at least one atom
        }
        if(ligMaxAtoms <= 0) {
            noMax = true;
        }
        if( (ligMinAtoms > ligMaxAtoms) && !noMax) {
            System.out.println("WARNING: Setting for minimum number of ligand atoms is > maximum setting, won't ever match.");
        }
        
        Integer numAtoms;
        Integer numLigIgnoredAtomChecks = 0;

        for(Integer i = 0; i < resList.size(); i++) {

            r = resList.get(i);

            if(r.isLigand()) {
                
                numAtoms = r.getNumAtoms();
                if( (numAtoms < ligMinAtoms) || ((numAtoms > ligMaxAtoms) && !noMax) ) {
                    // Ligand did NOT pass the atom check, ignore it
                    numLigIgnoredAtomChecks++;
                    continue;
                }

                ligSSECount++;
                s = new SSE("L");

                // set SSE properties
                s.addResidue(r);
                s.setSeqSseNumDssp(dsspSSElist.size() + ligSSECount);
                s.setSseType("L");

                // set Residue properties
                r.setSSE(s);
                r.setDsspSseState(true);


                // Add the new SSE to the list of SSEs!
                //System.out.println("      Adding ligand SSE '" + s + "'.");
                ligSSElist.add(s);

            }
        }

        //System.out.println("    Found " + ligSSElist.size() + " ligand SSEs.");
        if(numLigIgnoredAtomChecks > 0) {
            System.out.println("    Ignored " + numLigIgnoredAtomChecks + " ligands due to atom number constraints [" + ligMinAtoms + ", " + ligMaxAtoms + "].");
        }
        
        return(ligSSElist);
    }
    
    


    /**
     * This function creates a modified list of SSEs for the PTGL. It does this by filtering all SSEs
     * which are too short and all SSEs which are not of interest for the PTGL (those which are neither helices nor beta-strands).
     * @param inputSSEs the list of input SSEs, i.e., all SSEs of this chain which are 
     */
    private static ArrayList<SSE> createAllPtglSSEsFromDsspSSEList(ArrayList<SSE> inputSSEs) {

        ArrayList<SSE> outputSSEs = new ArrayList<SSE>();

        // If the list is emtpy we're already done. :)
        if(inputSSEs.size() < 1) {
            System.out.println("    Creating empty list of PTGL SSEs, list of DSSP SSEs is empty.");
            return(outputSSEs);
        }

        // Let's do some pre-filtering to get all the SSEs we don't want out of there first
        ArrayList<SSE> impSSEs = getImportantSSETypes(inputSSEs);
        ArrayList<SSE> consideredSSEs = removeShortSSEs(impSSEs, Settings.getInteger("plcc_I_min_SSE_length"));
        
        if(Settings.getInteger("plcc_I_debug_level") > 0) {
            printSSEList(consideredSSEs, "Considered");
        }
        
        System.out.println("    Pre-filtered list of SSEs, " + consideredSSEs.size() + " left out of " + inputSSEs.size() + " (ignored SSEs of type 'B', 'T' and 'S').");

        SSE curSSE, nextSSE, afterNextSSE;
        curSSE = nextSSE = afterNextSSE = null;
        Integer numSSEsLeft;
        String cst;         // SSE type of the current SSE

        for(Integer i = 0; i < consideredSSEs.size(); i++) {

            // Fill the 3 SSE variables if enough SSEs are left
            curSSE = consideredSSEs.get(i);
            cst = curSSE.getSseType();
            numSSEsLeft = consideredSSEs.size() - (i + 1);
            //System.out.println("    At considered SSE #" + i + " of type " + curSSE.getSseType() + ", " + numSSEsLeft + " SSEs left after this one.");

            // Decide what to do based on the type of the current SSE:
            if(cst.equals("L")) {
                // We don't need to do anything special if this SSE is a ligand, just add it.
                outputSSEs.add(curSSE);
                continue;
            }
            else if(cst.equals("E")) {
                // Same goes for non-isolated beta strands
                outputSSEs.add(curSSE);
                continue;
            }
            else if(cst.equals("H") || cst.equals("I") || cst.equals("G")) {
                // We hit one of the 3 helix types. These may require merging:
                //  If we hit a helix of type I or G preceded or followed by a helix of type H, we merge them into
                //  a single helix of type H.
                //  Also we don't distinguish between the 3 types of helices, just claim they all were of type H.
                   
                // Decide what to do depending on the number of SSEs left (because we may need to merge with those).
                if(numSSEsLeft < 1) {
                    // This is the last SSE so no more merging will follow. Just add this one.
                    curSSE.setSseType("H");
                    outputSSEs.add(curSSE);
                    continue;
                }
                else {
                    // There is at least one more SSE.
                    nextSSE = consideredSSEs.get(i + 1);

                    

                    // If it is a helix of type 'H', merge these two SSEs.
                    if(nextSSE.getSseType().equals("H") || nextSSE.getSseType().equals("I") || nextSSE.getSseType().equals("G")) {
                        
                        if(Settings.getBoolean("plcc_B_merge_helices")) {
                            
                            // we only merge SSEs if they are roughly adjacent, i.e., if their distance in the AA sequence is smaller than a certain threshold
                            if(curSSE.getPrimarySeqDistanceInAminoAcidsTo(nextSSE) <= Settings.getInteger("plcc_I_merge_helices_max_dist")) {
                                // To merge, we add all residues of the next SSE to this one and skip the next one.
                                // System.out.println("    Merging SSEs #" + i + " of type " + cst +  " and #" + (i + 1) + " of type " + nextSSE.getSseType()  + ".");
                                curSSE.addResidues(nextSSE.getResidues());
                                i++;    // ignore the next SSE, we assigned its residues to this one already                                                            
                            }                                                                                    
                        }                        
                    }                    

                    // No matter whether we merged or not, we should add the current SSE.
                    curSSE.setSseType("H");         // This turns the new SSE (whether merged or a former H/G/I) into a H
                    outputSSEs.add(curSSE);                                                                            
                }



            }
            else if(cst.equals("C")){       // coils are only considered if explicitely requested
                if(Settings.getBoolean("plcc_B_include_coils")) {
                    outputSSEs.add(curSSE);
                }
            }
            else {
                System.out.println("      WARNING: Unhandled SSE of type '" + cst + "' encountered. Ignored, this should not happen.");
            }


            if(consideredSSEs.size() > (i + 1)) {
                nextSSE = consideredSSEs.get(i + 1);
            }


        }

        System.out.println("    PTGL adaptations of SSE list (merging etc) done, " + outputSSEs.size() +  " left out of the " + consideredSSEs.size() + " pre-filtered SSEs.");

        return(outputSSEs);

    }

    
    /**
     * Filters all SSEs that are not considered at all by the PTGL out and returns another
     * ArrayList that doesn't include those SSEs. It does NOT modify the original list.
     *
     * By default, it filters SSEs of type "B" (residue in isolated beta-bridge), "S" (bend) and "T" (hydrogen bonded turn).
     *
     */
    private static ArrayList<SSE> getImportantSSETypes(ArrayList<SSE> list) {

        ArrayList<SSE> filteredList = new ArrayList<SSE>();

        SSE s = null;
        String sT = null;
        for(Integer i = 0; i < list.size(); i++) {

            s = list.get(i);
            sT = s.getSseType();

            // Add the SSE to the list if it is some kind of helix (H,G,I), a non-isolated beta strand (E) or a ligand (L)
            if(sT.equals("H") || sT.equals("G") || sT.equals("I") || sT.equals("E") || sT.equals("L")) {
                filteredList.add(s);

                //System.out.println("    Considering SSE '" + s + "'.");
            }

            // We may want to consider coils.
            if(Settings.getBoolean("plcc_B_include_coils")) {
                if(sT.equals("C")) {
                    filteredList.add(s);
                }
            }
        }

        return(filteredList);
    }
    
    /**
     * Filters all SSEs which are shorter than minLength (i.e., consists of less than minLength residues) from the list. Returns a new list and does NOT modify the old one.
     * @param list the input list, it is not modified
     * @param minLength the minimal length of the SSEs to keep, in residues
     * @return a new list which is a filtered version of the old one
     */
    private static ArrayList<SSE> removeShortSSEs(ArrayList<SSE> list, Integer minLength) {

        ArrayList<SSE> filteredList = new ArrayList<SSE>();

        for(SSE s : list) {
            if(s.getLength() >= minLength) {
                filteredList.add(s);
            }            
        }

        return(filteredList);
    }

    
    /**
     * Prints a formatted list of SSEs to STDOUT.
     * @param sl the list of SSEs
     * @param title the title to use for the output 
     */
    public static void printSSEList(ArrayList<SSE> sl, String title) {

        // just a counting aid
        System.out.println("---[ " + title + " ] (" + sl.size() + " SSEs) ---");
        System.out.println("           1         2         3         4         5         6         7         8         9");
        System.out.println("  123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

        if(sl.size() < 1) {
            System.out.println("  <no_SSEs>");
        }
        else {

            // SSE list, only the type
            System.out.print("  ");
            for(Integer j = 0; j < sl.size(); j++) {
                System.out.print(sl.get(j).getSseType());
            }
            System.out.print("\n");

            // details for each SSE
            System.out.print(" ");
            for(Integer j = 0; j < sl.size(); j++) {
                System.out.print(" " + sl.get(j).shortStringRep());
            }
            System.out.print("\n");
        }            
    }

    
    /**
     * Merges 2 lists of SSEs and returns a new list containing all SSEs from both lists.
     * @param listA the first SSE list
     * @param listB the second SSE list
     */
    public static ArrayList<SSE> mergeSSEs(ArrayList<SSE> listA, ArrayList<SSE> listB) {
        ArrayList<SSE> listMerged = new ArrayList<SSE>();

        for(Integer i = 0; i < listA.size(); i++) {
            listMerged.add(listA.get(i));
        }

        for(Integer j = 0; j < listB.size(); j++) {
            listMerged.add(listB.get(j));
        }

        return(listMerged);
    }
    
    
    /**
     * Draws a Ramachandran plot (phi and psi angels of the residues) to an image file. Note that the angles are parsed from the DSSP
     * file but consider our SSE modifications. Still, the plot shows more about DSSP than about plcc.
     * 
     * @param filePath the output path of the image without extension
     * @param the Residues, in an ArrayList
     * @return true if the file was written, false otherwise
     */
    public static Boolean drawRamachandranPlot(String filePath, ArrayList<Residue> residues, String label) {
        
        
        // Apache Batik SVG library, using W3C DOM tree implementation
        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        // Create an instance of the SVG Generator.
        //SVGGraphics2D ig2 = new SVGGraphics2D(document);
        
        
        

        // All these values are in pixels
        // page setup
        Integer marginLeft = 40;
        Integer marginRight = 40;
        Integer marginTop = 40;
        Integer marginBottom = 40;

        // The header that contains the text describing of the graph.
        Integer headerHeight = 40;

        // The footer contains the vertex numbering.
        Integer footerHeight = 40;
        
        // Where to start drawing
        Integer headerStartX = marginLeft;
        Integer headerStartY = marginTop;
        Integer imgStartX = marginLeft;
        Integer imgStartY = marginTop + headerHeight;
        Integer footerStartX = marginLeft;
        
        
        Integer plotWidth = 800;
        Integer plotHeight = 800;
    
        // the image area: the part where the vertices and arcs are drawn
        Integer imgWidth = plotWidth;
        Integer imgHeight = plotHeight;              

        // where to start drawing the vertices
        Integer footerStartY = imgStartY + plotHeight + 40;
        
        // putting it all together
        Integer pageWidth = marginLeft + imgWidth + marginRight;
        Integer pageHeight = marginTop + headerHeight + imgHeight + footerHeight + marginBottom;


        
        try {

            // ------------------------- Prepare stuff -------------------------
            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
            BufferedImage bi = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ig2 = bi.createGraphics();
            ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // make background 
            ig2.setPaint(Color.LIGHT_GRAY);
            ig2.fillRect(0, 0, pageWidth, pageHeight);            
            ig2.setPaint(Color.BLACK);


            // prepare font
            Font font = new Font("TimesRoman", Font.PLAIN, 20);
            ig2.setFont(font);
            FontMetrics fontMetrics = ig2.getFontMetrics();
            
            java.awt.Shape shape;

            // ------------------------- Draw header -------------------------

            // check width of header string
            String plotHeader = label;
            Integer stringWidth = fontMetrics.stringWidth(plotHeader);       // Should be around 300px for the text above
            Integer stringHeight = fontMetrics.getAscent();
            String sseNumber;
            
            ig2.drawString(plotHeader, headerStartX, headerStartY);
            // draw axis labels
            
            font = new Font("TimesRoman", Font.PLAIN, 16);
            ig2.setFont(font);
            
            Integer xAxisLabelStartY = headerStartY + 30;
            
            // horizontal labels
            ig2.drawString("-180°", headerStartX, xAxisLabelStartY);
            ig2.drawString("-phi", headerStartX + (plotWidth / 4), xAxisLabelStartY);
            ig2.drawString("0°", headerStartX + (plotWidth / 2), xAxisLabelStartY);
            ig2.drawString("+phi", headerStartX + (plotWidth * 3/4), xAxisLabelStartY);
            ig2.drawString("+180°", headerStartX + plotWidth, xAxisLabelStartY);
            
            // vertical labels
            xAxisLabelStartY += 20;
            Integer yAxisLabelStartX = 5;
            ig2.drawString("+180°", yAxisLabelStartX, xAxisLabelStartY);
            ig2.drawString("+psi", yAxisLabelStartX, xAxisLabelStartY  + (plotHeight / 4));
            ig2.drawString("0°", yAxisLabelStartX, xAxisLabelStartY + (plotHeight / 2));
            ig2.drawString("-psi", yAxisLabelStartX, headerStartX + (plotHeight * 3/4));
            ig2.drawString("-180°", yAxisLabelStartX, headerStartX + plotHeight);
            
            // footer
            ig2.drawString("Color codes indicate SSE type of residues: red=helix, black=beta strand, gray=coil/other.", footerStartX, footerStartY);
            
            // ------------------------- Draw area and the borders around the drawing area ----------------------------
            ig2.setPaint(Color.WHITE);
            ig2.fillRect(imgStartX, imgStartY, plotWidth, plotHeight);
            
            // draw it                        
            ig2.setStroke(new BasicStroke(1));
            Rectangle2D border = new Rectangle2D.Double(imgStartX, imgStartY, plotWidth, plotHeight);
            shape = ig2.getStroke().createStrokedShape(border);
            ig2.draw(shape);
            
            // draw x and y axis
            ig2.setPaint(Color.LIGHT_GRAY);
            ig2.drawLine(imgStartX, imgStartY + (plotHeight / 2), imgStartX + plotWidth, imgStartY +  + (plotWidth / 2)); // y
            ig2.drawLine(imgStartX + (plotWidth / 2), imgStartY, imgStartX + (plotWidth / 2), imgStartY + plotHeight); // x
            

            // ------------------------- Draw the plot -------------------------
            
            // Draw the edges as arcs
            
            Rectangle2D.Double dot;
            Residue res;
            Integer edgeType, arcCenterX, arcCenterY, leftVert, rightVert, leftVertPosX, rightVertPosX, arcWidth, arcHeight, arcTopLeftX, arcTopLeftY, spacerX, spacerY;
            for(Integer i = 0; i < residues.size(); i++) {
                res = residues.get(i);

                // Choose color
                if(res.getSSEString().equals("H")) { ig2.setPaint(Color.RED); }
                else if(res.getSSEString().equals("E")) { ig2.setPaint(Color.BLACK); }
                else if(res.getSSEString().equals("L")) { ig2.setPaint(Color.MAGENTA); }
                else if(res.getSSEString().equals("C")) { ig2.setPaint(Color.GRAY); }                
                else { ig2.setPaint(Color.LIGHT_GRAY); }
                
                // skip ligands and other non-AAs
                if(! res.isAA()) {
                    continue;
                }

                // determine the position of the dot within the drawing area
                Double phiNorm = (res.getPhi() + 180.0) / 360.0;
                Double psiNorm = (res.getPsi() + 180.0) / 360.0;
                Double posDrawX = phiNorm * imgWidth;
                Double posDrawY = psiNorm * imgHeight;
                
                // now determine the absolute position in the canvas
                Double posCanvasX = imgStartX + posDrawX;
                Double posCanvasY = imgStartY + posDrawY;
                
                Double dotWidth = 1.0;
                Double dotHeight = 1.0;
                

                // draw it                        
                ig2.setStroke(new BasicStroke(1));
                dot = new Rectangle2D.Double(posCanvasX, posCanvasY, dotWidth, dotHeight);
                shape = ig2.getStroke().createStrokedShape(dot);
                ig2.draw(shape);

            }
            

            
            // all done, write the image to disk
            ImageIO.write(bi, "PNG", new File(filePath + ".png"));
            //ig2.stream(new FileWriter(filePath + ".svg"), false);
            ig2.dispose();

        } catch (Exception e) {
            System.err.println("WARNING: Could not write image file for ramachandran plot to file '" + filePath + "'. Check permissions.");
            return(false);
        }

        return(true);        
    }
    
    
    /**
     * Informs the user that the currently selected DEBUG settings brake the output of this program, e.g. because
     * important computations are aborted after the first few residues.
     */
    public static void print_debug_malfunction_warning() {
        System.out.println("###################################################################################");
        System.out.println("# WARNING: Contact debug mode ACTIVE. Will abort after the first few residues.    #");
        System.out.println("# WARNING: This mode will produce no or WRONG SSE level results! Do NOT use them. #");
        System.out.println("###################################################################################");
    }
    

}
