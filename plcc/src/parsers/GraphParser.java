/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package parsers;

import graphdrawing.DrawableGraph;
import graphdrawing.IDrawableEdge;
import graphdrawing.IDrawableGraph;
import graphdrawing.IDrawableGraphProvider;
import graphdrawing.IDrawableVertex;
import java.util.ArrayList;
import java.util.List;
import tools.DP;

/**
 *
 * @author spirit
 */
public abstract class GraphParser implements IGraphParser, IDrawableGraphProvider {
    protected final ParsedGraphInfo gInfo;
    protected final String input;
    protected final List<ParsedEdgeInfo> outEdges;
    protected final List<ParsedVertexInfo> outVerts;
    protected Boolean parsed;
    
    public GraphParser(String gml) {
        this.input = gml;
        this.outEdges = new ArrayList<>();
        this.outVerts = new ArrayList<>();
        gInfo = new ParsedGraphInfo();
        this.parsed = false;
    }
    
    /**
     * Sub classes should implement this and call it at the end of their constructor.
     * It must parse the input string and fill the lists of parsed vertices and edges from it. Be sure to set this.parsed = true at the end of the method.
     */
    protected abstract void parse();

    protected List<IDrawableEdge> getDrawableEdges() {
        List<IDrawableEdge> dv = new ArrayList<>();
        for (ParsedEdgeInfo pvi : this.getEdges()) {
            dv.add((IDrawableEdge) pvi);
        }
        return dv;
    }

    @Override
    public IDrawableGraph getDrawableGraph() {
        IDrawableGraph g = new DrawableGraph(this.getDrawableVertices(), this.getDrawableEdges(), this.gInfo.getMap());
        return g;
    }

    protected List<IDrawableVertex> getDrawableVertices() {
        List<IDrawableVertex> dv = new ArrayList<>();
        for (ParsedVertexInfo pvi : this.getVertices()) {
            dv.add((IDrawableVertex) pvi);
        }
        return dv;
    }

    @Override
    public List<ParsedEdgeInfo> getEdges() {
        if(! this.parsed) { DP.getInstance().e("GraphParser", "getEdges: Did not parse input yet (or parse function did not set this.parsed=true)."); }
        return this.outEdges;
    }
    
    @Override
    public List<ParsedVertexInfo> getVertices() {
        if(! this.parsed) { DP.getInstance().e("GraphParser", "getVertices: Did not parse input yet (or parse function did not set this.parsed=true)."); }
        return this.outVerts;
    }
   

    @Override
    public ParsedGraphInfo getGraphInfo() {
        return this.gInfo;
    }


    
}
