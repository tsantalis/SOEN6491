package stateChecking.violet.dragMode;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import com.horstmann.violet.framework.Node;

/**
 * @see stateChecking.violet.dragMode.GraphPanel#DRAG_LASSO
 */
public class DragLasso extends DragMode {
	public int getDragMode() {
		return GraphPanel.DRAG_LASSO;
	}

	public void mouseDragged(Point2D mousePoint, boolean isCtrl, GraphPanel graphPanel) {
		double x1 = graphPanel.getMouseDownPoint().getX();
		double y1 = graphPanel.getMouseDownPoint().getY();
		double x2 = mousePoint.getX();
		double y2 = mousePoint.getY();
		Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2),
				Math.abs(y1 - y2));
		Iterator iter = graphPanel.getGraph().getNodes().iterator();
		while (iter.hasNext()) {
			Node n = (Node) iter.next();
			Rectangle2D bounds = n.getBounds();
			if (!isCtrl && !lasso.contains(bounds)) {
				graphPanel.removeSelectedItem(n);
			} else if (lasso.contains(bounds)) {
				graphPanel.addSelectedItem(n);
			}
		}
	}

	public void paintComponent(Graphics2D g2, GraphPanel graphPanel) {
		Color oldColor = g2.getColor();
		g2.setColor(GraphPanel.PURPLE);
		double x1 = graphPanel.getMouseDownPoint().getX();
		double y1 = graphPanel.getMouseDownPoint().getY();
		double x2 = graphPanel.getLastMousePoint().getX();
		double y2 = graphPanel.getLastMousePoint().getY();
		Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2),
				Math.abs(y1 - y2));
		g2.draw(lasso);
		g2.setColor(oldColor);
	}

	public void mouseReleased(Object tool, Point2D mousePoint, GraphPanel graphPanel) {
	}
}