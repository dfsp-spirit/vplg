/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * This is a utility class, which draws linear notationss of FGs based on the linnot string (instead of based on the PG + FG datastructures).
 * @author ts
 */
public class LinnotDrawer {
    
    private static String getDefaultSSE(String graphType) {
        if(graphType.equals(SSEGraph.GRAPHTYPE_ALPHA)) {
            return SSEGraph.notationLabelHelix;
        }
        if(graphType.equals(SSEGraph.GRAPHTYPE_BETA)) {
            return SSEGraph.notationLabelStrand;
        }
        return "?";
    }
    
    private static String stripAllBrackets(String linnot) {
        linnot = linnot.replace("(", "");
        linnot = linnot.replace(")", "");
        linnot = linnot.replace("[", "");
        linnot = linnot.replace("]", "");
        linnot = linnot.replace("{", "");
        linnot = linnot.replace("}", "");
        return linnot;
    }
    
    private static String[] getTokensFromLinnot(String linnot) {
        linnot = LinnotDrawer.stripAllBrackets(linnot);
        String[] tokens = linnot.split(",");
        return tokens;
    }
    
    private static String getSSETypeFromToken(String token, String graphType) {
        String[] knownTypes = new String [] { "h", "e", "l"  };
        for(String s : knownTypes) {
            if(token.contains(s)) {
                return s;
            }
        }
        return LinnotDrawer.getDefaultSSE(graphType);        
    }
    
    private static List<String> getSSETypesFromLinnot(String linnot) {
        List<String> types = new ArrayList<String>();
        return types;
    }
    
    public static DrawResult drawLinnotStringADJ(String linnot, String graphType) {
        
        // the default SSE type to assume if not SSE type is given in vertex descriptor string (the 'h' can be omitted for alpha graphs in the linnot string).
        String defaultSSEType = LinnotDrawer.getDefaultSSE(graphType);
        
        // --------------- prepare stuff ---------------
        
        PageLayout pl = new PageLayout(5);  // 5 is num of verts
        Rectangle2D roi = new Rectangle2D.Double(0, 0, pl.getPageWidth(), pl.getPageHeight());
        SVGGraphics2D ig2;                                   
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        ig2 = new SVGGraphics2D(document);          
        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // make background white
        ig2.setPaint(Color.WHITE);
        ig2.fillRect(0, 0, pl.getPageWidth(), pl.getPageHeight());
        ig2.setPaint(Color.BLACK);

        // prepare font
        Font font = pl.getStandardFont();
        Font fontBold = pl.getStandardFontBold();
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();
        
        // ------------- start drawing -----------

        
        // ------------- end drawing -------------
            
        DrawResult drawRes = new DrawResult(ig2, roi);
        return drawRes; 
    }
    
}
