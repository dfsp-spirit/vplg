/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package algorithms;

import tools.DP;
import similarity.ScoringMatrix;

/**
 * An implementation of the SmithWaterman dynamic programming string alignment algorithm.
 * @author ts
 */
public class SmithWaterman {
    
    private Integer[][] scoringMatrix;
    private Integer[][] dynPrgMatrix;
    private String stringA;
    private String stringB;
    private String alphabet;
    private Integer gapPenalty;
    private Boolean matrixComputed;
    
    /**
     * Creates an instance that can be used to align the two strings and obtain the alignment score.
     * You have to set the alphabet and the scoring matrix after calling this constructor.
     * @param stringA the first string
     * @param stringB the second string
     */
    public SmithWaterman(String stringA, String stringB) {
        this.stringA = stringA;
        this.stringB = stringB;        
        
        this.gapPenalty = -1;
        this.matrixComputed = false;
        
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
        
        this.matrixComputed = false;
        return(true);
    }
    
    /**
     * Sets the scoring scheme from the ScoringMatrix, i.e., uses the gap penalty, matrix and alphabet from it.
     * @param scoreScheme the ScoringMatrix
     */
    public void setScoringScheme(ScoringMatrix scoreScheme) {
        this.alphabet = scoreScheme.getAlphabet();
        this.gapPenalty = scoreScheme.getGapPenalty();
        this.scoringMatrix = scoreScheme.getMatrix();
        this.matrixComputed = false;
    }
    
    
    /**
     * Sets the gap penalty. This should be a negative integer.
     * @param p the penalty, a negative Integer
     */
    public void setGapPenalty(Integer p) {
        this.gapPenalty = p;
        
        if(p > 0) {
            DP.getInstance().w("Gap penalty should be negative but is '" + p + "'.");    
        }
        
        this.matrixComputed = false;
    }
    
    
    /**
     * Sets the alphabet.
     * @param alphabet the alphabet string. Each position in this string is assumed to be a letter of the alphabet. The ordering
     * matters because the scoring matrix is assumed to use the same indices.
     */
    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
        this.matrixComputed = false;
    }
    
    
    /**
     * Fills the alignment matrix and returns the alignment score of the best alignment according to the scoring matrix used.
     * @return the alignment score
     */
    public Integer computeAlignmentScore() {
        Integer matrixWidth = this.stringA.length();
        Integer matrixHeight = this.stringB.length();
        Integer match, delete, insert;
        
        this.dynPrgMatrix = new Integer[matrixWidth][matrixHeight];
        
        
        // init first row and column (all gaps)
        for(Integer i = 0; i < matrixWidth; i++) {
            dynPrgMatrix[i][0] = i * this.gapPenalty;
        }
        for(Integer j = 0; j < matrixHeight; j++) {
            dynPrgMatrix[0][j] = j * this.gapPenalty;
        }
        
        // fill the rest of the matrix
        for(Integer i = 1; i < matrixWidth; i++) {
            for(Integer j = 1; j < matrixHeight; j++) {
                match = dynPrgMatrix[i-1][j-1] + score(i, j);
                delete = dynPrgMatrix[i-1][j] + this.gapPenalty;
                insert = dynPrgMatrix[i][j-1] + this.gapPenalty;
                dynPrgMatrix[i][j] = Math.max(match, delete);
                dynPrgMatrix[i][j] = Math.max(dynPrgMatrix[i][j], insert);
                dynPrgMatrix[i][j] = Math.max(dynPrgMatrix[i][j], 0);
            }
        }             
        
        this.matrixComputed = true;
        return(this.getMaxScorePosition(dynPrgMatrix)[2]);
    }
    
    /**
     * Returns the actual alignment of the two Strings. Computes the matrix only if this has not been done yet.
     * @return the alignment as a String array of length 2. Each line contains the characters of one of the two strings.
     */
    public String[] getAlignment() {
        if(! this.matrixComputed) {
            this.computeAlignmentScore();
        }
        
        String alignmentA = "";
        String alignmentB = "";
        
        Integer i = this.getMaxScorePosition(dynPrgMatrix)[0];
        Integer j = this.getMaxScorePosition(dynPrgMatrix)[1];
        
        
        Integer score, scoreDiag, scoreUp, scoreLeft;
        
        // the highest score in the matrix
        score = this.dynPrgMatrix[i][j];
        
        while(i > 0 && j > 0 && score > 0) {
            
            
            // the 3 possible score we may come from
            scoreDiag = this.dynPrgMatrix[i-1][j-1];
            scoreUp = this.dynPrgMatrix[i][j-1];
            scoreLeft = this.dynPrgMatrix[i-1][j];
            
            // check where we came from
            if (score == scoreDiag + score(i, j)) {
              alignmentA = this.stringA.charAt(i) + alignmentA;
              alignmentB = this.stringB.charAt(j) + alignmentB;
              i--;
              j--;
            }
            else if (score == scoreLeft + this.gapPenalty)
            {
              alignmentA = this.stringA.charAt(i) + alignmentA;
              alignmentB = "-" + alignmentB;
              i--;
            }
            else if (score == scoreUp + this.gapPenalty)
            {
              alignmentA = "-" + alignmentA;
              alignmentB = this.stringB.charAt(j) + alignmentB;
              j--;
            }
            
            // the current score in the matrix
            score = this.dynPrgMatrix[i][j];
        }
        
        while (i > 0) {            
            alignmentA = this.stringA.charAt(i) + alignmentA;
            alignmentB = "-" + alignmentB;
            i--;
        }


        while (j > 0) {
            alignmentA = "-" + alignmentA;
            alignmentB = this.stringB.charAt(j) + alignmentB;
            j--;
        }
        
        return(new String[] { alignmentA, alignmentB } );
    }
    
    
    /**
     * Returns the matrix position (i, j) that holds the highest value in the matrix and that score.
     * @param matrix the input matrix
     * @return an integer array of length 3 which holds the i and j values at indices 0 and 1 and the max score value at index 2.
     */
    private Integer[] getMaxScorePosition(Integer[][] matrix) {
        
        Integer max = Integer.MIN_VALUE;
        Integer[] pos = new Integer[3];
        
        for(Integer i = 0; i < matrix.length; i++) {
            for(Integer j = 0; j < matrix[0].length; j++) {
                if(matrix[i][j] > max) {
                    max = matrix[i][j];
                    pos[0] = i;
                    pos[1] = j;
                    pos[2] = max;
                }
            }
        }
        
        return(pos);        
    }
    
    
    /**
     * Returns the score from the scoring matrix for matching the character at index indexStringA in stringA with the character at index indexStringB in stringB.
     * @param indexStringA the index of the character in stringA
     * @param indexStringB the index of the character in stringB
     * @return the score from the scoring matrix
     */
    private Integer score(Integer indexStringA, Integer indexStringB) {
        Character cA = this.stringA.charAt(indexStringA);
        Character cB = this.stringB.charAt(indexStringB);
        
        Integer matrixPosCharA = this.alphabet.indexOf(cA);
        Integer matrixPosCharB = this.alphabet.indexOf(cB);
        
        return(this.scoringMatrix[matrixPosCharA][matrixPosCharB]);
    }
    
}
