package net.coderextreme.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import net.coderextreme.icbm.*;
import net.coderextreme.motion.*;


public class App {
	public static void main(String args[]) {
        	try {
			Registry registry = LocateRegistry.createRegistry(1099);
			System.out.println("RMI registry started on port 1099");
			MUDServer.main(new String[] {"ICBM"});
			if (args.length > 1) {
				Motion.main(args);
			} else {
				Motion.main(new String[] {"target/classes/net/coderextreme/motion/contact.data", "target/classes/net/coderextreme/motion/gedda/"});
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
}
