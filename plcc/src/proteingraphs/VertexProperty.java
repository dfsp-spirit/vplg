/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package proteingraphs;

import java.util.Arrays;
import plccSettings.Settings;
import tools.TextTools;

/**
 * These constants are for the writers and parsers, e.g., the GMLGraphParser.
 * @author spirit
 */
public class VertexProperty {
    
    public static final String FGNOTATIONLABEL = TextTools.formatAsCaseStyle(Arrays.asList("FG", "notation", "label"), Settings.getBoolean("plcc_B_gml_snake_case"));
    public static final String LABEL = "label";
    public static final String VERTEXID = "id";
    public static final String COLOR = "color";
    
}
