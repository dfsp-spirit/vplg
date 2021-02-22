/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package algorithms;

/**
 *
 * @author spirit
 */
public class TreeNodeData {
    
    public String label;
    public String cliqueString;
    boolean markedSpecial;
    
    public TreeNodeData(String s) {
        label = s;
        cliqueString = "";
        markedSpecial = false;
    }
    
    public void markSpecial(boolean s) {
        this.markedSpecial = s;
    }
    
    public void markSpecial(boolean s, String info) {
        this.markedSpecial = s;
        this.cliqueString = info;
    }
    
    @Override public String toString() {
        if(this.markedSpecial) {
            return "(" + label + ")" + cliqueString;
        }
        return label;
    }
    
}
