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

    // The AAs above 20 (J, B, Z, X) are PDB / ligand specific special AAs.
    static String[] names3 = { "ALA", "ARG", "ASN", "ASP", "CYS", "GLU", "GLN", "GLY", "HIS", "ILE", "LEU", "LYS", "MET", "PHE", "PRO", "SER", "THR", "TRP", "TYR", "VAL", "LIG", "_B_", "_Z_", "_X_" };
    static String[] names1 = { "A",    "R",  "N",   "D",    "C",  "E",   "Q",   "G",   "H",   "I",   "L",   "K",   "M",   "F",   "P",   "S",   "T",   "W",   "Y",   "V",   "J",   "B",   "Z",   "X" };

    // The number of atoms the AAs have (in order of the names3/names1 arrays)
    static Integer[] atoms = {  5,     11,    8,     8,     6,     9,     9,     4,     10,    8,     8,     9,     8,     11,    7,     6,     7,     14,    12,    7,     8,     9,     4,     10 };

    
    /**
     * Returns the number of (non-H) atoms that the AA with the given internal ID has.
     */
    public static Integer atomCountOfID(Integer id) {
        if(id <= names3.length && id >= 1) {
            return(atoms[id - 1]);
        }
        else{
            System.err.println("ERROR: No AA with internal ID " + id + " exists.");
            System.exit(-1);
            return(-1);
        }
        
        
    }


    /**
     * Converts the internal PTGL id of an amino acid to the 3-letter code.
     */
    public static String intIDToName3(Integer id) {
        if(id <= names3.length && id >= 1) {
            return(names3[id - 1]);
        }
        else{
            System.err.println("ERROR: No AA with internal ID " + id + " exists.");
            System.exit(-1);
            return("ERR");
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
        System.exit(-1);
        return("");             // for the IDE
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
        System.exit(-1);
        return("");             // for the IDE
    }


    // Returns the internal number assigned to each AA.
    // Needed for compatibility of the output with the old geom_neo because
    //  it prints its internal AA numbers into the output file 'con.set'.
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
