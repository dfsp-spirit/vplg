/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package proteinstructure;


// imports
import java.util.ArrayList;
import tools.Comp3DTools;
import graphdrawing.IDrawableVertex;
import proteingraphs.Position3D;
import proteingraphs.SSEGraph;
import proteingraphs.SSEGraphVertex;
import tools.DP;

/**
 * Represents a secondary structure element (SSE), e.g., an alpha-helix or a beta-strand.
 * 
 * @author ts
 */
public class SSE extends SSEGraphVertex implements IDrawableVertex, java.io.Serializable  {

    
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
    public static final String SSE_TYPE_OTHER = "O";                     // never assigned by DSSP, it calls these " "

    public static final Integer SSECLASS_NONE = 0;
    public static final Integer SSECLASS_HELIX = 1;
    public static final Integer SSECLASS_BETASTRAND = 2;
    public static final Integer SSECLASS_LIGAND = 3;
    public static final Integer SSECLASS_OTHER = 4;
    
    public static final String SSE_FGNOTATION_HELIX = "h";
    public static final String SSE_FGNOTATION_STRAND = "e";
    public static final String SSE_FGNOTATION_LIGAND = "l";
    public static final String SSE_FGNOTATION_OTHER = "o";

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
     * Constructor that sets the SSE type to 'sseType'. 
     * @param sseType It should be "H", "E", ... etc. Use SSE.SSE_TYPE_* constants.
     */
    public SSE(String sseType) {
        super();
        this.sseType = sseType;
        residues = new ArrayList<>();
    }
    
    /**
     * Returns the PTGL string notation for folding graphs for this sse type (like "e" for strand).
     * @return "h" forlhelix, etc (helo)
     */
    @Override
    public String getSseFgNotation() {
        if(this.isHelix()) { return(SSE.SSE_FGNOTATION_HELIX); }
        else if(this.isBetaStrand()) { return(SSE.SSE_FGNOTATION_STRAND); }
        else if(this.isLigandSSE()) { return(SSE.SSE_FGNOTATION_LIGAND); }
        else if(this.isOtherSSE()) { return(SSE.SSE_FGNOTATION_OTHER); }
        else {
            DP.getInstance().w("SSE", "getSseFgNotation: SSE is none of the check types, assuming SS_FGNOTATION_OTHER.");
            return(SSE.SSE_FGNOTATION_OTHER); 
        }
    }
    
    public static Integer sseClassFromFgNotation(String n) {
        if(null == n) {
            DP.getInstance().w("SSE", "sseClassFromFgNotation: given notation is null. Assuming SSECLASS_OTHER.");
            return SSE.SSECLASS_OTHER;
        }
        if(n.equals("h")) {
            return SSE.SSECLASS_HELIX;
        }
        else if(n.equals("e")) { return SSE.SSECLASS_BETASTRAND; }
        else if(n.equals("l")) { return SSE.SSECLASS_LIGAND; }
        else { return SSE.SSECLASS_OTHER; } 
    }
    
    /**
     * Constructor that sets the SSE type by the SSE class 'sseClass'. Use SSE.SSECLASS_* constants.
     */
    public SSE(Integer sseClass) {
        if(sseClass.equals(SSE.SSECLASS_HELIX)) {
            this.sseType = SSE.SSE_TYPE_ALPHA_HELIX;
        } 
        else if(sseClass.equals(SSE.SSECLASS_BETASTRAND)) {
            this.sseType = SSE.SSE_TYPE_BETASTRAND;
        }
        else if(sseClass.equals(SSE.SSECLASS_LIGAND)) {
            this.sseType = SSE.SSE_TYPE_LIGAND;
        }
        else if(sseClass.equals(SSE.SSECLASS_OTHER)) {
            this.sseType = SSE.SSE_TYPE_OTHER;
        }
        else {
            DP.getInstance().w("SSE", "<constructor>: Creating SSE of invalid class '" + sseClass + "' not possible. Assuming alpha helix.");
            this.sseType = SSE.SSE_TYPE_ALPHA_HELIX;
        }
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
        if(this.isHelix()) { return(SSE.SSECLASS_HELIX); }
        else if(this.isBetaStrand()) { return(SSE.SSECLASS_BETASTRAND); }
        else if(this.isLigandSSE()) { return(SSE.SSECLASS_LIGAND); }
        else if(this.isOtherSSE()) { return(SSE.SSECLASS_OTHER); }
        else { return(SSE.SSECLASS_NONE); }
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
    
    public String getSSEClass() {
        if(this.isHelix()) {
            return "H";
        }
        else if(this.isBetaStrand()) {
            return "E";
        }
        else if(this.isLigandSSE()) {
            return "L";
        }
        else {
            return "O";
        }
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
    
    /** Returns the DSSP SSEType string of this SSE. This is a single character, e.g., "T" for turn, "H" for an alpha helix. */
    public String getSseType() { return(sseType); }
    public ArrayList<Residue> getResidues() { return(residues); }
    public Chain getChain() { return(chain); }
    
    /**
     * Returns the length of this SSE in residues.
     * @return The number of residues this SSE consists of
     */
    public Integer getLength() { return(residues.size()); }
    
    public Integer getSeqSseNumDssp() { return(seqSseNumDssp); }

    /**
     * Returns the amino acid sequence of this SSE, determined from the 1-letter-code names of its residues.
     * @return the amino acid sequence of this SSE
     */
    public String getAASequence() {
        StringBuilder seq = new StringBuilder();

        Residue r;
        for(Integer i = 0; i < this.residues.size(); i++) {
            r = this.residues.get(i);
            seq.append(r.getAAName1());
        }

        return(seq.toString());
    }
    
    /**
     * Returns the chemical properties string of all AAs of this SSE, according to the 5 types system
     * @return the chemical properties string of all AAs of this SSE, according to the 5 types system
     */
    public String getallAAChemProp5String() {
        StringBuilder chemProps = new StringBuilder();

        Residue r;
        for(Integer i = 0; i < this.residues.size(); i++) {
            r = this.residues.get(i);
            chemProps.append(r.getChemicalProperty5OneLetterString());
        }

        return(chemProps.toString());
    }
    
    /**
     * Returns the chemical properties string of all AAs of this SSE, according to the 3 types system
     * @return the chemical properties string of all AAs of this SSE, according to the 3 types system
     */
    public String getallAAChemProp3String() {
        StringBuilder chemProps = new StringBuilder();

        Residue r;
        for(Integer i = 0; i < this.residues.size(); i++) {
            r = this.residues.get(i);
            chemProps.append(r.getChemicalProperty3OneLetterString());
        }

        return(chemProps.toString());
    }
    
    /**
     * Determines the position of the central atom of this SSE. For real SSEs consisting of amino acids, the central
     * atom of all alpha carbons is chosen. For ligands, the central atom of the first residue is chosen.
     * @return the position of the central atom of this SSE
     */
    public Position3D getCentralAtomPosition() {
        if(this.isLigandSSE()) {
            // this is a ligand
            if(this.residues.size() > 0) {
                return this.residues.get(0).getCenterAtom().getPosition3D();
            }
            else {
                return null;
            }
        } else {
            // this is a protein SSE. 
            
            // First get all CA atoms.
            ArrayList<Atom> alphaCarbons = new ArrayList<Atom>();
            Atom ca;
            for(Residue r : this.residues) {
                ca = r.getAlphaCarbonAtom();
                if(ca != null) { 
                    alphaCarbons.add(ca);
                }
            }
            
            // now determine the central one
            if(alphaCarbons.isEmpty()) {
                // no alpha carbons -- just return some Atom if there are any
                if(this.residues.size() > 0) {
                    if(this.residues.get(0).getAtoms().size() > 0) {
                        return this.residues.get(0).getAtoms().get(0).getPosition3D();
                    }
                }
            } else {
                // we have some alpha carbons, determine the central one
                Atom c = Comp3DTools.getCenterAtomOf(alphaCarbons);
                if(c != null) {
                    return c.getPosition3D();
                }
            }
        }
        return null;
    }
    
    /**
     * Determines whether this SSE currently has at least one residue with at least one atom.
     * @return 
     */
    public boolean hasResidueWithAtoms() {
        if(this.residues.size() > 0) {
            for(Residue r : this.residues) {
                if(r.hasAtoms()) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override public String toString() {
        return("[" + sseType + ":DSSP:" + this.getStartResidue().getDsspResNum() + "-" + this.getEndResidue().getDsspResNum() + ",PDB:" + this.getStartResidue().getUniquePDBName() + "-" + this.getEndResidue().getUniquePDBName() +"]");
    }
    
    /**
     * Returns a very short label string for this SSE, e.g., "1-H" for SSE #1 in the chain, a helix.
     * @return a very short label string for this SSE, e.g., "1-H" for SSE #1 in the chain, a helix.
     */ 
    public String shortLabel() {
        return("" + sseType.toLowerCase() + this.sequentialIndexInGraph);
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
            return null;
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
            return null;
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
        Residue r = this.getStartResidue();
        if(r == null) {
            DP.getInstance().w("SSE", "getStartDsspNum(): This SSE has no residues.");
            return 0;
        }
        return(r.getDsspResNum());
    }

    /**
     * Returns a unique String identifying the first residue of this SSE in the PDB file.
     */
    public String getStartPdbResID() {
        return(this.getStartResidue().getUniquePDBName());
    }

    /**
     * Returns a String identifying all residues of this SSE in the PDB file.
     * @param separator the separator to use between the residue strings, e.g., ","
     */
    public String getAllPdbResiduesString(String separator) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < this.residues.size(); i++) {
            Residue r = this.residues.get(i);
            sb.append(r.getUniquePDBName());
            if(i < this.residues.size() - 1) {
                sb.append(separator);
            }
        }
        return(sb.toString());
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
        Residue r = this.getEndResidue();
        if(r == null) {
            DP.getInstance().w("SSE", "getEndDsspNum(): This SSE has no residues.");
            return 0;
        }
        return(r.getDsspResNum());
    }
    
    /**
     * Returns the 3-letter ligand residue name from the PDB file name (e.g., ICT for isocitric acid) of this residue. Returns the empty string ("") if this is not a ligand SSE or if this SSE has no residues.
     * @return a string of length 3
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
     * Returns the trimmed 3-letter ligand residue name from the PDB file name (e.g., 'ICT' for isocitric acid or 'NA' for sodium ion) of this residue. Returns the empty string ("") if this is not a ligand SSE or if this SSE has no residues.
     * @return a string of length 1 - 3
     */
    public String getTrimmedLigandName3() {

        
        if(! this.isLigandSSE()) {
            return("");
        }
        
        if(this.residues.size() > 0) {
            return(this.residues.get(0).getTrimmedName3());
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
     *  WARNING: Note that this may make no sense if the SSE is part of several graphs!
     *  It also sets inGraph=true for this SSE.
     * @param index the sequential index in the graph, i.e., the index in the AA sequence
     */
    @Deprecated
    public void setSeqIndexInGraph(Integer index) { this.sequentialIndexInGraph = index; this.inGraph = true; }
    
    
    /** Sets the spatial number of this SSE in its current graph. 
     *  WARNING: Note that this may make no sense if the SSE is part of several graphs!
     *  It also sets inGraph=true for this SSE.
     * @param index the spatial index in the graph
     */
    @Deprecated
    public void setSpatialIndexInGraph(Integer index) { this.spatialIndexInGraph = index; this.inGraph = true; }
    
    
    /** Returns the sequential number of this SSE in its current graph or -1 if it not yet part of a protein graph. WARNING: Note that this may make no sense if the SSE is part of several graphs!
     * @return the sequential number of this SSE in its current graph or -1 if it not yet part of a protein graph. WARNING: Note that this may make no sense if the SSE is part of several graphs!
     */
    @Deprecated
    public Integer getSeqIndexInGraph() { 
        if(this.inGraph) { 
            return(this.sequentialIndexInGraph);
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
