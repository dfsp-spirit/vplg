/*
 * This file is part of SPARGEL, the sparse graph extension library.
 * 
 * This is free software, published under the WTFPL.
 * 
 * Written by Tim Schaefer, see http://rcmd.org/contact/.
 * 
 */
package jgraph;

import java.util.List;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

/**
 * A protein ligand graph implementation bases on JGraphT.
 * @author ts
 */
public class ProteinLigandGraph<V, E> extends SimpleGraph<V, E> implements UndirectedGraph<V, E> {
    
    
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
        
        ProteinLigandGraph plg = new ProteinLigandGraph<VertexSSE, PLGEdge>(PLGEdge.class);
        
        VertexSSE sse1 = new VertexSSE(VertexSSE.SSE_TYPE_HELIX);
        VertexSSE sse2 = new VertexSSE(VertexSSE.SSE_TYPE_BETASTRAND);
        VertexSSE sse3 = new VertexSSE(VertexSSE.SSE_TYPE_BETASTRAND);
        VertexSSE sse4 = new VertexSSE(VertexSSE.SSE_TYPE_HELIX);
        VertexSSE sse5 = new VertexSSE(VertexSSE.SSE_TYPE_LIGAND);
        
        plg.addEdge(sse1, sse2, new PLGEdge(PLGEdge.SPATREL_PARALLEL));
        plg.addEdge(sse2, sse3, new PLGEdge(PLGEdge.SPATREL_ANTIPARALLEL));
        plg.addEdge(sse1, sse4, new PLGEdge(PLGEdge.SPATREL_MIXED));
        plg.addEdge(sse3, sse5, new PLGEdge(PLGEdge.SPATREL_LIGAND));
    }
    
}
