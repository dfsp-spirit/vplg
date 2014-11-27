/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

/**
 * A helper class which converts various representations and names of amino acids. You can use it to convert the
 * 3-letter-code to the 1-letter-code and stuff like that.
 * 
 * @author ts
 */
public class AminoAcid {

    // declare class vars
    
    public static final Integer CHEMPROPAA_INT_SMALL_APOLAR = 0;
    public static final Integer CHEMPROPAA_INT_HYDROPHOBIC = 1;
    public static final Integer CHEMPROPAA_INT_POLAR = 2;
    public static final Integer CHEMPROPAA_INT_NEGATIVE_CHARGE = 3;
    public static final Integer CHEMPROPAA_INT_POSITIVE_CHARGE = 4;
    public static final Integer CHEMPROPAA_INT_UNKNOWN = 5;
    
    public static final String CHEMPROPAA_STRING_UNKNOWN = "?";
    public static final String CHEMPROPAA_STRING_SMALL_APOLAR = "A";
    public static final String CHEMPROPAA_STRING_HYDROPHOBIC = "H";
    public static final String CHEMPROPAA_STRING_POLAR = "P";
    public static final String CHEMPROPAA_STRING_NEGATIVE_CHARGE = "+";
    public static final String CHEMPROPAA_STRING_POSITIVE_CHARGE = "-";
    
    protected static final String[] ALL_CHEM_PROPS = new String[] { CHEMPROPAA_STRING_SMALL_APOLAR, CHEMPROPAA_STRING_HYDROPHOBIC, CHEMPROPAA_STRING_POLAR, CHEMPROPAA_STRING_NEGATIVE_CHARGE, CHEMPROPAA_STRING_POSITIVE_CHARGE, CHEMPROPAA_STRING_UNKNOWN };


    /** The AAs above 20 (J, B, Z, X) are PDB / ligand specific special AAs. */    
    protected static final String[] names3 = { "ALA", "ARG", "ASN", "ASP", "CYS", "GLU", "GLN", "GLY", "HIS", "ILE", "LEU", "LYS", "MET", "PHE", "PRO", "SER", "THR", "TRP", "TYR", "VAL", "LIG", "_B_", "_Z_", "_X_" };
    // quick find line, index:               0      1      2      3      4      5      6      7      8      9      10     11     12     13     14     15     16     17     18     19     20     21     22     23    
    
    /** One letter AA code. */
    protected static final String[] names1 = { "A",    "R",  "N",   "D",    "C",  "E",   "Q",   "G",   "H",   "I",   "L",   "K",   "M",   "F",   "P",   "S",   "T",   "W",   "Y",   "V",   "J",   "B",   "Z",   "X" };

    /** The number of atoms the AAs have (in order of the names3/names1 arrays) */
    protected static final Integer[] atoms = {  5,     11,    8,     8,     6,     9,     9,     4,     10,    8,     8,     9,     8,     11,    7,     6,     7,     14,    12,    7,     8,     9,     4,     10 };
    
    /** The biochemical properties of the AAs (in order of the names3/names1 arrays) */
    protected static final Integer[] chemProps = {  AminoAcid.CHEMPROPAA_INT_SMALL_APOLAR,     AminoAcid.CHEMPROPAA_INT_POSITIVE_CHARGE,    AminoAcid.CHEMPROPAA_INT_POLAR,     AminoAcid.CHEMPROPAA_INT_NEGATIVE_CHARGE,     AminoAcid.CHEMPROPAA_INT_HYDROPHOBIC,     AminoAcid.CHEMPROPAA_INT_NEGATIVE_CHARGE,     AminoAcid.CHEMPROPAA_INT_POLAR,     AminoAcid.CHEMPROPAA_INT_SMALL_APOLAR,     AminoAcid.CHEMPROPAA_INT_POLAR,    AminoAcid.CHEMPROPAA_INT_HYDROPHOBIC,     AminoAcid.CHEMPROPAA_INT_HYDROPHOBIC,     AminoAcid.CHEMPROPAA_INT_POSITIVE_CHARGE,     AminoAcid.CHEMPROPAA_INT_HYDROPHOBIC,     AminoAcid.CHEMPROPAA_INT_HYDROPHOBIC,    AminoAcid.CHEMPROPAA_INT_HYDROPHOBIC,     AminoAcid.CHEMPROPAA_INT_SMALL_APOLAR,     AminoAcid.CHEMPROPAA_INT_SMALL_APOLAR,     AminoAcid.CHEMPROPAA_INT_HYDROPHOBIC,    AminoAcid.CHEMPROPAA_INT_HYDROPHOBIC,    AminoAcid.CHEMPROPAA_INT_HYDROPHOBIC,     AminoAcid.CHEMPROPAA_INT_UNKNOWN,     AminoAcid.CHEMPROPAA_INT_UNKNOWN,     AminoAcid.CHEMPROPAA_INT_UNKNOWN,     AminoAcid.CHEMPROPAA_INT_UNKNOWN };

    
    /**
     * Translates a chemProp code into the respective one letter string.
     * @param chemPropCode the code, use constants AminoAcid.CHEMPROPAA_INT_*
     * @return the string, one of CHEMPROPAA_STRING_* (or "?" if the code is invalid)
     */
    public static final String getChemProp1LetterString(Integer chemPropCode) {
        if(chemPropCode.equals(CHEMPROPAA_INT_UNKNOWN)) {
            return CHEMPROPAA_STRING_UNKNOWN;
        }
        else if(chemPropCode.equals(CHEMPROPAA_INT_SMALL_APOLAR)) {
            return CHEMPROPAA_STRING_SMALL_APOLAR;
        }
        else if(chemPropCode.equals(CHEMPROPAA_INT_HYDROPHOBIC)) {
            return CHEMPROPAA_STRING_HYDROPHOBIC;
        }
        else if(chemPropCode.equals(CHEMPROPAA_INT_POLAR)) {
            return CHEMPROPAA_STRING_POLAR;
        }
        else if(chemPropCode.equals(CHEMPROPAA_INT_NEGATIVE_CHARGE)) {
            return CHEMPROPAA_STRING_NEGATIVE_CHARGE;
        }
        else if(chemPropCode.equals(CHEMPROPAA_INT_POSITIVE_CHARGE)) {
            return CHEMPROPAA_STRING_POSITIVE_CHARGE;
        }
        else {
            System.err.println("ERROR: AminoAcid: getChemProp1LetterString: Invalid code " + chemPropCode + ".");
            return "?";
        }
    }
    
    /**
     * Returns the number of (non-H) atoms that the AA with the given internal ID has.
     * @param id the internal AA ID
     * @return the number of atoms or -1 if an invalid ID was given
     */
    public static Integer atomCountOfID(Integer id) {
        if(id <= names3.length && id >= 1) {
            return(atoms[id - 1]);
        }
        else{
            System.err.println("ERROR: No AA with internal ID " + id + " exists.");
            return(-1);
        }
        
        
    }


    /**
     * Converts the internal PTGL id of an amino acid to the 3-letter code.
     * The IDs start with 1 (not zero! -- blame the original PTGL for this, not me).
     */
    public static String intIDToName3(Integer id) {
        if(id <= names3.length && id >= 1) {
            return(names3[id - 1]);
        }
        else{
            System.err.println("ERROR: No AA with internal ID " + id + " exists.");
            //System.exit(-1);
            return("???");
        }
    }


    /**
     * Converts the internal PTGL id of an amino acid to the 1-letter code.
     */
    public static String intIDToName1(Integer id) {
        return(name3ToName1(intIDToName3(id)));
    }


    /**
     * Converts a 3 letter AA code to the corresponding 1 letter code.
     */
    public static String name3ToName1(String name3) {


        for(Integer i = 0; i < names3.length; i++) {
            if(names3[i].equals(name3)) {
                return(names1[i]);
            }
        }

        // only hit if nothing was found
        System.err.println("ERROR: Could not convert 3 letter amino acid code '" + name3 + "' to 1 letter code, not found.");
        //System.exit(-1);
        return("?");             // for the IDE
    }
    
    /**
     * Get chemical property classification for an AA given by 3 letter AA code.
     * @param name3 the 3 letter name of the amino acid, e.g., "ALA"
     * @return the chemical property, which is one of the constants at AminoAcid.CHEMPROPAA_*
     */
    public static Integer getChemPropOfAAByName3(String name3) {
        for(Integer i = 0; i < names3.length; i++) {
            if(names3[i].equals(name3)) {
                return(chemProps[i]);
            }
        }

        // only hit if nothing was found
        //System.err.println("ERROR: Could not find chemical properties for 3 letter amino acid code '" + name3 + "'.");
        return(AminoAcid.CHEMPROPAA_INT_UNKNOWN);
    }

    /**
     * Retuns fake 1 letter AA name for ligands
     */
    public static String getLigandName1() { return(Settings.get("plcc_S_ligAACode")); }


    /**
     * Converts a 1 letter AA code to the corresponding 3 letter code.
     */
    public static String name1ToName3(String name1) {


        for(Integer i = 0; i < names1.length; i++) {
            if(names1[i].equals(name1)) {
                return(names3[i]);
            }
        }

        // only hit if nothing was found
        System.err.println("ERROR: Could not convert 1 letter amino acid code '" + name1 + "' to 3 letter code, not found.");        
        return("");             // for the IDE
    }


    /** Returns the internal number assigned to each AA.
     * Needed for compatibility of the output with the old geom_neo because
     * it prints its internal AA numbers into the output file 'con.set'.
     * @param name3 the AA name
     * @return the internal ID
     */
    public static Integer name3ToID(String name3) {
        if( name3.equals("ALA") ) return 1;
        if( name3.equals("ARG") ) return 2;
        if( name3.equals("ASN") || name3.equals("ASX") ) return 3;
        if( name3.equals("ASP") ) return 4;
        if( name3.equals("CYS") ) return 5;
        if( name3.equals("GLU") ) return 6;
        if( name3.equals("GLN") || name3.equals("GLX") ) return 7;
        if( name3.equals("GLY") ) return 8;
        if( name3.equals("HIS") ) return 9;
        if( name3.equals("ILE") ) return 10;
        if( name3.equals("LEU") ) return 11;
        if( name3.equals("LYS") || name3.equals("INI") ) return 12;
        if( name3.equals("MET") ) return 13;
        if( name3.equals("PHE") ) return 14;
        if( name3.equals("PRO") ) return 15;
        if( name3.equals("SER") ) return 16;
        if( name3.equals("THR") ) return 17;
        if( name3.equals("TRP") ) return 18;
        if( name3.equals("TYR") ) return 19;
        if( name3.equals("VAL") ) return 20;

        
        if( name3.equals("LIG") ) return 21;
        
        // If the name didn't match anything it is a ligand
        return 21;
    }


    // Converts 1 letter AA code to internal ID.
    public static Integer name1ToID(String name1) {
        return(name3ToID(name1ToName3(name1)));
    }

}
