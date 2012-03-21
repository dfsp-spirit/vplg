/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package algorithms;

/**
 * An implementation of the Needleman-Wunsch dynamic programming string alignment algorithm.
 * @author spirit
 */
public class NeedlemanWunsch {
    
    private Integer[][] scoringMatrix;
    private Integer[][] dynPrgMatrix;
    private String stringA;
    private String stringB;
    private String alphabet;
    
    /**
     * Creates an instance that can be used to align the two strings and obtain the alignment score.
     * You have to set the alphabet and the scoring matrix after calling this constructor.
     * @param stringA the first string
     * @param stringB the second string
     */
    public NeedlemanWunsch(String stringA, String stringB) {
        this.stringA = stringA;
        this.stringB = stringB;        
        
        this.alphabet = "AGCT";
        this.scoringMatrix = new Integer[][] { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1} };
    }
    
    /**
     * Sets the given scoring matrix. Use this after the alphabet has been set.
     * @param scMatrix the scoring matrix. It has to be of length (alphabet.length * alphabet.length). Character indices in the matrix have to match those in the alphabet.
     * @return true if the matrix was accepted, false otherwise
     */
    public Boolean setScoringMatrix(Integer[][] scMatrix) {        
        this.scoringMatrix = scMatrix;
        
        if(this.scoringMatrix.length <= 0) { return(false); }
        
        if(this.scoringMatrix[0].length != this.scoringMatrix.length) { return(false); }
        
        if(this.scoringMatrix.length != this.alphabet.length()) { return(false); }
        
        return(true);
    }
    
    
    /**
     * Sets the alphabet.
     * @param alphabet the alphabet string. Each position in this string is assumed to be a letter of the alphabet. The ordering
     * matters because the scoring matrix is assumed to use the same indices.
     */
    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }
    
    
    /**
     * Computes the alignment and returns the alignment score according to the scoring matrix used.
     * @return the alignment score
     */
    public Integer computeAlignment() {
        Integer score = 0;
        Integer matrixSize = alphabet.length() + 1;
        this.dynPrgMatrix = new Integer[matrixSize][matrixSize];
        
        // init
        for(Integer i = 0; i < matrixSize; i++) {
            for(Integer j = 0; j < matrixSize; j++) {
                dynPrgMatrix[i][j] = 0;
            }
        }
        
        
        
        return(score);
    }
    
}
