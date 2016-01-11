/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package parsers;

import graphdrawing.IDrawableGraphProvider;
import java.util.ArrayList;
import java.util.List;
import proteingraphs.EdgeProperty;
import proteingraphs.ProtGraph;
import proteingraphs.VertexProperty;
import tools.DP;

/**
 * A parser for the very inflexible and stupid, but simple and fast-to-type SPGF format. SPGF is the Stupid Protein Graph Format (tm). It goes like this: "h,e,e,h,h,l;0p1,2a3,3j5". That was a PLG with 6 vertices and 3 edges.
 * @author spirit
 */
public class SPGFGraphParser extends GraphParser implements IGraphParser, IDrawableGraphProvider {
    
    
    public SPGFGraphParser(String spgf) {
        super(spgf);
        this.parse();
    }
            
    
    @Override
    protected void parse() {

        //System.out.println("SPGFGraphParser.parse()");
        String[] lines = input.split("\\r?\\n");
        
        // parse data and prepare the list of SSEs for the graph
        
        
        
        String line, vertsPart, edgesPart; 
        List<String> vertexFGNotations;
        List<String[]> edgeInfos;
        ParsedEdgeInfo e; 
        ParsedVertexInfo v;
        Boolean hitLine = false;
        for(int i = 0; i < lines.length; i++) {
            line = lines[i];
            //System.out.println("Line # " + i + " of " + lines.length + ": '" + line + "'");
            
            if(line.startsWith("#")) {
                //System.out.println("Skippig comment line " + i);
                continue;
            } else {
                if(hitLine) {
                    DP.getInstance().w("SPGFGraphParser", "parse: hit another graph line, should handle only a single one. Overwriting old results.");
                }
                //System.out.println("hit line, handling #" + i);
                line = line.trim();
                vertsPart = line.split(";")[0];
                edgesPart = line.split(";")[1];               
              
                vertexFGNotations = new ArrayList<>();
                for(String vi : vertsPart.split(",")) {
                    this.outVerts.add(new ParsedVertexInfo(vi));
                }
                
                
                edgeInfos = new ArrayList<>();
                for(String ei : edgesPart.split(",")) {
                    this.addOutEdge(new ParsedEdgeInfo(Integer.valueOf(ei.charAt(0)+""), Integer.valueOf(ei.charAt(2)+""), ei.charAt(1)+""));
                }
                
                //System.out.println("Found " + this.outVerts.size() + " verts, " + this.outEdges.size() + " edges.");
                    
                hitLine = true;
            }
            
        
        }    
        this.parsed = true;
    }
    
    
}

