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
import plcc.Residue;

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
    
    
    /**
     * Automatically adds an edge from a ResContactInfo object if applicable. Note that the edge is only added if the RCI describes a contact.
     * @param rci the ResContactInfo object, must be for 2 residues which are part of this graph
     * @return true if the edge was added, false otherwise
     */
    public boolean addEdgeFromRCI(ResContactInfo rci) {
        if(rci.describesContact()) {            
            Residue resA = rci.getResA();
            Residue resB = rci.getResB();
            //TODO: fix this
            //int indexResA = this.getVertexIndex(resA);
            //int indexResB = this.getVertexIndex(resB);
            int indexResA = -1;
            int indexResB = -1;
            if(indexResA >= 0 && indexResB >= 0) {
                AAEdgeInfo ei = new AAEdgeInfo(rci);
                this.addEdge(indexResA, indexResB, ei);
                return true;
            }
            else {
                System.err.println("WARNING: Could not add edge from ResContactInfo, vertices not found in graph.");
            }
        }
        return false;
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