/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package similarity;

/**
 * A scoring matrix class for various strings. Intended to be used with the string alignment algorithms.
 * Provides some standard matrices via static objects.
 * Also saves a gap penalty and an alphabet, thus it can be described as a scoring scheme.
 * @author ts
 */
public class ScoringMatrix {
    
    
    private String alphabet;
    private Integer[][] matrix;
    private Integer gapPenalty;
    
    public static Integer GAP_PENALTY_SSE = -1;
    public static Integer GAP_PENALTY_NUCLEOTIDE = -1;
    public static Integer GAP_PENALTY_AA = -1;
    
    /** The alphabet for SSE strings. */
    public static String ALPHABET_SSE = "HELO";
    
    /** The alphabet for nucleotides (length 4). */
    public static String ALPHABET_NUCLEOTIDE = "ATGC";
    
    /** The alphabet for amino acids (length 20). */
    public static String ALPHABET_AA = "GAVLISTCMPHRNQEDFWYK";
    
    public static Integer[][] MATRIX_SSE = new Integer[][] { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1} };
    public static Integer[][] MATRIX_NUCLEOTIDE = new Integer[][] { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1} };
    
    
    /**
     * Constructor. You can use one of the static alphabets (ScoringMatrix.ALPHABET_*), gap penalties (ScoringMatrix.GAP_PENALTY_*) 
     * and matrices (ScoringMatrix.MATRIX_*) provided by this class as parameters.
     * @param alphabet the alphabet to use, you can use the static ones at ScoringMatrix.ALPHABET_*
     * @param matrix the matrix to use, you can use the static ones at ScoringMatrix.MATRIX_*
     * @param gapPenalty the gap penalty to use, you can use the static ones at ScoringMatrix.GAP_PENALTY_*
     */
    public ScoringMatrix(String alphabet, Integer[][] matrix, Integer gapPenalty) {
        this.alphabet = alphabet;
        this.matrix = matrix;
        this.gapPenalty = gapPenalty; 
        
        if(this.matrix.length <= 0 || this.matrix[0].length != this.matrix.length || this.matrix.length != this.alphabet.length()) {
            System.err.println("WARNING: ScoringMatrix(): Matrix does not cover the alphabet.");
        }
    }
    
    
    /**
     * Scores the character pair (charA, charB) and returns the score.
     * @param charA the first character
     * @param charB the second character
     * @return the score
     */
    public Integer score(Character charA, Character charB) {
        Integer matrixPosCharA = this.alphabet.indexOf(charA);
        Integer matrixPosCharB = this.alphabet.indexOf(charB);
        
        if(matrixPosCharA < 0 || matrixPosCharB < 0) {
            System.err.println("ERROR: ScoringMatrix.score(): Invalid character '" + charA + "' or '" + charB + "', not in alphabet of this matrix.");
            System.exit(1);
        }
        
        return(this.matrix[matrixPosCharA][matrixPosCharB]);
    }
    
    
    
    
    /**
     * Returns the alphabet in the order of the matrix.
     * @return the alphabet
     */
    public String getAlphabet() {
        return(this.alphabet);
    }
    
    
    /**
     * Returns the scoring matrix.
     * @return the matrix
     */
    public Integer[][] getMatrix() {
        return(this.matrix);
    }
    
    
    /**
     * Returns the width of this matrix.
     * @return the width
     */
    public Integer getWidth() {
        return(this.matrix.length);
    }
    
    
    /**
     * Returns the height of this matrix.
     * @return the height
     */
    public Integer getHeight() {
        if(this.getWidth() <= 0) {
            return(0);
        }
        return(this.matrix[0].length);
    }
    
    /**
     * Returns the gap penalty this scoring scheme uses.
     * @return the gap penalty, a negative integer
     */
    public Integer getGapPenalty() {
        return(this.gapPenalty);
    }
    
}
