/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package datastructures;

import java.util.ArrayList;

/**
 * An undirected graph. This can be used as is or extended. It is based on the abstract Graph class.
 * @author spirit
 */
public class UndirectedGraph extends Graph<Integer> {
    
    /**
     * Creates a new undirected graph from a list of vertices. For a list of length n, an edge matrix
     * of size n*n will be created and initialized with empty edges.
     * @param vertList a list of vertices.
     */
    public UndirectedGraph(ArrayList<Integer> vertList) {
        super(vertList);
    }
        
}
