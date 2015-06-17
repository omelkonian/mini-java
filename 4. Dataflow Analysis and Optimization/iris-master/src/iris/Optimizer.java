package iris;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.deri.iris.Configuration;
import org.deri.iris.KnowledgeBase;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.evaluation.topdown.FirstLiteralSelector;
import org.deri.iris.optimisations.magicsets.MagicSets;
import org.deri.iris.storage.IRelation;


public class Optimizer {
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: java Optimizer <SpigletFilename>");
			System.exit(-1);
		}

		Parser parser = new Parser();

		final String factsPath = "generated-facts/" + args[0];
		final String rulePath = "analysis-logic/";
		final String queriesPath = "queries_opt.iris";

		Map<IPredicate, IRelation> factMap = new HashMap<>();

		/**
		 * The following loop -- given a project directory -- will list and read
		 * parse all fact files in its "/facts" subdirectory. This allows you to
		 * have multiple .iris files with your program facts. For instance you
		 * can have one file for each relation's facts as our examples show.
		 */
		final File factsDirectory = new File(factsPath);
		if (factsDirectory.isDirectory()) {
			for (final File fileEntry : factsDirectory.listFiles()) {

				if (fileEntry.isDirectory())
					System.out.println("Omitting directory " + fileEntry.getPath());

				else {
					Reader factsReader = new FileReader(fileEntry);
					parser.parse(factsReader);

					// Retrieve the facts and put all of them in factMap
					factMap.putAll(parser.getFacts());
				}
			}
		} else {
			System.err.println("Invalid facts directory path");
			System.exit(-1);
		}

		File rulesFile = new File(rulePath + "DeadCodeComputation.iris");
		Reader rulesReader = new FileReader(rulesFile);
		parser.parse(rulesReader);
		List<IRule> rules = parser.getRules();
		
		rulesFile = new File(rulePath + "ConstantPropagation.iris");
		rulesReader = new FileReader(rulesFile);
		parser.parse(rulesReader);
		rules.addAll(parser.getRules());		

		rulesFile = new File(rulePath + "CopyPropagation.iris");
		rulesReader = new FileReader(rulesFile);
		parser.parse(rulesReader);
		rules.addAll(parser.getRules());				
		
		
		File queriesFile = new File(queriesPath);
		Reader queriesReader = new FileReader(queriesFile);
		
		// Parse queries file.		
		parser.parse(queriesReader);
		
		// Retrieve the queries from the parsed file.
		List<IQuery> queries = parser.getQueries();

		// Create a default configuration.
		Configuration configuration = new Configuration();
		// configuration.ruleSafetyProcessor = new
		// AugmentingRuleSafetyProcessor();

		// Enable Magic Sets together with rule filtering.
		configuration.programOptmimisers.add(new MagicSets());

		// Create the knowledge base.
		IKnowledgeBase knowledgeBase = new KnowledgeBase(factMap, rules, configuration);
		
		// Data structures
		List<DeadLine> deadLines = new ArrayList<DeadLine>();
		List<Constant> constants = new ArrayList<Constant>();
		List<Identical> identicals = new ArrayList<Identical>();
		
		// Evaluate all queries over the knowledge base.
		for (IQuery query : queries) {
			List<IVariable> variableBindings = new ArrayList<>();
			IRelation relation = knowledgeBase.execute(query, variableBindings);

			// Output the variables.
			System.out.println("\n" + Optimizer.color(query.toString(), "YELLOW") + "\n" + variableBindings);

			for (int i = 0; i < relation.size(); i++) {
				System.out.println(relation.get(i));

				if (query.toString().contains("dead")) {
					/*
					 * Dead lines
					 */
					
					// Extract method and line from tuple
					ITuple tuple = relation.get(i);					
					String[] fields = tuple.toString().split(", ");
					String method = fields[0].substring(2, fields[0].length() - 1);								
					int line = Integer.parseInt(fields[1].substring(0, fields[1].length() - 1));					
					
					// Insert to list
					deadLines.add(new DeadLine(method, line));
					
				} else if (query.toString().contains("constant")) {
					/*
					 * Constant Propagate 
					 */

					// Extract method, line, variable and value from tuple
					ITuple tuple = relation.get(i);					
					String[] fields = tuple.toString().split(", ");
					String method = fields[0].substring(2, fields[0].length() - 1);		
					int line = Integer.parseInt(fields[1].substring(0, fields[1].length()));
					String variable = fields[2].substring(1, fields[2].length() - 1);
					String valueString = fields[3].substring(0, fields[3].length() - 1);
					if (!valueString.matches("(\\d)*")) // Do not propagate spiglet labels
							continue;
					int value = Integer.parseInt(valueString);
					
					// Insert to list
					constants.add(new Constant(method, line, variable, value));

				} else if (query.toString().contains("identical")) {
					/*
					 * Copy Propagate
					 */

					// Extract method, line, variable1 and variable2 from tuple
					ITuple tuple = relation.get(i);					
					String[] fields = tuple.toString().split(", ");
					String method = fields[0].substring(2, fields[0].length() - 1);								
					int line = Integer.parseInt(fields[1].substring(0, fields[1].length()));					
					String variable1 = fields[2].substring(1, fields[2].length() - 1);
					String variable2 = fields[3].substring(1, fields[3].length() - 2);
					
					// Insert to list
					identicals.add(new Identical(method, line, variable1, variable2));					

				} else {
					System.out.println("Invalid query");
					System.exit(-1);
				}
			}
		}

		/**
		 * Create optimized file
		 */
		
		System.out.println("");
		System.out.println("==================================================");
		System.out.println(Optimizer.color("                TRANSFORMATIONS", "RED"));
		System.out.println("==================================================");
		System.out.println("");
		
		File oldSpiglet = new File("./../fact-generation/Examples/spiglet/" + args[0] + ".spg");
		File newSpiglet = new File("./spiglet-optimized/" + args[0] + ".spg");
		
		FileChannel src = new FileInputStream(oldSpiglet).getChannel();
		FileChannel dest = new FileOutputStream(newSpiglet).getChannel();
		dest.transferFrom(src, 0, src.size());
		
		src.close();
		dest.close();
		
		SpigletFile spigFile = new SpigletFile(newSpiglet);
		// Propagate identical variables	
		for (Identical ide : identicals)
			spigFile.renameVar(ide.method, ide.line, ide.variable1, ide.variable2);		
		System.out.println(Optimizer.color("Identicals Propagated", "YELLOW"));
		
		// Propagate constant variables
		for (Constant con : constants)
			spigFile.constantifyVar(con.method, con.line, con.variable, con.value);
		System.out.println(Optimizer.color("Constants Propagated", "YELLOW"));
		
		// Remove dead lines 
		spigFile.removeLines(deadLines);
		System.out.println(Optimizer.color("Dead lines removed\n", "YELLOW"));
				
		System.out.println(Optimizer.color("Spiglet file " + args[0] + " optimized", "GREEN"));
	}
	
	public static int getColorCode(String color) {
		switch(color) {
		case "GREEN":
			return 32;
		case "YELLOW":
			return 33;
		case "RED":
			return 31;
		case "BLUE":
			return 34;	
		}
		return 0;
	}

	public static String color(String msg, String color) {
		return (char)27 + "[" + Optimizer.getColorCode(color) + "m" + msg + (char)27 + "[0m";
	}
}

class DeadLine {
	public String method;
	public int line;	
	
	public DeadLine(String method, int line) { 
		this.line = line;
		this.method = method;
	}
}

class Constant {
	public String method;
	public int line;
	public String variable;
	public int value;
	
	public Constant(String method, int line, String variable, int value) {
		this.method = method;
		this.line = line;
		this.variable = variable;
		this.value = value;
	}
}

class Identical {
	public String method;
	public int line;
	public String variable1;
	public String variable2;
	
	public Identical(String method, int line, String variable1, String variable2) {
		this.method = method;
		this.line = line;
		this.variable1 = variable1;
		this.variable2 = variable2;
	}
}

class SpigletFile {
	File file;	
	Map<String, Integer> methodLines;
	
	public SpigletFile(File file) throws IOException {
		this.file = file;
		this.methodLines = new LinkedHashMap<String, Integer>();
		this.getMethodStarts();
	}
	
	private void getMethodStarts() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		int curLine = 1;
		String lineS;
		while ((lineS = br.readLine()) != null) {
			String firstWord = lineS.split(" ", 2)[0];
			if (lineS.matches("(.)*\\[(\\d)*\\]"))
				this.methodLines.put(firstWord, curLine);
			else if (firstWord.equals("MAIN"))
				this.methodLines.put("MAIN", curLine - 1);
			curLine++;		
		}		
		br.close();
	}
	
	public int getLine(String method, int line) {
		return this.methodLines.get(method) + line + 1;
	}
	
	private void replaceLine(int line, String regex, String replacement) throws IOException {		
		File tempFile = new File(this.file + "_temp");
		BufferedReader reader = new BufferedReader(new FileReader(this.file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;
        int lineCount = 1;
        while((currentLine = reader.readLine()) != null) {
            if(lineCount != line)
                writer.write(currentLine + System.getProperty("line.separator"));
            else { 
            	if (currentLine.split(" ", 2)[0].matches("RETURN")) {
            		writer.write(currentLine + System.getProperty("line.separator"));
            		line++;
            		continue;
            	}            		
            	writer.write(currentLine.replaceAll(regex, replacement) + System.getProperty("line.separator"));
            }
            lineCount++;
        }                
        writer.close(); 
        reader.close(); 
        tempFile.renameTo(this.file);
        System.out.println("Line replaced [" + regex + ", " + replacement + "]");
	}
	
	private void removeLine(int line) throws IOException {
		File tempFile = new File(this.file + "_temp");
		BufferedReader reader = new BufferedReader(new FileReader(this.file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;
        int lineCount = 1;
        while((currentLine = reader.readLine()) != null) {
            if(lineCount != line)
                writer.write(currentLine + System.getProperty("line.separator"));
            lineCount++;
        }                
        writer.close(); 
        reader.close(); 
        tempFile.renameTo(this.file);
        System.out.println("Line removed [" + line + "]");
	}
	
	private String extractLine(String method, int line) throws IOException {
		int realLine = this.getLine(method, line);
		BufferedReader br = new BufferedReader(new FileReader(file));
		int curLine = 1;
		String lineS;
		while ((lineS = br.readLine()) != null) {
			if (curLine == realLine) {
				br.close();
				return lineS;
			}				
			curLine++;		
		}		
		br.close();
		return null;
	}
	
	public void removeLines(List<DeadLine> deadLines) throws IOException {
		int offset = 0;
		for (String method : this.methodLines.keySet()) {
			SortedSet<Integer> lines = new TreeSet<Integer>();
			for (DeadLine dl : deadLines)
				if (dl.method.equals(method))
					lines.add(dl.line);
			
			for (int line : lines) {
				int realLine = this.getLine(method, line);
				this.removeLine(realLine - offset);
				offset++;
			}
		}						
	}
	
	public void renameVar(String method, int line, String oldVar, String newVar) throws IOException {
		if (this.canApplyRename(method, line, oldVar))
			this.replaceLine(this.getLine(method, line), oldVar, newVar);
	}

	public void constantifyVar(String method, int line, String variable, int value) throws IOException {
		if (this.canApplyConstant(method, line, variable)) 
			this.replaceLine(this.getLine(method, line), variable, Integer.toString(value));		
	}
	
	private boolean canApplyConstant(String method, int line, String variable) throws IOException {		
		String command = this.extractLine(method, line);
		int index = command.indexOf(variable) - 1;
		
		String previous1 = "";		
		while (index >= 0) {
			char c = command.charAt(index);
			if (Character.isWhitespace(command.charAt(index))) {
				if (previous1.length() > 0) { 
					if (previous1.matches("(\\d)*")) {
						previous1 = c + previous1;
						index--;
						continue;
					}
					else
						break;
				}
				index--;
				continue;
			}
			previous1 = c + previous1;  
			index--;
		}
		String previous2 = "";
		while (index >= 0) {
			char c = command.charAt(index);
			if (Character.isWhitespace(command.charAt(index))) {
				if (previous2.length() > 0) {
					if (previous2.matches("[\\d\\s]*")) {
						previous2 = c + previous2;
						index--;
						continue;
					}
					else
						break;
				}
				index--;
				continue;
			}
			previous2 = c + previous2;
			index--;
		}					
		
		if ((previous1.matches("TEMP(.)*") && previous2.matches("MOVE"))
		|| (previous1.matches("PRINT"))	
		|| (command.split(" ", 2)[0].matches("RETURN"))
		|| (previous1.matches("HALLOCATE"))
		|| ((previous1.matches("TEMP(.)*") && previous2.matches("(LT|PLUS|MINUS|TIMES)"))))
			return true;
		
		return false;
	}
	
	private boolean canApplyRename(String method, int line, String variable) throws IOException {
		String realLine = this.extractLine(method, line);
		if (realLine.contains("NOOP") || !(realLine.contains(variable)))
			return false;		
		return ((realLine.trim().split(" ", 2)[1].substring(0, variable.length()).matches(variable)) ? false : true);
	}
}