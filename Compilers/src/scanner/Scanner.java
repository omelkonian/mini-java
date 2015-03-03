package scanner;

public class Scanner {
	private String command;

	private int currentPos;

	public Scanner(String command) {
		this.command = command;
		this.currentPos = 0;
	}

	public Token next() {
		if (currentPos == command.length())
			return new Token('\n');
		while (this.command.charAt(currentPos) == 32)
			currentPos++;
		return new Token(this.command.charAt(currentPos++));
	}
}
