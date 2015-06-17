
public class SemanticException extends Exception {

	private static final long serialVersionUID = 1L;

	public SemanticException(String message, int lineNumber, int columnNumber) {
		super((char)27 + "[31mSemantic Error " + (char)27 + "[0m \n" + message + " [" + lineNumber + ":" + columnNumber + "]\n");
		
	}
}
