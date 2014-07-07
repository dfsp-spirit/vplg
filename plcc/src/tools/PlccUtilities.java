/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

import java.util.Random;

/**
 *
 * @author ts
 */
public class PlccUtilities {
    
    /**
     * Returns a pseudo-random number between min and max, inclusive.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    public static int pickOneRandomlyUniformFrom(int[] possibleValues) {
        int choiceByIndex = PlccUtilities.randInt(0, possibleValues.length - 1);
        return possibleValues[choiceByIndex];
    }
    
}
