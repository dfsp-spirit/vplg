/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package io;

// imports
import java.util.ArrayList;
import proteinstructure.SSE;



/**
 *
 * @author niclas
 */
public class DsspParser {
    
    // declare class vars
    static String dsspFile = null;
    static Integer dsspDataStartLine = null;
    static ArrayList<String> dsspLines = null;
    static ArrayList<SSE> s_dsspSSEs = null;             // all SSEs according to DSSP definition
    
    static Integer curLineNumDSSP = null;
    static String curLineDSSP = null;
    static Integer curResNumDSSP = null;
    
    
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
    
    
    
    
}
