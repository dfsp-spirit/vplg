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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import plcc.DBManager;
import plcc.ProtGraph;
import plcc.ProtGraphs;
import plcc.Settings;

/**
 * This class implements methods that compare an element against the whole database.
 * @author ts
 */
public class CompareOneToDB {
    
    
    /**
     * Returns the maxNumberOfResults most similar to a given SSEString. This is just a string-based dummy method, it does not use
     * the graph contacts and structure to compare proteins.
     * @param sseString the SSE string of a protein chain
     * @param maxNumberOfResults the maximum number of proteins to return. E.g., if this is 3, the 3 most similar protein are returned. If several proteins compete for the last place in the list (because they received the same similarity score), a random one is chosen.
     * @param graphType the graph type to use
     * @param global whether to use global matching (NeedlemanWunsch). If this is false, local matching (SmithWaterman) will be used instead.
     * @return a comparison result
     */
    private static ArrayList<ComparisonResult> getMostSimilarToSSEString(String sseString, Integer maxNumberOfResults, String graphType, Boolean global) {
        
        String methodName = Similarity.SIMILARITYMETHOD_STRINGSSE;
        
        //if(maxNumberOfResults != 1) {
        //    System.err.println("WARNING: getMostSimilarToSSEString: parameter maxNumberOfResults not implemented yet and fixed at 1, returning only best score.");
        //}
        
        ArrayList<ComparisonResult> results = new ArrayList<ComparisonResult>();        
        ArrayList<String[]> sseStrings  = new ArrayList<String[]>();
        
        try {
            sseStrings = DBManager.getAllGraphData(graphType);
        } catch(Exception e) {
            System.err.println("WARNING: CompareOneToDB: SQL error while getting all SSEStrings: '" + e.getMessage() + "'.");
        }
        
        System.out.println("Comparing given protein graph to " + sseStrings.size() + " of the protein graphs in the database.");
        
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
     * TESTING: Compares a single SSEString to all SSEStrings in the database.
     * @param patternSSEString 
     */
    public static void performSSEStringComparison(String patternSSEString) {
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
    
    
    
     /**
     * Performs the comparison and prints results to STDOUT.
     */
    public static void performGraphSetComparison() {
        ArrayList<ComparisonResult> res = CompareOneToDB.getMostSimilarByGraphSetBased(Settings.get("plcc_S_search_similar_PDBID"), Settings.get("plcc_S_search_similar_chainID"), Settings.get("plcc_S_search_similar_graphtype"), Settings.getInteger("plcc_I_search_similar_num_results"));
                
        if(res.size() > 0) {

            for(ComparisonResult result : res) {
                //System.out.println("  sim(" + result.getTargetString() + ") via "  + result.getMethod() + ": "  + result.getScore() + "\t(" +  result.getPropertyTarget() + ")"); 
                System.out.println("  sim(" + result.getTargetString() + ") via "  + result.getMethod() + ": "  + result.getScore()); 
            }

        } else {
            System.err.println("WARNING: Received no similarity results -- is the database empty?");  
        }
        System.out.println("Similarity search for PDB ID '" + Settings.get("plcc_B_search_similar_PDBID") + "' chain '" + Settings.get("plcc_B_search_similar_chainID") + "' graph type '" + Settings.get("plcc_S_search_similar_graphtype") + "' complete (" + res.size() + " results), exiting.");        
    }
    
    
    /**
     * Performs the comparison and prints results to STDOUT.
     */
    public static void performGraphCompatGraphComparison() {
        ArrayList<ComparisonResult> res = CompareOneToDB.getMostSimilarByCompatibilityGraph(Settings.get("plcc_S_search_similar_PDBID"), Settings.get("plcc_S_search_similar_chainID"), Settings.get("plcc_S_search_similar_graphtype"), Settings.getInteger("plcc_I_search_similar_num_results"));
                
        System.out.println("Comparison done, checking results.");
        
        if(res.size() > 0) {

            for(ComparisonResult result : res) {
                //System.out.println("  sim(" + result.getTargetString() + ") via "  + result.getMethod() + ": "  + result.getScore() + "\t(" +  result.getPropertyTarget() + ")"); 
                System.out.println("  sim(" + result.getTargetString() + ") via "  + result.getMethod() + ": "  + result.getScore()); 
            }

        } else {
            System.err.println("WARNING: Received no similarity results -- is the database empty?");  
        }
        System.out.println("Similarity search for PDB ID '" + Settings.get("plcc_B_search_similar_PDBID") + "' chain '" + Settings.get("plcc_B_search_similar_chainID") + "' graph type '" + Settings.get("plcc_S_search_similar_graphtype") + "' complete (" + res.size() + " results), exiting.");        
    }
    

    
    /**
     * Compares the given graph with all others in the database using compatibility graph-based graph comparison methods.
     * s@param g_pdbid the PDB ID used to identify the pattern graph
     * @param g_chainid the chain ID used to identify the pattern graph
     * @param g_graphtype the graph type used to identify the pattern graph
     * @return a list of comparison results
     */
    public static ArrayList<ComparisonResult> getMostSimilarByCompatibilityGraph(String g_pdbid, String g_chainid, String g_graphtype, Integer maxNumberOfResults) {
        
        System.out.println("Retrieving " + g_graphtype + " graph for PDB entry " + g_pdbid + " chain " + g_chainid + " from DB.");
        
        String graphString = null;
        ArrayList<ComparisonResult> results = new ArrayList<ComparisonResult>();
        
        // get the graph string of the pattern graph
        try { 
            graphString = DBManager.getGraphString(g_pdbid, g_chainid, g_graphtype); 
        } catch (SQLException e) { 
            System.err.println("ERROR: SQL: Could not get graph from DB: '" + e.getMessage() + "'."); 
            return(results);            
        }
        
        if(graphString == null) {
            System.err.println("WARNING: DB: getMostSimilarByGraphSetBased: Pattern graph not found in database.");
            return(results);
        }
        
        ProtGraph pg = ProtGraphs.fromPlccGraphFormatString(graphString);
        
        String methodName = Similarity.SIMILARITYMETHOD_GRAPHCOMPAT;
        
        
        ArrayList<String[]> graphData  = new ArrayList<String[]>();
        
        try {
            graphData = DBManager.getAllGraphData(g_graphtype);
        } catch(Exception e) {
            System.err.println("WARNING: CompareOneToDB: SQL error while getting all SSEStrings: '" + e.getMessage() + "'.");
        }
        
        System.out.println("Comparing given protein graph to " + graphData.size() + " of the protein graphs in the database.");
        
        String pdbidDB, chainidDB, graphTypeDB, sseStringDB, graphStringDB;
        Integer score;
        ProtGraph pgDB;    
        
        // iterate thtough all graphs in the DB and compare our template graph with all of them
        for(String[] sseData : graphData) {
            pdbidDB = sseData[0];
            chainidDB = sseData[1];
            graphTypeDB = sseData[2];
            sseStringDB = sseData[3];
            graphStringDB = sseData[4];
            
            
            
            try {
                pgDB = ProtGraphs.fromPlccGraphFormatString(graphStringDB);
            } catch(Exception e) {
                System.err.println("WARNING: Could not create protein graph from graph string of " + pdbidDB + " chain " + chainidDB + " gt " + graphTypeDB + ": '" + e.getLocalizedMessage() + "'.");
                continue;
            }
                                    
            
            
            GraphSimilarity simSSE = new GraphSimilarity(pg, pgDB);
            score = simSSE.compareByCompatibilityGraph();
            
            ComparisonResult cr = new ComparisonResult(methodName, score);
            cr.setTarget(pdbidDB, chainidDB, graphTypeDB);
            cr.setPropertySource(graphString);
            cr.setPropertyTarget(graphStringDB);
                
            
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
     * 
     * 
     * Compares the given graph with all others in the database using set-based graph comparison methods.
     * @param g_pdbid the PDB ID used to identify the pattern graph
     * @param g_chainid the chain ID used to identify the pattern graph
     * @param g_graphtype the graph type used to identify the pattern graph
     * @return a list of comparison results
     */
    public static ArrayList<ComparisonResult> getMostSimilarByGraphSetBased(String g_pdbid, String g_chainid, String g_graphtype, Integer maxNumberOfResults) {

        System.out.println("Retrieving " + g_graphtype + " graph for PDB entry " + g_pdbid + " chain " + g_chainid + " from DB.");
        
        String graphString = null;
        ArrayList<ComparisonResult> results = new ArrayList<ComparisonResult>();
        
        // get the grapg of the pattern graph
        try { 
            graphString = DBManager.getGraphString(g_pdbid, g_chainid, g_graphtype); 
        } catch (SQLException e) { 
            System.err.println("ERROR: SQL: Could not get graph from DB: '" + e.getMessage() + "'."); 
            return(results);            
        }
        
        if(graphString == null) {
            System.err.println("WARNING: DB: getMostSimilarByGraphSetBased: Pattern graph not found in database.");
            return(results);
        }
        
        ProtGraph pg = ProtGraphs.fromPlccGraphFormatString(graphString);
        //System.out.println("  Loaded graph with " + pg.numVertices() + " vertices and " + pg.numEdges() + " edges.");
        
        // now get all the other graphs and compare them to the pattern graph
        
        String methodName = Similarity.SIMILARITYMETHOD_GRAPHSET;
        
        //if(maxNumberOfResults != 1) {
        //    System.err.println("WARNING: getMostSimilarToSSEString: parameter maxNumberOfResults not implemented yet and fixed at 1, returning only best score.");
        //}
        
        ArrayList<String[]> graphData  = new ArrayList<String[]>();
        
        try {
            graphData = DBManager.getAllGraphData(g_graphtype);
        } catch(Exception e) {
            System.err.println("WARNING: CompareOneToDB: SQL error while getting all SSEStrings: '" + e.getMessage() + "'.");
        }
        
        System.out.println("Comparing given protein graph to " + graphData.size() + " of the protein graphs in the database.");
        
        String pdbidDB, chainidDB, graphTypeDB, sseStringDB, graphStringDB;
        Integer score;
        ProtGraph pgDB;    
        
        for(String[] sseData : graphData) {
            pdbidDB = sseData[0];
            chainidDB = sseData[1];
            graphTypeDB = sseData[2];
            sseStringDB = sseData[3];
            graphStringDB = sseData[4];
            
            
            
            try {
                pgDB = ProtGraphs.fromPlccGraphFormatString(graphStringDB);
            } catch(Exception e) {
                System.err.println("WARNING: Could not create protein graph from graph string of " + pdbidDB + " chain " + chainidDB + " gt " + graphTypeDB + ".");
                continue;
            }
            
            GraphSimilarity simSSE = new GraphSimilarity(pg, pgDB);
            score = simSSE.compareGraphsSetBased();
            
            ComparisonResult cr = new ComparisonResult(methodName, score);
            cr.setTarget(pdbidDB, chainidDB, graphTypeDB);
            cr.setPropertySource(graphString);
            cr.setPropertyTarget(graphStringDB);
                
            
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
    
}
