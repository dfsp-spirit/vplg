/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Marcus Kessler 2013. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author Marcus Kessler
 */
package plcc;

import java.util.Map;
import net.sourceforge.spargel.datastructures.UAdjListGraph;

/**
 *
 * @author marcus
 */
public class ComplexGraph extends UAdjListGraph {
    
    public Map<Edge, Integer> numHHInteractionsMap;
    public Map<Edge, Integer> numHSInteractionsMap;
    public Map<Edge, Integer> numHLInteractionsMap;
    public Map<Edge, Integer> numSSInteractionsMap;
    public Map<Edge, Integer> numSLInteractionsMap;
    public Map<Edge, Integer> numLLInteractionsMap;
    public Map<Edge, Integer> numAllInteractionsMap;
    public Map<Edge, Integer> numDisulfidesMap;
    public Map<Vertice, String> proteinNodeMap;
    private String pdbid;
    
    
    /**
     * Constructor.
     */
    ComplexGraph(String pdbid) {
        this.pdbid = pdbid;
        numHHInteractionsMap = createEdgeMap();
        numHSInteractionsMap = createEdgeMap();
        numHLInteractionsMap = createEdgeMap();
        numSSInteractionsMap = createEdgeMap();
        numSLInteractionsMap = createEdgeMap();
        numLLInteractionsMap = createEdgeMap();
        numAllInteractionsMap = createEdgeMap();
        numDisulfidesMap = createEdgeMap();
        proteinNodeMap = createVerticeMap();
    }
    
    public Vertice getVerticeFromChain(String chainID){
        for (int i = 0; i < ComplexGraph.this.getNumVertices(); i++){
            if (ComplexGraph.this.proteinNodeMap.get(ComplexGraph.this.getVertice(i)).equals(chainID)){
                return ComplexGraph.this.getVertice(i);
            }
        }
        return null;
    }
    
    public String getPDBID(){
        return this.pdbid;
    }
}
