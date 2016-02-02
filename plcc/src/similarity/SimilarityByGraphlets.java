/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package similarity;

import java.sql.SQLException;
import java.util.Random;
import io.DBManager;
import tools.DP;

/**
 * Functions to compare graphs based on graphlet similarity scores.
 * @author ts
 */
public class SimilarityByGraphlets {
    
    public static final String GRAPHLET_SIM_METHOD_RGF = "rgf";
    public static final String GRAPHLET_SIM_METHOD_CUSTOM = "cus";
    
    public enum GraphletDeterminationMethod { DATABASE, FILE, FAKE_FOR_DEBUG }
    
    
    /**
     * Implements the relative graphlet frequency distance using Integer graphlet counts. This is a similarity measure to compare two
     * networks by the frequencies of graphlets in them.
     * 
     * DEPREDCATED: Use the Double array version for normalized graphlets instead!
     * 
     * For details see Pržulj N, Corneil DG, Jurisica I: Modeling Interactome, Scale-Free or Geometric?, Bioinformatics 2004, 20(18):3508-3515.
     * A pretty good explanation can be found at http://en.wikipedia.org/wiki/Graphlets#Relative_graphlet_frequency_distance as well.
     * @param graphletCountsA the vector which contains the counts of graphlets (usually the 3-, 4- and 5-graphlets, so a total of 30) for network A 
     * @param graphletCountsB the vector of graphlet counts for network B
     * @return the relative graphlet frequency distance between the networks A and B, a value between 0 and 1.
     */
    @Deprecated
    public static double getRelativeGraphletFrequencyDistance(Integer[] graphletCountsA, Integer[] graphletCountsB) {
        double res = 0.0;
        
        Integer totalInA = sumIntegerArray(graphletCountsA);
        Integer totalInB = sumIntegerArray(graphletCountsB);
        
        System.out.println("Sum of graphlets is " + totalInA + " for A, " + totalInB + " for B.");
        
        double scoreA, scoreB;
        for(int i = 0; i < graphletCountsA.length; i++) {
            scoreA = -Math.log((double)graphletCountsA[i] / (double)totalInA);
            scoreB = -Math.log((double)graphletCountsB[i] / (double)totalInB);
            if(Double.isInfinite(scoreA) || Double.isInfinite(scoreB)) {
                //System.out.println("Skipping graphlet #" + i + ", lead to infinite score.");
                continue;                
            }                    
            //System.out.println("Handling graphlet #" + i + ", scoreA=" + scoreA + ", scoreB=" + scoreB + ".");
            res += Math.abs(scoreA  - scoreB);
        }
        
        return res;
    }
    
    /**
     * Implements the relative graphlet frequency distance using normalized graphlet counts (doubles between 0.0 and 1.0.). This is a similarity measure to compare two
     * networks by the frequencies of graphlets in them.
     * For details see Pržulj N, Corneil DG, Jurisica I: Modeling Interactome, Scale-Free or Geometric?, Bioinformatics 2004, 20(18):3508-3515.
     * A pretty good explanation can be found at http://en.wikipedia.org/wiki/Graphlets#Relative_graphlet_frequency_distance as well.
     * @param graphletCountsA the vector which contains the counts of graphlets (usually the 3-, 4- and 5-graphlets, so a total of 30) for network A, normalized (0.0 .. 1.0)
     * @param graphletCountsB the vector of graphlet counts for network B, normalized (0.0 .. 1.0)
     * @return the relative graphlet frequency distance between the networks A and B, a value between 0.0 and 1.0
     */
    public static double getRelativeGraphletFrequencyDistanceNormalized(Double[] graphletCountsA, Double[] graphletCountsB) {
        double res = 0.0;
        
        Double totalInA = sumDoubleArray(graphletCountsA);
        Double totalInB = sumDoubleArray(graphletCountsB);
        
        System.out.println("getRelativeGraphletFrequencyDistanceNormalized: Sum of graphlets is " + totalInA + " for A, " + totalInB + " for B.");
        
        double scoreA, scoreB;
        for(int i = 0; i < graphletCountsA.length; i++) {
            scoreA = -Math.log(graphletCountsA[i] / totalInA);
            scoreB = -Math.log(graphletCountsB[i] / totalInB);
            if(Double.isInfinite(scoreA) || Double.isInfinite(scoreB)) {
                //System.out.println("Skipping graphlet #" + i + ", lead to infinite score.");
                continue;                
            }                    
            //System.out.println("Handling graphlet #" + i + ", scoreA=" + scoreA + ", scoreB=" + scoreB + ".");
            res += Math.abs(scoreA  - scoreB);
        }
        
        return res;
    }
    
    
    /**
     * Computes the sum of the array.
     * @param i the input Integer array
     * @return the sum of all values in i
     */
    private static Integer sumIntegerArray(Integer[] i) {
        Integer sum = 0;
        for (int j = 0; j < i.length; j++) {
            sum += i[j];            
        }
        return sum;
    }
    
    /**
     * Computes the sum of the array.
     * @param i the input Integer array
     * @return the sum of all values in i
     */
    private static Double sumDoubleArray(Double[] i) {
        
        if(i == null) {
            return 0.0;
        }
        
        Double sum = 0.d;
        for (int j = 0; j < i.length; j++) {
            sum += i[j];            
        }
        return sum;
    }
    
    /**
     * Formats the input array as a string in vector notation, e.g., "<0,1,3,3>".
     * @param arr the input array
     * @return a string representation of the input array in vector notation, e.g., "<0,1,3,3>".
     */
    public static String getVectorStringForIntegerArray(Integer[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if(i < (arr.length - 1)) {
                sb.append(",");
            }
        }
        
        sb.append(">");
        return sb.toString();
    }
    
    /**
     * Formats the input array as a string in vector notation, e.g., "<0.1,1.3,3.5,3.7>".
     * @param arr the input array
     * @return a string representation of the input array in vector notation, e.g., "<0.1,1.3,3.5,3.7>".
     */
    public static String getVectorStringForDoubleArray(Double[] arr) {
        
        if(arr == null) {
            return "<>";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        
        for (int i = 0; i < arr.length; i++) {
            sb.append(String.format("%2f", arr[i]));
            if(i < (arr.length - 1)) {
                sb.append(", ");
            }
        }
        
        sb.append(">");
        return sb.toString();
    }
    
    
    /**
     * Mutates an integer array, i.e., substracts or adds values to each element randomly.
     * It is assured that the mutated values are >= 0.
     * @param source the source array, it is not modified by this function
     * @param mutateMax the max value by which the value in source should be changed
     * @param mutationProb the mutation probability for a single element
     * @return the new array, a mutated version of source
     */
    public static Integer[] mutateIntegerArray(Integer[] source, int mutateMax, double mutationProb) {
        Integer[] res = new Integer[source.length];
        Random rand = new Random();
        int mutateMin = 1;
        
        double r;
        int numMutated = 0;
        for (int i = 0; i < res.length; i++) {
            r = rand.nextDouble();
            if(r < mutationProb) {
                // mutate the value.
                int mutateAmount = rand.nextInt((mutateMax - mutateMin) + 1) + mutateMin;
                boolean substract = rand.nextBoolean(); // if false, we add it.
                int mutatedValue = (substract ? source[i] - mutateAmount : source[i] + mutateAmount);
                if(mutatedValue < 0) { mutatedValue = 0; }
                res[i] = mutatedValue;
                numMutated++;
            }
            else {
                // do not mutate, just copy original value                
                res[i] = source[i];
            }            
        }
        
        System.out.println("Mutated " + numMutated + " values in input array of length " + source.length + ".");
        
        return res;
    }
    
    
    /**
     * Mutates a double array, i.e., substracts or adds values to each element randomly.
     * It is assured that the mutated values are between 0 and 1, and that the resulting array still follows this rule.
     * @param source the source array, it is not modified by this function (resulting value will never go below 0.0 and above 1.0 though)
     * @param mutateMax the max value by which the value in source should be changed
     * @param mutationProb the mutation probability for a single element
     * @return the new array, a mutated version of source
     */
    public static Double[] mutateDoubleArray(Double[] source, Double mutateMax, double mutationProb) {
        Double[] res = new Double[source.length];
        Random rand = new Random();
        double mutateMin = 0.01;
        
        double r;
        int numMutated = 0;
        for (int i = 0; i < res.length; i++) {
            r = rand.nextDouble();
            if(r < mutationProb) {
                // mutate the value.
                double mutateAmount = rand.nextDouble() + mutateMin;
                boolean substract = rand.nextBoolean(); // if false, we add it.
                Double mutatedValue = (substract ? source[i] - mutateAmount : source[i] + mutateAmount);
                if(mutatedValue < 0.0) { mutatedValue = 0.0; }
                if(mutatedValue > 1.0) { mutatedValue = 1.0; }
                res[i] = mutatedValue;
                numMutated++;
            }
            else {
                // do not mutate, just copy original value                
                res[i] = source[i];
            }            
        }
        
        System.out.println("Mutated " + numMutated + " values in input array of length " + source.length + ".");
        
        return res;
    }
    
    
    
    /**
     * Determines the graphlet count vector for the given protein graph by the requested method.
     * @param pdb_id the PDB id
     * @param chain_name the chain id
     * @param graph_type the graph type, e.g., "albe"
     * @param method choose one from the enum SimilarityByGraphlets.GraphletDeterminationMethod
     * @return the graphlet count vector
     */
    private static Integer[] getGraphletVectorForProteinGraph(String pdb_id, String chain_name, String graph_type, GraphletDeterminationMethod method) {
        
        Integer[] graphlets = null;
        
        if(method == GraphletDeterminationMethod.DATABASE) {        
            try {
                graphlets = DBManager.getGraphletCounts(pdb_id, chain_name, graph_type);
            } catch(SQLException e) {
                DP.getInstance().w("Could not retrieve graphlet count vector for " + pdb_id + " chain " + chain_name + " from DB: '" + e.getMessage() + "'.");
            }
        }
        else if(method == GraphletDeterminationMethod.FAKE_FOR_DEBUG) {
            graphlets = getRandIntegerArray(30, 0, 10);
        }
        else if(method == GraphletDeterminationMethod.FILE) {
            throw new java.lang.UnsupportedOperationException("Get graphlet vector from file: not implemented yet");
        }
        return graphlets;
    }
    
    
    /**
     * Generates an integer array filled with random values (from uniform distribution).
     * @param length the length of the array (number of elements)
     * @param minValue the minimal value that is allowed for a single entry
     * @param maxValue the maximal value that is allowed for a single entry
     * @return the array
     */
    public static Integer[] getRandIntegerArray(int length, int minValue, int maxValue) {
        Integer[] res = new Integer[length];
        Random rand = new Random();
        
        for(int i = 0; i < res.length; i++ ) {
            res[i] = rand.nextInt((maxValue - minValue) + 1) + minValue;
        }
        
        return res;
    }
    
    
    /**
     * Returns a random double array of the specified length
     * @param length the number of elements to be added to the array
     * @return an array of doubles, all between 0.0 and 1.0
     */
    public static Double[] getRandDoubleArray(int length) {
        Double[] res = new Double[length];
        Random rand = new Random();
        
        for(int i = 0; i < res.length; i++ ) {
            res[i] = rand.nextDouble();
        }
        
        return res;
    }
    
    /**
     * Returns a uniform  double array of the specified length
     * @param length the number of elements to be added to the array
     * @return an array of doubles, all entries equal between 0.0 and 1.0, sum around 1.0
     */
    public static Double[] getUniformDoubleArraySumAround1(int length) {
        
        if(length <= 1) {
            return new Double[] { 1.0 };
        }
        
        Double[] res = new Double[length];
        
        for(int i = 0; i < res.length; i++ ) {
            res[i] = 1.0 / (double)length;
        }
                      
        return res;
    }
}
