 /*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

/**
 * The DOT language interface, used to output graphs in DOT language format. See 
 * http://en.wikipedia.org/wiki/DOT_language for details.
 * @author ts
 */
public interface DOTLanguageFormat {
    
    /**
     * DOT language output support. See 
     * http://en.wikipedia.org/wiki/DOT_language for details.
     */    
    public String toDOTLanguageFormat();
}
