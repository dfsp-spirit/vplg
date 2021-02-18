/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package parsers;

import tools.DP;

/**
 *
 * @author spirit
 */
public class ParserTools {

    /**
     * Parses key-value lines in format "<key> <value>", where <value> may be enclosed in "".
     * @param s the input string
     * @return string array of length 2, at position 0 is the key, at 1 is the value
     */
    public static String[] getKeyValue(String s) {
        s = s.trim();
        if (s.contains("\"")) {
            int idx = s.indexOf(" ");
            String key = s.substring(0, idx);
            String value = s.substring(idx + 2, s.length() - 1);
            return new String[]{key, value};
        } else {
            String[] tmp = s.split(" ");
            if (tmp.length != 2) {
                DP.getInstance().e("GMLGraphParser", "getKeyValue: Splitting line without \" in it resulted in " + tmp.length + " tokens, expected 2.");
            }
            return tmp;
        }
    }
    
}
