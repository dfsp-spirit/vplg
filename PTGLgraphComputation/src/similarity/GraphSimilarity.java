/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package similarity;

import tools.DP;
import algorithms.CompatGraphComputation;
import algorithms.NeedlemanWunsch;
import algorithms.SmithWaterman;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import proteingraphs.CompatGraph;
import io.DBManager;
import proteingraphs.ProtGraph;
import proteingraphs.SSEGraph;
import settings.Settings;
import static similarity.SimilarityByGraphlets.getRandIntegerArray;

/**
 * This class provides various methods to compare two protein graphs by their string representations,
 * e.g., the SSE string.
 * @author ts
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
     * Compares the graphs g1 and g2 by computing their compatibility graph. The score is based on the 
     * size of the compatibility graph.
     * @return a score based on the size of the compatibility graph, larger values mean closer similarity
     */
    public Integer compareByCompatibilityGraph() {
        CompatGraphComputation cgc = new CompatGraphComputation(this.graphA, this.graphB);
        CompatGraph cg = cgc.computeEdgeCompatibiltyGraph();
        //System.out.println("Compatibility graph(V=" + cg.numVertices() + ", E=" + cg.getNumEdges() + ") for " + graphA.toShortString() + " and " + graphB.toShortString() + ": \n" + cg + "\n");        
        
        // TODO: Come up with a good score that takes the graph sizes of g1 and g2 into account instead
        //       of punishing small input graphs. This score makes no sense at all yet because we have
        //       to perform clique detection to find common subgraphs first.
        
        if(cg == null) {
            DP.getInstance().w("compareByCompatibilityGraph(): CompatGraph is NULL, assuming score 0.");
            return(0);
        }
        
        Boolean findCliques = true;
        if(findCliques) {
            Integer minCliqueSize = 1;      // the vertices of cliques smaller than this number will not be printed
            System.out.println("  Detecting cliques in compatibility graph, transforming compatibilty graph to protein graph...");
            //try {
                //System.out.println("   Trying to create ProtGraph...");
                ProtGraph pg = cg.toFakeSSEGraph();
                if(pg == null) {
                    System.out.println("WARNING: GraphSimilarity.compareByCompatibilityGraph(): ProtGraph is NULL.");
                } else {                
                    //System.out.println("   Created ProtGraph.");
                    System.out.println("   ProtGraph for clique detection is: " + pg.toShortString() + ".");
                }
                //try {
                    ArrayList<Set<Integer>> cliques = pg.getMaximalCliques();
                    System.out.println("  Found " + cliques.size() + " cliques:");
                    
                    Integer num = 0;
                    for(Set<Integer> clique : cliques) {
                        
                        // print clique info
                        System.out.print("   Clique #" + num + " of size " + clique.size());
                        
                        // print the vertex indices of the clique
                        if(clique.size() >= minCliqueSize) {
                            System.out.print(" [");
                            for(Integer i : clique) {
                                System.out.print(" " + i);
                            }
                            System.out.print(" ]\n");                                                    
                        }                                                
                        num++;
                    }                    
                    
                //} catch (Exception ex) {
                //    DP.getInstance().w("Clique detection failed: '" + ex.getMessage() + "'.");
                //}
            //} catch (Exception e) {
            //    DP.getInstance().w("Could not start clique detection, could not create SSEGraph: '" + e.getMessage() + "'.");
            //}                                                
        }        
        
        return(cg.numVertices() + cg.getNumEdges());
    }
    
    
    /**
     * For debugging/testing purposes only, not used in the program.
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
        
        
        System.out.println("Comparing by relative graphlet frequency distance -- random graphlet distributions:");
        Integer[] graphletsA = SimilarityByGraphlets.getRandIntegerArray(30, 0, 9);
        Integer[] graphletsB = SimilarityByGraphlets.getRandIntegerArray(30, 0, 9);
        System.out.println("GraphletsA: " + SimilarityByGraphlets.getVectorStringForIntegerArray(graphletsA));
        System.out.println("GraphletsB: " + SimilarityByGraphlets.getVectorStringForIntegerArray(graphletsB));
        System.out.println("RGFD(A,B) is : " + SimilarityByGraphlets.getRelativeGraphletFrequencyDistance(graphletsA, graphletsB));
        
        
        System.out.println("Comparing by relative graphlet frequency distance -- similar graphlet distributions:");
        Integer[] graphletsC = SimilarityByGraphlets.getRandIntegerArray(30, 0, 9);
        Integer[] graphletsD = SimilarityByGraphlets.mutateIntegerArray(graphletsC, 3, 0.1);
        System.out.println("GraphletsC: " + SimilarityByGraphlets.getVectorStringForIntegerArray(graphletsC));
        System.out.println("GraphletsD: " + SimilarityByGraphlets.getVectorStringForIntegerArray(graphletsD));
        System.out.println("RGFD(C,D) is : " + SimilarityByGraphlets.getRelativeGraphletFrequencyDistance(graphletsC, graphletsD));
        
        
        
        
        
        System.out.println("Comparing by relative graphlet frequency distance -- 7timA and 8icdA:");
        Settings.init();
        DBManager.initUsingDefaults();
        
        // insert fake values into DB
        Double[] graphletsE = SimilarityByGraphlets.getRandDoubleArray(30);
        Double[] graphletsF  = SimilarityByGraphlets.mutateDoubleArray(graphletsE, 0.3, 0.5);
        
        System.out.println("GraphletsE: " + SimilarityByGraphlets.getVectorStringForDoubleArray(graphletsE));
        System.out.println("GraphletsF: " + SimilarityByGraphlets.getVectorStringForDoubleArray(graphletsF));
        
        
        System.out.println("Writing fake graphlet counts for 7tim_A and 8icd_A to DB...");
        try {
            if(DBManager.writeNormalizedGraphletsToDB("7tim", "A", ProtGraph.GRAPHTYPE_INT_ALBE, graphletsE)) {
                System.out.println("  7tim_A OK.");
            }
            else {
                System.out.println("  ERROR.");
            }
        } catch (SQLException e) { System.err.println("SQL ERROR: '" + e.getMessage() + "'."); }
        
        try {
            
            if(DBManager.writeNormalizedGraphletsToDB("8icd", "A", ProtGraph.GRAPHTYPE_INT_ALBE, graphletsF)) {
                System.out.println("  8icd_A OK.");
            }
            else {
                System.out.println("  ERROR.");
            }
        } catch (SQLException e) { System.err.println("SQL ERROR: '" + e.getMessage() + "'."); }
        
        
        System.out.println("Retrieving graphlet counts for 7tim_A and 8icd_A from DB...");
        try {
            graphletsE = DBManager.getNormalizedProteinGraphGraphletCounts("7tim", "A", ProtGraph.GRAPHTYPE_ALBE);            
        }
        catch(SQLException e) {
            System.err.println("Could not get graphletsE: '" + e.getMessage() + "'.");
        }
        
        try {
            graphletsF = DBManager.getNormalizedProteinGraphGraphletCounts("8icd", "A", ProtGraph.GRAPHTYPE_ALBE);
        }
        catch(SQLException e) {
            System.err.println("Could not get graphletsF: '" + e.getMessage() + "'.");
        }
        
        
        if(graphletsE == null || graphletsF == null) {
            DP.getInstance().e("Could not retrieve graphlet counts from DB, graphletsE and/or graphletsF is null.");
        }
        
        DP.getInstance().flush();

        System.out.println("GraphletsE: " + SimilarityByGraphlets.getVectorStringForDoubleArray(graphletsE));
        System.out.println("GraphletsF: " + SimilarityByGraphlets.getVectorStringForDoubleArray(graphletsF));
        System.out.println("RGFD(E,F) is : " + SimilarityByGraphlets.getRelativeGraphletFrequencyDistanceNormalized(graphletsE, graphletsF));
        
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


