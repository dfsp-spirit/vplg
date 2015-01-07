/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package tools;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import plcc.IO;

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
    
    /**
     * Picks a value from the possibilities.
     * @param possibleValues
     * @return some value
     */
    public static int pickOneRandomlyUniformFrom(int[] possibleValues) {
        int choiceByIndex = PlccUtilities.randInt(0, possibleValues.length - 1);
        return possibleValues[choiceByIndex];
    }
    
    /**
     * Multisorts two arrays, i.e., applies the same index shuffling to the second array as to the first one, which gets sorted (according to the natural ordering defined by its type).
     * Note that this only works if the first array contains only unique values.
     * @param arr1 the array that will be sorted by its natural ordering
     * @param arr2 the array that will gets its data re-indexed accordingly
     */
    public static void multiSortUniqueArrays(Double[] arr1, String[] arr2) {
        if(arr1.length!=arr2.length)throw new IllegalArgumentException();
        SortedMap<Double, String> map = new TreeMap<>();
        for (int i = 0; i < arr1.length; i++) {
            map.put(arr1[i], arr2[i]);
        }
        int ct = 0;
        for (Entry<Double, String> entry : map.entrySet()) {
            arr1[ct]=entry.getKey();
            arr2[ct]=entry.getValue();
            ct++;
        }
    }
    
    
    
    /**
   * Multi-sorts the given arrays with the quicksort algorithm. It assumes that
   * all arrays have the same sizes and it sorts on the first dimension of these
   * arrays. If the given arrays are null or empty, it will do nothing, if just
   * a single array was passed it will sort it via {@link Arrays} sort;
   */
  public static void multiQuickSort(int[]... arrays) {
    multiQuickSort(0, arrays);
  }
  
  /**
   * Multi-sorts the given arrays with the quicksort algorithm. It assumes that
   * all arrays have the same sizes and it sorts on the given dimension index
   * (starts with 0) of these arrays. If the given arrays are null or empty, it
   * will do nothing, if just a single array was passed it will sort it via
   * {@link Arrays} sort;
   */
  public static void multiQuickSort(int sortDimension, int[]... arrays) {
    // check if the lengths are equal, break if everything is empty
    if (arrays == null || arrays.length == 0) {
      return;
    }
    // if the array only has a single dimension, sort it and return
    if (arrays.length == 1) {
      Arrays.sort(arrays[0]);
      return;
    }
    // also return if the sort dimension is not in our array range
    if (sortDimension < 0 || sortDimension >= arrays.length) {
      return;
    }
    // check sizes
    int firstArrayLength = arrays[0].length;
    for (int i = 1; i < arrays.length; i++) {
      if (arrays[i] == null || firstArrayLength != arrays[i].length)
        return;
    }

    multiQuickSort(arrays, 0, firstArrayLength, sortDimension);
  }
  
  
  /**
   * Test function for multi array sorting.
   */
  public static void testMultiQuickSortTS() {
      Double[] scores = new Double[] { 0.9, 0.6, 0.4, 0.1, 0.4 };
      String[] labels = new String[] { "E", "D", "C", "A", "B" };
      
      System.out.println("Testing multi array sorting (Double[], String[]).");
      System.out.println("scores: " + IO.doubleArrayToString(scores));
      System.out.println("labels: " + IO.stringArrayToString(labels));
      
      System.out.println("Sorting...");
      PlccUtilities.multiQuickSortTS(scores, labels);
      
      System.out.println("scores: " + IO.doubleArrayToString(scores));
      System.out.println("labels: " + IO.stringArrayToString(labels));
    
  }
  
  /**
   * Test function for multi array sorting.
   */
  public static void testMultiQuickSort() {
      int[] scores = new int[] { 9, 6, 4, 1, 4 };
      int[] labels = new int[] { 5, 4, 3, 1, 2 };
      
      System.out.println("Testing multi array sorting (int[], int[]).");
      System.out.println("scores: " + IO.intArrayToString(scores));
      System.out.println("labels: " + IO.intArrayToString(labels));
      
      System.out.println("Sorting...");
      PlccUtilities.multiQuickSort(scores, labels);
      
      System.out.println("scores: " + IO.intArrayToString(scores));
      System.out.println("labels: " + IO.intArrayToString(labels));
    
  }
  
  /**
   * Replaces all null values in the array with the given replaceval, changing the input array.
   * @param in the array that will be changed
   * @param replaceVal the value to put instead of nulls
   */
  public static void replaceNullValuesInArrayWith(Double[] in, Double replaceVal) {
      for(int i = 0; i < in.length; i++) {
          if(in[i] == null) {
              in[i] = replaceVal;
          }
      }
  }
  
  /**
   * Implements a multi array sort (like the PHP function). This means it sorts the first array according
   * to its natural order. The indices of the second arrays are altered in the same way
   * like those in the first array. This means that the second array in NOT ordered in its
   * natural order afterwards, but it also means the following: if two elements, one from each array, shared
   * the same index in both arrays before this function was called, the same still holds true (it is, of course, most likely a different index now).
   * @param arr1 the first array
   * @param arr2 the second array
   */
  public static void multiQuickSortTS(Double[] arr1, String[] arr2) {
    // check if the lengths are equal, break if everything is empty
      
      if(arr1 == null || arr2 == null) {
          //System.err.println("multiQuickSortTS: array null.");
          return;
      }
      
      if(arr1.length <= 1) {
          //System.err.println("multiQuickSortTS: array length <= 1.");
          return;
      }
      
      if(arr1.length != arr2.length) {
          System.err.println("multiQuickSortTS: array lengths are not equal.");
          return;
      }
  
      //System.out.println("  multiQuickSortTS: array lengths are " + arr1.length + " and " + arr2.length + ".");
      multiQuickSortTS(arr1, arr2, 0, arr1.length);
  }

  /**
   * Internal multi quicksort, doing the real algorithm.
   * @param a a list of arrays
   * @param offset an offset for partitioning
   * @param length the array length
   * @param the index in the array list a, defining which of the arrays in the list is the one that should be sorted in its natural order
   */
  private static void multiQuickSort(int[][] a, int offset, int length, int indexToSort) {
    if (offset < length) {
      int pivot = multiPartition(a, offset, length, indexToSort);
      multiQuickSort(a, offset, pivot, indexToSort);
      multiQuickSort(a, pivot + 1, length, indexToSort);
    }
  }
  
  /**
   * Modified multi quick sort version for two arrays, the first one is used for sorting in natural order.
   * @param arr1 the array to be sorted
   * @param arr2 the other one, elements in this one will be swapped like the ones in the sorted array
   * @param offset
   * @param length 
   */
  private static void multiQuickSortTS(Double[] arr1, String[] arr2, int offset, int length) {
    if (offset < length) {
      int pivot = multiPartitionTS(arr1, arr2, offset, length);
      //System.out.println("  multiQuickSortTS: pivot=" + pivot + ", offset=" + offset + ".");
      multiQuickSortTS(arr1, arr2, offset, pivot);
      multiQuickSortTS(arr1, arr2, pivot + 1, length);
    }
  }

    /**
     * Partitions the given array in-place and uses the end element as pivot,
     * everything less than the pivot will be placed left and everything greater
     * will be placed right of the pivot. It returns the index of the pivot
     * element after partitioning. This is a multi way partitioning algorithm, you
     * have to provide a partition array index to know which is the array that
     * needs to be partitioned. The swap operations are applied on the other
     * array as well.
     */
    private static int multiPartition(int[][] array, int start, int end, int partitionArrayIndex) {
        final int ending = end - 1;
        final int x = array[partitionArrayIndex][ending];
        int i = start - 1;
        for (int j = start; j < ending; j++) {
            if (array[partitionArrayIndex][j] <= x) {
                i++;
                for (int arrayIndex = 0; arrayIndex < array.length; arrayIndex++) {
                    // swap all the other arrays, the ones that do NOT get sorted by their natural order
                    swap(array[arrayIndex], i, j);
                }
            }
        }
        i++;
        for (int arrayIndex = 0; arrayIndex < array.length; arrayIndex++) {
            // swap the one that gets sorted by its natural order
            swap(array[arrayIndex], i, ending);
        }

        return i;
    }

  /**
   * Partitions the given array in-place and uses the end element as pivot,
   * everything less than the pivot will be placed left and everything greater
   * will be placed right of the pivot. It returns the index of the pivot
   * element after partitioning. This is a multi way partitioning algorithm, you
   * and it is assumed that the first array, arr1, needs to be partitioned.
   * The swap operations are applied on the other
   * array as well.
   */
    private static int multiPartitionTS(Double[] arr1, String[] arr2, int start, int end) {            
        //System.out.println("  multiPartitionTS: array lengths are " + arr1.length + " and " + arr2.length + ", start=" + start + ", end=" + end + ".");
        final int ending = end - 1;
        final double pivot = arr1[ending];
        int i = start - 1;
        for (int j = start; j < ending; j++) {
            if (arr1[j] <= pivot) {
                i++;
                // swap the 'other' array, the one that does NOT get sorted
                //System.out.println("  multiPartitionTS: arr2: swapping values i=" + i + " and j=" + j + " (i and j are the old indices before swapping).");
                swap(arr2, i, j);
                swap(arr1, i, j);
          }
        }
        i++;

        // swap the sorted array
        
        //System.out.println("  multiPartitionTS: arr1: swapping values i=" + i + " and ending=" + ending + " (i and j are the old indices before swapping).");
        swap(arr1, i, ending);
        swap(arr2, i, ending);
        return i;
    }
  
  
 /**
   * Swaps the given indices x with y in the array.
   * @param array the array
   * @param x the first index
   * @param y the second index
   */
  public static void swap(int[] array, int x, int y) {
    int tmpIndex = array[x];
    array[x] = array[y];
    array[y] = tmpIndex;
  }
  
  /**
   * Swaps the given indices x with y in the array.
   * @param array the array
   * @param x the first index
   * @param y the second index
   */
  public static void swap(Double[] array, int x, int y) {
    double tmpIndex = array[x];
    array[x] = array[y];
    array[y] = tmpIndex;
  }
  
  /**
   * Swaps the given indices x with y in the array.
   * @param array the array
   * @param x the first index
   * @param y the second index
   */
  public static void swap(String[] array, int x, int y) {
    String tmpIndex = array[x];
    array[x] = array[y];
    array[y] = tmpIndex;
  }
  
  /**
   * Main used for testing only
   * @param args ignored
   */
  public static void main(String[] args) {
      PlccUtilities.testMultiQuickSortTS();
      System.out.println("");
      PlccUtilities.testMultiQuickSort();
  }
  
  
  /**
   * Parses a PDB API format PDB ID + chain string.
   * @param pdbAPIformatIdandChain an input string like "7tim.A", representing PDBID 7tim and chain A
   * @return an array of length 2, position 0 holds PDB ID (always lowercase) and position 1 holds chain (case left intact from input). Or null if the input string was invalid.
   */
  public static String[] parsePdbidAndChain(String pdbAPIformatIdandChain) {
      String pdbid, chain;
      if(pdbAPIformatIdandChain != null) {
          if(pdbAPIformatIdandChain.length() == 6) {
              if(pdbAPIformatIdandChain.charAt(4) == '.') {                
                  pdbid = pdbAPIformatIdandChain.substring(0, 4);
                  chain = pdbAPIformatIdandChain.substring(5, 6);
                  if(PlccUtilities.isValidPDBID(pdbid) && PlccUtilities.isValidChainID(chain)) {
                      return new String[] { pdbid.toLowerCase(), chain };
                  }
              }
          }
      }
      return null;
  }
  
  /**
   * Checks whether s could be a valid PDB ID.
   * @param s the string to test
   * @return true for something like "7tim" or "8ICD"
   */
  public static boolean isValidPDBID(String s) {
      if( s != null) {
          if(s.length() == 4) {
            Pattern p = Pattern.compile("[a-zA-Z0-9]");
            return p.matcher(s).find();
          }
      }            
      return false;
  }
  
  /**
   * Checks whether s could be a valid chain ID.
   * @param s the string to test
   * @return true for something like "A" or "Z"
   */
  public static boolean isValidChainID(String s) {
      if( s != null) {
          if(s.length() == 1) {
            Pattern p = Pattern.compile("[a-zA-Z0-9]");
            return p.matcher(s).find();
          }
      }            
      return false;
  }
    
}
