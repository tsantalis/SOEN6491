/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2011, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ------------------------
 * XYPolygonAnnotation.java
 * ------------------------
 * (C) Copyright 2005-2009, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Peter Kolb (patch 2809117);
 *
 * Changes:
 * --------
 * 09-Feb-2005 : Version 1 (DG);
 *
 */

package longMethod.jfreechart.draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A polygon annotation that can be placed on an {@link XYPlot}.  The
 * polygon coordinates are specified in data space.
 */
public class XYPolygonAnnotation extends AbstractXYAnnotation
        implements Cloneable, PublicCloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -6984203651995900036L;

    /** The polygon. */
    private double[] polygon;

    /** The stroke used to draw the box outline. */
    private transient Stroke stroke;

    /** The paint used to draw the box outline. */
    private transient Paint outlinePaint;

    /** The paint used to fill the box. */
    private transient Paint fillPaint;

    /**
     * Creates a new annotation (where, by default, the polygon is drawn
     * with a black outline).  The array of polygon coordinates must contain
     * an even number of coordinates (each pair is an (x, y) location on the
     * plot) and the last point is automatically joined back to the first point.
     *
     * @param polygon  the coordinates of the polygon's vertices
     *     (<code>null</code> not permitted).
     */
    public XYPolygonAnnotation(double[] polygon) {
        this(polygon, new BasicStroke(1.0f), Color.black);
    }

    /**
     * Creates a new annotation where the box is drawn as an outline using
     * the specified <code>stroke</code> and <code>outlinePaint</code>.
     * The array of polygon coordinates must contain an even number of
     * coordinates (each pair is an (x, y) location on the plot) and the last
     * point is automatically joined back to the first point.
     *
     * @param polygon  the coordinates of the polygon's vertices
     *     (<code>null</code> not permitted).
     * @param stroke  the shape stroke (<code>null</code> permitted).
     * @param outlinePaint  the shape color (<code>null</code> permitted).
     */
    public XYPolygonAnnotation(double[] polygon,
                               Stroke stroke, Paint outlinePaint) {
        this(polygon, stroke, outlinePaint, null);
    }

    /**
     * Creates a new annotation.  The array of polygon coordinates must
     * contain an even number of coordinates (each pair is an (x, y) location
     * on the plot) and the last point is automatically joined back to the
     * first point.
     *
     * @param polygon  the coordinates of the polygon's vertices
     *     (<code>null</code> not permitted).
     * @param stroke  the shape stroke (<code>null</code> permitted).
     * @param outlinePaint  the shape color (<code>null</code> permitted).
     * @param fillPaint  the paint used to fill the shape (<code>null</code>
     *                   permitted).
     */
    public XYPolygonAnnotation(double[] polygon,
                               Stroke stroke,
                               Paint outlinePaint, Paint fillPaint) {
        super();
        if (polygon == null) {
            throw new IllegalArgumentException("Null 'polygon' argument.");
        }
        if (polygon.length % 2 != 0) {
            throw new IllegalArgumentException("The 'polygon' array must "
                    + "contain an even number of items.");
        }
        this.polygon = (double[]) polygon.clone();
        this.stroke = stroke;
        this.outlinePaint = outlinePaint;
        this.fillPaint = fillPaint;
    }

    /**
     * Returns the coordinates of the polygon's vertices.  The returned array
     * is a copy, so it is safe to modify without altering the annotation's
     * state.
     *
     * @return The coordinates of the polygon's vertices.
     *
     * @since 1.0.2
     */
    public double[] getPolygonCoordinates() {
        return (double[]) this.polygon.clone();
    }

    /**
     * Returns the fill paint.
     *
     * @return The fill paint (possibly <code>null</code>).
     *
     * @since 1.0.2
     */
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    /**
     * Returns the outline stroke.
     *
     * @return The outline stroke (possibly <code>null</code>).
     *
     * @since 1.0.2
     */
    public Stroke getOutlineStroke() {
        return this.stroke;
    }

    /**
     * Returns the outline paint.
     *
     * @return The outline paint (possibly <code>null</code>).
     *
     * @since 1.0.2
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Draws the annotation.  This method is usually called by the
     * {@link XYPlot} class, you shouldn't need to call it directly.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param rendererIndex  the renderer index.
     * @param info  the plot rendering info.
     */
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis,
                     int rendererIndex, PlotRenderingInfo info) {

        // if we don't have at least 2 (x, y) coordinates, just return
        if (this.polygon.length < 4) {
            return;
        }
        GeneralPath area = computeArea(plot, dataArea, domainAxis, rangeAxis);


        if (this.fillPaint != null) {
            g2.setPaint(this.fillPaint);
            g2.fill(area);
        }

        if (this.stroke != null && this.outlinePaint != null) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.stroke);
            g2.draw(area);
        }
        addEntity(info, area, rendererIndex, getToolTipText(), getURL());

    }

	private GeneralPath computeArea(XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis) {
		PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
                plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
                plot.getRangeAxisLocation(), orientation);

        GeneralPath area = new GeneralPath();
        double x = domainAxis.valueToJava2D(this.polygon[0], dataArea,
                domainEdge);
        double y = rangeAxis.valueToJava2D(this.polygon[1], dataArea,
                rangeEdge);
        if (orientation == PlotOrientation.HORIZONTAL) {
            area.moveTo((float) y, (float) x);
            for (int i = 2; i < this.polygon.length; i += 2) {
                x = domainAxis.valueToJava2D(this.polygon[i], dataArea,
                        domainEdge);
                y = rangeAxis.valueToJava2D(this.polygon[i + 1], dataArea,
                        rangeEdge);
                area.lineTo((float) y, (float) x);
            }
            area.closePath();
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            area.moveTo((float) x, (float) y);
            for (int i = 2; i < this.polygon.length; i += 2) {
                x = domainAxis.valueToJava2D(this.polygon[i], dataArea,
                        domainEdge);
                y = rangeAxis.valueToJava2D(this.polygon[i + 1], dataArea,
                        rangeEdge);
                area.lineTo((float) x, (float) y);
            }
            area.closePath();
       }
		return area;
	}
}
