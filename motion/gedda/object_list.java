import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class object_list extends Hashtable<String,object_node> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static public object_list OBJECT_LIST = new object_list("id.data", "rel.data", "prop.data");
	private int next_id;
	private String idfn;
	private String relfn;
	private String propfn;
	private oimove oim = new oimove();
	public object_list() {
		next_id = 0;
	};
	public void write_db(BufferedWriter f, BufferedWriter pf)
	throws IOException {
		if (f != null) {
			f.write(""+next_id);
			f.newLine();
			f.flush();
			// f.close();
		}
		if (pf != null) {
			Iterator<object_node> e2 = values().iterator();
			while (e2.hasNext()) {
				object_node on = e2.next();

				for (String key : on.keys()) {
					pf.write(on.id()+" "+
						key+" "+
						on.get(key));
					pf.newLine();
				}
			}
			pf.flush();
			// pf.close();
		}
	}
	public void save_db()
	{
	   try {
		BufferedWriter f, pf, rf;
		f = new BufferedWriter(new FileWriter(idfn));
		pf = new BufferedWriter(new FileWriter(propfn));
		write_db(f, pf);

	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	}
	public void write_db()
	{
	   try {
		BufferedWriter f, pf;
		f = new BufferedWriter(new OutputStreamWriter(System.out));
		pf = new BufferedWriter(new OutputStreamWriter(System.out));
		write_db(f, pf);

	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	}
	public object_list(String ifn, String rfn, String pfn)
	{
	    try {
		BufferedReader f;
		String s;
		String i, i2, a, v;
		object_node o;

		idfn = ifn;
		relfn = rfn;
		propfn = pfn;
		clear();
		f = new BufferedReader(new FileReader(ifn));
		if (f != null) {
			s = f.readLine();
			next_id = new Integer(s).intValue();
			f.close();
		}
		f = new BufferedReader(new FileReader(pfn));
		if (f != null) {
			while ((s = f.readLine()) != null) {
				int s0 = 0;
				int s1;
				int s2;
				s1 = s.indexOf(' ', s0);
				i = s.substring(s0, s1);
				s2 = s.indexOf(' ', s1+1);
				a = s.substring(s1+1, s2);
				v = s.substring(s2+1);
				//System.err.println("P|"+i+"|"+a+"|"+v);
				o = insert_object(i,null);
				o.put(a, v);
			}
			f.close();
		}
		//System.err.println("------------------");
		//write_db();
		//System.err.println("------------------");

		f = new BufferedReader(new FileReader(rfn));
		if (f != null) {
			while ((s = f.readLine()) != null) {
				int s0 = 0;
				int s1;
				int s2;
				int s3;
				s1 = s.indexOf(' ', s0);
				i = s.substring(s0, s1);
				s2 = s.indexOf(' ', s1+1);
				i2 = s.substring(s1+1, s2);
				s3 = s.indexOf(' ', s2+1);
				a = s.substring(s2+1, s3);
				//System.err.println("R|"+i+"|"+i2+"|"+a+"|"+v);
				o = insert_object(i2,null);
				o.put(a, i);
			}
			f.close();
		}
		//System.err.println("------------------");
		//write_db();
		//System.err.println("------------------");
	    } catch (FileNotFoundException fnfe) {
		fnfe.printStackTrace();
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	}
	public object_node find(String i)
	{
		Iterator<String> e = keySet().iterator();
		object_node o = null;
		while(e.hasNext()) {
			Object key = e.next();
			o = (object_node)get(key);
			if (o.id().equals(i)) {
				break;
			}
			o = null;
		}
		return o;
	};
	public List<object_node> find_relatees(object_node p /* parent */, String prop) {
		object_node o = null;
		List<object_node> v = new Vector<object_node>(size());
		Iterator<String> e = keySet().iterator();
		while(e.hasNext()) {
			Object key = e.next();
			o = (object_node)get(key);
			if (o != null && o.get(prop) != null && o.get(prop).equals(p.id())) {
				v.add(o);
			}
		}
		return v;
	}

	public object_node insert_object(String oname, oimove oim)
	{
		object_node on = (object_node)get(oname);
		if (on == null) {
			on  = new object_node(oname);
			put(oname, on);
			if (oim == null) {
				oim = this.oim;
			}
			oim.objectsOnWindow.add(on);
		}
		return on;
	}

	public object_node new_object(oimove oim)
	{
		String s;
		object_node on;

		do {
			s = ""+next_id;
			on = find(s);
			next_id++;
		} while (on != null);
		on = insert_object(s, oim);
		return on;
	}

	public void delete_object(String o)
	{
		remove(o);
	}
	public void insert_relationship(object_node pon, object_node con,
		String rel)
	{
		con.put(rel, pon.id());
	}

	public void delete_relationship(object_node pon, String rel)
	{
		pon.remove(rel);
	}

	public void dumpdb() {
		Iterator<object_node> e = this.values().iterator();
		while (e.hasNext()) {
			object_node on2 = e.next();
			System.err.print(on2.id());
			for (String key : on2.keys()) {
				String value = on2.get(key);
				System.err.println(":"+key+"="+value);
			}
		}
	}
}
