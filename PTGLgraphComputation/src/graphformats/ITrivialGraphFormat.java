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
public interface ITrivialGraphFormat {
    
    /**
     * Returns a string representation of this object in trivial graph format (TGF).
     * @return the trivial graph format string
     */ 
    public String toTrivialGraphFormat();
    
}
