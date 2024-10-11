package net.coderextreme.motion;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.rmi.*;
import javax.swing.*;

public class MoveOp extends Operation {
	Component compo = null;
	public MoveOp(Motion m, Component compo) throws RemoteException {
		Motion.OBJECT_LIST.put_object(this);
		this.compo = compo;
	}
	public void play(Motion m) {
	    try {
		System.out.println(this.id()+" EVENT X="+this.get(Motion.PROP_EVENT_X)+
			" Y="+ this.get(Motion.PROP_EVENT_Y));
		m.setSelectionAct(false);
		// JComponent jc = getComponent();
		MouseEvent me = new MouseEvent(compo,
			0, 0, 0,
			Integer.parseInt(this.get(Motion.PROP_EVENT_X)) -
			Integer.parseInt(this.get(Motion.PROP_ORIG_X)),
			Integer.parseInt(this.get(Motion.PROP_EVENT_Y)) -
			Integer.parseInt(this.get(Motion.PROP_ORIG_Y)),
			1, false);
		String order = get(Motion.PROP_ORDER);
		System.out.println("2Fiddling with the mouse "+order+" "+this.id());
		System.out.println("MOX="+me.getX()+
			" MOY="+ me.getY());
		if (order.equals(Motion.VALUE_FIRST)) {
			System.out.println("GEN PRES");
			m.mousePressed(me);
		} else if (order.equals(Motion.VALUE_MIDDLE)) {
			System.out.println("GEN DRAG");
			m.mouseDragged(me);
		} else if (order.equals(Motion.VALUE_LAST)) {
			System.out.println("GEN RELE");
			m.mouseReleased(me);
		} else {
			System.out.println("Unknown value for PROP_ORDER");
		}
		m.setSelectionAct(true);
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
}
