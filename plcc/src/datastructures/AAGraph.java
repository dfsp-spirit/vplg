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
import plcc.GraphModellingLanguageFormat;
import plcc.ResContactInfo;
import plcc.Residue;

/**
 * An undirected, adjacency list based amino acid graph. Suitable for large, sparse graphs. 
 * Holds the AAs for a single chain or multiple chains.
 * 
 * @author ts
 */
public class AAGraph extends SparseGraph<Residue, AAEdgeInfo> implements GraphModellingLanguageFormat {
    
    
    public static final String CHAINID_ALL_CHAINS = "ALL_CHAINS";
    
    /** PDB identifier. */
    private String pdbid;
    
    
    /** PDB chain identifier. */
    private String chainid;

    
    /** Constructor */
    public AAGraph(ArrayList<Residue> vertices) {
        super(vertices);
    }
    
    /** Advanced Constructor, constructs the edges automatically from ResContactInfo list */
    public AAGraph(ArrayList<Residue> vertices, ArrayList<ResContactInfo> contacts) {
        super(vertices);
        for(int i = 0; i < contacts.size(); i++) {
            this.addEdgeFromRCI(contacts.get(i));
        }
    }
    
    /** Constructor */
    public AAGraph(ArrayList<Residue> vertices, String pdbid, String chainid) {
        super(vertices);
        this.setPdbid(pdbid);
        this.setChainid(chainid);
    }
    
    
    /**
     * Automatically adds an edge from a ResContactInfo object if applicable. Note that the edge is only added if the RCI describes a contact.
     * @param rci the ResContactInfo object, must be for 2 residues which are part of this graph
     * @return true if the edge was added, false otherwise
     */
    public final boolean addEdgeFromRCI(ResContactInfo rci) {
        if(rci.describesContact()) {            
            Residue resA = rci.getResA();
            Residue resB = rci.getResB();

            int indexResA = this.getVertexIndex(resA);
            int indexResB = this.getVertexIndex(resB);
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
    
   
    /** Returns a Graph Modelling Language format representation of this graph.
     *  See http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-technical-report.pdf for the publication 
     * and http://en.wikipedia.org/wiki/Graph_Modelling_Language for a brief description.
     * 
     */
    @Override
    public String toGraphModellingLanguageFormat() {
        
        StringBuilder gmlf = new StringBuilder();
        
        
        String label_pdbid = (this.pdbid == null ? "" : " PDB " + this.pdbid);
        String label_chainid = ((this.chainid == null ||  this.chainid.equals(AAGraph.CHAINID_ALL_CHAINS)) ? "" : " chain " + this.chainid);
        // print the header

        String startNode = "  node [";
        String endNode   = "  ]";
        String startEdge = "  edge [";
        String endEdge   = "  ]";
        
        gmlf.append("graph [\n");
        gmlf.append("  id ").append(1).append("\n");
        gmlf.append("  label \"" + "VPLG Protein Graph ").append(label_pdbid).append(label_chainid).append("\"\n");
        gmlf.append("  comment \"" + "VPLG Protein Graph ").append(label_pdbid).append("\"\n");
        gmlf.append("  directed 0\n");
        gmlf.append("  isplanar 0\n");
        gmlf.append("  creator \"PLCC\"\n");
        
        
        // print all nodes
        Residue residue;
        for(Integer i = 0; i < this.getNumVertices(); i++) {
            residue = this.vertices.get(i);
            gmlf.append(startNode).append("\n");
            gmlf.append("    id ").append(i).append("\n");
            gmlf.append("    label \"").append(i).append("-").append(residue.getUniquePDBName()).append("\"\n");
            gmlf.append("    residue \"").append(residue.getName3()).append("\"\n");
            gmlf.append(endNode).append("\n");
        }
        
        // print all edges
        Integer src, tgt;
        ArrayList<Integer[]> allEdges = this.getEdgeListIndex();
        for(Integer[] edge : allEdges) {
            src = edge[0];
            tgt = edge[1];
            
            gmlf.append(startEdge).append("\n");
            gmlf.append("    source ").append(src).append("\n");
            gmlf.append("    target ").append(tgt).append("\n");                        
            gmlf.append("    weight ").append(this.getEdgeDistance(src, tgt)).append("\n");                        
            gmlf.append(endEdge).append("\n");
        }
        
        // print footer (close graph)
        gmlf.append("]\n");
        
        return(gmlf.toString());
    }
    
    
    /**
     * Returns the 3D euclidian distance between the Residues at indices i and j in this graph.
     * @param i the residue i by index
     * @param j the residue j by index
     * @return the euclidian distance
     */
    public int getEdgeDistance(int i, int j) {
        return this.getVertex(i).resCenterDistTo(this.getVertex(j));
    }
    
    /**
     * Determines whether this graph contains AAs from multiple chains or a single chain only. This 
     * simply checks the chainid string, it does not look at each residue or something.
     * @return true if this is a multi-chain graph, false otherwise
     */
    public boolean isMultiChainGraph() {
        return this.chainid.equals(AAGraph.CHAINID_ALL_CHAINS);
    }
    
    // simple getters and setters follow, auto-generated
    public String getPdbid() {
        return pdbid;
    }

    public final void setPdbid(String pdbid) {
        this.pdbid = pdbid;
    }

    public String getChainid() {
        return chainid;
    }

    public final void setChainid(String chainid) {
        this.chainid = chainid;
    }
}
