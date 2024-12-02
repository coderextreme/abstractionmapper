package net.coderextreme.icbm;
import java.rmi.RemoteException;
import java.util.Vector;

public class Base extends MUDRoom {
	public Base(String n) throws RemoteException
	{
		super(n);
	}
	@Override
	public int command(MUDRemote subject, Vector<Object> comm)
	    throws RemoteException {
		String verb;
		if (!comm.isEmpty()) {
			verb = (String)comm.elementAt(0);
		} else {
			return 0;
		}
		if (verb == null) {
			return 0;
		}
		int ev = 0;
		if (verb.equalsIgnoreCase("/se")) {
			ev = takeExit(getProperty("Generic_Room.URL"), subject);
		}
		if (verb.equalsIgnoreCase("/south")) {
			ev = takeExit(getProperty("Mountain_Top.URL"), subject);
		}
		if (verb.equalsIgnoreCase("/exits")) {
			Vector<Object> v = new Vector<>(1);
			v.addElement("/south, /se");
			ev = subject.tell(v);
		}
		return ev;			
	}
}
