/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package proteingraphs;

import java.util.Arrays;
import Settings.Settings;
import tools.TextTools;

/**
 * These constants are for the writers and parsers, e.g., the GMLGraphParser.
 * @author spirit
 */
public class VertexProperty {
    
    public static final String FGNOTATIONLABEL = TextTools.formatAsCaseStyle(Arrays.asList("FG", "notation", "label"), Settings.getBoolean("PTGLgraphComputation_B_gml_snake_case"));
    public static final String LABEL = "label";
    public static final String VERTEXID = "id";
    public static final String COLOR = "color";
    
}
