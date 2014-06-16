/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package similarity;

/**
 * Functions to compare graphs based on graphlet similarity scores.
 * @author ts
 */
public class SimilarityByGraphlets {
    
    public static double getRelativeGraphletFrequencyDistance(Integer[] graphletCountsA, Integer[] graphletCountsB) {
        double res = 0.0;
        
        Integer totalInA = sumIntegerArray(graphletCountsA);
        Integer totalInB = sumIntegerArray(graphletCountsB);
        
        for(int i = 0; i < graphletCountsA.length; i++) {
            res += Math.abs(Math.log(graphletCountsA[i] / totalInA)  - Math.log(graphletCountsB[i] / totalInB));
        }
        
        return res;
    }
    
    
    
    private static Integer sumIntegerArray(Integer[] i) {
        Integer sum = 0;
        for (int j = 0; j < i.length; j++) {
            sum += i[j];            
        }
        return sum;
    }
    
}
