/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;


import tools.DP;
import java.io.*;
import java.util.*;

/**
 * This is the static I/O class for the ProtGraph class. It allows you to read ProtGraphs from files and write them
 * to files.
 * 
 * @author ts
 */
public class ProtGraphs {
    
    private static ObjectInputStream objectIn;
    
    public static final Integer GRAPHTYPE_ALPHA = 1;
    public static final Integer GRAPHTYPE_BETA = 2;
    public static final Integer GRAPHTYPE_ALBE = 3;
    public static final Integer GRAPHTYPE_ALPHALIG = 4;
    public static final Integer GRAPHTYPE_BETALIG = 5;
    public static final Integer GRAPHTYPE_ALBELIG = 6;
    
    public static Integer getGraphTypeCode(String gt) {
        if(gt.equals("alpha")) { return(GRAPHTYPE_ALPHA); }
        else if(gt.equals("beta")) { return(GRAPHTYPE_BETA); }
        else if(gt.equals("albe")) { return(GRAPHTYPE_ALBE); }
        else if(gt.equals("alphalig")) { return(GRAPHTYPE_ALPHALIG); }
        else if(gt.equals("betalig")) { return(GRAPHTYPE_BETALIG); }
        else if(gt.equals("albelig")) { return(GRAPHTYPE_ALBELIG); }
        else {
            DP.getInstance().w("ProtGraphs.getGraphTypeCode(): Graph string '" + gt + "' invalid.");
            return(-1);
        }
    }
    
    /**
     * Returns the graph string (e.g., "albe") for the given graph code number. This is used in the database,
     * where graph_type is stored as an Integer.
     * @param codeNum the internal code number used for the graph type, e.g., '1'
     * @return the common name of the graph type, e.g., "albe"
     */
    public static String getGraphTypeString(Integer codeNum) {
        if(codeNum == GRAPHTYPE_ALPHA) { return("alpha"); }
        else if(codeNum == GRAPHTYPE_BETA) { return("beta"); }
        else if(codeNum == GRAPHTYPE_ALBE) { return("albe"); }
        else if(codeNum == GRAPHTYPE_ALPHALIG) { return("alphalig"); }
        else if(codeNum == GRAPHTYPE_BETALIG) { return("betalig"); }
        else if(codeNum == GRAPHTYPE_ALBELIG) { return("albelig"); }
        else {
            DP.getInstance().w("ProtGraphs.getGraphTypeString(): Graph code '" + codeNum + "' invalid.");
            return("invalid_graph_type");
        }
    }

    /**
     * Reads a file that has to contain a serialized ProtGraph object in binary form (as written by ProtGraph.toFile()).
     *
     */
    public static ProtGraph fromSerializedBinaryFile(String file) {

        ProtGraph pg = null;

        try {
            objectIn = new ObjectInputStream(new FileInputStream(file));
            pg = (ProtGraph)objectIn.readObject();
        }
        catch(Exception e) {
            DP.getInstance().w("Could not read ProtGraph object from file '" + file + "'.");
            pg = null;
        }
        finally {
            try {
                objectIn.close();
            }
            catch(Exception ex) {
                // Wayne interessiert's.
            }
        }

        return(pg);

    }
    
    
    /**
     * Reads a file that has to contain a graph in TGF (trivial graph format) and turns it into a protein graph.
     * @param file the path to the file
     * @return the TGF string
     */
    public static ProtGraph fromTrivialGraphFormatFile(String file) {
        return(ProtGraphs.fromTrivialGraphFormatString(FileParser.slurpFileToString(file)));
    }
    
    
    /**
     * Reads a string that has to contain a graph in TGF (trivial graph format) and turns it into a protein graph.
     * @param s the source string
     * @return the ProtGraph object
     */
    public static ProtGraph fromTrivialGraphFormatString(String s) {

        ProtGraph pg = null;
        Boolean inVertices = true;
        
        String linesArr[] = s.split("\\r?\\n");
        ArrayList<String> lines = new ArrayList<String>(Arrays.asList(linesArr));
        
        // DEBUG
        //System.out.println("DEBUG: ProtGraphs.fromTrivialGraphFormatString: Handling string with " + lines.size() + " lines.");
        //String cgFile = "compatgraph.tgf";
        //IO.stringToTextFile(cgFile, s);
        //System.out.println("DEBUG: wrote TGF to file '" + cgFile + "'.");
        
        //DEBUG
        /*
        Integer stopAtLine = lines.size() > 50 ? 50 : lines.size();
        for(Integer i = 0; i < stopAtLine; i++) {
            System.out.println("DEBUG: line #" + i + ":'" + lines.get(i) + "'");
        }
        */
        
        ArrayList<SSE> sses = new ArrayList<SSE>();
        String l, vertexPart, vertexPart1, vertexPart2, labelPart, edgePart, vertexLabel, edgeLabel;
        l = vertexLabel = edgeLabel = vertexPart = labelPart = vertexPart1 = vertexPart2 = null;
        String [] words;
        Integer vertex, firstSpace, vertex1, vertex2;
        SSE sse = null;
        Residue r = null;

        Integer curLine = 0;
        for(Integer i = 0; i < lines.size(); i++) {

            curLine++;
            l = lines.get(i);
            // In the nodes part (before the '#')

            // skip empty lines
            if(l.length() < 1) {
                //DP.getInstance().w("TGF_FORMAT: Skipping line " + curLine + " (empty).");
                continue;
            }

            if(inVertices) {

                if(l.startsWith("#")) {         // We hit the border between vertices and edges, create the graph from the vertices we .
                    inVertices = false;            //  found so far and skip this line.
                    
                    if(sses.size() <= 0) {
                        DP.getInstance().w("ProtGraphs.fromTrivialGraphFormatFile(): In edge section but no vertices found so far.");
                    }
                    
                    pg = new ProtGraph(sses);
                    pg.setInfo("<NONE>", "<NONE>", "custom");
                    //System.out.println("DEBUG: fromTrivialGraphFormatString: Found vertex set / edge set separator '#' in line " + curLine + ".");
                    continue;
                }
                else {
                    firstSpace = l.indexOf(" ");

                    if(firstSpace < 0) {
                        // No spaces where found (but the line is non-empty), so there is no vertex label, only the vertex number.
                        // System.out.println("      No spaces found in line " + curLine + ", assuming empty label.");
                        vertexPart = l.trim();
                        labelPart = "";
                    }
                    else {
                        // The line contains spaces, so the part before the 1st space is considered the vertex, the rest is the vertex label.
                        try {
                            vertexPart = l.substring(0, firstSpace);
                            labelPart = (l.substring(firstSpace, l.length())).trim();
                        } catch(Exception e) {
                            // We'll just skip this line for now
                            DP.getInstance().w("TGF_FORMAT: Skipping line " + curLine + " (could not parse as vertex: " + e.getMessage() + ").");
                            continue;
                        }                                                                        
                    }

                    //System.out.println("    At vertex, line " + curLine + ", vertexPart = '" + vertexPart + "', labelPart = '" + labelPart + "'.");
                    // The parts have been defined, parse them
                    try {
                        vertex = Integer.valueOf(vertexPart);
                        vertexLabel = vertexPart.trim();
                    } catch(Exception e) {
                        DP.getInstance().w("TGF_FORMAT: Skipping line " + curLine + " (could not extract vertex data:" + e.getMessage() + ".");
                        continue;
                        //System.err.println("ERROR: Parsing vertex line in trivial graph format file '" + file + "' failed. File broken or in wrong format?");
                        //System.exit(1);
                    }

                    // We can now create the vertex object (a fake SSE)
                    sse = new SSE("H");
                    r = new Residue(vertex, vertex);        // make sure it has a start/end residue
                    sse.addResidue(r);
                    sse.setSeqSseChainNum(vertex);  // this is not true and makes no sense with a non-PG, of course
                    sses.add(sse);
                }
            }
            else {                  // We are in the edge part
                //System.out.println("DEBUG: In edge part at line " + curLine + ".");
                try {
                    words = l.split("\\s", 3);                


                    if(words.length < 2) {
                        DP.getInstance().w("TGF_FORMAT: Skipping line " + curLine + " (less than 2 fields can't encode an edge).");
                        continue;
                    }
                    else if(words.length == 2) {
                        vertexPart1 = words[0];
                        vertexPart2 = words[1];
                        labelPart = "";
                    }
                    else {
                        vertexPart1 = words[0];
                        vertexPart2 = words[1];
                        labelPart = words[2];
                    }
                    //System.out.println("DEBUG: split line " + curLine + ".");
                } catch (Exception ez) {
                    System.err.println("ERROR: fromTrivialGraphFormatString: Could not split line '" + l + "': " + ez.getMessage() + ". Skipping.");
                    continue;
                }

                // The parts have been defined, parse them
                //System.out.println("    At edge, line " + curLine + ", vertexPart1 = '" + vertexPart1 + "', vertexPart2 = '" + vertexPart2 + "', labelPart = '" + labelPart + "'.");
                try {
                    vertex1 = Integer.valueOf(vertexPart1);
                    vertex2 = Integer.valueOf(vertexPart2);
                    edgeLabel = labelPart.trim();
                } catch(Exception e) {
                    DP.getInstance().w("TGF_FORMAT: Skipping line " + curLine + " (could not parse as edge).");
                    continue;
                    //System.err.println("ERROR: Parsing vertex line in trivial graph format file '" + file + "' failed. File broken or in wrong format?");
                    //System.exit(1);
                }

                // We can now add this edge
                if(pg == null) {
                    System.err.println("ERROR: TGF_FORMAT: Graph has no '#' line separating vertices from edges. File broken (skipping edge).");
                }
                else {
                    //pg.addContact(vertex1 - 1, vertex2 - 1, 1);
                    pg.addContact(vertex1, vertex2, 1);
                    //System.out.println("Added contact.");
                }


            }
        }
        
        //System.out.println("DEBUG: fromTrivialGraphFormatString: Handled " + lines.size() + " lines.");

        if(pg == null) {
            DP.getInstance().w("TGF_FORMAT: Graph in trivial graph format is empty (has no edges).");
            return(new ProtGraph(new ArrayList<SSE>()));
        }
        
        //System.out.println("DEBUG: ProtGraphs.fromTrivialGraphFormatString: Created ProtGraph.");
        //System.out.println("DEBUG: ProtGraphs.fromTrivialGraphFormatString: Created ProtGraph is " + pg.toShortString() + ".");

        return(pg);

    }
    
    
    /**
     * Parses the plcc format string for meta data and returns it in a HashMap. Everyline in the string that is in the correct format ("> key > value") is parsed and the resulting (key, value) pair
     * is added to the returned HashMap. This function guarantees that the HashMap contains at least one key: format_version. If it is not found in the file,
     * it is set to '1' because version 1 is the only version which did NOT have this field.
     * 
     * @param graphString the graph string to scan
     * @return the meta data in a HashMap.
     */
    public static HashMap<String, String> getMetaData(String graphString) {
        
        HashMap<String, String> md = new HashMap<String, String>();
        
        md.put("format_version", "1");      // will be overwritten later if it occurs in the file. If it does NOT occur, this is the correct value.
        
        ArrayList<String> lines = multiLineStringToStringList(graphString);
        
        // other vars
        String l = null;            // a line!
        String empty, key, value;
        empty = key = value = null;
        String [] words;

        Integer curLine = 0;
        Boolean error = false;
        
        // Get all met data entries
        for(Integer i = 0; i < lines.size(); i++) {

            curLine++;            
            
            // remove all whitespace from the line, it is not needed and will make splitting way easier later
            l = lines.get(i).replaceAll("\\s*","");
                                    
            if(l.startsWith(">")) {
                
                //System.out.println("[SSE] * Handling line #" + curLine + " of the " + lines.size() + " lines.");
                //System.out.println("[SSE]   Line: '" + l + "'");
                
                try {
                    words = l.split("\\>");
                    
                    if(words.length != 3) {
                        System.err.println("ERROR: PLCC_FORMAT: Hit meta data line containing " + words.length + " fields at line #" + curLine + " (expected 3).");
                        error = true;
                    }
                    
                    empty = words[0];           // the empty string, leftmost field
                    key = words[1];
                    value = words[2];                    
                } catch(Exception e) {
                    System.err.println("ERROR: PLCC_FORMAT: Broken meta data line encountered at line #" + curLine + ". Ignoring.");
                    error = false;              // may have been set before exception!
                    continue;
                }
                
                if(error) {
                    System.err.println("ERROR: PLCC_FORMAT: Broken meta data line encountered at line #" + curLine + ", wrong number of fields. Ignoring.");
                    error = false;
                    continue;
                }
                
                md.put(key, value);
            }
        }
        
        
        return(md);
    }
    
    
    /**
     * Debug function, prints the meta data contained in the plcc file 'file'.
     * @param graphString the plcc input graph string
     */ 
    public static void printPlccMetaData(String graphString) {
        
        HashMap<String, String> md = getMetaData(graphString);
        
        System.out.println("Meta data for plcc graph follows:");
        
        for(String key : md.keySet()) {
            System.out.println(" " + key + " = " + (String)md.get(key) );
        }
        
        System.out.println("Meta data output complete.");
    }
    
    
    /**
     * Determines the file format version of the plcc format graph file 'file'. This information can be used to call the proper parsing function for the
     * file format version.
     * @param graphString the graph string
     * @return the 'format_version' meta data value found in the string as an Integer
     */
    public static Integer getPlccFileVersion(String graphString) {
        
        Integer v = 1;
        HashMap<String, String> md = getMetaData(graphString);
                
        try {
            v = Integer.parseInt(md.get("format_version"));            
        } catch(Exception e) {
            System.err.println("ERROR: getPlccFileVersion(): format_version is not an Integer in plcc graph string, input data broken.");
            System.exit(1);
        }
        
        return(v);               
    }
    
    
    /**
     * Creates a PG from the given String.
     * @param graphString the string
     * @return the resulting graph
     */
    public static ProtGraph fromPlccGraphFormatString(String graphString) {
        Integer v = getPlccFileVersion(graphString);
        
        if(v == 2) {
            return(fromPlccGraphFormatStringV2(graphString));
        }
        else {
            System.err.println("ERROR: Invalid file format version of graph string: '" + v + "'.");
            return null;
        }
    }
    
    
    
    
    
    public static ArrayList<String> multiLineStringToStringList(String s) {
        
        String lines[] = s.split("\\r?\\n");
        
        return(new ArrayList<String>(Arrays.asList(lines)));
    }
    
    
    /**
     * Reads a file that has to contain a graph in PLCC graph format version 2 and turns it into a protein graph. Note that this can be used for drawing a protein graph
     * from a file, but it does NOT restore the complete graph because information on the atoms and residues is not contained in the graph file.
     *
     * @param graphString a string in VPLG format representing the graph
     * @return the created graph
     */
    public static ProtGraph fromPlccGraphFormatStringV2(String graphString) {

        ProtGraph pg = null;

        ArrayList<String> lines = multiLineStringToStringList(graphString);
        ArrayList<SSE> sses = new ArrayList<SSE>();
        
        // tmp vars for vertex lines
        String pdbid, chain, graphType, sseType, pdbStartRes, pdbEndRes, sequence;
        pdbid = chain = graphType = sseType = pdbStartRes = pdbEndRes = sequence = null;
        Integer sseID, dsspStartRes, dsspEndRes, seqSSENum;
        sseID = dsspStartRes = dsspEndRes = seqSSENum = 0;
        
        // tmp vars for edge lines
        String spatRel = null;
        Integer sseID1, sseID2; 
        sseID1 = sseID2 = 0;
        
        // other vars
        String l = null;            // a line!
        String empty = null;
        String [] words;
        SSE sse = null;
        Residue r = null;
        Boolean error = false;

        Integer curLine = 0;
        Integer numContactsAdded = 0;
        
        // Get all vertices so we can create the graph.
        for(Integer i = 0; i < lines.size(); i++) {

            curLine++;            
            
            // remove all whitespace from the line, it is not needed and will make splitting way easier later
            l = lines.get(i).replaceAll("\\s*","");
                                    
            if(l.startsWith("|")) {
                
                //System.out.println("[SSE] * Handling line #" + curLine + " of the " + lines.size() + " lines.");
                //System.out.println("[SSE]   Line: '" + l + "'");
                
                try {
                    words = l.split("\\|");
                    
                    Integer numExpected = 12;
                    
                    if(words.length != numExpected) {
                        System.err.println("ERROR: PLCC_FORMAT: Hit vertex line containing " + words.length + " fields at line #" + curLine + " (expected " + numExpected + ").");
                        error = true;
                    }
                    
                    empty = words[0];           // the empty string, leftmost field
                    pdbid = words[1];
                    chain = words[2];
                    graphType = words[3];
                    seqSSENum = Integer.valueOf(words[4]);
                    sseID = (Integer.valueOf(words[5]) - 1);    /** the -1 is because we add 1 when we print it so the list doesnt start at zero and matches the one in the image */
                    sseType = words[6];
                    dsspStartRes = Integer.valueOf(words[7]);
                    dsspEndRes = Integer.valueOf(words[8]);
                    pdbStartRes = words[9];
                    pdbEndRes = words[10];
                    sequence = words[11];
                    
                } catch(Exception e) {
                    System.err.println("ERROR: PLCC_FORMAT: Broken vertex line encountered at line #" + curLine + ". Ignoring.");
                    error = false;              // may have been set before exception!
                    continue;
                }
                
                if(error) {
                    System.err.println("ERROR: PLCC_FORMAT: Broken vertex line encountered at line #" + curLine + ", wrong number of fields. Ignoring.");
                    error = false;
                    continue;
                }
                
                // We can now create the vertex object (a fake SSE)
                sse = new SSE(sseType);
                
                // add the residues
                Integer numRes = dsspEndRes - dsspStartRes + 1;
                
                // Note that we cannot restore the correct AA sequences because the DSSP residue numbers include chain brake extra residues, thus the
                //  length of the sequence string does NOT equal the number of residues.
                //if(sequence.length() != numRes) {
                //    System.err.println("ERROR: PLCC_FORMAT: Broken vertex line encountered at line #" + curLine + ". Sequence length should be " + numRes + " but is " + sequence.length() + ". Ignoring.");
                //    continue;
                //}
                
                
                //Integer sequenceStringIndex = 0;      // unused, see comment on AA sequence above
                for(Integer j = dsspStartRes; j <= dsspEndRes; j++) {
                    r = new Residue(j, j);        // Make sure the SSE has a start/end residue, just put fake values for the PDB numbers, there is no way to determine them unless you have a complete list because they are not necessarily sequential.
                    //r.setAAName1(sequence.substring(sequenceStringIndex, sequenceStringIndex + 1));       // unused, see comment on AA sequence above
                    r.setAAName1("?");                                                                      // see comment on AA sequence above
                    r.setChainID(chain);
                    r.setiCode(" ");
                    
                    sse.addResidue(r);
                    //sequenceStringIndex++;        // unused, see comment on AA sequence above
                }                               
                
                // set other SSE info
                sse.setSeqSseChainNum(seqSSENum);   // This is the correct value, parsed from the input file
                sses.add(sse);                
            }
        }
        
        // All vertices have been parsed, create the graph.
        
        if(sses.size() <= 0) {
            System.err.println("ERROR: PLCC_FORMAT: Graph file did not contain any valid SSE lines, vertex set empty. Exiting.");
            System.exit(1);
        }
        else {
            //System.out.println("  Parsed " + sses.size() + " SSEs from input file in plcc graph format.");
        }
        
        pg = new ProtGraph(sses);
        pg.setInfo(pdbid, chain, graphType);
        
        // Now create all edges
        curLine = 0;
        for(Integer i = 0; i < lines.size(); i++) {

            curLine++;                        
            
            // remove all whitespace from the line, it is not needed and will make splitting way easier later
            l = lines.get(i).replaceAll("\\s*","");
            
            if(l.startsWith("=")) {
                
                //System.out.println("[Contact] * Handling line #" + curLine + " of the " + lines.size() + " lines.");
                //System.out.println("[Contact]   Line: '" + l + "'");
                
                try {
                    words = l.split("=");
                    
                    if(words.length != 4) {
                        System.err.println("ERROR: PLCC_FORMAT: Hit edge line containing " + words.length + " fields at line #" + curLine + " (expected 4). Ignoring.");
                        error = true;
                    }
                    
                    empty = words[0];
                    sseID1 = Integer.valueOf(words[1]);
                    spatRel = words[2];
                    sseID2 = Integer.valueOf(words[3]);
                            
                    
                } catch(Exception e) {
                    System.err.println("ERROR: PLCC_FORMAT: Broken edge line encountered at line #" + curLine + ". Ignoring.");
                    error = false;                  // may have been set before exception!
                    continue;
                }
                
                if(error) {
                    System.err.println("ERROR: PLCC_FORMAT: Broken edge line encountered at line #" + curLine + ", wrong number of fields. Ignoring.");
                    error = false;
                    continue;
                }
                
                // everything seems fine, add the contact
                pg.addContact(sseID1 - 1, sseID2 - 1, SpatRel.stringToInt(spatRel));
                numContactsAdded++;
                
            }
        }
                                
        // Done.  
        //System.out.println("  Parsed " + numContactsAdded + " contacts from input file in plcc graph format.");
            
        if(pg == null) {
            System.err.println("ERROR: Parsing graph from plcc format graph string failed, returning empty graph.");
            return(new ProtGraph(new ArrayList<SSE>()));
        }

        
        pg.setMetaData(getMetaData(graphString));
        return(pg);

    }



}
