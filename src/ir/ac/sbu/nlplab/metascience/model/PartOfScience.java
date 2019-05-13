package ir.ac.sbu.nlplab.metascience.model;

import org.apache.jena.ontology.Individual;

public interface PartOfScience {
	
	Individual getBase();

	String getBaseLocalName();
}
