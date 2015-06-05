/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package parsers;


import java.util.ArrayList;
import java.util.List;
import plcc.ProtGraph;

/**
 *
 * @author spirit
 */
public class GMLGraphParser implements IGraphParser {
    
    private final String gml;
    private final List<ParsedEdgeInfo> outEdges;
    private final List<ParsedVertexInfo> outVerts;
    private final ParsedGraphInfo gInfo;
    
    public GMLGraphParser(String gml) {
        this.gml = gml;
        this.outEdges = new ArrayList<>();
        this.outVerts = new ArrayList<>();
        gInfo = new ParsedGraphInfo();
        this.parse();
    }
    
    @Override
    public List<ParsedEdgeInfo> getEdges() {
        return this.outEdges;
    }
    
    @Override
    public List<ParsedVertexInfo> getVerts() {
        return this.outVerts;
    }
    
    @Override
    public ParsedGraphInfo getGraphInfo() {
        return this.gInfo;
    }
            
    
    private void parse() {

        String[] lines = gml.split("\\r?\\n");
        String[] tokens;
        ProtGraph g;
        
        // parse data and prepare the list of SSEs for the graph
        
        Boolean inGraph = false;
        Boolean inVertex = false;
        Boolean inEdge = false;
        Boolean hitGraphStart = false;
        
        String line; 
        ParsedEdgeInfo e = new ParsedEdgeInfo(); 
        ParsedVertexInfo v = new ParsedVertexInfo();
        for(int i = 0; i < lines.length; i++) {
            line = lines[i];
            // handle opening lines
            if(line.contains("graph [")) {
                hitGraphStart = true;
                inGraph = true;                
                inEdge = false;
                inVertex = false;
                continue;
            }
            if(line.contains("node [")) {
                inVertex = true;
                v = new ParsedVertexInfo();
                inEdge = false;
                inGraph = false;
                continue;
            }
            if(line.contains("edge [")) {
                inVertex = false;
                e = new ParsedEdgeInfo();
                inEdge = true;
                inGraph = false;
                continue;
            }
            
            // handle closing lines
            String lstrip = line.trim();
            if(lstrip.equals("]")) {
                if(inGraph) { inGraph = false; }
                if(inEdge) { inEdge = false; if(e.verify()) { this.outEdges.add(e); } else { System.err.println("ERROR: incomplete e"); } }
                if(inVertex) { inVertex = false; if(v.verify()) { this.outVerts.add(v); } else { System.err.println("ERROR: incomplete v"); } }
                if(! hitGraphStart) { System.err.println("ERROR: unmatched ] found in line " + i + "."); }
                continue;
            }
            
            // handle key-value lines
            String[] kv = GMLGraphParser.getKeyValue(line);
            String key = kv[0];
            String value = kv[1];
            
            if(inGraph) {
                this.gInfo.setGraphProperty(key, value);
            }
            
            if(inEdge) {
                e.setEdgeProperty(key, value);
                if(key.equals("source")) {
                    e.setStartVertexID(Integer.valueOf(value));
                }
                if(key.equals("target")) {
                    e.setEndVertexID(Integer.valueOf(value));
                }

            }
            
            if(inVertex) {
                v.setVertexProperty(key, value);
                
                if(key.equals("id")) {
                    v.setVertexID(Integer.valueOf(value));
                }

            }
        }                 
    }
    
    /**
     * Parses key-value lines in format "<key> <value>", where <value> may be enclosed in "".
     * @param s
     * @return 
     */
    public static String[] getKeyValue(String s) {
        s = s.trim();
        if(s.contains("\"")) {
            int idx = s.indexOf(" ");   // find first space, this one separates key and value
            String key = s.substring(0, idx);   // get key: everything up tp the space (excluding the space)
            String value = s.substring(idx + 1, (s.length() - 1));  // get value: everything after the space
            return new String[] { key, value };
        }
        else {
            return s.split(" ");    // just split at space
        }
    }
    
}
