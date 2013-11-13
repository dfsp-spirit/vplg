/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package datastructures;

import java.util.ArrayList;
import plcc.AminoAcid;
import plcc.ResContactInfo;

/**
 * An undirected, adjacency list based amino acid graph. Suitable for large, sparse graphs. 
 * Holds the AAs for a single chain.
 * 
 * @author ts
 */
public class AAGraph extends SparseGraph<AminoAcid, AAEdgeInfo> {
    
    
    public static final String CHAINID_ALL_CHAINS = "ALL_CHAINS";
    
    /** PDB identifier. */
    private String pdbid;
    
    
    /** PDB chain identifier. */
    private String chainid;

    
    /** Constructor */
    public AAGraph(ArrayList<AminoAcid> vertices) {
        super(vertices);
    }
    
    /** Advanced Constructor, constructs the edges automatically from ResContactInfo list */
    public AAGraph(ArrayList<AminoAcid> vertices, ArrayList<ResContactInfo> contacts) {
        super(vertices);
    }
    
    /** Constructor */
    public AAGraph(ArrayList<AminoAcid> vertices, String pdbid, String chainid) {
        super(vertices);
        this.setPdbid(pdbid);
        this.setChainid(chainid);
    }
    
    public boolean addEdgeFromRCI(ResContactInfo rci) {
        throw new java.lang.UnsupportedOperationException("addEdgeFromRCI: Not implemented yet");
    }
    
    /** Constructor */
    public AAGraph() {
        super();
    }
    
    // simple getters and setters follow, auto-generated
    public String getPdbid() {
        return pdbid;
    }

    public void setPdbid(String pdbid) {
        this.pdbid = pdbid;
    }

    public String getChainid() {
        return chainid;
    }

    public void setChainid(String chainid) {
        this.chainid = chainid;
    }
}
