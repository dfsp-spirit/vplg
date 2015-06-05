/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package linnottograph;

import graphdrawing.DrawableEdge;
import graphdrawing.DrawableGraph;
import graphdrawing.DrawableVertex;
import graphdrawing.IDrawableEdge;
import graphdrawing.IDrawableGraph;
import graphdrawing.IDrawableGraphProvider;
import graphdrawing.IDrawableVertex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import plcc.SpatRel;

/**
 *
 * @author spirit
 */
public class LinnotToGraph implements ILinnotToGraph, IDrawableGraphProvider {
    
    
    private final ILinnotParser lnp;
    
    public LinnotToGraph(String linnot, String graphType) {
        lnp = new LinnotParser(linnot, graphType);
    }
    
    @Override
    public List<IDrawableVertex> getVertices() {
        List<IDrawableVertex> outVerts = new ArrayList<>();
        List<String> parsedVertTypes = lnp.getVertexTypesNtoC();
        
        for(String p : parsedVertTypes) {
            outVerts.add(new DrawableVertex(p));
        }
        
        return outVerts;
    }
    
    @Override
    public List<IDrawableEdge> getEdges() {
        List<IDrawableEdge> outEdges = new ArrayList<>();
        List<Integer[]> parsedEdges = lnp.getOutGraphEdges();
        
        List<Integer> vertIndices;
        String spatRel;
        for(Integer[] e : parsedEdges) {
            vertIndices = new ArrayList<>();
            vertIndices.add(e[0]);
            vertIndices.add(e[1]);
            spatRel = SpatRel.getString(e[2]);
            outEdges.add(new DrawableEdge(spatRel, vertIndices));
        }
        
        return outEdges;
    }
    
    @Override
    public IDrawableGraph getDrawableGraph() {
        IDrawableGraph g = new DrawableGraph(this.getVertices(), this.getEdges(), new HashMap<String, String>());
        return g;
    }
    
}
