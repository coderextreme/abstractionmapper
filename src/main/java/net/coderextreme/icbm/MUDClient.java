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
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import com.formdev.flatlaf.FlatLightLaf;

class NickPrompt implements ActionListener {
	JFrame jf = new JFrame("Enter nick:");
	JTextField jtf = new JTextField(15);
	MUDClient mudc = null;
	private void commonInit() {
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
	@Override
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
	Vector<Object> history = new Vector<>();
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
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}
	public void switchTo(MUDRemote mr) {
	    try {
		Enumeration e1 = mc.environment().elements();
		while (e1 != null && e1.hasMoreElements()) {
			ParentItem pi = (ParentItem)e1.nextElement();
			MUDRemote r = pi.object();
			mc.remove(r);
		}
		System.err.println("Looking up "+mr.getName());
		mc.add(mr);
	    } catch (IOException ex) {
			ex.printStackTrace(System.err);
	    }
	}
	public static void main (String args[]) {
		NickPrompt np = new NickPrompt();
	}
	public void startMainFrame() {
		try {
			if (!InetAddress.getLocalHost().getHostName().equals("yottzumm")) {
				rmi = Runtime.getRuntime().exec(new String[] {System.getProperty("java.home")+File.separator+"bin"+File.separator+"rmiregistry", "1099"});
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ie) {
				}
				System.out.println("RMI registry started on port 1099");
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
		try {
		        try {
			    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			    // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			    // UIManager.setLookAndFeel(new FlatLightLaf());
			    FlatLightLaf.setup();
			} catch (Exception e) {
			    e.printStackTrace(System.err);
			}
			init();
			rooms = new JPanel();
			BoxLayout bl = new BoxLayout(rooms, BoxLayout.Y_AXIS);
			rooms.setLayout(bl);
			JScrollPane jsp1 = new JScrollPane(rooms);
			checkConnections();

			JFrame jf = new JFrame(nick);
			jf.getContentPane().setLayout(new BorderLayout());
			jf.getContentPane().add("Center", this);
			jf.getContentPane().add("East", jsp1);
			jf.setLocation(100, 100);
			jf.setSize(400, 300);
			jf.setVisible(true);
			jf.addWindowListener(this);
			new Thread(mc).start();
		} catch (HeadlessException e) {
			e.printStackTrace(System.err);
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
		@Override
		public void actionPerformed(ActionEvent ae) {
			room = MUDObject.getProperty(s+".URL");
			try {
				MUDObject.getNetworkPermission();
				MUDRemote mr = (MUDRemote)MUDObject.lookup(room);
				if (mr != null) {
					if (jb.isSelected()) {
						try {
							mc.add(mr);
							Vector<Object> comm = new Vector<>();
							Vector<Object> metoo = new Vector<>();
							comm.addElement(nick+" enters "+mr.getName());
							mc.tell_siblings(comm, metoo);
						} catch (RemoteException addex) {
							System.err.println("Couldn't connect to "+mr+" "+addex.getMessage());
							jb.setSelected(false);
						}
					} else {
						mc.remove(mr);
					}
				} else {
					System.err.println("No where to go!");
					Vector<Object> comm = new Vector<>();
					comm.addElement(nick+" enters thin air");
					mc.tell(comm);
				}
			} catch (RemoteException e) {
				e.printStackTrace(System.err);
				jb.getParent().remove(jb);
				checkConnections();
			}
		}
	}
	public void checkConnections() {
		rooms.removeAll();
		Vector v = MUDObject.getNames();
		Collections.sort(v);
		Iterator<Object> i = v.iterator();
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
	@Override
	public void windowClosing(WindowEvent we) {
		if (rmi != null) {
			rmi.destroy();
			rmi = null;
		}
		System.exit(0);
	}
	@Override
	public void windowActivated(WindowEvent we) {
	}
	@Override
	public void windowClosed(WindowEvent we) {
	}
	@Override
	public void windowDeactivated(WindowEvent we) {
	}
	@Override
	public void windowDeiconified(WindowEvent we) {
	}
	@Override
	public void windowIconified(WindowEvent we) {
	}
	@Override
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
			e.printStackTrace(System.err);
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
			} catch (RemoteException e) {
				System.err.println("Exception "+e);
				e.printStackTrace(System.err);
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
	    } catch (UnknownHostException e) {
			System.err.println("Exception "+e);
			e.printStackTrace(System.err);
	    }
	}
	@Override
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
		if (!history.isEmpty()) {
			tf.setText(history.elementAt(current-1).toString());
		}
	}
	public void previous() {
		if (current > 1) {
			current--;
		}
		if (!history.isEmpty()) {
			tf.setText(history.elementAt(current-1).toString());
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			previous();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			next();
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}
}
