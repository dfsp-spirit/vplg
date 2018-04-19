/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package io;


// imports
import proteinstructure.ProtMetaInfo;
import proteingraphs.ResContactInfo;
import proteingraphs.ProtGraph;
import proteinstructure.Model;
import proteinstructure.Residue;
import proteinstructure.Chain;
import proteinstructure.AminoAcid;
import proteinstructure.Atom;
import proteinstructure.SSE;
import tools.DP;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import resultcontainers.ProteinResults;
import plcc.Settings;
import proteinstructure.BindingSite;


/**
 * A file parser, used to extract information from PDB and DSSP files.
 * 
 * @author ts
 */
public class FileParser {

    // declare class vars
    public static Boolean silent = false;
    public static Boolean essentialOutputOnly = false;
    static String fs = System.getProperty("file.separator");
    static Integer maxResidues = 11000;
    static Integer maxUsedDsspResNumInDsspFile = null;
    static String firstModelName = "1";             // the model ID that identifies the first model in a PDB file
    static String defaultModelName = firstModelName;
    static String lastAltLoc = " ";

    static String dsspFile = null;
    static String pdbFile = null;
    static Integer dsspDataStartLine = null;

    static ArrayList<String> pdbLines = null;
    static ArrayList<String> dsspLines = null;

    static ArrayList<Model> s_models = null;
    static ArrayList<String> s_allModelIDsFromWholePDBFile = null;
    static ArrayList<Chain> s_chains = null;
    static ArrayList<Residue> s_residues = null;
    static ArrayList<Atom> s_atoms = null;
    static ArrayList<SSE> s_dsspSSEs = null;             // all SSEs according to DSSP definition
    static ArrayList<SSE> s_ptglSSEs = null;                // the modified SSE list the PTGL uses
    static HashMap<String, ArrayList<String>> homologuesMap = null;
    static List<BindingSite> s_sites;
    
    // The list of sulfur bridges (aka disulfide bridges) from the DSSP file. The key is the DSSP sulfur bridge
    // id (an arbitrary character, starting with 'a' for the first bridge usually). The list in the value part contains
    // the DSSP residue IDs of all residues which are part of the bridge (and thus should have length 2).
    static HashMap<Character, ArrayList<Integer>> s_sulfurBridges;
    
    // The list of interchain sulfur bridges from the DSSP file. The list structure is the same as for s_sulfurBridges.
    static HashMap<Character, ArrayList<Integer>> s_interchainSulfurBridges;
    // This list is needed to track if sulfur bridges span between to independent chains.
    static HashMap<Character, String> s_interchainSulfurBridgesChainID;
    
    // Filled by cif parser so that file only needs to be read once
    // contains some basic meta data (compare with getPDBMetaData()):
    // HashMap of (String, String) pairs (key, value) with information on the PDB file
    //   -> 'resolution' (which may be cast to Double), 'experiment', 'keywords', 'header', 'title'.
    static HashMap<String, String> metaData = null;
    
    static Boolean dataInitDone = false;

    static Integer curLineNumPDB = null;
    static Integer curLineNumDSSP = null;
    static String curLinePDB = null;
    static String curLineDSSP = null;
    static String curModelID = null;
    static String oldModelID = null;
    static String curChainID = null;
    static String oldChainID = null;
    static Integer curResNumPDB = null;
    static Integer curResNumDSSP = null;
    static Residue curRes = null;
    static Integer[] resIndexPDB = null;        // for a DSSP res number, holds the index of that residue in s_residues
    static Integer[] resIndexDSSP = null;       // for a PDB res number, holds the index of that residue in s_residues

    
    /**
     * Parses the contents of a line from the given startIndex to the end of the line.
     * @param startIndexInclusive the start index, inclusive
     * @param line the whole line as a string
     * @return the contents of the line from start index to end -- may be empty. if the line is too short, the string is also empty (even if it is shorter than start index).
     */
    public static String getLineContentsToEndFrom(int startIndexInclusive, String line) {
        if(line == null) {
            return "";
        }
        int length = line.length();
        if(length <= startIndexInclusive) {
            return "";
        }
        String substr = line.substring(startIndexInclusive, length);
        return substr;
    }

    /**
     * @param df Path to a DSSP file. Does NOT test whether it exist, do that earlier.
     * @param pf Path to a PBD file. Does NOT test whether it exist, do that earlier.
     */
    public static Boolean initData(String pf, String df) {

        pdbFile = pf;
        dsspFile = df;

        if(! FileParser.silent) {
            System.out.println("  Reading files...");
        }
        // read all lines of the files into lists
        pdbLines = new ArrayList<String>();
        pdbLines = slurpPDBFileToModel(pdbFile, "2");
        if(! FileParser.silent) {
            System.out.println("    Read " + pdbLines.size() + " lines of file '" + pdbFile + "'.");
        }

        dsspLines = new ArrayList<String>();
        dsspLines = slurpFile(dsspFile, true); // vararg tells the function that this is a dssp file
        if(! FileParser.silent) {
            System.out.println("    Read all " + dsspLines.size() + " lines of file '" + dsspFile + "'.");
        }

        // parse the lists, filling the data arrays (models, chains, residues, atoms)
        s_models = new ArrayList<Model>();
        s_allModelIDsFromWholePDBFile = new ArrayList<String>();
        s_chains = new ArrayList<Chain>();
        s_residues = new ArrayList<Residue>();
        s_atoms = new ArrayList<Atom>();
        s_dsspSSEs = new ArrayList<SSE>();
        s_ptglSSEs = new ArrayList<SSE>();
        s_sites = new ArrayList<>();
        
        homologuesMap = new HashMap<>();

        // resIndexPDB = new Integer[maxResidues];      // Removed because some PDB files have negative residue numbers, they break this. :/ So we
        //                                              //  have to go through the whole list (which is slow and stupid, bah).
        resIndexDSSP = new Integer[maxResidues];

        
        if(parseData()) {
            dataInitDone = true;
            return(true);
        }
        else {
            System.err.println("ERROR: Could not parse dssp and pdb data.");
            dataInitDone = false;
            System.exit(1);
            return(false);          // for the IDE ;)
        }
    }
    
    /**
     * Like initData but for mmCIF data.
     * @param pf Path to a PDB file. Does NOT test whether it exist, do that earlier.
     * @param df Path to a DSSP file. Does NOT test whether it exist, do that earlier.
     * @return 
     */
    public static Boolean initDataCIF(String pf, String df) {
        
        pdbFile = pf;
        dsspFile = df;
        
        dsspLines = new ArrayList<String>();
        dsspLines = slurpFile(dsspFile, true); // vararg tells the function that this is a dssp file
        if(! FileParser.silent) {
            System.out.println("    Read all " + dsspLines.size() + " lines of file '" + dsspFile + "'.");
        }
        
        s_models = new ArrayList<Model>();
        s_chains = new ArrayList<Chain>();
        s_residues = new ArrayList<Residue>();
        s_atoms = new ArrayList<Atom>();
        s_dsspSSEs = new ArrayList<SSE>();
        s_ptglSSEs = new ArrayList<SSE>();
        // We dont fill it yet, but need to initialize it to avoid Nullpointer exception
        s_allModelIDsFromWholePDBFile = new ArrayList<String>();
        metaData = new HashMap<>();
        
        if(parseDataCIF()) {
            dataInitDone = true;            
            return(true);
        }
        else {
            System.err.println("ERROR: Could not parse dssp and pdb data.");
            dataInitDone = false;
            System.exit(1);
            return(false);          // for the IDE ;)
        }
        
    }


    /**
     * Reads the target text file and returns the data in it.
     * @param file Path to a readable text file. Does NOT test whether it exist, do that earlier.
     * @param bool_dssp boolean vararg. If given: expects one bool which determines wether file is a dssp file.
     * @return all lines of the file as an ArrayList
     */
    public static ArrayList<String> slurpFile(String file, boolean... bool_dssp) {
        
        boolean local_warning = false; // ensures that warning is only printed once
        boolean local_skip_blanks = false;
        
        if (bool_dssp.length > 0) {
            local_skip_blanks = bool_dssp[0];
        }

        ArrayList<String> lines = new ArrayList<String>();

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = in.readLine()) != null) {
                if (! local_skip_blanks) {
                    lines.add(line);
                }
                else {
                    if (! line.isEmpty()) { // skips empty lines and prints warning
                        lines.add(line);
                    }
                    else {
                        if (! local_warning) {
                            DP.getInstance().w("FP", "DSSP File contains empty lines. Parser skips them and tries to go on.");
                            DP.getInstance().w("FP", " ... You should check if the empty line occurs at the end of the file "
                                    + "(b/c of your DSSP-database or text editor you pasted to)");
                            DP.getInstance().w("FP", " ... or if this indicates a severe error. Garbage in -> garbage out.");
                            local_warning = true;
                        }
                    }
                }
            }
	} catch (IOException e) {
            System.err.println("ERROR: Could not read text file '" + file + "'.");
            e.printStackTrace();
            System.exit(1);
	}

        return(lines);
    }
    
    
    /**
     * Reads the target text file and returns the data in it.
     * @param file Path to a readable text file. Does NOT test whether it exist, do that earlier.
     * @return all lines of the file as a single string
     */
    public static String slurpFileToString(String file) throws IOException {

        String lines = "";

        //try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = in.readLine()) != null) {
                lines += line + "\n";
            }
	//} 
        /*catch (IOException e) {
            System.err.println("ERROR: Could not read text file '" + file + "':" + e.getMessage() + ".");
            //e.printStackTrace();
            System.exit(1);
	}
        */

        return(lines);
    }
    
    /**
     * Reads the target text file and returns the data in it.
     * @param file Path to a readable text file. Does NOT test whether it exist, do that earlier.
     * @return the data as a multi line string 
     */
    public static String slurpFileSingString(String file) {

        String lines = "";
        Integer numLines = 0;

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = in.readLine()) != null) {
                numLines++;
                lines += line + "\n";
            }
	} catch (IOException e) {
            System.err.println("ERROR: Could not read text file '" + file + "'.");
            e.printStackTrace();
            System.exit(1);
	}

        return(lines);
    }



    // Reads a PDB file until it hit the model with the ID specified in the argument 'stopAtModelID'.
    // This means that if you set the latter to "3" it will stop reading after the last line of model "2", ignoring
    // the rest of the file.
    private static ArrayList<String> slurpPDBFileToModel(String file, String stopAtModelID) {

        ArrayList<String> lines = new ArrayList<String>();
        Integer numLines = 0;
        String mID = "";

        Boolean error = false;
        Boolean endModelAlreadyHit = false;
        String errMsg = "";

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = in.readLine()) != null) {

                // make sure we abort before the model
                if(line.startsWith("MODEL ")) {

                    try {
                        mID = (line.substring(10, 16)).trim();
                    } catch(Exception e) {
                        System.err.println("ERROR: Hit MODEL line at PDB line number " + (numLines + 1) + " but parsing the line failed.");
                        error = true;
                        errMsg = "ERROR: Hit MODEL line at PDB line number " + (numLines + 1) + " but parsing the line failed.";
                        e.printStackTrace();
                        System.exit(-1);
                    }

                    if( mID.equals(stopAtModelID) || endModelAlreadyHit ) {
                        endModelAlreadyHit = true;
                        if(! FileParser.silent) {
                            System.out.println("  Aborting the parsing of PDB file at line " + (numLines + 1) + " as requested because model " + mID + " starts here.");
                        }
                        break;
                    }
                    else {

                        if(! error) {
                            numLines++;
                            lines.add(line);
                        }
                    }

                }
                else {
                    // just add all other lines
                    numLines++;
                    lines.add(line);
                }
                
            }
	} catch (IOException e) {
            System.err.println("ERROR: Could not read text file '" + file + "'.");
            if(error) {
                System.err.println("ERROR: Parsing line failed: '" + errMsg + "'.");
            }
            e.printStackTrace();
            System.exit(1);
	}

        return(lines);
    }


    // getters
    public static ArrayList<Model> getModels() { return(s_models); }
    public static ArrayList<String> getAllModelIDsFromWholePdbFile() { return(s_allModelIDsFromWholePDBFile); }
    public static ArrayList<Chain> getChains() { return(s_chains); }
    public static ArrayList<Residue> getResidues() { return(s_residues); }
    public static ArrayList<Atom> getAtoms() { return(s_atoms); }
    public static ArrayList<SSE> getDsspSSEs() { return(s_dsspSSEs); }
    public static ArrayList<SSE> getPtglSSEs() { return(s_ptglSSEs); }

    public static HashMap<Character, ArrayList<Integer>> getSulfurBridges() {
        return s_sulfurBridges;
    }

    public static HashMap<Character, ArrayList<Integer>> getInterchainSulfurBridges() {
        return s_interchainSulfurBridges;
    }
    
    
    /**
     * Parses all data, goes through all the lines and calls the appropriate function to handle each line.
     * @return ignore
     */
    private static Boolean parseData() {

        if(! FileParser.silent) {
            System.out.println("  Parsing pdb and dssp file lines...");
        }

        // init class vars
        curLineNumPDB = 0;
        curLineNumDSSP = 0;
        curLinePDB = "";
        curLineDSSP = "";
        curModelID = defaultModelName; 
        curChainID = " ";
        oldChainID = " ";
        oldModelID = "";

        if(! FileParser.silent) {
            System.out.println("  Scanning whole PDB file for models...");
        }
        createAllModelIDsFromWholePdbFile();   // fills s_allModelIDsFromWholePDBFile

        // create all models, chains and residues first. parse atoms afterwards.
        if(! FileParser.silent) {
            System.out.println("  Creating all Models from handled PDB lines...");
        }
        createAllModelsFromHandledPdbLines();   // fills s_models

        if(s_models.size() > 1) {
            System.out.println("ERROR: Found > 1 model (" + s_models.size() + " to be precise) models in the parsed PDB file lines, something went wrong. Exiting.");
            System.exit(1);
        }
        
        if(! FileParser.silent) {
            System.out.println("  Setting chain homologues...");
        }
        setHomologuesOfChains();   // fills homologuesMap     

        if(! FileParser.silent) {
            System.out.println("  Creating all Chains...");
        }
        createAllChainsFromPdbData();   // fills s_chains
        
        if(Settings.getBoolean("plcc_B_parse_binding_sites")) {
            if(! FileParser.silent) {
                System.out.println("  Creating binding sites...");
            }
                createAllBindingSitesFromPdbData(); // fills s_bindingsites
            if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
                for(BindingSite s : s_sites) {
                    System.out.println("    PDB: " + s.toString());
                }
                System.out.println("    Found " + s_sites.size() + " binding sites.");
            }
        }
        

        if(! FileParser.silent) {
            System.out.println("  Creating all Residues...");
        }
        dsspDataStartLine = readDsspToData();
        createAllResiduesFromDsspData(false);    // fills s_residues

        // If there is no data part at all in the DSSP file, the function readDsspToData() will catch
        //  this error and exit, this code will never be reached in that case.
        if(s_residues.size() < 1) {
            System.err.println("ERROR: DSSP file contains no residues (maybe the PDB file only holds DNA/RNA data). Exiting.");
            System.exit(2);
        }

        if(! FileParser.silent) {
            System.out.println("  Creating all Ligand Residues...");
        }
        createAllLigandResiduesFromPdbData();       // adds stuff to s_residues

        if(! FileParser.silent) {
            System.out.println("  Creating all SSEs according to DSSP definition...");
        }
        //s_dsspSSEs = createAllSSEsFromResidueList();     // fills s_dsspSSEs

        //System.out.println("   Done with all DSSP SSEs, ligand SSEs not created yet:");
        //for(Integer i = 0; i < s_dsspSSEs.size(); i++) {
        //    System.out.println("    SSE #" + i + ": " + s_dsspSSEs.get(i) + ".");
        //}

        //System.out.println("  Creating all ligand SSEs...");
        //createAllLigandSSEsFromResidueList();       // adds stuff to s_dsspSSEs

        //System.out.println("  Creating modified SSE list according to PTGL definition...");
        //createAllPtglSSEsFromSSEList();         // fills s_ptglSSEs


        if(! FileParser.silent) {
            System.out.println("  Creating all Atoms...");
        }
        Boolean ignoreRestOfFile = false;
       
        // parse all PDB lines based on format definitions at http://deposit.rcsb.org/adit/docs/pdb_atom_format.html
        for(Integer i = 0; i < pdbLines.size(); i++) {

            curLineNumPDB = i + 1;
            curLinePDB = pdbLines.get(i);

            //System.out.println("  PDB line " + currentLineNumPDB + ": " + currentLinePDB + "");


            // handle MODEL lines
            if(curLinePDB.startsWith("MODEL ")) {
                handlePdbLineMODEL();
            }

            // handle any ATOM lines (ATOM, HETATM)
            else if(curLinePDB.startsWith("ATOM  ") || curLinePDB.startsWith("HETATM")) {
                handlePdbLineANYATOM();
            }

            //  handle TER lines
            else if(curLinePDB.startsWith("TER   ")) {
                handlePdbLineTER();
            }

            //  handle all other lines
            else {
                handlePdbLineOTHER();
            }

        }

        if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
            System.out.println("    PDB: Hit end of PDB file at line " + curLineNumPDB + ".");

            // remove duplicate atoms from altLoc here
            System.out.println("    PDB: Selecting alternative locations for atoms of all residues.");
        }
        ArrayList<Atom> deletedAtoms;
        int numAtomsDeletedAltLoc = 0;
        int numResiduesAffected = 0;
        Residue r;
        for(int i = 0; i < s_residues.size(); i++) {
            r = s_residues.get(i);
            deletedAtoms = r.chooseYourAltLoc();
            
            //if(r.getPdbResNum() == 209) {
            //    System.out.println("DEBUG: ===> Residue " + r.toString() + " at list position " + i + " <===.");
            //}
            
            if(deletedAtoms.size() > 0) {
                numResiduesAffected++;
            }
            
            //delete atoms from global atom list as well
            for(Atom a : deletedAtoms) {
                if(s_atoms.remove(a)) {
                    numAtomsDeletedAltLoc++;
                } else {
                    DP.getInstance().w("Atom requested to be removed from global list does not exist in there.");
                }
            }
        }
        
        // assign residues to binding sites
        if(Settings.getBoolean("plcc_B_parse_binding_sites")) {
            Integer siteResPdbResNum; String siteResChainID; String siteResName;
            int numResAssigned = 0;
            int numWaterResIgnored = 0;
            for(BindingSite s : s_sites) {
                List<String[]> siteResidueInfos = s.getResidueInfos();
                // try-catch for coping with the Int/Str-ParseError if insertion codes
                //    are used in the SITE fields
                try {
                    for(String[] resInfo : siteResidueInfos) {

                        siteResPdbResNum = Integer.parseInt(resInfo[2]);

                        siteResChainID = resInfo[1];
                        siteResName = resInfo[0];
                        Residue res = FileParser.getResByPdbFields(siteResPdbResNum, siteResChainID, null);
                        if(res != null) {
                            res.addPartOfBindingSite(s);
                            numResAssigned++;
                        } else {
                            if(siteResName.toUpperCase().equals("HOH")) {
                                numWaterResIgnored++;
                            }
                            else {
                                DP.getInstance().w("FileParser", "Could not assign residue " + siteResName + " #" + siteResPdbResNum + " of chain " + siteResChainID + " to binding site, no such residue found in residue list.");
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    DP.getInstance().w("FP", "NumberFormatException while parsing the PDB-File.");
                    DP.getInstance().w("FP", "Trying to go on now. Set plcc_B_parse_binding_sites=false "
                            + "if insertion codes are used in the SITE fields "
                            + "(skips the detection of binding sites then).");
                }                
            }
            if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
                System.out.println("    PDB: Assigned " + numResAssigned + " residues to be part of a binding site (ignored " + numWaterResIgnored + " waters).");
            }
        }
        
        
        if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
            System.out.println("    PDB: Deleted " + numAtomsDeletedAltLoc + " duplicate atoms from " + numResiduesAffected + " residues which had several alternative locations.");

            // report statistics
            System.out.println("  All data parsed. Found " + s_models.size() + " models, " +
                                                           s_chains.size() + " chains, " +
                                                           s_residues.size() + " residues, " +
                                                           s_atoms.size() + " atoms.");
        }

        return(true);
    }
    
    private static String[] lineToArrayCIF(String line) {
        String tmpReturnList[] = new String[line.split(" ").length];
        String tmpLineList[];
        int counterValues = 0;
        tmpLineList = line.split(" ");
        
        for (int i=0; i < tmpLineList.length; i++) {
            if (! tmpLineList[i].isEmpty()) {
                tmpReturnList[counterValues] = tmpLineList[i];
                counterValues++;
            }
        }
        
        // TODO aktuell viele NULL Einträge da Größe festgesetzt
        
        return tmpReturnList;
    }
    
    private static Chain createChain(String cID, Model m) {
        System.out.println(cID);
        Chain c = new Chain(cID);
        c.setModel(m);
        c.setModelID(m.getModelID());
        m.addChain(c);
        s_chains.add(c);
        if (! (FileParser.silent || FileParser.essentialOutputOnly)) {
            System.out.println("   PDB: New chain named " + cID + " found.");
        }
        return c;
    }
    
    /**
     * Like parseData but for mmCIF files: goes through all lines of PDB and DSSP file and calls appropriate function to handle each line. 
     * @return ignore (?)
     */
    private static Boolean parseDataCIF() {
        // - - - DSSP - - -
        //
        // - - residues - -
        // same like in parseData() only Boolean argument changed
        //     -> make own function
        if(! FileParser.silent) {
            System.out.println("  Creating all Residues...");
        }
        dsspDataStartLine = readDsspToData();
        // fills s_residues using AUTHCHAIN for chain ids
        // we need to do this here to get DsspResNum
        createAllResiduesFromDsspData(true);

        // If there is no data part at all in the DSSP file, the function readDsspToData() will catch
        //  this error and exit, this code will never be reached in that case.
        if(s_residues.size() < 1) {
            System.err.println("ERROR: DSSP file contains no residues (maybe the PDB file only holds DNA/RNA data). Exiting.");
            System.exit(2);
        }
        
        // - - ligands - -
        // -> in difference to old parser ligands are created "on the fly" together with the other residues
        //     -> is that ok or do we need the DsspResNum???
        /*
        if(! FileParser.silent) {
            System.out.println("  Creating all Ligand Residues...");
        }
        createAllLigandResiduesFromPdbData();       // adds stuff to s_residues
        */
        
        
        // - - - PDB - - - 
        //
        // idea: read each line once and dont save them. If it works that would save time and space.
        // lets do this for all the basic stuff first and neglect models, sites etc
        //     -> atoms, residues (s.a.), chains, SSEs (?)
        //     -> do the matching atom <-> residue, residue <-> chain later
        //         -> actually try to do it on the fly
        
        // for now local variables, may be needed as class variable though
        Boolean dataBlockFound = false; // for now only parse the first data block (stop if seeing 2nd block)
        Boolean inLoop = false;
        
        // variables for one loop (reset when hitting new loop)
        String tableCategory = null;
        ArrayList<String> tableColHeads = new ArrayList<>();
        // importantColInd holds indices of the important columns
        // VALUES: default value -1: column not existing; -2: column not existing and warning has been printed
        // INDICES: 0: chain name; 1: PDBx field name; 2: atom id; 3: (detailed) atom name 4: alternative location
        // 5: residue names (label_comp_id); 6: residue numbers (label_seq_id); 7: insertion code
        // 8,9,10: coordx,y,z, 11: chemical symbol, 12: label_seq_id (PDB Res Num)
        int[] importantColInd = new int[13]; 
        
        // variables per (atom) line
        Integer atomSerialNumber, resNumPDB, coordX, coordY, coordZ;
        String atomRecordName, atomName, resNamePDB, chainID, chemSym, altLoc, iCode;
        Double oCoordX, oCoordY, oCoordZ;            // the original coordinates in Angstroem (coordX are 10th part Angstroem)
        Float oCoordXf, oCoordYf, oCoordZf;
        int lastLigandNumPDB = 0; // used to determine if atom belongs to new ligand residue
        String lastChainID = ""; // s.a.
        String[] tmpLineData;
        
        // variables for successive matching atom -> residue -> chain
        // remember them so we dont need to lookup
        Residue tmpRes = null;
        Chain tmpChain = null;
        Residue lig = null;
        
        Integer numLine = 0;
        
        // for now create a default model with ID '1'
        // usually there should be no model nevertheless
        //     -> only used in nmr and on those splitpdb should be used (doesnt work for CIF?!)
        Model m = new Model("1");
        s_models.add(m);
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(pdbFile));
            String line;
            while ((line = in.readLine()) != null) {
                numLine ++;
                // first, check if line is a comment
                if (! line.startsWith("#")) {
                    
                    // check for data block
                    if (line.startsWith("data_")) {
                        if (dataBlockFound) {
                            DP.getInstance().w("[FP_CIF]", " Parsing of first data block ended at line " + numLine.toString()
                                + "as right now only the first data block is parsed.");
                            break; // for now we only parse first data block
                        }
                        else {
                            dataBlockFound = true;
                            if (line.length() > 5) {
                                if (! silent) {
                                    System.out.println("  PDB: Found the first data block named: " + line.subSequence(5, line.length()));
                                }
                            }
                            else {
                                if (! silent) {
                                    System.out.println("  PDB: Found the first data block (without a name).");
                                }
                            }
                        }
                    }
                    
                    // check for experiment method (meta data)
                    if (line.startsWith("_exptl.method")) {
                        tmpLineData = lineToArrayCIF(line);
                        if (tmpLineData.length > 1) {
                            if (! (tmpLineData[1] == "?" || tmpLineData[1] == ".")) {
                                metaData.put("experiment", tmpLineData[1]);
                            } else {
                                metaData.put("experiment", "");
                            }
                        }
                    }
                    
                    // check for resolution (meta data)
                    // TODO: really the best attribute(s) to do this?
                    if (line.startsWith("_reflns.d_resolution_high") || line.startsWith("_reflns.d_res_high") || line.startsWith("_refine.ls_d_res_high")) {
                        tmpLineData = lineToArrayCIF(line);
                        if (tmpLineData.length > 1) {
                            if (! (tmpLineData[1] == "?" || tmpLineData[1] == ".")) {
                                metaData.put("resolution", tmpLineData[1]);
                            } else {
                                metaData.put("resolution", "");
                            }
                        }
                    }
                    
                    // handle line when in loop
                    if (inLoop) {
                        
                        // check if we need data from this loop or if we can skip it
                        if (! (tableCategory == null)) {
                            if (! tableCategory.equals("_atom_site")) {
                                continue;
                            }
                        }
                        
                        // check for column heads
                        if (line.startsWith("_")) {
                            if (tableCategory == null) {
                                if (line.split("\\.").length < 2) {
                                    DP.getInstance().w("[FP_CIF]", " Expected table definition in line " + 
                                            numLine.toString() + " but couldnt parse it. Skip it (may miss important data!).");
                                } else {
                                    tableCategory = line.split("\\.")[0];
                                    if (! tableCategory.equals("_atom_site")) {
                                        continue;
                                    }
                                }
                            }
                            tableColHeads.add(line.split("\\.")[1].trim());
                            
                            // check if important columns are spotted and remember their place in importantColInd
                            switch (tableColHeads.get(tableColHeads.size() - 1)) {
                                // nice to know: break is neccessary to prevent fallthrough
                                // 0: chain name, prioritize auth_asym_id > label_asym_id
                                case "label_asym_id":
                                    if (importantColInd[0] == -1) {
                                        importantColInd[0] = tableColHeads.size() - 1;
                                    }
                                    break;
                                case "auth_asym_id":
                                    importantColInd[0] = tableColHeads.size() - 1;
                                    break;
                                // 1: PDBx field name
                                case "group_PDB":
                                    importantColInd[1] = tableColHeads.size() - 1;
                                    break;
                                // 2: atom id
                                case "id":
                                    importantColInd[2] = tableColHeads.size() - 1;
                                    break;
                                // 3: (detailed) atom name
                                case "label_atom_id":
                                    importantColInd[3] = tableColHeads.size() - 1;
                                    break;
                                // 4: (detailed) atom name
                                case "label_alt_id":
                                    importantColInd[4] = tableColHeads.size() - 1;
                                    break;
                                // 5: residue name
                                case "label_comp_id":
                                    importantColInd[5] = tableColHeads.size() - 1;
                                    break;
                                // 6: residue number
                                // case "label_seq_id":
                                // use author provided data to match dssp
                                // -> remember both so we can choose later (s.b.)
                                case "auth_seq_id":
                                    importantColInd[6] = tableColHeads.size() - 1;
                                    break;
                                // 7: insertion code
                                case "pdbx_PDB_ins_code":
                                    importantColInd[7] = tableColHeads.size() - 1;
                                    break;
                                // 8: coordX
                                case "Cartn_x":
                                    importantColInd[8] = tableColHeads.size() - 1;
                                    break;
                                // 9: coordY
                                case "Cartn_y":
                                    importantColInd[9] = tableColHeads.size() - 1;
                                    break;
                                // 10: coordZ
                                case "Cartn_z":
                                    importantColInd[10] = tableColHeads.size() - 1;
                                    break;
                                // 11: chemical symbol
                                case "type_symbol":
                                    importantColInd[11] = tableColHeads.size() - 1;
                                    break;
                                // 12: label_seq_id -> PDB Res Num
                                case "label_seq_id":
                                    importantColInd[12] = tableColHeads.size() - 1;
                                    break;
                            }

                            // TODO update important ColIndexes for rest
                            
                        } else {
                            // we are in the row section (data!)
                            tmpLineData = lineToArrayCIF(line);
                            
                            // check for a new chain (always hold the current 
                            if (importantColInd[0] >= 0) {
                                if (tmpLineData.length >= importantColInd[0]) {
                                    String tmp_cID = tmpLineData[importantColInd[0]];
                                    if (tmpChain == null) {
                                        tmpChain = createChain(tmp_cID, m);
                                    } else 
                                        if (! (tmpChain.getPdbChainID().equals(tmp_cID))) {
                                            tmpChain = createChain(tmp_cID, m);
                                        }
                                } else {
                                    DP.getInstance().w("[FP_CIF]", " Line " + numLine + " should contain a value in column " + 
                                            importantColInd[0] + " (expected chain name) but didnt. Skipping line.");
                                }  
                            }
                            
                            // - - atom - -
                            // reset variables
                            atomSerialNumber = resNumPDB = coordX = coordY = coordZ = null;
                            atomRecordName = atomName = resNamePDB = chainID = chemSym = altLoc = null;
                            iCode = " "; // if column does not exist or ? || . is assigned use 1 blank (compare old parser)
                            oCoordX = oCoordY = oCoordZ = null;            // the original coordinates in Angstroem (coordX are 10th part Angstroem)
                            oCoordXf = oCoordYf = oCoordZf = null;
                            
                            // chain name => 0
                            if (importantColInd[0] > -1) {
                                chainID = tmpLineData[importantColInd[0]];
                            } else {
                                if (importantColInd[1] == -1) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().w("[FP_CIF", "Seems like both _atom_site.label_asym_id and .auth_asym_id are missing. Trying to ignore it.");
                                    }    
                                    importantColInd[1] = -2;
                                } 
                            }
                            
                            // PDBx field alias atom record name => 1
                            if (importantColInd[1] > -1) {
                                atomRecordName = tmpLineData[importantColInd[1]];
                            } else {
                                if (importantColInd[1] == -1) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().w("[FP_CIF", "Seems like _atom_site.group_PDB is missing. Trying to ignore it.");
                                    }    
                                    importantColInd[1] = -2;
                                } 
                            }
                            
                            // atom id alias serial number => 2
                            if (importantColInd[2] > -1) {
                                atomSerialNumber = Integer.valueOf(tmpLineData[importantColInd[2]]); // there should be no need to trim as whitespaces should be ignored earlier
                            } else {
                                if (importantColInd[2] == -1) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().w("[FP_CIF", "Seems like _atom_site.id is missing. Trying to ignore it.");
                                    }    
                                    importantColInd[2] = -2;
                                }
                            }
                            
                            // detailed atom name => 3
                            if (importantColInd[3] > -1) {
                                // old PDB files used spacing to differentiate between atoms
                                // e.g. " CA " = C alpha, how to deal with this? mmCIF has no spacings
                                // for now workaround for probable C alpha
                                if (tmpLineData[importantColInd[3]].equals("CA")) {
                                    atomName = " " + tmpLineData[importantColInd[3]] + " ";
                                } else {
                                    atomName = tmpLineData[importantColInd[3]];
                                }
                                
                            } else {
                                if (importantColInd[3] == -1) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().w("[FP_CIF", "Seems like _atom_site.label_atom_id is missing. Trying to ignore it.");
                                    }    
                                    importantColInd[3] = -2;
                                }
                            }
                            
                            // alternative location => 4
                            if (importantColInd[4] > -1) {
                                altLoc = tmpLineData[importantColInd[4]];
                            } else {
                                if (importantColInd[4] == -1) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().w("[FP_CIF", "Seems like _atom_site.label_alt_loc is missing. Trying to ignore it.");
                                    }    
                                    importantColInd[4] = -2;
                                }
                            }
                            
                            // residue name => 5
                            if (importantColInd[5] > -1) {
                                resNamePDB = tmpLineData[importantColInd[5]];
                            } else {
                                if (importantColInd[5] == -1) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().w("[FP_CIF", "Seems like _atom_site.label_comp_id is missing. Trying to ignore it.");
                                    }    
                                    importantColInd[5] = -2;
                                }
                            }
                            
                            // residue number => 6
                            // use auth_seq_id > label_seq_id (hope DSSP does so too)
                            if (importantColInd[6] > -1) {
                                resNumPDB = Integer.valueOf(tmpLineData[importantColInd[6]]);
                            } else {
                                if (importantColInd[12] > -1) {
                                    // use label_seq_id instead
                                    resNumPDB = Integer.valueOf(tmpLineData[importantColInd[12]]);
                                } else {
                                    if (importantColInd[6] == -1) {
                                        if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                            DP.getInstance().w("[FP_CIF", "Seems like _atom_site.auth_seq_id AND label_seq_id is missing. Trying to ignore it.");
                                        }    
                                        importantColInd[6] = -2;
                                    }
                                }
                            }
                            
                            // insertion code => 7
                            // only update if column and value exist, otherwise stick to blank ""
                            if (importantColInd[7] > -1) {
                                if (! (tmpLineData[importantColInd[7]].equals("?") || tmpLineData[importantColInd[7]].equals("."))) {
                                    iCode = tmpLineData[importantColInd[7]];
                                }
                            } else {
                                if (importantColInd[7] == -1) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().w("[FP_CIF", "Seems like _atom_site.pdbx_PDB_ins_code is missing. Trying to ignore it.");
                                    }   
                                    importantColInd[7] = -2;
                                }
                            }
                            
                            // coordX => 8
                            if (importantColInd[8] > -1) {
                                // for information on difference between ptgl and plcc style look in old parser
                                if (Settings.getBoolean("plcc_B_strict_ptgl_behaviour")) {
                                    oCoordX = Double.valueOf(tmpLineData[importantColInd[8]]) * 10.0;
                                    coordX = oCoordX.intValue();
                                } else {
                                    oCoordXf = Float.valueOf(tmpLineData[importantColInd[8]]) * 10;
                                    coordX = Math.round(oCoordXf);
                                }
                                
                            } else {
                                if (importantColInd[8] == -1) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().e("[FP_CIF", "Seems like _atom_site.Cartn_x is missing. Exiting now.");
                                    }    
                                    System.exit(1);
                                }
                            }
                            
                            // coordY => 9
                            if (importantColInd[9] > -1) {
                                if (Settings.getBoolean("plcc_B_strict_ptgl_behaviour")) {
                                    oCoordY = Double.valueOf(tmpLineData[importantColInd[9]]) * 10.0;
                                    coordY = oCoordY.intValue();
                                } else {
                                    oCoordYf = Float.valueOf(tmpLineData[importantColInd[9]]) * 10;
                                    coordY = Math.round(oCoordYf);
                                }
                                
                            } else {
                                if (importantColInd[9] == -1) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().e("[FP_CIF", "Seems like _atom_site.Cartn_y is missing. Exiting now.");
                                    }    
                                    System.exit(1);
                                }
                            }
                            
                            // coordY => 10
                            if (importantColInd[10] > -1) {
                                if (Settings.getBoolean("plcc_B_strict_ptgl_behaviour")) {
                                    oCoordZ = Double.valueOf(tmpLineData[importantColInd[10]]) * 10.0;
                                    coordZ = oCoordZ.intValue();
                                } else {
                                    oCoordZf = Float.valueOf(tmpLineData[importantColInd[10]]) * 10;
                                    coordZ = Math.round(oCoordZf);
                                }
                                
                            } else {
                                if (importantColInd[10] == -1) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().e("[FP_CIF", "Seems like _atom_site.Cartn_z is missing. Exiting now.");
                                    }    
                                    System.exit(1);
                                }
                            }
                            
                            // chemical symbol => 11
                            if (importantColInd[11] > -1) {
                                chemSym = tmpLineData[importantColInd[11]];
                            } else {
                                if (importantColInd[11] == -1) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().w("[FP_CIF", "Seems like _atom_site.type_symbol is missing. Trying to ignore it.");
                                    }   
                                    importantColInd[11] = -2;
                                }
                            }
                            
                            // TODO: possible to ignore alt loc atoms right now?
                            
                            // Files that contain DNA or RNA are not supported atm
                            if(FileParser.isDNAorRNAresidueName(resNamePDB)) {
                                if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                    DP.getInstance().w("Atom #" + atomSerialNumber + " in PDB file belongs to DNA/RNA residue (residue 3-letter code is '" + resNamePDB + "'), skipping.");
                                }
                                continue; // do not use that atom
                            }
                            
                            // update (only if needed) -> get DsspResNum for atom from res
                            // match res <-> chain here 
                            if (! atomRecordName.equals("HETATM")) {
                                if (tmpRes == null) {
                                    tmpRes = getResidueFromList(resNumPDB, chainID, iCode);
                                    tmpRes.setChain(tmpChain);
                                    tmpChain.addResidue(tmpRes);
                                } else {
                                    if (! (resNumPDB == tmpRes.getPdbResNum())) {
                                        tmpRes = getResidueFromList(resNumPDB, chainID, iCode);
                                        tmpRes.setChain(tmpChain);
                                        tmpChain.addResidue(tmpRes);
                                    }
                                }
                            }
                            
                            Atom a = new Atom();
                            
                            // handle stuff that's different between ATOMs and HETATMs
                            if(atomRecordName.equals("ATOM")) {
                                if (isIgnoredAtom(chemSym)) {
                                    if( ! (Settings.getBoolean("plcc_B_handle_hydrogen_atoms_from_reduce") && chemSym.trim().equals("H"))) {
                                        continue;
                                    }
                                }
                                
                                // old parser has here some outcommented code on N termini
                                
                                // set atom type
                                a.setAtomtype(Atom.ATOMTYPE_AA);
                                
                                // only ATOMs, not HETATMs, have a DSSP entry
                                //a.setDsspResNum(getDsspResNumForPdbResNum(resNumPDB));
                                if((Settings.getBoolean("plcc_B_handle_hydrogen_atoms_from_reduce") && chemSym.trim().equals("H"))) {
                                    a.setDsspResNum(null);
                                }
                                else {
                                    // a.setDsspResNum(getDsspResNumForPdbFields(resNumPDB, chainID, iCode));
                                    a.setDsspResNum(tmpRes.getDsspResNum());
                                }
                                
                                
                            } else if (atomRecordName.equals("HETATM")) {
                                
                                // idea: add always residue (for consistency) but atom only if needed
                                
                                String lf, ln, ls;      // temp for lig formula, lig name, lig synonyms
                                
                                Integer curLigNum = 0;
                                
                                if( ! ( resNumPDB.equals(lastLigandNumPDB) && chainID.equals(lastChainID) ) ) {
                                    curLigNum++;
                                    
                                    // create new Residue from info, we'll have to see whether we really add it below though
                                    lig = new Residue();
                                    lig.setPdbResNum(resNumPDB);
                                    lig.setType(Residue.RESIDUE_TYPE_LIGAND);
                                    // do we need this?
                                    //resNumDSSP = getLastUsedDsspResNumOfDsspFile() + curLigNum; // assign an unused fake DSSP residue number
                                    //lig.setDsspResNum(resNumDSSP);
                                    lig.setChainID(chainID);
                                    lig.setiCode(iCode);
                                    lig.setResName3(resNamePDB);
                                    lig.setAAName1(AminoAcid.getLigandName1());
                                    lig.setChain(getChainByPdbChainID(chainID));
                                    // still ignoring models!
                                    //lig.setModelID(modelID);
                                    lig.setSSEString(Settings.get("plcc_S_ligSSECode"));
                                    
                                    
                                    // add ligand to list of residues if it not on the ignore list
                                    if(isIgnoredLigRes(resNamePDB)) {
                                        curLigNum--;    // We had to increment before to determine the fake DSSP res number, but
                                                        //  this ligand won't be stored so decrement to previous value.
                                        //System.out.println("    PDB: Ignored ligand '" + resNamePDB + "-" + resNumPDB + "' at PDB line " + pLineNum + ".");
                                    } else {
                                        
                                        // ignore this for now: needs parsing of two more loops (_pdbx_nonpoly_scheme, _chem_comp)   	 

                                        /*                                        
                                        // add info from PDB HET fields (HET, HETNAM, HETSYN, FORMUL)
                                        // Note: we now use prepared statements so any strange chars do no longer lead to irritations or security trouble
                                        Boolean removeStuff = Settings.getBoolean("plcc_B_uglySQLhacks");
                                        lf = getLigFormula(resNamePDB);
                                        if(removeStuff) {
                                            lf = lf.replaceAll("\\s", "");               // remove all whitespace
                                            lf = lf.replaceAll("~", "");                 // remove tilde char (it causes SQL trouble during DB insert)
                                            lf = lf.replaceAll("\\\\", "");              // remove all backslashes (it causes SQL WARNING 'nonstandard use of escape in a string literal' during DB insert)
                                            lf = lf.replaceAll("'", "");              // remove all ticks (obviously SQL trouble)
                                        }

                                        ln = getLigName(resNamePDB);
                                        if(removeStuff) {
                                            ln = ln.replaceAll(" ", "_");                // replace spaces with underscores
                                            ln = ln.replaceAll("\\s", "");               // remove all other whitespace
                                            ln = ln.replaceAll("~", "");
                                            ln = ln.replaceAll("\\\\", "");
                                            ln = ln.replaceAll("'", "");
                                        }

                                        ls = getLigSynonyms(resNamePDB);
                                        if(removeStuff) {
                                            ls = ls.replaceAll(" ", "_");
                                            ls = ls.replaceAll("\\s", "");
                                            ls = ls.replaceAll("~", "");
                                            ls = ls.replaceAll(";", ".");
                                            ls = ls.replaceAll("\\\\", "");
                                            ls = ls.replaceAll("'", "");
                                        }

                                        lig.setLigFormula(lf);
                                        lig.setLigName(ln);
                                        lig.setLigSynonyms(ls);
                                        
                                        */

                                        lastLigandNumPDB = resNumPDB;
                                        lastChainID = chainID;

                                        //TODO: Add a check for the molecular weight of the ligand here and only add ligands
                                        //      which are within the range (range should be defined in cfg file).
                                        //      Problem atm is that the mol weight is not in the PDB file. Idea: count the atoms
                                        //      instead, use a range over number of atoms.

                                        s_residues.add(lig);

                                        getChainByPdbChainID(chainID).addResidue(lig);

                                        // do we need this?
                                        //resIndex = s_residues.size() - 1;
                                        //resIndexDSSP[resNumDSSP] = resIndex;
                                        //resIndexPDB[resNumPDB] = resIndex;      // This will crash because some PDB files contain negative residue numbers so fuck it.
                                        if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
                                            System.out.println("   PDB: Added ligand '" +  resNamePDB + "-" + resNumPDB + "', chain " + chainID + " (line " + numLine + ", ligand #" + curLigNum + ", DSSP # WHERE FROM?" + ").");
                                            System.out.println("   PDB:   => Ligand name = '" + lig.getLigName() + "', formula = '" + lig.getLigFormula() + "', synonyms = '" + lig.getLigSynonyms() + "'.");
                                        }

                                    }
                                }
                                   
                                
                                if(isIgnoredLigRes(resNamePDB)) {
                                    a.setAtomtype(Atom.ATOMTYPE_IGNORED_LIGAND);       // invalid ligand (ignored)

                                    // We do not need these atoms and they may lead to trouble later on, so
                                    //  just return without adding the new Atom to any Residue here so this line
                                    //  is skipped and the next line can be handled.
                                    //  If people want all ligands they have to change the isIgnoredLigRes() function.
                                    continue; // can we do this here? Does it cut off other important stuff?
                                }
                                else {
                                    a.setAtomtype(Atom.ATOMTYPE_LIGAND);       // valid ligand
                                    //a.setDsspResNum(getDsspResNumForPdbFields(resNumPDB, chainID, iCode));  // We can't do this because the fake DSSP residue number has not yet been assigned
                                }
                                
                                
                            }
                            
                            // now create the new Atom
                            
                            // tmpRes ist now created / updated above
                            // speedup: only look for new residue if needed (atoms belonging to one res are grouped)
                            /*
                            if (! (tmpRes == null)) {
                                if (tmpRes.getChainID() == chainID && tmpRes.getiCode().equals(iCode) && tmpRes.getPdbResNum() == resNumPDB) {
                                    System.out.println("Speedup!");
                                } else {
                                    tmpRes = getResidueFromList(resNumPDB, chainID, iCode);
                                }
                            } else {
                                tmpRes = getResidueFromList(resNumPDB, chainID, iCode);
                            }
                            */
                            // Note that the command above may have returned NULL, we care for that below

                            a.setPdbAtomNum(atomSerialNumber);
                            a.setAtomName(atomName);
                            a.setAltLoc(altLoc);
                            a.setResidue(tmpRes);
                            a.setChainID(chainID);        
                            a.setChain(getChainByPdbChainID(chainID));
                            a.setPdbResNum(resNumPDB);
                            // we cant get the DSSP res num easily here and have to do it later (I guess)
                            // old parser seems to assign 0 here whatsoever so we just leave the default value there
                            // a.setDsspResNum(resNumDSSP);
                            a.setCoordX(coordX);
                            a.setCoordY(coordY);
                            a.setCoordZ(coordZ);
                            a.setChemSym(chemSym);
                            
                            // from old parser, not working with models right now
                            /*
                            if(curModelID != null) {
                                a.setModelID(curModelID);
                                a.setModel(getModelByModelID(curModelID));
                            }
                            */
                            
                            if (tmpRes == null) {
                                DP.getInstance().w("Residue with PDB # " + resNumPDB + " of chain '" + chainID + "' with iCode '" + iCode + "' not listed in DSSP data, skipping atom " + atomSerialNumber + " belonging to that residue (PDB line " + numLine.toString() + ").");
                                continue;
                            } else {

                                if(Settings.getBoolean("plcc_B_handle_hydrogen_atoms_from_reduce") && chemSym.trim().equals("H")) {
                                    tmpRes.addHydrogenAtom(a);
                                }
                                else {
                                    tmpRes.addAtom(a);
                                    s_atoms.add(a);
                                }
                            }
                            
                            if (! (lig == null)) {
                                if (atomRecordName.equals("HETATM")) {
                                    lig.addAtom(a);
                                    a.setResidue(lig);
                                }
                            }
                            
                        }
                    } else {
                        // loops must not be nested according to file format definition
                        //     that is why we can check for a new loop here
                        // check if new loop starts (check for loop end is done with # in beginning)
                        if (line.startsWith("loop_")) {
                            inLoop = true;
                            
                            // reset vars per loop
                            tableCategory = null;
                            tableColHeads.clear();
                            Arrays.fill(importantColInd, -1);
                            lastLigandNumPDB = 0;
                            chainID = "";
                        }
                    }
                } else {
                    // # seems to stand between each category, we use it to decide if loop ended
                    //     and hope it does not occur inside a loop
                    inLoop = false;
                }
            }
            
            if (! (silent || FileParser.essentialOutputOnly)) {
                System.out.println("  PDB: Found in total " + s_chains.size() + " chains.");
            }
            
	} catch (IOException e) {
            System.err.println("ERROR: Could not parse PDB file.");
            System.err.println("ERROR: Message: " + e.getMessage());
            System.exit(1);
	}
        // all lines have beenn read
        return(true);
    }


    


    /**
     * Handles PDB MODEL lines.
     * @return ignored
     */
    private static boolean handlePdbLineMODEL() {
        // Now handled by function createAllModelsFromPdbData() because the models have to exist
        //  before we create the other stuff (atoms, residues, etc.) so we can assign the models to them.
        // This function still updates the global 'curModelID' variable that's used by other functions (the one that
        // creates that atoms) though, so it is still required.
        String mID = "";

        try {
            mID = (curLinePDB.substring(10, 16)).trim();
        } catch(Exception e) {
            System.err.println("ERROR: Hit MODEL line at PDB line number " + curLinePDB + " but parsing the line failed.");
            e.printStackTrace();
            System.exit(1);
        }

        curModelID = mID;
        return(true);
    }

    /**
     * Handle PDB ATOM and HETATM lines. Currently this function creates all the atoms. It tracks chains etc via global variables. It would be better if this was 
     * turned into a function createAllAtomsFromPdbData() that works like the createAll* functions.
     * @return true if the line could be parsed, false otherwise
     */
     private static boolean handlePdbLineANYATOM() {
        

        Integer atomSerialNumber, resNumPDB, resNumDSSP;
        Integer coordX, coordY, coordZ;
        atomSerialNumber = resNumPDB = resNumDSSP = coordX = coordY = coordZ = 0;
        String atomRecordName, atomName, resNamePDB, chainID, chemSym, altLoc, iCode;
        atomRecordName = atomName = resNamePDB = chainID = chemSym = altLoc = iCode = "";
        Double oCoordX, oCoordY, oCoordZ;            // the original coordinates in Angstroem (coordX are 10th part Angstroem)
        oCoordX = oCoordY = oCoordZ = 0.0;
        Float oCoordXf, oCoordYf, oCoordZf;
        oCoordXf = oCoordYf = oCoordZf = 0.0f;

        try {
            atomRecordName = curLinePDB.substring(0, 6).trim();
            atomSerialNumber = Integer.valueOf((curLinePDB.substring(6, 11)).trim());
            // 11 is ignored: blank
            atomName = curLinePDB.substring(12, 16);
            altLoc = curLinePDB.substring(16, 17);
            resNamePDB = curLinePDB.substring(17, 20);
            // 20 is ignored: blank
            chainID = curLinePDB.substring(21, 22);
            resNumPDB = Integer.valueOf((curLinePDB.substring(22, 26)).trim());
            iCode = curLinePDB.substring(26, 27);       // don't trim this!
            // 27 - 29 are ignored: blanks
            
            
            
            if(Settings.getBoolean("plcc_B_strict_ptgl_behaviour")) {                            
                // PTGL style: always round them down, i.e., simply ignore the last 2 digits
                oCoordX = Double.valueOf((curLinePDB.substring(30, 38)).trim()) * 10.0;
                oCoordY = Double.valueOf((curLinePDB.substring(38, 46)).trim()) * 10.0;
                oCoordZ = Double.valueOf((curLinePDB.substring(46, 54)).trim()) * 10.0;
                
                // now, an example PDB file value of "8.796" was transformed into -87.96f                
                
                coordX = oCoordX.intValue();
                coordY = oCoordY.intValue();
                coordZ = oCoordZ.intValue();                                
                
                //System.out.println("[atom#" + atomSerialNumber + "] oCoords=(" + oCoordX + "," + oCoordY + "," + oCoordZ + "), Coords=(" + coordX + "," + coordY + "," + coordZ + ")");
                
            }
            else {
                
                
                // PLCC style: round the coordinates
                oCoordXf = Float.valueOf((curLinePDB.substring(30, 38)).trim()) * 10;
                oCoordYf = Float.valueOf((curLinePDB.substring(38, 46)).trim()) * 10;
                oCoordZf = Float.valueOf((curLinePDB.substring(46, 54)).trim()) * 10;
   
                coordX = Integer.valueOf(Math.round(oCoordXf));
                coordY = Integer.valueOf(Math.round(oCoordYf));
                coordZ = Integer.valueOf(Math.round(oCoordZf));
                
                // now, is has become 88
                
                //System.out.println("[atom#" + atomSerialNumber + "] oCoords=(" + oCoordXf + "," + oCoordYf + "," + oCoordZf + "), Coords=(" + coordX + "," + coordY + "," + coordZ + ")");
            }                        
    
            
            // 54 - 59 are ignored: occupancy
            // 60 - 65 are ignored: temp factor
            // 66 - 72 are ignored: blanks
            // 72 - 75 are ignored: segment identifier
            chemSym = curLinePDB.substring(76, 78);
            
            
            //if(chemSym.trim().equals("H")) {
            //    System.out.println("Found hydrogen line '" + curLinePDB + "'.");
            //}
            
            // 78 - 79 are ignored: atom charge
        } catch(Exception e) {
            System.err.println("ERROR: Hit ATOM/HETATM line at PDB line number " + curLineNumPDB + " but parsing the line failed (length " + curLinePDB.length() + "): '" + e.getMessage() + "'.");
            return false;
            //System.exit(1);
        }

        // Ignore alternate location identifiers (only use the 1st one. It is identified by an altLoc field that is empty, "A" or "1").
        //if( ! (altLoc.equals(" ") || altLoc.equals("A") || altLoc.equals("1") )) {
        //    //System.out.println("INFO: Ignored alternate atom location identifier '" + altLoc + "' for atom #" + atomSerialNumber + " in PDB file.");
        //    return(false);
        //}

        // Files that contain DNA or RNA are not supported atm
        if(FileParser.isDNAorRNAresidueName(resNamePDB)) {
            if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                DP.getInstance().w("Atom #" + atomSerialNumber + " in PDB file belongs to DNA/RNA residue (residue 3-letter code is '" + resNamePDB + "'), skipping.");
            }
            return false;
        }

        Atom a = new Atom();

        // handle stuff that's different between ATOMs and HETATMs
        if(atomRecordName.equals("ATOM")) {

            if(isIgnoredAtom(chemSym)) {
                //a.setAtomtype(3);
                
                if( ! (Settings.getBoolean("plcc_B_handle_hydrogen_atoms_from_reduce") && chemSym.trim().equals("H"))) {
                    return(false);
                }
                
                
            }

            // the N terminus is in an ATOM line, never in a HETATM line
            // TODO: How to determine the N terminus? I can't see anything special about the N terminus lines, the code below finds all N.
            //if(atomName.equals(" N  ")) {
            //    System.out.println("  Found N terminus at PDB line number " + curLineNumPDB + ".");
            //}

            // set atom type
            a.setAtomtype(Atom.ATOMTYPE_AA);

            // only ATOMs, not HETATMs, have a DSSP entry
            //a.setDsspResNum(getDsspResNumForPdbResNum(resNumPDB));
            if((Settings.getBoolean("plcc_B_handle_hydrogen_atoms_from_reduce") && chemSym.trim().equals("H"))) {
                a.setDsspResNum(null);
            }
            else {
                a.setDsspResNum(getDsspResNumForPdbFields(resNumPDB, chainID, iCode));
            }
        }
        else {          // HETATM

            if(isIgnoredLigRes(resNamePDB)) {
                a.setAtomtype(Atom.ATOMTYPE_IGNORED_LIGAND);       // invalid ligand (ignored)

                // We do not need these atoms and they may lead to trouble later on, so
                //  just return without adding the new Atom to any Residue here so this line
                //  is skipped and the next line can be handled.
                //  If people want all ligands they have to change the isIgnoredLigRes() function.
                return(false);
            }
            else {
                a.setAtomtype(Atom.ATOMTYPE_LIGAND);       // valid ligand
                //a.setDsspResNum(getDsspResNumForPdbFields(resNumPDB, chainID, iCode));  // We can't do this because the fake DSSP residue number has not yet been assigned
            }
        }

        // now create the new Atom        
        Residue tmpRes = getResidueFromList(resNumPDB, chainID, iCode);
        // Note that the command above may have returned NULL, we care for that below
       
        a.setPdbAtomNum(atomSerialNumber);
        a.setAtomName(atomName);
        a.setAltLoc(altLoc);
        a.setResidue(tmpRes);
        a.setChainID(chainID);        
        a.setChain(getChainByPdbChainID(chainID));
        a.setPdbResNum(resNumPDB);
        a.setDsspResNum(resNumDSSP); // always 0 atm (default value and changed later?)
        a.setCoordX(coordX);
        a.setCoordY(coordY);
        a.setCoordZ(coordZ);
        a.setChemSym(chemSym);

        if(curModelID != null) {
            a.setModelID(curModelID);
            a.setModel(getModelByModelID(curModelID));
        }

        
        if(tmpRes == null) {
            DP.getInstance().w("Residue with PDB # " + resNumPDB + " of chain '" + chainID + "' with iCode '" + iCode + "' not listed in DSSP data, skipping atom " + atomSerialNumber + " belonging to that residue.");
            return(false);
        } else {            
            
            if(Settings.getBoolean("plcc_B_handle_hydrogen_atoms_from_reduce") && chemSym.trim().equals("H")) {
                tmpRes.addHydrogenAtom(a);
            }
            else {
                tmpRes.addAtom(a);
                s_atoms.add(a);
            }
        }                

        return(true);
    }
    
    /**
     * Returns true if the residue name stands for a 
     * @param resNamePDB
     * @return whether the residue name marks a DNA / RNA residue
     */
    public static boolean isDNAorRNAresidueName(String resNamePDB) {
        
        // standard ribonucleotides
        if(resNamePDB.equals("  G") || resNamePDB.equals("  U") || resNamePDB.equals("  A") || resNamePDB.equals("  T") || resNamePDB.equals("  C") || resNamePDB.equals("  I")) {
            return true;
        }
        
        // standard deoxribonucleotides
        if(resNamePDB.equals(" DG") || resNamePDB.equals(" DU") || resNamePDB.equals(" DA") || resNamePDB.equals(" DT") || resNamePDB.equals(" DC") || resNamePDB.equals(" DI")) {
            return true;
        }
        
        
        return false;
    }

    
    /**
     * DEPRECATED: Gets the Residue with the requested PDB properties from the residue list, but does not support PDB insertion code. So use the function with iCode support below instead.
     * 
     * @param resNum PDB residue number
     * @param cID chain ID
     * @return the Residue with the requested properties
     */
    @Deprecated private static Residue getResidueFromListOld(Integer resNum, String cID) {
        
        DP.getInstance().w("getResidueFromList(Integer, String): DEPRECATED: Use the function with iCode support instead!");

        Residue r;

        for(Integer i = 0; i < s_residues.size(); i++) {

            r = s_residues.get(i);

            if(r.getPdbResNum().equals(resNum)) {

                if(r.getChainID().equals(cID)) {
                    return(r);
                }

            }
        }

        // Not found in the whole list, something went wrong
        System.err.println("ERROR: Residue with PDB residue number " + resNum + " and chain ID " + cID + " does not exist in residue list.");
        return(null);
    }
    
    
    /**
     * Tries to get the residue with the given PDB residue number, chain ID and insertion code from the internal list of all residues.
     * @param resNumPDB the PDB residue number
     * @param chainID the chain ID of the residue
     * @param iCode the insertion code of the residue
     * @return the residue if such a residue exists, null if no such residue exists.
     */
    private static Residue getResidueFromList(Integer resNumPDB, String chainID, String iCode) {

        Residue tmp;
        Residue found = null;
        int numFound = 0;

        for(Integer i = 0; i < s_residues.size(); i++) {

            tmp = s_residues.get(i);

            if(tmp.getPdbResNum().equals(resNumPDB)) {

                if(tmp.getChainID().equals(chainID)) {
                    
                    if(tmp.getiCode().equals(iCode)) {
                        found = tmp;
                        numFound++;
                        // break here and return found to increase speed
                        return(found);
                    }                    
                }
            }
        }
        
        if(numFound > 1) {
            System.err.println("ERROR: More than one residue in list matches description (PDB#=" + resNumPDB + ", chain=" + chainID + ", iCode=" + iCode + ").");
        }

        // Not found in the whole list, something went wrong
        //System.err.println("ERROR: Residue with PDB residue number '" + resNum + "' and chain ID '" + cID + "' and iCode '" + ic + "' does not exist in residue list.");
        return(found);
    }


    /**
     * Parses a TER record line of a PDB file and writes info to STDOUT. 
     * @return true
     */
    private static boolean handlePdbLineTER() {

        
        Integer cTerminusResNumPDB = -1;
        String cID = "";
        String iCode = " ";

        try {
            
            // Ugly hack for the non-standard PDB files produced by the 'reduce' hydrogen program follows.
            // Reduce rewrites the PDB file and does NOT fill the empty lines with spaces, so we need to cope with shorter line lengths here.
            if(curLinePDB.length() < 27) {
                cID = curLinePDB.substring(21, 22);
                cTerminusResNumPDB = Integer.valueOf((FileParser.getLineContentsToEndFrom(22, curLinePDB).trim()));                                        
                iCode = ""; // icode parsing is not supported with reduce-made PDB files, I'm not sure what they do with it and how we should discriminate it from the last digit of the residue number
                DP.getInstance().w("Main", " Non-standard PDB 'TER' line of length " + curLinePDB.length() + " encountered. Parsing of iCode not supported for these lines. Assuming terminating residue number '" + cTerminusResNumPDB + "' and empty iCode for chain '" + cID + "'.");
            }
            else {
                // this is the method for standard PDB files
                cTerminusResNumPDB = Integer.valueOf((curLinePDB.substring(22, 26)).trim());
                cID = curLinePDB.substring(21, 22);                        
                iCode = curLinePDB.substring(26, 27);
            }
            
        } catch (Exception e) {
            System.err.println("WARNING: Hit TER line at PDB line number " + curLineNumPDB + " but parsing the line failed: '" + e.getMessage() + "', ignoring.");
            //e.printStackTrace();
            //System.exit(1);
        }
        

        if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
            System.out.println("    PDB: Found C Terminus of chain " + cID + " at PDB line " + curLineNumPDB + ", PDB residue number " + cTerminusResNumPDB + " iCode '" + iCode + "'.");
        }
        //TODO: mark the proper residue as C terminus

        return(true);
    }


    /**
     * Does nothing, other lines are ignored atm.
     * @return always returns true
     */
    private static boolean handlePdbLineOTHER() {
        //System.out.println("  Ignored PDB line " + currentLineNumPDB + ", starts with '" + currentLinePDB.substring(0, 7) + "'.");
        return(true);
    }


    // reads the dssp lines (starting at first line) till it arrives at the first data line (skips header stuff)
    private static Integer readDsspToData() {

        curLineNumDSSP = 0;
        curLineDSSP = "";
        Boolean hitDsspData = false;

        for(Integer i = 0; i < dsspLines.size(); i++) {
            curLineNumDSSP++;
            curLineDSSP = dsspLines.get(i);

            if(curLineDSSP.startsWith("  #")) {     // We found the last line of the header. The next line is the first one we are interested in.

                if(curLineNumDSSP >= dsspLines.size() - 1) {        // this may already be the last line if the DSSP file is broken, we shouldn't go to the next line in that case ;)
                    System.err.println("ERROR: DSSP file '" + dsspFile + "' ends after last header line (line " + curLineNumDSSP + ".");
                    System.exit(1);
                }
                else {                                          // yay, data found!
                    curLineNumDSSP++;
                    curLineDSSP = dsspLines.get(i + 1);
                    hitDsspData = true;                    
                    break;                                      // look no further!
                }
            }
        }

        if(hitDsspData) {
            if(! FileParser.silent) {
                System.out.println("    DSSP: Found start of DSSP data in line " + curLineNumDSSP + ".");
            }
            return(curLineNumDSSP);
        }
        else {
            System.err.println("ERROR: Hit the end of DSSP file at line " + curLineNumDSSP + " without finding the end of the header.");
            System.err.println("ERROR: DSSP file contains no data. Maybe the PDB file contains only DNA/RNA, check COMPND lines. Exiting.");
            System.exit(2);
            return(null);
        }

    }

    

    /**
     * DEPRECATED: use getResByPdbFields() instead, this ignores chain ID and iCode, the returned residue is not unique.
     */

    @Deprecated public static Residue getResByPdbNum(Integer p) {
        
        DP.getInstance().w("getResByPdbNum(): This function is deprecated because it does not support chains and iCode (use getResByPdbFields()).");
        System.exit(1);

        for(Integer i = 0; i < s_residues.size(); i++) {
            if((s_residues.get(i).getPdbResNum()).equals(p)) {
                return(s_residues.get(i));
            }
        }

        // only reached if not found
        System.out.println("WARNING: Could not find Residue with PDB number " + p + ".");
        return(null);
    }
    

    /**
     * Finds the residue with PDB residue number 'p' of chain 'chID' with iCode 'ic' in the residue list.
     * @param p pdb residue number
     * @param chID chain id
     * @param ic insert code, may be null if you don't care
     * @return The Residue object if found, null otherwise.
     */
    public static Residue getResByPdbFields(Integer p, String chID, String ic) {
        
        for(Integer i = 0; i < s_residues.size(); i++) {
            if((s_residues.get(i).getPdbResNum()).equals(p)) {
                
                if((s_residues.get(i).getChainID()).equals(chID)) {
                    
                    if( ic == null || (s_residues.get(i).getiCode()).equals(ic)) {
                        return(s_residues.get(i));
                    }
                        
                    
                }
                
            }
        }

        // only reached if not found
        // System.out.println("WARNING: Could not find Residue with PDB number " + p + " and chain ID '" + chID + "' and iCode '" + ic + "'.");
        return(null);
    }


    /**
     * Determines whether a line from a DSSP file is a chainbreak line.
     * @return True if it is, false otherwise.
     */
    public static Boolean isChainBreakLine(String dsspLine) {
        if((dsspLine.substring(13, 14)).equals("!")) {
            return(true);
        }
        else {
            return(false);
        }
    }


    /**
     * DEPRECATED. Use getDsspResNumForPdbFields() instead.
     */
    @Deprecated public static Integer getDsspResNumForPdbResNum(Integer prn) {
        Integer foundDsspResNum = null;
        Integer foundPdbResNum = null;
        Integer resultDsspResNum = null;
        String dline = null;
        String tmpPdbResNum = null;
        Character lastChar = null;

        System.err.println("ERROR: getDsspResNumForPdbResNum(): This function is deprecated because it does not support iCode and chains.");
        System.exit(-1);

        //System.out.println("    Starting search at DSSP line number '" + (dsspDataStartLine - 1) + "'.");
        for(Integer i = dsspDataStartLine - 1; i < dsspLines.size(); i++) {
            dline = dsspLines.get(i);
            foundDsspResNum = Integer.valueOf(dline.substring(1, 5).trim());

            //System.out.println("Looking for PDB# " + prn + " at line "  + (i + 1) + ": '" + dline + "'");

            // skip chain brakes
            if(isChainBreakLine(dline)) {
                continue;
            }
            
            // The PDB residue number string may still contain the Chain ID (e.g. '123A', thus we cannot simply try to cast it to Integer
            try{
                tmpPdbResNum = dline.substring(7, 11).trim();

                if(tmpPdbResNum.length() == 0) { // The next command (lastChar) would be out of bounds anyway if this code is executed
                    System.err.println("ERROR: Length of PDB number at line " + (i + 1) + " of DSSP file is 0." );
                    System.exit(-1);
                }

                lastChar = tmpPdbResNum.charAt(tmpPdbResNum.length() - 1);
                if(Character.isDigit(lastChar)) {
                    foundPdbResNum = Integer.valueOf(tmpPdbResNum);
                }
                else {
                    // The last character is NO digit so ignore it (this is only possible if the length of the string is > 1 of course)
                    if(tmpPdbResNum.length() <= 1) {
                        System.err.println("ERROR: Length of PDB number at line " + (i + 1) + " of DSSP file is 1 and this is not a digit." );
                        System.exit(-1);
                    }
                    else {
                        foundPdbResNum = Integer.valueOf(tmpPdbResNum.substring(0, tmpPdbResNum.length() - 1));                    
                    }
                }
            } catch(Exception e) {
                System.err.println("ERROR: Something went wrong with parsing PDB number at line " + (i + 1) + " of DSSP file." );
                System.exit(-1);
            }

            //System.out.println("    Found PDB residue number '" + foundPdbResNum + "', looking for '" + prn + "'. DSSP # is '" + foundDsspResNum + "' here.");

            if(foundPdbResNum.equals(prn)) {
                //System.out.println("    Hit!");
                resultDsspResNum = foundDsspResNum;
                break;
            }
            
        }

        if(resultDsspResNum == null) {
            System.out.println("WARNING: getDsspResNumForPdbResNum(): Could not find DSSP residue number for residue with PDB number " + prn + ".");
        }
        
        return(resultDsspResNum);
    }
    
    
            
    public static Integer getDsspResNumForPdbFields(Integer prn, String chainID, String iCode) {
        Integer foundDsspResNum = null;
        Integer foundPdbResNum = null;
        String foundPdbICode = null;
        String foundChain = null;
        Integer resultDsspResNum = null;
        String dline = null;
        String tmpPdbResNum = null;
        Character lastChar = null;

        //System.out.println("    Starting search at DSSP line number '" + (dsspDataStartLine - 1) + "'.");
        for(Integer i = dsspDataStartLine - 1; i < dsspLines.size(); i++) {
            dline = dsspLines.get(i);
            foundDsspResNum = Integer.valueOf(dline.substring(1, 5).trim());

            //System.out.println("Looking for PDB# " + prn + " at line "  + (i + 1) + ": '" + dline + "'");

            // skip chain brakes
            if(isChainBreakLine(dline)) {
                continue;
            }
            
            // The PDB residue number string may still contain the iCode (e.g. '123A', thus we cannot simply try to cast it to Integer
            try{
                tmpPdbResNum = dline.substring(7, 11).trim();
                foundChain = dline.substring(11, 12).trim();

                if(tmpPdbResNum.length() == 0) { // The next command (lastChar) would be out of bounds anyway if this code is executed
                    DP.getInstance().w("Length of PDB number at line " + (i + 1) + " of DSSP file is 0." );
                }

                lastChar = tmpPdbResNum.charAt(tmpPdbResNum.length() - 1);
                if(Character.isDigit(lastChar)) {
                    // The last char is a digit, so the iCode field is empty.
                    foundPdbICode = " ";
                    foundPdbResNum = Integer.valueOf(tmpPdbResNum);
                }
                else {
                    // The last character is NO digit so it is the iCode
                    if(tmpPdbResNum.length() <= 1) {
                        DP.getInstance().w("Length of PDB number at line " + (i + 1) + " of DSSP file is 1 and this is not a digit." );
                    }
                    else {
                        foundPdbResNum = Integer.valueOf(tmpPdbResNum.substring(0, tmpPdbResNum.length() - 1));
                        foundPdbICode = tmpPdbResNum.substring(tmpPdbResNum.length() - 2, tmpPdbResNum.length() - 1);
                    }
                }
            } catch(Exception e) {
                DP.getInstance().w("Something went wrong with parsing PDB number at line " + (i + 1) + " of DSSP file, ignoring." );
                //System.exit(-1);
            }

            //System.out.println("    Found PDB residue number '" + foundPdbResNum + "', looking for '" + prn + "'. DSSP # is '" + foundDsspResNum + "' here.");

            if(foundPdbResNum.equals(prn)) {
                
                if(foundChain.equals(chainID)) {
                    
                    if(foundPdbICode.equals(iCode)) {
                        resultDsspResNum = foundDsspResNum;
                        break;
                    }
                }                
            }            
        }

        //if(resultDsspResNum == null) {
        //    System.out.println("WARNING: getDsspResNumForPdbFields(): Could not find DSSP residue number for residue with PDB number " + prn + " and chainID '" + chainID + "' and iCode '" + iCode + "'.");
        //}
        
        return(resultDsspResNum);
    }


    public static Boolean isIgnoredLigRes(String r) {
        if(r.equals("DOD") || r.equals("HOH")) {
            return(true);
        } else {
            return(false);
        }
    }


    private static void createAllModelsFromHandledPdbLines() {

        Integer pLineNum = 0;
        String mID = "";
        Model m;
        String pLine = "";

        // create default model
        s_models.add(new Model(defaultModelName));

        for(Integer i = 0; i < pdbLines.size(); i++) {

            pLineNum = i + 1;
            pLine = pdbLines.get(i);

            if(pLine.startsWith("MODEL ")) {

                try {
                    mID = (pLine.substring(10, 16)).trim();
                } catch(Exception e) {
                    System.err.println("ERROR: Hit MODEL line at PDB line number " + pLineNum + " but parsing the line failed.");
                    e.printStackTrace();
                    System.exit(-1);
                }

                // Model found
                if( ! modelExistsWithModelID(mID)) {
                    m = new Model(mID);
                    if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
                        System.out.println("    PDB: New PDB Model (model ID '" + mID + "') starts at PDB line " + pLineNum + ".");
                    }
                    s_models.add(m);
                }
            }

        }

        // create the default model if this is a non-NMR file (crystal data) that contains no models
        if(s_models.size() < 1) {
            if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
                System.out.println("    PDB: No models found in handled PDB lines. This most likely is a crystal data (non-NMR) file. Adding default model '" + defaultModelName + "'.");
            }
            s_models.add(new Model(defaultModelName));
            
        }

        if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
            System.out.println("    PDB: Handled PDB lines contain data from " + s_models.size() + " model(s).");
        }

    }


    // Counts all models. Not really needed for the program itself since it only uses the first model, no matter how
    // many others follow.
    private static void createAllModelIDsFromWholePdbFile() {

        ArrayList<String> allPDBLines = slurpFile(pdbFile);
        Integer pLineNum = 0;
        String mID = "";
        String pLine = "";
        Integer numModels = 0;

        if(! FileParser.silent) {
            System.out.println("  Counting total number of models in the whole PDB file '" + pdbFile + "' (" + allPDBLines.size() + " lines)...");
        }

        for(Integer i = 0; i < allPDBLines.size(); i++) {

            pLineNum = i + 1;
            pLine = allPDBLines.get(i);

            if(pLine.startsWith("MODEL ")) {

                try {
                    mID = (pLine.substring(10, 16)).trim();
                } catch(Exception e) {
                    System.err.println("ERROR: Hit MODEL line at PDB line number " + pLineNum + " but parsing the line failed.");
                    e.printStackTrace();
                    System.exit(-1);
                }

                // Model found
                //System.out.println("    PDB: New PDB Model (model ID '" + mID + "') starts at PDB line " + pLineNum + ".");
                numModels++;
                s_allModelIDsFromWholePDBFile.add(mID);
            }

        }

        if(! FileParser.silent) {
            System.out.println("    PDB: Scanned whole PDB file for Models, found " + numModels + ".");

            if(numModels < 1) {
                System.out.println("    PDB: This most likely is crystal data (a non-NMR file without models). A default model will be created.");
            }
        }

        if(numModels > 1) {
            if(! FileParser.silent) {
                System.out.println("    PDB: Multiple models found, all but the default model will be ignored.");
            }

            if(Settings.getBoolean("plcc_B_split_dsspfile_warning")) {
                System.err.println("*************************************** WARNING ******************************************");
                DP.getInstance().w("Multiple models detected in PDB file. I'm fine with that but unless you did split");
                DP.getInstance().w(" the PDB file into separate models for DSSP, the current DSSP file is broken.");
                DP.getInstance().w(" I parse that file and rely on it. You know the deal: garbage in, garbage out.");
                DP.getInstance().w(" I'll continue but you have been warned. ;)");
                DP.getInstance().w(" (Set 'plcc_B_split_dsspfile_warning' to 'false' in the config file to suppress this message.)");
                System.err.println("*************************************** WARNING ******************************************");
            }
        }
        
    }


    // determines whether a model with a certain model ID already exists in s_models
    private static Boolean modelExistsWithModelID(String mID) {

        Model m;

        for(Integer i = 0; i < s_models.size(); i++) {

            m = s_models.get(i);

            if(m.getModelID().equals(mID)) {
                return(true);
            }
        }

        return false;
    }

    private static void setHomologuesOfChains() {

        Integer pLineNum = 0;
        String pLine = "";
        String chainLine = "";
        String[] curChains;
        Boolean nextLineContinuesChainList = false;
          
        for(Integer i = 0; i < pdbLines.size(); i++) {
          
            pLineNum = i + 1;
            pLine = pdbLines.get(i);

                
            if (pLine.startsWith("COMPND  ")) {
                try {
                    if (pLine.substring(11, 17).equals("CHAIN:") || nextLineContinuesChainList) {
                       
                        if (nextLineContinuesChainList) {                      
                            chainLine = chainLine + pLine.substring(11, pLine.length());
                            nextLineContinuesChainList = false;
                        } else {                          
                            chainLine = pLine.substring(18, pLine.length());                           
                        }
                        
                        //when end of chain list is not reached
                        if (!chainLine.contains(";")) {
                            nextLineContinuesChainList = true;
                        } 

                        // do this not before chain list is complete
                        if (!nextLineContinuesChainList) {
                            chainLine = chainLine.replaceAll(" ", "").replace(";", "");
                            curChains = chainLine.split(",");
                            for (String chain : curChains) {
                                ArrayList<String> homologueChains = new ArrayList<>();
                                for (String hChain : curChains) {
                                    if (!chain.equals(hChain)) {
                                        homologueChains.add(hChain);
                                    }
                                }
                                homologuesMap.put(chain, homologueChains);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Hit errenous COMPND line at line #" + pLineNum + "', could not add homologues entry.");
                }
            }             
        }
    }
    
    /**
     * Parses binding site data from the PDB data.
     */
    private static void createAllBindingSitesFromPdbData() {
        Integer pLineNum = 0;
        String pLine = "";
        Boolean haveBeenInSiteSectionAlready = Boolean.FALSE;
        String lastSiteInRemark = null;
     
        
        String modelID = null;
        String lastModelID = null;
        String siteName;
        String siteLineNumString;
        Integer siteLineNum;
        BindingSite curSite = null;
        String siteNumResDeclaredString;
        Integer siteNumResDeclared;

        // parse the REMARK lines
        String siteIdentifier = null;
        String siteEvidenceCode;
        String siteDescription;            
        Map<String, BindingSite> sites = new HashMap<>();
            
        for(Integer i = 0; i < pdbLines.size(); i++) {

            pLineNum = i + 1;
            pLine = pdbLines.get(i);
                        
            if(pLine.startsWith("REMARK 800")) {                
                int lineLength = pLine.length();
                if(pLine.startsWith("REMARK 800 SITE_IDENTIFIER:")) {
                    siteIdentifier = pLine.substring(27, (lineLength - 1)).trim();
                    lastSiteInRemark = siteIdentifier;
                    if(! sites.containsKey(siteIdentifier)) { 
                        sites.put(siteIdentifier, new BindingSite(siteIdentifier));
                        //System.out.println("    Adding new binding site '" + siteIdentifier + "' from remark line.");
                    }
                }
                
                if(pLine.startsWith("REMARK 800 EVIDENCE_CODE:")) {
                    siteEvidenceCode = pLine.substring(25, (lineLength - 1)).trim();
                    if(lastSiteInRemark != null) {
                        sites.get(lastSiteInRemark).setEvidenceCode(siteEvidenceCode);
                        if(lastSiteInRemark == null) {
                            DP.getInstance().w("FileParser", "No site encountered before hitting site evidence code line.");
                        }
                    }
                }
                
                if(pLine.startsWith("REMARK 800 SITE_DESCRIPTION:")) {
                    siteDescription = pLine.substring(28, (lineLength - 1)).trim();
                    if(lastSiteInRemark != null) {
                        sites.get(lastSiteInRemark).setDescription(siteDescription);
                        if(lastSiteInRemark == null) {
                            DP.getInstance().w("FileParser", "No site encountered before hitting site description line.");
                        }
                    }
                }
            }
                        
            
            // keep track of current model
            if(pLine.startsWith("MODEL  ")) {
                try {
                    modelID = (pLine.substring(10, 16)).trim();
                    lastModelID = modelID;
                } catch(Exception e) {
                    DP.getInstance().e("FileParser", " Hit broken MODEL line at PDB line number " + pLineNum + " while looking for SITEs.");
                    System.exit(1);
                }
            }
            
            // parse the SITE lines
            if(pLine.startsWith("SITE  ")) {
                
                haveBeenInSiteSectionAlready = true;
                siteLineNumString = pLine.substring(4, 10).trim();
                
                if(siteLineNumString.equals("***")) {
                    DP.getInstance().w("FileParser", " Binding site PDB line is marked with '***', ignoring it. This could be the result of using a non-standard format PDB file, e.g., from the REDUCE software.");
                    continue;
                }
                
                siteLineNum = 0;
                try {
                    siteLineNum = Integer.parseInt(siteLineNumString);
                } catch(Exception e) {
                    DP.getInstance().w("FileParser", "Parsing site internal line num failed for line '" + pLine + "', assuming 0.");
                }
                siteNumResDeclaredString = pLine.substring(14, 17).trim();
                siteNumResDeclared = Integer.parseInt(siteNumResDeclaredString);
                siteName = pLine.substring(11, 14).trim();
                curSite = sites.get(siteName);
                if(curSite == null) {
                    DP.getInstance().w("FileParser", "Site not found in map of sites when hitting SITE line in PDB file, should have been created from data in REMARK section already. Creating it now.");
                    sites.put(siteName, new BindingSite(siteName));
                    curSite = sites.get(siteName);
                }
                
                // set model and number of declared residues on the first line -- we could also do this on any other (or each) line, it would make no difference
                if(siteLineNum.equals(1)) {
                    curSite.setNumResDeclared(siteNumResDeclared);
                    curSite.setModelID(lastModelID);
                }
                
                // parse the residue info from this line and add it
                List<String[]> residueInfos = FileParser.parseBindingSiteResidueInfos(pLine);
                //System.out.println("Added " + residueInfos.size() + " residues to new site " + siteName + " in site line number " +  siteLineNum + "");
                curSite.addResidueInfos(residueInfos);                                        
            }
        }

        for(String key : sites.keySet()) {
            s_sites.add(sites.get(key));
        }
        
    }
    
    /**
     * Parses residue info from a PDB SITE line, which looks like 'SITE     1 AC1 15 ASN A  10  LYS A  12  HIS A  95  GLU A 165                    
SITE     2 AC1 15 ALA A 169  ILE A 170  GLY A 171  SER A 211                    
SITE     3 AC1 15 LEU A 230  GLY A 232  GLY A 233  HOH A 620                    
SITE     4 AC1 15 HOH A 621  HOH A 622  HOH A 623                               
'
     * @param line the input SITE line, like 'SITE     1 AC1 15 ASN A  10  LYS A  12  HIS A  95  GLU A 165'
     * @return a list of residue infos, consisting of residue name, chain, and PDB residue number (e.g., ["ASN", "A", "170"]).
     */
    private static List<String[]> parseBindingSiteResidueInfos(String line) {
        List<String[]> residueInfos = new ArrayList<>();
        
        // parse first res info
        try {
            String[] resInfo = FileParser.parseResInfo(line.substring(18, 28).trim());
            if(resInfo != null) { residueInfos.add(resInfo); }
        } catch(Exception e) { DP.getInstance().w("FileParser", "parseBindingSiteResidueInfos: First residue in SITE line not found, but should have at least 1 residue."); }
        
        // parse 2nd res info, if available
        try {
            String[] resInfo = FileParser.parseResInfo(line.substring(29, 39).trim());
            if(resInfo != null) { residueInfos.add(resInfo); }
        } catch(Exception e) { /* It's fine if there is only 1 residue per line */ }
        
        // parse 3rd res info, if available
        try {
            String[] resInfo = FileParser.parseResInfo(line.substring(40, 50).trim());
            if(resInfo != null) { residueInfos.add(resInfo); }
        } catch(Exception e) { /* It's fine if there is only 1 residue per line */ }
        
        // parse 4th res info, if available
        try {
            String[] resInfo = FileParser.parseResInfo(line.substring(51, 61).trim());
            if(resInfo != null) { residueInfos.add(resInfo); }
        } catch(Exception e) { /* It's fine if there is only 1 residue per line */ }
        
        
        return residueInfos;
    }
    
    /**
     * Parses residue info from a SITE line substring
     * @param s input string like 'ASN A  10'
     * @return the tokenized info or null if the string did not contain info
     */
    private static String[] parseResInfo(String s) {
        if(s == null) { return null; }
        if(s.isEmpty()) { return null; }
        StringTokenizer t = new StringTokenizer(s, " ");
        String[] data = new String[3];
        if(t.countTokens() == 3) {
            for(int i = 0; i < 3; i++) {                
                data[i] = t.nextToken();
            }
            //System.out.println("Found 3 tokens: " + data[0] + "," + data[1] + "," + data[2]);
            return data;
        }
        else {
            //System.err.println("parseResInfo: Found " + t.countTokens() + " tokens instead of 3.");
        }
        return null;
    }

    private static void createAllChainsFromPdbData() {

        Integer pLineNum = 0;
        String cID = "";
        Chain c, d;
        String pLine = "";
        Boolean cAlreadyExists;
        String modelID = null;

        for(Integer i = 0; i < pdbLines.size(); i++) {

            pLineNum = i + 1;
            pLine = pdbLines.get(i);
            cAlreadyExists = false;

            // keep track of current model
            if(pLine.startsWith("MODEL  ")) {
                try {
                    modelID = (pLine.substring(10, 16)).trim();
                } catch(Exception e) {
                    System.err.println("ERROR: Hit broken MODEL line at PDB line number " + pLineNum + " while looking for Chains.");
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            // parse the chain info from the ATOM and HETATM lines
            if(pLine.startsWith("ATOM  ") || pLine.startsWith("HETATM")) {

                try {
                    cID = pLine.substring(21, 22);
                } catch(Exception e) {
                    System.err.println("ERROR: Could not parse Chain ID colum of PDB line " + pLineNum + ".");
                    e.printStackTrace();
                    System.exit(1);
                }

                if(cID.equals(" ")) {
                    cID = "_";
                    System.out.println("    PDB: Found chain with empty name, renamed to '" + cID + "'.");
                }

                // Chain found, add it if it is a new one
                for(Integer j = 0; j < s_chains.size(); j++) {      // s_chains is small so this should be ok
                    d = s_chains.get(j);
                    if(d.getPdbChainID().equals(cID)) {
                        cAlreadyExists = true;
                        break;
                    }
                }
                
                if(! cAlreadyExists) {
                    c = new Chain(cID);
                    
                    
                    if(modelID != null) {
                        c.setModelID(modelID);       // left at null otherwise, which is fine
                    }
                    
                    c.setHomologues(homologuesMap.get(cID));

                    s_chains.add(c);

                    if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
                        System.out.println("    PDB: New PDB Chain (chain ID '" + cID + "') starts at PDB line " + pLineNum + ".");
                    }
                }
            }

        }

        if(! FileParser.silent) {
            System.out.println("    PDB: Scanned PDB file for Chains, found " + s_chains.size() + ".");
        }

    }


    /**
     * Parses the DSSP data and creates the residue list from it.
     * @param isCIF true if using mmCIF parser and mmCIF pdb file as chain ids may be 4 character long then
     */
    private static void createAllResiduesFromDsspData(Boolean isCIF) {

        String dLine;
        Integer dLineNum, dsspResNum, pdbResNum, resIndex, acc;
        dLineNum = dsspResNum = pdbResNum = 0;
        String dsspChainID, resName1Letter, sseString, iCode;
        dsspChainID = resName1Letter = iCode = "";
        sseString = "UNDEF";
        Character lastChar = null;
        Float phi = 0.0f;
        Float psi = 0.0f;        

        s_residues = new ArrayList<Residue>();
        s_sulfurBridges = new HashMap<Character, ArrayList<Integer>>();
        s_interchainSulfurBridges = new HashMap<Character, ArrayList<Integer>>();
        s_interchainSulfurBridgesChainID = new  HashMap<Character, String>();
        Residue r;

        for(Integer i = dsspDataStartLine - 1; i < dsspLines.size(); i++) {
            dLine = dsspLines.get(i);
            dLineNum = i + 1;

            if(dLine.substring(13, 14).equals("!")) {       // chain brake
                if(! FileParser.silent) {
                    if (! Settings.getBoolean("plcc_B_no_chain_break_info")) {
                        System.out.println("    DSSP: Found chain brake at DSSP line " + dLineNum + ".");
                    }
                }
            }
            else {          // parse the residue line

                try {
                    // column 0 is ignored: blank
                    dsspResNum = Integer.valueOf(dLine.substring(1, 5).trim());
                    // 5 is ignored: blank
                    pdbResNum = Integer.valueOf(dLine.substring(6, 10).trim());
                    iCode = dLine.substring(10, 11);                    
                    // with PDB mmCIF files things got more difficult: 4-character chain ids
                    //     prioritize AUTHCHAIN > CHAIN
                    if (! isCIF) {
                        dsspChainID = dLine.substring(11, 12);
                    } else {
                        dsspChainID = dLine.substring(159, 163).trim(); // AUTHCHAIN column 160-163
                    }
                    
                    // 12 is ignored: blank
                    resName1Letter = dLine.substring(13, 14);
                    // 14+15 are ignored: blank
                    sseString = dLine.substring(16, 17);
                    // lots of stuff is ignored here
                    phi = Float.valueOf(dLine.substring(103, 108).trim());  // phi backbone angle
                    psi = Float.valueOf(dLine.substring(109, 114).trim());  // psi backbone angle
                    acc = Integer.valueOf(dLine.substring(35, 38).trim());  // solvent accessible surface
                    // rest is ignored: not needed
                } catch(Exception e) {
                    System.err.println("ERROR: Parsing of DSSP line " + dLineNum + " failed: '" + e.getMessage() + "'. DSSP file looks broken.");
                    //e.printStackTrace();
                    continue;
                }

                // successfully parsed, create Residue and add all the info we know atm
                r = new Residue(pdbResNum, dsspResNum);
                //System.err.println("DEBUG: Created residue with PDB# " + pdbResNum + ", DSSP# " + dsspResNum + ".");
                //System.err.println("DEBUG: Residue: " + r.getFancyName() + ".");
                r.setChainID(dsspChainID);
                // don't set the chain here, we'll set it later with the pdbChainID while creating the atoms
                // EDIT: Why not?! I guess I thought that DSSP and PDB files used different chain IDs for residues when I wrote that.
                // EDIT jnw: do the matching in CIF files later so that each file is only read once
                if (! isCIF) {
                    getChainByPdbChainID(dsspChainID).addResidue(r);
                }


                r.setType(Residue.RESIDUE_TYPE_AA);                   // DSSP files only contain protein residues, ligands are ignored
                r.setPhi(phi);
                r.setPsi(psi);
                r.setAcc(acc);

                // Cysteins which form a sulfur bridge are arbitrary lowercase letters in dssp. A residue
                // with name 'a' means it is a cysteine, but it has forms a sulfur bridge with a second residue in the file that is
                // also labeled 'a'.
                try {
                    char c = (resName1Letter.toCharArray())[0];
                    if( ! Character.isUpperCase(c)) {
                        resName1Letter = "C";   // change residue code cysteine

                        // now go save the sulfur bridge
                        if(s_sulfurBridges.containsKey(c)) {
                            // the sulfur bridge partner is already in there
                            (s_sulfurBridges.get(c)).add(dsspResNum);

                            // Check whether its a interhcain sulfur bridge (different chain IDs)
                            if(! s_interchainSulfurBridgesChainID.get(c).equals(dsspChainID)) {
                                ArrayList<Integer> tmpInterchain = new ArrayList<Integer>();

                                // Get the dsspResNum from the first residue of this interchain sulfur bridge
                                tmpInterchain.add(s_sulfurBridges.get(c).get(0));
                                // Also add the dsspResNum of the current (second) residue.
                                tmpInterchain.add(dsspResNum);
                                s_interchainSulfurBridges.put(c, tmpInterchain);
                            }


                        } else {
                            // this is the first residue of the sulfur bridge pair
                            ArrayList<Integer> tmp = new ArrayList<Integer>();
                            tmp.add(dsspResNum);
                            s_sulfurBridges.put(c, tmp);

                            // If its the first residue of the sulfur bridge save the chain this residue belongs to.
                            // This will be used to check if the second residue is on the same change or not.
                            s_interchainSulfurBridgesChainID.put(c, dsspChainID);
                        }
                    }
                } catch (Exception e) {
                    DP.getInstance().w("Something is fishy with residue in line " + dLineNum + " of the DSSP file: '" + e.getMessage() + "'. Sulfur bridge trouble?");
                }


                // Fix DSSP file like the one for 2ZW3.pdb which list cysteine residues as 'o' instead of 'c'.
                if(resName1Letter.equals("O") || resName1Letter.equals("U")) {
                    DP.getInstance().w("Turned AA with 1-letter code '" + resName1Letter + "' into 'C'.");
                    resName1Letter = "C";
                }

                // Define "asparagine or aspartic acid" as asparagine
                if(resName1Letter.equals("B")) {
                    DP.getInstance().w("Turned AA with 1-letter code '" + resName1Letter + "' into 'N'.");
                    resName1Letter = "N";
                }

                r.setiCode(iCode);
                r.setAAName1(resName1Letter);
                r.setResName3(AminoAcid.name1ToName3(resName1Letter));
                r.setSSEString(sseString);
                // Note: the SSE itself will be set by the getAllSSEs*() functions, it is still NULL atm

                // set model id to '1'. This is always true because DSSP can't handle models and the input files have to be split for it, thus every PDB file it reads has only 1 model
                r.setModelID("1");


                // DEBUG 3eoj
                //System.err.println("DEBUG --- residue state ---");
                //if(r.getPdbResNum() == 8 || r.getPdbResNum() == 7 || r.getPdbResNum() == 213) {
                //    System.err.println("DEBUG: RESIDUE: " + r.getFancyName() + ".");
                //}

                // add to list of Residues
                s_residues.add(r);
                resIndex = s_residues.size() - 1;
                // procudes null ponter exception in CIF parser and I dont see where we need it (maybe I'll understand later)
                if (! isCIF) {
                    resIndexDSSP[dsspResNum] = resIndex;
                }
                //resIndexPDB[pdbResNum] = resIndex;  // This will crash because some PDB files use negative residue numbers, omfg.
                //System.out.println("    DSSP: Added residue PDB # " +  pdbResNum + ", DSSP # " + dsspResNum + " to s_residues at index " + resIndex + ".");

                // Debug tests

                /*
                Residue tmp = s_residues.get(resIndex);
                if(dsspResNum != (s_residues.get(resIndex)).getDsspResNum()) {
                    System.err.println("ERROR: DSSP residue number of residue at index " + resIndex + " is not " + dsspResNum + " as expected, insertion failed.");
                    System.exit(-1);
                }

                if(pdbResNum != (s_residues.get(resIndex)).getPdbResNum()) {
                    System.err.println("ERROR: PDB residue number of residue at index " + resIndex + " is not " + pdbResNum + " as expected, insertion failed.");
                    System.exit(-1);
                }
                */

            }
        }
    }

    /**
     * Parses the PDB data and creates the ligand list from it.
     */
    private static Integer createAllLigandResiduesFromPdbData() {

        Integer curLigNum = 0;

        Integer atomSerialNumber, resNumPDB, resNumDSSP, lastLigandNumPDB, resIndex;
        atomSerialNumber = resNumPDB = resNumDSSP = lastLigandNumPDB = resIndex = 0;

        String atomName, resNamePDB, chainID, chemSym, modelID, lastChainID, iCode;
        atomName = resNamePDB = chainID = chemSym = modelID = lastChainID = iCode = "";

        String lf, ln, ls;      // temp for lig formula, lig name, lig synonyms
        lf = ln = ls = "";

        modelID = "1";          // default model
        Residue lig;
        Integer pLineNum = 0;
        String pLine = "";

        for(Integer i = 0; i < pdbLines.size(); i++) {

            pLine = pdbLines.get(i);
            pLineNum = i + 1;

            // keep track of current model
            if(pLine.startsWith("MODEL ")) {
                try {
                    modelID = (pLine.substring(10, 16)).trim();
                } catch(Exception e) {
                    System.err.println("ERROR: Hit broken MODEL line at PDB line number " + pLineNum + " while looking for Chains.");
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            // keep track of last Residue (resNumPDB will be overwritten in next step)
            //lastLigandNumPDB = resNumPDB;

            // we have to create the ligand residues from the HETATM lines
            if(pLine.startsWith("HETATM")) {

                try {

                    atomSerialNumber = Integer.valueOf((pLine.substring(6, 11)).trim());
                    atomName = pLine.substring(12, 16);
                    resNamePDB = pLine.substring(17, 20);
                    chainID = pLine.substring(21, 22);
                    resNumPDB = Integer.valueOf((pLine.substring(22, 26)).trim());
                    iCode = pLine.substring(26, 27);
                    chemSym = pLine.substring(76, 78);
                } catch(Exception e) {
                    System.err.println("ERROR: Hit HETATM line at PDB line number " + pLineNum + " but parsing the line failed (length " + pLine.length() + ").");
                    System.err.println("ERROR: (continued) Message was '" + e.getMessage() + "'.");
                    //e.printStackTrace();
                    System.exit(1);
                    //continue;
                }
                
                // Create new ligand residue here if this Atom does not belong to the same residue as the last one did
                if( ! ( resNumPDB.equals(lastLigandNumPDB) && chainID.equals(lastChainID) ) ) {

                    curLigNum++;

                    // create new Residue from info, we'll have to see whether we really add it below though
                    lig = new Residue();
                    lig.setPdbResNum(resNumPDB);
                    lig.setType(Residue.RESIDUE_TYPE_LIGAND);
                    resNumDSSP = getLastUsedDsspResNumOfDsspFile() + curLigNum; // assign an unused fake DSSP residue number
                    lig.setDsspResNum(resNumDSSP);
                    lig.setChainID(chainID);
                    lig.setiCode(iCode);
                    lig.setResName3(resNamePDB);
                    lig.setAAName1(AminoAcid.getLigandName1());
                    lig.setChain(getChainByPdbChainID(chainID));
                    lig.setModelID(modelID);
                    lig.setSSEString(Settings.get("plcc_S_ligSSECode"));
                                      

                    // add ligand to list of residues if it not on the ignore list
                    if(isIgnoredLigRes(resNamePDB)) {
                        curLigNum--;    // We had to increment before to determine the fake DSSP res number, but
                                        //  this ligand won't be stored so decrement to previous value.
                        //System.out.println("    PDB: Ignored ligand '" + resNamePDB + "-" + resNumPDB + "' at PDB line " + pLineNum + ".");
                    }
                    else {
                        // add info from PDB HET fields (HET, HETNAM, HETSYN, FORMUL)
                        // Note: we now use prepared statements so any strange chars do no longer lead to irritations or security trouble
                        Boolean removeStuff = Settings.getBoolean("plcc_B_uglySQLhacks");
                        lf = getLigFormula(resNamePDB);
                        if(removeStuff) {
                            lf = lf.replaceAll("\\s", "");               // remove all whitespace
                            lf = lf.replaceAll("~", "");                 // remove tilde char (it causes SQL trouble during DB insert)
                            lf = lf.replaceAll("\\\\", "");              // remove all backslashes (it causes SQL WARNING 'nonstandard use of escape in a string literal' during DB insert)
                            lf = lf.replaceAll("'", "");              // remove all ticks (obviously SQL trouble)
                        }

                        ln = getLigName(resNamePDB);
                        if(removeStuff) {
                            ln = ln.replaceAll(" ", "_");                // replace spaces with underscores
                            ln = ln.replaceAll("\\s", "");               // remove all other whitespace
                            ln = ln.replaceAll("~", "");
                            ln = ln.replaceAll("\\\\", "");
                            ln = ln.replaceAll("'", "");
                        }

                        ls = getLigSynonyms(resNamePDB);
                        if(removeStuff) {
                            ls = ls.replaceAll(" ", "_");
                            ls = ls.replaceAll("\\s", "");
                            ls = ls.replaceAll("~", "");
                            ls = ls.replaceAll(";", ".");
                            ls = ls.replaceAll("\\\\", "");
                            ls = ls.replaceAll("'", "");
                        }

                        lig.setLigFormula(lf);
                        lig.setLigName(ln);
                        lig.setLigSynonyms(ls);

                        lastLigandNumPDB = resNumPDB;
                        lastChainID = chainID;
                        
                        //TODO: Add a check for the molecular weight of the ligand here and only add ligands
                        //      which are within the range (range should be defined in cfg file).
                        //      Problem atm is that the mol weight is not in the PDB file. Idea: count the atoms
                        //      instead, use a range over number of atoms.
                        
                        s_residues.add(lig);
                        
                        getChainByPdbChainID(chainID).addResidue(lig);

                        resIndex = s_residues.size() - 1;
                        resIndexDSSP[resNumDSSP] = resIndex;
                        //resIndexPDB[resNumPDB] = resIndex;      // This will crash because some PDB files contain negative residue numbers so fuck it.
                        if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
                            System.out.println("    PDB: Added ligand '" +  resNamePDB + "-" + resNumPDB + "', chain " + chainID + " (line " + pLineNum + ", ligand #" + curLigNum + ", DSSP #" + resNumDSSP + ").");
                            System.out.println("    PDB:   => Ligand name = '" + lig.getLigName() + "', formula = '" + lig.getLigFormula() + "', synonyms = '" + lig.getLigSynonyms() + "'.");
                        }
                        
                    }

                }
                else {
                    //System.out.println("    PDB: Atom " + atomSerialNumber + " of reside " + resNumPDB + " belongs to old ligand " +  lastLigandNumPDB + " we already added.");
                }
                
            } // end of: if HETATM
        } // end of: iteration through all PDB lines


        return(curLigNum);
    }


    // parses the lines of a PDB file to find the formula of a hetero residue in the FORMUL records
    public static String getLigFormula(String ligName3) {
        String hetID, continuation, asterisk, formula, line;
        hetID = continuation = asterisk = formula = line = "";
        Integer compNum = 0;

        for(Integer i = 0; i < pdbLines.size(); i++) {
            line = pdbLines.get(i);

            if(line.startsWith("FORMUL")) {

                // see http://www.wwpdb.org/documentation/format32/sect4.html#FORMUL
                try {

                    compNum = Integer.valueOf((line.substring(8, 10)).trim());
                    hetID = line.substring(12, 15);                         // Can't trim this as spaces are important!
                    continuation = (line.substring(16, 18)).trim();
                    asterisk = (line.substring(18, 19)).trim();
                    
                    // Ugly hack for the non-standard PDB files produced by the 'reduce' hydrogen program follows.
                    // Reduce rewrites the PDB file and does NOT fill the empty lines with spaces, so we need to cope with shorter line lengths here.
                    if(line.length() < 71) {
                        formula = FileParser.getLineContentsToEndFrom(19, line);
                    }
                    else {
                        // this is the method for standard PDB files
                        formula = (line.substring(19, 71)).trim();
                    }
                } catch(NumberFormatException e) {
                    System.err.println("ERROR: Parsing FORMUL line at PDB line number " + i + " failed.");
                    //e.printStackTrace();
                    continue;
                }
                catch(StringIndexOutOfBoundsException s) {
                    System.err.println("ERROR: Parsing FORMUL line at PDB line number " + i + " failed.");
                    continue;
                }

                if(continuation.equals("")) {
                    // We only handle the first line of very long formulas that span multiple lines, the rest
                    //  is truncated. This is a quick hack and should be fixed later. No time atm.
                    if(hetID.equals(ligName3)) {
                        // We found the right line.
                        return(formula);
                    }
                }
            }
            
        }

        // This should never be reached.
        System.out.println("WARNING: getLigFormula(): No formula found for hetero group " + ligName3 + ".");        
        return("");
    }

    // parses the lines of a PDB file to find the chemical name of a hetero residue in the HETNAM records
    public static String getLigName(String ligName3) {
        String hetID, continuation, name, line;
        hetID = continuation = name = line = "";

        for(Integer i = 0; i < pdbLines.size(); i++) {
            line = pdbLines.get(i);

            if(line.startsWith("HETNAM")) {

                // see http://www.wwpdb.org/documentation/format32/sect4.html#HETNAM
                try {
                    continuation = (line.substring(8, 10)).trim();
                    hetID = line.substring(11, 14);                         // Can't trim this as spaces are important!
                    
                    // Ugly hack for the non-standard PDB files produced by the 'reduce' hydrogen program follows.
                    // Reduce rewrites the PDB file and does NOT fill the empty lines with spaces, so we need to cope with shorter line lengths here.
                    if(line.length() < 71) {
                        name = FileParser.getLineContentsToEndFrom(15, line);
                    }
                    else {
                        name = (line.substring(15, 71)).trim();
                    }
                } catch(Exception e) {
                    System.err.println("ERROR: Parsing HETNAM line at PDB line number " + i + " failed.");
                    //e.printStackTrace();
                    //System.exit(-1);
                    continue;
                }

                if(continuation.equals("")) {
                    // We only handle the first line of very long names that span multiple lines, the rest
                    //  is truncated. This is a quick hack and should be fixed later. No time atm.
                    if(hetID.equals(ligName3)) {
                        // We found the right line.
                        return(name);
                    }
                }
            }

        }


        // Water, deuterated water and methanol have no HETNAM records when used as solvent, so need to warn if none was found for them.
        if(ligName3.equals("HOH") || ligName3.equals("DOD") || ligName3.equals("MOH")) {
            if(ligName3.equals("HOH")) { return("WATER"); }
            if(ligName3.equals("DOD")) { return("DEUTERATED_WATER"); }
            if(ligName3.equals("MOH")) { return("METHANOL"); }
            return("SOLVENT");              // never reached, just for the IDE.
        }
        else {
            // All others should have a HETNAM record so this should never be reached.
            System.out.println("WARNING: getLigName(): No name found for hetero group " + ligName3 + ".");
            return("");
        }
    }

    // parses the lines of a PDB file to find the synonyms for a hetero residue in the HETSYN records
    public static String getLigSynonyms(String ligName3) {
        String hetID, continuation, synonyms, line;
        hetID = continuation = synonyms = line = "";

        for(Integer i = 0; i < pdbLines.size(); i++) {
            line = pdbLines.get(i);

            if(line.startsWith("HETSYN")) {

                // see http://www.wwpdb.org/documentation/format32/sect4.html#HETSYN
                try {
                    continuation = (line.substring(8, 10)).trim();
                    hetID = line.substring(11, 14);                         // Can't trim this as spaces are important!
                    synonyms = (line.substring(15, 71)).trim();
                } catch(Exception e) {
                    System.err.println("ERROR: Parsing HETSYN line at PDB line number " + i + " failed.");
                    //e.printStackTrace();
                    //System.exit(-1);
                    continue;
                }

                if(continuation.equals("")) {
                    // We only handle the first line of very long synonyms that span multiple lines, the rest
                    //  is truncated. This is a quick hack and should be fixed later. No time atm.
                    if(hetID.equals(ligName3)) {
                        // We found the right line.
                        return(synonyms);
                    }
                }
            }

        }

        // This could be reached for various valid entries I guess (or do they have a HETSYN line with empty syn field?).
        //System.out.println("WARNING: getLigSynonyms(): No synonym found for hetero group " + ligName3 + ".");
        return("");
    }

    public static Chain getChainByPdbChainID(String cID) {
        Chain resultChain = null;
        Chain tmpChain = null;

        for(Integer i = 0; i < s_chains.size(); i++) {
            tmpChain = s_chains.get(i);

            if(cID.equals(tmpChain.getPdbChainID())) {
                resultChain = tmpChain;
                break;      // no need to look further
            }
        }

        if(resultChain == null) {
            System.err.println("ERROR: Could not find Chain '" + cID + "' in s_chains.");
            System.exit(1);
            return(null);   // for the IDE
        }

        return(resultChain);
    }

    public static Model getModelByModelID(String mID) {
        Model resultModel = null;
        Model tmpModel = null;

        for(Integer i = 0; i < s_models.size(); i++) {
            tmpModel = s_models.get(i);

            if(mID.equals(tmpModel.getModelID())) {
                resultModel = tmpModel;
                break;      // no need to look further
            }
        }

        if(resultModel == null) {
            System.err.println("ERROR: Could not find Model '" + mID + "' in s_models.");
            System.exit(-1);
            return(null);   // for the IDE
        }

        return(resultModel);
    }

    public static ArrayList<String> getDsspLines() {
        if(dataInitDone) {
            return(dsspLines);
        }
        else {
            System.err.println("ERROR: FileParser.getDsspLines(): Request for data before initData() was called.");
            System.exit(1);
            return(null);
        }
    }


    // Determines if an ATOM should be ignored via its chemSym string
    private static Boolean isIgnoredAtom(String cSym) {

        // Ignored atoms: H=hydrogen, Q=pseudo atom used in NMR refinement, D=deuterium (^2H)
        if(cSym.trim().equals("H") || cSym.trim().equals("Q") || cSym.trim().equals("D")) {
            return(true);
        }

        return(false);
    }


    private static Integer getLastUsedDsspResNumOfDsspFile() {

        Integer dLineNum, dsspResNum;
        dLineNum = dsspResNum = 0;
        String dLine;
        dLine = "";

        if(maxUsedDsspResNumInDsspFile == null) {
            // value is not known yet, we have to determine it from the DSSP file
            for(Integer i = dsspDataStartLine - 1; i < dsspLines.size(); i++) {
                dLine = dsspLines.get(i);
                dLineNum = i + 1;
                
                if( (dLine.length() >= 14)) { // Warning has been printed before
                    if(! dLine.substring(13, 14).equals("!")) {       // if this is NOT a chain brake line

                        try {
                            dsspResNum = Integer.valueOf(dLine.substring(1, 5).trim());
                        } catch (Exception e) {
                            System.err.println("ERROR: Parsing of DSSP line " + dLineNum + " failed. DSSP file broken.");
                            e.printStackTrace();
                            System.exit(1);
                        }
                    }
                }
            }

            // DSSP residue numbers are ordered in the file so after iterating through
            //  all lines of the file, this holds the last used DSSP residue number.

            maxUsedDsspResNumInDsspFile = dsspResNum;
            //System.out.println("    DSSP: Last used DSSP residue number is " + maxUsedDsspResNumInDsspFile + ", found in line " + dLineNum + ".");


        }

        // value is known now and saved in class global var, just return it.
        return(maxUsedDsspResNumInDsspFile);
    }


    /**
     * Parses all lines of the PDB file and extracts the source organism of the macromolecular structure with chain ID 'chain'.
     * @param pdbid the PDB ID
     * @param chainid the chain ID
     * @return  the PMI
     */
    public static ProtMetaInfo getMetaInfo(String pdbid, String chainid) {

        ProtMetaInfo pmi = new ProtMetaInfo(pdbid, chainid);
        String mol_id = pmi.setYourMolID(pdbLines);

        if(pmi.isReady()) {
            //System.out.println("    Extracted MOL_ID '" + mol_id + "' for chain '" + chainid + "' from PDB header.");
            if(pmi.parseAllMetaData(pdbLines)) {
                //if(! FileParser.silent) {
                //    System.out.println("    Retrieved all meta data for chain '" + chainid + "' from PDB header.");
                //}
            }
        }
        else {
            DP.getInstance().w("getMetaInfo(): Could not extract MOL_ID for chain '" + chainid + "' from PDB header, meta data unknown.");
        }
            
        return(pmi);
    }


    /**
     * Retrieves meta data that applies to the whole PDB file from its lines, e.g. experiment type and resolution.
     *
     * @return A HashMap of (String, String) pairs (key, value) with information on the PDB file. The following
     * Strings are set: 'resolution' (which may be cast to Double), 'experiment', 'keywords', 'header', 'title'.
     *
     */
    public static HashMap<String, String> getPDBMetaData() {

        HashMap<String, String> md = new HashMap<String, String>();
        Double resolution = -1.0;
        String experiment = "NA";
        String keywords = "";
        String header = "NA";
        String title = "";
        String date = "NA";

        String line;
        for(Integer i = 0; i < pdbLines.size(); i++) {
            line = pdbLines.get(i);
            
            if(line.startsWith("EXPDTA")) {
                // line looks like this: 'EXPDTA    SOLUTION NMR'
                experiment = line.substring("EXPDATA".length()).trim();
            }
            else if(line.startsWith("REMARK   2 RESOLUTION.")) {
                resolution = getResFromREMARK2Line(line.trim());
            }
            else if(line.startsWith("KEYWDS")) {
                // Multiple KEYWDS lines may exist, e.g.:
                //    KEYWDS    PROTEIN RNA COMPLEX, ANTITERMINATOR COMPLEX, RNA HAIRPIN,
                //    KEYWDS   2 TRANSCRIPTION/RNA COMPLEX
                // but the data part always starts in column 10 (if the first is 0)
                keywords += line.substring(10).trim();
            }
            else if(line.startsWith("HEADER")) {
                try {
                    header = line.substring(10, 50).trim();
                    date = line.substring(50, 59).trim();
                } catch(Exception e) {
                    // Just leave it at "NA" then.
                }
            }
            else if(line.startsWith("TITLE")) {
                // Multiple TITLE lines may exist, but the data part always starts in column 10 (if the first is 0)
                title += line.substring(10).trim();
            }
            else {
                // we aren't interested in other lines atm
            }
        }

        // All data has been parsed and should contain stuff.
        if(keywords.isEmpty()) { keywords = "NA"; }
        if(title.isEmpty()) { title = "NA"; }

        md.put("resolution", resolution.toString());
        md.put("experiment", experiment);
        md.put("keywords", keywords);
        md.put("header", header);
        md.put("title", title);
        md.put("date", date);
        
        // register md with results
        ProteinResults pr = ProteinResults.getInstance();
        pr.addProteinMetaData("Resolution", resolution.toString());
        pr.addProteinMetaData("Experiment", experiment);
        pr.addProteinMetaData("Keywords", keywords);
        pr.addProteinMetaData("Header", header);
        pr.addProteinMetaData("Title", title);
        pr.addProteinMetaData("Date", date);

        return(md);
    }
    
    public static HashMap<String, String> getMetaData() {
        return metaData;
    }

    /**
     * Parses the 'REMARK 2 RESOLUTION' record of a PDB file and returns the resolution in Angstroem, or -1.0 if it is 'NOT APPLICABLE' or could not be determined.
     * Lines may look like this 'REMARK   2 RESOLUTION.    2.24 ANGSTROMS.' or this 'REMARK   2 RESOLUTION. NOT APPLICABLE.'
     */
    private static Double getResFromREMARK2Line(String l) {
        Double res = -1.0;

        String prefix = "REMARK   2 RESOLUTION.";

        // Should have been checked already, but you never know.
        if(l.indexOf(prefix) < 0) {
            return(res);
        }

        try {
            Integer startIndex = l.indexOf(prefix) + prefix.length();       // the column where the data starts (cut off the prefix)
            String data = l.substring(startIndex, l.length());              // the data part

            Integer suffixStart = data.indexOf("ANGSTROMS");
            if(suffixStart >= 0) {
                // There should be a resolution in this data part, it looks similar to this: '    2.24 ANGSTROMS.'
                res = Double.valueOf(data.substring(0, suffixStart).trim());
            }
            else {
                // The data part may look similar to this: ' NOT APPLICABLE.' - or be total rubbish, we don't care.
                //System.out.println("Data part contains no 'ANGSTROMS', assuming 'NOT APPLICABLE'.");
                res = -1.0;
            }

        } catch(Exception e) {
            DP.getInstance().w("Could not parse resolution from 'REMARK 2 RESOLUTION' record, assuming 'NOT APPLICABLE'.");
            res = -1.0;
        }
        return(res);
    }
    
    /**
     * Parses a PDB file with multiple MODELS, converts those models to separate chains, and saves the result in a new PDB file.
     * If no or only one model was found, the method stops because there is nothing to convert.
     * @param pdbFile the input file
     * @param outFile the output path for the new file
     * @return true if converting was successful or if there was nothing to convert (no or only one model), false otherwise.
     */
    public static Boolean convertPdbModelsToChains(String pdbFile, String outFile) {
        // Read the PDB file
        ArrayList<String> file = slurpFile(pdbFile);
        
        Integer pLineNum = 0;
        String mID = "";
        // Contains the all the models as <k,v> = <line number were models starts, model ID>
        TreeMap<Integer, String> models = new TreeMap<Integer, String>();
        String pLine = "";
      
        // Iterate through the PDB file and look at each line
        for(Integer i = 0; i < file.size(); i++) {
            pLineNum = i + 1;
            pLine = file.get(i);
            
            if(pLine.startsWith("MODEL ")) {
                // If the line is marked as MODEL, try to get the model ID
                try {
                    mID = (pLine.substring(10, 16)).trim();
                } catch(Exception e) {
                    System.err.println("ERROR: Hit MODEL line at PDB line number " + pLineNum + " but parsing the line failed.");
                    e.printStackTrace();
                    return false;
                }

                // Model found
                if(!models.containsValue(mID)) {
                    System.out.println("    PDB: New PDB Model (model ID '" + mID + "') starts at PDB line " + pLineNum + ".");
                    models.put(pLineNum, mID);
                }
                else {
                    System.err.println("ERROR: Found models with the same ID.");
                    return false;
                }
            }
        }
        
        // Only if there is more than two models we will have to convert something, otherwise stop here
        if(models.size() < 2) {
            //System.out.println(models.size());
            // Initate a string builder that will store the output string to write the new PDB file
            StringBuilder sb = new StringBuilder();
            String lineSep = System.lineSeparator();
            for(Integer i = 0; i < file.size(); i++) {
                pLine = file.get(i);

                if(!(pLine.startsWith("MODEL ") || pLine.startsWith("ENDMDL"))) {
                    sb.append(pLine);
                    sb.append(lineSep);
                }
            }
            
            // Save the converted PDB file
            File convertedPdbFile = new File(outFile);
            try {
                FileWriter fw = new FileWriter(convertedPdbFile.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.append(sb);
                bw.close();
            } catch (IOException ex) {
                DP.getInstance().e("FileParser", "Could not write converted PDB file '" + outFile + "': '" + ex.getMessage() + "'.");
                return false;
            }
            
            System.out.println("No different models/only one model found. Nothing to convert here but the MODEL and ENDMDL lines have been removed.");
            return true;
        }
        
        // Initate a string builder that will store the output string to write the new PDB file
        StringBuilder sb = new StringBuilder();
        String lineSep = System.lineSeparator();
        
        // Parse the header of the file until the first MODEL is reached
        for(Integer i = 0; i < file.size(); i++) {
            pLineNum = i + 1;
            pLine = file.get(i);
            
            if(pLine.startsWith("MODEL ")) {
                break;
            }
            else {
                sb.append(pLine);
                sb.append(lineSep);
            }
        }
        
             
        // Alphabet that makes up all possible chain IDs
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String newChainID = null;
        Integer atomID = 0;
        Integer residueID = 0;
        Integer newAtomID = null;
        Integer terCount = 0;
        
        if(models.size() > alphabet.length()) {
            DP.getInstance().w("FileParser", "There are more models than possible chain IDs (" + alphabet.length() + "). Aborting the conversion now.");
            return false;
        }

        // Go through all the found models by starting at the line the models begin
        for(Integer lineNum : models.keySet()) {
            String chainID = "-1";
            
            // Get the first new chain ID from the alphabet that can be used to rename chains
            if(alphabet.length() > 0) {
                newChainID = alphabet.substring(0, 1);
                alphabet = alphabet.replace(newChainID, "");
            }
            else {
                DP.getInstance().w("FileParser", "Not enough letters to set unique chain IDs. Aborting.");
                return false;
            }
            
            while(!file.get(lineNum).startsWith("ENDMDL")) {
                
                // Count all the TER records in the file. This is later used to write the MASTER record
                if(file.get(lineNum).startsWith("TER")) {
                    terCount++;
                }
                
                // Go through the model until its end is reached and extract the chain ID from each entry
                String newLine = file.get(lineNum);
                chainID = newLine.substring(21,22);
                
                // Get the IDs from the current line which will be the old IDs that get overwritten
                Integer oldAtomID = Integer.parseInt(newLine.substring(6, 11).trim());
                Integer oldresidueID = Integer.parseInt(newLine.substring(22, 26).trim());
                // Now add the highest IDs from the previous model to the current IDs to get the new IDs
                // If we are still processing the first model, zero will be added, so nothing changes
                newAtomID = oldAtomID + atomID;
                Integer newResidueID = oldresidueID + residueID;
                
                if(newAtomID > 99999) {
                    DP.getInstance().w("FileParser", "Trying to assign a new atom ID greater than 99999. The PDB file format does not support this.\n"
                            + "Aborting the conversion now.");
                    return false;
                }
                
                if(newResidueID > 9999) {
                    DP.getInstance().w("FileParser", "Trying to assign a new residue ID greater than 9999. The PDB file format does not support this.\n"
                                        + "Aborting the conversion now.");
                    return false;
                }
                
                // Creates a string of spaces. To keep the PDB file format intact it is necessary to add the correct amount of spaces
                // in front of the later inserted string. To do this, we take the maximum length the entry may have (5 for atom IDs, 4 for residue IDs)
                // and substract the length of the the new ID. With this we know how much spaces need to be added as prefix.
                String spacesAtomID = new String(new char[5 - newAtomID.toString().length()]).replace("\0", " ");
                String spacesResidueID = new String(new char[4 - newResidueID.toString().length()]).replace("\0", " ");
                

                lineNum++;
                
                // Now look at the next line to see what chain ID this line has
                String nextLine = file.get(lineNum);
                String nextChainID = nextLine.substring(21, 22);
                
                // If both chain IDs are different and we have not reached the end of the model yet, 
                // get a new chain ID and replace the old chain ID with this new one
                if(!chainID.equals(nextChainID) && !nextLine.startsWith("ENDMDL")) {
                    newChainID = alphabet.substring(0, 1);
                    // Delete the character from the alphabet as we have used it now
                    alphabet = alphabet.replace(newChainID, "");
                    
                    newLine = new StringBuilder(newLine).replace(21, 22, newChainID).toString();
                    // Add the new IDs
                    newLine = new StringBuilder(newLine).replace(6, 11, spacesAtomID + newAtomID.toString()).toString();
                    newLine = new StringBuilder(newLine).replace(22, 26, spacesResidueID + newResidueID.toString()).toString();
                    sb.append(newLine);
                    sb.append(lineSep);
                }
                else {
                    // If the next line is the same as the one before, we do not have to assing a new chain ID
                    // but we still have to rename the old chain ID with the currently used new chain ID
                    newLine = new StringBuilder(newLine).replace(21, 22, newChainID).toString();
                    // Add the new IDs
                    newLine = new StringBuilder(newLine).replace(6, 11, spacesAtomID + newAtomID.toString()).toString();
                    newLine = new StringBuilder(newLine).replace(22, 26, spacesResidueID + newResidueID.toString()).toString();
                    sb.append(newLine);
                    sb.append(lineSep);
                    
                }
            }
            // Get the last IDs before a new model is reached and the IDs would therefore start all over again
            // We will use those last/highest IDs to add them to the IDs from the next model; in this way the
            // IDs will be consecutive
            atomID = Integer.parseInt(file.get(lineNum - 1).substring(6, 11).trim());
            residueID = Integer.parseInt(file.get(lineNum - 1).substring(22, 26).trim());
        }
        
        // Get the missing spaces for the MASTER record entries
        Integer masterNumAtm = newAtomID - terCount;
        String spacesMasterNumAtm = new String(new char[5 - masterNumAtm.toString().length()]).replace("\0", " ");
        String spacesMasterNumTer = new String(new char[5 - terCount.toString().length()]).replace("\0", " ");
        
        // Find the MASTER record and keep all its entries except the number of ATOM/HETATM records and the number of TER records.
        // Those two entries are updated with the new correct numbers of the converted file. Since all the other numbers to not get
        // changed during the converting, they are not touched.
        for(Integer x = file.size() - 1; x > 0; x--) {
            if(file.get(x).startsWith("MASTER ")) {
                String newMaster = file.get(x);
                newMaster = new StringBuilder(newMaster).replace(50, 55, spacesMasterNumAtm + masterNumAtm.toString()).toString();
                newMaster = new StringBuilder(newMaster).replace(55, 60, spacesMasterNumTer + terCount.toString()).toString();
                sb.append(newMaster);
                sb.append(lineSep);
            }
        }
        
        sb.append("END                                                                             ");
        sb.append(lineSep);
        
        // Save the converted PDB file
        File convertedPdbFile = new File(outFile);
        try {
            FileWriter fw = new FileWriter(convertedPdbFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(sb);
            bw.close();
        } catch (IOException ex) {
            DP.getInstance().e("FileParser", "Could not write converted PDB file '" + outFile + "': '" + ex.getMessage() + "'.");
            return false;
        }
        //System.out.println(sb.toString());
        //System.out.println(chains.toString());
        //System.out.println(models.toString());
        return true;
    }
    
    
    
    
    /**
     * 
     * Debug function for PTGL compatibility. Parses residue contact data from a file in <PDBID>.geo format (or <PDBID>.geolig if isGeoLig is true). Files in this
     * format can be generated by this program itself or by the PTGL program 'geom_neo.c'.
     * 
     * Call './geom_neo <PDBID>' to generate the input file.
     * 
     * The <pdbid.geo> file format:
     * 
     * All of the following fields are separated by a single space ' '.
     * 
     * 
     * 1      1 2   16 23 12 42  38   0 0    23  2 1    0  0 0    0  0 0    0  0 0   8
     * 1438 413 414  3 33 13 52  37   0 0    24  2 1    0  0 0   32  2 8    0  0 0   8
     * -----------------------------------------------------------------------------------
     * contact number (left justified, length 4)
     *        dssp residue number of 1st residue (right justified, length 3)
     *          dssp residue number of 2nd residue (left justified, length 3)
     *              AA code (as a number, internal geom_neo representation, see num() function) of 1st residue (right justified, lenth 2)
     *                 center sphere radius (around C alpha) of 1st residue, truncated to 99 if it was larger (left justified, lenth 2)
     *                    AA code (as a number, internal geom_neo representation, see num() function) of 2nd residue (right justified, lenth 2)
     *                       center sphere radius (around C alpha) of 1st residue, truncated to 99 if it was larger (left justified, lenth 2)
     *                           C alpha distance of these two residues, set to 999 if it was larger (right justified, length 3)
     * 
     *                                possible H bridge no. 1 contact distance (right justified, length 3)
     *                                  possible H bridge no. 2 contact distance (left justified, length 3)
     * 
     *                                       possible backbone-backbone contact atom distance; 0 if no BB contact (right justified, length 3)
     *                                           atom index* of the backbone atom of 1st residue that forms this BB contact, internal geom_neo number representation (right justified, length 2)
     *                                             atom index* of the backbone atom of the 2nd residue that forms this BB contact, internal geom_neo number representation (left justified, length 2)
     * 
     *                                                  possible chain-backbone contact atom distance; 0 if no CB contact (right justified, length 3)
     *                                                     atom index* of sidechain atom of 1st residue (right justified, length 2)
     *                                                       atom index* of backbone atom of 2nd residue (left justified, length 2)
     * 
     *                                                            possible backbone-chain contact atom distance; 0 if no BC contact (right justified, length 3)
     *                                                               atom index* of backbone atom of 1st residue (right justified, length 2)
     *                                                                 atom index* of sidechain atom of 2nd residue (left justified, length 2)
     * 
     *                                                                      possible chain-chain contact atom distance; 0 if no CC contact (right justified, length 3)
     *                                                                         atom index* of sidechain atom of 1st residue (right justified, length 2)
     *                                                                           atom index* of sidechain atom of 2nd residue (left justified, length 2)
     * 
     *                                                                               number of contacts for this residue pair (right justified, length 2)
     * -----------------------------------------------------------------------------------* 
     * 1      1 2   16 23 12 42  38   0 0    23  2 1    0  0 0    0  0 0    0  0 0   8
     * 1438 413 414  3 33 13 52  37   0 0    24  2 1    0  0 0   32  2 8    0  0 0   8
     * 
     * 
     * NOTE: atom indices of geom_neo start at '1', not at '0'!
     * 
     * 
     * @param filePath the path to the PDBID.geo or .geolig file
     * @param isGeoLig whether the file is in geo (false) or geolig (true) format
     * @param ourContacts an ArrayList containing the contact data computed by this application
     * 
     */
    public static void compareResContactsWithPdbidDotGeoFile(String filePath, Boolean isGeoLig, ArrayList<ResContactInfo> ourContacts) {
        
        Integer radDif1, radDif2, maxRadDif, distDif, maxDistDif, sumDif, sumDifAbs, common;  // difference logging
        
        Integer expLineLength = 79;     // expected line length (fixed, see .geo format documentation)
        
        if(isGeoLig) {
            
            expLineLength = 143;    // expected line length (fixed, see .geolig format documentation)
            
            // not implemented yet
            System.err.println("ERROR: getResContactInfoFromGeoFile(): Ligand data parsing not implemented yet.");
            System.exit(1);
        }
               
        ArrayList<String> lines = slurpFile(filePath);        
        String line;
        
            
        // ------------------------------------ contact arrays init done, init line vars -------------------------------
        Integer undef = -1;
        Integer contNum, resADsspNum, resBDsspNum, resAInternalCode, resBInternalCode, resAColSphereRadius, resBColSphereRadius;
        contNum = resADsspNum = resBDsspNum = resAInternalCode = resBInternalCode = resAColSphereRadius = resBColSphereRadius = undef;
        Integer resDist, hb1Dist, hb2Dist, BBdist, BBatomResAIndex, BBatomResBIndex;
        resDist = hb1Dist = hb2Dist = BBdist = BBatomResAIndex = BBatomResBIndex = undef;
        Integer CBdist, CBatomResAIndex, CBatomResBIndex;
        Integer BCdist, BCatomResAIndex, BCatomResBIndex;
        Integer CCdist, CCatomResAIndex, CCatomResBIndex, totalCont;
        
        CBdist = CBatomResAIndex = CBatomResBIndex = BCdist = BCatomResAIndex = BCatomResBIndex = CCdist = CCatomResAIndex = CCatomResBIndex = totalCont = undef;
        
        
        Integer numParsed = 0;      // number of lines successfully parsed from the pdbid.geo file (geom_neo)
        Integer numIgnored = 0;     // number of lines that could not be parsed from the pdbid.geo file (geom_neo)
        Integer numGeomNeoContactsMissingInPlcc = 0;
        Integer numPlccContactsMissingInGeomNeo = 0;
        Integer numLigContactsIgnored = 0;
        
        radDif1 = radDif2 = maxRadDif = distDif = maxDistDif = sumDif = sumDifAbs = common = 0;
        
        // stuff for comparing the other way around
        Integer numRes = s_residues.size();
        System.out.println("Assuming that " + numRes + " residues exist, preparing matrix.");
        Integer numResDSSP = numRes + 1;       // since DSSP residue numbers start with 1, not 0
        Integer [][] geom_neo_contact_exists = new Integer[numResDSSP][numResDSSP];
        for(Integer i = 0; i < numResDSSP; i++) {
            for(Integer j = 0; j < numResDSSP; j++) {
                geom_neo_contact_exists[i][j] = 0;
            }
        }
        
        
        for(Integer i = 0; i < lines.size(); i++) {
            
            line = lines.get(i);
            
            
            /**
            if(line.length() != expLineLength) {
                DP.getInstance().w("Skipping geo line " + (i + 1) + " of file " + filePath + ": length " + line.length() + ", expected length " + expLineLength + ".");
                numIgnored++;
                continue;
            }
            
            
            // ----------------------------------- parse data -------------------------------
            
            try {                
                // residue properties
                contNum = Integer.parseInt(line.substring(0, 4).trim());
                resADsspNum = Integer.parseInt(line.substring(5, 8).trim());
                resBDsspNum = Integer.parseInt(line.substring(9, 12).trim());
                resAInternalCode = Integer.parseInt(line.substring(13, 15).trim());
                resAColSphereRadius = Integer.parseInt(line.substring(16, 18).trim());
                resBInternalCode = Integer.parseInt(line.substring(19, 21).trim());
                resBColSphereRadius = Integer.parseInt(line.substring(22, 24).trim());
                
                // contacts and distances
                resDist = Integer.parseInt(line.substring(25, 28).trim());
                
                hb1Dist = Integer.parseInt(line.substring(29, 32).trim());
                hb2Dist = Integer.parseInt(line.substring(33, 36).trim());
                
                BBdist = Integer.parseInt(line.substring(37, 40).trim());
                BBatomResAIndex = Integer.parseInt(line.substring(41, 43).trim());
                BBatomResBIndex = Integer.parseInt(line.substring(44, 46).trim());
                
                CBdist = Integer.parseInt(line.substring(47, 50).trim());
                CBatomResAIndex = Integer.parseInt(line.substring(51, 53).trim());
                CBatomResBIndex = Integer.parseInt(line.substring(54, 56).trim());
                
                BCdist = Integer.parseInt(line.substring(57, 60).trim());
                BCatomResAIndex = Integer.parseInt(line.substring(61, 63).trim());
                BCatomResBIndex = Integer.parseInt(line.substring(64, 66).trim());
                
                CCdist = Integer.parseInt(line.substring(67, 70).trim());
                CCatomResAIndex = Integer.parseInt(line.substring(71, 73).trim());
                CCatomResBIndex = Integer.parseInt(line.substring(74, 76).trim());
                
                totalCont = Integer.parseInt(line.substring(77, 79).trim());
                
            } catch(Exception e) {
                System.err.println("ERROR: Hit malformed contact line in residue contact file '" + filePath + "'. Skipping line " + (i + 1) + ".");
                numIgnored++;
                continue;
            }
             */
            
            Scanner scanner = new Scanner(line);
            
            try {                
                // residue properties
                contNum = scanner.nextInt();
                resADsspNum = scanner.nextInt();
                resBDsspNum = scanner.nextInt();
                resAInternalCode = scanner.nextInt();
                resAColSphereRadius = scanner.nextInt();
                resBInternalCode = scanner.nextInt();
                resBColSphereRadius = scanner.nextInt();
                
                // contacts and distances
                resDist = scanner.nextInt();
                
                hb1Dist = scanner.nextInt();
                hb2Dist = scanner.nextInt();
                
                BBdist = scanner.nextInt();
                BBatomResAIndex = scanner.nextInt();
                BBatomResBIndex = scanner.nextInt();
                
                CBdist = scanner.nextInt();
                CBatomResAIndex = scanner.nextInt();
                CBatomResBIndex = scanner.nextInt();
                
                BCdist = scanner.nextInt();
                BCatomResAIndex = scanner.nextInt();
                BCatomResBIndex = scanner.nextInt();
                
                CCdist = scanner.nextInt();
                CCatomResAIndex = scanner.nextInt();
                CCatomResBIndex = scanner.nextInt();
                
                totalCont = scanner.nextInt();
                
            } catch(Exception e) {
                DP.getInstance().w("Hit malformed contact line in residue contact file '" + filePath + "'. Skipping line " + (i + 1) + ".");
                numIgnored++;
                continue;
            }
                    //scanner.nextInt();

            
            
            numParsed++;
            geom_neo_contact_exists[resADsspNum][resBDsspNum] = 1;
            
            // ---------------------------------------------
            
            System.out.println("Comparing external ResContact #" + contNum + ": DSSP pair " + resADsspNum + ", " + resBDsspNum + " with radii " + resAColSphereRadius + "," + resBColSphereRadius +  " in CA dist " + resDist + " ...");
                                    
            // compare with our data, i.e., check the number of geom_neo contacts which were not accepted by plcc
            
            Boolean found = false;
            radDif1 = radDif2 = distDif = 0;
            for(ResContactInfo rc : ourContacts) {
                
                //System.out.println("Comparing with our contact pair " + rc.getDsspResNumResA() + "," + rc.getDsspResNumResB() + ".");
                
                if((rc.getDsspResNumResA().equals(resADsspNum) && rc.getDsspResNumResB().equals(resBDsspNum)) || (rc.getDsspResNumResA().equals(resBDsspNum) && rc.getDsspResNumResB().equals(resADsspNum))) {                    
                    found = true;
                    common++;
                    
                    // log differences
                    sumDif += rc.getCenterSphereRadiusResA() - resAColSphereRadius;
                    radDif1 = Math.abs(rc.getCenterSphereRadiusResA() - resAColSphereRadius);
                    sumDifAbs += radDif1;
                    if(radDif1 > maxRadDif) { maxRadDif = radDif1; }
                    
                    sumDif += rc.getCenterSphereRadiusResB() - resBColSphereRadius;
                    radDif2 = Math.abs(rc.getCenterSphereRadiusResB() - resBColSphereRadius);
                    sumDifAbs += radDif2;
                    if(radDif2 > maxRadDif) { maxRadDif = radDif2; }
                    
                    sumDif += rc.getResPairDist() - resDist;
                    distDif = Math.abs(rc.getResPairDist() - resDist);
                    sumDifAbs += distDif;
                    if(distDif > maxDistDif) { maxDistDif = distDif; }                    
                    
                    System.out.println("  Found. We claim radii " + rc.getCenterSphereRadiusResA() + "," + rc.getCenterSphereRadiusResB() +  " in CA dist " + rc.getResPairDist() + ". (Differences: " + radDif1 + "," + radDif2 + "," + distDif + ")");
                    break;
                }
                else {
                    // ourContacts are ordered...
                    if(rc.getDsspResNumResA() > resADsspNum) {
                        // ...so checking the rest is useless.
                        found = false;
                        numGeomNeoContactsMissingInPlcc++;
                        break;
                    }
                    
                }
            }
            if(!found) {
                System.out.println("  NOT found by plcc: DSSP contact pair " + resADsspNum + ", " + resBDsspNum + " not in plcc internal contact list.");
            }                                 
        }
        
        // We should also check for contacts which occur in our data but not in the comparison file, i.e.,
        //  the number of plcc contacts which were not accepted by geom_neo 
        Integer dsspResA, dsspResB;
        for(ResContactInfo rc : ourContacts) {
            dsspResA = rc.getDsspResNumResA();
            dsspResB = rc.getDsspResNumResB();
            
            // We need to skip the ligand contacts, of course! They are not computed by geom_neo and they
            // would lead to an array out of bounds exception because they are not included in num_res_DSSP, the 
            // number of DSSP residues that determines the matrix size.
            if(rc.getResA().isLigand() || rc.getResB().isLigand()) {
                numLigContactsIgnored++;
                continue;
            }

            if(geom_neo_contact_exists[dsspResA][dsspResB] != 1) {
                numPlccContactsMissingInGeomNeo++;
                System.out.println("  NOT found by geom_neo: DSSP contact pair " + dsspResA + ", " + dsspResB + " not in geom_neo contact list file.");

            }
        }
        
        Integer avgDif, avgDifAbs;
        avgDif = sumDif / common;
        avgDifAbs = sumDifAbs / common;
        
        System.out.println("Parsed " + numParsed + " of " + lines.size() + " lines of file '" + filePath + "', ignored " + numIgnored + " (1st line is always empty so 1 is expected here).");
        System.out.println("File contains " + numParsed + " residue contacts, we found " + (ourContacts.size() - numLigContactsIgnored) + " (and " + numLigContactsIgnored + " ligand contacts).");
        System.out.println("Maximum differences: residue collision sphere radius=" + maxRadDif + ", distance=" + maxDistDif + ". (sumDif=" + sumDif + ", sumDifAbs=" + sumDifAbs + ", avgDif=" + avgDif + ", avgDifAbs=" + avgDifAbs + ")");
        System.out.println("Number of geom_neo contacts which were not detected by plcc: " + numGeomNeoContactsMissingInPlcc + ".");
        System.out.println("Number of plcc contacts which were not detected by geom_neo: " + numPlccContactsMissingInGeomNeo + ".");
        System.out.println("Number of ligand contacts ignored in plcc list: " + numLigContactsIgnored + ".");
    }
    
    
    
    /**
     * Debug function for PTGL compatibility. Parses SSE contact data from a file in geo.dat format. Files in this
     * format can be generated by the PTGL program 'bet_neo.c'.
     * 
     * Call './bet_neo <PDBID><CHAINID>' to generate the input file.
     * 
     * A geo.dat line consists of 9 fields separated by spaces:
     * <pdbid><chain> <SSE#1> <SSE#2> <int1> <int2> <int3> <int4> <spatial_relation> <double_difference>
     * 
     * The fields are:
     * 
     * <pdbid><chain>      : the PDBID and CHAIN of the protein (e.g.: 8icdA)
     * <SSE#1>             : sequential number of the 1st contact SSE
     * <SSE#2>             : sequential number of the 2nd contact SSE
     * <int1>              : number of BB contacts (between residues of both SSEs, counting max 1 contact per residue contact pair)
     * <int2>              : number of CB contacts ...
     * <int3>              : number of BC contacts ...
     * <int4>              : number of CC contacts ...
     * <spatial_relation>  : the spatial relation between the 2 SSEs: p=parallel, a=antiparallel, m=mixed
     * <double_difference> : the double difference value that was computed to determine <spatial_relation>
     * 
     * Example line:
     * 3kmfA 1 4 0 5 0 3 m -4
     * 
     * @param compareSSEContactsFile the input file to parse (the geo.dat output file of geom_neo)
     * @param pg the current protein graph. Its contacts should be compared to those in the input file
     */
    public static void compareSSEContactsWithGeoDatFile(String compareSSEContactsFile, ProtGraph pg) {
        
        ArrayList<String> lines = slurpFile(compareSSEContactsFile);        
        String line;
        
            
        // ------------------------------------ contact arrays init done, init line vars -------------------------------
        Integer undef = -1;
        Integer sseANum, sseAIndex, sseBNum, sseBIndex, numBB, numCB, numBC, numCC, doubleDif;
        String pdbidAndChain, chain, pdbid, spatRel;
        pdbidAndChain = chain = pdbid = spatRel = "?";
        sseANum = sseAIndex = sseBNum = sseBIndex = numBB = numCB = numBC = numCC = doubleDif = -1;
        
        Integer numParsed = 0;      // number of lines successfully parsed from the geo.dat file
        Integer numIgnored = 0;     // number of lines that could not be parsed from the geo.dat file
        Integer numBetNeoContactsMissingInPlcc = 0;
        Integer numPlccContactsMissingInBetNeo = 0;
        Integer numLigContactsIgnored = 0;
        Integer common = 0;
        Boolean found = false;
              
        // stuff for comparing the other way around
        Integer numSSEs = pg.numVertices();
                
        Integer numSSEIDs = numSSEs + 1; // the +1 is required because SSE IDs start with 1 (not 0) in the file
        //System.out.println("DEBUG: Assuming that " + numSSEs + " SSEs exist, preparing matrix.");
        Integer [][] bet_neo_contact_exists = new Integer[numSSEIDs][numSSEIDs];    
        for(Integer i = 0; i < numSSEIDs; i++) {
            for(Integer j = 0; j < numSSEIDs; j++) {
                bet_neo_contact_exists[i][j] = 0;
            }
        }        
        
        for(Integer i = 0; i < lines.size(); i++) {
            
            line = lines.get(i);                               
            found = false;
            
            Scanner scanner = new Scanner(line);
            
            try {                
                // scan SSE properties from the line
                pdbidAndChain = scanner.next();
                pdbid = pdbidAndChain.substring(0, 4);
                chain = pdbidAndChain.substring(4, 5);
                sseANum = scanner.nextInt();
                sseAIndex = sseANum - 1;
                sseBNum = scanner.nextInt();
                sseBIndex = sseBNum - 1;
                numBB = scanner.nextInt();
                numCB = scanner.nextInt();
                numBC = scanner.nextInt();
                numCC = scanner.nextInt();
                spatRel = scanner.next();
                //spatRel = scanner.findInLine("[mapl]");         // 'm' or 'a' or 'p' or 'l' for the spatial SSE contact types
                doubleDif = scanner.nextInt();
                
            } catch(Exception e) {
                DP.getInstance().w("Hit malformed SSE contact line in SSE contact file '" + compareSSEContactsFile + "'. Skipping line " + (i + 1) + ".");
                //DP.getInstance().w("Parsed data (pdbidchain, sseA, sseB, numBB, numCB, numBC, numCC, sr, dd): " + pdbidAndChain + ", " + sseA + ", " + sseB + ", " + numBB + ", " + numBC + ", " + numCB + ", " + numCC + ", " + spatRel + ", " + doubleDif + ".");
                numIgnored++;
                continue;
            }
            
            //System.out.println("INFO:    Parsed data (pdbidchain, sseA, sseB, numBB, numCB, numBC, numCC, sr, dd): " + pdbidAndChain + ", " + sseA + ", " + sseB + ", " + numBB + ", " + numBC + ", " + numCB + ", " + numCC + ", " + spatRel + ", " + doubleDif + ".");
            
            // abort this function if the input file we are parsing is not for the correct chain or PDB ID
            if((! pdbid.equals(pg.getPdbid())) || (! chain.equals(pg.getChainid()))) {
                System.out.println("Skipping SSE comparison for this chain, internal data is for chain '" + pg.getChainid() + "' of pdbid '" + pg.getPdbid() + "', file data for '" + chain + "' of '" + pdbid + "'.");
                return;
            }

            
            
            numParsed++;
            bet_neo_contact_exists[sseANum][sseBNum] = 1;     // the -1 is required because plcc starts indices at 0, not 1
            //bet_neo_contact_exists[sseB - 1][sseA - 1] = 1;       // Don't do this, they are ordered in there and i<j holds for each contact pair (i, j)
            
            // -----------------------------------------------
            
            if(pg.sseContactExistsPos(sseAIndex, sseBIndex)) {
                found = true;
                common++;
            }
            else {
                found = false;
                System.out.println("  NOT found by plcc: potential SSE contact num pair (" + sseANum + ", " + sseBNum + ") not in internal accepted contact list.");
                numBetNeoContactsMissingInPlcc++;                
            }
        }
        
        // check the other way around
        
        for(Integer i = 0; i < pg.numVertices(); i++) {         // i and j are now SSE indices, not SSE numbers
            for(Integer j = i + 1; j < pg.numVertices(); j++) {
                
                sseAIndex = i;
                sseBIndex = j;
                sseANum = i + 1;
                sseBNum = j + 1;
                
                if(pg.getSSEBySeqPosition(sseAIndex).isLigandSSE() || pg.getSSEBySeqPosition(sseAIndex).isOtherSSE() || pg.getSSEBySeqPosition(sseBIndex).isLigandSSE() || pg.getSSEBySeqPosition(sseBIndex).isOtherSSE()) {
                    numLigContactsIgnored++;
                    continue;
                }
                
                // If plcc list a contact here...
                if(pg.sseContactExistsPos(sseAIndex, sseBIndex)) {
                    
                    // ...check whether it also exists in bet_neo
                    if(bet_neo_contact_exists[sseANum][sseBNum] != 1) {
                        numPlccContactsMissingInBetNeo++;
                        System.out.println("  NOT found by bet_neo: SSE contact number pair (" + sseANum + ", " + sseBNum + ") not in bet_neo contact list file.");
                    }
                }                
            }
        }
        
        // print results
        System.out.println("Parsed " + numParsed + " of " + lines.size() + " lines of file '" + compareSSEContactsFile + "', ignored " + numIgnored + " (1st line is always empty so 1 is expected here).");
        System.out.println("File contains " + numParsed + " potential SSE contacts, we found " + (pg.numSSEContacts() - numLigContactsIgnored) + " accepted contacts (and " + numLigContactsIgnored + " ligand contacts).");
        System.out.println("Note that the geom_neo list contains all potential SSE contacts (not filtered by rule set yet), while the plcc list contains only accepted contacts.");
        System.out.println("Number of bet_neo contacts which were not detected by plcc: " + numBetNeoContactsMissingInPlcc + " (but read above).");
        System.out.println("Number of plcc contacts which were not detected by bet_neo: " + numPlccContactsMissingInBetNeo + ".");
        System.out.println("Number of ligand contacts ignored in plcc list: " + numLigContactsIgnored + ".");                
    }

}

