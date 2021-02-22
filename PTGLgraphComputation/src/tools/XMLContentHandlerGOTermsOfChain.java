/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2014. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package tools;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A content handler which parses GO term data from the RCSB PDB REST webservice.
 * @author spirit
 */
public class XMLContentHandlerGOTermsOfChain extends DefaultHandler {
    
    private Hashtable<String, Object> tags;     
    
    /** Each GO term is stored in a string array, positions: 0 = GO identifier, 1 = name, 2 = description. */
    protected List<String[]> terms;    
    
    private boolean doneParsing = false;
    protected String tag = "[SAX-CH-GO] ";
    private final boolean verbose = false;
    String parsedPdbID = null;
    String parsedChainID = null;

    @Override
    public void startDocument() throws SAXException {
        doneParsing = false;
        terms = new ArrayList<>();
    }

    
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        
        String key = localName;
        
        if(key.equals("term")) {
            if(verbose) {
                System.out.println(tag + "Found GO term '" + qName + "'.");
            }
            for(int i = 0; i < atts.getLength(); i++) {
                String attribute = atts.getLocalName(i);
                if(attribute.equals("id")) {
                    System.out.println(attribute + "=" + atts.getValue(i));
                }
                else if(attribute.equals("structureId")) {
                    parsedPdbID = atts.getValue(attribute);
                    System.out.println(attribute + "=" + atts.getValue(attribute));
                }
                else if(attribute.equals("chainId")) {
                    parsedChainID = atts.getValue(attribute);
                    System.out.println(attribute + "=" + atts.getValue(attribute));
                }
                else {
                    System.out.println("Hit unexpected attribute '" + attribute + "'.");
                }
            }
        }
        else if(key.equals("detail")) {            
            String[] term = new String[6];
            term[0] = parsedPdbID;
            term[1] = parsedChainID;
            for(int i = 0; i < atts.getLength(); i++) {
                String attribute = atts.getLocalName(i);
                if(attribute.equals("name")) {
                    System.out.println(attribute + "=" + atts.getValue(attribute));
                    term[2] = atts.getValue(attribute);
                }
                if(attribute.equals("definition")) {
                    System.out.println(attribute + "=" + atts.getValue(attribute));
                    term[3] = atts.getValue(attribute);
                }
                if(attribute.equals("synonyms")) {
                    System.out.println(attribute + "=" + atts.getValue(attribute));
                    term[4] = atts.getValue(attribute);
                }
                if(attribute.equals("ontology")) {
                    System.out.println(attribute + "=" + atts.getValue(attribute));
                    term[5] = atts.getValue(attribute);
                }
            }
            terms.add(term);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        doneParsing = true;
        String prop;
        for(String[] term : terms) {
            System.out.print("GO term for " + term[0] + " chain " + term[1] + ": ");
            for(int i = 0; i < term.length; i++) {
                prop = term[i];
                if(i >= 2) {
                    System.out.print(prop);
                    if(i < term.length - 1) {
                        System.out.print(", ");
                    }
                }
            }
            System.out.print("\n");
        }
    }
    
    /**
     * Returns the terms parsed from the XML string after parsing.
     * @return a list of terms, each term is an array of strings
     */
    public List<String[]> getTerms() {
        if( ! doneParsing) {
            throw new java.lang.RuntimeException("XML Handler ERROR: XMLContentHandlerSequenceClusterList: Not finished parsing the document yet, but being asked for results.");
        }
        else {
            return terms;
        }
    }
    
    
    /**
     * Main class for testing PDB representatives parsing only.
     * @param args ignored
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        
        String xml = "<goTerms>\n" +
"  <term id=\"GO:0006810\" structureId=\"4HHB\" chainId=\"A\">\n" +
"  <detail name=\"transport\" definition=\"some transport def here\" synonyms=\"blabla transport\" ontology=\"B\" />\n" +
"  </term>\n" +
"  <term id=\"GO:0006811\" structureId=\"4HHB\" chainId=\"A\">\n" +
"  <detail name=\"receptor-mediated endocytosis\" definition=\"some def of endo here\" synonyms=\"blabla2ndo\" ontology=\"B\" />\n" +
"  </term>\n" +
                "</goTerms>\n";
        
        XMLParserJAX p;
        String[] sep;
        try {
            p = new XMLParserJAX();
            p.setErrorHandler(new XMLErrorHandlerJAX(System.err));
            XMLContentHandlerGOTermsOfChain handler = new XMLContentHandlerGOTermsOfChain();
            p.handleXML(xml, handler);
            List<String[]> terms = handler.getTerms();
            System.out.println("Received a list of " + terms.size() + " terms from handler:");
            
        } catch(ParserConfigurationException | SAXException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
    
}
