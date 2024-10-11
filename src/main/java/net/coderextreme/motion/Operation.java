package net.coderextreme.motion;

import net.coderextreme.icbm.*;
import java.rmi.*;

public abstract class Operation extends MUDObject {
	public Operation() throws RemoteException {};
	public abstract void play(Motion m);
}
