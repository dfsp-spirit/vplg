/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package proteingraphs;


//import com.google.gson.Gson;

import datastructures.SparseGraph;
import proteinstructure.SSE;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import javax.imageio.*;
import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.*;
import javax.swing.*;
//import java.awt.*;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import tools.DP;



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
    public ProtGraph(List<SSE> sses) {
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
            List<SSE> tmpSSEList = new ArrayList<SSE>();
            
            // Determine last SSE in parent graph which is part of the FG. We need this because for ADJ and SEQ notations, we need
            //  to add vertices which are NOT part of the CC as well (all vertices between first and last vertex of the CC).
            int lastIndexOfSSEinParentGraphWhichIsPartOfFG = -1;
            int firstIndexOfSSEinParentGraphWhichIsPartOfFG = -1;
            
            /** Whether to include all parent graph vertices in the FGs for ADJ and SEQ notations. If false, only vertices between the first and last FG vertex will be added. */
            Boolean includeAllVerticesInADJandSEQ = false;
            
            if(includeAllVerticesInADJandSEQ) {
                // add all vertices of the parent PG to this ~CC
                firstIndexOfSSEinParentGraphWhichIsPartOfFG = 0;
                lastIndexOfSSEinParentGraphWhichIsPartOfFG = sseList.size() - 1;
            }
            else {
                // add on the vertices between first and last vertex of the CC
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
            }                                   
            
            Integer[] fgVertexIndexInADJ = new Integer[sseList.size()];
            Arrays.fill(fgVertexIndexInADJ, -1);
            boolean inADJandSEQvertices = false;
            boolean vertexAddedThisStep;
            int posInADJ = 0;
            
            for(Integer j = 0; j < sseList.size(); j++) {

                vertexAddedThisStep = false;
                inADJandSEQvertices = (j >= firstIndexOfSSEinParentGraphWhichIsPartOfFG && j <= lastIndexOfSSEinParentGraphWhichIsPartOfFG);
                // If SSE j is marked to be part of connected component i
                if(m[j].equals(i) ) {
                    // ...add it to the list of SSEs for that CC.
                    tmpSSEList.add(sseList.get(j));
                    numInNewGraph[j] = numVerticesAdded;
                    posInParentGraph[numVerticesAdded] = j;  
                    //System.out.println("[PG] CC #" + i + ": Position of vertex " + numVerticesAdded + " in parent graph was " + j + ".");
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
            //System.out.println("[PG] CC #" + i + ": posInParentGraph complete: " + IO.intArrayToString(posInParentGraph));
            ArrayList<Integer> fgVertexIndicesInParentGraph = new ArrayList<Integer>();
            for(int x = 0; x < posInParentGraph.length; x++) {
                if(posInParentGraph[x] >= 0) {
                    fgVertexIndicesInParentGraph.add(posInParentGraph[x]);
                }
            }
            //System.out.println("[PG] CC #" + i + ": vertexIndicesInParentGraph: " + IO.intListToString(fgVertexIndicesInParentGraph));
            fg.setVertexIndicesInParentGraph(fgVertexIndicesInParentGraph);
            
            /* Boolean debug = true;
            if(debug) {
                System.out.println("DDDDD Created new FG from CC #" + i + " of graph, size = " + fg.size + ". Showing positions of vertices in parent graph: " + IO.intArrayListToString(fgVertexIndicesInParentGraph) + ".");                
                if(includeADJandSEQvertices) {
                    System.out.println("DDDDD Including ADJ and SEQ vertices as well. fgVertexIndexInADJ: " + IO.intArrayListToString(fgVertexIndicesInADJandSEQfoldingGraphs) + ".");
                } else {
                    System.out.println("DDDDD Including only RED and KEY vertices. fgVertexIndexInADJ: " + IO.intArrayListToString(fgVertexIndicesInADJandSEQfoldingGraphs) + ".");
                }
            }
            */
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
            fg.setFoldingGraphNumber(i);   // will be set properly for RED/KEY CCs after this computation, see below
            fg.setInfo(this.pdbid, this.chainid, this.chainMolid, this.graphType);            
            conComps.add(fg);
        }
        
        // now set the foldingGraphNumbers (ordered by position of the left-most vertex of the CC in the parent)

        ProtGraphs.setFoldingGraphNumbers(conComps);    // this call also sorts the CCs
        
        // log computation done
        this.connectedComponents = conComps;
        this.connectedComponentsComputed = true;
        
        
    }
    
    
    /**
     * Returns the largest connected component of this graph as a folding graph object (or NULL if this graph has no CCs, i.e., it has no vertices).
     * Computes the CCs if they have not been computed yet.
     * 
     * @return the largest CC as a FoldingGraph (or NULL if this graph has no vertices). If several largest CCs (with identical size) exist, the one closest to the C terminus will be returned.
     */
    public FoldingGraph getLargestConnectedComponent() {
        if(! this.connectedComponentsComputed) {
            this.computeConnectedComponents();
        }
        
        Integer maxSize = 0;
        Integer indexOfLargestCC = 0;
        
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
    
    /**
     * Returns the RED/KEY folding graphs as a list of graphs. Computes them first if this has not yet been done.
     * @return the list of CCs as graphs
     */
    public ArrayList<FoldingGraph> getFoldingGraphs() {
        return(this.getConnectedComponents());
    }
       
    
    /**
     * Creates a .graph file in the notation that is used by the Perl script by Patrick. The script
     * computes the PTGL notations from this format. Note that .graph is the input format for Patrick's PTGL notation Perl script.
     * @return the graph in the PTGL .graph file format
     */
    public String toPTGLGraphFormatPerl() {
        StringBuilder sb = new StringBuilder();
        
        // header
        sb.append("chains: 1\n");
        sb.append("# protein: ").append(this.pdbid).append(this.chainid).append("\n");
        sb.append("# SSEs: ").append(this.getSize()).append("\n");
        
        // vertices
        SSE v;
        for(int i = 0; i < this.size; i++) {
            v = this.getVertex(i);
            sb.append(String.format("%s\t%d\t%d\t%d", v.getPLCCSSELabel(), v.getLength(), v.getStartDsspNum(), v.getEndDsspNum()));
            sb.append("\n");
        }
        
        // edges
        SSE w;
        int indexV; int indexW;
        sb.append("# SSE contacts:\n");
        for(Integer[] e : this.getEdgeList()) {
            indexV = e[0];
            indexW = e[1];
            v = this.getVertex(indexV);
            w = this.getVertex(indexW);
            sb.append(String.format("%d %d %s %s", indexV, indexW, this.getContactTypeStringPTGLGraph(indexV, indexW), ("" + v.getPLCCSSELabel() + w.getPLCCSSELabel())));
            sb.append("\n");
        }
        
        return sb.toString();
    }

    
    /**
     * Tests only.
     * @param args ignored 
     */
    public static void main(String[] args) {
        
        // create test graph
        ArrayList<SSE> sses = new ArrayList<SSE>();
        for(int i = 0; i < 4; i++) {
            SSE s = new SSE(SSE.SSECLASS_HELIX);
            sses.add(s);
        }
        ProtGraph pg1 = new ProtGraph(sses);
        pg1.addContact(0, 1, SpatRel.MIXED);
        pg1.addContact(0, 2, SpatRel.MIXED);
        pg1.addContact(0, 3, SpatRel.MIXED);        
        pg1.addContact(1, 2, SpatRel.MIXED);        
        pg1.addContact(2, 3, SpatRel.MIXED);
        
        ArrayList<Integer> sseIndices = new ArrayList<Integer>();
        for(Integer i = 0; i < 4; i++) { sseIndices.add(i); }
        
        boolean pg1containsCycle = pg1.hasCycleInVertexSet(sseIndices);
        if(pg1containsCycle) {
            System.out.println("Graph 1 contains a cycle.");
        }
        else {
            System.out.println("Graph 1 does NOT contain a cycle.");
        }
        
        boolean pg1isConnected = pg1.isConnected();
        if(pg1isConnected) {
            System.out.println("Graph 1 is connected.");
        }
        else {
            System.out.println("Graph 1 is NOT connected.");
        }
        
        ProtGraph pg2 = new ProtGraph(sses);
        pg2.addContact(0, 1, SpatRel.MIXED);
        pg2.addContact(1, 2, SpatRel.MIXED);
        pg2.addContact(2, 3, SpatRel.MIXED);
        //pg2.addContact(3, 0, SpatRel.MIXED);
        
        boolean pg2containsCycle = pg2.hasCycleInVertexSet(sseIndices);
        if(pg2containsCycle) {
            System.out.println("Graph 2 contains a cycle.");
        }
        else {
            System.out.println("Graph 2 does NOT contain a cycle.");
        }
        
        boolean pg2isConnected = pg2.isConnected();
        if(pg2isConnected) {
            System.out.println("Graph 2 is connected.");
        }
        else {
            System.out.println("Graph 2 is NOT connected.");
        }
        
        ProtGraph pg3 = ProtGraphs.generateRandomPG(5, "albe", "A", "rand");
        String pf = pg3.toPTGLGraphFormatPerl();
        System.out.println("Graph format perl:\n" + pf);
        
       
    }
    
    /**
     * Constructs a SparseGraph from the data in this graph.
     * @return the SparseGraph
     */
    @Override
    public SparseGraph<String, String> toSparseGraph() {
        SparseGraph<String, String> g = new SparseGraph<>();
        
        // add verts
        for(int i = 0; i < this.sseList.size(); i++) {
            g.addVertex(this.getFGNotationOfVertex(i));
        }
        
        // add edges
        Integer[] e;
        for(int i = 0; i < this.getEdgeList().size(); i++) {
            e = this.getEdgeList().get(i);
            g.addEdge(e[0], e[1], SpatRel.getString(this.matrix[e[0]][e[1]]));
        }
        
        if(this.sseList.size() != g.getNumVertices()) {
            DP.getInstance().e("FoldingGraph", "toSparseGraph: vertex counts of graphs do not match.");
        }
        if(this.getEdgeList().size() != g.getNumEdges()) {
            DP.getInstance().e("FoldingGraph", "toSparseGraph: edge counts of graphs do not match.");
        }
        return g;        
    }
    
    
}

    


    
    
    
    
    

