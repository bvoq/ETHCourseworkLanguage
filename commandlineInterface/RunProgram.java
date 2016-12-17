package commandlineInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import exception.ProgramException;
import program.Bytecode;
import program.Program;

public class RunProgram {
	//The programming language can do shading, recursion and all of the required things (aside from if statements >.>)
	String sampleProgram1 =
			"PI = 3.1415926;\n"
					+ "f(p,q) = (2*q)+(6*p);\n"
					+ "x = cos(PI/4);\n"
					+ "y = sin(PI/4);\n"
					+ "z = f(x,y);";

	String sampleProgram2 =
			"f(x) = x + 1\n;"
					+ "z = f(f(f(f(1))));\n";

	
	public static void help(PrintStream out) {
		out.println("Example usage:");
		out.println("-i in.elang : load file in.elang");
		out.println("-o out.ebytecode : stores the program as in.ebytecode");
		out.println("-e in.ebytecode : runs bytecode input file");
		out.println("-v : verbose");
		out.println("eproglang   (launches the interactive version of eproglang)");
		out.println("eproglang -i input.elang -v  (runs the code after compiling it into bytecode)");
		out.println("eproglang -i input.elang -o output.bytecode  (compiles into bytecode, runs it and stores it)");
		out.println("eproglang -e   (runs compiled bytecode)");
		System.exit(0);
	}

	public static String loadString(String fileLocation) throws FileNotFoundException {
		File file = new File(fileLocation);
		Scanner in = new Scanner(file);
		String stringified = "";
		while(in.hasNext()) {
			stringified += in.next();
		}
		in.close();
		return stringified;
	}
	
	public static List<Object> loadBytecode(String fileLocation) throws FileNotFoundException {
		File file = new File(fileLocation);
		Scanner in = new Scanner(file);
		List<Object> out = new ArrayList<Object>();
		while(in.hasNext()) {
			if(in.hasNextDouble()) out.add(in.nextDouble());
			else out.add(in.next());
		}
		return out;
	}
	public static void main(String[] args) {
		String programCode = "", bytecodeOutputLocation = "";
		boolean verbose = false;
		List<Object> programBytecode = new ArrayList<Object>();
		for(int i = 0; i < args.length; ++i) {
			if("-i".equals(args[i]) && i + 1 < args.length) {
				try {
					programCode = loadString(args[++i]);
				} catch (FileNotFoundException e) {
					System.err.println("Cannot read file: "  + args[i]);
					System.exit(0);
				}
			}
			else if("-o".equals(args[i]) && i + 1 < args.length) {
				bytecodeOutputLocation = args[++i];
			}
			else if("-e".equals(args[i]) && i + 1 < args.length) {
				try {
					programBytecode = loadBytecode(args[++i]);
				} catch (FileNotFoundException e) {
					System.err.println("Cannot read file: "  + args[i]);
					System.exit(0);
				}
			}
			else if("-v".equals(args[i])) {
				verbose = true;
			}
			else help(System.out);
		}
		
		if(programCode.length() > 0 && programBytecode.size() > 0) {
			System.err.println("Cannot both run a program and bytecode.");
			System.exit(0);
		}
		Bytecode b = null;
		if(programCode.length() > 0) {
			//Compiling bytecode
			Program q = new Program(programCode);
			b = q.compileToByteCodeTopDown();
		} else if(programBytecode.size() > 0) {
			//Loading bytecode
			b = new Bytecode();
			b.bytecode = programBytecode;
		} else {
			//Interactive program (executing in real-time)
			Scanner in = new Scanner(System.in);
			System.out.println("EProglang - A small programming language by Kevin De Keyser for ETH coursework.");
			Program p = new Program();
			p.interactiveEvaluation(in);
			b = p.compileToByteCodeTopDown();
		}
		
		if(bytecodeOutputLocation.length() > 0) {
			//Storing Bytecode (before executing it, in case exit() method is called)
			if(b == null) {
				System.err.println("No program given, so no bytecode was generated/stored.");
			} else {
				try {
				File file = new File(bytecodeOutputLocation);
				PrintStream out = new PrintStream(file);
				for(int i = 0; i < b.bytecode.size(); ++i) {
					out.println(b.bytecode.get(i).toString());
					}
				out.close();
				} catch(Exception e) {
					System.err.println("Cannot store bytecode at " + bytecodeOutputLocation);
				}
			}
		}
		
		if(programCode.length() > 0 || programBytecode.size() > 0) {
			//Running Bytecode (after storing it, in case exit() method is called)
			try {
				b.evaluate(verbose);
			} catch(ProgramException e) {
				System.err.println(e.getMessage());
			}
		}

	}
}
