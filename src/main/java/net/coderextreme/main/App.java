package net.coderextreme.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import net.coderextreme.icbm.MUDServer;
import net.coderextreme.motion.Motion;

public class App {
	public static void main(String args[]) {
        	try {
			Registry registry = LocateRegistry.createRegistry(1099);
			System.out.println("RMI registry started on port 1099");
			MUDServer.main(new String[] {"ICBM"});
			if (args.length > 1) {
				Motion.main(args);
			} else {
				Motion.main(new String[] {"src/main/resources/net/coderextreme/motion/contact.data", "src/main/resources/net/coderextreme/motion/gedda/"});
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
}
