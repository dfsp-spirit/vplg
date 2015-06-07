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
 * A graph that can be drawn using the protein graph drawing method of the PTGL. The graph
 * needs to be edge typed and vertex typed, and the types must match the PTGL labels.
 * @author spirit
 */
public interface IDrawableGraph {
    
    public String getPropertyString(String name);
    public List<IDrawableEdge> getDrawableEdges();
    public List<IDrawableVertex> getDrawableVertices();
    public Boolean containsEdge(Integer i, Integer j);
    public String getSpatRelOfEdge(Integer i, Integer j);
    
}
