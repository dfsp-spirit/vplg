/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package jgrapht;

import java.util.List;
import java.util.Set;
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
    
    public String toFormatGML() {
        StringBuilder sb = new StringBuilder();
        
        // write graph header and meta data
        sb.append("graph [\n");
        sb.append("  id ").append(1).append("\n");
        sb.append("  label \"" + "VPLG Protein Graph " + "\"\n");
        sb.append("  comment \"").append(this.getPdbid()).append("-").append(this.getChain()).append("\"\n");
        sb.append("  directed 0\n");
        sb.append("  isplanar 0\n");
        sb.append("  creator \"VPLG\"\n");
        
        // write vertices
        for(VertexSSE v : (Set<VertexSSE>)this.vertexSet()) {
            sb.append(v.toFormatGML());
        }
        
        // write edges
        for(PLGEdge e : (Set<PLGEdge>)this.edgeSet()) {
            sb.append(e.toFormatGML());
        }
        
        // write graph footer
        sb.append("]\n");
        
        return sb.toString();
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
        
        // test GML output
        System.out.println("Graph in GML format:\n");
        System.out.println(plg.toFormatGML());
        
        // done
        System.out.println("Tests done, exiting.");
    }
    
}
