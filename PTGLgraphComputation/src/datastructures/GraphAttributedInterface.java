/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package datastructures;

import java.util.List;

/**
 * An interface for adding graph, edge and vertex attributes to a graph.
 * @author ts
 */
public interface GraphAttributedInterface {
    
    public void setEdgeAttribute(int i, int j, String name, String value);
    
    public String getEdgeAttribute(int i, int j, String name);
    
    public void setVertexAttribute(int i, String name, String value);
    
    public String getVertexAttribute(int i, String name);
    
    public void setGraphAttribute(String name, String value);
    
    public String getGraphAttribute(String name);
    
    public void initVertexAttribute(List<Integer> verts, String name, String value);
    
    public void initEdgeAttribute(List<Integer[]> edges, String name, String value);
    
}
