package godClass.jhotdraw.undoManager;


import java.util.Vector;
import CH.ifa.draw.util.Undoable;

public class Undo {
	private Vector<Undoable> undoStack;
	private int maxStackCapacity;

	public Vector<Undoable> getUndoStack() {
		return undoStack;
	}

	public void setUndoStack(Vector<Undoable> undoStack) {
		this.undoStack = undoStack;
	}

	public int getMaxStackCapacity() {
		return maxStackCapacity;
	}

	public void setMaxStackCapacity(int maxStackCapacity) {
		this.maxStackCapacity = maxStackCapacity;
	}

	public Undoable peekUndo() {
		if (getUndoSize() > 0) {
			return (Undoable) undoStack.lastElement();
		} else {
			return null;
		}
	}

	/**
	* Throw NoSuchElementException if there is none
	*/
	public Undoable popUndo() {
		Undoable lastUndoable = peekUndo();
		undoStack.removeElementAt(getUndoSize() - 1);
		return lastUndoable;
	}

	/**
	* Returns the current size of undo buffer.
	*/
	public int getUndoSize() {
		return undoStack.size();
	}

	public boolean isUndoable() {
		if (getUndoSize() > 0) {
			return ((Undoable) undoStack.lastElement()).isUndoable();
		} else {
			return false;
		}
	}

	public void pushUndo(Undoable undoActivity) {
		if (undoActivity.isUndoable()) {
			if (getUndoSize() >= maxStackCapacity) {
				undoStack.removeElementAt(0);
			}
			undoStack.addElement(undoActivity);
		} else {
			undoStack = new Vector<Undoable>(maxStackCapacity);
		}
	}
}