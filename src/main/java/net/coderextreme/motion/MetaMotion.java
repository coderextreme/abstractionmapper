package net.coderextreme.motion;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class MetaMotion extends JInternalFrame {
	Motion m;
	JTextArea metadata;
	public MetaMotion(Motion m) {
		this.m = m;
		JButton atsi = new JButton("Apply to Selected Items");
		JButton asfi = new JButton("Alternatives Search for Items");
		JButton csfi = new JButton("Combined Search for Items");
		JButton rm = new JButton("Retrieve Metadata");
		atsi.addActionListener(new ApplyMetadata());
		asfi.addActionListener(new OrSearch());
		csfi.addActionListener(new AndSearch());
		rm.addActionListener(new Retrieve());
		Box b = Box.createHorizontalBox();
		b.add(atsi);
		b.add(asfi);
		b.add(csfi);
		b.add(rm);
		metadata = new JTextArea(10, 40);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(b, "North");
		getContentPane().add(metadata, "Center");
		pack();
		show();
	}
	class AndSearch implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			m.andSearch(TextAreaToStringArray(metadata));
		}
	}
	class OrSearch implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			m.orSearch(TextAreaToStringArray(metadata));
		}
	}
	class ApplyMetadata implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			m.apply(TextAreaToStringArray(metadata));
		}
	}
	public String [] TextAreaToStringArray(JTextArea jta) {
		StringTokenizer st = new StringTokenizer(metadata.getText(),"\n");
		Vector strings = new Vector();
		while (st.hasMoreTokens()) {
			strings.addElement(st.nextToken());
		}
		String [] sarray = new String[strings.size()];
		strings.copyInto(sarray);
		return sarray;
	}
	class Retrieve implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			m.getSelectedMetaData(metadata);
			repaint();
		}
	}
}
