package parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ---------- ---GRAMMAR---------------
 * 
 * 1. goal -> expr
 *  
 * 2. expr  -> term expr2 
 * 3. expr2 -> '+' term expr2 
 * 4. 		|  '-' term expr2 
 * 5. 		|  ε
 *  
 * 6. term  -> factor term2 
 * 7. term2 -> '*' factor term2 
 * 8. 		|  '/' factor term2 
 * 9. 		|  ε 
 * 
 * 10. factor -> '0..9' 
 * 11. 		  |  (expr)
 * 
 */
public class Parser {
	private int lookaheadToken;
	private InputStream in;
	private Deque<StringBuilder> stack;

	public void parse(InputStream in) throws IOException, ParseError {
		this.in = in;
		this.stack = new ArrayDeque<StringBuilder>();
		lookaheadToken = consume();
		goal();
	}
	
	private int consume() throws IOException {
		int ret = in.read();
		while (ret == ' ')
			ret = in.read();
		return ret;
	}

	private void goal() throws IOException, ParseError {
		// goal -> expr
		expr();	
		if (lookaheadToken != '\n' && lookaheadToken != -1)
			throw new ParseError();
		System.out.println("-> " + stack.pop().toString());
	}

	private void expr() throws IOException, ParseError {
		// expr -> term expr2
		term();
		expr2();
		StringBuilder expr2 = stack.pop();
		StringBuilder term = stack.pop();
		if (expr2.toString().equals("")) {
			stack.push(term); 
			System.out.println("ToPush: " + term);
		}
		else {
			StringBuilder toPush = expr2.insert(getInsertPosition(expr2), term + " ");
			stack.push(toPush);
			System.out.println("ToPush: " + toPush);
		}
	}

	private void expr2() throws IOException, ParseError {
		// expr2 -> + term expr2
		if (lookaheadToken == '+') {
			lookaheadToken = consume();
			term();
			expr2();
			StringBuilder expr2 = stack.pop();
			StringBuilder term = stack.pop();
			if (expr2.toString().equals("")) {
				StringBuilder toPush = new StringBuilder("(+ " + term + ")");
				stack.push(toPush);
				System.out.println("ToPush: " + toPush);
			} else {
				StringBuilder toPush = expr2.insert(getInsertPosition(expr2), "(+ " + term + ") ");
				stack.push(toPush);
				System.out.println("ToPush: " + toPush);
			}
		}
		// expr2 -> - term expr2
		else if (lookaheadToken == '-') {
			lookaheadToken = consume();
			term();
			expr2();
			StringBuilder expr2 = stack.pop();
			StringBuilder term = stack.pop();
			if (expr2.toString().equals("")) {
				StringBuilder toPush = new StringBuilder("(- " + term + ")");
				stack.push(toPush);
				System.out.println("ToPush: " + toPush);
			} else {
				StringBuilder toPush = expr2.insert(getInsertPosition(expr2), "(- " + term + ") ");
				stack.push(toPush);
				System.out.println("ToPush: " + toPush);
			}
		}
		// expr2 -> ε
		else if (lookaheadToken == ')' || lookaheadToken == '\n') 
			stack.push(new StringBuilder(""));
		else 
			throw new ParseError();
	}

	private void term() throws IOException, ParseError {
		factor();
		term2();
		StringBuilder term2 = stack.pop();
		StringBuilder factor = stack.pop();
		if (term2.toString().equals("")) {
			stack.push(factor);
			System.out.println("ToPush: " + factor);
		}
		else {
			StringBuilder toPush = term2.insert(getInsertPosition(term2), factor + " ");
			stack.push(toPush);
			System.out.println("ToPush: " + toPush);
		}
	}

	private void term2() throws IOException, ParseError {
		// term2 -> * factor term2
		if (lookaheadToken == '*') {
			lookaheadToken = consume();
			factor();
			term2();
			StringBuilder term2 = stack.pop();
			StringBuilder factor = stack.pop();
			if (term2.toString().equals("")) {
				StringBuilder toPush = new StringBuilder("(* " + factor + ")");
				stack.push(toPush);
				System.out.println("ToPush: " + toPush);
			} else {
				StringBuilder toPush = term2.insert(getInsertPosition(term2), "(* " + factor + ") ");
				stack.push(toPush);
				System.out.println("ToPush: " + toPush);
			}
		}
		// term2 -> / factor term2
		else if (lookaheadToken == '/') {
			lookaheadToken = consume();
			factor();
			term2();
			StringBuilder term2 = stack.pop();
			StringBuilder factor = stack.pop();
			if (term2.toString().equals("")) {
				StringBuilder toPush = new StringBuilder("(/ " + factor + ")");
				stack.push(toPush);
				System.out.println("ToPush: " + toPush);
			} else {
				StringBuilder toPush = term2.insert(getInsertPosition(term2), "(/ " + factor + ") ");
				stack.push(toPush);
				System.out.println("ToPush: " + toPush);
			}
		}
		// term2 -> ε
		else if (lookaheadToken == '+' || lookaheadToken == '-'
			  || lookaheadToken == ')' || lookaheadToken == '\n') {
			stack.push(new StringBuilder(""));
		} else
			throw new ParseError();
	}

	private void factor() throws IOException, ParseError {
		// factor -> '0..9'
		if (lookaheadToken > '0' && lookaheadToken < '9') {
			StringBuilder toPush = new StringBuilder(Character.toString((char) lookaheadToken));
			stack.push(toPush);
			System.out.println("ToPush: " + toPush);
			lookaheadToken = consume();
		}
		// factor -> (expr)
		else if (lookaheadToken == '(') {
			lookaheadToken = consume();
			expr();
			if (lookaheadToken == ')')
				lookaheadToken = consume();
			else
				throw new ParseError();
		} else
			throw new ParseError();
	}

	private int getInsertPosition(StringBuilder string) {
		for (int i = 0; i < string.length(); i += 2) {	
			if (string.charAt(i) == '(') {			
				int j = i + 3;
				// First operand
				if (string.charAt(j) == '(') {
					int openPars = 1;
					j++;
					while (openPars != 0) {						
						if (string.charAt(j) == ')') openPars--;
						else if (string.charAt(j) == '(') openPars++;
						j++;
					}
					j--;
				}
				// Second operand
				if (string.charAt(j + 1) == ')')
					return i + 3;
			}
		}
		return -1;
	}
}
