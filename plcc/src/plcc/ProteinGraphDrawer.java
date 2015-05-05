/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.io.IOUtils;
import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import tools.DP;

/**
 *
 * @author spirit
 */
public class ProteinGraphDrawer {

    /**
     * Draws the SEQ legend for the graph at the given position. This legend is not suitable for other folding graph notations but SEQ, because their edges are different.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position (x, y) where to start drawing
     * @return the x coordinate in the image where the legend ends (which is the left margin + the legend width).
     * This can be used to determine the minimal width of the total image (it has to be at least this value).
     */
    public static Integer drawLegendSEQ(SVGGraphics2D ig2, Position2D startPos, PageLayout pl, SSEGraph g) {
        Boolean drawAll = Settings.getBoolean("plcc_B_graphimg_legend_always_all");
        ig2.setFont(pl.getLegendFont());
        FontMetrics fontMetrics = ig2.getFontMetrics();
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        Integer spacer = 10;
        Integer pixelPosX = startPos.x;
        Integer vertWidth = pl.getVertDiameter();
        Integer vertOffset = pl.getVertDiameter() / 4 * 3;
        String label;
        label = "[Edges: ";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
        label = "seq_distance";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
        label = "]";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
        if (g.numVertices() > 0) {
            label = " [Vertices: ";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeHelix() || drawAll) {
            ProteinGraphDrawer.drawSymbolAlphaHelix(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "helix";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeBetaStrand() || drawAll) {
            ProteinGraphDrawer.drawSymbolBetaStrand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "strand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeLigand() || drawAll) {
            ProteinGraphDrawer.drawSymbolLigand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "ligand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeOther() || drawAll) {
            ProteinGraphDrawer.drawSymbolOtherSSE(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "other";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.numVertices() > 0) {
            label = "]";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        return pixelPosX;
    }

    /**
     * Draws the ADJ folding graph image of this graph and returns the DrawResult.
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     *
     * @param pnfr a folding graph notation result
     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawFoldingGraphADJG2D(PTGLNotationFoldResult pnfr) {
        FoldingGraph fg = pnfr.getFoldingGraph();
        SSEGraph pg = fg.parent;
        Integer startVertexInParent = fg.getMinimalVertexIndexInParentGraph();
        Integer endVertexInParent = fg.getMaximalVertexIndexInParentGraph();
        Integer shiftBack = startVertexInParent;
        List<Integer> fgVertexPosInParent = fg.getVertexIndexListInParentGraph();
        List<Integer> parentVertexPosInFG = pg.computeParentGraphVertexPositionsInFoldingGraph(fgVertexPosInParent, pg.size);
        Integer numVerts = endVertexInParent - startVertexInParent + 1;
        Boolean bw = false;
        PageLayout pl = new PageLayout(numVerts);
        Position2D vertStart = pl.getVertStart();
        Integer lineHeight = pl.textLineHeight;
        SVGGraphics2D ig2;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        ig2 = new SVGGraphics2D(document);
        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ig2.setPaint(Color.WHITE);
        ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
        ig2.setPaint(Color.BLACK);
        Font font = pl.getStandardFont();
        Font fontBold = pl.getStandardFontBold();
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();
        String proteinHeader = "The ADJ " + pg.graphType + " folding graph " + fg.getFoldingGraphFoldName() + " (# " + fg.getFoldingGraphNumber() + ") of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + fg.numVertices() + ", E=" + fg.numSSEContacts() + "].";
        String notation = "ADJ notation: '" + pnfr.adjNotation + "'";
        Integer stringHeight = fontMetrics.getAscent();
        String sseNumberSeq;
        String sseNumberFoldingGraph;
        String sseNumberProteinGraph;
        if (Settings.getBoolean("plcc_B_graphimg_header")) {
            ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
            if (Settings.getBoolean("plcc_B_print_notations_on_fg_images")) {
                ig2.drawString(notation, pl.headerStart.x, pl.headerStart.y + lineHeight);
            }
        }
        Integer k;
        Integer l;
        Shape shape;
        Arc2D.Double arc;
        ig2.setStroke(new BasicStroke(2));
        Integer edgeType;
        Integer leftVert;
        Integer rightVert;
        Integer leftVertPosX;
        Integer rightVertPosX;
        Integer arcWidth;
        Integer arcHeight;
        Integer arcTopLeftX;
        Integer arcTopLeftY;
        Integer spacerX;
        Integer spacerY;
        for (Integer i = startVertexInParent; i <= endVertexInParent; i++) {
            for (Integer j = i + 1; j <= endVertexInParent; j++) {
                k = parentVertexPosInFG.get(i);
                l = parentVertexPosInFG.get(j);
                if (k < 0 || l < 0) {
                    continue;
                }
                if (fg.containsEdge(k, l)) {
                    edgeType = fg.getContactType(k, l);
                    if (edgeType.equals(SpatRel.PARALLEL)) {
                        ig2.setPaint(Color.RED);
                    } else if (edgeType.equals(SpatRel.ANTIPARALLEL)) {
                        ig2.setPaint(Color.BLUE);
                    } else if (edgeType.equals(SpatRel.MIXED)) {
                        ig2.setPaint(Color.GREEN);
                    } else if (edgeType.equals(SpatRel.LIGAND)) {
                        ig2.setPaint(Color.MAGENTA);
                    } else if (edgeType.equals(SpatRel.BACKBONE)) {
                        ig2.setPaint(Color.ORANGE);
                    } else {
                        ig2.setPaint(Color.LIGHT_GRAY);
                    }
                    if (bw) {
                        ig2.setPaint(Color.LIGHT_GRAY);
                    }
                    if (i < j) {
                        leftVert = i;
                        rightVert = j;
                    } else {
                        leftVert = j;
                        rightVert = i;
                    }
                    leftVertPosX = pl.getVertStart().x + ((leftVert - shiftBack) * pl.vertDist);
                    rightVertPosX = pl.getVertStart().x + ((rightVert - shiftBack) * pl.vertDist);
                    arcWidth = rightVertPosX - leftVertPosX;
                    arcHeight = arcWidth / 2;
                    arcTopLeftX = leftVertPosX;
                    arcTopLeftY = pl.getVertStart().y - arcHeight / 2;
                    spacerX = pl.vertRadius;
                    spacerY = 0;
                    arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
                    shape = ig2.getStroke().createStrokedShape(arc);
                    ig2.fill(shape);
                }
            }
        }
        Ellipse2D.Double circle;
        Rectangle2D.Double rect;
        ig2.setStroke(new BasicStroke(2));
        for (Integer i = startVertexInParent; i <= endVertexInParent; i++) {
            if (pg.sseList.get(i).isHelix()) {
                ig2.setPaint(Color.RED);
            } else if (pg.sseList.get(i).isBetaStrand()) {
                ig2.setPaint(Color.BLACK);
            } else if (pg.sseList.get(i).isLigandSSE()) {
                ig2.setPaint(Color.MAGENTA);
            } else if (pg.sseList.get(i).isOtherSSE()) {
                ig2.setPaint(Color.GRAY);
            } else {
                ig2.setPaint(Color.LIGHT_GRAY);
            }
            Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
            if (parentVertexPosInFoldingGraph < 0) {
                ig2.setPaint(Color.LIGHT_GRAY);
            }
            if (bw) {
                ig2.setPaint(Color.GRAY);
            }
            SSE sse = pg.getVertex(i);
            if (sse.isBetaStrand()) {
                rect = new Rectangle2D.Double(vertStart.x + ((i - shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.fill(rect);
            } else if (sse.isLigandSSE()) {
                circle = new Ellipse2D.Double(vertStart.x + ((i - shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.setStroke(new BasicStroke(3));
                ig2.draw(circle);
                ig2.setStroke(new BasicStroke(2));
            } else {
                circle = new Ellipse2D.Double(vertStart.x + ((i - shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.fill(circle);
            }
        }
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        if (!bw) {
            if (fg.getSize() > 0) {
                ig2.setFont(fontBold);
                ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);
                ig2.drawString("C", vertStart.x + numVerts * pl.vertDist, vertStart.y + 20);
                ig2.setFont(font);
            }
        }
        if (Settings.getBoolean("plcc_B_graphimg_footer")) {
            Integer printNth = 1;
            if (fg.getSize() > 99) {
                printNth = 2;
            }
            if (fg.getSize() > 999) {
                printNth = 3;
            }
            if (fg.getSize() > 0) {
                ig2.drawString("FG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                ig2.setPaint(Color.LIGHT_GRAY);
                ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + lineHeight + (stringHeight / 4));
                ig2.setPaint(Color.BLACK);
            } else {
                ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
            }
            for (Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                if ((i + 1) % printNth == 0) {
                    sseNumberFoldingGraph = "" + (parentVertexPosInFG.get(i) >= 0 ? (parentVertexPosInFG.get(i) + 1) : "");
                    sseNumberSeq = "" + (pg.sseList.get(i).getSSESeqChainNum());
                    sseNumberProteinGraph = "" + (i + 1);
                    stringHeight = fontMetrics.getAscent();
                    ig2.setPaint(Color.BLACK);
                    ig2.drawString(sseNumberFoldingGraph, pl.getFooterStart().x + ((i - shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.setPaint(Color.LIGHT_GRAY);
                    ig2.drawString(sseNumberSeq, pl.getFooterStart().x + ((i - shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                    ig2.drawString(sseNumberProteinGraph, pl.getFooterStart().x + ((i - shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight * 2) + (stringHeight / 4));
                    ig2.setPaint(Color.BLACK);
                }
            }
            if (Settings.getBoolean("plcc_B_graphimg_legend")) {
                ProteinGraphDrawer.drawLegendDEFADJ(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + (lineHeight * 3) + (stringHeight / 4)), pl, fg, false);
            }
        }
        Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
        DrawResult drawRes = new DrawResult(ig2, roi);
        return drawRes;
    }

    /**
     * Draws a protein graph in all formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param drawBlackAndWhite whether to omit colors, only useful for non-protein graphs
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<DrawTools.IMAGEFORMAT, String> drawProteinGraph(String baseFilePathNoExt, Boolean drawBlackAndWhite, DrawTools.IMAGEFORMAT[] formats, ProtGraph pg) {
        DrawResult drawRes = ProteinGraphDrawer.drawProteinGraphG2D(drawBlackAndWhite, pg);
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
            sb.append("      Output protein graph files: ");
            for (DrawTools.IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }

    /**
     * Draws a SEQ folding graph in all formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param drawBlackAndWhite whether to omit colors, only useful for non-protein graphs
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @param pnfr
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<DrawTools.IMAGEFORMAT, String> drawFoldingGraphSEQ(String baseFilePathNoExt, Boolean drawBlackAndWhite, DrawTools.IMAGEFORMAT[] formats, PTGLNotationFoldResult pnfr) {
        DrawResult drawRes = ProteinGraphDrawer.drawFoldingGraphSEQG2D(pnfr);
        String svgFilePath = baseFilePathNoExt + ".svg";
        HashMap<DrawTools.IMAGEFORMAT, String> resultFilesByFormat = new HashMap<DrawTools.IMAGEFORMAT, String>();
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(DrawTools.IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write SEQ folding graph file : '" + ex.getMessage() + "'.");
        }
        if (!Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("          Output SEQ folding graph files: ");
            for (DrawTools.IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }

    /**
     * Draws a RED folding graph in all formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param drawBlackAndWhite whether to omit colors, only useful for non-protein graphs
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @param pnfr
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<DrawTools.IMAGEFORMAT, String> drawFoldingGraphRED(String baseFilePathNoExt, Boolean drawBlackAndWhite, DrawTools.IMAGEFORMAT[] formats, PTGLNotationFoldResult pnfr) {
        DrawResult drawRes = ProteinGraphDrawer.drawFoldingGraphREDG2D(pnfr);
        String svgFilePath = baseFilePathNoExt + ".svg";
        HashMap<DrawTools.IMAGEFORMAT, String> resultFilesByFormat = new HashMap<DrawTools.IMAGEFORMAT, String>();
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(DrawTools.IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write RED folding graph file : '" + ex.getMessage() + "'.");
        }
        if (!Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("          Output RED folding graph files: ");
            for (DrawTools.IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }

    /**
     * Draws the KEY legend for the graph at the given position. This legend is not suitable for other folding graph notations but KEY, because their edges and SSE symbols are different.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position (x, y) where to start drawing
     * @return the x coordinate in the image where the legend ends (which is the left margin + the legend width).
     * This can be used to determine the minimal width of the total image (it has to be at least this value).
     */
    public static Integer drawLegendKEY(SVGGraphics2D ig2, Position2D startPos, PageLayout pl, SSEGraph g) {
        Boolean drawAll = Settings.getBoolean("plcc_B_graphimg_legend_always_all");
        ig2.setFont(pl.getLegendFont());
        FontMetrics fontMetrics = ig2.getFontMetrics();
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        Integer spacer = 10;
        Integer pixelPosX = startPos.x;
        Integer vertWidth = pl.getVertDiameter();
        Integer vertOffset = pl.getVertDiameter() / 4 * 3;
        String label;
        label = "[Edges: ";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
        label = "sequential";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
        label = "]";
        ig2.setPaint(Color.BLACK);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
        ig2.setPaint(Color.BLACK);
        if (g.numVertices() > 0) {
            label = " [Vertices: ";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeHelix() || drawAll) {
            label = "helix";
            ig2.setPaint(Color.RED);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeBetaStrand() || drawAll) {
            label = "strand";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeLigand() || drawAll) {
            ig2.setPaint(Color.MAGENTA);
            label = "ligand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeOther() || drawAll) {
            ig2.setPaint(Color.GRAY);
            label = "other";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.numVertices() > 0) {
            label = "]";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        return pixelPosX;
    }

    /**
     * Draws the KEY folding graph image of this graph and returns the DrawResult.
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     *
     * See http://ptgl.uni-frankfurt.de/cgi-bin/showpict.pl?topology=a&rep=3&protlist=7timA+7timB+&nmrlist=
     * and http://ptgl.uni-frankfurt.de/ptglhelp.html#key
     *
     * See the KEY beta FG #3 of 1GOS chain A as an example.
     *
     * @param pnfr a folding graph notation result
     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawFoldingGraphKEYG2DOld(PTGLNotationFoldResult pnfr) {
        FoldingGraph fg = pnfr.getFoldingGraph();
        SSEGraph pg = fg.parent;
        Boolean debugVerbose = false;
        if (fg.isBifurcated()) {
            DP.getInstance().e("SSEGraph", "drawFoldingGraphKEYG2D: This FG is bifurcated, KEY notation not supported.");
            return null;
        }
        Integer leftMostVertexInParent = fg.getMinimalVertexIndexInParentGraph();
        Integer rightMostVertexInParent = fg.getMaximalVertexIndexInParentGraph();
        List<Integer> keyposParentIndicesSeqOrder = pnfr.keypos;
        Integer keystartFGIndex = pnfr.keyStartFG;
        if (keystartFGIndex == null) {
            DP.getInstance().e("SSEGraph", "drawFoldingGraphKEYG2D: keystartFGIndex is null");
        }
        fg.computeSpatialVertexOrdering();
        ArrayList<Integer> keyposFGIndicesSpatOrder = fg.spatOrder;
        if (keyposFGIndicesSpatOrder.size() != fg.getSize()) {
            if (fg.isASingleCycle()) {
                keyposFGIndicesSpatOrder = fg.getSpatialOrderingOfVertexIndicesForSingleCycleFG(keystartFGIndex);
                fg.spatOrder = keyposFGIndicesSpatOrder;
                if (keyposFGIndicesSpatOrder.size() != fg.getSize()) {
                    DP.getInstance().e("SSEGraph", "Could not draw KEY notation: Spatorder size " + keyposFGIndicesSpatOrder.size() + " of chain " + fg.chainid + " gt " + fg.graphType + " FG " + fg.getFoldingGraphFoldName() + " (#" + fg.getFoldingGraphNumber() + ") does not match FG size " + fg.getSize() + " even when allowing circles. Parent verts of FG: " + IO.intListToString(fg.getVertexIndexListInParentGraph()) + ". KEY='" + pnfr.keyNotation + "'. " + (fg.isASingleCycle() ? "FG is a single cycle." : "FG is NOT a single cycle."));
                    return null;
                } else {
                    //System.err.println("Single cycle handling fixed it.");
                }
            } else {
                DP.getInstance().e("SSEGraph", "Could not draw KEY notation: Spatorder size " + keyposFGIndicesSpatOrder.size() + " of chain " + fg.chainid + " gt " + fg.graphType + " FG " + fg.getFoldingGraphFoldName() + " (#" + fg.getFoldingGraphNumber() + ") does not match FG size " + fg.getSize() + ". Parent verts of FG: " + IO.intListToString(fg.getVertexIndexListInParentGraph()) + ". KEY='" + pnfr.keyNotation + "'. " + (fg.isASingleCycle() ? "FG is a single cycle." : "FG is NOT a single cycle."));
                return null;
            }
        }
        if (debugVerbose) {
            DP.getInstance().d("SSEGraph", fg.getQuickIDString() + " Spatorder size " + keyposFGIndicesSpatOrder.size() + " of chain " + fg.chainid + " gt " + fg.graphType + " FG " + fg.getFoldingGraphFoldName() + " (#" + fg.getFoldingGraphNumber() + "). FG size " + fg.getSize() + ". Parent verts of FG: " + IO.intListToString(fg.getVertexIndexListInParentGraph()) + ". KEY='" + pnfr.keyNotation + "'.");
        }
        List<Integer> spatOrderseqDistToPrevious = new ArrayList<>();
        spatOrderseqDistToPrevious.add(null);
        Integer cur;
        Integer last;
        Integer spatPosCur;
        Integer spatPosLast;
        for (Integer i = 1; i < fg.getSize(); i++) {
            cur = i;
            last = i - 1;
            spatPosCur = keyposFGIndicesSpatOrder.indexOf(cur);
            spatPosLast = keyposFGIndicesSpatOrder.indexOf(last);
            spatOrderseqDistToPrevious.add(spatPosCur - spatPosLast);
        }
        List<Integer> fgVertexPosInParent = fg.getVertexIndexListInParentGraph();
        List<Integer> parentVertexPosInFG = ProtGraph.computeParentGraphVertexPositionsInFoldingGraph(fgVertexPosInParent, pg.size);
        Integer numVerts = fg.getSize();
        Boolean bw = false;
        PageLayout pl = new PageLayout(numVerts);
        pl.isForKEY = true;
        Position2D vertStart = pl.getVertStart();
        Integer lineHeight = pl.textLineHeight;
        Integer vertDist = 50;
        Integer vertHeight = 80;
        Integer vertWidth = 40;
        Integer vertStartX = pl.getVertStart().x;
        Integer vertStartY = pl.getVertStart().y;
        SVGGraphics2D ig2;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        ig2 = new SVGGraphics2D(document);
        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ig2.setPaint(Color.WHITE);
        ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
        ig2.setPaint(Color.BLACK);
        Font font = pl.getStandardFont();
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();
        String proteinHeader = "The KEY " + pg.graphType + " folding graph " + fg.getFoldingGraphFoldName() + " (# " + fg.getFoldingGraphNumber() + ") of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + fg.numVertices() + ", E=" + fg.numSSEContacts() + "].";
        String notation = "KEY notation: '" + pnfr.keyNotation + "'";
        Integer stringHeight = fontMetrics.getAscent();
        String sseNumberSeq;
        String sseNumberFoldingGraph;
        String sseNumberProteinGraph;
        if (Settings.getBoolean("plcc_B_graphimg_header")) {
            ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
            ig2.drawString(notation, pl.headerStart.x, pl.headerStart.y + lineHeight);
        }
        Integer[] shiftBack = new Integer[pg.size];
        Integer shift = 0;
        for (int i = 0; i < parentVertexPosInFG.size(); i++) {
            if (parentVertexPosInFG.get(i) < 0) {
                shift++;
            }
            shiftBack[i] = shift;
        }
        boolean debugDrawingKEY = false;
        //if(fg.toShortString().equals("1gos-B-alpha-FG0[4V,3E]")) {
        //    debugDrawingKEY = true;
        //}
        Integer[] orientationsSpatOrder = new Integer[fg.size];
        Integer[] orientationsSeqOrder = new Integer[fg.size];
        List<Integer> keyposFGIndices = new ArrayList<>();
        Integer v;
        for (int i = 0; i < keyposParentIndicesSeqOrder.size(); i++) {
            v = parentVertexPosInFG.get(keyposParentIndicesSeqOrder.get(i));
            if (v < 0) {
                DP.getInstance().e("SSEGRaph", "Keypos parent index #" + i + " is " + v + ", skipping.");
            } else {
                keyposFGIndices.add(v);
            }
        }
        if (keyposFGIndices.size() != fg.getSize()) {
            DP.getInstance().e("SSEGRaph", "keyposFGIndices.size()=" + keyposFGIndices.size() + ", fg.size=" + fg.size + ".");
        }
        Integer firstVertexSpatFGIndex = fg.spatOrder.get(0);
        orientationsSpatOrder[0] = FoldingGraph.ORIENTATION_UPWARDS;
        orientationsSeqOrder[firstVertexSpatFGIndex] = FoldingGraph.ORIENTATION_UPWARDS;
        if (debugDrawingKEY) {
            System.out.println("Setting orientationsSeqOrder[" + firstVertexSpatFGIndex + "] to " + FoldingGraph.ORIENTATION_UPWARDS + ".");
        }
        Integer currentVert;
        Integer lastVert;
        StringBuilder KEYNotation = new StringBuilder();
        String bracketStart = "{";
        String bracketEnd = "}";
        KEYNotation.append(bracketStart);
        KEYNotation.append(fg.getVertex(keystartFGIndex).getLinearNotationLabel());
        if (keyposFGIndices.size() > 1) {
            for (int i = 1; i < keyposFGIndicesSpatOrder.size(); i++) {
                if (i < (keyposFGIndicesSpatOrder.size())) {
                    KEYNotation.append(",");
                }
                currentVert = keyposFGIndicesSpatOrder.get(i);
                lastVert = keyposFGIndicesSpatOrder.get(i - 1);
                KEYNotation.append(currentVert - lastVert);
                Integer spatRel = fg.getContactType(lastVert, currentVert);
                if (Objects.equals(spatRel, SpatRel.PARALLEL)) {
                    KEYNotation.append("x");
                    orientationsSpatOrder[i] = orientationsSpatOrder[i - 1];
                    orientationsSeqOrder[currentVert] = orientationsSpatOrder[i];
                } else {
                    orientationsSpatOrder[i] = (Objects.equals(orientationsSpatOrder[i - 1], FoldingGraph.ORIENTATION_UPWARDS) ? FoldingGraph.ORIENTATION_DOWNWARDS : FoldingGraph.ORIENTATION_UPWARDS);
                    orientationsSeqOrder[currentVert] = orientationsSpatOrder[i];
                }
                KEYNotation.append(fg.getVertex(currentVert).getLinearNotationLabel());
            }
        }
        KEYNotation.append(bracketEnd);
        if (debugDrawingKEY) {
            DP.getInstance().d("orientationsSpatOrder=" + IO.integerArrayToString(orientationsSpatOrder));
            DP.getInstance().d("orientationsSeqOrder=" + IO.integerArrayToString(orientationsSeqOrder));
        }
        Integer edgeType;
        Integer connCenterX;
        Integer connCenterY;
        Integer leftVertSeq;
        Integer rightVertSeq;
        Integer leftVertSpat;
        Integer rightVertSpat;
        Integer leftVertPosX;
        Integer leftVertPosY;
        Integer rightVertPosX;
        Integer rightVertPosY;
        Integer connWidth;
        Integer connHeight;
        Integer connTopLeftX;
        Integer connTopLeftY;
        Integer spacerX;
        Integer spacerY;
        Integer iSpatIndex;
        Integer jSpatIndex;
        Boolean startUpwards;
        Integer relDrawDistToLast;
        ig2.setStroke(new BasicStroke(1));
        if (debugDrawingKEY) {
            System.out.println("orientationsSeqOrder=" + IO.integerArrayToString(orientationsSeqOrder) + ", orientationsSpatOrder=" + IO.integerArrayToString(orientationsSpatOrder));
        }
        int numContactsDrawn = 0;
        if (keyposFGIndices.size() > 1) {
            for (int i = 1; i < keyposFGIndicesSpatOrder.size(); i++) {
                currentVert = keyposFGIndicesSpatOrder.get(i);
                lastVert = keyposFGIndicesSpatOrder.get(i - 1);
                Integer spatRel = fg.getContactType(lastVert, currentVert);
                relDrawDistToLast = currentVert - lastVert;
                if (debugDrawingKEY) {
                    System.out.println("At i=" + 1 + ", currentVert=" + currentVert + ", lastVert=" + lastVert + "");
                }
                if (spatRel.equals(SpatRel.NONE)) {
                    if (debugDrawingKEY) {
                        System.out.println("  skipping");
                    }
                    continue;
                }
                if (debugDrawingKEY) {
                    System.out.println("  spatRel=" + SpatRel.getString(spatRel) + ", drawing edge " + lastVert + " => " + currentVert + "...");
                }
                boolean edgeIsBackwards = lastVert > currentVert;
                numContactsDrawn++;
                ig2.setPaint(Color.BLACK);
                iSpatIndex = i;
                int j = i - 1;
                jSpatIndex = j;
                if (iSpatIndex < jSpatIndex) {
                    leftVertSpat = iSpatIndex;
                    leftVertSeq = currentVert;
                    rightVertSpat = jSpatIndex;
                    rightVertSeq = lastVert;
                } else {
                    leftVertSpat = jSpatIndex;
                    leftVertSeq = lastVert;
                    rightVertSpat = iSpatIndex;
                    rightVertSeq = currentVert;
                }
                leftVertPosX = vertStartX + (leftVertSeq * vertDist);
                rightVertPosX = vertStartX + (rightVertSeq * vertDist);
                connWidth = rightVertPosX - leftVertPosX;
                connHeight = connWidth / 2;
                connCenterX = rightVertPosX - (connWidth / 2);
                connCenterY = vertStartY - (connHeight / 2);
                connTopLeftX = leftVertPosX;
                connTopLeftY = vertStartY - (connHeight / 2);
                spacerX = vertWidth;
                spacerY = 0;
                if (Objects.equals(orientationsSeqOrder[leftVertSeq], FoldingGraph.ORIENTATION_UPWARDS)) {
                    leftVertPosY = vertStartY - vertHeight;
                    startUpwards = true;
                    if (debugDrawingKEY) {
                        DP.getInstance().d("  leftVert " + leftVertSeq + " starts upwards, ");
                    }
                } else {
                    leftVertPosY = vertStartY;
                    startUpwards = false;
                    if (debugDrawingKEY) {
                        DP.getInstance().d("  leftVert " + leftVertSeq + " starts downwards, ");
                    }
                }
                if (Objects.equals(orientationsSeqOrder[rightVertSeq], FoldingGraph.ORIENTATION_UPWARDS)) {
                    rightVertPosY = vertStartY;
                    if (debugDrawingKEY) {
                        DP.getInstance().d("  rightVert " + rightVertSeq + " starts upwards. ");
                    }
                } else {
                    rightVertPosY = vertStartY - vertHeight;
                    if (debugDrawingKEY) {
                        DP.getInstance().d("  rightVert " + rightVertSeq + " starts downwards. ");
                    }
                }
                boolean edgeIsCrossOver = Objects.equals(orientationsSeqOrder[leftVertSeq], orientationsSeqOrder[rightVertSeq]);
                if (debugDrawingKEY) {
                    System.out.print("Getting arc from " + leftVertPosX + "," + leftVertPosY + " to " + rightVertPosX + "," + rightVertPosY + ".\n");
                    Color old = ig2.getColor();
                    ig2.setColor(Color.BLACK);
                    int spacerDebugX = 10;
                    int spacerDebugY = i * 10;
                    ig2.drawString("i=" + i + "(" + lastVert + "->" + currentVert + ")", leftVertPosX + spacerDebugX, leftVertPosY + spacerDebugY);
                    ig2.drawString("x" + i, leftVertPosX, leftVertPosY);
                    ig2.drawString("y" + i, rightVertPosX, rightVertPosY);
                    ig2.setColor(old);
                }
                ArrayList<Shape> connShapes;
                /** If the central line of the crossover-connector would cut through a vertex, we may want to shift it to the side a bit. */
                int pixelsToShiftOnYAxis = 0;
                if (edgeIsCrossOver) {
                    if (relDrawDistToLast % 2 == 0) {
                        pixelsToShiftOnYAxis = (pl.vertDist / 4) + (relDrawDistToLast * 5);
                        if (pixelsToShiftOnYAxis > (pl.vertDist * 0.8)) {
                            pixelsToShiftOnYAxis = (pl.vertDist / 2);
                        }
                    }
                }
                if (edgeIsBackwards && edgeIsCrossOver) {
                    if (Settings.getBoolean("plcc_B_key_use_alternate_arcs")) {
                        connShapes = DrawTools.getArcConnectorAlternative(leftVertPosX, rightVertPosY, rightVertPosX, leftVertPosY, ig2.getStroke(), !startUpwards, pixelsToShiftOnYAxis);
                    } else {
                        connShapes = DrawTools.getArcConnector(leftVertPosX, rightVertPosY, rightVertPosX, leftVertPosY, ig2.getStroke(), !startUpwards, pixelsToShiftOnYAxis);
                    }
                } else {
                    if (Settings.getBoolean("plcc_B_key_use_alternate_arcs")) {
                        connShapes = DrawTools.getArcConnectorAlternative(leftVertPosX, leftVertPosY, rightVertPosX, rightVertPosY, ig2.getStroke(), startUpwards, pixelsToShiftOnYAxis);
                    } else {
                        connShapes = DrawTools.getArcConnector(leftVertPosX, leftVertPosY, rightVertPosX, rightVertPosY, ig2.getStroke(), startUpwards, pixelsToShiftOnYAxis);
                    }
                }
                for (Shape s : connShapes) {
                    ig2.draw(s);
                }
            }
            if (numContactsDrawn != fg.getEdgeList().size()) {
                if (fg.isASingleCycle() && numContactsDrawn == (fg.getEdgeList().size() - 1)) {
                } else {
                    DP.getInstance().w("SSEGraph", "drawFoldingGrapgKEYG2D: drew " + numContactsDrawn + ", but FG " + fg.toShortString() + " has only " + fg.getEdgeList().size() + " edges. " + (fg.isASingleCycle() ? " FG is single circle." : "FG is NOT single circle."));
                }
            }
        }
        ig2.setStroke(new BasicStroke(1));
        Polygon p;
        AffineTransform origXform = ig2.getTransform();
        AffineTransform newXform;
        int rotationCenterX;
        int rotationCenterY;
        int angle = 180;
        Shape shape;
        for (Integer s = 0; s < fg.sseList.size(); s++) {
            Integer i = fg.spatOrder.get(s);
            if (fg.sseList.get(i).isHelix()) {
                ig2.setPaint(Color.RED);
            } else if (fg.sseList.get(i).isBetaStrand()) {
                ig2.setPaint(Color.BLACK);
            } else if (fg.sseList.get(i).isLigandSSE()) {
                ig2.setPaint(Color.MAGENTA);
            } else if (fg.sseList.get(i).isOtherSSE()) {
                ig2.setPaint(Color.GRAY);
            } else {
                ig2.setPaint(Color.LIGHT_GRAY);
            }
            Integer currentVertX = vertStartX + (i * vertDist);
            Integer currentVertY = vertStartY;
            if (fg.sseList.get(i).isHelix()) {
                p = DrawTools.getDefaultArrowPolygonUp(vertStartY - vertHeight, currentVertX, currentVertY);
            } else {
                p = DrawTools.getDefaultArrowPolygonUp(vertStartY - vertHeight, currentVertX, currentVertY);
            }
            shape = ig2.getStroke().createStrokedShape(p);
            newXform = (AffineTransform) (origXform.clone());
            rotationCenterX = (vertStartX + (i * vertDist));
            rotationCenterY = vertStartY - (vertHeight / 2);
            newXform.rotate(Math.toRadians(angle), rotationCenterX, rotationCenterY);
            if (Objects.equals(orientationsSeqOrder[i], FoldingGraph.ORIENTATION_DOWNWARDS)) {
                ig2.setTransform(newXform);
            }
            ig2.draw(shape);
            if (Objects.equals(orientationsSeqOrder[i], FoldingGraph.ORIENTATION_DOWNWARDS)) {
                ig2.setTransform(origXform);
            }
            Integer compareToTerminus = i;
            Integer ssePos = s;
            if (Objects.equals(s, 0)) {
                ig2.setPaint(Color.BLACK);
                ig2.drawString("N", currentVertX, currentVertY + 20);
            }
            if (Objects.equals(s, fg.numVertices() - 1)) {
                ig2.setPaint(Color.BLACK);
                ig2.drawString("C", currentVertX, currentVertY + 20);
            }
        }
        if (Settings.getBoolean("plcc_B_graphimg_footer")) {
            Integer printNth = 1;
            if (fg.getSize() > 0) {
                ig2.setPaint(Color.BLACK);
                ig2.drawString("FG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                ig2.setPaint(Color.LIGHT_GRAY);
                ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + lineHeight + (stringHeight / 4));
                ig2.setPaint(Color.BLACK);
            } else {
                ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
            }
            String sseNumberFoldingGraphKEY;
            for (Integer i = leftMostVertexInParent; i <= rightMostVertexInParent; i++) {
                Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
                if (parentVertexPosInFoldingGraph < 0) {
                    continue;
                }
                if ((i + 1) % printNth == 0) {
                    sseNumberFoldingGraph = "" + (parentVertexPosInFG.get(i) >= 0 ? (parentVertexPosInFG.get(i) + 1) : "");
                    sseNumberFoldingGraphKEY = fg.spatOrder.indexOf(i) + "";
                    sseNumberSeq = "" + (pg.sseList.get(i).getSSESeqChainNum());
                    sseNumberProteinGraph = "" + (i + 1);
                    stringHeight = fontMetrics.getAscent();
                    ig2.setPaint(Color.BLACK);
                    ig2.drawString(sseNumberFoldingGraph, pl.getFooterStart().x + ((i - shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.setPaint(Color.LIGHT_GRAY);
                    ig2.drawString(sseNumberSeq, pl.getFooterStart().x + ((i - shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                    ig2.drawString(sseNumberProteinGraph, pl.getFooterStart().x + ((i - shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight * 2) + (stringHeight / 4));
                    ig2.setPaint(Color.BLACK);
                }
            }
            if (Settings.getBoolean("plcc_B_graphimg_legend")) {
                ProteinGraphDrawer.drawLegendKEY(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + (lineHeight * 3) + (stringHeight / 4)), pl, fg);
            }
        }
        Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
        DrawResult drawRes = new DrawResult(ig2, roi);
        return drawRes;
    }

    /**
     * Draws the RED folding graph image of this graph and returns the DrawResult.
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     *
     * @param pnfr a folding graph notation result
     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawFoldingGraphREDG2D(PTGLNotationFoldResult pnfr) {
        FoldingGraph fg = pnfr.getFoldingGraph();
        SSEGraph pg = fg.parent;
        Integer startVertexInParent = fg.getMinimalVertexIndexInParentGraph();
        Integer endVertexInParent = fg.getMaximalVertexIndexInParentGraph();
        List<Integer> fgVertexPosInParent = fg.getVertexIndexListInParentGraph();
        List<Integer> parentVertexPosInFG = ProtGraph.computeParentGraphVertexPositionsInFoldingGraph(fgVertexPosInParent, pg.size);
        Integer[] shiftBack = new Integer[pg.size];
        Integer shift = 0;
        for (int i = 0; i < parentVertexPosInFG.size(); i++) {
            if (parentVertexPosInFG.get(i) < 0) {
                shift++;
            }
            shiftBack[i] = shift;
        }
        Integer numVerts = fg.getSize();
        Boolean bw = false;
        PageLayout pl = new PageLayout(numVerts);
        Position2D vertStart = pl.getVertStart();
        Integer lineHeight = pl.textLineHeight;
        SVGGraphics2D ig2;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        ig2 = new SVGGraphics2D(document);
        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ig2.setPaint(Color.WHITE);
        ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
        ig2.setPaint(Color.BLACK);
        Font font = pl.getStandardFont();
        Font fontBold = pl.getStandardFontBold();
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();
        String proteinHeader = "The RED " + pg.graphType + " folding graph " + fg.getFoldingGraphFoldName() + " (# " + fg.getFoldingGraphNumber() + ") of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + fg.numVertices() + ", E=" + fg.numSSEContacts() + "].";
        String notation = "RED notation: '" + pnfr.redNotation + "'";
        Integer stringHeight = fontMetrics.getAscent();
        String sseNumberSeq;
        String sseNumberFoldingGraph;
        String sseNumberProteinGraph;
        if (Settings.getBoolean("plcc_B_graphimg_header")) {
            ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
            if (Settings.getBoolean("plcc_B_print_notations_on_fg_images")) {
                ig2.drawString(notation, pl.headerStart.x, pl.headerStart.y + lineHeight);
            }
        }
        Integer k;
        Integer l;
        Shape shape;
        Arc2D.Double arc;
        ig2.setStroke(new BasicStroke(2));
        Integer edgeType;
        Integer leftVert;
        Integer rightVert;
        Integer leftVertPosX;
        Integer rightVertPosX;
        Integer arcWidth;
        Integer arcHeight;
        Integer arcTopLeftX;
        Integer arcTopLeftY;
        Integer spacerX;
        Integer spacerY;
        for (Integer i = startVertexInParent; i <= endVertexInParent; i++) {
            for (Integer j = i + 1; j <= endVertexInParent; j++) {
                k = parentVertexPosInFG.get(i);
                l = parentVertexPosInFG.get(j);
                if (k < 0 || l < 0) {
                    continue;
                }
                if (fg.containsEdge(k, l)) {
                    edgeType = fg.getContactType(k, l);
                    if (edgeType.equals(SpatRel.PARALLEL)) {
                        ig2.setPaint(Color.RED);
                    } else if (edgeType.equals(SpatRel.ANTIPARALLEL)) {
                        ig2.setPaint(Color.BLUE);
                    } else if (edgeType.equals(SpatRel.MIXED)) {
                        ig2.setPaint(Color.GREEN);
                    } else if (edgeType.equals(SpatRel.LIGAND)) {
                        ig2.setPaint(Color.MAGENTA);
                    } else if (edgeType.equals(SpatRel.BACKBONE)) {
                        ig2.setPaint(Color.ORANGE);
                    } else {
                        ig2.setPaint(Color.LIGHT_GRAY);
                    }
                    if (bw) {
                        ig2.setPaint(Color.LIGHT_GRAY);
                    }
                    if (i < j) {
                        leftVert = i;
                        rightVert = j;
                    } else {
                        leftVert = j;
                        rightVert = i;
                    }
                    leftVertPosX = pl.getVertStart().x + ((leftVert - shiftBack[leftVert]) * pl.vertDist);
                    rightVertPosX = pl.getVertStart().x + ((rightVert - shiftBack[rightVert]) * pl.vertDist);
                    arcWidth = rightVertPosX - leftVertPosX;
                    arcHeight = arcWidth / 2;
                    arcTopLeftX = leftVertPosX;
                    arcTopLeftY = pl.getVertStart().y - arcHeight / 2;
                    spacerX = pl.vertRadius;
                    spacerY = 0;
                    arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
                    shape = ig2.getStroke().createStrokedShape(arc);
                    ig2.fill(shape);
                }
            }
        }
        Ellipse2D.Double circle;
        Rectangle2D.Double rect;
        ig2.setStroke(new BasicStroke(2));
        for (Integer i = startVertexInParent; i <= endVertexInParent; i++) {
            if (pg.sseList.get(i).isHelix()) {
                ig2.setPaint(Color.RED);
            } else if (pg.sseList.get(i).isBetaStrand()) {
                ig2.setPaint(Color.BLACK);
            } else if (pg.sseList.get(i).isLigandSSE()) {
                ig2.setPaint(Color.MAGENTA);
            } else if (pg.sseList.get(i).isOtherSSE()) {
                ig2.setPaint(Color.GRAY);
            } else {
                ig2.setPaint(Color.LIGHT_GRAY);
            }
            Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
            if (parentVertexPosInFoldingGraph < 0) {
                continue;
            }
            if (bw) {
                ig2.setPaint(Color.GRAY);
            }
            SSE sse = pg.getVertex(i);
            if (sse.isBetaStrand()) {
                rect = new Rectangle2D.Double(vertStart.x + ((i - shiftBack[i]) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.fill(rect);
            } else if (sse.isLigandSSE()) {
                circle = new Ellipse2D.Double(vertStart.x + ((i - shiftBack[i]) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.setStroke(new BasicStroke(3));
                ig2.draw(circle);
                ig2.setStroke(new BasicStroke(2));
            } else {
                circle = new Ellipse2D.Double(vertStart.x + ((i - shiftBack[i]) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.fill(circle);
            }
        }
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        if (!bw) {
            if (fg.getSize() > 0) {
                ig2.setFont(fontBold);
                ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);
                ig2.drawString("C", vertStart.x + numVerts * pl.vertDist, vertStart.y + 20);
                ig2.setFont(font);
            }
        }
        if (Settings.getBoolean("plcc_B_graphimg_footer")) {
            Integer printNth = 1;
            if (fg.getSize() > 99) {
                printNth = 2;
            }
            if (fg.getSize() > 999) {
                printNth = 3;
            }
            if (fg.getSize() > 0) {
                ig2.drawString("FG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                ig2.setPaint(Color.LIGHT_GRAY);
                ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + lineHeight + (stringHeight / 4));
                ig2.setPaint(Color.BLACK);
            } else {
                ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
            }
            for (Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
                if (parentVertexPosInFoldingGraph < 0) {
                    continue;
                }
                if ((i + 1) % printNth == 0) {
                    sseNumberFoldingGraph = "" + (parentVertexPosInFG.get(i) >= 0 ? (parentVertexPosInFG.get(i) + 1) : "");
                    sseNumberSeq = "" + (pg.sseList.get(i).getSSESeqChainNum());
                    sseNumberProteinGraph = "" + (i + 1);
                    stringHeight = fontMetrics.getAscent();
                    ig2.setPaint(Color.BLACK);
                    ig2.drawString(sseNumberFoldingGraph, pl.getFooterStart().x + ((i - shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.setPaint(Color.LIGHT_GRAY);
                    ig2.drawString(sseNumberSeq, pl.getFooterStart().x + ((i - shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                    ig2.drawString(sseNumberProteinGraph, pl.getFooterStart().x + ((i - shiftBack[i]) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight * 2) + (stringHeight / 4));
                    ig2.setPaint(Color.BLACK);
                }
            }
            if (Settings.getBoolean("plcc_B_graphimg_legend")) {
                ProteinGraphDrawer.drawLegend(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + (lineHeight * 3) + (stringHeight / 4)), pl, fg);
            }
        }
        Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
        DrawResult drawRes = new DrawResult(ig2, roi);
        return drawRes;
    }

    /**
     * Draws the DEF folding graph image of this graph and returns the DrawResult.
     *
     * The DEF folding graph visualization was introduced by Tim. It is similar to ADJ, but draws all vertices of the parent PG; not just those between the start and end vertices of the FG.
     * Note that this does not have a linnot string, it is just for visualization of the FG within the PG.
     *
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     *
     * @param pnfr a folding graph notation result
     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawFoldingGraphDEFG2D(PTGLNotationFoldResult pnfr) {
        FoldingGraph fg = pnfr.getFoldingGraph();
        SSEGraph pg = fg.parent;
        Integer startVertexInParent = 0;
        Integer endVertexInParent = pg.size - 1;
        Integer shiftBack = startVertexInParent;
        List<Integer> fgVertexPosInParent = fg.getVertexIndexListInParentGraph();
        List<Integer> parentVertexPosInFG = pg.computeParentGraphVertexPositionsInFoldingGraph(fgVertexPosInParent, pg.size);
        Integer numVerts = endVertexInParent - startVertexInParent + 1;
        Boolean bw = false;
        PageLayout pl = new PageLayout(numVerts);
        Position2D vertStart = pl.getVertStart();
        Integer lineHeight = pl.textLineHeight;
        SVGGraphics2D ig2;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        ig2 = new SVGGraphics2D(document);
        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ig2.setPaint(Color.WHITE);
        ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
        ig2.setPaint(Color.BLACK);
        Font font = pl.getStandardFont();
        Font fontBold = pl.getStandardFontBold();
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();
        String proteinHeader = "The DEF " + pg.graphType + " folding graph " + fg.getFoldingGraphFoldName() + " (# " + fg.getFoldingGraphNumber() + ") of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + fg.numVertices() + ", E=" + fg.numSSEContacts() + "].";
        Integer stringHeight = fontMetrics.getAscent();
        String sseNumberSeq;
        String sseNumberFoldingGraph;
        String sseNumberProteinGraph;
        if (Settings.getBoolean("plcc_B_graphimg_header")) {
            ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
        }
        Integer k;
        Integer l;
        Shape shape;
        Arc2D.Double arc;
        ig2.setStroke(new BasicStroke(2));
        Integer edgeType;
        Integer leftVert;
        Integer rightVert;
        Integer leftVertPosX;
        Integer rightVertPosX;
        Integer arcWidth;
        Integer arcHeight;
        Integer arcTopLeftX;
        Integer arcTopLeftY;
        Integer spacerX;
        Integer spacerY;
        for (Integer i = startVertexInParent; i <= endVertexInParent; i++) {
            for (Integer j = i + 1; j <= endVertexInParent; j++) {
                Boolean parentGraphOnlyPair;
                k = parentVertexPosInFG.get(i);
                l = parentVertexPosInFG.get(j);
                if (k < 0 || l < 0) {
                    parentGraphOnlyPair = true;
                } else {
                    parentGraphOnlyPair = false;
                }
                if (parentGraphOnlyPair) {
                    if (pg.containsEdge(i, j)) {
                        ig2.setPaint(Color.LIGHT_GRAY);
                        if (i < j) {
                            leftVert = i;
                            rightVert = j;
                        } else {
                            leftVert = j;
                            rightVert = i;
                        }
                        leftVertPosX = pl.getVertStart().x + ((leftVert - shiftBack) * pl.vertDist);
                        rightVertPosX = pl.getVertStart().x + ((rightVert - shiftBack) * pl.vertDist);
                        arcWidth = rightVertPosX - leftVertPosX;
                        arcHeight = arcWidth / 2;
                        arcTopLeftX = leftVertPosX;
                        arcTopLeftY = pl.getVertStart().y - arcHeight / 2;
                        spacerX = pl.vertRadius;
                        spacerY = 0;
                        arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
                        shape = ig2.getStroke().createStrokedShape(arc);
                        ig2.fill(shape);
                    }
                } else {
                    if (fg.containsEdge(k, l)) {
                        edgeType = fg.getContactType(k, l);
                        if (edgeType.equals(SpatRel.PARALLEL)) {
                            ig2.setPaint(Color.RED);
                        } else if (edgeType.equals(SpatRel.ANTIPARALLEL)) {
                            ig2.setPaint(Color.BLUE);
                        } else if (edgeType.equals(SpatRel.MIXED)) {
                            ig2.setPaint(Color.GREEN);
                        } else if (edgeType.equals(SpatRel.LIGAND)) {
                            ig2.setPaint(Color.MAGENTA);
                        } else if (edgeType.equals(SpatRel.BACKBONE)) {
                            ig2.setPaint(Color.ORANGE);
                        } else {
                            ig2.setPaint(Color.LIGHT_GRAY);
                        }
                        if (bw) {
                            ig2.setPaint(Color.LIGHT_GRAY);
                        }
                        if (i < j) {
                            leftVert = i;
                            rightVert = j;
                        } else {
                            leftVert = j;
                            rightVert = i;
                        }
                        leftVertPosX = pl.getVertStart().x + ((leftVert - shiftBack) * pl.vertDist);
                        rightVertPosX = pl.getVertStart().x + ((rightVert - shiftBack) * pl.vertDist);
                        arcWidth = rightVertPosX - leftVertPosX;
                        arcHeight = arcWidth / 2;
                        arcTopLeftX = leftVertPosX;
                        arcTopLeftY = pl.getVertStart().y - arcHeight / 2;
                        spacerX = pl.vertRadius;
                        spacerY = 0;
                        arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
                        shape = ig2.getStroke().createStrokedShape(arc);
                        ig2.fill(shape);
                    }
                }
            }
        }
        Ellipse2D.Double circle;
        Rectangle2D.Double rect;
        ig2.setStroke(new BasicStroke(2));
        for (Integer i = startVertexInParent; i <= endVertexInParent; i++) {
            if (pg.sseList.get(i).isHelix()) {
                ig2.setPaint(Color.RED);
            } else if (pg.sseList.get(i).isBetaStrand()) {
                ig2.setPaint(Color.BLACK);
            } else if (pg.sseList.get(i).isLigandSSE()) {
                ig2.setPaint(Color.MAGENTA);
            } else if (pg.sseList.get(i).isOtherSSE()) {
                ig2.setPaint(Color.GRAY);
            } else {
                ig2.setPaint(Color.LIGHT_GRAY);
            }
            Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
            if (parentVertexPosInFoldingGraph < 0) {
                ig2.setPaint(Color.LIGHT_GRAY);
            }
            if (bw) {
                ig2.setPaint(Color.GRAY);
            }
            SSE sse = pg.getVertex(i);
            if (sse.isBetaStrand()) {
                rect = new Rectangle2D.Double(vertStart.x + ((i - shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.fill(rect);
            } else if (sse.isLigandSSE()) {
                circle = new Ellipse2D.Double(vertStart.x + ((i - shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.setStroke(new BasicStroke(3));
                ig2.draw(circle);
                ig2.setStroke(new BasicStroke(2));
            } else {
                circle = new Ellipse2D.Double(vertStart.x + ((i - shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.fill(circle);
            }
        }
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        if (!bw) {
            if (fg.getSize() > 0) {
                ig2.setFont(fontBold);
                ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);
                ig2.drawString("C", vertStart.x + numVerts * pl.vertDist, vertStart.y + 20);
                ig2.setFont(font);
            }
        }
        if (Settings.getBoolean("plcc_B_graphimg_footer")) {
            Integer printNth = 1;
            if (fg.getSize() > 99) {
                printNth = 2;
            }
            if (fg.getSize() > 999) {
                printNth = 3;
            }
            if (fg.getSize() > 0) {
                ig2.drawString("FG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                ig2.setPaint(Color.LIGHT_GRAY);
                ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + lineHeight + (stringHeight / 4));
                ig2.setPaint(Color.BLACK);
            } else {
                ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
            }
            for (Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                if ((i + 1) % printNth == 0) {
                    sseNumberFoldingGraph = "" + (parentVertexPosInFG.get(i) >= 0 ? (parentVertexPosInFG.get(i) + 1) : "");
                    sseNumberSeq = "" + (pg.sseList.get(i).getSSESeqChainNum());
                    sseNumberProteinGraph = "" + (i + 1);
                    stringHeight = fontMetrics.getAscent();
                    ig2.setPaint(Color.BLACK);
                    ig2.drawString(sseNumberFoldingGraph, pl.getFooterStart().x + ((i - shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.setPaint(Color.LIGHT_GRAY);
                    ig2.drawString(sseNumberSeq, pl.getFooterStart().x + ((i - shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                    ig2.drawString(sseNumberProteinGraph, pl.getFooterStart().x + ((i - shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight * 2) + (stringHeight / 4));
                    ig2.setPaint(Color.BLACK);
                }
            }
            if (Settings.getBoolean("plcc_B_graphimg_legend")) {
                ProteinGraphDrawer.drawLegendDEFADJ(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + (lineHeight * 3) + (stringHeight / 4)), pl, fg, true);
            }
        }
        Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
        DrawResult drawRes = new DrawResult(ig2, roi);
        return drawRes;
    }

    /**
     * Draws the legend for the graph at the given position. This legend is not suitable for SEQ folding graphs, because their edges are different.
     * This is a special version for DEF and ADJ graphs, which adds the gray vertices and labels them as parent graph vertices.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position (x, y) where to start drawing
     * @param pl the page layout
     * @param g tge sse graph
     * @param includeParentEdge whether to draw the symbol fro parent edges (only DEF graphs need this, but not ADJ graphs)
     * @return the x coordinate in the image where the legend ends (which is the left margin + the legend width).
     * This can be used to determine the minimal width of the total image (it has to be at least this value).
     */
    public static Integer drawLegendDEFADJ(SVGGraphics2D ig2, Position2D startPos, PageLayout pl, SSEGraph g, Boolean includeParentEdge) {
        Boolean drawAll = Settings.getBoolean("plcc_B_graphimg_legend_always_all");
        ig2.setFont(pl.getLegendFont());
        FontMetrics fontMetrics = ig2.getFontMetrics();
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        Integer spacer = 10;
        Integer pixelPosX = startPos.x;
        Integer vertWidth = pl.getVertDiameter();
        Integer vertOffset = pl.getVertDiameter() / 4 * 3;
        String label;
        if (g.numEdges() > 0) {
            label = "[Edges: ";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsContactTypeParallel() || drawAll) {
            label = "parallel";
            ig2.setPaint(Color.RED);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsContactTypeAntiparallel() || drawAll) {
            label = "antiparallel";
            ig2.setPaint(Color.BLUE);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsContactTypeMixed() || drawAll) {
            label = "mixed";
            ig2.setPaint(Color.GREEN);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsContactTypeLigand() || drawAll) {
            label = "ligand";
            ig2.setPaint(Color.MAGENTA);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        label = "parent";
        ig2.setPaint(Color.GRAY);
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
        if (g.chainEnd.size() > 0 || drawAll) {
            label = "interchain";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.numEdges() > 0) {
            label = "]";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.numVertices() > 0) {
            label = " [Vertices: ";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeHelix() || drawAll) {
            ProteinGraphDrawer.drawSymbolAlphaHelix(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "helix";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeBetaStrand() || drawAll) {
            ProteinGraphDrawer.drawSymbolBetaStrand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "strand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeLigand() || drawAll) {
            ProteinGraphDrawer.drawSymbolLigand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "ligand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeOther() || drawAll) {
            ProteinGraphDrawer.drawSymbolOtherSSE(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "other";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        ProteinGraphDrawer.drawSymbolsParentSSEs(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
        pixelPosX += vertWidth + spacer + 50;
        label = "parent";
        ig2.drawString(label, pixelPosX, startPos.y);
        pixelPosX += fontMetrics.stringWidth(label) + spacer;
        if (g.numVertices() > 0) {
            label = "]";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        return pixelPosX;
    }

    /**
     * Draws the protein graph image of this graph and returns the DrawResult.
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     *
     * @param nonProteinGraph whether the graph is a non-protein graph and thus does NOT contain information on the relative SSE orientation in the expected way. If so, it is drawn in gray scale because the color code becomes useless (true => gray scale, false => color).
     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawProteinGraphG2D(Boolean nonProteinGraph, ProtGraph pg) {
        Integer numVerts = pg.numVertices();
        Boolean bw = nonProteinGraph;
        PageLayout pl = new PageLayout(numVerts);
        Position2D vertStart = pl.getVertStart();
        SVGGraphics2D ig2;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        ig2 = new SVGGraphics2D(document);
        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ig2.setPaint(Color.WHITE);
        ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
        ig2.setPaint(Color.BLACK);
        Font font = pl.getStandardFont();
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();
        String proteinHeader = "The " + pg.graphType + " protein graph of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + pg.numVertices() + ", E=" + pg.numSSEContacts() + "].";
        Integer stringHeight = fontMetrics.getAscent();
        String sseNumberSeq;
        String sseNumberGraph;
        if (Settings.getBoolean("plcc_B_graphimg_header")) {
            ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
        }
        Shape shape;
        Arc2D.Double arc;
        ig2.setStroke(new BasicStroke(2));
        Integer edgeType;
        Integer leftVert;
        Integer rightVert;
        Integer leftVertPosX;
        Integer rightVertPosX;
        Integer arcWidth;
        Integer arcHeight;
        Integer arcTopLeftX;
        Integer arcTopLeftY;
        Integer spacerX;
        Integer spacerY;
        Integer iChainID;
        Integer jChainID;
        for (Integer i = 0; i < pg.sseList.size(); i++) {
            for (Integer j = i + 1; j < pg.sseList.size(); j++) {
                if (pg.containsEdge(i, j)) {
                    edgeType = pg.getContactType(i, j);
                    if (edgeType.equals(SpatRel.PARALLEL)) {
                        ig2.setPaint(Color.RED);
                    } else if (edgeType.equals(SpatRel.ANTIPARALLEL)) {
                        ig2.setPaint(Color.BLUE);
                    } else if (edgeType.equals(SpatRel.MIXED)) {
                        ig2.setPaint(Color.GREEN);
                    } else if (edgeType.equals(SpatRel.LIGAND)) {
                        ig2.setPaint(Color.MAGENTA);
                    } else if (edgeType.equals(SpatRel.BACKBONE)) {
                        ig2.setPaint(Color.ORANGE);
                    } else if (edgeType.equals(SpatRel.COMPLEX)) {
                        ig2.setPaint(Color.BLACK);
                    } else {
                        ig2.setPaint(Color.LIGHT_GRAY);
                    }
                    if (bw) {
                        ig2.setPaint(Color.LIGHT_GRAY);
                    }
                    iChainID = -1;
                    jChainID = -1;
                    for (Integer x = 0; x < pg.chainEnd.size(); x++) {
                        if (i < pg.chainEnd.get(x)) {
                            iChainID = x;
                            break;
                        }
                    }
                    for (Integer x = 0; x < pg.chainEnd.size(); x++) {
                        if (j < pg.chainEnd.get(x)) {
                            jChainID = x;
                            break;
                        }
                    }
                    if (!Objects.equals(iChainID, jChainID)) {
                        ig2.setPaint(Color.BLACK);
                    }
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
                    arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
                    shape = ig2.getStroke().createStrokedShape(arc);
                    ig2.fill(shape);
                }
            }
        }
        Ellipse2D.Double circle;
        Rectangle2D.Double rect;
        ig2.setStroke(new BasicStroke(2));
        for (Integer i = 0; i < pg.sseList.size(); i++) {
            if (pg.sseList.get(i).isHelix()) {
                ig2.setPaint(Color.RED);
            } else if (pg.sseList.get(i).isBetaStrand()) {
                ig2.setPaint(Color.BLACK);
            } else if (pg.sseList.get(i).isLigandSSE()) {
                ig2.setPaint(Color.MAGENTA);
            } else if (pg.sseList.get(i).isOtherSSE()) {
                ig2.setPaint(Color.GRAY);
            } else {
                ig2.setPaint(Color.LIGHT_GRAY);
            }
            if (bw) {
                ig2.setPaint(Color.GRAY);
            }
            if (pg.sseList.get(i).isBetaStrand()) {
                rect = new Rectangle2D.Double(vertStart.x + (i * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.fill(rect);
            } else if (pg.sseList.get(i).isLigandSSE()) {
                circle = new Ellipse2D.Double(vertStart.x + (i * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.setStroke(new BasicStroke(3));
                ig2.draw(circle);
                ig2.setStroke(new BasicStroke(2));
            } else {
                circle = new Ellipse2D.Double(vertStart.x + (i * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.fill(circle);
            }
        }
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        if (!bw) {
            if (pg.sseList.size() > 0) {
                ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);
                ig2.drawString("C", vertStart.x + pg.sseList.size() * pl.vertDist, vertStart.y + 20);
            }
        }
        if (Settings.getBoolean("plcc_B_graphimg_footer")) {
            Integer printNth = 1;
            if (pg.sseList.size() > 9) {
                printNth = 1;
            }
            if (pg.sseList.size() > 99) {
                printNth = 2;
            }
            if (pg.sseList.size() > 999) {
                printNth = 3;
            }
            Integer lineHeight = pl.textLineHeight;
            if (pg.sseList.size() > 0) {
                ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
            } else {
                ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
            }
            iChainID = -1;
            for (Integer i = 0; i < pg.sseList.size(); i++) {
                if ((i + 1) % printNth == 0) {
                    sseNumberGraph = "" + (i + 1);
                    sseNumberSeq = "" + (pg.sseList.get(i).getSSESeqChainNum());
                    stringHeight = fontMetrics.getAscent();
                    ig2.drawString(sseNumberGraph, pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.drawString(sseNumberSeq, pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                    for (Integer x = 0; x < pg.chainEnd.size(); x++) {
                        if (i < pg.chainEnd.get(x)) {
                            iChainID = x;
                            break;
                        }
                    }
                    if (iChainID != -1) {
                        ig2.drawString(pg.allChains.get(iChainID).getPdbChainID(), pl.getFooterStart().x + (i * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight * 2) + (stringHeight / 4));
                    }
                }
            }
            if (Settings.getBoolean("plcc_B_graphimg_legend")) {
                if (iChainID != -1) {
                    ProteinGraphDrawer.drawLegend(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + lineHeight * 3 + (stringHeight / 4)), pl, pg);
                } else {
                    ProteinGraphDrawer.drawLegend(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + lineHeight * 2 + (stringHeight / 4)), pl, pg);
                }
            }
        }
        Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
        DrawResult drawRes = new DrawResult(ig2, roi);
        return drawRes;
    }

    /**
     * Creates an encapsulated postscript (EPS) format image file of the folding graph.
     * See http://xmlgraphics.apache.org/commons/postscript.html for documentation on this.
     * @param outputFile the target file
     * @throws IOException In case of an I/O error
     */
    public static void drawProteinGraphToEncPostscriptFile(File outputFile) throws IOException {
        OutputStream out = new FileOutputStream(outputFile);
        out = new BufferedOutputStream(out);
        try {
            EPSDocumentGraphics2D g2d = new EPSDocumentGraphics2D(false);
            g2d.setGraphicContext(new GraphicContext());
            g2d.setupDocument(out, 400, 200);
            g2d.drawRect(0, 0, 400, 200);
            Graphics2D copy = (Graphics2D) g2d.create();
            int c = 12;
            for (int i = 0; i < c; i++) {
                float f = (i + 1) / (float) c;
                Color col = new Color(0.0F, 1 - f, 0.0F);
                copy.setColor(col);
                copy.fillRect(70, 90, 50, 50);
                copy.rotate(-2 * Math.PI / (double) c, 70, 90);
            }
            copy.dispose();
            g2d.rotate(-0.25);
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("sans-serif", Font.PLAIN, 36));
            g2d.drawString("Hello world!", 140, 140);
            g2d.setColor(Color.RED.darker());
            g2d.setFont(new Font("serif", Font.PLAIN, 36));
            g2d.drawString("Hello world!", 140, 180);
            g2d.finish();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Draws the legend for the graph at the given position. This legend is not suitable for SEQ folding graphs, because their edges are different.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position (x, y) where to start drawing
     * @return the x coordinate in the image where the legend ends (which is the left margin + the legend width).
     * This can be used to determine the minimal width of the total image (it has to be at least this value).
     */
    public static Integer drawLegend(SVGGraphics2D ig2, Position2D startPos, PageLayout pl, SSEGraph g) {
        Boolean drawAll = Settings.getBoolean("plcc_B_graphimg_legend_always_all");
        ig2.setFont(pl.getLegendFont());
        FontMetrics fontMetrics = ig2.getFontMetrics();
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        Integer spacer = 10;
        Integer pixelPosX = startPos.x;
        Integer vertWidth = pl.getVertDiameter();
        Integer vertOffset = pl.getVertDiameter() / 4 * 3;
        String label;
        if (g.numEdges() > 0) {
            label = "[Edges: ";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsContactTypeParallel() || drawAll) {
            label = "parallel";
            ig2.setPaint(Color.RED);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsContactTypeAntiparallel() || drawAll) {
            label = "antiparallel";
            ig2.setPaint(Color.BLUE);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsContactTypeMixed() || drawAll) {
            label = "mixed";
            ig2.setPaint(Color.GREEN);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsContactTypeLigand() || drawAll) {
            label = "ligand";
            ig2.setPaint(Color.MAGENTA);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.chainEnd.size() > 0 || drawAll) {
            label = "interchain";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.numEdges() > 0) {
            label = "]";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.numVertices() > 0) {
            label = " [Vertices: ";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeHelix() || drawAll) {
            ProteinGraphDrawer.drawSymbolAlphaHelix(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "helix";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeBetaStrand() || drawAll) {
            ProteinGraphDrawer.drawSymbolBetaStrand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "strand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeLigand() || drawAll) {
            ProteinGraphDrawer.drawSymbolLigand(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "ligand";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.containsSSETypeOther() || drawAll) {
            ProteinGraphDrawer.drawSymbolOtherSSE(ig2, new Position2D(pixelPosX, startPos.y - vertOffset), pl);
            pixelPosX += vertWidth + spacer;
            label = "other";
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        if (g.numVertices() > 0) {
            label = "]";
            ig2.setPaint(Color.BLACK);
            ig2.drawString(label, pixelPosX, startPos.y);
            pixelPosX += fontMetrics.stringWidth(label) + spacer;
        }
        return pixelPosX;
    }

    /**
     * Draws an ADJ folding graph in all image formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param drawBlackAndWhite whether to omit colors, only useful for non-protein graphs
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @param pnfr
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<DrawTools.IMAGEFORMAT, String> drawFoldingGraphADJ(String baseFilePathNoExt, Boolean drawBlackAndWhite, DrawTools.IMAGEFORMAT[] formats, PTGLNotationFoldResult pnfr) {
        DrawResult drawRes = ProteinGraphDrawer.drawFoldingGraphADJG2D(pnfr);
        String svgFilePath = baseFilePathNoExt + ".svg";
        HashMap<DrawTools.IMAGEFORMAT, String> resultFilesByFormat = new HashMap<DrawTools.IMAGEFORMAT, String>();
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(DrawTools.IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write ADJ folding graph file : '" + ex.getMessage() + "'.");
        }
        if (!Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("          Output ADJ folding graph files: ");
            for (DrawTools.IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }

    /**
     * Draws the SEQ folding graph image of this graph and returns the DrawResult.
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     *
     * @param pnfr a folding graph notation result
     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawFoldingGraphSEQG2D(PTGLNotationFoldResult pnfr) {
        FoldingGraph fg = pnfr.getFoldingGraph();
        SSEGraph pg = fg.parent;
        Integer startVertexInParent = fg.getMinimalVertexIndexInParentGraph();
        Integer endVertexInParent = fg.getMaximalVertexIndexInParentGraph();
        Integer shiftBack = startVertexInParent;
        List<Integer> fgVertexPosInParent = fg.getVertexIndexListInParentGraph();
        List<Integer> parentVertexPosInFG = pg.computeParentGraphVertexPositionsInFoldingGraph(fgVertexPosInParent, pg.size);
        Integer numVerts = endVertexInParent - startVertexInParent + 1;
        Boolean bw = false;
        PageLayout pl = new PageLayout(numVerts);
        Position2D vertStart = pl.getVertStart();
        Integer lineHeight = pl.textLineHeight;
        SVGGraphics2D ig2;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        ig2 = new SVGGraphics2D(document);
        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ig2.setPaint(Color.WHITE);
        ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
        ig2.setPaint(Color.BLACK);
        Font font = pl.getStandardFont();
        Font fontBold = pl.getStandardFontBold();
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();
        String proteinHeader = "The SEQ " + pg.graphType + " folding graph " + fg.getFoldingGraphFoldName() + " (# " + fg.getFoldingGraphNumber() + ") of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + fg.numVertices() + ", E=" + fg.numSSEContacts() + "].";
        String notation = "SEQ notation: '" + pnfr.seqNotation + "'";
        Integer stringHeight = fontMetrics.getAscent();
        String sseNumberSeq;
        String sseNumberFoldingGraph;
        String sseNumberProteinGraph;
        if (Settings.getBoolean("plcc_B_graphimg_header")) {
            ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
            if (Settings.getBoolean("plcc_B_print_notations_on_fg_images")) {
                ig2.drawString(notation, pl.headerStart.x, pl.headerStart.y + lineHeight);
            }
        }
        Integer k;
        Integer l;
        Shape shape;
        Arc2D.Double arc;
        ig2.setStroke(new BasicStroke(2));
        Integer edgeType;
        Integer leftVert;
        Integer rightVert;
        Integer leftVertPosX;
        Integer rightVertPosX;
        Integer arcWidth;
        Integer arcHeight;
        Integer arcTopLeftX;
        Integer arcTopLeftY;
        Integer spacerX;
        Integer spacerY;
        List<Integer> fgVertexIndices = fg.getVertexIndexList();
        Integer j;
        for (Integer i = 0; i < fgVertexIndices.size() - 1; i++) {
            j = fgVertexIndices.get(i + 1);
            k = fgVertexPosInParent.get(i);
            l = fgVertexPosInParent.get(j);
            ig2.setPaint(Color.BLACK);
            if (k < l) {
                leftVert = k;
                rightVert = l;
            } else {
                leftVert = l;
                rightVert = k;
            }
            leftVertPosX = pl.getVertStart().x + ((leftVert - shiftBack) * pl.vertDist);
            rightVertPosX = pl.getVertStart().x + ((rightVert - shiftBack) * pl.vertDist);
            arcWidth = rightVertPosX - leftVertPosX;
            arcHeight = arcWidth / 2;
            arcTopLeftX = leftVertPosX;
            arcTopLeftY = pl.getVertStart().y - arcHeight / 2;
            spacerX = pl.vertRadius;
            spacerY = 0;
            arc = new Arc2D.Double(arcTopLeftX + spacerX, arcTopLeftY + spacerY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
            shape = ig2.getStroke().createStrokedShape(arc);
            ig2.fill(shape);
        }
        Ellipse2D.Double circle;
        Rectangle2D.Double rect;
        ig2.setStroke(new BasicStroke(2));
        for (Integer i = startVertexInParent; i <= endVertexInParent; i++) {
            if (pg.sseList.get(i).isHelix()) {
                ig2.setPaint(Color.RED);
            } else if (pg.sseList.get(i).isBetaStrand()) {
                ig2.setPaint(Color.BLACK);
            } else if (pg.sseList.get(i).isLigandSSE()) {
                ig2.setPaint(Color.MAGENTA);
            } else if (pg.sseList.get(i).isOtherSSE()) {
                ig2.setPaint(Color.GRAY);
            } else {
                ig2.setPaint(Color.LIGHT_GRAY);
            }
            Integer parentVertexPosInFoldingGraph = parentVertexPosInFG.get(i);
            if (parentVertexPosInFoldingGraph < 0) {
                ig2.setPaint(Color.LIGHT_GRAY);
            }
            if (bw) {
                ig2.setPaint(Color.GRAY);
            }
            SSE sse = pg.getVertex(i);
            if (sse.isBetaStrand()) {
                rect = new Rectangle2D.Double(vertStart.x + ((i - shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.fill(rect);
            } else if (sse.isLigandSSE()) {
                circle = new Ellipse2D.Double(vertStart.x + ((i - shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.setStroke(new BasicStroke(3));
                ig2.draw(circle);
                ig2.setStroke(new BasicStroke(2));
            } else {
                circle = new Ellipse2D.Double(vertStart.x + ((i - shiftBack) * pl.vertDist), vertStart.y, pl.getVertDiameter(), pl.getVertDiameter());
                ig2.fill(circle);
            }
        }
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        if (!bw) {
            if (fg.getSize() > 0) {
                ig2.setFont(fontBold);
                ig2.drawString("N", vertStart.x - pl.vertDist, vertStart.y + 20);
                ig2.drawString("C", vertStart.x + numVerts * pl.vertDist, vertStart.y + 20);
                ig2.setFont(font);
            }
        }
        if (Settings.getBoolean("plcc_B_graphimg_footer")) {
            Integer printNth = 1;
            if (fg.getSize() > 99) {
                printNth = 2;
            }
            if (fg.getSize() > 999) {
                printNth = 3;
            }
            if (fg.getSize() > 0) {
                ig2.drawString("FG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                ig2.setPaint(Color.LIGHT_GRAY);
                ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + lineHeight + (stringHeight / 4));
                ig2.setPaint(Color.BLACK);
            } else {
                ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
            }
            ig2.setPaint(Color.BLACK);
            for (Integer i = startVertexInParent; i <= endVertexInParent; i++) {
                if ((i + 1) % printNth == 0) {
                    sseNumberFoldingGraph = "" + (parentVertexPosInFG.get(i) >= 0 ? (parentVertexPosInFG.get(i) + 1) : "");
                    sseNumberSeq = "" + (pg.sseList.get(i).getSSESeqChainNum());
                    sseNumberProteinGraph = "" + (i + 1);
                    stringHeight = fontMetrics.getAscent();
                    ig2.drawString(sseNumberFoldingGraph, pl.getFooterStart().x + ((i - shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
                    ig2.setPaint(Color.LIGHT_GRAY);
                    ig2.drawString(sseNumberSeq, pl.getFooterStart().x + ((i - shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                    ig2.drawString(sseNumberProteinGraph, pl.getFooterStart().x + ((i - shiftBack) * pl.vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight * 2) + (stringHeight / 4));
                    ig2.setPaint(Color.BLACK);
                }
            }
            if (Settings.getBoolean("plcc_B_graphimg_legend")) {
                ProteinGraphDrawer.drawLegendSEQ(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + (lineHeight * 3) + (stringHeight / 4)), pl, fg);
            }
        }
        Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
        DrawResult drawRes = new DrawResult(ig2, roi);
        return drawRes;
    }

    /**
     * Draws a KEY folding graph in all formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param drawBlackAndWhite whether to omit colors, only useful for non-protein graphs
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @param pnfr
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<DrawTools.IMAGEFORMAT, String> drawFoldingGraphKEY(String baseFilePathNoExt, Boolean drawBlackAndWhite, DrawTools.IMAGEFORMAT[] formats, PTGLNotationFoldResult pnfr) {
        HashMap<DrawTools.IMAGEFORMAT, String> resultFilesByFormat = new HashMap<DrawTools.IMAGEFORMAT, String>();
        if (!pnfr.getFoldingGraph().supportsKeyNotation()) {
            if (pnfr.keyNotation != null || pnfr.keyStartFG >= 0) {
                DP.getInstance().e("SSEGraph", "drawFoldingGraphKEY: FG reported to NOT support KEY notation, but a KEY notation string is set: '" + pnfr.keyNotation + "', start index is parent is " + pnfr.keyStartFG + ".");
            }
            if (!Settings.getBoolean("plcc_B_silent")) {
                System.out.println("          Output KEY folding graph files: none, FG does not support KEY notation.");
            }
            return resultFilesByFormat;
        }
        DrawResult drawRes = ProteinGraphDrawer.drawFoldingGraphKEYG2D(pnfr);
        if (drawRes == null) {
            return resultFilesByFormat;
        }
        String svgFilePath = baseFilePathNoExt + ".svg";
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(DrawTools.IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write KEY folding graph file : '" + ex.getMessage() + "'.");
        }
        if (!Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("        Output KEY folding graph files: ");
            for (DrawTools.IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }

    /**
     * Draws the KEY folding graph image of this graph and returns the DrawResult. This is the new version, started JAN 2015.
     * If 'nonProteinGraph' is true, this graph is considered a custom (=non-protein) graph and the color coding for vertices and edges is NOT used.
     * In that case, the graph is drawn black and white and the labels for the N- and C-termini are NOT drawn.
     *
     * See http://ptgl.uni-frankfurt.de/cgi-bin/showpict.pl?topology=a&rep=3&protlist=7timA+7timB+&nmrlist=
     * and http://ptgl.uni-frankfurt.de/ptglhelp.html#key
     *
     * See the KEY beta FG #3 of 1GOS chain A as an example.
     *
     * @param pnfr a folding graph notation result
     * @return the DrawResult. You can write this to a file or whatever.
     */
    private static DrawResult drawFoldingGraphKEYG2D(PTGLNotationFoldResult pnfr) {
        FoldingGraph fg = pnfr.getFoldingGraph();
        SSEGraph pg = fg.parent;
        boolean debug = false;
        /*
        if(pg.pdbid.equals("8icd") && pg.chainid.equals("A")) {
        if(fg.graphType.equals("beta") && fg.getFoldingGraphNumber().equals(1)) {
        debug = true;
        }
        }
         */
        if (debug) {
            System.out.println("******************** START *********************");
        }
        if (fg.isBifurcated()) {
            DP.getInstance().e("SSEGraph", "drawFoldingGraphKEYG2D: This FG is bifurcated, KEY notation not supported.");
            return null;
        }
        Integer leftMostVertexInParent = fg.getMinimalVertexIndexInParentGraph();
        Integer rightMostVertexInParent = fg.getMaximalVertexIndexInParentGraph();
        List<Integer> keyposParentIndicesSeqOrder = pnfr.keypos;
        Integer keystartFGIndex = pnfr.keyStartFG;
        if (keystartFGIndex == null) {
            DP.getInstance().e("SSEGraph", "drawFoldingGraphKEYG2D: keystartFGIndex is null");
        }
        fg.computeSpatialVertexOrdering();
        ArrayList<Integer> keyposFGIndicesSpatOrder = fg.spatOrder;
        if (keyposFGIndicesSpatOrder.size() != fg.getSize()) {
            if (fg.isASingleCycle()) {
                keyposFGIndicesSpatOrder = fg.getSpatialOrderingOfVertexIndicesForSingleCycleFG(keystartFGIndex);
                fg.spatOrder = keyposFGIndicesSpatOrder;
                if (keyposFGIndicesSpatOrder.size() != fg.getSize()) {
                    DP.getInstance().e("SSEGraph", "Could not draw KEY notation: Spatorder size " + keyposFGIndicesSpatOrder.size() + " of chain " + fg.chainid + " gt " + fg.graphType + " FG " + fg.getFoldingGraphFoldName() + " (#" + fg.getFoldingGraphNumber() + ") does not match FG size " + fg.getSize() + " even when allowing circles. Parent verts of FG: " + IO.intListToString(fg.getVertexIndexListInParentGraph()) + ". KEY='" + pnfr.keyNotation + "'. " + (fg.isASingleCycle() ? "FG is a single cycle." : "FG is NOT a single cycle."));
                    return null;
                } else {
                    //System.err.println("Single cycle handling fixed it.");
                }
            } else {
                DP.getInstance().e("SSEGraph", "Could not draw KEY notation: Spatorder size " + keyposFGIndicesSpatOrder.size() + " of chain " + fg.chainid + " gt " + fg.graphType + " FG " + fg.getFoldingGraphFoldName() + " (#" + fg.getFoldingGraphNumber() + ") does not match FG size " + fg.getSize() + ". Parent verts of FG: " + IO.intListToString(fg.getVertexIndexListInParentGraph()) + ". KEY='" + pnfr.keyNotation + "'. " + (fg.isASingleCycle() ? "FG is a single cycle." : "FG is NOT a single cycle."));
                return null;
            }
        }
        Integer keyendFGIndex = keyposFGIndicesSpatOrder.get(fg.getSize() - 1);
        if (!keystartFGIndex.equals(keyposFGIndicesSpatOrder.get(0))) {
            System.err.println("WARNING: Draw folding graph KEY notation: spatial ordering does no start with KEY start vertex.");
        }
        if (debug) {
            DP.getInstance().d("keyposFGIndicesSpatOrder=" + IO.intListToString(keyposFGIndicesSpatOrder));
        }
        List<Integer> spatOrderseqDistToPrevious = new ArrayList<>();
        spatOrderseqDistToPrevious.add(null);
        Integer cur;
        Integer last;
        Integer spatPosCur;
        Integer spatPosLast;
        for (Integer i = 1; i < fg.getSize(); i++) {
            cur = i;
            last = i - 1;
            spatPosCur = keyposFGIndicesSpatOrder.indexOf(cur);
            spatPosLast = keyposFGIndicesSpatOrder.indexOf(last);
            spatOrderseqDistToPrevious.add(spatPosCur - spatPosLast);
        }
        List<Integer> fgVertexPosInParent = fg.getVertexIndexListInParentGraph();
        List<Integer> parentVertexPosInFG = ProtGraph.computeParentGraphVertexPositionsInFoldingGraph(fgVertexPosInParent, pg.size);
        Integer numVerts = fg.getSize();
        Boolean bw = false;
        PageLayout pl = new PageLayout(numVerts);
        pl.isForKEY = true;
        Position2D vertStart = pl.getVertStart();
        Integer lineHeight = pl.textLineHeight;
        Integer vertDist = 50;
        Integer vertHeight = 80;
        Integer vertWidth = 40;
        Integer vertStartX = pl.getVertStart().x;
        Integer vertStartY = pl.getVertStart().y;
        SVGGraphics2D ig2;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        ig2 = new SVGGraphics2D(document);
        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ig2.setPaint(Color.WHITE);
        ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
        ig2.setPaint(Color.BLACK);
        Font font = pl.getStandardFont();
        Font fontBold = pl.getStandardFontBold();
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();
        String proteinHeader = "The KEY " + pg.graphType + " folding graph " + fg.getFoldingGraphFoldName() + " (# " + fg.getFoldingGraphNumber() + ") of PDB entry " + pg.pdbid + ", chain " + pg.chainid + " [V=" + fg.numVertices() + ", E=" + fg.numSSEContacts() + "].";
        String notation = "KEY notation: '" + pnfr.keyNotation + "'";
        if (debug) {
            DP.getInstance().d("Notation from PNFR: " + notation);
        }
        Integer stringHeight = fontMetrics.getAscent();
        String sseNumberSeqInChain;
        String sseNumberFoldingGraph;
        String sseNumberProteinGraph;
        if (Settings.getBoolean("plcc_B_graphimg_header")) {
            ig2.drawString(proteinHeader, pl.headerStart.x, pl.headerStart.y);
        }
        Integer[] shiftBack = new Integer[pg.size];
        Integer shift = 0;
        for (int i = 0; i < parentVertexPosInFG.size(); i++) {
            if (parentVertexPosInFG.get(i) < 0) {
                shift++;
            }
            shiftBack[i] = shift;
        }
        boolean debugDrawingKEY = false;
        //if(fg.toShortString().equals("1gos-B-alpha-FG0[4V,3E]")) {
        Integer[] orientationsSpatOrder = new Integer[fg.size];
        Integer[] orientationsSeqOrder = new Integer[fg.size];
        List<Integer> keyposFGIndices = new ArrayList<>();
        Integer v;
        for (int i = 0; i < keyposParentIndicesSeqOrder.size(); i++) {
            v = parentVertexPosInFG.get(keyposParentIndicesSeqOrder.get(i));
            if (v < 0) {
                DP.getInstance().e("SSEGRaph", "Keypos parent index #" + i + " is " + v + ", skipping.");
            } else {
                keyposFGIndices.add(v);
            }
        }
        if (keyposFGIndices.size() != fg.getSize()) {
            DP.getInstance().e("SSEGRaph", "keyposFGIndices.size()=" + keyposFGIndices.size() + ", fg.size=" + fg.size + ".");
        }
        Integer firstVertexSpatFGIndex = fg.spatOrder.get(0);
        orientationsSpatOrder[0] = FoldingGraph.ORIENTATION_UPWARDS;
        orientationsSeqOrder[firstVertexSpatFGIndex] = FoldingGraph.ORIENTATION_UPWARDS;
        if (debugDrawingKEY) {
            System.out.println("Setting orientationsSeqOrder[" + firstVertexSpatFGIndex + "] to " + FoldingGraph.ORIENTATION_UPWARDS + ".");
        }
        Integer currentVert;
        Integer lastVert;
        StringBuilder KEYNotation = new StringBuilder();
        Boolean appendSSEType = true;
        String fgGraphtType = pnfr.getFoldingGraph().graphType;
        if (fgGraphtType.equals(SSEGraph.GRAPHTYPE_ALPHA) || fgGraphtType.equals(SSEGraph.GRAPHTYPE_BETA)) {
        }
        String bracketStart = "[";
        String bracketEnd = "]";
        if (fg.isBifurcated()) {
            bracketStart = "{";
            bracketEnd = "}";
        } else if (fg.isASingleCycle()) {
            bracketStart = "(";
            bracketEnd = ")";
        }
        KEYNotation.append(bracketStart);
        if (appendSSEType) {
            KEYNotation.append(fg.getVertex(keystartFGIndex).getLinearNotationLabel());
        }
        if (keyposFGIndices.size() > 1) {
            for (int i = 1; i < keyposFGIndicesSpatOrder.size(); i++) {
                if (i < (keyposFGIndicesSpatOrder.size())) {
                    if (!appendSSEType && i == 1) {
                    } else {
                        KEYNotation.append(",");
                    }
                }
                currentVert = keyposFGIndicesSpatOrder.get(i);
                lastVert = keyposFGIndicesSpatOrder.get(i - 1);
                KEYNotation.append(currentVert - lastVert);
                Integer spatRel = fg.getContactType(lastVert, currentVert);
                if (Objects.equals(spatRel, SpatRel.PARALLEL)) {
                    KEYNotation.append("x");
                    orientationsSpatOrder[i] = orientationsSpatOrder[i - 1];
                    orientationsSeqOrder[currentVert] = orientationsSpatOrder[i];
                } else {
                    orientationsSpatOrder[i] = (Objects.equals(orientationsSpatOrder[i - 1], FoldingGraph.ORIENTATION_UPWARDS) ? FoldingGraph.ORIENTATION_DOWNWARDS : FoldingGraph.ORIENTATION_UPWARDS);
                    orientationsSeqOrder[currentVert] = orientationsSpatOrder[i];
                }
                if (appendSSEType) {
                    KEYNotation.append(fg.getVertex(currentVert).getLinearNotationLabel());
                }
            }
        }
        KEYNotation.append(bracketEnd);
        if (debug) {
            DP.getInstance().d("Notation from this func: " + KEYNotation.toString());
        }
        Integer[] newOrientations = new Integer[fg.spatOrder.size()];
        Arrays.fill(newOrientations, FoldingGraph.ORIENTATION_NONE);
        Integer currentVertexIndexInFGSequential;
        List<Shape> connShapes;
        Polygon pol;
        Position2D p;
        Position2D lastP;
        for (int i = 0; i < fg.spatOrder.size(); i++) {
            currentVertexIndexInFGSequential = fg.spatOrder.get(i);
            p = new Position2D(vertStartX + (i * vertDist) + pl.vertRadius / 2, vertStartY);
            Integer contactTypeInt = null;
            if (i > 0) {
                contactTypeInt = fg.getContactType(fg.spatOrder.get(i - 1), fg.spatOrder.get(i));
                if (contactTypeInt.equals(SpatRel.PARALLEL)) {
                    newOrientations[i] = newOrientations[i - 1];
                } else {
                    newOrientations[i] = (Objects.equals(newOrientations[i - 1], FoldingGraph.ORIENTATION_UPWARDS) ? FoldingGraph.ORIENTATION_DOWNWARDS : FoldingGraph.ORIENTATION_UPWARDS);
                }
            } else {
                newOrientations[i] = FoldingGraph.ORIENTATION_UPWARDS;
            }
            ig2.setColor(Color.BLACK);
            if (fg.getVertex(currentVertexIndexInFGSequential).isBetaStrand()) {
                ig2.setColor(Color.BLACK);
                pol = DrawTools.getDefaultArrowPolygonLowestPointAt(p.x, p.y, newOrientations[i]);
            } else if (fg.getVertex(currentVertexIndexInFGSequential).isHelix()) {
                ig2.setColor(Color.RED);
                pol = DrawTools.getDefaultBarrelPolygonLowestPointAt(p.x, p.y);
            } else if (fg.getVertex(currentVertexIndexInFGSequential).isLigandSSE()) {
                ig2.setColor(Color.MAGENTA);
                pol = DrawTools.getDefaultBarrelPolygonLowestPointAt(p.x, p.y);
            } else {
                ig2.setColor(Color.LIGHT_GRAY);
                pol = DrawTools.getDefaultBarrelPolygonLowestPointAt(p.x, p.y);
            }
            ig2.draw(pol);
            ig2.fill(pol);
            ig2.setColor(Color.BLACK);
            ig2.drawString((currentVertexIndexInFGSequential + 1) + "", pl.getFooterStart().x + (i * vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (stringHeight / 4));
            List<Integer> parentMapping = fg.getVertexIndexListInParentGraph();
            sseNumberProteinGraph = "" + (parentMapping.get(currentVertexIndexInFGSequential) + 1);
            sseNumberSeqInChain = "" + (pg.getSSEBySeqPosition(parentMapping.get(currentVertexIndexInFGSequential)).getSSESeqChainNum());
            ig2.setColor(Color.LIGHT_GRAY);
            ig2.drawString(sseNumberSeqInChain, pl.getFooterStart().x + (i * vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
            ig2.drawString(sseNumberProteinGraph, pl.getFooterStart().x + (i * vertDist) + pl.vertRadius / 2, pl.getFooterStart().y + (lineHeight * 2) + (stringHeight / 4));
            ig2.setColor(Color.BLACK);
        }
        if (debug) {
            System.out.print("Orientations: ");
            for (Integer o : newOrientations) {
                System.out.print(FoldingGraph.getOrientationString(o) + " ");
            }
            System.out.println("");
        }
        StringBuilder testKEY = new StringBuilder();
        String[] newKey = new String[fg.size - 1];
        Arrays.fill(newKey, "?");
        ig2.setColor(Color.BLACK);
        for (int i = 0; i < fg.spatOrder.size(); i++) {
            currentVertexIndexInFGSequential = fg.spatOrder.get(i);
            p = new Position2D(vertStartX + (i * vertDist) + pl.vertRadius / 2, vertStartY);
            Integer previousVertexIndexInFGSequential;
            Integer previousVertexIndexSpatial;
            Integer currentVertexIndexSpatial;
            Integer relDist;
            String relDistString;
            List<Shape> shapes;
            if (currentVertexIndexInFGSequential > 0) {
                previousVertexIndexInFGSequential = currentVertexIndexInFGSequential - 1;
                currentVertexIndexSpatial = fg.spatOrder.indexOf(currentVertexIndexInFGSequential);
                previousVertexIndexSpatial = fg.spatOrder.indexOf(previousVertexIndexInFGSequential);
                relDist = currentVertexIndexSpatial - previousVertexIndexSpatial;
                relDistString = relDist + "";
                if (appendSSEType) {
                    relDistString += fg.getVertex(currentVertexIndexInFGSequential).getLinearNotationLabel();
                }
                newKey[previousVertexIndexInFGSequential] = relDistString;
                if (debug) {
                    System.out.println("EDGE (i=" + i + ") This is vert " + currentVertexIndexInFGSequential + " at position " + currentVertexIndexSpatial + ", last vert sequential was " + previousVertexIndexInFGSequential + " at position " + previousVertexIndexSpatial + ".");
                }
                lastP = new Position2D(vertStartX + (previousVertexIndexSpatial * vertDist) + pl.vertRadius / 2, vertStartY);
                Integer lastOrientation = newOrientations[previousVertexIndexSpatial];
                Integer currentOrientation = newOrientations[currentVertexIndexSpatial];
                if (debug) {
                    System.out.println("  lastOrientation was: " + FoldingGraph.getOrientationString(lastOrientation) + ", current is " + FoldingGraph.getOrientationString(currentOrientation) + ". relDist(" + previousVertexIndexSpatial + " to " + currentVertexIndexSpatial + ")=" + relDist + ".");
                }
                if (lastOrientation.equals(currentOrientation)) {
                    if (lastOrientation.equals(FoldingGraph.ORIENTATION_UPWARDS)) {
                        if (debug) {
                            ig2.setColor(Color.RED);
                        }
                        if (Settings.getBoolean("plcc_B_key_use_alternate_arcs")) {
                            shapes = DrawTools.getCrossoverArcConnectorAlternativeBezierVersion(lastP.x, lastP.y - vertHeight, p.x, p.y, ig2.getStroke(), true, 0);
                        } else {
                            shapes = DrawTools.getCrossoverArcConnector(lastP.x, lastP.y - vertHeight, p.x, p.y, ig2.getStroke(), true, 0);
                        }
                    } else {
                        if (debug) {
                            ig2.setColor(Color.ORANGE);
                        }
                        if (Settings.getBoolean("plcc_B_key_use_alternate_arcs")) {
                            shapes = DrawTools.getCrossoverArcConnectorAlternativeBezierVersion(lastP.x, lastP.y, p.x, p.y - vertHeight, ig2.getStroke(), false, 0);
                        } else {
                            shapes = DrawTools.getCrossoverArcConnector(lastP.x, lastP.y, p.x, p.y - vertHeight, ig2.getStroke(), false, 0);
                        }
                    }
                } else {
                    if (currentOrientation.equals(FoldingGraph.ORIENTATION_UPWARDS)) {
                        if (debug) {
                            ig2.setColor(Color.BLUE);
                        }
                        shapes = DrawTools.getArcConnector(lastP.x, lastP.y, p.x, p.y, ig2.getStroke(), false, 0);
                    } else {
                        if (debug) {
                            ig2.setColor(Color.GREEN);
                        }
                        shapes = DrawTools.getArcConnector(lastP.x, lastP.y - vertHeight, p.x, p.y - vertHeight, ig2.getStroke(), true, 0);
                    }
                }
                for (Shape s : shapes) {
                    ig2.draw(s);
                }
                ig2.setColor(Color.BLACK);
            }
            Integer terminusCurrentVertexIndexSpatial = fg.spatOrder.indexOf(currentVertexIndexInFGSequential);
            Integer terminusCurrentOrientation = newOrientations[terminusCurrentVertexIndexSpatial];
            ig2.setFont(fontBold);
            if (currentVertexIndexInFGSequential.equals(0)) {
                ig2.setColor(Color.BLACK);
                ig2.drawString("N", p.x - 5, p.y + 25);
            }
            if (currentVertexIndexInFGSequential.equals(fg.spatOrder.size() - 1)) {
                ig2.setColor(Color.BLACK);
                if (terminusCurrentOrientation.equals(FoldingGraph.ORIENTATION_UPWARDS)) {
                    ig2.drawString("C", p.x - 5, p.y - 90);
                } else {
                    ig2.drawString("C", p.x - 5, p.y + 25);
                }
            }
            ig2.setFont(font);
        }
        testKEY.append(bracketStart);
        if (appendSSEType) {
            testKEY.append(fg.getVertex(fg.spatOrder.get(0)).getLinearNotationLabel());
            testKEY.append(",");
        }
        for (int i = 0; i < newKey.length; i++) {
            testKEY.append(newKey[i]);
            if (i < (newKey.length - 1)) {
                testKEY.append(",");
            }
        }
        testKEY.append(bracketEnd);
        if (debug) {
            System.out.println("newKEY = " + testKEY.toString());
        }
        if (Settings.getBoolean("plcc_B_graphimg_header")) {
            if (Settings.getBoolean("plcc_B_print_notations_on_fg_images")) {
                ig2.drawString(notation, pl.headerStart.x, pl.headerStart.y + lineHeight);
            }
        }
        if (Settings.getBoolean("plcc_B_graphimg_footer")) {
            if (fg.getSize() > 0) {
                ig2.setPaint(Color.BLACK);
                ig2.drawString("FG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + (stringHeight / 4));
                ig2.setPaint(Color.LIGHT_GRAY);
                ig2.drawString("SQ", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + (stringHeight / 4));
                ig2.drawString("PG", pl.getFooterStart().x - pl.vertDist, pl.getFooterStart().y + lineHeight + lineHeight + (stringHeight / 4));
                ig2.setPaint(Color.BLACK);
            } else {
                ig2.drawString("(Graph has no vertices.)", pl.getFooterStart().x, pl.getFooterStart().y);
            }
            if (Settings.getBoolean("plcc_B_graphimg_legend")) {
                ProteinGraphDrawer.drawLegendKEY(ig2, new Position2D(pl.getFooterStart().x, pl.getFooterStart().y + (lineHeight * 3) + (stringHeight / 4)), pl, fg);
            }
        }
        Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
        DrawResult drawRes = new DrawResult(ig2, roi);
        if (debug) {
            System.out.println("******************** END *********************");
        }
        return drawRes;
    }

    /**
     * Draws an DEF folding graph in all image formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param drawBlackAndWhite whether to omit colors, only useful for non-protein graphs
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @param pnfr
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<DrawTools.IMAGEFORMAT, String> drawFoldingGraphDEF(String baseFilePathNoExt, Boolean drawBlackAndWhite, DrawTools.IMAGEFORMAT[] formats, PTGLNotationFoldResult pnfr) {
        DrawResult drawRes = ProteinGraphDrawer.drawFoldingGraphDEFG2D(pnfr);
        String svgFilePath = baseFilePathNoExt + ".svg";
        HashMap<DrawTools.IMAGEFORMAT, String> resultFilesByFormat = new HashMap<DrawTools.IMAGEFORMAT, String>();
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(DrawTools.IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write DEF folding graph file : '" + ex.getMessage() + "'.");
        }
        if (!Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("          Output DEF folding graph files: ");
            for (DrawTools.IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }

    /**
     * Draws the symbol for SSEs of type other at the given position.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position where to draw
     * @param pl the PageLayout to use (determines the width and height)
     */
    protected static void drawSymbolOtherSSE(SVGGraphics2D ig2, Position2D startPos, PageLayout pl) {
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.GRAY);
        Ellipse2D.Double circle = new Ellipse2D.Double(startPos.x, startPos.y, pl.getVertDiameter(), pl.getVertDiameter());
        ig2.fill(circle);
    }

    /**
     * Draws the 3 symbols for parent graph SSEs (gray alpha/strand/ligand).
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position where to draw
     * @param pl the PageLayout to use (determines the width and height)
     */
    protected static void drawSymbolsParentSSEs(SVGGraphics2D ig2, Position2D startPos, PageLayout pl) {
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.GRAY);
        Ellipse2D.Double circle = new Ellipse2D.Double(startPos.x, startPos.y, pl.getVertDiameter(), pl.getVertDiameter());
        ig2.fill(circle);
        Rectangle2D.Double rect = new Rectangle2D.Double(startPos.x + 25, startPos.y, pl.getVertDiameter(), pl.getVertDiameter());
        ig2.fill(rect);
        ig2.setStroke(new BasicStroke(3));
        Ellipse2D.Double circle2 = new Ellipse2D.Double(startPos.x + 50, startPos.y, pl.getVertDiameter(), pl.getVertDiameter());
        ig2.draw(circle2);
        ig2.setStroke(new BasicStroke(2));
    }

    /**
     * Draws the symbol for a ligand at the given position.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position where to draw
     * @param pl the PageLayout to use (determines the width and height)
     */
    protected static void drawSymbolLigand(SVGGraphics2D ig2, Position2D startPos, PageLayout pl) {
        ig2.setStroke(new BasicStroke(3));
        ig2.setPaint(Color.MAGENTA);
        Ellipse2D.Double circle = new Ellipse2D.Double(startPos.x, startPos.y, pl.getVertDiameter(), pl.getVertDiameter());
        ig2.draw(circle);
        ig2.setStroke(new BasicStroke(2));
    }

    /**
     * Draws the symbol for an alpha helix at the given position.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position where to draw
     * @param pl the PageLayout to use (determines the width and height)
     */
    protected static void drawSymbolAlphaHelix(SVGGraphics2D ig2, Position2D startPos, PageLayout pl) {
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.RED);
        Ellipse2D.Double circle = new Ellipse2D.Double(startPos.x, startPos.y, pl.getVertDiameter(), pl.getVertDiameter());
        ig2.fill(circle);
    }

    /**
     * Draws the symbol for a beta strand at the given position.
     * @param ig2 the SVGGraphics2D object on which to draw
     * @param startPos the start position where to draw
     * @param pl the PageLayout to use (determines the width and height)
     */
    protected static void drawSymbolBetaStrand(SVGGraphics2D ig2, Position2D startPos, PageLayout pl) {
        ig2.setStroke(new BasicStroke(2));
        ig2.setPaint(Color.BLACK);
        Rectangle2D.Double rect = new Rectangle2D.Double(startPos.x, startPos.y, pl.getVertDiameter(), pl.getVertDiameter());
        ig2.fill(rect);
    }
    
}
