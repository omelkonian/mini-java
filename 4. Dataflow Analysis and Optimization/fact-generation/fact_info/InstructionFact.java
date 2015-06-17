package fact_info;

public class InstructionFact extends Fact {
	public int line;
	public String instruction;
	
	public InstructionFact(String method, int line, String instruction) {
		super("instruction", method);
		this.line = line;
		this.instruction = instruction;
	}
}
