package ir.ac.sbu.nlplab.metascience.model;

import java.util.Iterator;
import java.util.Set;

public interface Constraint extends Model {
	
	ConstraintState getState();

	Set<Variable> apply();
	
	void addVariable(Variable variable);
	
	void removeVariable(Variable variable);

	Iterator<Variable> listVariables();
	
	int getNumVariables();

}