package ir.ac.sbu.nlplab.metascience.exception;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;

public class OntologicalTypeMismatchException extends Exception {
	public OntologicalTypeMismatchException(Individual individual, OntClass ontClass) {
		super("Type mismatch: " + individual.getLocalName() + " should be of ontological type "
				+ ontClass.getLocalName() + ".");
	}

}
