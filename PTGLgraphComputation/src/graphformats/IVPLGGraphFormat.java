/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphformats;

/**
 *
 * @author ts
 */
public interface IVPLGGraphFormat {
    
    /**
     * Returns a string representation of this object in VPLG graph format.
     * @return the VPLG graph format string
     */ 
    public String toVPLGGraphFormat();
    
}
