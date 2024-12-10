package net.coderextreme.motion.gedda;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

class rubber {
	public void setGraphics(Graphics g) {
		g.setXORMode(Color.blue);
	}
}

class dotted {
	public void setGraphics(Graphics g) {
		g.setXORMode(Color.green);
	}
};

class selected_line {
	public void setGraphics(Graphics g) {
		g.setXORMode(Color.red);
	}
}


public class oimove extends JFrame
		implements MouseMotionListener, MouseListener, ActionListener {
	public static final String PROP_X = "x";
	public static final String PROP_Y = "y";
	public static final String PROP_WIDTH = "width";
	public static final String PROP_HEIGHT = "height";
	String PROP_BACKGROUND = "bitmap";
	public static final String PROP_ICON = "icon";
	public static final String PROP_LABEL = "label";
	public static final String PROP_NAME = "name";
	public static final String REL_PARENTAL = "parental";
	String PROP_LINE = "next_point";
	String PROP_SUBJECT = "subject";
	String PROP_FG_COLOR = "foreground_color";
	String PROP_BG_COLOR = "background_color";
	String ENVROOT = "MROOT";

	/* globals */

	d_tech_list object_selection;
	d_tech_list objectsOnWindow;
	object_node Grel_sel;
	object_node Grel_sel2;
	object_node Gselected_line;
	object_node current_property_object;
	object_node top;
	static public oimove SELECTED_OIMOVE;
	static public Set<oimove> OIMOVES = Collections.synchronizedSet(new HashSet<>(3));


	/*
	static OI_actions_rec myactions[] = {
		{"press",	null, object_selection, d_tech_list::press },
		{"drag",	drag,		null, null_PMF },
		{"release",	release,	null, null_PMF },
		{"selectionAdd", null, object_selection, d_tech_list::selection_add },
		{"resize",	resize,		null, null_PMF },
		{"dragOnto",	drag_onto,	null, null_PMF },
		{"pressNone",	null,	object_selection, d_tech_list::press_none },
		{"view",	view,		null, null_PMF },
		{"raise",	raise,		null, null_PMF },
		{"lower",	lower,		null, null_PMF },
		{"expose",	expose,		null, null_PMF },
		{"props",	props,		null, null_PMF },
		{"moveTo",	null,	object_selection, d_tech_list::moveto },
		{"linkTo",	null,	object_selection, d_tech_list::linkto },
		{"unlink",	objunlink,	null, null_PMF },
		{"copyTo",	null,	object_selection, d_tech_list::copyto },
		{"makeRelation",	make_rel,	null, null_PMF },
		{"pressRel",	null, relation_selection, d_tech_list::press },
		{"selectRel",	select_rel,	null, null_PMF },
		{"addPoint",	add_a_point,	null, null_PMF },
		{"endLine",	end_line,	null, null_PMF },
		{"motionLine",	motion_line,	null, null_PMF },
		{"selectLine",	select_line,	null, null_PMF },
		{"browse",	display_browse_box,	null, null_PMF },
		{"undo",	undo,		null, null_PMF },
		{"redo",	redo,		null, null_PMF },
	};
	*/
	public oimove() {
		objectsOnWindow = new d_tech_list(this);
		object_selection = new d_tech_list(this);
		OIMOVES.add(this);
	}
	
	public static boolean selected(object_node on) {
		for (oimove oim : OIMOVES) {
			if (oim.object_selection.contains(on)) {
				return true;
			}
		}
		return false;
	}
	static public void main (String [] argv)
	{
		oimove oim2;
		if (argv.length == 0) {
			oim2 = createWindow(null);
		} else {
			oim2 = createWindow(argv[0]);
		}	
		oim2.setmenu(oim2);
		for (int i = 1; i < argv.length; i++) {
			createWindow(argv[i]);
		}
	}
	public static oimove createWindow(String id) {
		oimove oim2 = new oimove();
		object_node appWindow = null;
		if (id == null) {
			appWindow = oim2.load_wins(object_list.OBJECT_LIST.new_object(oim2));
		} else {
			appWindow = oim2.load_wins(object_list.OBJECT_LIST.insert_object(id, oim2));
		}
		oim2.setTitle("Object Id: "+appWindow.id());
		oim2.getContentPane().setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(appWindow);
		oim2.getContentPane().add(scrollPane,  BorderLayout.CENTER);
		oim2.top(appWindow);
		oim2.pack();
		oim2.setVisible(true);
		return oim2;
	}
	public void top(object_node on) {
		top = on;
		top.addMouseListener(this);
		top.addMouseMotionListener(this);
		top.registerKeyboardAction((@SuppressWarnings("unused") ActionEvent ae) -> {
                    oimove.this.props();
                }, "Props", KeyStroke.getKeyStroke('p'), JComponent.WHEN_IN_FOCUSED_WINDOW);
		top.registerKeyboardAction((@SuppressWarnings("unused") ActionEvent ae) -> {
                    oimove.this.view();
                }, "View", KeyStroke.getKeyStroke('o'), JComponent.WHEN_IN_FOCUSED_WINDOW);
		top.registerKeyboardAction((@SuppressWarnings("unused") ActionEvent ae) -> {
                    oimove.this.createCopy(last_x, last_y);
                }, "Instantiate", KeyStroke.getKeyStroke('i'), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
	protected void createCopy(long x, long y) {
		Iterator<oimove> oimoves = OIMOVES.iterator();
		long minx = Long.MAX_VALUE;
		long miny = Long.MAX_VALUE;
		while (oimoves.hasNext()) {
			oimove oim = oimoves.next();
			for (object_node on : oim.object_selection) {
				Point p = on.get_xy();
				if (p.x < minx) {
					minx = p.x;
				}
				if (p.y < miny) {
					miny = p.y;
				}
			}
		}
		oimoves = OIMOVES.iterator();
		while (oimoves.hasNext()) {
			oimove oim = oimoves.next();
			oim.object_selection.copyto(this, x-minx, y-miny);
		}
	}

	public void setmenu(Frame f) {
		MenuBar mb = new MenuBar();
		Menu m = new Menu("File");
		MenuItem mi = new MenuItem("Save");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Save As");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Restore");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Open");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Close Browse Windows");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Close All Windows");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Hide Browse Windows");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Show Results Window");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Show Graph Window");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Change Defaults");
		mi.addActionListener(this);
		m.add(mi);
		mb.add(m);

		m = new Menu("Create");
		mi = new MenuItem("Processor");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Network");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Sub-project");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Site");
		mi.addActionListener(this);
		m.add(mi);
		mb.add(m);

		m = new Menu("Browse");
		mi = new MenuItem("Technologies");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Locations");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Projects");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Functions");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Stores");
		mi.addActionListener(this);
		m.add(mi);
		mi = new MenuItem("Networks");
		mi.addActionListener(this);
		m.add(mi);
		mb.add(m);
		f.setMenuBar(mb);
	}

	void view()
	{
		object_node on;

		Iterator<object_node> sit = object_selection.iterator();
		if (sit.hasNext()) {
			on =  sit.next();
			if (on != null) {
				on = subject_node(on);
				if (on != null) {
					createWindow(on.id());
				}
			}
		}
	}

	void
	set_dim(object_node objp)
	{
		int w, h;
		String s;
		object_node on = objp;
		Dimension d = objp.getSize();

		if (on != null) {
			s=""+d.width;
			on.put(PROP_WIDTH, s);

			s=""+d.height;
			on.put(PROP_HEIGHT, s);
		}
	}

/*
	void
	remove_reference(object_node o, object_node on)
	{
		d_tech_list objs;

		objs = (d_tech_list )on.data();
		objs.remove(o);
	}
*/

//	void
//	set_datas(object_node o, object_node on)
//	{
//		d_tech_list objs;
//
//		objs = (d_tech_list )on.data();
//		if (objs == null) {
//			objs = new d_tech_list(this);
//			on.set_data(objs);
//		}
//		if (!objs.find(o))
//			objs.prepend(o);
//	}

	object_node 
	create_icon_from_props(object_node on, boolean is_icon, object_node objp)
	{
		String icon_name;
		String name;
		String label;
		String color;

		icon_name = (String)on.get(PROP_ICON);
		if (icon_name == null && is_icon) {
			icon_name = "default.gif";
			on.put(PROP_ICON, icon_name);
		}

		name = (String)on.get(PROP_NAME);
		if (name == null)
			name = "label";

		label = (String)on.get(PROP_LABEL);
		if (label == null)
			label = on.id();

		Dimension wh = on.get_wh();
		object_node g;
		if (icon_name == null)
			g  = new object_node(label);
		else {
			// System.out.println("loading "+icon_name);
			g = new object_node(label);
			g.setIcon(icon_name);
		}
		g.set_parent(objp);
		// set_datas(g, on);

		color = (String)on.get(PROP_FG_COLOR);
		g.set_foreground_color(color);
		color = (String)on.get(PROP_BG_COLOR);
		g.set_background_color(color);
		objp.add(g);
		return g;
	}


	object_node
	load_wins (object_node on)
	{

		String s = (String)on.get(PROP_BACKGROUND);
		on.set_background(s);
		//set_datas(on, on);

		// TODO change stuff when window size changes
		// set_dim(framewin);
		return on;
	}

	/*
	 *	global variable to hold original button press offset.
	 */
	public int deltaX = 0;
	public int deltaY = 0;
	public int last_x = 0;
	public int last_y = 0;
	rubber drag_band = null;
	dotted line_rel = null;
	selected_line sel_line_rel = null;
	object_node dragged = null;

	void
	drag(MouseEvent eventp )
	{
		last_x = eventp.getX();
		last_y = eventp.getY();
	}

	void
	do_rect (int xp[], int yp[], int index, int x1, int y1, int x2, int y2)
	{
		xp[index] = x1;
		yp[index] = y1;
		index++;

		xp[index] = x1;
		yp[index] = y2;
		index++;

		xp[index] = x2;
		yp[index] = y2;
		index++;

		xp[index] = x2;
		yp[index] = y1;
	}


	void
	drag_onto(object_node objp, MouseEvent eventp)
	{
		last_x = eventp.getX();
		last_y = eventp.getY();
	}


	void
	resize(object_node objp, char[][] pcp )
	{
		int w, h;

		Dimension d = objp.getSize();
		switch (pcp[0][0]) {
		case 'w' -> objp.setSize(d.width+1, d.height);
		case 'n' -> objp.setSize(d.width-1, d.height);
		case 't' -> objp.setSize(d.width, d.height+1);
		case 's' -> objp.setSize(d.width, d.height-1);
		}
	}
	object_node pos;
	void
	new_icon(object_node objp, object_node nh, object_node old_view, long x, long y)
	{
		pos = object_list.OBJECT_LIST.new_object(this);
		object_node h =	this.top;
		System.err.println("top is "+h.id());
		String s;

		if (pos != null) {
			old_view.copy_properties(pos);
			s = ""+(x+Integer.parseInt(objp.get(PROP_X)));
			pos.put(PROP_X, s);
			s = ""+(y+Integer.parseInt(objp.get(PROP_Y)));
			pos.put(PROP_Y, s);
			if (nh != null)
				pos.put(PROP_SUBJECT, nh.id());
			object_list.OBJECT_LIST.insert_relationship(h, pos, REL_PARENTAL);
		}
		new swing_safe(() -> {
			object_node baby;
			baby = create_icon_from_props(pos, true, oimove.SELECTED_OIMOVE.top);
			baby.invalidate();
			baby.validate();
			baby.repaint();
			baby.parent().repaint();
		});

	}

	void
	raise(object_node objp)
	{
		objp.getFrame().toFront();
	}

	void
	lower(object_node objp)
	{
		objp.getFrame().toBack();
	}

	void
	draw_line_segments(Graphics graphics, String line)
	{
		object_node lon;
		boolean first;

		first = true;


		while (line != null) {
			lon = object_list.OBJECT_LIST.find(line);
			Point pxy = lon.get_xy();
			line = (String)lon.get(PROP_LINE);
			if (first) {
				first = false;
			} else {
				graphics.drawLine(last_x, last_y, pxy.x, pxy.y);
			}
			last_x = pxy.x; last_y = pxy.y;
		}
	}

/*
	// draw lines assocated with *on* on *parent*
	void
	draw_lines(Graphics g, object_node on, object_node parent)
	{
		String s;

		if (line_rel == null)
		{
			line_rel = new dotted();
		}
		if (sel_line_rel == null)
		{
			sel_line_rel = new selected_line();
		}
		if (on != null) {
			// current line
			prop_val_iter pvi1 = new prop_val_iter(on, PROP_LINE);
			while((s = pvi1.select_property()) != null) {
				line_rel.setGraphics(g);
				draw_line_segments(g, s);
			}

			// stored lines
			rel_obj_iter roi = new rel_obj_iter(OBJECT_LIST, on, REL_PARENTAL);
			object_node ro;
			while ((ro = roi.select_rel_object()) != null) {
				if ((String)ro.get(PROP_ICON) == null) {
					if (ro == Gselected_line)
						sel_line_rel.setGraphics(g);
					else
						line_rel.setGraphics(g);
					draw_line_segments(g, ro.id());
				}
			}
		}
	}

	void
	expose(object_node objp, Graphics graphics)
	{
		object_node child;
		object_node child2;

		if (sel_line_rel == null)
		{
			sel_line_rel = new selected_line();
		}

		if (line_rel == null)
		{
			line_rel = new dotted();
		}

		child = null;
		while ((child = objp.next_child(child)) != null) {
			object_node on = child;
			child2 = null;
			while ((child2 = objp.next_child(child2)) != null) {
				object_node on2 = child2;
				if (on != null && on2 != null) {
					String rel;
					object_node son;
					object_node son2;
					son = subject_node(on);
					son2 = subject_node(on2);
					if (son  != null && son2 != null) {
						rel_name_iter rni = new rel_name_iter(son.rel_list(), son2.id());
						if ((rel = rni.select_relation_name()) != null) {
							if (child == Grel_sel &&
								child2 == Grel_sel2)
								sel_line_rel.setGraphics(graphics);
							else
								line_rel.setGraphics(graphics);
							graphics.drawLine(
								child.getLocation().x + child.getSize().width / 2,
								child.getLocation().y + child.getSize().height / 2,
								child2.getLocation().x + child2.getSize().width / 2,
								child2.getLocation().y + child2.getSize().height / 2
								);
						}
					}
				}
			}
		}
	}
*/


	void
	apply(object_node objp)
	{
		for (String key : objp.keys()) {
			JTextField tf = objp.getField(key);
			String value = tf.getText();
			objp.put(key, value);
			if (key.equals(PROP_X)) {
				Point p = objp.getLocation();
				p.x = Integer.parseInt(value);
				objp.setLocation(p);
			} else if (key.equals(PROP_Y)) {
				Point p = objp.getLocation();
				p.y = Integer.parseInt(value);
				objp.setLocation(p);
			} else if (key.equals(PROP_WIDTH)) {
				Dimension d = objp.getSize();
				d.width = Integer.parseInt(value);
				objp.setSize(d);
			} else if (key.equals(PROP_HEIGHT)) {
				Dimension d = objp.getSize();
				d.height = Integer.parseInt(value);
				objp.setSize(d);
			} else if (key.equals(PROP_ICON)) {
				objp.setIcon(value);
			} else if (key.equals(PROP_BG_COLOR)) {
				objp.set_background_color(value);
			} else if (key.equals(PROP_FG_COLOR)) {
				objp.set_foreground_color(value);
			}
		}
		objp.repaint();
		objp.parent().invalidate();
		objp.parent().repaint();
		objp.parent().validate();
	}

	void
	add_props(object_node on, Box box)
	{
		JTextField tf;
		JLabel lab;

		for (String key : on.keys()) {
			String value = (String)on.get(key);
			lab = new JLabel(key);
			tf = new JTextField(value, value.length()+3);
			on.setField(key, tf);
			Box hbox = Box.createHorizontalBox();
			hbox.add(lab);
			hbox.add(tf);
			box.add(hbox);
		}
	}

	void
	props()
	{

		Iterator<object_node> sit = object_selection.iterator();
		if (sit.hasNext()) {
			object_node o = sit.next();
			String s;
			if (o != null)
				s = "Properties of "+o.id();
			else
				s = "Properties";

			JFrame owner = null;
			if (o != null) {
				if (o.getFrame() == null) {

					owner = new JFrame(o.id());
				} else {
					owner = o.getFrame();
				}
			}
			Box current_box = Box.createVerticalBox();
			class PropsDialog extends JDialog implements ActionListener {
				object_node objp = null;
				Box b = null;
				public PropsDialog(JFrame owner, String s, object_node o, Box box) {
					super(owner, s);
					objp = o;
					b = box;
				}
				@Override
				public void actionPerformed(ActionEvent ae) {
					switch (ae.getActionCommand()) {
						case "Reset" -> {
									b.removeAll();
									add_props(objp, b);
									object_node son;
									son = subject_node(objp);
									if (son != null)
										add_props(son, b);
									this.add("Center", b);
									b.invalidate();
									b.validate();
									b.repaint();
						}
						case "Apply" -> apply(objp);
						case "Dismiss" -> this.dispose();
						default -> { }
                    }
				}
			
			}
			PropsDialog diag = new PropsDialog(owner, s, o, current_box);
			diag.getContentPane().setLayout(new BorderLayout());
			Panel p = new Panel();
			p.setLayout(new FlowLayout());
			Button b = new Button("Apply");
			b.addActionListener(diag);
			p.add(b);
			b = new Button("Reset");
			b.addActionListener(diag);
			p.add(b);
			b = new Button("Dismiss");
			b.addActionListener(diag);
			p.add(b);
			diag.getContentPane().add("South", p);
			current_property_object = o;
			if (o != null) {
				add_props(o, current_box);
				object_node son;
				son = subject_node(o);
				if (son != null)
					add_props(son, current_box);
			}
			diag.getContentPane().add("Center", current_box);
			diag.pack();
			diag.setVisible(true);
		}
	}

	void
	location_from_event(object_node on)
	{
		if (on != null) {
			String s;
			s = ""+last_x;
			on.put(PROP_X, s);
			on.put(PROP_Y, s);
		}
	}


	void
	objunlink(object_node objp)
	{
		object_node on = objp;
		object_node par = objp.parent();

		/* delete from database */
		object_list.OBJECT_LIST.delete_relationship(on, REL_PARENTAL);
		on.remove(PROP_SUBJECT);
		object_list.OBJECT_LIST.delete_object(on.id());

		/* delete from user interface */
		object_selection.remove(objp);
		objectsOnWindow.remove(objp);
		if (Grel_sel == objp || Grel_sel2 == objp) {
			Grel_sel = null;
			Grel_sel2 = null;
		}
	}

/*
	void
	make_rel(object_node objp, char[][]pcp)
	{
		Iterator sit = object_selection.iterator();
		Iterator rit = relation_selection.iterator();
		object_node o;
		object_node or;
		object_node on;
		object_node oon;
		object_node onr;
		object_node new_rel;
		object_node son;
		object_node soon;

		on = objp;
		if (rit.hasNext()) {
			or = (object_node)rit.next();
			onr = or;
			if (onr != null) {
				while(sit.hasNext()) {
					o =  (object_node)sit.next();
					oon = o;
					new_rel = OBJECT_LIST.new_object();
					soon = subject_node(oon);
					if (soon == null) {
						soon = OBJECT_LIST.new_object();
						oon.put(PROP_SUBJECT, soon.id());
					}
					son = subject_node(on);
					if (son == null) {
						son = OBJECT_LIST.new_object();
						on.put(PROP_SUBJECT, son.id());
					}
					onr.copy_properties(new_rel);
					switch (pcp[0][0]) {
					case 'p':
					// object under event is first in relationship
						OBJECT_LIST.insert_relationship(son,
							soon, onr.id(), new_rel.id());
						break;
					case 'c':
					// object under event is second in relationship
						OBJECT_LIST.insert_relationship(soon,
							son, onr.id(), new_rel.id());
						break;
					}
				}
			}
		}
	}
*/

	boolean
	close_to (long x1, long y1, long x2, long y2, long x, long y)
	/* x1,y1 and x2,y2 are points on a line. x,y is a point near the line */
	{
		/* compute normal */
		long a;
		long b;
		float d;

		b = (x1 - x2);
		a = (y2 - y1);
		d = (float)(Math.abs((int)(a * (x - x1) + b * ( y - y1))) / Math.sqrt(a * a + b * b));
		return (d < 5 &&
			( (x1 <= x && x <= x2) || (x2 <= x && x <= x1)) &&
			( (y1 <= y && y <= y2) || (y2 <= y && y <= y1)));
	}
/*

	void
	select_rel(object_node objp, MouseEvent me)
	{
		object_node child;
		object_node child2;

		Grel_sel = null;
		Grel_sel2 = null;

		child = null;
		while ((child = objp.next_child(child)) != null) {
			object_node on = child;
			child2 = null;
			while ((child2 = objp.next_child(child2)) != null) {
				object_node on2 = child2;
				if (on != null && on2 != null) {
					String rel;
					object_node son;
					object_node son2;
					son = subject_node(on);
					son2 = subject_node(on2);
					if (son != null && son2 != null) {
						rel_name_iter rni = new rel_name_iter(son.rel_list(),
							son2.id());
						if ((rel = rni.select_relation_name()) != null &&
							close_to(
							child.getLocation().x + child.getSize().width / 2,
							child.getLocation().y + child.getSize().height / 2,
							child2.getLocation().x + child2.getSize().width / 2,
							child2.getLocation().y + child2.getSize().height / 2,
							me.getX(),
							me.getY()
							)) {
							Grel_sel = child;
							Grel_sel2 = child2;
						}
					}
				}
			}
		}
	}

	void
	add_a_point(object_node objp, KeyEvent eventp)
	{
		object_node pon;
		object_node on;

		on = OBJECT_LIST.new_object();
		location_from_event(on);
		pon = objp;
		if (pon != null) {
			on.oo_prepend(PROP_LINE, pon, PROP_LINE);
		}
	}

	void
	end_line(object_node objp)
	{
		object_node pon;
		object_node on;
		String s;

		pon = objp;
		if (pon != null) {
			s = (String)pon.get(PROP_LINE);
			on = OBJECT_LIST.find(s);
			if (on != null) {
				OBJECT_LIST.insert_relationship(pon, on, REL_PARENTAL);
				pon.remove(PROP_LINE);
			}
		}
	}
*/
	rubber  drag_line;

	void
	motion_line(object_node objp, MouseEvent eventp )
	{
		object_node on;
		object_node con;
		String s;

		if (drag_line == null) {
			drag_line = new rubber();
		}

		last_x = eventp.getX();
		last_y = eventp.getY();
	}
/*
	boolean
	select_segments(object_node on, MouseEvent eventp)
	{

		object_node keep;
		long kx = 0, ky = 0;
		boolean is_sel;

		keep = null;
		is_sel = false;

		while(on != null) {
			Point pxy = on.get_xy();
			if (keep != null && close_to(pxy.x, pxy.y, kx, ky,
					eventp.getX(), eventp.getY())) {
				is_sel = true;
			}
			keep = on;
			kx = pxy.x;
			ky = pxy.y;
			on = OBJECT_LIST.find((String)on.get(PROP_LINE));
		}
		return is_sel;
	}

	void
	select_line(MouseEvent me)
	{
		object_node objp = current_property_object;
		if (objp == null) {
			return;
		}
		rel_obj_iter roi = new rel_obj_iter(OBJECT_LIST, objp, REL_PARENTAL);
		object_node ro;
		Gselected_line = null;
		while ((ro = roi.select_rel_object()) != null) {
			if ((String)ro.get(PROP_ICON) == null) {
				if (select_segments(ro, me)) {
					Gselected_line = ro;
					break;
				}
			}
		}
		select_rel(objp, me);
	}
*/


	/*
	void
	display_browse_box(object_node objp, KeyEvent eventp)
	{
		OI_box pc_browser;
		OI_box reverse;
		OI_box forward;
		OI_static_text st;
		object_node on;
		int row;

		on = objp;
		pc_browser = oi_create_box("browser", 1, 1);
		pc_browser.set_layout(OI_layout_row, 1, 1);
		reverse = oi_create_box("reverse", 1, 1);
		reverse.set_layout(OI_layout_column, 1, 1);
		reverse.set_gravity(OI_grav_center);
		forward = oi_create_box("forward", 1, 1);
		forward.set_layout(OI_layout_column, 1, 1);
		forward.set_gravity(OI_grav_center);
		st = oi_create_static_text("object", (String)on.get(PROP_LABEL));
		st.set_gravity(OI_grav_center);
		reverse.layout_associated_object(pc_browser, 0, 0, OI_ACTIVE);
		st.layout_associated_object(pc_browser, 1, 0, OI_ACTIVE);
		forward.layout_associated_object(pc_browser, 2, 0, OI_ACTIVE);

		rel_obj_iter roi(OBJECT_LIST, on, null);
		object_node ro;
		row = 0;
		while (ro = roi.select_rel_object()) {
			st = oi_create_static_text("child", (String)ro.get(PROP_LABEL));
			st.layout_associated_object(forward, 0, row++, OI_ACTIVE);
		}
		rev_rel_obj_iter rroi(OBJECT_LIST, on, null);
		row = 0;
		while (ro = rroi.select_rel_object()) {
			st = oi_create_static_text("child", (String)ro.get(PROP_LABEL));
			st.layout_associated_object(reverse, 0, row++, OI_ACTIVE);
		}

		// pc_browser.set_associated_object(objp.root(), OI_DEF_LOC, OI_DEF_LOC, OI_ACTIVE);
	}
	*/

	void
	undo(object_node objp)
	{
		// move current pointer 
		// unexecute command
	}

	void
	redo(object_node objp)
	{
		// move current pointer
		// reexecute command
	}
	@Override
    public void mousePressed(MouseEvent me) {
		object_node on = object_list.OBJECT_LIST.find(top.id());
		List<object_node> ol = object_list.OBJECT_LIST.find_relatees(on, oimove.REL_PARENTAL);
		Iterator<object_node> e = ol.iterator();
		boolean found = false;
		while (e.hasNext()) {
			object_node ro = e.next();
			// System.err.println("");
			Point pxy = ro.get_xy();
			Dimension wh = ro.get_wh();
			if (	pxy.x < me.getX() &&
				pxy.x + wh.width > me.getX() &&
				pxy.y < me.getY() &&
				pxy.y + wh.height > me.getY()) {
				found = true;
				if (object_selection.contains(ro)) {
					// System.err.println("remove sel");
					do {
						object_selection.remove(ro);
					} while (object_selection.contains(ro));
				} else {
					// System.err.println("add sel");
					object_selection.prepend(ro);
					objectsOnWindow.prepend(ro);
				}
				// System.err.println("brx "+pxy.x);
				// System.err.println("getx "+me.getX());
				// System.err.println("brw "+(pxy.x+wh.width));
				// System.err.println("bry "+pxy.y);
				// System.err.println("gety "+me.getY());
				// System.err.println("brh "+(pxy.y+wh.height));
			}
		}
		deltaX = me.getX();
		deltaY = me.getY();
		if (!found) {
			object_selection.removeFromOimove(this);
		}
		top.repaint();
		repaint();
		SELECTED_OIMOVE = this;
	}

		@Override
        public void mouseReleased(MouseEvent me) {
		object_node local_root;
		if (dragged == null) {
		} else if (dragged != current_property_object) {
			local_root = current_property_object.root();
		} else {
			dragged.setLocation(dragged.getLocation().x+me.getX()-deltaX,
				dragged.getLocation().y+me.getY()-deltaY);

			object_node on;
			on = dragged;
			if (on != null) {
				String s = ""+(dragged.getLocation().x+
					me.getX()-deltaX);
				on.put(PROP_X, s);
				s = ""+( dragged.getLocation().y+
					me.getY()-deltaY);
				on.put(PROP_Y, s);
			}
		}
		dragged = null;
        }
		@Override
        public void mouseClicked(MouseEvent me) {
/*
		select_line(me);
*/
		if (me.getClickCount() == 2) {
			// maybe do something here
		}
        }
		@Override
        public void mouseEntered(MouseEvent me) {
        }
		@Override
        public void mouseExited(MouseEvent me) {
        }
		@Override
        public void mouseMoved(MouseEvent me) {
		last_x = me.getX();
		last_y = me.getY();
        }

		@Override
	public void mouseDragged(MouseEvent me) {
		if (me.getSource() instanceof object_node) {
			motion_line((object_node) me.getSource(), me);
		} else {
			drag(me);
		}
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals("Save")) {
			object_list.OBJECT_LIST.save_db();
		}
	}
	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		if (drag_band == null)
		{
			drag_band = new rubber();
		}
		object_node objp = current_property_object;
		if (objp != null) {

			object_node parent = objp.parent();
	/*
			if (parent != null) {
				expose(parent, graphics);
			}
	*/
	
			object_node on = objp;
			String s = (String)on.get(PROP_LINE);
			object_node con = object_list.OBJECT_LIST.find(s);
			if (con != null) {
				Point pxy = con.get_xy();
				graphics.drawLine( last_x, last_y, pxy.x, pxy.y);
			}
		}


/*
		draw_lines(graphics, on, objp.parent());
*/
	}
	public object_node subject_node(object_node on)
	{
		object_node o =  object_list.OBJECT_LIST.find((String)on.get(PROP_SUBJECT));
		return o;
	}
}
