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

public class Base extends MUDRoom {
	public Base(String n) throws RemoteException
	{
		super(n);
	}
	public int command(MUDRemote subject, Vector comm)
	    throws RemoteException {
		String verb = null;
		if (comm.size() > 0) {
			verb = (String)comm.elementAt(0);
		} else {
			return 0;
		}
		if (verb == null) {
			return 0;
		}
		int ev = 0;
		if (verb.equalsIgnoreCase("/se")) {
			ev = takeExit(getProperty("Generic_Room.URL"), subject);
		}
		if (verb.equalsIgnoreCase("/south")) {
			ev = takeExit(getProperty("Mountain_Top.URL"), subject);
		}
		if (verb.equalsIgnoreCase("/exits")) {
			Vector v = new Vector(1);
			v.addElement("/south, /se");
			ev = subject.tell(v);
		}
		return ev;			
	}
}
