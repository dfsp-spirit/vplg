/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphdrawing;

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
import plcc.ILinnotParser;
import plcc.LinnotParser;

/**
 * This is a utility class, which draws linear notationss of FGs based on the linnot string (instead of based on the PG + FG datastructures).
 * @author ts
 */
public class LinnotDrawer {
    
    
    
    
    
    public static DrawResult drawLinnotStringADJ(String linnot, String graphType) {
        
        // the default SSE type to assume if not SSE type is given in vertex descriptor string (the 'h' can be omitted for alpha graphs in the linnot string).
        ILinnotParser lnp = new LinnotParser(linnot, graphType);

        List<String> sseTypes = lnp.getSSETypesList();
        List<String> contactTypes = lnp.getContactTypesList();
        List<Integer> relDists = lnp.getRelDistList();
        
        int numVerts = lnp.getNumSSEs();
        
        // --------------- prepare stuff ---------------
        
        PageLayout pl = new PageLayout(numVerts);
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
