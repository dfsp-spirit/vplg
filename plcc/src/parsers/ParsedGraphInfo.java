/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package parsers;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author spirit
 */
public class ParsedGraphInfo {
    
    protected Map <String, String> edgeProps;
    
    public ParsedGraphInfo() {
        this.edgeProps = new HashMap<>();
    }
    
    public void setGraphProperty(String key, String value) {
        this.edgeProps.put(key, value);
    }
    
    public String getGraphProperty(String key) {
        return this.edgeProps.get(key);
    }
    
}
