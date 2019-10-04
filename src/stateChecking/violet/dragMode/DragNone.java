package stateChecking.violet.dragMode;


import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * @see stateChecking.violet.dragMode.GraphPanel#DRAG_NONE
 */
public class DragNone extends DragMode {
	public int getDragMode() {
		return GraphPanel.DRAG_NONE;
	}

	public void mouseDragged(Point2D mousePoint, boolean isCtrl, GraphPanel graphPanel) {
	}

	public void paintComponent(Graphics2D g2, GraphPanel graphPanel) {
	}
}