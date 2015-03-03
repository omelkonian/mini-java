package parser;

import java.util.LinkedList;
import java.util.Queue;

import scanner.Scanner;
import scanner.Token;
import scanner.TokenType;

/**
 * -------------GRAMMAR---------------
 * 	exp := num | (exp op exp)
 *	
 * 	num := 0 | 1 | ... | 9
 * 	
 * 	op := + | - | * | /
 */
public class Parser {
	private Scanner scanner;
	private Queue<Token> tokens;

	public boolean parse(String command) {
		scanner = new Scanner(command);
		tokens = new LinkedList<Token>();	

		return true;
	}
	
	private boolean terminal(TokenType type) {
		if (tokens.isEmpty())
			return true;
		return false;
	}
	
	private boolean exp() {
		return exp1() || exp2();
	}
	
	private boolean exp1() {
		return number();
	}
	
	private boolean exp2() {
		return terminal(TokenType.LEFT_PAR) && exp() && operator() && exp() && terminal(TokenType.RIGHT_PAR); 
	}
	
	private boolean number() {
		return terminal(TokenType.DIGIT);
	}
	
	private boolean operator() {
		return terminal(TokenType.OPERATOR);
	}
}
