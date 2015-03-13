package parser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
	public static void main(String[] args) throws IOException {
//		String expression = "1 /   (    ( 1/   2)   / ( 3 /4 ) )    \n";
		String expression = "1+3*5+7/2\n";
//		String expression = "3-2-1\n";
		System.out.println(expression);
		Parser parser = new Parser();
		try {
			parser.parse(new ByteArrayInputStream(expression.getBytes(StandardCharsets.UTF_8)));
//			parser.parse(System.in);
		} catch (ParseError err) {
			System.err.println(err.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}	
}
