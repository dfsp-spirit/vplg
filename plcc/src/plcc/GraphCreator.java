/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import tools.DP;

/**
 * Some graph parser and generator functions.
 * @author ts
 */
public class GraphCreator {
    
    
    public static ProtGraph fromGMLString(String gml) {
        System.err.println("ERROR: ProtGraph fromGMLString() not implemented yet.");
        System.exit(1);
        
        ProtGraph g;
        
        // parse data and prepare the list of SSEs for the graph
        ArrayList<SSE> sseList = new ArrayList<SSE>();
        
        // now create the graph from the SSE list
        g = new ProtGraph(sseList);
        
        // add some edges here
        return g;
    }
    
    public static ProtGraph fromCSVString(String csv) {
        
        System.err.println("ERROR: ProtGraph fromGMLString() not implemented yet.");
        System.exit(1);
        
        String fs = ",";    // field separator
        String commentLineStartString = "#";
        String metaDataStartString = "#METADATA ";
        ProtGraph g;
        
        String[] lines = csv.split("\\r?\\n");
        System.out.println("Graph string consists of " + lines.length + " lines.");
        String[] tokens;
        
        // ------------------------- add plain vertices ------------------------       
        
        int maxVertexField0, maxVertexField1;
        try {
            maxVertexField0 = GraphCreator.findMaxFromCSV(csv, 0, fs, "#");
            maxVertexField1 = GraphCreator.findMaxFromCSV(csv, 1, fs, "#");
        } catch(Exception e) {
            DP.getInstance().w("GraphCreator", "fromCSVString: input string contains no valid lines, returning empty graph.");
            DP.getInstance().w("GraphCreator", e.getMessage());
            return new ProtGraph(new ArrayList<SSE>());
        }
        int maxVertexIndex = (maxVertexField0 > maxVertexField1 ? maxVertexField0 : maxVertexField1);
        
        DP.getInstance().p("Loading graph from CSV, max vertex index is " + maxVertexIndex + ".");
        DP.getInstance().flush();
        int numVertices = maxVertexIndex + 1;
        SSE ci;
        ArrayList<SSE> sseList = new ArrayList<SSE>();
        for(int i = 0; i < numVertices; i++) {
            ci = new SSE("?");
            // we put random stuff in here first, we'll fill out the details later when we parse the edges
            sseList.add(ci);
        }
        g = new ProtGraph(sseList);
        System.out.println("" + g.getSize() + " vertices added, parsing edges.");
        // ------------------------- handle edges and fill in vertex info ------------------------
        
        int lineNum = 0;
        int src, tgt;
        double weight;
        
        
        String key, value;      
        
        for(String line : lines) {
            lineNum++;
                        
            if(commentLineStartString != null) {
                if(line.startsWith(commentLineStartString)) {
                    if(line.startsWith(metaDataStartString)) {
                        line = line.replace(metaDataStartString, "");
                        tokens = line.split("=");
                        try {
                            key = tokens[0];
                            value = tokens[1];
                            
                            if(key.equals("whatever")) {
                                // do something with it if you want
                            }
                        } catch(Exception e) {
                            DP.getInstance().w("GraphCreator", "Ignoring broken metadata line #" + lineNum + " in CSV string: '" + e.getMessage() + "'.");
                        }
                    }
                    continue;
                }
            }
            
            
            tokens = line.split(fs);
            
            if(tokens.length >= 3) {
                // add this as an edge
                try {
                    src = Integer.parseInt(tokens[0]);
                    tgt = Integer.parseInt(tokens[1]);
                    weight = Double.parseDouble(tokens[2]);
                } catch(Exception e) {
                    // just skip invalid line
                    System.out.println("ERROR: Line #" + lineNum + " broken, first 3 fields broken.");
                    continue;
                }
                
                // parse extra info if available
                if(tokens.length > 3) {                    

                    String srcClassString, tgtClassString;
                    
                    if(tokens.length == 7) {
                        //System.out.println("Parsing line #" + lineNum + " in old format.");
                        // old format, the x and y coordinates are one field in format "<xcoord>_<ycoord>"
                        String srcBothCoords, tgtBothCoords;
                        try {
                            srcBothCoords = tokens[3];
                            tgtBothCoords = tokens[4];
                            srcClassString = tokens[5];
                            tgtClassString = tokens[6];
                        } catch(Exception e) {
                            System.out.println("Old format line #" + lineNum + " broken: '" + line + "' (" + e.getMessage() + ").");
                            // skip line
                            continue;
                        }              
                        
                    }
                    
                    else {
                        System.out.println("Unsupported number of tokens in line: " + tokens.length + ".");
                    }
                    
                }
            } else {
                System.out.println("Line has too few tokens: " + tokens.length + ".");
            }
        }
        
        System.out.println("Graph done.");
        return g;
    }
    
    
    /**
     * Determines the maximum value of a column in a CSV file. The column has to contain integers.
     * @param csv the input string
     * @param fieldIndex the index of the column to check
     * @return the maximum
     * @throws NoSuchElementException if the input contains no valid rows
     */
    public static int findMaxFromCSV(String csv, int fieldIndex, String fieldSeparator, String commentLineStartString) throws NoSuchElementException {
        String fs = fieldSeparator;
        int max = Integer.MAX_VALUE;
        ArrayList<Integer> values = new ArrayList<Integer>();
        
        String[] lines = csv.split("\\r?\\n");
        String[] tokens;
        
        int lineNum = 0;
        for(String line : lines) {
            lineNum++;
            
            if(commentLineStartString != null) {
                if(line.startsWith(commentLineStartString)) {
                    continue;
                }
            }
            
            tokens = line.split(fs);
            
            int val;
            if(fieldIndex < tokens.length) {
                try {
                    val = Integer.parseInt(tokens[fieldIndex]);
                } catch(Exception e) {
                    // skip line
                    continue;
                }
                values.add(val);
            }            
        }
        
        return Collections.max(values);
    }
    
}
