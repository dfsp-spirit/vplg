/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author ts
 */
public interface SimpleGraphInterface {
    
    public Integer getSize();
    
    public Boolean containsEdge(Integer i, Integer j);
    
    public ArrayList<Integer> neighborsOf(Integer i);
    
}
