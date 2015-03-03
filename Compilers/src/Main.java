import parser.Parser;
import ast.AbstractSyntaxTree;
import ast.DigitNode;
import ast.Node;
import ast.OperatorNode;

public class Main {
	public static void main(String[] args) {
		Parser parser = new Parser();
		String expression = new String("((1 + 2 + 3) + (5 * 4 / 3))"); 
		System.out.print("Syntax is " + ((parser.parse(expression)) ? "correct.":"wrong."));
		
//		AbstractSyntaxTree ast = new AbstractSyntaxTree();
//		OperatorNode root = new OperatorNode('+');
//		
//		OperatorNode div = new OperatorNode('/');
//		DigitNode n0 = new DigitNode('8');
//		DigitNode n1 = new DigitNode('2');
//		div.addChildren(n0, n1);
//		
//		OperatorNode mul = new OperatorNode('*');
//		DigitNode n2 = new DigitNode('2');
//		DigitNode n3 = new DigitNode('3');
//		mul.addChildren(n2, n3);
//		
//		root.addChildren(div, mul);
//		
//		ast.setRoot(root);
//		ast.print();
//		
//		System.out.println(ast.getPrefixNotation());
	}
}
