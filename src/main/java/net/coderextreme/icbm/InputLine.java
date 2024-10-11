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

public class InputLine extends Vector {
	public InputLine get_buffer(String str) {
		if (str != null) {
			StringTokenizer strtok = new StringTokenizer(str); 
			while(strtok.hasMoreTokens()) {
				String token = strtok.nextToken();
				addElement(token);
			}
		}
		return this;
	}
	public InputLine get_line(InputStream is) {
		BufferedReader bufread = new BufferedReader(new InputStreamReader(is));
		try {
			do {
				removeAllElements();
				String str = bufread.readLine();
				get_buffer(str);
			} while(size() <= 0);
			return this;
		} catch(IOException ioe) {
			return null;
		}
/*
		removeAllElements();
		StreamTokenizer st = new StreamTokenizer(bufread);
		st.wordChars(33,126);
		st.eolIsSignificant(true);
		InputLine line  = null;
		int token;
		do {
			try {
				token = st.nextToken();
			} catch (IOException ioe) {
				System.out.println("IO Error!");
				break;
			}
			if (token == st.TT_WORD) {
				addElement(st.sval);
			} else if (token == st.TT_NUMBER) {
				if (st.nval == (int)st.nval) {
					addElement(new Integer((int)st.nval));
				} else {
					addElement(new Double(st.nval));
				}
			} else if (token == st.TT_EOL) {
				line = this;
			} else if (token == st.TT_EOF) {
				break;
			} else {
				StringBuffer s = new StringBuffer();
				s.append(st.ttype);
				addElement(s);
			}
		} while (token != st.TT_EOL || size() <= 0);
		return line;
*/
	}
}
