package ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;


public abstract class Node {
	private char symbol;
	
	public Node(char symbol) {
		this.symbol = symbol;
	}

	public char getSymbol() {
		return symbol;
	}

	public void setSymbol(char symbol) {
		this.symbol = symbol;
	}
	
	public void print(int indent) {
		for (int i = 0; i < indent; i++)
			System.out.print("| ");
		System.out.println(symbol);
	}
}
