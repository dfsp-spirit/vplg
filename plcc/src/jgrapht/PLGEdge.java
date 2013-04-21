/*
 * This file is part of SPARGEL, the sparse graph extension library.
 * 
 * This is free software, published under the WTFPL.
 * 
 * Written by Tim Schaefer, see http://rcmd.org/contact/.
 * 
 */
package jgrapht;

import java.util.BitSet;
import plcc.SpatRel;

/**
 * Ab edge for a protein ligand graph. This edge in undirected, and it holds information on the 
 * relative spatial orientation of the two SSEs it connects (e.g., parallel) and the type of the 
 * connection between them (e.g., disulfide bridge).
 * 
 * @author ts
 */
public class PLGEdge extends org.jgrapht.graph.DefaultEdge {
    
    protected int spatialOrientation;
    protected BitSet bondProperties;

    // relative spatial orientations between SSEs
    
    public static final Integer SPATREL_NONE = 0;                   // no relation (the SSEs are not in contact)
    public static final Integer SPATREL_MIXED = 1;                  // mixed (can't be determined, msot likely almost orthographic)
    public static final Integer SPATREL_PARALLEL = 2;               // parallel
    public static final Integer SPATREL_ANTIPARALLEL = 3;           // antiparallel
    public static final Integer SPATREL_LIGAND = 4;                 // ligand (unknown / spatial relations not applicable)
    public static final Integer SPATREL_BACKBONE = 5;               // backbone neighbors (sequential N to C terminus)
       
    public static final String SPATREL_STRING_NONE = "-";
    public static final String SPATREL_STRING_MIXED = "m";
    public static final String SPATREL_STRING_PARALLEL = "p";
    public static final String SPATREL_STRING_ANTIPARALLEL = "a";
    public static final String SPATREL_STRING_LIGAND = "l";
    public static final String SPATREL_STRING_BACKBONE = "b";
    
    // connection types between SSEs
    
    public static final Integer BONDPROPERTY_VDW = 0;               // default contact type, determined by computing van-der-Waals radius overlap
    public static final Integer BONDPROPERTY_DISULFIDE = 1;         // disulfide bridge
    public static final Integer BONDPROPERTY_INTERCHAIN = 2;        // interchain contact
        
    public static final String BONDPROPERTY_STRING_VDW = "v";
    public static final String BONDPROPERTY_STRING_DISULFIDE = "s";
    public static final String BONDPROPERTY_STRING_INTERCHAIN = "i";        
    
    private static final Integer numBondTypes = 3;
    
    public PLGEdge() {
        super();
        this.bondProperties = new BitSet(numBondTypes);
        this.bondProperties.set(PLGEdge.BONDPROPERTY_VDW);
        this.spatialOrientation = SpatRel.MIXED;
    }
    
    public PLGEdge(int spatialOrientation) {
        super();
        this.bondProperties = new BitSet(numBondTypes);
        this.bondProperties.set(PLGEdge.BONDPROPERTY_VDW);
        this.spatialOrientation = spatialOrientation;
    }

    public int getSpatialOrientation() {
        return spatialOrientation;
    }

    public void setSpatialOrientation(int spatialOrientation) {
        this.spatialOrientation = spatialOrientation;
    }

    public BitSet getBondProperties() {
        return bondProperties;
    }

    public void setBondProperties(BitSet bondProperties) {
        this.bondProperties = bondProperties;
    }
    
    public void setBondProperty(Integer bondType, Boolean setting) {
        if(bondType < 0 || bondType >= numBondTypes) {
            System.err.println("ERROR: setBondProperty(): Invalid bond type '" + bondType + "' given, ignoring. Must be 0.." + (numBondTypes - 1) + ".");
            return;
        } else {
            this.bondProperties.set(bondType, setting);
        }
    }
    
    public Boolean getBondProperty(Integer bondType) {
        if(bondType < 0 || bondType >= numBondTypes) {
            System.err.println("ERROR: getBondProperty(): Invalid bond type '" + bondType + "' given, ignoring. Must be 0.." + (numBondTypes - 1) + ".");
            return false;
        } else {
            return this.bondProperties.get(bondType);
        }
    }
    
    
    @Override
    public String toString() {
      return "[[E]" + this.getSource() + "-" + PLGEdge.getSpatRelString(this.spatialOrientation) + "-" + this.getTarget() + "]";
    }
    
    
    /** 
     * Returns the String representation for a contact with Integer id 'i'. Each string representation is a single
     * lowercase letter, e.g. "m" for 1 (meaning 'mixed').
     */
    public static String getSpatRelString(Integer i) {
        if(i.equals(PLGEdge.SPATREL_NONE)) {
            return(PLGEdge.SPATREL_STRING_NONE);
        }
        else if(i.equals(PLGEdge.SPATREL_MIXED)) {
            return(PLGEdge.SPATREL_STRING_MIXED);
        }
        else if(i.equals(PLGEdge.SPATREL_PARALLEL)) {
            return(PLGEdge.SPATREL_STRING_PARALLEL);
        }
        else if(i.equals(PLGEdge.SPATREL_ANTIPARALLEL)) {
            return(PLGEdge.SPATREL_STRING_ANTIPARALLEL);
        }
        else if(i.equals(PLGEdge.SPATREL_LIGAND)) {
            return(PLGEdge.SPATREL_STRING_LIGAND);
        }
        else if(i.equals(PLGEdge.SPATREL_BACKBONE)) {
            return(PLGEdge.SPATREL_STRING_BACKBONE);
        }
        else {
            System.err.println("ERROR: Spatial relation integer " + i + " is invalid. Using default.");
            return(PLGEdge.SPATREL_STRING_MIXED);
        }
    }
    
    /** 
     * Returns the String representation for a contact with bond property ID 'i'. Each string representation is a single
     * lowercase letter, e.g. "s" for "disulfide".
     */
    public static String getBondPropertyString(Integer i) {
        if(i.equals(PLGEdge.BONDPROPERTY_VDW)) {
            return(PLGEdge.BONDPROPERTY_STRING_VDW);
        }
        else if(i.equals(PLGEdge.BONDPROPERTY_DISULFIDE)) {
            return(PLGEdge.BONDPROPERTY_STRING_DISULFIDE);
        }
        else if(i.equals(PLGEdge.BONDPROPERTY_INTERCHAIN)) {
            return(PLGEdge.BONDPROPERTY_STRING_INTERCHAIN);
        }
        else {
            System.err.println("ERROR: Bond property integer " + i + " is invalid. Using default.");
            return(PLGEdge.BONDPROPERTY_STRING_VDW);
        }
    }

    /**
     * Returns the Integer representation of the contact String (e.g., 1 for "m").
     */
    public static Integer getSpatRelInt(String s) {
        if(s.equals(PLGEdge.SPATREL_STRING_NONE)) {
            return(PLGEdge.SPATREL_NONE);
        }
        else if(s.equals(PLGEdge.SPATREL_STRING_MIXED)) {
            return(PLGEdge.SPATREL_MIXED);
        }
        else if(s.equals(PLGEdge.SPATREL_STRING_PARALLEL)) {
            return(PLGEdge.SPATREL_PARALLEL);
        }
        else if(s.equals(PLGEdge.SPATREL_STRING_ANTIPARALLEL)) {
            return(PLGEdge.SPATREL_ANTIPARALLEL);
        }
        else if(s.equals(PLGEdge.SPATREL_STRING_LIGAND)) {
            return(PLGEdge.SPATREL_LIGAND);
        }
        else if(s.equals(PLGEdge.SPATREL_STRING_BACKBONE)) {
            return(PLGEdge.SPATREL_BACKBONE);
        }        
        else {
            System.err.println("ERROR: Spatial relation string '" + s + "' is invalid. Using default.");            
            return(PLGEdge.SPATREL_MIXED);
        }

    }
    
    /**
     * Returns the Integer representation of the bond property string (e.g., 1 for "s", meaning disulfide).
     */
    public static Integer getBondPropertyInt(String s) {
        if(s.equals(PLGEdge.BONDPROPERTY_STRING_VDW)) {
            return(PLGEdge.BONDPROPERTY_VDW);
        }
        else if(s.equals(PLGEdge.BONDPROPERTY_STRING_DISULFIDE)) {
            return(PLGEdge.BONDPROPERTY_DISULFIDE);
        }
        else if(s.equals(PLGEdge.BONDPROPERTY_STRING_INTERCHAIN)) {
            return(PLGEdge.BONDPROPERTY_INTERCHAIN);
        }
        else {
            System.err.println("ERROR: Bond property string '" + s + "' is invalid. Using default.");
            return(PLGEdge.BONDPROPERTY_VDW);
        }

    }
    
}
