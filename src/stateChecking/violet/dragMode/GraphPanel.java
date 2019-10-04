/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package stateChecking.violet.dragMode;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.GraphFrame;
import com.horstmann.violet.framework.Grid;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.framework.PropertySheet;
import com.horstmann.violet.framework.ToolBar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * A panel to draw a graph
 */
public class GraphPanel extends JPanel implements MouseListener, MouseMotionListener
{

	private Graph graph;
	private Grid grid;
	private GraphFrame frame;
	private ToolBar toolBar;

	private double zoom;
	private double gridSize;
	private boolean hideGrid;
	private boolean modified;

	private Object lastSelected;
	private Set selectedItems;

	private Point2D lastMousePoint;
	private Point2D mouseDownPoint;   
	private DragMode dragMode = new DragNone();

	public static final int DRAG_NONE = 0;
	public static final int DRAG_MOVE = 1;
	public static final int DRAG_RUBBERBAND = 2;
	public static final int DRAG_LASSO = 3;

	private static final int GRID = 10;

	public static final int CONNECT_THRESHOLD = 8;

	public static final Color PURPLE = new Color(0.7f, 0.4f, 0.7f);
	/**
	 * Constructs a graph.
	 * @param aToolBar the tool bar with the node and edge tools
	 */
	public GraphPanel(ToolBar aToolBar)
	{
		grid = new Grid();
		gridSize = GRID;
		grid.setGrid((int) gridSize, (int) gridSize);
		zoom = 1;
		toolBar = aToolBar;
		setBackground(Color.WHITE);

		selectedItems = new HashSet();

		addMouseListener(this);

		addMouseMotionListener(this);
	}

	/**
	 * Edits the properties of the selected graph element.
	 */
	public void editSelected()
	{
		Object edited = lastSelected;
		if (lastSelected == null)
		{
			if (selectedItems.size() == 1)
				edited = selectedItems.iterator().next();
			else
				return;
		}

		PropertySheet sheet = new PropertySheet(edited, this);
		sheet.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent event)
			{
				graph.layout();
				repaint();
			}
		});
		JOptionPane.showInternalMessageDialog(this, sheet, 
				ResourceBundle.getBundle("com.horstmann.violet.framework.EditorStrings").getString("dialog.properties"),            
				JOptionPane.QUESTION_MESSAGE);
		setModified(true);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(zoom, zoom);
		Rectangle2D bounds = getBounds();
		Rectangle2D graphBounds = graph.getBounds(g2);
		if (!hideGrid) grid.draw(g2, new Rectangle2D.Double(0, 0, 
				Math.max(bounds.getMaxX() / zoom, graphBounds.getMaxX()), 
				Math.max(bounds.getMaxY() / zoom, graphBounds.getMaxY())));
		graph.draw(g2, grid);

		Iterator iter = selectedItems.iterator();
		Set toBeRemoved = new HashSet();
		while (iter.hasNext())
		{
			Object selected = iter.next();                 

			if (!graph.getNodes().contains(selected)
					&& !graph.getEdges().contains(selected)) 
			{
				toBeRemoved.add(selected);
			}
			else if (selected instanceof Node)
			{
				Rectangle2D grabberBounds = ((Node) selected).getBounds();
				drawGrabber(g2, grabberBounds.getMinX(), grabberBounds.getMinY());
				drawGrabber(g2, grabberBounds.getMinX(), grabberBounds.getMaxY());
				drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds.getMinY());
				drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds.getMaxY());
			}
			else if (selected instanceof Edge)
			{
				Line2D line = ((Edge) selected).getConnectionPoints();
				drawGrabber(g2, line.getX1(), line.getY1());
				drawGrabber(g2, line.getX2(), line.getY2());
			}
		}

		iter = toBeRemoved.iterator();
		while (iter.hasNext())      
			removeSelectedItem(iter.next());                 

		dragMode.paintComponent(g2, this);
	}

	/**
	 * Draws a single "grabber", a filled square
	 * @param g2 the graphics context
	 * @param x the x coordinate of the center of the grabber
	 * @param y the y coordinate of the center of the grabber
	 */
	public static void drawGrabber(Graphics2D g2, double x, double y)
	{
		final int SIZE = 5;
		Color oldColor = g2.getColor();
		g2.setColor(PURPLE);
		g2.fill(new Rectangle2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE));
		g2.setColor(oldColor);
	}

	/**
	 * Sets or resets the modified flag for this graph
	 * @param newValue true to indicate that the graph has been modified
	 */
	public void setModified(boolean newValue)
	{
		modified = newValue;

		if (frame == null)
		{
			Component parent = this;
			do
			{
				parent = parent.getParent();
			}
			while (parent != null && !(parent instanceof GraphFrame));
			if (parent != null) frame = (GraphFrame) parent;
		}
		if (frame != null)
		{
			String title = frame.getFileName();
			if (title != null)
			{
				if (modified)
				{
					if (!frame.getTitle().endsWith("*")) frame.setTitle(title + "*");
				}
				else frame.setTitle(title);
			}
		}
	}

	public void addSelectedItem(Object obj)
	{
		lastSelected = obj;      
		selectedItems.add(obj);
	}

	public void removeSelectedItem(Object obj)
	{
		if (obj == lastSelected)
			lastSelected = null;
		selectedItems.remove(obj);
	}

	public void setSelectedItem(Object obj)
	{
		selectedItems.clear();
		lastSelected = obj;
		if (obj != null) selectedItems.add(obj);
	}

	private void clearSelection()
	{
		selectedItems.clear();
		lastSelected = null;
	}

	public void mousePressed(MouseEvent event)
	{
		requestFocus();
		final Point2D mousePoint = new Point2D.Double(event.getX() / zoom,
				event.getY() / zoom);
		boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0; 
		Node n = graph.findNode(mousePoint);
		Edge e = graph.findEdge(mousePoint);
		Object tool = toolBar.getSelectedTool();
		if (event.getClickCount() > 1
				|| (event.getModifiers() & InputEvent.BUTTON1_MASK) == 0)
			// double/right-click
		{
			if (e != null)
			{
				setSelectedItem(e);
				editSelected();
			}
			else if (n != null)
			{
				setSelectedItem(n);
				editSelected();
			}
		}
		else if (tool == null) // select
		{
			if (e != null)
			{
				setSelectedItem(e);
			}
			else if (n != null)
			{
				if (isCtrl)
					addSelectedItem(n);
				else if (!selectedItems.contains(n))
					setSelectedItem(n);
				setDragMode(DRAG_MOVE);
			}
			else
			{
				if (!isCtrl)
					clearSelection();
				setDragMode(DRAG_LASSO);
			}
		}
		else if (tool instanceof Node)
		{
			Node prototype = (Node) tool;
			Node newNode = (Node) prototype.clone();
			boolean added = graph.add(newNode, mousePoint);
			if (added)
			{
				setModified(true);
				setSelectedItem(newNode);
				setDragMode(DRAG_MOVE);
			}
			else if (n != null)
			{
				if (isCtrl)
					addSelectedItem(n);
				else if (!selectedItems.contains(n))
					setSelectedItem(n);
				setDragMode(DRAG_MOVE);
			}
		}
		else if (tool instanceof Edge)
		{
			if (n != null) setDragMode(DRAG_RUBBERBAND);
		}

		lastMousePoint = mousePoint;
		mouseDownPoint = mousePoint;
		repaint();
	}

	public void mouseReleased(MouseEvent event)
	{
		Point2D mousePoint = new Point2D.Double(event.getX() / zoom,
				event.getY() / zoom);
		Object tool = toolBar.getSelectedTool();
		dragMode.mouseReleased(tool, mousePoint, this);

		setDragMode(DRAG_NONE);

		revalidate();
		repaint();
	}

	public void mouseClicked(MouseEvent arg0) {

	}

	public void mouseEntered(MouseEvent arg0) {

	}

	public void mouseExited(MouseEvent arg0) {

	}

	public void mouseDragged(MouseEvent event)
	{
		Point2D mousePoint = new Point2D.Double(event.getX() / zoom, 
				event.getY() / zoom);
		boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0; 

		dragMode.mouseDragged(mousePoint, isCtrl, this);

		lastMousePoint = mousePoint;
		repaint();
	}

	public void mouseMoved(MouseEvent arg0) {

	}

	public void setDragMode(int dragMode) {
		switch (dragMode) {
		case DRAG_LASSO:
			this.dragMode = new DragLasso();
			break;
		case DRAG_MOVE:
			this.dragMode = new DragMove();
			break;
		case DRAG_RUBBERBAND:
			this.dragMode = new DragRubberband();
			break;
		case DRAG_NONE:
			this.dragMode = new DragNone();
			break;
		default:
			this.dragMode = null;
			break;
		}
	}

	public int getDragMode() {
		return dragMode.getDragMode();
	}

	public Point2D getMouseDownPoint() {
		return mouseDownPoint;
	}

	public Graph getGraph() {
		return graph;
	}

	public Object getLastSelected() {
		return lastSelected;
	}

	public Point2D getLastMousePoint() {
		return lastMousePoint;
	}

	public Set getSelectedItems() {
		return selectedItems;
	}
}