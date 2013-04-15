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
    
    public VertexSSE(int sseType) {
        this.sseType = sseType;
        this.residueString = "";
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
