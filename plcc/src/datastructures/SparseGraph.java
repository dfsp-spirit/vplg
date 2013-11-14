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
import java.util.Arrays;
import java.util.HashMap;
import plcc.GraphMLFormat;
import plcc.IO;
import plcc.TrivialGraphFormat;

/**
 * An undirected sparse graph. Uses index based access to vertices and
 * adjacency list representation of the edges.
 * 
 * V is the vertex type and E is the edge info type.
 * 
 * @author ts
 */
public class SparseGraph<V, E> {
    
    
    /** Some vertices. Graphs like them. */
    protected ArrayList<V> vertices;
    
    /** The edges, encoded as adjacency lists. */
    protected ArrayList<ArrayList<Integer>> edges;
    protected HashMap<String, E> edgeInfo;
    
    public static final Integer EDGETYPE_NONE = 0;
    public static final Integer EDGETYPE_EDGE = 1;
    
    
    /**
     * Constructs a graph from a vertex list. The list may be empty, of course.
     * @param vertList the vertex list
     */
    public SparseGraph(ArrayList<V> vertList) {
        this.vertices = vertList;        
        edges = new ArrayList<ArrayList<Integer>>();
        edgeInfo = new HashMap<String, E>();
        // add one ArrayList for each vertex
        for (Integer i = 0; i < vertices.size(); i++) {
            edges.add(new ArrayList<Integer>());
        }
    }
    
    /**
     * Constructs an empty graph.
     */
    public SparseGraph() {
        this.vertices = new ArrayList<V>();
        edges = new ArrayList<ArrayList<Integer>>();
        edgeInfo = new HashMap<String, E>();
        // add one ArrayList for each vertex
        for (Integer i = 0; i < vertices.size(); i++) {
            edges.add(new ArrayList<Integer>());
        }
    }
    
    /**
     * Returns the edge name string for the edge info HashMap. Used internally only.
     * @param i the vertex i by index
     * @param j the vertex j by index
     * @return the edge name
     */
    private String getEdgeName(int i, int j) {
        return "" + i + "#" + j + "";
    }
    
    
    /**
     * Adds an edge between the vertices at indices i and j.
     * @param i the vertex i by index
     * @param j the vertex j by index 
     * @param e the edge info
     */ 
    public void addEdge(int i, int j, E e) {
        if(! this.edges.get(i).contains(j)) {
          this.edges.get(i).add(j);
          this.setEdgeInfo(i, j, e);
        }
        if(! this.edges.get(j).contains(i)) {
          this.edges.get(j).add(i);
          this.setEdgeInfo(j, i, e);
        }
    }
    
    
    /**
     * Returns the index of the vertex object v in this graph, or a value smaller
     * than 0 if this graph contains no such vertex.
     * @param v the Vertex
     * @return the index if the vertex is found, a value smaller than zero otherwise
     */
    public int getVertexIndex(V v) {
        int idx = -1;
        for(int i = 0; i < this.vertices.size(); i++) {
            if(this.vertices.get(i).equals(v)) {
                idx = i;
                return idx;
            }
        }
        return idx;
    }
    
    
    /**
     * Checks whether an edge exists between the vertices at index i and j.
     * @param i the vertex i by index
     * @param j the vertex j by index
     * @return true if i and j are adjacent, false otherwise
     */ 
    public boolean hasEdge(int i, int j) {
        return this.edges.get(i).contains(j);
    }
    
    /**
     * Retuns the EdgeInfo for the edge between vertices at indices i and j
     * @param i the vertex i by index
     * @param j the vertex j by index
     * @return the EdgeInfo for the edge between vertices at indices i and j
     */
    public E getEdgeInfo(int i, int j) {
        return this.edgeInfo.get(this.getEdgeName(i, j));
    }
    
    
    /**
     * Sets the EdgeInfo for the edge between vertices at indices i and j. Note that the edge has to exist already.
     * @param i the vertex i by index
     * @param j the vertex j by index
     * @param e the EdgeInfo
     * @return true if such and edge exists and the data was set, false otherwise
     */
    public boolean setEdgeInfo(int i, int j, E e) {
        if(this.hasEdge(i, j)) {
            edgeInfo.put(this.getEdgeName(i, j), e);
            return true;
        }
        return false;
    }
    
    /**
     * Returns the total number of vertices in this graph.
     * @return the total vertex count
     */
    public int getNumVertices() {
        return this.vertices.size();
    }
    
    
    /**
     * Returns the total number of edges in this graph.
     * @return the total edge count
     */
    public int getNumEdges() {
        int numTotal = 0;
        for(int i = 0; i < this.edges.size(); i++) {
            numTotal += this.edges.get(i).size();
        }
        return numTotal;    
    }
    
    
    /**
     * Returns the degree of vertex at index i.
     * @param vIndex the vertex index
     * @return the vertex degree, i.e., the number of vertices adjacent to i
     */
    public int getVertexDegree(int vIndex) {
        return this.edges.get(vIndex).size();
    }
    
    
    /**
     * Returns a list of edges in this graph. Each integer array (of length 2) in the returned list holds
     * the indices of a pair of adjacent vertices.
     * @return a list of vertex pairs given by their indices which are neighbors
     */
    public ArrayList<Integer[]> getEdgeListIndex() {
        ArrayList<Integer[]> allEdges = new ArrayList<Integer[]>();
        for(int i = 0; i < this.edges.size(); i++) {
            for(int j = 0; j < this.edges.get(i).size(); j++) {
               int neighborOfI = this.edges.get(i).get(j);
               allEdges.add(new Integer[]{i, neighborOfI});
            }
        }
        return allEdges;
    }
    
    
}
