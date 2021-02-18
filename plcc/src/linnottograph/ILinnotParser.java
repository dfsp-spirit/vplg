/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package linnottograph;

import java.util.List;

/**
 * Internal interface, for testing only. Could be removed later.
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
    public List<String> getVertexTypesNtoC();
    public List<Integer> getNtoCPositionsOfVisitPath();
    public List<Integer[]> getOutGraphEdges();
    public Boolean distancesMakeSense();
}
