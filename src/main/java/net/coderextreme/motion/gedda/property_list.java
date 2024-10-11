package net.coderextreme.motion.gedda;

import java.util.Hashtable;
import javax.swing.JTextField;

public class property_list extends Hashtable<String, String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Hashtable<String, JTextField> httf = new Hashtable<String, JTextField>();
	public void insert_property(String prop, String v)
	{
		put(prop, v);
	}

	public String find(String prop)
	{
		return get(prop);
	}

	public void delete_property(String prop)
	{
		remove(prop);
		httf.remove(prop);
	}
	public JTextField getField(String prop) {
		return httf.get(prop);
	}
	public void setField(String prop, JTextField tf) {
		httf.put(prop, tf);
	}
}

