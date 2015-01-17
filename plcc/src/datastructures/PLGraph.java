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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A protein ligand graph, which uses SSEs as vertices and has both edge and vertex types. This is a sparse
 * implementation built on adjacency lists. It is based on the SPARGEL library. You need to add SPARGEL to the PLCC dependencies.
 * @author ts
 * @param <V> the vertex type
 */
public class PLGraph<V>  {
    
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
    private String graphType;
    
    private List<V> vertices;
    private HashMap<V, Set<V>> edges;
    
    public PLGraph() {
        super();
        this.pdbid = "";
        this.chain = "";
        this.graphType = PLGRAPH_TYPE_STRING_NONE;
        this.vertices = new ArrayList<>();
        this.edges = new HashMap<>();
    }    
    
    public PLGraph(List<V> vertices) {
        //super(vertices);
        this.pdbid = "";
        this.chain = "";
        this.graphType = PLGRAPH_TYPE_STRING_NONE;
        this.vertices = vertices;
        this.edges = new HashMap<>();
    }
    
    public boolean addVertex(V v) {
        if( ! vertices.contains(v)) {
            this.vertices.add(v);
            this.edges.put(v, new HashSet<V>());
            return true;
        }
        return false;
    }
    
    public boolean addEdge(V v1, V v2) {
        if(vertices.contains(v1) && vertices.contains(v2)) {
            edges.get(v1).add(v2);
            edges.get(v2).add(v1);
            return true;
        }
        return false;
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

    public String getGraphType() {
        return graphType;
    }

    public void setGraphType(String graphType) {
        this.graphType = graphType;
    }
    
    public Set<V> getNeighborsOfVertex(V v) {
        return edges.get(v);
    }
    
    // protein ligand graph types
    public static final int PLGRAPH_TYPE_NONE = 0;
    public static final int PLGRAPH_TYPE_ALPHA = 1;
    public static final int PLGRAPH_TYPE_BETA = 2;
    public static final int PLGRAPH_TYPE_ALBE = 3;
    public static final int PLGRAPH_TYPE_ALPHALIG = 4;
    public static final int PLGRAPH_TYPE_BETALIG = 5;
    public static final int PLGRAPH_TYPE_ALBELIG = 6;
    
    // protein ligand graph types
    public static final String PLGRAPH_TYPE_STRING_NONE = "none";
    public static final String PLGRAPH_TYPE_STRING_ALPHA = "alpha";
    public static final String PLGRAPH_TYPE_STRING_BETA = "beta";
    public static final String PLGRAPH_TYPE_STRING_ALBE = "albe";
    public static final String PLGRAPH_TYPE_STRING_ALPHALIG = "alphalig";
    public static final String PLGRAPH_TYPE_STRING_BETALIG = "betalig";
    public static final String PLGRAPH_TYPE_STRING_ALBELIG = "albelig";
    
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
    
    public String toXMLFormat() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<proteingraph>\n");
        xml.append("<pdbid>"  + pdbid + "</pdbid>\n");
        xml.append("<chain>"  + chain + "</chain>\n");
        xml.append("<graphType>"  + graphType + "</graphType>\n");
        
        xml.append("<vertices>\n");
        for(V v : vertices) {
            xml.append("<vertex>" + v.toString() + "</vertex>\n");
        }
        xml.append("</vertices>\n");
        
        xml.append("<edges>\n");
        for(V v : vertices) {
            for(V n : getNeighborsOfVertex(v)) {
                xml.append("<edge>" + v.toString() + "</edge>\n");
            }
        }
        xml.append("</edges>\n");
        
        xml.append("</proteingraph>\n");
        return xml.toString();
    }
}
