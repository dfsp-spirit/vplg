/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package plcc;


//import com.google.gson.Gson;
import java.util.*;
import java.io.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.swing.*;
//import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.print.*;
import java.io.*;
import java.util.PriorityQueue;
import java.util.List;
import java.util.Collections;


import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;



// Apache Batik SVG library
//import org.apache.batik.svggen.SVGGraphics2D;
//import org.apache.batik.dom.GenericDOMImplementation;
//import org.w3c.dom.Document;
//import org.w3c.dom.DOMImplementation;

/**
 * A protein graph. Contains information on the SSEs and contacts between them. Represents a specific graph type, e.g., an albe graph. This
 * means it may only contain a subset of the SSEs of the chain.
 * @author ts
 */
public class ProtGraph extends SSEGraph implements java.io.Serializable  {
                               
        
    public void declareProteinGraph() { this.isProteinGraph = true; this.parent = null; }
               
    

    /**
     * Constructor. Requires a list of SSEs that will be represented by the vertices of the graph.
     * @param sses a list of SSEs which make up this folding graph. The contacts have to be added later (or there will be none).
     */
    ProtGraph(ArrayList<SSE> sses) {
        super(sses);
        this.isProteinGraph = true;
    }

    
    

    

    
    
    
    
    /**
     * Determines all connected components of this graph and writes them into the connectedComponents ArrayList.
     * See Cormen et al. 2001, Introduction to Algorithms.
     */
    public void computeConnectedComponents() {
        ArrayList<FoldingGraph> conComps = new ArrayList<FoldingGraph>();

        // If the list of SSEs is empty, there are no connected components
        if(this.size < 1) {
            this.connectedComponents = conComps;
            return;
        }

        Integer [] color = new Integer [this.size];
        Integer [] dist = new Integer [this.size];
        Integer [] predec = new Integer [this.size];
        Integer [] m = new Integer [this.size];
        Integer v = null;
        LinkedList<Integer> queue;

        Integer conCompNum;

        // Init stuff
        for(Integer i = 0; i < sseList.size(); i++) {
            color [i] = 1;  // 1 = white, 2 = gray, 3 = black. White vertices have not been handled yet, gray ones
                            //  are currently being handled and black ones have already been handled.
            dist[i] = Integer.MAX_VALUE;    // distance of vertex i to the root of the connected component
            predec[i] = -1;       // the predecessor of vertex i
            m[i] = 0;               // the connected component vertex i is part of
        }

        // The number of the 1st connected component. Do NOT use 0!
        conCompNum = 1;     // current connected component number
        queue = new LinkedList<Integer>();

        // Start breadth-first search in every vertex
        for(Integer i = 0; i < sseList.size(); i++) {

            // If vertex i is not yet part of any connected component...
            if(! (m[i] > 0)) {

                // Mark the current vertex with the current CC number. This is not mentioned in Cormen et al. 2001 but
                //  not doing it will obviously break stuff.
                m[i] = conCompNum;

                color[i] = 2;   // i is currently being handled
                dist[i] = 0;    // i is the root of this connected component
                queue.addFirst(i);
            
                while( ! queue.isEmpty()) {
                    v = queue.peekFirst();      // v is the 1st element of the FIFO

                    // For all neighbors w of v
                    for(Integer w : neighborsOf(v)) {
                        // If w has not been treated yet
                        if(color[w].equals(1)) {
                            color[w] = 2;               // ...now it is being treated
                            dist[w] = dist[v] + 1;      // w is a successor of v (a neighbor of v that is treated after v)
                            predec[w] = v;              // so v is a predecessor of w
                            m[w] = conCompNum;          // w is part of the current connected component because it has been reached from its root v
                            queue.addLast(w);           // add the neighbors of v to the queue so all their neighbors are checked, too.
                                                        //   (Note that adding them to the end makes this find all vertices in distance n before finding
                                                        //    any vertex in distance n + 1 to i.)

                        }                        
                    }
                    queue.removeFirst();    // This vertex has just been handled,
                    color[v] = 3;           //  so mark it as handled.
                }
                // The queue is empty, so all vertices reachable from i have been checked. Start the next
                //  connected component.
                conCompNum++;
            }
        }

        // All vertices are marked with their comCompNum, create the graphs
        /*
        System.out.print("++Printing array info for all " + this.size + " vertices:\n++");
        for(Integer i = 0; i < this.size; i++) {
            System.out.printf("%2d", i);
        }
        System.out.print("\n++CC marking\n++");
        for(Integer i = 0; i < this.size; i++) {
            System.out.print(" " + m[i]);
        }
        System.out.print("\n++color\n++");
        for(Integer i = 0; i < this.size; i++) {
            System.out.print(" " + color[i]);
        }
        System.out.print("\n++dist\n++");
        for(Integer i = 0; i < this.size; i++) {
            System.out.print(" " + dist[i]);
        }
        System.out.print("\n++predecessor\n++");
        for(Integer i = 0; i < this.size; i++) {
            System.out.print(" " + predec[i]);
        }
        System.out.print("\n++Printing of CC markings done.\n");
         */

        // Iterate through all connected components (CCs)
        Integer [] numInNewGraph;
        Integer [] posInParentGraph;
        Integer numVerticesAdded;
        Integer numEdgesAdded;
        FoldingGraph fg;
        for(Integer i = 0; i <= conCompNum; i++) {
            numVerticesAdded = 0;
            numEdgesAdded = 0;
             numInNewGraph = new Integer [sseList.size()];
             posInParentGraph = new Integer [sseList.size()];  // This would only need as size the number of SSEs in the FG (instead of the PG), but we do not know it yet. This gets transformed into an ArrayList of proper size later.
             // init the arrays
             for(Integer j = 0; j < sseList.size(); j++) {
                 numInNewGraph[j] = -1;
                 posInParentGraph[j] = -1;
             }


            // For each CC, create a graph with all SSEs that are marked with this connected component number.
            // We need to get all SSEs first because we need to pass the list to the constructor:
            ArrayList<SSE> tmpSSEList = new ArrayList<SSE>();
            
            // Determine last SSE in parent graph which is part of the FG. We need this because for ADJ and SEQ notations, we need
            //  to add vertices which are NOT part of the CC as well (all vertices between first and last vertex of the CC).
            int lastIndexOfSSEinParentGraphWhichIsPartOfFG = -1;
            int firstIndexOfSSEinParentGraphWhichIsPartOfFG = -1;
            
            
            for(Integer j = 0; j < sseList.size(); j++) {
                // If SSE j is marked to be part of connected component i
                if(m[j].equals(i)) {
                    if(firstIndexOfSSEinParentGraphWhichIsPartOfFG == -1) {
                        // first index not set yet
                        firstIndexOfSSEinParentGraphWhichIsPartOfFG = j;
                    }
                    lastIndexOfSSEinParentGraphWhichIsPartOfFG = j;
                }
            }
            
            
            /** Whether we compute an ADJ or SEQ folding graph, and thus need to include vertices which are NOT part of the connected component. */
            boolean includeADJandSEQvertices = false;
            
            Integer[] fgVertexIndexInADJ = new Integer[sseList.size()];
            Arrays.fill(fgVertexIndexInADJ, -1);
            boolean inADJandSEQvertices = false;
            boolean vertexAddedThisStep;
            int posInADJ = 0;
            
            for(Integer j = 0; j < sseList.size(); j++) {

                vertexAddedThisStep = false;
                inADJandSEQvertices = (j >= firstIndexOfSSEinParentGraphWhichIsPartOfFG && j <= lastIndexOfSSEinParentGraphWhichIsPartOfFG);
                // If SSE j is marked to be part of connected component i
                if(m[j].equals(i) || (includeADJandSEQvertices && inADJandSEQvertices)) {
                    // ...add it to the list of SSEs for that CC.
                    tmpSSEList.add(sseList.get(j));
                    numInNewGraph[j] = numVerticesAdded;
                    posInParentGraph[numVerticesAdded] = j;                                                            
                    numVerticesAdded++;
                    vertexAddedThisStep = true;
                }
                
                if(inADJandSEQvertices) {
                    if(vertexAddedThisStep) {
                        fgVertexIndexInADJ[numVerticesAdded - 1] = posInADJ;
                    }                    
                    posInADJ++;
                }
            }
        
            
            // Ok, we got the SSEs. Now create the graph.
            if(tmpSSEList.size() < 1) { continue; }
            fg = new FoldingGraph(tmpSSEList);
            
            // compute proper list of indices in ADJ and SEQ folding graphs and set it
            ArrayList<Integer> fgVertexIndicesInADJandSEQfoldingGraphs = new ArrayList<Integer>();
            for(int x = 0; x < fgVertexIndexInADJ.length; x++) {
                if(fgVertexIndexInADJ[x] >= 0) {
                    fgVertexIndicesInADJandSEQfoldingGraphs.add(fgVertexIndexInADJ[x]);
                }
            }
            
            // compute proper list of indices in old graph and set it
            ArrayList<Integer> fgVertexIndicesInParentGraph = new ArrayList<Integer>();
            for(int x = 0; x < posInParentGraph.length; x++) {
                if(posInParentGraph[x] >= 0) {
                    fgVertexIndicesInParentGraph.add(posInParentGraph[x]);
                }
            }
            fg.setVertexIndicesInParentGraph(fgVertexIndicesInParentGraph);
            
            Boolean debug = true;
            if(debug) {
                System.out.println("DDDDD Created new FG from CC #" + i + " of graph, size = " + fg.size + ". Showing positions of vertices in parent graph: " + IO.intArrayListToString(fgVertexIndicesInParentGraph) + ".");                
                if(includeADJandSEQvertices) {
                    System.out.println("DDDDD Including ADJ and SEQ vertices as well. fgVertexIndexInADJ: " + IO.intArrayListToString(fgVertexIndicesInADJandSEQfoldingGraphs) + ".");
                } else {
                    System.out.println("DDDDD Including only RED and KEY vertices. fgVertexIndexInADJ: " + IO.intArrayListToString(fgVertexIndicesInADJandSEQfoldingGraphs) + ".");
                }
            }
            fg.setVertexIndicesInADJandSEQfoldingGraphs(fgVertexIndicesInADJandSEQfoldingGraphs);
                                                 
            
            // Now add the contacts/edges between the vertices by iterating through the contact matrix of this graph and
            //  translating the indices to the new graph.
            for(Integer k = 0; k < this.size; k++) {
                for(Integer l = 0; l < this.size; l++) {
                    
                    // If there is such a contact in this graph..
                    if(this.sseContactExistsPos(k, l)) {

                        // ...we may need to add the contact to the new graph. Only if both vertices of that edge
                        //  are part of the new graph, of course.
                        if(numInNewGraph[k] >= 0 && numInNewGraph[l] >= 0) {
                            fg.addContact(numInNewGraph[k], numInNewGraph[l], matrix[k][l]);
                            numEdgesAdded++;
                        }
                    }
                }
            }

            // All the contacts have been added, the graph is complete. Let's add it to the list.
            //System.out.println("  Found new connected component consisting of " + numVerticesAdded + "/" + tpg.numVertices() + " vertices and " + numEdgesAdded + "/" + tpg.numEdges() + " edges:");
            //tpg.print();
            fg.declareFoldingGraphOf(this); // Each connected component of a protein graph is a folding graph
            fg.setFoldingGraphNumber(i);
            fg.setInfo(this.pdbid, this.chainid, this.graphType);            
            conComps.add(fg);
        }
        
        this.connectedComponents = conComps;
        this.connectedComponentsComputed = true;
    }
    
    
    /**
     * Returns the largest connected component of this graph as a folding graph object (or NULL if this graph has no CCs, i.e., it has no vertices).
     * Computes the CCs if they have not been computed yet.
     * 
     * @return the CC as a FoldingGraph (or NULL if this graph has no vertices)
     */
    public FoldingGraph getLargestConnectedComponent() {
        if(! this.connectedComponentsComputed) {
            this.computeConnectedComponents();
        }
        
        Integer maxSize = 0;
        Integer indexOfLargestCC = -1;
        
        FoldingGraph fg;
        for(Integer i = 0;  i < this.connectedComponents.size(); i++) {
            fg = this.connectedComponents.get(i);
            if(fg.getSize() >= maxSize) {
                maxSize = fg.getSize();
                indexOfLargestCC = i;
            }   
        }
        
        if(indexOfLargestCC >= 0) {
            return(this.connectedComponents.get(indexOfLargestCC));
        } else {
            return(null);
        }
    }
    
    
    
    /**
     * Returns the number of connected components this graph consists of, computing them first if necessary.
     * @return the number of CCs
     */
    public Integer numConnectedComponents() {
        
        if( ! this.connectedComponentsComputed) {
            this.computeConnectedComponents();
        }
        
        return(this.connectedComponents.size());
    }
    
    
    /**
     * Determines whether this graph is connected, i.e. consists of a single connected component.
     * @return true if it is connected, false otherwise
     */
    @Override public Boolean isConnected() {
        return(this.numConnectedComponents().equals(1));
    }
    
    
    /**
     * Returns the connected components of this graph as a list of graphs. Computes them first if this has not yet been done.
     * @return the list of CCs as graphs
     */
    public ArrayList<FoldingGraph> getConnectedComponents() {
        if( ! this.connectedComponentsComputed) {
            this.computeConnectedComponents();            
        }
        
        return(this.connectedComponents);
    }
    
    
    
    
}

    


    
    
    
    
    

