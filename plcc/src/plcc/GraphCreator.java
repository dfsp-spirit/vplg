/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import proteinstructure.Residue;
import proteinstructure.SSE;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Random;
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
    
    
    /**
     * Parses a protein graph from a CSV string. Note that various properties
     * of the graph will be null, because only the SSEs and contacts are 
     * parsed from the CSV string
     * @param csv the CSV string
     * @return the protein graph parsed from the CSV
     */
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
    
    
    /**
     * Generates a random protein ligand graph with the specified number of vertices and the specified edge probability.
     * @param numVertices the number of vertices to add to the graph
     * @param edgeProb the edge probability (for each edge pair)
     * @return the resulting protein ligand graph
     */
    public ProtGraph createRandom(int numVertices, double edgeProb) {
        ProtGraph g;
        
        System.out.println(" Creating random protein ligand graph.");
        
        double[] vertexRelFreqs = new double[] { 0.41, 0.46, 0.13 };        
        double[] vertexBorders = GraphCreator.createBordersFromRelativeFrequencyOfOccurence(vertexRelFreqs);
        int[] vertexClasses = new int[] { SSE.SSECLASS_HELIX, SSE.SSECLASS_BETASTRAND, SSE.SSECLASS_LIGAND};
        
        ArrayList<SSE> vertices = new ArrayList<SSE>();
        
        // add vertices
        SSE sse;
        Integer vClass;
        for(int i = 0; i < numVertices; i++) {
            vClass = GraphCreator.drawClassFromDistribution(vertexBorders, vertexClasses);
            sse = new SSE(vClass);    
            Residue r = new Residue(i, i);
            r.setAAName1("A");
            r.setChainID("A");
            r.setiCode("");
            sse.addResidue(r);
            if(vClass.equals(SSE.SSECLASS_LIGAND)) {
                r.setResName3("LIG");
            }
            sse.setSeqSseChainNum(i);            
            vertices.add(sse);
        }
        
        g = new ProtGraph(vertices);
        g.setInfo("rand", "A", "albelig");
        
        System.out.println("  Added all " + g.getSize() + " vertices to the graph.");
        // add all edges
        
        double[] edgeRelFreqs = new double[] { 0.70, 0.09, 0.21 };        
        double[] edgeBorders = GraphCreator.createBordersFromRelativeFrequencyOfOccurence(vertexRelFreqs);
        int[] edgeClasses = new int[] { SpatRel.PARALLEL, SpatRel.ANTIPARALLEL, SpatRel.MIXED };
        
        Random rand = new Random();
        long currentTime = System.currentTimeMillis();
        rand.setSeed(currentTime);
        int numEdges = 0;
        double random;
        for(int i = 0; i < g.numVertices(); i++) {
            for(int j = i+1; j < g.numVertices(); j++) {
                random = rand.nextDouble();
                if(random <= edgeProb) {
                    
                    Integer spatOrientation;
                    // check for ligands
                    if(g.getVertex(i).isLigandSSE() || g.getVertex(j).isLigandSSE()) {
                        spatOrientation = SpatRel.LIGAND;
                    }
                    else {
                        spatOrientation = GraphCreator.drawClassFromDistribution(edgeBorders, edgeClasses);
                    }
                    
                    g.addContact(i, j, spatOrientation);
                    numEdges++;
                }
            }
        }
        System.out.println("  Added all " + numEdges + " edges to the graph.");
        
        return g;
    }
    
    /**
     * Creates the borders for the given relative frequencies between 0.0 and 1.0.
     * @param relFreq the requested relative frequencies that should be reached 
     * @return the borders between 0.0 and 1.0 that lead to the requested frequencies when drawing uniformely random from 0.0 to 1.0
     * @throws IllegalArgumentException 
     */
    public static double[] createBordersFromRelativeFrequencyOfOccurence(double[] relFreq) throws IllegalArgumentException {
        if(relFreq.length < 1) {
            throw new IllegalArgumentException("Relative Frequencies must not be empty.");
        }
        
        double sum = 0.0;
        for(double f : relFreq) {
            sum += f;
        }
        if(sum < 0.9 || sum > 1.1) {
            throw new IllegalArgumentException("Sum of all relative frequencies must be 1.0 +/- 0.1.");
        }
        
        double border = 0.0;
        double[] borders = new double[relFreq.length - 1];
        
        for (int i = 0; i < (relFreq.length -1); i++) {
            border += relFreq[i];
            borders[i] = border;
            
        }        
        
        return borders;
        
    }
    
    
    /**
     * Draws a class from the given distribution defined by the borders
     * @param borders the borders between classes, all must be in range 0..1
     * @param classes the classes encoded as integers, can be arbitrary integers
     * @return the class that was drawn randomly according to the distribution defined by the borders
     * @throws IllegalArgumentException 
     */
    public static int drawClassFromDistribution(double[] borders, int[] classes) throws IllegalArgumentException {
        
        if(borders.length != (classes.length - 1)) {
            throw new IllegalArgumentException("Number of classes must be (number of borders - 1), but there are " + borders.length + " borders and " + classes.length + " classes.");
        }
        
        for(double b : borders) {
            if(b < 0. || b > 1.) {
                throw new IllegalArgumentException("All borders must be from the range 0..1 but one is " + b + ".");
            }
        }
        
        double draw = Math.random();
        
        int classIndex = 0;
        for(int i = 0; i < borders.length; i++) {
            if(draw > borders[i]) {
                classIndex++;
            }
        }
        
        return classes[classIndex];
    }
    
    
    /**
     * Main for testing only. (For Tatiana)
     * @param argv command line arguments, ignored atm.
     */
    public static void main(String[] argv) {
        // preparations
        String fs = File.separator;
        GraphCreator cg = new GraphCreator();
        
        // graph settings
        int numVertices = 30;
        double edgeProb = 0.05;
        
        // create the random protein ligand graph
        SSEGraph g = cg.createRandom(numVertices, edgeProb);

        // write it to a file in GML format
        String gmlFile = System.getProperty("user.home") + fs + "random_protein_ligand_graph_" + numVertices + "V.gml";
        if(IO.stringToTextFile(gmlFile, g.toGraphModellingLanguageFormat())) {
            System.out.println("Wrote random graph with " + numVertices + " vertices and edge probability " + edgeProb + " to file '" + gmlFile + "'.");
        } else {
            System.err.println("ERROR: Could not write random graph  to file '" + gmlFile + "'.");
        }
        
        // Done.
        System.exit(0);
    }
    
}
