package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class App {
	public static void main(String args[]) {
        	try {
			Registry registry = LocateRegistry.createRegistry(1099);
			System.out.println("RMI registry started on port 1099");
			icbm.MUDServer.main(new String[] {"ICBM"});
			motion.Motion.main(new String[] {"motion/contact.data", "motion/gedda/"});
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
}
