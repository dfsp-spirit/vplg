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
import tools.DP;



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
    
    
    /**
     * Inits class variables.
     * @param df DSSP file path
     */
    protected static void initVariables(String df) {
        dsspFile = df;
        
        // read all lines of the files into lists
        if(! FileParser.silent) {
            System.out.println("  Reading files...");
        }
        
        dsspLines = new ArrayList<String>();
        dsspLines = FileParser.slurpFile(dsspFile, true); // vararg tells the function that this is a dssp file
        if(! FileParser.silent) {
            System.out.println("    Read all " + dsspLines.size() + " lines of file '" + dsspFile + "'.");
        }

        s_dsspSSEs = new ArrayList<SSE>();
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
    
}
