package statement;

import java.util.HashMap;

import exception.EvaluationException;
import program.Bytecode;

abstract public class Expression extends Statement {
	/**
	 * This evaluates the expression directly without compiling into bytecode.
	 * @param variablesInScope
	 * @return
	 * @throws EvaluationException
	 */
	abstract public double evaluate(HashMap<String, AssignStatement> variablesInScope) throws EvaluationException;
	

	/**
	 * alphaRename replaces occurrences of bound variables with a special name in order to rectify the formula.
	 * @param search The bound variable/function to be replaced.
	 * @param replace The new name.
	 */
	abstract public void alphaRename(String search, String replace);
}
