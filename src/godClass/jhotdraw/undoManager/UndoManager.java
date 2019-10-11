/*
 * @(#)UndoManager.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package godClass.jhotdraw.undoManager;

import CH.ifa.draw.util.Undoable;

import java.util.*;

/**
 * This class manages all the undoable commands. It keeps track of all 
 * the modifications done through user interactions.
 *
 * @version <$CURRENT_VERSION$>
 */
public class UndoManager {
	private Undo undo = new Undo();

	/**
	 * Maximum default buffer size for undo and redo stack
	 */
	public static final int DEFAULT_BUFFER_SIZE = 20;

	/**
	 * Collection of undo activities
	 */
	private Vector<Undoable> redoStack;
		
	public UndoManager() {
		this(DEFAULT_BUFFER_SIZE);
	}

	public UndoManager(int newUndoStackSize) {
		undo.setMaxStackCapacity(newUndoStackSize);
		undo.setUndoStack(new Vector<Undoable>(undo.getMaxStackCapacity()));
		redoStack = new Vector<Undoable>(undo.getMaxStackCapacity());
	}

	public void pushUndo(Undoable undoActivity) {
		undo.pushUndo(undoActivity);
	}

	public void pushRedo(Undoable redoActivity) {
		if (redoActivity.isRedoable()) {
			// If buffersize exceeds, remove the oldest command
			if (getRedoSize() >= undo.getMaxStackCapacity()) {
				redoStack.removeElementAt(0);
			}
			// add redo activity only if it is not already the last
			// one in the buffer
			if ((getRedoSize() == 0) || (peekRedo() != redoActivity)) {
				redoStack.addElement(redoActivity);
			}
		}
		else {
			// a not undoable activity clears the tack because
			// the last activity does not correspond with the
			// last undo activity
			redoStack = new Vector<Undoable>(undo.getMaxStackCapacity());
		}
	}

	public boolean isUndoable() {
		return undo.isUndoable();
	}
	
	public boolean isRedoable() {
		if (getRedoSize() > 0) {
			return ((Undoable)redoStack.lastElement()).isRedoable();
		}
		else {
			return false;
		}
	}

	protected Undoable peekUndo() {
		return undo.peekUndo();
	}

	protected Undoable peekRedo() {
		if (getRedoSize() > 0) {
			return (Undoable) redoStack.lastElement();
		}
		else {
			return null;
		}
	}

	/**
	 * Returns the current size of undo buffer.
	 */
	public int getUndoSize() {
		return undo.getUndoSize();
	}

	/**
	 * Returns the current size of redo buffer.
	 */
	public int getRedoSize() {
		return redoStack.size();
	}

	/**
	 * Throw NoSuchElementException if there is none
	 */
	public Undoable popUndo() {
		return undo.popUndo();
	}

	/**
	 * Throw NoSuchElementException if there is none
	 */
	public Undoable popRedo() {
		// Get the last element - throw NoSuchElementException if there is none
		Undoable lastUndoable = peekRedo();

		// Remove it from undo collection
		redoStack.removeElementAt(getRedoSize() - 1);

		return lastUndoable;
	}

	public void clearUndos() {
		clearStack(undo.getUndoStack());
	}

	public void clearRedos() {
		clearStack(redoStack);
	}
	
	protected void clearStack(Vector<Undoable> clearStack) {
		clearStack.removeAllElements();
	}
}
