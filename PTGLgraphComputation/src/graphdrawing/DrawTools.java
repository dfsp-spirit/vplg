/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012 - 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package graphdrawing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import proteingraphs.FoldingGraph;
import proteingraphs.Position2D;
import settings.Settings;
import tools.DP;

/**
 *
 * @author ts
 */
public class DrawTools {
    
    public static final String FORMAT_PNG = "PNG";
    public static final String FORMAT_SVG = "SVG";
    public static final String FORMAT_TIFF = "TIFF";
    public static final String FORMAT_PDF = "PDF";
    public static final String FORMAT_JPEG = "JPEG";
    
    //public static final Integer DIRECTION_UPWARDS = 0;
    //public static final Integer DIRECTION_DOWNWARDS = 1;
    
    public static final Integer ORIENTATION_ABOVE = 0;
    public static final Integer ORIENTATION_RIGHT_OF = 1;
    public static final Integer ORIENTATION_BELOW = 2;
    public static final Integer ORIENTATION_LEFT_OF = 3;
    
    public static final String DEFAULT_FORMAT_BITMAP = FORMAT_PNG;
    public static final String DEFAULT_FORMAT_VECTOR = FORMAT_SVG;
    
    public static final String[] ALL_IMAGE_FORMATS = new String[] { FORMAT_PNG, FORMAT_SVG, FORMAT_TIFF, FORMAT_PDF, FORMAT_JPEG };

    /**
     * This function creates a connector between the 2D points (startX, startY) and (targetX, targetY). This connector is returned as a list of Shape
     * objects that can be painted on a Graphics2D canvas by stroking or filling them (e.g., G2Dinstance.fill(shapeInstance) or similar). The shapes
     * are created using the given Stroke (call G2Dinstance.getStroke() to use the current Stroke).
     *
     * NOTE: This is an alternate version, favored by Ina. The Crossover connectors of this version will cut through the other SSE symbols. See the
     * Javadoc of the getCrossoverArcConnectorAlternative() function for details.
     *
     * If the start and end points are on the same height (i.e., startY == targetY), the connector will look
     * similar to an 'S' and will consist of a single shape (an arc forming a half circle). Otherwise it will
     * consist of 3 Shapes (two arcs and a line).
     *
     * You can choose whether the connector should start upwards or downwards from (startX, startY) using the startUpwards parameter.
     *
     * @param startX the x coordinate of the start point
     * @param startY the y coordinate of the start point
     * @param targetX the x coordinate of the end point
     * @param targetY the y coordinate of the end point
     * @param stroke the Stroke to use. You can get one from your Graphics2D instance.
     * @param startUpwardsInCaseOfSimpleArc whether to start upwards from the 2D Point (startX, startY). If this is false, downwards is used instead.
     * @param pixelsToShiftCentralLineOnYAxis the number of pixels to shift the central line on the y axis. Can be positive (for shift to the right) or negative (shift to the left), but must NOT be larger than 1/2 of the distance between the y axis start and end pixels of this connector.
     * @return a list of Shapes that can be painted on a G2D canvas.
     */
    public static ArrayList<Shape> getArcConnectorAlternative(Integer startX, Integer startY, Integer targetX, Integer targetY, Stroke stroke, Boolean startUpwardsInCaseOfSimpleArc, int pixelsToShiftCentralLineOnYAxis) {
        if (startY.equals(targetY)) {
            return getSimpleArcConnector(startX, startY, targetX, stroke, startUpwardsInCaseOfSimpleArc);
        } else {
            Boolean computedStartUpwards;
            if (startY < targetY) {
                computedStartUpwards = true;
            } else {
                computedStartUpwards = false;
            }
            if (!Objects.equals(computedStartUpwards, startUpwardsInCaseOfSimpleArc)) {
                System.err.println("DrawTools: WARNING: You startUpwards value " + startUpwardsInCaseOfSimpleArc.toString() + " for the crossover arc from (" + startX + "," + startY + ") to (" + targetX + "," + targetY + ") seems questionable.");
            }
            return getCrossoverArcConnectorAlternativeBezierVersion(startX, startY, targetX, targetY, stroke, computedStartUpwards, 0);
            //return getCrossoverArcConnectorAlternative(startX, startY, targetX, targetY, stroke, startUpwardsInCaseOfSimpleArc, pixelsToShiftCentralLineOnYAxis);
        }
    }
    
    public static void main(String [] args) {
        Settings.init();
        System.out.println("Testing drawing functions...");
        DrawTools.drawTest("test");
        System.out.println("Done.");
    }

   
    /**
     *
     * @param x the value of x
     * @param y the value of y
     * @param direction the value of direction, use FoldingGraph.ORIENTATION_*
     */
    protected static Polygon getDefaultArrowPolygonLowestPointAt(int x, int y, Integer direction) {
        if (direction.equals(FoldingGraph.ORIENTATION_UPWARDS)) {
            return getDefaultArrowPolygonUpwardsLowestPointAt(x, y);
        } else if (direction.equals(FoldingGraph.ORIENTATION_DOWNWARDS)) {
            return getDefaultArrowPolygonDownwardsLowestPointAt(x, y);
        } else {
            System.err.println("DrawTools: getDefaultArrowPolygonLowestPointAt(): Invalid direction given: '" + direction + "'.");
            return null;
        }
    }

    /**
     * Draws a grid starting at upperLeftPos
     *
     * @param ig2
     * @param upperLeftPos start position
     * @param cellWidth width of a grid cell
     * @param cellHeight height of a grid cell
     * @param cellsPerRow number of cells in a row
     * @param cellsPerColumn number of cells in a column
     */
    private static void drawGrid(SVGGraphics2D ig2, Position2D upperLeftPos, Integer cellWidth, Integer cellHeight, Integer cellsPerRow, Integer cellsPerColumn) {
        Shape line;
        for (int i = 0; i <= cellsPerColumn; i++) {
            line = new Line2D.Double(upperLeftPos.x, upperLeftPos.y + i * cellHeight, upperLeftPos.x + (cellsPerRow * cellWidth), upperLeftPos.y + i * cellHeight);
            ig2.draw(line);
        }
        for (int i = 0; i <= cellsPerRow; i++) {
            line = new Line2D.Double(upperLeftPos.x + i * cellWidth, upperLeftPos.y, upperLeftPos.x + (i * cellWidth), upperLeftPos.y + cellsPerColumn * cellHeight);
            ig2.draw(line);
        }
    }

    /**
     *
     * @param baseFile the value of baseFile
     */
    public static void drawTest(String baseFile) {
        String svgFilePath = baseFile + ".svg";
        DrawResult drawRes = DrawTools.drawTestG2D();
        try {
            DrawTools.writeG2dToSVGFile(svgFilePath, drawRes);
            System.out.println("Test image file written to '" + new File(svgFilePath).getAbsolutePath() + "'.");
        } catch (IOException ex) {
            System.err.println("Could not write test image file to '" + new File(svgFilePath).getAbsolutePath() + "': '" + ex.getMessage() + "'.");
        }
    }
    
    /**
     * Just a helper function that sets default values for the width of the arrow. See the first 3 parameters of the drawOutlinedArrow() function for parameter explanation.
     *
     * @param x the x position
     * @param y the y position (coord)
     * @return the polygon
     */
    protected static Polygon getDefaultArrowPolygonDownwardsLowestPointAt(int x, int y) {
        int defaultWidthTail = 10;
        int defaultWidthHead = 20;
        int defaultLengthHead = 20;
        int defaultHeight = 80;
        return getArrowPolygonDown(y - defaultHeight, x, y, defaultWidthTail, defaultWidthHead, defaultLengthHead);
    }

    /**
     * Just a helper function that sets default values for the width of the arrow. See the first 3 parameters of the drawOutlinedArrow() function for parameter explanation.
     *
     * @param x the x position
     * @param y the y position
     * @return the polygon
     */
    protected static Polygon getDefaultArrowPolygonUpwardsLowestPointAt(int x, int y) {
        int defaultWidthTail = 10;
        int defaultWidthHead = 20;
        int defaultLengthHead = 20;
        int defaultHeight = 80;
        return getArrowPolygonUp(y - defaultHeight, x, y, defaultWidthTail, defaultWidthHead, defaultLengthHead);
    }

    /**
     * Creates an outlined barrel (4 lines) on the given Graphics2D context, using the Polygon class. The barrel points straight up.
     * You can transform this is you want to change its angle. It's a shape so you can stroke or fill it, too.
     * You should also check out getDefaultBarrelPolygon() for an easy way to get barrels with the same width and length.
     *
     * @param tailX The x location of the center of the "tail" of the barrel
     * @param tailY The y location of the center of the "tail" of the barrel
     * @param headX The x location of the "head" of the barrel
     * @param headY The y location of the "head" of the barrel
     * @param width The width of the barrel
     */
    protected static Polygon getBarrelPolygon(int headX, int headY, int tailX, int tailY, int width) {
        int numPoints = 4;
        int[] xPoints = new int[numPoints];
        int[] yPoints = new int[numPoints];
        xPoints[0] = tailX - (width / 2);
        yPoints[0] = tailY;
        xPoints[1] = tailX + (width / 2);
        yPoints[1] = tailY;
        xPoints[2] = headX + (width / 2);
        yPoints[2] = headY;
        xPoints[3] = headX - (width / 2);
        yPoints[3] = headY;
        return new Polygon(xPoints, yPoints, numPoints);
    }

    /**
     * Just a helper function that sets default values for the width of the barrel.
     * See getBarrelPolygon() for details on the other parameters.
     *
     */
    protected static Polygon getDefaultBarrelPolygon(int headY, int bothX, int tailY) {
        int defaultWidth = 10;
        return getBarrelPolygon(bothX, headY, bothX, tailY, defaultWidth);
    }
    
    protected static Polygon getDefaultBarrelPolygonLowestPointAt(int x, int y) {
        int defaultWidth = 10;
        int defaultHeight = 80;
        return getBarrelPolygon(x, y - defaultHeight, x, y, defaultWidth);
    }

    /**
     * Just a helper function that sets default values for the width of the arrow. See the first 3 parameters of the drawOutlinedArrow() function for parameter explanation.
     *
     * @param headY the Y axis position of the head
     * @param bothX the X axis position (for whole polygon, the arrow is always straight up)
     * @param tailY the Y axis position of the tail
     * @return the polygon
     */
    protected static Polygon getDefaultArrowPolygonUp(int headY, int bothX, int tailY) {
        int defaultWidthTail = 10;
        int defaultWidthHead = 20;
        int defaultLengthHead = 20;
        return getArrowPolygonUp(headY, bothX, tailY, defaultWidthTail, defaultWidthHead, defaultLengthHead);
    }

    /**
     * Creates an outlined arrow (7 lines) on the given Graphics2D context, using the Polygon class. The arrow points straight down, thus headX is = tailX and does not have to be supplied.
     * You can transform this is you want to change its angle. It's a shape so you can stroke or fill it, too.
     * You should also check out getDefaultArrowPolygon() for an easy way to get arrows with the same width, length and head length.
     *
     * @param tailX The x location of the center of the "tail" of the arrow
     * @param tailY The y location of the center of the "tail" of the arrow
     * @param headY The y location of the "head" of the arrow
     * @param widthTail The width of the arrow at the tail
     * @param widthHead The width of the arrow at the broadest part of the head. Note that widthHead > widthTail is required if this is meant to make sense.
     * @param lengthHead the length of the arrow head
     * @return the polygon
     */
    protected static Polygon getArrowPolygonDown(int tailY, int tailX, int headY, int widthTail, int widthHead, int lengthHead) {
        int numPoints = 7;
        int[] xPoints = new int[numPoints];
        int[] yPoints = new int[numPoints];
        xPoints[0] = tailX - (widthTail / 2);
        yPoints[0] = tailY;
        xPoints[1] = tailX + (widthTail / 2);
        yPoints[1] = tailY;
        xPoints[2] = tailX + (widthTail / 2);
        yPoints[2] = headY - lengthHead;
        xPoints[3] = tailX + (widthHead / 2);
        yPoints[3] = headY - lengthHead;
        xPoints[4] = tailX;
        yPoints[4] = headY;
        xPoints[5] = tailX - (widthHead / 2);
        yPoints[5] = headY - lengthHead;
        xPoints[6] = tailX - (widthTail / 2);
        yPoints[6] = headY - lengthHead;
        return new Polygon(xPoints, yPoints, numPoints);
    }

    /**
     * Draws a cross at the given position
     *
     * @param ig2 where to draw
     * @param pos the position to draw at
     */
    private static void drawCrossAt(SVGGraphics2D ig2, Position2D pos) {
        int l = 5;
        Shape line1 = new Line2D.Double(pos.x - l, pos.y - l, pos.x + l, pos.y + l);
        Shape line2 = new Line2D.Double(pos.x - l, pos.y + l, pos.x + l, pos.y - l);
        ig2.draw(line1);
        ig2.draw(line2);
    }
    
    /**
     * Returns the 2 shapes for a cross at the given position
     * @param pos the position
     * @return the shape list for the cross (2 lines)
     */
    private static List<Shape> getCrossAt(Position2D pos) {
        int l = 5;
        List<Shape> parts = new ArrayList<Shape>();
        parts.add(new Line2D.Double(pos.x - l, pos.y - l, pos.x + l, pos.y + l));
        parts.add(new Line2D.Double(pos.x - l, pos.y + l, pos.x + l, pos.y - l));
        return parts;
    }

    /**
     * This function creates a connector between the 2D points (startX, startY) and (targetX, targetY). This connector is returned as a list of Shape
     * objects that can be painted on a Graphics2D canvas by stroking or filling them (e.g., G2Dinstance.fill(shapeInstance) or similar). The shapes
     * are created using the given Stroke (call G2Dinstance.getStroke() to use the current Stroke).
     *
     * If the start and end points are on the same height (i.e., startY == targetY), the connector will look
     * similar to an 'S' and will consist of a single shape (an arc forming a half circle). Otherwise it will
     * consist of 3 Shapes (two arcs and a line).
     *
     * You can choose whether the connector should start upwards or downwards from (startX, startY) using the startUpwards parameter.
     *
     * @param startX the x coordinate of the start point
     * @param startY the y coordinate of the start point
     * @param targetX the x coordinate of the end point
     * @param targetY the y coordinate of the end point
     * @param stroke the Stroke to use. You can get one from your Graphics2D instance.
     * @param startUpwardsInCaseOfSimpleArc whether to start upwards from the 2D Point (startX, startY). If this is false, downwards is used instead. This is ignored for cross-over arcs (where it can be deduced from the start/target coordinates), it is only used for non-crossover arcs.
     * @param pixelsToShiftCentralLineOnYAxis the number of pixels to shift the central line on the y axis. Can be positive (for shift to the right) or negative (shift to the left), but must NOT be larger than 1/2 of the distance between the y axis start and end pixels of this connector.
     * @return a list of Shapes that can be painted on a G2D canvas.
     */
    public static ArrayList<Shape> getArcConnector(Integer startX, Integer startY, Integer targetX, Integer targetY, Stroke stroke, Boolean startUpwardsInCaseOfSimpleArc, int pixelsToShiftCentralLineOnYAxis) {
                
        Boolean useAlternate = Settings.getBoolean("PTGLgraphComputation_B_key_use_alternate_arcs");
        if(useAlternate) {
            return getArcConnectorAlternative(startX, startY, targetX, targetY, stroke, startUpwardsInCaseOfSimpleArc, pixelsToShiftCentralLineOnYAxis);
        }
        else {
            if (startY.equals(targetY)) {
                return getSimpleArcConnector(startX, startY, targetX, stroke, startUpwardsInCaseOfSimpleArc);
            } else {
                Boolean computedStartUpwards;
                if (startY < targetY) {
                    computedStartUpwards = true;
                } else {
                    computedStartUpwards = false;
                }
                if (!Objects.equals(computedStartUpwards, startUpwardsInCaseOfSimpleArc)) {
                    System.err.println("DrawTools: WARNING: You startUpwards value " + startUpwardsInCaseOfSimpleArc.toString() + " for the crossover arc from (" + startX + "," + startY + ") to (" + targetX + "," + targetY + ") seems questionable.");
                }
                return getCrossoverArcConnector(startX, startY, targetX, targetY, stroke, startUpwardsInCaseOfSimpleArc, pixelsToShiftCentralLineOnYAxis);
            }
        }
    }

    /**
     * The function that implements a more complex 'S'-shaped arc connector between the 2D points (startX, startY) and (targetX, targetY) in the
     * requested direction. Internal function, call the more general getArcConnector() function instead.
     *
     * This connector consists of 3 Shapes: two half-circles and a line (think of the letter 'S').
     *
     * You can choose whether the connector should start upwards or downwards from (startX, startY) using the startUpwards parameter.
     *
     * upwards:                downwards:
     * __                     __
     * /  \                   /   \
     * |    |                  |    |
     * s    |                  |    t
     * |   t         s    |
     * |   |         |    |
     * \__/           \__/
     *
     * @param startX the x coordinate of the start point
     * @param startY the y coordinate of the start point
     * @param targetX the x coordinate of the end point
     * @param targetY the y coordinate of the end point
     * @param stroke the Stroke to use. You can get one from your Graphics2D instance.
     * @param startUpwards whether to start upwards from the 2D Point (startX, startY). If this is false, downwards is used instead.
     * @param pixelsToShiftCentralLineOnYAxis the number of pixels to shift the central line on the y axis. Can be positive (for shift to the right) or negative (shift to the left), but must NOT be larger than 1/2 of the distance between the y axis start and end pixels of this connector.
     * @return a list of Shapes that can be painted on a G2D canvas.
     */
    protected static ArrayList<Shape> getCrossoverArcConnector(Integer startX, Integer startY, Integer targetX, Integer targetY, Stroke stroke, Boolean startUpwards, int pixelsToShiftCentralLineOnYAxis) {
        Boolean computedStartUpwards;
        if (startY < targetY) {
            computedStartUpwards = true;
        } else {
            computedStartUpwards = false;
        }
        if (!Objects.equals(computedStartUpwards, startUpwards)) {
            System.err.println("DrawTools.getCrossoverArcConnector: WARNING: You startUpwards value " + startUpwards.toString() + " for the crossover arc from (" + startX + "," + startY + ") to (" + targetX + "," + targetY + ") seems questionable.");
        }
        if (pixelsToShiftCentralLineOnYAxis != 0) {
            return DrawTools.getCrossoverArcConnectorShiftCenter(startX, startY, targetX, targetY, stroke, startUpwards, pixelsToShiftCentralLineOnYAxis);
        }
        Integer upwards = 0;
        Integer downwards = 180;
        ArrayList<Shape> parts = new ArrayList<Shape>();
        Integer leftVertPosX;
        Integer rightVertPosX;
        Integer bothArcsSumWidth;
        Integer bothArcsSumHeight;
        Integer vertStartY;
        Integer leftArcHeight;
        Integer leftArcWidth;
        Integer rightArcHeight;
        Integer rightArcWidth;
        Integer arcWidth;
        Integer arcEllipseHeight;
        Integer leftArcUpperLeftX;
        Integer leftArcUpperLeftY;
        Integer centerBetweenBothArcsX;
        Integer centerBetweenBothArcsY;
        Integer leftArcEndX;
        Integer leftArcEndY;
        Integer rightArcEndX;
        Integer rightArcEndY;
        Integer leftArcLowerRightX;
        Integer leftArcLowerRightY;
        Integer leftArcUpperRightX;
        Integer leftArcUpperRightY;
        Integer rightArcLowerRightX;
        Integer rightArcLowerRightY;
        Integer rightArcUpperRightX;
        Integer rightArcUpperRightY;
        Integer rightArcUpperLeftX;
        Integer rightArcUpperLeftY;
        Integer tmp;
        Integer lineStartX;
        Integer lineStartY;
        Integer lineEndX;
        Integer lineEndY;
        Integer lineLength;
        
        if (startX < targetX) {
           // arc goes from left to right
            leftVertPosX = startX;
            rightVertPosX = targetX;
        } else {
            return getCrossoverArcConnector(targetX, targetY, startX, startY, stroke, (!startUpwards), pixelsToShiftCentralLineOnYAxis);        
        }
        vertStartY = startY;
        lineLength = Math.abs(startY - targetY);
        bothArcsSumWidth = rightVertPosX - leftVertPosX;
        leftArcWidth = rightArcWidth = bothArcsSumWidth / 2;
        bothArcsSumHeight = bothArcsSumWidth;
        leftArcHeight = rightArcHeight = arcEllipseHeight = bothArcsSumHeight / 2;
        centerBetweenBothArcsX = rightVertPosX - (bothArcsSumWidth / 2);
        centerBetweenBothArcsY = vertStartY;
        leftArcUpperLeftX = leftVertPosX;
        leftArcUpperLeftY = vertStartY - (arcEllipseHeight / 2);
        leftArcLowerRightX = leftArcUpperLeftX + leftArcWidth;
        leftArcLowerRightY = leftArcUpperLeftY + leftArcHeight - (arcEllipseHeight / 2);
        leftArcUpperRightX = leftArcUpperLeftX + leftArcWidth;
        leftArcUpperRightY = leftArcUpperLeftY;
        Shape shape;
        Arc2D arc;
        
        if (startUpwards) {
            leftArcEndX = leftArcLowerRightX;
            leftArcEndY = leftArcLowerRightY;
            arc = new Arc2D.Double(leftArcUpperLeftX, leftArcUpperLeftY, leftArcWidth, leftArcHeight, upwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);
            lineStartX = leftArcEndX;
            lineStartY = leftArcEndY;
            lineEndX = leftArcEndX;
            lineEndY = leftArcEndY + lineLength;
            Line2D l = new Line2D.Double(lineStartX, lineStartY, lineEndX, lineEndY);
            shape = stroke.createStrokedShape(l);
            parts.add(shape);
            rightArcUpperLeftX = lineEndX;
            rightArcUpperLeftY = lineEndY;
            arc = new Arc2D.Double(rightArcUpperLeftX, rightArcUpperLeftY - (arcEllipseHeight / 2), rightArcWidth, rightArcHeight, downwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);
        } else {
            leftArcEndX = leftArcUpperRightX;
            leftArcEndY = leftArcUpperRightY;
            arc = new Arc2D.Double(leftArcUpperLeftX, leftArcUpperLeftY, leftArcWidth, leftArcHeight, downwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);
            lineStartX = leftArcEndX;
            lineStartY = leftArcEndY + (arcEllipseHeight / 2);
            lineEndX = leftArcEndX;
            lineEndY = lineStartY - lineLength;
            Line2D l = new Line2D.Double(lineStartX, lineStartY, lineEndX, lineEndY);
            shape = stroke.createStrokedShape(l);
            parts.add(shape);
            rightArcUpperLeftX = lineEndX;
            rightArcUpperLeftY = lineEndY - (arcEllipseHeight / 2);
            arc = new Arc2D.Double(rightArcUpperLeftX, rightArcUpperLeftY, rightArcWidth, rightArcHeight, upwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);
        }
        return parts;
    }

    /**
     * Creates an outlined arrow (7 lines) on the given Graphics2D context, using the Polygon class. The arrow points straight up, thus headX is = tailX and does not have to be supplied.
     * You can transform this is you want to change its angle. It's a shape so you can stroke or fill it, too.
     * You should also check out getDefaultArrowPolygon() for an easy way to get arrows with the same width, length and head length.
     *
     * @param tailX The x location of the center of the "tail" of the arrow
     * @param tailY The y location of the center of the "tail" of the arrow
     * @param headY The y location of the "head" of the arrow
     * @param widthTail The width of the arrow at the tail
     * @param widthHead The width of the arrow at the broadest part of the head. Note that widthHead > widthTail is required if this is meant to make sense.
     * @param lengthHead the length of the arrow head
     * @return the polygon
     */
    protected static Polygon getArrowPolygonUp(int headY, int tailX, int tailY, int widthTail, int widthHead, int lengthHead) {
        int numPoints = 7;
        int[] xPoints = new int[numPoints];
        int[] yPoints = new int[numPoints];
        xPoints[0] = tailX - (widthTail / 2);
        yPoints[0] = tailY;
        xPoints[1] = tailX + (widthTail / 2);
        yPoints[1] = tailY;
        xPoints[2] = tailX + (widthTail / 2);
        yPoints[2] = headY + lengthHead;
        xPoints[3] = tailX + (widthHead / 2);
        yPoints[3] = headY + lengthHead;
        xPoints[4] = tailX;
        yPoints[4] = headY;
        xPoints[5] = tailX - (widthHead / 2);
        yPoints[5] = headY + lengthHead;
        xPoints[6] = tailX - (widthTail / 2);
        yPoints[6] = headY + lengthHead;
        return new Polygon(xPoints, yPoints, numPoints);
    }

    /**
     * The function that implements a more complex 'S'-shaped arc connector between the 2D points (startX, startY) and (targetX, targetY) in the
     * requested direction. Internal function, call the more general getArcConnector() function instead.
     *
     * NOTE: This is an alternate version, favored by Ina, implemented using Bezier curves. The Crossover connectors of this version will cut through the other SSE symbols.
     *
     * You can choose whether the connector should start upwards or downwards from (startX, startY) using the startUpwards parameter.
     *
     * upwards:                downwards:
     * __                           __
     * /  \                         /  \
     * |    \                       /    |
     * s     \                     /     t
     * \   t         s     /
     * \  |         |    /
     * \_/          \__/
     *
     * @param startX the x coordinate of the start point
     * @param startY the y coordinate of the start point
     * @param targetX the x coordinate of the end point
     * @param targetY the y coordinate of the end point
     * @param stroke the Stroke to use. You can get one from your Graphics2D instance.
     * @param startUpwards whether to start upwards from the 2D Point (startX, startY). If this is false, downwards is used instead.
     * @param pixelsToShiftCentralLineOnYAxis ignored in this alternative implementation.
     * @return a list of Shapes that can be painted on a G2D canvas.
     */
    protected static ArrayList<Shape> getCrossoverArcConnectorAlternativeBezierVersion(Integer startX, Integer startY, Integer targetX, Integer targetY, Stroke stroke, Boolean startUpwards, int pixelsToShiftCentralLineOnYAxis) {
        Boolean computedStartUpwards;
        if (startY < targetY) {
            computedStartUpwards = true;
        } else {
            computedStartUpwards = false;
        }
        if (!Objects.equals(computedStartUpwards, startUpwards)) {
            System.err.println("DrawTools.getCrossoverArcConnectorAlternativeBezierVersion: WARNING: You startUpwards value " + startUpwards.toString() + " for the crossover arc from (" + startX + "," + startY + ") to (" + targetX + "," + targetY + ") seems questionable.");
        }
        Integer upwards = 0;
        Integer downwards = 180;
        ArrayList<Shape> parts = new ArrayList<Shape>();
        Integer leftVertPosX;
        Integer leftVertPosY;
        Integer rightVertPosX;
        Integer rightVertPosY;
        Integer bothArcsXDistance;
        Integer bothArcsSumHeight;
        Integer vertStartY;
        Integer leftArcHeight;
        Integer leftArcWidth;
        Integer rightArcHeight;
        Integer rightArcWidth;
        Integer arcWidth;
        Integer arcEllipseHeight;
        Integer leftArcUpperLeftX;
        Integer leftArcUpperLeftY;
        Integer centerBetweenBothArcsX;
        Integer centerBetweenBothArcsY;
        Integer leftArcEndX;
        Integer leftArcEndY;
        Integer rightArcEndX;
        Integer rightArcEndY;
        Integer leftCurveStartX;
        Integer leftCurveStartY;
        Integer leftCurveEndX;
        Integer leftCurveEndY;
        Integer rightCurveStartX;
        Integer rightCurveStartY;
        Integer rightCurveEndX;
        Integer rightCurveEndY;
        Integer leftArcLowerRightX;
        Integer leftArcLowerRightY;
        Integer leftArcUpperRightX;
        Integer leftArcUpperRightY;
        Integer rightArcLowerRightX;
        Integer rightArcLowerRightY;
        Integer rightArcUpperRightX;
        Integer rightArcUpperRightY;
        Integer rightArcUpperLeftX;
        Integer rightArcUpperLeftY;
        Integer lineStartX;
        Integer lineStartY;
        Integer lineEndX;
        Integer lineEndY;
        Integer lineLength;
        if (startX < targetX) {
            leftVertPosX = startX;
            leftVertPosY = startY;
            rightVertPosX = targetX;
            rightVertPosY = targetY;
        } else {
            return getCrossoverArcConnectorAlternativeBezierVersion(targetX, targetY, startX, startY, stroke, (!startUpwards), 0);
        }
        vertStartY = leftVertPosY;
        lineLength = Math.abs(startY - targetY);
        bothArcsXDistance = rightVertPosX - leftVertPosX;
        leftArcWidth = rightArcWidth = 10;
        bothArcsSumHeight = leftArcWidth + rightArcWidth;
        leftArcHeight = rightArcHeight = arcEllipseHeight = bothArcsSumHeight / 2;
        centerBetweenBothArcsX = rightVertPosX - (bothArcsXDistance / 2);
        if (startUpwards) {
            centerBetweenBothArcsY = vertStartY + 40;   // 40 = vertHeight / 2
        } else {
            centerBetweenBothArcsY = vertStartY - 40;
        }
        leftArcUpperLeftX = leftVertPosX;
        leftArcUpperLeftY = vertStartY - (arcEllipseHeight / 2);
        leftArcLowerRightX = leftArcUpperLeftX + leftArcWidth;
        leftArcLowerRightY = leftArcUpperLeftY + leftArcHeight - (arcEllipseHeight / 2);
        leftArcUpperRightX = leftArcUpperLeftX + leftArcWidth;
        leftArcUpperRightY = leftArcUpperLeftY;
        Shape shape;
        Shape shape2;
        Arc2D arc;
        
        Boolean debugStartUpFirstArc = false;
        Boolean debugStartUpSecondArc = false;
        Boolean debugStartDownFirstArc = false;
        Boolean debugStartDownSecondArc = false;
        
        if (startUpwards) {
            leftArcEndX = leftArcLowerRightX;
            leftArcEndY = leftArcLowerRightY;
            leftCurveEndX = centerBetweenBothArcsX;
            leftCurveEndY = centerBetweenBothArcsY;
            shape = new CubicCurve2D.Double(startX, startY, startX, startY - leftArcHeight, leftArcEndX, leftArcEndY - leftArcHeight, leftCurveEndX, leftCurveEndY);
            shape = stroke.createStrokedShape(shape);
            parts.add(shape);
            
            if(debugStartUpFirstArc) {
                parts.addAll(getCrossAt(new Position2D(startX, startY)));
                parts.addAll(getCrossAt(new Position2D(startX, startY - leftArcHeight)));
                parts.addAll(getCrossAt(new Position2D(leftArcEndX, leftArcEndY - leftArcHeight)));
                parts.addAll(getCrossAt(new Position2D(leftCurveEndX, leftCurveEndY)));
            }                        
            
            /*
            if (debugStartUpFirstArc) {
                int startPointX = startX - 15;
                int startPointY = startY;
                parts.add(stroke.createStrokedShape(new Line2D.Double(startPointX, startPointY, startPointX, startPointY)));
                int targetPointX = targetX + 15;
                int targetPointY = targetY;
                parts.add(stroke.createStrokedShape(new Line2D.Double(targetPointX, targetPointY, targetPointX, targetPointY)));
            }
            */
            
            rightCurveStartX = leftCurveEndX;
            rightCurveStartY = leftCurveEndY;
            rightArcEndX = targetX;
            rightArcEndY = targetY;
            //shape2 = new CubicCurve2D.Double(rightCurveStartX, rightCurveStartY, rightCurveStartX, rightArcEndY, rightArcEndX, rightArcEndY + rightArcHeight, rightArcEndX, rightArcEndY);
            shape2 = new CubicCurve2D.Double(rightCurveStartX, rightCurveStartY, rightArcEndX - rightArcWidth, rightArcEndY + rightArcHeight, rightArcEndX, rightArcEndY + rightArcHeight, rightArcEndX, rightArcEndY);
            //shape2 = new CubicCurve2D.Double(rightArcEndX, rightArcEndY, rightArcEndX, rightArcEndY + rightArcHeight, rightCurveStartX, rightArcEndY, rightCurveStartX, rightCurveStartY);
            shape2 = stroke.createStrokedShape(shape2);
            parts.add(shape2);
            
            if(debugStartUpSecondArc) {
                parts.addAll(getCrossAt(new Position2D(rightCurveStartX, rightCurveStartY)));
                parts.addAll(getCrossAt(new Position2D(rightArcEndX - rightArcWidth, rightArcEndY + rightArcHeight)));
                parts.addAll(getCrossAt(new Position2D(rightArcEndX, rightArcEndY + rightArcHeight)));
                parts.addAll(getCrossAt(new Position2D(rightArcEndX, rightArcEndY)));
            } 
            
        } else {
            leftArcEndX = leftArcUpperRightX;
            leftArcEndY = leftArcUpperRightY;
            leftCurveEndX = centerBetweenBothArcsX;
            leftCurveEndY = centerBetweenBothArcsY;
            shape = new CubicCurve2D.Double(startX, startY, startX, startY + leftArcHeight, startX + leftArcWidth, startY + leftArcHeight, leftCurveEndX, leftCurveEndY);
            shape = stroke.createStrokedShape(shape);
            parts.add(shape);
            
            if(debugStartDownFirstArc) {
                parts.addAll(getCrossAt(new Position2D(startX, startY)));
                parts.addAll(getCrossAt(new Position2D(startX, startY + leftArcHeight)));
                parts.addAll(getCrossAt(new Position2D(startX + leftArcWidth, startY + leftArcHeight)));
                parts.addAll(getCrossAt(new Position2D(leftCurveEndX, leftCurveEndY)));
            }
            
            /*
            if (debugStartDownFirstArc) {
                int startPointX = startX - 15;
                int startPointY = startY;
                parts.add(stroke.createStrokedShape(new Line2D.Double(startPointX, startPointY, startPointX, startPointY)));
                int targetPointX = targetX + 15;
                int targetPointY = targetY;
                parts.add(stroke.createStrokedShape(new Line2D.Double(targetPointX, targetPointY, targetPointX, targetPointY)));
            }
            */
            rightCurveStartX = leftCurveEndX;
            rightCurveStartY = leftCurveEndY;
            rightArcEndX = targetX;
            rightArcEndY = targetY;
            shape2 = new CubicCurve2D.Double(rightCurveStartX, rightCurveStartY, rightArcEndX - rightArcWidth, rightArcEndY - rightArcHeight, rightArcEndX, rightArcEndY - rightArcHeight, rightArcEndX, rightArcEndY);
            shape2 = stroke.createStrokedShape(shape2);
            parts.add(shape2);
            
            if(debugStartDownSecondArc) {
                parts.addAll(getCrossAt(new Position2D(rightCurveStartX, rightCurveStartY)));
                parts.addAll(getCrossAt(new Position2D(rightArcEndX - rightArcWidth, rightArcEndY - rightArcHeight)));
                parts.addAll(getCrossAt(new Position2D(rightArcEndX, rightArcEndY - rightArcHeight)));
                parts.addAll(getCrossAt(new Position2D(rightArcEndX, rightArcEndY)));
            }
        }
        return parts;
    }

    /**
     * The function that implements a simple half circle-shaped arc connector between the 2D points (startX, startY) and (targetX, targetY) in the
     * requested direction. Internal function, call the more general getArcConnector() function instead.
     *
     * You can choose whether the connector should start upwards or downwards from (startX, startY) using the startUpwards parameter.
     *
     * upwards:                downwards:
     *
     * __                   s    t
     * /  \                  |    |
     * |    |                  \__/
     * s    t
     *
     * @param startX the x coordinate of the start point
     * @param bothY the y coordinate of both the start point and the end point
     * @param targetX the x coordinate of the end point
     * @param stroke the Stroke to use. You can get one from your Graphics2D instance.
     * @param startUpwards whether to start upwards from the 2D Point (startX, startY). If this is false, downwards is used instead.
     * @return a list of Shapes that can be painted on a G2D canvas.
     */
    protected static ArrayList<Shape> getSimpleArcConnector(Integer startX, Integer bothY, Integer targetX, Stroke stroke, Boolean startUpwards) {
        ArrayList<Shape> parts = new ArrayList<Shape>();
        Integer leftVertPosX;
        Integer rightVertPosX;
        Integer arcWidth;
        Integer arcHeight;
        Integer vertStartY;
        Integer arcTopLeftX;
        Integer arcTopLeftY;
        if (startX < targetX) {
            leftVertPosX = startX;
            rightVertPosX = targetX;
        } else {
            leftVertPosX = targetX;
            rightVertPosX = startX;
        }
        vertStartY = bothY;
        arcWidth = rightVertPosX - leftVertPosX;
        arcHeight = arcWidth / 2;
        arcTopLeftX = leftVertPosX;
        arcTopLeftY = vertStartY - arcHeight / 2;
        Arc2D arc;
        if (startUpwards) {
            arc = new Arc2D.Double(arcTopLeftX, arcTopLeftY, arcWidth, arcHeight, 0, 180, Arc2D.OPEN);
        } else {
            arc = new Arc2D.Double(arcTopLeftX, arcTopLeftY, arcWidth, arcHeight, 180, 180, Arc2D.OPEN);
        }
        Shape shape = stroke.createStrokedShape(arc);
        parts.add(shape);
        return parts;
    }

    /**
     * Draws a cross at the given position
     *
     * @param ig2 where to draw
     * @param pos the position to draw at
     * @param label the label string to draw (currently it is assumed that this is very short, like 2 chars)
     * @param labelPos the relative label orientation. 0 = above the cross, 1 = right of the cross, 2 = below the cross, 3 = left of the cross
     */
    private static void drawLabeledCrossAt(SVGGraphics2D ig2, Position2D pos, String label, int labelPos) {
        drawCrossAt(ig2, pos);
        Integer labelPosX = pos.x;
        Integer labelPosY = pos.y;
        if (labelPos == 0) {
            labelPosY -= 6;
            labelPosX -= 4;
        } else if (labelPos == 1) {
            labelPosX += 8;
            labelPosY += 4;
        } else if (labelPos == 2) {
            labelPosY += 17;
            labelPosX -= 5;
        } else if (labelPos == 3) {
            labelPosX -= 22;
            labelPosY += 5;
        }
        ig2.drawString(label, labelPosX, labelPosY);
    }

    /**
     * Draws a grid with some KEY arcs on it. Then uses all possible arc types to connect them. This is a debug-only function.
     */
    private static DrawResult drawTestG2D() {
        Boolean bw = false;
        Position2D vertStart = new Position2D(50, 50);
        Integer lineHeight = 12;
        Integer vertDist = 50;
        Integer vertHeight = 80;
        Integer vertWidth = 40;
        Integer vertStartX = 100;
        Integer vertStartY = 200 + vertHeight;
        SVGGraphics2D ig2;
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        ig2 = new SVGGraphics2D(document);
        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ig2.setPaint(Color.WHITE);
        ig2.fillRect(0, 0, 800, 600);
        ig2.setPaint(Color.BLACK);
        Font font = new Font("Verdana", Font.PLAIN, 12);
        ig2.setFont(font);
        FontMetrics fontMetrics = ig2.getFontMetrics();
        Integer currentPosX = vertStartX;
        Integer currentPosY = vertStartY;
        ig2.setPaint(Color.GRAY);
        drawGrid(ig2, new Position2D(vertStartX, vertStartY - vertHeight * 2), vertDist, vertHeight, 8, 4);
        ig2.setPaint(Color.BLACK);
        ig2.setPaint(Color.BLACK);
        Integer[] directions = new Integer[]{FoldingGraph.ORIENTATION_UPWARDS, FoldingGraph.ORIENTATION_DOWNWARDS, FoldingGraph.ORIENTATION_UPWARDS, FoldingGraph.ORIENTATION_UPWARDS, FoldingGraph.ORIENTATION_DOWNWARDS, FoldingGraph.ORIENTATION_DOWNWARDS, FoldingGraph.ORIENTATION_DOWNWARDS, FoldingGraph.ORIENTATION_UPWARDS};
        for (int i = 0; i < directions.length; i++) {
            Polygon p = getDefaultArrowPolygonLowestPointAt(currentPosX + i * vertDist, currentPosY, directions[i]);
            Shape s = ig2.getStroke().createStrokedShape(p);
            ig2.draw(s);
        }
        Integer arcStartX = vertStartX + (0 * vertDist);
        Integer arcStartY = vertStartY - vertHeight;
        Integer arcEndX = vertStartX + (1 * vertDist);
        Integer arcEndY = vertStartY - vertHeight;
        ig2.setPaint(Color.RED);
        drawLabeledCrossAt(ig2, new Position2D(arcStartX, arcStartY), "1S", DrawTools.ORIENTATION_LEFT_OF);
        drawLabeledCrossAt(ig2, new Position2D(arcEndX, arcEndY), "1E", DrawTools.ORIENTATION_RIGHT_OF);
        ig2.setPaint(Color.BLACK);
        List<Shape> arc1 = getArcConnector(arcStartX, arcStartY, arcEndX, arcEndY, ig2.getStroke(), true, 0);
        for (Shape s : arc1) {
            ig2.draw(s);
        }
        arcStartX = vertStartX + (2 * vertDist);
        arcStartY = vertStartY - vertHeight;
        arcEndX = vertStartX + (3 * vertDist);
        arcEndY = vertStartY;
        ig2.setPaint(Color.RED);
        //drawLabeledCrossAt(ig2, new Position2D(arcStartX, arcStartY), "2S", DrawTools.ORIENTATION_LEFT_OF);
        //drawLabeledCrossAt(ig2, new Position2D(arcEndX, arcEndY), "2E", DrawTools.ORIENTATION_RIGHT_OF);
        ig2.setPaint(Color.BLACK);
        arc1 = getArcConnector(arcStartX, arcStartY, arcEndX, arcEndY, ig2.getStroke(), true, 0);
        for (Shape s : arc1) {
            ig2.draw(s);
        }
        arcStartX = vertStartX + (4 * vertDist);
        arcStartY = vertStartY;
        arcEndX = vertStartX + (5 * vertDist);
        arcEndY = vertStartY - vertHeight;
        ig2.setPaint(Color.RED);
        //drawLabeledCrossAt(ig2, new Position2D(arcStartX, arcStartY), "3S", DrawTools.ORIENTATION_LEFT_OF);
        //drawLabeledCrossAt(ig2, new Position2D(arcEndX, arcEndY), "3E", DrawTools.ORIENTATION_RIGHT_OF);
        ig2.setPaint(Color.BLACK);
        arc1 = getArcConnector(arcStartX, arcStartY, arcEndX, arcEndY, ig2.getStroke(), false, 0);
        for (Shape s : arc1) {
            ig2.draw(s);
        }
        arcStartX = vertStartX + (6 * vertDist);
        arcStartY = vertStartY;
        arcEndX = vertStartX + (7 * vertDist);
        arcEndY = vertStartY;
        ig2.setPaint(Color.RED);
        drawLabeledCrossAt(ig2, new Position2D(arcStartX, arcStartY), "4S", DrawTools.ORIENTATION_LEFT_OF);
        drawLabeledCrossAt(ig2, new Position2D(arcEndX, arcEndY), "4E", DrawTools.ORIENTATION_RIGHT_OF);
        ig2.setPaint(Color.BLACK);
        arc1 = getArcConnector(arcStartX, arcStartY, arcEndX, arcEndY, ig2.getStroke(), false, 0);
        for (Shape s : arc1) {
            ig2.draw(s);
        }
        currentPosX = vertStartX;
        currentPosY = vertStartY + 2 * vertHeight;
        directions = new Integer[]{FoldingGraph.ORIENTATION_DOWNWARDS, FoldingGraph.ORIENTATION_UPWARDS, FoldingGraph.ORIENTATION_DOWNWARDS, FoldingGraph.ORIENTATION_DOWNWARDS, FoldingGraph.ORIENTATION_UPWARDS, FoldingGraph.ORIENTATION_UPWARDS, FoldingGraph.ORIENTATION_UPWARDS, FoldingGraph.ORIENTATION_DOWNWARDS};
        for (int i = 0; i < directions.length; i++) {
            Polygon p = getDefaultArrowPolygonLowestPointAt(currentPosX + i * vertDist, currentPosY, directions[i]);
            Shape s = ig2.getStroke().createStrokedShape(p);
            ig2.draw(s);
        }
        arcStartX = currentPosX + (1 * vertDist);
        arcStartY = currentPosY - vertHeight;
        arcEndX = currentPosX + (0 * vertDist);
        arcEndY = currentPosY - vertHeight;
        ig2.setPaint(Color.RED);
        drawLabeledCrossAt(ig2, new Position2D(arcStartX, arcStartY), "5S", DrawTools.ORIENTATION_RIGHT_OF);
        drawLabeledCrossAt(ig2, new Position2D(arcEndX, arcEndY), "5E", DrawTools.ORIENTATION_LEFT_OF);
        ig2.setPaint(Color.BLACK);
        arc1 = getArcConnector(arcStartX, arcStartY, arcEndX, arcEndY, ig2.getStroke(), true, 0);
        for (Shape s : arc1) {
            ig2.draw(s);
        }
        arcStartX = currentPosX + (3 * vertDist);
        arcStartY = currentPosY;
        arcEndX = currentPosX + (2 * vertDist);
        arcEndY = currentPosY - vertHeight;
        ig2.setPaint(Color.RED);
        //drawLabeledCrossAt(ig2, new Position2D(arcStartX, arcStartY), "6S", DrawTools.ORIENTATION_RIGHT_OF);
        //drawLabeledCrossAt(ig2, new Position2D(arcEndX, arcEndY), "6E", DrawTools.ORIENTATION_LEFT_OF);
        ig2.setPaint(Color.BLACK);
        arc1 = getArcConnector(arcStartX, arcStartY, arcEndX, arcEndY, ig2.getStroke(), false, 0);
        for (Shape s : arc1) {
            ig2.draw(s);
        }
        arcStartX = currentPosX + (5 * vertDist);
        arcStartY = currentPosY - vertHeight;
        arcEndX = currentPosX + (4 * vertDist);
        arcEndY = currentPosY;
        ig2.setPaint(Color.RED);
        //drawLabeledCrossAt(ig2, new Position2D(arcStartX, arcStartY), "7S", DrawTools.ORIENTATION_RIGHT_OF);
        //drawLabeledCrossAt(ig2, new Position2D(arcEndX, arcEndY), "7E", DrawTools.ORIENTATION_LEFT_OF);
        ig2.setPaint(Color.BLACK);
        arc1 = getArcConnector(arcStartX, arcStartY, arcEndX, arcEndY, ig2.getStroke(), true, 0);
        for (Shape s : arc1) {
            ig2.draw(s);
        }
        arcStartX = currentPosX + (7 * vertDist);
        arcStartY = currentPosY;
        arcEndX = currentPosX + (6 * vertDist);
        arcEndY = currentPosY;
        ig2.setPaint(Color.RED);
        drawLabeledCrossAt(ig2, new Position2D(arcStartX, arcStartY), "8S", DrawTools.ORIENTATION_RIGHT_OF);
        drawLabeledCrossAt(ig2, new Position2D(arcEndX, arcEndY), "8E", DrawTools.ORIENTATION_LEFT_OF);
        ig2.setPaint(Color.BLACK);
        arc1 = getArcConnector(arcStartX, arcStartY, arcEndX, arcEndY, ig2.getStroke(), false, 0);
        for (Shape s : arc1) {
            ig2.draw(s);
        }
        /*
        ig2.setPaint(Color.GREEN);
        drawLabeledCrossAt(ig2, new Position2D(250, 440), "250,440", DrawTools.ORIENTATION_BELOW);
        ig2.setPaint(Color.ORANGE);
        drawLabeledCrossAt(ig2, new Position2D(200, 360), "200,360", DrawTools.ORIENTATION_ABOVE);
        */
        ig2.setPaint(Color.BLACK);
        Rectangle2D roi = new Rectangle2D.Double(0, 0, 800, 600);
        DrawResult drawRes = new DrawResult(ig2, roi);
        return drawRes;
    }

    /**
     * The function that implements a more complex 'S'-shaped arc connector between the 2D points (startX, startY) and (targetX, targetY) in the
     * requested direction. Internal function, call the more general getArcConnector() function instead.
     *
     * This connector consists of 3 Shapes: two half-circles and a line (think of the letter 'S').
     *
     * You can choose whether the connector should start upwards or downwards from (startX, startY) using the startUpwards parameter.
     *
     * upwards:                downwards:
     * __                     __
     * /  \                   /   \
     * |    |                  |    |
     * s    |                  |    t
     * |   t         s    |
     * |   |         |    |
     * \__/           \__/
     *
     * @param startX the x coordinate of the start point
     * @param startY the y coordinate of the start point
     * @param targetX the x coordinate of the end point
     * @param targetY the y coordinate of the end point
     * @param stroke the Stroke to use. You can get one from your Graphics2D instance.
     * @param startUpwards whether to start upwards from the 2D Point (startX, startY). If this is false, downwards is used instead.
     * @param pixelsToShiftCentralLineOnYAxis the number of pixels to shift the central line on the y axis. Can be positive (for shift to the right) or negative (shift to the left), but must NOT be larger than 1/2 of the distance between the y axis start and end pixels of this connector:
     * ______
     * /      \
     * |        |
     * s        |
     * |   t
     * |   |
     * \__/
     * @return a list of Shapes that can be painted on a G2D canvas.
     */
    protected static ArrayList<Shape> getCrossoverArcConnectorShiftCenter(Integer startX, Integer startY, Integer targetX, Integer targetY, Stroke stroke, Boolean startUpwards, int pixelsToShiftCentralLineOnYAxis) {
        Integer upwards = 0;
        Integer downwards = 180;
        ArrayList<Shape> parts = new ArrayList<Shape>();
        Integer leftVertPosX;
        Integer rightVertPosX;
        Integer bothArcsSumWidth;
        Integer bothArcsSumHeight;
        Integer vertStartY;
        Integer leftArcHeight;
        Integer leftArcWidth;
        Integer rightArcHeight;
        Integer rightArcWidth;
        Integer arcWidth;
        Integer arcEllipseHeight;
        Integer leftArcUpperLeftX;
        Integer leftArcUpperLeftY;
        Integer centerBetweenBothArcsX;
        Integer centerBetweenBothArcsY;
        Integer leftArcEndX;
        Integer leftArcEndY;
        Integer rightArcEndX;
        Integer rightArcEndY;
        Integer leftArcLowerRightX;
        Integer leftArcLowerRightY;
        Integer leftArcUpperRightX;
        Integer leftArcUpperRightY;
        Integer rightArcLowerRightX;
        Integer rightArcLowerRightY;
        Integer rightArcUpperRightX;
        Integer rightArcUpperRightY;
        Integer rightArcUpperLeftX;
        Integer rightArcUpperLeftY;
        Integer lineStartX;
        Integer lineStartY;
        Integer lineEndX;
        Integer lineEndY;
        Integer lineLength;
        Integer leftArcEllipseHeight;
        Integer rightArcEllipseHeight;
        if (startX < targetX) {
            leftVertPosX = startX;
            rightVertPosX = targetX;
        } else {
            leftVertPosX = targetX;
            rightVertPosX = startX;
        }
        vertStartY = startY;
        lineLength = Math.abs(startY - targetY);
        if (Math.abs(pixelsToShiftCentralLineOnYAxis) >= (lineLength / 2)) {
            DP.getInstance().e("SSEGraph", "getCrossoverArcConnector(): Requested shift " + pixelsToShiftCentralLineOnYAxis + " is too large, would place the central line outside of this crossover arc. Max is " + ((lineLength / 2) - 1) + ".");
            return parts;
        }
        bothArcsSumWidth = rightVertPosX - leftVertPosX;
        leftArcWidth = rightArcWidth = bothArcsSumWidth / 2;
        leftArcWidth = leftArcWidth - pixelsToShiftCentralLineOnYAxis;
        rightArcWidth = rightArcWidth + pixelsToShiftCentralLineOnYAxis;
        if (leftArcWidth + rightArcWidth != bothArcsSumWidth) {
            int diff = bothArcsSumWidth - (leftArcWidth + rightArcWidth);
            if (diff > 1) {
                DP.getInstance().w("SSEGraph", "getCrossoverArcConnectorShiftCenter: Rounding error is " + diff + " px, but should never be > than 1 px.");
            }
            leftArcWidth += diff;
        }
        bothArcsSumHeight = bothArcsSumWidth;
        leftArcHeight = leftArcWidth;
        rightArcHeight = rightArcWidth;
        leftArcEllipseHeight = leftArcHeight;
        rightArcEllipseHeight = rightArcHeight;
        centerBetweenBothArcsX = rightVertPosX - rightArcWidth;
        centerBetweenBothArcsY = vertStartY;
        leftArcUpperLeftX = leftVertPosX;
        leftArcUpperLeftY = vertStartY - (leftArcEllipseHeight / 2);
        leftArcLowerRightX = leftArcUpperLeftX + leftArcWidth;
        leftArcLowerRightY = leftArcUpperLeftY + leftArcHeight - (leftArcEllipseHeight / 2);
        leftArcUpperRightX = leftArcUpperLeftX + leftArcWidth;
        leftArcUpperRightY = leftArcUpperLeftY;
        Shape shape;
        Arc2D arc;
        if (startUpwards) {
            leftArcEndX = leftArcLowerRightX;
            leftArcEndY = leftArcLowerRightY;
            arc = new Arc2D.Double(leftArcUpperLeftX, leftArcUpperLeftY, leftArcWidth, leftArcHeight, upwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);
            lineStartX = leftArcEndX;
            lineStartY = leftArcEndY;
            lineEndX = leftArcEndX;
            lineEndY = leftArcEndY + lineLength;
            Line2D l = new Line2D.Double(lineStartX, lineStartY, lineEndX, lineEndY);
            shape = stroke.createStrokedShape(l);
            parts.add(shape);
            rightArcUpperLeftX = lineEndX;
            rightArcUpperLeftY = lineEndY;
            arc = new Arc2D.Double(rightArcUpperLeftX, rightArcUpperLeftY - (rightArcEllipseHeight / 2), rightArcWidth, rightArcHeight, downwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);
        } else {
            leftArcEndX = leftArcUpperRightX;
            leftArcEndY = leftArcUpperRightY;
            arc = new Arc2D.Double(leftArcUpperLeftX, leftArcUpperLeftY, leftArcWidth, leftArcHeight, downwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);
            lineStartX = leftArcEndX;
            lineStartY = leftArcEndY + (leftArcEllipseHeight / 2);
            lineEndX = leftArcEndX;
            lineEndY = lineStartY - lineLength;
            Line2D l = new Line2D.Double(lineStartX, lineStartY, lineEndX, lineEndY);
            shape = stroke.createStrokedShape(l);
            parts.add(shape);
            rightArcUpperLeftX = lineEndX;
            rightArcUpperLeftY = lineEndY - (rightArcEllipseHeight / 2);
            arc = new Arc2D.Double(rightArcUpperLeftX, rightArcUpperLeftY, rightArcWidth, rightArcHeight, upwards, 180, Arc2D.OPEN);
            shape = stroke.createStrokedShape(arc);
            parts.add(shape);
        }
        return parts;
    }
    
    public enum IMAGEFORMAT { PNG, SVG, TIFF, PDF, JPEG }
    
    /**
     * Returns the file extension including the dot, e.g., ".pdf" for format PDF.
     * @param f the image format
     * @return the file extension, including the dot (e.g., ".pdf" for format PDF).
     */
    public static String getFileExtensionForImageFormat(IMAGEFORMAT f) {
        if (f.equals(IMAGEFORMAT.JPEG)) {
            return ".jpg";
        } 
        else if(f.equals(IMAGEFORMAT.TIFF)) {
            return ".tiff";
        }
        else if(f.equals(IMAGEFORMAT.PDF)) {
            return ".pdf";
        }
        else if(f.equals(IMAGEFORMAT.SVG)) {
            return ".svg";
        }
        else if(f.equals(IMAGEFORMAT.PNG)) {
            return ".png";
        }
        else {
            DP.getInstance().e("DrawTools", "getFileExtensionForImageFormat: Unsupported image format, returning empty file extension.");
            return "";
        }
    }
    
    
    /**
     * Writes an SVGGraphics object (the contents of the canvas) to an image file in SVG format.
     * @param svgFilePath the path to the output file, including file extension
     * @param drawRes the graphics object, its contents will be saved to the image file
     * @throws IOException if something went wrong with writing the file
     */
    public static void writeG2dToSVGFile(String svgFilePath, DrawResult drawRes) throws IOException {
        // reset output stream to suppress the annoying output of the Apache batik library. Gets reset after lib call.
        OutputStream tmp=System.out;
        System.setOut(new PrintStream(new org.apache.commons.io.output.NullOutputStream()));
        drawRes.g2d.stream(new FileWriter(svgFilePath), false);     
        System.setOut((PrintStream) tmp);
    }
    


    
    /**
     * Converts the input SVG file to various other formats.
     * @param svgInputFilePath the SVG input image
     * @param outputFileBasePathNoExt the base output file name (without . and without extension)
     * @param drawRes the drawRes, which is required to determine the ROI within the SVG
     * @param formats a list of formats, use the constants in DrawTools class
     * @return a list of files that were written successfully, by format
     */
    public static HashMap<IMAGEFORMAT, String> convertSVGFileToOtherFormats(String svgInputFilePath, String outputFileBasePathNoExt, DrawResult drawRes, IMAGEFORMAT[] formats) {
        
        HashMap<IMAGEFORMAT, String> outfilesByFormat = new HashMap<IMAGEFORMAT, String>();
        
        // write other formats
        SVGConverter svgConverter = new SVGConverter();
        svgConverter.setArea(drawRes.roi);
        svgConverter.setWidth((float) drawRes.roi.getWidth());
        svgConverter.setHeight((float) drawRes.roi.getHeight());

        String formatFileExt = "";
        for(IMAGEFORMAT format : formats) {           

            if(format.equals(IMAGEFORMAT.PNG)) {                
                svgConverter.setDestinationType(DestinationType.PNG);
                //System.out.println("Handling PNG: input file='" + svgInputFilePath + "', output file ='" + outputFileBasePathNoExt + "'.");
                formatFileExt = DestinationType.PNG_EXTENSION;
            } else if(format.equals(IMAGEFORMAT.JPEG)) {
                svgConverter.setDestinationType(DestinationType.JPEG);
                svgConverter.setQuality(0.8F);  // JPEG compression
                formatFileExt = DestinationType.JPEG_EXTENSION;
            } else if(format.equals(IMAGEFORMAT.TIFF)) {
                svgConverter.setDestinationType(DestinationType.TIFF);                
                formatFileExt = DestinationType.TIFF_EXTENSION;
            } else if(format.equals(IMAGEFORMAT.PDF)) {
                //System.out.println("Handling PDF: input file='" + svgInputFilePath + "', output file ='" + outputFileBasePathNoExt + "'.");
                svgConverter.setDestinationType(DestinationType.PDF);
                formatFileExt = DestinationType.PDF_EXTENSION;
            } else {
                DP.getInstance().w("Unsupported image output format ignored.");
                continue;
            }
            
            String outputFileBasePathWithExt = outputFileBasePathNoExt + formatFileExt;

            svgConverter.setSources(new String[]{svgInputFilePath});            
            svgConverter.setDst(new File(outputFileBasePathWithExt));
            
            // reset output stream to suppress the annoying output of the Apache batik library. Gets reset after lib call.
            // jnw: fix bug where output streams were nested and caused StackOverflowErrors for large inputs!
            //  -> see: https://stackoverflow.com/questions/52931276/java-stackoverflowerror-at-java-io-printstream-writeprintstream-java480-and-n
            OutputStream tmp=System.out;
            System.setOut(new PrintStream(new org.apache.commons.io.output.NullOutputStream()));
  
            try {      
                svgConverter.execute();
                outfilesByFormat.put(format, outputFileBasePathNoExt + formatFileExt);
                System.setOut((PrintStream) tmp);
            } catch (SVGConverterException ex) {
                System.setOut((PrintStream) tmp);
                DP.getInstance().e("Could not convert SVG file to format '" + format + "': '" + ex.getMessage() + "'. Skipping.");
            } finally {
                System.setOut((PrintStream) tmp);
            }
            
        }
        return outfilesByFormat;
    }
    
    /**
     * Draws a string to the image and treats line breaks. Extends org.apache.batik.svggen.SVGGraphics2D.drawString .
     * @param image to draw to
     * @param text to draw
     * @param x where to start
     * @param y where to start
     */
    public static void drawStringLineBreaks(SVGGraphics2D image, String text, int x, int y) {
        for (String line : text.split("\n")) {
            image.drawString(line, x, y);
            y += image.getFontMetrics().getHeight();
        }
    }
    
}
