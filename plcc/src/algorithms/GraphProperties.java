/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author Tim Schäfer
 */
package algorithms;

import datastructures.SimpleGraphInterface;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tools.DP;

/**
 * Computes various graph properties for a graph and stores the results. This is kind of a result container and a facade, which calls all the other graph algorithms in the background. This class is lazy.
 * @author spirit
 */
public class GraphProperties {
    
    /** The graph this works on. */
    protected SimpleGraphInterface graph;
    
    /** The largest connected component of graph */
    protected SimpleGraphInterface subgraph;
    
    protected ConnectedComponents cc;
    protected GraphDistances gd;
    protected Map<Integer, Integer> degreeDist;
    
    public GraphProperties(final SimpleGraphInterface g) {
        this.graph = g;
        if(g == null) { DP.getInstance().e("GraphProperties", "Constructor: Graph is null."); }
        this.cc = new ConnectedComponents(g);
        this.gd = new GraphDistances(g);
        this.degreeDist = null;
    }
    
    public Integer getNumVertices() {
        if(this.graph == null) { DP.getInstance().e("GraphProperties", "getNumVertices: Graph is null. Returning 0 as size."); return 0; }
        return this.graph.getSize();
    }
    
    public Integer getNumEdges() {
        Integer e = 0;
        for(int i = 0; i < graph.getSize(); i++) {
            e += graph.neighborsOf(i).size();
        }
        return e/2;
    }
    
    /**
     * Returns the average vertex degree of g.
     * @return the average vertex degree, i.e., 2*|E| / |V|.
     */
    public Double getAverageDegree() {
        if(getNumVertices() == null) { return null; }
        if(getNumVertices() == 0) { return 0D; }
        Double E = getNumEdges().doubleValue();
        Double V = getNumVertices().doubleValue();
        return (Double)((2 * E) / V);
    }
    
    /**
     * The graph density measures how many edges are in set E compared to the maximum possible number of edges between vertices in set V.
     * @return the graph density, i.e.,  2 * |E| / (|V| * (|V| − 1))
     */
    public Double getDensity() {
        if(getNumVertices() == null) { return null; }
        if(getNumVertices() == 0) { return 0D; }
        Double E = getNumEdges().doubleValue();
        Double V = getNumVertices().doubleValue();
        return (Double)((2 * E) / (V * (V-1)) );
    }
    
    public Double getAverageClusterCoefficient() {
        return GraphMetrics.averageNetworkClusterCoefficient(graph);
    }
    
    public Integer[] getDegreeDistributionUpTo(int m) {
        return GraphMetrics.getDegreeDistributionUpTo(graph, m);
    }
    
    public Double getAverageShortestPathLength() {
        return gd.getAverageShortestPathLength();
    }
    
    public Map<Integer, Integer> getDegreeDistribution(Boolean includeZeros) {
        if(this.degreeDist == null) {
            this.degreeDist = GraphMetrics.degreeDistribution(graph, includeZeros);
        }
        return this.degreeDist;
    }
    
    /**
     * Determines the maximum degree of the graph
     * @return the maximum degree of the graph.
     */
    public Integer getMaxDegree() {
        Map<Integer, Integer> dgd = this.getDegreeDistribution(false);
        Set<Integer> degrees = dgd.keySet();
        if(degrees.isEmpty()) { return 0; }
        return Collections.max(degrees);
    }
    
    /**
     * Determines the minimum degree of the graph.
     * @return the minimum degree of the graph
     */
    public Integer getMinDegree() {
        Map<Integer, Integer> dgd = this.getDegreeDistribution(false);
        Set<Integer> degrees = dgd.keySet();
        if(degrees.isEmpty()) { return 0; }
        return Collections.min(degrees);
    }
    
    public List<Integer> getCumulativeDegreeDistribution() {
        return GraphMetrics.cumulativeDegreeDistribution(graph);
    }
    
    /**
     * The radius r of a graph is the minimum eccentricity of any vertex.
     * @return the graph radius or null if this graph is not connected or empty
     */
    public Integer getGraphRadius() {
        return gd.getGraphRadius();
    }
    
    /**
     * The diameter d of a graph is the maximum eccentricity of any vertex.
     * @return the graph diameter or null if this graph is not connected or empty
     */
    public Integer getGraphDiameter() {
        return gd.getGraphDiameter();
    }
    
    
    /**
     * Checks whether the graph is connected, i.e., every vertex is reachable from every other vertex
     * @return whether the graph is connected
     */
    public Boolean getGraphIsConnected() {
        return cc.inputGraphIsConnected();
    }
    
    /**
     * Returns a list of connected components.
     * @return a list of connected components.
     */
    public List<SimpleGraphInterface> getConnectedComponents() {
        return cc.get();
    }
    
    /**
     * Returns (and computes if needed) the largest CC.
     * @return the largest CC. If several largest of equal size exist, the last one is returned. Returns null if the graph has no verts (and thus the largest CC has size 0).
     */
    public SimpleGraphInterface getLargestConnectedComponent() {
        return cc.getLargest();
    }
    
}
