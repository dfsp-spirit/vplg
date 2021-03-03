/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012 - 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphdrawing;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import tools.DP;

/**
 *
 * @author spirit
 */
public class DrawableEdge implements IDrawableEdge {
    
    private final String spatRel;
    private final List<Integer> vertPairIndicesNtoC;
    
    public DrawableEdge(String spatRel, List<Integer> vertPairIndicesNtoC) {
        this.spatRel = spatRel;
        this.vertPairIndicesNtoC = vertPairIndicesNtoC;
        if(this.vertPairIndicesNtoC.size() != 2) {
            DP.getInstance().w("DrawableEdge", "Length of vert pair indices N to C has invalid length " + this.vertPairIndicesNtoC.size() + ", should be 2.");
        }
    }
    
    @Override
    public String getSpatRel() {
        return this.spatRel;
    }
    
    @Override
    public String toString() {
        String s = "[" + this.spatRel + "{";
        for(Integer se : vertPairIndicesNtoC) {
            s += se + " ";
        }
        s += "}]";
        return s;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.spatRel);
        hash = 31 * hash + Objects.hashCode(this.vertPairIndicesNtoC);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DrawableEdge other = (DrawableEdge) obj;
        if (!Objects.equals(this.spatRel, other.spatRel)) {
            return false;
        }
        if (!Objects.equals(this.vertPairIndicesNtoC, other.vertPairIndicesNtoC)) {
            return false;
        }
        return true;
    }

    /**
     * @return the vertPairIndicesNtoC
     */
    @Override
    public List<Integer> getVertPairIndicesNtoC() {
        return vertPairIndicesNtoC;
    }
    
}
