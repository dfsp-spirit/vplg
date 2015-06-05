/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package parsers;

import java.util.List;

/**
 *
 * @author spirit
 */
public interface IGraphParser {
    public List<ParsedEdgeInfo> getEdges();    
    public List<ParsedVertexInfo> getVerts();    
    public ParsedGraphInfo getGraphInfo();
    
}
