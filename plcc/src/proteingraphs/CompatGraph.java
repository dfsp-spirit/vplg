/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package proteingraphs;

import tools.DP;
import algorithms.CompatGraphComputation;
import datastructures.Graph;
import java.io.IOException;
import java.util.ArrayList;
import org.xml.sax.SAXException;

/**
 * Implements a very simple undirected graph used as a temporary data structure for various calculations.
 * Add all vertices before adding any edges.
 * @author ts
 */
public class CompatGraph extends Graph<Integer[]>{
    
    private Integer[][] edges;    
        
    /** A list of all vertices in the compatibility graph. Each of these vertices originates from a pair (i, j) of
    compatible edges in the source graphs and is therefore represented by an integer array of length two which holds the 
    source vertex indices, e.g., [i, j]. */
    //private ArrayList<Integer[]> vertices;
    
    //public static final Integer EDGETYPE_NONE = 0;
    //public static final Integer EDGETYPE_EDGE = 1;
    
    private Boolean edgesInited;
    
    /**
     * Creates a graph with 0 vertices (and 0 edges). Use addVertex() to add vertices, then add edges.
     * @param numVertices 
     */
    public CompatGraph(ArrayList<Integer[]> vertices) {        
        super(vertices);
        this.edgesInited = false;
    }
    
    /**
     * Adds a vertex. Add all vertices before adding any edges.
     * @param sourceEdge1
     * @param sourceEdge2 
     */
    public void addVertex(Integer sourceEdge1, Integer sourceEdge2) {
        
        if(this.edgesInited) {
            DP.getInstance().w("Added vertex after edges have been inited, deleting all edges.");
            this.edgesInited = false;
        }
        
        vertices.add(new Integer[] {sourceEdge1, sourceEdge2});
        
    }
    
    /**
     * Inits the edge matrix, i.e., sets all possible edges to 'no edge'.
     */
    public void initEdges() {
        
        Integer numVertices = this.numVertices();
        
        this.edges = new Integer[numVertices][numVertices];
        
        for(Integer i = 0; i < numVertices; i++) {
            for(Integer j = 0; j < numVertices; j++) {
                this.edges[i][j] = CompatGraph.EDGETYPE_NONE;
            }            
        }
        
        this.edgesInited = true;
    }
    
        
    
    /**
     * Adds an edge from vertex v1 to v2. Add all vertices before adding any edges.
     * @param v1 vertex 1
     * @param v2 vertex 2
     */
    @Override public void addEdge(Integer v1, Integer v2) {
        
        if(! this.edgesInited) {
            this.initEdges();
        }
        
        if(v1 < 0 || v2 < 0 || v1 >= this.numVertices() || v2 >= this.numVertices()) {
            System.err.println("ERROR: BasicGraph: Cannot add edge (" + v1 + "," + v2 + "), no such vertices in graph.");
        } else {
            //this.edges[v1][v2] = CompatGraph.EDGETYPE_EDGE;
            //this.edges[v2][v1] = CompatGraph.EDGETYPE_EDGE;
            
           this.edges[v1][v2] = CompatGraph.EDGETYPE_EDGE;
           this.edges[v2][v1] = CompatGraph.EDGETYPE_EDGE;                  
            
        }                
    }
    
       
  
    
    
    
    /**
     * Checks whether the two edges edge1 and edge2 share a vertex. The edges are given by the vertex pair they connect.
     * Required in order two determine whether an edge is a Z edge.
     * 
     * @param edge1v1 first vertex of the first edge
     * @param edge1v2 second vertex of the first edge
     * @param edge2v1 first vertex of the second edge
     * @param edge2v2 second vertex of the second edge
     * @return true if they share a vertex, false otherwise
     */
    public Boolean edgesShareVertex(Integer edge1v1, Integer edge1v2, Integer edge2v1, Integer edge2v2) {
        Boolean sharing = false;

        if(edge1v1 == edge2v1 || edge1v1 == edge2v2 || edge1v2 == edge2v1 || edge1v2 == edge2v2) {
            sharing = true;
        }        
        return(sharing);
    }
    
    
    /**
     * Returns the index of the vertex that is shared by the 2 edges edge1 and edge2. (edge1 != edge2)
     * @param edge1v1
     * @param edge1v2
     * @param edge2v1
     * @param edge2v2
     * @return the index of the vertex or null if no such vertex exists (i.e., edge1 and edge2 dont share a vertex)
     */
    public Integer getSharedVertex(Integer edge1v1, Integer edge1v2, Integer edge2v1, Integer edge2v2) {
        if(! this.edgesShareVertex(edge1v1, edge1v2, edge2v1, edge2v2)) {
            return(null);
        }
        
        if(CompatGraphComputation.sameEdges(edge1v1, edge1v2, edge2v1, edge2v2)) {
            return(null);
        }
        
        
        if(edge1v1 == edge2v1) {
            return(edge1v1);
        }
        else if(edge1v1 == edge2v2) {
            return(edge1v1);
        }
        else if(edge2v1 == edge1v2) {
            return(edge2v1);
        } 
        else if(edge2v2 == edge1v2) {
            return(edge2v2);
        }
        
        return(null);
    }
    
    
    
    
    
    
    /**
     * 
     * Creates a fake SSEGraph form this BasicGraph.
     * 
     * @return the SSEGraph
     */
    public ProtGraph toFakeSSEGraph() {        
        String tgf = this.toTrivialGraphFormat();
        
        
        if(tgf == null) {
            System.err.println("DEBUG: CompatGraph.toFakeSSEGraph(): The tgf string is NULL.");
        }
        
        ProtGraph pg = ProtGraphs.fromTrivialGraphFormatString(tgf);
        
        if(pg == null) {
            DP.getInstance().w("CompatGraph.toFakeSSEGraph(): The pg is NULL.");
        } else {
            //System.out.println("DEBUG: CompatGraph.toFakeSSEGraph(): The pg is ok.");
        }
        
        return(pg);
    }
    
    
    @Override public String toString() {
        String s = "CG_VERTICES(" + this.numVertices() +  "):";
        for(Integer i = 0; i < this.numVertices(); i++) {
            s += " [" + i + ": " + this.vertices.get(i)[0] + ", " + this.vertices.get(i)[1] + "]";
        }
        s += "\nCG_EDGES(" + this.getNumEdges() + "):";
        
        for(Integer i = 0; i < this.numVertices(); i++) {
            for(Integer j = i+1; j < this.numVertices(); j++) {
                if(this.hasEdge(i, j)) {
                    s += " (" + i + "," + j + ")";
                }
                
            }               
        }
        
        return(s);
    }
    
    
    /**
     * Determines the number of edges in this graph.
     */ 
    public Integer getNumEdges() {
        Integer num = 0;
        
        for(Integer i = 0; i < this.numVertices(); i++) {
            for(Integer j = i+1; j < this.numVertices(); j++) {
                if(this.hasEdge(i, j)) {
                    num++;
                }
                
            }               
        }
        
        return(num);
    }            
            
}
