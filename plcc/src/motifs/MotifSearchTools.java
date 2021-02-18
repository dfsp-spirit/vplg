/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package motifs;

import java.util.Arrays;
import java.util.Collections;

/**
 * Some text tools required for the implementation of the (sometimes rather weird) motif search code in the linear notations.
 * This is a utility class which only provides static methods to be used by the DBManager functions chainContainsMotif*.
 * @author ts
 */
public class MotifSearchTools {
    
    /**
     * Removes all occurrences of the brackets {} () [] from a copy of input String, and returns the modified copy.
     * @param in the input string, this is not changed
     * @return the modified output string with brackets removed
     */
    public static String removePTGLBracketsAndCommaFromString(String in) {
        if(in == null || in.length() == 0) {
            return in;
        }
        String out = in.replaceAll("[\\{\\}\\[\\]\\(\\)\\,]", "");                
        return out;
    }
    
    /**
     * Removes all occurrences of the sse types e h l, the comma and the brackets {} () [] from a copy of input String, and returns the modified copy.
     * @param in the input string, this is not changed
     * @return the modified output string with brackets removed
     */
    public static String removePTGLBracketsAndSSETypesFromString(String in) {
        if(in == null || in.length() == 0) {
            return in;
        }
        String tmp = MotifSearchTools.removePTGLBracketsAndCommaFromString(in);
        String out = tmp.replaceAll("[ehl]", "");
        return out;
    }
    
    
    /**
     * Removes all occurrences of the PTGL relative orientations a p m z from a copy of input String, and returns the modified copy.
     * @param in the input string, this is not changed
     * @return the modified output string
     */
    public static String removePTGLRelativeOrientationsFromString(String in) {        
        return MotifSearchTools.replacePTGLRelativeOrientationsInStringWith(in, "");
    }
    
    /**
     * Replaces all occurrences of the PTGL relative orientations a p m z in a copy of input String with the replacement string, and returns the modified copy.
     * @param in the input string, this is not changed
     * @param replacement the replacement string
     * @return the modified output string
     */
    public static String replacePTGLRelativeOrientationsInStringWith(String in, String replacement) {
        if(in == null || in.length() == 0) {
            return in;
        }        
        String out = in.replaceAll("[apmz]", replacement);
        return out;
    }
    
    /**
     * Find the starting position of a (sub-)array within another array, or -1 if there is no such index.
     * This uses the Collections.indexOfSubList() method internally.
     * @param array the larger array
     * @param subArray the sub array to find in array
     * @return the starting index of subArray in array, or -1 if no such index exists
     */
    public static int findSubArray(Integer[] array, Integer[] subArray) {
        return Collections.indexOfSubList(Arrays.asList(array), Arrays.asList(subArray));
    }
    
    
    /**
     * Parses a PTGL linear notation ADJ or RED string, and returns an array of the relative distances (in the order they appear in the string).
     * @param linnotString the linnot string, like '{3ph,1ph,-2ph}'
     * @return an array of the numbers in the string, like {3, 1, -2}. Or null if the input string is empty or null.
     */
    public static Integer[] getRelativeDistancesArrayFromPTGLRedAdjString(String linnotString) {
        if(linnotString == null || linnotString.isEmpty()) {
            return null;
        }
        String noBrackets = MotifSearchTools.removePTGLBracketsAndSSETypesFromString(linnotString); // leaves us with something like '3p1p-2p}'
        String commata = MotifSearchTools.replacePTGLRelativeOrientationsInStringWith(noBrackets, ",");
        String[] strArray = commata.split("\\,", -1);
        Integer[] res = new Integer[strArray.length];
        Arrays.fill(res, 0);
        
        //replacePTGLRelativeOrientationInStringWith(noBrackets, ","); leaves a "," at the end, making the string one element longer
        for(int i = 0; i < strArray.length - 1; i++) {
            res[i] = Integer.parseInt(strArray[i]);
        }
        
        return res;
    }   
    
    /**
     * Runs some tests only.
     * @param args ignored
     */
    public static void main(String[] args) {
        MotifSearchTools.test();
    }
    
    
    /**
     * Runs some simple tests against the functions of this class.
     */
    public static void test() {
        
        String in, out;
        
        // test removePTGLBRacketsFromString
        in = "{3p,1p,1p,-4p,6p}";
        out = MotifSearchTools.removePTGLBracketsAndCommaFromString(in);
        System.out.println("In was '" + in + "', out is '" + out + "'.");
        if(out.equals("3p1p1p-4p6p")) {
            System.out.println("Test 1 OK.");
        }
        else {
            System.err.println("Test 1 FAILED.");
        }
        
        in = "[3p,1p,1p,-4p,6p]";
        out = MotifSearchTools.removePTGLBracketsAndCommaFromString(in);
        System.out.println("In was '" + in + "', out is '" + out + "'.");
        if(out.equals("3p1p1p-4p6p")) {
            System.out.println("Test 2 OK.");
        }
        else {
            System.err.println("Test 2 FAILED.");
        }
        
        in = "(3p,1p,1p,-4p,6p)";
        out = MotifSearchTools.removePTGLBracketsAndCommaFromString(in);
        System.out.println("In was '" + in + "', out is '" + out + "'.");
        if(out.equals("3p1p1p-4p6p")) {
            System.out.println("Test 3 OK.");
        }
        else {
            System.err.println("Test 3 FAILED.");
        }
        
        in = "";
        out = MotifSearchTools.removePTGLBracketsAndCommaFromString(in);
        System.out.println("In was '" + in + "', out is '" + out + "'.");
        if(out.equals("")) {
            System.out.println("Test 4 OK.");
        }
        else {
            System.err.println("Test 4 FAILED.");
        }
        
        in = null;
        out = MotifSearchTools.removePTGLBracketsAndCommaFromString(in);
        System.out.println("In was '" + in + "', out is '" + out + "'.");
        if(out != null) {
            System.err.println("Test 5 FAILED.");
        }
        else {
            System.out.println("Test 5 OK.");
        }
        
        in = "(h,3ph,1pe,1pe,-4ph,6ph)";
        out = MotifSearchTools.removePTGLBracketsAndSSETypesFromString(in);
        System.out.println("In was '" + in + "', out is '" + out + "'.");
        if(out.equals("3p1p1p-4p6p")) {
            System.out.println("Test 6 OK.");
        }
        else {
            System.err.println("Test 6 FAILED.");
        }
        
        
        
    }
    
}
