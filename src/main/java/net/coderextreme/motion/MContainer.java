package net.coderextreme.motion;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.rmi.*;
import javax.swing.*;
import net.coderextreme.icbm.*;

public class MContainer extends Operation {
	private Motion m = null;
	private String rel = null;
	public MContainer(Motion m, String rel, MUDRemote bo) throws RemoteException  {
		this.m = m;
		this.rel = rel;
		m.OBJECT_LIST.put_object(this);
		if (bo != null) {
			this.put(m.PROP_HEAD, bo.id());
			this.put(m.PROP_TAIL, bo.id());
		}
	}
	public void play(Motion m) {
	    try {
		m.setSelectionAct(false);
		String head = get(m.PROP_HEAD);
		System.out.println("begin head is "+this.get(m.PROP_HEAD));
		Operation ptr = (Operation)(m.OBJECT_LIST.find(head));
		if (ptr != null) {
			ptr.play(m);
			
			Vector v2 = m.OBJECT_LIST.find_relatees(ptr, rel);
			System.out.println("first object is "+ptr.id());
			MUDRemote r = (MUDRemote)v2.firstElement();
			String ptrid = r.get(m.PROP_CHILD);
			MUDRemote bo = m.OBJECT_LIST.find(ptrid);
			while (bo != null) {
				System.out.println("next object is "+bo.id());
				if (bo instanceof Operation) {
					ptr = (Operation)bo;
					ptr.play(m);
				}
				v2 = m.OBJECT_LIST.find_relatees(bo, rel);
				if (v2.size() > 0) {
					r = (MUDRemote)v2.firstElement();
					ptrid = r.get(m.PROP_CHILD);
					bo = m.OBJECT_LIST.find(ptrid);
				} else {
					bo = null;
				}
			}
		}
		m.setSelectionAct(true);
	    } catch (RemoteException re) {
		re.printStackTrace();
	    }
	}
	public int add(MUDRemote bo) throws RemoteException {
		String previd = this.get(m.PROP_TAIL);
		String h = this.get(m.PROP_HEAD);
		System.out.println("tail is "+this.get(m.PROP_TAIL));
		System.out.println("head is "+this.get(m.PROP_HEAD));
		if (previd == null) {
			this.put(m.PROP_HEAD, bo.id());
			this.put(m.PROP_TAIL, bo.id());
			previd = bo.id();
		}
		if (previd != null && !previd.trim().equals("")) {
			MUDRemote prev = m.OBJECT_LIST.find(previd);
			if (prev != bo) {
				MUDRemote rl =  m.OBJECT_LIST.new_object();
				rl.put(m.PROP_RELATIONSHIP, rel);
				rl.put(m.PROP_PARENT, previd);
				rl.put(m.PROP_CHILD, bo.id());
				BrowseRelationship br = new BrowseRelationship(
					prev, bo, rl, rel);
				System.out.println("rel=="+prev.id()+"=="+bo.id()+"=="+rl.id()+"=="+rel);
				m.addRelationship(br);
			}
		}
		this.put(m.PROP_TAIL, bo.id());
		System.out.println("end tail is "+this.get(m.PROP_TAIL));
		System.out.println("end head is "+this.get(m.PROP_HEAD));
		System.out.println("ADDEND "+bo.id()+" AX="+bo.get(Motion.PROP_X)+" AY="+ bo.get(Motion.PROP_Y));
		return 1;
	}
}
