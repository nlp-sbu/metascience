package ir.ac.sbu.nlplab.metascience.exception;

public class NotLawOfNatureException  extends Exception {
	public NotLawOfNatureException() {
		super("Is not a law of nature.");
	}

	public NotLawOfNatureException(String message) {
		super(message);
	}

}
