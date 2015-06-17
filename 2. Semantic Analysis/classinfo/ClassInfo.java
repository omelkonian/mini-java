package classinfo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassInfo {
	
	public String name;
	public String extendName;
	
	public Map<String, FieldInfo> fields;
	public Map<String, MethodInfo> methods;
	
	public ClassInfo() {
		this.fields = new LinkedHashMap<String, FieldInfo>();
		this.methods = new LinkedHashMap<String, MethodInfo>();
	}
	
	public void print() {
		System.out.println("CLASS: " + this.name);
		System.out.println("    extends " + this.extendName);
		System.out.println("    FIELDS:");
		for (Map.Entry<String, FieldInfo> entry : this.fields.entrySet()) 
			System.out.println("           " + entry.getKey() + " -> " + entry.getValue().toString());
		System.out.println("    METHODS:");
		for (Map.Entry<String, MethodInfo> entry : this.methods.entrySet()) 
			System.out.println("           " + entry.getKey() + " -> " + entry.getValue().toString());
	}
}
