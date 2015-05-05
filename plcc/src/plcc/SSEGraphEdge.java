/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plcc;

/**
 *
 * @author spirit
 */
public class SSEGraphEdge implements IDrawableEdge {
    
    private Integer[] vertIndices;
    private String spatRel;
    
    SSEGraphEdge(Integer[] vertIndices, String spatRel) {
        this.vertIndices = vertIndices;
        this.spatRel = spatRel;
    }
    
    public String getSpatRel() {
        return spatRel;
    }
    
}
