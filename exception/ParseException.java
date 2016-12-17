package exception;

public class ParseException extends ProgramException {
	public static final int LENGTH_OF_CODEPREVIEW = 0; //This is the length of codepreview (must be odd), 0 for no preview.
	
	public ParseException(String message) {
		super(message);
	}
	
	public static String createSyntaxErrorMessage(String expected, String program, int location, String insteadOf) {
		assert((LENGTH_OF_CODEPREVIEW == 0 || LENGTH_OF_CODEPREVIEW % 2 == 1) && LENGTH_OF_CODEPREVIEW >= 0);
		assert(expected.length() > 0);
		String message = "";
		if(LENGTH_OF_CODEPREVIEW > 0 && location >= 0) {
			int lengthOfOnesideWidth = (LENGTH_OF_CODEPREVIEW - 1) / 2;
			int startPosition =  Math.max(0, location - lengthOfOnesideWidth);
			int endPosition = Math.min(program.length() - 1, location + lengthOfOnesideWidth);
			for(int i = startPosition; i <= endPosition ; ++i) message += program.charAt(i);
			message += "\n";
			for(int i = startPosition; i < location; ++i) message += " ";
			message += "^\n";
		}
		message += "Expected " + expected;
		if(location != -1)  message += " at " + location;
		if(insteadOf.length() > 0)  message += " instead of " + insteadOf;
		message += ".\n";
		return message;
	}
}
