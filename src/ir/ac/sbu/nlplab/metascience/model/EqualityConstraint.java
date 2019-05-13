package ir.ac.sbu.nlplab.metascience.model;

public interface EqualityConstraint extends QuantitativeConstraint {

	String getEquality();
	
	void setEquality(String equality);
	
	void addVariableAs(Variable variable, String roleURI);

}
