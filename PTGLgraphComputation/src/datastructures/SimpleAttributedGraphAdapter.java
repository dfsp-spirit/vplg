/*
 * This file is part of SPARGEL, the sparse graph extension library.
 * 
 * This is free software, published under the WTFPL.
 * 
 * Written by Tim Schaefer, see http://rcmd.org/contact/.
 * 
 */

package datastructures;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author spirit
 */
public abstract class SimpleAttributedGraphAdapter implements GraphAttributedInterface, SimpleGraphInterface {
    
    protected HashMap<String, String> graphAttributes;
    protected HashMap<String, String> vertexAttributes;
    protected HashMap<String, String> edgeAttributes;    
    
    public SimpleAttributedGraphAdapter() {
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
    public Character getVertexLabelChar(Integer i) {
        return SimpleGraphDrawer.defaultVertexLabel;
    }
    
    @Override
    public Character getEdgeLabelChar(Integer i, Integer j) {
        return SimpleGraphDrawer.defaultEdgeStart;
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
