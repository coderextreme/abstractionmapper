package net.coderextreme.motion.gedda;

import java.util.*;

class rel_name_iter
{
	private String rel_id;
	Enumeration e;
	public rel_name_iter(relationship_list rl, String rid) {
		e = rl.elements();
		rel_id = rid;
	};
	public String select_relation_name()
	{
		while(e.hasMoreElements()) {
			relationship_node rn = (relationship_node)e.nextElement();
			if (rn.related_id().equals(rel_id))
				return rn.relationship();
		}
		return null;
	}
}
