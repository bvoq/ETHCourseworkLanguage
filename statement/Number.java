package statement;

import java.io.PrintStream;
import java.util.HashMap;

import program.Bytecode;

public class Number extends Expression {
	private double value;
	
	public Number(double value) {
		this.value = value;
	}

	@Override
	public double evaluate(HashMap<String, AssignStatement> variablesInScope) {
		return value;
	}

	@Override
	public void printBracketForm(PrintStream out) {
		out.print(value);
	}

	@Override
	public void alphaRename(String search, String replace) {
		//Does nothing, a number needs not to be alpha renamed.
	}
	
	public double getValue() {
		return value;
	}

	@Override
	public void convertToBytecode(Bytecode bytecode, HashMap<String, AssignStatement> variablesInScope) {
		bytecode.push(value);
	}
}
