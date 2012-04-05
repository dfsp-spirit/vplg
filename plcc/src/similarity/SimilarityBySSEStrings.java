/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package similarity;

import algorithms.NeedlemanWunsch;
import algorithms.SmithWaterman;
import plcc.SSEGraph;

/**
 * This class provides various methods to compare two protein graphs by their string representations,
 * e.g., the SSE string.
 * @author spirit
 */
public class SimilarityBySSEStrings {
    
    private SSEGraph graphA;
    private SSEGraph graphB;
    
    
    /**
     * Constructor, sets the graphs.
     */ 
    public SimilarityBySSEStrings(SSEGraph graphA, SSEGraph graphB) {
        this.graphA = graphA;
        this.graphB = graphB;        
    }
    
    /**
     * Computes the global alignment score for the SSEStrings of the two graphs.
     * @return the global alignment score
     */
    public Integer compareSSEStringsGlobal() {
        String sseStringA = graphA.getSSEString();
        String sseStringB = graphB.getSSEString();
        
        NeedlemanWunsch nw = new NeedlemanWunsch(sseStringA, sseStringB);
        nw.setAlphabet("HELO");
        nw.setGapPenalty(-1);
        nw.setScoringMatrix(new Integer[][] { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1} } );
        
        return(nw.computeAlignmentScore());
    }
    
    /**
     * Computes the local alignment score for the SSEStrings of the two graphs.
     * @return the local alignment score
     */
    public Integer compareSSEStringsLocal() {
        String sseStringA = graphA.getSSEString();
        String sseStringB = graphB.getSSEString();
        
        SmithWaterman sw = new SmithWaterman(sseStringA, sseStringB);
        sw.setAlphabet("HELO");
        sw.setGapPenalty(-1);
        sw.setScoringMatrix(new Integer[][] { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1} } );
        
        return(sw.computeAlignmentScore());
    }
    
    
    /**
     * Testing only.
     */ 
    public static void main(String[] args) {
        String sseStringA = "HEHEHEHHEHHHHHHHEHHLL";
        String sseStringB = "HEEHHEEEHHEHLL";
        Integer score;
        String[] alignment;
        
        System.out.println("Comparing strings '" + sseStringA + "' and '" + sseStringB + "' globally.");
        
        NeedlemanWunsch nw = new NeedlemanWunsch(sseStringA, sseStringB);
        nw.setAlphabet("HELO");
        nw.setGapPenalty(-1);
        nw.setScoringMatrix(new Integer[][] { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1} } );
        
        
        
        score = nw.computeAlignmentScore();
        System.out.println("Alignment score is: " + score + ".");
        System.out.println("Alignment:");
        alignment = nw.getAlignment();
        System.out.println(alignment[0]);
        System.out.println(alignment[1]);
        
        
        System.out.println("Comparing strings '" + sseStringA + "' and '" + sseStringB + "' locally.");
        
        SmithWaterman sw = new SmithWaterman(sseStringA, sseStringB);
        sw.setAlphabet("HELO");
        sw.setGapPenalty(-1);
        sw.setScoringMatrix(new Integer[][] { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1} } );        
        
        
        score = sw.computeAlignmentScore();
        System.out.println("Alignment score is: " + score + ".");
        System.out.println("Alignment:");
        alignment = sw.getAlignment();
        System.out.println(alignment[0]);
        System.out.println(alignment[1]);        
        
        System.exit(0);
    }
}


