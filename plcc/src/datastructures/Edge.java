/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package datastructures;

/**
 * A simple edge used to be used by the Graphs class.
 * @author spirit
 */
class Edge {
    protected Integer startVertex;
    protected Integer endVertex;
    protected Integer edgeType;
    protected Integer defaultEdgeType = Graph.EDGETYPE_EDGE;

    /**
     * Creates an edge of type edgeType from startVertex to endVertex.
     * @param startVertex the start vertex
     * @param endVertex the end vertex
     * @param edgeType the edge type. Use one of the constants in Graph.
     */
    public Edge(Integer startVertex, Integer endVertex, Integer edgeType) {
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.edgeType = edgeType;
    }
    
    /**
     * Creates an edge of type edgeType from vertexIndices[0] to vertexIndices[1].
     * @param vertexIndices the Integer array of length 2 holding the start vertex at index 0 and the end vertex at index 1.
     * @param edgeType the edge type. Use one of the constants in Graph.
     */
    public Edge(Integer[] vertexIndices, Integer edgeType) {
        this.startVertex = vertexIndices[0];
        this.endVertex = vertexIndices[1];
        this.edgeType = edgeType;
    }
    
    /**
     * Creates an edge of type edgeType from vertexIndices[0] to vertexIndices[1].
     * @param vertexIndices the Integer array of length 2 holding the start vertex at index 0 and the end vertex at index 1.
     */
    public Edge(Integer[] vertexIndices) {
        this.startVertex = vertexIndices[0];
        this.endVertex = vertexIndices[1];
        this.edgeType = this.defaultEdgeType;
    }
    
    /**
     * Returns the index of the start vertex.
     * @return the vertex
     */
    public Integer getStartVertex() {
        return(this.startVertex);
    }
    
    
    /**
     * Returns the index of the end vertex.
     * @return the vertex
     */
    public Integer getEndVertex() {
        return(this.endVertex);
    }
    
    
    /**
     * Returns the edge type. One of the constants in Graph.
     * @return the edge type. Note that 0:= no edge, >=1 := some edge.
     */
    public Integer getType() {
        return(this.edgeType);
    }
    
}
