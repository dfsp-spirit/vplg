/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2014. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package tools;

import java.io.PrintStream;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A JAX error handler which gives more info than the default one, which only communicates fatal errors.
 * @author spirit
 */


public class XMLErrorHandlerJAX implements ErrorHandler {
    
    final private PrintStream out;

    public XMLErrorHandlerJAX(PrintStream out) {
        this.out = out;
    }

    private String getParseExceptionInfo(SAXParseException spe) {
        String systemId = spe.getSystemId();

        if (systemId == null) {
            systemId = "null";
        }

        String info = "URI=" + systemId + " Line=" 
            + spe.getLineNumber() + ": " + spe.getMessage();

        return info;
    }

    @Override
    public void warning(SAXParseException spe) throws SAXException {
        out.println("Warning: " + getParseExceptionInfo(spe));
    }
        
    @Override
    public void error(SAXParseException spe) throws SAXException {
        String message = "Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

    @Override
    public void fatalError(SAXParseException spe) throws SAXException {
        String message = "Fatal Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }
}
