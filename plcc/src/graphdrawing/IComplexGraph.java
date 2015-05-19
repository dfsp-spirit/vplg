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
     * @return a list of the indices of the SSEs (N to C in seq.), where the chains end.
     */
    public String getChainNameOfSSE(Integer sseIndex);
    
}
