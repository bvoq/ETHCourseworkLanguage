package program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import exception.EvaluationException;

public class Bytecode {
	public List<Object> bytecode;
	public Bytecode() {
		bytecode = new ArrayList<Object>();
	}

	public void push(String in) {
		bytecode.add(in);
	}

	public void push(double in) {
		bytecode.add(in); //Will make it a double thanks to autoboxing.
	}

	public HashMap<String, Double> evaluate(boolean verbose) throws EvaluationException {
		return evaluate(new HashMap<String, Double>(), verbose);
	}
	/**
	 * This evaluates the bytecode.
	 */
	public HashMap<String, Double> evaluate(HashMap<String, Double> predefinedVariables, boolean verbose) throws EvaluationException {
		HashMap<String, Double> store = predefinedVariables;
		Stack<Double> stack = new Stack<Double>();
		for(int i = 0; i < bytecode.size(); ++i) {
			if(bytecode.get(i) instanceof Double) stack.push((Double)bytecode.get(i));
			else if(bytecode.get(i) instanceof String) {
				switch((String)bytecode.get(i)) {
				case "OP":
					double r = stack.pop();
					double l = stack.pop();
					switch((String)bytecode.get(++i)) {
					case "+":
						stack.push(l+r);
						break;
					case "-":
						stack.push(l-r);
						break;
					case "*":
						stack.push(l*r);
						break;
					case "/":
						stack.push(l/r);
						break;
					case "^":
						stack.push(Math.pow(l, r));
						break;
					}
					break;
				case "FUNC":
					double p = stack.pop();
					switch((String)bytecode.get(++i)) {
					case "sin":
						stack.push(Math.sin(p));
						break;
					case "cos":
						stack.push(Math.cos(p));
						break;
					case "exit":
						System.exit((int)p);
						break;
					}
					break;
				case "LOAD":
					String loadName = (String)bytecode.get(++i);
					if(!store.containsKey(loadName))
						throw new EvaluationException("The variable " + loadName + " has not been declared.");
					stack.push(store.get(loadName));
					break;
				case "STORE":
					String storeName = (String)bytecode.get(++i);
					store.put(storeName, stack.pop());
					if(verbose) System.out.println("Assigning " + storeName + " " + store.get(storeName));
					break;
				default:
					throw new RuntimeException("Unknown bytecode instruction " + (String)bytecode.get(i));
				}
			}
			else {
				throw new RuntimeException("Horribly the stack has an object of an unexpected type.");
			}
		}
		return store;
	}

}
