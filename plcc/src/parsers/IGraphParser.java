/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
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
    public List<ParsedVertexInfo> getVertices();    
    public ParsedGraphInfo getGraphInfo();
    
}
