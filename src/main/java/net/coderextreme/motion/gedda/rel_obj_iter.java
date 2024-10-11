package net.coderextreme.motion.gedda;

import java.util.*;

class rel_obj_iter
{
	private String r;
	private object_list ol;
	private Enumeration e;
	public rel_obj_iter(object_list ol, object_node on, String rel)
	{
		e = on.rel_list().elements();
		r = rel;  /* r == null specifies ALL relationships */
		this.ol = ol;
	}
	public object_node  select_rel_object()
	{
		object_node on;
		on = null;
		while(e.hasMoreElements()) {
			relationship_node rn;
			rn = (relationship_node)e.nextElement();
			if (r == null || rn.relationship().equals(r)) {
				on = ol.find(rn.related_id());
				break;
			}
		}
		return on;
	}
}
