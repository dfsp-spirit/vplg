/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import java.io.IOException;
import org.xml.sax.SAXException;

/**
 *
 * @author ts
 */
public interface GraphMLFormat {
    
    /**
     * Returns a string representation of this object in GraphML format. This is an XML-based format,
     * see http://graphml.graphdrawing.org/ for details.
     * @return the GraphML format string
     */ 
    public String toGraphMLFormat() throws SAXException, IOException;
    
}
