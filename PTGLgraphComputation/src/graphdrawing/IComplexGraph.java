/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphdrawing;

import java.util.List;

/**
 * An interface for complex graph special functions, designed to extend SSE graphs.
 * @author ts
 */
public interface IComplexGraph {
    /**
     * Determines protein chain ends.
     * @param sseIndex the SSE index in the graph
     * @return a list of the indices of the SSEs (N to C in seq.), where the chains end.
     */
    public String getChainNameOfSSE(Integer sseIndex);
    
    /**
     * Determines macromol ID of the (chain of the) SSE.
     * @param sseIndex the SSE index
     * @return the macromol ID of the parent chain of the SSE (MOL_ID field from PBD file, e.g., "1")
     */
    public String getMolIDOfSSE(Integer sseIndex);
}
