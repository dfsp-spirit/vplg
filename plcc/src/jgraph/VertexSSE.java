/*
 * This file is part of SPARGEL, the sparse graph extension library.
 * 
 * This is free software, published under the WTFPL.
 * 
 * Written by Tim Schaefer, see http://rcmd.org/contact/.
 * 
 */
package jgraph;

/**
 * A vertex for a protein ligand graph. Represents a secondary structure element (SSE).
 * @author ts
 */
public class VertexSSE {
    
    protected String residueString;

    public String getResidueString() {
        return residueString;
    }

    public void setResidueString(String residueString) {
        this.residueString = residueString;
    }
    
}
