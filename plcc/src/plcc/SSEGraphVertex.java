/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

/**
 * A basic vertex for an SSEGraph.
 * @author ts
 */
public class SSEGraphVertex {
    
    /** A visited state, required for various graph algorithms like BFS. */
    public int visitedState;
    
    public SSEGraphVertex() {
        visitedState = 0;
    }
    
}
