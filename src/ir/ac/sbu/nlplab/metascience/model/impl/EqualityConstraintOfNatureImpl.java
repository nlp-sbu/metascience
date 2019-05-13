package ir.ac.sbu.nlplab.metascience.model.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.Resource;

import ir.ac.sbu.nlplab.fomalscience.mathematics.MathExpressionUtility;
import ir.ac.sbu.nlplab.metascience.exception.NoValueException;
import ir.ac.sbu.nlplab.metascience.model.ConstraintOfNature;
import ir.ac.sbu.nlplab.metascience.model.ConstraintState;
import ir.ac.sbu.nlplab.metascience.model.EqualityConstraint;
import ir.ac.sbu.nlplab.metascience.model.Variable;

public class EqualityConstraintOfNatureImpl extends QuantitativeConstraintImpl
		implements EqualityConstraint, ConstraintOfNature {

	protected Resource target;
	protected String equality;

	public EqualityConstraintOfNatureImpl(Individual base, Resource target, String equality) {
		super(base);
		this.target = target;
		this.equality = equality;
	}

	@Override
	public Resource getTarget() {
		return this.target;
	}

	@Override
	public String getEquality() {
		return this.equality;
	}

	@Override
	public void setEquality(String equality) {
		this.equality = equality;
	}

	@Override
	protected boolean amReady() {
		int nvv = this.getNumValuedVariables();
		int nv = this.getNumVariables();
		return (nvv >= nv - 1);
	}

	@Override
	protected Set<Variable> perform() {
		Set<Variable> result = new HashSet<Variable>();
		if (this.state == ConstraintState.READY) {
			Variable unknown = null;
			for (Variable v : this.variables) {
				if (v.getNumValues() == 0) {
					unknown = v;
					break;
				}
			}
			if (unknown == null) {
				unknown = this.variables.iterator().next();
			}
			String equalities = "{" + this.getEquality();
			for (Variable v : this.variables) {
				if (!(v == unknown)) {
					equalities = equalities + ", " + v.getName() + " == " + v.getShortestValue();
				}
			}
			equalities = equalities + "}";
			String variableNames = "{";
			for (Variable v : variables) {
				variableNames = variableNames + v.getName() + ", ";
			}
			variableNames = variableNames.substring(0, variableNames.length() - 2);
			variableNames = variableNames + "}";
			String value = MathExpressionUtility.solveEquality(equalities, variableNames, unknown.getName());
			unknown.addValue(value);
			result.add(unknown);
		}
		return result;
	}

	@Override
	public void addVariableAs(Variable variable, String roleURI) {
		this.addVariable(variable);
		String temp = this.equality.replaceAll("<" + roleURI + ">", variable.getName());
		this.equality = temp;
	}

	@Override
	protected String getContentString() {
		return this.equality;
	}
}
