package ir.ac.sbu.nlplab.metascience.model;

import java.util.Set;

import org.apache.jena.ontology.Individual;

public interface ConstraintNetwork {

	void addVariable(Variable variable);

	void removeVariable(Variable variable);

	void addConstraint(Constraint constraint);

	void removeConstraint(Constraint constraint);

	Set<Variable> getVariables();

	Set<Constraint> getConstraints();

	Variable findVariable(String variableTypeURI, String targetURI);

	VariableType findVariableType(Individual base);

}