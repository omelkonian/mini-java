package symboltable;

import java.util.Map;
import java.util.LinkedHashMap;


public class Scope {
	public Scope parent;
	public String currentClassName;

    public Map<String, VariableEntry> entries; 

    public Scope(Scope parent) {
        this.parent = parent;
        this.currentClassName = (parent != null)?parent.currentClassName:null;
        this.entries = new LinkedHashMap<String, VariableEntry>();
    }

    public void insert(VariableEntry toInsert) {
    	this.entries.put(toInsert.name, toInsert);    	
    }

    public String lookup(String name) {
    	String ret = (this.entries.containsKey(name)) ? this.entries.get(name).type : null;
    	if (ret != null)
    		return ret;
    	else {
    		if (this.parent == null)
    			return null;
    		else
    			return this.parent.lookup(name);
    	}
    }
    
    public void clear() {
    	this.entries.clear();
    }
    
    public void print(int indent) {
    	if (this.parent != null) 
    		this.parent.print(indent);
    	for (VariableEntry entry : entries.values()) 
    		System.out.println(entry.name + " -> " + entry.type);
    }
}
