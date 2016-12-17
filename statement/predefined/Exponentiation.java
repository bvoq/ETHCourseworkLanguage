package statement.predefined;

import java.util.HashMap;
import java.util.List;

import exception.EvaluationException;
import exception.ParseException;
import program.Bytecode;
import statement.AssignStatement;
import statement.EvaluateFunction;
import statement.Expression;

public class Exponentiation extends EvaluateFunction {
	
	public Exponentiation(String functionName, List<Expression> parameterExpressions) throws ParseException {
		super(functionName, parameterExpressions);
		if(parameterExpressions.size() != 2) throw new ParseException( ParseException.createSyntaxErrorMessage("2 arguments at exponentiation", "", -1, ""));
	}

	@Override
	public double evaluate(HashMap<String, AssignStatement> variablesInScope) throws EvaluationException {
		double value1 = parameterAssignment.get(0).getExpression().evaluate(variablesInScope);
		double value2 = parameterAssignment.get(1).getExpression().evaluate(variablesInScope);
		return Math.pow(value1, value2);
	}
	
	@Override
	public void convertToBytecode(Bytecode bytecode, HashMap<String, AssignStatement> variablesInScope) throws EvaluationException {
		parameterAssignment.get(0).getExpression().convertToBytecode(bytecode, variablesInScope);
		parameterAssignment.get(1).getExpression().convertToBytecode(bytecode, variablesInScope);
		bytecode.push("OP");
		bytecode.push("^");
	}
}
