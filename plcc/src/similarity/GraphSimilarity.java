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
public class GraphSimilarity {
    
    private SSEGraph graphA;
    private SSEGraph graphB;
    
    
    /**
     * Constructor, sets the graphs.
     */ 
    public GraphSimilarity(SSEGraph graphA, SSEGraph graphB) {
        this.graphA = graphA;
        this.graphB = graphB;        
    }
    
    /**
     * Computes the global alignment score for the sequential SSEStrings of the two graphs.
     * @return the global alignment score
     */
    public Integer compareSeqSSEStringsGlobal() {
        String sseStringA = graphA.getSSEStringSequential();
        String sseStringB = graphB.getSSEStringSequential();
        
        NeedlemanWunsch nw = new NeedlemanWunsch(sseStringA, sseStringB);
        nw.setAlphabet("HELO");
        nw.setGapPenalty(-1);
        nw.setScoringMatrix(new Integer[][] { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1} } );
        
        return(nw.computeAlignmentScore());
    }
    
    /**
     * Computes the global alignment score for the spatial SSEStrings of the two graphs.
     * @return the global alignment score
     */
    public Integer compareSpatialSSEStringsGlobal() {
        String sseStringA = graphA.getSSEStringSpatial();
        String sseStringB = graphB.getSSEStringSpatial();
        
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
    public Integer compareSeqSSEStringsLocal() {
        String sseStringA = graphA.getSSEStringSequential();
        String sseStringB = graphB.getSSEStringSequential();
        
        SmithWaterman sw = new SmithWaterman(sseStringA, sseStringB);
        sw.setAlphabet("HELO");
        sw.setGapPenalty(-1);
        sw.setScoringMatrix(new Integer[][] { {1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1} } );
        
        return(sw.computeAlignmentScore());
    }
    
    /**
     * Computes the local alignment score for the SSEStrings of the two graphs.
     * @return the local alignment score
     */
    public Integer compareSpatialSSEStringsLocal() {
        String sseStringA = graphA.getSSEStringSpatial();
        String sseStringB = graphB.getSSEStringSpatial();
        
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
    
    
    /**
     * Set-based graph comparison. Inspects some pretty general graph properties (e.g., number of vertices and edges of certain types)
     * of two graphs and computes a simple similarity score.
     * @return the score. The maximum score is 0, which means that the two graphs have the same number of edges and vertices. If the graphs differ in these
     * properties, the score is < 0. (This means the score can never be > 0.)
     */ 
    public Integer compareGraphsSetBased() {
        Integer score = 0;
        
        Integer numEdgesTotalA = graphA.numSSEContacts();
        Integer numEdgesTotalB = graphB.numSSEContacts();
        
        Integer diffEdgesTotal = Math.abs(numEdgesTotalA - numEdgesTotalB);
        
        Integer numVerticesTotalA = graphA.numVertices();
        Integer numVerticesTotalB = graphB.numVertices();
        Integer numHelicesA = graphA.numHelices();
        Integer numHelicesB = graphB.numHelices();
        Integer numStrandsA = graphA.numBetaStrands();
        Integer numStrandsB = graphB.numBetaStrands();
        
        Integer diffVerticesTotal = Math.abs(numVerticesTotalA - numVerticesTotalB);
        Integer diffHelices = Math.abs(numHelicesA - numHelicesB);
        Integer diffStrands = Math.abs(numStrandsA - numStrandsB);
        
        score = - (diffEdgesTotal + diffVerticesTotal + diffHelices + diffStrands);
        
        //System.out.println("score is: " + score + ".");
        
        return(score);
    }
}


