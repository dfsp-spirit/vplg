/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.ArrayList;
import java.util.Arrays;
import proteinstructure.Atom;

/**
 *
 * @author niclas
 */
public class PiEffectCalculations {

    /**
     * Computes Midpoint of given atoms e.g. a aromatic ring
     * @param atoms the list of atoms to consider
     * @return  double array of length 3 describing coords, or null if atom list is empty
     */
    public static double[] calculateMidpointOfAtoms(ArrayList<Atom> atoms) {
        if (! atoms.isEmpty()) {
            double[] ret = new double[3];
            Arrays.fill(ret, 0.0);
            for (Atom k : atoms) {
                ret[0] += k.getCoordX();
                ret[1] += k.getCoordY();
                ret[2] += k.getCoordZ();
            }
            ret[0] = ret[0] / atoms.size();
            ret[1] = ret[1] / atoms.size();
            ret[2] = ret[2] / atoms.size();
            
            /*
            Alternative midpoint calculation. Calculate the centroid of the polygon.
            In this case the polygon (aromatic ring) is considered planar. With this it is
            easy to calculate the centroid. The z-coordinate for the centroid is calculated
            as the average of all z-coordinates from the atoms that are part of the ring.
            For testing purposes only right now.
            */
            double area = 0;
            double xs = 0;
            double ys = 0;
            double zs = 0;
            
            ArrayList<Atom> modifiedAtomList = atoms;
            modifiedAtomList.add(atoms.get(0));
            
            
            // area of the polygon calculated with the Shoelace formula
            for (int x = 0; x < modifiedAtomList.size() - 1; x++) {
                area += (modifiedAtomList.get(x).getCoordX() * modifiedAtomList.get(x + 1).getCoordY() - modifiedAtomList.get(x + 1).getCoordX() * modifiedAtomList.get(x).getCoordY());
            }
            area = 0.5 * area;
            
            // x coordinate of the centroid 
            for (int x = 0; x < modifiedAtomList.size() - 1; x++) {
                xs += (modifiedAtomList.get(x).getCoordX() + modifiedAtomList.get(x + 1).getCoordX()) * 
                        (modifiedAtomList.get(x).getCoordX() * modifiedAtomList.get(x + 1).getCoordY() - modifiedAtomList.get(x + 1).getCoordX() * modifiedAtomList.get(x).getCoordY());
            }
            xs = (1/(6 * area)) * xs;
            
            
            // y coordinate of the centroid
            for (int x = 0; x < modifiedAtomList.size() - 1; x++) {
                ys += (modifiedAtomList.get(x).getCoordY() + modifiedAtomList.get(x + 1).getCoordY()) * 
                        (modifiedAtomList.get(x).getCoordX() * modifiedAtomList.get(x + 1).getCoordY() - modifiedAtomList.get(x + 1).getCoordX() * modifiedAtomList.get(x).getCoordY());
            }
            ys = (1/(6 * area)) * ys;
            
            for (Atom a : atoms) {
                zs += a.getCoordZ();
            }
            zs = zs / atoms.size();
            
            
            System.out.println("!!! MP: " + String.valueOf(ret[0]) + "," + String.valueOf(ret[1]) + "," + String.valueOf(ret[2]) + "\n");
            System.out.println("!!! CT: " + String.valueOf(xs) + "," + String.valueOf(ys) + "," + String.valueOf(zs) + "\n");
            
            return ret;
        }
        else {
            return null;
        }
    }

    /**
     * Computes the angle between two vectors in 3D
     * @param vectorA is a double array of length 3
     * @param vectorB is a double array of length 3
     * @return double angle as radian, or NaN if at least one vector contains less than 3 elements
     */
    public static double calculateAngleBetw3DVecs(double[] vectorA, double[] vectorB) {
        if (vectorA.length < 3 || vectorB.length < 3) {
            return Double.NaN;
        }
        else if (vectorA.length > 3 || vectorB.length > 3) {
            DP.getInstance().w("PiEffectCalculations", "At least one spanning vector for calculation of normal of plane contained more "
                    + "than three coordinates. Only the first three coordinates were used for computation.");
        }
        double angle;
        angle = vectorA[0] * vectorB[0] + vectorA[1] * vectorB[1] + vectorA[2] * vectorB[2];
        angle = angle / (Math.sqrt(Math.pow(vectorA[0], 2) + Math.pow(vectorA[1], 2) + Math.pow(vectorA[2], 2)) * Math.sqrt(Math.pow(vectorB[0], 2) + Math.pow(vectorB[1], 2) + Math.pow(vectorB[2], 2)));
        angle = Math.acos(angle);
        return angle;
    }

    /**
     * Computes the normal of a plane
     * @param spanningVectorA is a double array of length 3
     * @param spanningVectorB is a double array of length 3
     * @return  double array of length 3 describing the normal, or null if at least one spanning vector contains less than 3 elements
     */
    public static double[] calculateNormalOfPlane(double[] spanningVectorA, double[] spanningVectorB) {
        if (spanningVectorA.length < 3 || spanningVectorB.length < 3) {
            return null;
        }
        else if (spanningVectorA.length > 3 || spanningVectorB.length > 3) {
            DP.getInstance().w("PiEffectCalculations", "At least one spanning vector for calculation of normal of plane contained more "
                    + "than three coordinates. Only the first three coordinates were used for computation.");
        }
        double[] normal = new double[3];
        normal[0] = (spanningVectorA[1] * spanningVectorB[2]) - (spanningVectorA[2] * spanningVectorB[1]);
        normal[1] = (spanningVectorA[2] * spanningVectorB[0]) - (spanningVectorA[0] * spanningVectorB[2]);
        normal[2] = (spanningVectorA[0] * spanningVectorB[1]) - (spanningVectorA[1] * spanningVectorB[0]);
        return normal;
    }

    /**
     * Computes the distance from an atom to a point in 3D
     * @param atom the atom that is considered
     * @param point double array of length 3 containing the coordinates of the considered point
     * @return double distance as 10th of Angstrom, or NaN if point has less than 3 coordinates
     */
    public static double calculateDistanceAtomToPoint(Atom atom, double[] point) {
        if (point.length < 3) {
            return Double.NaN;
        }
        else if (point.length > 3) {
            DP.getInstance().w("PiEffectsCalculations", "The given point contained more than three coordinates for calculation of distance "
                    + "between atom and point. Only the first three coordinates were used for computation.");
        }
        double ret;
        ret = Math.sqrt(Math.pow(atom.getCoordX() - point[0], 2) + Math.pow(atom.getCoordY() - point[1], 2) + Math.pow(atom.getCoordZ() - point[2], 2));
        return ret;
    }

    /**
     * Converts a angle in radian to degree
     * @param rad double angle in radian
     * @return double angle in degree
     */
    public static double converteRadianToDegree(double rad) {
        if (Double.isNaN(rad)) {
            return Double.NaN;
        }
        double deg;
        deg = (360 / (2 * Math.PI)) * rad;
        return deg;
    }
    
    /**
     * Checks if the normal vector needs to be flipped (MAY  BE REMOVED)
     * @param normal double array of length 3 containing the coordinates of the normal
     * @param ringMidKoord double array of length 3 containing the coordinates of the midpoint of the ring
     * @param h Atom Hydrogen bound to X in calculation of pi effects (used for distance calculation)
     * @return the normal pointing towards H as double array of length 3
     */
    public static double[] checkDirectionNormal(double[] normal, double[] ringMidKoord, Atom h) {
        //test: increase the length of the normal vector
        normal[0] *= 10;
        normal[1] *= 10;
        normal[2] *= 10;
        
        double[] normalEndpoint =  new double[3];
        normalEndpoint[0] = ringMidKoord[0] + normal[0];
        normalEndpoint[1] = ringMidKoord[1] + normal[1];
        normalEndpoint[2] = ringMidKoord[2] + normal[2];

        double[] flippedNormalEndpoint = new double[3];
        flippedNormalEndpoint[0] = ringMidKoord[0] - normal[0];
        flippedNormalEndpoint[1] = ringMidKoord[1] - normal[1];
        flippedNormalEndpoint[2] = ringMidKoord[2] - normal[2];

        if (Math.sqrt(Math.pow(normalEndpoint[0] - h.getCoordX(), 2) + Math.pow(normalEndpoint[1] - h.getCoordY(), 2) + Math.pow(normalEndpoint[2] - h.getCoordZ() , 2)) > Math.sqrt(Math.pow(flippedNormalEndpoint[0] - h.getCoordX(), 2) + Math.pow(flippedNormalEndpoint[1] - h.getCoordY(), 2) + Math.pow(flippedNormalEndpoint[2] - h.getCoordZ(), 2))) {
            normal[0] *= -1;
            normal[1] *= -1;
            normal[2] *= -1;
            
            //DEBUG needs to be removed
            //System.out.println("FLIPPED NORMAL VECTOR (debug only)");
        } else {
            //DEBUG
            //System.out.println("NO NEED TO FLIP NORMAL");
        }
        
        return normal;
    }
    
    /**
     * Gives the Atom number to a the name of an Arg H
     * @param h Atom hydrogen
     * @return int
     */
    public static int giveAtomNumOfNBondToArgH(Atom h) {
        if (h.getAtomName().contains("HE")) {
            return 7;
        }
        else if (h.getAtomName().contains("HH1")) {
            return 9;
        }
        else if (h.getAtomName().contains("HH2")) {
            return 10;
        }
        DP.getInstance().e("PiEffectCalculations", "Hydrogen of Arginin had an uncommon name. Not expected to happen. Please check your PDB file "
                + "or report back to the code authors. Program may crash.");
        return -1;
    }
    
}
