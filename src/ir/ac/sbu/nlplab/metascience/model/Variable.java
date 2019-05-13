package ir.ac.sbu.nlplab.metascience.model;

import java.util.Iterator;

import org.apache.jena.rdf.model.Resource;

public interface Variable extends PartOfScience {

	VariableType getType();

	Resource getTarget();

	String getName();

	void addConstraint(Constraint constraint);

	void removeConstraint(Constraint constraint);

	void addValue(String value);

	void removeValue(String value);

	int getNumValues();

	boolean hasValue();

	Iterator<String> listValues();

	String getShortestValue();

	String getValuesSetEqual();
}
