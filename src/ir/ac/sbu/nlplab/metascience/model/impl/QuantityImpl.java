package ir.ac.sbu.nlplab.metascience.model.impl;

import org.apache.jena.ontology.Individual;

import ir.ac.sbu.nlplab.metascience.model.Quantity;

public class QuantityImpl extends VariableTypeImpl implements Quantity {

	public QuantityImpl(Individual base) {
		super(base);
	}

}
