/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;


/**
 * A spatial relation which describes the relative orientation of an SSE contact 
 * pair.
 * 
 * @author ts
 */
public class SpatRel {


    public static final Integer NONE = 0;                 // no relation (the SSEs are not in contact)
    public static final Integer MIXED = 1;                // mixed (can't be determined, msot likely almost orthographic)
    public static final Integer PARALLEL = 2;             // parallel
    public static final Integer ANTIPARALLEL = 3;         // antiparallel
    public static final Integer LIGAND = 4;               // ligand (no direction, no spatial relations not aplicable)
    public static final Integer BACKBONE = 5;             // backbone (sequential N to C terminus)
    public static final Integer DISULFIDE = 6;            // disulfide bridge
    public static final Integer COMPLEX = 7;              // inter-chain contact
    
    public static final String STRING_NONE = "-";
    public static final String STRING_MIXED = "m";
    public static final String STRING_PARALLEL = "p";
    public static final String STRING_ANTIPARALLEL = "a";
    public static final String STRING_LIGAND = "l";
    public static final String STRING_BACKBONE = "b";
    public static final String STRING_DISULFIDE = "d";
    public static final String STRING_COMPLEX = "c";

    /** 
     * Returns the String representation for a contact with Integer id 'i'. Each string representation is a single
     * lowercase letter, e.g. "m" for 1 (meaning 'mixed').
     */
    public static String getString(Integer i) {
        if(i.equals(SpatRel.NONE)) {
            return(SpatRel.STRING_NONE);
        }
        else if(i.equals(SpatRel.MIXED)) {
            return(SpatRel.STRING_MIXED);
        }
        else if(i.equals(SpatRel.PARALLEL)) {
            return(SpatRel.STRING_PARALLEL);
        }
        else if(i.equals(SpatRel.ANTIPARALLEL)) {
            return(SpatRel.STRING_ANTIPARALLEL);
        }
        else if(i.equals(SpatRel.LIGAND)) {
            return(SpatRel.STRING_LIGAND);
        }
        else if(i.equals(SpatRel.BACKBONE)) {
            return(SpatRel.STRING_BACKBONE);
        }
        else if(i.equals(SpatRel.DISULFIDE)) {
            return(SpatRel.STRING_DISULFIDE);
        }
        else if(i.equals(SpatRel.COMPLEX)) {
            return(SpatRel.STRING_COMPLEX);
        }
        else {
            System.err.println("ERROR: Spatial relation integer " + i + " is invalid.");
            System.exit(1);
            return("?");
        }
    }

    /**
     * Returns the Integer representation of the contact String (e.g., 1 for "m").
     */
    public static Integer stringToInt(String s) {
        if(s.equals(SpatRel.STRING_NONE)) {
            return(SpatRel.NONE);
        }
        else if(s.equals(SpatRel.STRING_MIXED)) {
            return(SpatRel.MIXED);
        }
        else if(s.equals(SpatRel.STRING_PARALLEL)) {
            return(SpatRel.PARALLEL);
        }
        else if(s.equals(SpatRel.STRING_ANTIPARALLEL)) {
            return(SpatRel.ANTIPARALLEL);
        }
        else if(s.equals(SpatRel.STRING_LIGAND)) {
            return(SpatRel.LIGAND);
        }
        else if(s.equals(SpatRel.STRING_BACKBONE)) {
            return(SpatRel.BACKBONE);
        }
        else if(s.equals(SpatRel.STRING_DISULFIDE)) {
            return(SpatRel.DISULFIDE);
        }
        else if(s.equals(SpatRel.STRING_COMPLEX)) {
            return(SpatRel.COMPLEX);
        }
        else {
            System.err.println("ERROR: Spatial relation string '" + s + "' is invalid.");
            System.exit(1);
            return(-1);
        }

    }

}
