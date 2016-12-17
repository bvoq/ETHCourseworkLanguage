package statement;

import java.util.HashMap;
import java.util.List;

import exception.EvaluationException;
import program.Bytecode;

public class UserdefinedEvalFunction extends EvaluateFunction {

	public UserdefinedEvalFunction(String functionName, List<Expression> parameterExpression) {
		super(functionName, parameterExpression);
	}

	@Override
	public double evaluate(HashMap<String, AssignStatement> variablesInScope) throws EvaluationException {
		if(!variablesInScope.containsKey(functionName)) {
			throw new EvaluationException("Function/Variable " + functionName + " was not declared.");
		}

		AssignStatement thisFunction = variablesInScope.get(functionName);
		
		if(thisFunction.getParameterSize() != parameterAssignment.size()) {
			if(thisFunction.getParameterSize() == 0) throw new EvaluationException(thisFunction.getName() + " is a variable (nullary function), but received " + parameterAssignment.size() + " parameters");
			else throw new EvaluationException(thisFunction.getName() + " has " + thisFunction.getParameterSize() + " parameters, but received " + parameterAssignment.size());
		}

		for(int i = 0; i < thisFunction.getParameterSize(); ++i) {
			//System.out.println(parameterAssignment.get(i).getName() + " = " + parameterAssignment.get(i).name);
			variablesInScope.put(parameterAssignment.get(i).getName(), parameterAssignment.get(i));
		}
		double evaluation = thisFunction.getExpression().evaluate(variablesInScope);
		for(int i = 0; i < thisFunction.getParameterSize(); ++i) {
			variablesInScope.remove(parameterAssignment.get(i).getName(), parameterAssignment.get(i));
		}

		return evaluation;
	}

	@Override
	public void convertToBytecode(Bytecode bytecode, HashMap<String, AssignStatement> variablesInScope) throws EvaluationException {
		if(parameterAssignment.size() == 0) { //variables are loaded and stored (not for efficiency, but for the exercise).
			bytecode.push("LOAD");
			bytecode.push(functionName);
		} else {
			if(!variablesInScope.containsKey(functionName)) {
				throw new EvaluationException("Function/Variable " + functionName + " was not declared.");
			}

			AssignStatement thisFunction = variablesInScope.get(functionName);

			if(thisFunction.getParameterSize() != parameterAssignment.size()) {
				if(thisFunction.getParameterSize() == 0) throw new EvaluationException(thisFunction.getName() + " is a variable (nullary function), but received " + parameterAssignment.size() + " parameters");
				else throw new EvaluationException(thisFunction.getName() + " has " + thisFunction.getParameterSize() + " parameters, but received " + parameterAssignment.size());
			}

			assert(thisFunction.getParameterSize() == parameterAssignment.size());

			for(int i = 0; i < thisFunction.getParameterSize(); ++i) {
				variablesInScope.put(parameterAssignment.get(i).getName(), parameterAssignment.get(i));
			}
			thisFunction.getExpression().convertToBytecode(bytecode, variablesInScope);
			for(int i = 0; i < thisFunction.getParameterSize(); ++i) {
				variablesInScope.remove(parameterAssignment.get(i).getName(), parameterAssignment.get(i));
			}
		}
	}
}
