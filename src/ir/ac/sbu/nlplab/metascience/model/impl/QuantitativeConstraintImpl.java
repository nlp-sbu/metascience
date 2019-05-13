package ir.ac.sbu.nlplab.metascience.model.impl;

import org.apache.jena.ontology.Individual;

import ir.ac.sbu.nlplab.metascience.model.QuantitativeConstraint;
import ir.ac.sbu.nlplab.metascience.model.QuantitativeVariable;
import ir.ac.sbu.nlplab.metascience.model.Variable;

public abstract class QuantitativeConstraintImpl extends ConstraintImpl implements QuantitativeConstraint {

	public QuantitativeConstraintImpl(Individual base){
		super(base);
	}

	@Override
	public void addVariable(Variable variable) {
//		if (variable instanceof QuantitativeVariable) {
			this.variables.add(variable);
//		}
	}

	protected int getNumValuedVariables() {
		int nvv = 0;
		for (Variable v : this.variables) {
			if (v.getNumValues() > 0) {
				nvv++;
			}
		}
		return nvv;
	}
}
