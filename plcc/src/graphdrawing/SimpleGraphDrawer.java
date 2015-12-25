/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphdrawing;

import algorithms.GraphProperties;
import datastructures.SimpleGraphInterface;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import plcc.Settings;
import tools.DP;

/**
 * A drawing class which draws a simple graph. Vertices are arranged on an x,y grid, and edges are drawn between them as straight lines.
 * @author spirit
 */
public class SimpleGraphDrawer {
    
    protected static Integer MIN_WIDTH = 540;
    protected static Integer MIN_HEIGHT = 200;
    protected static Integer WIDTH = 540;
    protected static Integer HEIGHT = 200;
    protected static Integer BORDER = 20;
    protected static Integer VERT_START_X = BORDER;
    protected static Integer VERT_START_Y = BORDER;
    protected static Integer VERT_DIST_VERTICAL = 20;
    protected static Integer VERT_DIST_HORIZONTAL = 10;
    protected static Integer VERTS_PER_LINE = 50;
    protected static Integer VERT_WIDTH_HEIGHT = 4;
    protected static Integer VERT_HALF_WIDTH_HEIGHT = 2;
    
    
    public static Integer SETTING_MIN_SEQ_DIST_TO_DRAW = 5;
    public static Integer SETTING_MIN_DEGREE_TO_DRAW = 10;
    
    public static Font getStandardFont() {
        return(new Font(Settings.get("plcc_S_img_default_font"), Font.PLAIN, Settings.getInteger("plcc_I_img_default_font_size")));
    }
    
    private static Integer[] vertGridPosition(int i) {
        Integer x = i % VERTS_PER_LINE;
        Integer y = Math.floorDiv(i, VERTS_PER_LINE);
        //System.out.println("gridPos of vert "+ i + ": " + x + " " + y + "(vpl=" + VERTS_PER_LINE+")");
        return new Integer[]{x, y};
    }
    
    private static Integer[] pixelPosOfGridPosition(Integer[] gridPos) {
        return new Integer[]{VERT_START_X + (gridPos[0] * VERT_DIST_HORIZONTAL), VERT_START_Y + (gridPos[1] * VERT_DIST_VERTICAL)};
    }
    
    
    /**
     * Draws a protein graph in all formats, returns a list of written files.
     * @param baseFilePathNoExt the base file path where to put the image (without dot and file extension)
     * @param formats an array of type DrawTools.IMAGEFORMAT. Do not include SVG, this will always be drawn anyways
     * @param pg the graph to draw
     * @return a map of formats to the corresponding output files written to disk
     */
    public static HashMap<DrawTools.IMAGEFORMAT, String> drawSimpleGraph(String baseFilePathNoExt, DrawTools.IMAGEFORMAT[] formats, SimpleGraphInterface pg, Map<Integer, Color> vertexColors) {
        
        
        
        DrawResult drawRes = SimpleGraphDrawer.drawSimpleGraphG2D(pg, vertexColors);
        String svgFilePath = baseFilePathNoExt + ".svg";
        HashMap<DrawTools.IMAGEFORMAT, String> resultFilesByFormat = new HashMap<DrawTools.IMAGEFORMAT, String>();
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            resultFilesByFormat.put(DrawTools.IMAGEFORMAT.SVG, svgFilePath);
            resultFilesByFormat.putAll(DrawTools.convertSVGFileToOtherFormats(svgFilePath, baseFilePathNoExt, drawRes, formats));
        } catch (IOException ex) {
            DP.getInstance().e("Could not write simple graph file : '" + ex.getMessage() + "'.");
        }
        if (!Settings.getBoolean("plcc_B_silent")) {
            StringBuilder sb = new StringBuilder();
            sb.append("      Output simple graph files: ");
            for (DrawTools.IMAGEFORMAT format : resultFilesByFormat.keySet()) {
                sb.append("(").append(format.toString()).append(" => ").append(resultFilesByFormat.get(format)).append(") ");
            }
            System.out.println(sb.toString());
        }
        return resultFilesByFormat;
    }
    
    private static DrawResult drawSimpleGraphG2D(SimpleGraphInterface pg, Map<Integer, Color> vertexColors) {
       
        Integer maxPixY = pixelPosOfGridPosition(vertGridPosition(pg.getSize()-1))[1];
        if(maxPixY > HEIGHT) {
            HEIGHT = maxPixY + BORDER;
        }
        
        SVGGraphics2D ig2;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        ig2 = new SVGGraphics2D(document);
        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ig2.setPaint(Color.WHITE);
        ig2.fillRect(0, 0, WIDTH, HEIGHT);
        ig2.setPaint(Color.BLACK);
        Font font = getStandardFont();
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();        
        Integer stringHeight = fontMetrics.getAscent();   
      
        Shape shape;
        Line2D line;
        ig2.setStroke(new BasicStroke(2));     
        
        Color C_VERTEX = Color.RED;
        Color C_EDGE = new Color(128, 128, 128, 128);
        
        Integer x = VERT_START_X;
        Integer y = VERT_START_Y;
        
        GraphProperties gp = new GraphProperties(pg);
        Integer maxDegree = gp.getMaxDegree();
      
        for (Integer i = 0; i < pg.getSize(); i++) {            
            ig2.setColor(vertexColors.get(i));
            Integer[] pixPos = pixelPosOfGridPosition(vertGridPosition(i));
            shape = new Rectangle(pixPos[0] - VERT_HALF_WIDTH_HEIGHT, pixPos[1] - VERT_HALF_WIDTH_HEIGHT, VERT_WIDTH_HEIGHT, VERT_WIDTH_HEIGHT);
            ig2.draw(shape);
        }
        
        Integer diff, degree1, degree2;
        for (Integer i = 0; i < pg.getSize(); i++) {
            for (Integer j = i + 1; j < pg.getSize(); j++) {
                if (pg.containsEdge(i, j)) {  
                    diff = Math.abs(i-j);
                    degree1 = pg.neighborsOf(i).size();
                    degree2 = pg.neighborsOf(j).size();
                    if(diff > SETTING_MIN_SEQ_DIST_TO_DRAW && (degree1 >= SETTING_MIN_DEGREE_TO_DRAW || degree2 >= SETTING_MIN_DEGREE_TO_DRAW)) {
                        Integer largerDegree = (degree1 > degree2 ? degree1 : degree2);
                        Double degreeP = largerDegree.doubleValue()/ maxDegree.doubleValue();
                        Integer transp = ((Double)Math.floor((degreeP / maxDegree) * 200)).intValue();
                        //ig2.setPaint(C_EDGE);
                        ig2.setPaint(new Color(128 , 128, 128, 55+transp));
                        ig2.setStroke(new BasicStroke(1));
                        Integer[] pixPosStart = pixelPosOfGridPosition(vertGridPosition(i));
                        Integer[] pixPosEnd = pixelPosOfGridPosition(vertGridPosition(j));


                        line = new Line2D.Double(pixPosStart[0], pixPosStart[1], pixPosEnd[0], pixPosEnd[1]);
                        //shape = ig2.getStroke().createStrokedShape(line);
                        //ig2.draw(shape);
                        ig2.draw(line);
                    }
                }
            }
        }
        Rectangle2D roi = new Rectangle2D.Double(0, 0, WIDTH, HEIGHT);
        DrawResult drawRes = new DrawResult(ig2, roi);
        return drawRes;
    }
    
}
