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
import java.util.HashSet;
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
     * Computes the average (mean) network clustering coefficient of g, i.e., the average of all normalized local clustering coefficients of the vertices.
     * The local clustering coefficient definition used here is an alternative
     * to the definition of Watts/Strogatz which returns valid values even for vertices with less than 2 neighbors: in that case, the edge density of the graph is returned.
     * The resulting value is normalized by the edge density of the whole graph. The mean over all normalized values is then computed by this function.
     * @return the normalized average network cluster coefficient of g, a value between greater 0 (and potentially greater than 1.0). If the value is greater 1.0, the graph tends to cluster. Returns null if g is empty.
     */
    public Double getAverageNormalizedNetworkClusterCoefficient() {
        return GraphMetrics.averageNormalizedNetworkClusterCoefficient(graph);
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
    
    public Double getMaximumNetworkClusterCoefficient() {
        return GraphMetrics.maximumNetworkClusterCoefficient(graph);
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
    
    /**
     * Computes the cumulative degree distribution of the graph.
     * @return the cumulative degree distribution. a list that contains at each position n the number of vertices in the graph which have at least degree n.
     */
    public List<Integer> getCumulativeDegreeDistribution() {
        return GraphMetrics.cumulativeDegreeDistributionUpTo(graph, null);
    }
    
    /**
     * Computes the cumulative degree distribution of g.
     * @param upToDegree the max degree to include. If set to null, (g.getSize() -1) will be used. You could also specify the max degree of the graph.
     * @return the cumulative degree distribution. a list that contains at each position n the number of vertices in the graph which have at least degree n.
     */
    public List<Integer> getCumulativeDegreeDistributionUpTo(Integer upToDegree) {
        return GraphMetrics.cumulativeDegreeDistributionUpTo(graph, upToDegree);
    }
    
    /**
     * Computes the cumulative degree distribution of g. Uses the getCumulativeDegreeDistributionUpTo function internally, only difference is return type array instead of list.
     * @param upToDegree the max degree to include. If set to null, (g.getSize() -1) will be used. You could also specify the max degree of the graph.
     * @return the cumulative degree distribution. a list that contains at each position n the number of vertices in the graph which have at least degree n.
     */
    public Integer[] getCumulativeDegreeDistributionUpToAsArray(Integer upToDegree) {
        List<Integer> l = GraphMetrics.cumulativeDegreeDistributionUpTo(graph, upToDegree);
        Integer[] arr = l.toArray(new Integer[l.size()]);
        return arr;
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
     * Determines the set of all vertices which have maximum eccentricity (at least 2).
     * @return the set of all vertices which have maximum eccentricity
     */
    public Set<Integer> determineMaxEccVertexSet(){
        return gd.determineMaxEccVertexSet();
    }
    
    
    /**
     * Determines the set of all vertices which have at least the given eccentricity.
     * @param minEcc the eccentricity
     * @return the set of all vertices which have at least the given eccentricity
     */
    public Set<Integer> determineVertexSetWithEccAtLeast(int minEcc) {
        return gd.determineVertexSetWithEccAtLeast(minEcc);
    }
    
    /**
     * Determines the set of all vertices which have at least the given clustering coefficient.
     * @param minClC the minimum clustering coefficient
     * @return the set of all vertices which have at least the given clustering coefficient. (Vertices with undefined CC are never included in the set.)
     */
    public Set<Integer> determineVertexSetWithClCAtLeast(Double minClC) {        
        return GraphMetrics.determineVertexSetWithClCAtLeast(graph, minClC);
    }
    
    /**
     * Determines the set of all vertices which have at most the given clustering coefficient.
     * @param maxClC the maximum clustering coefficient allowed for vertices to be included
     * @return the set of all vertices which have at most the given clustering coefficient in g, by index. (Vertices with undefined CC are never included in the set.)
     */
    public Set<Integer> determineVertexSetWithClCAtMost(Double maxClC) {        
        return GraphMetrics.determineVertexSetWithClCAtMost(graph, maxClC);
    }
    
    /**
     * Determines the set of all vertices which have at least the given degree.
     * @param deg the min degree allowed for vertices to be included
     * @return the set of all vertices which have least the given degree in g, by index.
     */
    public Set<Integer> determineVertexSetWithDegreeAtLeast(Integer deg) {        
        Set<Integer> verts = new HashSet<>();
        for(int i = 0; i < graph.getSize(); i++) {
            if(graph.neighborsOf(i).size() >= deg) {
                verts.add(i);
            }
        }
        return verts;
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
    
    
    /**
     * Creates an overview string with the most important props, testing only function.
     * @return an overview string with the most important props of g
     */
    public String getOverviewPropsString(Boolean addLabel) {
        StringBuilder sb = new StringBuilder();
        sb.append(addLabel? "numVerts: " : "").append(this.getNumVertices()).append("\n");
        sb.append(addLabel? "numEdges: " : "").append(this.getNumEdges()).append("\n");
        sb.append(addLabel? "diameter: " : "").append(this.getGraphDiameter()).append("\n");
        sb.append(addLabel? "radius: " : "").append(this.getGraphRadius()).append("\n");
        sb.append(addLabel? "aSPL: " : "").append(this.getAverageShortestPathLength()).append("\n");
        sb.append(addLabel? "ClC: " : "").append(this.getAverageClusterCoefficient()).append("\n");
        sb.append(addLabel? "naClC: " : "").append(this.getAverageNormalizedNetworkClusterCoefficient()).append("\n");        
        sb.append(addLabel? "avgDegree: " : "").append(this.getAverageDegree()).append("\n");
        sb.append(addLabel? "maxDegree: " : "").append(this.getMaxDegree()).append("\n");
        sb.append(addLabel? "minDegree: " : "").append(this.getMinDegree()).append("\n");
        sb.append(addLabel? "density: " : "").append(this.getDensity()).append("\n");
        sb.append(addLabel? "numCC: " : "").append(this.getConnectedComponents().size()).append("\n");                                
        return sb.toString();
    }
    
}
