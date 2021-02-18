/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphdrawing;

import java.util.List;
import java.util.Set;

/**
 * An edge that can be drawn using the protein graph drawing method of the PTGL.
 * @author spirit
 */
public interface IDrawableEdge {
    
    /**
     * Returns the spatial relation string
     * @return the spatial relation string of the edge
     */
    public String getSpatRel();
    
    /**
     * Returns the vertex indices of the edge
     * @return a list of length 2, containing the vertices (by index) that this edge is incident on
     */
    public List<Integer> getVertPairIndicesNtoC();
    
    @Override
    public String toString();
}
