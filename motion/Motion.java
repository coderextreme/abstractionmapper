package motion;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import java.rmi.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import icbm.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;
import com.formdev.flatlaf.FlatLightLaf;

public class Motion extends JInternalFrame implements InternalFrameListener,
		MouseListener, MouseMotionListener, ComponentListener,
		ActionListener, Serializable
/*
, DropTargetListener
*/
{
	public static final String PROP_A		= "a";  // multiple selection
	public static final String PROP_X		= "x";
	public static final String PROP_Y		= "y";
	public static final String PROP_ORIG_X		= "orig_x";
	public static final String PROP_ORIG_Y		= "orig_y";
	public static final String PROP_EVENT_X		= "event_x";
	public static final String PROP_EVENT_Y		= "event_y";
	public static final String PROP_WIDTH		= "width";
	public static final String PROP_HEIGHT		= "height";
	public static final String PROP_BACKGROUND	= "bitmap";
	public static final String PROP_ICON		= "icon";
	public static final String PROP_LABEL		= "label";
	public static final String PROP_NAME		= "name";
	public static final String PROP_FG_COLOR	= "fg";
	public static final String PROP_LINE		= "next_point";
	public static final String PROP_VISIBLE		= "visible";
	public static final String PROP_CHILD		= "child";
	public static final String PROP_PARENT		= "parent";
	public static final String PROP_PROPERTYFILE	= "property_file";
	public static final String PROP_RELATIONSHIP	= "relationship";
	public static final String PROP_OPERATION	= "move";
	public static final String PROP_ORDER		= "order";
	public static final String PROP_HEAD		= "head";
	public static final String PROP_TAIL		= "tail";
	public static final String PROP_COLLABORATIONSERVER = "CollaborationServer";
	public static final String PROP_SESSIONNAME	= "s";
	public static final String PROP_SESSIONPASSWORD	= "o";
	public static final String PROP_WEBSOCKET	= "WebSocket";
	public static final String VALUE_FIRST		= "first";
	public static final String VALUE_MIDDLE		= "middle";
	public static final String VALUE_LAST		= "last";
	public static final String RELATIONSHIP_PARENTAL = "parental";
	public static final String RELATIONSHIP_CONNECTED = "connected";
	int ss = 5; // handles square width
	int handle = 0;
	static final int north = 1;
	static final int northeast = 2;
	static final int east = 3;
	static final int southeast = 4;
	static final int south = 5;
	static final int southwest = 6;
	static final int west = 7;
	static final int northwest = 8;
	int lx;
	int ly;
	static Vector elems = new Vector();
	static JRadioButtonMenuItem jrel_jrb;
	static JRadioButtonMenuItem jarrow_jrb;
	static JRadioButtonMenuItem jbreak_jrb;
	transient JCheckBoxMenuItem jrec_jrb;
	MUDRemote previousObject = null;
	int lastsearch = 0;
	int AND = 1;
	int OR = 2;
	transient JLayeredPane workarea;
	transient icbm.MUDClient typearea;
	static public object_list OBJECT_LIST = null;
	private Vector relationships = new Vector();
	static Hashtable icons = new Hashtable();
	MUDRemote root;
	static Vector selected = new Vector();
	static Vector clipboard = new Vector();
	static int num_frames = 0;
	static String image_dir = "gedda/";
	boolean selact = true;
	static Vector code_frames = new Vector();
	static JDesktopPane jdp = new JDesktopPane();
	static JFrame bigdesk = new JFrame("Desktop");
	static MetaMotion mm = null;
	static MContainer con = null;

	public static void init() {
	    try {
		try {
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (Exception e) {
			System.out.println(e);
		}
		if (OBJECT_LIST == null) {
			OBJECT_LIST = new object_list("motion"+File.separator+"prop.data");
		}
		String root = "base";
		MUDRemote o = OBJECT_LIST.find(root);
		o.put(PROP_VISIBLE, "true");
		show_if_visible(o);
		show_platforms(root);
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	public static void main (String argv[]) {
		if (argv.length > 0) {
			OBJECT_LIST = new object_list(argv[0]);
		}
		if (argv.length > 1) {
			image_dir = argv[1];
		}
		bigdesk = new JFrame("Desktop");
		JMenuBar mb = new JMenuBar();
		bigdesk.setJMenuBar(mb);
		JMenu file = new JMenu("File");
		mb.add(file);
		JMenuItem jmi = new JMenuItem("Save");
		jmi.addActionListener(new SaveAction());
		file.add(jmi);

		JMenu edit = new JMenu("Edit");
		mb.add(edit);
		jrel_jrb = new JRadioButtonMenuItem("Relationship");
		jarrow_jrb = new JRadioButtonMenuItem("Arrow");
		jbreak_jrb = new JRadioButtonMenuItem("Break");
		edit.add(jarrow_jrb);
		edit.add(jrel_jrb);
		edit.add(jbreak_jrb);
		ButtonGroup bg = new ButtonGroup();
		bg.add(jarrow_jrb);
		bg.add(jrel_jrb);
		bg.add(jbreak_jrb);

		bigdesk.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		jdp = new JDesktopPane();
		jdp.setSize(jdp.getToolkit().getScreenSize());
		bigdesk.setSize(jdp.getToolkit().getScreenSize());
		bigdesk.getContentPane().add(jdp);
		bigdesk.setVisible(true);
		init();
	}
	public void addElement(JComponent jc) {
		System.err.println("adding to elems");
		elems.addElement(jc);
		jc.addMouseListener(this);
		jc.addMouseMotionListener(this);
	}

	public ImageIcon loadIcon(MUDRemote bo) {
	    try {
		String icon_name = image_dir+bo.get(PROP_ICON);
		// cache icons for speed
		ImageIcon ii = (ImageIcon)icons.get(icon_name);
		if (ii == null) {
			ii = new ImageIcon(icon_name);
			icons.put(icon_name, ii);
		}
		return ii;
	    } catch (RemoteException re) {
		re.printStackTrace();
		return null;
	    }
	}
	public void setUpObject(MUDRemote bo, boolean add_rel) {
	    try {
		ImageIcon ii = loadIcon(bo);
		JLabel lab = new JLabel(bo.get(PROP_LABEL), ii, SwingConstants.CENTER);
		lab.setVerticalTextPosition(SwingConstants.CENTER);
		lab.setHorizontalTextPosition(SwingConstants.CENTER);
		lab.setForeground(getFgColor(bo.get(PROP_FG_COLOR)));
		BrowseObject bo2 = new BrowseObject(this);
		bo2.setComponent(lab);
		lab.setBounds(bo.getInt(PROP_X), bo.getInt(PROP_Y),
				ii.getIconWidth(),
				ii.getIconHeight());
		((MUDObject)bo).setComponent(lab);
		// bo.setMotion(this);
		workarea.add(lab, JLayeredPane.DRAG_LAYER);
		if (add_rel) {
			MUDRemote root = (MUDRemote)workarea.getClientProperty("object");
			
			// MUDRemote rel = OBJECT_LIST.new_object();
			bo.put(PROP_PARENT, root.id());
			// bo.put(PROP_CHILD, bo.id());
			bo.put(PROP_RELATIONSHIP, RELATIONSHIP_PARENTAL);
			// setUpObject(rel, false);
		}
		workarea.repaint();
		addElement(lab);
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}


/*
	public void dragEnter(DropTargetDragEvent dtde)  {
	}
	public void dragExit(DropTargetEvent dte)  {
	}
	public void dragOver(DropTargetDragEvent dtde)  {
	}
	public void drop(DropTargetDropEvent e)  {
		Transferable t = e.getTransferable();
		if ((e.getSourceActions() & DnDConstants.ACTION_COPY) != 0) {
			e.acceptDrop(DnDConstants.ACTION_COPY);
		} else if ((e.getSourceActions() & DnDConstants.ACTION_MOVE) != 0) {
			e.acceptDrop(DnDConstants.ACTION_MOVE);
		} else if ((e.getSourceActions() & DnDConstants.ACTION_LINK) != 0) {
			e.acceptDrop(DnDConstants.ACTION_LINK);
		} else {
			e.rejectDrop();
			return;
		}
		try {
			DataFlavor df = new DataFlavor(Class.forName("motion.MUDObject"), "Browse Object");
			if (t.isDataFlavorSupported(df)) {
				MUDRemote bo = (MUDRemote)t.getTransferData(df);
				// bo.setPlatformMotion(this);
				// bo.setMotion(this);
				Point p = e.getLocation();
				bo.putInt(PROP_X, p.x);
				bo.putInt(PROP_Y, p.y);
				e.dropComplete(true);
			}
		} catch (Exception ex) {
			System.out.println("Error transferring data");
		}
	}
	public void dropActionChanged(DropTargetDragEvent dtde)  {
	}
*/
	public Motion(MUDRemote root) throws RemoteException {
		super("Interior of object "+root.id(), true, true, true, true);
		ActionMap map = this.getActionMap();
		map.put(TransferHandler.getCutAction().getValue(Action.NAME),
			TransferHandler.getCutAction());
		map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
			TransferHandler.getCopyAction());
		map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
			TransferHandler.getPasteAction());

		JMenuBar jmb = new JMenuBar();
		setJMenuBar(jmb);
		JMenu file = new JMenu("File");
		jmb.add(file);
		jrec_jrb = new JCheckBoxMenuItem("Recording");
		file.add(jrec_jrb);
		jrec_jrb.addActionListener(this);
		JMenu edit = new JMenu("Edit");
		jmb.add(edit);
		JMenuItem jmi = new JMenuItem("Properties");
		jmi.addActionListener(new PropsAction());
		edit.add(jmi);
		jmi = new JMenuItem("View");
		jmi.addActionListener(new ViewAction());
		edit.add(jmi);
		jmi = new JMenuItem("Copy");
		jmi.addActionListener(new CopyAction());
		jmi.setActionCommand((String)TransferHandler.getCopyAction().getValue(Action.NAME));
		jmi.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		edit.add(jmi);
		jmi = new JMenuItem("Cut");
		jmi.addActionListener(new CutAction());
		jmi.setActionCommand((String)TransferHandler.getCutAction().getValue(Action.NAME));
		jmi.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		edit.add(jmi);
		jmi = new JMenuItem("Paste");
		jmi.addActionListener(new PasteAction());
		jmi.setActionCommand((String)TransferHandler.getPasteAction().getValue(Action.NAME));
		jmi.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		edit.add(jmi);
		jmi = new JMenuItem("Launch Browser Collaboration");
		jmi.addActionListener(new LaunchAction());
		edit.add(jmi);
		jmi = new JMenuItem("Run");
		jmi.addActionListener(new RunAction());
		edit.add(jmi);
		jmi = new JMenuItem("Move");
		jmi.addActionListener(new MoveAction());
		edit.add(jmi);

		// for catching event not on an object
		addMouseListener(this);
		addMouseMotionListener(this);
		addInternalFrameListener(this);
		addComponentListener(this);
		getContentPane().setLayout(new BorderLayout());

		this.root = root;
		workarea = new JLayeredPane();
		workarea.setLayout(null);
		switchTo(root);
		MUDRemote bo = root;
		if (bo != null) {
			int x = bo.getInt(PROP_X);
			int y = bo.getInt(PROP_Y);
			setLocation(x,y);
		}


		// for catching keystrokes

		workarea.registerKeyboardAction(
			new PropsAction(), "Props", KeyStroke.getKeyStroke('p'),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		workarea.registerKeyboardAction(
			new ViewAction(), "View", KeyStroke.getKeyStroke('o'),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		workarea.registerKeyboardAction(new PasteAction(),
			"Paste", KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		workarea.registerKeyboardAction(new CopyAction(),
			"Copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		workarea.registerKeyboardAction(new CutAction(),
			"Cut", KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		workarea.registerKeyboardAction(new RunAction(),	
			"Act", KeyStroke.getKeyStroke('a'),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		workarea.registerKeyboardAction(new SaveAction(),
			"Save", KeyStroke.getKeyStroke('s'),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		workarea.registerKeyboardAction(new LaunchAction(),
			"Launch Browser Collaboration", KeyStroke.getKeyStroke('l'),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		workarea.registerKeyboardAction(new MoveAction(),
			"Make a Move Operation", KeyStroke.getKeyStroke('m'),
			JComponent.WHEN_IN_FOCUSED_WINDOW);

		// pack();

		workarea.setSize(400,300);
		JScrollPane jscp = new JScrollPane(workarea);
		JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jscp, typearea);
		//jsp.setResizeWeight(0.9);
	        //jsp.setDividerLocation(0.5);
		jsp.setDividerLocation(500);
		getContentPane().add("Center", jsp);
	}
	public void switchTo(MUDRemote root) {
	    try {
		// DropTarget dt = new DropTarget(workarea, this);
		// workarea.setDropTarget(dt);
		super.setTitle("Interior of object "+root.id());
		System.err.println("root is "+root.id());
		// elems.clear();
		selected.clear();
		workarea.removeAll();

		MUDRemote bo = root;
		if (bo == null) {
			bo = OBJECT_LIST.new_object();
		}
		workarea.putClientProperty("object",  bo);
		MUDRemote subj = bo;
/*
		String child = bo.get(PROP_CHILD);
		MUDRemote subj = null;
		if (child != null) {
			subj = OBJECT_LIST.find(child); 
			System.out.println("child is "+child);
		}
*/
		IMD imd = null;
		System.out.println("adding object "+root.id());
		if (subj != null && subj.get(PROP_BACKGROUND) != null) {
			String bg_name = image_dir+subj.get(PROP_BACKGROUND);
			System.out.println ("bg="+bg_name);
			// cache icons for speed
			ImageIcon ii = (ImageIcon)icons.get(bg_name);
			if (ii == null) {
				ii = new ImageIcon(bg_name);
				icons.put(bg_name, ii);
			}
			int w = subj.getInt(PROP_WIDTH);
			int h = subj.getInt(PROP_HEIGHT);
			if (w == 0) {
				w = ii.getIconWidth();
				subj.putInt(PROP_WIDTH, w);
			}
			if (h == 0) {
				h = ii.getIconHeight();
				subj.putInt(PROP_HEIGHT, h);
			}
			imd = new IMD(ii);
			imd.setSize(new Dimension(w,h));
			imd.setLocation(0,0);
		} else if  (subj != null) {
			if (subj.getInt(PROP_WIDTH) == 0) {
				subj.putInt(PROP_WIDTH, 48);
			}
			if (subj.getInt(PROP_HEIGHT) == 0) {
				subj.putInt(PROP_HEIGHT, 28);
			}
		}
		if (imd != null) {
			// JScrollPane jscp = new JScrollPane(imd);
			workarea.add(imd, JLayeredPane.DEFAULT_LAYER);
			workarea.repaint();
		} else {
			System.out.println("Problems adding background");
		}


		if (subj != null) {
			int w = subj.getInt(PROP_WIDTH);
			int h = subj.getInt(PROP_HEIGHT);
			setSize(w,h);
		} else {
			setSize(48,48);
		}
		// setVisible(true);

		Vector v = OBJECT_LIST.find_relatees(root, RELATIONSHIP_PARENTAL);
		root.addToInventory(v);
		Enumeration e = root.inventory().elements();
		while(e.hasMoreElements()) {
			bo = root.nextItem(e);

			int x = bo.getInt(PROP_X);
			int y = bo.getInt(PROP_Y);
			bo.putInt(PROP_X, x);
			bo.putInt(PROP_Y, y);
			System.out.println("object "+bo.id()+
				" at "+x+","+y);
			setUpObject(bo, false);
		}
		if (typearea == null) {
			typearea = new icbm.MUDClient(/*root.id()*/);
			typearea.setNick(root.id());
			typearea.init();
			new Thread(typearea.getRunnable()).start();
		} else {
			// typearea.setMotion(this);
		}
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	class IMD extends JButton {
		public IMD(ImageIcon ii) {
			super(ii);
			motion.dnd.TransferActionListener tal = new motion.dnd.TransferActionListener();
			addActionListener(tal);
		}
		public void paint(Graphics g) {
			super.paint(g);
			for (Enumeration e = relationships.elements();
				e != null && e.hasMoreElements();) {
				BrowseRelationship br = (BrowseRelationship)e.nextElement();
				if (br == null) {
					System.out.println("problems with null relationship");
				} else {
					try {
						br.paint(g, Motion.this.root);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
	public void actionPerformed(ActionEvent ae) {
		AbstractButton ab = (AbstractButton)ae.getSource();
		if (ab.isSelected()) {
			System.out.println("Adding a recording frame");
			code_frames.addElement(this);
		} else {
			System.out.println("Removing a recording frame");
			code_frames.removeElement(this);
		}
	}

	public Enumeration getSelectedEnumeration() {
		return selected.elements();
	}
	public void emptySelected() {
		Vector v = new Vector(selected.size());
		for (Enumeration e = selected.elements();
				e != null && e.hasMoreElements(); ) {
			JComponent jc = (JComponent)e.nextElement();
			v.addElement(jc);
		}
		if (selact) {
			selected.removeAllElements();
		}
		for (Enumeration e = v.elements();
				e != null && e.hasMoreElements(); ) {
			JComponent jc = (JComponent)e.nextElement();
			jc.repaint();
		}
	}
	public boolean selectionContains(Component com) {
		return (selected.indexOf(com) >= 0);
	}
	public void internalFrameClosing(InternalFrameEvent we) {
	    try {
		Motion m = (Motion)we.getSource();
		m.setVisible(false);
		MUDRemote bo = (MUDRemote)m.workarea.getClientProperty("object");
		bo.put(PROP_VISIBLE, "false");
		num_frames--;
		System.out.println("closing "+num_frames);
		if (num_frames == 0) {
			System.exit(0);
		}
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	public void internalFrameActivated(InternalFrameEvent we) {
	}
	public void internalFrameClosed(InternalFrameEvent we) {
	}
	public void internalFrameDeactivated(InternalFrameEvent we) {
	}
	public void internalFrameDeiconified(InternalFrameEvent we) {
	}
	public void internalFrameIconified(InternalFrameEvent we) {
	}
	public void internalFrameOpened(InternalFrameEvent we) {
	    try {
		Motion m = (Motion)we.getSource();
		MUDRemote bo = (MUDRemote)m.workarea.getClientProperty("object");
		bo.put(PROP_VISIBLE, "true");
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	public boolean within(MouseEvent me, int x, int y, int width, int height) {
		int mx = me.getX();
		int my = me.getY();
		if (mx >= x && mx <= x + width &&
		    my >= y && my <= y + height) {
			return true;
		} else {
			return false;
		}
	}
	
	public void mousePressed(MouseEvent me) {
	    try {
/*
		JPopupMenu jpm = new JPopupMenu();
		jpm.add("Cut");
		jpm.add("Children -->");
		jpm.show(me.getComponent(),
		       me.getX(), me.getY());
*/
		MUDRemote bo = null;
		JComponent jc = null;
		Enumeration e;
		lx = me.getX();
		ly = me.getY();
		try {
			jc = (JComponent)me.getComponent();
			bo = (MUDRemote)jc.getClientProperty("object");
			
		} catch (ClassCastException cce) {
			return;
		}
		beginCollectXY(me);
		if (jrel_jrb.isSelected()) {
			if (previousObject != null && bo != null) {
				// form relationship
				try {
					MUDRemote bro = OBJECT_LIST.new_object();
					bro.put(PROP_RELATIONSHIP, RELATIONSHIP_CONNECTED);
					bro.put(PROP_PARENT, previousObject.id());
					bro.put(PROP_CHILD, bo.id());
					BrowseRelationship br = new BrowseRelationship(previousObject, bo, bro, RELATIONSHIP_CONNECTED);
					relationships.addElement(br);
					repaint();
				} catch (RemoteException re) {
					re.printStackTrace();
				}
			}
		} else if (jbreak_jrb.isSelected()) {
			// break relationship
			Vector removeRels = new Vector(2);
			for (e = relationships.elements(); e != null && e.hasMoreElements();) {
				BrowseRelationship br = (BrowseRelationship)e.nextElement();
				if ((br.first == previousObject && br.second == bo) ||
					(br.first == bo && br.second == previousObject)) {
					
					removeRels.addElement(br);
					
				}
			}
			for (e = removeRels.elements(); e != null && e.hasMoreElements();) {
				BrowseRelationship br = (BrowseRelationship)e.nextElement();
				relationships.removeElement(br);
				OBJECT_LIST.delete_object(br.relationship.id());
			}
			
		}
		previousObject = bo;
		handle = 0;
		for (e = getSelectedEnumeration();
				e != null && e.hasMoreElements(); ) {
			JComponent c = (JComponent)e.nextElement();
			Point p = c.getLocation();
			Dimension d = c.getSize();
			if (within(me, 0, 0, ss, ss)) {
				handle = northwest;
			} else if (within(me, 0, (d.height-ss)/2, ss, ss)) {
				handle = west;
			} else if (within(me, 0, d.height-ss, ss, ss)) {
				handle = southwest;

			} else if (within(me, d.width-ss, 0, ss, ss)) {
				handle = northeast;
			} else if (within(me, d.width-ss, (d.height-ss)/2, ss, ss)) {
				handle = east;
			} else if (within(me, d.width-ss, d.height-ss, ss, ss)) {
				handle = southeast;

			} else if (within(me, (d.width-ss)/2, 0, ss, ss)) {
				handle = north;
			} else if (within(me, (d.width-ss)/2, d.height-ss, ss, ss)) {
				handle = south;
			} 
		}
		if (handle == 0 && selact) {
			if (!me.isShiftDown()) {
				emptySelected();
				selected.addElement(jc);
			} else if (selected.indexOf(jc) == -1) {
				selected.addElement(jc);
			} else {
				selected.removeElement(jc);
				jc.repaint();
			}
		}
		repaint();
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	public void mouseClicked(MouseEvent me) {
		if (me.getClickCount() == 2) {
			view();
		}
	}
	public void mouseEntered(MouseEvent me) {
	}
	public void mouseExited(MouseEvent me) {
	}
	public void mouseReleased(MouseEvent me) {
		endCollectXY(me);
		repaint();
	}
	public void mouseDragged(MouseEvent me) {
		try {
			JComponent jc = (JComponent)me.getComponent();
			// do not drag the window with this listener
			if (jc instanceof JInternalFrame) {
				return;
			}
			if (!selectionContains(jc)) {
				return;
			}
		} catch (ClassCastException cce) {
			cce.printStackTrace();
			return;
		}
	    try {
		//System.out.println("PRIOR TO COLLECTION ON DRAG X="+me.getX()+" Y="+me.getY());
		nextCollectXY(me);
		//System.out.println("AFTER TO COLLECTION ON DRAG X="+me.getX()+" Y="+me.getY());
		int mx = me.getX();
		int my = me.getY();
		int dx = mx - lx;
		int dy = my - ly;
		//System.out.println("DELTAS X="+dx+" Y="+dy);
		Enumeration e = getSelectedEnumeration();
		for (;e != null && e.hasMoreElements(); ) {
			JComponent c = (JComponent)e.nextElement();
			Point p = c.getLocation();
			Dimension d = c.getSize();
			if (handle == north) {
				setCompBounds(me, c, p.x, p.y + dy, d.width, d.height - dy);
			} else if (handle == northeast) {
				setCompBounds(me, c, p.x, p.y + dy, mx, d.height - dy);
			} else if (handle == east) {
				setCompBounds(me, c, p.x, p.y, mx, d.height);
			} else if (handle == southeast) {
				setCompBounds(me, c, p.x, p.y, mx, my);
			} else if (handle == south) {
				setCompBounds(me, c, p.x, p.y, d.width, my);
			} else if (handle == southwest) {
				setCompBounds(me, c, p.x + dx, p.y, d.width - dx, my);
			} else if (handle == west) {
				setCompBounds(me, c, p.x + dx, p.y, d.width - dx, d.height);
			} else if (handle == northwest) {
				setCompBounds(me, c, p.x + dx, p.y + dy, d.width - dx, d.height - dy);
			} else { // handle == 0
				MUDRemote bo = (MUDRemote)c.getClientProperty("object");
				if (!(bo instanceof Operation) || selact) {
					p.x += dx;
					p.y += dy;
					c.setLocation(p.x, p.y);
					if (bo != null) {
						//System.out.println("NEW LOCATION AND OLD LOCATION ID="+bo.id()+" NEW X="+p.x+" Y="+p.y+" OLD X="+bo.get(PROP_X)+" Y="+bo.get(PROP_Y));
						bo.putInt(PROP_X, p.x);
						bo.putInt(PROP_Y, p.y);
					}
				}
			}
		}
		repaint();
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	public void mouseMoved(MouseEvent me) {
	}
	public void setCompBounds(MouseEvent me, JComponent c, int x, int y, int width, int height) {
		if (width < 0) {
			x += width;
			lx = me.getX();
			width = -width;
			switch (handle) {
			case northeast:
				handle = northwest;
				break;
			case northwest:
				handle = northeast;
				break;
			case southeast:
				handle = southwest;
				break;
			case southwest:
				handle = southeast;
				break;
			case east:
				handle = west;
				break;
			case west:
				handle = east;
				break;
			}
		}
		if (height < 0) {
			y += height;
			ly = me.getY();
			height = -height;
			
			switch (handle) {
			case northeast:
				handle = southeast;
				break;
			case northwest:
				handle = southwest;
				break;
			case southeast:
				handle = northeast;
				break;
			case southwest:
				handle = northwest;
				break;
			case south:
				handle = north;
				break;
			case north:
				handle = south;
				break;
			}
		}
		c.setBounds(x, y, width, height);
	}
	public void andSearch(String [] strings) {
	    try {
		System.err.println("AND search");
		emptySelected();
		for (Enumeration e = elems.elements(); e != null && e.hasMoreElements(); ) {
			JComponent jc = (JComponent)e.nextElement();
			MUDRemote bo = (MUDRemote)jc.getClientProperty("object");
			int i = 0;
			for (; i < strings.length; i++) {
				if (!bo.isPropertySet(strings[i])) {
					break;
				}
			}
			if (i >= strings.length && selact) {
				selected.addElement(jc);
			}
			jc.repaint();
			
		}
		lastsearch = AND;
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	public void orSearch(String [] strings) {
	    try {
		System.err.println("OR search");
		emptySelected();
		for (Enumeration e = elems.elements(); e != null && e.hasMoreElements(); ) {
			JComponent jc = (JComponent)e.nextElement();
			MUDRemote bo = (MUDRemote)jc.getClientProperty("object");
			boolean found = false;
			for (int i = 0; i < strings.length; i++) {
				System.err.println("Searching for "+strings[i]);
				if (bo.isPropertySet(strings[i])) {
					System.err.println("found "+strings[i]);
					found = true;
				}
			}
			if (found && selact) {
				System.err.println("Adding a selected object");
				selected.addElement(jc);
			}
			jc.repaint();
		}
		lastsearch = OR;
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	public void apply(String [] strings) {
	    try {
		for (Enumeration e = getSelectedEnumeration(); e != null && e.hasMoreElements(); ) {
			JComponent jc = (JComponent)e.nextElement();
			MUDRemote bo = (MUDRemote)jc.getClientProperty("object");
			for (int i = 0; i < strings.length; i++) {
				bo.putProp(strings[i]);
			}
		}
		if (lastsearch == OR) {
			orSearch(strings);
		}
		if (lastsearch == AND) {
			andSearch(strings);
		}
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	public void getSelectedMetaData(JTextArea jta) {
	    try {
		Hashtable combined = new Hashtable();
		for (Enumeration e = getSelectedEnumeration(); e != null && e.hasMoreElements(); ) {
			JComponent jc = (JComponent)e.nextElement();
			MUDRemote bo = (MUDRemote)jc.getClientProperty("object");
			Enumeration props = bo.getPropertyKeys();
			for(; props != null && props.hasMoreElements();) {
				String s = (String)props.nextElement();
				combined.put(s, "");
			}
		}
		StringBuffer sb = new StringBuffer();
		Enumeration props = combined.keys();
		for(; props != null && props.hasMoreElements();) {
			String s = (String)props.nextElement();
			sb.append(s);
			sb.append("\n");
		}
		jta.setText(sb.toString());
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	void add_prop(String key, Box box, MUDRemote on) throws RemoteException {
		String value = (String)on.get(key);
		JLabel lab = new JLabel(key);
		JTextField tf = new JTextField(value, value.length()+3);
		Box hbox = Box.createHorizontalBox();
		((MUDObject)on).setField(key, tf);
		((MUDObject)on).setLabel(key, lab);
		((MUDObject)on).setBox(key, hbox);
		hbox.add(lab);
		hbox.add(tf);
		box.add(hbox);
	}
	void remove_prop(String key, Box box, MUDRemote on) throws RemoteException {
		JTextField tf = ((MUDObject)on).getField(key);
		JLabel lab = ((MUDObject)on).getLabel(key);
		Box hbox = ((MUDObject)on).getBox(key);
		hbox.remove(tf);
		hbox.remove(lab);
		box.remove(hbox);
		((MUDObject)on).removeField(key);
		((MUDObject)on).removeLabel(key);
		((MUDObject)on).removeBox(key);
		JLabel lab2 = (JLabel)((MUDObject)on).getComponent();
		if (PROP_LABEL.equals(key)) {
			lab2.setText("");
		} else if (PROP_FG_COLOR.equals(key)) {
			lab2.setForeground(getFgColor(on.get(PROP_FG_COLOR)));
		} else if (PROP_ICON.equals(key)) {
			lab2.setIcon(null);
		}
	}
	void
	add_props(MUDRemote on, Box box)
	{
		try {
			Enumeration e = on.keys();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				add_prop(key, box, on);
			}
		} catch (RemoteException re) {
			re.printStackTrace();
		}
	}
	void paste() throws RemoteException {
		for (Enumeration e = clipboard.elements();
				e != null && e.hasMoreElements(); ) {
			JComponent jc = (JComponent)e.nextElement();
			MUDRemote o = (MUDRemote)jc.getClientProperty("object");
			if (o == null) {
				continue;
			}
			MUDRemote bo = OBJECT_LIST.new_object();
			bo.copyFrom(o);
			bo.putInt(PROP_X, o.getInt(PROP_X)+64);
			Dimension d = jc.getSize();
			bo.putInt(PROP_Y, o.getInt(PROP_Y)+64);
			setUpObject(bo, true);
		}
	}

// run SELECTED operations
	void run_some_operations() {
	    try {
		for (Enumeration e = selected.elements();
				e != null && e.hasMoreElements(); ) {
			JComponent jc = (JComponent)e.nextElement();
			MUDRemote o = (MUDRemote)jc.getClientProperty("object");
			if (o instanceof Operation) {
				System.out.println("o "+o.id()+" is function of type "+o.getClass());
				((Operation)o).play(this);
			} else if (o != null) {
				System.out.println("o "+o.id()+" is a "+o.getClass());
			} else {
				System.out.println("oops, null found in selected items pointing to browse object");
			}
		}
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}


	public void launch() {
		WebDriver driver = new ChromeDriver();
		System.err.println("Driver is "+driver.getClass().getName());
		for (Enumeration e = selected.elements();
				e != null && e.hasMoreElements(); ) {
			System.err.println("Found a selected object.");
			JComponent jc = (JComponent)e.nextElement();
			MUDRemote o = (MUDRemote)jc.getClientProperty("object");
			if (o == null) {
				System.err.println("Remote object property is null.  OOPS!");
				continue;
			} else {
				System.err.println("Found an object property!");
			}
			try {
				System.err.println("Launching web browser for "+o.get(PROP_LABEL)+".");
				String cs = o.get(PROP_COLLABORATIONSERVER);
				System.err.println(PROP_COLLABORATIONSERVER+":"+cs);

				String sn = o.get(PROP_SESSIONNAME);
				if (sn != null) {
					sn = URLEncoder.encode(sn);
				}
				System.err.println(PROP_SESSIONNAME+":"+sn);

				String pwd = o.get(PROP_SESSIONPASSWORD);
				if (pwd != null) {
					pwd = URLEncoder.encode(pwd);
				}
				System.err.println(PROP_SESSIONPASSWORD+":"+pwd);

				String ws = o.get(PROP_WEBSOCKET);
				if (ws != null) {
					ws = URLEncoder.encode(ws);
				}
				System.err.println(PROP_WEBSOCKET+":"+ws);

				String url = cs+"/"+sn+"/"+pwd+"/"+ws;
				System.err.println("url:"+url);
				driver.get(url);

			} catch (Exception re) {
				System.err.println("Cannot send Chrome web browser to collaboration site.  Need:  "+PROP_COLLABORATIONSERVER+", "+PROP_SESSIONNAME+", "+PROP_WEBSOCKET+", and "+PROP_SESSIONPASSWORD);
				re.printStackTrace(System.err);
			}
		}
	}
	public void copy() {
		clipboard.removeAllElements();
		clipboard.addAll(selected);
	}
	public void cut() {
		copy();
		remove(selected);
	}
	void remove(Vector list)
	{
	    try {
		for (Enumeration e = list.elements();
				e != null && e.hasMoreElements(); ) {
			JComponent jc = (JComponent)e.nextElement();
			MUDRemote o = (MUDRemote)jc.getClientProperty("object");
			Vector removeRels = new Vector(2);
			for (e = relationships.elements(); e != null && e.hasMoreElements();) {
				BrowseRelationship br = (BrowseRelationship)e.nextElement();
				if (br.first == o || br.second == o) {
					
					removeRels.addElement(br);
					
				}
			}
			for (e = removeRels.elements(); e != null && e.hasMoreElements();) {
				BrowseRelationship br = (BrowseRelationship)e.nextElement();
				relationships.removeElement(br);
				OBJECT_LIST.delete_object(br.relationship.id());
			}
			elems.removeElement(jc);
			selected.removeElement(jc);
			OBJECT_LIST.delete_object(o.id());
			jc.removeMouseListener(this);
			jc.removeMouseMotionListener(this);
			workarea.remove(jc);
		}
		this.repaint();
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}

	void
	props()
	{
	    try {
		for (Enumeration e = selected.elements();
				e != null && e.hasMoreElements(); ) {
			JComponent jc = (JComponent)e.nextElement();
			MUDRemote o = (MUDRemote)jc.getClientProperty("object");
			if (o == null) {
				continue;
			}

			String s = "Properties of "+o.id();
			Box current_box = Box.createVerticalBox();
			class PropsDialog extends JDialog implements ActionListener {
				MUDRemote objp = null;
				Box b = null;
				JComponent jc = null;
				public PropsDialog(String s, MUDRemote o, Box box, JComponent jc) {
					super(bigdesk, s);
					objp = o;
					b = box;
					this.jc = jc;
				}
				
				public void actionPerformed(ActionEvent ae) {
					try {
						System.err.println("\t"+objp.id()+":"+ae.getActionCommand());
					} catch (RemoteException re) {
						System.err.println("\tXXXX:"+ae.getActionCommand());
					}
					if (ae.getActionCommand().equals("Reset")) {
						b.removeAll();
						add_props(objp, b);
						this.getContentPane().add("Center", b);
						b.invalidate();
						b.validate();
						b.repaint();
					} else if (ae.getActionCommand().equals("Apply")) {
						apply(objp, jc);
					} else if (ae.getActionCommand().equals("Load Property File")) {
						loadPropertyFile(objp, b);
						add_props(objp, b);
						// apply(objp, jc);
						this.pack();
					} else if (ae.getActionCommand().equals("Dismiss")) {
						this.setVisible(false);
					} else if (ae.getActionCommand().equals("Add")) {
						String key = JOptionPane.showInputDialog(this, "Enter a new property name");
						if (key != null) {
							try {
								objp.put(key, "");
								add_prop(key, b, objp);
								this.pack();
							} catch (RemoteException re) {
							}
						}

					} else if (ae.getActionCommand().equals("Remove")) {
						String key = JOptionPane.showInputDialog(this, "Enter a property to remove");
						if (key != null) {
							try {
								objp.remove(key);
								remove_prop(key, b, objp);
								this.pack();
							} catch (RemoteException re) {
							}
						}
					}
				}
			
			};
	
			PropsDialog diag = (PropsDialog)((MUDObject)o).getDialog();
			if (diag == null) {
				diag = new PropsDialog(s, o, current_box, jc);
				diag.getContentPane().setLayout(new BorderLayout());
				((MUDObject)o).setDialog(diag);
			}
			diag.getContentPane().removeAll();
			Panel p = new Panel();
			p.setLayout(new FlowLayout());
			Button b = new Button("Load Property File");
			b.addActionListener(diag);
			p.add(b);
			b = new Button("Apply");
			b.addActionListener(diag);
			p.add(b);
			b = new Button("Reset");
			b.addActionListener(diag);
			p.add(b);
			b = new Button("Dismiss");
			b.addActionListener(diag);
			p.add(b);
			b = new Button("Add");
			b.addActionListener(diag);
			p.add(b);
			b = new Button("Remove");
			b.addActionListener(diag);
			p.add(b);
			diag.getContentPane().add("South", p);
			add_props(o, current_box);
			diag.getContentPane().add("Center", current_box);
			diag.pack();
			diag.setVisible(true);
		}
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	void
	apply(MUDRemote objp, JComponent jc)
	{
	    try {
		Enumeration e = objp.keys();
		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			JTextField tf = ((MUDObject)objp).getField(key);
			String value = tf.getText();
			objp.put(key, value);
			if (key.equals(PROP_X)) {
				Point p = jc.getLocation();
				p.x = Integer.parseInt(value);
				jc.setLocation(p.x, p.y);
			} else if (key.equals(PROP_Y)) {
				Point p = jc.getLocation();
				p.y = Integer.parseInt(value);
				jc.setLocation(p.x, p.y);
			} else if (key.equals(PROP_ICON)) {
				((JLabel)jc).setIcon(loadIcon(objp));
			} else if (key.equals(PROP_LABEL)) {
				((JLabel)jc).setText(value);
			} else if (key.equals(PROP_FG_COLOR)) {
				((JLabel)jc).setForeground(getFgColor(value));
			}
		}
		jc.repaint();
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	void view() {
		try {
		    for (Enumeration e = selected.elements();
				e != null && e.hasMoreElements(); ) {
			JComponent jc = (JComponent)e.nextElement();
			MUDRemote o = (MUDRemote)jc.getClientProperty("object");
			if (o == null) {
				System.out.println("continue");
				continue;
			}
			String child = o.get(PROP_CHILD);
			MUDRemote bo = null;
			if (child != null) {
				System.out.println("child is "+child);
				bo = OBJECT_LIST.insert_object(child); 
			} else {
				System.out.println("child is null");
			}
			if (bo == null) {
				bo = OBJECT_LIST.new_object();
				o.put(PROP_CHILD, bo.id());
				setUpObject(bo, false);
			}
			o.put(PROP_VISIBLE, "true");
			show_if_visible(o);
		    }
		} catch (RemoteException re) {
			re.printStackTrace();
		}
	}
	public void componentMoved(ComponentEvent e) {
	   try {
		Motion m = (Motion)e.getSource();
		MUDRemote bo = (MUDRemote)m.workarea.getClientProperty("object");

		Point p = m.getLocation();
		bo.putInt(PROP_X, p.x);
		bo.putInt(PROP_Y, p.y);
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	public void componentHidden(ComponentEvent e) {
	}
	public void componentResized(ComponentEvent e) {
	   try {
		Motion m = (Motion)e.getSource();
		MUDRemote bo = (MUDRemote)m.workarea.getClientProperty("object");
		Dimension d = m.getSize();
		if (d.width == 0) {
			d.width = 48;
		}
		bo.putInt(PROP_WIDTH, d.width);
		if (d.height == 0) {
			d.height = 48;
		}
		bo.putInt(PROP_HEIGHT, d.height);
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	public void componentShown(ComponentEvent e) {
	}
	public void show_relationships() {
	    try {
		Enumeration e = OBJECT_LIST.elements();
		while(e.hasMoreElements()) {
			MUDRemote bro = (MUDRemote)e.nextElement();
			String reltype = bro.get(PROP_RELATIONSHIP);
			if (reltype != null) {
				// System.out.println("rel type "+reltype);
			}
			if (reltype != null && reltype.equals(RELATIONSHIP_CONNECTED)) {
				MUDRemote parent = OBJECT_LIST.find(bro.get(PROP_PARENT));
				MUDRemote child = OBJECT_LIST.find(bro.get(PROP_CHILD));
				BrowseRelationship br = new BrowseRelationship(
					parent, child, bro, RELATIONSHIP_CONNECTED);
				relationships.addElement(br);
			}
		}
		repaint();
	    } catch (RemoteException re) {
			re.printStackTrace();
	    }
	}
	static public void show_platforms(String base) {
	    try{
		Enumeration e = OBJECT_LIST.elements();
		while(e.hasMoreElements()) {
			MUDRemote bo = (MUDRemote)e.nextElement();
			if (bo.id().equals(base)) {
				continue;
			}
			// bo.put(PROP_VISIBLE, "true");
			show_if_visible(bo);
		}
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}

	static public void show_if_visible(MUDRemote bo) {
	    try {
		String sb = (String)bo.get(PROP_VISIBLE);
		if (sb != null) {
			boolean b = Boolean.valueOf(sb).booleanValue();
			if (b) {
				Motion m = new Motion(bo);
				jdp.add(m);
				// bo.setPlatformMotion(m);
				// bo.setMotion(m);
				m.setVisible(true);
				num_frames++;
				m.show_relationships();
				m.toFront();
				m.repaint();
				if (mm == null) {
					mm = new MetaMotion(m);
					jdp.add(mm);
				}
			} else {
				System.out.println("can't see id = "+bo.id());
			}
		}
	    } catch (RemoteException re2) {
		re2.printStackTrace();
	    }
	}
	public void setSelected(Vector s) {
		selected = s;
	}
	public Vector getSelected() {
		return selected;
	}
	public void setSelectionAct(boolean selact) {
		this.selact = selact;
	}
	public void beginCollectXY(MouseEvent me) {
		collectAddElement(me, VALUE_FIRST);
	}
	public void nextCollectXY(MouseEvent me) {
		collectAddElement(me, VALUE_MIDDLE);
	}
	public void endCollectXY(MouseEvent me) {
		collectAddElement(me, VALUE_LAST);
	}
	public void collectAddElement(MouseEvent me, String order) {
		try {
			//System.out.println("EVENT X="+me.getX()+" Y="+me.getY());;
			if (code_frames.size() == 0) {
				// System.out.println("No code!");
				if (me.getWhen() == 0) {
					// overload event returns
					int dx = me.getX();
					int dy = me.getY();
					System.out.println("DELTA IN COLLECT ADD ELEMENT X="+dx+" Y="+dy);
					Enumeration e = getSelectedEnumeration();
					for (;e != null && e.hasMoreElements(); ) {
						JComponent c = (JComponent)e.nextElement();
						MUDRemote bo = (MUDRemote)c.getClientProperty("object");
						Point p = c.getLocation();
						if (!(bo instanceof Operation)) {
							p.x += dx;
							p.y += dy;
							c.setLocation(p.x, p.y);
							if (bo != null) {
								System.out.println("POSITION AND PREVIOUS DATA="+bo.id()+" X="+p.x+" Y="+p.y+" PREV X="+bo.get(PROP_X)+" PREV Y="+bo.get(PROP_Y));
								bo.putInt(PROP_X, p.x);
								bo.putInt(PROP_Y, p.y);
							}
						}
					}
				}
				return;
			}
		} catch (RemoteException re2) {
			re2.printStackTrace();
		}
		try {
			System.out.println("Code!");
			MoveOp mo = new MoveOp(this, me.getComponent());
			mo.putInt(PROP_ORIG_X, lx);
			mo.putInt(PROP_ORIG_Y, ly);
			mo.putInt(PROP_EVENT_X, me.getX());
			mo.putInt(PROP_EVENT_Y, me.getY());
			mo.put(PROP_ICON, "func.gif");
			mo.put(PROP_ORDER, order);
			JComponent jc = (JComponent)me.getComponent();
			mo.setComponent(jc);
			// mo.setMotion(this);
			System.out.println("adding to container, "+order+" "+mo.id());
			if (con == null) {
				con = new MContainer(Motion.this, Motion.RELATIONSHIP_CONNECTED, mo);
				con.put(PROP_VISIBLE, "true");
				show_if_visible(con);
			} else {
				con.add(mo);
			}
		} catch (RemoteException re) {
			re.printStackTrace();
		}
	}
	public void addRelationship(BrowseRelationship br) {
		relationships.add(br);
	}
class PropsAction implements ActionListener {
	public void actionPerformed(ActionEvent ae) {
		props();
	}
}

class ViewAction implements ActionListener {
	public void actionPerformed(ActionEvent ae) {
		view();
	}
}

class PasteAction implements ActionListener {
	public void actionPerformed(ActionEvent ae) {
		try {
			paste();
		} catch (RemoteException re) {
			re.printStackTrace();
		}
	}
}
class CutAction implements ActionListener {
	public void actionPerformed(ActionEvent ae) {
		cut();
	}
}
class LaunchAction implements ActionListener {
	public void actionPerformed(ActionEvent ae) {
		launch();
	}
}

class CopyAction implements ActionListener {
	public void actionPerformed(ActionEvent ae) {
		copy();
	}
}

class RunAction implements ActionListener {
	public void actionPerformed(ActionEvent ae) {
		run_some_operations();
	}
}

class MoveAction implements ActionListener {
	public void actionPerformed(ActionEvent ae) {
	    try {
		if (con == null) {
			System.out.println("con is null");
			return;
		}
		System.out.println("adding move func to "+code_frames.size());
//  Motion.RELATIONSHIP_CONNECTED);
		for (Enumeration f = code_frames.elements();
			f.hasMoreElements();) {
			System.out.println("adding a func");
			Motion m = (Motion)f.nextElement();
			con.putInt(PROP_X, 0);
			con.putInt(PROP_Y, 0);
			con.put(PROP_ICON, "func.gif");
			m.setUpObject(con, true);
		}
		if (code_frames.size() > 0) {
			con = null;
		}
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
}
	public Vector getChildren(MUDRemote root) {
		Vector v = new Vector();
		try {
			v = OBJECT_LIST.find_relatees(root, RELATIONSHIP_PARENTAL);
			root.addToInventory(v);
		} catch (RemoteException re) {
			re.printStackTrace();
		}
		return v;
	}
	public Color getFgColor(String color) {
		if (color == null) {
			return Color.BLUE;
		}
		String s = color;
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

		return new Color(red, green, blue);
	}
	void
	loadPropertyFile(MUDRemote objp, Box b)
	{
		try {
			String pf = objp.get(PROP_PROPERTYFILE);
			System.err.println("\t"+PROP_PROPERTYFILE+" "+objp.id()+":"+pf);
			OBJECT_LIST.objectFromPropertyFile(pf, objp.id());
		} catch (RemoteException e) {
			e.printStackTrace(System.err);
		}
	}
}
class SaveAction implements ActionListener {
	public void actionPerformed(ActionEvent ae) {
		Motion.OBJECT_LIST.save_db();
	}
}
