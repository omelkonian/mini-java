import syntaxtree.*;
import java.io.*;

public class Main {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java Main <inputFile1> ..... <inputFileN>");
			System.exit(1);
		}
		for (String inputFile : args) {
			FileInputStream fis = null;
			try {
				int index = inputFile.lastIndexOf('/'); // Get file name to check main class
				String input = (index != -1) ? inputFile.substring(index + 1, inputFile.length() - 5) : inputFile;
				
				// Print file name
				for (int i = 0; i < 50; i++)	System.out.print("#");
				System.out.println("");
				for (int i = 0; i < 15; i++)	System.out.print(" ");
				System.out.println(input);
				for (int i = 0; i < 50; i++) 	System.out.print("#");	
				System.out.println("");
								
				fis = new FileInputStream(inputFile);
				MiniJavaParser parser = new MiniJavaParser(fis);
				Goal root = parser.Goal();

				// FIRST PASS: Collect class information for every class.				
				FirstPassVisitor visitor1 = new FirstPassVisitor();
				root.accept(visitor1, null);

				// SECOND PASS: Complete semantic check.
				SecondPassVisitor visitor2 = new SecondPassVisitor(visitor1.classes);
				root.accept(visitor2, null);
				
				System.out.println((char)27 + "[32mSemantic check successful " + (char)27 + "[0m");
			} catch (ParseException ex) {
				System.out.println(ex.getMessage());
			} catch (FileNotFoundException ex) {
				System.err.println(ex.getMessage());
			} catch (Exception ex) { // SEMANTIC ERROR!
				System.out.println(ex.getMessage());
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
}
