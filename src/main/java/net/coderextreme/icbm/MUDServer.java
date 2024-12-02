package net.coderextreme.icbm;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

public class MUDServer extends MUDClient {
	public MUDServer(String args[]) {
		super();
		try {
			rmi = Runtime.getRuntime().exec(new String[] { System.getProperty("java.home")+File.separator+"bin"+File.separator+"rmiregistry", "1099" });
			Thread.sleep(3000);
			System.out.println("RMI registry started on port 1099");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace(System.err);
		}
		try {
			MUDRemote room = new MUDRoom("Generic_Room");
			Thread.sleep(1);
			MUDRemote mt = new MountainTop("Mountain_Top");
			Thread.sleep(1);
			MUDRemote val = new Valley("Valley");
			Thread.sleep(1);
			MUDRemote cp = new ClassParent("class_parent");
			Thread.sleep(1);
			MUDRemote base = new Base("base");
			Thread.sleep(1);
			MUDClient.main(args);
		} catch (InterruptedException | RemoteException e) {
			System.err.println("Exception "+e);
			e.printStackTrace(System.err);
		}
	}
	public static void main(String args[]) {
		MUDServer ms = new MUDServer(args);
/*
		if (ms.rmi != null) {
			ms.rmi.destroy();
			ms.rmi = null;
		}
		System.exit(0);
*/
		
	}
}
