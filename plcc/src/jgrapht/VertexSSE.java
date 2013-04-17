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
    
    public static final int SSE_TYPE_HELIX = 0;
    public static final int SSE_TYPE_BETASTRAND = 1;
    public static final int SSE_TYPE_LIGAND = 1;
    
    protected int sseType;
    protected String residueString;
    protected int firstResidueDsspNumber;
    protected int lastResidueDsspNumber;
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
    
    public VertexSSE(int sseType) {
        this.sseType = sseType;
        this.residueString = "";
        this.firstResidueDsspNumber = -1;
        this.lastResidueDsspNumber = -1;
        this.firstResiduePdbString = "";
        this.lastResiduePdbString = "";
    }

    public int getSseType() {
        return sseType;
    }

    public void setSseType(int sseType) {
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
      return "[t=" + this.sseType + ",l= " + this.getSequenceLength() + "]";
    }
}
