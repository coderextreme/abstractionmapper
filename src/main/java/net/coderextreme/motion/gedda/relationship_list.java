package net.coderextreme.motion.gedda;

import java.util.*;

class relationship_list extends Vector<relationship_node> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public relationship_node first() {
		return get(0);
	}
	public void insert_relationship(String i, String r, String v)
	{
		add(0,new relationship_node (i, r, v));
	}

	public void delete_relationship(String i, String a)
	{
		for (ListIterator<relationship_node> le = listIterator(); le.hasNext(); ) {
			relationship_node temp = (relationship_node)le.next();
			if (temp != null &&
				temp.relationship().equals(a) &&
				temp.related_id().equals(i)) {
				le.remove();
			}
		}
	}
//	public void put(object_node object_node, String relParental, object_node parent) {
//		// TODO Auto-generated method stub
//		
//	}
//	public object_node get(object_node object_node, String relParental) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}

