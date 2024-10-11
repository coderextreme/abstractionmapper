package net.coderextreme.dev;
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
import java.lang.reflect.*;

public class LocalMUDClient extends URLClassLoader {
	static public void main(String args[]) {
		try {
			String nick = "John";
			if (args.length > 0) {
				nick = args[0];
			}
			String room = "rmi://localhost/Generic_Room";
			if (args.length > 1) {
				room = args[1];
			}
			String url = "jar:http://localhost/webstart/chat/icbmc.jar!/";
			if (args.length > 2) {
				url = args[2];
			}
			String args2[] = new String[1];
			// String args2[] = new String[2];
			args2[0] = nick;
			// args2[1] = room;

			URL urls[] = new URL[1];
			urls[0] = new URL(url);
			LocalMUDClient j = new LocalMUDClient(urls);
			Class c = j.findClass("icbm.MUDClient");
			System.err.println(c.getName());
			Class params[] = new Class[1];
			params[0] = args2.getClass();
			Constructor cons = c.getConstructor(params);

			Object objs[] = new Object[1];
			objs[0] = args2;
			cons.newInstance(objs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public LocalMUDClient(URL urls[]) {
		super(urls);
	}
}
