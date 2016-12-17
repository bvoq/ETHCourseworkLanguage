package program;

import statement.Statement;
import statement.AssignStatement;
import statement.Expression;
import statement.Number;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import tokenizer.Tokenizer;
import parser.Parser;

import exception.ParseException;
import exception.EvaluationException;
import exception.ProgramException;

/**
 * The Program implements a fairly simple programming language, with some neat features:
 * - Variable Shadowing (difference between bound and unbound variables)
 * - Bytecode compiler
 * - Real-time evaluation
 * - Recursion (although fairly useless without if statements)
 * 
 * @author Kevin De Keyser
 *
 */
public class Program {
	private String programCode;


	private HashMap<String, AssignStatement> userdefinedAssignStatements; //variables or functions
	private List<Statement> statements;

	public Program() {
		
	}
	
	public Program(String programCode) {
		this.programCode = programCode;
	}


	/**
	 * This parses and evaluates the program at the same time. Although I condemn this approach, this is what was asked in the exercise.
	 * @throws ParseException
	 */
	public void topDownParser(boolean verbose) throws ParseException {
		userdefinedAssignStatements = new HashMap<String, AssignStatement>();
		statements = new ArrayList<Statement>();

		Tokenizer tok = new Tokenizer(programCode);
		Parser parse = new Parser(tok, programCode);
		while(tok.hasNext()) {
			try {
				Statement newStatement = parse.parseStatement();
				if(newStatement instanceof AssignStatement) {
					AssignStatement newAssignStatement = (AssignStatement) newStatement;
					if(newAssignStatement.getParameterSize() == 0) {
						newAssignStatement.variableEvaluation(userdefinedAssignStatements);
					}
					userdefinedAssignStatements.put(newAssignStatement.getName(), newAssignStatement);
					if(verbose) {
						if(newAssignStatement.getParameterSize() == 0) {
							Expression newEvaluatedStatement = newAssignStatement.getExpression();
							try {
								Number num = (Number)newEvaluatedStatement;
								System.out.println("Evaluated variable " + newAssignStatement.getName() + " = " + num.getValue());
							} catch(Exception e) {
								System.err.print("Cannot evaluate " + newAssignStatement.getName());
							}
						}
						else {
							System.out.print("Assigning ");
							newAssignStatement.printBracketForm(System.out);
							System.out.print("\n");
						}
					}
				}
				statements.add(newStatement);
				//newStatement.evaluate();
			} catch(ProgramException e) {
				System.err.println(e.getMessage());
			} catch(Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public void interactiveEvaluation(Scanner in) {
		boolean verbose = true; //Always verbose when interactive
		userdefinedAssignStatements = new HashMap<String, AssignStatement>();
		statements = new ArrayList<Statement>();
		programCode = "";
		while(in.hasNext()) {
			System.out.print(">> ");
			System.out.flush();
			String currentLine = in.nextLine();
			programCode += currentLine;
			Tokenizer tok = new Tokenizer(currentLine);
			Parser parse = new Parser(tok, currentLine);
			while(tok.hasNext()) {
				try {
					Statement newStatement = parse.parseStatement();
					if(newStatement instanceof AssignStatement) {
						AssignStatement newAssignStatement = (AssignStatement) newStatement;
						if(newAssignStatement.getParameterSize() == 0) {
							newAssignStatement.variableEvaluation(userdefinedAssignStatements);
						}
						userdefinedAssignStatements.put(newAssignStatement.getName(), newAssignStatement);
						if(verbose) {
							if(newAssignStatement.getParameterSize() == 0) {
								Expression newEvaluatedStatement = newAssignStatement.getExpression();
								try {
									Number num = (Number)newEvaluatedStatement;
									System.out.println("Evaluated variable " + newAssignStatement.getName() + " = " + num.getValue());
								} catch(Exception e) {
									System.err.print("Cannot evaluate " + newAssignStatement.getName());
								}
							}
							else {
								System.out.print("Assigning ");
								newAssignStatement.printBracketForm(System.out);
								System.out.print("\n");
							}
						}
					}
					statements.add(newStatement);
					//newStatement.evaluate();
				} catch(ProgramException e) {
					System.err.println(e.getMessage());
				} catch(Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * This only parses the entire program top to bottom.
	 * @throws ParseException
	 */
	public void parse() throws ParseException {
		userdefinedAssignStatements = new HashMap<String, AssignStatement>();
		statements = new ArrayList<Statement>();

		Tokenizer tok = new Tokenizer(programCode);
		Parser parse = new Parser(tok, programCode);
		while(tok.hasNext()) {
			Statement newStatement = parse.parseStatement();
			if(newStatement instanceof AssignStatement) {
				AssignStatement newAssignStatement = (AssignStatement) newStatement;
				userdefinedAssignStatements.put(newAssignStatement.getName(), newAssignStatement);
			}
			statements.add(newStatement);
		}
	}

	/**
	 * This simply compiles the program to Bytecode.
	 * @return
	 */
	public Bytecode compileToByteCodeTopDown() {
		userdefinedAssignStatements = new HashMap<String, AssignStatement>();

		try {
			topDownParser(false);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
		Bytecode bytecode = new Bytecode();
		try {
			for(int i = 0; i < statements.size(); ++i) {
				statements.get(i).convertToBytecode(bytecode, userdefinedAssignStatements);
			}
		} catch(ProgramException e) {
			System.err.println(e.getMessage());
		}
		return bytecode;
	}

	/**
	 * This prints the (somewhat pre-processed) program in bracket notation.
	 * @param out Stream where to print the code.
	 */
	public void printBracketForm(PrintStream out) {
		if(statements != null) {
			for(int i = 0; i < statements.size(); ++i) {
				statements.get(i).printBracketForm(out);
			}
		} else {
			System.out.println("The program has not yet been evaluated / parsed.");
		}
	}

	public HashMap<String, AssignStatement> getUserdefinedStatements() {
		return userdefinedAssignStatements;
	}
}