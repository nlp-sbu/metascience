package ir.ac.sbu.nlplab.science;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;

import ir.ac.sbu.nlplab.metascience.Ont;
import ir.ac.sbu.nlplab.metascience.model.VarTypePoWClass;
import ir.ac.sbu.nlplab.metascience.model.impl.VarTypePoWClassImpl;

public class ScienceFacade {
	public static Set<VarTypePoWClass> listStateVariableTypes(OntModel scientificOntology, Individual theory) {
		String selectQueryString = "PREFIX rdf: <" + Ont.RDF_NS + ">\n" +
				"PREFIX rdfs: <" + Ont.RDFS_NS + ">\n" +
				"PREFIX owl: <" + Ont.OWL_NS + ">\n" +
				"PREFIX metascience: <" + Ont.METASCIENCE_NS + ">\n" +
				"PREFIX science: <" + Ont.SCIENCE_NS + ">\n" +
				"PREFIX example: <" + Ont.EXAMPLE_NS + ">\n" +
				"SELECT ?variableType ?partOfWorld \n" +
				"WHERE {\n" +
				"	?viewPutsVariable a <" + Ont.METASCIENCE_NS + Ont.VIEW_OF_THEORY_PUTS_VARIABLE_ON_POW + ">.\n" +
				"	?viewPutsVariable <" + Ont.METASCIENCE_NS + Ont.VIEW_OF_THEORY_PUTS_VARIABLE_ON_POW__THEORY + "> <"
				+ theory.getURI() + ">.\n" +
				"	?viewPutsVariable <" + Ont.METASCIENCE_NS + Ont.VIEW_OF_THEORY_PUTS_VARIABLE_ON_POW__VIEW + "> <"
				+ Ont.SCIENCE_NS + Ont.BEHAVIOR + ">.\n" +
				"	?viewPutsVariable <" + Ont.METASCIENCE_NS + Ont.VIEW_OF_THEORY_PUTS_VARIABLE_ON_POW__VARIABLE_TYPE
				+ "> ?variableType.\n" +
				"	?viewPutsVariable <" + Ont.METASCIENCE_NS + Ont.VIEW_OF_THEORY_PUTS_VARIABLE_ON_POW__POW
				+ "> ?partOfWorld.\n" +
				"}\n";
		Query selectQuery = QueryFactory.create(selectQueryString);
		QueryExecution selectQueryExecution = QueryExecutionFactory.create(selectQuery, scientificOntology);
		ResultSet selectResults = selectQueryExecution.execSelect();
		Set<VarTypePoWClass> varTypePoWClasses = new HashSet<VarTypePoWClass>();
		while (selectResults.hasNext()) {
			QuerySolution solution = selectResults.nextSolution();
			Individual variableType = scientificOntology.getIndividual(solution.getResource("variableType").getURI());
			Individual pow = scientificOntology.getIndividual(solution.getResource("partOfWorld").getURI());
			OntClass powOntClass = null;
			if (pow == null) {
				powOntClass = scientificOntology.getOntClass(solution.getResource("partOfWorld").getURI());
			} else {
				powOntClass = pow.getOntClass();
			}
			varTypePoWClasses.add(new VarTypePoWClassImpl(variableType, powOntClass));
		}
		selectQueryExecution.close();
		return varTypePoWClasses;
	}

	public static Individual getVariableType(OntModel science, Individual variable) {
		return variable.getProperty(science.getProperty(Ont.METASCIENCE_NS + Ont.VARIABLE_HAS_TYPE)).getObject()
				.as(Individual.class);
	}

	public static Set<Individual> getLawsRepresentingPartOfWorld(OntModel science, Individual theory,
			Individual partOfWorld) {
		Individual idea = getIdea(science, partOfWorld);
		Set<Individual> laws = getLawsRepresentingIdea(science, theory, idea);
		return laws;
	}

	public static Individual getIdea(OntModel science, Individual individual) {
		OntClass ontClass = individual.getOntClass();
		String ideaURI = ontClass.getNameSpace() + "Idea_" + ontClass.getLocalName();
		Individual idea = science.getIndividual(ideaURI);
		return idea;
	}

	public static Set<Individual> getLawsRepresentingIdea(OntModel science, Individual theory, Individual idea) {
		Set<Individual> laws = new HashSet<Individual>();
		String theoryContainsLawURI = Ont.METASCIENCE_NS + Ont.THEORY_CONTAINS_LAW;
		for (StmtIterator lawsOfTheory = theory.listProperties(science.getProperty(theoryContainsLawURI)); lawsOfTheory
				.hasNext();) {
			Resource lawResource = lawsOfTheory.next().getObject().asResource();
			if (lawResource.canAs(Individual.class)) {
				Individual lawInd = lawResource.as(Individual.class);
				String lawRepsPoW = Ont.METASCIENCE_NS + Ont.LAW_OF_NATURE_REPRESENTS_PART_OF_WORLD;
				for (StmtIterator partsOfWorld = lawInd
						.listProperties(science.getProperty(lawRepsPoW)); partsOfWorld
								.hasNext();) {
					if (partsOfWorld.next().getObject().equals(idea)) {
						laws.add(lawInd);
						break;
					}
				}
			}
		}
		return laws;
	}

	// public static Set<String>
	// listValuesOfVariableOfGivenTypeDescribingGivenPartOfWorld(OntModel
	// science, Individual varType, Individual pow) {
	// }

}
