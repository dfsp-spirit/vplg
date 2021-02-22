/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package datastructures;

/**
 * A simple adapter to make using the SimpleGraphInterface easier. You can extend this to 
 * get some methods pre-implemented.
 * @author ts
 */
public abstract class SimpleGraphAdapter implements SimpleGraphInterface {
    
    @Override
    public Character getVertexLabelChar(Integer i) {
        return SimpleGraphDrawer.defaultVertexLabel;
    }
    
    @Override
    public Character getEdgeLabelChar(Integer i, Integer j) {
        return SimpleGraphDrawer.defaultEdgeStart;
    }
}
