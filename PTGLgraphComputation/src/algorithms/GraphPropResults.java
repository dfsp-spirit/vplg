/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2016. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author Tim Schäfer
 */
package algorithms;

/**
 * A simple container to hold the result of a graph property computation performed by the <code>GraphProperties</code> class. This is useful for preventing to compute some stuff several times, since some graph properties are computed each time you request them from <code>GraphProperties</code>.
 * This container holds now references to the graph (or the connected components), only descriptive data.
 * @author ts
 */
public class GraphPropResults {
    
    public Integer numVertices;
    public Integer numEdges;
    public Integer graphDiameter;
    public Integer graphRadius;
    public Double averageShortestPathLength;
    public Double averageClusterCoefficient;
    public Double averageNormalizedNetworkClusterCoefficient;
    public Double averageDegree;
    public Integer maxDegree;
    public Integer minDegree;
    public Double density;
    public Integer numConnectedComponents;    
}
