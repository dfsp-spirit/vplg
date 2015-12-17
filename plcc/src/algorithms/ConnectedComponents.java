/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author Tim Schäfer
 */
package algorithms;

import datastructures.SimpleGraphInterface;
import datastructures.SparseGraph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Computes the connected components of a graph given as a SimpleGraphInterface. Lazy, only computes stuff when asked for results.
 * @author spirit
 */
public class ConnectedComponents {
    
    protected SimpleGraphInterface g;
    protected List<SimpleGraphInterface> conComps;
    protected boolean alreadyComputed;
    
    public ConnectedComponents(SimpleGraphInterface g) {
        this.g = g;
        conComps = new ArrayList<>();
        alreadyComputed = false;
    }
    
    public List<SimpleGraphInterface> get() {
        if( ! this.alreadyComputed) {
            this.compute();
        }
        return this.conComps;
    }
    
    /**
     * Returns (and computes if needed) the largest CC.
     * @return the largest CC. If several largest of equal size exist, the last one is returned. Returns null if the graph has no verts (and thus the largest CC has size 0).
     */
    public SimpleGraphInterface getLargest() {
        if( ! this.alreadyComputed) {
            this.compute();
        }
        
        int max = -1;
        int currentLargestIndex = -1;
        for(int i = 0; i < conComps.size(); i++) {
            if(this.conComps.get(i).getSize() >= max) {
                currentLargestIndex = i;
            }
        }
        
        if(currentLargestIndex == -1) {
            return null;
        }
        return this.conComps.get(currentLargestIndex);
    }
    
    
    /**
     * Runs the actual computation of CC. Called lazily by the other methods if needed.
     */
    private void compute() {
    // If the list of SSEs is empty, there are no connected components
        if(g.getSize() < 1) {
            return;
        }

        Integer [] color = new Integer [g.getSize()];
        Integer [] dist = new Integer [g.getSize()];
        Integer [] predec = new Integer [g.getSize()];
        Integer [] m = new Integer [g.getSize()];
        Integer v = null;
        LinkedList<Integer> queue;

        Integer conCompNum;

        // Init stuff
        for(Integer i = 0; i < g.getSize(); i++) {
            color [i] = 1;  // 1 = white, 2 = gray, 3 = black. White vertices have not been handled yet, gray ones
                            //  are currently being handled and black ones have already been handled.
            dist[i] = Integer.MAX_VALUE;    // distance of vertex i to the root of the connected component
            predec[i] = -1;       // the predecessor of vertex i
            m[i] = 0;               // the connected component vertex i is part of
        }

        // The number of the 1st connected component. Do NOT use 0!
        conCompNum = 1;     // current connected component number
        queue = new LinkedList<>();

        // Start breadth-first search in every vertex
        for(Integer i = 0; i < g.getSize(); i++) {

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
                    for(Integer w : g.neighborsOf(v)) {
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
        
        
        // we have that vertices marked with their CC, now construct the subgraphs/connected components from them
        SparseGraph<Integer, String> sg;
        
        
        // Iterate through all connected components (CCs)
        Integer [] numInNewGraph;
        Integer [] posInParentGraph;
        Integer numVerticesAdded;
        Integer numEdgesAdded;
        SimpleGraphInterface fg;
        for(Integer i = 0; i <= conCompNum; i++) {
            numVerticesAdded = 0;
            numEdgesAdded = 0;
             numInNewGraph = new Integer [g.getSize()];
             posInParentGraph = new Integer [g.getSize()];  // This would only need as size the number of SSEs in the FG (instead of the PG), but we do not know it yet. This gets transformed into an ArrayList of proper size later.
             // init the arrays
             for(Integer j = 0; j < g.getSize(); j++) {
                 numInNewGraph[j] = -1;
                 posInParentGraph[j] = -1;
             }


            // For each CC, create a graph with all SSEs that are marked with this connected component number.
            // We need to get all SSEs first because we need to pass the list to the constructor:
            List<Integer> tmpSSEList = new ArrayList<>();
            
            // Determine last SSE in parent graph which is part of the FG. We need this because for ADJ and SEQ notations, we need
            //  to add vertices which are NOT part of the CC as well (all vertices between first and last vertex of the CC).
            int lastIndexOfSSEinParentGraphWhichIsPartOfFG = -1;
            int firstIndexOfSSEinParentGraphWhichIsPartOfFG = -1;
            
            /** Whether to include all parent graph vertices in the FGs for ADJ and SEQ notations. If false, only vertices between the first and last FG vertex will be added. */
            Boolean includeAllVerticesInADJandSEQ = false;
            
            if(includeAllVerticesInADJandSEQ) {
                // add all vertices of the parent PG to this ~CC
                firstIndexOfSSEinParentGraphWhichIsPartOfFG = 0;
                lastIndexOfSSEinParentGraphWhichIsPartOfFG = g.getSize() - 1;
            }
            else {
                // add on the vertices between first and last vertex of the CC
                for(Integer j = 0; j < g.getSize(); j++) {
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
            
            Integer[] fgVertexIndexInADJ = new Integer[g.getSize()];
            Arrays.fill(fgVertexIndexInADJ, -1);
            boolean inADJandSEQvertices = false;
            boolean vertexAddedThisStep;
            int posInADJ = 0;
            
            for(Integer j = 0; j < g.getSize(); j++) {

                vertexAddedThisStep = false;
                inADJandSEQvertices = (j >= firstIndexOfSSEinParentGraphWhichIsPartOfFG && j <= lastIndexOfSSEinParentGraphWhichIsPartOfFG);
                // If SSE j is marked to be part of connected component i
                if(m[j].equals(i) ) {
                    // ...add it to the list of SSEs for that CC.
                    tmpSSEList.add(j);
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
            sg = new SparseGraph<Integer, String>(tmpSSEList);
            
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
            
                                                 
            
            // Now add the contacts/edges between the vertices by iterating through the contact matrix of this graph and
            //  translating the indices to the new graph.
            for(Integer k = 0; k < g.getSize(); k++) {
                for(Integer l = 0; l < g.getSize(); l++) {
                    
                    // If there is such a contact in this graph..
                    if(g.containsEdge(k, l)) {

                        // ...we may need to add the contact to the new graph. Only if both vertices of that edge
                        //  are part of the new graph, of course.
                        if(numInNewGraph[k] >= 0 && numInNewGraph[l] >= 0) {
                            sg.addEdge(numInNewGraph[k], numInNewGraph[l], k+" "+l);
                            numEdgesAdded++;
                        }
                    }
                }
            }

            conComps.add(sg);
        }
    }
}
