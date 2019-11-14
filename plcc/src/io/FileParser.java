/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package io;


// imports
import proteinstructure.Model;
import proteinstructure.Residue;
import proteinstructure.Chain;
import proteinstructure.AminoAcid;
import proteinstructure.Atom;
import proteinstructure.SSE;
import proteinstructure.Molecule;
import tools.DP;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.util.Arrays;
import java.util.List;
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

    static String pdbFile = null;
    static Integer lastUsedDsspNum = null;
    static ArrayList<String> pdbLines = null;
    static ArrayList <Molecule> s_molecules = null;
    static ArrayList<Model> s_models = null;
    static ArrayList<String> s_allModelIDsFromWholePDBFile = null;
    static ArrayList<Chain> s_chains = null;
    static ArrayList<Integer> s_residueIndices = null;
    static ArrayList<Integer> s_rnaIndices = null;
    static ArrayList<Atom> s_atoms = null;
    static ArrayList<SSE> s_ptglSSEs = null;                // the modified SSE list the PTGL uses
    static HashMap<String, ArrayList<String>> homologuesMap = null;
    static List<BindingSite> s_sites;
    
    static Integer lastIndexGetRes = 0;  // last index from s_residue where getResidueFromList found target
    
    // The list of sulfur bridges (aka disulfide bridges) from the DSSP file. The key is the DSSP sulfur bridge
    // id (an arbitrary character, starting with 'a' for the first bridge usually). The list in the value part contains
    // the DSSP residue IDs of all residues which are part of the bridge (and thus should have length 2).
    static HashMap<Character, ArrayList<Integer>> s_sulfurBridges;
    
    // The list of interchain sulfur bridges from the DSSP file. The list structure is the same as for s_sulfurBridges.
    static HashMap<Character, ArrayList<Integer>> s_interchainSulfurBridges;
    // This list is needed to track if sulfur bridges span between to independent chains.
    static HashMap<Character, String> s_interchainSulfurBridgesChainID;
    
    protected static Boolean dataInitDone = false;

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
     * Inits class variables.
     * @param pf PDB file path
     * @param df DSSP file path
     */
    protected static void initVariables(String pf, String df) {
        pdbFile = pf;
        dsspFile = df;
        
        // read all lines of the files into lists
        if(! FileParser.silent) {
            System.out.println("  Reading files...");
        }
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
    
        s_models = new ArrayList<Model>();
        s_allModelIDsFromWholePDBFile = new ArrayList<String>();
        s_chains = new ArrayList<Chain>();
        s_molecules = new ArrayList <Molecule> ();
        s_residueIndices = new ArrayList<>();
        s_rnaIndices = new ArrayList<>();
        s_atoms = new ArrayList<Atom>();
        s_dsspSSEs = new ArrayList<SSE>();
        s_ptglSSEs = new ArrayList<SSE>();
        s_sites = new ArrayList<>();
        homologuesMap = new HashMap<>();
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
    public static ArrayList<Molecule>getMolecule(){ return(s_molecules);}
    public static ArrayList<Integer> getResidueIndices() { return(s_residueIndices); }
    public static ArrayList<Integer> getRnaIndices() {return (s_rnaIndices);}
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
    * Inserts spaces on left end until targetStr has length newLenght.
     * If newLength less than / equals length of targetStr then targetStr is returned
     * @param targetStr
     * @param newLength 
     * @return 
     */
     public static String leftInsertSpaces(String targetStr, int newLength) {
         if (newLength <= targetStr.length()) {
             return targetStr;
         }
         return new String(new char[newLength - targetStr.length()]).replace("\0", " ") + targetStr;
     }
    
     
    /**
     * Returns true if the residue name stands for a 
     * @param resNamePDB
     * @return whether the residue name marks a DNA / RNA residue
     */
    public static boolean isRNAresidueName(String molNamePDB) {
        
        // standard ribonucleotides
        if(molNamePDB.equals("  G") || molNamePDB.equals("  U") || molNamePDB.equals("  A") || molNamePDB.equals("  T") || molNamePDB.equals("  C") || molNamePDB.equals("  I")) {
            return true;
        }
        
        return false;
    }
    
    
    public static boolean isDNAresidueName(String molNamePDB) {
        
        // standard deoxribonucleotides
        if(molNamePDB.equals(" DG") || molNamePDB.equals(" DU") || molNamePDB.equals(" DA") || molNamePDB.equals(" DT") || molNamePDB.equals(" DC") || molNamePDB.equals(" DI")) {
            return true;
        }
        
        
        return false;
    }
    
    
    /**
     * Returns true if AAName is standard aminoacid name (3-letter code).
     * @param AAName Amino acid name, 3-letter code, capitalized
     * @param includeNonStandard allows additional to 20 standard amino acids: UNK
     * @return 
     */
    private static boolean isAminoacid(String AAName, Boolean includeNonStandard) {
        ArrayList<String> AANames = new ArrayList<>(Arrays.asList("ALA", "ARG", "ASN", "ASP", "CYS", 
            "GLU", "GLN", "GLY", "HIS", "ILE", "LEU", "LYS", "MET", 
            "PHE", "PRO", "SER", "THR", "TRP", "TYR", "VAL"));
        
        if (includeNonStandard) {
            AANames.add("UNK");  // UNKnown, 'X' or any amino acid (often only backbone atoms are present
            AANames.add("MSE");  // Selenomethionine: Methionine with selen instead of sulfur (helps in crystallography)
        }
        
        if (AANames.contains(AAName)) {
            return true;
        } else {
            return false;
        }
    }
    
    
    /**
     * Returns true if AAName is standard aminoacid name (3-letter code).
     * @param AAName Aminoacid name, 3-letter code, capitalized
     * @return 
     */
    private static boolean isRNA(String RNAName) {
        String[] standardAANames = {"A", "G", "C", "G"};
        if (Arrays.asList(standardAANames).contains(RNAName)) {
            return true;
        } else {
            return false;
        }
    }
    

    /**
     * Calls getResidueFromList and returns its value with printing of error message if null returned.
     * @param resNumPDB
     * @param chainID
     * @param iCode
     * @param atomSerialNumber
     * @param numLine current line of pdb file.
     * @return 
     */
    //ändern zu getMolFromListWithErrMsg, return tmpMol
    private static Residue getResFromListWithErrMsg(Integer resNumPDB, String chainID, String iCode, Integer atomSerialNumber, Integer numLine) {
        Residue tmpRes = getResidueFromList(resNumPDB, chainID, iCode);
        if (tmpRes == null) {
            DP.getInstance().w("FP_CIF", " Residue with PDB # " + resNumPDB + 
                    " of chain '" + chainID + "' with iCode '" + iCode + 
                    "' not listed in DSSP data, skipping atom " + atomSerialNumber.toString() + 
                    " belonging to that residue (PDB line " + numLine.toString() + ").");
        }
        return tmpRes;
    }

    
    /**
     * Tries to get the residue with the given PDB residue number, chain ID and insertion code from the internal list of all residues.
     * Always starts at index of last hit (beginning with 0), i.e. rotates through list.
     * @param resNumPDB the PDB residue number
     * @param chainID the chain ID of the residue
     * @param iCode the insertion code of the residue
     * @return the residue if such a residue exists, null if no such residue exists.
     */
    private static Residue getResidueFromList(Integer resNumPDB, String chainID, String iCode) {
       
        Residue tmp;
        Residue found = null;
        int numFound = 0;

        // iterate up to s_residue.size() times
        for(Integer i = 0; i < s_molecules.size(); i++) {
            
            // start at last occurence
            Integer currentIndex = (lastIndexGetRes + i) % s_molecules.size();
            
            if (s_molecules.get(i) instanceof Residue) {
                tmp = (Residue) s_molecules.get(i);
                
                if(tmp.getPdbNum().equals(resNumPDB)) {

                    if(tmp.getChainID().equals(chainID)) {

                    
                        if(tmp.getiCode().equals(iCode)) {
                            found = tmp;
                            numFound++;
                            lastIndexGetRes = currentIndex;

                            // break here and return found to increase speed
                            return(found);
                        }
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
     * Finds the residue with PDB residue number 'p' of chain 'chID' with iCode 'ic' in the residue list.
     * @param p pdb residue number
     * @param chID chain id
     * @param ic insert code, may be null if you don't care
     * @return The Residue object if found, null otherwise.
     */
    public static Residue getResByPdbFields(Integer p, String chID, String ic) {
        Residue r;
        
        for(Integer i = 0; i < s_molecules.size(); i++) {
            if (s_molecules.get(i) instanceof Residue) {
                r = (Residue) s_molecules.get(i);
                if((r.getPdbNum()).equals(p)) {

                    if((r.getChainID()).equals(chID)) {

                        if( ic == null || (r.getiCode()).equals(ic)) {
                            return(r);
                        }
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


    /**
     * Parses the DSSP data and creates the residue list from it.
     * @param isCIF true if using mmCIF parser and mmCIF pdb file as chain IDs may be 4 character long then
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
        int offset;  // if > 99,999 residues everything is shifted

        s_sulfurBridges = new HashMap<Character, ArrayList<Integer>>();
        s_interchainSulfurBridges = new HashMap<Character, ArrayList<Integer>>();
        s_interchainSulfurBridgesChainID = new  HashMap<Character, String>();
        Residue r;

        for(Integer i = dsspDataStartLine - 1; i < dsspLines.size(); i++) {
            dLine = dsspLines.get(i);
            dLineNum = i + 1;
            
            offset = Math.max(dLine.split(" ")[0].length() - 5, 0);  // typically first 5 columns code for res num, but if exceeded add offset

            if(dLine.substring(13 + offset, 14 + offset).equals("!")) {       // chain brake
                if(! FileParser.silent) {
                    if (! Settings.getBoolean("plcc_B_no_chain_break_info")) {
                        System.out.println("    DSSP: Found chain brake at DSSP line " + dLineNum + ".");
                    }
                }
            }
            else {          // parse the residue line

                try {
                    // column 0 is ignored: blank
                    dsspResNum = Integer.valueOf(dLine.substring(1 + offset, 5 + offset).trim());
                    
                    // last used DSSP res num is later needed for ligands
                    // (and we only wand to go through dssp file once)
                    if (isCIF) {
                        // lets hope dssp res num always increases
                        lastUsedDsspNum = dsspResNum;
                    }
                    
                    // 5 is ignored: blank
                    pdbResNum = Integer.valueOf(dLine.substring(6 + offset, 10 + offset).trim());
                    iCode = dLine.substring(10 + offset, 11 + offset);                    
                    // with PDB mmCIF files things got more difficult: 4-character chain ids
                    //     prioritize AUTHCHAIN > CHAIN
                    if (! isCIF) {
                        dsspChainID = dLine.substring(11 + offset, 12 + offset);
                    } else {
                        dsspChainID = dLine.substring(159 + offset, 163 + offset).trim(); // AUTHCHAIN column 160-163
                    }
                    
                    // 12 is ignored: blank
                    resName1Letter = dLine.substring(13 + offset, 14 + offset);
                    // 14+15 are ignored: blank
                    sseString = dLine.substring(16 + offset, 17 + offset);
                    // lots of stuff is ignored here
                    phi = Float.valueOf(dLine.substring(103 + offset, 108 + offset).trim());  // phi backbone angle
                    psi = Float.valueOf(dLine.substring(109 + offset, 114 + offset).trim());  // psi backbone angle
                    acc = Integer.valueOf(dLine.substring(35 + offset, 38 + offset).trim());  // solvent accessible surface
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
                    getChainByPdbChainID(dsspChainID).addMolecule(r);
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
                r.setName3(AminoAcid.name1ToName3(resName1Letter));
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
                s_molecules.add(r);
                resIndex = s_molecules.size() - 1;
                // add index to s_residueIndices
                s_residueIndices.add(resIndex);
                // produces null ponter exception in CIF parser and I dont see where we need it (maybe I'll understand later)
                if (! isCIF) {
                    resIndexDSSP[dsspResNum] = resIndex;
                }
                //resIndexPDB[pdbResNum] = resIndex;  // This will crash because some PDB files use negative residue numbers, omfg.
                //System.out.println("    DSSP: Added residue PDB # " +  pdbResNum + ", DSSP # " + dsspResNum + " to s_residues at index " + resIndex + ".");


            }
        }
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
            DP.getInstance().e("FP", "Could not find Chain '" + cID + "' in s_chains. Maybe using cif-type "
                    + "DSSP file instead of legacy type? Exiting now.");
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

}