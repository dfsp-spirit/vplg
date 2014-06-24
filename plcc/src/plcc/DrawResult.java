/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

import java.awt.geom.Rectangle2D;
import org.apache.batik.svggen.SVGGraphics2D;

/**
 * A simple class that holds a draw result: a 2D canvas and the region of interest (which can be used to
 * write a viewport of the drawing to a file).
 * @author ts
 */
public class DrawResult {
    
    /** The graphics object, this has been draw to already. */
    public SVGGraphics2D g2d;
    
    /** The region of interest, i.e., the part of the canvas you should write to a file. */
    public Rectangle2D roi;    
    
    DrawResult(SVGGraphics2D g2d, Rectangle2D roi) {
        this.g2d = g2d;
        this.roi = roi;
    }
    
}
