package ir.ac.sbu.nlplab.metascience.model.impl;

import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.PropertyImpl;

import ir.ac.sbu.nlplab.metascience.Ont;
import ir.ac.sbu.nlplab.metascience.model.VariableType;

public class VariableTypeImpl extends PartOfScienceImpl implements VariableType {

	public VariableTypeImpl(Individual base) {
		super(base);
		Statement statement = base.getProperty(new PropertyImpl(Ont.METASCIENCE_NS + Ont.VARIABLE_TYPE_HAS_SYMBOL));
		this.symbol = statement.getObject().asLiteral().getString().trim();
	}

	protected String symbol;

	@Override
	public String getSymbol() {
		return this.symbol;
	}

}
