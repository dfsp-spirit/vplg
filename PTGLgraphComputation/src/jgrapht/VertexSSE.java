/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package jgrapht;

import java.util.HashMap;

/**
 * A vertex for a protein ligand graph. Represents a secondary structure element (SSE).
 * @author ts
 */
public class VertexSSE implements Comparable<VertexSSE> {
       
    
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

    @Override
    public int compareTo(VertexSSE other) {
        return this.sequentialSSENumberInChain.compareTo(other.sequentialSSENumberInChain);
    }
    
    private HashMap<String, String> getExportProperties() {
        HashMap<String, String> props = new HashMap<String, String>();
        props.put("id", "" + this.sequentialSSENumberInChain);
        props.put("label", "\"" + this.sequentialSSENumberInChain + this.sseType + "\"");
        props.put("residues", "\"" + this.residueString + "\"");
        props.put("first_res_dssp", "" + this.firstResidueDsspNumber);
        props.put("last_res_dssp", "" + this.lastResidueDsspNumber);
        props.put("first_res_pdb", "\"" + this.firstResiduePdbString + "\"");
        props.put("last_res_pdb", "\"" + this.lastResiduePdbString + "\"");
        return props;
    }
    
    public String toFormatGML() {
        String startNode = "  node [\n";
        String endNode   = "  ]\n";
        
        StringBuilder sb = new StringBuilder(startNode);
        HashMap<String, String> props = this.getExportProperties();
        for(String prop : props.keySet()) {
            sb.append("   ").append(prop).append(" ").append(props.get(prop)).append("\n");
        }
        sb.append(endNode);
        return sb.toString();
    }
}
