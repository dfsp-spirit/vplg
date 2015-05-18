/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plcc;

import graphdrawing.IDrawableEdge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author spirit
 */
public class SSEGraphEdge implements IDrawableEdge {
    
    private Integer[] vertIndices;
    private String spatRel;
    List<Integer> vertIndicesNtoC;
    
    SSEGraphEdge(Integer[] vertIndices, String spatRel) {
        this.vertIndices = vertIndices;
        this.spatRel = spatRel;
        this.vertIndicesNtoC = new ArrayList<>();
        Collections.addAll(vertIndicesNtoC, vertIndices);
    }
    
    @Override
    public String getSpatRel() {
        return spatRel;
    }

    @Override
    public List<Integer> getVertPairIndicesNtoC() {
        return vertIndicesNtoC;
    }
    
}
