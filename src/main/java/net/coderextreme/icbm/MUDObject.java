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
import java.util.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.lang.Thread;
import java.rmi.*;
import javax.swing.*;
import java.rmi.server.*;
import java.rmi.registry.*;

// class for all objects in the mud which aren't actual players
public class MUDObject implements MUDRemote, Runnable {
	private Vector children;   // things a object has in its possession
	private Vector missing_children;

	private Vector parents;  // things this object is within
	private Vector missing_parents;

	private String name = "Name";
	private String url;
	private static Properties settings;
	private Hashtable aliases = new Hashtable();
	public static Vector getNames() {
		Enumeration e = settings.propertyNames();
		Vector rooms = new Vector(settings.size());
		while (e.hasMoreElements()) {
			String s = e.nextElement().toString();
			int url = s.indexOf(".URL");
			if (url == -1) {
				continue;
			}
			String name = s.substring(0, url);
			
			System.err.println("getting property "+s);
			String actualURL = settings.getProperty(s);
			if (lookup(actualURL) != null) {
				rooms.add(name);
			}
		}
		return rooms;
	}

	static public MUDRemote lookup(String url) {
		System.err.println("looking up "+url);
		int pp = url.indexOf("//")+2;
		int eh = url.indexOf("/", pp);
		try {
			MUDObject.getNetworkPermission();
			Registry registry = LocateRegistry.getRegistry(url.substring(pp, eh));
			MUDRemote stub = (MUDRemote) registry.lookup(url.substring(eh+1));
			return stub;
		} catch (Exception conne) {
			conne.printStackTrace();
			System.err.println("Couldn't connect to "+url.substring(pp, eh)+" "+url.substring(eh+1));
			return null;
		}
	}
	public MUDObject(String url) throws RemoteException {
		this();
		int pp = url.indexOf("//")+2;
		int eh = url.indexOf("/", pp);
		children = new Vector();
		parents = new Vector();

		missing_children = new Vector();
		missing_parents = new Vector();
		name = url.substring(eh+1);
		this.url = url;
		getNetworkPermission();
		MUDRemote stub = (MUDRemote) UnicastRemoteObject.exportObject(this, 0);
		System.err.println("pp is "+pp+", eh is "+eh);
		Registry registry = null;
		if (pp < 2) {
			// localhost
			registry = LocateRegistry.getRegistry();
		} else {
			registry = LocateRegistry.getRegistry(url.substring(pp, eh));
		}
		registry.rebind(name, stub);
		readAliases();
	}
	public MUDObject() throws RemoteException {
		super();
	}
	public static String getProperty(String s){
		if (settings == null) {
			try {
				settings = new Properties();
				InputStream is = MUDObject.class.getClassLoader().getResourceAsStream("net/coderextreme/dev/objects.properties");
				if (is == null) {
					System.err.println("input stream for properties is null");
					System.exit(0);
				}
				settings.load(is);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return settings.getProperty(s);
	}
	public String getName() {
		return name;
	}
	public void setName(String n) {
		name = n;
		readAliases();
	}
	public boolean ping() throws RemoteException {
		return true;
	}
	public Vector inventory() {
		// System.err.println("Returning inventory on "+getName());
		int i = 0;
		while (i < missing_children.size()) {
			InventoryItem ii = (InventoryItem)missing_children.elementAt(i);
			MUDRemote o = ii.object();
			try {
				System.err.println("looking up 1 '"+ii.getURL()+"'");
				MUDRemote no = lookup(ii.getURL());
				if (no != null && no.ping()) {
					ii.setObject(no);
					missing_children.removeElementAt(i);
					// System.err.println("Checkin for child 1 "+ii.getURL());
					if (!children.contains(ii)) {
						// System.err.println("Adding child 1 "+ii.getURL());
						children.addElement(ii);
					}
				} else {
					i++;
				}
			} catch (Exception e) {
				System.err.println("Exception "+e);
				i++;
			}
		}
		while (i < children.size()) {
			InventoryItem ii = (InventoryItem)children.elementAt(i);
			MUDRemote o = ii.object();
			try {
				o.ping();
				i++;
			} catch (RemoteException re) {
				children.removeElementAt(i);
				// System.err.println("Checkin for missing child "+ii.getURL());
				if (!missing_children.contains(ii)) {
					// System.err.println("Adding missing child "+ii.getURL());
					missing_children.addElement(ii);
				}
			}
		}
		return children;
	}
	public Vector environment()
	{
		// System.err.println("Returning environment on "+getName());
		int i = 0;
		while (i < parents.size()) {
			MUDRemote o = (MUDRemote)((ParentItem)parents.elementAt(i)).object();
			try {
				o.ping();
				i++;
			} catch (RemoteException re) {
				ParentItem pi = (ParentItem)parents.elementAt(i);
				parents.removeElementAt(i);
				missing_parents.addElement(pi);
			}
		}
		while (i < missing_parents.size()) {
			ParentItem pi = (ParentItem)missing_parents.elementAt(i);
			try {
				System.err.println("looking up 2 '"+pi.getURL()+"'");
				MUDRemote no = lookup(pi.getURL());
				if (no != null && no.ping()) {
					missing_parents.removeElementAt(i);
					add(no);
				} else {
					i++;
				}
			} catch (UnmarshalException e) {
				System.err.println("Unmarshal Exception "+e);
				i++;
			} catch (Exception e) {
				System.err.println("Exception "+e);
				i++;
			}
		}
		return parents;
	}
	public MUDRemote nextItem(Enumeration e) throws RemoteException {
		return ((InventoryItem)e.nextElement()).object();
	}
	public int tell_all_children(Vector message, Vector objects_not_to_tell)
		throws RemoteException {
		// objects like rooms pass messages to inventory items.
		for (Enumeration e = inventory().elements(); e != null && e.hasMoreElements();) {
			MUDRemote o = nextItem(e);
			if (o != null && !objects_not_to_tell.contains(o)) {
				// stop infinite recursion
				objects_not_to_tell.addElement(o);
				// tell subchildren
				o.tell_all_children(message, objects_not_to_tell);
				// I'll tell this person
				o.tell(message);
			}
		}
		return 1;
	}

	public int tell_children(Vector message, Vector objects_not_to_tell)
		throws RemoteException {
		// objects like rooms pass messages to inventory items.
		for (Enumeration e = inventory().elements(); e != null && e.hasMoreElements();) {
			MUDRemote o = nextItem(e);
			if (o != null && !objects_not_to_tell.contains(o)) {
				// stop infinite recursion
				objects_not_to_tell.addElement(o);
				// I'll tell this person
				o.tell(message);
			}
		}
		return 1;
	}

	public int tell_coowners(Vector message, Vector objects_not_to_tell)
		throws RemoteException {
		// objects like rooms pass messages to inventory items.
		for (Enumeration e = inventory().elements(); e != null && e.hasMoreElements();) {
			MUDRemote o = nextItem(e);
			if (o != null && !objects_not_to_tell.contains(o)) {
				// stop infinite recursion
				objects_not_to_tell.addElement(o);
				o.tell_parents(message, objects_not_to_tell);
			}
		}
		return 1;
	}

	public MUDRemote nextParent(Enumeration e) throws RemoteException {
		return ((ParentItem)e.nextElement()).object();
	}
	public Vector get_siblings() throws RemoteException {
		Vector siblings = new Vector();
		for (Enumeration e = environment().elements(); e != null && e.hasMoreElements();) {
			MUDRemote o = nextParent(e);
			Vector v = o.inventory();
			for (Enumeration f = v.elements(); f != null && f.hasMoreElements();) {
				siblings.addElement(nextItem(f));
			}
		}
		return siblings;
	}

	public int tell_siblings(Vector message, Vector objects_not_to_tell)
		throws RemoteException {
		Vector siblings = get_siblings();
		for (Enumeration e = siblings.elements(); e != null && e.hasMoreElements();) {
			MUDRemote o = (MUDRemote)e.nextElement();
			if (o != null && !objects_not_to_tell.contains(o)) {
				// stop infinite recursion
				objects_not_to_tell.addElement(o);
				// I'll tell this person
				try {
					o.tell(message);
				} catch (RemoteException re) {
					Config.debug_println("Someone left!");
				}
			}
		}
		return 1;
	}

	public int tell_all_parents(Vector message, Vector objects_not_to_tell)
		throws RemoteException {
		for (Enumeration e = environment().elements(); e != null && e.hasMoreElements();) {
			MUDRemote o = nextParent(e);
			if (o != null && !objects_not_to_tell.contains(o)) {
				// stop infinite recursion
				objects_not_to_tell.addElement(o);
				// I'll tell this person
				o.tell_all_parents(message, objects_not_to_tell);
				o.tell(message);
			}
		}
		return 1;
	}

	public int tell_parents(Vector message, Vector objects_not_to_tell)
		throws RemoteException {
		for (Enumeration e = environment().elements(); e != null && e.hasMoreElements();) {
			MUDRemote o = nextParent(e);
			if (o != null && !objects_not_to_tell.contains(o)) {
				// stop infinite recursion
				objects_not_to_tell.addElement(o);
				// I'll tell this person
				o.tell(message);
			}
		}
		return 1;
	}

	public int tell_everything(Vector message, Vector objects_not_to_tell)
		throws RemoteException {
		for (Enumeration e = environment().elements(); e != null && e.hasMoreElements();) {
			MUDRemote o = nextParent(e);
			if (o != null && !objects_not_to_tell.contains(o)) {
				// stop infinite recursion
				objects_not_to_tell.addElement(o);
				// tell everything near o
				o.tell_everything(message, objects_not_to_tell);
				// I'll tell this person
				o.tell(message);
			}
		}
		for (Enumeration e = inventory().elements(); e != null && e.hasMoreElements();) {
			MUDRemote o = nextItem(e);
			if (o != null && !objects_not_to_tell.contains(o)) {
				// stop infinite recursion
				objects_not_to_tell.addElement(o);
				// tell everything near o
				o.tell_everything(message, objects_not_to_tell);
				// I'll tell this person
				o.tell(message);
			}
		}
		return 1;
	}

	public int tell(Vector message) {
		// receive messages from other objects
		if (System.out != null) {
			for (Enumeration e = message.elements(); e != null && e.hasMoreElements();) {
				String s = (String)e.nextElement();
				System.out.print(s+" ");
			}
			System.out.println("");
			System.out.flush();
		}
		return 1;
	}

	public int command(MUDRemote subject, Vector comm)
		throws RemoteException {
		// object should parse command and take active, if possible
		String verb = null;
		comm = (Vector)comm.clone();
		if (comm.size() > 0) {
			verb = (String)comm.elementAt(0);
		} else {
			return 0;
		}
		if (verb == null) {
			return 0;
		}
		if (verb.equalsIgnoreCase("/home")) {
			return takeExit(getProperty("Generic_Room.URL"), subject);
		} else if (verb.equalsIgnoreCase("/quit")) {
			destruct();
			System.exit(0);
			return 1;
		} else if (verb.equalsIgnoreCase("/alias")) {
			/*
			comm.removeElementAt(0);
			if (comm.size() > 0) {
				String newverb = comm.elementAt(0).toString();
				comm.removeElementAt(0);
				if (comm.size() > 1) {
					String cmd = comm.elementAt(1).toString();
					if (cmd.equalsIgnoreCase(newverb)) {
						Vector err = new Vector();
						err.addElement("Can't create circular references");
						subject.tell(err);
						return 0;
					} else {
						aliases.put(newverb, comm);
						try {
							FileOutputStream fos = new FileOutputStream(System.getProperty("user.home")+File.separator+".icbm."+getName(), false);
							ObjectOutputStream oos = new ObjectOutputStream(fos);
							oos.writeObject(aliases);
							oos.flush();
							fos.close();
							readAliases();
						} catch (IOException ioe) {
							System.err.println("Problems writing aliases file");
						}
						Vector newalias = new Vector();
						newalias.addElement("Created");
						newalias.addElement(newverb);
						return subject.tell(newalias);
					}
				} else {
					return 0;
				}
			} else
			*/
			{
				return 0;
			}
		} else if (verb.equalsIgnoreCase("/say")) {
			try {
				Vector not_me = new Vector();
				comm.removeElementAt(0);
				comm.insertElementAt("&lt;"+subject.getName()+"&gt;", 0);
				return tell_siblings(comm, not_me);
			} catch (NullPointerException npe) {
				Vector thin_air = new Vector();
				thin_air.insertElementAt(
					"You speak into thin air.", 0);
				return subject.tell(thin_air);
			}
		} else if (verb.equalsIgnoreCase("/look")) {
			Vector message = new Vector();
			Vector look;
			int i;
			Enumeration e = environment().elements();
			while (e != null && e.hasMoreElements()) {
				message.addElement("<font color=green><br>You're in");
				MUDRemote room = nextParent(e);
				look = new Vector();
				look.addElement(subject.getName());
				look.addElement("looks at");
				look.addElement(room.getName());
				Vector sibs = new Vector();
				sibs.addElement(subject);
				tell_siblings(look, sibs);
				message.addElement(room.getName());
				message.addElement("<br>You see");
				boolean something = false;
				for (Enumeration f = room.inventory().elements(); f != null && f.hasMoreElements();) {
					MUDRemote o = nextItem(f);
					message.addElement(o.getName());
					something = true;
				}	
				if (something == false) {
					message.addElement("nothing");
				}
				message.addElement("</font>");
			}
			i = subject.tell(message);
			return i;
		} else if (verb.equalsIgnoreCase("/take") || verb.equalsIgnoreCase("/get")) {
			int breakout = 0;
			for (Enumeration e = environment().elements(); e != null && e.hasMoreElements() && breakout == 0;) {
				MUDRemote room = nextParent(e);
				for (Enumeration f = room.inventory().elements(); f != null && f.hasMoreElements() && breakout == 0;) {
					MUDRemote o = nextItem(f);
					if (o.getName().equalsIgnoreCase(comm.elementAt(1).toString())) {
						o.add(this);
						Vector v = new Vector();
						v.addElement(this.getName());
						v.addElement("picked you up");
						o.tell(v);
						o.remove(room);
						breakout = 1;
					}
				}	
			}
			return breakout;
		} else if (verb.equalsIgnoreCase("/whois")) {
			if (comm.size() > 1) {
				int breakout = 0;
				for (Enumeration e = environment().elements(); e != null && e.hasMoreElements() && breakout == 0;) {
					MUDRemote room = nextParent(e);
					for (Enumeration f = room.inventory().elements(); f != null && f.hasMoreElements() && breakout == 0;) {
						MUDRemote o = nextItem(f);
						if (o.getName().equalsIgnoreCase(comm.elementAt(1).toString())) {
							Vector v = new Vector(2);
							v.addElement(o.getName());
							v.addElement(o.getURL());
							tell(v);
							breakout = 1;
						}
					}
					
				}
				return breakout;
			} else {
				return 0;
			}
		} else if (verb.equalsIgnoreCase("/inventory")) {
			Vector v = new Vector();
			v.addElement("You have");
			for (Enumeration e = inventory().elements(); e != null && e.hasMoreElements();){
				MUDRemote o = nextItem(e);
				v.addElement(o.getName());
			}
			tell(v);
			return 1;
		} else if (verb.equalsIgnoreCase("/drop")) {
			int breakout = 0;
			for (Enumeration e = inventory().elements(); e != null && e.hasMoreElements() && breakout == 0;){
				MUDRemote o = nextItem(e);

				if (o.getName().equalsIgnoreCase(comm.elementAt(1).toString())) {
					o.remove(this);
					Vector v = new Vector();
					v.addElement(this.getName());
					v.addElement("dropped you");
					o.tell(v);
					for (Enumeration f = environment().elements(); f != null && f.hasMoreElements();) {
						MUDRemote room = nextParent(f);
						o.add(room);
					}
					breakout = 1;
				}
			}
			return breakout;
		} else if (verb.equalsIgnoreCase("/enter")) {
			int breakout = 0;
			for (Enumeration e = environment().elements(); e != null && e.hasMoreElements() && breakout == 0;) {
				MUDRemote room = nextParent(e);
				for (Enumeration f = room.inventory().elements(); f != null && f.hasMoreElements() && breakout == 0;) {
					MUDRemote o = nextItem(f);
					if (o.getName().equalsIgnoreCase(comm.elementAt(1).toString())) {
						Vector v = new Vector();
						v.addElement("/goto" );
						v.addElement(o);
						subject.tell(v);
						breakout = 1;
					}
				}	
			}
			return breakout;
		} else if (verb.equalsIgnoreCase("/join")) {
			if (comm.size() > 1) {
				return takeExit(comm.elementAt(1).toString(), subject);
			} else {
				return 0;
			}
		} else if (verb.equalsIgnoreCase("/nick")) {
			if (comm.size() > 1) {
				Vector v = new Vector(1);
				v.addElement("<font color=red>***"+subject.getName()+" is now know as "+comm.elementAt(1)+"</font>");
				Vector not_me = new Vector(5);
				tell_siblings(v, not_me);
				subject.setName((String)comm.elementAt(1));
				subject.readAliases();
				subject.setURL((String)comm.elementAt(1));
				return 1;
			} else {
				return 0;
			}

		} else {
			comm = (Vector)aliases.get(verb);
			if (comm != null && comm.size() > 0) {
				return command(subject, comm);
			}
			return 0;
		}
	}
	public void readAliases() {
/*
		try {
			FileInputStream fis = new FileInputStream(System.getProperty("user.home")+File.separator+".icbm."+getName());
			ObjectInputStream ois = new ObjectInputStream(fis);
			aliases = (Hashtable)ois.readObject();
			fis.close();
		} catch (ClassNotFoundException ce) {
			System.err.println("Problems accessing alias file: can't find hashtable");
		} catch (IOException ioe) {
			// System.err.println("Problems accessing alias file");
		}
*/
	}
	public int addobject(MUDRemote new_object)
		throws RemoteException {
		return new_object.add(this);
	}
	public int addToInventory(Vector v) throws RemoteException {
		Enumeration e = v.elements();
		while (e.hasMoreElements()) {
			((MUDRemote)e.nextElement()).add(this);
		}
		return 1;
	}
	public int addToInventory(InventoryItem i)
		throws RemoteException {
			// System.err.println("Checkin for child 2 "+i.getURL());
			if (!children.contains(i)) {
				// System.err.println("Adding child 2 "+i.getURL());
				children.addElement(i);
			}
			return 1;
	}
	public int add(MUDRemote new_location)
		throws RemoteException {
		// players tell room that something arrived
		int ev = 0;
		try {
			String aurl = new_location.getURL();
			if (!new_location.contains(this)) {
				parents.addElement(new ParentItem(new_location, aurl));
				double d[][] = new double[4][4];
				d[0][0] = 1; d[0][1] = 0; d[0][2] = 0; d[0][3] = 0;
				d[1][0] = 0; d[1][1] = 1; d[1][2] = 0; d[1][3] = 0;
				d[2][0] = 0; d[2][1] = 0; d[2][2] = 1; d[2][3] = 0;
				d[3][0] = 0; d[3][1] = 0; d[3][2] = 0; d[3][3] = 1;
				if (getURL() == null) {
					setURL("rmi://localhost/"+id());
				}
				InventoryItem i = new InventoryItem(this, d, getURL());
			
				ev = new_location.addToInventory(i);
			}
		} catch (RemoteException re) {
			throw re;
		}
		return ev;
	}
	public boolean contains(MUDRemote o) throws RemoteException {
		for (int i = 0; i < children.size(); i++) {
			InventoryItem ii = (InventoryItem)children.elementAt(i);
			if (ii == o) {
				return true;
			}
			MUDRemote mr = ii.object();
			if (mr == o) {
				return true;
			}
		}
		return false;
	}
	public int remove(MUDRemote from)
		throws RemoteException {
		int i = 0;
		for (Enumeration e = parents.elements(); e != null && e.hasMoreElements();){
				MUDRemote o = nextParent(e);
				if (from.getURL().equals(o.getURL())) {

					parents.removeElementAt(i);
					return from.removeInventoryElement(this);
				}
				i++;
		}
		return 0;
	}

	public int removeInventoryElement(MUDRemote mr)
	    throws RemoteException {
		inventory();
		for (int i = 0; i < children.size();) {
			InventoryItem ii = (InventoryItem)children.elementAt(i);
			if (ii != null && ii.getURL() != null && mr != null && ii.getURL().equals(mr.getURL())) {
				children.removeElementAt(i);
				return 1;
			} else {
				i++;
			}
		}
		return 0;
	}
	public int destruct() throws RemoteException { // players tell room that something left
		for (Enumeration e = inventory().elements(); e != null && e.hasMoreElements();) {
			MUDRemote o = nextItem(e);
			o.remove(this);
		}
		for (Enumeration e = environment().elements(); e != null && e.hasMoreElements();) {
			MUDRemote o = nextParent(e);
			remove(o);
		}
		return 1;
	}
	public void run() {  // default routine for players.
				// override for non I/O bound threads
		InputLine line = new InputLine();
		while (line.get_line(System.in) != null) {
			processInput(line);
		}
		// System.exit(0);
	}
	public int processInput(InputLine line) {
		   int ret = 0;
	           try {
			// first check the player
			ret = command(this, line);
			// then check the things that the player is holding
			if (ret == 0) {
				Enumeration e = inventory().elements();
				while (e.hasMoreElements() && ret == 0) {
					MUDRemote o = nextItem(e);
					ret = o.command(this, line);
				}
			}
			// then check the rooms the player is in
			for (Enumeration e = environment().elements(); e != null && e.hasMoreElements() && ret == 0;) {
				MUDRemote o = nextParent(e);
				ret = o.command(this, line);
			}
			// then check neighbors
			if (ret == 0) {
				Vector siblings = get_siblings();
				for (Enumeration e = siblings.elements(); e != null && e.hasMoreElements() && ret == 0;) {
					MUDRemote o = (MUDRemote)e.nextElement();
					ret = o.command(this, line);
				}
			}
			if (line.size() > 0) {
				if (ret == 0) {
				 	if (((String)line.elementAt(0)).indexOf("/") == 0) {
						Vector v = new Vector();
						v.addElement("<font color=blue>What?</font>");
						tell(v);
						ret = 1;
					}
				}
				if (ret == 0) {
					line.insertElementAt("/say", 0);
					ret = command(this, line);
				}
			}
	   	   } catch (RemoteException re) {
			System.err.println("Remote Exception "+re);
	   	   }
		   return ret;
        }
	public int takeExit(String url, MUDRemote subject)
	    throws RemoteException {
		MUDRemote mo = null;
		try {
			System.err.println("looking up 3 '"+url+"'");
			mo = lookup(url);
			mo.ping();
		} catch (Exception e) {
			System.err.println("Exception "+e);
			// e.printStackTrace();
			return 0;
		}
		if (mo == null) {
			System.err.println("Couldn't get remote object");
			return 0;
		} else {
			try {
				if (subject.environment().size() > 0) {
					ParentItem pi = (ParentItem)subject.environment().elementAt(0);
					MUDRemote mr = pi.object();
					subject.remove(mr);
				}
				mo.addobject(subject);
			} catch (RemoteException re) {
				// System.err.println("Remote Exception error:"+re);
				re.printStackTrace();
				return 0;
			}
		}
		// now take a look at where we are
		Vector look = new Vector();
		look.addElement("/look");
		subject.command(subject,look);
		return 1;
	}
	public void setURL(String u) {
		url = u;
		int pp = url.indexOf("//")+2;
		int eh = url.indexOf("/", pp);
		name = url.substring(eh+1);
		try {
			System.err.println("Binding '"+url+"'");
			getNetworkPermission();
			MUDRemote stub = (MUDRemote) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(name, stub);
			// UnicastRemoteObject.exportObject(this);
			// Naming.rebind(url, this);
			settings.load(getClass().getClassLoader().getResourceAsStream("dev/objects.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getURL() throws RemoteException {
		return url;
	}
	public boolean equals(Object obj) {
		try {
			MUDRemote mr = (MUDRemote)obj;
			if (url == null || mr.getURL() == null) {
				return false;
			}
			System.err.println("testing "+url+ " against "+mr.getURL());
			return url.equalsIgnoreCase(mr.getURL());
		} catch (RemoteException re) {
			re.printStackTrace();
			return false;
		}
	}
	JComponent comp = null;
	int ss = 5; // handles square width
	MUDRemote mr = null;
	Hashtable ht = new Hashtable();
	Hashtable rels = new Hashtable();
	Hashtable ht_tf = new Hashtable();
	Hashtable ht_lab = new Hashtable();
	Hashtable ht_box = new Hashtable();
	boolean added = false;
	JDialog diag = null;
	public void setDialog(JDialog jd) {
		diag = jd;
	}
	public JDialog getDialog() {
		return diag;
	}
	public void setComponent(JComponent c) {
		comp = c;
		comp.putClientProperty("object", this);
	}
	public void copyFrom(MUDRemote o) throws RemoteException {
		// name = o.name;
		ss = ((MUDObject)o).ss;
		ht = (Hashtable)((MUDObject)o).ht.clone();
		rels = (Hashtable)((MUDObject)o).rels.clone();
	}
	public String id() throws RemoteException {
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
	public void putProp(String index) throws RemoteException {
		ht.put(index, index);
	}
	public Enumeration getPropertyKeys() throws RemoteException {
		return ht.keys();
	}
	public boolean isPropertySet(String index) throws RemoteException {
		return ht.containsKey(index);
	}
	public JTextField getField(String prop) {
		return (JTextField)ht_tf.get(prop);
	}
	public void setField(String prop, JTextField tf) {
		ht_tf.put(prop, tf);
	}
	public void removeField(String prop) {
		ht_tf.remove(prop);
	}
	public JLabel getLabel(String prop) {
		return (JLabel)ht_lab.get(prop);
	}
	public void setLabel(String prop, JLabel lab) {
		ht_lab.put(prop, lab);
	}
	public void removeLabel(String prop) {
		ht_lab.remove(prop);
	}
	public Box getBox(String prop) {
		return (Box)ht_box.get(prop);
	}
	public void setBox(String prop, Box box) {
		ht_box.put(prop, box);
	}
	public void removeBox(String prop) {
		ht_box.remove(prop);
	}
	public String get(String prop) throws RemoteException {
		return (String)rels.get(prop);
	}
	public void putInt(String prop, int value) throws RemoteException {
		rels.put(prop, String.valueOf(value));
	}
	public int getInt(String prop) throws RemoteException {
		
		String s = (String)rels.get(prop);
		if (s == null) {
			return 0;
		}
		int i = Integer.parseInt(s);
		return i;
	}
	public void put(String prop, String value) throws RemoteException {
		rels.put(prop, value);
	}
	public void remove(String prop) {
		rels.remove(prop);
	}
	public Enumeration keys() throws RemoteException {
		return rels.keys();
	}
	public static void getNetworkPermission() {

/*
        cs = (ClipboardService)ServiceManager.lookup
                 ("javax.jnlp.ClipboardService");
		String [] sns = javax.jnlp.ServiceManager.getServiceNames();
		for (int s = 0; s < sns.length; s++) {
			System.err.println(sns[s]);
		}
*/
	}
}
