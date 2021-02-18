/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2014. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package tools;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A wrapper around a JAX-based XML parser. This one is suitable if you do not need to edit/re-use the
 * document and thus do not need the whole DOM tree in memory at any time. It is an event-based read-once and
 * use instantly parser. We need it to parse XML received by calling web services like the RCSB PDB REST API.
 * @author spirit
 */
public class XMLParserJAX {
    
    SAXParserFactory factory;
    SAXParser saxParser;
    
    public XMLParserJAX() throws ParserConfigurationException, SAXException {
        factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        saxParser = factory.newSAXParser();
    }
    
    public void setErrorHandler(ErrorHandler h) throws SAXException {
        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setErrorHandler(new XMLErrorHandlerJAX(System.err));        
    }
    
    public void handleXML(String xml, DefaultHandler handler) throws SAXException, IOException {        
        saxParser.parse(new InputSource(new StringReader(xml)), handler);
    }
    
    
    
    /**
     * Main class for testing only. Currently test the SequenceCluster handler.
     * @param args ignored
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        
        String xml = "<sequenceCluster clusterNr=\"4\" clusterPercID=\"40\">\n" +
"<pdbChain name=\"2W72.A\" rank=\"1\"/>\n" +
"<pdbChain name=\"2W72.B\" rank=\"2\"/>\n" +
"<pdbChain name=\"2W72.D\" rank=\"2\"/>\n" +
"<pdbChain name=\"2W72.C\" rank=\"3\"/>\n" +
"<pdbChain name=\"1IRD.A\" rank=\"4\"/>\n" +
"<pdbChain name=\"1IRD.B\" rank=\"5\"/>\n" +
                "</sequenceCluster>\n";
        
        XMLParserJAX p;
        String[] sep;
        try {
            p = new XMLParserJAX();
            p.setErrorHandler(new XMLErrorHandlerJAX(System.err));
            XMLContentHandlerSequenceClusterList handler = new XMLContentHandlerSequenceClusterList();            
            p.handleXML(xml, handler);
            List<String> pdbChains = handler.getPdbChainList();
            System.out.println("Received a list of " + pdbChains.size() + " chains from handler:");
            for(String ic : pdbChains) {
                sep = PlccUtilities.parsePdbidAndChain(ic);
                if(sep != null) {
                    System.out.println("PDB ID: " + sep[0] + ", chain " + sep[1] + "");
                } else {
                    System.out.println("Result could not be parsed.");
                }
            }
            
        } catch(ParserConfigurationException | SAXException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
    
}
