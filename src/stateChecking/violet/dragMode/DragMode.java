package stateChecking.violet.dragMode;


import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public abstract class DragMode {
	public abstract int getDragMode();

	public abstract void mouseDragged(Point2D mousePoint, boolean isCtrl, GraphPanel graphPanel);

	public abstract void paintComponent(Graphics2D g2, GraphPanel graphPanel);
}