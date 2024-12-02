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
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.text.DefaultCaret;

public class FromMUD extends MUDObject {
	JEditorPane ta = null;
	MUDClient mudc = null;
	public FromMUD(String str, MUDClient mudc, JEditorPane jep) throws RemoteException {
		super(str);
		this.ta = jep;
		DefaultCaret caret = (DefaultCaret)this.ta.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.mudc = mudc;
	}

	@Override
	public int tell(Vector<Object> message) {
        try {
		   // force client to move between locations
		   if (!message.isEmpty() && message.elementAt(0).toString().equals("/goto")) {
				MUDRemote mo = (MUDRemote)message.elementAt(1);
				mudc.switchTo(mo);
				message = new Vector<>();
				message.addElement("You have been transported to "+mo.getName());
		   }
	   } catch (RemoteException e) {
		e.printStackTrace(System.err);
	   }
	   StringBuilder sb = new StringBuilder();
	   try {
		for (Enumeration e = message.elements(); e != null && e.hasMoreElements();) {
			String s = e.nextElement().toString();
			sb.append(s);
			sb.append(" ");
		}
		sb.append("<br>\n");
		mudc.addToEnd(sb.toString());
		try {
		    	//Rectangle r = new Rectangle(0,ta.getHeight()-2,1,1);
			//ta.scrollRectToVisible(r);
		} catch (NullPointerException npe) {
			System.err.println("NPE");

		//} catch (BadLocationException ble) {
		//	ble.printStackTrace();
		}
	    } catch (NullPointerException npe) {
			npe.printStackTrace(System.err);
	    }
	    return 1;
	}
};
