package net.coderextreme.icbm;

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
