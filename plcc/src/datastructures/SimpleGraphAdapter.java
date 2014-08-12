/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package datastructures;

/**
 * A simple adapter to make using the SimpleGraphInterface easier. You can extend this to get some methods pre-implemented.
 * @author ts
 */
public abstract class SimpleGraphAdapter implements SimpleGraphInterface {
    
    @Override
    public Character getVertexLabelChar(Integer i) {
        char c = (char) ('0' + i);
        return c;
    }
    
    @Override
    public Character getEdgeLabelChar(Integer i, Integer j) {
        return '*';
    }
}
