/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author Tim Schäfer
 */
package algorithms;

import datastructures.SimpleGraphInterface;
import java.util.List;

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
    
}
