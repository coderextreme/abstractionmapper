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
package icbm;

import java.io.*;
import java.util.*;

public class CommandThread extends Thread {
	InputStream is = null;
	MUDClient mudc;
	MUDObject mc;
	public CommandThread(InputStream is, MUDClient mudc, MUDObject mc) {
		this.is = is;
		this.mudc = mudc;
		this.mc = mc;
	}
	public void run() {
		int c;
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("<table><tr><td>");
			boolean spaces = false;
			boolean linesep = false;
			while ((c = is.read()) != -1) {
				if (c == '\n' || c == '\r') {
					if (!linesep) {
						sb.append("</td></tr>\n<tr><td>");
						linesep = true;
					}
					spaces = false;
				} else if (c == ' ' || c == '\t') {
					if (!spaces) {
				
						sb.append("</td><td>");
						spaces = true;
					}
					linesep = false;
				} else {
					sb.append(""+(char)c);
					spaces = false;
					linesep = false;
				}
			}
			sb.append("</td></tr></table>");
			mudc.addToEnd(sb.toString());
			mc.tell(new Vector());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
