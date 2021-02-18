/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
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
import proteingraphs.FoldingGraph;
import proteingraphs.SpatRel;
import tools.DP;

/**
 *
 * @author spirit
 */
public class LinnotToGraph implements ILinnotToGraph, IDrawableGraphProvider {
    
    
    private final ILinnotParserExt lnp;
    
    // should make linnot an interface + 4 classes for subtypes, refactor this to a factory and get rid of linnotType
    public LinnotToGraph(String linnot, String linnotType, String graphType) {
        if(linnotType.equals(FoldingGraph.FG_NOTATION_RED)) {
            lnp = new LinnotParserRED(linnot, graphType);
        }
        else if(linnotType.equals(FoldingGraph.FG_NOTATION_ADJ)) {
            lnp = new LinnotParserADJ(linnot, graphType);
        }
        else {
            DP.getInstance().e("LinnotToGraph", "constructor: Parsing of linnot type " + linnotType + " NOT supported.");
            lnp = null;
            System.exit(1);
        }
    }
    
    @Override
    public List<IDrawableVertex> getVertices() {
        List<IDrawableVertex> outVerts = new ArrayList<>();
        List<String> parsedVertTypes = lnp.getResultVertices();
        
        for(String p : parsedVertTypes) {
            outVerts.add(new DrawableVertex(p));
        }
        
        return outVerts;
    }
    
    @Override
    public List<IDrawableEdge> getEdges() {
        List<IDrawableEdge> outEdges = new ArrayList<>();
        List<Integer[]> parsedEdges = lnp.getResultEdges();
        
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
