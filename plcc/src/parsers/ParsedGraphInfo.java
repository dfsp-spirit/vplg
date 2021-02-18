/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
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
    
    public Map<String, String> getMap() {
        return this.edgeProps;
    }
    
}
