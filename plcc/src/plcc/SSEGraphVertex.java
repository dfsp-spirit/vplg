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
    
    public static final int STATE_NOT_VISITED = 0;
    public static final int STATE_VISITED = 1;
    
    /** A visited state, required for various graph algorithms like BFS. */
    public int visitedState;
    public int indexInParentGraph;
    
    public SSEGraphVertex() {
        visitedState = 0;
        indexInParentGraph = -1;
    }
    
}
