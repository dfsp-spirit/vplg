/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package linnottograph;

import java.util.List;

/**
 *
 * @author spirit
 */
public interface ILinnotParserExt {
    public List<String> getResultVertices();
    public List<Integer[]> getResultEdges();    
}
