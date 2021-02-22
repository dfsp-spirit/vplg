/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package datastructures;

import java.util.HashMap;
import java.util.List;

/**
 * A simple adapter to make using the GraphAttributedInterface easier. You can extend this
 * to get some methods pre-implemented.
 * @author ts
 */
public abstract class GraphAttributedAdapter implements GraphAttributedInterface {
    
    
    private HashMap<String, String> graphAttributes;
    private HashMap<String, String> vertexAttributes;
    private HashMap<String, String> edgeAttributes;    
    
    public GraphAttributedAdapter() {
        graphAttributes = new HashMap<>();
        vertexAttributes = new HashMap<>();
        edgeAttributes = new HashMap<>();
    }
    
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
    
    @Override
    public void initVertexAttribute(List<Integer> verts, String name, String value) {
        for(Integer v : verts) {
            setVertexAttribute(v, name, value);
        }
    }
    
    @Override
    public void initEdgeAttribute(List<Integer[]> edges, String name, String value) {
        for(Integer[] edge : edges) {
            setEdgeAttribute(edge[0], edge[1], name, value);
        }
    }
}
