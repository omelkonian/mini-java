package parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ----------	---GRAMMAR---------------
 * 
 * 	1. 	goal   ->	expr
 *	2. 	expr   -> 	term expr2	
 *	3. 	expr2  ->	'+' term expr2
 *	4.		   |	'-' term expr2
 *	5.		   |    ε
 *	6.	term   ->	factor term2
 *	7.	term2  ->	'*' factor term2
 *	8.		   |	'/' factor term2    
 *	9.		   |    ε
 * 	10.	factor ->	'0..9'  
 * 	11. 	   |	( expr )
 * 
 */
public class Parser {
	private int lookaheadToken;
	private InputStream in;
	private Deque<StringBuilder> stack;
	
	public boolean parse(InputStream in) throws IOException {
		this.in = in;
		this.stack = new ArrayDeque<StringBuilder>();
		lookaheadToken = in.read();
		return goal();
	}
	
	private boolean goal() throws IOException {
		// goal -> expr
		if (expr()) {
			
			return true;
		}
		return false;
	}
	
	private boolean expr() throws IOException {
		System.out.println("expr");
		// expr -> term expr2
		if (term() && expr2()) {
			StringBuilder expr2 = stack.pop();
			StringBuilder term = stack.pop();
			//...
			return true;
		}
		return false;
	}
	
	private boolean expr2() throws IOException {
		System.out.println("expr2");
		// expr2 -> + term expr2
		if (lookaheadToken == '+') {
			lookaheadToken = in.read();
			if (term() && expr2()) {
				StringBuilder expr2 = stack.pop();
				StringBuilder term = stack.pop();
				return true;
			}
			else
				return false; //ERROR
		}
		// expr2 -> - term expr2
		else if (lookaheadToken == '-') {
			lookaheadToken = in.read();
			if (term() && expr2()) {
				StringBuilder expr22 = stack.pop();
				StringBuilder term = stack.pop();
				//...
				return true;
			}
			else
				return false; //ERROR			
		}
		// expr2 -> ε
		else if (lookaheadToken == ')' || lookaheadToken == '\n') {
			stack.push(new StringBuilder(""));
			return true;	
		}
		// ERROR
		else {
			return false;
		}
	}
	
	private boolean term() throws IOException {
		System.out.println("term");
		if (factor() && term2()) {
			StringBuilder term2 = stack.pop();
			StringBuilder factor = stack.pop();
			if (term2.equals(""))
				stack.push(factor);
			else
				//...
			return true;
		}
		return false;
	}
	
	private boolean term2() throws IOException {
		System.out.println("term2");
		// term2 -> * factor term2
		if (lookaheadToken == '*') {
			lookaheadToken = in.read();
			if (factor() && term2()) {
				StringBuilder term2 = stack.pop();
				StringBuilder factor = stack.pop();
				//..
				return true;
			}
			else 
				return false; //ERROR
		}
		// term2 -> / factor term2
		else if (lookaheadToken == '/') {
			lookaheadToken = in.read();
			if (factor() && term2()) {
				StringBuilder term2 = stack.pop();
				StringBuilder factor = stack.pop();
				if (term2.equals("")) 
					stack.push(factor.insert(0, "/ "));
				else {
					char operator = term2.charAt(0);
					
				}
				return true;
			}
			else 
				return false; //ERROR			
		}
		// term2 -> ε
		else if (lookaheadToken == '+' || lookaheadToken == '-' ||
				 lookaheadToken == ')' || lookaheadToken == '\n') { 
			stack.push(new StringBuilder(""));
			return true;
		}
		// ERROR
		else {
			return false;
		}		
	}
	
	private boolean factor() throws IOException {
		System.out.println("factor");
		// factor -> '0..9'
		if (lookaheadToken > '0' && lookaheadToken < '9') { 
			lookaheadToken = in.read();
			stack.push(new StringBuilder(Integer.toString(lookaheadToken)));
			return true;
		}
		// factor -> (expr)
		else if (lookaheadToken == '(') {
			lookaheadToken = in.read();
			if (expr() && lookaheadToken == ')')  {
				//...
				return true;
			}
			else 
				return false; //ERROR
		}
		// ERROR
		else {
			return false;
		}		
	}
}
