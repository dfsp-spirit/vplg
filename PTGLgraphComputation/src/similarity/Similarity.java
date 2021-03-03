/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package similarity;

/**
 * Just some constants atm.
 * @author ts
 */
public class Similarity {
    
    /** Comparison using string alignment methods based on the SSE strings of the graphs. */
    public static final String SIMILARITYMETHOD_STRINGSSE = "string_sse";
    
    /** Comparison using set based graph comparison, e.g., compare the number of edges and vertices. */
    public static final String SIMILARITYMETHOD_GRAPHSET = "graph_set";
    
    /** Comparison using the size of the compatibility graph h of a graph pair (g1, g2). */
    public static final String SIMILARITYMETHOD_GRAPHCOMPAT = "graph_compat";
    
    /** Comparison using the relative graphlet frequency distance. */
    public static final String SIMILARITYMETHOD_GRAPHLET_RELGRAPHLETFREQ = "graphlet_relfreq";
    
}
