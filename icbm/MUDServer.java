package icbm;

import java.rmi.*;
import java.io.*;

public class MUDServer extends MUDClient {
	public MUDServer(String args[]) {
		super();
		try {
			rmi = Runtime.getRuntime().exec(System.getProperty("java.home")+File.separator+"bin"+File.separator+"rmiregistry 1099");
			Thread.sleep(2000);
		} catch (Exception e) {
			System.err.println("Exception "+e);
			e.printStackTrace();
		}
		try {
			icbm.MUDRemote room = new icbm.MUDRoom("Generic_Room");
			Thread.sleep(1);
			icbm.MUDRemote mt = new icbm.MountainTop("Mountain_Top");
			Thread.sleep(1);
			icbm.MUDRemote val = new icbm.Valley("Valley");
			Thread.sleep(1);
			icbm.MUDRemote cp = new icbm.ClassParent("class_parent");
			Thread.sleep(1);
			icbm.MUDRemote base = new icbm.Base("base");
			Thread.sleep(1);
			icbm.MUDClient.main(args);
		} catch (Exception e) {
			System.err.println("Exception "+e);
			e.printStackTrace();
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
