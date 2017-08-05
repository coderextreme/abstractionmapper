package motion;

import java.awt.*;
import java.rmi.*;
import javax.swing.*;
import icbm.*;

class BrowseRelationship {
	MUDRemote first = null;
	MUDRemote second = null;
	MUDRemote relationship = null;
	String relationName = "connected";
	public BrowseRelationship(MUDRemote first, MUDRemote second, MUDRemote relationship, String relationName) {
		this.first = first;
		this.second = second;
		this.relationship = relationship;
		this.relationName = relationName;
	}
	public void paint(Graphics g, MUDRemote root) throws RemoteException {
		if (first != null && second != null && root.contains(second) && root.contains(first)) {
			try {
				JComponent c1 = ((MUDObject)first).getComponent();
				JComponent c2 = ((MUDObject)second).getComponent();
				if (c1 != null && c2 != null) {
					Rectangle r1 = c1.getBounds();
					Rectangle r2 = c2.getBounds();
					// Point p1 = c1.getLocationOnScreen();
					// Point p2 = c2.getLocationOnScreen();
					// Container c = c1.getParent();
					// Point w1 = c.getLocationOnScreen();
					g.setColor(Color.black);
					/*
					g.drawLine(
						r1.x + r1.width / 2 + 15,
						r1.y + r1.height / 2 + 52,
						r2.x + r2.width / 2 + 15,
						r2.y + r2.height / 2 + 52
						);
					*/
					g.drawLine(
						r1.x + r1.width / 2,
						r1.y + r1.height / 2,
						r2.x + r2.width / 2,
						r2.y + r2.height / 2
						);
				}
			} catch (IllegalComponentStateException e) {
				e.printStackTrace();
			}
		}
	}
}
