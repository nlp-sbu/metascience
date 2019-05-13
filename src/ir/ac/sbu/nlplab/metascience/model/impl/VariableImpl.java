package ir.ac.sbu.nlplab.metascience.model.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.Resource;

import ir.ac.sbu.nlplab.metascience.exception.NoValueException;
import ir.ac.sbu.nlplab.metascience.model.Constraint;
import ir.ac.sbu.nlplab.metascience.model.Variable;
import ir.ac.sbu.nlplab.metascience.model.VariableType;

public class VariableImpl extends PartOfScienceImpl implements Variable {

	protected VariableType type;
	protected Resource target;
	protected Set<Constraint> laws;
	protected Set<String> values;
	protected String name;

	public VariableImpl(Individual base, VariableType type, Resource target) {
		super(base);
		this.type = type;
		this.target = target;
		this.laws = new HashSet<Constraint>();
		this.values = new HashSet<String>();
		this.name = this.target.getLocalName() + this.getType().getSymbol();
	}

	@Override
	public VariableType getType() {
		return this.type;
	}

	@Override
	public Resource getTarget() {
		return this.target;
	}

	@Override
	public void addConstraint(Constraint law) {
		this.laws.add(law);
	}

	@Override
	public void removeConstraint(Constraint law) {
		for (Constraint c : this.laws) {
			if (c.equals(law)) {
				this.laws.remove(c);
				break;
			}
		}
	}

	@Override
	public void addValue(String value) {
		this.values.add(value);
	}

	@Override
	public void removeValue(String value) {
		for (String s : this.values) {
			if (s.equals(value)) {
				this.values.remove(s);
				break;
			}
		}
	}

	@Override
	public int getNumValues() {
		return this.values.size();
	}

	@Override
	public String toString() {
		String s = this.getName() + ": <";
		if (this.type == null)
			s = s + "NULL";
		else
			s = s + this.type.getBaseLocalName();
		s = s + ", ";
		if (this.target == null)
			s = s + "NULL";
		else
			s = s + this.target.getLocalName();
		s = s + " = <";
		if (this.values.size() > 0) {
			for (String v : this.values) {
				s = s + v + ", ";
			}
			s = s.substring(0, s.length() - 2);
		}
		s = s + ">";
		s = s + ">";
		return s;
	}

	@Override
	public Iterator<String> listValues() {
		return this.values.iterator();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Variable))
			return false;
		Variable o = (Variable) other;
		boolean b1 = this.getType().equals(o.getType());
		boolean b2 = this.target.getURI().equalsIgnoreCase(o.getTarget().getURI());
		return (b1 && b2);
	}

	@Override
	public String getShortestValue() {
		if (this.getNumValues() == 0) {
			return null;
		} else {
			String shortestValue = "";
			int minLength = Integer.MAX_VALUE;
			for (String value : this.values) {
				if (value.length() < minLength) {
					shortestValue = value;
					minLength = value.length();
				}
			}
			return shortestValue;
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean hasValue() {
		return (this.values.size() > 0);
	}

	@Override
	public String getValuesSetEqual() {
		String s = "";
		for(String value: this.values) {
			s = s + value + " = ";
		}
		if(s.length() >= 3) {
			s = s.substring(0, s.length() - 3);
		}
		return s;
	}
}
