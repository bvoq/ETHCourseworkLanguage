package statement.predefined;

import java.util.HashMap;
import java.util.List;

import exception.EvaluationException;
import exception.ParseException;
import program.Bytecode;
import statement.AssignStatement;
import statement.EvaluateFunction;
import statement.Expression;

public class SineFunction extends EvaluateFunction {
	
	public SineFunction(String functionName, List<Expression> parameterExpressions) throws ParseException {
		super(functionName, parameterExpressions);
		if(parameterExpressions.size() != 1) throw new ParseException( ParseException.createSyntaxErrorMessage("1 argument at sine function", "", -1, ""));
	}

	@Override
	public double evaluate(HashMap<String, AssignStatement> variablesInScope) throws EvaluationException {
		double value = parameterAssignment.get(0).getExpression().evaluate(variablesInScope);
		return Math.sin(value);
	}
	
	
	@Override
	public void convertToBytecode(Bytecode bytecode, HashMap<String, AssignStatement> variablesInScope) throws EvaluationException {
		parameterAssignment.get(0).getExpression().convertToBytecode(bytecode, variablesInScope);
		bytecode.push("FUNC");
		bytecode.push("sin");
	}
}