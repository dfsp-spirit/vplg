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


    public static Integer NONE = 0;                 // no relation (the SSEs are not in contact)
    public static Integer MIXED = 1;                // mixed (can't be determined, msot likely almost orthographic)
    public static Integer PARALLEL = 2;             // parallel
    public static Integer ANTIPARALLEL = 3;         // antiparallel
    public static Integer LIGAND = 4;               // ligand (no direction, no spatial relations not aplicable)

    /** 
     * Returns the String representation for a contact with Integer id 'i'. Each string representation is a single
     * lowercase letter, e.g. "m" for 1 (meaning 'mixed').
     */
    public static String getString(Integer i) {
        if(i.equals(NONE)) {
            return("-");
        }
        else if(i.equals(MIXED)) {
            return("m");
        }
        else if(i.equals(PARALLEL)) {
            return("p");
        }
        else if(i.equals(ANTIPARALLEL)) {
            return("a");
        }
        else if(i.equals(LIGAND)) {
            return("l");
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
        if(s.equals(getString(NONE))) {
            return(NONE);
        }
        else if(s.equals(getString(MIXED))) {
            return(MIXED);
        }
        else if(s.equals(getString(PARALLEL))) {
            return(PARALLEL);
        }
        else if(s.equals(getString(ANTIPARALLEL))) {
            return(ANTIPARALLEL);
        }
        else if(s.equals(getString(LIGAND))) {
            return(LIGAND);
        }
        else {
            System.err.println("ERROR: Spatial relation string '" + s + "' is invalid.");
            System.exit(1);
            return(-1);
        }

    }

}
