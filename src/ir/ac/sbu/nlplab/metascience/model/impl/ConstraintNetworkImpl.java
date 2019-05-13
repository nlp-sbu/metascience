package ir.ac.sbu.nlplab.metascience.model.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.ontology.Individual;

import ir.ac.sbu.nlplab.metascience.model.Constraint;
import ir.ac.sbu.nlplab.metascience.model.ConstraintNetwork;
import ir.ac.sbu.nlplab.metascience.model.Variable;
import ir.ac.sbu.nlplab.metascience.model.VariableType;

public class ConstraintNetworkImpl implements ConstraintNetwork {
	protected Set<Variable> variables;
	protected Set<Constraint> constraints;

	public ConstraintNetworkImpl() {
		this.variables = new HashSet<Variable>();
		this.constraints = new HashSet<Constraint>();
	}

	@Override
	public void addVariable(Variable variable) {
		for (Variable v : this.variables) {
			boolean b = v.equals(variable);
			if (b) {
				return;
			}
		}
		this.variables.add(variable);
	}

	@Override
	public void removeVariable(Variable variable) {
		this.variables.remove(variable);
	}

	@Override
	public void addConstraint(Constraint constraint) {
		this.constraints.add(constraint);
	}

	@Override
	public void removeConstraint(Constraint constraint) {
		this.constraints.remove(constraint);
	}

	@Override
	public Set<Variable> getVariables() {
		return this.variables;
	}

	@Override
	public Set<Constraint> getConstraints() {
		return this.constraints;
	}

	@Override
	public Variable findVariable(String variableTypeURI, String targetURI) {
		for (Variable variable : this.variables) {
			boolean b1, b2;
			b1 = variable.getType().getBase().getURI().equalsIgnoreCase(variableTypeURI);
			b2 = variable.getTarget().getURI().equalsIgnoreCase(targetURI);
			if (b1 && b2)
				return variable;
		}
		return null;
	}

	@Override
	public VariableType findVariableType(Individual base) {
		for (Variable v : this.variables) {
			if (v.getType().getBase().equals(base)) {
				return v.getType();
			}
		}
		return null;
	}

	public String toString() {
		String s = "[variables:\n";
		for (Variable v : this.variables) {
			s = s + "    " + v.toString() + "\n";
		}
		s = s + " constraints:\n";
		for (Constraint c : this.constraints) {
			s = s + "    " + c.toString() + "\n";
		}
		s = s + "]";
		return s;
	}
}
