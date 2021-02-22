/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A very simple graph interface. Works by index.
 * @author ts
 */
public interface SimpleGraphInterface {
    
    public Integer getSize();
    
    public Boolean containsEdge(Integer i, Integer j);
    
    public List<Integer> neighborsOf(Integer i);
    
    public Character getVertexLabelChar(Integer i);
    
    public Character getEdgeLabelChar(Integer i, Integer j);
    
}
