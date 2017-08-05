public class prop_val_iter
{
	private String prop_name;
	property_list ol;
	public prop_val_iter(property_list pl, String pname) {
		ol = pl;
		prop_name = pname;
	}
	public prop_val_iter(object_node on, String pname) {
		ol = on.prop_list();
		prop_name = pname;
	}
	public String select_property() {

		return (String)ol.get(prop_name);
	}
}
