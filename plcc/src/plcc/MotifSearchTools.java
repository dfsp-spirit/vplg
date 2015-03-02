/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

/**
 *
 * @author ts
 */
public class MotifSearchTools {
    
    public static String removePTGLBRacketsFromString(String in) {
        if(in == null || in.length() == 0) {
            return in;
        }
        String out = in.replaceAll("[\\{\\}\\[\\]\\(\\)]", "");                
        return out;
    }
    
    public static void main(String[] args) {
        MotifSearchTools.test();
    }
    
    public static void test() {
        
        // test removePTGLBRacketsFromString
        String in = "{3p,1p,1p,-4p,6p}";
        String out = MotifSearchTools.removePTGLBRacketsFromString(in);
        System.out.println("In was '" + in + "', out is '" + out + "'.");
        if(! in.equals(out)) {
            System.err.println("Test failed.");
        }
        
    }
    
}
