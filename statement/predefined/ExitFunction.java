package statement.predefined;

import java.util.HashMap;
import java.util.List;

import exception.EvaluationException;
import exception.ParseException;
import program.Bytecode;
import statement.AssignStatement;
import statement.EvaluateFunction;
import statement.Expression;

public class ExitFunction extends EvaluateFunction {
	
	public ExitFunction(String functionName, List<Expression> parameterExpressions) throws ParseException {
		super(functionName, parameterExpressions);
		if(parameterExpressions.size() > 1)
			throw new ParseException("Expected 0/1 parameter of exit method, but not " + parameterExpressions.size());
	}

	@Override
	public double evaluate(HashMap<String, AssignStatement> variablesInScope) throws EvaluationException {
		if(parameterAssignment.size() == 0) {
			System.exit(0);
		}
		else {
			double value = parameterAssignment.get(0).getExpression().evaluate(variablesInScope);
			System.exit((int)value);
		}
		return -1;
	}
	
	@Override
	public void convertToBytecode(Bytecode bytecode, HashMap<String, AssignStatement> variablesInScope) throws EvaluationException {
		if(parameterAssignment.size() == 0) {
			bytecode.push(new Double(0));
		}
		else {
			double value = parameterAssignment.get(0).getExpression().evaluate(variablesInScope);
			bytecode.push(new Double((int)value));
		}
	}
}
