package stateChecking.violet.dragMode;


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
}