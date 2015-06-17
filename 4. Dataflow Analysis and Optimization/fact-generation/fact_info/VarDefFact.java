package fact_info;

public class VarDefFact extends Fact {
	public int line;
	public String variable;
	
	public VarDefFact(String method, int line, String variable) {
		super("varDef", method);		
		this.line = line;
		this.variable = variable;
	}
}
