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
import proteingraphs.SSEGraph;


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
    
    /**
     * The graph class, e.g., protein graph, folding graph, or similar. Use the constants in SSEGraph.GRAPHCLASS_*
     */
    private String graphClass;
    
    /**
     * The fold number, for folding graphs only.
     */
    private Integer foldNumber;
    
    private List<V> vertices;
    private HashMap<V, Set<V>> edges;
    private HashMap<String, String> edgeLabels;
    
    public PLGraph() {
        super();
        this.pdbid = "";
        this.chain = "";
        this.graphType = PLGRAPH_TYPE_STRING_NONE;
        this.vertices = new ArrayList<>();
        this.edges = new HashMap<>();
        this.edgeLabels = new HashMap<>();
        this.graphClass = SSEGraph.GRAPHCLASS_PROTEINGRAPH;
        this.foldNumber = null;
    }    
    
    public PLGraph(List<V> vertices) {
        //super(vertices);
        this.pdbid = "";
        this.chain = "";
        this.graphType = PLGRAPH_TYPE_STRING_NONE;
        this.vertices = vertices;
        this.edges = new HashMap<>();
        this.edgeLabels = new HashMap<>();
        this.graphClass = SSEGraph.GRAPHCLASS_PROTEINGRAPH;
        this.foldNumber = null;
    }
    
    public void setFoldingGraph(Integer foldNumber) {
        this.graphClass = SSEGraph.GRAPHCLASS_FOLDINGGRAPH;
        this.foldNumber = foldNumber;
    }
    
    public void setProteingraph() {
        this.graphClass = SSEGraph.GRAPHCLASS_PROTEINGRAPH;
        this.foldNumber = null;
    }
    
    public boolean addVertex(V v) {
        if( ! vertices.contains(v)) {
            this.vertices.add(v);
            this.edges.put(v, new HashSet<V>());
            return true;
        }
        return false;
    }
    
    private String getEdgeName(V v1, V v2) {
        return v1.toString() + "-" + v2.toString();
    }
    
    public boolean addEdge(V v1, V v2, String label) {
        if(vertices.contains(v1) && vertices.contains(v2)) {
            edges.get(v1).add(v2);
            edges.get(v2).add(v1);
            edgeLabels.put(getEdgeName(v1, v2), label);
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
    
    public void setGraphClass(String graphClass) {
        this.graphClass = graphClass;
    }
    
    public String getGraphClass() {
        return this.graphClass;
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
    
    public static final String VERTEX_TYPE_STRING_NONE = "-";
    public static final String VERTEX_TYPE_STRING_SSE_HELIX = "helix";
    public static final String VERTEX_TYPE_STRING_SSE_BETASTRAND = "strand";
    public static final String VERTEX_TYPE_STRING_SSE_LIGAND = "ligand";
    public static final String VERTEX_TYPE_STRING_SSE_OTHER = "other";
    
    // edge types (relative orientations)
    public static final int EDGE_TYPE_NONE = 0;
    public static final int EDGE_TYPE_MIXED = 1;
    public static final int EDGE_TYPE_PARALLEL = 2;
    public static final int EDGE_TYPE_ANTIPARALLEL= 3;
    public static final int EDGE_TYPE_LIGAND = 4;   
    
    
    private String getSSEType(V v) {
        if(v.toString().startsWith("h")) {
            return PLGraph.VERTEX_TYPE_STRING_SSE_HELIX;
        }
        else if(v.toString().startsWith("e")) {
            return PLGraph.VERTEX_TYPE_STRING_SSE_BETASTRAND;
        }
        else if(v.toString().startsWith("l")) {
            return PLGraph.VERTEX_TYPE_STRING_SSE_LIGAND;
        }
        else if(v.toString().startsWith("o")) {
            return PLGraph.VERTEX_TYPE_STRING_SSE_OTHER;
        }
        else {
            return PLGraph.VERTEX_TYPE_STRING_NONE;
        }
    }
    
    /**
     * Returns the graph in XGMML, an XML graph format based on GML.
     * See http://cgi7.cs.rpi.edu/research/groups/pb/punin/public_html/XGMML/ for general info on XGMML, and 
     * see http://cgi7.cs.rpi.edu/research/groups/pb/punin/public_html/XGMML/XGMML_EXP/example1.gr for an example graph.
     * @return 
     */
    public String toXMLFormat() {
        
        String label = "PLCC graph";
        
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<graph label=\"").append(label).append("\" \n" +
//"    xmlns:dc=\"http://purl.org/dc/elements/1.1/\" \n" +
//"    xmlns:xlink=\"http://www.w3.org/1999/xlink\" \n" +
//"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n" +
"    xmlns=\"http://www.cs.rpi.edu/XGMML\"  \n" +
"    directed=\"0\">  \n");
        xml.append("    <att name=\"graphClass\" type=\"string\" value=\"").append(this.graphClass).append("\"/>\n");
        xml.append("    <att name=\"pdbid\" type=\"string\" value=\"").append(this.pdbid).append("\"/>\n");
        xml.append("    <att name=\"chain\" type=\"string\" value=\"").append(this.chain).append("\"/>\n");
        xml.append("    <att name=\"graphType\" type=\"string\" value=\"").append(this.graphType).append("\"/>\n");
        
        if(this.graphClass.equals(SSEGraph.GRAPHCLASS_FOLDINGGRAPH)) {
            xml.append("    <att name=\"foldNumber\" type=\"string\" value=\"").append(this.foldNumber).append("\"/>\n");
        }

        for(V v : vertices) {
            xml.append("    <node label=\"").append(v.toString()).append("\" id=\"").append(v.toString()).append("\">\n");
            xml.append("        <att name=\"sseType\" type=\"string\" value=\"").append(getSSEType(v)).append("\"/>\n");
            xml.append("    </node>\n");
        }

        
        for(V v : vertices) {
            for(V n : getNeighborsOfVertex(v)) {
                String orientation = edgeLabels.get(getEdgeName(v, n));
                if(orientation == null) {
                    continue; // skip edge (e, v), we will or have already add the edge (v, e)
                }
                xml.append("    <edge source=\"").append(v.toString()).append("\" target=\"").append(n.toString()).append("\" label=\"" + "(").append(v.toString()).append("-").append(orientation).append("-").append(n.toString()).append(")" + "\">\n");                
                xml.append("        <att name=\"edgeType\" type=\"string\" value=\"").append(orientation).append("\"/>\n");
                xml.append("    </edge>\n");
            }
        }
        
        xml.append("</graph>\n");
        return xml.toString();
    }
}
