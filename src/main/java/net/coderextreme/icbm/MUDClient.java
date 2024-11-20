package net.coderextreme.icbm;
/*
    ICBM (Internet Chat by MUD)

    Copyright (C) 1997, 1998, 2000  John Carlson

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.rmi.*;
import java.applet.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.event.*;
import java.rmi.registry.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import com.formdev.flatlaf.*;

class NickPrompt implements ActionListener {
	JFrame jf = new JFrame("Enter nick:");
	JTextField jtf = new JTextField(15);
	MUDClient mudc = null;
	public void commonInit() {
		jf.getContentPane().add(jtf);
		jtf.addActionListener(this);
		jf.pack();
		jf.setVisible(true);
	}
	public NickPrompt() {
		mudc = new MUDClient();
		commonInit();
	}
	public NickPrompt(MUDClient mudc) {
		this.mudc = mudc;
		commonInit();
	}
	public void actionPerformed(ActionEvent ae) {
		mudc.setNick(jtf.getText());
		mudc.startMainFrame();
		jf.setVisible(false);
	}
}

public class MUDClient extends JPanel implements WindowListener, ActionListener, KeyListener {
	JEditorPane ta = new JEditorPane("text/html", "");
	JTextField tf = new JTextField();
	JScrollPane jsp = null;
	MUDObject mc;
	String nick = "Guest";
	String room = "rmi://localhost/Generic_Room";
	StringBuffer html = new StringBuffer("<html><head><title>ICBM</title></head><body><h1>Start Chatting</h1><h2>Type /home to orient yourself</h2></body></html>");
	// String cookie = System.getProperty("LTERM_COOKIE");
	Vector history = new Vector();
	int current = 0;
	static protected Process rmi = null;
	JPanel rooms;
	public MUDClient(String args[]) {
		ta.addHyperlinkListener(new Hyperactive());
		ta.setEditable(false);
		DefaultCaret caret = (DefaultCaret)ta.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		main(args);
	}
	public MUDClient() {
		ta.addHyperlinkListener(new Hyperactive());
		ta.setEditable(false);
		DefaultCaret caret = (DefaultCaret)ta.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		try {
			LocateRegistry.getRegistry();
		} catch (Exception e) {
		}
	}
	boolean odd = false;
	public void switchTo(MUDRemote mr) {
	    try {
		Enumeration e1 = mc.environment().elements();
		while (e1 != null && e1.hasMoreElements()) {
			ParentItem pi = (ParentItem)e1.nextElement();
			MUDRemote room = pi.object();
			mc.remove(room);
		}
		System.err.println("Looking up "+mr.getName());
		mc.add(mr);
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	public static void main (String args[]) {
		NickPrompt np = new NickPrompt();
	}
	public void startMainFrame() {
		try {
			if (!InetAddress.getLocalHost().getHostName().equals("yottzumm")) {
				rmi = Runtime.getRuntime().exec(System.getProperty("java.home")+File.separator+"bin"+File.separator+"rmiregistry 1099");
				Thread.sleep(2000);
                        	System.out.println("RMI registry started on port 1099");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
		        try {
			    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			    // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			    UIManager.setLookAndFeel(new FlatLightLaf());
			} catch (Exception e) {
			    e.printStackTrace();
			}
			init();
			rooms = new JPanel();
			BoxLayout bl = new BoxLayout(rooms, BoxLayout.Y_AXIS);
			rooms.setLayout(bl);
			JScrollPane jsp = new JScrollPane(rooms);
			checkConnections();

			JFrame jf = new JFrame(nick);
			jf.getContentPane().setLayout(new BorderLayout());
			jf.getContentPane().add("Center", this);
			jf.getContentPane().add("East", jsp);
			jf.setLocation(100, 100);
			jf.setSize(400, 300);
			jf.setVisible(true);
			jf.addWindowListener(this);
			new Thread(mc).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (rmi != null) {
			rmi.destroy();
			rmi = null;
		}
	}
	class ContactListen implements ActionListener {
		String s;
		MUDClient mudc;
		JCheckBox jb;
		public ContactListen(MUDClient mudc, String s, JCheckBox jb) {
			this.s = s;
			this.mudc = mudc;
			this.jb = jb;
		}
		public void actionPerformed(ActionEvent ae) {
			room = MUDObject.getProperty(s+".URL");
			try {
				MUDObject.getNetworkPermission();
				MUDRemote mr = (MUDRemote)MUDObject.lookup(room);
				if (mr != null) {
					if (jb.isSelected()) {
						try {
							mc.add(mr);
							Vector comm = new Vector();
							Vector metoo = new Vector();
							comm.addElement(nick+" enters "+mr.getName());
							mc.tell_siblings(comm, metoo);
						} catch (Exception addex) {
							System.err.println("Couldn't connect to "+mr+" "+addex.getMessage());
							jb.setSelected(false);
						}
					} else {
						mc.remove(mr);
					}
				} else {
					System.err.println("No where to go!");
					Vector comm = new Vector();
					comm.addElement(nick+" enters thin air");
					mc.tell(comm);
				}
			} catch (Exception e) {
				e.printStackTrace();
				jb.getParent().remove(jb);
				checkConnections();
			}
		}
	}
	public void checkConnections() {
		rooms.removeAll();
		Vector v = MUDObject.getNames();
		Collections.sort(v);
		Iterator i = v.iterator();
		while (i.hasNext()) {
			String s = i.next().toString();
			if (s.trim().equals("")) {
				continue;
			}
			JCheckBox jb = new JCheckBox(s);
			jb.addActionListener(new ContactListen(this, s, jb));
			rooms.add(jb);
		}
	}
	public void windowClosing(WindowEvent we) {
		if (rmi != null) {
			rmi.destroy();
			rmi = null;
		}
		System.exit(0);
	}
	public void windowActivated(WindowEvent we) {
	}
	public void windowClosed(WindowEvent we) {
	}
	public void windowDeactivated(WindowEvent we) {
	}
	public void windowDeiconified(WindowEvent we) {
	}
	public void windowIconified(WindowEvent we) {
	}
	public void windowOpened(WindowEvent we) {
	}
	public void addToEnd(String text) {
	    if (ta != null) {
		try {
		    ta.setEditable(true);
		    // Document doc = ta.getDocument();
		    // doc.insertString(doc.getLength()-14, text, null);
		    // doc.insertString(doc.getLength(), text, null);
		    html.insert(html.length()-14, text);
		    ta.setText(html.toString());
		    ta.setEditable(false);
		    //Rectangle r = new Rectangle(0,ta.getHeight()-2,1,1);
                    //ta.scrollRectToVisible(r);

		} catch (Exception e) {
			e.printStackTrace();
		}
	    }
/*
	    if (cookie != null && !cookie.trim().equals("")) {
		    System.out.print("\033{S"+cookie+"\012");
	    }
*/
	    System.out.print(text);
/*
	    if (cookie != null && !cookie.trim().equals("")) {
		    System.out.print("\000");
	    }
*/
	}
        public void setNick(String nick) {
		this.nick = nick;
	}
        public MUDObject getRunnable() {
		return mc;
	}
	public void setRoom(String r) {
		room = r;
	}
	public void init() {
	    try {
		String url = "rmi://"+InetAddress.getLocalHost().getHostName()+"/"+nick;
		try {
			mc = new FromMUD(url, this, ta);
			// mc.setURL(url);
		} catch (Exception e) {
			System.err.println("Exception "+e);
			e.printStackTrace();
		}
		setLayout(new BorderLayout());
		jsp = new JScrollPane(ta);
		add("South", tf);
		add("Center", jsp);
		history.addElement("");
		current = history.size();
		tf.setText("/home");
		tf.addKeyListener(this);
		tf.addActionListener(this);
	    } catch (Exception e) {
		System.err.println("Exception "+e);
		e.printStackTrace();
	    }
	}
	public void actionPerformed(ActionEvent ae) {
	    InputLine line = new InputLine();
	    line = line.get_buffer(tf.getText());

	    history.insertElementAt(tf.getText(), history.size()-1);
	    
	    mc.processInput(line);
	    tf.setText("");
	    current = history.size();
	}
	public void next() {
		current++;
		if (current > history.size()) {
		    current = history.size();
		}
		if (history.size() > 0) {
			tf.setText(history.elementAt(current-1).toString());
		}
	}
	public void previous() {
		if (current > 1) {
			current--;
		}
		if (history.size() > 0) {
			tf.setText(history.elementAt(current-1).toString());
		}
	}
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			previous();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			next();
		}
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
	}
}
