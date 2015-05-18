/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012 - 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphdrawing;

import java.util.List;
import java.util.Map;
import plcc.SpatRel;

/**
 *
 * @author spirit
 */
public class DrawableGraph implements IDrawableGraph {
    
    private final Map<String, String> props;
    private final List<IDrawableEdge> drawableEdges;
    private final List<IDrawableVertex> drawableVertices;
    
    public DrawableGraph(List<IDrawableVertex> drawableVertices, List<IDrawableEdge> drawableEdges, Map<String, String> props) {
        this.drawableVertices = drawableVertices;
        this.drawableEdges = drawableEdges;
        this.props = props;
    }
    
    @Override
    public String getPropertyString(String name) {
        return props.get(name);
    }
    
    @Override
    public List<IDrawableEdge> getDrawableEdges() {
        return this.drawableEdges;
    }
    
    @Override
    public List<IDrawableVertex> getDrawableVertices() {
        return this.drawableVertices;
    }
    
    private Boolean isEdgeFromTo(IDrawableEdge e, Integer i, Integer j) {
        if(e == null) {
            return false;
        }
        List<Integer> l = e.getVertPairIndicesNtoC();
        if(l.get(0).equals(i) && l.get(1).equals(j)) {
            return true;
        }
        if(l.get(0).equals(j) && l.get(1).equals(i)) {
            return true;
        }
        return false;
    }
    
    
    public String getSpatRelOfEdge(Integer i, Integer j) {
        for(IDrawableEdge e : this.drawableEdges) {
            if(isEdgeFromTo(e, i, j)) {
                return e.getSpatRel();
            }
        }
        return SpatRel.STRING_NONE;
    }
    
}
