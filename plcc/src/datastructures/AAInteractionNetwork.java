/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package datastructures;

import java.util.List;

/**
 * Simple wrapper to turn the AA matrix into a SimpleGraphInterface, allowing us to compute graph properties on it. Use AminoAcid.names3 to get the list of strings representing the vertex types.
 * Atm this makes little sense because the matrix is usually complete for proteins, i.e., each AA type interacts with each other type. The weights are of more interest.
 * @author spirit
 */
public class AAInteractionNetwork extends SparseGraph<String, Integer>{
            
    public AAInteractionNetwork(List<String> vertices, int[][] edges) {
        super(vertices);
        for(int i = 0; i < edges.length; i++) {
            for(int j = 0; j < edges[0].length; j++) {
                this.addEdge(i, j, edges[i][j]);
            }
        }
        
    }
    
}
