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
import java.rmi.RemoteException;
import java.util.Vector;

public class MUDRoom extends MUDObject {
	public MUDRoom(String n) throws RemoteException
	{
		super(getProperty(n+".URL"));
		System.err.println("getting property "+n+".URL");
		System.err.println("property is "+(getProperty(n+".URL")));
		// setURL(getProperty(n+".URL"));
	}
	@Override
	public int command(MUDRemote subject, Vector<Object> comm)
		throws RemoteException {
		String verb = null;
		if (!comm.isEmpty()) {
			verb = (String)comm.elementAt(0);
		} else {
			return 0;
		}
		if (verb == null) {
			return 0;
		}
		if (subject == this) {
			if (verb.equalsIgnoreCase("/quit")) {
				System.exit(0);
			}
		} // doesn't fall through 
		int ev = 0;
		if (verb.equalsIgnoreCase("/west")) {
			ev  = takeExit(getProperty("Mountain_Top.URL"), subject);
		}
		if (verb.equalsIgnoreCase("/nw")) {
			ev  = takeExit(getProperty("Valley.URL"), subject);
		}
		if (verb.equalsIgnoreCase("/exits")) {
			Vector<Object> v = new Vector<>(1);
			v.addElement("/west, /nw");
			ev = subject.tell(v);
		}
		return ev;
	}
}
