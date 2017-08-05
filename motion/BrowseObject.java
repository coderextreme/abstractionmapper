package motion;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.rmi.*;
import icbm.*;

public class BrowseObject extends AbstractBorder {
	private String name = "Name";
	JComponent comp = null;
	int ss = 5; // handles square width
	Motion motion = null;
	Motion pmotion = null;
	Hashtable ht = new Hashtable();
	Hashtable rels = new Hashtable();
	Hashtable ht_tf = new Hashtable();
	boolean added = false;
	JDialog diag = null;
	public BrowseObject(String name) throws RemoteException {
		this.name = name;
	}
	public BrowseObject(Motion motion) {
		this.motion = motion;
	}
	public void setDialog(JDialog jd) {
		diag = jd;
	}
	public JDialog getDialog() {
		return diag;
	}
	public void setMotion (Motion frame) {
		motion = frame;
	}
	public Motion getMotion () {
		return motion;
	}
	public Motion getPlatformMotion () {
		return pmotion;
	}
	public void setPlatformMotion (Motion motion) {
		pmotion = motion;
	}
	public void setComponent(JComponent c) {
		comp = c;
		comp.putClientProperty("object", this);
		comp.setBorder(this);
	}
	public void copyFrom(BrowseObject o) {
		// name = o.name;
		ss = o.ss;
		ht = (Hashtable)o.ht.clone();
		rels = (Hashtable)o.rels.clone();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) throws RemoteException {
		this.name = name;
	}
	public String id() {
		return name;
	}
	public JComponent getComponent() {
		return comp;
	}
	public boolean isBorderOpaque() {
		return false;
	}
	public Insets getBorderInsets(Component c) {
		return new Insets(ss, ss, ss, ss);
	}
	public void putProp(String index) {
		ht.put(index, index);
	}
	public Enumeration getPropertyKeys() {
		return ht.keys();
	}
	public boolean isPropertySet(String index) {
		boolean results = ht.containsKey(index);
		System.err.println("Results for "+index+" "+results);
		return results;
	}
	public JTextField getField(String prop) {
		return (JTextField)ht_tf.get(prop);
	}
	public void setField(String prop, JTextField tf) {
		ht_tf.put(prop, tf);
	}
	public String get(String prop) {
		return (String)rels.get(prop);
	}
	public void putInt(String prop, int value) {
		rels.put(prop, String.valueOf(value));
	}
	public int getInt(String prop) {
		
		String s = (String)rels.get(prop);
		if (s == null) {
			return 0;
		}
		int i = Integer.parseInt(s);
		return i;
	}
	public void put(String prop, String value) {
		rels.put(prop, value);
	}
	public void remove(String prop) {
		rels.remove(prop);
	}
	public Enumeration keys() {
		return rels.keys();
	}

	public synchronized void paintBorder(Component comp, Graphics g, int x, int y, int width, int height) {
		Point p = new Point(x, y);
		Dimension d = new Dimension(width, height);
		g.setColor(Color.black);
		JLabel lab = (JLabel)comp;
                g.drawImage(((ImageIcon)lab.getIcon()).getImage(),
                                        p.x, p.y,
                                        width, height,
                                        comp);
		g.setColor(lab.getForeground());
		if (lab.getText() != null) {
			g.drawString(lab.getText(), p.x+width/2-lab.getText().length()/2*7, p.y+height-2);
		}

		if (motion.selectionContains(comp)) {
			// left side from top to bottom
			g.fillRect(p.x, p.y, ss, ss);
			g.fillRect(p.x, p.y+(d.height-ss)/2, ss, ss);
			g.fillRect(p.x, p.y+d.height-ss, ss, ss);

			// right side from top to bottom
			g.fillRect(p.x+d.width-ss, p.y, ss, ss);
			g.fillRect(p.x+d.width-ss, p.y+(d.height-ss)/2, ss, ss);
			g.fillRect(p.x+d.width-ss, p.y+d.height-ss, ss, ss);

			// top side
			g.fillRect(+p.x+(d.width-ss)/2, p.y, ss, ss);
			
			// bottom side
			g.fillRect(+p.x+(d.width-ss)/2, p.y+d.height-ss, ss, ss);
		}
	}
}
