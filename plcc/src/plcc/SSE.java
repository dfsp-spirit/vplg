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

/**
 * Represents a secondary structure element (SSE), e.g., an alpha-helix or a beta-strand.
 * 
 * @author ts
 */
public class SSE implements java.io.Serializable {


    // settings
    private Integer MAX_RES = 100000;
    public static final String SSE_TYPE_LIGAND = "L";                   // ligand
    public static final String SSE_TYPE_BETASTRAND = "E";               // beta strand that is part of a beta sheet
    public static final String SSE_TYPE_ALPHA_HELIX = "H";              // alpha helix
    public static final String SSE_TYPE_ISOLATED_BETA = "B";            // isolated beta strand, not part of any beta sheet
    public static final String SSE_TYPE_3HELIX = "G";                   // 3-helix (3 turns per 10 residues)
    public static final String SSE_TYPE_5HELIX = "I";                   // 5-helix (pi helix)
    public static final String SSE_TYPE_HTURN = "T";                    // hydrogen-bonded turn
    public static final String SSE_TYPE_BEND = "S";                     // bend
    public static final String SSE_TYPE_COIL = "C";                     // never assigned by DSSP, it calls these " "


    // Test for SSE adj graph
    /** The sequential index of this SSE in the graph (the index in the AA sequence, N to C terminus). Starts with 0. Negative value means unset. */
    private Integer sequentialIndexInGraph;
    
    /** The spatial index of this SSE in the graph. Starts with 0. Does not exist for some graphs, e.g., bifurcated graphs. Negative value means unset. */
    private Integer spatialIndexInGraph;
    
    /** True if this SSE has been assigned to an SSE graph already, false otherwise. */
    private Boolean inGraph;        

    
    // declare class vars
    private String sseIDPtgl = null;            // the PTGL-style SSE id (starting with "A" t the N-terminus, then A..Za..z
    
    /** sequential number of the SSE in it's chain (N-terminus to C-terminus, 0..n). Note that the number in the graph is a property of a DrawSSE only. */
    private Integer sseSeqChainNum = null;         // 
    private String sseType = null;
    private ArrayList<Residue> residues = null;
    private Chain chain = null;
    private Integer seqSseNumDssp = null;
    private String betaSheetLabel = null;

    /**
     * Constructor that sets the SSE type to 'sseType'. It should be "H", "E", ... etc.
     */
    public SSE(String sseType) {
        this.sseType = sseType;
        residues = new ArrayList<Residue>();
    }

    
    /**
     * Returns true if this SSE is a ligand SSE..
     */
    public Boolean isLigandSSE() {
        if(sseType.equals(SSE.SSE_TYPE_LIGAND)) {
            return(true);
        }
        return(false);
    }

    /**
     * Returns an integer that encodes the SSE type as follows: 1=helix, 2=beta strand, 3=ligand, 4=other. Only used by the statistics DB function atm.
     */
    public Integer getSSETypeInt() {
        if(this.isHelix()) { return(1); }
        else if(this.isBetaStrand()) { return(2); }
        else if(this.isLigandSSE()) { return(3); }
        else if(this.isOtherSSE()) { return(4); }
        else { return(0); }
    }

    /**
     * Returns true if this SSE is a helix.
     */
    public Boolean isHelix() {
        if(sseType.equals(SSE.SSE_TYPE_ALPHA_HELIX) || sseType.equals(SSE.SSE_TYPE_3HELIX) || sseType.equals(SSE.SSE_TYPE_5HELIX)) {
            return(true);
        }
        return(false);
    }

    /**
     * Returns true if this SSE is a beta strand.
     */
    public Boolean isBetaStrand() {
        if(sseType.equals(SSE.SSE_TYPE_BETASTRAND) || sseType.equals(SSE.SSE_TYPE_ISOLATED_BETA)) {
            return(true);
        }
        return(false);
    }

    /**
     *
     * Returns true if this is not a helix, beta strand or ligand SSE.
     */
    public Boolean isOtherSSE() {
        if(sseType.equals(SSE.SSE_TYPE_HTURN) || sseType.equals(SSE.SSE_TYPE_BEND) || sseType.equals(SSE.SSE_TYPE_COIL)) {
            return(true);
        }
        return(false);
    }
    
    /**
     * Returns the PLCC SSE type label (e.g., "H" for a helic, "L" for a ligand" SSE).
     * @return the PLCC SSE type label (e.g., "H" for a helic, "L" for a ligand" SSE).
     */
    public String getPLCCSSELabel() {
        if(this.isHelix()) {
            return "H";
        } else if(this.isBetaStrand()) {
            return "E";
        } else if(this.isLigandSSE()) {
            return "L";
        } else {
            return "O";
        }
    }


    /**
     * Not used yet.
     */
    public Boolean isPartOfBetaSheet() {
        System.out.println("WARNING: SSE.isPartOfBetaSheet(): Not implemented yet, returning TRUE.");
        if(sseType.equals(SSE.SSE_TYPE_BETASTRAND)) {
            return(true);
        }
        return(false);
    }

    // getters
    public String getSseIDPtgl() { return(sseIDPtgl); }
    
    /**
     * Determines the sequential number of this SSE in the primary sequence of its chain, N- to C-terminus.
     * @return the sequential SSE number in this chain (starts with 1, it is NOT an index).
     */
    public Integer getSSESeqChainNum() { return(sseSeqChainNum); }
    
    /** Returns the DSSP SSEType string of this SSE. This is a single character, e.g., "H" for an alpha helix. */
    public String getSseType() { return(sseType); }
    public ArrayList<Residue> getResidues() { return(residues); }
    public Chain getChain() { return(chain); }
    
    /**
     * Returns the length of this SSE in residues.
     * @return The number of residues this SSE consists of
     */
    public Integer getLength() { return(residues.size()); }
    
    public Integer getSeqSseNumDssp() { return(seqSseNumDssp); }

    public String getAASequence() {
        String seq = "";

        Residue r;
        for(Integer i = 0; i < this.residues.size(); i++) {
            r = this.residues.get(i);
            seq += r.getAAName1();
        }

        return(seq);
    }


    @Override public String toString() {
        return("[" + sseType + ":DSSP:" + this.getStartResidue().getDsspResNum() + "-" + this.getEndResidue().getDsspResNum() + ",PDB:" + this.getStartResidue().getUniquePDBName() + "-" + this.getEndResidue().getUniquePDBName() +"]");
    }

    /**
     * Returns a string representation of this SSE object.
     * @return a string representation of this SSE object.
     */
    public String longStringRep() {
        return("[SSE] # " + seqSseNumDssp + ", type " + sseType + ", DSSP residues " + this.getStartResidue().getDsspResNum() + ".." + this.getEndResidue().getDsspResNum() + " (length " + this.getLength() + "), sequence='" + this.getAASequence() + "'");
    }

    /**
     * Returns a string representation of this SSE object.
     * @return a string representation of this SSE object.
     */
    public String shortStringRep() {
        return(this.toString());
    }
    

    /**
     * Returns the first Residue of this SSE (N- to C-terminus).
     * @return the Residue object
     */
    public Residue getStartResidue() {

        if(residues.size() < 1) {
            System.err.println("ERROR: Empty SSE '" + sseIDPtgl + "' has no start. Check size before asking.");
            System.exit(1);
        }

        Integer minResNumDssp = MAX_RES;
        Residue startRes = null;

        Residue r;
        for(Integer i = 0; i < residues.size(); i++) {

            r = residues.get(i);

            if(r.getDsspResNum() < minResNumDssp) {

                minResNumDssp = r.getDsspResNum();
                startRes = r;
            }
        }

        if(startRes == null || minResNumDssp == MAX_RES) {
            System.err.println("ERROR: Could not determine start residue of non-empty SSE '" + sseIDPtgl + "' with length " + this.residues.size() + ".");
            System.exit(1);
        }

        return(startRes);
    }

    /**
     * Determines the DSSP residue number of the first residue in this SSE. You have to make sure
     * that the SSE is non-empty (has >= 1 residues) before calling this.
     *
     * @return The DSSP residue number of the first residue of this SSE.
     */
    public Integer getStartDsspNum() {
        return(this.getStartResidue().getDsspResNum());
    }

    /**
     * Returns a unique String identifying the first residue of this SSE in the PDB file.
     */
    public String getStartPdbResID() {
        return(this.getStartResidue().getUniquePDBName());
    }

    /**
     * Returns a unique String identifying the last residue of this SSE in the PDB file.
     */
    public String getEndPdbResID() {
        return(this.getEndResidue().getUniquePDBName());
    }

    /**
     * Determines the DSSP residue number of the last residue in this SSE. You have to make sure
     * that the SSE is non-empty (has >= 1 residues) before calling this.
     *
     * @return The DSSP residue number of the last residue of this SSE.
     */
    public Integer getEndDsspNum() {
        return(this.getEndResidue().getDsspResNum());
    }
    
    /**
     * Returns the 3-letter ligand residue name from the PDB file name (e.g., ICT for isocitric acid) of this residue. Returns the empty string ("") if this is not a ligand SSE or if this SSE has no residues.
     */
    public String getLigandName3() {

        
        if(! this.isLigandSSE()) {
            return("");
        }
        
        if(this.residues.size() > 0) {
            return(this.residues.get(0).getName3());
        }
        else {
            return("");
        }
    }


    /**
     * Returns the last Residue (i.e., the one with the highest DSSP number) of this SSE.
     */
    public Residue getEndResidue() {

        if(residues.size() < 1) {
            System.err.println("ERROR: Empty SSE '" + sseIDPtgl + "' has no end residue. Check size before asking.");
            System.exit(-1);
        }

        Integer maxResNumDssp = -1;
        Residue endRes = null;

        Residue r;
        for(Integer i = 0; i < residues.size(); i++) {

            r = residues.get(i);

            if(r.getDsspResNum() > maxResNumDssp) {

                maxResNumDssp = r.getDsspResNum();
                endRes = r;
            }
        }

        if(endRes == null || maxResNumDssp == -1) {
            System.err.println("ERROR: Could not determine end residue of non-empty SSE '" + sseIDPtgl + "' with length " + this.residues.size() + ".");
            System.exit(1);
        }

        return(endRes);
    }

    // setters
    public void setSseIDPtgl(String sID) { this.sseIDPtgl = sID; }
    
    /**
     * Sets the SSE type of this SSE to 'sT'.
     * @param sT 
     */
    public void setSseType(String sT) { this.sseType = sT; }
    
    /**
     * Adds the residue r to this SSE.
     * @param r 
     */
    public void addResidue(Residue r) { 
        if(r == null) {
            throw new IllegalArgumentException("Residue must not be null.");
        }
        this.residues.add(r); 
        r.setSSETypePlcc(this.getPLCCSSELabel());
    }
    
    public void setChain(Chain c) { this.chain = c; }
    public void setSeqSseNumDssp(Integer s) { this.seqSseNumDssp = s; }
    
    /** Sets the sequential number of this SSE in the primary sequence of the protein chain. 
     * @param s the sequential number of this SSE in the primary sequence (N- to C-terminus), starts with 1.
     */
    public void setSeqSseChainNum(Integer s) { this.sseSeqChainNum = s; }

    public void addResidues(ArrayList<Residue> rl) {
        for(Integer i = 0; i < rl.size(); i++) {
            this.addResidue(rl.get(i));
        }
    }
    
    
    /**
     * Determines the distance of the SSE to the SSE s in the primary structure. This means it returns
     * the number of residues between the SSEs in the AA sequence. This is based on DSSP residue numbers.
     * @param s the other SSE
     * @return the distance in residues (difference in DSSP residue numbers)
     */
    public Integer getPrimarySeqDistanceInAminoAcidsTo(SSE s) {
        
        Integer d = -100000;    // ignored anyways, just for the System.exit() cases to calm the IDE
        
        if(this.equals(s)) {
            return(0);
        }
        
        //if( ! s.getChain().equals(this.getChain())) {
        //    System.err.println("ERROR: getPrimarySeqDistanceTo(): The compared SSEs do not belong to the same chain.");
        //    System.exit(1);
        //}
       
        
        // determine which residue comes first (N=>C terminus)
        if(s.getStartDsspNum() < this.getStartDsspNum()) {
            d = this.getStartDsspNum() - s.getEndDsspNum() - 1;
        }
        else if(s.getStartDsspNum() > this.getStartDsspNum()) {
            d = s.getStartDsspNum() - this.getEndDsspNum() - 1;
        }
        else {
            System.err.println("ERROR: getPrimarySeqDistanceInAminoAcidsTo(): The compared SSEs overlap.");
            System.exit(1);
        }
        
        return(d);
    }
    
    
    /**
     * A comparator that compares a pair of SSEs. They are considered identical if they are from the same chain AND their DSSP start residues and DSSP end residues are identical.
     * @param s the other SSE
     * @return true if they are equal according to the definition given above, false otherwise
     */
    public Boolean sameSSEas(SSE s) {
        Boolean same = false;
        
        if( this.getChain().getPdbChainID().equals(s.getChain().getPdbChainID()) && this.getStartDsspNum() == s.getStartDsspNum() && this.getEndDsspNum() == s.getEndDsspNum()) {
            same = true;
        }
        
        return(same);
    }
    
    
    /** Sets the sequential number of this SSE in its current graph (the index in the AA sequence, N to C terminus). This should be its index in the graph's SSE list and thus start with 0.
     *  It also sets inGraph=true for this SSE.
     * @param index the sequential index in the graph, i.e., the index in the AA sequence
     */
    public void setSeqIndexInGraph(Integer index) { this.sequentialIndexInGraph = index; this.inGraph = true; }
    
    
    /** Sets the spatial number of this SSE in its current graph. 
     *  It also sets inGraph=true for this SSE.
     * @param index the spatial index in the graph
     */
    public void setSpatialIndexInGraph(Integer index) { this.spatialIndexInGraph = index; this.inGraph = true; }
    
    
    /** Returns the sequential number of this SSE in its current graph or -1 if it not yet part of a protein graph. 
     * @return the sequential number of this SSE in its current graph or -1 if it not yet part of a protein graph
     */
    public Integer getSeqIndexInGraph() { 
        if(this.inGraph) { 
            return(this.sequentialIndexInGraph);
        } 
        else {
            return(-1);
        }
    }
    
    /** Returns the spatial number of this SSE in its current graph or -1 if it not yet part of a protein graph or -2 if no such ordering exists for this graph. 
     * @return the spatial number of this SSE in its current graph or -1 if it not yet part of a protein graph or -2 if no such ordering exists for this graph 
     */
    public Integer getSpatialIndexInGraph() { 
        if(this.inGraph) { 
            return(this.spatialIndexInGraph);
        } 
        else {
            return(-1);
        }
    }
    
       
    
    /**
     * Convenience function, returns the notation label for this SSE for the linear notations (SEQ, KEY, ...). E.g., "e" for a beta strand and "h" for a helix.
     * @return a one-letter string representing the label
     */
    public String getLinearNotationLabel() {
        if(this.isHelix()) { return(SSEGraph.notationLabelHelix); }
        else if(this.isBetaStrand()) { return(SSEGraph.notationLabelStrand); } 
        else if(this.isLigandSSE()) { return(SSEGraph.notationLabelLigand); } 
        else { return(SSEGraph.notationLabelOther); }            
    }
    
    

}
