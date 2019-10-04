package stateChecking.violet.dragMode;


import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import com.horstmann.violet.framework.Node;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

/**
 * @see stateChecking.violet.dragMode.GraphPanel#DRAG_MOVE
 */
public class DragMove extends DragMode {
	public int getDragMode() {
		return GraphPanel.DRAG_MOVE;
	}

	public void mouseDragged(Point2D mousePoint, boolean isCtrl, GraphPanel graphPanel) {
		if (graphPanel.getLastSelected() instanceof Node) {
			Node lastNode = (Node) graphPanel.getLastSelected();
			Rectangle2D bounds = lastNode.getBounds();
			double dx = mousePoint.getX() - graphPanel.getLastMousePoint().getX();
			double dy = mousePoint.getY() - graphPanel.getLastMousePoint().getY();
			Iterator iter = graphPanel.getSelectedItems().iterator();
			while (iter.hasNext()) {
				Object selected = iter.next();
				if (selected instanceof Node) {
					Node n = (Node) selected;
					bounds.add(n.getBounds());
				}
			}
			dx = Math.max(dx, -bounds.getX());
			dy = Math.max(dy, -bounds.getY());
			iter = graphPanel.getSelectedItems().iterator();
			while (iter.hasNext()) {
				Object selected = iter.next();
				if (selected instanceof Node) {
					Node n = (Node) selected;
					if (!graphPanel.getSelectedItems().contains(n.getParent())) {
						n.translate(dx, dy);
					}
				}
			}
		}
	}

	public void paintComponent(Graphics2D g2, GraphPanel graphPanel) {
	}

	public void mouseReleased(Object tool, Point2D mousePoint, GraphPanel graphPanel) {
		graphPanel.getGraph().layout();
		graphPanel.setModified(true);
	}
}