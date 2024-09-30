package motion;
import java.util.*;
import java.io.*;
import java.rmi.*;
import icbm.*;

class object_list extends Hashtable {
	private int next_id = 0;
	String filename = "prop.data";
	public object_list(String filename) {
		objectListFromText(filename);
		// objectListFromSO();
	}
	public void createRelationshipsFromSO() {
	   try {
		// link in relationships
		MapRemote mr = new HashtableRemote("rmi://localhost/ht", "ht");
		Set keys = mr.keySet();
		Iterator i = keys.iterator();
		while (i.hasNext()) {
			String key = (String)i.next();
			Hashtable attrs = (Hashtable)mr.get(key);
			Enumeration attkeys = attrs.keys();
			while (attkeys.hasMoreElements()) {
				String att = (String)attkeys.nextElement();
				Vector values = (Vector)attrs.get(att);
				Vector newvalues = new Vector(values.size());
				Enumeration vals = values.elements();
				while (vals.hasMoreElements()) {
					Object v = vals.nextElement();
					// note that top level objects must have a string key
					String stri = v.toString().intern();
					if (stri.equals(Motion.PROP_X)) {
						newvalues.addElement(Integer.parseInt(stri));
					} else if (stri.equals(Motion.PROP_Y)) {
						newvalues.addElement(Integer.parseInt(stri));
					} else if (stri.equals(Motion.PROP_WIDTH)) {
						newvalues.addElement(Integer.parseInt(stri));
					} else if (stri.equals(Motion.PROP_HEIGHT)) {
						newvalues.addElement(Integer.parseInt(stri));
					} else if (mr.get(stri) != null) {
						ObjectAtURL uo = new ObjectAtURL();
						uo.URL = mr.getURL();
						uo.objectID = v.toString().intern();
						newvalues.addElement(uo);
					} else {
						newvalues.addElement(v);
					}
				}
				mr.updateValueList(key, att, newvalues);
			}
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	public void objectListFromSO() {
	    try {
		MapRemote mr = new HashtableRemote("rmi://localhost/ht", "ht");
		mr.restore();
		Set keys = mr.keySet();
		Iterator i = keys.iterator();
		while (i.hasNext()) {
			String key = (String)i.next();
			Hashtable attrs = (Hashtable)mr.get(key);
			Enumeration attkeys = attrs.keys();
			while (attkeys.hasMoreElements()) {
				String att = (String)attkeys.nextElement();
				Vector values = (Vector)attrs.get(att);
				Vector newvalues = new Vector(values.size());
				Enumeration vals = values.elements();
				while (vals.hasMoreElements()) {
					Object v = vals.nextElement();
					createObjectAttributeValue(key, att, v.toString());
				}
			}
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	public void objectListFromText(String filename) {
		this.filename = filename;
		try {
			MapRemote mr = new HashtableRemote("rmi://localhost/ht", "ht");
			BufferedReader bf = new BufferedReader(
				new FileReader(filename));
			String buf = null;
			while ((buf = bf.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(buf, "\t");
				String objectkey = st.nextToken().intern();
				String name = st.nextToken();
				String value = st.nextToken();
				createObjectAttributeValue(objectkey, name, value);
				Hashtable attrs = (Hashtable)mr.get(objectkey);
				if (attrs == null) {
					attrs = new Hashtable();
				}
				Vector values = (Vector)attrs.get(name);
				if (values == null) {
					values = new Vector();
				}
				values.addElement(value);
				attrs.put(name, values);
				mr.put(objectkey.intern(), attrs);
			}
			createRelationshipsFromSO();
			mr.save();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Couldn't load from "+filename);
		}
	}
	public MapRemote objectFromPropertyFile(String filename, String objectkey) {
		try {
			File file = new File(filename);
			System.err.println("Reading "+objectkey+" from "+file.getAbsolutePath());
			MapRemote mr = new HashtableRemote("rmi://localhost/ht", "ht");
			BufferedReader bf = new BufferedReader(new FileReader(file));
			String buf = null;
			while ((buf = bf.readLine()) != null) {
				System.err.println(buf);
				StringTokenizer st = new StringTokenizer(buf, "=");
				String name = st.nextToken();
				String value = st.nextToken();
				createObjectAttributeValue(objectkey.intern(), name, value);
				Hashtable attrs = (Hashtable)mr.get(objectkey.intern());
				if (attrs == null) {
					attrs = new Hashtable();
				}
				ArrayList values = (ArrayList)attrs.get(name);
				if (values == null) {
					values = new ArrayList();
				}
				values.add(value);
				attrs.put(name, values);
				System.err.println(objectkey.intern()+"\t"+name+"="+value+" size "+values.size());
				mr.put(objectkey.intern(), attrs);
			}
			return mr;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.println("Couldn't load from "+filename);
		}
		return null;
	}
	public void createObjectAttributeValue(String objectkey, String name, String value) throws RemoteException {
		try {
			int parsedint = Integer.parseInt(objectkey);
			if (parsedint >= next_id) {
				next_id = parsedint+1;
			}
		} catch (NumberFormatException e) {
		}
		MUDRemote bo = (MUDRemote)get(objectkey);
		if ( bo == null) {
			bo = new MUDObject(objectkey);
			put(objectkey, bo);
		}
		bo.put(name, value);
	}
	public MUDRemote new_object() throws RemoteException
	{
		String s;
		MUDRemote on;

		s = ""+next_id;
		on = insert_object(s);
		next_id++;
		return on;
	}
	public MUDRemote insert_object(String oname) throws RemoteException {
		MUDRemote on = (MUDRemote)get(oname);
		if (on == null) {
			on  = new MUDObject(oname);
			put(oname, on);
		}
		return on;
	}
	public void put_object(MUDRemote on) throws RemoteException {
		String s;
		s = ""+next_id;
		on.setName(s);
		put(s, on);
		next_id++;
	}
	public void delete_object(String o)
	{
		if (get(o) != null) {
			delete_relationships(o);
			remove(o);
		}
	}
	public void delete_relationships(String o) {
	    try {
		MUDRemote obj = (MUDRemote)get(o);
		Enumeration e = elements();
		while(e.hasMoreElements()) {
			MUDRemote bo = (MUDRemote)e.nextElement();
			if (bo.get("parent") != null
			 && bo.get("parent").equals(obj.id())) {
				delete_object(bo.id());
			}
			if (bo.get("child") != null
			 && bo.get("child").equals(obj.id())) {
				delete_object(bo.id());
			}
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	public Vector find_relatees(MUDRemote parent, String prop) throws RemoteException {
		MUDRemote o = null;
		Vector v = new Vector(size());
		Enumeration e = elements();
		while(e.hasMoreElements()) {
			MUDRemote bo = (MUDRemote)e.nextElement();
/*
			System.out.println("looking at child "+bo.id()+
						" for parent "+parent.id()+
						" rel "+ prop);
*/
			if (
				bo.get("parent") != null
				&& bo.get("parent").equals(parent.id())
				&& bo.get("relationship") != null
				&& bo.get("relationship").equals(prop)) {
				v.addElement(bo);
				bo.add(parent);
				System.out.println("found a "+prop+" name = "+bo.id());
			}
		}
		return v;
	}
	public MUDRemote find(String i) {
		return (MUDRemote)get(i);
	}
	public void save_db()
	{
	   try {
		MapRemote mr = new HashtableRemote("rmi://localhost/ht", "ht");
		mr.save();
		BufferedWriter f = new BufferedWriter(new FileWriter(filename));
		write_db(f);
		f.close();

	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	}
	public void write_db(BufferedWriter f)
	throws IOException {
		if (f != null) {
			Enumeration e2 = elements();
			while (e2.hasMoreElements()) {
				MUDRemote on = (MUDRemote)e2.nextElement();

				Enumeration e = on.keys();
				while (e.hasMoreElements()) {
					String key = (String)e.nextElement();
					f.write(on.id()+"\t"+
						key+"\t"+
						on.get(key));
					f.newLine();
				}
			}
			f.flush();
		}
	}
}
