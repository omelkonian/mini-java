
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import symboltable.Scope;
import symboltable.VariableEntry;
import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.ArrayType;
import syntaxtree.AssignmentStatement;
import syntaxtree.Block;
import syntaxtree.BooleanType;
import syntaxtree.BracketExpression;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.Clause;
import syntaxtree.CompareExpression;
import syntaxtree.Expression;
import syntaxtree.ExpressionList;
import syntaxtree.ExpressionTail;
import syntaxtree.ExpressionTerm;
import syntaxtree.FalseLiteral;
import syntaxtree.FormalParameter;
import syntaxtree.FormalParameterList;
import syntaxtree.FormalParameterTail;
import syntaxtree.FormalParameterTerm;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MessageSend;
import syntaxtree.MethodDeclaration;
import syntaxtree.MinusExpression;
import syntaxtree.NotExpression;
import syntaxtree.PlusExpression;
import syntaxtree.PrimaryExpression;
import syntaxtree.PrintStatement;
import syntaxtree.Statement;
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.Type;
import syntaxtree.TypeDeclaration;
import syntaxtree.VarDeclaration;
import syntaxtree.WhileStatement;
import visitor.GJDepthFirst;
import classinfo.ClassInfo;
import classinfo.MethodInfo;

public class SecondPassVisitor extends GJDepthFirst<String, Scope> {
	// To hold information about all classes
	private Map<String, ClassInfo> classes;
	
	// To check parameters on message sends (used only temporarily)
	private List<String> tempParameters; 
	
	// For printing line:column number in error messages
	private int lineNumber;
	private int columnNumber;
	
	public SecondPassVisitor(Map<String, ClassInfo> classes) {
		this.classes = classes;
		this.tempParameters = new ArrayList<String>();
		this.lineNumber = 1;
		this.columnNumber = 1;
	}
	
	// Checks if class2 is subclass of class1
	private boolean isCompatible(String class1, String class2) {
		if (class1.compareTo(class2) == 0)
			return true;
		if (!(this.classes.containsKey(class1) && this.classes.containsKey(class2)))
			return false;
		class2 = this.classes.get(class2).extendName;
		while (class2 != null) {
			if (class1.compareTo(class2) == 0)
				return true;
			class2 = this.classes.get(class2).extendName;
		}
		return false;
	}
	
	private String lookupField(Scope scope, String fieldName) {
		// Check scope
		String ret = scope.lookup(fieldName);
		if (ret != null)
			return ret;
		
		// Check super classes
		String className = scope.currentClassName;
		className = this.classes.get(className).extendName;
		while (className != null) {
			ClassInfo superClass = this.classes.get(className);
			if (superClass.fields.containsKey(fieldName)) 
				return superClass.fields.get(fieldName).toString();
			
			className = this.classes.get(className).extendName;
		}
		return null;
	}
	
	private MethodInfo lookupMethod(String className, String methodName) {
		ClassInfo classInfo = this.classes.get(className);
		
		// Check class 	
		if (classInfo.methods.containsKey(methodName))
			return classInfo.methods.get(methodName);
		
		// Check super classes
		String extendName = classInfo.extendName;
		while (extendName != null) {
			classInfo = this.classes.get(extendName);
			if (classInfo.methods.containsKey(methodName))
				return classInfo.methods.get(methodName);
			extendName = classInfo.extendName;
		}
		return null;
	}
	
   /**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
   	public String visit(Goal n, Scope scope) throws Exception {
   		n.f0.accept(this, null);
   		for (int i = 0; i < n.f1.size(); i++) {
   			n.f1.elementAt(i).accept(this,null);
   		}
   		this.classes.clear();
   		return null;
   	} 

  /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "String"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
   	public String visit(MainClass n, Scope scope) throws Exception {
   		Scope main = new Scope(null);
   		main.currentClassName = n.f1.accept(this, null);
   		
   		// Check variable declarations
   		for (int i = 0; i < n.f14.size(); i++)
 		   n.f14.elementAt(i).accept(this, main);

   		// Check statements
   		for (int i = 0; i < n.f15.size(); i++)
   			n.f15.elementAt(i).accept(this, main);
   		
   		main.clear();
   		return null;
   	}

   /**
    * f0 -> ClassDeclaration()
    *       | ClassExtendsDeclaration()
    */
   	public String visit(TypeDeclaration n, Scope scope) throws Exception {
   		Scope typeDeclaration = new Scope(null);
   		n.f0.accept(this, typeDeclaration);
   		typeDeclaration.clear();
   		return null;
   	}

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   public String visit(ClassDeclaration n, Scope scope) throws Exception {
	   // Get class name
	   scope.currentClassName = n.f1.accept(this, null);
	   
	   // Get fields
	   for (int i = 0; i < n.f3.size(); i++)
		   n.f3.elementAt(i).accept(this, scope);

	   // Check methods
	   for (int i = 0; i < n.f4.size(); i++)
		   n.f4.elementAt(i).accept(this, scope);
      
	   return null;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
   public String visit(ClassExtendsDeclaration n, Scope scope) throws Exception {
	   // Get class name
	   scope.currentClassName = n.f1.accept(this, null);
	   
	   // Check fields
	   for (int i = 0; i < n.f5.size(); i++)
		   n.f5.elementAt(i).accept(this, scope);

	   // Check methods
	   for (int i = 0; i < n.f6.size(); i++)
		   n.f6.elementAt(i).accept(this, scope);
	      
	   return null;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Scope scope) throws Exception {
	   	// Check if type is valid
   		String type = n.f0.accept(this, null);
   		if (type.compareTo("INT") != 0 && type.compareTo("INT_ARRAY") != 0 && type.compareTo("BOOLEAN") != 0 && !this.classes.containsKey(type))
   			throw new SemanticException("No such class('" + type + "')", this.lineNumber, this.columnNumber);
   		
   		// Variable uniqueness
   		String varName = n.f1.accept(this, scope);
   		if (scope.entries.containsKey(varName))
   			throw new SemanticException("Variable '" + varName + "' previously declared", this.lineNumber, this.columnNumber);
   		
   		// Add entry to scope
   		scope.insert(new VariableEntry(type, n.f1.accept(this, scope)));
   		
   		return null;
   }

   /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
   	public String visit(MethodDeclaration n, Scope scope) throws Exception {
	   Scope method = new Scope(scope);
	   
	   // Check return type in definition
	   String retType = n.f1.accept(this, null);	   
	   if (retType.compareTo("INT") != 0 && retType.compareTo("INT_ARRAY") != 0 && retType.compareTo("BOOLEAN") != 0 && !this.classes.containsKey(retType))
		   throw new SemanticException("No such class('" + retType + "')", this.lineNumber, this.columnNumber);
	      
	   // Get and check parameters
	   if (n.f4.present()) 
		   n.f4.accept(this, method);
	
	   // Check variable declarations
	   for (int i = 0; i < n.f7.size(); i++)
		   n.f7.elementAt(i).accept(this, method);
	   
	   // Check statements
	   for (int i = 0; i < n.f8.size(); i++)
		   n.f8.elementAt(i).accept(this, method);
	   
	   // Check return type
	   if (!this.isCompatible(retType, n.f10.accept(this, method)))
		   throw new SemanticException("Method must return type '" + retType + "'", this.lineNumber, this.columnNumber);
	   
	   method.clear();
	   return null;
   	}

   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
   public String visit(FormalParameterList n, Scope scope) throws Exception {
      n.f0.accept(this, scope);
      n.f1.accept(this, scope);
      return null;
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public String visit(FormalParameter n, Scope scope) throws Exception {
      String type = n.f0.accept(this, null);
      if (type.compareTo("INT") != 0 && type.compareTo("INT_ARRAY") != 0 && type.compareTo("BOOLEAN") != 0 && !this.classes.containsKey(type))
 			throw new SemanticException("No such class('" + type + "')", this.lineNumber, this.columnNumber);
      
      String name = n.f1.accept(this, null);
      
      scope.insert(new VariableEntry(type, name));
      
      return null;
   }

   /**
    * f0 -> ( FormalParameterTerm() )*
    */
   public String visit(FormalParameterTail n, Scope scope) throws Exception {
      for (int i = 0; i < n.f0.size(); i++)
    	  n.f0.elementAt(i).accept(this, scope);
      return null;
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public String visit(FormalParameterTerm n, Scope scope) throws Exception {
      n.f1.accept(this, scope);
      return null;
   }

   /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   	public String visit(Type n, Scope scope) throws Exception {
    	 return  n.f0.accept(this, null);
   	}

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   	public String visit(ArrayType n, Scope scope) throws Exception {
      	return "INT_ARRAY";
   	}

   /**
    * f0 -> "boolean"
    */
   	public String visit(BooleanType n, Scope scope) throws Exception {
     	 return "BOOLEAN";
   	}

   /**
    * f0 -> "int"
    */
   	public String visit(IntegerType n, Scope scope) throws Exception {
    	return "INT";
   	}

   /**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
   public String visit(Statement n, Scope scope) throws Exception {
      n.f0.accept(this, scope);
      return null;
   }

   /**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
   public String visit(Block n, Scope scope) throws Exception {
      // Check statements
      for (int i = 0; i < n.f1.size(); i++)
    	  n.f1.elementAt(i).accept(this, scope);
      return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public String visit(AssignmentStatement n, Scope scope) throws Exception {
	   // Get left side type
	   String varName = n.f0.accept(this, null);
	   String lType = this.lookupField(scope, varName);
	   if (lType == null)
		   throw new SemanticException("Variable '" + varName + "' has not been declared", this.lineNumber, this.columnNumber);
	   
	   // Get right side type
	   String rType = n.f2.accept(this, scope);
	   
	   // Check types
	   if (!isCompatible(lType, rType))
		   throw new SemanticException("Variable '" + varName + "' must be assigned value of type '" + lType + "'", this.lineNumber, this.columnNumber);
	   
	   return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
   public String visit(ArrayAssignmentStatement n, Scope scope) throws Exception {
	   // Check index type
	   if (n.f2.accept(this, scope).compareTo("INT") != 0)
		   throw new SemanticException("Index must always be value of type INT", this.lineNumber, this.columnNumber);
	   
	   // Check array type
	   String arrayID = n.f0.accept(this, null);
	   if (this.lookupField(scope, arrayID) == null)
		   throw new SemanticException("Variable '" + arrayID + "' has not been declared", this.lineNumber, this.columnNumber);
	   if (this.lookupField(scope, arrayID).compareTo("INT_ARRAY") != 0)
		   throw new SemanticException("Array with id '" + arrayID + " must be INT_ARRAY", this.lineNumber, this.columnNumber);
	   
	   // Check left type
	   if (n.f5.accept(this, scope).compareTo("INT") != 0)
		   throw new SemanticException("Arrays can only hold values of type INT", this.lineNumber, this.columnNumber);
	   
	   return null;
   }

   /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
   public String visit(IfStatement n, Scope scope) throws Exception {
	   // Check condition type
	   if (n.f2.accept(this, scope).compareTo("BOOLEAN") != 0)
		   throw new SemanticException("If conditions must have type BOOLEAN", this.lineNumber, this.columnNumber);
	   
	   // Check if and else statements
	   n.f4.accept(this, scope);
	   n.f6.accept(this, scope);
	   
	   return null;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public String visit(WhileStatement n, Scope scope) throws Exception {
	   // Check condition type
	   if (n.f2.accept(this, scope).compareTo("BOOLEAN") != 0)
		   throw new SemanticException("While conditions must have type BOOLEAN", this.lineNumber, this.columnNumber);
	   
	   // Check inner statement
	   n.f4.accept(this, scope);
	   
	   return null;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public String visit(PrintStatement n, Scope scope) throws Exception {
	   String argType = n.f2.accept(this, scope);
	   if (argType.compareTo("INT") != 0 && argType.compareTo("BOOLEAN") != 0)
		   throw new SemanticException("Print statement only accepts values of type INT or BOOLEAN", this.lineNumber, this.columnNumber);
	   n.f2.accept(this, scope);
	   return null;
   }

   /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | Clause()
    */
   public String visit(Expression n, Scope scope) throws Exception {
	   return n.f0.accept(this, scope);
   }

   /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
   public String visit(AndExpression n, Scope scope) throws Exception {
	   String lType = n.f0.accept(this, scope);
	   String rType = n.f2.accept(this, scope);
	   if (lType.compareTo("BOOLEAN") != 0 || rType.compareTo("BOOLEAN") != 0)
		   throw new SemanticException("Both sides of AND expression must be expressions of type BOOLEAN", this.lineNumber, this.columnNumber);
	   return "BOOLEAN";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public String visit(CompareExpression n, Scope scope) throws Exception {
	   String lType = n.f0.accept(this, scope);
	   String rType = n.f2.accept(this, scope);
	   if (lType.compareTo("INT") != 0 || rType.compareTo("INT") != 0)
		   throw new SemanticException("Both sides of '<' expression must be expressions of type INT", this.lineNumber, this.columnNumber);
	   return "BOOLEAN";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public String visit(PlusExpression n, Scope scope) throws Exception {
	   String lType = n.f0.accept(this, scope);
	   String rType = n.f2.accept(this, scope);  
	   if (lType.compareTo("INT") != 0 || rType.compareTo("INT") != 0)
		   throw new SemanticException("Both sides of '+' expression must be expressions of type INT", this.lineNumber, this.columnNumber);
	   return "INT";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public String visit(MinusExpression n, Scope scope) throws Exception {
	   String lType = n.f0.accept(this, scope);
	   String rType = n.f2.accept(this, scope);	  
	   if (lType.compareTo("INT") != 0 || rType.compareTo("INT") != 0)
		   throw new SemanticException("Both sides of '-' expression must be expressions of type INT", this.lineNumber, this.columnNumber);
	   return "INT";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public String visit(TimesExpression n, Scope scope) throws Exception {
	   String lType = n.f0.accept(this, scope);
	   String rType = n.f2.accept(this, scope);
	   if (lType.compareTo("INT") != 0 || rType.compareTo("INT") != 0)
		   throw new SemanticException("Both sides of '*' expression must be expressions of type INT", this.lineNumber, this.columnNumber);
	   return "INT";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public String visit(ArrayLookup n, Scope scope) throws Exception {
      String arrayType = n.f0.accept(this, scope);
      if (arrayType.compareTo("INT_ARRAY") != 0)
    	  throw new SemanticException("Arrays can only hold values of type INT", this.lineNumber, this.columnNumber);
      
      String indexType = n.f2.accept(this, scope);
      if (indexType.compareTo("INT") != 0)
    	  throw new SemanticException("Array indexes can only be expressions of type INT", this.lineNumber, this.columnNumber);      
      
      return "INT";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public String visit(ArrayLength n, Scope scope) throws Exception {
      String arrayType = n.f0.accept(this, scope);
      if (arrayType.compareTo("INT_ARRAY") != 0)
    	  throw new SemanticException("Getting length attribute of an expression not of type INT_ARRAY", this.lineNumber, this.columnNumber);      
      return "INT";
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   public String visit(MessageSend n, Scope scope) throws Exception {
	   // Check class
	   String className = n.f0.accept(this, scope);
	   if (!this.classes.containsKey(className))
		   className = this.lookupField(scope, className);
	   if (className == null || !this.classes.containsKey(className))
		   throw new SemanticException("Trying to send message to non-existent class", this.lineNumber, this.columnNumber);
	   
	   // Check method
	   String method = n.f2.accept(this, null);
	   MethodInfo methodInfo = this.lookupMethod(className, method);
	   if (methodInfo == null)
		   throw new SemanticException("Class '" + className + "' does not have a method called '" + method + "'", this.lineNumber, this.columnNumber);
      
	   // Check parameters
	   if (n.f4.present()) {
		   // Get parameters from ExpressionList		   
		   n.f4.accept(this, scope);
		   
		   // Cross-check
		   boolean parametersAgree = true;
		   
		   if (methodInfo.parameters.size() != this.tempParameters.size())
			   parametersAgree = false;
		   for (int i = 0; i < this.tempParameters.size(); i++) {
			   if (!isCompatible(methodInfo.parameters.get(i).toString(), this.tempParameters.get(i)))
				   parametersAgree = false;
		   }
		   
		   if (!parametersAgree)
			   throw new SemanticException("Method '" + className + "." + method + "' is called with wrong arguments", this.lineNumber, this.columnNumber);
		   
		   this.tempParameters.clear();
	   }
	   
	   // Get return type
	   return methodInfo.returnType.toString();	   
   }

   /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
   public String visit(ExpressionList n, Scope scope) throws Exception {
	   	this.tempParameters.add(n.f0.accept(this, scope));
      	n.f1.accept(this, scope);
      	return null;
   }

   /**
    * f0 -> ( ExpressionTerm() )*
    */																		
   public String visit(ExpressionTail n, Scope scope) throws Exception {
	   	for (int i = 0; i < n.f0.size(); i++) 
	   		this.tempParameters.add(n.f0.elementAt(i).accept(this, scope));
	   	return null;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public String visit(ExpressionTerm n, Scope scope) throws Exception {
	   	return n.f1.accept(this, scope);
   }

   /**
    * f0 -> NotExpression()
    *       | PrimaryExpression()
    */
   public String visit(Clause n, Scope scope) throws Exception {
	   return n.f0.accept(this, scope);	
   }

   /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | BracketExpression()
    */
   public String visit(PrimaryExpression n, Scope scope) throws Exception {
	   String var = n.f0.accept(this, scope);
	   String type = "";
	   if (n.f0.which == 3) { // IDENTIFIER
		   // Check if variable has been declared
		   type = this.lookupField(scope, var);
		   if (type == null)
			   throw new SemanticException("Variable '" + var + "' has not been declared", this.lineNumber, this.columnNumber);
		   var = type;
	   }
	   return var;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, Scope scope) throws Exception {
	   this.lineNumber = n.f0.beginLine;
	   this.columnNumber = n.f0.beginColumn;
	   return "INT";
   }

   /**
    * f0 -> "true"
    */
   public String visit(TrueLiteral n, Scope scope) throws Exception {
	   this.lineNumber = n.f0.beginLine;
	   this.columnNumber = n.f0.beginColumn;
	   return "BOOLEAN";
   }

   /**
    * f0 -> "false"
    */
   public String visit(FalseLiteral n, Scope scope) throws Exception {
	   this.lineNumber = n.f0.beginLine;
	   this.columnNumber = n.f0.beginColumn;
	   return "BOOLEAN";
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   	public String visit(Identifier n, Scope scope) throws Exception {
   		this.lineNumber = n.f0.beginLine;
 	   	this.columnNumber = n.f0.beginColumn;
 	   	return n.f0.toString();
   	}

   /**
    * f0 -> "this"
    */
   public String visit(ThisExpression n, Scope scope) throws Exception {  
	   	this.lineNumber = n.f0.beginLine;
	   	this.columnNumber = n.f0.beginColumn;
	   	return scope.currentClassName;
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public String visit(ArrayAllocationExpression n, Scope scope) throws Exception {
	   String indexType = n.f3.accept(this, scope);
	   if (indexType.compareTo("INT") != 0)
		   throw new SemanticException("Array size must be expression of type INT", this.lineNumber, this.columnNumber);
	   return "INT_ARRAY";
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public String visit(AllocationExpression n, Scope scope) throws Exception {
	   String className = n.f1.accept(this, scope);
	   if (!this.classes.containsKey(className))
		   throw new SemanticException("Trying to instantiate object of non-existent class", this.lineNumber, this.columnNumber);
	   return className;
   }

   /**
    * f0 -> "!"
    * f1 -> Clause()
    */
   public String visit(NotExpression n, Scope scope) throws Exception {
	   String clauseType = n.f1.accept(this, scope);
	   if (clauseType.compareTo("BOOLEAN") != 0)
		   throw new SemanticException("Operand of '!' expression must be expression of type BOOLEAN", this.lineNumber, this.columnNumber);
	   return "BOOLEAN";
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public String visit(BracketExpression n, Scope scope) throws Exception {
	   return n.f1.accept(this, scope);
   }
}
