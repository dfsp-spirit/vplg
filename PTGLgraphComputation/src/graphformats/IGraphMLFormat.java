/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphformats;

import java.io.IOException;
import org.xml.sax.SAXException;

/**
 *
 * @author ts
 */
public interface IGraphMLFormat {
    
    /**
     * Returns a string representation of this object in GraphML format. This is an XML-based format,
     * see http://graphml.graphdrawing.org/ for details.
     * @return the GraphML format string
     */ 
    public String toGraphMLFormat() throws SAXException, IOException;
    
}
