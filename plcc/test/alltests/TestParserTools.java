/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package alltests;

import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import parsers.ParserTools;

/**
 *
 * @author spirit
 */
public class TestParserTools extends TestCase {
    
    @org.junit.Test public void testGetKeyValueInteger() {                     
        String[] kv = ParserTools.getKeyValue(" id 3");
        assertEquals("id", kv[0]);
        assertEquals("3", kv[1]);
        assertEquals(2, kv.length);
    }
    
    @org.junit.Test public void testGetKeyValueString() {                     
        String[] kv = ParserTools.getKeyValue(" name \"BratzBert\" ");
        assertEquals("name", kv[0]);
        assertEquals("BratzBert", kv[1]);
        assertEquals(2, kv.length);
    }
    
}
