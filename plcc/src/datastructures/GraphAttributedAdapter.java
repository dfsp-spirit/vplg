/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package datastructures;

import java.util.HashMap;

/**
 * A simple adapter to make using the GraphAttributedInterface easier. You can extend this
 * to get some methods pre-implemented.
 * @author ts
 */
public abstract class GraphAttributedAdapter implements GraphAttributedInterface {
    
    
    private HashMap<String, String> graphAttributes;
    private HashMap<String, String> vertexAttributes;
    private HashMap<String, String> edgeAttributes;    
    
    
    private String getEdgeIdentifierLabel(int i, int j) {
        return i + "_" + j + "_"; 
    }
    
    private String getVertexIdentifierLabel(int i) {
        return i + "_"; 
    }
    
    @Override
    public void setEdgeAttribute(int i, int j, String name, String value) {
        edgeAttributes.put(getEdgeIdentifierLabel(i, j) + name, value);
    }
    
    @Override
    public String getEdgeAttribute(int i, int j, String name) {
        return edgeAttributes.get(getEdgeIdentifierLabel(i, j) + name);
    }
    
    @Override
    public void setVertexAttribute(int i, String name, String value) {
        vertexAttributes.put(getVertexIdentifierLabel(i) + name, value);
    }
    
    @Override
    public String getVertexAttribute(int i, String name) {
        return vertexAttributes.get(getVertexIdentifierLabel(i) + name);
    }
    
    @Override
    public void setGraphAttribute(String name, String value) {
        graphAttributes.put(name, value);
    }
    
    @Override
    public String getGraphAttribute(String name) {
        return graphAttributes.get(name);
    }
    
}
