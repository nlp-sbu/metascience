package ir.ac.sbu.nlplab.metascience.model.impl;

import org.apache.jena.ontology.Individual;

import ir.ac.sbu.nlplab.metascience.model.PartOfScience;

public class PartOfScienceImpl implements PartOfScience {

	protected Individual base;

	public PartOfScienceImpl(Individual base) {
		this.base = base;
	}

	@Override
	public Individual getBase() {
		return this.base;
	}

	@Override
	public String getBaseLocalName() {
		if (this.base != null) {
			return this.base.getLocalName();
		} else
			return "NULL";
	}

	public String toString() {
		return this.getBase().getURI();
	}

}
