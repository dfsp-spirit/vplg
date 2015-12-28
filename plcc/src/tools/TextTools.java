/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2014. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package tools;

/**
 * Some text format and parsing tools. Some of these are not really used in PLCC, but I needed them for writing my thesis.
 * @author spirit
 */
public class TextTools {
    
    /**
     * Test main class
     * @param args ignored
     */
    public static void main(String[] args) {
        String s = "";
        System.out.println(TextTools.prepareDatabaseOutputForPlottingInLatex(s));
    }
    
    /**
     * Takes a string like '0,4,7,2' and outputs '(0,0) (1,4) (2,7) (3,2)'. Used for plotting degree distributions in pgf plots package.
     * @param s a string like '0,4,7,2'
     * @return a string like '(0,0) (1,4) (2,7) (3,2)'
     */
    public static String prepareDatabaseOutputForPlottingInLatex(String s) {
        StringBuilder sb = new StringBuilder();
        String[] numbers = s.split(",");
        for(int i = 0; i < numbers.length; i++) {
            String n = numbers[i];
            sb.append("(").append(i).append(",").append(n).append(") ");
        }
        return sb.toString();
    }
    
}
