package scanner;

public class Token {
	private TokenType type;
	private char symbol;
	
	public Token(char symbol) {
		this.symbol = symbol;
		switch (symbol) {
		case '(':
			this.type = TokenType.LEFT_PAR;
			break;
		case ')':
			this.type = TokenType.RIGHT_PAR;
			break;
		case '+':
		case '-':
		case '*':
		case '/':
			this.type = TokenType.OPERATOR;
			break;
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			this.type = TokenType.DIGIT;
			break;
		case '\n':
			this.type = TokenType.FINISH;
			break;
		default:
			this.type = TokenType.UNKNOWN;
			break;
		}
	}
	
	public void print() {
		System.out.println(type.toString() + " -> " + symbol);
	}

	public TokenType getTokenType() {
		return type;
	}

	public void setTokenType(TokenType tokenType) {
		this.type = tokenType;
	}

	public char getSymbol() {
		return symbol;
	}

	public void setSymbol(char symbol) {
		this.symbol = symbol;
	}
	
	
}
