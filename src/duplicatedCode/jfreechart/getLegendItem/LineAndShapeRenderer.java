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
 * -------------------------
 * LineAndShapeRenderer.java
 * -------------------------
 * (C) Copyright 2001-2009, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Jeremy Bowman;
 *                   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *                   Peter Kolb (patch 2497611);
 *
 * Changes
 * -------
 * 23-Oct-2001 : Version 1 (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 16-Jan-2002 : Renamed HorizontalCategoryItemRenderer.java
 *               --> CategoryItemRenderer.java (DG);
 * 05-Feb-2002 : Changed return type of the drawCategoryItem method from void
 *               to Shape, as part of the tooltips implementation (DG);
 * 11-May-2002 : Support for value label drawing (JB);
 * 29-May-2002 : Now extends AbstractCategoryItemRenderer (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs
 *               for HTML image maps (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 11-Oct-2002 : Added new constructor to incorporate tool tip and URL
 *               generators (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and
 *               CategoryToolTipGenerator interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 06-Nov-2002 : Renamed drawCategoryItem() --> drawItem() and now using axis
 *               for category spacing (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset in drawItem()
 *               method (DG);
 * 12-May-2003 : Modified to take into account the plot orientation (DG);
 * 29-Jul-2003 : Amended code that doesn't compile with JDK 1.2.2 (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 22-Sep-2003 : Fixed cloning (DG);
 * 10-Feb-2004 : Small change to drawItem() method to make cut-and-paste
 *               override easier (DG);
 * 16-Jun-2004 : Fixed bug (id=972454) with label positioning on horizontal
 *               charts (DG);
 * 15-Oct-2004 : Updated equals() method (DG);
 * 05-Nov-2004 : Modified drawItem() signature (DG);
 * 11-Nov-2004 : Now uses ShapeUtilities class to translate shapes (DG);
 * 27-Jan-2005 : Changed attribute names, modified constructor and removed
 *               constants (DG);
 * 01-Feb-2005 : Removed unnecessary constants (DG);
 * 15-Mar-2005 : Fixed bug 1163897, concerning outlines for shapes (DG);
 * 13-Apr-2005 : Check flags that control series visibility (DG);
 * 20-Apr-2005 : Use generators for legend labels, tooltips and URLs (DG);
 * 09-Jun-2005 : Use addItemEntity() method (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 25-May-2006 : Added check to drawItem() to detect when both the line and
 *               the shape are not visible (DG);
 * 20-Apr-2007 : Updated getLegendItem() for renderer change (DG);
 * 17-May-2007 : Set datasetIndex and seriesIndex in getLegendItem() (DG);
 * 18-May-2007 : Set dataset and seriesKey for LegendItem (DG);
 * 24-Sep-2007 : Deprecated redundant fields/methods (DG);
 * 27-Sep-2007 : Added option to offset series x-position within category (DG);
 * 17-Jun-2008 : Apply legend shape, font and paint attributes (DG);
 * 26-Jun-2008 : Added crosshair support (DG);
 * 14-Jan-2009 : Added support for seriesVisible flags (PK);
 *
 */

package duplicatedCode.jfreechart.getLegendItem;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.BooleanList;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

/**
 * A renderer that draws shapes for each data item, and lines between data
 * items (for use with the {@link CategoryPlot} class).
 * The example shown here is generated by the <code>LineChartDemo1.java</code>
 * program included in the JFreeChart Demo Collection:
 * <br><br>
 * <img src="../../../../../images/LineAndShapeRendererSample.png"
 * alt="LineAndShapeRendererSample.png" />
 */
public class LineAndShapeRenderer extends AbstractCategoryItemRenderer
        implements Cloneable, PublicCloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -197749519869226398L;

    /**
     * A flag that controls whether or not lines are visible for ALL series.
     *
     * @deprecated As of 1.0.7 (this override flag is unnecessary).
     */
    private Boolean linesVisible;

    /**
     * A table of flags that control (per series) whether or not lines are
     * visible.
     */
    private BooleanList seriesLinesVisible;

    /**
     * A flag indicating whether or not lines are drawn between non-null
     * points.
     */
    private boolean baseLinesVisible;

    /**
     * A flag that controls whether or not shapes are visible for ALL series.
     *
     * @deprecated As of 1.0.7 (this override flag is unnecessary).
     */
    private Boolean shapesVisible;

    /**
     * A table of flags that control (per series) whether or not shapes are
     * visible.
     */
    private BooleanList seriesShapesVisible;

    /** The default value returned by the getShapeVisible() method. */
    private boolean baseShapesVisible;

    /**
     * A flag that controls whether or not shapes are filled for ALL series.
     *
     * @deprecated As of 1.0.7 (this override flag is unnecessary).
     */
    private Boolean shapesFilled;

    /**
     * A table of flags that control (per series) whether or not shapes are
     * filled.
     */
    private BooleanList seriesShapesFilled;

    /** The default value returned by the getShapeFilled() method. */
    private boolean baseShapesFilled;

    /**
     * A flag that controls whether the fill paint is used for filling
     * shapes.
     */
    private boolean useFillPaint;

    /** A flag that controls whether outlines are drawn for shapes. */
    private boolean drawOutlines;

    /**
     * A flag that controls whether the outline paint is used for drawing shape
     * outlines - if not, the regular series paint is used.
     */
    private boolean useOutlinePaint;

    /**
     * A flag that controls whether or not the x-position for each item is
     * offset within the category according to the series.
     *
     * @since 1.0.7
     */
    private boolean useSeriesOffset;

    /**
     * The item margin used for series offsetting - this allows the positioning
     * to match the bar positions of the {@link BarRenderer} class.
     *
     * @since 1.0.7
     */
    private double itemMargin;

    /**
     * Creates a renderer with both lines and shapes visible by default.
     */
    public LineAndShapeRenderer() {
        this(true, true);
    }

    /**
     * Creates a new renderer with lines and/or shapes visible.
     *
     * @param lines  draw lines?
     * @param shapes  draw shapes?
     */
    public LineAndShapeRenderer(boolean lines, boolean shapes) {
        super();
        this.linesVisible = null;
        this.seriesLinesVisible = new BooleanList();
        this.baseLinesVisible = lines;
        this.shapesVisible = null;
        this.seriesShapesVisible = new BooleanList();
        this.baseShapesVisible = shapes;
        this.shapesFilled = null;
        this.seriesShapesFilled = new BooleanList();
        this.baseShapesFilled = true;
        this.useFillPaint = false;
        this.drawOutlines = true;
        this.useOutlinePaint = false;
        this.useSeriesOffset = false;  // preserves old behaviour
        this.itemMargin = 0.0;
    }

    // LINES VISIBLE

    /**
     * Returns the flag used to control whether or not the line for an item is
     * visible.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemLineVisible(int series, int item) {
        Boolean flag = this.linesVisible;
        if (flag == null) {
            flag = getSeriesLinesVisible(series);
        }
        if (flag != null) {
            return flag.booleanValue();
        }
        else {
            return this.baseLinesVisible;
        }
    }

    /**
     * Returns the flag used to control whether or not the lines for a series
     * are visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly <code>null</code>).
     *
     * @see #setSeriesLinesVisible(int, Boolean)
     */
    public Boolean getSeriesLinesVisible(int series) {
        return this.seriesLinesVisible.getBoolean(series);
    }

    // SHAPES VISIBLE

    /**
     * Returns the flag used to control whether or not the shape for an item is
     * visible.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemShapeVisible(int series, int item) {
        Boolean flag = this.shapesVisible;
        if (flag == null) {
            flag = getSeriesShapesVisible(series);
        }
        if (flag != null) {
            return flag.booleanValue();
        }
        else {
            return this.baseShapesVisible;
        }
    }

    /**
     * Returns the flag used to control whether or not the shapes for a series
     * are visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     *
     * @see #setSeriesShapesVisible(int, Boolean)
     */
    public Boolean getSeriesShapesVisible(int series) {
        return this.seriesShapesVisible.getBoolean(series);
    }

    // SHAPES FILLED

    /**
     * Returns the flag used to control whether or not the shape for an item
     * is filled. The default implementation passes control to the
     * <code>getSeriesShapesFilled</code> method. You can override this method
     * if you require different behaviour.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemShapeFilled(int series, int item) {
        return getSeriesShapesFilled(series);
    }

    /**
     * Returns the flag used to control whether or not the shapes for a series
     * are filled.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getSeriesShapesFilled(int series) {

        // return the overall setting, if there is one...
        /*if (this.shapesFilled != null) {
            return this.shapesFilled.booleanValue();
        }*/

        // otherwise look up the paint table
        Boolean flag = this.seriesShapesFilled.getBoolean(series);
        if (flag != null) {
            return flag.booleanValue();
        }
        else {
            return this.baseShapesFilled;
        }

    }

    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return The legend item.
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {

        CategoryPlot cp = getPlot();
        if (cp == null) {
            return null;
        }

        if (isSeriesVisible(series) && isSeriesVisibleInLegend(series)) {
            CategoryDataset dataset = cp.getDataset(datasetIndex);
            String label = getLegendItemLabelGenerator().generateLabel(
                    dataset, series);
            String description = label;
            String toolTipText = null;
            if (getLegendItemToolTipGenerator() != null) {
                toolTipText = getLegendItemToolTipGenerator().generateLabel(
                        dataset, series);
            }
            String urlText = null;
            if (getLegendItemURLGenerator() != null) {
                urlText = getLegendItemURLGenerator().generateLabel(
                        dataset, series);
            }
            Shape shape = lookupLegendShape(series);
            Paint paint = lookupSeriesPaint(series);
            Paint fillPaint = (this.useFillPaint
                    ? getItemFillPaint(series, 0) : paint);
            boolean shapeOutlineVisible = this.drawOutlines;
            Paint outlinePaint = (this.useOutlinePaint
                    ? getItemOutlinePaint(series, 0) : paint);
            Stroke outlineStroke = lookupSeriesOutlineStroke(series);
            LegendItem result = new LegendItem(label, description, toolTipText,
                    urlText, getItemShapeVisible(series, 0), shape, getItemShapeFilled(series, 0),
                    fillPaint, shapeOutlineVisible, outlinePaint, outlineStroke,
                    getItemLineVisible(series, 0), new Line2D.Double(-7.0, 0.0, 7.0, 0.0),
                    getItemStroke(series, 0), getItemPaint(series, 0));
            result.setLabelFont(lookupLegendTextFont(series));
            Paint labelPaint = lookupLegendTextPaint(series);
            if (labelPaint != null) {
                result.setLabelPaint(labelPaint);
            }
            result.setDataset(dataset);
            result.setDatasetIndex(datasetIndex);
            result.setSeriesKey(dataset.getRowKey(series));
            result.setSeriesIndex(series);
            return result;
        }
        return null;

    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area in which the data is drawn.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2, CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            ValueAxis rangeAxis, CategoryDataset dataset, int row, int column,
            int pass) {

        // do nothing if item is not visible
        if (!getItemVisible(row, column)) {
            return;
        }

        // do nothing if both the line and shape are not visible
        if (!getItemLineVisible(row, column)
                && !getItemShapeVisible(row, column)) {
            return;
        }

        // nothing is drawn for null...
        Number v = dataset.getValue(row, column);
        if (v == null) {
            return;
        }

        int visibleRow = state.getVisibleSeriesIndex(row);
        if (visibleRow < 0) {
            return;
        }
        int visibleRowCount = state.getVisibleSeriesCount();

        PlotOrientation orientation = plot.getOrientation();

        // current data point...
        double x1;
        if (this.useSeriesOffset) {
            x1 = domainAxis.getCategorySeriesMiddle(column,
                    dataset.getColumnCount(), visibleRow, visibleRowCount,
                    this.itemMargin, dataArea, plot.getDomainAxisEdge());
        }
        else {
            x1 = domainAxis.getCategoryMiddle(column, getColumnCount(),
                    dataArea, plot.getDomainAxisEdge());
        }
        double value = v.doubleValue();
        double y1 = rangeAxis.valueToJava2D(value, dataArea,
                plot.getRangeAxisEdge());

        if (pass == 0 && getItemLineVisible(row, column)) {
            if (column != 0) {
                Number previousValue = dataset.getValue(row, column - 1);
                if (previousValue != null) {
                    // previous data point...
                    double previous = previousValue.doubleValue();
                    double x0;
                    if (this.useSeriesOffset) {
                        x0 = domainAxis.getCategorySeriesMiddle(
                                column - 1, dataset.getColumnCount(),
                                visibleRow, visibleRowCount,
                                this.itemMargin, dataArea,
                                plot.getDomainAxisEdge());
                    }
                    else {
                        x0 = domainAxis.getCategoryMiddle(column - 1,
                                getColumnCount(), dataArea,
                                plot.getDomainAxisEdge());
                    }
                    double y0 = rangeAxis.valueToJava2D(previous, dataArea,
                            plot.getRangeAxisEdge());

                    Line2D line = null;
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        line = new Line2D.Double(y0, x0, y1, x1);
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        line = new Line2D.Double(x0, y0, x1, y1);
                    }
                    g2.setPaint(getItemPaint(row, column));
                    g2.setStroke(getItemStroke(row, column));
                    g2.draw(line);
                }
            }
        }

        if (pass == 1) {
            Shape shape = getItemShape(row, column);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
            }

            if (getItemShapeVisible(row, column)) {
                if (getItemShapeFilled(row, column)) {
                    if (this.useFillPaint) {
                        g2.setPaint(getItemFillPaint(row, column));
                    }
                    else {
                        g2.setPaint(getItemPaint(row, column));
                    }
                    g2.fill(shape);
                }
                if (this.drawOutlines) {
                    if (this.useOutlinePaint) {
                        g2.setPaint(getItemOutlinePaint(row, column));
                    }
                    else {
                        g2.setPaint(getItemPaint(row, column));
                    }
                    g2.setStroke(getItemOutlineStroke(row, column));
                    g2.draw(shape);
                }
            }

            // draw the item label if there is one...
            if (isItemLabelVisible(row, column)) {
                if (orientation == PlotOrientation.HORIZONTAL) {
                    drawItemLabel(g2, orientation, dataset, row, column, y1,
                            x1, (value < 0.0));
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    drawItemLabel(g2, orientation, dataset, row, column, x1,
                            y1, (value < 0.0));
                }
            }

            // submit the current data point as a crosshair candidate
            int datasetIndex = plot.indexOf(dataset);
            updateCrosshairValues(state.getCrosshairState(),
                    dataset.getRowKey(row), dataset.getColumnKey(column),
                    value, datasetIndex, x1, y1, orientation);

            // add an item entity, if this information is being collected
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, shape);
            }
        }

    }
}
