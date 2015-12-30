/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author Tim Schäfer
 */
package algorithms;

import datastructures.SimpleGraphInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import proteingraphs.ProtGraph;
import proteingraphs.ProtGraphs;

/**
 * Computes the graph distances of a graph given as a SimpleGraphInterface. Lazy, only computes stuff when asked for results.
 * Uses the Floyd–Warshall algorithm.
 * @author ts
 */
public class GraphDistances {
    
    protected SimpleGraphInterface g;
    protected boolean alreadyComputed;
    protected Integer[ ][ ] distMatrix;           // distances of the vertices within this graph
    private Boolean setInfinityDistancesToMinusOne = false;
    private static Integer DISTANCE_INFINITY = Integer.MAX_VALUE;
    protected Set<Integer> verticesOfMaxEccentricity;
    private Boolean graphIsConnected;
    
    public GraphDistances(SimpleGraphInterface g) {
        this.g = g;
        this.alreadyComputed = false;
        this.graphIsConnected = null;        
    }
    
    
    /**
     * Sets the value returned in the matrix for vertex pairs which are not reachable from each other.
     * @param yesOrNo if TRUE, the distances will be set to -1. If FALSE, they will be set to (or rather left at) Integer.MAX_VALUE. Setting this to true is slower, as all V*V values have to be changed.
     */
    public void setInfinityDistancesToMinusOne(Boolean yesOrNo) {
        this.setInfinityDistancesToMinusOne = yesOrNo;
        if(yesOrNo == true) {
            GraphDistances.DISTANCE_INFINITY = -1;
        }
        else {
            GraphDistances.DISTANCE_INFINITY = Integer.MAX_VALUE;
        }
    }
    
    private void compute() {
        this.FloydWarshall();
        this.alreadyComputed = true;
    }
    
    /**
     * Computes and returns the distance matrix. The matrix lists Integer.MAX_VALUE for vertex pairs which are not reachable from each other. If you want the distances to be set to -1 instead, use the setInfinityDistancesToMinusOne function.
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
                    if(Objects.equals(distMatrix[i][j], Integer.MAX_VALUE)) {
                        distMatrix[i][j] = -1;
                    } 
                }
            }
            
        }

    }
    
    private void determineWhetherGraphIsConnected() {
        if( ! this.alreadyComputed) {
            this.compute();
        }
        
        if(this.graphIsConnected != null) {
            return;
        }
        
        Boolean result = true;  //assume it is connected
        
        outerLoop:
        for(int i = 0; i < g.getSize(); i++) {
            for(int j = 0; j < g.getSize(); j++) {
                if(Objects.equals(distMatrix[i][j], GraphDistances.DISTANCE_INFINITY)) {
                    result = false;     // it is not connected if distance betweem any pair of vertices is infinite
                    break outerLoop;
                } 
            }
        }
        
        this.graphIsConnected = result;
    }
    
    /**
     * Determines the set of all vertices which have maximum eccentricity.
     * @return the set of all vertices which have maximum eccentricity
     */
    public Set<Integer> determineMaxEccVertexSet() {
        if(this.verticesOfMaxEccentricity == null) {
            this.verticesOfMaxEccentricity = new HashSet<>();
            Integer maxEcc = this.getGraphDiameter();
            Integer ecc;
            
            for(int i = 0; i < g.getSize(); i++) {
                ecc = getEccentricityOfVertex(i);
                if(ecc != null) {
                    if(ecc.equals(maxEcc)) {
                        this.verticesOfMaxEccentricity.add(i);
                    }
                }
            }
        }                
        return this.verticesOfMaxEccentricity;
    }
    
    /**
     * Determines the set of all vertices which have at least the given eccentricity.
     * @param minEcc the eccentricity
     * @return the set of all vertices which have at least the given eccentricity
     */
    public Set<Integer> determineVertexSetWithEccAtLeast(int minEcc) {
        Set<Integer> v = new HashSet<>();
        Integer ecc;

        for(int i = 0; i < g.getSize(); i++) {
            ecc = getEccentricityOfVertex(i);
            if(ecc != null) {
                if(ecc >= minEcc) {
                    v.add(i);
                }
            }
        }

        return v;
    }
    
    /**
     * The eccentricity ecc(v) of a vertex v is the greatest distance between v and any other vertex. If the graph is not connected, it is null for every vertex.
     * @param v the vertex, by index
     * @return the eccentricity or null if not defined
     */
    public Integer getEccentricityOfVertex(int v) {
        
        if( ! this.alreadyComputed) {
            this.compute();
        }
        
        if(g.getSize() == 0) {
            return null;
        }
        
        this.determineWhetherGraphIsConnected();
        if(this.graphIsConnected) {
                
            Integer ecc = 0;
            for(int i = 0; i < g.getSize(); i++) {
                if(i == v) {
                    continue;
                }
                if(distMatrix[i][v] > ecc) {
                    ecc = distMatrix[i][v];
                }
            }
            
            if(ecc.equals(0)) {
                return null;
            }                        
            return ecc;
            
        }
        else {
            return null;
        }
    }
    
    /**
     * The radius r of a graph is the minimum eccentricity of any vertex.
     * @return the graph radius or null if this graph is not connected or empty
     */
    public Integer getGraphRadius() {
        if(g.getSize() == 0) {
            return null;
        }
        
        this.determineWhetherGraphIsConnected();
        
        if(this.graphIsConnected) {
            Integer minEcc = Integer.MAX_VALUE;
            for(int i = 0; i < g.getSize(); i++) {
                Integer e = this.getEccentricityOfVertex(i);
                if(e != null) {
                    if(e < minEcc) {
                        minEcc = e;
                    }
                }
            }
            if(minEcc == Integer.MAX_VALUE) {
                minEcc = null;
            }
            return minEcc;
        }
        else {
            return null;
        }
    }
    
    /**
     * The diameter d of a graph is the maximum eccentricity of any vertex.
     * @return the graph diameter or null if this graph is not connected or empty
     */
    public Integer getGraphDiameter() {
        if(g.getSize() == 0) {
            return null;
        }
        
        this.determineWhetherGraphIsConnected();
        
        if(this.graphIsConnected) {
            Integer maxEcc = 0;
            for(int i = 0; i < g.getSize(); i++) {
                Integer e = this.getEccentricityOfVertex(i);
                if(e != null) {
                    if(e > maxEcc) {
                        maxEcc = e;
                    }
                }
            }
            return maxEcc;
        }
        else {
            return null;
        }
    }
    
    /**
     * Computes the average shortest path distance within the graph.
     * @return the average shortest path distance, or null if the graph is not connected
     */
    public Double getAverageShortestPathLength() {
        
        this.determineWhetherGraphIsConnected();
        if(this.graphIsConnected) {
            
            
            Double d = .0d;
            Integer numDist = 0;
            for(int i = 0; i < g.getSize(); i++) {
                for(int j = i+1; j < g.getSize(); j++) {
                    d += distMatrix[i][j];
                    numDist++;
                }                
            }
            return d / numDist.doubleValue();
        }
        else {
            return null;
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
        gd.setInfinityDistancesToMinusOne(true);
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
                if(!Objects.equals(distsDijkstra[i][j], distsFloydWarshall[i][j])) {
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
