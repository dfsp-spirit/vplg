/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2014. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package tools;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A JAX-based XML parser. This one is suitable if you do not need to edit/re-use the
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
    
    public void getListFromXML(String xml, DefaultHandler handler) throws SAXException, IOException {
        saxParser.parse(new InputSource(new StringReader(xml)), handler);
    }
    
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
        try {
            p = new XMLParserJAX();
            p.setErrorHandler(new XMLErrorHandlerJAX(System.err));
            p.getListFromXML(xml, new XMLContentHandlerPDBDomainList());
            
        } catch(ParserConfigurationException | SAXException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
    
}
