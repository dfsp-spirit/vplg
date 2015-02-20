/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Marcus Kessler 2013. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author Marcus Kessler
 * modified by TS
 */
package plcc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.spargel.datastructures.UAdjListGraph;
import net.sourceforge.spargel.writers.GMLWriter;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import tools.DP;

/**
 *
 * @author marcus
 */
public class ComplexGraph extends UAdjListGraph {

    public Map<Edge, String[]> chainNamesInEdge;
    public Map<Edge, Integer> numHelixHelixInteractionsMap;
    public Map<Edge, Integer> numHelixStrandInteractionsMap;
    public Map<Edge, Integer> numHelixLoopInteractionsMap;
    public Map<Edge, Integer> numSSInteractionsMap;
    public Map<Edge, Integer> numStrandLoopInteractionsMap;
    public Map<Edge, Integer> numLoopLoopInteractionsMap;
    public Map<Edge, Integer> numAllInteractionsMap;
    public Map<Edge, Integer> numDisulfidesMap;
    public Map<Vertex, String> proteinNodeMap;
    public Map<List<Integer>, Integer> numSSEContacts;

    public Integer[][] numChainInteractions;
    public Integer[][] homologueChains;
    public String[] chainResAASeq;
    public Integer neglectedEdges;

    /**
     * The RCSB PDB id this graph is based on.
     */
    private String pdbid;
    private float lastColorStep;
    private float[] savedVertexColors;

    /**
     * Constructor.
     */
    ComplexGraph(String pdbid) {
        this.pdbid = pdbid;

        numHelixHelixInteractionsMap = createEdgeMap();
        numHelixStrandInteractionsMap = createEdgeMap();
        numHelixLoopInteractionsMap = createEdgeMap();
        numSSInteractionsMap = createEdgeMap();
        numStrandLoopInteractionsMap = createEdgeMap();
        numLoopLoopInteractionsMap = createEdgeMap();
        numAllInteractionsMap = createEdgeMap();
        numDisulfidesMap = createEdgeMap();
        proteinNodeMap = createVertexMap();
        chainNamesInEdge = createEdgeMap();
        numSSEContacts = new HashMap();

        lastColorStep = 0;
        neglectedEdges = 0;
    }

    public Vertex getVertexFromChain(String chainID) {
        Iterator<Vertex> vertIter = this.getVertices().iterator();
        Vertex nextVert;
        while (vertIter.hasNext()) {
            nextVert = vertIter.next();
            if (ComplexGraph.this.proteinNodeMap.get(nextVert).equals(chainID)) {
                return nextVert;
            }
        }
        return null;
    }

    public String getPDBID() {
        return this.pdbid;
    }

    public Boolean chainsHaveEnoughContacts(Integer A, Integer B) {
        if (this.numChainInteractions[A][B] != null) {
            if (this.numChainInteractions[A][B] >= Main.chainComplexGraphContactThreshold) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

// Work here..
// TODO : get Chain name... somehow
    public boolean writeSSEComplexContactInfoToDB(String pdbid) {

    //DBManager.writeSSEComplexContactToDB()
        String chainA;
        String chainB;

        Integer numHelixHelixInteractions;
        Integer numHelixStrandInteractions;
        Integer numHelixLoopInteractions;
        Integer numSSInteractions;
        Integer numStrandLoopInteractions;
        Integer numLoopLoopInteractions;
        Integer numAllInteractions;
        Integer numDisulfides;

        for (Map.Entry pair : this.numSSEContacts.entrySet()) {

            List curSSEs = (List<Integer>) pair.getKey();
            Integer sse1_dssp_start = (Integer)curSSEs.get(0);
            Integer sse2_dssp_start = (Integer)curSSEs.get(1);
            Integer contactCount = (Integer)pair.getValue();
            
            // TESTING!!!! remove
            chainA = "A";
            chainB = "B";
          
            try {
                DBManager.writeSSEComplexContactToDB(pdbid, chainA, chainB, sse1_dssp_start, sse2_dssp_start, contactCount);
                //it.remove(); // avoids a ConcurrentModificationException
            } catch (SQLException ex) {
                Logger.getLogger(ComplexGraph.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }

    /**
     * Separates interaction maps and associates them with a chain name then
     * calls DBManager.writeComplexContactToDB()
     *
     * @param pdbid PDB Identifier
     * @return true if DBManager is succesfull
     */
    public boolean writeChainComplexContactInfoToDB(String pdbid) {

        String chainA;
        String chainB;

        Integer numHelixHelixInteractions;
        Integer numHelixStrandInteractions;
        Integer numHelixLoopInteractions;
        Integer numSSInteractions;
        Integer numStrandLoopInteractions;
        Integer numLoopLoopInteractions;
        Integer numAllInteractions;
        Integer numDisulfides;

        for (Map.Entry pair : this.chainNamesInEdge.entrySet()) {

            Edge curEdge = (Edge) pair.getKey();
            String[] chainPair = (String[]) pair.getValue();

            chainA = chainPair[0];
            chainB = chainPair[1];

            numHelixHelixInteractions = this.numHelixHelixInteractionsMap.get(curEdge);
            numHelixStrandInteractions = this.numHelixStrandInteractionsMap.get(curEdge);
            numHelixLoopInteractions = this.numHelixLoopInteractionsMap.get(curEdge);
            numSSInteractions = this.numSSInteractionsMap.get(curEdge);
            numStrandLoopInteractions = this.numStrandLoopInteractionsMap.get(curEdge);
            numLoopLoopInteractions = this.numLoopLoopInteractionsMap.get(curEdge);
            numDisulfides = this.numDisulfidesMap.get(curEdge);
            numAllInteractions = this.numAllInteractionsMap.get(curEdge);

            Integer[] interactionNums = {numHelixHelixInteractions, numHelixStrandInteractions, numHelixLoopInteractions,
                numSSInteractions, numStrandLoopInteractions, numLoopLoopInteractions,
                numDisulfides, numAllInteractions};

            // make sure no entry is null or something shitty
            for (int i = 0; i < interactionNums.length; i++) {
                if (interactionNums[i] == null) {
                    interactionNums[i] = 0;
                }
            }

            try {
                DBManager.writeChainComplexContactToDB(pdbid, chainA, chainB, interactionNums);
                //it.remove(); // avoids a ConcurrentModificationException
            } catch (SQLException ex) {
                Logger.getLogger(ComplexGraph.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }

    private float getUniqueColor(Integer numVertices) {
        float step = 360 / (numVertices + 1); // +1 to avoid double red
        float hue = (lastColorStep + step) / 360;
        lastColorStep += step;
        return hue;
    }

    private static DrawResult drawProteinGraphG2D(Boolean nonProteinGraph, ComplexGraph cg) {

        Integer numVerts = cg.getVertices().size();

        Boolean bw = nonProteinGraph;

    // All these values are in pixels
        // page setup
        PageLayout pl = new PageLayout(numVerts);
        Position2D vertStart = pl.getVertStart();

    // ------------------------- Prepare stuff -------------------------
        // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
        //BufferedImage bi = new BufferedImage(pl.getPageWidth(), pl.getPageHeight(), BufferedImage.TYPE_INT_ARGB);
        SVGGraphics2D ig2;

        //if(Settings.get("plcc_S_img_output_format").equals("SVG")) {                    
        // Apache Batik SVG library, using W3C DOM tree implementation
        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        // Create an instance of the SVG Generator.
        ig2 = new SVGGraphics2D(document);
        //ig2.getRoot(document.getDocumentElement());
        // }
        //else {
        //    ig2 = (SVGGraphics2D)bi.createGraphics();
        //}

        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // make background white
        ig2.setPaint(Color.WHITE);
        ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
        ig2.setPaint(Color.BLACK);

    //pl.drawAreaOutlines(ig2);
        // prepare font
        Font font = pl.getStandardFont();
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();

    // ------------------------- Draw header -------------------------
        // check width of header string
        String proteinHeader = "The chain complex graph of PDB entry " + cg.pdbid + " [V=" + cg.getVertices().size() + ", E=" + cg.getEdges() + "].";
        String addInfo = "(Interchain contact threshold is set to " + Main.chainComplexGraphContactThreshold + ". Neglected edges: " + cg.neglectedEdges + ")";
        //Integer stringWidth = fontMetrics.stringWidth(proteinHeader);       // Should be around 300px for the text above
        Integer stringHeight = fontMetrics.getAscent();
        String sseNumberSeq;    // the SSE number in the primary structure, N to C terminus
        String sseNumberGraph;  // the SSE number in this graph, 1..(this.size)

        if (Settings.getBoolean("plcc_B_graphimg_header")) {
            ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
            //i2.drawString(addInfo, pl.headerStart.x, pl.headerStart.y + pl.textLineHeight);
        }

    // ------------------------- Draw the graph -------------------------
        // Draw the edges as arcs
        java.awt.Shape shape;
        Arc2D.Double arc;
        ig2.setStroke(new BasicStroke(2));  // thin edges
        Integer edgeType, leftVert, rightVert, leftVertPosX, rightVertPosX, arcWidth, arcHeight, arcTopLeftX, arcTopLeftY, spacerX, spacerY, iChainID, jChainID;
        Integer labelPosX, labelPosY;

        String edges = cg.getEdges().toString();
        for (Integer i = 0; i < cg.getVertices().size(); i++) {
            for (Integer j = i + 1; j < cg.getVertices().size(); j++) {

                String tmp = "(" + i + ", " + j + ")";

                // If there is a contact...
                if (edges.indexOf(tmp) != -1) {

                    Integer cInteractions = cg.numChainInteractions[i][j];

                // determine edge type and the resulting color
                    //edgeType = cg.getContactType(i, j);
                    ig2.setPaint(Color.RED);
                    if (bw) {
                        ig2.setPaint(Color.LIGHT_GRAY);
                    }      // for non-protein graphs

                // ----- complex graph specific stuff -----
                    // determine chain of SSEs
                    iChainID = -1;
                    jChainID = -1;
                    /*
                     for(Integer x = 0; x < cg.chainEnd.size(); x++){
                     if(i < cg.chainEnd.get(x)) {iChainID = x; break;}
                     }
                     for(Integer x = 0; x < cg.chainEnd.size(); x++){
                     if(j < cg.chainEnd.get(x)) {jChainID = x; break;}
                     }
                     if (!Objects.equals(iChainID, jChainID)) {ig2.setPaint(Color.PINK);}
                
                     */
                // ----- end complex graph specific stuff -----

                    // determine the center of the arc and the width of its rectangle bounding box
                    if (i < j) {
                        leftVert = i;
                        rightVert = j;
                    } else {
                        leftVert = j;
                        rightVert = i;
                    }

                    leftVertPosX = pl.getVertStart().x + (leftVert * pl.vertDist);
                    rightVertPosX = pl.getVertStart().x + (rightVert * pl.vertDist);

                    arcWidth = rightVertPosX - leftVertPosX;
                    arcHeight = arcWidth / 2;

                    arcTopLeftX = leftVertPosX;
                    arcTopLeftY = pl.getVertStart().y - arcHeight / 2;

                    spacerX = pl.vertRadius;
                    spacerY = 0;

                    // draw it                                                
                    arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
                    shape = ig2.getStroke().createStrokedShape(arc);
                    ig2.fill(shape);

                }
            }
        }

        // draw arc labels on top to prevent unreadability 
        for (Integer i = 0; i < cg.getVertices().size(); i++) {
            for (Integer j = i + 1; j < cg.getVertices().size(); j++) {

                String tmp = "(" + i + ", " + j + ")";

                // If there is a contact...
                if (edges.indexOf(tmp) != -1) {
                    iChainID = -1;
                    jChainID = -1;
                    if (i < j) {
                        leftVert = i;
                        rightVert = j;
                    } else {
                        leftVert = j;
                        rightVert = i;
                    }

                    // TODO: is it clever to calculate everything again?
                    leftVertPosX = pl.getVertStart().x + (leftVert * pl.vertDist);
                    rightVertPosX = pl.getVertStart().x + (rightVert * pl.vertDist);

                    arcWidth = rightVertPosX - leftVertPosX;
                    arcHeight = arcWidth / 2;

                    arcTopLeftX = leftVertPosX;
                    arcTopLeftY = pl.getVertStart().y - arcHeight / 2;

                    spacerX = pl.vertRadius;
                    spacerY = 0;
                    //calculate label positions
                    labelPosX = leftVertPosX + arcWidth / 2 + 2;
                    labelPosY = arcTopLeftY + spacerY - 5;
                    //draw labels on arcs
                    Font labelfont = new Font(Settings.get("plcc_S_img_default_font"), Font.PLAIN, Settings.getInteger("plcc_I_img_default_font_size") - 5);
                    ig2.setFont(labelfont);
                    ig2.setPaint(Color.BLACK);
                    Integer cInteractions = cg.numChainInteractions[i][j];
                    ig2.drawString(cInteractions.toString(), labelPosX, labelPosY + (stringHeight / 4));

                }
            }
        }

        ig2.setFont(font);
        // Draw the vertices as circles
        Ellipse2D.Double circle;
        Rectangle2D.Double rect;
        ig2.setStroke(new BasicStroke(2));

        boolean colorSet = false;
        cg.savedVertexColors = new float[cg.getVertices().size()];
        for (Integer i = 0; i < cg.getVertices().size(); i++) {
            // set standard color
            ig2.setPaint(Color.GRAY);
            if (bw) {
                ig2.setPaint(Color.GRAY);
            }      // for non-protein graphs

            // set hue, saturation, brighness
            float h = (float) 0.5;
            float s = (float) 1.0;
            float b = (float) 1.0;

            for (Integer j = 0; j < cg.getVertices().size(); j++) {

                // if chain has an homologue partner...
                if (cg.homologueChains[i][j] != null) {
                    if (cg.homologueChains[i][j] == 1) {
                        // if homologue partner wasn't colored before..
                        if (cg.savedVertexColors[i] == 0) {
                            h = cg.getUniqueColor(numVerts); //get unique color
                            cg.savedVertexColors[i] = h;
                            cg.savedVertexColors[j] = h;

                        } else {
                            h = cg.savedVertexColors[i];
                        }
                        ig2.setPaint(Color.getHSBColor(h, s, b));
                        colorSet = true;
                        //cg.homologueChains[j][i] = 0; // otherwise every vertices gets a different color. OR: don't create a symmetrical matrix?!
                    }
                }
            }

            // if no homologue chains occur
            if (!colorSet) {
                h = cg.getUniqueColor(numVerts); //get unique color
                cg.savedVertexColors[i] = h;
                ig2.setPaint(Color.getHSBColor(h, s, b));
            }
        // pick color depending on SSE type

            // draw a shape based on SSE type
            rect = new Rectangle2D.Double(vertStart.x + (i * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
            ig2.fill(rect);

        }

        // Draw the markers for the N-terminus and C-terminus if there are any vertices in this graph            
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);

        /*
         if( ! bw) {
         if(cg.getVertices().size() > 0) {                    
         ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);    // N terminus label
         ig2.drawString("C", vertStart.x + cg.getVertices().size() * pl.vertDist, vertStart.y + 20);  // C terminus label
         }
         }
         */
    // ************************************* footer **************************************
        if (Settings.getBoolean("plcc_B_graphimg_footer")) {

        // Draw the vertex numbering into the footer
            // Determine the dist between vertices that will have their vertex number printed below them in the footer field
            Integer printNth = 1;
            if (cg.getVertices().size() > 9) {
                printNth = 1;
            }
            if (cg.getVertices().size() > 99) {
                printNth = 2;
            }

            // line markers: S for sequence order, G for graph order
            Integer lineHeight = pl.textLineHeight;
            if (cg.getVertices().size() > 0) {
                ig2.drawString("C#", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
            } else {
                ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
            }
            iChainID = -1;
            String edgesString = cg.proteinNodeMap.toString();

            for (Integer i = 0; i < cg.getVertices().size(); i++) {
                ig2.drawString("CN", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                // Draw label for every nth vertex
                if ((i + 1) % printNth == 0) {
                    sseNumberGraph = "" + (i + 1);
                    //sseNumberSeq = "" + (cg.proteinNodeMap.get(i));
                    Integer foundIndex = edgesString.indexOf(i.toString() + "=");
                    String chainId;
                    if (i < 10) {
                        chainId = edgesString.substring(foundIndex + 2, foundIndex + 3);
                    } else {
                        chainId = edgesString.substring(foundIndex + 3, foundIndex + 4);
                    }

                    sseNumberSeq = "" + chainId;
                    //stringWidth = fontMetrics.stringWidth(sseNumberSeq);
                    stringHeight = fontMetrics.getAscent();

                    ig2.drawString(sseNumberGraph, pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.drawString(sseNumberSeq, pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));

                // determine chain of SSEs
                /*for(Integer x = 0; x < cg.getVertices().size(); x++){
                     if(i < cg.chainEnd.get(x)) {iChainID = x; break;}
                     }
                     */
                    //if(iChainID != -1) {ig2.drawString(cg.allChains.get(iChainID).getPdbChainID(), pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight * 2) + (stringHeight / 4));}
                }
            }

            /*
             if(Settings.getBoolean("plcc_B_graphimg_legend")) {
             if(iChainID != -1){
             SSEGraph.drawLegend(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + lineHeight * 3 + (stringHeight / 4)), pl, pg);
             }
             else{
             SSEGraph.drawLegend(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + lineHeight * 2 + (stringHeight / 4)), pl, pg);
             }
             }
             */
        }

    // all done, write the image to disk
        //if(Settings.get("plcc_S_img_output_format").equals("SVG")) {
    //boolean useCSS = true;
        //FileOutputStream fos = new FileOutputStream(new File("/tmp/mySVG.svg"));
        //Writer out = new OutputStreamWriter(fos, "UTF-8");
        //ig2.stream(out, useCSS); 
        Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());

        DrawResult drawRes = new DrawResult(ig2, roi);
        return drawRes;

    }

    /**
     * Draw a complex graph to an image file in the requested format.
     *
     * @param baseFilePathNoExt the img base file name, no file extension
     * @param drawBlackAndWhite whether to draw in grayscale only
     * @param formats a list of img formats to write
     * @param cg the complex graph to draw
     * @return a list of file names that were written to disk, (as a map of
     * formats to file names)
     */
    public static HashMap<DrawTools.IMAGEFORMAT, String> drawComplexGraph(String baseFilePathNoExt, Boolean drawBlackAndWhite, DrawTools.IMAGEFORMAT[] formats, ComplexGraph cg) {

        DrawResult drawRes = ComplexGraph.drawProteinGraphG2D(drawBlackAndWhite, cg);

        //System.out.println("drawProteinGraph: Basefilepath is '" + baseFilePathNoExt + "'.");
        String svgFilePath = baseFilePathNoExt + ".svg";
        HashMap<DrawTools.IMAGEFORMAT, String> resultFilesByFormat = new HashMap<DrawTools.IMAGEFORMAT, String>();
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(DrawTools.IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write protein graph file : '" + ex.getMessage() + "'.");
        }

        if (!Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("      Output complex graph files: ");
            for (DrawTools.IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                String ffile = new File(resultFilesByFormat.get(format)).getName();
                sb.append("(").append(format.toString()).append(" => ").append(ffile).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }

    /**
     * Writes this complex graph to the file 'file' in GML format. Note that
     * this function will overwrite the file if it exists.
     *
     * @param file the target file. Has to be writable.
     * @return true if the file was written, false otherwise
     */
    public boolean writeToFileGML(File file) {

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                System.err.println("ERROR: Could not create file '" + file.getAbsolutePath() + "': " + ex.getMessage() + ".");
                return false;
            }
        }

        GMLWriter<ComplexGraph.Vertex, ComplexGraph.Edge> gw = new GMLWriter<ComplexGraph.Vertex, ComplexGraph.Edge>(this);
        gw.addVertexAttrWriter(new GMLWriter.AttrWriter<Vertex>() {
            @Override
            public String getAttribute() {
                return "label";
            }

            @Override
            public boolean hasValue(Vertex o) {
                return proteinNodeMap.containsKey(o);
            }

            @Override
            public String write(Vertex o) {
                return '"' + proteinNodeMap.get(o).toString() + '"';
            }
        });

        //gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>());
        /**
         * Overwrite the edge attribute writer for the labels. This has to be
         * done because the labels need to be enclosed in closing quotation
         * marks to mark them as strings (due to the GML format definition).
         */
        gw.addEdgeAttrWriter(new GMLWriter.AttrWriter<Edge>() {
            @Override
            public String getAttribute() {
                return "label";
            }

            @Override
            public boolean hasValue(Edge e) {
                return numAllInteractionsMap.containsKey(e);
            }

            @Override
            public String write(Edge e) {
                return '"' + numAllInteractionsMap.get(e).toString() + '"';
            }

        });

        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_HH_contacts", numHelixHelixInteractionsMap));
        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_HS_contacts", numHelixStrandInteractionsMap));
        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_HL_contacts", numHelixLoopInteractionsMap));
        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_SS_contacts", numSSInteractionsMap));
        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_SL_contacts", numStrandLoopInteractionsMap));
        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_LL_contacts", numLoopLoopInteractionsMap));

        FileOutputStream fop = null;
        boolean allOK = true;
        try {
            fop = new FileOutputStream(file);
            gw.write(fop);
            fop.flush();
            fop.close();
        } catch (Exception e) {
            System.err.println("ERROR: Could not write complex graph to file '" + file.getAbsolutePath() + "': " + e.getMessage() + ".");
            allOK = false;
        } finally {
            if (fop != null) {
                try {
                    fop.close();
                } catch (Exception e) {
                    // nvm
                }
            }

        }
        return allOK;
    }
}
