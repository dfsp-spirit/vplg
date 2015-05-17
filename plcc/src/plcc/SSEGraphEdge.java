/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plcc;

import graphdrawing.IDrawableEdge;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author spirit
 */
public class SSEGraphEdge implements IDrawableEdge {
    
    private Integer[] vertIndices;
    private String spatRel;
    Set<Integer> vertIndicesNtoC;
    
    SSEGraphEdge(Integer[] vertIndices, String spatRel) {
        this.vertIndices = vertIndices;
        this.spatRel = spatRel;
        this.vertIndicesNtoC = new HashSet<>();
        Collections.addAll(vertIndicesNtoC, vertIndices);
    }
    
    @Override
    public String getSpatRel() {
        return spatRel;
    }

    @Override
    public Set<Integer> getVertPairIndicesNtoC() {
        return vertIndicesNtoC;
    }
    
}
