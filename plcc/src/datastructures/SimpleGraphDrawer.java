/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author ts
 */
public class SimpleGraphDrawer {
    
    SimpleGraphInterface g;
    int numVerts;
    int[][] edgeShifts;
    
    public static final String edgeStart = "*";
    public static final String edgeEnd = "+";
    public static final String edgeElong = "|";
    
    public SimpleGraphDrawer(SimpleGraphInterface g) {
        this.g = g;
        numVerts = g.getSize();
        edgeShifts = new int[numVerts][numVerts];
        for(int i = 0; i < numVerts; i++) {
            Arrays.fill(edgeShifts[i], -1);
        }
    }
    
    public String getGraphConsoleDrawing() {
        StringBuilder sb = new StringBuilder();
                        
        int j;
        List<DrawEdge> edgesHere;
        String[] drawChars;
        for(int i = 0; i < numVerts; i++) {
            j = i + 1;
            //edgesHere = drawnEdgesBetweenVerts(i, j);
            edgesHere = drawnEdgesAtVert(i);
            assignShifts(edgesHere);
            
            System.out.print("At vert #" + i + ", edges:");
            //for(DrawEdge e : edgesHere) {
            //    System.out.print(e);
            //}
            
            
            int maxShift = maxShiftOf(edgesHere);
            drawChars = new String[maxShift + 1];
            sb.append("[").append(i).append("]");
            Arrays.fill(drawChars, " ");
            for(DrawEdge e : edgesHere) {
                System.out.print(e + "=>" + getShift(e) + "");
                if(e.source == i) {
                    // edge starts here
                    System.out.print("s ");
                    drawChars[getShift(e)] = edgeStart;
                }
                else if(e.target == i) {
                    // edge ends here
                    System.out.print("e ");
                    drawChars[getShift(e)] = edgeEnd;
                }
                else {
                    // edge continues
                    System.out.print("c ");
                    drawChars[getShift(e)] = edgeElong;
                }
            }
            System.out.print("\n");
            
            for(String s : drawChars) {
                sb.append(s);
            }
            sb.append("\n");
        }
                
        return sb.toString();
    }
    
    private int maxShiftOf(List<DrawEdge> l) {
        int max = -1;
        for(DrawEdge e : l) {
            if(getShift(e) > max) {
                max = getShift(e);
            }
        }
        return max;
    }
    
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
        
    private boolean hasShiftAssigned(DrawEdge e) {
        return(this.edgeShifts[e.source][e.target] >= 0);
    }
    
    private Integer getShift(DrawEdge e) {
        return(this.edgeShifts[e.source][e.target]);
    }
    
    private Set<Integer> getUsedShifts(List<DrawEdge> l) {
        Set<Integer> used = new HashSet<Integer>();
        for(DrawEdge e : l) {
            if(hasShiftAssigned(e)) {
                used.add(getShift(e));
            }
        }
        return used;
    }
    
    private void setShift(DrawEdge e, int s) {
        this.edgeShifts[e.source][e.target] = s;
    }
    
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
    
    private List<DrawEdge> drawnEdgesBetweenVerts(int i, int j) {
        
        List<DrawEdge> edges = new ArrayList<DrawEdge>();
        
        for(int k = 0; k < numVerts; k++) {
            for(int l = k + 1; l < numVerts; l++) {
                if(g.containsEdge(k, l)) {
                    if(k < i && l < i) {
                        // edge ends before the area we are interested in
                    }
                    else if(k > j && l > j) {
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
       
    
    private class DrawEdge {
        int source;
        int target;
        
        public Integer getLength() {
            return target - source;
        }
        
        @Override
        public String toString() {
            return "(" + source + "," + target + ")";
        }
        
        public DrawEdge(int i, int j) {
            this.source = i;
            this.target = j;
            
            if(source > target) {
                int tmp = source;
                source = target;
                target = tmp;
            }
        }
        
        public int[] toEdge() {
            return new int[] {source, target, this.getLength()};
        }
        
       
    }
    
    
    
    
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
        
        
        SimpleGraphDrawer sgd = new SimpleGraphDrawer(pg1);
        System.out.println("Graph:\n" + sgd.getGraphConsoleDrawing());
        
    }
    
    class DrawEdgeComparator implements Comparator<DrawEdge> {

    @Override public int compare(DrawEdge o1, DrawEdge o2) {
        return o1.getLength().compareTo(o2.getLength());
    }
}
    
}
