package fact_info;

public class NextFact extends Fact {
	public int from;
	public int to;
	
	public NextFact(String method, int from, int to) {
		super("next", method);
		this.from = from;
		this.to = to;
	}
}
