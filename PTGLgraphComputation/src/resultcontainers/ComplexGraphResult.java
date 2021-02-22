/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Marcus Kessler 2013. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package resultcontainers;

import proteingraphs.ComplexGraph;
import java.io.File;

/**
 * A simple data structure to hold the complex graph results for HTML generation.
 * @author ts
 */
public class ComplexGraphResult {
    
    /** The complex graph itself. */
    protected ComplexGraph compGraph;
    
    /** A text file holding the complex graph in GML format. */
    protected File comGraphFileGML;

    public File getComGraphFileGML() {
        return comGraphFileGML;
    }

    public void setComGraphFileGML(File comGraphFileGML) {
        this.comGraphFileGML = comGraphFileGML;
    }
    
    public ComplexGraphResult() {
        this.compGraph = null;
        this.comGraphFileGML = null;
    }

    /**
     * Getter for the complex graph.
     * @return the complex graph
     */
    public ComplexGraph getCompGraph() {
        return compGraph;
    }

    /**
     * Setter for the complex graph.
     * @param compGraph the complex graph
     */
    public void setCompGraph(ComplexGraph compGraph) {
        this.compGraph = compGraph;
    }
    
}
