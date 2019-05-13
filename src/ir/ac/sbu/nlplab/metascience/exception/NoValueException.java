package ir.ac.sbu.nlplab.metascience.exception;

public class NoValueException extends Exception {
	public NoValueException() {
		super("Variable has no value.");
	}

	public NoValueException(String message) {
		super(message);
	}

}
