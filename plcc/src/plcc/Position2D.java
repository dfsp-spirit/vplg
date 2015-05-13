/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package plcc;

/**
 * Very simle helper class, implements a 2D position (x, y). You can think of this as a Point.
 * @author ts
 */
public class Position2D {
    
    /**
     * The x coordinate.
     */
    public Integer x;
    
    /**
     * The y coordinate.
     */
    public Integer y;
   
    /**
     * Position constructor.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Position2D(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }
    
    
    /**
     * Position constructor. Creates a new position with default coordinates (0,0).
     */ 
    public Position2D() {
        this.x = 0;
        this.y = 0;
    }

    
    /**
     * Changes the position by the values x, y. For example, if the current position is (5,5) and you call translate(2,-2), the new position will be (7,3).
     * @param x
     * @param y 
     */
    public void translateBy(Integer x, Integer y) {
        this.x += x;
        this.y += y;
    }
    
    
    /**
     * Updates this position to the specified coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void moveTo(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }       
    

    /**
     * Determines the rounded euclidian distance from this Position to Position p.
     * @param p the other position
     * @return the distance, rounded to integer
     */
    public Integer distanceTo(Position2D p) {
        Double distDouble = .0;
        Integer distInt, dx, dy = 0;

        dx = this.x - p.x;
        dy = this.y - p.y;


        distDouble = Math.sqrt(dx * dx + dy * dy);
        distInt = Integer.valueOf((int)Math.round(distDouble));

        return(distInt);
        
    }
    
}
