package ir.ac.sbu.nlplab.metascience.model.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.Resource;

import ir.ac.sbu.nlplab.metascience.exception.NoValueException;
import ir.ac.sbu.nlplab.metascience.model.AllEqualConstraint;
import ir.ac.sbu.nlplab.metascience.model.ConstraintOfNature;
import ir.ac.sbu.nlplab.metascience.model.ConstraintState;
import ir.ac.sbu.nlplab.metascience.model.Variable;

public class AllEqualConstraintOfNatureImpl extends QuantitativeConstraintImpl
		implements AllEqualConstraint, ConstraintOfNature {

	protected Resource target;

	public AllEqualConstraintOfNatureImpl(Individual base, Resource target) {
		super(base);
		this.target = target;
	}

	@Override
	public Resource getTarget() {
		return this.target;
	}

	@Override
	protected boolean amReady() {
		return (this.getNumValuedVariables() >= 1);
	}

	@Override
	protected Set<Variable> perform() {
		Set<Variable> result = new HashSet<Variable>();
		if (this.state == ConstraintState.READY) {

			String shortestValue = "";
			String thisValue = "";
			int minLength = Integer.MAX_VALUE;
			Variable pivot = null;
			for (Variable v : this.variables) {
				if (v.getNumValues() > 0) {
					thisValue = v.getShortestValue();
					if (thisValue.length() < minLength) {
						shortestValue = thisValue;
						pivot = v;
						minLength = thisValue.length();
					}
				}
			}
			for (Variable v : this.variables) {
				if (v != pivot) {
					v.addValue(shortestValue);
					result.add(v);
				}
			}
		}
		return result;
	}

	@Override
	protected String getContentString() {
		String s = "";
		if (this.variables.size() > 0) {
			for (Variable v : this.variables) {
				s = s + v.getName() + " = ";
			}
			s = s.substring(0, s.length() - 3);
		}
		return s;
	}
}
