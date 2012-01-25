/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package plcc;

import java.util.ArrayList;

public class ProtMetaInfo {

    private String pdbid;
    private String chainid;
    private String molID;                   // MOL_ID token used in SOURCE and COMPND fields of the PDB file
    private String molName;                 // PDB file MOLECULE record
    private String orgScientific;           //          SOURCE record, ORGANISM_SCIENTIFIC line
    private String orgCommon;               //          SOURCE record, ORGANISM_COMMON line
    private String orgTaxid;                //          SOURCE record, ORGANISM_TAXID line


    /**
     * Constructor. Creates an empty Protein Meta Info object for chain 'chainid' of protein 'pdbid'. All info fields
     * are set to 'UNKNOWN'. The FileParser class fills the fields.
     */
    public ProtMetaInfo(String pdbid, String chainid) {
        this.pdbid = pdbid;
        this.chainid = chainid;

        this.molName = "UNKNOWN";
        this.orgScientific = "UNKNOWN";
        this.orgCommon = "UNKNOWN";
        this.orgTaxid = "UNKNOWN";
        this.molID = "UNKNOWN";
    }

    /**
     * Returns true if this object has been initialized properly and found the MOL_ID of its chain in the PDB file.
     */
    public Boolean isReady() {
        return( ! this.molID.equals("UNKNOWN"));
    }

    /**
     * Makes this object try to extract and set its MOL_ID from the lines in pdbLines (which could be all lines of a PDB file).
     * 
     * @return The MOL_ID ('UNKNOWN' if none could be found). Note that the ID is also set for this instance.
     *
     * Parsing this is very ugly, here is an example COMPND block from 3kmf.pdb:
     * 
     * COMPND    MOL_ID: 1;
     * COMPND   2 MOLECULE: HEMOGLOBIN SUBUNIT ALPHA;
     * COMPND   3 CHAIN: A, E;
     * COMPND   4 SYNONYM: HEMOGLOBIN ALPHA CHAIN, ALPHA-GLOBIN;
     * COMPND   5 MOL_ID: 2;
     * COMPND   6 MOLECULE: HEMOGLOBIN SUBUNIT BETA;
     * COMPND   7 CHAIN: C, G;
     * COMPND   8 SYNONYM: HEMOGLOBIN BETA CHAIN, BETA-GLOBIN, LVV-HEMORPHIN-
     * COMPND   9 7
     *
     */
    public String setYourMolID(ArrayList<String> pdbLines) {
        String mol_id = "UNKNOWN";
        String cur_mol_id = "UNKNOWN";
        String cur_chain_list = "UNKNOWN";
        String line = null;

        Integer indexColon, indexSemicolon;
        indexColon = indexSemicolon = -1;

        for(Integer i = 0; i < pdbLines.size(); i++) {

            line = pdbLines.get(i);

            if(line.startsWith("COMPND")) {         // This is a COMPND line

                // parse line with MOL_ID tokens to set cur_mol_id
                if(line.indexOf("MOL_ID:") >= 0) {   // It contains a MOL_ID token. The MOL_ID is the part between the colon and the semicolon, trimmed.

                    // DEBUG
                    //System.out.println("Hit MOL_ID line: '" + line + "'.");

                    indexColon = line.indexOf(":");
                    indexSemicolon = line.indexOf(";");

                    if(indexColon < 0 || indexSemicolon < 0 || indexColon > indexSemicolon) {
                        // Not found, this line is broken it seems. Ignore it.
                        // System.err.println("WARNING: setYourMolID(): Could not parse MOL_ID line in strange format, skipping.");
                        continue;
                    }
                    else {
                        cur_mol_id = line.substring(indexColon + 1, indexSemicolon).trim();
                    }

                    if(cur_mol_id.isEmpty()) {
                        // weirdo line, ignore it
                        // System.err.println("WARNING: setYourMolID(): Could not parse empty MOL_ID line, skipping.");
                        cur_mol_id = "UNKNOWN";
                        continue;
                    }
                }

                // Parse chain token lines. If we find a line with the chain we are interested in, the current MOL_ID is the
                //  MOL_ID we are looking for.
                if(line.indexOf("CHAIN:") >= 0) {
                    indexColon = line.indexOf(":");
                    indexSemicolon = line.indexOf(";");

                    if(indexColon < 0){
                        // Not found, this line is broken it seems. Ignore it.
                        continue;
                    }
                    else {
                        // The line contains a colon, yay.
                        if(indexSemicolon < 0) {
                            // This line contains no semicolon at the end for some reason. (The file 2hhb.pdb is an example for this.)
                            cur_chain_list = line.substring(indexColon + 1, (line.length() - 1)).trim();
                        }
                        else {
                            cur_chain_list = line.substring(indexColon + 1, indexSemicolon).trim();
                        }
                        
                        //System.out.println("cur_chain_list = '" + cur_chain_list + "'.");
                    }

                    // This line may contain a list of multiple chains, e.g. a line like 'COMPND   3 CHAIN: A, E;'. Check whether
                    //  the chain we are interested in is listed.
                    if(cur_chain_list.indexOf(this.chainid) >= 0) {
                        // The chain we are looking for is part of this chain list, so the current MOL_ID is the correct one.
                        mol_id = cur_mol_id;
                        this.molID = mol_id;
                        return(mol_id);
                    }
                    

                }

            }

            // The REMARK section comes after the PDB header, so we can stop searching when we hit it.
            if(line.startsWith("REMARK")) {
                break;
            }

        }

        System.err.println("WARNING: setYourMolID(): Could not find MOL_ID token of chain '" + this.chainid + "' in COMPND record of PDB file.");
        this.molID = mol_id;
        return(mol_id);
    }


    /**
     * Parses all protein meta data on its chain from the PDB file in 'pdbLines'. Only call this after checking that this instance 'isReady()'.
     * @return True if the info has been parsed (which does not mean that all of it could be retrived, these fields are then 'UNKNOWN'). False if the
     * info could not be parsed because the MOL_ID of this chain is not known yet (getYourMOL_ID has not been called yet or it could not be determined).
     */
    public Boolean getAllMetaData(ArrayList<String> pdbLines) {
        if( ! this.isReady()) {
            System.err.println("WARNING: getAllMetaData(): PMI instance not ready, MOL_ID of chain '" + chainid + "' not known yet.");
            return(false);
        }

        String cur_mol_id = "UNKNOWN";
        String line = null;

        Integer indexColon, indexLastChar;
        indexColon = indexLastChar = -1;

        Boolean correctMolIDCOMPND = false;
        Boolean correctMolIDSOURCE = false;

        for(Integer i = 0; i < pdbLines.size(); i++) {
            line = pdbLines.get(i);            

            if(line.startsWith("COMPND")) {

                // parse line with MOL_ID tokens to determine current MOL_ID
                if(line.indexOf("MOL_ID:") >= 0) {   // It contains a MOL_ID token. The MOL_ID is the part between the colon and the semicolon, trimmed.
                    //System.out.println("DEBUG: hit COMPND MOL_ID line.");

                    indexColon = line.indexOf(":");
                    indexLastChar = line.length() - 1;

                    if(indexColon < 0 || indexLastChar < 0 || indexColon >= indexLastChar) {
                        // Not found, this line is broken it seems. Ignore it.
                        System.err.println("WARNING: Could not parse MOL_ID from CPMPND line containing this token.");
                        continue;
                    }
                    else {
                        cur_mol_id = line.substring(indexColon + 1, indexLastChar).trim();
                        cur_mol_id = cur_mol_id.replaceAll(";", "");        // The line may or may not contain a trailing semicolon
                    }

                    if(cur_mol_id.isEmpty()) {
                        // weirdo line, ignore it
                        System.err.println("WARNING: Could not parse MOL_ID from COMPND line containing this token.");
                        cur_mol_id = "UNKNOWN";
                        continue;
                    }

                    if(cur_mol_id.equals(this.getMolID())) {
                        //System.out.println("DEBUG:  correct MOL_ID " + cur_mol_id + ".");
                        correctMolIDCOMPND = true;
                    }
                    else {
                        //System.out.println("DEBUG:  wrong MOL_ID " + cur_mol_id + ".");
                        correctMolIDCOMPND = false;
                    }
                }
                else if(line.indexOf("MOLECULE:") >= 0) {
                    //System.out.println("DEBUG: hit COMPND MOLECULE line.");

                    // We hit a MOLECULE line, parse it if we are currently in the correct MOL_ID
                    if(correctMolIDCOMPND) {
                        indexColon = line.indexOf(":");
                        indexLastChar = line.length() - 1;
                        
                        if(indexColon < 0 || indexLastChar < 0 || indexColon >= indexLastChar) {
                            // Not found, this line is broken it seems. Ignore it.
                            System.err.println("WARNING: Could not parse MOLECULE from COMPND line containing this token.");
                            continue;
                        }
                        else {
                            this.setMolName(line.substring(indexColon + 1, indexLastChar).trim().replaceAll(";", ""));
                        }                        
                    }
                }
            }
            else if(line.startsWith("SOURCE")) {
                
                // parse line with MOL_ID tokens to determine current MOL_ID
                if(line.indexOf("MOL_ID:") >= 0) {   // It contains a MOL_ID token. The MOL_ID is the part between the colon and the semicolon, trimmed.
                    //System.out.println("DEBUG: hit SOURCE MOL_ID line.");

                    indexColon = line.indexOf(":");
                    indexLastChar = line.length() - 1;

                    if(indexColon < 0 || indexLastChar < 0 || indexColon >= indexLastChar) {
                        // Not found, this line is broken it seems. Ignore it.
                        System.err.println("WARNING: Could not parse MOL_ID from SOURCE line containing this token.");
                        continue;
                    }
                    else {
                        cur_mol_id = line.substring(indexColon + 1, indexLastChar).trim();
                        cur_mol_id = cur_mol_id.replaceAll(";", "");        // The line may or may not contain a trailing semicolon
                    }

                    if(cur_mol_id.isEmpty()) {
                        // weirdo line, ignore it
                        System.err.println("WARNING: Could not parse MOL_ID from SOURCE line containing this token.");
                        cur_mol_id = "UNKNOWN";
                        continue;
                    }

                    if(cur_mol_id.equals(this.getMolID())) {
                        correctMolIDSOURCE = true;
                    }
                    else {
                        correctMolIDSOURCE = false;
                    }
                }
                else if(line.indexOf("ORGANISM_SCIENTIFIC:") >= 0) {
                    // check whether this SOURCE line contains the ORGANISM_SCIENTIFIC token
                    //System.out.println("DEBUG: hit SOURCE ORGANISM_SCIENTIFIC line.");

                    if(correctMolIDSOURCE) {
                        indexColon = line.indexOf(":");
                        indexLastChar = line.length() - 1;

                        if(indexColon < 0 || indexLastChar < 0 || indexColon >= indexLastChar) {
                            // Not found, this line is broken it seems. Ignore it.
                            System.err.println("WARNING: Could not parse ORGANISM_SCIENTIFIC from SOURCE line containing this token.");
                            continue;
                        }
                        else {
                            this.setOrgScientific(line.substring(indexColon + 1, indexLastChar).trim().replaceAll(";", ""));
                        }
                    }
                }
                else if(line.indexOf("ORGANISM_COMMON:") >= 0) {
                    // check whether this SOURCE line contains the ORGANISM_COMMON token
                    //System.out.println("DEBUG: hit SOURCE ORGANISM_COMMON line.");

                    if(correctMolIDSOURCE) {
                        indexColon = line.indexOf(":");
                        indexLastChar = line.length() - 1;

                        if(indexColon < 0 || indexLastChar < 0 || indexColon >= indexLastChar) {
                            // Not found, this line is broken it seems. Ignore it.
                            System.err.println("WARNING: Could not parse ORGANISM_COMMON from SOURCE line containing this token.");
                            continue;
                        }
                        else {
                            this.setOrgCommon(line.substring(indexColon + 1, indexLastChar).trim().replaceAll(";", ""));
                        }
                    }
                }
                else if(line.indexOf("ORGANISM_TAXID:") >= 0) {
                    // check whether this SOURCE line contains the ORGANISM_TAXID token
                    //System.out.println("DEBUG: hit SOURCE ORGANISM_TAXID line.");

                    if(correctMolIDSOURCE) {
                        indexColon = line.indexOf(":");
                        indexLastChar = line.length() - 1;

                        if(indexColon < 0 || indexLastChar < 0 || indexColon >= indexLastChar) {
                            // Not found, this line is broken it seems. Ignore it.
                            System.err.println("WARNING: Could not parse ORGANISM_TAXID from SOURCE line containing this token.");
                            continue;
                        }
                        else {
                            this.setOrgTaxid(line.substring(indexColon + 1, indexLastChar).trim().replaceAll(";", ""));
                        }
                    }
                }
            }
        }

        return(true);
    }


    /**
     * Prints the currently known meta data to stdout. A debug function only.
     */
    public void print() {
        System.out.println("| Printing data on ProtMetaInfo for protein " + this.pdbid + ", chain " + this.chainid + ".");
        System.out.println("| molID=" + this.molID + ", molName=" + this.molName + ", orgScientific=" + this.orgScientific);
        System.out.println("| orgCommon=" + this.orgCommon + ", orgTaxid=" + this.orgTaxid);
    }


    // setters and getters
    public String getMolName() { return(this.molName); }
    public String getMolID() { return(this.molID); }
    public String getOrgScientific() { return(this.orgScientific); }
    public String getOrgCommon() { return(this.orgCommon); }
    public String getOrgTaxid() { return(this.orgTaxid); }
    
    public String getPdbid() { return(this.pdbid); }
    public String getChainid() { return(this.chainid); }

    public void setMolName(String s) { this.molName = s; }
    public void setMolID(String s) { this.molID = s; }
    public void setOrgScientific(String s) { this.orgScientific = s; }
    public void setOrgCommon(String s) { this.orgCommon = s; }
    public void setOrgTaxid(String s) { this.orgTaxid = s; }


}
