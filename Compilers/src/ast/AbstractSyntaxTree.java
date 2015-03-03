package ast;

public class AbstractSyntaxTree {
	private Node root;

	StringBuilder expression;
	int insertPos;

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}
	
	public String getPrefixNotation() {
		if (root instanceof DigitNode)
			return Character.toString(root.getSymbol());
		
		expression = new StringBuilder("");
		insertPos = 0;
		this.constructString(root);
		
		return expression.toString();
	}
	
	private void constructString(Node cur) {
		if (cur instanceof DigitNode) {
			expression.insert(insertPos++, cur.getSymbol());
			System.out.println(expression.toString());
			return;
		}
		OperatorNode current = (OperatorNode) cur;
		expression.insert(insertPos, "(" + current.getSymbol() + " )");
		insertPos += 3;
		 
		this.constructString(current.getFirstChild());
		expression.insert(insertPos++, ' ');
		this.constructString(current.getSecondChild());
		insertPos++;
	}
	
	public void print() {
		root.print(0);
	}
}
