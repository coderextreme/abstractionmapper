import java.util.*;
import java.awt.event.*;
import java.awt.*;

class ftpoidt {
	void toggle(object_node current) {
		if (current != null) {
			Color back = current.getBackground();
			Color fore = current.getForeground();
			current.setBackground(fore);
			current.setForeground(back);
			current.repaint();
		} else {
			System.err.println("Toggle failed");
		}
	}
}

class d_tech_list extends Vector<object_node> {
	private oimove oimove_;
	public d_tech_list(oimove oimove_) {
		this.oimove_ = oimove_;
	}
	void prepend(object_node objp) {
		insertElementAt(objp, 0);
	}

	void apply_to_node(ftpoidt impl)
	{
		for (Iterator i = iterator(); i.hasNext(); ) {
			object_node objp = (object_node)i.next();
			impl.toggle(objp);
		}
	}

	boolean single_element() {
		return size() == 1;
	}
	int number_of_elements() {
		return size();
	}

	void selection_add(object_node objp, MouseEvent eventp)
	{
		ftpoidt t = new ftpoidt();
		t.toggle(objp);
		if (!contains(objp)) {
			System.err.println("Adding by chance");
			prepend(objp);
		} else {
			remove(objp);
		}
	}

	void moveto(object_node objp, KeyEvent eventp)
	{
		object_node par;
		object_node on;
		object_node npon;

		npon = objp;
		Iterator<object_node> sit = iterator();
		while(sit.hasNext()) {
			object_node o = sit.next();
			par = o.parent();
			on = o;

			// o.set_associated_object(objp, oimove_.last_x, oimove_.last_y, OI_ACTIVE);
			/* update the relationship */
			on.put(oimove_.REL_PARENTAL, npon.id());
			oimove_.location_from_event(on);
		}
	}
	void linkto(object_node objp, KeyEvent eventp)
	{
		/* add parental relationship to selected objects */
		Iterator<object_node> sit = this.iterator();
		object_node o;
		object_node on;
		object_node subject;

		while(sit.hasNext()) {
			o = sit.next();
			on = o;
			subject = oimove_.subject_node(on);
			oimove_.new_icon(objp, subject, on, 0, 0);
		}
	}

	void copyto(oimove selectedOimove, long x, long y)
	{
		Iterator<object_node> sit = iterator();
		object_node o;
		object_node on;
		object_node old_view;
		object_node old_subject;

		while(sit.hasNext()) {
			o = sit.next();
			System.err.println("new object");
			on = object_list.OBJECT_LIST.new_object(selectedOimove);
			old_view = o;
			System.err.println("subject node");
			Iterator<oimove> oimoves = oimove.OIMOVES.iterator();
			while (oimoves.hasNext()) {
				old_subject = oimoves.next().subject_node(old_view);
				if (old_subject != null) {
					System.err.println("copy prop");
					old_subject.copy_properties(on);
				}
			}

			/* place the icon  */
			System.err.println("new icon");
			selectedOimove.new_icon(o, on, old_view, x, y);
			System.err.println("finish loop");
		}
		selectedOimove.invalidate();
		selectedOimove.validate();
		selectedOimove.repaint();
	}
	public void removeFromOimove(oimove oim) {
		Iterator<object_node> sit = iterator();
		while(sit.hasNext()) {
			object_node o = sit.next();
			oim.objectsOnWindow.remove(o);
		}
	}
}
