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
    
}
