/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package tools;

/**
 * Some math tools for PLCC.
 * @author spirit
 */
public class MathTools {
    
    
    /**
     * Floor division of integers, inspired by Java8.
     * @param divident the divident
     * @param divisor the divisor, must not be null
     * @return the quotient
     */
    public static Integer floorDiv(Integer divident, Integer divisor) {
        Double divRes = (divident.doubleValue()) / divisor.doubleValue();
        Integer quotient = ((Double)Math.floor(divRes)).intValue();
        return quotient;
    }
}
