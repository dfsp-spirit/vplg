/**
 * Computes the graph distances of a graph given as a SimpleGraphInterface. Lazy, only computes stuff when asked for results.
 * Currently uses Dijkstra's algo.
 * @author spirit
 */
package algorithms;

import datastructures.SimpleGraphInterface;
import java.util.List;

/**
 *
 * @author ts
 */
public class GraphDistances {
    
    protected SimpleGraphInterface g;
    protected boolean alreadyComputed;
    protected Integer[ ][ ] distMatrix;           // distances of the vertices within this graph
    
    public GraphDistances(SimpleGraphInterface g) {
        this.g = g;
        this.alreadyComputed = false;
    }
    
    private void compute() {
        
    }
    
    public Integer[][] getDistMatrix() {
        if( ! this.alreadyComputed) {
            this.compute();
        }
        return this.distMatrix;
    }
}
