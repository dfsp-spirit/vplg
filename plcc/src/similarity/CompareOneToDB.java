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
import java.util.ArrayList;
import java.util.Collections;
import plcc.DBManager;
import plcc.Settings;

/**
 * This class implements methods that compare an element against the whole database.
 * @author ts
 */
public class CompareOneToDB {
    
    
    /**
     * Returns the maxNumberOfResults most similar 
     * @param sseString the SSE string of a protein chain
     * @param maxNumberOfResults the maximum number of proteins to return. E.g., if this is 3, the 3 most similar protein are returned. If several proteins compete for the last place in the list (because they received the same similarity score), a random one is chosen.
     * @param graphType the graph type to use
     * @param global whether to use global matching (NeedlemanWunsch). If this is false, local matching (SmithWaterman) will be used instead.
     * @return a comparison result
     */
    private static ArrayList<ComparisonResult> getMostSimilarToSSEString(String sseString, Integer maxNumberOfResults, String graphType, Boolean global) {
        
        String methodName = "SSEString";
        
        //if(maxNumberOfResults != 1) {
        //    System.err.println("WARNING: getMostSimilarToSSEString: parameter maxNumberOfResults not implemented yet and fixed at 1, returning only best score.");
        //}
        
        ArrayList<ComparisonResult> results = new ArrayList<ComparisonResult>();        
        ArrayList<String[]> sseStrings  = new ArrayList<String[]>();
        
        try {
            sseStrings = DBManager.getAllSSEStrings(graphType);
        } catch(Exception e) {
            System.err.println("WARNING: CompareOneToDB: SQL error while getting all SSEStrings: '" + e.getMessage() + "'.");
        }
        
        ScoringMatrix sm = new ScoringMatrix(ScoringMatrix.ALPHABET_SSE, ScoringMatrix.MATRIX_SSE, ScoringMatrix.GAP_PENALTY_SSE);
        SmithWaterman sw;
        NeedlemanWunsch nw;
        String pdbidDB, chainidDB, graphTypeDB, sseStringDB;
        Integer score;
               
        for(String[] sseData : sseStrings) {
            pdbidDB = sseData[0];
            chainidDB = sseData[1];
            graphTypeDB = sseData[2];
            sseStringDB = sseData[3];
            
            if(global) {
                nw = new NeedlemanWunsch(sseString, sseStringDB);
                nw.setScoringScheme(sm);                
                score = nw.computeAlignmentScore();
            } else {
                sw = new SmithWaterman(sseString, sseStringDB);
                sw.setScoringScheme(sm);                
                score = sw.computeAlignmentScore();
            }
            
            ComparisonResult cr = new ComparisonResult(methodName, score);
            cr.setTarget(pdbidDB, chainidDB, graphTypeDB);
            cr.setPropertySource(sseString);
            cr.setPropertyTarget(sseStringDB);
                
            
            results.add(cr);
            Collections.sort(results, new ComparisonResultComparator());
            
            // make sure the list size does never exceed maxNumberOfResults
            if(results.size() > maxNumberOfResults) {
                // just add every result until the maximim number is reached
                Integer overhead = results.size() - maxNumberOfResults;
                for(Integer i = 0; i < overhead; i++) {
                    // always remove the first element, i.e., the one with the lowest score
                    // This will not break the ordering of the rest, so no need to re-order
                    results.remove(0);  
                }
            }
            
                        
        }
        
        
        return(results);
    }
    
    
    /**
     * Compares a single SSEString to all SSEStrings in the database.
     * @param patternSSEString 
     */
    public static void compareSSEStringToDB(String patternSSEString) {
        ArrayList<ComparisonResult> res = CompareOneToDB.getMostSimilarToSSEString(patternSSEString, Settings.getInteger("plcc_I_search_similar_num_results"), Settings.get("plcc_S_search_similar_graphtype"), true);
                
        if(res.size() > 0) {

            for(ComparisonResult result : res) {
                System.out.println("  sim(" + result.getTargetString() + ") via "  + result.getMethod() + ": "  + result.getScore() + "\t(" +  result.getPropertyTarget() + ")"); 
            }

        } else {
            System.err.println("WARNING: Received no similarity results -- is the database empty?");  
        }
        System.out.println("Similarity search for PDB ID '" + Settings.get("plcc_B_search_similar_PDBID") + "' chain '" + Settings.get("plcc_B_search_similar_chainID") + "' graph type '" + Settings.get("plcc_S_search_similar_graphtype") + "' complete (" + res.size() + " results), exiting.");        
    }
    
}
