/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package similarity;

/**
 * This class implements a datatype that stores the result of the comparison of two SSE graphs by some property.
 * @author ts
 */
public class ComparisonResult {
    
    public String pdbidSource;
    public String chainNameSource;
    public String graphTypeSource;
    public String propertySource;   // just use its toString method if its not a string, this is for printing only
    
    public String pdbidTarget;
    public String chainNameTarget;
    public String graphTypeTarget;
    public String propertyTarget;
    
    public String similarityMethod;
    public Integer similarityScore;
    
    /**
     * Constructor. Initializes this comparison result with the properties of the first graph. Use setTarget() to define
     * the second graph and setMethod() to set the comparison method and results.
     * 
     * @param pdbidSource the PDB ID of the first graph
     * @param chainNameSource the chain name of the first graph
     * @param graphTypeSource  the graph type of the first graph
     */
    public ComparisonResult(String similarityMethod, Integer similarityScore) {
        this.similarityMethod = similarityMethod;
        this.similarityScore = similarityScore;
        
        this.pdbidSource = "UNKNOWN";
        this.chainNameSource = "UNKNOWN";
        this.graphTypeSource = "UNKNOWN";
        
        this.pdbidTarget = "UNKNOWN";
        this.chainNameTarget = "UNKNOWN";
        this.graphTypeTarget = "UNKNOWN";        
    }
    
    
    /**
     * Sets information to identify the first graph of the pair.
     * @param pdbidSource the PDB ID of the first graph
     * @param chainNameSource the chain name of the first graph
     * @param graphTypeSource  the graph type of the first graph
     */
    public void setSource(String pdbidSource, String chainNameSource, String graphTypeSource) {        
        this.pdbidSource = pdbidSource;
        this.chainNameSource = chainNameSource;
        this.graphTypeSource = graphTypeSource;
    }
    
    
    /**
     * Sets information to identify the second graph of the pair.
     * @param pdbidTarget the PDB ID of the second graph
     * @param chainNameTarget the chain name of the second graph
     * @param graphTypeTarget the graph type of the second graph
     */
    public void setTarget(String pdbidTarget, String chainNameTarget, String graphTypeTarget) {        
        this.pdbidTarget = pdbidTarget;
        this.chainNameTarget = chainNameTarget;
        this.graphTypeTarget = graphTypeTarget;
    }
    
    /**
     * Sets the similarity method and its resulting score.
     * @param similarityMethod
     * @param similarityScore 
     */
    public void setMethodAndScore(String similarityMethod, Integer similarityScore) {
        this.similarityMethod = similarityMethod;
        this.similarityScore = similarityScore;
    }
    
    
    /**
     * Returns a string which can be used to uniquely identify the source graph.
     * @return the ID string
     */
    public String getSourceString() {
        return(this.pdbidSource + "-" + this.chainNameSource + "-" + this.graphTypeSource);
    }
    
    /**
     * Returns a string which can be used to uniquely identify the source graph.
     * @return the ID string
     */
    public String getTargetString() {
        return(this.pdbidTarget + "-" + this.chainNameTarget + "-" + this.graphTypeTarget);
    }
    
    /**
     * Returns the similarity score of the comparison.
     * @return the similarity score of the comparison
     */
    public Integer getScore() {
        return(this.similarityScore);
    }
    
    public String getMethod() {
        return(this.similarityMethod);
    }
    
    public String getPropertySource() {
        return(this.propertySource);
    }
    
    public String getPropertyTarget() {
        return(this.propertyTarget);
    }
    
    public void setPropertySource(String prop) {
        this.propertySource = prop;
    }
    
    public void setPropertyTarget(String prop) {
        this.propertyTarget = prop;
    }
    
}
