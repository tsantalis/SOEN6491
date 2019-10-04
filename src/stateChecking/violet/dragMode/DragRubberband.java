package stateChecking.violet.dragMode;


import com.horstmann.violet.framework.Edge;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * @see stateChecking.violet.dragMode.GraphPanel#DRAG_RUBBERBAND
 */
public class DragRubberband extends DragMode {
	public int getDragMode() {
		return GraphPanel.DRAG_RUBBERBAND;
	}

	public void mouseDragged(Point2D mousePoint, boolean isCtrl, GraphPanel graphPanel) {
	}

	public void paintComponent(Graphics2D g2, GraphPanel graphPanel) {
		Color oldColor = g2.getColor();
		g2.setColor(GraphPanel.PURPLE);
		g2.draw(new Line2D.Double(graphPanel.getMouseDownPoint(), graphPanel.getLastMousePoint()));
		g2.setColor(oldColor);
	}

	public void mouseReleased(Object tool, Point2D mousePoint, GraphPanel graphPanel) {
		Edge prototype = (Edge) tool;
		Edge newEdge = (Edge) prototype.clone();
		if (mousePoint.distance(graphPanel.getMouseDownPoint()) > GraphPanel.CONNECT_THRESHOLD
				&& graphPanel.getGraph().connect(newEdge, graphPanel.getMouseDownPoint(), mousePoint)) {
			graphPanel.setModified(true);
			graphPanel.setSelectedItem(newEdge);
		}
	}
}