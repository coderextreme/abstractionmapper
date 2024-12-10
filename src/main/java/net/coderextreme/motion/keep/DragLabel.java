package net.coderextreme.motion.keep;

import javax.swing.Icon;
import javax.swing.JLabel;

import net.coderextreme.motion.Motion;

class DragLabel extends JLabel
/*
	 implements DragSourceListener, DragGestureListener
*/
{
	Motion m = null;
	//DragSource ds = null;
	DragLabel(Icon image, Motion m)  {
		super(image);
		this.m = m;
/*
		ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
*/
	}
	DragLabel(String text, Motion m)  {
		super(text);
		this.m = m;
/*
		ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
*/
	}
	DragLabel(String text, Icon icon, int horizontalAlignment, Motion m) {
		super(text, icon, horizontalAlignment);
		this.m = m;
/*
		ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
*/
	}
/*
	public void dragGestureRecognized( DragGestureEvent event) {
		MUDRemote bo = (MUDRemote)getClientProperty("object");
		ImageIcon ii = m.loadIcon(bo);
		event.getDragSource().startDrag(
			event, DragSource.DefaultMoveDrop, ii.getImage(),
			new Point(0,0),
			new MTransferable(bo), (JLabel)bo.getComponent());
	}
	public void dragEnter (DragSourceDragEvent event) {
		System.out.println("Entered dragging area");
	}
	public void dragExit (DragSourceEvent event) {
		System.out.println("Exited dragging area");
	}
	public void dragOver (DragSourceDragEvent event) {
	}
	public void dragDropEnd (DragSourceDropEvent event) {
		System.out.println("end drop");
	}
	public void dropActionChanged (DragSourceDragEvent event) {
		System.out.println("action changed");
	}
*/
}
