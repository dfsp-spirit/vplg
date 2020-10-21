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
        
        String s, n; Integer sum;       // some degree distributions. the lists were copied from the database; the code in this class formats them for plotting with Latex PGF plots package.
        //s = "0,1,8,53,115,173,203,203,196,154,104,69,43,17,8,2,0";
        //s = "0,0,18,51,101,202,327,397,520,512,391,296,163,68,20,4,0,0,0,2,4,4,0";
        
        //s = "0,0,0,6,22,28,52,49,52,73,58,37,22,8,6,2,0,1,0"; sum = TextTools.getSum(s); // 8icd degree distribution, with ligands
        //s = "0,0,3,1,13,34,61,58,92,107,89,53,46,13,2,2,0,0,0,0,0,1,0,1,0,2,0"; sum = TextTools.getSum(s); // 3kmf degree distribution, with ligands
        //s = "0,1,8,53,115,173,203,203,196,154,104,69,43,17,8,2,0"; sum = TextTools.getSum(s); // 1hzh degree distribution, with ligands
        //s = "0,0,18,51,101,202,327,397,520,512,391,296,163,68,20,4,0,0,0,2,4,4,0"; sum = TextTools.getSum(s); // 4a97 degree distribution, with ligands
        
        s = "0,0,0,6,22,27,52,50,54,70,59,37,21,8,6,2,0"; sum = TextTools.getSum(s); // 8icd degree distribution, without ligands
        //s = "0,0,3,1,13,36,65,55,103,115,87,54,31,7,2,2,0"; sum = TextTools.getSum(s); // 3kmf degree distribution, without ligands
        //s = "0,1,8,52,114,177,203,206,192,145,97,68,41,17,8,2,0"; sum = TextTools.getSum(s); // 1hzh degree distribution, without ligands
        //s= "0,0,18,55,110,196,330,406,529,530,386,281,142,64,21,2,0"; sum = TextTools.getSum(s); // 4a97 degree distribution, without ligands
        
        //s = "416,416,416,416,410,388,360,308,259,207,134,76,39,17,9,3,1,1,0"; sum = TextTools.leftMostIntegerOf(s);   // cumul, 8ICD including ligands (not that the sum is the left-most value for cumulative distributions)
        //s = "578,578,578,575,574,561,527,466,408,316,209,120,67,21,8,6,4,4,4,4,4,4,3,3,2,2,0"; sum = 578;    // cumul, 3KMF including ligands
        //s = "1349,1349,1348,1340,1287,1172,999,796,593,397,243,139,70,27,10,2,0"; sum = 1349; // cumul, 1HZH including ligands
        //s = "3080,3080,3080,3062,3011,2910,2708,2381,1984,1464,952,561,265,102,34,14,10,10,10,10,8,4,0"; sum = 3080;  // cumul, 4A97 including ligands
        
        //s = "414,414,414,414,408,386,359,307,257,203,133,74,37,16,8,2,0"; sum = 414;    // cumul, 8icd without ligands
        //s = "574,574,574,571,570,557,521,456,401,298,183,96,42,11,4,2,0"; sum = 574;   // cumul, 3KMF without ligands
        //s = "1331,1331,1330,1322,1270,1156,979,776,570,378,233,136,68,27,10,2,0"; sum = 1331;   // cumul 1hzh without ligands
        
        
        //s = "3070,3070,3070,3052,2997,2887,2691,2361,1955,1426,896,510,229,87,23,2,0"; sum = 3070;   // cumul 4a97 without ligands
        //s = "416,326,252,187,131,91,54,26,6,4,3,1,1,0"; sum = 416;   // cumul 8ICD, only long range contacts (seqdist >= 10 residues). this disconnects the graph into 91 connected components, this is for the whole disconnected graph.
        
        
        //s = "416,416,414,400,342,282,222,154,114,35,11,1,1,0"; sum = 416; // cumul 8ICD, only SHORT range contacts (seqdist <= 10 residues). this disconnects the graph into 2 connected components, this is for the whole disconnected graph.
        
        
        n = generateNormalizedStringFromIntegerList(s, sum);
        System.out.println("s: " + s);   
        System.out.println("mean of distribution s: " + TextTools.getMeanOfIntegerHistogram(s));
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
    
    public static Integer getSum(String s) {
        Integer sum = 0;
        String[] numbers = s.split(",");
        for(int i = 0; i < numbers.length; i++) {
            String ns = numbers[i];
            Integer num = Integer.parseInt(ns);
            sum += num;
        }
        return sum;
    }
    
    public static Integer leftMostIntegerOf(String s) {
        String[] numbers = s.split(",");
        String ns = numbers[0];
        Integer num = Integer.parseInt(ns);
        return num;
    }
    
    /**
     * Handles the numbers as a histogram and computes the mean
     * @param s
     * @return the mean
     */
    public static Double getMeanOfIntegerHistogram(String s) {
        Integer sum = 0;
        Integer numValues = 0;
        String[] numbers = s.split(",");
        for(int i = 0; i < numbers.length; i++) {
            String ns = numbers[i];
            Integer num = Integer.parseInt(ns);
            sum += num * i;
            numValues += num;
        }
        Integer length = numbers.length;
        Double mean = (sum.doubleValue() / numValues.doubleValue());
        return mean;
    }
    
  
    
    
    public static String generateNormalizedStringFromIntegerList(String s, Integer sum) {
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
    
    
    public static Boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }
    
    public static Boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }
    
    public static Boolean isBoolean(String s) {
        try {
            Boolean.parseBoolean(s);
            return true;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }
    
    public static Boolean isTrueOrFalse(String s) {
        return s.toLowerCase().equals("true") || s.toLowerCase().equals("false");
    }
        
    
}
