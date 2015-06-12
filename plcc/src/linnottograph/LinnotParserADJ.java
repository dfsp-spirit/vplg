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
import proteingraphs.SSEGraph;
import tools.DP;

/**
 * Parser for ADJ linnot strings. Note that this parses a linnot string,
 * but constructs a PG (not an FG) from it. You can use the PG function to compute FGs in order to get
 * an FG from it (there may be multiple FGs, but the other ones will be isolated vertices. If the linnot one is an isolated vertex, there will obviously be only one FG in the list.).
 * @author spirit
 */
public class LinnotParserADJ extends LinnotParserRED implements ILinnotParser {
    
    public LinnotParserADJ(String linnot, String graphType) {
        super(linnot, graphType);
    }
    
    @Override
    public List<String> getVertexTypesNtoC() {
        List<String> vtypes = new ArrayList<>();
        Integer maxShift = this.getMaxShiftLeft();
        List<Integer> visited = this.getVisitPath();
        int numVerts = this.getAllVisitedVertices().size();
        Map<Integer, String> m = new HashMap<>();
        
        //System.out.println("visited " + visited.size() + ". maxShift = " + maxShift + "." );
        
        Integer indexNtoC;
        Integer reltoStartPos;
        for(int i = 0; i < visited.size(); i++) {
            reltoStartPos = visited.get(i);
            String vtype = LinnotParserTools.getSSETypeFromToken(tokens[i], SSEGraph.GRAPHTYPE_ALBELIG);
            indexNtoC = reltoStartPos - maxShift;
            //System.out.println("indexNtoC=" + indexNtoC);
            m.put(indexNtoC, vtype);
        }
        
        for(int i = 0; i < numVerts; i++) {
            vtypes.add(m.get(i));
        }                
        
        
        
        return vtypes;
    }
    
    protected Map<Integer, Integer> getVertexShiftsFromVertexInsertMap(Integer[] vertInsertMap) {
        Map<Integer, Integer> shifts = new HashMap<>();
        System.err.println("correctEdgesForVertexInserts: not implemented yet.");
        Integer totalShift = 0;
        for(int i = 0; i < vertInsertMap.length; i++) { // not done yet!!!!
            totalShift += vertInsertMap[i];
            shifts.put(i, totalShift);
        }
        return shifts;
    }
    
    protected List<Integer[]> correctEdgesForVertexInserts(List<Integer[]> inEdges, Integer[] vertInsertMap) {
        List<Integer[]> correctedEdges = new ArrayList<>();
        correctedEdges.addAll(inEdges);
        System.err.println("correctEdgesForVertexInserts: not implemented yet.");
        return correctedEdges;
    }
    
    protected Integer[] getVertexInsertMap() {
        Integer[] vim = new Integer[2];
        List<Integer> vPathNtoC = getNtoCPositionsOfVisitPath();
        System.out.println("vPath: " + IO.intListToString(vPathNtoC));
        return vim;
    }
    
    @Override
    public List<Integer[]> getOutGraphEdges() {
        List<Integer[]> shiftedEdges = this.getNonZEdges();
        List<Integer[]> finalEdges = new ArrayList<>();
        Integer maxShift = this.getMaxShiftLeft();
                       
        Integer [] oe;
        for(Integer[] e : shiftedEdges) {
            oe = new Integer[]{ e[0] - maxShift, e[1] - maxShift, e[2] };
            finalEdges.add(oe);
        }
        
        // ------------ fix ADJ verts from the parent FG ---------------
        Integer[] vim = this.getVertexInsertMap();
        List<Integer[]> correctedEdges = this.correctEdgesForVertexInserts(finalEdges, vim);
        finalEdges = correctedEdges;
        
        if(! this.distancesMakeSense()) {
            DP.getInstance().w("LinnotParserADJ", "getOutGraphEdges: Distances make no sense, linnot may not be a valid ADJ linnot string.");
        }
        return finalEdges;
    }
    
    
    
}
