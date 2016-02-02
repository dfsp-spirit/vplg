/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package parsers;

import datastructures.SparseGraph;
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
     * Checks whether a parse edge info with same start and target vertices (including switched version) exists in the list.
     * @param list list of edge infos
     * @param e the edge we are looking for
     * @return true or false, guess what it means
     */
    private static Boolean listContainsEdgeInfo(List<ParsedEdgeInfo> list, ParsedEdgeInfo e) {
        Integer s, t;
        Integer e_s = e.getStartVertexID();
        Integer e_t = e.getEndVertexID();
        for(ParsedEdgeInfo efl : list) {
            s = efl.getStartVertexID();
            t = efl.getEndVertexID();
            if(s.equals(e_s) && t.equals(e_t) ) {
                return true;
            }
            if(s.equals(e_t) && t.equals(e_s) ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Wrapper, prevents adding of edges which are already in the list.
     * @param e the edge
     * @return true if it was NOT in the list and was added, false if it was in the list and ignored
     */
    protected Boolean addOutEdge(ParsedEdgeInfo e) {
        if(GraphParser.listContainsEdgeInfo(outEdges, e)) {
            return false;
        }
        outEdges.add(e);
        return true;
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
    
    /**
     * Constructs a sparse graph from the parsed data.
     * @return  a sparse graph. uses the strings from the 'label' field of parsed vertices and edges (or the vertex/edge index if the label is null).
     */
    public SparseGraph<String, String> getSparseGraph() {
        
        SparseGraph<String, String> g = new SparseGraph<>();
        
        String vLabel, eLabel;
        for(int i = 0; i < this.getVertices().size(); i++) {
            ParsedVertexInfo v = this.getVertices().get(i);
            vLabel = v.getVertexProperty("label");
            if(vLabel == null) { vLabel = "" + i; }
            g.addVertex(vLabel);
        }
        
        for(int i = 0; i < this.getEdges().size(); i++) {
            ParsedEdgeInfo e = this.getEdges().get(i);
            eLabel = e.getEdgeProperty("label");            
            if(eLabel == null) { eLabel = "" + i; }
            g.addEdge(e.getStartVertexID(), e.getEndVertexID(), eLabel);
        }
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
    
    
    
    public static void main(String[] args) {
        
    }


    
}
