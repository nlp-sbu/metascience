package ir.ac.sbu.nlplab.metascience.model.impl;

import org.apache.jena.ontology.ObjectProperty;

import ir.ac.sbu.nlplab.metascience.model.Variable;

public class PredicateVariableImpl {
	private ObjectProperty predicate;
	private Variable variable;
	
	public PredicateVariableImpl() {
		this.setPredicate(null);
		this.setVariable(null);
	}
	
	public PredicateVariableImpl(ObjectProperty predicate, Variable variable) {
		this.setPredicate(predicate);
		this.setVariable(variable);
	}

	public ObjectProperty getPredicate() {
		return predicate;
	}

	public void setPredicate(ObjectProperty predicate) {
		this.predicate = predicate;
	}
	
	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}
}
