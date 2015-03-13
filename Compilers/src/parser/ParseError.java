package parser;

@SuppressWarnings("serial")
public class ParseError extends Exception {
	public String getMessage() {
		return "parse error";
    }
}