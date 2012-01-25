/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

import java.awt.Font;

/**
 * A page layout that holds all non-SSE-specific information about the drawing process, e.g. the
 * position of header, footer, etc. An instance of this class is passed to a DrawSSE so it can determine
 * its own position.
 * 
 * This class uses various settings from the config file and computes the rest based on the number of 
 * vertices in the graph which is to be drawn on this page.
 * 
 * @author ts
 */
public class PageLayout {
    
    public Position2D headerStart;
    public Integer headerHeight;
    public Integer footerHeight;
    public Position2D imgStart;
    
    public Integer marginLeft;
    public Integer marginTop;
    public Integer marginRight;
    public Integer marginBottom; 
    
    public Integer vertDist;
    public Integer vertRadius;
    public Integer vertDiameter;
    
    public Integer minPageWidth;
    public Integer minPageHeight;
    
    // must not be changed after constructor
    private Integer numVerts;
    
    /**
     * Constructor. The margins are no set with this constructor, unless you set them later, the defaults from
     * the config file are used.
     * 
     * @param numVerts the number of vertices in the graph, this is required to determine the width and height of the image area and thus the entire image.
     */
    PageLayout(Integer numVerts) {
        
        this.numVerts = numVerts;
        
        this.marginLeft = Settings.getInteger("plcc_I_img_margin_left");
        this.marginTop = Settings.getInteger("plcc_I_img_margin_top");
        this.marginRight = Settings.getInteger("plcc_I_img_margin_right");
        this.marginBottom = Settings.getInteger("plcc_I_img_margin_bottom");
        
        this.headerStart = new Position2D(marginLeft,  marginTop);        
        this.headerHeight = Settings.getInteger("plcc_I_img_header_height");
        this.footerHeight = Settings.getInteger("plcc_I_img_footer_height");
        
        this.imgStart = new Position2D(headerStart.x,  headerStart.y + headerHeight);
        
        
        this.minPageWidth = Settings.getInteger("plcc_I_img_minPageWidth");
        this.minPageHeight = Settings.getInteger("plcc_I_img_minPageHeight");                
        
        this.vertDist = Settings.getInteger("plcc_I_img_vert_dist");
        this.vertRadius = Settings.getInteger("plcc_I_img_vert_radius");
        this.vertDiameter = vertRadius * 2;
        
        
        
    }
    
    
    /**
     * Returns the number of vertices this page layout is made for.
     * @return the number of vertices
     */
    public Integer getNumVerts() {
        return(this.numVerts);
    }
        
    
    /**
     * Determines the coordinates of the footer start in the image.
     * @return the coordinates as a Position2D
     */
    public Position2D getFooterStart() {
        return(new Position2D(marginLeft, marginTop + headerHeight + this.getImageAreaHeight() + 40));
    }
    
    /**
     * Determines the maximum distance that can occur between a vertex pair of the image, i.e., the distance
     * from the first (left-most) to the last (right-most) vertex on the canvas. 
     * @return the theoretical maximum distance in pixels
     */
    public Integer getMaxVertDist() {
        return(this.numVerts * this.vertDist);    
    }
    
    /**
     * Determines the width of the image area (page = header + image + footer).
     * @return the width in pixels
     */
    public Integer getImageAreaWidth() {
        return(numVerts * vertDist + (2 * vertRadius) );
    }
    
    /**
     * Determines the height of the image area (page = header + image + footer).
     * @return the height in pixels
     */
    public Integer getImageAreaHeight() {
        return(this.getMaxArcHeight() + (2 * vertRadius) );
    }
    
    
    
    
    /**
     * Determines the maximum arc height that can occur in the image, i.e., the distance
     * from the first (left-most) to the last (right-most) vertex on the canvas divided by the width/height factor of an arc. 
     * @return the maximum arc height in pixels
     */
    public Integer getMaxArcHeight() {
        return(this.getMaxVertDist() / 4);    
    }
    
    
    /**
     * Determines the total page width (page = header + image + footer).
     * @return the width in pixels
     */
    public Integer getPageWidth() {
        Integer w = marginLeft + this.getImageAreaWidth() + marginRight;
        return(w < minPageWidth ? minPageWidth : w);
    }
    
    
    /**
     * Determines the total page height (page = header + image + footer).
     * @return the height in pixels
     */
    public Integer getPageHeight() {
        Integer h = marginTop + headerHeight + this.getImageAreaHeight() + footerHeight + marginBottom;
        return(h < minPageHeight ? minPageHeight : h);
    }
    
            
    /**
     * Returns the standard font which can be set in the config file.
     * @return the font as java.awt.Font.
     */
    public Font getStandardFont() {
        return(new Font(Settings.get("plcc_S_img_default_font"), Font.PLAIN, Settings.getInteger("plcc_I_img_default_font_size")));
    }                
    
        
    /**
     * Sets the page margins of this PageLayout in pixels. Since they are public, you can also set them manually and separately.
     * @param left the left margin, in pixels
     * @param top the top margin, in pixels
     * @param right the right margin, in pixels
     * @param bottom the bottom margin, in pixels
     */ 
    public void setMargins(Integer left, Integer top, Integer right, Integer bottom) {
        this.marginLeft = left;
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
    }    
    
    
    /**
     * Sets the page margins of this PageLayout in pixels. The given value is used for all 4 margins (left, top, right and bottom).
     * @param margin the margin, in pixels
     */
    public void setMargins(Integer margin) {
        this.marginLeft = margin;
        this.marginTop = margin;
        this.marginRight = margin;
        this.marginBottom = margin;
    }
    
    
    
    /**
     * Determines the position where the vertices should be drawn. This is within the image area, but of course NOT in the upper left corner. ;)
     * @return the position where to draw the left-most vertex
     */
    public Position2D getVertStart() {
        return(new Position2D(imgStart.x, imgStart.y + this.getMaxArcHeight()));
    }
    
}
