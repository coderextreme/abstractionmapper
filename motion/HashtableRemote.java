package motion;

import java.util.*;
import java.io.*;
import java.rmi.*;
import java.net.*;
import java.rmi.server.*;
import java.rmi.registry.*;

interface MapRemote extends Remote {
	public void clear() throws RemoteException;
	public boolean containsKey(Object key) throws RemoteException;
	public boolean containsValue(Object value) throws RemoteException;
	public Set entrySet() throws RemoteException;
	public Object get(Object key) throws RemoteException;
	public boolean isEmpty() throws RemoteException;
	public Set keySet() throws RemoteException;
	public Object put(Object key, Object value) throws RemoteException;
	public void putAll(Map t) throws RemoteException;
	public Object remove(Object key) throws RemoteException;
	public int size() throws RemoteException;
	public Collection values() throws RemoteException;
	public void save() throws RemoteException;
	public void restore() throws RemoteException;
	public void setURL(String u) throws RemoteException;
	public String getURL() throws RemoteException;
	public void updateValueList(String key, String att, Vector newvalues) throws RemoteException;
}

public class HashtableRemote implements MapRemote {
	Hashtable ht = new Hashtable();
	String url = null;
	public HashtableRemote(String url, String name) throws RemoteException, MalformedURLException {
		MapRemote stub = (MapRemote) UnicastRemoteObject.exportObject(this, 0);
		Registry registry = LocateRegistry.getRegistry();
		registry.rebind(name, stub);
		// Naming.rebind(url, this);
		setURL(url);
	}
	public void setURL(String u) throws RemoteException {
		url = u;
	}
	public String getURL() throws RemoteException {
		return url;
	}
	public void updateValueList(String key, String att, Vector newvalues) throws RemoteException {
		Hashtable attrs = (Hashtable)get(key);
		attrs.put(att, newvalues);
	}
	public void clear() throws RemoteException {
		ht.clear();
	}
	public boolean containsKey(Object key) throws RemoteException {
		return ht.containsKey(key);
	}
	public boolean containsValue(Object value) throws RemoteException {
		return ht.containsValue(value);
	}
	public Set entrySet() throws RemoteException {
		return ht.entrySet();
	}
	public Object get(Object key) throws RemoteException {
		return ht.get(key);
	}
	public boolean isEmpty() throws RemoteException {
		return ht.isEmpty();
	}
	public Set keySet() throws RemoteException {
		return ht.keySet();
	}
	public Object put(Object key, Object value) throws RemoteException {
		return ht.put(key, value);
	}
	public void putAll(Map t) throws RemoteException {
		ht.putAll(t);
	}
	public Object remove(Object key) throws RemoteException {
		return  ht.remove(key);
	}
	public int size() throws RemoteException {
		return ht.size();
	}
	public Collection values() throws RemoteException {
		return ht.values();
	}
	public static void main(String args[]) {
		try {
			MapRemote mr = new HashtableRemote("rmi://localhost/ht", "ht");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void save() throws RemoteException {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("htdata"));
			oos.writeObject(ht);
			oos.close();
		} catch (Exception e) {
			throw new RemoteException("Couldn't write table.  Don't do anything else!");
		}
	}
	public void restore() throws RemoteException {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("htdata"));
			ht = (Hashtable)ois.readObject();
			ois.close();
		} catch (Exception e) {
			Motion.OBJECT_LIST.objectListFromText("prop.data");
		}
	}
}
