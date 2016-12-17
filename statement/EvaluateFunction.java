package statement;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

abstract public class EvaluateFunction extends Expression {
	protected String functionName; //including left paranthesis
	protected List<AssignStatement> parameterAssignment;
	
	public EvaluateFunction(String functionName, List<Expression> parameterExpression) {
		this.functionName = functionName;
		this.parameterAssignment = new ArrayList<AssignStatement>();
		for(int i = 0; i < parameterExpression.size(); ++i) {
			AssignStatement newAssignStatement = new AssignStatement(functionName + "PARAM" + i, new ArrayList<String>(), parameterExpression.get(i));
			parameterAssignment.add(newAssignStatement);
		}
	}
	
	@Override
	public void printBracketForm(PrintStream out) {
		out.print(functionName + "(");
		for(int i = 0; i < parameterAssignment.size(); ++i) {
			parameterAssignment.get(i).getExpression().printBracketForm(out);
			if(i != parameterAssignment.size() - 1) out.print(", ");
		}
		out.print(")");
	}
	
	public String getFunctionName() {
		return functionName;
	}
	public List<AssignStatement> getParameterAssignment() {
		return parameterAssignment;
	}
	
	@Override
	public void alphaRename(String search, String replace) {
		if(search.equals(functionName)) functionName = replace;
		for(AssignStatement e : parameterAssignment) {
			e.getExpression().alphaRename(search, replace);
		}
	}
}
