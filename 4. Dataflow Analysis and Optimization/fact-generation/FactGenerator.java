import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;

import syntaxtree.*;
import visitor.GJDepthFirst;
import fact_info.*;


/**
* Generates all facts need for dataflow analysis.
*/
public class FactGenerator extends GJDepthFirst<String, String> {
	
	public Set<Fact> facts;
	public Map<String, Integer> labelPositions;
	public Map<String, List<Integer>> labelMap;
	public int currentLine;
	public boolean toUse;
	public boolean hasPrevious;
	public String jumpLabel;
	
    /**
    * Constructor
    */
    public FactGenerator() {
		this.facts = new HashSet<Fact>();
		this.labelPositions = new HashMap<String, Integer>();
		this.labelMap = new HashMap<String, List<Integer>>();
		this.toUse = false;
		this.hasPrevious = false;
    }

    /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
   public String visit(Goal n, String methodName) throws Exception {
      String _ret=null;
      
      this.currentLine = 1;
      n.f1.accept(this, "MAIN");
      
      for (int i = 0; i < n.f3.size(); i++)
    	  n.f3.elementAt(i).accept(this, null);
      
      return _ret;
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public String visit(StmtList n, String methodName) throws Exception {
	   this.hasPrevious = false;
	   for (int i = 0; i < n.f0.size(); i++) {
		   if (this.hasPrevious)
			   this.facts.add(new NextFact(methodName, this.currentLine - 1, this.currentLine));
		   else 
			   this.hasPrevious = true;
		   
		   NodeSequence sequence = (NodeSequence) n.f0.elementAt(i); 		   		   
		   String label = sequence.elementAt(0).accept(this, methodName);
		   String statement = sequence.elementAt(1).accept(this, methodName);
		   
		   if (statement.substring(0, 4).equals("JUMP")) {			   
			   this.hasPrevious = false;			   
			   String jumpLabel = statement.substring(5, statement.length());
			   
			   // Update labelMap
			   if (this.labelMap.containsKey(jumpLabel))
				   this.labelMap.get(jumpLabel).add(this.currentLine);
			   else {
				   List<Integer> newList = new ArrayList<Integer>();
				   newList.add(this.currentLine);
				   this.labelMap.put(jumpLabel, newList);
			   }			   
		   }
		   		   
		   if (label != null)
			   this.labelPositions.put(label, this.currentLine);
		   String instruction = (label != null)?(label + "     "):"";		   
		   instruction += statement;
		   		   		  
		   // Instruction fact
		   this.facts.add(new InstructionFact(methodName, this.currentLine, instruction));
		   		   		  
		   this.currentLine++;
	   }
	   
	   // Add next facts from jumps
	   for (Map.Entry<String, Integer> entry : this.labelPositions.entrySet())	{
		   String label = entry.getKey();
		   int line = entry.getValue();	 
		   
		   if (this.labelMap.containsKey(label))
		   for (int jumpLine : this.labelMap.get(label))
			   this.facts.add(new NextFact(methodName, jumpLine, line));
	   }
	   		 
	   this.labelMap.clear();
	   this.labelPositions.clear();
	   
	   return null;
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
   public String visit(Procedure n, String methodName) throws Exception {      
      this.currentLine = 1;
      String method = n.f0.accept(this, null);      
      int argumentsNo = Integer.parseInt(n.f2.accept(this, null));
      
      // Var facts
      for (int i = 0; i < argumentsNo; i++)
    	  this.facts.add(new VarFact(method, "TEMP " + Integer.toString(i)));
      
      n.f4.accept(this, method);
      
      return null;
   }

   /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    */
   public String visit(Stmt n, String methodName) throws Exception {	   	   	   
	   	return n.f0.accept(this, methodName);
   }

   /**
    * f0 -> "NOOP"
    */
   public String visit(NoOpStmt n, String methodName) throws Exception {
      return "NOOP";
   }

   /**
    * f0 -> "ERROR"
    */
   public String visit(ErrorStmt n, String methodName) throws Exception {
      return "ERROR";
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public String visit(CJumpStmt n, String methodName) throws Exception {
	   this.toUse = true;
	   String temp = n.f1.accept(this, methodName);
	   this.toUse = false;
	   String label = n.f2.accept(this, methodName);      
	   
	   // Update labelMap
	   if (this.labelMap.containsKey(label))
		   this.labelMap.get(label).add(this.currentLine);
	   else {
		   List<Integer> newList = new ArrayList<Integer>();
		   newList.add(this.currentLine);
		   this.labelMap.put(label, newList);
	   }
      
	   return "CJUMP " + temp + " " + label;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public String visit(JumpStmt n, String methodName) throws Exception {
	   String label = n.f1.accept(this, methodName);

	   // Update labelMap
	   if (this.labelMap.containsKey(label))
		   this.labelMap.get(label).add(this.currentLine);
	   else {
		   List<Integer> newList = new ArrayList<Integer>();
		   newList.add(this.currentLine);
		   this.labelMap.put(label, newList);
	   }
	   
	   return "JUMP " + label;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
   public String visit(HStoreStmt n, String methodName) throws Exception {
	   this.toUse = true;
	   String temp1 = n.f1.accept(this, methodName);
	   String literal = n.f2.accept(this, methodName);
	   String temp2 = n.f3.accept(this, methodName);
	   this.toUse = false;
	   
	   return "HSTORE " + temp1 + " " + literal + " " + temp2;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
   public String visit(HLoadStmt n, String methodName) throws Exception {
	   String temp1 = n.f1.accept(this, methodName);
	   
	   // VarDef fact
	   this.facts.add(new VarDefFact(methodName, this.currentLine, temp1));
	   
	   this.toUse = true;
	   String temp2 = n.f2.accept(this, methodName);
	   this.toUse = false;
	   String literal = n.f3.accept(this, methodName);	   	   	   
	   
	   return "HLOAD " + temp1 + " " + temp2 + " " + literal;
   }

   public static boolean isInteger(String expression) {
	   for (int i = 0; i < expression.length(); i++)
		   if (!Character.isDigit(expression.charAt(i)))
			   return false;
	   return true;
   }
   public static boolean isLabel(String expression) {
	   	if (FactGenerator.isInteger(expression))
	   		return false;
	   	for (int i = 0; i < expression.length(); i++)
	   		if (Character.isWhitespace(expression.charAt(i)))
	   			return false;
        return true;
   }
   
   public static boolean isConstant(String expression) {
		   return ((FactGenerator.isInteger(expression) || FactGenerator.isLabel(expression)) ? true : false);
   }
   
   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public String visit(MoveStmt n, String methodName) throws Exception {
	   String temp = n.f1.accept(this, methodName);
	   this.toUse = true;
	   String expression = n.f2.accept(this, methodName);
	   this.toUse = false;
	   // VarDef fact
	   this.facts.add(new VarDefFact(methodName, this.currentLine, temp));
	   
	   // Check if expr is variable or constant
	   if (FactGenerator.isConstant(expression)) 
		   this.facts.add(new ConstMoveFact(methodName, this.currentLine, temp, expression)); // ConstMove fact	  
	   else if (expression.length() > 4 && expression.substring(0, 4).equals("TEMP")) 
		   this.facts.add(new VarMoveFact(methodName, this.currentLine, temp, expression)); // VarMove fact
	   
	   return "MOVE " + temp + " " + expression; 
   } 

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public String visit(PrintStmt n, String methodName) throws Exception {
	   this.toUse = true;
	   String simpleExpr = n.f1.accept(this, methodName);
	   this.toUse = false;
	   return "PRINT " + simpleExpr;
   }

   /**
    * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   public String visit(Exp n, String methodName) throws Exception {
      return n.f0.accept(this, methodName);
   }

   /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
   public String visit(StmtExp n, String methodName) throws Exception {      
	   	n.f1.accept(this, methodName);
	   	
	   	// Next fact
	   	this.facts.add(new NextFact(methodName, this.currentLine - 1, this.currentLine));
	   	
	   	// Instruction fact
	   	String expr = n.f3.accept(this, methodName);
	   	this.facts.add(new InstructionFact(methodName, this.currentLine, "RETURN " + expr));
	   	// VarUse fact
      if (expr.length() > 4 && expr.substring(0, 4).equals("TEMP"))
        this.facts.add(new VarUseFact(methodName, this.currentLine, expr));

	   	return null;
   }

   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
   public String visit(Call n, String methodName) throws Exception {
	   String ret = "CALL ";
	   this.toUse = true;
	   ret += n.f1.accept(this, methodName) + "( ";	   	   
	   for (int i = 0; i < n.f3.size(); i++)
		   ret += n.f3.elementAt(i).accept(this, methodName) + " ";
	   this.toUse = false;
	   ret += ")";
	   	   
	   return ret;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public String visit(HAllocate n, String methodName) throws Exception {     
	   return "HALLOCATE " + n.f1.accept(this, methodName);
   }

   /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
   public String visit(BinOp n, String methodName) throws Exception { 
	   	String temp = n.f1.accept(this, methodName);
	   	
	   	this.toUse = true;
	   	String expr = n.f2.accept(this, methodName);
	   	this.toUse = false;
	   		   	
	   	// VarUse fact
	   	this.facts.add(new VarUseFact(methodName, this.currentLine, temp));
	   				   
	   	return n.f0.accept(this, methodName) + " " + temp + " " + expr;
   }

   /**
    * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
   public String visit(Operator n, String methodName) throws Exception {
	   return n.f0.choice.toString();
   }

   /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public String visit(SimpleExp n, String methodName) throws Exception {	   	  
	   	return n.f0.accept(this, methodName);
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public String visit(Temp n, String methodName) throws Exception {
	   String variable = "TEMP " + n.f1.accept(this, methodName);;
	   
	   // Var fact
	   this.facts.add(new VarFact(methodName, variable));
	   
	   if (this.toUse)		   
		   this.facts.add(new VarUseFact(methodName, this.currentLine, variable)); // VarUse fact
      
	   return variable;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, String methodName) throws Exception {
	   return n.f0.toString();	
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public String visit(Label n, String methodName) throws Exception {
	   return n.f0.toString();
   }
    
}
