package fact_info;

public class ConstMoveFact extends Fact {
	public int line;
	public String to;
	public String from;
	
	public ConstMoveFact(String method, int line, String to, String from) {
		super("constMove", method);		
		this.line = line;
		this.to = to;
		this.from = from;
	}
}
