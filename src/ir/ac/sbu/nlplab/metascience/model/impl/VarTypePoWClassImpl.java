package ir.ac.sbu.nlplab.metascience.model.impl;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;

import ir.ac.sbu.nlplab.metascience.model.VarTypePoWClass;

public class VarTypePoWClassImpl implements VarTypePoWClass {

	public VarTypePoWClassImpl(Individual varType, OntClass powClass) {
		this.varType = varType;
		this.powClass = powClass;
	}

	protected Individual varType;
	protected OntClass powClass;

	@Override
	public Individual getVarType() {
		return this.varType;
	}

	@Override
	public OntClass getPoWClass() {
		return this.powClass;
	}
	
	public String toString() {
		return "<" + this.varType.getLocalName() + ", " + this.powClass.getLocalName() + ">";
	}

}
