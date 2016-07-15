/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;
import proteinstructure.Atom;

/**
 *
 * @author niclas, andreas
 */
public class PiEffectCalculations {

    /**
     * Computes the Midpoint (Centroid) of aromatic 5-/6-rings.
     * @param atoms the list of atoms to consider
     * @return  double array of length 3 describing coords, or null if atom list is empty
     */
    public static double[] calculateMidpointOfAtoms(ArrayList<Atom> atoms) {
        if (! atoms.isEmpty()) {
            double[] c = new double[3];
            
            if(atoms.get(0).getResidue().getResName3().equals("TRP")) {
                if (atoms.size() == 5) {
                    double[] r0 = new double[3];
                    r0[0] = (atoms.get(1).getCoordX() + atoms.get(2).getCoordX() + atoms.get(4).getCoordX()) / 3;
                    r0[1] = (atoms.get(1).getCoordY() + atoms.get(2).getCoordY() + atoms.get(4).getCoordY()) / 3;
                    r0[2] = (atoms.get(1).getCoordZ() + atoms.get(2).getCoordZ() + atoms.get(4).getCoordZ()) / 3;

                    double[] r1 = new double[3];
                    r1[0] = (atoms.get(0).getCoordX() + atoms.get(2).getCoordX() + atoms.get(1).getCoordX()) / 3;
                    r1[1] = (atoms.get(0).getCoordY() + atoms.get(2).getCoordY() + atoms.get(1).getCoordY()) / 3;
                    r1[2] = (atoms.get(0).getCoordZ() + atoms.get(2).getCoordZ() + atoms.get(1).getCoordZ()) / 3;

                    double[] r2 = new double[3];
                    r2[0] = (atoms.get(1).getCoordX() + atoms.get(4).getCoordX() + atoms.get(3).getCoordX()) / 3;
                    r2[1] = (atoms.get(1).getCoordY() + atoms.get(4).getCoordY() + atoms.get(3).getCoordY()) / 3;
                    r2[2] = (atoms.get(1).getCoordZ() + atoms.get(4).getCoordZ() + atoms.get(3).getCoordZ()) / 3;

                    
                    double a0 = Math.sqrt(Math.pow(((atoms.get(2).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(4).getCoordZ() - atoms.get(1).getCoordZ()) - (atoms.get(2).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(4).getCoordY() - atoms.get(1).getCoordY())), 2) + 
                                Math.pow(((atoms.get(2).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(4).getCoordX() - atoms.get(1).getCoordX()) - (atoms.get(2).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(4).getCoordZ() - atoms.get(1).getCoordZ())), 2) + 
                                Math.pow(((atoms.get(2).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(4).getCoordY() - atoms.get(1).getCoordY()) - (atoms.get(2).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(4).getCoordX() - atoms.get(1).getCoordX())), 2));
                    
                    double a1 = Math.sqrt(Math.pow(((atoms.get(2).getCoordY() - atoms.get(0).getCoordY()) * (atoms.get(1).getCoordZ() - atoms.get(0).getCoordZ()) - (atoms.get(2).getCoordZ() - atoms.get(0).getCoordZ()) * (atoms.get(1).getCoordY() - atoms.get(0).getCoordY())), 2) + 
                                Math.pow(((atoms.get(2).getCoordZ() - atoms.get(0).getCoordZ()) * (atoms.get(1).getCoordX() - atoms.get(0).getCoordX()) - (atoms.get(2).getCoordX() - atoms.get(0).getCoordX()) * (atoms.get(1).getCoordZ() - atoms.get(0).getCoordZ())), 2) + 
                                Math.pow(((atoms.get(2).getCoordX() - atoms.get(0).getCoordX()) * (atoms.get(1).getCoordY() - atoms.get(0).getCoordY()) - (atoms.get(2).getCoordY() - atoms.get(0).getCoordY()) * (atoms.get(1).getCoordX() - atoms.get(0).getCoordX())), 2));
                    
                    double a2 = Math.sqrt(Math.pow(((atoms.get(1).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(3).getCoordZ() - atoms.get(1).getCoordZ()) - (atoms.get(1).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(3).getCoordY() - atoms.get(1).getCoordY())), 2) + 
                                Math.pow(((atoms.get(1).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(3).getCoordX() - atoms.get(1).getCoordX()) - (atoms.get(1).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(3).getCoordZ() - atoms.get(1).getCoordZ())), 2) + 
                                Math.pow(((atoms.get(1).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(3).getCoordY() - atoms.get(1).getCoordY()) - (atoms.get(1).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(3).getCoordX() - atoms.get(1).getCoordX())), 2));


                    double a = a0 + a1 + a2;

                    c[0] = (r0[0] * a0 / a) + (r1[0] * a1 / a) + (r2[0] * a2 / a);
                    c[1] = (r0[1] * a0 / a) + (r1[1] * a1 / a) + (r2[1] * a2 / a);
                    c[2] = (r0[2] * a0 / a) + (r1[2] * a1 / a) + (r2[2] * a2 / a);
                    
                }
                else if (atoms.size() == 6) {
                    double[] r0 = new double[3];
                    r0[0] = (atoms.get(0).getCoordX() + atoms.get(2).getCoordX() + atoms.get(1).getCoordX()) / 3;
                    r0[1] = (atoms.get(0).getCoordY() + atoms.get(2).getCoordY() + atoms.get(1).getCoordY()) / 3;
                    r0[2] = (atoms.get(0).getCoordZ() + atoms.get(2).getCoordZ() + atoms.get(1).getCoordZ()) / 3;

                    double[] r1 = new double[3];
                    r1[0] = (atoms.get(1).getCoordX() + atoms.get(2).getCoordX() + atoms.get(3).getCoordX()) / 3;
                    r1[1] = (atoms.get(1).getCoordY() + atoms.get(2).getCoordY() + atoms.get(3).getCoordY()) / 3;
                    r1[2] = (atoms.get(1).getCoordZ() + atoms.get(2).getCoordZ() + atoms.get(3).getCoordZ()) / 3;

                    double[] r2 = new double[3];
                    r2[0] = (atoms.get(1).getCoordX() + atoms.get(4).getCoordX() + atoms.get(3).getCoordX()) / 3;
                    r2[1] = (atoms.get(1).getCoordY() + atoms.get(4).getCoordY() + atoms.get(3).getCoordY()) / 3;
                    r2[2] = (atoms.get(1).getCoordZ() + atoms.get(4).getCoordZ() + atoms.get(3).getCoordZ()) / 3;

                    double[] r3 = new double[3];
                    r3[0] = (atoms.get(3).getCoordX() + atoms.get(4).getCoordX() + atoms.get(5).getCoordX()) / 3;
                    r3[1] = (atoms.get(3).getCoordY() + atoms.get(4).getCoordY() + atoms.get(5).getCoordY()) / 3;
                    r3[2] = (atoms.get(3).getCoordZ() + atoms.get(4).getCoordZ() + atoms.get(5).getCoordZ()) / 3;

                    
                    double a0 = Math.sqrt(Math.pow(((atoms.get(2).getCoordY() - atoms.get(0).getCoordY()) * (atoms.get(1).getCoordZ() - atoms.get(0).getCoordZ()) - (atoms.get(2).getCoordZ() - atoms.get(0).getCoordZ()) * (atoms.get(1).getCoordY() - atoms.get(0).getCoordY())), 2) + 
                                Math.pow(((atoms.get(2).getCoordZ() - atoms.get(0).getCoordZ()) * (atoms.get(1).getCoordX() - atoms.get(0).getCoordX()) - (atoms.get(2).getCoordX() - atoms.get(0).getCoordX()) * (atoms.get(1).getCoordZ() - atoms.get(0).getCoordZ())), 2) + 
                                Math.pow(((atoms.get(2).getCoordX() - atoms.get(0).getCoordX()) * (atoms.get(1).getCoordY() - atoms.get(0).getCoordY()) - (atoms.get(2).getCoordY() - atoms.get(0).getCoordY()) * (atoms.get(1).getCoordX() - atoms.get(0).getCoordX())), 2));
                    
                    double a1 = Math.sqrt(Math.pow(((atoms.get(2).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(3).getCoordZ() - atoms.get(1).getCoordZ()) - (atoms.get(2).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(3).getCoordY() - atoms.get(1).getCoordY())), 2) + 
                                Math.pow(((atoms.get(2).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(3).getCoordX() - atoms.get(1).getCoordX()) - (atoms.get(2).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(3).getCoordZ() - atoms.get(1).getCoordZ())), 2) + 
                                Math.pow(((atoms.get(2).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(3).getCoordY() - atoms.get(1).getCoordY()) - (atoms.get(2).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(3).getCoordX() - atoms.get(1).getCoordX())), 2));
                    
                    double a2 = Math.sqrt(Math.pow(((atoms.get(4).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(3).getCoordZ() - atoms.get(1).getCoordZ()) - (atoms.get(4).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(3).getCoordY() - atoms.get(1).getCoordY())), 2) + 
                                Math.pow(((atoms.get(4).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(3).getCoordX() - atoms.get(1).getCoordX()) - (atoms.get(4).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(3).getCoordZ() - atoms.get(1).getCoordZ())), 2) + 
                                Math.pow(((atoms.get(4).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(3).getCoordY() - atoms.get(1).getCoordY()) - (atoms.get(4).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(3).getCoordX() - atoms.get(1).getCoordX())), 2));
                    
                    double a3 = Math.sqrt(Math.pow(((atoms.get(4).getCoordY() - atoms.get(3).getCoordY()) * (atoms.get(4).getCoordZ() - atoms.get(3).getCoordZ()) - (atoms.get(4).getCoordZ() - atoms.get(3).getCoordZ()) * (atoms.get(4).getCoordY() - atoms.get(3).getCoordY())), 2) + 
                                Math.pow(((atoms.get(4).getCoordZ() - atoms.get(3).getCoordZ()) * (atoms.get(4).getCoordX() - atoms.get(3).getCoordX()) - (atoms.get(4).getCoordX() - atoms.get(3).getCoordX()) * (atoms.get(4).getCoordZ() - atoms.get(3).getCoordZ())), 2) + 
                                Math.pow(((atoms.get(4).getCoordX() - atoms.get(3).getCoordX()) * (atoms.get(4).getCoordY() - atoms.get(3).getCoordY()) - (atoms.get(4).getCoordY() - atoms.get(3).getCoordY()) * (atoms.get(4).getCoordX() - atoms.get(3).getCoordX())), 2));


                    double a = a0 + a1 + a2 + a3;

                    c[0] = (r0[0] * a0 / a) + (r1[0] * a1 / a) + (r2[0] * a2 / a) + (r3[0] * a3 / a);
                    c[1] = (r0[1] * a0 / a) + (r1[1] * a1 / a) + (r2[1] * a2 / a) + (r3[1] * a3 / a);
                    c[2] = (r0[2] * a0 / a) + (r1[2] * a1 / a) + (r2[2] * a2 / a) + (r3[2] * a3 / a);
                    
                }
                
            }
            else {
                double[] r0 = new double[3];
                r0[0] = (atoms.get(0).getCoordX() + atoms.get(1).getCoordX() + atoms.get(2).getCoordX()) / 3;
                r0[1] = (atoms.get(0).getCoordY() + atoms.get(1).getCoordY() + atoms.get(2).getCoordY()) / 3;
                r0[2] = (atoms.get(0).getCoordZ() + atoms.get(1).getCoordZ() + atoms.get(2).getCoordZ()) / 3;
                
                double[] r1 = new double[3];
                r1[0] = (atoms.get(1).getCoordX() + atoms.get(3).getCoordX() + atoms.get(2).getCoordX()) / 3;
                r1[1] = (atoms.get(1).getCoordY() + atoms.get(3).getCoordY() + atoms.get(2).getCoordY()) / 3;
                r1[2] = (atoms.get(1).getCoordZ() + atoms.get(3).getCoordZ() + atoms.get(2).getCoordZ()) / 3;
                
                double[] r2 = new double[3];
                r2[0] = (atoms.get(1).getCoordX() + atoms.get(3).getCoordX() + atoms.get(4).getCoordX()) / 3;
                r2[1] = (atoms.get(1).getCoordY() + atoms.get(3).getCoordY() + atoms.get(4).getCoordY()) / 3;
                r2[2] = (atoms.get(1).getCoordZ() + atoms.get(3).getCoordZ() + atoms.get(4).getCoordZ()) / 3;
                
                double[] r3 = new double[3];
                r3[0] = (atoms.get(3).getCoordX() + atoms.get(5).getCoordX() + atoms.get(4).getCoordX()) / 3;
                r3[1] = (atoms.get(3).getCoordY() + atoms.get(5).getCoordY() + atoms.get(4).getCoordY()) / 3;
                r3[2] = (atoms.get(3).getCoordZ() + atoms.get(5).getCoordZ() + atoms.get(4).getCoordZ()) / 3;
                
                
                double a0 = Math.sqrt(Math.pow(((atoms.get(1).getCoordY() - atoms.get(0).getCoordY()) * (atoms.get(2).getCoordZ() - atoms.get(0).getCoordZ()) - (atoms.get(1).getCoordZ() - atoms.get(0).getCoordZ()) * (atoms.get(2).getCoordY() - atoms.get(0).getCoordY())), 2) + 
                            Math.pow(((atoms.get(1).getCoordZ() - atoms.get(0).getCoordZ()) * (atoms.get(2).getCoordX() - atoms.get(0).getCoordX()) - (atoms.get(1).getCoordX() - atoms.get(0).getCoordX()) * (atoms.get(2).getCoordZ() - atoms.get(0).getCoordZ())), 2) + 
                            Math.pow(((atoms.get(1).getCoordX() - atoms.get(0).getCoordX()) * (atoms.get(2).getCoordY() - atoms.get(0).getCoordY()) - (atoms.get(1).getCoordY() - atoms.get(0).getCoordY()) * (atoms.get(2).getCoordX() - atoms.get(0).getCoordX())), 2));
                
                double a1 = Math.sqrt(Math.pow(((atoms.get(3).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(2).getCoordZ() - atoms.get(1).getCoordZ()) - (atoms.get(3).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(2).getCoordY() - atoms.get(1).getCoordY())), 2) + 
                            Math.pow(((atoms.get(3).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(2).getCoordX() - atoms.get(1).getCoordX()) - (atoms.get(3).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(2).getCoordZ() - atoms.get(1).getCoordZ())), 2) + 
                            Math.pow(((atoms.get(3).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(2).getCoordY() - atoms.get(1).getCoordY()) - (atoms.get(3).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(2).getCoordX() - atoms.get(1).getCoordX())), 2));
                
                double a2 = Math.sqrt(Math.pow(((atoms.get(3).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(4).getCoordZ() - atoms.get(1).getCoordZ()) - (atoms.get(3).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(4).getCoordY() - atoms.get(1).getCoordY())), 2) + 
                            Math.pow(((atoms.get(3).getCoordZ() - atoms.get(1).getCoordZ()) * (atoms.get(4).getCoordX() - atoms.get(1).getCoordX()) - (atoms.get(3).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(4).getCoordZ() - atoms.get(1).getCoordZ())), 2) + 
                            Math.pow(((atoms.get(3).getCoordX() - atoms.get(1).getCoordX()) * (atoms.get(4).getCoordY() - atoms.get(1).getCoordY()) - (atoms.get(3).getCoordY() - atoms.get(1).getCoordY()) * (atoms.get(4).getCoordX() - atoms.get(1).getCoordX())), 2));
                
                double a3 = Math.sqrt(Math.pow(((atoms.get(5).getCoordY() - atoms.get(3).getCoordY()) * (atoms.get(4).getCoordZ() - atoms.get(3).getCoordZ()) - (atoms.get(5).getCoordZ() - atoms.get(3).getCoordZ()) * (atoms.get(4).getCoordY() - atoms.get(3).getCoordY())), 2) + 
                            Math.pow(((atoms.get(5).getCoordZ() - atoms.get(3).getCoordZ()) * (atoms.get(4).getCoordX() - atoms.get(3).getCoordX()) - (atoms.get(5).getCoordX() - atoms.get(3).getCoordX()) * (atoms.get(4).getCoordZ() - atoms.get(3).getCoordZ())), 2) + 
                            Math.pow(((atoms.get(5).getCoordX() - atoms.get(3).getCoordX()) * (atoms.get(4).getCoordY() - atoms.get(3).getCoordY()) - (atoms.get(5).getCoordY() - atoms.get(3).getCoordY()) * (atoms.get(4).getCoordX() - atoms.get(3).getCoordX())), 2));
                
                
                double a = a0 + a1 + a2 + a3;

                c[0] = (r0[0] * a0 / a) + (r1[0] * a1 / a) + (r2[0] * a2 / a) + (r3[0] * a3 / a);
                c[1] = (r0[1] * a0 / a) + (r1[1] * a1 / a) + (r2[1] * a2 / a) + (r3[1] * a3 / a);
                c[2] = (r0[2] * a0 / a) + (r1[2] * a1 / a) + (r2[2] * a2 / a) + (r3[2] * a3 / a);
            }
            
            //System.out.println("!!! MP: " + String.valueOf(ret[0]) + "," + String.valueOf(ret[1]) + "," + String.valueOf(ret[2]));
            //System.out.println("!!! CT: " + String.valueOf(c[0]) + "," + String.valueOf(c[1]) + "," + String.valueOf(c[2]));
            return c;
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
   
    /**
     * Estimates the planarity of an aromatic ring by calculating the RMSD of all
     * atoms to a least squares plane fitted to the atoms.
     * A least squares plane will be fitted to the supplied atoms and the RMSD
     * from all atoms to this plane will be calculated. The RMSD value serves as
     * an estimater for planarity, where perfect planarity would correspond to
     * a RMSD value of zero.
     * The least squares plane calculation is based on:
     * - D. M. Blow (1960). "To Fit a Plane to a Set of Points by Least Squares." Acta Cryst., 13, 168.
     * - V. Schomaker et al. (1959). "To Fit a Plane or a Line to a Set of Points by Least Squares." Acta Cryst., 12, 600-604.
     * @param atoms List of atoms forming the aromatic ring to be checked for planarity.
     * @return 
     */
    public static double calculateAromaticRingPlanarity(ArrayList<Atom> atoms) {
        
        // we need a point to lie in the plane; one point that fullfills this criterion 
        // is the geometric average of the atoms
        
        double[] geoAvg = new double[3];
        if (! atoms.isEmpty()) {
            Arrays.fill(geoAvg, 0.0);
            for (Atom k : atoms) {
                geoAvg[0] += k.getCoordX();
                geoAvg[1] += k.getCoordY();
                geoAvg[2] += k.getCoordZ();
            }
            geoAvg[0] = geoAvg[0] / atoms.size();
            geoAvg[1] = geoAvg[1] / atoms.size();
            geoAvg[2] = geoAvg[2] / atoms.size();
        }
        else {
            DP.getInstance().w("The list of atoms is empty. Nothing to calculate.");
            return -1;
        }

        // get all x,y,z coordinates from the atoms
        double[][] atomCoords = new double[atoms.size()][3];
        for(int i = 0; i < atoms.size(); i++) {
            atomCoords[i][0] = atoms.get(i).getCoordX();
            atomCoords[i][1] = atoms.get(i).getCoordY();
            atomCoords[i][2] = atoms.get(i).getCoordZ();
        }
        
        RealMatrix covarianceMatrix = new Covariance(atomCoords).getCovarianceMatrix();
        
        // perform eigendecomposition of the covariance matrix to get the eigenvalues and eigenvectors
        EigenDecomposition eigendecomp = new EigenDecomposition(covarianceMatrix);
        double[] eigenvalues = eigendecomp.getRealEigenvalues();
        
        // the smallest eigenvalue corresponds to the best plane
        double minEigenvalue = eigenvalues[0];
        RealVector minEigenvector = eigendecomp.getEigenvector(0);
        
        for(int x = 1; x < eigenvalues.length; x++) {
            if(eigenvalues[x] < minEigenvalue) {
                minEigenvalue = eigenvalues[x];
                minEigenvector = eigendecomp.getEigenvector(x);
            }
        }
        
        double originToPlaneDistance = minEigenvector.getEntry(0) * geoAvg[0] + minEigenvector.getEntry(1) * geoAvg[1] + minEigenvector.getEntry(2) * geoAvg[2];
        
        double rmsd = 0;
        for(Atom atom : atoms) {
            rmsd += Math.pow(minEigenvector.getEntry(0) * atom.getCoordX() + minEigenvector.getEntry(1) * atom.getCoordY() + minEigenvector.getEntry(2) * atom.getCoordZ() - originToPlaneDistance, 2);
        }
        rmsd = Math.sqrt(rmsd/atoms.size());
        return rmsd;
    }
}