/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import datastructures.SimpleGraphInterface;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import proteingraphs.FoldingGraph;

/**
 *
 * @author spirit
 */
public class ConnectedComponents {
    
    protected SimpleGraphInterface g;
    protected List<SimpleGraphInterface> conComps;
    
    public ConnectedComponents(SimpleGraphInterface g) {
        this.g = g;
        conComps = new ArrayList<>();
    }
    
    public void compute() {
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
        queue = new LinkedList<Integer>();

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
    }
}
