package ir.ac.sbu.nlplab.metascience.model.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.ontology.Individual;

import ir.ac.sbu.nlplab.metascience.model.Constraint;
import ir.ac.sbu.nlplab.metascience.model.ConstraintState;
import ir.ac.sbu.nlplab.metascience.model.Variable;

public abstract class ConstraintImpl extends ModelImpl implements Constraint {

	protected Set<Variable> variables;
	protected ConstraintState state;

	public ConstraintImpl(Individual base) {
		super(base);
		this.state = ConstraintState.NOT_READY;
		this.variables = new HashSet<Variable>();
	}

	@Override
	public void addVariable(Variable variable) {
		this.variables.add(variable);
	}

	@Override
	public void removeVariable(Variable variable) {
		for (Variable v : this.variables) {
			if (v.equals(variable)) {
				this.variables.remove(v);
				return;
			}
		}
	}

	@Override
	public Iterator<Variable> listVariables() {
		return this.variables.iterator();
	}

	@Override
	public int getNumVariables() {
		return this.variables.size();
	}

	@Override
	public ConstraintState getState() {
		if (this.state == ConstraintState.NOT_READY && this.amReady())
			this.state = ConstraintState.READY;
		return this.state;
	}

	public Set<Variable> apply() {
		Set<Variable> variables = null;
		if (this.getState() == ConstraintState.READY) {
			 variables = this.perform();
			this.state = ConstraintState.APPLIED;
		}
		return variables;
	}

	public String toString() {
		return this.getContentString();
	}

	protected abstract boolean amReady();

	protected abstract Set<Variable> perform();

	protected abstract String getContentString();
}
