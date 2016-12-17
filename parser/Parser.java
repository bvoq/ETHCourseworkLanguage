package parser;
import statement.Expression;
import statement.AssignStatement;
import statement.EvaluateFunction;
import statement.Statement;
import statement.UserdefinedEvalFunction;
import statement.predefined.Addition;
import statement.predefined.CosineFunction;
import statement.predefined.Division;
import statement.predefined.ExitFunction;
import statement.predefined.Exponentiation;
import statement.predefined.Multiplication;
import statement.predefined.SineFunction;
import statement.predefined.Subtraction;
import tokenizer.Tokenizer;
import statement.Number;

import java.util.ArrayList;
import java.util.List;

import exception.ParseException;
import exception.ProgramException;
import program.Program;

/**
 * Parses and interprets expressions according to the following EBNF:
 * 
 * <pre>
 * equals <= =
 * semicolon <= ;
 * digit <= 0 | 1 | ... | 9
 *  char <= A | B | ... | Z | a | b | ... | z
 *   num <= digit { digit } [ . digit { digit } ]
 *   var <= char { char }
 *  func <= char { char } (
 *    op <= + | - | * | / | ˆ
 *  open <= (
 * close <= )
 * comma <= ,
 *  atom <= num | var
 *  term <= open expr close | func expr {comma expr} close | atom
 *  expr <= term [ op term ]
 *  stmt <= var equals expr;
 *  prog <= { stmt }
 * </pre>
 */

public class Parser {
	
	private Tokenizer tok; //Reads tokens according to the non-terminal symbols of the EBNF grammar.
	private String programCode;
	
	public Parser(Tokenizer tok, String programCode) {
		this.tok = tok;
		this.programCode = programCode;
	}
	
	public Statement parseStatement() throws ParseException {
		Statement newStatement = parseAssignStatement();
		//if(!tok.hasNextSemi()) throw new ParseException ( ParseException.createSyntaxErrorMessage(";", programCode, -1, tok.hasNext() ? tok.next() : "EOF"));
		//Let's make semicolons totally optional! Woo!
		if(tok.hasNextSemi()) tok.nextSemi();
		
		return newStatement;
	}
	
	private AssignStatement parseAssignStatement() throws ParseException {
		AssignStatement newAssignStatement;
		if(!tok.hasNextVar() && !tok.hasNextFunc()) throw new ParseException ( ParseException.createSyntaxErrorMessage("variable assignment or function assignment", programCode, -1, tok.hasNext() ? tok.next() : "EOF"));
		if(tok.hasNextVar()) {
			String varName = tok.nextVar();
			if(!tok.hasNextAssign()) throw new ParseException ( ParseException.createSyntaxErrorMessage("= sign", programCode, -1, tok.hasNext() ? tok.next() : "EOF"));
			tok.nextAssign();
			Expression expression = parseExpression();
			newAssignStatement = new AssignStatement(varName, new ArrayList<String>(), expression); //Variable is an assignment without parameters
		} else {
			String funcName = tok.nextFunc();
			funcName = funcName.substring(0,funcName.length() - 1);
			//TODO Check whether funcName is not already known...
			
			List<String> parameterNames = new ArrayList<String>();
			while(!tok.hasNextClose()) {
				if(!tok.hasNextVar())  throw new ParseException ( ParseException.createSyntaxErrorMessage("variable or function parameter", programCode, -1, tok.hasNext() ? tok.next() : "EOF"));
				parameterNames.add(tok.nextVar());
				if(!tok.hasNextComma() && !tok.hasNextClose()) throw new ParseException ( ParseException.createSyntaxErrorMessage(", or )", programCode, -1, tok.hasNext() ? tok.next() : "EOF"));
				if(tok.hasNextComma()) tok.nextComma();
			}
			tok.nextClose();

			if(!tok.hasNextAssign()) throw new ParseException ( ParseException.createSyntaxErrorMessage("= sign", programCode, -1, tok.hasNext() ? tok.next() : "EOF"));
			tok.nextAssign();
			Expression assignPart = parseExpression();
			newAssignStatement = new AssignStatement(funcName, parameterNames, assignPart);
		}
		return newAssignStatement;
	}
	
	private Expression parseExpression() throws ParseException {
		Expression newExpression;
		Expression termExpression = parseTerm();
		newExpression = termExpression; //Will be overridden if operator.
		if(tok.hasNextOp()) {
			String operator = tok.nextOp();
			Expression secondExpression = parseTerm();
			List<Expression> parameterList = new ArrayList<Expression>();
			parameterList.add(termExpression);
			parameterList.add(secondExpression);
			
			if("+".equals(operator)) {
				Addition addition = new Addition(operator, parameterList);
				newExpression = addition;
			} else if("-".equals(operator)) {
				Subtraction subtraction = new Subtraction(operator, parameterList);
				newExpression = subtraction;
			} else if("*".equals(operator)) {
				Multiplication multiplication = new Multiplication(operator, parameterList);
				newExpression = multiplication;
			} else if("/".equals(operator)) {
				Division division = new Division(operator, parameterList);
				newExpression = division;
			} else if("^".equals(operator)) {
				Exponentiation exponentiation = new Exponentiation(operator, parameterList);
				newExpression = exponentiation;
			} else {
				//This should not happen
				assert(false);
				throw new ParseException( "Unknown operator " + operator);
			}
		}
		return newExpression;
	}
	
	private Expression parseTerm() throws ParseException {
		Expression newTermExpression;
		if(!tok.hasNextOpen() && !tok.hasNextFunc() && !tok.hasNextNum() && !tok.hasNextVar())
			throw new ParseException ( ParseException.createSyntaxErrorMessage("term", programCode, -1, tok.hasNext() ? tok.next() : "EOF"));
		
		else if(tok.hasNextOpen()) {
			tok.nextOpen();
			newTermExpression = parseExpression();
			if(!tok.hasNextClose()) throw new ParseException ( ParseException.createSyntaxErrorMessage(")", programCode, -1, tok.hasNext() ? tok.next() : "EOF"));
			tok.nextClose();
		}
		else if(tok.hasNextFunc()) {
			newTermExpression = parseEvalFunction();
		}
		else if(tok.hasNextVar()) {
			String varName = tok.nextVar();
			newTermExpression = new UserdefinedEvalFunction(varName, new ArrayList<Expression>()); //nullary userdefined function
		}
		else if(tok.hasNextNum()) {
			newTermExpression = new Number(tok.nextNum());
		}
		else {
			throw new RuntimeException("This is here, so Java does not complain about newTermExpression not being initialized");
		}
		return newTermExpression;
	}
	
	private EvaluateFunction parseEvalFunction() throws ParseException {
		EvaluateFunction newFunction;
		String functionName = tok.nextFunc(); //Including left paranthesis
		functionName = functionName.substring(0, functionName.length()-1); //remove paranthesis
		List<Expression> parameterExpressions = new ArrayList<Expression>();
		while(!tok.hasNextClose()) {
			Expression newParameter = parseExpression();
			parameterExpressions.add(newParameter);
			if(!tok.hasNextClose() && !tok.hasNextComma()) 
				throw new ParseException ( ParseException.createSyntaxErrorMessage(", or )", programCode, -1, tok.hasNext() ? tok.next() : "EOF"));
			if(tok.hasNextComma()) tok.nextComma();
		}
		tok.nextClose();
		if("sin".equals(functionName)) newFunction = new SineFunction("sin", parameterExpressions);
		else if("cos".equals(functionName)) newFunction = new CosineFunction("cos", parameterExpressions);
		else if("exit".equals(functionName)) newFunction = new ExitFunction("exit", parameterExpressions);
		else newFunction = new UserdefinedEvalFunction(functionName, parameterExpressions);
		return newFunction;
	}
}
