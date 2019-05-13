package ir.ac.sbu.nlplab.metascience.model.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.Resource;

import ir.ac.sbu.nlplab.metascience.model.ConstraintOfNature;
import ir.ac.sbu.nlplab.metascience.model.ConstraintState;
import ir.ac.sbu.nlplab.metascience.model.SumZeroConstraint;
import ir.ac.sbu.nlplab.metascience.model.Variable;

public class SumZeroConstraintOfNatureImpl extends QuantitativeConstraintImpl
		implements SumZeroConstraint, ConstraintOfNature {

	protected Resource target;

	public SumZeroConstraintOfNatureImpl(Individual base, Resource target) {
		super(base);
		this.target = target;
	}

	@Override
	public Resource getTarget() {
		return this.target;
	}

	// public void addPredicateVariable(PredicateVariable predicateVariable) {
	// this.predicateVariables.add(predicateVariable);
	// }
	//
	// public void removePredicateVariable(PredicateVariable predicateVariable)
	// {
	// for (PredicateVariable pv : this.predicateVariables) {
	// if (pv.equals(predicateVariable)) {
	// this.predicateVariables.remove(pv);
	// break;
	// }
	// }
	// }

	// public int numPredicateVariable() {
	// return this.predicateVariables.size();
	// }

	@Override
	protected boolean amReady() {
		return (this.getNumValuedVariables() >= this.getNumVariables() - 1);
	}

	@Override
	protected Set<Variable> perform() {
		Set<Variable> result = new HashSet<Variable>();
		if (this.state == ConstraintState.READY) {
			Variable newValuedVariable = null;
			int nvv = this.getNumValuedVariables();
			int nv = this.getNumVariables();
			if (nvv == nv - 1) {
				for (Variable v : this.variables) {
					if (v.getNumValues() == 0) {
						newValuedVariable = v;
						break;
					}
				}
			} else {
				newValuedVariable = this.variables.iterator().next();
			}

			String s = "";
			for (Variable v : this.variables) {
				if (v != newValuedVariable) {
					s = s + "-(" + v.getShortestValue() + ")";
				}
			}
			newValuedVariable.addValue(s);
			result.add(newValuedVariable);
		}
		return result;
	}

	@Override
	protected String getContentString() {
		String s = "";
		if (this.variables.size() > 0) {
			for (Variable v : this.variables) {
				s = s + v.getName() + " + ";
			}
			s = s.substring(0, s.length() - 3) + " = 0";
		}
		return s;
	}
}
