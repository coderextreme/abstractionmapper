package icbm;
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

public class ParentItem implements Serializable {
	private MUDRemote obj;
	private String url;

	public ParentItem(MUDRemote o, String u) {
		obj = o;
		url = u;
	}
	public MUDRemote object() {
		return obj;
	}
	public void setObject(MUDRemote mr) {
		obj = mr;
	}
	public void setURL(String u) {
		url = u;
	}
	public String getURL() {
		return url;
	}
	public boolean equals(Object o) {
		return obj.equals(((ParentItem)o).obj);
	}
	public int hashcode() {
		return url.hashCode();
	}
}
