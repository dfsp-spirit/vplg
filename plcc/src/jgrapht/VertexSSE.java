/*
 * This file is part of SPARGEL, the sparse graph extension library.
 * 
 * This is free software, published under the WTFPL.
 * 
 * Written by Tim Schaefer, see http://rcmd.org/contact/.
 * 
 */
package jgrapht;

/**
 * A vertex for a protein ligand graph. Represents a secondary structure element (SSE).
 * @author ts
 */
public class VertexSSE {
       
    
    public static final String SSE_TYPE_LIGAND = "L";                   // ligand
    public static final String SSE_TYPE_BETASTRAND = "E";               // beta strand that is part of a beta sheet
    public static final String SSE_TYPE_ALPHA_HELIX = "H";              // alpha helix
    public static final String SSE_TYPE_ISOLATED_BETA = "B";            // isolated beta strand, not part of any beta sheet
    public static final String SSE_TYPE_3HELIX = "G";                   // 3-helix (3 turns per 10 residues)
    public static final String SSE_TYPE_5HELIX = "I";                   // 5-helix (pi helix)
    public static final String SSE_TYPE_HTURN = "T";                    // hydrogen-bonded turn
    public static final String SSE_TYPE_BEND = "S";                     // bend
    public static final String SSE_TYPE_COIL = "C";                     // never assigned by DSSP, it calls these " "
    
    protected String sseType;
    protected String residueString;
    protected int firstResidueDsspNumber;
    protected int lastResidueDsspNumber;
    protected Integer sequentialSSENumberInChain;

    public Integer getSequentialSSENumberInChain() {
        return sequentialSSENumberInChain;
    }

    public void setSequentialSSENumberInChain(Integer sequentialSSENumberInChain) {
        this.sequentialSSENumberInChain = sequentialSSENumberInChain;
    }
    protected String firstResiduePdbString;

    public String getFirstResiduePdbString() {
        return firstResiduePdbString;
    }

    public void setFirstResiduePdbString(String firstResiduePdbString) {
        this.firstResiduePdbString = firstResiduePdbString;
    }

    public String getLastResiduePdbString() {
        return lastResiduePdbString;
    }

    public void setLastResiduePdbString(String lastResiduePdbString) {
        this.lastResiduePdbString = lastResiduePdbString;
    }
    protected String lastResiduePdbString;
    
    public VertexSSE(String sseType, int seqNumInChain) {
        this.sequentialSSENumberInChain = seqNumInChain;
        this.sseType = sseType;
        this.residueString = "";
        this.firstResidueDsspNumber = -1;
        this.lastResidueDsspNumber = -1;
        this.firstResiduePdbString = "";
        this.lastResiduePdbString = "";
    }

    public String getSseType() {
        return sseType;
    }

    public void setSseType(String sseType) {
        this.sseType = sseType;
    }

    public int getFirstResidueDsspNumber() {
        return firstResidueDsspNumber;
    }

    public void setFirstResidueDsspNumber(int firstResidueDsspNumber) {
        this.firstResidueDsspNumber = firstResidueDsspNumber;
    }

    public int getLastResidueDsspNumber() {
        return lastResidueDsspNumber;
    }

    public void setLastResidueDsspNumber(int lastResidueDsspNumber) {
        this.lastResidueDsspNumber = lastResidueDsspNumber;
    }
    
    public int getSequenceLength() {
        return this.residueString.length();
    }

    public String getResidueString() {
        return residueString;
    }

    public void setResidueString(String residueString) {
        this.residueString = residueString;
    }
    
    @Override
    public String toString() {
      return "[[V]t=" + this.sseType +  "]";
    }
}
