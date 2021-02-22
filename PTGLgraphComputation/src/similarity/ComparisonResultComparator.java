/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package similarity;

import java.util.Comparator;

/**
 * Simple comparator which allows comparison of ComparisonResults by their score.
 * @author ts
 */
public class ComparisonResultComparator implements Comparator<ComparisonResult> {
    
    @Override public int compare(ComparisonResult cr1, ComparisonResult cr2) {
        return cr1.getScore().compareTo(cr2.getScore());
    }
    
}


