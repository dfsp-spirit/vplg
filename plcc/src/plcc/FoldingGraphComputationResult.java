/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

import java.util.ArrayList;
import tools.DP;

/**
 * This stores the result of a folding graph computation. The result includes 
 * two graphs: 1) the RED/KEY graph, which is a connected component of the parent 
 * protein graph. And 2) the ADJ/SEQ graph, which is a CC plus the vertices of the 
 * parent PG which lie between the FG vertices (if any).
 * 
 * @author ts
 */
public class FoldingGraphComputationResult {

    /** The FG with vertices for the ADJ and SEQ notations: vertices of a single connected component of the protein graph AND all parent graph vertices in between them. I.e.,
     if the FG consists of the vertices 4, 6, 7 and 8 of the parent PG, the vertex 5 is included as well in this graph type. */
    private FoldingGraph fgADJandSEQ;
    
    /** The FG with vertices for the RED and KEY notations: only the vertices of a single connected component of the protein graph. */
    private FoldingGraph fgREDandKEY;
    
    
    /**
     * Constructor.
     * @param fgADJandSEQ a FG suited for ADJ and SEQ notations (includes non-CC vertices from parent PG)
     * @param fgREDandKEY a FG suited for RED and KEY notations (strictly a CC of the parent PG)
     */
    FoldingGraphComputationResult(FoldingGraph fgADJandSEQ, FoldingGraph fgREDandKEY) {
        this.fgADJandSEQ = fgADJandSEQ;
        this.fgREDandKEY = fgREDandKEY;
        
        if( ! this.fgADJandSEQ.getIsForADJandSEQNotations()) {
            DP.getInstance().e("FoldingGraphComputationResult", "Given ADJ/SEQ FG is not a proper FG of this type.");
        }
        
        if( this.fgREDandKEY.getIsForADJandSEQNotations()) {
            DP.getInstance().e("FoldingGraphComputationResult", "Given RED/KEY FG is not a proper FG of this type.");
        }
    }

    /*
    public FoldingGraph getFgADJandSEQ() {
        return fgADJandSEQ;
    }

    public void setFgADJandSEQ(FoldingGraph fgADJandSEQ) {
        this.fgADJandSEQ = fgADJandSEQ;
    }

    public FoldingGraph getFgREDandKEY() {
        return fgREDandKEY;
    }

    public void setFgREDandKEY(FoldingGraph fgREDandKEY) {
        this.fgREDandKEY = fgREDandKEY;
    }
    
    public String getNotationADJ() {
        return this.fgADJandSEQ.getNotationADJ();
    }
    
    public String getNotationSEQ() {
        return this.fgADJandSEQ.getNotationSEQ();
    }
    
    public String getNotationRED() {
        return this.fgREDandKEY.getNotationRED();
    }
    
    public String getNotationKEY() {
        return this.fgREDandKEY.getNotationKEY(true);
    }
    
    
    public static ArrayList<FoldingGraph> getFoldingGraphsADJandSEQFromFGCR(ArrayList<FoldingGraphComputationResult> fgcrList) {
        ArrayList<FoldingGraph> list = new ArrayList<FoldingGraph>();
        for(int i = 0; i < fgcrList.size(); i++) {
            list.add(fgcrList.get(i).getFgADJandSEQ());
        }
        return list;
    }
    
    public static ArrayList<FoldingGraph> getFoldingGraphsREDandKEYFromFGCR(ArrayList<FoldingGraphComputationResult> fgcrList) {
        ArrayList<FoldingGraph> list = new ArrayList<FoldingGraph>();
        for(int i = 0; i < fgcrList.size(); i++) {
            list.add(fgcrList.get(i).getFgREDandKEY());
        }
        return list;
    }
    */
}
