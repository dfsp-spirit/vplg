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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for computing graph metrics.
 * @author ts
 */
public class GraphMetrics {
    
    
    /**
     * Computes the local cluster coefficient of vertex at index v in the graph g. This follows
     * the definition of Watts/Strogatz.
     * @param g the graph
     * @param v the vertex index
     * @return the local CC of the vertex, i.e., the fraction of actual edges between its neighbors versus max possible edges between them. Note that null is returned if the vertex v has less than 2 neighbors.
     */
    public static Double localClusteringCoefficientUndirected(SimpleGraphInterface g, Integer v) {
        List<Integer> neighbors = g.neighborsOf(v);                
        Integer numNeighbors = neighbors.size();
        
        if(numNeighbors < 2) {
            return null;
        }
        
        Integer maxPossibleEdges = (numNeighbors * (numNeighbors - 1)) / 2;       // division by 2 is due to undirectedness of graph
        Integer numEdgesBetweenNeighbors = 0;
        for(int i = 0; i < numNeighbors; i++) {
            for(int j = i+1; j < numNeighbors; j++) {
                if(g.containsEdge(neighbors.get(i), neighbors.get(j))) {
                    numEdgesBetweenNeighbors++;
                }
            }
        }
        Double lcc = numEdgesBetweenNeighbors.doubleValue() / maxPossibleEdges.doubleValue();
        return lcc;
    }
    
    
    /**
     * Computes the average network cluster coefficient of g, i.e., the average of all local cluster coefficients of the vertices.
     * @param g a graph
     * @return the average network cluster coefficient of g. Note that this is NOT the same as the global CC. Also note that null is returned if the graph is empty.
     */
    public static Double averageNetworkClusterCoefficient(SimpleGraphInterface g) {
        if(g.getSize() == 0) {
            return null;
        }
        
        Double ancc = .0d;
        Integer numVerts = 0;
        for(int i = 0; i < g.getSize(); i++) {
            Double lcc = GraphMetrics.localClusteringCoefficientUndirected(g, i);
            if(lcc != null) {
                ancc += lcc;
                numVerts++;
            }
        }
        return ancc / numVerts.doubleValue();
    }
    
    /**
     * Computes the average network cluster coefficient of g, i.e., the average of all local cluster coefficients of the vertices.
     * @param g a graph
     * @return the average network cluster coefficient of g. Note that this is NOT the same as the global CC. Also note that null is returned if the graph is empty.
     */
    public static Double maximumNetworkClusterCoefficient(SimpleGraphInterface g) {
        if(g.getSize() == 0) {
            return null;
        }
        
        Double max_cc = .0d;
        for(int i = 0; i < g.getSize(); i++) {
            Double lcc = GraphMetrics.localClusteringCoefficientUndirected(g, i);
            if(lcc != null) {
                if(lcc >= max_cc) {
                    max_cc = lcc;
                }
            }
        }
        return max_cc;
    }
    
    
    /**
     * Gives degree distribution as array, from degree 0 to (maxDegreeExclusive - 1).
     * @param maxDegreeExclusive max degree, exclusive. If you put 50, you will get degree distribution from 0 to 49.
     * @return the dgd
     */
    public static Integer[] getDegreeDistributionUpTo(SimpleGraphInterface g, int maxDegreeExclusive) {
        Integer[] dgd = new Integer[maxDegreeExclusive];
        Arrays.fill(dgd, 0);
        
        int degree;
        for(int i = 0; i < g.getSize(); i++) {
            degree = g.neighborsOf(i).size();
            dgd[degree] = dgd[degree] + 1;
        }
        
        return dgd;
    }
    
    /**
     * Computes the degree distribution of g.
     * @param g the graph
     * @param includeZeroCounts whether degrees which do not occur in the graph should be listed (with a value of 0) in the returned map
     * @return a map listing the degree as key, and the number of verts with that degree in g as the value
     */
    public static Map<Integer, Integer> degreeDistribution(SimpleGraphInterface g, Boolean includeZeroCounts) {
        Map<Integer, Integer> dgd = new HashMap<>();
        
        if(includeZeroCounts) {
            for(int i = 0; i < g.getSize(); i++) {
                dgd.put(i, 0);
            }
        }
        
        Integer degree, currentCount;
        for(int i = 0; i < g.getSize(); i++) {
            degree = g.neighborsOf(i).size();
            if(dgd.containsKey(degree)) {
                currentCount = dgd.get(degree);
                dgd.put(degree, currentCount + 1);
            }
            else {
                dgd.put(degree, 1);
            }
        }
        
        return dgd;
    }
    
    
    /**
     * Sums values in a map in a range of keys. Map has to have both types integer.
     * @return the sum of the values in the key range
     */
    private static Integer sumMapIntIntValuesInRange(Map<Integer, Integer> m, Integer startKeyInclusive, Integer endKeyInclusive) {
        Integer sum = 0;
        for(Integer i = startKeyInclusive; i <= endKeyInclusive; i++) {
            if(m.containsKey(i)) {
                sum += m.get(i);
            }
        }
        return sum;
    }
    
    /**
     * Computes the cumulative degree distribution of g.
     * @param g a graph
     * @param upToDegree the max degree to include. If set to null, (g.getSize() -1) will be used.  You could also specify the max degree of the graph.
     * @return the cumulative degree distribution. a list that contains at each position n the number of vertices in the graph which have at least degree n.
     */
    public static List<Integer> cumulativeDegreeDistributionUpTo(SimpleGraphInterface g, Integer upToDegree) {
        List<Integer> cdd = new ArrayList<>();
        Map<Integer, Integer> dgd = GraphMetrics.degreeDistribution(g, Boolean.TRUE);
        
        if(upToDegree == null) {
            upToDegree = (g.getSize() -1);
        }
        
        for(int i = 0; i < upToDegree; i++) {
            cdd.add(GraphMetrics.sumMapIntIntValuesInRange(dgd, i, (g.getSize() -1) ));
        }
        return cdd;
    }
    
}
