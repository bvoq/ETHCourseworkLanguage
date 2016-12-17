package statement;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

import statement.Number;

import exception.EvaluationException;
import program.Bytecode;

//WOOOO!! I'm so excited right now, it can do higher order functions! Woo!

//This will be the future Expression handler
public class AssignStatement extends Statement {
	protected String name;
	protected Expression expression;
	protected int parameterSize;
	
	public AssignStatement(String name, List<String> parameters, Expression expression) {
		this.name = name;
		this.expression = expression;
		this.parameterSize = parameters.size();
		
		//if(parameterSize == 0) this.expression = new Number(expression.evaluate());

		alphaRenaming(parameters, this.expression);
	}
	
	/**
	 * alphaRenaming replaces all bound variables with a special name in order to make sure no bound/unbound collisions happen.
	 * @param parameters The original parameter names.
	 * @param expression The replaced parameters.
	 */
	protected void alphaRenaming(List<String> parameters, Expression expression) {
		//Special name for parameter symbols: functionname+"PARAM"+number
		for(int i = 0; i < parameters.size(); ++i) {
			expression.alphaRename(parameters.get(i), name + "PARAM" + i);
		}	
	}
	
	@Override
	public void printBracketForm(PrintStream out) {
		out.print(name);
		if(parameterSize > 0) {
			out.print("(");
			for(int i = 0; i < parameterSize; ++i) {
				out.print(name + "PARAM" + i);
				if(i < parameterSize - 1) out.print(",");
			}
			out.print(")");
		}
		out.print(" = ");
		expression.printBracketForm(out);
		out.print(";");
	}
	
	public void variableEvaluation(HashMap<String, AssignStatement> variablesInScope) {
		assert(parameterSize == 0);
		try {
			Expression newExpression = new Number(expression.evaluate(variablesInScope));
			expression = newExpression;
		}
		catch(Exception e) {
			//silence the error and therefore don't change the expression
		}
	}
	
	public String getName () {
		return name;
	}
	
	public int getParameterSize() {
		return parameterSize;
	}
	
	public Expression getExpression() {
		return expression;
	}

	@Override
	public void convertToBytecode(Bytecode bytecode, HashMap<String, AssignStatement> variablesInScope)
			throws EvaluationException {
		if(parameterSize == 0) {
			expression.convertToBytecode(bytecode, variablesInScope);
			bytecode.push("STORE");
			bytecode.push(name);
		} else {
			//No bytecode needed for a function. It will get converted into bytecode when evaluated.
		}
	}

}
