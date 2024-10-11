package net.coderextreme.motion.gedda;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class object_node extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String identifier = null;
	private relationship_list relationships = new relationship_list();
	private property_list properties = new property_list();
	Map<String,JTextField> httf = new Hashtable<String,JTextField>();
	private ImageIcon background = null;
	//private d_tech_list objs = null;
	public object_node(String i) {
		identifier = i;
	}
	public String id() {
		return identifier;
	}
	public relationship_list rel_list() {
		 return relationships;
	}
	public String get(String prop_name)
	{
		return properties.find(prop_name);
	}

	public void put(String prop_name, String value)
	{
		properties.put(prop_name, value);
	}
	public void remove(String prop_name)
	{
		properties.remove(prop_name);
	}
	public Set<String> keys() {
		return properties.keySet();
	}
	public JTextField getField(String prop) {
		return httf.get(prop);
	}
	public void setField(String prop, JTextField tf) {
		httf.put(prop, tf);
	}
	public void copy_properties(object_node newon)
	{
		/* copy values of object */
		for (String key : properties.keySet()){
			String value = properties.get(key);
			newon.properties.put(key, value);
		}
	}
	Point get_xy()
	{

		String s;
		s = this.properties.get(oimove.PROP_X);
		int x = Integer.parseInt(s);
		s = this.properties.get(oimove.PROP_Y);
		int y = Integer.parseInt(s);
		return new Point(x, y);
	}
	Dimension get_wh() {
		int w = 64;
		int h = 64;
		String s;
		s = this.properties.get(oimove.PROP_WIDTH);
		if (s != null) {
			// System.err.println("Width "+s);
			w = Integer.parseInt(s);
		}
		s = this.properties.get(oimove.PROP_HEIGHT);
		if (s != null) {
			// System.err.println("Height "+s);
			h = Integer.parseInt(s);
		}
		return new Dimension(w, h);
	}
	public object_node parent() {
		return object_list.OBJECT_LIST.find(properties.get(oimove.REL_PARENTAL));
	}
	public void set_parent(object_node parent) {
		properties.put(oimove.REL_PARENTAL, parent.id());
	}
	public object_node root() {
		object_node parent = parent();
		if (parent == null) {
			return this;
		} else if (parent == parent.parent()) {
			return parent;
		} else {
			return parent.root();
		}
	}

	public Rectangle getBounds() {
		Dimension wh = get_wh();
		Rectangle r = new Rectangle(0, 0, wh.width, wh.height);
		return r;
	}
	public Dimension getPreferredSize() {
		Dimension wh = get_wh();
		Dimension d = new Dimension(wh.width, wh.height);
		return d;
	}

	public Dimension getMinimumSize() {
		Dimension wh = get_wh();
		Dimension d = new Dimension(wh.width, wh.height);
		return d;
	}

	public JFrame getFrame() {
		Container c = null;
		do {
			c = getParent();
		} while (c != null && c.getParent() != null);
		return (JFrame)c;
	}
	public object_node next_child(object_node child) {
		object_node parent = parent();
		for (int c = 0; parent != null && c > parent.getComponentCount(); c++) {
			if (parent.getComponent(c) == child) {
				return (object_node)parent.getComponent(c+1);
			}
		}
		return null;
	}

	Map<String,ImageIcon> icons = new Hashtable<String,ImageIcon>();
	public void paint(Graphics g) {
		super.paint(g);
		if (background != null) {
			g.drawImage(background.getImage(), 0, 0,
				background.getIconWidth(),
				background.getIconHeight(),
				this);
		}
		object_node on = this;
		List<object_node> ol = object_list.OBJECT_LIST.find_relatees(on, oimove.REL_PARENTAL);
		Iterator<object_node> roi = ol.iterator();
		while (roi.hasNext()) {
			System.err.println("Found child");
			object_node ro = roi.next();
			// System.err.println("viewing "+ro.id());
			Point pxy = ro.get_xy();
			Dimension wh = ro.get_wh();
			String icon_name = (String)ro.get(oimove.PROP_ICON);
			if (icon_name != null) {
				// cache icons for speed
				ImageIcon ii = (ImageIcon)icons.get(icon_name);
				if (ii == null) {
					ii = new ImageIcon(icon_name);
					icons.put(icon_name, ii);
				}
				System.err.println("Drew image");
				g.drawImage(ii.getImage(), 
					pxy.x, pxy.y,
					wh.width, wh.height, 
					this);
			}
			String label = ro.get(oimove.PROP_LABEL);
			if (label != null) {
				g.setColor(ro.get_foreground_color());
				g.drawString(label, pxy.x+wh.width/2-label.length()/2*7, pxy.y+wh.height-2);
			}
			System.err.println(oimove.selected(ro));
			if (oimove.selected(ro)) {
					Point p = ro.get_xy();
					Dimension d = ro.get_wh();
					g.setColor(Color.yellow);
					g.drawRect(p.x-1, p.y-1,
						d.width+2, d.height+1);
			}
		}	
	/*
		if (label != null) {
			g.drawString(label, 0, 0);
		}
	*/
	}
	private Color get_foreground_color() {
		return this.getForeground();
	}
	public void set_background(String s) {
		if (s != null) {
			background = new ImageIcon(s);
		}
	}
	public void setIcon(String name) {
		properties.put(oimove.PROP_ICON, name);
	}
//	public d_tech_list data() {
//		return objs;
//	}
//	public void set_data(d_tech_list objs) {
//		this.objs = objs;
//	}
	public property_list prop_list() {
		return this.properties;
	}
	public void set_background_color(String color) {
		String s = color;
		if (s != null) {
			int s0 = 0;
			int s1;
			int s2;
			String rcs;
			String gcs;
			String bcs;
			int red;
			int green;
			int blue;
			s1 = s.indexOf(' ', s0);
			rcs = s.substring(s0, s1);
			red = Integer.parseInt(rcs);
			s2 = s.indexOf(' ', s1+1);
			gcs = s.substring(s1+1, s2);
			green = Integer.parseInt(gcs);
			bcs = s.substring(s2+1);
			blue = Integer.parseInt(bcs);
			this.setBackground(new Color(red, green, blue));
		}
	}
	public void set_foreground_color(String color) {

		String s = color;

		if (s!= null) {
			int s0 = 0;
			int s1;
			int s2;
			String rcs;
			String gcs;
			String bcs;
			int red;
			int green;
			int blue;
			s1 = s.indexOf(' ', s0);
			rcs = s.substring(s0, s1);
			red = Integer.parseInt(rcs);
			s2 = s.indexOf(' ', s1+1);
			gcs = s.substring(s1+1, s2);
			green = Integer.parseInt(gcs);
			bcs = s.substring(s2+1);
			blue = Integer.parseInt(bcs);
	
			this.setForeground(new Color(red, green, blue));
		}
	}
}
