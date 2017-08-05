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

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class SendText implements ActionListener {
	MUDObject mc = null;
	MUDClient mudc = null;
	JTextField tf = null;
	public SendText(MUDClient mudc, MUDObject mc, JTextField tf) {
		this.mc = mc;
		this.mudc = mudc;
		this.tf = tf;
	}
	public void actionPerformed(ActionEvent ae) {
	    InputLine line = new InputLine();
	    line = line.get_buffer(tf.getText());
/*
	    if (tf.getText().startsWith(";")) {
		    String cmd = (String)line.elementAt(0);
		    cmd = cmd.substring(1);
		    line.setElementAt(cmd, 0);
		    String[] argv = new String[line.size()];
		    int arg = 0;
		    for (Enumeration ew = line.elements();
				ew.hasMoreElements(); arg++) {
			argv[arg] = (String)ew.nextElement();
		    }
		    try {
			    Process proc = Runtime.getRuntime().exec(argv);
			    InputStream is = proc.getInputStream();
			    CommandThread ct = new CommandThread(is, mudc, mc);
			    ct.start();
		    } catch (Exception e) {
			 mudc.addToEnd("Problems starting process :"+e);
		    }
	    } else {
*/
		    mc.processInput(line);
/*
	    }
*/
	    tf.setText("");
	}
}
