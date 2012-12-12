/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package datastructures;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import plcc.GraphMLFormat;
import plcc.TrivialGraphFormat;

import java.io.*;

// Xerces 1 or 2 additional classes for XML generation (GraphML format).
//import org.apache.xml.serialize.*;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer.*;
import java.util.HashMap;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * A generic, abstract graph class. The simplest non-abstract implementation is UndirectedGraph.
 * @author spirit
 */
public abstract class Graph<V> implements TrivialGraphFormat, GraphMLFormat {
    
    /** Some vertices. Graphs like them. */
    protected ArrayList<V> vertices;
    //protected ArrayList<ArrayList<Edge>> edges;
    
    protected HashMap<String, Object> metadata;
    protected ArrayList<HashMap<String, Object>> vertexMetadata;
    
    /** Some edges because vertices like to be linked. */
    protected Integer[][] edgeMatrix;
    
    public static final Integer EDGETYPE_NONE = 0;
    public static final Integer EDGETYPE_EDGE = 1;
    
    public final Integer EDGETYPE_DEFAULT = Graph.EDGETYPE_EDGE;
    
    protected Boolean connectedComponentsComputed;
    protected Boolean distancesCalculated;
    protected ArrayList<Graph> connectedComponents;
    
    /**
     * Constructs a graph from a vertex list. The list may be empty, of course.
     * @param vertList the vertex list
     */
    public Graph(ArrayList<V> vertList) {
        this.vertices = vertList;
        //this.edges = new ArrayList<ArrayList<Edge>>();
        this.edgeMatrix = new Integer[vertList.size()][vertList.size()];
        this.metadata = new HashMap<String, Object>();
        this.vertexMetadata = new ArrayList<HashMap<String, Object>>();
        
        for(Integer i = 0; i < this.vertices.size(); i++) {
            for(Integer j = 0; j < this.vertices.size(); j++) {
                this.edgeMatrix[i][j] = Graph.EDGETYPE_NONE;            
            }            
        }
        
    }
    
    public Graph() {
        this.vertices = new ArrayList<V>();
        //this.edges = new ArrayList<ArrayList<Edge>>();
        this.edgeMatrix = new Integer[vertices.size()][vertices.size()];
        this.metadata = new HashMap<String, Object>();
        this.vertexMetadata = new ArrayList<HashMap<String, Object>>();
        
        for(Integer i = 0; i < this.vertices.size(); i++) {
            for(Integer j = 0; j < this.vertices.size(); j++) {
                this.edgeMatrix[i][j] = Graph.EDGETYPE_NONE;            
            }            
        }
        
    }
    
    /**
     * Adds an edge to this graph.
     * @param e 
     */
    public void addEdge(Edge e) {
        this.edgeMatrix[e.getStartVertex()][e.getEndVertex()] = e.getType();
    }
    
    
    public void addEdge(Integer start, Integer end, Integer edgeType) {
        this.edgeMatrix[start][end] = edgeType;
    }
    
    
    public void addEdge(Integer start, Integer end) {
        this.edgeMatrix[start][end] = this.EDGETYPE_DEFAULT;
    }
    
    
    /**
     * Returns the vertex with the given index.
     * @param index the vertex index
     * @return the vertex
     */
    public V getVertex(Integer index) {
        return(this.vertices.get(index));
    }
    
    
    /**
     * Determines whether an edge exists between the vertices v1 and v2.
     * @param v1
     * @param v2
     * @return true if it does
     */
    public Boolean hasEdge(Integer i, Integer j) {
        return(this.edgeMatrix[i][j] != EDGETYPE_NONE);
    }
    
    
    /**
     * Returns the number of vertices
     * @return the number of vertices
     */
    public Integer getSize() {
        return(this.vertices.size());
    }
    
    
    /**
     * Returns the number of vertices
     * @return the number of vertices
     */
    public Integer numVertices() {
        return(this.vertices.size());
    }
    
    /**
     * Returns the number of vertices
     * @return the number of vertices
     */
    public Integer numEdges() {
        Integer num = 0;
        for(Integer i = 0; i < this.numVertices(); i++) {
            for(Integer j = i+1; j < this.numVertices(); j++) {
                if(this.hasEdge(i, j)) {
                    num++;
                }         
            }
        }
        return(num);
    }
    
    
    /**
     * Returns the number of vertices
     * @return the number of vertices
     */
    public Integer numEdgesOfType(Integer edgeType) {
        Integer num = 0;
        for(Integer i = 0; i < this.numVertices(); i++) {
            for(Integer j = i+1; j < this.numVertices(); j++) {
                if(this.edgeMatrix[i][j] == edgeType) {
                    num++;
                }         
            }
        }
        return(num);
    }
    
    
    
    /**
     * Returns all edges.
     * @return all edges
     */
    public Integer[][] getEdgeMatrix() {
        return(this.edgeMatrix);
    }
    
    public ArrayList<Integer[]> getEdges() {
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        
        for(Integer i = 0; i < this.edgeMatrix.length; i++) {
            for(Integer j = 0; j < this.edgeMatrix.length; j++) {
                if(this.edgeMatrix[i][j] != EDGETYPE_NONE) {
                    edges.add(new Integer[] { i, j });
                }
            }            
        }
        
        return edges;
    }
    
    /**
     * Returns the edge type of the edge between the vertices (v1, v2). Note that the edge type
     * may be NONE unless you check this using hasEdge() before calling this function.
     * @param v1 vertex 1
     * @param v2 vertex 2
     * @return the edge type
     */
    public Integer getEdgeType(Integer v1, Integer v2) {
        return(this.edgeMatrix[v1][v2]);
    }
    
    /**
     * Returns all vertices.
     * @return all vertices
     */
    public ArrayList<V> getVertices() {
        return(this.vertices);
    }
    
    
    /**
     * Returns a trivial graph format string representation of this graph.
     * @return the TGF string
     */
    public String toTrivialGraphFormat() {
        String tgf = "";
        
        for(Integer i = 0; i < this.numVertices(); i++) {
            tgf += "" + i + "\n";
        }
        
        tgf += "#\n";
        
        for(Integer i = 0; i < this.numVertices(); i++) {
            for(Integer j = i+1; j < this.numVertices(); j++) {
                if(this.hasEdge(i, j)) {
                    tgf += "" + i + " " + j + "\n";
                }            
            }
        }                
        return(tgf);
    }
    
    
    /**
     * Returns a GraphML format string representation of this graph.
     * GraphML is an XML-based format, see http://graphml.graphdrawing.org/ for details.
     * @return the GraphML format string
     */
    public String toGraphMLFormat() throws SAXException, IOException {
        
        // Prepare format
        //String filename = "tmp_graph.xml";
        StringWriter writer = new StringWriter();
        
        //FileOutputStream writer = new FileOutputStream(filename);
        OutputFormat of = new OutputFormat("XML", "UTF-8", true);
        of.setIndent(1);
        of.setIndenting(true);
        of.setDoctype(null, "http://graphml.graphdrawing.org/dtds/1.0rc/graphml.dtd");
        XMLSerializer serializer = new XMLSerializer(writer, of);
        
        // SAX ContentHandler
        ContentHandler hd = null;
        hd = serializer.asContentHandler();
        hd.startDocument();
        String namespaceURI = "http://graphml.graphdrawing.org/dtds/1.0rc/graphml.dtd";
        String attributeURI = "http://graphml.graphdrawing.org/dtds/1.0rc/graphml.dtd";
        
        // ********** graph object **********
        //hd.processingInstruction("xml-stylesheet","type=\"text/xsl\" href=\"users.xsl\"");
        AttributesImpl atts = new AttributesImpl();
        atts.clear();
        atts.addAttribute(attributeURI, "", "ID", "CDATA", "G");  // some graph ID
        atts.addAttribute(attributeURI, "", "EDGEDEFAULT", "CDATA", "UNDIRECTED");  // undirected edges
        hd.startElement(namespaceURI, "", "GRAPH", atts);
        
                
        // ********** get data from graph: vertices **********
                
        String vertexID, vertex;
        
        for (Integer i=0; i < this.vertices.size(); i++) {
            // prepare attributes, e.g. "property=value"
            atts.clear();
            vertexID = this.vertices.get(i).toString();
            vertex = this.vertices.get(i).toString();
            
            atts.addAttribute(attributeURI, "", "ID", "CDATA", vertexID);  // vertex ID
            
            // create the node/vertex element, e.g. "<protein>identifier</protein>
            hd.startElement(namespaceURI, "", "NODE", atts);
            hd.characters(vertex.toCharArray(), 0, vertex.length());
            hd.endElement(namespaceURI, "", "NODE");
        }
        
        // ********** get data from graph: edges **********
        
        String edgeID, edge, sourceVertex, targetVertex;
        Integer edgeNum = 0;
        
        for (Integer i = 0; i < this.edgeMatrix.length; i++) {
            for (Integer j = i+1; j < this.edgeMatrix.length; j++) {
                
                if(this.edgeMatrix[i][j] != Graph.EDGETYPE_NONE) {
                    // prepare attributes, e.g. "property=value"
                    atts.clear();
                    edgeID = edgeNum.toString(); //this.edgeMatrix[i][j].toString();
                    edge = edgeNum.toString();
                    sourceVertex = this.vertices.get(i).toString();
                    targetVertex = this.vertices.get(j).toString();

                    atts.addAttribute(attributeURI, "", "ID", "CDATA", edgeID);  // edge ID
                    atts.addAttribute(attributeURI, "", "SOURCE", "CDATA", sourceVertex);  // source vertex
                    atts.addAttribute(attributeURI, "", "TARGET", "CDATA", targetVertex);  // target vertex

                    // create the edge element, e.g. "<protein>identifier</protein>
                    hd.startElement(namespaceURI, "", "EDGE", atts);
                    hd.characters(edge.toCharArray(), 0, edge.length());
                    hd.endElement(namespaceURI, "", "EDGE");
                    edgeNum++;
                }
            }
        }
        
        // ********** end the graph **********
        
        hd.endElement(namespaceURI, "", "GRAPH");
        
        
        hd.endDocument();
        String doc = writer.toString();
        writer.close();   
        
        return(doc);
    }
    
    
    
    @Override public String toString() {
        String s = "GRAPH_VERTICES(" + this.numVertices() +  "):";
        for(Integer i = 0; i < this.numVertices(); i++) {
            s += " [" + i + ": " + this.vertices.get(i) + ", " + this.vertices.get(i) + "]";
        }
        s += "\nGRAPH_EDGES(" + this.numEdges() + "):";
        
        for(Integer i = 0; i < this.numVertices(); i++) {
            for(Integer j = i+1; j < this.numVertices(); j++) {
                if(this.hasEdge(i, j)) {
                    s += " (" + i + "," + j + ")";
                }
                
            }               
        }
        
        return(s);
    }
    
    
    /**
     * Retrieves edge meta data.
     * @param key the key to get
     * @return the object entry
     */
    public Object getMetadataEntry(String key) {
        return this.metadata.get(key);
    }
    
    
    /**
     * Sets the meta data entry.
     * @param key the key
     * @param value the value
     */
    public void setMetadataEntry(String key, Object value) {
        this.metadata.put(key, value);    
    }
    
    
    /**
     * Retrieves vertex meta data.
     * @param vertIndex the index of the vertex
     * @param key the key to get
     * @return the object entry
     */
    public Object getVertexMetadataEntry(Integer vertIndex, String key) {
        return this.vertexMetadata.get(vertIndex).get(key);
    }
    
    
    /**
     * Sets the vertex meta data entry.
     * @param vertIndex the index of the vertex
     * @param key the key
     * @param value the value
     */
    public void setVertexMetadataEntry(Integer vertIndex, String key, Object value) {
        this.vertexMetadata.get(vertIndex).put(key, value);
    }
    
    
    /**
     * DOT language output support. See http://en.wikipedia.org/wiki/DOT_language for details.
     */    
    public String toDOTLanguageFormat() {
        
        String graphLabel = "Graph";
        
        // start graph
        String dlf = "graph " + graphLabel + " {\n";
        
        // print the nodes
        V vertex; String shapeModifier, vertColor;
        for(Integer i = 0; i < this.getSize(); i++) {
            
                        
            vertex = this.vertices.get(i);
            
            shapeModifier = " shape=circle";
            vertColor = " color=black";
            
            dlf += "    " + i + " [label=\"" + i + "\"" + shapeModifier + vertColor + "];\n";
        }
        
        // print the edges        
        Integer src, tgt;
        String colorModifier, lineModifier, edgeLabel;
        ArrayList<Integer[]> edges = this.getEdges();
        for(Integer[] edge : edges) {
            src = edge[0];
            tgt = edge[1];
            
            colorModifier = " color=gray";
            lineModifier = "";
            edgeLabel = "label=\"" + src + "<=>" + tgt + "\"";                                        
            dlf += "    " + src + " -- " + tgt + " [" + edgeLabel + colorModifier + lineModifier + "]" + ";\n";                        
        }
        
        
        // close graph
        dlf += "}\n";
        
        return(dlf);
    }
    
    
    /** Returns a Graph Modelling Language format representation of this graph.
     *  See http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-technical-report.pdf for the publication 
     * and http://en.wikipedia.org/wiki/Graph_Modelling_Language for a brief description.
     * 
     */
    public String toGraphModellingLanguageFormat() {
        
        String gmlf = "";
        
        // print the header
        String comment = "Graph";
        String startNode = "  node [";
        String endNode   = "  ]";
        String startEdge = "  edge [";
        String endEdge   = "  ]";
        
        gmlf += "graph [\n";
        gmlf += "  id " + 1 + "\n";
        gmlf += "  label \"" + "VPLG Protein Graph" + "\"\n";
        gmlf += "  comment \"" + comment + "\"\n";
        gmlf += "  directed 0\n";
        gmlf += "  isplanar 1\n";
        
        
        // print all nodes
        V vertex;
        for(Integer i = 0; i < this.getSize(); i++) {
            vertex = this.vertices.get(i);
            gmlf += startNode + "\n";
            gmlf += "    id " + i + "\n";
            gmlf += "    label \"" + i + "\"\n";           
            gmlf += endNode + "\n";
        }
        
        // print all edges
        Integer src, tgt;
        ArrayList<Integer[]> edges = this.getEdges();
        for(Integer[] edge : edges) {
            src = edge[0];
            tgt = edge[1];
            
            gmlf += startEdge + "\n";
            gmlf += "    source " + src + "\n";
            gmlf += "    target " + tgt + "\n";            
            gmlf += "    label \"" + src + "=>" + tgt + "\"\n";            
            gmlf += endEdge + "\n";
        }
        
        // print footer (close graph)
        gmlf += "]\n";
        
        return(gmlf);
    }
    
    
    /**
     * Determines the kavosh edge list format string for this graph. First line is number of vertices. All other lines represent one edge.
     * @return the kavosh edge list format string for this graph
     */
    public String toKavoshFormat() {
        String kf = "";
        
        kf += this.numVertices() + "\n";

        for(Integer i = 0; i < this.getSize(); i++) {
            for(Integer j = 0 ; j < this.getSize(); j++) {
                if(this.hasEdge(i, j) && i != j) {
                    kf += (i+1) + " " + (j+1) + "\n";                    
                }            
            }            
        }
        
        return kf;
    }

    
    
}
