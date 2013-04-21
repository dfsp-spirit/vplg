/*
 * This file is part of SPARGEL, the sparse graph extension library.
 * 
 * This is free software, published under the WTFPL.
 * 
 * Written by Tim Schaefer, see http://rcmd.org/contact/.
 * 
 */
package jgrapht;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;

/**
 * A protein ligand graph implementation bases on JGraphT.
 * @author ts
 */
public class ProteinLigandGraph<V extends Object, E extends Object> extends SimpleGraph<V, E> implements UndirectedGraph<V, E> {
    
    
    protected String pdbid;
    protected String chain;
    protected int modelid;

    public String getPdbid() {
        return pdbid;
    }

    public void setPdbid(String pdbid) {
        this.pdbid = pdbid;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public int getModelid() {
        return modelid;
    }

    public void setModelid(int modelid) {
        this.modelid = modelid;
    }
    
    
    public ProteinLigandGraph(Class<E> edgeClass) {
        super(edgeClass);
        this.chain = "";
        this.pdbid = "";
        this.modelid = -1;
    }
    
    public ProteinLigandGraph(Class<E> edgeClass, List<V> vertices) {
        super(edgeClass);
        this.chain = "";
        this.pdbid = "";
        this.modelid = -1;
        
        for(V v : vertices) {
            this.addVertex(v);
        }
    }
    
    
    /**
     * Usage example.
     * @param argv the command line arguments, ignored
     */
    public static void main(String[] argv) {
        
        // let's create a graph
        System.out.println("Creating protein ligand graph.");
        ProteinLigandGraph<VertexSSE, PLGEdge> plg = new ProteinLigandGraph<VertexSSE, PLGEdge>(PLGEdge.class);
        
        VertexSSE sse1 = new VertexSSE(VertexSSE.SSE_TYPE_ALPHA_HELIX, 1);
        VertexSSE sse2 = new VertexSSE(VertexSSE.SSE_TYPE_BETASTRAND, 2);
        VertexSSE sse3 = new VertexSSE(VertexSSE.SSE_TYPE_BETASTRAND, 3);
        VertexSSE sse4 = new VertexSSE(VertexSSE.SSE_TYPE_ALPHA_HELIX, 4);
        VertexSSE sse5 = new VertexSSE(VertexSSE.SSE_TYPE_LIGAND, 5);
        
        plg.addVertex(sse1);
        plg.addVertex(sse2);
        plg.addVertex(sse3);
        plg.addVertex(sse4);
        plg.addVertex(sse5);
        
        plg.addEdge(sse1, sse2, (PLGEdge)new PLGEdge(PLGEdge.SPATREL_PARALLEL));
        plg.addEdge(sse2, sse3, (PLGEdge)new PLGEdge(PLGEdge.SPATREL_ANTIPARALLEL));
        plg.addEdge(sse1, sse4, (PLGEdge)new PLGEdge(PLGEdge.SPATREL_MIXED));
        plg.addEdge(sse3, sse5, (PLGEdge)new PLGEdge(PLGEdge.SPATREL_LIGAND));
        
        System.out.println("Created protein ligand graph: |V|=" + plg.vertexSet().size() + ", |E|=" + plg.edgeSet().size() + ".");
        // run some stuff on it
        
        StringBuilder sb = new StringBuilder("Shortest path from 1 to 5: ");
        DijkstraShortestPath<VertexSSE, PLGEdge> dsp = new DijkstraShortestPath<VertexSSE, PLGEdge>(plg, sse1, sse5);
        for(PLGEdge e : (List<PLGEdge>)dsp.getPath().getEdgeList()) {
            sb.append(e.toString()).append(" ");
        }
        System.out.println(sb.toString());
    }
    
}
