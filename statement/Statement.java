package statement;

import java.io.PrintStream;
import java.util.HashMap;

import exception.EvaluationException;
import program.Bytecode;

abstract public class Statement {
	/**
	 * This simply concatenates the expression in bytecode to the parameter.
	 * Also it uses a simply Postorder traversal to generate reverse-polish notation code.
	 * @param bytecode
	 */
	abstract public void convertToBytecode(Bytecode bytecode, HashMap<String, AssignStatement> variablesInScope) throws EvaluationException;
	public abstract void printBracketForm(PrintStream out);
	
}
