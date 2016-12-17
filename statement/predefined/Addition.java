package statement.predefined;

import java.util.HashMap;
import java.util.List;

import exception.EvaluationException;
import program.Bytecode;
import statement.AssignStatement;
import statement.EvaluateFunction;
import statement.Expression;

public class Addition extends EvaluateFunction {
	
	public Addition(String functionName, List<Expression> parameterExpressions) {
		super(functionName, parameterExpressions);
	}

	@Override
	public double evaluate(HashMap<String, AssignStatement> variablesInScope) throws EvaluationException {
		double value1 = parameterAssignment.get(0).getExpression().evaluate(variablesInScope);
		double value2 = parameterAssignment.get(1).getExpression().evaluate(variablesInScope);
		return value1 + value2;
	}
	
	@Override
	public void convertToBytecode(Bytecode bytecode, HashMap<String, AssignStatement> variablesInScope) throws EvaluationException {
		parameterAssignment.get(0).getExpression().convertToBytecode(bytecode, variablesInScope);
		parameterAssignment.get(1).getExpression().convertToBytecode(bytecode, variablesInScope);
		bytecode.push("OP");
		bytecode.push("+");
	}
}
