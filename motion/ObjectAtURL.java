package motion;

import java.io.*;

public class ObjectAtURL implements Serializable {
	public String URL;
	public String objectID;
	public String toString() {
		return URL+"?"+objectID;
	}
}
