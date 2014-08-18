/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plcc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author spirit
 */
public class PTGLNotations {
    
    
    ProtGraph g;
    String redNotation;
    String adjNotation;
    String keyNotation;
    String seqNotation;
    
    
    
    
    public PTGLNotations(ProtGraph g) {
        this.g = g;
        redNotation = adjNotation = keyNotation = seqNotation = "";
    }
    
    
    public void computeLinearNotations() {
        
        redNotation = adjNotation = keyNotation = seqNotation = "";
        
        StringBuilder RED = new StringBuilder();
        StringBuilder ADJ = new StringBuilder();
        StringBuilder KEY = new StringBuilder();
        StringBuilder SEQ = new StringBuilder();
        
        
        // prepare the data we need
        List<FoldingGraph> foldingGraphs = g.getConnectedComponents();
        
        List<List<Integer>> connectedComponents = new ArrayList<List<Integer>>();
        for(int i = 0; i < foldingGraphs.size(); i++) {
            connectedComponents.add(foldingGraphs.get(i).getVertexIndexListInParentGraph());
        }
        
        List<Integer> degrees = g.getAllVertexDegrees();
        
        /** Contains a list of the sorted connected components of g. The keys are the indices of the left-most vertex in each set (and the values are the set). */
        HashMap<Integer, List<Integer>> sortedConnectedComponents = new HashMap<Integer, List<Integer>>();
        for(List<Integer> connComp : connectedComponents) {
            Collections.sort(connComp);
            sortedConnectedComponents.put(connComp.get(0), connComp);
        }
        
        // OK, start the linear notation computation
        
        String bracketStart = "[";
        String bracketEnd = "]";
        
        
        
        // all done, set results
        this.redNotation = RED.toString();
        this.adjNotation = ADJ.toString();
        this.keyNotation = KEY.toString();
        this.seqNotation = SEQ.toString();
    }
    
    
    
}
