package fact_info;

public class VarUseFact extends Fact {
	public int line;
	public String variable;
	
	public VarUseFact(String method, int line, String variable) {
		super("varUse", method);		
		this.line = line;
		this.variable = variable;
	}
}
