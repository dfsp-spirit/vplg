/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package linnottograph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.IO;
import java.util.Arrays;
import java.util.Collections;
import proteingraphs.SSEGraph;
import proteinstructure.SSE;
import tools.DP;

/**
 * Parser for ADJ linnot strings. Note that this parses a linnot string,
 * but constructs a PG (not an FG) from it. You can use the PG function to compute FGs in order to get
 * an FG from it (there may be multiple FGs, but the other ones will be isolated vertices. If the linnot one is an isolated vertex, there will obviously be only one FG in the list.).
 * @author spirit
 */
public class LinnotParserADJ extends LinnotParserRED implements ILinnotParser, ILinnotParserExt {
    
    private List<String> vertexTypesNtoC; 
    private Map<Integer, String> vertexTypesMapNtoC;
    private List<Integer[]> outGraphEdges;
    
    private List<String> resultVertices; 
    private List<Integer[]> resultEdges;
    
    public LinnotParserADJ(String linnot, String graphType) {
        super(linnot, graphType);
        this.vertexTypesNtoC = this.getVertexTypesNtoC();
        vertexTypesMapNtoC = getVertexTypesMapNtoC();
        this.outGraphEdges = this.getOutGraphEdges();
        
        this.resultVertices = null;
        this.resultEdges = null;
        this.considerNonCCVertices(); // fills in this.resultVertices and this.resultEdges
    }
            
     
    
    /**
     * Returns an array which tells you whether the vertex at the respective position in the N to C list has been inserted afterwards (i.e., whether it does NOT originate from the folding graph, but from the parent PG).
     * @return an array of the length of all vertices described by the ADJ linnot, including the vertices which are not part of this CC. A 0 at a position means the vertex belongs to this CC, a 1 means it was inserted based on the relative distances between the others.
     */
    protected Integer[] getVertexInsertMapNtoC() {        
        List<Integer> vPath = this.getVisitPath();
        System.out.println("vPath: " + IO.intListToString(vPath));
        List<Integer> vPathNtoC = getNtoCPositionsOfVisitPath();
        System.out.println("vPathNtoC: " + IO.intListToString(vPathNtoC));
        List<Integer> relDistsVisitPath = getRelDistList();
        System.out.println("relDistsvPath: " + IO.intListToString(relDistsVisitPath));
        
        List<Integer> unvisited = getUnvisitedVerticesNtoC();
        System.out.println("unvisited: " + IO.intListToString(unvisited));
        Integer[] vim = new Integer[vPath.size() + unvisited.size()];
        Arrays.fill(vim, 0);
        for(Integer i : unvisited) {
            vim[i] = 1;
        }
        
        return vim;
    }
    
    protected List<Integer> getUnvisitedVerticesNtoC() {
        List<Integer> vPathNtoC = getNtoCPositionsOfVisitPath();
        Integer m = Collections.max(vPathNtoC);
        List<Integer> unvisited = new ArrayList<>();
        for(Integer i = 0; i <= m; i++ ) {
            if( ! vPathNtoC.contains(i)) {
                unvisited.add(i);
            }
        }
        return unvisited;
    }
        
    /**
     * Changes the indices of the vertices and edges in this.resultVertices and this.resultEdges according to the relative distances from the ADJ linnot.
     */
    private void considerNonCCVertices() {
        Integer[] vim = this.getVertexInsertMapNtoC();
        
        this.resultVertices = new ArrayList<>();
        System.out.println("considerNonCCVertices: vertexTypesNtoC = " + IO.stringListToString(this.vertexTypesNtoC));
        
        System.out.print("considerNonCCVertices: vertexTypesMapNtoC= ");
        for(Integer key : vertexTypesMapNtoC.keySet()) {           
            System.out.print(key + "=" + vertexTypesMapNtoC.get(key) + " ");
        }
        System.out.print("\n");
        
        List<Integer> keysSorted = new ArrayList<>();
        keysSorted.addAll(vertexTypesMapNtoC.keySet());
        Collections.sort(keysSorted);
        
        String v;
        for(Integer k : keysSorted) {
            v = vertexTypesMapNtoC.get(k);
            if(null != v) {
                this.resultVertices.add(v);
            }
        }
        
        //for(String s : this.vertexTypesNtoC) {
        //    this.resultVertices.add(s);
        //}
        
        this.resultEdges = new ArrayList<>();
        for(Integer [] e : this.outGraphEdges) {
            this.resultEdges.add(e);
        }
        
        Integer offset = 0; // when a vertex has been added, the places of the following vertices need to be adapted.
        for(Integer i = 0; i < vim.length; i++) {
            if(vim[i] == 1) {
                insertVertexIntoResultLists(i + offset, SSE.SSE_FGNOTATION_OTHER);
                offset++;
            }
        }
    }
    
    
    /**
     * Adds the vertex to the vertex list, and shifts source and target indices in the edges accordingly.
     * 
     * @param position
     * @param fglinnot 
     */
    private void insertVertexIntoResultLists(Integer positionOfNewNtoC, String fglinnot) {
        // fix vertex list
        System.out.println("### insertVertexIntoResultLists: before adding vertex: " + IO.stringListToString(this.resultVertices));
        this.resultVertices.add(positionOfNewNtoC, fglinnot);
        System.out.println("### insertVertexIntoResultLists: after adding vertex: " + IO.stringListToString(this.resultVertices));
        
        System.out.println("### insertVertexIntoResultLists: before shifting edges: " + IO.listOfintegerArraysToString(this.resultEdges));
        
        // fix edge list
        for(int i = 0; i < this.resultEdges.size(); i++) {
            Integer[] e = this.resultEdges.get(i);
            if(e[0] >= positionOfNewNtoC) {
                e[0]++;
            }
            if(e[1] >= positionOfNewNtoC) {
                e[1]++;
            }
        }
        System.out.println("### insertVertexIntoResultLists: after shifting edges: " + IO.listOfintegerArraysToString(this.resultEdges));
    }
    
    @Override
    public List<String> getResultVertices() {
        System.out.println("LinnotParserADJ: result vertices: " + IO.stringListToString(this.resultVertices));
        return this.resultVertices;
    }

    @Override
    public List<Integer[]> getResultEdges() {
        System.out.println("LinnotParserADJ: result edges: " + IO.listOfintegerArraysToString(resultEdges));
        return this.resultEdges;
    }        
    
}
