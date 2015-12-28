/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2014. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package tools;

import java.util.Locale;

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
        
        String s, n; Integer sum;
        //s = "0,1,8,53,115,173,203,203,196,154,104,69,43,17,8,2,0";
        //s = "0,0,18,51,101,202,327,397,520,512,391,296,163,68,20,4,0,0,0,2,4,4,0";
        
        //s = "416,416,416,416,410,388,360,308,259,207,134,76,39,17,9,3,1,1,0"; sum = 416;   // cumul, 8ICD
        //s = "578,578,578,575,574,561,527,466,408,316,209,120,67,21,8,6,4,4,4,4,4,4,3,3,2,2,0"; sum = 578;    // cumul, 3KMF
        s = "1349,1349,1348,1340,1287,1172,999,796,593,397,243,139,70,27,10,2,0"; sum = 1349; // cumul, 1HZH
        //s = "3080,3080,3080,3062,3011,2910,2708,2381,1984,1464,952,561,265,102,34,14,10,10,10,10,8,4,0"; sum = 3080;  // cumul, 4A97
        
        
        n = generateNormalizedString(s, sum);
        System.out.println("s: " + s);        
        System.out.println("=>");
        System.out.println(TextTools.prepareDatabaseOutputForPlottingInLatex(s));
        System.out.println("");
        System.out.println("n (using sum=" + sum + "): " + n);
        System.out.println("=>");
        System.out.println(TextTools.prepareDatabaseOutputForPlottingInLatex(n));
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
    
    
    
    public static String generateNormalizedString(String s, Integer sum) {
        StringBuilder sb = new StringBuilder();
        String[] numbers = s.split(",");
        for(int i = 0; i < numbers.length; i++) {
            String ns = numbers[i];
            Integer num = Integer.parseInt(ns);
            Double normalized = (num.doubleValue() / sum.doubleValue());
            String normalizedString = String.format(Locale.US, "%.4f", normalized);
            sb.append(normalizedString);
            if(i < numbers.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
}
