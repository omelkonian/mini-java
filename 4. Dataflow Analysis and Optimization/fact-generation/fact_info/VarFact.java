package fact_info;

public class VarFact extends Fact {
	public String variable;
	
	public VarFact(String method, String variable) {
		super("var", method);
		this.variable = variable;
	}
}
