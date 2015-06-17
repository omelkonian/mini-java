package fact_info;

public class VarMoveFact extends Fact {
	public int line;
	public String to;
	public String from;
	
	public VarMoveFact(String method, int line, String to, String from) {
		super("varMove", method);		
		this.line = line;
		this.to = to;
		this.from = from;
	}
}
