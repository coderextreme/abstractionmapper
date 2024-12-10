package net.coderextreme.motion;

import java.rmi.RemoteException;
import java.util.Vector;

import net.coderextreme.icbm.MUDRemote;

public class MContainer extends Operation {
	private Motion m = null;
	private String rel = null;
	public MContainer(Motion m, String rel, MUDRemote bo) throws RemoteException  {
		this.m = m;
		this.rel = rel;
		Motion.OBJECT_LIST.put_object(this);
		if (bo != null) {
			this.put(Motion.PROP_HEAD, bo.id());
			this.put(Motion.PROP_TAIL, bo.id());
		}
	}
	@Override
	public void play(Motion m) {
	    try {
		m.setSelectionAct(false);
		String head = get(Motion.PROP_HEAD);
		System.out.println("begin head is "+this.get(Motion.PROP_HEAD));
		Operation ptr = (Operation)(Motion.OBJECT_LIST.find(head));
		if (ptr != null) {
			ptr.play(m);
			
			Vector v2 = Motion.OBJECT_LIST.find_relatees(ptr, rel);
			System.out.println("first object is "+ptr.id());
			MUDRemote r = (MUDRemote)v2.firstElement();
			String ptrid = r.get(Motion.PROP_CHILD);
			MUDRemote bo = Motion.OBJECT_LIST.find(ptrid);
			while (bo != null) {
				System.out.println("next object is "+bo.id());
				if (bo instanceof Operation operation) {
					ptr = operation;
					ptr.play(m);
				}
				v2 = Motion.OBJECT_LIST.find_relatees(bo, rel);
				if (!v2.isEmpty()) {
					r = (MUDRemote)v2.firstElement();
					ptrid = r.get(Motion.PROP_CHILD);
					bo = Motion.OBJECT_LIST.find(ptrid);
				} else {
					bo = null;
				}
			}
		}
		m.setSelectionAct(true);
	    } catch (RemoteException re) {
			re.printStackTrace(System.err);
	    }
	}
        @Override
	public int add(MUDRemote bo) throws RemoteException {
		String previd = this.get(Motion.PROP_TAIL);
		System.out.println("tail is "+this.get(Motion.PROP_TAIL));
		System.out.println("head is "+this.get(Motion.PROP_HEAD));
		if (previd == null) {
			this.put(Motion.PROP_HEAD, bo.id());
			this.put(Motion.PROP_TAIL, bo.id());
			previd = bo.id();
		}
		if (previd != null && !previd.trim().equals("")) {
			MUDRemote prev = Motion.OBJECT_LIST.find(previd);
			if (prev != bo) {
				MUDRemote rl =  Motion.OBJECT_LIST.new_object();
				rl.put(Motion.PROP_RELATIONSHIP, rel);
				rl.put(Motion.PROP_PARENT, previd);
				rl.put(Motion.PROP_CHILD, bo.id());
				BrowseRelationship br = new BrowseRelationship(
					prev, bo, rl, rel);
				System.out.println("rel=="+prev.id()+"=="+bo.id()+"=="+rl.id()+"=="+rel);
				m.addRelationship(br);
			}
		}
		this.put(Motion.PROP_TAIL, bo.id());
		System.out.println("end tail is "+this.get(Motion.PROP_TAIL));
		System.out.println("end head is "+this.get(Motion.PROP_HEAD));
		System.out.println("ADDEND "+bo.id()+" AX="+bo.get(Motion.PROP_X)+" AY="+ bo.get(Motion.PROP_Y));
		return 1;
	}
}
