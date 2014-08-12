/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author ts
 */
public interface SimpleGraphInterface {
    
    public Integer getSize();
    
    public Boolean containsEdge(Integer i, Integer j);
    
    public ArrayList<Integer> neighborsOf(Integer i);
    
}
