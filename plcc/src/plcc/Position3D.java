/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

/**
 *
 * @author spirit
 */
public class Position3D {
    
    /**
     * The x coordinate.
     */
    public float x;
    
    /**
     * The y coordinate.
     */
    public float y;
    
    /**
     * The z coordinate.
     */
    public float z;
   
    /**
     * Position constructor.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    public Position3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    
    /**
     * Position constructor. Creates a new position with default coordinates (0,0).
     */ 
    public Position3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    
    /**
     * Changes the position by the values x, y. For example, if the current position is (5,5) and you call translate(2,-2), the new position will be (7,3).
     * @param x
     * @param y 
     */
    public void translateBy(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }
    
    
    /**
     * Updates this position to the specified coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void moveTo(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }       
    

    /**
     * Determines the rounded euclidian distance from this Position to Position p.
     * @param p the other position
     * @return the distance, rounded to integer
     */
    public float distanceTo(Position3D p) {
        Double distDouble = .0;
        float distInt, dx, dy, dz = 0;

        dx = this.x - p.x;
        dy = this.y - p.y;
        dz = this.z - p.z;


        distDouble = Math.sqrt(dx * dx + dy * dy + dz * dz);
        distInt = ((int)Math.round(distDouble));

        return(distInt);
        
    }
    
}
