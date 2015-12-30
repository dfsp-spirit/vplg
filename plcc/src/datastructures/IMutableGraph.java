/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package datastructures;


/**
 * Interface for mutable graphs, which support deleting and adding vertices and edges after the initial construction.
 * @author spirit
 * @param <V> the vertex type of the graph
 */
public interface IMutableGraph<V> {
    
    /**
     * Deletes an edge by index, also taking care of the edge info.
     * @param i first index
     * @param j 2nd index
     * @return true if the edge existed and was removed, false otherwise
     */
    public Boolean deleteEdge(int i, int j);
    
    /**
     * Deletes a vertex by index. Also removes all edges it is involved in, and fixed the shifted indices of edges which include a vertex with index larger than the deleted vertex.
     * @param idx the vertex, by index
     * @return whether the vertex was contained in the graph and deleted
     */
    public Boolean deleteVertex(int idx);
    
    /**
     * Adds a vertex to the graph, placing it add the end of the vertex list. Also cares for the (empty) adjacency list that needs to be added for it.
     * @param v the new vertex to be added
     */  
    public void addVertex(V v);
    
}
