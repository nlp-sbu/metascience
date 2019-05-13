package ir.ac.sbu.nlplab.metascience.model.impl;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.rdf.model.Resource;

import ir.ac.sbu.nlplab.metascience.Ont;
import ir.ac.sbu.nlplab.metascience.exception.OntologicalTypeMismatchException;
import ir.ac.sbu.nlplab.metascience.model.QuantitativeVariable;
import ir.ac.sbu.nlplab.metascience.model.VariableType;

public class QuantitativeVariableImpl extends VariableImpl implements QuantitativeVariable {

	public QuantitativeVariableImpl(Individual base, VariableType type, Resource target)
			throws OntologicalTypeMismatchException {
		super(base, type, target);
		OntClass oc = type.getBase().getOntModel().getOntClass(Ont.METASCIENCE_NS + Ont.QUANTITY);
		if (!type.getBase().hasOntClass(oc, false)) {
			throw new OntologicalTypeMismatchException(type.getBase(), oc);
		}
	}
}
