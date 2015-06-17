package parser;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		Parser parser = new Parser();
		try {
			parser.parse(System.in);
		} catch (ParseError err) {
			System.err.println(err.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}	
}
