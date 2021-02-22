/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package parsers;


import datastructures.SparseGraph;
import graphdrawing.DrawableGraph;
import graphdrawing.DrawableVertex;
import graphdrawing.IDrawableEdge;
import graphdrawing.IDrawableGraph;
import graphdrawing.IDrawableGraphProvider;
import graphdrawing.IDrawableVertex;
import io.FileParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import proteingraphs.EdgeProperty;
import proteingraphs.ProtGraph;
import proteingraphs.VertexProperty;
import tools.DP;

/**
 *
 * @author spirit
 */
public class GMLGraphParser extends GraphParser implements IGraphParser, IDrawableGraphProvider {
    
    
    public GMLGraphParser(String gml) {
        super(gml);
        this.parse();
    }
            
    
    @Override
    protected void parse() {

        String[] lines = input.split("\\r?\\n");
        
        
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
                if(inEdge) { inEdge = false; if(e.verify()) { this.addOutEdge(e); } else { System.err.println("ERROR: incomplete e"); } }
                if(inVertex) { inVertex = false; if(v.verify()) { this.outVerts.add(v); } else { System.err.println("ERROR: incomplete v"); } }
                if(! hitGraphStart) { System.err.println("ERROR: unmatched ] found in line " + i + "."); }
                continue;
            }
            
            // handle key-value lines
            String[] kv = ParserTools.getKeyValue(line);
            String key = kv[0];
            String value = kv[1];
            
            if(inGraph) {
                this.gInfo.setGraphProperty(key, value);
            }
            
            if(inEdge) {
                e.setEdgeProperty(key, value);
                
                if(key.equals(EdgeProperty.SOURCE)) {
                    e.setStartVertexID(Integer.valueOf(value));
                }
                if(key.equals(EdgeProperty.TARGET)) {
                    e.setEndVertexID(Integer.valueOf(value));
                }
                if(key.equals(EdgeProperty.SPATREL)) {
                    e.setSpatRel(value);
                }

            }
            
            if(inVertex) {
                v.setVertexProperty(key, value);
                
                if(key.equals(VertexProperty.VERTEXID)) {
                    v.setVertexID(Integer.valueOf(value));                                     
                }
                if(key.equals(VertexProperty.FGNOTATIONLABEL)) {
                    v.setSseFgNotation(value);                                     
                }

            }
        }      
        this.parsed = true;
    }
    
    
    /**
     * Testing main only
     * @param args ignored
     */
    public static void main(String[] args) {
        String fileName = "ic/8icd/ALL/8icd_aagraph.gml";
        System.out.println("Trying to parse GML file '" + fileName + "'...");
        String gml = "";
        try {
            gml = FileParser.slurpFileToString(fileName);
        } catch(IOException e) {
            System.err.println("Noooooo: '" + e.getMessage() + "'.");
            System.exit(1);
        }
        
        GMLGraphParser gp = new GMLGraphParser(gml);
        SparseGraph<String, String> g = gp.getSparseGraph();
        System.out.println("Graph constructed, |V|=" + g.getNumVertices() +", |E|=" + g.getNumEdges() + ".");
    }
    
    
}
