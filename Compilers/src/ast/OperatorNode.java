package ast;

import java.util.Vector;

public class OperatorNode extends Node {
	private Node[] children;
	
	public OperatorNode(char symbol) {
		super(symbol);
		children = new Node[2];
	}

	public Node getFirstChild() {
		return children[0];
	}
	
	public Node getSecondChild() {
		return children[1];
	}

	public void addChildren(Node child1, Node child2) {
		children[0] = child1;
		children[1] = child2;
	}
	
	@Override
	public void print(int indent) {
		super.print(indent);
		for (Node n : children)
			n.print(indent + 1);
	}
}
