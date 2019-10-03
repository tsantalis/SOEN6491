package stateChecking.violet.dragMode;


import java.awt.geom.Point2D;

public abstract class DragMode {
	public abstract int getDragMode();

	public abstract void mouseDragged(Point2D mousePoint, boolean isCtrl, GraphPanel graphPanel);
}