/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012 - 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphdrawing;

import java.util.Set;

/**
 *
 * @author spirit
 */
public class DrawableEdge implements IDrawableEdge {
    
    private final String spatRel;
    private final Set<Integer> vertPairIndicesNtoC;
    
    public DrawableEdge(String spatRel, Set<Integer> vertPairIndicesNtoC) {
        this.spatRel = spatRel;
        this.vertPairIndicesNtoC = vertPairIndicesNtoC;
    }
    
    @Override
    public String getSpatRel() {
        return this.spatRel;
    }

    /**
     * @return the vertPairIndicesNtoC
     */
    public Set<Integer> getVertPairIndicesNtoC() {
        return vertPairIndicesNtoC;
    }
    
}
