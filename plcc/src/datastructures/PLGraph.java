/*
 * This file is part of SPARGEL, the sparse graph extension library.
 * 
 * This is free software, published under the WTFPL.
 * 
 * Written by Tim Schaefer, see http://rcmd.org/contact/.
 * 
 */
package datastructures;


import java.util.ArrayList;


/**
 * A protein ligand graph, which uses SSEs as vertices and has both edge and vertex types. This is a sparse
 * implementation built on adjacency lists. It is based on the SPARGEL library. You need to add SPARGEL to the PLCC dependencies.
 * @author ts
 */
public class PLGraph<VertexSSE>  {
    
    /**
     * The RCSB identifier of this protein. A 4-letter string, e.g., '7TIM'.
     */
    private String pdbid;
    
    /**
     * The PDB chain id. A 1-character string, e.g., "A".
     */
    private String chain;
    
    /**
     * The PTGL graph type as an int, see the PLGRaph.PLGRAPH_TYPE* constants.
     */ 
    private int graphType;
    
    public PLGraph() {
        super();
        this.pdbid = "";
        this.chain = "";
        this.graphType = PLGRAPH_TYPE_NONE;
    }    
    
    public PLGraph(ArrayList<VertexSSE> vertices) {
        //super(vertices);
        this.pdbid = "";
        this.chain = "";
        this.graphType = PLGRAPH_TYPE_NONE;
    }

    public String getPdbid() {
        return pdbid;
    }

    public void setPdbid(String pdbid) {
        this.pdbid = pdbid;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public int getGraphType() {
        return graphType;
    }

    public void setGraphType(int graphType) {
        this.graphType = graphType;
    }
    
    // protein ligand graph types
    public static final int PLGRAPH_TYPE_NONE = 0;
    public static final int PLGRAPH_TYPE_ALPHA = 1;
    public static final int PLGRAPH_TYPE_BETA = 2;
    public static final int PLGRAPH_TYPE_ALBE = 3;
    public static final int PLGRAPH_TYPE_ALPHALIG = 4;
    public static final int PLGRAPH_TYPE_BETALIG = 5;
    public static final int PLGRAPH_TYPE_ALBELIG = 6;
    
    // vertex types (SSEs)
    public static final int VERTEX_TYPE_NONE = 0;
    public static final int VERTEX_TYPE_SSE_HELIX = 1;
    public static final int VERTEX_TYPE_SSE_BETASTRAND = 2;
    public static final int VERTEX_TYPE_SSE_LIGAND = 3;
    public static final int VERTEX_TYPE_SSE_OTHER = 4;
    
    // edge types (relative orientations)
    public static final int EDGE_TYPE_NONE = 0;
    public static final int EDGE_TYPE_MIXED = 1;
    public static final int EDGE_TYPE_PARALLEL = 2;
    public static final int EDGE_TYPE_ANTIPARALLEL= 3;
    public static final int EDGE_TYPE_LIGAND = 4;   
}
