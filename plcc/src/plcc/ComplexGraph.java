/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Marcus Kessler 2013. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author Marcus Kessler
 * modified by TS
 */
package plcc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Iterator;
import net.sourceforge.spargel.datastructures.UAdjListGraph;
import net.sourceforge.spargel.writers.GMLWriter;

/**
 *
 * @author marcus
 */
public class ComplexGraph extends UAdjListGraph {

    public Map<Edge, Integer> numHelixHelixInteractionsMap;
    public Map<Edge, Integer> numHelixStrandInteractionsMap;
    public Map<Edge, Integer> numHelixLoopInteractionsMap;
    public Map<Edge, Integer> numSSInteractionsMap;
    public Map<Edge, Integer> numStrandLoopInteractionsMap;
    public Map<Edge, Integer> numLoopLoopInteractionsMap;
    public Map<Edge, Integer> numAllInteractionsMap;
    public Map<Edge, Integer> numDisulfidesMap;
    public Map<Vertex, String> proteinNodeMap;
    
    /** The RCSB PDB id this graph is based on. */
    private String pdbid;

    /**
     * Constructor.
     */
    ComplexGraph(String pdbid) {
        this.pdbid = pdbid;

        numHelixHelixInteractionsMap = createEdgeMap();
        numHelixStrandInteractionsMap = createEdgeMap();
        numHelixLoopInteractionsMap = createEdgeMap();
        numSSInteractionsMap = createEdgeMap();
        numStrandLoopInteractionsMap = createEdgeMap();
        numLoopLoopInteractionsMap = createEdgeMap();
        numAllInteractionsMap = createEdgeMap();
        numDisulfidesMap = createEdgeMap();
        proteinNodeMap = createVertexMap();
    }

    public Vertex getVertexFromChain(String chainID) {
        Iterator<Vertex> vertIter = this.getVertices().iterator();
        Vertex nextVert;
        while (vertIter.hasNext()) {
            nextVert = vertIter.next();
            if (ComplexGraph.this.proteinNodeMap.get(nextVert).equals(chainID)) {
                return nextVert;
            }
        }
        return null;
    }

    public String getPDBID() {
        return this.pdbid;
    }

    /**
     * Writes this complex graph to the file 'file' in GML format. Note that
     * this function will overwrite the file if it exists.
     *
     * @param file the target file. Has to be writable.
     * @return true if the file was written, false otherwise
     */
    public boolean writeToFileGML(File file) {

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                System.err.println("ERROR: Could not create file '" + file.getAbsolutePath() + "': " + ex.getMessage() + ".");
                return false;
            }
        }

        GMLWriter<ComplexGraph.Vertex, ComplexGraph.Edge> gw = new GMLWriter<ComplexGraph.Vertex, ComplexGraph.Edge>(this);
        gw.addVertexAttrWriter(new GMLWriter.AttrWriter<Vertex>() {
            @Override
            public String getAttribute() {
                return "label";
            }

            @Override
            public boolean hasValue(Vertex o) {
                return proteinNodeMap.containsKey(o);
            }

            @Override
            public String write(Vertex o) {
                return '"' + proteinNodeMap.get(o).toString() + '"';
            }
        });

        //gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>());
        /**
         * Overwrite the edge attribute writer for the labels. This has to be done
         * because the labels need to be enclosed in closing quotation marks to mark them as strings (due to
         * the GML format definition).
         */
        gw.addEdgeAttrWriter(new GMLWriter.AttrWriter<Edge>() {
            @Override
            public String getAttribute() {
                return "label";
            }

            @Override
            public boolean hasValue(Edge e) {
                return numAllInteractionsMap.containsKey(e);
            }

            @Override
            public String write(Edge e) {
                return '"' + numAllInteractionsMap.get(e).toString() + '"';
            }
            
        });
        
        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_HH_contacts", numHelixHelixInteractionsMap));
        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_HS_contacts", numHelixStrandInteractionsMap));
        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_HL_contacts", numHelixLoopInteractionsMap));
        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_SS_contacts", numSSInteractionsMap));
        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_SL_contacts", numStrandLoopInteractionsMap));
        gw.addEdgeAttrWriter(new GMLWriter.MapAttrWriter<>(
                "num_LL_contacts", numLoopLoopInteractionsMap));

        FileOutputStream fop = null;
        boolean allOK = true;
        try {
            fop = new FileOutputStream(file);
            gw.write(fop);
            fop.flush();
            fop.close();
        } catch (Exception e) {
            System.err.println("ERROR: Could not write complex graph to file '" + file.getAbsolutePath() + "': " + e.getMessage() + ".");
            allOK = false;
        } finally {
            if (fop != null) {
                try {
                    fop.close();
                } catch (Exception e) {
                    // nvm
                }
            }

        }
        return allOK;
    }
}
