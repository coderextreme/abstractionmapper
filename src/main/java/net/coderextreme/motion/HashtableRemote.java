package net.coderextreme.motion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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
	public void updateValueList(String key, String att, Vector<Object> newvalues) throws RemoteException;
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
	@Override
	public void setURL(String u) throws RemoteException {
		url = u;
	}
	@Override
	public String getURL() throws RemoteException {
		return url;
	}
	@Override
	public void updateValueList(String key, String att, Vector<Object> newvalues) throws RemoteException {
		Hashtable attrs = (Hashtable)get(key);
		attrs.put(att, newvalues);
	}
	@Override
	public void clear() throws RemoteException {
		ht.clear();
	}
	@Override
	public boolean containsKey(Object key) throws RemoteException {
		return ht.containsKey(key);
	}
	@Override
	public boolean containsValue(Object value) throws RemoteException {
		return ht.containsValue(value);
	}
	@Override
	public Set entrySet() throws RemoteException {
		return ht.entrySet();
	}
	@Override
	public Object get(Object key) throws RemoteException {
		return ht.get(key);
	}
	@Override
	public boolean isEmpty() throws RemoteException {
		return ht.isEmpty();
	}
	@Override
	public Set keySet() throws RemoteException {
		return ht.keySet();
	}
	@Override
	public Object put(Object key, Object value) throws RemoteException {
		return ht.put(key, value);
	}
	@Override
	public void putAll(Map t) throws RemoteException {
		ht.putAll(t);
	}
	@Override
	public Object remove(Object key) throws RemoteException {
		return  ht.remove(key);
	}
	@Override
	public int size() throws RemoteException {
		return ht.size();
	}
	@Override
	public Collection values() throws RemoteException {
		return ht.values();
	}
	public static void main(String args[]) {
		try {
			MapRemote mr = new HashtableRemote("rmi://localhost/ht", "ht");
		} catch (MalformedURLException | RemoteException e) {
			e.printStackTrace(System.err);
		}
	}
    @Override
	public void save() throws RemoteException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("htdata"))){
			
			oos.writeObject(ht);
			oos.close();
		} catch (IOException e) {
			throw new RemoteException("Couldn't write table.  Don't do anything else!");
		}
	}
    @Override
	public void restore() throws RemoteException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("htdata"))) {
			
			ht = (Hashtable)ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			Motion.OBJECT_LIST.objectListFromText(new File("prop.data"));
		}
	}
}
