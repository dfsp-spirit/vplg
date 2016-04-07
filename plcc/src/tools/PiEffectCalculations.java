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
     * Convertes a angle in radian to degree
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
    
}
