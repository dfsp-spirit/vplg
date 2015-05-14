/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plcc;

import java.util.List;

/**
 *
 * @author ts
 */
public interface ILinnotParser {
    public List<String> getContactTypesList();
    public List<String> getSSETypesList();
    public List<Integer> getRelDistList();
    public Integer getNumParsedSSEs();
    public Integer getNumParsedEdges();
    public Integer getNumBackEdges();
    public List<Integer> getAllVisitedVertices();
    public Integer getMaxShiftLeft();
    public List<Integer> getVisitPath();
    public List<Integer[]> getNonZEdges();
}
