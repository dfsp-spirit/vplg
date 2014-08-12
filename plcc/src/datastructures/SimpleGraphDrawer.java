/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package datastructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import plcc.ProtGraph;
import plcc.SSE;
import plcc.SpatRel;

/**
 * A class to draw a graph on the console (text-based). Will draw the vertices in a column on the left screen side,
 * the first one at the top and the last one at the bottom. The edges will be drawn to the right.
 * @author ts
 */
public class SimpleGraphDrawer {
    
    SimpleGraphInterface g;
    int numVerts;
    int[][] edgeShifts;
    
    public static final String edgeStart = "*";
    //public static final String edgeStart = Character.toString ((char) 191);
    public static final String edgeEnd = "+";
    //public static final String edgeEnd = Character.toString ((char) 217);
    public static final String edgeElong = "|";
    
    /**
     * Constructor, sets the graph to draw.
     * @param g the input graph to draw
     */
    public SimpleGraphDrawer(SimpleGraphInterface g) {
        this.g = g;
        numVerts = g.getSize();
        edgeShifts = new int[numVerts][numVerts];
        for(int i = 0; i < numVerts; i++) {
            Arrays.fill(edgeShifts[i], -1);
        }
    }
    
    
    /**
     * The one worker function to call, creates a console-style drawing of a graph (ASCII-art style).
     * @return the graph drawing as a (multi-line) string
     */
    public String getGraphConsoleDrawing() {
        StringBuilder sb = new StringBuilder();
                        
        int j;
        List<DrawEdge> edgesHere;
        String[] drawChars;
        int maxShift;
        for(int i = 0; i < numVerts; i++) {
            j = i + 1;
            //edgesHere = drawnEdgesBetweenVerts(i, j);
            edgesHere = drawnEdgesAtVert(i);
            assignShifts(edgesHere);
            
            System.out.print("At vertex #" + i + ", edges here are : ");
            //for(DrawEdge e : edgesHere) {
            //    System.out.print(e);
            //}
            
            
            maxShift = maxShiftOf(edgesHere);
            drawChars = new String[(maxShift + 1) * 2];
            
            // append vertex marker
            sb.append("").append(String.format("%4d", i)).append(" ");
            
            Arrays.fill(drawChars, " ");
            for(DrawEdge e : edgesHere) {
                System.out.print(e + "=>" + getShift(e) + "");
                if(e.source == i) {
                    // edge starts here
                    System.out.print("s ");
                    drawChars[getShift(e) * 2] = edgeStart;
                }
                else if(e.target == i) {
                    // edge ends here
                    System.out.print("e ");
                    drawChars[getShift(e) * 2] = edgeEnd;
                }
                else {
                    // edge continues
                    System.out.print("c ");
                    drawChars[getShift(e) * 2] = edgeElong;
                }
            }
            System.out.print("\n");
            
            for(String s : drawChars) {
                sb.append(s);
            }
            sb.append("\n");
            
            // draw the line between the vertices
            if(i < numVerts -1) {
                System.out.print("Between verts #" + i + " and " + j + ", edges: ");
                edgesHere = drawnEdgesBetweenVerts(i, j);
                assignShifts(edgesHere);
                maxShift = maxShiftOf(edgesHere);
                drawChars = new String[(maxShift + 1) * 2];
                sb.append("     ");
                Arrays.fill(drawChars, " ");
                for(DrawEdge e : edgesHere) {
                    System.out.print(e + "=>" + getShift(e) + "");
                    System.out.print("c "); // all edges continue here
                    drawChars[getShift(e) * 2] = edgeElong;
                }
                System.out.print("\n");
                
                for(String s : drawChars) {
                    sb.append(s);
                }
                sb.append("\n");
            }
        }
                
        return sb.toString();
    }

    /**
     * Determines the largest shift in use in the list.
     * @param l the edge list
     * @return the largest shift of the edges in l
     */
    private int maxShiftOf(List<DrawEdge> l) {
        int max = -1;
        for(DrawEdge e : l) {
            if(getShift(e) > max) {
                max = getShift(e);
            }
        }
        return max;
    }
    
    
    /**
     * Assigned the shifts to all edges in the list. It does this by sorting them by draw length, checking which ones already
     * have a shift (because they started further up) and assigns the free shifts to the rest.
     * @param l the edge list
     */
    public void assignShifts(List<DrawEdge> l) {
        Collections.sort(l, new DrawEdgeComparator());
        Set<Integer> usedShifts = getUsedShifts(l);
        
        int curShift = 0;
        
        for(DrawEdge e : l) {
            if( ! hasShiftAssigned(e)) {
                while(usedShifts.contains(curShift)) {
                    curShift++;
                }
                setShift(e, curShift);
                usedShifts.add(curShift);
            }
        }        
    }

    /**
     * Determines whether as edge already has a shift assigned. Important because once an edge has a shift,
     * we must never change it (it would destroy the drawing).
     * @param e the edge
     * @return whether it has a shift assigned already
     */
    private boolean hasShiftAssigned(DrawEdge e) {
        return(this.edgeShifts[e.source][e.target] >= 0);
    }
    
    
    /**
     * Getter for edge shift. The shift is how far to the right in a line the edge is drawn.
     * @param e the edge
     * @return the shift. The shift is how far to the right in a line the edge is drawn.
     */
    private Integer getShift(DrawEdge e) {
        return(this.edgeShifts[e.source][e.target]);
    }
    
    
    /**
     * Returns the shifts of all edges in the list.
     * @param l the edge list
     * @return a list of shifts
     */
    private Set<Integer> getUsedShifts(List<DrawEdge> l) {
        Set<Integer> used = new HashSet<Integer>();
        for(DrawEdge e : l) {
            if(hasShiftAssigned(e)) {
                used.add(getShift(e));
            }
        }
        return used;
    }
    
    /**
     * Simple setter to store edge shift. The shift is how far to the right in a line the edge is drawn.
     * @param e the edge
     * @param s the shift. The shift is how far to the right in a line the edge is drawn.
     */
    private void setShift(DrawEdge e, int s) {
        this.edgeShifts[e.source][e.target] = s;
    }
    
    /**
     * Determines all edges which are (partly) drawn at vertex i.
     * @param i the vertex, by index
     * @return all edges that appear at the vertex in the drawing
     */
    private List<DrawEdge> drawnEdgesAtVert(int i) {
        
        List<DrawEdge> edges = new ArrayList<DrawEdge>();
        
        for(int k = 0; k < numVerts; k++) {
            for(int l = k + 1; l < numVerts; l++) {
                if(g.containsEdge(k, l)) {
                    if(k < i && l < i) {
                        // edge ends before the area we are interested in
                    }
                    else if(k > i && l > i) {
                        // edge starts after the area we are interested in
                    }
                    else {
                        edges.add(new DrawEdge(k, l));
                    }
                }
            }            
        }
        
        return edges;
    }
    
    
    /**
     * Determines all edges which are (partly) drawn between the vertices i and j.
     * @param i the first vertex, by index
     * @param j the second vertex, by index
     * @return all edges that appear between these verts in the drawing
     */
    private List<DrawEdge> drawnEdgesBetweenVerts(int i, int j) {
        
        List<DrawEdge> edges = new ArrayList<DrawEdge>();
        
        for(int k = 0; k < numVerts; k++) {
            for(int l = k + 1; l < numVerts; l++) {
                if(g.containsEdge(k, l)) {
                    if(k <= i && l <= i) {
                        // edge ends before the area we are interested in
                    }
                    else if(k >= j && l >= j) {
                        // edge starts after the area we are interested in
                    }
                    else {
                        edges.add(new DrawEdge(k, l));
                    }
                }
            }            
        }
        
        return edges;
    }
       
    /**
     * An edge that has a draw length assigned. The draw length is the length of segments that are drawn for it. 
     * An edge from vertex at index 2 to vertex at index 5 has a draw length of 5 - 2 = 3.
     */
    private class DrawEdge {
        int source;
        int target;

        /**
         * Determines the draw length.
         * @return the draw length. An edge from vertex at index 2 to vertex at index 5 has a draw length of 5 - 2 = 3.
         */
        public Integer getLength() {
            return target - source;
        }
        
        @Override
        public String toString() {
            return "(" + source + "," + target + ")";
        }
        
        
        /**
         * Constructor for an edge from vertex at index i to j.
         * @param i the first vertex index
         * @param j the second vertex index
         */
        public DrawEdge(int i, int j) {
            this.source = i;
            this.target = j;
            
            if(source > target) {
                int tmp = source;
                source = target;
                target = tmp;
            }
        }
        
        
        /**
         * Converts the edge to an int[] of length 3.
         * @return an int[] of length 3, filled with source, target and draw length
         */
        public int[] toEdge() {
            return new int[] {source, target, this.getLength()};
        }
        
       
    }
    
    
    
    /**
     * Test main only
     * @param args ignored
     */
    public static void main(String [] args) {
        
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
        
        // create a second test graph
        ProtGraph pg2 = new ProtGraph(sses);
        pg2.addContact(0, 1, SpatRel.MIXED);
        pg2.addContact(1, 2, SpatRel.MIXED);
        pg2.addContact(2, 3, SpatRel.MIXED);
        pg2.addContact(3, 0, SpatRel.MIXED);
        
        SimpleGraphDrawer sgd1 = new SimpleGraphDrawer(pg1);
        System.out.println("Graph 1:\n" + sgd1.getGraphConsoleDrawing());
        
        SimpleGraphDrawer sgd2 = new SimpleGraphDrawer(pg2);
        System.out.println("Graph 2:\n" + sgd2.getGraphConsoleDrawing());
        
    }
    
    /**
     * A comparator for sorting DrawEdges based on their length.
     */
    class DrawEdgeComparator implements Comparator<DrawEdge> {

        /**
         * Compares edges based on their length
         * @param o1 DrawEdge 1
         * @param o2 DrawEdge 2
         * @return the comparison result
         */
        @Override public int compare(DrawEdge o1, DrawEdge o2) {
            return o1.getLength().compareTo(o2.getLength());
        }
}
    
}
