import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import fact_info.*;


public class Writer {	
	public Writer(Set<Fact> facts, String directoryName) throws IOException {			
		// Write instruction facts.
		File newFile = new File(directoryName + "/" + "instruction.iris");
		newFile.setWritable(true);
		FileOutputStream file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof InstructionFact) {
			InstructionFact i = (InstructionFact) f;
			String toWrite = "instruction(\"" + i.head + "\", " + Integer.toString(i.line) + ", \"" + i.instruction + "\").";
			file.write(toWrite.getBytes());
		}
		file.close();	
		
		// Write var facts.
		newFile = new File(directoryName + "/" + "var.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof VarFact) {
			VarFact i = (VarFact) f;
			String toWrite = "var(\"" + i.head + "\", " + i.variable + "\").";
			file.write(toWrite.getBytes());
		}
		file.close();
		
		// Write next facts.
		newFile = new File(directoryName + "/" + "next.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof NextFact) {
			NextFact i = (NextFact) f;
			String toWrite = "next(\"" + i.head + "\", " + Integer.toString(i.from) + ", " + Integer.toString(i.to) + ").";
			file.write(toWrite.getBytes());
		}
		file.close();
		
		// Write varMove facts.
		newFile = new File(directoryName + "/" + "varMove.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof VarMoveFact) {
			VarMoveFact i = (VarMoveFact) f;
			String toWrite = "varMove(\"" + i.head + "\", " + Integer.toString(i.line) + ", \"" + i.to + "\", \"" + i.from + "\").";
			file.write(toWrite.getBytes());
		}
		file.close();
		
		// Write constMove facts.
		newFile = new File(directoryName + "/" + "constMove.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof ConstMoveFact) {
			ConstMoveFact i = (ConstMoveFact) f;
			String toWrite = "constMove(\"" + i.head + "\", " + Integer.toString(i.line) + ", \"" + i.to + "\", \"" + Integer.parseInt(i.from) + "\").";
			file.write(toWrite.getBytes());
		}
		file.close();
		
		// Write varUse facts.
		newFile = new File(directoryName + "/" + "varUse.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof VarUseFact) {
			VarUseFact i = (VarUseFact) f;
			String toWrite = "varUse(\"" + i.head + "\", " + Integer.toString(i.line) + ", \"" + i.variable + "\").";
			file.write(toWrite.getBytes());
		}
		file.close();
		
		// Write varDef facts.
		newFile = new File(directoryName + "/" + "varDef.iris");
		newFile.setWritable(true);
		file = new FileOutputStream(newFile);
		for (Fact f : facts)
		if (f instanceof VarDefFact) {
			VarDefFact i = (VarDefFact) f;
			String toWrite = "varDef(\"" + i.head + "\", " + Integer.toString(i.line) + ", \"" + i.variable + "\").";
			file.write(toWrite.getBytes());
		}
		file.close();
	}
}
