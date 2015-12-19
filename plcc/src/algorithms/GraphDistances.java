/**
 * Computes the graph distances of a graph given as a SimpleGraphInterface. Lazy, only computes stuff when asked for results.
 * Currently uses Dijkstra's algo.
 * @author spirit
 */
package algorithms;

import datastructures.SimpleGraphInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import proteingraphs.ProtGraph;
import proteingraphs.ProtGraphs;

/**
 *
 * @author ts
 */
public class GraphDistances {
    
    protected SimpleGraphInterface g;
    protected boolean alreadyComputed;
    protected Integer[ ][ ] distMatrix;           // distances of the vertices within this graph
    public Boolean setInfinityDistancesToMinusOne = true;
    
    public GraphDistances(SimpleGraphInterface g) {
        this.g = g;
        this.alreadyComputed = false;
    }
    
    private void compute() {
        this.FloydWarshall();
        this.alreadyComputed = true;
    }
    
    /**
     * Computes and returns the distance matrix. The matrix lists Integer.MAX_VALUE for vertex pairs which are not reachable from each other.
     * @return the distance matrix
     */
    public Integer[][] getDistMatrix() {
        if( ! this.alreadyComputed) {
            this.compute();
        }
        return this.distMatrix;
    }
    
    private void FloydWarshall() {
        distMatrix = new Integer[g.getSize()][g.getSize()];
        // fill with zeros
        for(int i = 0; i < distMatrix.length; i++) {
            Arrays.fill(distMatrix[i], 0);
        }
        
        // set distance to 1 if edge exists between node pair
        for(int i = 0; i < g.getSize(); i++) {
            for(int j = 0; j < g.getSize(); j++) {
                if(g.containsEdge(i, j)) {
                    distMatrix[i][j] = 1;
                }                
            }
        }

        // replace 0 with +infinity
        for(int i = 0; i < g.getSize(); i++) {
            for(int j = 0; j < g.getSize(); j++) {
                if(distMatrix[i][j] == 0) {
                    distMatrix[i][j] = Integer.MAX_VALUE;
                }
                if(i == j) {
                    distMatrix[i][j] = 0;
                }
            }
        }

        // compute path lengths
        Integer sum;
        for(int k = 0; k < g.getSize(); k++) {
            for(int i = 0; i < g.getSize(); i++) {
                for(int j = 0; j < g.getSize(); j++) {
                    if(i != j) {
                        sum = distMatrix[i][k] + distMatrix[k][j];
                        // correct for addition to MAX_VALUE trouble
                        if(distMatrix[i][k] == Integer.MAX_VALUE || distMatrix[k][j] == Integer.MAX_VALUE) {
                            sum = Integer.MAX_VALUE;
                        }
                        distMatrix[i][j] = Math.min(distMatrix[i][j], sum);
                    }
                }
            }
        }
        
        
        if(this.setInfinityDistancesToMinusOne) {
            
            for(int i = 0; i < g.getSize(); i++) {
                for(int j = 0; j < g.getSize(); j++) {
                    if(distMatrix[i][j] == Integer.MAX_VALUE) {
                        distMatrix[i][j] = -1;
                    }
                }
            }
            
        }

    }
    
    /**
     * Test function only
     * @param args ignored
     */
    public static void main(String[] args) {
        System.out.println("Generating graph...");
        ProtGraph pg = ProtGraphs.generate_7tim_A_albe();
        System.out.println("Solving APSP for graph...");
        Boolean ok = true;
        
        GraphDistances gd = new GraphDistances(pg);
        gd.setInfinityDistancesToMinusOne = true;
        Integer[][] distsFloydWarshall = gd.getDistMatrix();
        System.out.println("Floyd Warshall done.");
                
        Integer[][] distsDijkstra = new Integer[pg.getSize()][pg.getSize()];
        for(int i = 0; i < pg.getSize(); i++) {
            distsDijkstra[i] = pg.pathDistanceAllVerts(i);
        }
        System.out.println("Dijkstra done.");
        
        if( distsDijkstra.length != distsFloydWarshall.length) {
            ok = false;
            System.err.println("ERROR: array sizes from different algos dont match");
        }
        if( distsDijkstra.length !=  pg.getSize()) {
            ok = false;
            System.err.println("ERROR: array sizes dont match graph size");
        }
        
        for(int i = 0; i < distsDijkstra.length; i++) {
            for(int j = 0; j < distsDijkstra.length; j++) {
                if(distsDijkstra[i][j] != distsFloydWarshall[i][j]) {
                    ok = false;
                    System.err.println("ERROR: algos do not agree on distance of " + i + " and " + j + ": " + distsDijkstra[i][j] + " / " + distsFloydWarshall[i][j] + ".");
                }
            }            
        }
        
        if(ok) {
            System.out.println("Tests passed. All good");
        }
        else {
            System.out.println("Tests failed.");
        }
    }
}
