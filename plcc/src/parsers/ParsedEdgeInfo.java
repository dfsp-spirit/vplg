/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package parsers;

import graphdrawing.IDrawableEdge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author spirit
 */
public class ParsedEdgeInfo implements IDrawableEdge {
    
    private Integer startVertexID;
    private Integer endVertexID;
    protected Map <String, String> edgeProps;
    
    public ParsedEdgeInfo() {
        this.edgeProps = new HashMap<>();
    }
    
    public void setEdgeProperty(String key, String value) {
        this.edgeProps.put(key, value);
    }
    
    public String getEdgeProperty(String key) {
        return this.edgeProps.get(key);
    }
    
    public Boolean verify() {
        return !(null == this.endVertexID || this.startVertexID == null);
    }

    @Override
    public String getSpatRel() {
        return this.edgeProps.get("spatRel");
    }

    @Override
    public List<Integer> getVertPairIndicesNtoC() {
        List<Integer> indices = new ArrayList<>();
        indices.add(this.getStartVertexID());
        indices.add(this.getEndVertexID());
        Collections.sort(indices);
        return indices;
    }

    /**
     * @return the startVertexID
     */
    public Integer getStartVertexID() {
        return startVertexID;
    }

    /**
     * @param startVertexID the startVertexID to set
     */
    public void setStartVertexID(Integer startVertexID) {
        this.startVertexID = startVertexID;
    }

    /**
     * @return the endVertexID
     */
    public Integer getEndVertexID() {
        return endVertexID;
    }

    /**
     * @param endVertexID the endVertexID to set
     */
    public void setEndVertexID(Integer endVertexID) {
        this.endVertexID = endVertexID;
    }
    
}
