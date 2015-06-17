
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import syntaxtree.Goal;
import fact_info.ConstMoveFact;
import fact_info.Fact;
import fact_info.InstructionFact;
import fact_info.NextFact;
import fact_info.VarDefFact;
import fact_info.VarFact;
import fact_info.VarMoveFact;
import fact_info.VarUseFact;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Usage: java Main <inputFile1> ..... <inputFileN>");
			System.exit(1);
		}
		for (String inputFile : args) {
			FileInputStream fis = null;
			try {
				int index = inputFile.lastIndexOf('/'); // Get file name to check main class
				String input = (index != -1) ? inputFile.substring(index + 1, inputFile.length() - 4) : inputFile;
								
				fis = new FileInputStream(inputFile);
				SpigletParser parser = new SpigletParser(fis);
				Goal root = parser.Goal();

				// Fact-generation visitor				
				FactGenerator visitor = new FactGenerator();
				root.accept(visitor, null);

				// Write output files
				String path = "./../iris-master/generated-facts/" + input;
				File newDir = new File(path);
				newDir.mkdir();							
				write(visitor.facts, path);
				
				// Print output
				System.out.println(input + ": " + (char)27 + "[32mFacts generated " + (char)27 + "[0m");
				
			} catch (ParseException ex) {
				System.out.println(ex.getMessage());
			} catch (FileNotFoundException ex) {
				System.err.println(ex.getMessage());							
			} finally {
				try {
					if (fis != null)
						fis.close();
				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}
			}
		}
	}
	
	public static void write(Set<Fact> facts, String directoryName) throws IOException {
		Set<String> map = new HashSet<String>();
		
		// Write instruction facts.
		File newFile = new File(directoryName + "/" + "instruction.iris");
		newFile.setWritable(true);
		FileOutputStream file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof InstructionFact) {
			InstructionFact i = (InstructionFact) f;
			String toWrite = "instruction(\"" + i.method + "\", " + Integer.toString(i.line) + ", \"" + i.instruction + "\").";
			toWrite += "\n";
			if (!map.contains(toWrite)) {
				file.write(toWrite.getBytes());
				map.add(toWrite);
			}
		}
		file.close();	
		
		// Write var facts.
		newFile = new File(directoryName + "/" + "var.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof VarFact) {
			VarFact i = (VarFact) f;
			String toWrite = "var(\"" + i.method + "\", \"" + i.variable + "\").\n";
			if (!map.contains(toWrite)) {
				file.write(toWrite.getBytes());
				map.add(toWrite);
			}
		}
		file.close();
		
		// Write next facts.
		newFile = new File(directoryName + "/" + "next.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof NextFact) {
			NextFact i = (NextFact) f;
			String toWrite = "next(\"" + i.method + "\", " + Integer.toString(i.from) + ", " + Integer.toString(i.to) + ").\n";
			if (!map.contains(toWrite)) {
				file.write(toWrite.getBytes());
				map.add(toWrite);
			}
		}
		file.close();
		
		// Write varMove facts.
		newFile = new File(directoryName + "/" + "varMove.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof VarMoveFact) {
			VarMoveFact i = (VarMoveFact) f;
			String toWrite = "varMove(\"" + i.method + "\", " + Integer.toString(i.line) + ", \"" + i.to + "\", \"" + i.from + "\").\n";
			if (!map.contains(toWrite)) {
				file.write(toWrite.getBytes());
				map.add(toWrite);
			}
		}
		file.close();
		
		// Write constMove facts.
		newFile = new File(directoryName + "/" + "constMove.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof ConstMoveFact) {
			ConstMoveFact i = (ConstMoveFact) f;
			boolean isLabel = FactGenerator.isLabel(i.from);
			String toWrite = "";
			if (isLabel)
				toWrite += "constMove(\"" + i.method + "\", " + Integer.toString(i.line) + ", \"" + i.to + "\", \"" + i.from + "\").\n";
			else
				toWrite += "constMove(\"" + i.method + "\", " + Integer.toString(i.line) + ", \"" + i.to + "\", " + i.from + ").\n";
			if (!map.contains(toWrite)) {
				file.write(toWrite.getBytes());
				map.add(toWrite);
			}
		}
		file.close();
		
		// Write varUse facts.
		newFile = new File(directoryName + "/" + "varUse.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof VarUseFact) {
			VarUseFact i = (VarUseFact) f;
			String toWrite = "varUse(\"" + i.method + "\", " + Integer.toString(i.line) + ", \"" + i.variable + "\").\n";
			if (!map.contains(toWrite)) {
				file.write(toWrite.getBytes());
				map.add(toWrite);
			}
		}
		file.close();
		
		// Write varDef facts.
		newFile = new File(directoryName + "/" + "varDef.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof VarDefFact) {
			VarDefFact i = (VarDefFact) f;
			String toWrite = "varDef(\"" + i.method + "\", " + Integer.toString(i.line) + ", \"" + i.variable + "\").\n";
			if (!map.contains(toWrite)) {
				file.write(toWrite.getBytes());
				map.add(toWrite);
			}
		}
		file.close();
	}
}
