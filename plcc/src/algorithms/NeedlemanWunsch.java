/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package algorithms;

import tools.DP;
import similarity.ScoringMatrix;

/**
 * An implementation of the Needleman-Wunsch dynamic programming string alignment algorithm.
 * @author ts
 */
public class NeedlemanWunsch {
    
    private Integer[][] scoringMatrix;
    private Integer[][] dynPrgMatrix;
    private final String stringA;
    private final String stringB;
    private String alphabet;
    private Integer gapPenalty;
    private Boolean matrixComputed;
    
    /**
     * Creates an instance that can be used to align the two strings and obtain the alignment score.
     * You have to set the alphabet and the scoring matrix after calling this constructor.
     * @param stringA the first string
     * @param stringB the second string
     */
    public NeedlemanWunsch(String stringA, String stringB) {
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
        
        // init
        //for(Integer i = 0; i < matrixWidth; i++) {
        //    for(Integer j = 0; j < matrixHeight; j++) {
        //        dynPrgMatrix[i][j] = 0;
        //    }
        //}
        
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
            }
        }             
        
        this.matrixComputed = true;
        return(dynPrgMatrix[matrixWidth - 1][matrixHeight - 1]);
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
        
        Integer i = this.stringA.length() - 1;
        Integer j = this.stringB.length() - 1;
        
        Integer score, scoreDiag, scoreUp, scoreLeft;
        
        while(i > 0 && j > 0) {
            // the current score
            score = this.dynPrgMatrix[i][j];
            
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
