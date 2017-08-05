class relationship_node {
	private String rel_id;
	private String rel;
	private String val;
	public relationship_node(String rid, String r, String v) {
		rel_id = rid;
		rel = r;
		val = v;
	}
	public String related_id() { return rel_id; }
	public String relationship() { return rel; }
	public String value() { return val; }
	public void related_id(String rid) { rel_id = rid; }
	public void relationship(String r) { rel = r; }
	public void value(String v) { val = v; }
}
