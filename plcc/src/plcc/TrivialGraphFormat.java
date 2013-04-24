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
 * @author ts
 */
public interface TrivialGraphFormat {
    
    /**
     * Returns a string representation of this object in trivial graph format (TGF).
     * @return the trivial graph format string
     */ 
    public String toTrivialGraphFormat();
    
}
