/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

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
   
    multiQuickSortTS(arr1, arr2, 0, arr1.length);
  }

  /**
   * Internal multi quicksort, doing the real algorithm.
   */
  private static void multiQuickSort(int[][] a, int offset, int length,
      int indexToSort) {
    if (offset < length) {
      int pivot = multiPartition(a, offset, length, indexToSort);
      multiQuickSort(a, offset, pivot, indexToSort);
      multiQuickSort(a, pivot + 1, length, indexToSort);
    }
  }
  
  private static void multiQuickSortTS(Double[] arr1, String[] arr2, int offset, int length) {
    if (offset < length) {
      int pivot = multiPartitionTS(arr1, arr2, offset, length);
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
   * elements as well.
   */
  private static int multiPartition(int[][] array, int start, int end,
      int partitionArrayIndex) {
    final int ending = end - 1;
    final int x = array[partitionArrayIndex][ending];
    int i = start - 1;
    for (int j = start; j < ending; j++) {
      if (array[partitionArrayIndex][j] <= x) {
        i++;
        for (int arrayIndex = 0; arrayIndex < array.length; arrayIndex++) {
          swap(array[arrayIndex], i, j);
        }
      }
    }
    i++;
    for (int arrayIndex = 0; arrayIndex < array.length; arrayIndex++) {
      swap(array[arrayIndex], i, ending);
    }

    return i;
  }
  
  private static int multiPartitionTS(Double[] arr1, String[] arr2, int start, int end) {
    final int ending = end - 1;
    final double x = arr1[ending];
    int i = start - 1;
    for (int j = start; j < ending; j++) {
      if (arr1[j] <= x) {
        i++;
        for (int arrayIndex = 0; arrayIndex < arr1.length; arrayIndex++) {
          swap(arr1, i, j);
        }
      }
    }
    i++;
    
    swap(arr2, i, ending);    

    return i;
  }
  
  
 /**
   * Swaps the given indices x with y in the array.
   */
  public static void swap(int[] array, int x, int y) {
    int tmpIndex = array[x];
    array[x] = array[y];
    array[y] = tmpIndex;
  }
  
  /**
   * Swaps the given indices x with y in the array.
   */
  public static void swap(Double[] array, int x, int y) {
    double tmpIndex = array[x];
    array[x] = array[y];
    array[y] = tmpIndex;
  }
  
  /**
   * Swaps the given indices x with y in the array.
   */
  public static void swap(String[] array, int x, int y) {
    String tmpIndex = array[x];
    array[x] = array[y];
    array[y] = tmpIndex;
  }
    
}
