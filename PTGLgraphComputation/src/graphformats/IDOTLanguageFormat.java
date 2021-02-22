 /*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphformats;

/**
 * The DOT language interface, used to output graphs in DOT language format. See 
 * http://en.wikipedia.org/wiki/DOT_language for details.
 * @author ts
 */
public interface IDOTLanguageFormat {
    
    /**
     * DOT language output support. See 
     * http://en.wikipedia.org/wiki/DOT_language for details.
     */    
    public String toDOTLanguageFormat();
}
