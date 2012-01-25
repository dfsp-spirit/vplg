/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

// imports
import java.util.ArrayList;
import java.net.*;
//import org.jgrapht.*;
//import org.jgrapht.graph.*;

/**
 *
 * @author spirit
 */
public class ContactMatrix {

    Integer size = null;
    private String handleChain = "ALL";
    private String pdbid;
    private Integer[ ][ ] contBB;       // backbone - backbone contacts
    private Integer[ ][ ] contBC;       // backbone - chain
    private Integer[ ][ ] contCB;       // backbone - chain
    private Integer[ ][ ] contCC;
    private Integer[ ][ ] contLB;       // ligand - backbone
    private Integer[ ][ ] contBL;       // ligand - backbone
    private Integer[ ][ ] contLC;
    private Integer[ ][ ] contCL;
    private Integer[ ][ ] contLL;
    private Integer[ ][ ] contSSE;      // contact matrix of the SSEs (1 = contact, 0 = no contact)
    private Integer[ ][ ] resContSSE;   // holds the number of residue level contacts
    private Integer[ ][ ] spatialSSE;   // spatial relations between pairs of SSEs
    private Integer[ ][ ] dblDif;       // double difference used to calculate spatial relations
    private ArrayList<SSE> sseList;

    // All possible spatial relationships between two adjacent SSEs (some C #define replacement)
    // Also see the spatialInt2String() function if messing with these.
    private Integer REL_NONE = SpatRel.NONE;                       // no relation (the SSEs are not in contact)
    private Integer REL_MIXED = SpatRel.MIXED;                      // mixed (can't be determined, msot likely almost orthographic)
    private Integer REL_PARALLEL = SpatRel.PARALLEL;                // parallel
    private Integer REL_ANTIPARALLEL = SpatRel.ANTIPARALLEL;        // antiparallel
    private Integer REL_LIGAND = SpatRel.LIGAND;                   // one of them is a ligand without direction so all others don't apply

    // Constructor
    ContactMatrix(ArrayList<SSE> sses, String pdbid) {
        this.pdbid = pdbid;
        this.sseList = sses;
        size = sseList.size();
        contBB = new Integer[size][size];
        contBC = new Integer[size][size];
        contCB = new Integer[size][size];
        contCC = new Integer[size][size];
        contLB = new Integer[size][size];
        contBL = new Integer[size][size];
        contLC = new Integer[size][size];
        contCL = new Integer[size][size];
        contLL = new Integer[size][size];
        contSSE = new Integer[size][size];
        resContSSE = new Integer[size][size];
        spatialSSE = new Integer[size][size];
        dblDif = new Integer[size][size];

        this.init();
    }

    
    /**
     * Defines which chain should be considered (only contacts between residues from that chain will count). This is used by fillFromContactList() and has to be set before calling that function.
     * @param cID the PDB chain ID, e.g. "A". This has to be a valid chain ID that occurs in the current PDB file.
     */
    public void restrictToChain(String cID) {
        this.handleChain = cID;
    }
    
    

    /**
     * Fills the 2-dimensional contact arrays of this CM using the contact information
     * given in contList.
     * @param contList a list of residue level contacts
     * @param keepSSEs a list of SSE type Strings that holds all SSE types which should be kept. All others will be filtered. For an albe graph, this list would be ["H", "E"].
     */
    public void fillFromContactList(ArrayList<ResContactInfo> contList, ArrayList<String> keepSSEs) {

        ResContactInfo rc;
        Residue a, b;
        a = b = null;
        Integer aSSEPos, bSSEPos, contNumIgnored, contNumConsidered;
        aSSEPos = bSSEPos = null;
        contNumIgnored = contNumConsidered = 0;
        
        //System.out.println("DEBUG: fillFromContactList(): Considering contacts from chain '" + this.handleChain + "'.");
                
        for(Integer i = 0; i < contList.size(); i++) {
            rc = contList.get(i);
            a = rc.getResA();
            b = rc.getResB();

            // Only handle this contact if both residues belong to the chain we are interested in (or if we are interested in all chains)
            if( (a.getChainID().equals(this.handleChain) && b.getChainID().equals(this.handleChain)) || this.handleChain.equals("ALL") ) {
                
                

                // We need to get the SSEs for the residues now. Note that they may not be part of any SSE
                //  in our list (they may be part of an SSE of another chain or no valid SSE, e.g. a coil).
                aSSEPos = getSSEPosOfDsspResidue(a.getDsspResNum());
                bSSEPos = getSSEPosOfDsspResidue(b.getDsspResNum());
               
                if(aSSEPos < 0 || bSSEPos < 0) {
                    // At least one of these residues is not part of one of the SEEs. This can happen if it is part
                    // of a coiled region. Just skip this residue pair.
                    //System.out.println("* Ignored DSSP contact pair " + a.getDsspResNum() + System.getProperty("file.separator") + b.getDsspResNum() + ", not part of any SSE in list.");
                    contNumIgnored++;
                    continue;
                } else {
                    //System.out.println("* Handling DSSP contact pair " + a.getDsspResNum() + System.getProperty("file.separator") + b.getDsspResNum() + ", part of SSEs #" + aSSEPos + " and #" + bSSEPos + ".");
                                        
                    // Both are part of SSEs from our list. But are they part of SSEs of the correct type?
                    if( ! ( keepSSEs.contains(sseList.get(aSSEPos).getSseType()) && keepSSEs.contains(sseList.get(bSSEPos).getSseType())) ) {
                        contNumIgnored++;
                        continue;
                    }

                    // Fill the residue level SSE contact matrix
                    this.resContSSE[aSSEPos][bSSEPos]++;
                    this.resContSSE[bSSEPos][aSSEPos]++;
                    
                    // TODO: I think each contact type (B/C, B/B, ...) that a pair of residues has should only be
                    //       counted ONCE below. So if a pair has 3 B/B cotacts and 2 C/B contacts, it should 
                    //       be counted as 1 B/B and 1 C/B. This is because the number of contacts is not even
                    //       stored in the <PDBID>.geo file of the PTGL -- only the distance of the shortest contact
                    //       for each contact type is stored.
                    
                    Integer mpt = Settings.getInteger("plcc_I_max_contacts_per_type");   
                                       // See comment above, the maximum number of contacts of a certain type that
                                       // is counted for a residue pair. Simply set it to something very large 
                                       // if you don't want any limit (Integer.MAX_VALUE comes to mind).
                                       // The PTGL uses a setting of 1.
                    
                    Integer numc;   // just a temp var for current number of contacts
                    
                    // All checks done, these are valid contacts.
                    numc = rc.getNumContactsBB();
                    this.addContacts("BB", aSSEPos, bSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsBC();
                    this.addContacts("BC", aSSEPos, bSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsCB();
                    this.addContacts("CB", aSSEPos, bSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsCC();
                    this.addContacts("CC", aSSEPos, bSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsLB();
                    this.addContacts("LB", aSSEPos, bSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsBL();
                    this.addContacts("BL", aSSEPos, bSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsLC();
                    this.addContacts("LC", aSSEPos, bSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsCL();
                    this.addContacts("CL", aSSEPos, bSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsLL();
                    this.addContacts("LL", aSSEPos, bSSEPos, (numc > mpt ? mpt : numc));

                    // Fill the other half of the matrix [ (y/x) instead of (x/y) ]          
                    numc = rc.getNumContactsBB();
                    this.addContacts("BB", bSSEPos, aSSEPos, rc.getNumContactsBB());
                    
                    numc = rc.getNumContactsBC();
                    this.addContacts("BC", bSSEPos, aSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsCB();
                    this.addContacts("CB", bSSEPos, aSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsCC();
                    this.addContacts("CC", bSSEPos, aSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsLB();
                    this.addContacts("LB", bSSEPos, aSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsBL();
                    this.addContacts("BL", bSSEPos, aSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsLC();
                    this.addContacts("LC", bSSEPos, aSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsCL();
                    this.addContacts("CL", bSSEPos, aSSEPos, (numc > mpt ? mpt : numc));
                    
                    numc = rc.getNumContactsLL();
                    this.addContacts("LL", bSSEPos, aSSEPos, (numc > mpt ? mpt : numc));
                    
                    contNumConsidered++;
                }                                
            }
            else {
                //System.out.println("DEBUG: fillFromContactList(): Ignoring contact " + rc.shortStringRep() + ", at least one residue not in chain '" + this.handleChain + "'.");
                contNumIgnored++;
            }
        }
        
        //System.out.println("DEBUG: Handled " + contList.size() + " contacts, " + contNumConsidered + " considered, " + contNumIgnored + " ignored.");

    }


    /**
     * Just prints the contact matrix for the contacts of the requested type.
     * @param type the contact type, e.g. "BB" for backbone-backbone contacts or "CL" for sidechain-ligand contacts
     */ 
    public void printTotalContactMatrix(String type) {

        System.out.println("Atom level contact matrix for type '" + type + "' of the SSEs follows:");
        for(Integer i = 0; i < this.size; i++) {
            // print line i
            for(Integer j = 0; j < this.size; j++) {
                // print column j of line i
                if(i.equals(j)) {
                    System.out.print(" -- ");
                }
                else {
                    System.out.printf("%3d ", this.getContacts(type, i, j));
                }
            }
            System.out.print("\n");
        }

    }

    /**
     * Prints the SSE matrix to STDOUT.
     */
    public void printSSEMatrix() {

        System.out.println("Atom level contact matrix of the SSEs follows:");
        for(Integer i = 0; i < this.size; i++) {
            // print line i
            for(Integer j = 0; j < this.size; j++) {
                // print column j of line i
                if(i.equals(j)) {
                    System.out.print("- ");
                }
                else {
                    System.out.printf("%1d ", contSSE[i][j]);
                }
            }
            System.out.print("\n");
        }

    }

    /**
     * Prints the residue contact matrix to STDOUT.
     */
    public void printResContMatrix() {

        System.out.println("Residue level contact matrix of the SSEs follows:");
        for(Integer i = 0; i < this.size; i++) {
            // print line i
            for(Integer j = 0; j < this.size; j++) {
                // print column j of line i
                if(i.equals(j)) {
                    System.out.print("- ");
                }
                else {
                    System.out.printf("%1d ", resContSSE[i][j]);
                }
            }
            System.out.print("\n");
        }

    }

    /**
     * Prints the matrix of spatial relations between the SSEs to stdout.
     */
    public void printSpatialRelationMatrix() {

        System.out.println("Spatial relations matrix of the SSEs follows:");
        for(Integer i = 0; i < this.size; i++) {
            // print line i
            for(Integer j = 0; j < this.size; j++) {
                // print column j of line i
                if(i.equals(j)) {
                    System.out.print("- ");
                }
                else {
                    System.out.printf("%s ", spatialInt2String(spatialSSE[i][j]));
                }
            }
            System.out.print("\n");
        }

    }

    /**
     * Translates the integer representing a spatial relation to a string of length 1 (e.g. 1 = REL_MIXED => m)
     * @param i the Integer constant to convert to a String
     */
    public String spatialInt2String(Integer i) {
        if(i.equals(REL_NONE)) {
            return("-");
        }
        else if(i.equals(REL_MIXED)) {
            return("m");
        }
        else if(i.equals(REL_PARALLEL)) {
            return("p");
        }
        else if(i.equals(REL_ANTIPARALLEL)) {
            return("a");
        }
        else if(i.equals(REL_LIGAND)) {
            return("l");
        }
        else {
            System.err.println("WARNING: Integer " + i + " does not represent any known spatial relation.");
            return("?");
        }
    }

    /**
     * Determines whether an SSE level contact exists between the SSEs at position x and y.
     * @param x the position of the first SSE in the list
     * @param y the position of the second SSE in the list
     * @return true if a contact exists, false otherwise
     */
    public Boolean sseContactExistsPos(Integer x, Integer y) {
        if(contSSE[x][y] > 0) {
            return(true);
        }
        else {
            return(false);
        }
    }
    

    /**
     * Determines the SSE (by its position in the SSE list) that contains the residue with the
     * given DSSP residue number. Note that some residues are not part of any SSE (those in coiled regions).
     * It works by comparing the given DSSP residue number with the start and end residue numbers of all SSEs
     * in the SSE list of the ContactMatrix.
     * @param dsspResNum the DSSP residue number
     * @return the SSE number if the residue was found in an SSE, -1 otherwise
     */
    public Integer getSSEPosOfDsspResidue(Integer dsspResNum) {
        Integer pos = -1;

        for(Integer i = 0; i < this.sseList.size(); i++) {
            if( (this.sseList.get(i).getStartResidue().getDsspResNum() <= dsspResNum) && (this.sseList.get(i).getEndResidue().getDsspResNum() >= dsspResNum)  ) {
                //System.out.println("   +DSSP Residue " + dsspResNum + " is part of SSE #" + i + ": " + this.sseList.get(i).shortStringRep() + ".");
                return(i);
            }
        }

        //System.out.println("   -DSSP Residue " + dsspResNum + " is NOT part of any SSE in list, returning " + pos + ".");
        return(pos);
    }

    /**
     * Returns the SSE object which is at the given position in the SSE list.
     * @param position the position in the SSE list
     * @return the SSE
     */
    public SSE getSSEByPosition(Integer position) {
        if(position >= this.size) {
            System.err.println("ERROR: getSSE(): Index " + position + " out of range, matrix size is " + this.size + ".");
            System.exit(-1);
        }
        return(sseList.get(position));
    }

    /**
     * Determines the position of an SSE with the given DSSP start and end numbers in the SSE list.
     * @param dsspStart the DSSP residue number of the 1st residue of the SSE
     * @param dsspEnd the DSSP residue number of the last residue of the SSE
     * @return the SSE index in the list if such an SSE was found, -1 otherwise
     */
    public Integer getSsePositionInList(Integer dsspStart, Integer dsspEnd) {

        for(Integer i = 0; i < this.sseList.size(); i++) {

            if(this.sseList.get(i).getStartResidue().getDsspResNum().equals(dsspStart)) {
                if(this.sseList.get(i).getEndResidue().getDsspResNum().equals(dsspEnd)) {
                    return(i);
                }
            }

        }

        //System.out.println("    No SSE with DSSP start and end residues " + dsspStart + System.getProperty("file.separator") + dsspEnd + " found.");
        return(-1);
    }


    /**
     * Inits this contact matrix, setting all array values to 0.
     */
    private void init() {
        for(Integer i = 0; i < size; i++) {
            for(Integer j = 0; j < size; j++) {
                contBB[i][j] = 0;
                contBC[i][j] = 0;
                contCB[i][j] = 0;
                contCC[i][j] = 0;
                contLB[i][j] = 0;
                contBL[i][j] = 0;
                contLC[i][j] = 0;
                contCL[i][j] = 0;
                contLL[i][j] = 0;
                contSSE[i][j] = 0;
                resContSSE[i][j] = 0;
                dblDif[i][j] = 0;               // this is fine even though it is a valid value, it will be overwritten in any case
                spatialSSE[i][j] = REL_NONE;
            }
        }
    }

    /**
     * Resets all matrices to their default values, i.e. deletes all information on contacts and spatial relations. (Simply calls init() again, but this one is public.)
     */
    public void reinit() {
        this.init();
    }


    /**
     * Returns the number of contacts of type 'type' at position (x,y).
     * @param type the contact type string, e.g. "BB" for backbone-backbone contact or "LB" for ligand-backbone contact
     * @param x the SSE index of the first SSE of the pair
     * @param y the SSE index of the first SSE of the pair
     * @return 
     */
    public Integer getContacts(String type, Integer x, Integer y) {

        if(x >= size || y >= size) {
            System.err.println("ERROR: getContacts(): Index (" + x + "/" + y + ") out of range, matrix size is " + size + ".");
            System.exit(-1);
        }

        if(type.equals("BB")) {
            return(contBB[x][y]);
        }
        else if(type.equals("BC") || type.equals("CB")) {
            return(contBC[x][y]);
        }
        else if(type.equals("CC")) {
            return(contCC[x][y]);
        }
        else if(type.equals("LB") || type.equals("BL")) {
            return(contLB[x][y]);
        }
        else if(type.equals("LC") || type.equals("CL")) {
            return(contLC[x][y]);
        }
        else if(type.equals("LL")) {
            return(contLL[x][y]);
        }
        else if(type.equals("TT")) {
            // total number of contacts
            return(contBB[x][y] + contBC[x][y] + contCC[x][y] + contLB[x][y] + contLC[x][y] + contLL[x][y]);
        }
        else if(type.equals("TP")) {
            // total number of protein contacts (all non-ligand contacts)
            return(contBB[x][y] + contBC[x][y] + contCC[x][y]);
        }
        else if(type.equals("TL")) {
            // total number of ligand contacts
            return(contLB[x][y] + contLC[x][y] + contLL[x][y]);
        }
        else {
            System.err.println("ERROR: getContacts(): Contact type '" + type + "' is not a valid contact type.");
            System.exit(1);
            return(-1);             // for the IDE
        }

    }

    
    /**
     * Adds 'num' contacts of type 'type' to the contact matrix at position (x,y).
     * @param type the contact type, e.g. "BB" for backbone-backbone, "CB" for sidechain-backbone, etc.
     * @param x SSE index of the first SSE of this SSE pair
     * @param y SSE index of the other SSE of this SSE pair
     * @param num number of contacts to add
     */
    public void addContacts(String type, Integer x, Integer y, Integer num) {

        if(x >= size || y >= size) {
            System.err.println("ERROR: addContacts(): Index (" + x + "/" + y + ") out of range, matrix size is " + size + ".");
            System.exit(1);
        }
        else {
            //System.out.println("    Adding " + num + " contacts of type " + type + " at index (" + x + "/" + y + ") of SSE list.");
        }

        if(type.equals("BB")) {
            contBB[x][y] += num;
        }
        else if(type.equals("BC") || type.equals("CB")) {   // Adding both BC and CB should not make a difference because one of the two should be empty, depending on which residue was hit first (only the one which gets hit first, i.e. the one with the lower DSSP residue number, counts).
            contBC[x][y] += num;
        }
        else if(type.equals("CC")) {
            contCC[x][y] += num;
        }
        else if(type.equals("LB") || type.equals("BL")) {
            contLB[x][y] += num;
        }
        else if(type.equals("LC") || type.equals("CL")) {
            contLC[x][y] += num;
        }
        else if(type.equals("LL")) {
            contLL[x][y] += num;
        }
        else {
            System.err.println("ERROR: addContacts(): Contact type '" + type + "' is not a valid contact type.");
            System.exit(1);
        }

    }
    
    
    /**
     * Debugging function, simply prints an atom contact count row of the atom contact matrices (e.g., contBC[][] and contCB[][]).
     * @param a the first Integer matrix
     * @param b the second Integer matrix, must have the same length as a
     * @param line the row in the matrix to use (one should be enough and its easier to compare two lines than two matrices visually). Using a line number which does not exist in the matrix is considered a fatal error.
     */
    public void printArrayComparison(Integer[][] a, Integer[][] b, Integer line) {
        
        Integer l = line;                       
        
        if(a.length != b.length) {
            System.err.println("ERROR: printArrayComparison(): Arrays need to have same length for this to make sense.");
            System.exit(1);
        }
        else {
            
            if(l >= a.length || l < 0) {
                System.err.println("ERROR: printArrayComparison(): Requested line number does not exist in matrix: " + l + ".");
                System.exit(1);
            }
            
            System.out.print("A(" + l + "): ");
            for(Integer i = 0; i < a.length; i++) {
                System.out.printf("%3d ", a[i][l]);
            }
            System.out.print("\n");
            
            System.out.print("B(" + l + "): ");
            for(Integer i = 0; i < a.length; i++) {
                System.out.printf("%3d ", b[i][l]);
            }
            System.out.print("\n");
        }               
    }


    /**
     * Calculates the SSE contacts based on the atom level contacts. When this is finished, the contSSE[][] array
     * is properly filled and you can call the functions to computer spatial relations between the SSEs.
     *
     */
    public void calculateSSEContactMatrix() {
        
        //DEBUG
        //System.out.println("Array comparison of contBC and contCB matrices follows:");
        //for(Integer i = 0; i < contBC.length; i++) {
        //    this.printArrayComparison(contBC, contCB, i);
        //}
        

        SSE a,b;
        a = b = null;

        for(Integer i = 0; i < this.size; i++) {

            a = this.sseList.get(i);

            for(Integer j = (i + 1); j < this.size; j++) {

                b = this.sseList.get(j);

                if(i.equals(j)) {
                    // Of course many contacts exist, but we are not interested in contacts of an SSE with itself. ;)
                    contSSE[i][j] = 0;
                    contSSE[j][i] = 0;
                }
                else {

                    // HH ----- Helix - Helix contacts
                    if(a.isHelix() && b.isHelix()) {
                        if( (contBC[i][j] + contCB[i][j] > 3) || (contCC[i][j] > 3) ) {
                            contSSE[i][j] = 1;
                            contSSE[j][i] = 1;
                        }
                        else {
                            contSSE[i][j] = 0;
                            contSSE[j][i] = 0;
                        }
                    }
                    // HE ----- Helix - Sheet contacts
                    else if( (a.isHelix() && b.isBetaStrand()) || (b.isHelix() && a.isBetaStrand())  ) {
                        if( ( (contBB[i][j] > 1) && (contBC[i][j] + contCB[i][j] > 3) ) || (contCC[i][j] > 3)) {
                            contSSE[i][j] = 1;
                            contSSE[j][i] = 1;
                        }
                        else {
                            contSSE[i][j] = 0;
                            contSSE[j][i] = 0;
                        }
                    }
                    // HO ----- Helix - Other contacts
                    else if( (a.isHelix() && b.isOtherSSE()) || (b.isHelix() && a.isOtherSSE())  ) {
                        if( (contBB[i][j] >= 2) || (contBC[i][j] + contCB[i][j] >= 2) || (contCC[i][j] >= 2)) {
                            contSSE[i][j] = 1;
                            contSSE[j][i] = 1;
                        }
                        else {
                            contSSE[i][j] = 0;
                            contSSE[j][i] = 0;
                        }
                    }
                    // EE ----- Sheet - Sheet contacts
                    else if(a.isBetaStrand() && b.isBetaStrand()) {
                        if( (contBB[i][j] > 1) || (contBC[i][j] + contCB[i][j] > 2)) {
                            contSSE[i][j] = 1;
                            contSSE[j][i] = 1;
                        }
                        else {
                            contSSE[i][j] = 0;
                            contSSE[j][i] = 0;
                        }
                    }
                    // Sheet - Other contacts
                    else if(a.isBetaStrand() && b.isOtherSSE() || (b.isBetaStrand() && a.isOtherSSE()) ) {
                        if( (contBB[i][j] >= 2) || (contBC[i][j] + contCB[i][j] >= 2) || (contCC[i][j] >= 2)) {
                            contSSE[i][j] = 1;
                            contSSE[j][i] = 1;
                        }
                        else {
                            contSSE[i][j] = 0;
                            contSSE[j][i] = 0;
                        }
                    }
                    // <*> - Ligand contacts
                    else if(a.isLigandSSE() || b.isLigandSSE()) {
                        if( (contLB[i][j] + contBL[i][j] >= 1) || (contLC[i][j] + contCL[i][j] >= 1) || (contLL[i][j] >= 1)) {
                            contSSE[i][j] = 1;
                            contSSE[j][i] = 1;
                        }
                        else {
                            contSSE[i][j] = 0;
                            contSSE[j][i] = 0;
                        }
                    }
                    // other - Other contacts
                    else if(a.isOtherSSE() && b.isOtherSSE()) {
                        if( (contBB[i][j] >= 2) || (contBC[i][j] + contCB[i][j] >= 2) || (contCC[i][j] >= 2)) {
                            contSSE[i][j] = 1;
                            contSSE[j][i] = 1;
                        }
                        else {
                            contSSE[i][j] = 0;
                            contSSE[j][i] = 0;
                        }
                    }
                    else {
                        System.err.println("WARNING: Contact between unhandled combination of SSE types " + a.getSseType() + " and " + b.getSseType() + ", using default rules.");
                        if( (contBB[i][j] >= 2) || (contBC[i][j] + contCB[i][j] >= 2) || (contCC[i][j] >= 2)) {
                            contSSE[i][j] = 1;
                            contSSE[j][i] = 1;
                        }
                        else {
                            contSSE[i][j] = 0;
                            contSSE[j][i] = 0;
                        }
                    }


                }

            }
        }

    }


    /**
     * Calculates the spatial relations between all SSEs and writes them to the matrix spatialSSE[][].
     * 
     * @param contList a list of residue level contacts that are used to determine SSE contacts
     * @param computeAll whether the spatial relation of SSE pairs which have too few residue level contacts 
     *        to be counted as an SSE contact (according to the definitions of this program) should also be computed.
     *        This is only useful for debugging geodat issues (see the '-y' command line option of plcc) and 
     *        slows the process down, therefore it should be set to 'false' if in doubt.
     * 
     */
    public void calculateSSESpatialRelationMatrix(ArrayList<ResContactInfo> contList, Boolean computeAll) {

        SSE sseA, sseB;
        Residue resA, resB;
        sseA = sseB = null;
        resA = resB = null;
        Integer sumMax, sumMin, difMax, difMin, tmp, resASSEPos, resBSSEPos;
        sumMax = difMax = tmp = Integer.MIN_VALUE;               // something small...
        sumMin = difMin = Integer.MAX_VALUE;               // & something laaarge :)
        
        Integer doubleDifference = null;

        Integer eeLargestAntip = Settings.getInteger("plcc_I_spatrel_dd_largest_antip_ee");
        Integer eeSmallestParallel = Settings.getInteger("plcc_I_spatrel_dd_smallest_parallel_ee");
        Integer hhLargestAntip = Settings.getInteger("plcc_I_spatrel_dd_largest_antip_hh");
        Integer hhSmallestParallel = Settings.getInteger("plcc_I_spatrel_dd_smallest_parallel_hh");
        Integer heLargestAntip = Settings.getInteger("plcc_I_spatrel_dd_largest_antip_he");
        Integer heSmallestParallel = Settings.getInteger("plcc_I_spatrel_dd_smallest_parallel_he");
        Integer defLargestAntip = Settings.getInteger("plcc_I_spatrel_dd_largest_antip_def");
        Integer defSmallestParallel = Settings.getInteger("plcc_I_spatrel_dd_smallest_parallel_def");
        
        Integer largestAntip;       // used in loop below, gets assigned one of the values above
        Integer smallestParallel;
        
        Boolean condition = false;

        //System.out.println("  Calculating spatial relations between SSEs...");

        for(Integer i = 0; i < this.size; i++) {

            sseA = this.sseList.get(i);

            for(Integer j = (i + 1); j < this.size; j++) {

                sseB = this.sseList.get(j);
                
                if(computeAll) {
                    // when computeAll is set, compute spatial relation for all SSEs which have at least a single residue level contact
                    condition = (this.getContacts("TT", i, j) > 0);
                } else {
                    // when computeAll is NOT set, compute spatial relation only for SSEs which have enough residue level contacts 
                    // to qualify as an SSE contact according to our definitions (which have been applied in 
                    // the calculateSSEContactMatrix() function).
                    
                    condition = this.sseContactExistsPos(i, j);
                }

                if(condition) {
                    
                    sumMax = difMax = Integer.MIN_VALUE;
                    sumMin = difMin = Integer.MAX_VALUE;

                    // Ligand always have the ligand relation because the others don't makes sense for them (they have no direction)
                    if(sseA.isLigandSSE() || sseB.isLigandSSE()) {
                        spatialSSE[i][j] = REL_LIGAND;
                        spatialSSE[j][i] = REL_LIGAND;
                        //System.out.println("    SSEs " + i + " and " + j + " are ligands, skipping DD calculation.");
                        continue;
                    }

                    // Compute the doubleDistance over all contact pairs of sseA and sseB
                    for(Integer k = 0; k < contList.size(); k++) {

                        resA = contList.get(k).getResA();
                        resB = contList.get(k).getResB();                                                       

                        // Get the SSEs
                        resASSEPos = getSSEPosOfDsspResidue(resA.getDsspResNum());
                        resBSSEPos = getSSEPosOfDsspResidue(resB.getDsspResNum());

                        // If they don't belong to any SSEs we are interested in, forget about them. Note that these
                        //  residues may even belong to another chain.
                        if(resASSEPos < 0 || resBSSEPos < 0) {
                            continue;
                        }

                        // If both residues belong to (different) SSEs *of this SSE pair (i,j)*
                        if((resASSEPos.equals(i) && resBSSEPos.equals(j)) || (resASSEPos.equals(j) && resBSSEPos.equals(i))) {

                            // check for new sumMax
                            tmp = contList.get(k).getDsspResNumResA() + contList.get(k).getDsspResNumResB();
                            if(tmp > sumMax) {
                                sumMax = tmp;
                            }

                            // check for new sumMin
                            if(tmp < sumMin) {
                                sumMin = tmp;
                            }

                            // check for new difMax
                            tmp = Math.abs(contList.get(k).getDsspResNumResA() - contList.get(k).getDsspResNumResB());
                            
                            // The following code is wrong! We now take the absolute value (see above) instead of the smaller one!
                            // (a - b) != (b - a), therefore always consider the smaller of these two results
                            //if(contList.get(k).getDsspResNumResB() - contList.get(k).getDsspResNumResA() < tmp) {
                            //    tmp = contList.get(k).getDsspResNumResB() - contList.get(k).getDsspResNumResA();
                            //}

                            if(tmp > difMax) {
                                difMax = tmp;
                            }

                            // check for new difMin
                            if(tmp < difMin) {
                                difMin = tmp;
                            }



                        }
                        else {
                            // This is essential to prevent calculations for residues which are not part of this SSE pair
                            continue;                              
                        }

                    }

                    //doubleDifference = (sumMax - sumMin) - (difMax - difMin);
                    doubleDifference = sumMax - sumMin - difMax + difMin;
                    //System.out.println("DEBUG: Detected DD " + doubleDifference + " for vertices " + i + " and " + j + ".");
                    
                    dblDif[i][j] = doubleDifference;
                    
                    // ----- start of DD interpretations based on the SSE types of the pair -----
                    // check SSE types
                    if(sseA.isBetaStrand() && sseB.isBetaStrand()) {
                        largestAntip = eeLargestAntip;
                        smallestParallel = eeSmallestParallel;
                    }
                    else if(sseA.isHelix() && sseB.isHelix()) {
                        largestAntip = hhLargestAntip;
                        smallestParallel = hhSmallestParallel;
                    }
                    else if( (sseA.isBetaStrand() && sseB.isHelix()) || (sseB.isBetaStrand() && sseA.isHelix()) ) {
                        largestAntip = heLargestAntip;
                        smallestParallel = heSmallestParallel;
                    }
                    else {
                        // note that ligands have been handled above, this code only runs for stuff like coil/helix
                        largestAntip = defLargestAntip;
                        smallestParallel = defSmallestParallel;
                    }
                    
                    // just a sanity test...
                    if(largestAntip == smallestParallel) {
                        System.err.println("WARNING: Double difference calculation borders are equal, should differ.");
                    }
                    // ... and another one
                    if(largestAntip > smallestParallel) {
                        System.err.println("WARNING: Double difference calculation borders are inverted: anti-parallel value should be larger than parallel value but it is vice versa.");
                    }
                    
                    
                    if(doubleDifference <= largestAntip) {
                        spatialSSE[i][j] = REL_ANTIPARALLEL;
                        spatialSSE[j][i] = REL_ANTIPARALLEL;
                        if(Settings.getInteger("plcc_I_debug_level") > 0) {
                            System.out.println("    SSEs " + i + " and " + j + " are antiparallel (DD=" + doubleDifference + ")." + sseA + ", " + sseB);
                        }
                    }
                    else if(doubleDifference >= smallestParallel) {
                        spatialSSE[i][j] = REL_PARALLEL;
                        spatialSSE[j][i] = REL_PARALLEL;
                        if(Settings.getInteger("plcc_I_debug_level") > 0) {
                            System.out.println("    SSEs " + i + " and " + j + " are parallel (DD=" + doubleDifference + ")." + sseA + ", " + sseB);
                        }
                    }
                    else {
                        // DD = 0
                        spatialSSE[i][j] = REL_MIXED;
                        spatialSSE[j][i] = REL_MIXED;
                        if(Settings.getInteger("plcc_I_debug_level") > 0) {
                            System.out.println("    SSEs " + i + " and " + j + " are mixed (DD=" + doubleDifference + ")." + sseA + ", " + sseB);
                        }
                    }
                    
                    // ----- end of DD interpretations -----

                }
                else {
                    // The two SSEs are not in contact.
                    spatialSSE[i][j] = REL_NONE;
                    spatialSSE[j][i] = REL_NONE;
                }                

            }
        }

    }


    /*
     * Returns a JGraphT object of this contact matrix (=graph). No longer used, we now use our own ProtGraph class instead.
     *
     */
    /*
    public ProteinGraph<SSE, LabeledEdge> toJGraph() {

        SSE sseA, sseB;
        sseA = sseB = null;


        ProteinGraph<SSE, LabeledEdge> graph = new ProteinGraph<SSE, LabeledEdge>(new ClassBasedEdgeFactory<SSE, LabeledEdge>(LabeledEdge.class));

        

        // add all SSEs (vertices) to the graph
        for (Integer i = 0; i < this.sseList.size(); i++) {
            graph.addVertex(sseList.get(i));
        }

        // add all edges to the graph
        for (Integer i = 0; i < this.sseList.size(); i++) {

            sseA = sseList.get(i);

            for (Integer j = 0; j < this.sseList.size(); j++) {

                sseB = sseList.get(j);

                if(this.sseContactExistsPos(i, j)) {
                    graph.addEdge(sseA, sseB, new LabeledEdge<String>(vertexNameSsePos(i), vertexNameSsePos(j), spatialInt2String(spatialSSE[i][j])));
                }

            }

        }
        return(graph);
    }
     * 
     */

    
    /**
     * Creates a ProtGraph object from the SSE objects and the SSE-level contacts stored in this matrix.
     */
    public ProtGraph toProtGraph() {
        // add vertices
        ProtGraph pg = new ProtGraph(sseList);

        // add edges
        for(Integer i = 0; i < sseList.size(); i++) {
            for(Integer j = 0; j < sseList.size(); j++) {
                if(this.sseContactExistsPos(i, j)) {
                    pg.addContact(i, j, spatialSSE[i][j]);
                }

            }
        }
        return(pg);
    }


    /**
     * Returns the graph vertex label of the SSE at position ssePos in the sseList.
     */
    private String vertexNameSsePos(Integer ssePos) {
        return(this.sseList.get(ssePos).shortStringRep());
    }


    /**
     * Writes the contact statistics to the database,
     */
    public void writeContactStatisticsToDB() {
        for(Integer i = 0; i < this.sseList.size(); i++) {
            for(Integer j = i + 1; j < this.sseList.size(); j++) {
                if(this.sseContactExistsPos(i, j)) {
                    DBManager.writeContactToDB(this.pdbid, this.handleChain, sseList.get(i).getStartDsspNum(), sseList.get(j).getStartDsspNum(), spatialSSE[i][j]);
                }
            }
        }


    }
    
    
    /**
     * Returns a string that represents this contact matrix in PTGL geo.dat (or geolig.dat) format.
     * @param makeItGeoligdat if this is true, it will return geolig.dat instead of geo.dat format
     * @param printAllContacts also print contacts that did NOT qualify as an SSE contact because of too few lower level contacts
     * @return a string representing this CM. Each line represents an SSE level contact and
     * consists of 9 fields separated by space:
     * <pdbid><chain> <SSE#1> <SSE#2> <int1> <int2> <int3> <int4> <spatial_relation> <double_difference>
     * The fields are:
     * <pdbid><chain>      : the PDBID and CHAIN of the protein (e.g.: 8icdA)
     * <SSE#1>             : sequential number of the 1st contact SSE
     * <SSE#2>             : sequential number of the 2nd contact SSE
     * <int1>              : number of BB contacts (between residues of both SSEs, counting max 1 contact per residue contact pair)
     * <int2>              : number of CB contacts ...
     * <int3>              : number of BC contacts ...
     * <int4>              : number of CC contacts ...
     * <spatial_relation>  : the spatial relation between the 2 SSEs: p=parallel, a=antiparallel, m=mixed
     * <double_difference> : the double difference value that was computed to determine <spatial_relation>
     * Example line: '3kmfA 1 4 0 5 0 3 m -4'
     */
    public String toGeodatFormat(Boolean makeItGeoligdat, Boolean printAllContacts) {
        
        String line = "";
        String lines = "";
        SSE sseA, sseB;
        
        for(Integer i = 0; i < this.size; i++) {

            sseA = this.sseList.get(i);

            for(Integer j = (i + 1); j < this.size; j++) {

                sseB = this.sseList.get(j);

                if(this.sseContactExistsPos(i, j) || printAllContacts) { 
                    
                    // skip this entry if it has no contacts at all
                    if(makeItGeoligdat) {
                        // we consider ligands, so check all contacts including ligand contacts
                        if(this.getContacts("TT", i, j) <= 0) {
                            continue;
                        }                        
                    }
                    else {
                        // we do not consider ligands
                        if(this.getContacts("TP", i, j) <= 0) {
                            continue;
                        }                        
                    }
                    
                    
                    if(makeItGeoligdat) {
                        // TODO: add lig fields
                        System.err.println("ERROR: toGeodatFormat(): Geolig.dat format not implemented yet.");
                        System. exit(1);
                        line = "ERROR: GEOLIG.DAT FORMAT NOT IMPLEMENTED YET\n";
                    }
                    else {
                        line = pdbid + handleChain + " " + (i+1) + " " + (j+1) + " " + contBB[i][j] + " " + contCB[i][j] + " " + contBC[i][j]  + " " + contCC[i][j] + " " + SpatRel.getString(spatialSSE[i][j]) + " " + dblDif[i][j] + "\n";
                    }                                        
                    
                    lines += line;
                }
            }
        }
        
        return(lines);
    }

}

